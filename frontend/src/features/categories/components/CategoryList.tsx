'use client';

import { useState, useEffect } from 'react';
import type { CategoryOption } from '@/features/products/types';
import CategoryForm from './CategoryForm';
import { apiFetch } from '@/lib/apiFetch';
import { isAdmin } from '@/lib/jwt';

export default function CategoryList() {
    const admin = isAdmin();
    const [categories, setCategories] = useState<CategoryOption[]>([]);
    const [error, setError] = useState('');
    const [editingCategory, setEditingCategory] = useState<CategoryOption | 'new' | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [deletingId, setDeletingId] = useState<number | null>(null);

    async function fetchCategories() {
        const response = await apiFetch('/api/categories', { method: 'GET' });
        const result = await response.json();

        if (!response.ok) {
            setError(result.message || 'Không thể tải danh sách danh mục');
            return;
        }

        setCategories(result.data);
    }

    useEffect(() => {
        fetchCategories();
    }, []);

    async function handleDelete(id: number) {
        const confirmed = confirm('Bạn chắc chắn muốn xóa danh mục này?');
        if (!confirmed) return;

        setDeletingId(id);
        const response = await apiFetch(`/api/categories/${id}`, { method: 'DELETE' });
        const result = await response.json();
        setDeletingId(null);

        if (!response.ok) {
            alert(result.message || 'Xóa thất bại');
            return;
        }

        setCategories(categories.filter((c) => c.id !== id));
    }

    function handleFormSuccess() {
        setEditingCategory(null);
        fetchCategories();
    }

    const filteredCategories = categories.filter((c) => {
        const keyword = searchTerm.toLowerCase();
        return (
            c.name.toLowerCase().includes(keyword) ||
            (c.description || '').toLowerCase().includes(keyword)
        );
    });

    if (error) {
        return (
            <p className="bg-red-50 text-red-600 text-sm px-4 py-3 rounded-lg border border-red-100">
                {error}
            </p>
        );
    }

    if (editingCategory !== null) {
        return (
            <CategoryForm
                categoryToEdit={editingCategory === 'new' ? undefined : editingCategory}
                onSuccess={handleFormSuccess}
                onCancel={() => setEditingCategory(null)}
            />
        );
    }

    return (
        <div>
            <div className="grid grid-cols-3 items-center mb-5 gap-4">
                <h2 className="text-lg font-semibold text-gray-800 whitespace-nowrap">
                    Danh sách danh mục
                </h2>

                <input
                    type="text"
                    placeholder="Tìm tên, mô tả..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full max-w-md mx-auto px-4 py-2 rounded-lg border-2 border-purple-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition text-sm"
                />

                {admin && (
                    <button
                        onClick={() => setEditingCategory('new')}
                        className="justify-self-end bg-purple-700 hover:bg-purple-800 text-white text-sm font-medium px-4 py-2 rounded-lg transition shadow-md shadow-purple-300 whitespace-nowrap"
                    >
                        + Thêm danh mục
                    </button>
                )}
            </div>

            {filteredCategories.length === 0 ? (
                <p className="text-gray-500 text-sm">Không tìm thấy danh mục nào.</p>
            ) : (
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                    <table className="w-full text-sm">
                        <thead>
                        <tr className="bg-purple-50 text-left text-gray-700">
                            <th className="px-5 py-3 font-bold">Tên</th>
                            <th className="px-5 py-3 font-bold">Mô tả</th>
                            {admin && (
                                <th className="px-5 py-3 font-bold text-right">Thao tác</th>
                            )}
                        </tr>
                        </thead>
                        <tbody>
                        {filteredCategories.map((c) => (
                            <tr key={c.id} className="border-t border-gray-100 even:bg-gray-50/50">
                                <td className="px-4 py-3 text-gray-800 font-medium">{c.name}</td>
                                <td className="px-4 py-3 text-gray-600">{c.description}</td>
                                {admin && (
                                    <td className="px-4 py-3 text-right space-x-3">
                                        <button
                                            onClick={() => setEditingCategory(c)}
                                            className="text-purple-700 hover:text-purple-900 font-medium"
                                        >
                                            Sửa
                                        </button>
                                        <button
                                            onClick={() => handleDelete(c.id)}
                                            disabled={deletingId === c.id}
                                            className="text-red-600 hover:text-red-800 font-medium disabled:opacity-50"
                                        >
                                            {deletingId === c.id ? 'Đang xóa...' : 'Xóa'}
                                        </button>
                                    </td>
                                )}
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}