'use client';

import { useState, useEffect } from 'react';
import { apiFetch } from '@/lib/apiFetch';
import type { Customer } from '@/features/customers/types';
import type { CreateOrderRequest, PaymentMethod } from '../types';

interface Product {
    id: number;
    name: string;
    price: number;
    status: string;
}

interface OrderRow {
    productId: number | '';
    quantity: number;
}

interface OrderFormProps {
    onSuccess: () => void;
    onCancel: () => void;
}

export default function OrderForm({ onSuccess, onCancel }: OrderFormProps) {
    const [customers, setCustomers] = useState<Customer[]>([]);
    const [products, setProducts] = useState<Product[]>([]);
    const [userId, setUserId] = useState<number | null>(null);

    const [customerId, setCustomerId] = useState<number | ''>('');
    const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>('CASH');
    const [rows, setRows] = useState<OrderRow[]>([{ productId: '', quantity: 1 }]);

    const [error, setError] = useState('');
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        async function loadInitialData() {
            const [customerRes, productRes, meRes] = await Promise.all([
                apiFetch('/api/customers?size=1000', { method: 'GET' }),
                apiFetch('/api/products?size=1000', { method: 'GET' }),
                apiFetch('/api/auth/me', { method: 'GET' }),
            ]);

            const customerResult = await customerRes.json();
            const productResult = await productRes.json();
            const meResult = await meRes.json();

            if (customerRes.ok) setCustomers(customerResult.data.content);
            if (productRes.ok) setProducts(productResult.data.content);
            if (meRes.ok) setUserId(meResult.data.id);
        }

        loadInitialData();
    }, []);

    function updateRow(index: number, field: keyof OrderRow, value: string) {
        setRows((prev) =>
            prev.map((row, i) =>
                i === index ? { ...row, [field]: Number(value) } : row
            )
        );
    }

    function addRow() {
        setRows((prev) => [...prev, { productId: '', quantity: 1 }]);
    }

    function removeRow(index: number) {
        setRows((prev) => prev.filter((_, i) => i !== index));
    }

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError('');

        if (!customerId) {
            setError('Vui lòng chọn khách hàng');
            return;
        }
        if (!userId) {
            setError('Không xác định được nhân viên xử lý, vui lòng thử lại');
            return;
        }
        const validRows = rows.filter((r) => r.productId !== '' && r.quantity > 0);
        if (validRows.length === 0) {
            setError('Vui lòng thêm ít nhất một sản phẩm');
            return;
        }

        const payload: CreateOrderRequest = {
            customerId: Number(customerId),
            userId,
            paymentMethod,
            orderDetails: validRows.map((r) => ({
                productId: Number(r.productId),
                quantity: r.quantity,
            })),
        };

        setSubmitting(true);
        const response = await apiFetch('/api/orders', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });
        const result = await response.json();
        setSubmitting(false);

        if (!response.ok) {
            setError(result.message || 'Tạo đơn hàng thất bại');
            return;
        }

        onSuccess();
    }

    return (
        <div className="bg-white rounded-2xl shadow-xl p-6 border border-purple-200 max-w-xl mx-auto">
            <h2 className="text-xl font-bold text-gray-800 mb-4">Tạo đơn hàng mới</h2>

            <form onSubmit={handleSubmit} className="space-y-4">
                {error && (
                    <p className="bg-red-50 text-red-600 text-sm px-3 py-2 rounded-lg border border-red-100">
                        {error}
                    </p>
                )}

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Khách hàng</label>
                    <select
                        value={customerId}
                        onChange={(e) => setCustomerId(e.target.value ? Number(e.target.value) : '')}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    >
                        <option value="">-- Chọn khách hàng --</option>
                        {customers.map((c) => (
                            <option key={c.id} value={c.id}>
                                {c.name} - {c.phone}
                            </option>
                        ))}
                    </select>
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Phương thức thanh toán</label>
                    <select
                        value={paymentMethod}
                        onChange={(e) => setPaymentMethod(e.target.value as PaymentMethod)}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    >
                        <option value="CASH">Tiền mặt</option>
                        <option value="BANK_TRANSFER">Chuyển khoản</option>
                        <option value="MOMO">Momo</option>
                    </select>
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Sản phẩm</label>

                    {rows.map((row, index) => (
                        <div key={index} className="flex gap-2 mb-2">
                            <select
                                value={row.productId}
                                onChange={(e) => updateRow(index, 'productId', e.target.value)}
                                className="flex-1 px-3 py-2 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-purple-500"
                            >
                                <option value="">-- Chọn sản phẩm --</option>
                                {products.map((p) => (
                                    <option key={p.id} value={p.id}>
                                        {p.name} - {p.price.toLocaleString('vi-VN')} đ
                                    </option>
                                ))}
                            </select>

                            <input
                                type="number"
                                min={1}
                                value={row.quantity}
                                onChange={(e) => updateRow(index, 'quantity', e.target.value)}
                                className="w-20 px-3 py-2 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-purple-500"
                            />

                            <button
                                type="button"
                                onClick={() => removeRow(index)}
                                disabled={rows.length === 1}
                                className="px-2 text-red-600 hover:text-red-800 disabled:opacity-30 disabled:cursor-not-allowed text-sm"
                            >
                                Xóa
                            </button>
                        </div>
                    ))}

                    <button
                        type="button"
                        onClick={addRow}
                        className="text-sm text-purple-700 hover:text-purple-900 font-medium mt-1"
                    >
                        + Thêm sản phẩm
                    </button>
                </div>

                <div className="flex gap-3 pt-2">
                    <button
                        type="submit"
                        disabled={submitting}
                        className="flex-1 bg-purple-700 hover:bg-purple-800 text-white font-medium py-2.5 rounded-lg transition shadow-md shadow-purple-300 disabled:opacity-50"
                    >
                        {submitting ? 'Đang tạo...' : 'Tạo đơn hàng'}
                    </button>
                    <button
                        type="button"
                        onClick={onCancel}
                        className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium py-2.5 rounded-lg transition"
                    >
                        Hủy
                    </button>
                </div>
            </form>
        </div>
    );
}