const BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

// Hàm gọi API refresh, trả về token mới hoặc null nếu thất bại
async function refreshAccessToken(): Promise<string | null> {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) return null;

    const response = await fetch(`${BASE_URL}/api/auth/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken }),
    });

    if (!response.ok) return null;

    const result = await response.json();
    const newToken = result.data.token;
    const newRefreshToken = result.data.refreshToken;

    localStorage.setItem('token', newToken);
    localStorage.setItem('refreshToken', newRefreshToken);

    return newToken;
}

// Hàm gọi API dùng chung — tự gắn token, tự retry khi 401
export async function apiFetch(path: string, options: RequestInit = {}) {
    let token = localStorage.getItem('token');

    const response = await fetch(`${BASE_URL}${path}`, {
        ...options,
        headers: {
            ...(options.headers || {}),
            'Authorization': `Bearer ${token}`,
        },
    });

    // Nếu không phải lỗi 401 -> trả về luôn, không cần refresh
    if (response.status !== 401) {
        return response;
    }

    // Nếu là 401 -> thử refresh token
    const newToken = await refreshAccessToken();

    if (!newToken) {
        // Refresh thất bại -> xóa token, bắt đăng nhập lại
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
        throw new Error('Phiên đăng nhập đã hết hạn');
    }

    // Refresh thành công -> gọi lại request gốc với token mới
    const retryResponse = await fetch(`${BASE_URL}${path}`, {
        ...options,
        headers: {
            ...(options.headers || {}),
            'Authorization': `Bearer ${newToken}`,
        },
    });

    return retryResponse;
}