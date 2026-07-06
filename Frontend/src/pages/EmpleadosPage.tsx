import { useEffect, useState } from 'react'
import type { FormEvent, ReactNode } from 'react'
import { api } from '../api/client'
import type { ApiError } from '../api/client'
import { useAuth, puedeEscribir } from '../auth/AuthContext'
import type { Empleado, EmpleadoInput } from '../types'
import DataTable from '../components/DataTable'
import Modal from '../components/Modal'
import FormField from '../components/FormField'


function formVacio(): EmpleadoInput {
  return {
    nombre: '',
    apellido: '',
    email: '',
    documento: '',
    fechaContratacion: '',
    perfil: {
      cargo: '',
      especialidad: '',
      telefono: '',
      bio: '',
      salario: undefined,
    },
  }
}


function formDesde(e: Empleado): EmpleadoInput {
  return {
    nombre: e.nombre,
    apellido: e.apellido,
    email: e.email,
    documento: e.documento,
    fechaContratacion: e.fechaContratacion,
    perfil: {
      cargo: e.perfil?.cargo ?? '',
      especialidad: e.perfil?.especialidad ?? '',
      telefono: e.perfil?.telefono ?? '',
      bio: e.perfil?.bio ?? '',
      salario: e.perfil?.salario ?? undefined,
    },
  }
}

