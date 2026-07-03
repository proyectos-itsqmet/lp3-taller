import { useEffect, useState } from 'react'
import type { Usuario, UsuarioInput, Rol } from '../types'
import { api } from '../api/client'
import type { ApiError } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import DataTable from '../components/DataTable'
import Modal from '../components/Modal'
import FormField from '../components/FormField'
import Badge from '../components/Badge'
import Spinner from '../components/Spinner'

// Form vacío para crear un usuario nuevo
const FORM_VACIO: UsuarioInput = {
  username: '',
  password: '',
  nombre: '',
  email: '',
  rol: 'EMPLEADO',
  enabled: true,
}

// Color del Badge según el rol
function colorRol(rol: Rol): string {
  if (rol === 'ADMIN') return 'indigo'
  if (rol === 'GERENTE') return 'amber'
  return 'slate'
}

export default function UsuariosPage() {
  const { user } = useAuth()

  const [lista, setLista] = useState<Usuario[]>([])
  const [cargando, setCargando] = useState<boolean>(true)
  const [error, setError] = useState<string>('')

  const [modalAbierto, setModalAbierto] = useState<boolean>(false)
  const [itemEnEdicion, setItemEnEdicion] = useState<Usuario | null>(null)
  const [form, setForm] = useState<UsuarioInput>(FORM_VACIO)
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})
  const [errorForm, setErrorForm] = useState<string>('')
  const [guardando, setGuardando] = useState<boolean>(false)

  // Cargar la lista al montar (solo si es ADMIN)
  useEffect(() => {
    if (user?.rol !== 'ADMIN') return
    cargar()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  async function cargar() {
    setCargando(true)
    setError('')
    try {
      const datos = await api.get<Usuario[]>('/usuarios')
      setLista(datos)
    } catch (e) {
      const err = e as ApiError
      setError(err.message || 'No se pudo cargar la lista de usuarios.')
    } finally {
      setCargando(false)
    }
  }

  function abrirCrear() {
    setItemEnEdicion(null)
    setForm(FORM_VACIO)
    setFieldErrors({})
    setErrorForm('')
    setModalAbierto(true)
  }

  function abrirEditar(u: Usuario) {
    setItemEnEdicion(u)
    // En edición no se pide la contraseña; se deja vacía (opcional en el back)
    setForm({
      username: u.username,
      password: '',
      nombre: u.nombre,
      email: u.email,
      rol: u.rol,
      enabled: u.enabled,
    })
    setFieldErrors({})
    setErrorForm('')
    setModalAbierto(true)
  }

  function cerrarModal() {
    setModalAbierto(false)
  }

  async function guardar(e: React.FormEvent) {
    e.preventDefault()
    setGuardando(true)
    setFieldErrors({})
    setErrorForm('')
    try {
      if (itemEnEdicion) {
        await api.put<Usuario>(`/usuarios/${itemEnEdicion.id}`, form)
      } else {
        await api.post<Usuario>('/usuarios', form)
      }
      cerrarModal()
      await cargar()
    } catch (e) {
      const err = e as ApiError
      if (err.status === 400 && err.fieldErrors) {
        setFieldErrors(err.fieldErrors)
      } else if (err.status === 409) {
        // Conflicto: username o email duplicado
        setErrorForm(err.message || 'El usuario o el email ya existe.')
      } else {
        setErrorForm(err.message || 'No se pudo guardar el usuario.')
      }
    } finally {
      setGuardando(false)
    }
  }

  async function eliminar(u: Usuario) {
    if (!window.confirm(`¿Eliminar al usuario "${u.username}"?`)) return
    try {
      await api.del(`/usuarios/${u.id}`)
      await cargar()
    } catch (e) {
      const err = e as ApiError
      // 409 = usuario en uso u otra restricción
      setError(err.message || 'No se pudo eliminar el usuario.')
    }
  }

  // Guard de rol: solo ADMIN ve el contenido
  if (user?.rol !== 'ADMIN') {
    return (
      <div className="rounded-md bg-amber-50 border border-amber-200 text-amber-800 px-4 py-3">
        No tenés permisos para gestionar usuarios.
      </div>
    )
  }

  const columnas = [
    { key: 'username', label: 'Usuario' },
    { key: 'nombre', label: 'Nombre' },
    { key: 'email', label: 'Email' },
    {
      key: 'rol',
      label: 'Rol',
      render: (row: Usuario) => <Badge color={colorRol(row.rol)}>{row.rol}</Badge>,
    },
    {
      key: 'enabled',
      label: 'Activo',
      render: (row: Usuario) =>
        row.enabled ? (
          <Badge color="green">Sí</Badge>
        ) : (
          <Badge color="red">No</Badge>
        ),
    },
  ]

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-semibold text-slate-800">Usuarios</h1>
        <button
          type="button"
          onClick={abrirCrear}
          className="bg-indigo-600 text-white hover:bg-indigo-700 rounded-md px-4 py-2"
        >
          Nuevo usuario
        </button>
      </div>

      {error && (
        <div className="mb-4 rounded-md bg-red-50 border border-red-200 text-red-700 px-4 py-3">
          {error}
        </div>
      )}

      {cargando ? (
        <Spinner />
      ) : (
        <DataTable
          columns={columnas}
          rows={lista}
          empty="No hay usuarios registrados."
          actions={(row: Usuario) => (
            <div className="flex gap-2">
              <button
                type="button"
                onClick={() => abrirEditar(row)}
                className="text-indigo-600 hover:text-indigo-800"
              >
                Editar
              </button>
              <button
                type="button"
                onClick={() => eliminar(row)}
                className="text-red-600 hover:text-red-800"
              >
                Eliminar
              </button>
            </div>
          )}
        />
      )}

      <Modal
        open={modalAbierto}
        title={itemEnEdicion ? 'Editar usuario' : 'Nuevo usuario'}
        onClose={cerrarModal}
        footer={
          <div className="flex justify-end gap-2">
            <button
              type="button"
              onClick={cerrarModal}
              className="border border-slate-300 rounded-md px-4 py-2 text-slate-700 hover:bg-slate-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              form="form-usuario"
              disabled={guardando}
              className="bg-indigo-600 text-white hover:bg-indigo-700 rounded-md px-4 py-2 disabled:opacity-50"
            >
              {guardando ? 'Guardando…' : 'Guardar'}
            </button>
          </div>
        }
      >
        <form id="form-usuario" onSubmit={guardar} className="space-y-4">
          {errorForm && (
            <div className="rounded-md bg-red-50 border border-red-200 text-red-700 px-4 py-3">
              {errorForm}
            </div>
          )}

          <FormField label="Usuario" error={fieldErrors.username}>
            <input
              type="text"
              value={form.username}
              onChange={(e) => setForm({ ...form, username: e.target.value })}
              className="w-full border border-slate-300 rounded-md px-3 py-2"
            />
          </FormField>

          <FormField label="Contraseña" error={fieldErrors.password}>
            <input
              type="password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              placeholder={itemEnEdicion ? 'Dejar en blanco para no cambiarla' : ''}
              className="w-full border border-slate-300 rounded-md px-3 py-2"
            />
          </FormField>

          <FormField label="Nombre" error={fieldErrors.nombre}>
            <input
              type="text"
              value={form.nombre}
              onChange={(e) => setForm({ ...form, nombre: e.target.value })}
              className="w-full border border-slate-300 rounded-md px-3 py-2"
            />
          </FormField>

          <FormField label="Email" error={fieldErrors.email}>
            <input
              type="email"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              className="w-full border border-slate-300 rounded-md px-3 py-2"
            />
          </FormField>

          <FormField label="Rol" error={fieldErrors.rol}>
            <select
              value={form.rol}
              onChange={(e) => setForm({ ...form, rol: e.target.value as Rol })}
              className="w-full border border-slate-300 rounded-md px-3 py-2"
            >
              <option value="ADMIN">ADMIN</option>
              <option value="GERENTE">GERENTE</option>
              <option value="EMPLEADO">EMPLEADO</option>
            </select>
          </FormField>

          <FormField label="Estado" error={fieldErrors.enabled}>
            <label className="flex items-center gap-2 text-slate-700">
              <input
                type="checkbox"
                checked={form.enabled}
                onChange={(e) => setForm({ ...form, enabled: e.target.checked })}
                className="h-4 w-4"
              />
              Usuario activo
            </label>
          </FormField>
        </form>
      </Modal>
    </div>
  )
}
