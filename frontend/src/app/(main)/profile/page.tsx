'use client';

import { useState, useEffect } from 'react';
import { apiFetch } from '@/lib/apiFetch';

interface MeInfo {
    id: number;
    username: string;
    fullName: string;
    phone: string;
    email: string;
    address: string;
    role: string;
}

export default function ProfilePage() {
    const [me, setMe] = useState<MeInfo | null>(null);
    const [loading, setLoading] = useState(true);

    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        async function loadMe() {
            const response = await apiFetch('/api/auth/me', { method: 'GET' });
            const result = await response.json();
            if (response.ok) setMe(result.data);
            setLoading(false);
        }
        loadMe();
    }, []);

    async function handleChangePassword(e: React.FormEvent) {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (newPassword.length < 6) {
            setError('Mật khẩu mới phải có ít nhất 6 ký tự');
            return;
        }
        if (newPassword !== confirmPassword) {
            setError('Xác nhận mật khẩu mới không khớp');
            return;
        }

        setSubmitting(true);
        try {
            const response = await apiFetch('/api/auth/change-password', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ oldPassword, newPassword }),
            });
            const result = await response.json();

            if (!response.ok) {
                setError(result.message || 'Đổi mật khẩu thất bại');
                return;
            }

            setSuccess('Đổi mật khẩu thành công');
            setOldPassword('');
            setNewPassword('');
            setConfirmPassword('');
        } catch {
            setError('Không thể kết nối tới server. Vui lòng thử lại.');
        } finally {
            setSubmitting(false);
        }
    }

    if (loading) {
        return <p className="text-gray-500 text-sm">Đang tải...</p>;
    }

    return (
        <main>
            <h1 className="text-2xl font-bold text-purple-800 mb-6 text-center">Hồ sơ cá nhân</h1>

            <div className="space-y-6 max-w-xl mx-auto">
                {/* Thông tin cá nhân */}
                <div className="bg-white rounded-3xl shadow-xl p-6 border border-purple-200">
                    <h2 className="text-xl font-bold text-gray-800 mb-4">Thông tin tài khoản</h2>

                    <div className="space-y-3 text-sm">
                        <div className="flex justify-between border-b border-gray-100 pb-2">
                            <span className="text-gray-500">Tên đăng nhập</span>
                            <span className="text-gray-800 font-medium">{me?.username}</span>
                        </div>
                        <div className="flex justify-between border-b border-gray-100 pb-2">
                            <span className="text-gray-500">Họ tên</span>
                            <span className="text-gray-800 font-medium">{me?.fullName}</span>
                        </div>
                        <div className="flex justify-between border-b border-gray-100 pb-2">
                            <span className="text-gray-500">Số điện thoại</span>
                            <span className="text-gray-800 font-medium">{me?.phone}</span>
                        </div>
                        <div className="flex justify-between border-b border-gray-100 pb-2">
                            <span className="text-gray-500">Email</span>
                            <span className="text-gray-800 font-medium">{me?.email}</span>
                        </div>
                        <div className="flex justify-between border-b border-gray-100 pb-2">
                            <span className="text-gray-500">Địa chỉ</span>
                            <span className="text-gray-800 font-medium">{me?.address || '—'}</span>
                        </div>
                        <div className="flex justify-between">
                            <span className="text-gray-500">Vai trò</span>
                            <span className="text-purple-700 font-medium">
                                {me?.role === 'ROLE_ADMIN' ? 'Quản trị viên' : 'Nhân viên'}
                            </span>
                        </div>
                    </div>
                </div>

                {/* Đổi mật khẩu */}
                <div className="bg-white rounded-3xl shadow-xl p-6 border border-purple-200">
                    <h2 className="text-xl font-bold text-gray-800 mb-4">Đổi mật khẩu</h2>

                    <form onSubmit={handleChangePassword} className="space-y-4">
                        {error && (
                            <p className="bg-red-50 text-red-600 text-sm px-3 py-2 rounded-lg border border-red-100">
                                {error}
                            </p>
                        )}
                        {success && (
                            <p className="bg-green-50 text-green-700 text-sm px-3 py-2 rounded-lg border border-green-100">
                                {success}
                            </p>
                        )}

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Mật khẩu hiện tại
                            </label>
                            <input
                                type="password"
                                value={oldPassword}
                                onChange={(e) => setOldPassword(e.target.value)}
                                className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Mật khẩu mới
                            </label>
                            <input
                                type="password"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                                className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Xác nhận mật khẩu mới
                            </label>
                            <input
                                type="password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                            />
                        </div>

                        <button
                            type="submit"
                            disabled={submitting}
                            className="w-full bg-purple-700 hover:bg-purple-800 text-white font-medium py-2.5 rounded-lg transition shadow-md shadow-purple-300 disabled:opacity-50"
                        >
                            {submitting ? 'Đang lưu...' : 'Đổi mật khẩu'}
                        </button>
                    </form>
                </div>
            </div>
        </main>
    );
}