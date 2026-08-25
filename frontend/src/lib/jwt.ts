interface JwtPayload {
    sub?: string;
    role?: string;
    roles?: string[];
    exp?: number;
    [key: string]: unknown;
}

export function decodeToken(token: string): JwtPayload | null {
    try {
        const payload = token.split(".")[1];
        const decoded = atob(payload.replace(/-/g, "+").replace(/_/g, "/"));
        return JSON.parse(decoded);
    } catch {
        return null;
    }
}

export function getCurrentRole(): string | null {
    if (typeof window === "undefined") return null;
    const token = localStorage.getItem("token");
    if (!token) return null;

    const payload = decodeToken(token);
    if (!payload) return null;

    // Tùy backend trả claim tên gì, kiểm tra cả 2 khả năng phổ biến
    if (payload.role) return payload.role;
    if (payload.roles && payload.roles.length > 0) return payload.roles[0];

    return null;
}

export function isAdmin(): boolean {
    return getCurrentRole() === "ROLE_ADMIN";
}

export function getCurrentUsername(): string | null {
    if (typeof window === "undefined") return null;
    const token = localStorage.getItem("token");
    if (!token) return null;

    const payload = decodeToken(token);
    return (payload?.sub as string) ?? null;
}