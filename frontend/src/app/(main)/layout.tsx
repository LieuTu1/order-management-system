'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { isAdmin } from '@/lib/jwt';

export default function MainLayout({
                                       children,
                                   }: {
    children: React.ReactNode;
}) {
    const router = useRouter();

    const [checked, setChecked] = useState(false);
    const [showUsers, setShowUsers] = useState(false);
    const [username, setUsername] = useState('');

    useEffect(() => {
        const token = localStorage.getItem('token');

        if (!token) {
            router.push('/login');
            return;
        }

        setChecked(true);
        setShowUsers(isAdmin());

        // Lấy username từ JWT
        try {
            const payload = JSON.parse(
                atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/'))
            );

            setUsername(
                payload.username ||
                payload.sub ||
                'User'
            );
        } catch {
            setUsername('User');
        }
    }, [router]);

    function handleLogout() {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        router.push('/login');
    }

    if (!checked) {
        return null;
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-purple-100 via-violet-200 to-fuchsia-100">

            <nav className="bg-white/80 backdrop-blur-sm shadow-sm border-b border-purple-200">
                <div className="max-w-6xl mx-auto px-6 py-4 flex items-center justify-between">

                    {/* LEFT - Logo + Navigation */}
                    <div className="flex items-center gap-8">

                        <span className="font-bold text-lg text-purple-800">
                            OMS
                        </span>

                        <Link
                            href="/products"
                            className="text-gray-700 hover:text-purple-800 font-medium transition"
                        >
                            Products
                        </Link>

                        <Link
                            href="/orders"
                            className="text-gray-700 hover:text-purple-800 font-medium transition"
                        >
                            Orders
                        </Link>

                        {showUsers && (
                            <Link
                                href="/users"
                                className="text-gray-700 hover:text-purple-800 font-medium transition"
                            >
                                Users
                            </Link>
                        )}

                        <Link
                            href="/customers"
                            className="text-gray-700 hover:text-purple-800 font-medium transition"
                        >
                            Customers
                        </Link>

                        <Link
                            href="/categories"
                            className="text-gray-700 hover:text-purple-800 font-medium transition"
                        >
                            Categories
                        </Link>

                        <Link
                            href="/suppliers"
                            className="text-gray-700 hover:text-purple-800 font-medium transition"
                        >
                            Suppliers
                        </Link>

                        <Link
                            href="/profile"
                            className="text-gray-700 hover:text-purple-800 font-medium transition"
                        >
                            Profile
                        </Link>
                    </div>


                    <div className="flex items-center gap-5">

                        <span className="text-sm font-medium text-gray-900">
                            Xin chào, <span className="font-semibold text-purple-700">{username}</span>
                        </span>

                        <button
                            onClick={handleLogout}
                            className="text-sm text-red-600 hover:text-red-800 font-medium transition"
                        >
                            Logout
                        </button>

                    </div>
                </div>
            </nav>

            <main className="max-w-6xl mx-auto px-6 py-8">
                {children}
            </main>

        </div>
    );
}

