import { useCallback, useEffect, useState } from 'react'
import { api } from '../api/client'
import { useResource } from '../hooks/useResource'
import ProductPicker from '../components/ProductPicker'
import { Modal, Field, Notice, ResourceView, quantity } from '../components/ui'
import { quantityError, unitLabel, registerMovement } from '../utils/operations'
export default function StockMovementForm({ type, onClose, onSaved, onChanged }) {
  const refs = useResource(useCallback(signal => Promise.all(['funcionarios', 'almoxarifados'].map(name => api.list(name, null, signal))), []))
  const [product, setProduct] = useState(null)
  const [form, setForm] = useState({ almoxarifadoId: '', quantidade: '', solicitanteId: '', responsavelId: '' })
  const [balance, setBalance] = useState({ loading: false, stock: null, missing: false, error: '' })
  const [busy, setBusy] = useState(false), [confirm, setConfirm] = useState(false), [error, setError] = useState(''), [invalid, setInvalid] = useState(''), [uncertain, setUncertain] = useState(false), [refresh, setRefresh] = useState(0)
  const [allowCreate, setAllowCreate] = useState(false)
  useEffect(() => {
    if (!busy && !uncertain) return
    const prevent = event => { event.preventDefault(); event.returnValue = '' }
    window.addEventListener('beforeunload', prevent)
    return () => window.removeEventListener('beforeunload', prevent)
  }, [busy, uncertain])
  const change = (key, value) => { setForm(previous => ({ ...previous, [key]: value })); setConfirm(false); setInvalid('') }
  useEffect(() => {
    if (!product || !form.almoxarifadoId) { setBalance({ loading: false, stock: null, missing: false, error: '' }); return }
    const controller = new AbortController()
    setAllowCreate(false); setBalance({ loading: true, stock: null, missing: false, error: '' })
    api.stock(product.id, Number(form.almoxarifadoId), controller.signal).then(stock => {
      if (!controller.signal.aborted) setBalance({ loading: false, stock, missing: false, error: '' })
    }).catch(error => {
      if (!controller.signal.aborted) setBalance({ loading: false, stock: null, missing: error.status === 404, error: error.status === 404 ? '' : error.message })
    })
    return () => controller.abort()
  }, [product?.id, form.almoxarifadoId, refresh])
  function prepare(event) {
    event.preventDefault()
    const issue = quantityError(product, form.quantidade)
    if (issue) { setInvalid(issue); return }
    if (type === 'saida' && (!balance.stock || Number(form.quantidade) > balance.stock.quantidade)) { setInvalid('A quantidade de saída é superior ao saldo disponível.'); return }
    setError(''); setConfirm(true)
  }
  async function execute() {
    setBusy(true); setError('')
    try {
      await registerMovement(api, type, { ...form, produtoId: product.id, quantidade: Number(form.quantidade), almoxarifadoId: Number(form.almoxarifadoId), solicitanteId: Number(form.solicitanteId), responsavelId: Number(form.responsavelId) }, allowCreate)
      onSaved(type === 'entrada' ? 'Entrada registrada com sucesso.' : 'Saída registrada com sucesso.')
    } catch (error) {
      setError(error.message); setConfirm(false); onChanged()
      if (!error.status || error.status >= 500) setUncertain(true)
      else setRefresh(value => value + 1)
    } finally { setBusy(false) }
  }
  const unavailable = !product || !form.almoxarifadoId || balance.loading || !!balance.error || (balance.missing && (type === 'saida' || !allowCreate))
  return <Modal title={type === 'entrada' ? 'Registrar entrada' : 'Registrar saída'} onClose={onClose} busy={busy}><form onSubmit={prepare}>
    <Notice error>{error}</Notice>
    {uncertain && <Notice error>Não foi possível confirmar o resultado. A operação pode ter sido recebida. Confira o saldo e o histórico antes de registrar outra movimentação; ela não será repetida aqui.</Notice>}
    <ResourceView resource={refs}>{([people, warehouses]) => <>
      <fieldset className="form-section" disabled={busy || confirm || uncertain}><legend>Material e localização</legend>
        <ProductPicker value={product} onChange={value => { setProduct(value); setConfirm(false); setInvalid('') }}/>
        <Field label="Almoxarifado *"><select required value={form.almoxarifadoId} onChange={event => change('almoxarifadoId', event.target.value)}><option value="">Selecionar almoxarifado</option>{warehouses.map(place => <option key={place.id} value={place.id}>{place.nome}</option>)}</select></Field>
      </fieldset>
      <div className="balance-summary" aria-live="polite"><span>Saldo atual</span><strong>{balance.loading ? 'Consultando…' : balance.stock ? `${quantity(balance.stock.quantidade)} ${unitLabel(product)}` : balance.missing ? 'Estoque não cadastrado' : 'Selecione material e almoxarifado'}</strong></div>
      <Notice error>{balance.error}</Notice>
      {balance.error && <button type="button" className="btn secondary" onClick={() => setRefresh(value => value + 1)}>Consultar saldo novamente</button>}
      {balance.missing && type === 'entrada' && <Field label="Criar estoque zerado para este produto e almoxarifado" hint="O registro será criado antes da entrada. Se a entrada falhar, o registro zerado permanecerá."><input type="checkbox" checked={allowCreate} disabled={busy || confirm || uncertain} onChange={event => setAllowCreate(event.target.checked)}/></Field>}
      {balance.missing && type === 'saida' && <Notice error>Cadastre o estoque através de uma entrada antes de registrar uma saída.</Notice>}
      <fieldset className="form-section" disabled={busy || confirm || uncertain}><legend>Quantidade e responsáveis</legend><div className="form-grid">
        <Field label="Quantidade *" error={invalid} hint={product ? `Unidade: ${unitLabel(product)}${product.unidadeMedidaConfigurada?.permiteFracionamento === false ? ' · somente inteiros' : ''}` : undefined}><input type="number" required min="0" step="any" value={form.quantidade} onChange={event => change('quantidade', event.target.value)}/></Field>
        <Field label="Solicitante *"><select required value={form.solicitanteId} onChange={event => change('solicitanteId', event.target.value)}><option value="">Selecionar funcionário</option>{people.map(person => <option key={person.id} value={person.id}>{person.nome}</option>)}</select></Field>
        <Field label="Responsável *"><select required value={form.responsavelId} onChange={event => change('responsavelId', event.target.value)}><option value="">Selecionar funcionário</option>{people.map(person => <option key={person.id} value={person.id}>{person.nome}</option>)}</select></Field>
      </div></fieldset>
      {confirm && <div className="confirmation" role="alert"><strong>Confirmar {type === 'entrada' ? 'entrada' : 'saída'} de {quantity(Number(form.quantidade))} {unitLabel(product)}?</strong><p>{product.nome} · {warehouses.find(place => String(place.id) === form.almoxarifadoId)?.nome}</p><small>O saldo será validado novamente ao registrar a operação.</small></div>}
      <div className="form-actions"><button type="button" className="btn secondary" disabled={busy} onClick={onClose}>{uncertain ? 'Voltar e conferir histórico' : 'Cancelar'}</button>{confirm ? <><button type="button" className="btn secondary" disabled={busy} onClick={() => setConfirm(false)}>Revisar dados</button><button type="button" className="btn" disabled={busy} onClick={execute}>{busy ? 'Registrando…' : 'Confirmar movimentação'}</button></> : <button className="btn" disabled={busy || uncertain || unavailable}>{type === 'entrada' ? 'Revisar entrada' : 'Revisar saída'}</button>}</div>
    </>}</ResourceView>
  </form></Modal>
}
