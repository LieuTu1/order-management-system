'use client';

import { useState, useEffect } from 'react';
import type { Product } from '../types';
import ProductCard from './ProductCard';
import ProductForm from './ProductForm';
import Pagination from '@/components/Pagination';
import { apiFetch } from '@/lib/apiFetch';
import { isAdmin } from '@/lib/jwt';

export default function ProductList() {
    const admin = isAdmin();
    const [products, setProducts] = useState<Product[]>([]);
    const [error, setError] = useState('');
    const [editingProduct, setEditingProduct] = useState<Product | 'new' | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    async function fetchProducts(pageToFetch: number) {
        const response = await apiFetch(
            `/api/products?page=${pageToFetch}&size=9&sort=id,desc`,
            { method: 'GET' }
        );
        const result = await response.json();

        if (!response.ok) {
            setError(result.message || 'Không thể tải danh sách sản phẩm');
            return;
        }

        setProducts(result.data.content ?? []);
        setTotalPages(result.data.totalPages ?? 0);
    }

    useEffect(() => {
        fetchProducts(page);
    }, [page]);

    async function handleDelete(id: number) {
        const confirmed = confirm('Bạn chắc chắn muốn xóa sản phẩm này?');
        if (!confirmed) return;

        const response = await apiFetch(`/api/products/${id}`, { method: 'DELETE' });

        if (!response.ok) {
            const result = await response.json();
            alert(result.message || 'Xóa thất bại');
            return;
        }

        fetchProducts(page);
    }

    function handleFormSuccess() {
        setEditingProduct(null);
        fetchProducts(page);
    }

    // Tìm kiếm chỉ lọc trong phạm vi trang hiện tại (dữ liệu đã phân trang từ backend)
    const filteredProducts = products.filter((product) => {
        const keyword = searchTerm.toLowerCase();
        return (
            product.name.toLowerCase().includes(keyword) ||
            product.sku.toLowerCase().includes(keyword) ||
            product.categoryName.toLowerCase().includes(keyword)
        );
    });

    if (error) {
        return (
            <p className="bg-red-50 text-red-600 text-sm px-4 py-3 rounded-lg border border-red-100">
                {error}
            </p>
        );
    }

    if (editingProduct !== null) {
        return (
            <ProductForm
                productToEdit={editingProduct === 'new' ? undefined : editingProduct}
                onSuccess={handleFormSuccess}
                onCancel={() => setEditingProduct(null)}
            />
        );
    }

    return (
        <div>
            <div className="grid grid-cols-3 items-center mb-5 gap-4">
                <h2 className="text-lg font-semibold text-gray-800 whitespace-nowrap">
                    Danh sách sản phẩm
                </h2>

                <input
                    type="text"
                    placeholder="Tìm theo tên,mã sản phẩm, danh mục..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full max-w-md mx-auto px-4 py-2 rounded-lg border-2 border-purple-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition text-sm"
                />

                {admin && (
                    <button
                        onClick={() => setEditingProduct('new')}
                        className="justify-self-end bg-purple-700 hover:bg-purple-800 text-white text-sm font-medium px-4 py-2 rounded-lg transition shadow-md shadow-purple-300 whitespace-nowrap"
                    >
                        + Thêm sản phẩm
                    </button>
                )}
            </div>

            {filteredProducts.length === 0 ? (
                <p className="text-gray-500 text-sm">Không tìm thấy sản phẩm nào.</p>
            ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
                    {filteredProducts.map((product) => (
                        <ProductCard
                            key={product.id}
                            product={product}
                            onEdit={admin ? () => setEditingProduct(product) : undefined}
                            onDelete={admin ? () => handleDelete(product.id) : undefined}
                        />
                    ))}
                </div>
            )}

            <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
    );
}