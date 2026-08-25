'use client';

import { Suspense, useEffect, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';

function OAuth2CallbackContent() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const [error, setError] = useState('');

    useEffect(() => {
        const token = searchParams.get('token');
        const refreshToken = searchParams.get('refreshToken');

        if (!token) {
            setError('Đăng nhập Google thất bại: không nhận được token.');
            return;
        }

        localStorage.setItem('token', token);
        if (refreshToken) {
            localStorage.setItem('refreshToken', refreshToken);
        }

        router.push('/products');
    }, [searchParams, router]);

    if (error) {
        return (
            <div className="bg-white rounded-2xl shadow-xl p-8 border border-red-200 max-w-md text-center">
                <p className="text-red-600 font-medium mb-4">{error}</p>
                <a href="/login" className="text-purple-700 hover:text-purple-900 font-medium">
                    Quay lại trang đăng nhập
                </a>
            </div>
        );
    }

    return <p className="text-gray-600">Đang đăng nhập...</p>;
}

export default function OAuth2CallbackPage() {
    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-purple-100 via-violet-200 to-fuchsia-100">
            <Suspense fallback={<p className="text-gray-600">Đang tải...</p>}>
                <OAuth2CallbackContent />
            </Suspense>
        </div>
    );
}