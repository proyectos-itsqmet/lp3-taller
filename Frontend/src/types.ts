
export type Rol = 'ADMIN' | 'GERENTE' | 'EMPLEADO'

export interface AuthUser {
  id: number
  username: string
  nombre: string
  email: string
  rol: Rol
}

export interface ProyectoResumen {
  id: number
  nombre: string
  estado: string
}

export interface EmpleadoResumen {
  id: number
  nombreCompleto: string
  cargo: string | null
}

export interface Perfil {
  id: number
  cargo: string
  especialidad: string | null
  telefono: string | null
  bio: string | null
  salario: number | null
}

export interface Empleado {
  id: number
  nombre: string
  apellido: string
  email: string
  documento: string
  fechaContratacion: string
  perfil: Perfil | null
  proyectos: ProyectoResumen[]
}

export interface Cliente {
  id: number
  nombre: string
  ruc: string
  email: string
  telefono: string | null
  direccion: string | null
  proyectos: ProyectoResumen[]
}

export interface Proyecto {
  id: number
  nombre: string
  descripcion: string | null
  fechaInicio: string
  fechaFin: string | null
  estado: string
  presupuesto: number | null
  clienteId: number | null
  clienteNombre: string | null
  empleados: EmpleadoResumen[]
}

export interface Usuario {
  id: number
  username: string
  nombre: string
  email: string
  enabled: boolean
  rol: Rol
}

// Requests

export interface PerfilInput {
  cargo: string
  especialidad?: string
  telefono?: string
  bio?: string
  salario?: number
}

export interface EmpleadoInput {
  nombre: string
  apellido: string
  email: string
  documento: string
  fechaContratacion: string
  perfil: PerfilInput
}

export interface ClienteInput {
  nombre: string
  ruc: string
  email: string
  telefono?: string
  direccion?: string
}

export interface ProyectoInput {
  nombre: string
  descripcion?: string
  fechaInicio: string
  fechaFin?: string | null
  estado: string
  presupuesto?: number
  clienteId: number
  empleadoIds: number[]
}

export interface UsuarioInput {
  username: string
  password: string
  nombre: string
  email: string
  rol: Rol
  enabled: boolean
}
