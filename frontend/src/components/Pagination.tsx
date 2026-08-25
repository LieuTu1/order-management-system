'use client';

interface PaginationProps {
    page: number; // 0-based
    totalPages: number;
    onPageChange: (page: number) => void;
}

export default function Pagination({ page, totalPages, onPageChange }: PaginationProps) {
    if (totalPages <= 1) return null;

    return (
        <div className="flex items-center justify-center gap-2 mt-6">
            <button
                onClick={() => onPageChange(page - 1)}
                disabled={page === 0}
                className="px-3 py-1.5 rounded-lg text-sm font-medium border border-gray-300 text-gray-600 hover:bg-gray-50 transition disabled:opacity-40 disabled:cursor-not-allowed"
            >
                Trước
            </button>

            <span className="text-sm text-gray-600 px-2">
                Trang <span className="font-semibold text-purple-700">{page + 1}</span> / {totalPages}
            </span>

            <button
                onClick={() => onPageChange(page + 1)}
                disabled={page >= totalPages - 1}
                className="px-3 py-1.5 rounded-lg text-sm font-medium border border-gray-300 text-gray-600 hover:bg-gray-50 transition disabled:opacity-40 disabled:cursor-not-allowed"
            >
                Sau
            </button>
        </div>
    );
}