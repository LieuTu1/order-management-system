'use client';

import { useState } from 'react';
import type { Order, OrderStatus } from '../types';
import { NEXT_STATUS_MAP } from '../types';
import { apiFetch } from '@/lib/apiFetch';

interface OrderCardProps {
    order: Order;
    onStatusChanged: (updatedOrder: Order) => void;
}

const STATUS_LABEL: Record<string, string> = {
    PENDING: 'Chờ xử lý',
    PROCESSING: 'Đang xử lý',
    COMPLETED: 'Hoàn tất',
    CANCELLED: 'Đã hủy',
};

const STATUS_COLOR: Record<string, string> = {
    PENDING: 'bg-purple-50 text-purple-700 border-purple-200',
    PROCESSING: 'bg-blue-50 text-blue-700 border-blue-200',
    COMPLETED: 'bg-green-50 text-green-700 border-green-200',
    CANCELLED: 'bg-red-50 text-red-700 border-red-200',
};

export default function OrderCard({ order, onStatusChanged }: OrderCardProps) {
    const [updating, setUpdating] = useState(false);
    const [error, setError] = useState('');

    const nextStatuses = NEXT_STATUS_MAP[order.status] || [];

    async function handleChangeStatus(newStatus: OrderStatus) {
        setError('');
        setUpdating(true);

        const response = await apiFetch(`/api/orders/${order.id}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus }),
        });
        const result = await response.json();

        setUpdating(false);

        if (!response.ok) {
            setError(result.message || 'Cập nhật trạng thái thất bại');
            return;
        }

        onStatusChanged(result.data);
    }

    return (
        <article className="bg-white border border-gray-200 rounded-2xl p-5 mb-4 shadow-sm hover:shadow-md transition">
            <div className="flex justify-between items-center mb-3">
                <span className="font-semibold text-gray-800">{order.orderCode}</span>
                <span
                    className={`text-xs font-medium px-2.5 py-1 rounded-full border ${
                        STATUS_COLOR[order.status] || 'bg-gray-50 text-gray-700 border-gray-200'
                    }`}
                >
                    {STATUS_LABEL[order.status] || order.status}
                </span>
            </div>

            <div className="text-sm text-gray-600 grid grid-cols-2 gap-x-8 gap-y-1 mb-4">
                <p>Ngày đặt: {order.orderDate}</p>
                <p>Thanh toán: {order.paymentMethod}</p>
                <p>Khách hàng: {order.customerName}</p>
                <p>Trạng thái thanh toán: {order.paymentStatus}</p>
                <p>Nhân viên xử lý: {order.userName}</p>
            </div>

            <hr className="border-gray-100 mb-4" />

            <h3 className="text-sm font-semibold text-gray-700 mb-2">Chi tiết sản phẩm</h3>

            <ul className="space-y-1.5 mb-4">
                {order.orderDetails.map((detail) => (
                    <li
                        key={detail.productId}
                        className="text-sm text-gray-600 flex justify-between border-b border-gray-50 pb-1.5"
                    >
                        <span>{detail.productName} × {detail.quantity}</span>
                        <span>{detail.subtotal.toLocaleString('vi-VN')} đ</span>
                    </li>
                ))}
            </ul>

            <hr className="border-gray-100 mb-3" />

            <p className="text-right text-gray-800 mb-3">
                Tổng tiền:{' '}
                <strong className="text-purple-700 text-lg">
                    {order.totalAmount.toLocaleString('vi-VN')} đ
                </strong>
            </p>

            {error && (
                <p className="text-red-600 text-xs mb-2">{error}</p>
            )}

            {nextStatuses.length > 0 && (
                <div className="flex gap-2 justify-end">
                    {nextStatuses.map((status) => (
                        <button
                            key={status}
                            onClick={() => handleChangeStatus(status)}
                            disabled={updating}
                            className={`text-xs font-medium px-3 py-1.5 rounded-full border transition disabled:opacity-50 ${
                                status === 'CANCELLED'
                                    ? 'border-red-300 text-red-600 hover:bg-red-50'
                                    : 'border-purple-300 text-purple-700 hover:bg-purple-50'
                            }`}
                        >
                            {updating ? '...' : `Chuyển sang ${STATUS_LABEL[status]}`}
                        </button>
                    ))}
                </div>
            )}
        </article>
    );
}