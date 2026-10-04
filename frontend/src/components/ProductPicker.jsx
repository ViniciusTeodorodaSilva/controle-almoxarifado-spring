import { useCallback, useEffect, useState } from 'react'
import { api } from '../api/client'
import { useResource } from '../hooks/useResource'
import { Field, Notice } from './ui'
import { unitLabel } from '../utils/operations'
export default function ProductPicker({ value, onChange, disabled = false, required = true }) {
  const [term, setTerm] = useState(''), [query, setQuery] = useState('')
  useEffect(() => { const timer = setTimeout(() => setQuery(term), 250); return () => clearTimeout(timer) }, [term])
  const resource = useResource(useCallback(signal => api.searchProducts({ termo: query }, signal), [query]))
  const rows = resource.data || []
  const choices = value && !rows.some(row => row.id === value.id) ? [value, ...rows] : rows
  return <div className="catalog-picker">
    <Field label="Pesquisar catálogo"><input type="search" value={term} disabled={disabled} placeholder="Código, nome ou descrição" onChange={event => setTerm(event.target.value)} onKeyDown={event => { if (event.key === 'Enter') event.preventDefault() }}/></Field>
    <Field label="Produto *"><select required={required} disabled={disabled || resource.loading || !!resource.error} value={value?.id || ''} onChange={event => onChange(choices.find(row => String(row.id) === event.target.value) || null)}>
      <option value="">{resource.loading ? 'Consultando catálogo…' : 'Selecionar material'}</option>
      {choices.map(product => <option key={product.id} value={product.id}>{product.codigo || '#' + product.id} · {product.nome} · {unitLabel(product)}{product.ativo === false ? ' (inativo)' : ''}</option>)}
    </select></Field>
    {resource.error && <Notice error>{resource.error.message} <button type="button" className="btn text" onClick={resource.reload}>Tentar novamente</button></Notice>}
    {!resource.loading && !resource.error && !choices.length && <small className="muted">Nenhum material encontrado. Cadastre-o em Produtos.</small>}
  </div>
}
