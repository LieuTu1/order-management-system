'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';

export default function LoginForm() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const router = useRouter();

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError('');

        const response = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
        });

        const result = await response.json();

        if (!response.ok) {
            setError(result.message);
            return;
        }

        localStorage.setItem('token', result.data.token);
        localStorage.setItem('refreshToken', result.data.refreshToken);
        router.push('/products');
    }

    return (
        <div className="bg-white rounded-2xl shadow-xl p-8 border border-purple-200">
            <h1 className="text-2xl font-bold text-gray-800 mb-1">Đăng nhập</h1>
            <p className="text-gray-500 text-sm mb-6">Chào mừng quay lại</p>

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
                    <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    />
                </div>

                <button
                    type="submit"
                    className="w-full bg-purple-700 hover:bg-purple-800 text-white font-medium py-2.5 rounded-lg transition shadow-md shadow-purple-300"
                >
                    Login
                </button>
            </form>

            <div className="flex items-center gap-3 my-5">
                <div className="flex-1 h-px bg-gray-200" />
                <span className="text-xs text-gray-400">Hoặc</span>
                <div className="flex-1 h-px bg-gray-200" />
            </div>

            <a
                href="http://localhost:8080/oauth2/authorization/google"
                className="w-full flex items-center justify-center gap-2 border border-gray-300 rounded-lg py-2.5 text-sm font-medium text-gray-700 hover:bg-gray-50 transition"
            >
                <svg className="w-5 h-5" viewBox="0 0 48 48">
                    <path fill="#FFC107" d="M43.6 20.5H42V20H24v8h11.3c-1.6 4.6-6 8-11.3 8-6.6 0-12-5.4-12-12s5.4-12 12-12c3 0 5.8 1.1 7.9 3l5.7-5.7C33.9 6.1 29.2 4 24 4 12.9 4 4 12.9 4 24s8.9 20 20 20 20-8.9 20-20c0-1.3-.1-2.7-.4-3.5z"/>
                    <path fill="#FF3D00" d="M6.3 14.7l6.6 4.8C14.6 16 18.9 13 24 13c3 0 5.8 1.1 7.9 3l5.7-5.7C33.9 6.1 29.2 4 24 4 16.3 4 9.6 8.3 6.3 14.7z"/>
                    <path fill="#4CAF50" d="M24 44c5.1 0 9.8-2 13.2-5.2l-6.1-5.2C29.1 35.6 26.6 36.5 24 36.5c-5.2 0-9.6-3.3-11.3-8l-6.5 5C9.4 39.6 16.1 44 24 44z"/>
                    <path fill="#1976D2" d="M43.6 20.5H42V20H24v8h11.3c-.8 2.3-2.2 4.2-4.1 5.6l6.1 5.2C39.6 36.6 44 31 44 24c0-1.3-.1-2.7-.4-3.5z"/>
                </svg>
                Đăng nhập với Google
            </a>

            <p className="text-center text-sm text-gray-500 mt-5">
                Chưa có tài khoản?{' '}
                <a href="/register" className="text-purple-700 hover:text-purple-900 font-medium">
                    Đăng ký
                </a>
            </p>
        </div>
    );
}