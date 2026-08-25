'use client';

import { useState } from 'react';
import { apiFetch } from '@/lib/apiFetch';
import type { SupplierOption } from '@/features/products/types';

interface SupplierFormProps {
    supplierToEdit?: SupplierOption;
    onSuccess: () => void;
    onCancel: () => void;
}

export default function SupplierForm({ supplierToEdit, onSuccess, onCancel }: SupplierFormProps) {
    const isEditMode = !!supplierToEdit;

    const [name, setName] = useState(supplierToEdit?.name ?? '');
    const [phone, setPhone] = useState(supplierToEdit?.phone ?? '');
    const [email, setEmail] = useState(supplierToEdit?.email ?? '');
    const [address, setAddress] = useState(supplierToEdit?.address ?? '');

    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError('');

        if (!name.trim() || !phone.trim() || !email.trim() || !address.trim()) {
            setError('Vui lòng điền đầy đủ tất cả các trường');
            return;
        }

        if (!/^0\d{9,10}$/.test(phone.trim())) {
            setError('Số điện thoại không hợp lệ (phải bắt đầu bằng 0, 10-11 chữ số)');
            return;
        }

        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) {
            setError('Email không đúng định dạng');
            return;
        }

        const payload = { name, phone, email, address };

        setSubmitting(true);
        const response = await apiFetch(
            isEditMode ? `/api/suppliers/${supplierToEdit!.id}` : '/api/suppliers',
            {
                method: isEditMode ? 'PUT' : 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload),
            }
        );
        const result = await response.json();
        setSubmitting(false);

        if (!response.ok) {
            setError(result.message || 'Lưu nhà cung cấp thất bại');
            return;
        }

        onSuccess();
    }

    return (
        <div className="bg-white rounded-3xl shadow-xl p-6 border border-purple-200 max-w-xl mx-auto">
            <h2 className="text-xl font-bold text-gray-800 mb-4">
                {isEditMode ? 'Sửa nhà cung cấp' : 'Thêm nhà cung cấp mới'}
            </h2>

            <form onSubmit={handleSubmit} className="space-y-4">
                {error && (
                    <p className="bg-red-50 text-red-600 text-sm px-3 py-2 rounded-lg border border-red-100">
                        {error}
                    </p>
                )}

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Tên nhà cung cấp
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