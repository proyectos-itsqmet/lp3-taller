import { AuthProvider, useAuth } from './auth/AuthContext'
import Spinner from './components/Spinner'
import Layout from './components/Layout'
import LoginPage from './pages/LoginPage'

// Decide qué mostrar según el estado de autenticación.
function Root() {
  const { user, loading } = useAuth()

  if (loading) {
    return (
      <div className="flex h-full items-center justify-center bg-slate-50">
        <Spinner />
      </div>
    )
  }

  if (!user) return <LoginPage />

  return <Layout />
}

export default function App() {
  return (
    <AuthProvider>
      <Root />
    </AuthProvider>
  )
}
