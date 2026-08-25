import CustomerList from '@/features/customers/components/CustomerList';

export default function CustomersPage() {
    return (
        <main>
            <h1 className="text-2xl font-bold text-purple-800 mb-6 text-center">KHÁCH HÀNG</h1>
            <CustomerList />
        </main>
    );
}
