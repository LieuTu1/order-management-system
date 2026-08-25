export interface Product {
    id: number;
    name: string;
    price: number;
    sku: string;
    stock: number;
    status: string;
    imageUrl: string;
    categoryId: number;
    categoryName: string;
    supplierId: number;
    supplierName: string;
}

export interface CategoryOption {
    id: number;
    name: string;
    description: string;
    status: string;
}

export interface SupplierOption {
    id: number;
    name: string;
    phone: string;
    email: string;
    address: string;
    status: string;
}

export interface ProductRequest {
    sku: string;
    name: string;
    price: number;
    stock: number;
    imageUrl: string;
    categoryId: number;
    supplierId: number;
}