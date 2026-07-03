import { useEffect, useState } from 'react'
import type { ReactNode } from 'react'
import { api } from '../api/client'
import type { ApiError } from '../api/client'
import { useAuth, puedeEscribir } from '../auth/AuthContext'
import type { Proyecto, ProyectoInput, Cliente, Empleado } from '../types'
import DataTable from '../components/DataTable'
import Modal from '../components/Modal'
import FormField from '../components/FormField'
import Badge from '../components/Badge'

// Estados posibles de un proyecto (calcan el enum del backend)
const ESTADOS = ['PLANIFICADO', 'EN_PROGRESO', 'FINALIZADO', 'CANCELADO']

// Color del Badge según el estado del proyecto
function colorEstado(estado: string): string {
  if (estado === 'EN_PROGRESO') return 'indigo'
  if (estado === 'FINALIZADO') return 'green'
  if (estado === 'CANCELADO') return 'red'
  return 'slate' // PLANIFICADO y cualquier otro
}

// Formulario en memoria: todo como texto (lo que dan los inputs) salvo los ids de empleados
interface ProyectoForm {
  nombre: string
  descripcion: string
  fechaInicio: string
  fechaFin: string
  estado: string
  presupuesto: string
  clienteId: string
  empleadoIds: number[]
}

const FORM_VACIO: ProyectoForm = {
  nombre: '',
  descripcion: '',
  fechaInicio: '',
  fechaFin: '',
  estado: 'PLANIFICADO',
  presupuesto: '',
  clienteId: '',
  empleadoIds: [],
}

