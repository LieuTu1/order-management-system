'use client';

import { useState } from 'react';
import { apiFetch } from '@/lib/apiFetch';
import type { Customer } from '../types';

interface CustomerFormProps {
    customerToEdit?: Customer;
    onSuccess: () => void;
    onCancel: () => void;
}

export default function CustomerForm({ customerToEdit, onSuccess, onCancel }: CustomerFormProps) {
    const isEditMode = !!customerToEdit;

    const [name, setName] = useState(customerToEdit?.name ?? '');
    const [phone, setPhone] = useState(customerToEdit?.phone ?? '');
    const [email, setEmail] = useState(customerToEdit?.email ?? '');
    const [address, setAddress] = useState(customerToEdit?.address ?? '');

    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError('');

        if (!name.trim()) {
            setError('Tên khách hàng không được để trống');
            return;
        }

        if (phone && !/^0\d{9,10}$/.test(phone.trim())) {
            setError('Số điện thoại không hợp lệ (phải bắt đầu bằng 0, 10-11 chữ số)');
            return;
        }

        if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) {
            setError('Email không đúng định dạng');
            return;
        }

        const payload = { name, phone, email, address };

        setSubmitting(true);
        const response = await apiFetch(
            isEditMode ? `/api/customers/${customerToEdit!.id}` : '/api/customers',
            {
                method: isEditMode ? 'PUT' : 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload),
            }
        );
        const result = await response.json();
        setSubmitting(false);

        if (!response.ok) {
            setError(result.message || 'Lưu khách hàng thất bại');
            return;
        }

        onSuccess();
    }

    return (
        <div className="bg-white rounded-3xl shadow-xl p-6 border border-purple-200 max-w-xl mx-auto">
            <h2 className="text-xl font-bold text-gray-800 mb-4">
                {isEditMode ? 'Sửa khách hàng' : 'Thêm khách hàng mới'}
            </h2>

            <form onSubmit={handleSubmit} className="space-y-4">
                {error && (
                    <p className="bg-red-50 text-red-600 text-sm px-3 py-2 rounded-lg border border-red-100">
                        {error}
                    </p>
                )}

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Tên khách hàng
                    </label>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Số điện thoại
                    </label>
                    <input
                        type="text"
                        value={phone}
                        onChange={(e) => setPhone(e.target.value)}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Email
                    </label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Địa chỉ
                    </label>
                    <input
                        type="text"
                        value={address}
                        onChange={(e) => setAddress(e.target.value)}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    />
                </div>

                <div className="flex gap-3 pt-2">
                    <button
                        type="submit"
                        disabled={submitting}
                        className="flex-1 bg-purple-700 hover:bg-purple-800 text-white font-medium py-2.5 rounded-lg transition shadow-md shadow-purple-300 disabled:opacity-50"
                    >
                        {submitting ? 'Đang lưu...' : isEditMode ? 'Lưu thay đổi' : 'Tạo mới'}
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