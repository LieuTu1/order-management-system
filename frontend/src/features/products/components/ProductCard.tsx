'use client';

import type { Product } from '../types';

interface ProductCardProps {
    product: Product;
    onEdit?: () => void;
    onDelete?: () => void;
}

export default function ProductCard({ product, onEdit, onDelete }: ProductCardProps) {
    return (
        <article className="bg-white border border-gray-200 rounded-xl p-4 shadow-sm hover:shadow-md transition">
            <img
                src={`${API_URL}${product.imageUrl}`}
                alt={product.name}
                className="w-full h-[200px] object-contain rounded-lg bg-gray-50 mb-3"
            />

            <div className="flex justify-between items-start mb-2">
                <h2 className="font-semibold text-gray-800">{product.name}</h2>
                <span className="text-xs font-medium px-2.5 py-1 rounded-full bg-purple-100 text-purple-800 border border-purple-300 whitespace-nowrap ml-2">
                    {product.status}
                </span>
            </div>

            <div className="text-sm text-gray-600 space-y-1 mb-3">
                <p>Mã sản phẩm: {product.sku}</p>
                <p>Danh mục: {product.categoryName}</p>
                <p>Nhà cung cấp: {product.supplierName}</p>
                <p>Kho: {product.stock}</p>
            </div>

            <hr className="border-gray-100 mb-3" />

            <p className="text-right mb-3">
                <strong className="text-purple-800 text-lg">
                    {product.price.toLocaleString('vi-VN')} đ
                </strong>
            </p>

            <div className="flex gap-2">
                {onEdit && (
                    <button
                        onClick={onEdit}
                        className="flex-1 text-sm bg-purple-50 hover:bg-purple-100 text-purple-700 font-medium py-2 rounded-lg transition"
                    >
                    Sửa
                    </button>
                )}

                {onDelete && (
                <button
                    onClick={onDelete}
                    className="flex-1 text-sm bg-red-50 hover:bg-red-100 text-red-600 font-medium py-2 rounded-lg transition"
                >
                    Xóa
                </button>
                )}

            </div>
        </article>
    );
}