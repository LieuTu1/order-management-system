'use client';

import { useState, useEffect } from 'react';
import type { Product, CategoryOption, SupplierOption } from '../types';
import { apiFetch } from '@/lib/apiFetch';

interface ProductFormProps {
    productToEdit?: Product;
    onSuccess: () => void;
    onCancel: () => void;
}

export default function ProductForm({ productToEdit, onSuccess, onCancel }: ProductFormProps) {
    const isEditing = !!productToEdit;

    const [sku, setSku] = useState(productToEdit?.sku || '');
    const [name, setName] = useState(productToEdit?.name || '');
    const [price, setPrice] = useState(productToEdit?.price || 0);
    const [stock, setStock] = useState(productToEdit?.stock || 0);
    const [categoryId, setCategoryId] = useState(productToEdit?.categoryId || 0);
    const [supplierId, setSupplierId] = useState(productToEdit?.supplierId || 0);

    const [categories, setCategories] = useState<CategoryOption[]>([]);
    const [suppliers, setSuppliers] = useState<SupplierOption[]>([]);
    const [error, setError] = useState('');

    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [status, setStatus] = useState(productToEdit?.status || 'ACTIVE');
    const [uploading, setUploading] = useState(false);

    useEffect(() => {
        async function fetchOptions() {
            const [catRes, supRes] = await Promise.all([
                apiFetch('/api/categories', { method: 'GET' }),
                apiFetch('/api/suppliers', { method: 'GET' }),
            ]);

            const catResult = await catRes.json();
            const supResult = await supRes.json();

            if (catRes.ok) {
                setCategories(catResult.data);
                if (!isEditing && catResult.data.length > 0) {
                    setCategoryId(catResult.data[0].id);
                }
            }

            if (supRes.ok) {
                setSuppliers(supResult.data);
                if (!isEditing && supResult.data.length > 0) {
                    setSupplierId(supResult.data[0].id);
                }
            }
        }

        fetchOptions();
    }, []);

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError('');

        if (!sku.trim() || !name.trim()) {
            setError('Vui lòng nhập đầy đủ mã sản phẩm và tên sản phẩm');
            return;
        }

        if (price < 0) {
            setError('Giá không được là số âm');
            return;
        }

        if (stock < 0) {
            setError('Tồn kho không được là số âm');
            return;
        }

        const body = {
            sku,
            name,
            price,
            stock,
            imageUrl: productToEdit?.imageUrl || '',
            categoryId,
            supplierId,
            status,
        };

        const url = isEditing ? `/api/products/${productToEdit!.id}` : '/api/products';
        const method = isEditing ? 'PUT' : 'POST';

        const response = await apiFetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
        });

        const result = await response.json();

        if (!response.ok) {
            setError(result.message || 'Có lỗi xảy ra');
            return;
        }

        onSuccess();
    }

    async function handleUploadImage() {
        if (!selectedFile || !productToEdit) return;

        setUploading(true);
        setError('');

        const formData = new FormData();
        formData.append('file', selectedFile);

        const response = await apiFetch(`/api/products/${productToEdit.id}/image`, {
            method: 'POST',
            body: formData,
        });

        setUploading(false);

        const result = await response.json();

        if (!response.ok) {
            setError(result.message || 'Upload ảnh thất bại');
            return;
        }

        setSelectedFile(null);
        onSuccess();
    }

    return (
        <div className="bg-white rounded-3xl shadow-xl p-6 border border-purple-200 max-w-xl mx-auto">
            <h2 className="text-xl font-bold text-gray-800 mb-4">
                {isEditing ? 'Sửa sản phẩm' : 'Thêm sản phẩm mới'}
            </h2>

            <form onSubmit={handleSubmit} className="space-y-4">
                {error && (
                    <p className="bg-red-50 text-red-600 text-sm px-3 py-2 rounded-lg border border-red-100">
                        {error}
                    </p>
                )}

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Mã sản phẩm</label>
                    <input
                        type="text"
                        value={sku}
                        onChange={(e) => setSku(e.target.value)}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Tên sản phẩm</label>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    />
                </div>

                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Giá</label>
                        <input
                            type="number"
                            value={price}
                            onChange={(e) => setPrice(Number(e.target.value))}
                            className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Tồn kho</label>
                        <input
                            type="number"
                            value={stock}
                            onChange={(e) => setStock(Number(e.target.value))}
                            className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                        />
                    </div>
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Danh mục</label>
                    <select
                        value={categoryId}
                        onChange={(e) => setCategoryId(Number(e.target.value))}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    >
                        {categories.map((c) => (
                            <option key={c.id} value={c.id}>
                                {c.name}
                            </option>
                        ))}
                    </select>
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Nhà cung cấp</label>
                    <select
                        value={supplierId}
                        onChange={(e) => setSupplierId(Number(e.target.value))}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    >
                        {suppliers.map((s) => (
                            <option key={s.id} value={s.id}>
                                {s.name}
                            </option>
                        ))}
                    </select>
                </div>

                {isEditing && (
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Trạng thái</label>
                        <select
                            value={status}
                            onChange={(e) => setStatus(e.target.value)}
                            className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                        >
                            <option value="ACTIVE">ACTIVE</option>
                            <option value="INACTIVE">INACTIVE</option>
                        </select>
                    </div>
                )}

                {isEditing && (
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Ảnh sản phẩm</label>

                        {productToEdit?.imageUrl && (
                            <img
                                src={`${API_URL}${productToEdit.imageUrl}`}
                                alt="Ảnh hiện tại"
                                className="w-32 h-32 object-contain rounded-lg bg-gray-50 mb-2 border border-gray-200"
                            />
                        )}

                        <input
                            type="file"
                            accept="image/*"
                            onChange={(e) => setSelectedFile(e.target.files?.[0] || null)}
                            className="w-full text-sm text-gray-600 file:mr-3 file:py-2 file:px-4 file:rounded-lg file:border-0 file:bg-purple-50 file:text-purple-700 file:font-medium hover:file:bg-purple-100"
                        />

                        {selectedFile && (
                            <button
                                type="button"
                                onClick={handleUploadImage}
                                disabled={uploading}
                                className="mt-2 text-sm bg-purple-700 hover:bg-purple-800 text-white font-medium px-4 py-2 rounded-lg transition disabled:opacity-50"
                            >
                                {uploading ? 'Đang tải lên...' : 'Tải ảnh lên'}
                            </button>
                        )}
                    </div>
                )}

                <div className="flex gap-3 pt-2">
                    <button
                        type="submit"
                        className="flex-1 bg-purple-700 hover:bg-purple-800 text-white font-medium py-2.5 rounded-lg transition shadow-md shadow-purple-300"
                    >
                        {isEditing ? 'Lưu thay đổi' : 'Tạo mới'}
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