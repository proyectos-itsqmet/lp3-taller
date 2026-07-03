import { useState } from 'react'
import { useAuth } from '../auth/AuthContext'
import Badge from './Badge'
import DashboardPage from '../pages/DashboardPage'
import EmpleadosPage from '../pages/EmpleadosPage'
import ClientesPage from '../pages/ClientesPage'
import ProyectosPage from '../pages/ProyectosPage'
import UsuariosPage from '../pages/UsuariosPage'

type Vista = 'dashboard' | 'empleados' | 'clientes' | 'proyectos' | 'usuarios'

interface ItemNav {
  clave: Vista
  etiqueta: string
  soloAdmin?: boolean
}

const ITEMS: ItemNav[] = [
  { clave: 'dashboard', etiqueta: 'Panel' },
  { clave: 'empleados', etiqueta: 'Empleados' },
  { clave: 'clientes', etiqueta: 'Clientes' },
  { clave: 'proyectos', etiqueta: 'Proyectos' },
  { clave: 'usuarios', etiqueta: 'Usuarios', soloAdmin: true },
]

// Color del badge según el rol del usuario.
const COLOR_ROL: Record<string, string> = {
  ADMIN: 'indigo',
  GERENTE: 'blue',
  EMPLEADO: 'slate',
}

export default function Layout() {
  const { user, logout } = useAuth()
  const [vista, setVista] = useState<Vista>('dashboard')

  const esAdmin = user?.rol === 'ADMIN'
  const items = ITEMS.filter((item) => !item.soloAdmin || esAdmin)

  function renderVista() {
    switch (vista) {
      case 'dashboard':
        return <DashboardPage />
      case 'empleados':
        return <EmpleadosPage />
      case 'clientes':
        return <ClientesPage />
      case 'proyectos':
        return <ProyectosPage />
      case 'usuarios':
        return <UsuariosPage />
      default:
        return <DashboardPage />
    }
  }

  return (
    <div className="flex h-full">
      {/* Sidebar oscuro */}
      <aside className="flex w-56 flex-col bg-slate-900 text-slate-200">
        <div className="px-5 py-4 text-xl font-bold text-white">CodeHub</div>
        <nav className="flex-1 px-3">
          {items.map((item) => {
            const activo = vista === item.clave
            return (
              <button
                key={item.clave}
                type="button"
                onClick={() => setVista(item.clave)}
                className={`mb-1 w-full rounded-md px-3 py-2 text-left text-sm font-medium transition-colors ${
                  activo
                    ? 'bg-indigo-600 text-white'
                    : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                }`}
              >
                {item.etiqueta}
              </button>
            )
          })}
        </nav>
      </aside>

      {/* Zona principal */}
      <div className="flex flex-1 flex-col overflow-hidden">
        {/* Header superior */}
        <header className="flex items-center justify-end gap-3 border-b border-slate-200 bg-white px-6 py-3">
          <span className="text-sm font-medium text-slate-700">{user?.nombre}</span>
          {user && <Badge color={COLOR_ROL[user.rol]}>{user.rol}</Badge>}
          <button
            type="button"
            onClick={() => logout()}
            className="rounded-md border border-slate-300 px-3 py-1.5 text-sm text-slate-600 hover:bg-slate-100"
          >
            Cerrar sesión
          </button>
        </header>

        {/* Área de contenido */}
        <main className="flex-1 overflow-y-auto bg-slate-50 p-6">{renderVista()}</main>
      </div>
    </div>
  )
}
