'use client';

import { useState } from 'react';
import { apiFetch } from '@/lib/apiFetch';
import type { CategoryOption } from '@/features/products/types';

interface CategoryFormProps {
    categoryToEdit?: CategoryOption;
    onSuccess: () => void;
    onCancel: () => void;
}

export default function CategoryForm({ categoryToEdit, onSuccess, onCancel }: CategoryFormProps) {
    const isEditMode = !!categoryToEdit;

    const [name, setName] = useState(categoryToEdit?.name ?? '');
    const [description, setDescription] = useState(categoryToEdit?.description ?? '');

    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError('');

        if (!name.trim()) {
            setError('Tên danh mục không được để trống');
            return;
        }

        const payload = { name, description };

        setSubmitting(true);
        const response = await apiFetch(
            isEditMode ? `/api/categories/${categoryToEdit!.id}` : '/api/categories',
            {
                method: isEditMode ? 'PUT' : 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload),
            }
        );
        const result = await response.json();
        setSubmitting(false);

        if (!response.ok) {
            setError(result.message || 'Lưu danh mục thất bại');
            return;
        }

        onSuccess();
    }

    return (
        <div className="bg-white rounded-2xl shadow-xl p-6 border border-purple-200 max-w-xl mx-auto">
            <h2 className="text-xl font-bold text-gray-800 mb-4">
                {isEditMode ? 'Sửa danh mục' : 'Thêm danh mục mới'}
            </h2>

            <form onSubmit={handleSubmit} className="space-y-4">
                {error && (
                    <p className="bg-red-50 text-red-600 text-sm px-3 py-2 rounded-lg border border-red-100">
                        {error}
                    </p>
                )}

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Tên danh mục
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
                        Mô tả
                    </label>
                    <textarea
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        rows={3}
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