import { useState } from 'react'
import type { FormEvent } from 'react'
import { useAuth } from '../auth/AuthContext'
import type { ApiError } from '../api/client'

// Página de inicio de sesión: card centrada con usuario y contraseña.
export default function LoginPage() {
  const { login } = useAuth()
  const [username, setUsername] = useState<string>('')
  const [password, setPassword] = useState<string>('')
  const [error, setError] = useState<string>('')
  const [cargando, setCargando] = useState<boolean>(false)

  async function handleSubmit(e: FormEvent<HTMLFormElement>): Promise<void> {
    e.preventDefault()
    setError('')
    setCargando(true)
    try {
      await login(username, password)
    } catch (err) {
      const apiErr = err as ApiError
      setError(apiErr?.message || 'Credenciales inválidas')
    } finally {
      setCargando(false)
    }
  }

  return (
    <div className="min-h-full flex items-center justify-center bg-slate-50 px-4 py-12">
      <div className="w-full max-w-sm bg-white rounded-lg shadow-md border border-slate-200 p-8">
        <div className="mb-6 text-center">
          <h1 className="text-2xl font-bold text-indigo-600">CodeHub</h1>
          <p className="mt-1 text-sm text-slate-500">Iniciá sesión para continuar</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="username" className="block text-sm font-medium text-slate-700 mb-1">
              Usuario
            </label>
            <input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoComplete="username"
              required
              className="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>

          <div>
            <label htmlFor="password" className="block text-sm font-medium text-slate-700 mb-1">
              Contraseña
            </label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
              required
              className="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>

          {error && (
            <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-md px-3 py-2">
              {error}
            </p>
          )}

          <button
            type="submit"
            disabled={cargando}
            className="w-full bg-indigo-600 text-white hover:bg-indigo-700 rounded-md px-4 py-2 font-medium disabled:opacity-60 disabled:cursor-not-allowed"
          >
            {cargando ? 'Ingresando…' : 'Ingresar'}
          </button>
        </form>

        <div className="mt-6 rounded-md bg-slate-50 border border-slate-200 p-3 text-xs text-slate-500">
          <p className="font-medium text-slate-600 mb-1">Usuarios de prueba</p>
          <ul className="space-y-0.5">
            <li>admin / admin123</li>
            <li>gerente / gerente123</li>
            <li>empleado / empleado123</li>
          </ul>
        </div>
      </div>
    </div>
  )
}
