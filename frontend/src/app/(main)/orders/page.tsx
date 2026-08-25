import OrderList from '@/features/orders/components/OrderList';

export default function OrderPage() {
    return (
        <main>
            <h1 className="text-2xl font-bold text-purple-800 mb-6 text-center">ĐƠN HÀNG</h1>
            <OrderList />
        </main>
    );
}