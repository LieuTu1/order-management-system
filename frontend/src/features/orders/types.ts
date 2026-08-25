export interface OrderDetail {
    productId: number;
    productName: string;
    quantity: number;
    unitPrice: number;
    subtotal: number;
}

export interface Order {
    id: number;
    orderCode: string;
    orderDate: string;
    paymentMethod: string;
    paymentStatus: string;
    customerId: number;
    customerName: string;
    userId: number;
    userName: string;
    totalAmount: number;
    status: string;
    orderDetails: OrderDetail[];
}

export type OrderStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'CANCELLED';
export type PaymentMethod = 'CASH' | 'BANK_TRANSFER' | 'MOMO';

export interface OrderDetailInput {
    productId: number;
    quantity: number;
}

export interface CreateOrderRequest {
    customerId: number;
    userId: number;
    paymentMethod: PaymentMethod;
    orderDetails: OrderDetailInput[];
}

// Các trạng thái tiếp theo hợp lệ từ trạng thái hiện tại
export const NEXT_STATUS_MAP: Record<string, OrderStatus[]> = {
    PENDING: ['PROCESSING', 'CANCELLED'],
    PROCESSING: ['COMPLETED', 'CANCELLED'],
    COMPLETED: [],
    CANCELLED: [],
};
