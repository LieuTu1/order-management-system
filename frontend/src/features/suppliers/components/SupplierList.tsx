'use client';

import { useState, useEffect } from 'react';
import type { SupplierOption } from '@/features/products/types';
import SupplierCard from './SupplierCard';
import SupplierForm from './SupplierForm';
import { apiFetch } from '@/lib/apiFetch';
import { isAdmin } from '@/lib/jwt';

export default function SupplierList() {
    const admin = isAdmin();

    const [suppliers, setSuppliers] = useState<SupplierOption[]>([]);
    const [error, setError] = useState('');
    const [editingSupplier, setEditingSupplier] =
        useState<SupplierOption | 'new' | null>(null);
    const [searchTerm, setSearchTerm] = useState('');

    async function fetchSuppliers() {
        const response = await apiFetch('/api/suppliers', {
            method: 'GET',
        });

        const result = await response.json();

        if (!response.ok) {
            setError(
                result.message || 'Không thể tải danh sách nhà cung cấp'
            );
            return;
        }

        setSuppliers(result.data);
    }

    useEffect(() => {
        fetchSuppliers();
    }, []);

    async function handleDelete(id: number) {
        const confirmed = confirm(
            'Bạn chắc chắn muốn xóa nhà cung cấp này?'
        );

        if (!confirmed) return;

        const response = await apiFetch(`/api/suppliers/${id}`, {
            method: 'DELETE',
        });

        if (!response.ok) {
            const result = await response.json();
            alert(result.message || 'Xóa thất bại');
            return;
        }

        setSuppliers(
            suppliers.filter((supplier) => supplier.id !== id)
        );
    }

    function handleFormSuccess() {
        setEditingSupplier(null);
        fetchSuppliers();
    }

    const filteredSuppliers = suppliers.filter((supplier) => {
        const keyword = searchTerm.toLowerCase();

        return (
            supplier.name.toLowerCase().includes(keyword) ||
            supplier.phone.toLowerCase().includes(keyword) ||
            supplier.email.toLowerCase().includes(keyword) ||
            supplier.address.toLowerCase().includes(keyword)
        );
    });

    if (error) {
        return (
            <p className="bg-red-50 text-red-600 text-sm px-4 py-3 rounded-lg border border-red-100">
                {error}
            </p>
        );
    }

    if (editingSupplier !== null) {
        return (
            <SupplierForm
                supplierToEdit={
                    editingSupplier === 'new'
                        ? undefined
                        : editingSupplier
                }
                onSuccess={handleFormSuccess}
                onCancel={() => setEditingSupplier(null)}
            />
        );
    }

    return (
        <div>
            <div className="flex justify-between items-center mb-5 gap-4">
                <h2 className="text-lg font-semibold text-gray-800 whitespace-nowrap">
                    Danh sách nhà cung cấp
                </h2>

                <input
                    type="text"
                    placeholder="Tìm theo tên, SĐT, email..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full max-w-md mx-auto px-4 py-2 rounded-lg border-2 border-purple-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition text-sm"
                />

                {admin && (
                    <button
                        onClick={() => setEditingSupplier('new')}
                        className="bg-purple-700 hover:bg-purple-800 text-white text-sm font-medium px-4 py-2 rounded-lg transition shadow-md shadow-purple-300 whitespace-nowrap"
                    >
                        + Thêm nhà cung cấp
                    </button>
                )}
            </div>

            {filteredSuppliers.length === 0 ? (
                <p className="text-gray-500 text-sm">
                    Không tìm thấy nhà cung cấp nào.
                </p>
            ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
                    {filteredSuppliers.map((supplier) => (
                        <SupplierCard
                            key={supplier.id}
                            supplier={supplier}
                            onEdit={
                                admin
                                    ? () => setEditingSupplier(supplier)
                                    : undefined
                            }
                            onDelete={
                                admin
                                    ? () => handleDelete(supplier.id)
                                    : undefined
                            }
                        />
                    ))}
                </div>
            )}
        </div>
    );
}

