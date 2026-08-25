import CategoryList from '@/features/categories/components/CategoryList';

export default function CategoriesPage() {
    return (
        <main>
            <h1 className="text-2xl font-bold text-purple-800 mb-6 text-center">DANH MỤC</h1>
            <CategoryList />
        </main>
    );
}