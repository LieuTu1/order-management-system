// Dữ liệu NHẬN VỀ khi gọi API
export interface User {
    id: number;
    username: string;
    fullName: string;
    phone: string;
    email: string;
    address: string;
    status: string;
    roleId: number;
    roleName: string;
}

// Dữ liệu GỬI LÊN khi tạo mới HOẶC sửa user
export interface UserRequest {
    username: string;
    password: string;
    fullName: string;
    phone: string;
    email: string;
    address: string;
    roleId: number;
}

// Dữ liệu 1 lựa chọn Role trong dropdown chọn Role
export interface RoleOption {
    id: number;
    role: string;
}