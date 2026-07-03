import type { ReactNode } from 'react'

interface Columna {
  key: string
  label: string
  render?: (row: any) => ReactNode
}

interface DataTableProps {
  columns: Columna[]
  rows: any[]
  actions?: (row: any) => ReactNode
  empty?: string
}

export default function DataTable({
  columns,
  rows,
  actions,
  empty = 'Sin registros',
}: DataTableProps) {
  const totalColumnas = columns.length + (actions ? 1 : 0)

  return (
    <div className="overflow-x-auto rounded-lg border border-slate-200">
      <table className="min-w-full divide-y divide-slate-200">
        <thead className="bg-slate-100 text-sm text-slate-600">
          <tr>
            {columns.map((c) => (
              <th key={c.key} className="px-4 py-2 text-left font-medium">
                {c.label}
              </th>
            ))}
            {actions && <th className="px-4 py-2 text-right font-medium">Acciones</th>}
          </tr>
        </thead>
        <tbody className="bg-white">
          {rows.length === 0 ? (
            <tr>
              <td colSpan={totalColumnas} className="px-4 py-6 text-center text-slate-400">
                {empty}
              </td>
            </tr>
          ) : (
            rows.map((row, i) => (
              <tr key={row.id ?? i} className="border-b hover:bg-slate-50">
                {columns.map((c) => (
                  <td key={c.key} className="px-4 py-2 text-sm text-slate-700">
                    {c.render ? c.render(row) : row[c.key]}
                  </td>
                ))}
                {actions && (
                  <td className="px-4 py-2 text-right text-sm">{actions(row)}</td>
                )}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  )
}
