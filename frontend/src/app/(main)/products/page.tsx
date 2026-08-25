import ProductList from '@/features/products/components/ProductList';

export default function ProductsPage() {
    return (
        <main>
            <h1 className="text-2xl font-bold text-purple-800 mb-6 text-center">SẢN PHẨM</h1>
            <ProductList />
        </main>
    );
}