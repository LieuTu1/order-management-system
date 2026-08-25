'use client';

import { useState, useEffect } from 'react';
import type { Order } from '../types';
import OrderCard from './OrderCard';
import OrderForm from './OrderForm';
import Pagination from '@/components/Pagination';
import { apiFetch } from '@/lib/apiFetch';

export default function OrderList() {
    const [orders, setOrders] = useState<Order[]>([]);
    const [error, setError] = useState('');
    const [searchTerm, setSearchTerm] = useState('');
    const [showForm, setShowForm] = useState(false);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    async function fetchOrders(pageToFetch: number) {
        const response = await apiFetch(
            `/api/orders?page=${pageToFetch}&size=10&sort=id,desc`,
            { method: 'GET' }
        );
        const result = await response.json();

        if (!response.ok) {
            setError(result.message || 'Không thể tải danh sách đơn hàng');
            return;
        }

        setOrders(result.data.content ?? []);
        setTotalPages(result.data.totalPages ?? 0);
    }

    useEffect(() => {
        fetchOrders(page);
    }, [page]);

    function handleStatusChanged(updatedOrder: Order) {
        setOrders((prev) =>
            prev.map((o) => (o.id === updatedOrder.id ? updatedOrder : o))
        );
    }

    function handleFormSuccess() {
        setShowForm(false);
        fetchOrders(page);
    }

    // Tìm kiếm chỉ lọc trong phạm vi trang hiện tại (dữ liệu đã phân trang từ backend)
    const filteredOrders = orders.filter((order) => {
        const keyword = searchTerm.toLowerCase();

        return (
            order.orderCode.toLowerCase().includes(keyword) ||
            order.customerName.toLowerCase().includes(keyword) ||
            order.userName.toLowerCase().includes(keyword) ||
            order.status.toLowerCase().includes(keyword)
        );
    });

    if (error) {
        return (
            <p className="bg-red-50 text-red-600 text-sm px-4 py-3 rounded-lg border border-red-100">
                {error}
            </p>
        );
    }

    if (showForm) {
        return (
            <OrderForm
                onSuccess={handleFormSuccess}
                onCancel={() => setShowForm(false)}
            />
        );
    }

    return (
        <div>
            <div className="grid grid-cols-3 items-center mb-5 gap-4">
                <h2 className="text-lg font-semibold text-gray-800">
                    Danh sách đơn hàng
                </h2>

                <input
                    type="text"
                    placeholder="Tìm mã đơn, khách hàng, nhân viên, trạng thái..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full max-w-md mx-auto px-4 py-2 rounded-lg border-2 border-purple-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition text-sm"
                />

                <button
                    onClick={() => setShowForm(true)}
                    className="justify-self-end whitespace-nowrap bg-purple-700 hover:bg-purple-800 text-white text-sm font-medium px-4 py-2 rounded-lg transition"
                >
                    + Tạo đơn hàng
                </button>
            </div>

            <div>
                {filteredOrders.map((order) => (
                    <OrderCard
                        key={order.id}
                        order={order}
                        onStatusChanged={handleStatusChanged}
                    />
                ))}
            </div>

            <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
    );
}