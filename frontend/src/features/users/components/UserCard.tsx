'use client';

import type { User } from '../types';

interface UserCardProps {
    user: User;
    onEdit: () => void;
    onDelete: () => void;
}

export default function UserCard({ user, onEdit, onDelete }: UserCardProps) {
    return (
        <article className="bg-white border border-gray-200 rounded-xl p-5 mb-4 shadow-sm hover:shadow-md transition">
            <div className="flex justify-between items-center mb-3">
                <span className="font-semibold text-gray-800">{user.roleName}</span>
                <span className="text-xs font-medium px-2.5 py-1 rounded-full bg-purple-50 text-purple-700 border border-purple-200">
                    {user.status}
                </span>
            </div>

            <div className="text-sm text-gray-600 space-y-1 mb-4">
                <p>Tài khoản: {user.username}</p>
                <p>Họ và tên: {user.fullName}</p>
                <p>Số điện thoại: {user.phone}</p>
                <p>Email: {user.email}</p>
                <p>Địa chỉ: {user.address || '—'}</p>
            </div>

            <div className="flex gap-2">
                <button
                    onClick={onEdit}
                    className="flex-1 text-sm bg-purple-50 hover:bg-purple-100 text-purple-700 font-medium py-2 rounded-lg transition"
                >
                    Sửa
                </button>
                <button
                    onClick={onDelete}
                    className="flex-1 text-sm bg-red-50 hover:bg-red-100 text-red-600 font-medium py-2 rounded-lg transition"
                >
                    Xóa
                </button>
            </div>
        </article>
    );
}