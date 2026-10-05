import { cloneElement, useEffect, useId, useRef } from 'react'
import { AlertCircle, Inbox, LoaderCircle, X } from 'lucide-react'
export function PageHeader({ eyebrow = 'PLATAFORMA BES', title, description, children }) {
  return <div className="page-heading"><div><p className="eyebrow">{eyebrow}</p><h1>{title}</h1>{description && <p className="description">{description}</p>}</div><div className="page-actions">{children}</div></div>
}
export function Card({ children, className = '' }) { return <section className={`card ${className}`}>{children}</section> }
export function Badge({ value }) {
  const labels = { EM_COMPRA: 'Em compra', RASCUNHO: 'Rascunho', AGUARDANDO_APROVACAO: 'Aguardando aprovação', APROVADO: 'Aprovado', PARCIALMENTE_RECEBIDO: 'Parcialmente recebido', RECEBIDO: 'Recebido', CANCELADO: 'Cancelado', EM_SEPARACAO: 'Em separação', PARCIALMENTE_ATENDIDA: 'Parcialmente atendida', ATENDIDA: 'Atendida', ABERTA: 'Aberta', CANCELADA: 'Cancelada', NORMAL: 'Normal', BAIXO: 'Baixo', ZERADO: 'Zerado', CONCLUIDA: 'Concluída', PENDENTE: 'Pendente', APROVADA: 'Aprovada', REJEITADA: 'Rejeitada', ENTRADA: 'Entrada', SAIDA: 'Saída', true: 'Ativo', false: 'Inativo' }
  return <span className={`badge badge-${String(value).toLowerCase()}`}>{labels[String(value)] || value || '—'}</span>
}
export function LoadingState() { return <div className="state" role="status"><LoaderCircle className="animate-spin" size={24}/><strong>Carregando informações…</strong></div> }
export function EmptyState({ message = 'Nenhum registro encontrado.', children }) { return <div className="state"><Inbox size={30}/><strong>{message}</strong><p>Cadastre um registro ou ajuste os filtros para começar.</p>{children}</div> }
export function ErrorState({ error, retry }) { return <div className="state error" role="alert"><AlertCircle size={28}/><strong>Não foi possível carregar os dados</strong><p>{error.message}</p>{retry && <button className="btn secondary" onClick={retry}>Tentar novamente</button>}</div> }
export function Notice({ children, error = false }) { return children ? <div className={`notice ${error ? 'notice-error' : ''}`} role={error ? 'alert' : 'status'}>{children}</div> : null }
export function DataTable({ columns, rows, empty }) {
  if (!rows.length) return <EmptyState message={empty}/>
  return <div className="table-scroll" tabIndex={0} aria-label="Tabela de registros, role horizontalmente para ver todas as colunas"><table style={columns.length >= 6 ? { minWidth: columns.length * 115 } : undefined}><thead><tr>{columns.map(column => <th key={column.key} scope="col">{column.label}</th>)}</tr></thead><tbody>{rows.map(row => <tr key={row.id}>{columns.map(column => <td key={column.key}>{column.render ? column.render(row) : (row[column.key] ?? '—')}</td>)}</tr>)}</tbody></table></div>
}
export function ResourceView({ resource, children }) {
  if (resource.loading) return <LoadingState/>
  if (resource.error) return <ErrorState error={resource.error} retry={resource.reload}/>
  return children(resource.data || [])
}
export function Field({ label, children, hint, error }) {
  const id = useId()
  return <div className="field"><label htmlFor={id}>{label}</label>{cloneElement(children, { id, 'aria-describedby': error ? id + '-error' : hint ? id + '-hint' : undefined, 'aria-invalid': error ? true : undefined })}{hint && <small id={id + '-hint'}>{hint}</small>}{error && <small className="field-error" id={id + '-error'} role="alert">{error}</small>}</div>
}
export function Modal({ title, children, onClose, busy = false }) {
  const dialog = useRef(null)
  useEffect(() => { const element = dialog.current; element.showModal(); return () => element.close() }, [])
  return <dialog ref={dialog} className="dialog" aria-labelledby="dialog-title" onCancel={event => { event.preventDefault(); if (!busy) onClose() }}>
    <div className="dialog-header"><h2 id="dialog-title">{title}</h2><button type="button" className="icon-button" aria-label="Fechar" onClick={onClose} disabled={busy}><X size={20}/></button></div>{children}
  </dialog>
}
export const quantity = value => new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 6 }).format(value ?? 0)
export const dateTime = value => value ? new Date(value).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' }) : '—'
