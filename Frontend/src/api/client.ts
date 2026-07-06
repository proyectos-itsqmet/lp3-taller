

const BASE = '/api'

export interface ApiError {
  status: number
  message: string
  fieldErrors?: Record<string, string>
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const res = await fetch(BASE + path, {
    ...options,
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  })


  if (res.status === 204) {
    return undefined as T
  }


  const contentType = res.headers.get('content-type') ?? ''
  const esJson = contentType.includes('application/json')
  const data = esJson ? await res.json() : await res.text()

  if (!res.ok) {
    const error: ApiError = {
      status: res.status,
      message: (esJson && data?.message) || 'Error inesperado',
      fieldErrors: esJson ? data?.fieldErrors : undefined,
    }
    throw error
  }

  return data as T
}

export const api = {
  get<T>(path: string): Promise<T> {
    return request<T>(path, { method: 'GET' })
  },
  post<T>(path: string, body?: unknown): Promise<T> {
    return request<T>(path, { method: 'POST', body: JSON.stringify(body) })
  },
  put<T>(path: string, body?: unknown): Promise<T> {
    return request<T>(path, { method: 'PUT', body: JSON.stringify(body) })
  },
  del(path: string): Promise<void> {
    return request<void>(path, { method: 'DELETE' })
  },
}


export async function descargarPdf(path: string, filename: string): Promise<void> {
  const res = await fetch(BASE + path, { credentials: 'include' })

  if (!res.ok) {
    const error: ApiError = {
      status: res.status,
      message: 'No se pudo descargar el archivo',
    }
    throw error
  }

  const blob = await res.blob()
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  document.body.appendChild(a)
  a.click()
  a.remove()
  URL.revokeObjectURL(url)
}
