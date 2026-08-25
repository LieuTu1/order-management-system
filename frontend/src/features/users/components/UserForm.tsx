'use client';

import { useState, useEffect } from 'react';
import type { User, RoleOption } from '../types';
import { apiFetch } from '@/lib/apiFetch';

interface UserFormProps {
    userToEdit?: User;
    onSuccess: () => void;
    onCancel: () => void;
}

export default function UserForm({ userToEdit, onSuccess, onCancel }: UserFormProps) {
    const isEditing = !!userToEdit;

    const [username, setUsername] = useState(userToEdit?.username || '');
    const [password, setPassword] = useState('');
    const [fullName, setFullName] = useState(userToEdit?.fullName || '');
    const [phone, setPhone] = useState(userToEdit?.phone || '');
    const [email, setEmail] = useState(userToEdit?.email || '');
    const [address, setAddress] = useState(userToEdit?.address || '');
    const [roleId, setRoleId] = useState(userToEdit?.roleId || 0);
    const [roles, setRoles] = useState<RoleOption[]>([]);
    const [error, setError] = useState('');
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        async function fetchRoles() {
            const response = await apiFetch('/api/roles', { method: 'GET' });
            const result = await response.json();

            if (response.ok) {
                setRoles(result.data);
                if (!isEditing && result.data.length > 0) {
                    setRoleId(result.data[0].id);
                }
            }
        }

        fetchRoles();
    }, []);

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError('');

        if (!isEditing && !password.trim()) {
            setError('Vui lòng nhập mật khẩu cho người dùng mới');
            return;
        }

        if (password && password.length < 6) {
            setError('Mật khẩu phải có ít nhất 6 ký tự');
            return;
        }

        if (phone && !/^0\d{9,10}$/.test(phone.trim())) {
            setError('Số điện thoại không hợp lệ (phải bắt đầu bằng 0, 10-11 chữ số)');
            return;
        }

        if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) {
            setError('Email không đúng định dạng');
            return;
        }

        const body = { username, password, fullName, phone, email, address, roleId };

        const url = isEditing ? `/api/users/${userToEdit!.id}` : '/api/users';
        const method = isEditing ? 'PUT' : 'POST';

        setSubmitting(true);

        try {
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
        } catch {
            setError('Không thể kết nối tới server hoặc phản hồi không hợp lệ. Vui lòng thử lại.');
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <div className="bg-white rounded-3xl shadow-xl p-6 border border-purple-200 max-w-xl mx-auto">
            <h2 className="text-xl font-bold text-gray-800 mb-4">
                {isEditing ? 'Sửa người dùng' : 'Thêm người dùng mới'}
            </h2>

            <form onSubmit={handleSubmit} className="space-y-4">
                {error && (
                    <p className="bg-red-50 text-red-600 text-sm px-3 py-2 rounded-lg border border-red-100">
                        {error}
                    </p>
                )}

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Username</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Password{' '}
                        {isEditing ? (
                            <span className="text-gray-400">(để trống nếu không đổi)</span>
                        ) : (
                            <span className="text-red-500">*</span>
                        )}
                    </label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Họ và tên</label>
                    <input
                        type="text"
                        value={fullName}
                        onChange={(e) => setFullName(e.target.value)}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Số điện thoại</label>
                    <input
                        type="text"
                        value={phone}
                        onChange={(e) => setPhone(e.target.value)}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Địa chỉ</label>
                    <input
                        type="text"
                        value={address}
                        onChange={(e) => setAddress(e.target.value)}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Role</label>
                    <select
                        value={roleId}
                        onChange={(e) => setRoleId(Number(e.target.value))}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    >
                        {roles.map((r) => (
                            <option key={r.id} value={r.id}>
                                {r.role}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="flex gap-3 pt-2">
                    <button
                        type="submit"
                        disabled={submitting}
                        className="flex-1 bg-purple-700 hover:bg-purple-800 text-white font-medium py-2.5 rounded-lg transition shadow-md shadow-purple-300 disabled:opacity-50"
                    >
                        {submitting ? 'Đang lưu...' : isEditing ? 'Lưu thay đổi' : 'Tạo mới'}
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