export default function EmpleadosPage() {
  const { user } = useAuth()
  const puedeEscribirRol = puedeEscribir(user?.rol)

  const [lista, setLista] = useState<Empleado[]>([])
  const [cargando, setCargando] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  const [modalAbierto, setModalAbierto] = useState<boolean>(false)
  const [itemEnEdicion, setItemEnEdicion] = useState<Empleado | null>(null)
  const [form, setForm] = useState<EmpleadoInput>(formVacio())
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})
  const [errorForm, setErrorForm] = useState<string>('')
  const [guardando, setGuardando] = useState<boolean>(false)


  async function cargarLista() {
    setCargando(true)
    setError(null)
    try {
      const data = await api.get<Empleado[]>('/empleados')
      setLista(data)
    } catch (err) {
      const e = err as ApiError
      setError(e.message ?? 'No se pudo cargar la lista de empleados.')
    } finally {
      setCargando(false)
    }
  }

  useEffect(() => {
    cargarLista()
  }, [])

  function abrirCrear() {
    setItemEnEdicion(null)
    setForm(formVacio())
    setFieldErrors({})
    setErrorForm('')
    setModalAbierto(true)
  }

  function abrirEditar(empleado: Empleado) {
    setItemEnEdicion(empleado)
    setForm(formDesde(empleado))
    setFieldErrors({})
    setErrorForm('')
    setModalAbierto(true)
  }

  function cerrarModal() {
    setModalAbierto(false)
    setFieldErrors({})
    setErrorForm('')
  }


  function cambiarCampo(campo: keyof EmpleadoInput, valor: string) {
    setForm((prev) => ({ ...prev, [campo]: valor }))
  }


  function cambiarPerfil(campo: keyof EmpleadoInput['perfil'], valor: string) {
    setForm((prev) => ({
      ...prev,
      perfil: {
        ...prev.perfil,
        [campo]: campo === 'salario' ? (valor === '' ? undefined : Number(valor)) : valor,
      },
    }))
  }

  async function guardar(evento: FormEvent) {
    evento.preventDefault()
    setGuardando(true)
    setFieldErrors({})
    setErrorForm('')
    try {
      if (itemEnEdicion) {
        await api.put<Empleado>(`/empleados/${itemEnEdicion.id}`, form)
      } else {
        await api.post<Empleado>('/empleados', form)
      }
      cerrarModal()
      await cargarLista()
    } catch (err) {
      const e = err as ApiError
      if (e.status === 400 && e.fieldErrors) {

        setFieldErrors(e.fieldErrors)
      } else {

        setErrorForm(e.message ?? 'No se pudo guardar el empleado.')
      }
    } finally {
      setGuardando(false)
    }
  }

  async function eliminar(empleado: Empleado) {
    const ok = window.confirm(
      `¿Seguro que querés eliminar a ${empleado.nombre} ${empleado.apellido}?`,
    )
    if (!ok) return
    try {
      await api.del(`/empleados/${empleado.id}`)
      await cargarLista()
    } catch (err) {
      const e = err as ApiError
      if (e.status === 409) {

        window.alert(e.message ?? 'No se puede eliminar: el empleado está en uso.')
      } else {
        setError(e.message ?? 'No se pudo eliminar el empleado.')
      }
    }
  }


  function errorDe(clave: string): string | undefined {
    return fieldErrors[clave]
  }

  const columns = [
    {
      key: 'nombreCompleto',
      label: 'Nombre completo',
      render: (row: Empleado) => `${row.nombre} ${row.apellido}`,
    },
    { key: 'email', label: 'Email' },
    { key: 'documento', label: 'Documento' },
    {
      key: 'cargo',
      label: 'Cargo',
      render: (row: Empleado) => row.perfil?.cargo ?? '—',
    },
    {
      key: 'proyectos',
      label: '# Proyectos',
      render: (row: Empleado) => row.proyectos.length,
    },
  ]

  function acciones(row: Empleado): ReactNode {
    if (!puedeEscribirRol) return null
    return (
      <div className="flex gap-2">
        <button
          type="button"
          onClick={() => abrirEditar(row)}
          className="rounded-md px-3 py-1 text-sm text-indigo-600 hover:bg-indigo-50"
        >
          Editar
        </button>
        <button
          type="button"
          onClick={() => eliminar(row)}
          className="rounded-md px-3 py-1 text-sm text-red-600 hover:bg-red-50"
        >
          Eliminar
        </button>
      </div>
    )
  }

  return (
    <div className="p-6">
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-semibold text-slate-800">Empleados</h1>
        {puedeEscribirRol && (
          <button
            type="button"
            onClick={abrirCrear}
            className="rounded-md bg-indigo-600 px-4 py-2 text-white hover:bg-indigo-700"
          >
            Nuevo empleado
          </button>
        )}
      </div>

      {error && (
        <div className="mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      {cargando ? (
        <p className="text-slate-500">Cargando empleados…</p>
      ) : (
        <DataTable
          columns={columns}
          rows={lista}
          actions={puedeEscribirRol ? acciones : undefined}
          empty="No hay empleados registrados."
        />
      )}

      <Modal
        open={modalAbierto}
        title={itemEnEdicion ? 'Editar empleado' : 'Nuevo empleado'}
        onClose={cerrarModal}
        footer={
          <div className="flex justify-end gap-2">
            <button
              type="button"
              onClick={cerrarModal}
              className="rounded-md border border-slate-300 px-4 py-2 text-slate-700 hover:bg-slate-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              form="form-empleado"
              disabled={guardando}
              className="rounded-md bg-indigo-600 px-4 py-2 text-white hover:bg-indigo-700 disabled:opacity-60"
            >
              {guardando ? 'Guardando…' : 'Guardar'}
            </button>
          </div>
        }
      >
        <form id="form-empleado" onSubmit={guardar} className="space-y-4">
          {errorForm && (
            <div className="rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {errorForm}
            </div>
          )}

          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <FormField label="Nombre" error={errorDe('nombre')}>
              <input
                type="text"
                value={form.nombre}
                onChange={(e) => cambiarCampo('nombre', e.target.value)}
                className="w-full rounded-md border border-slate-300 px-3 py-2"
              />
            </FormField>

            <FormField label="Apellido" error={errorDe('apellido')}>
              <input
                type="text"
                value={form.apellido}
                onChange={(e) => cambiarCampo('apellido', e.target.value)}
                className="w-full rounded-md border border-slate-300 px-3 py-2"
              />
            </FormField>

            <FormField label="Email" error={errorDe('email')}>
              <input
                type="email"
                value={form.email}
                onChange={(e) => cambiarCampo('email', e.target.value)}
                className="w-full rounded-md border border-slate-300 px-3 py-2"
              />
            </FormField>

            <FormField label="Documento" error={errorDe('documento')}>
              <input
                type="text"
                value={form.documento}
                onChange={(e) => cambiarCampo('documento', e.target.value)}
                className="w-full rounded-md border border-slate-300 px-3 py-2"
              />
            </FormField>

            <FormField label="Fecha de contratación" error={errorDe('fechaContratacion')}>
              <input
                type="date"
                value={form.fechaContratacion}
                onChange={(e) => cambiarCampo('fechaContratacion', e.target.value)}
                className="w-full rounded-md border border-slate-300 px-3 py-2"
              />
            </FormField>
          </div>

          <div className="border-t border-slate-200 pt-4">
            <h2 className="mb-3 text-sm font-semibold uppercase tracking-wide text-slate-500">
              Perfil
            </h2>
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <FormField label="Cargo" error={errorDe('perfil.cargo')}>
                <input
                  type="text"
                  value={form.perfil.cargo}
                  onChange={(e) => cambiarPerfil('cargo', e.target.value)}
                  className="w-full rounded-md border border-slate-300 px-3 py-2"
                />
              </FormField>

              <FormField label="Especialidad" error={errorDe('perfil.especialidad')}>
                <input
                  type="text"
                  value={form.perfil.especialidad ?? ''}
                  onChange={(e) => cambiarPerfil('especialidad', e.target.value)}
                  className="w-full rounded-md border border-slate-300 px-3 py-2"
                />
              </FormField>

              <FormField label="Teléfono" error={errorDe('perfil.telefono')}>
                <input
                  type="text"
                  value={form.perfil.telefono ?? ''}
                  onChange={(e) => cambiarPerfil('telefono', e.target.value)}
                  className="w-full rounded-md border border-slate-300 px-3 py-2"
                />
              </FormField>

              <FormField label="Salario" error={errorDe('perfil.salario')}>
                <input
                  type="number"
                  step="0.01"
                  value={form.perfil.salario ?? ''}
                  onChange={(e) => cambiarPerfil('salario', e.target.value)}
                  className="w-full rounded-md border border-slate-300 px-3 py-2"
                />
              </FormField>

              <div className="sm:col-span-2">
                <FormField label="Biografía" error={errorDe('perfil.bio')}>
                  <textarea
                    rows={3}
                    value={form.perfil.bio ?? ''}
                    onChange={(e) => cambiarPerfil('bio', e.target.value)}
                    className="w-full rounded-md border border-slate-300 px-3 py-2"
                  />
                </FormField>
              </div>
            </div>
          </div>
        </form>
      </Modal>
    </div>
  )
}
