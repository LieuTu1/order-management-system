'use client';

import { useState, useEffect } from 'react';
import type { User } from '../types';
import UserCard from './UserCard';
import UserForm from './UserForm';
import Pagination from '@/components/Pagination';
import { apiFetch } from '@/lib/apiFetch';

export default function UserList() {
    const [users, setUsers] = useState<User[]>([]);
    const [error, setError] = useState('');
    const [editingUser, setEditingUser] = useState<User | 'new' | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    async function fetchUsers(pageToFetch: number) {
        const response = await apiFetch(
            `/api/users?page=${pageToFetch}&size=9&sort=id,desc`,
            { method: 'GET' }
        );
        const result = await response.json();

        if (!response.ok) {
            setError(result.message || 'Không thể tải danh sách người dùng');
            return;
        }

        setUsers(result.data.content ?? []);
        setTotalPages(result.data.totalPages ?? 0);
    }

    useEffect(() => {
        fetchUsers(page);
    }, [page]);

    async function handleDelete(id: number) {
        const confirmed = confirm('Bạn chắc chắn muốn xóa người dùng này?');
        if (!confirmed) return;

        const response = await apiFetch(`/api/users/${id}`, { method: 'DELETE' });

        if (!response.ok) {
            const result = await response.json();
            alert(result.message || 'Xóa thất bại');
            return;
        }

        fetchUsers(page);
    }

    function handleFormSuccess() {
        setEditingUser(null);
        fetchUsers(page);
    }

    // Tìm kiếm + sắp xếp chỉ trong phạm vi trang hiện tại (dữ liệu đã phân trang từ backend)
    const filteredUsers = users
        .filter((user) => {
            const keyword = searchTerm.toLowerCase();
            return (
                user.username.toLowerCase().includes(keyword) ||
                user.fullName.toLowerCase().includes(keyword) ||
                user.email.toLowerCase().includes(keyword) ||
                user.roleName.toLowerCase().includes(keyword)
            );
        })
        .sort((a, b) => {
            // Ưu tiên ADMIN lên trước, STAFF xuống sau
            if (a.roleName !== b.roleName) {
                if (a.roleName === 'ADMIN') return -1;
                if (b.roleName === 'ADMIN') return 1;
                return a.roleName.localeCompare(b.roleName);
            }
            // Cùng role thì sắp theo tên A-Z
            return a.fullName.localeCompare(b.fullName, 'vi');
        });

    if (error) {
        return (
            <p className="bg-red-50 text-red-600 text-sm px-4 py-3 rounded-lg border border-red-100">
                {error}
            </p>
        );
    }

    if (editingUser !== null) {
        return (
            <UserForm
                userToEdit={editingUser === 'new' ? undefined : editingUser}
                onSuccess={handleFormSuccess}
                onCancel={() => setEditingUser(null)}
            />
        );
    }

    return (
        <div>
            <div className="flex justify-between items-center mb-5 gap-4">
                <h2 className="text-lg font-semibold text-gray-800 whitespace-nowrap">
                    Danh sách người dùng
                </h2>

                <input
                    type="text"
                    placeholder="Tìm theo tên, username, email..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="flex-1 max-w-xs px-4 py-2 rounded-lg border-2 border-purple-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition text-sm "
                />

                <button
                    onClick={() => setEditingUser('new')}
                    className="bg-purple-700 hover:bg-purple-800 text-white text-sm font-medium px-4 py-2 rounded-lg transition shadow-md shadow-purple-300 whitespace-nowrap"
                >
                    + Thêm người dùng
                </button>
            </div>

            {filteredUsers.length === 0 ? (
                <p className="text-gray-500 text-sm">Không tìm thấy người dùng nào.</p>
            ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
                    {filteredUsers.map((user) => (
                        <UserCard
                            key={user.id}
                            user={user}
                            onEdit={() => setEditingUser(user)}
                            onDelete={() => handleDelete(user.id)}
                        />
                    ))}
                </div>
            )}

            <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
    );
}