function ProyectosPage() {
  const { user } = useAuth()
  const puedeEditar = puedeEscribir(user?.rol)

  const [lista, setLista] = useState<Proyecto[]>([])
  const [clientes, setClientes] = useState<Cliente[]>([])
  const [empleados, setEmpleados] = useState<Empleado[]>([])
  const [cargando, setCargando] = useState<boolean>(true)
  const [error, setError] = useState<string>('')

  const [modalAbierto, setModalAbierto] = useState<boolean>(false)
  const [itemEnEdicion, setItemEnEdicion] = useState<Proyecto | null>(null)
  const [form, setForm] = useState<ProyectoForm>(FORM_VACIO)
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})
  const [errorForm, setErrorForm] = useState<string>('')
  const [guardando, setGuardando] = useState<boolean>(false)

  // Cargar proyectos, clientes y empleados al montar
  useEffect(() => {
    cargarTodo()
  }, [])

  async function cargarTodo(): Promise<void> {
    setCargando(true)
    setError('')
    try {
      const [proyectos, cli, emp] = await Promise.all([
        api.get<Proyecto[]>('/proyectos'),
        api.get<Cliente[]>('/clientes'),
        api.get<Empleado[]>('/empleados'),
      ])
      setLista(proyectos)
      setClientes(cli)
      setEmpleados(emp)
    } catch (e) {
      const err = e as ApiError
      setError(err.message || 'No se pudieron cargar los proyectos.')
    } finally {
      setCargando(false)
    }
  }

  function abrirCrear(): void {
    setItemEnEdicion(null)
    setForm(FORM_VACIO)
    setFieldErrors({})
    setErrorForm('')
    setModalAbierto(true)
  }

  function abrirEditar(p: Proyecto): void {
    setItemEnEdicion(p)
    setForm({
      nombre: p.nombre,
      descripcion: p.descripcion ?? '',
      fechaInicio: p.fechaInicio,
      fechaFin: p.fechaFin ?? '',
      estado: p.estado,
      presupuesto: p.presupuesto != null ? String(p.presupuesto) : '',
      clienteId: p.clienteId != null ? String(p.clienteId) : '',
      empleadoIds: p.empleados.map((e) => e.id),
    })
    setFieldErrors({})
    setErrorForm('')
    setModalAbierto(true)
  }

  function cerrarModal(): void {
    setModalAbierto(false)
  }

  // Actualiza un campo de texto del formulario
  function cambiarCampo(campo: keyof ProyectoForm, valor: string): void {
    setForm((prev) => ({ ...prev, [campo]: valor }))
  }

  // Multi-select de empleados: leemos las opciones seleccionadas como números
  function cambiarEmpleados(e: React.ChangeEvent<HTMLSelectElement>): void {
    const seleccion = Array.from(e.target.selectedOptions).map((o) => Number(o.value))
    setForm((prev) => ({ ...prev, empleadoIds: seleccion }))
  }

  async function guardar(e: React.FormEvent): Promise<void> {
    e.preventDefault()
    setGuardando(true)
    setFieldErrors({})
    setErrorForm('')

    // Construimos el ProyectoInput a partir del formulario de texto
    const input: ProyectoInput = {
      nombre: form.nombre,
      descripcion: form.descripcion || undefined,
      fechaInicio: form.fechaInicio,
      fechaFin: form.fechaFin ? form.fechaFin : null,
      estado: form.estado,
      presupuesto: form.presupuesto ? Number(form.presupuesto) : undefined,
      clienteId: Number(form.clienteId),
      empleadoIds: form.empleadoIds,
    }

    try {
      if (itemEnEdicion) {
        await api.put<Proyecto>(`/proyectos/${itemEnEdicion.id}`, input)
      } else {
        await api.post<Proyecto>('/proyectos', input)
      }
      cerrarModal()
      await cargarTodo()
    } catch (err) {
      const apiErr = err as ApiError
      if (apiErr.status === 400 && apiErr.fieldErrors) {
        setFieldErrors(apiErr.fieldErrors)
      } else {
        // 409 u otro error: se muestra dentro del modal.
        setErrorForm(apiErr.message || 'No se pudo guardar el proyecto.')
      }
    } finally {
      setGuardando(false)
    }
  }

  async function eliminar(p: Proyecto): Promise<void> {
    if (!window.confirm(`¿Eliminar el proyecto "${p.nombre}"?`)) return
    try {
      await api.del(`/proyectos/${p.id}`)
      await cargarTodo()
    } catch (err) {
      const apiErr = err as ApiError
      // 409 = el proyecto está en uso; mostramos el mensaje del backend
      setError(apiErr.message || 'No se pudo eliminar el proyecto.')
    }
  }

  // Definición de columnas de la tabla
  const columns = [
    { key: 'nombre', label: 'Nombre' },
    {
      key: 'clienteNombre',
      label: 'Cliente',
      render: (row: Proyecto): ReactNode => row.clienteNombre ?? '—',
    },
    {
      key: 'estado',
      label: 'Estado',
      render: (row: Proyecto): ReactNode => (
        <Badge color={colorEstado(row.estado)}>{row.estado}</Badge>
      ),
    },
    {
      key: 'fechas',
      label: 'Fechas',
      render: (row: Proyecto): ReactNode => (
        <span className="text-sm text-slate-600">
          {row.fechaInicio} → {row.fechaFin ?? '—'}
        </span>
      ),
    },
    {
      key: 'presupuesto',
      label: 'Presupuesto',
      render: (row: Proyecto): ReactNode =>
        row.presupuesto != null ? `$${row.presupuesto.toLocaleString('es-EC')}` : '—',
    },
    {
      key: 'empleados',
      label: 'Empleados',
      render: (row: Proyecto): ReactNode => row.empleados.length,
    },
  ]

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-semibold text-slate-800">Proyectos</h1>
        {puedeEditar && (
          <button
            type="button"
            onClick={abrirCrear}
            className="bg-indigo-600 text-white hover:bg-indigo-700 rounded-md px-4 py-2"
          >
            Nuevo proyecto
          </button>
        )}
      </div>

      {error && (
        <div className="mb-4 rounded-md border border-red-300 bg-red-50 px-4 py-2 text-sm text-red-700">
          {error}
        </div>
      )}

      {cargando ? (
        <p className="text-slate-500">Cargando proyectos…</p>
      ) : (
        <DataTable
          columns={columns}
          rows={lista}
          empty="No hay proyectos registrados."
          actions={
            puedeEditar
              ? (row: Proyecto): ReactNode => (
                  <div className="flex gap-2">
                    <button
                      type="button"
                      onClick={() => abrirEditar(row)}
                      className="text-indigo-600 hover:text-indigo-800 text-sm font-medium"
                    >
                      Editar
                    </button>
                    <button
                      type="button"
                      onClick={() => eliminar(row)}
                      className="text-red-600 hover:text-red-800 text-sm font-medium"
                    >
                      Eliminar
                    </button>
                  </div>
                )
              : undefined
          }
        />
      )}

      <Modal
        open={modalAbierto}
        title={itemEnEdicion ? 'Editar proyecto' : 'Nuevo proyecto'}
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
              form="form-proyecto"
              disabled={guardando}
              className="bg-indigo-600 text-white hover:bg-indigo-700 rounded-md px-4 py-2 disabled:opacity-50"
            >
              {guardando ? 'Guardando…' : 'Guardar'}
            </button>
          </div>
        }
      >
        <form id="form-proyecto" onSubmit={guardar} className="space-y-3">
          {errorForm && (
            <div className="rounded-md border border-red-300 bg-red-50 px-4 py-2 text-sm text-red-700">
              {errorForm}
            </div>
          )}

          <FormField label="Nombre" error={fieldErrors.nombre}>
            <input
              type="text"
              value={form.nombre}
              onChange={(e) => cambiarCampo('nombre', e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2"
            />
          </FormField>

          <FormField label="Descripción" error={fieldErrors.descripcion}>
            <textarea
              value={form.descripcion}
              onChange={(e) => cambiarCampo('descripcion', e.target.value)}
              rows={3}
              className="w-full border border-slate-300 rounded-md px-3 py-2"
            />
          </FormField>

          <div className="grid grid-cols-2 gap-3">
            <FormField label="Fecha de inicio" error={fieldErrors.fechaInicio}>
              <input
                type="date"
                value={form.fechaInicio}
                onChange={(e) => cambiarCampo('fechaInicio', e.target.value)}
                className="w-full border border-slate-300 rounded-md px-3 py-2"
              />
            </FormField>

            <FormField label="Fecha de fin" error={fieldErrors.fechaFin}>
              <input
                type="date"
                value={form.fechaFin}
                onChange={(e) => cambiarCampo('fechaFin', e.target.value)}
                className="w-full border border-slate-300 rounded-md px-3 py-2"
              />
            </FormField>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <FormField label="Estado" error={fieldErrors.estado}>
              <select
                value={form.estado}
                onChange={(e) => cambiarCampo('estado', e.target.value)}
                className="w-full border border-slate-300 rounded-md px-3 py-2"
              >
                {ESTADOS.map((es) => (
                  <option key={es} value={es}>
                    {es}
                  </option>
                ))}
              </select>
            </FormField>

            <FormField label="Presupuesto" error={fieldErrors.presupuesto}>
              <input
                type="number"
                min="0"
                step="0.01"
                value={form.presupuesto}
                onChange={(e) => cambiarCampo('presupuesto', e.target.value)}
                className="w-full border border-slate-300 rounded-md px-3 py-2"
              />
            </FormField>
          </div>

          <FormField label="Cliente" error={fieldErrors.clienteId}>
            <select
              value={form.clienteId}
              onChange={(e) => cambiarCampo('clienteId', e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2"
            >
              <option value="">— Seleccioná un cliente —</option>
              {clientes.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.nombre}
                </option>
              ))}
            </select>
          </FormField>

          <FormField label="Empleados asignados" error={fieldErrors.empleadoIds}>
            <select
              multiple
              value={form.empleadoIds.map(String)}
              onChange={cambiarEmpleados}
              className="w-full border border-slate-300 rounded-md px-3 py-2 h-40"
            >
              {empleados.map((emp) => (
                <option key={emp.id} value={emp.id}>
                  {emp.nombre} {emp.apellido}
                </option>
              ))}
            </select>
            <p className="mt-1 text-xs text-slate-500">
              Mantené Ctrl (o Cmd) para seleccionar varios.
            </p>
          </FormField>
        </form>
      </Modal>
    </div>
  )
}

export default ProyectosPage
