import { useEffect, useState } from 'react'
import { api, descargarPdf } from '../api/client'
import type { ApiError } from '../api/client'
import { useAuth, puedeEscribir } from '../auth/AuthContext'
import type { Empleado, Cliente, Proyecto } from '../types'
import Spinner from '../components/Spinner'


interface Conteos {
  empleados: number
  clientes: number
  proyectos: number
}

export default function DashboardPage() {
  const { user } = useAuth()
  const [conteos, setConteos] = useState<Conteos | null>(null)
  const [cargando, setCargando] = useState<boolean>(true)
  const [error, setError] = useState<string>('')
  const [descargando, setDescargando] = useState<string>('')

  useEffect(() => {
    let activo = true
    async function cargar(): Promise<void> {
      setCargando(true)
      setError('')
      try {

        const [empleados, clientes, proyectos] = await Promise.all([
          api.get<Empleado[]>('/empleados'),
          api.get<Cliente[]>('/clientes'),
          api.get<Proyecto[]>('/proyectos'),
        ])
        if (!activo) return
        setConteos({
          empleados: empleados.length,
          clientes: clientes.length,
          proyectos: proyectos.length,
        })
      } catch (err) {
        if (!activo) return
        const apiErr = err as ApiError
        setError(apiErr?.message || 'No se pudo cargar el resumen')
      } finally {
        if (activo) setCargando(false)
      }
    }
    cargar()
    return () => {
      activo = false
    }
  }, [])

  async function descargar(path: string, filename: string): Promise<void> {
    setDescargando(path)
    setError('')
    try {
      await descargarPdf(path, filename)
    } catch (err) {
      const apiErr = err as ApiError
      setError(apiErr?.message || 'No se pudo descargar el reporte')
    } finally {
      setDescargando('')
    }
  }

  const tarjetas: { label: string; valor: number; color: string }[] = conteos
    ? [
        { label: 'Empleados', valor: conteos.empleados, color: 'text-indigo-600' },
        { label: 'Clientes', valor: conteos.clientes, color: 'text-emerald-600' },
        { label: 'Proyectos', valor: conteos.proyectos, color: 'text-amber-600' },
      ]
    : []

  return (
    <div className="space-y-8">
      <div>
        <h2 className="text-2xl font-bold text-slate-800">Panel</h2>
        <p className="text-sm text-slate-500">Resumen general de CodeHub</p>
      </div>

      {error && (
        <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-md px-3 py-2">
          {error}
        </p>
      )}

      {cargando ? (
        <Spinner />
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {tarjetas.map((t) => (
            <div
              key={t.label}
              className="bg-white rounded-lg border border-slate-200 shadow-sm p-6"
            >
              <p className="text-sm font-medium text-slate-500">{t.label}</p>
              <p className={`mt-2 text-4xl font-bold ${t.color}`}>{t.valor}</p>
            </div>
          ))}
        </div>
      )}

      {puedeEscribir(user?.rol) && (
        <div className="bg-white rounded-lg border border-slate-200 shadow-sm p-6">
          <h3 className="text-lg font-semibold text-slate-800">Reportes</h3>
          <p className="text-sm text-slate-500 mb-4">Descargá los reportes en formato PDF.</p>
          <div className="flex flex-wrap gap-3">
            <button
              type="button"
              disabled={descargando !== ''}
              onClick={() => descargar('/reportes/proyectos', 'reporte-proyectos.pdf')}
              className="bg-indigo-600 text-white hover:bg-indigo-700 rounded-md px-4 py-2 font-medium disabled:opacity-60 disabled:cursor-not-allowed"
            >
              {descargando === '/reportes/proyectos' ? 'Descargando…' : 'Reporte de proyectos'}
            </button>
            <button
              type="button"
              disabled={descargando !== ''}
              onClick={() => descargar('/reportes/empleados', 'reporte-empleados.pdf')}
              className="bg-indigo-600 text-white hover:bg-indigo-700 rounded-md px-4 py-2 font-medium disabled:opacity-60 disabled:cursor-not-allowed"
            >
              {descargando === '/reportes/empleados' ? 'Descargando…' : 'Reporte de empleados'}
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
