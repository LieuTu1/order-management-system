export default function AuthLayout({ children }: { children: React.ReactNode }) {
    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-purple-100 via-violet-200 to-fuchsia-100 px-4">
            <div className="w-full max-w-md">
                {children}
            </div>
        </div>
    );
}