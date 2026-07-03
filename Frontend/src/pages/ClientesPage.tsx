import { useEffect, useState } from 'react'
import type { ReactNode } from 'react'
import { api } from '../api/client'
import type { ApiError } from '../api/client'
import { useAuth, puedeEscribir } from '../auth/AuthContext'
import type { Cliente, ClienteInput } from '../types'
import DataTable from '../components/DataTable'
import Modal from '../components/Modal'
import FormField from '../components/FormField'

// Estado inicial del formulario (todos los campos como texto para los inputs)
const FORM_VACIO: ClienteInput = {
  nombre: '',
  ruc: '',
  email: '',
  telefono: '',
  direccion: '',
}

export default function ClientesPage() {
  const { user } = useAuth()
  const escribir: boolean = puedeEscribir(user?.rol)

  const [clientes, setClientes] = useState<Cliente[]>([])
  const [cargando, setCargando] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  const [modalAbierto, setModalAbierto] = useState<boolean>(false)
  const [itemEnEdicion, setItemEnEdicion] = useState<Cliente | null>(null)
  const [form, setForm] = useState<ClienteInput>(FORM_VACIO)
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})
  const [guardando, setGuardando] = useState<boolean>(false)

  // Cargar la lista al montar
  useEffect(() => {
    cargarClientes()
  }, [])

  async function cargarClientes() {
    setCargando(true)
    setError(null)
    try {
      const data = await api.get<Cliente[]>('/clientes')
      setClientes(data)
    } catch (err) {
      const e = err as ApiError
      setError(e.message ?? 'No se pudieron cargar los clientes.')
    } finally {
      setCargando(false)
    }
  }

  function abrirCrear() {
    setItemEnEdicion(null)
    setForm(FORM_VACIO)
    setFieldErrors({})
    setModalAbierto(true)
  }

  function abrirEditar(cliente: Cliente) {
    setItemEnEdicion(cliente)
    setForm({
      nombre: cliente.nombre,
      ruc: cliente.ruc,
      email: cliente.email,
      telefono: cliente.telefono ?? '',
      direccion: cliente.direccion ?? '',
    })
    setFieldErrors({})
    setModalAbierto(true)
  }

  function cerrarModal() {
    setModalAbierto(false)
  }

  // Actualiza un campo del formulario
  function cambiarCampo(campo: keyof ClienteInput, valor: string) {
    setForm((anterior) => ({ ...anterior, [campo]: valor }))
  }

  async function guardar() {
    setGuardando(true)
    setFieldErrors({})
    try {
      if (itemEnEdicion) {
        await api.put<Cliente>(`/clientes/${itemEnEdicion.id}`, form)
      } else {
        await api.post<Cliente>('/clientes', form)
      }
      setModalAbierto(false)
      await cargarClientes()
    } catch (err) {
      const e = err as ApiError
      if (e.status === 400 && e.fieldErrors) {
        // Errores de validación por campo
        setFieldErrors(e.fieldErrors)
      } else {
        // Otros errores (409 duplicado, etc.) los mostramos como alerta
        window.alert(e.message ?? 'No se pudo guardar el cliente.')
      }
    } finally {
      setGuardando(false)
    }
  }

  async function eliminar(cliente: Cliente) {
    const confirmar = window.confirm(`¿Eliminar al cliente "${cliente.nombre}"?`)
    if (!confirmar) return
    try {
      await api.del(`/clientes/${cliente.id}`)
      await cargarClientes()
    } catch (err) {
      const e = err as ApiError
      if (e.status === 409) {
        // El cliente tiene proyectos asociados
        window.alert(e.message ?? 'No se puede eliminar: el cliente tiene proyectos asociados.')
      } else {
        window.alert(e.message ?? 'No se pudo eliminar el cliente.')
      }
    }
  }

  // Columnas de la tabla
  const columnas = [
    { key: 'nombre', label: 'Nombre' },
    { key: 'ruc', label: 'RUC' },
    { key: 'email', label: 'Email' },
    {
      key: 'proyectos',
      label: '#Proyectos',
      render: (row: Cliente): ReactNode => row.proyectos?.length ?? 0,
    },
  ]

  // Acciones por fila (solo si puede escribir)
  function acciones(row: Cliente): ReactNode {
    return (
      <div className="flex gap-2">
        <button
          onClick={() => abrirEditar(row)}
          className="text-indigo-600 hover:text-indigo-800 text-sm font-medium"
        >
          Editar
        </button>
        <button
          onClick={() => eliminar(row)}
          className="text-red-600 hover:text-red-800 text-sm font-medium"
        >
          Eliminar
        </button>
      </div>
    )
  }

  return (
    <div>
      {/* Encabezado */}
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-slate-800">Clientes</h1>
        {escribir && (
          <button
            onClick={abrirCrear}
            className="bg-indigo-600 text-white hover:bg-indigo-700 rounded-md px-4 py-2"
          >
            Nuevo cliente
          </button>
        )}
      </div>

      {/* Banner de error */}
      {error && (
        <div className="mb-4 rounded-md border border-red-300 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      {/* Tabla */}
      {cargando ? (
        <p className="text-slate-500">Cargando clientes…</p>
      ) : (
        <DataTable
          columns={columnas}
          rows={clientes}
          actions={escribir ? acciones : undefined}
          empty="No hay clientes registrados."
        />
      )}

      {/* Modal de crear/editar */}
      <Modal
        open={modalAbierto}
        title={itemEnEdicion ? 'Editar cliente' : 'Nuevo cliente'}
        onClose={cerrarModal}
        footer={
          <div className="flex justify-end gap-2">
            <button
              onClick={cerrarModal}
              className="rounded-md border border-slate-300 px-4 py-2 text-slate-700 hover:bg-slate-100"
            >
              Cancelar
            </button>
            <button
              onClick={guardar}
              disabled={guardando}
              className="bg-indigo-600 text-white hover:bg-indigo-700 rounded-md px-4 py-2 disabled:opacity-50"
            >
              {guardando ? 'Guardando…' : 'Guardar'}
            </button>
          </div>
        }
      >
        <div className="space-y-4">
          <FormField label="Nombre" error={fieldErrors.nombre}>
            <input
              type="text"
              value={form.nombre}
              onChange={(e) => cambiarCampo('nombre', e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2"
            />
          </FormField>

          <FormField label="RUC" error={fieldErrors.ruc}>
            <input
              type="text"
              value={form.ruc}
              onChange={(e) => cambiarCampo('ruc', e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2"
            />
          </FormField>

          <FormField label="Email" error={fieldErrors.email}>
            <input
              type="email"
              value={form.email}
              onChange={(e) => cambiarCampo('email', e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2"
            />
          </FormField>

          <FormField label="Teléfono" error={fieldErrors.telefono}>
            <input
              type="text"
              value={form.telefono ?? ''}
              onChange={(e) => cambiarCampo('telefono', e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2"
            />
          </FormField>

          <FormField label="Dirección" error={fieldErrors.direccion}>
            <input
              type="text"
              value={form.direccion ?? ''}
              onChange={(e) => cambiarCampo('direccion', e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2"
            />
          </FormField>
        </div>
      </Modal>
    </div>
  )
}
