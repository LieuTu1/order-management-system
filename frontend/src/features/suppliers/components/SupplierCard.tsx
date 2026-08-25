'use client';

import type { SupplierOption } from '@/features/products/types';

interface SupplierCardProps {
    supplier: SupplierOption;
    onEdit?: () => void;
    onDelete?: () => void;
}

export default function SupplierCard({
                                         supplier,
                                         onEdit,
                                         onDelete,
                                     }: SupplierCardProps) {
    return (
        <article className="bg-white rounded-2xl shadow-md p-5 border border-purple-100 hover:shadow-lg transition">

            <div className="flex justify-between items-start mb-4">
                <h2 className="font-semibold text-gray-800 text-lg">
                    {supplier.name}
                </h2>
            </div>

            <div className="text-sm text-gray-600 space-y-1 mb-4">
                <p>Tên nhà cung cấp: {supplier.name}</p>
                <p>Số điện thoại: {supplier.phone}</p>
                <p>Email: {supplier.email}</p>
                <p>Địa chỉ: {supplier.address}</p>
            </div>

            {(onEdit || onDelete) && (
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
            )}

        </article>
    );
}