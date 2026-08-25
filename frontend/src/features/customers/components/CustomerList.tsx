'use client';

import { useState, useEffect } from 'react';
import type { Customer } from '../types';
import CustomerForm from './CustomerForm';
import Pagination from '@/components/Pagination';
import { apiFetch } from '@/lib/apiFetch';

export default function CustomerList() {
    const [customers, setCustomers] = useState<Customer[]>([]);
    const [error, setError] = useState('');
    const [editingCustomer, setEditingCustomer] =
        useState<Customer | 'new' | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [deletingId, setDeletingId] = useState<number | null>(null);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    async function fetchCustomers(pageToFetch: number) {
        const response = await apiFetch(
            `/api/customers?page=${pageToFetch}&size=9&sort=id,desc`,
            { method: 'GET' }
        );

        const result = await response.json();

        if (!response.ok) {
            setError(
                result.message || 'Không thể tải danh sách khách hàng'
            );
            return;
        }

        setCustomers(result.data.content ?? []);
        setTotalPages(result.data.totalPages ?? 0);
    }

    useEffect(() => {
        fetchCustomers(page);
    }, [page]);

    async function handleDelete(id: number) {
        const confirmed = confirm(
            'Bạn chắc chắn muốn xóa khách hàng này?'
        );

        if (!confirmed) return;

        setDeletingId(id);

        const response = await apiFetch(`/api/customers/${id}`, {
            method: 'DELETE',
        });

        const result = await response.json();

        setDeletingId(null);

        if (!response.ok) {
            alert(result.message || 'Xóa thất bại');
            return;
        }

        fetchCustomers(page);
    }

    function handleFormSuccess() {
        setEditingCustomer(null);
        fetchCustomers(page);
    }

    // Tìm kiếm chỉ lọc trong phạm vi trang hiện tại (dữ liệu đã phân trang từ backend)
    const filteredCustomers = customers.filter((customer) => {
        const keyword = searchTerm.toLowerCase();

        return (
            customer.name.toLowerCase().includes(keyword) ||
            (customer.phone || '').toLowerCase().includes(keyword) ||
            (customer.email || '').toLowerCase().includes(keyword) ||
            (customer.address || '').toLowerCase().includes(keyword)
        );
    });

    if (error) {
        return (
            <p className="bg-red-50 text-red-600 text-sm px-4 py-3 rounded-lg border border-red-100">
                {error}
            </p>
        );
    }

    if (editingCustomer !== null) {
        return (
            <CustomerForm
                customerToEdit={
                    editingCustomer === 'new'
                        ? undefined
                        : editingCustomer
                }
                onSuccess={handleFormSuccess}
                onCancel={() => setEditingCustomer(null)}
            />
        );
    }

    return (
        <div>
            <div className="grid grid-cols-3 items-center mb-5 gap-4">
                <h2 className="text-lg font-semibold text-gray-800 whitespace-nowrap">
                    Danh sách khách hàng
                </h2>

                <input
                    type="text"
                    placeholder="Tìm theo tên, sđt, email..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full max-w-md mx-auto px-4 py-2 rounded-lg border-2 border-purple-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition text-sm"
                />

                <button
                    onClick={() => setEditingCustomer('new')}
                    className="justify-self-end bg-purple-700 hover:bg-purple-800 text-white text-sm font-medium px-4 py-2 rounded-lg transition shadow-md shadow-purple-300 whitespace-nowrap"
                >
                    + Thêm khách hàng
                </button>
            </div>

            {filteredCustomers.length === 0 ? (
                <p className="text-gray-500 text-sm">
                    Không tìm thấy khách hàng nào.
                </p>
            ) : (
                <div className="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
                    <table className="w-full text-sm">
                        <thead>
                        <tr className="bg-purple-50 text-left text-gray-700">
                            <th className="px-5 py-3 font-bold">Tên</th>
                            <th className="px-5 py-3 font-bold">SĐT</th>
                            <th className="px-5 py-3 font-bold">Email</th>
                            <th className="px-5 py-3 font-bold">Địa chỉ</th>
                            <th className="px-5 py-3 font-bold text-right">Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        {filteredCustomers.map((customer) => (
                            <tr
                                key={customer.id}
                                className="border-t border-gray-100 even:bg-gray-50/60 hover:bg-purple-50/50 transition"
                            >
                                <td className="px-5 py-3.5 text-gray-800 font-medium">
                                    {customer.name}
                                </td>
                                <td className="px-5 py-3.5 text-gray-500">{customer.phone}</td>
                                <td className="px-5 py-3.5 text-gray-500">{customer.email}</td>
                                <td className="px-5 py-3.5 text-gray-500">{customer.address}</td>
                                <td className="px-5 py-3.5 text-right space-x-3">
                                    <button
                                        onClick={() => setEditingCustomer(customer)}
                                        className="text-purple-700 hover:text-purple-900 font-medium"
                                    >
                                        Sửa
                                    </button>
                                    <button
                                        onClick={() => handleDelete(customer.id)}
                                        disabled={deletingId === customer.id}
                                        className="text-red-600 hover:text-red-800 font-medium disabled:opacity-50"
                                    >
                                        {deletingId === customer.id ? 'Đang xóa...' : 'Xóa'}
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
    );
}