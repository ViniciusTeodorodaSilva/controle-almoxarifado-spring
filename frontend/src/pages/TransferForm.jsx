import { useCallback, useEffect, useRef, useState } from 'react'
import { api } from '../api/client'
import { useResource } from '../hooks/useResource'
import ProductPicker from '../components/ProductPicker'
import { Modal, Field, Notice, ResourceView, DataTable, quantity } from '../components/ui'
import { unitLabel } from '../utils/operations'
import { transferItemError, transferError, transferPayload } from '../utils/stockIntelligence'
export default function TransferForm({ onClose, onSaved, onChanged }) {
  const attempt = useRef(null)
  const refs = useResource(useCallback(signal => Promise.all(['almoxarifados', 'funcionarios'].map(name => api.list(name, null, signal))), []))
  const [draft, setDraft] = useState({ origemId: '', destinoId: '', responsavelId: '', observacao: '', items: [] })
  const [product, setProduct] = useState(null), [amount, setAmount] = useState(''), [balance, setBalance] = useState(null)
  const [loading, setLoading] = useState(false), [balanceError, setBalanceError] = useState(''), [refresh, setRefresh] = useState(0)
  const [error, setError] = useState(''), [itemError, setItemError] = useState(''), [confirm, setConfirm] = useState(false), [busy, setBusy] = useState(false), [uncertain, setUncertain] = useState(false)
  useEffect(() => {
    if (!busy && !uncertain) return
    const prevent = event => { event.preventDefault(); event.returnValue = '' }
    window.addEventListener('beforeunload', prevent)
    return () => window.removeEventListener('beforeunload', prevent)
  }, [busy, uncertain])
  useEffect(() => {
    setBalance(null); setBalanceError(''); setLoading(false)
    if (!product || !draft.origemId) return
    const controller = new AbortController(); setLoading(true)
    api.stock(product.id, Number(draft.origemId), controller.signal).then(stock => { if (!controller.signal.aborted) setBalance(stock) })
      .catch(error => { if (!controller.signal.aborted) setBalanceError(error.status === 404 ? 'Produto sem estoque cadastrado na origem.' : error.message) })
      .finally(() => { if (!controller.signal.aborted) setLoading(false) })
    return () => controller.abort()
  }, [product?.id, draft.origemId, refresh])
  function change(key, value) {
    setDraft(previous => ({ ...previous, [key]: value, ...(key === 'origemId' ? { items: [] } : {}) })); setConfirm(false); setError(''); setItemError('')
  }
  function add() {
    const issue = transferItemError(product, amount, balance, draft.items)
    if (issue) { setItemError(issue); return }
    setDraft(previous => ({ ...previous, items: [...previous.items, { product, amount: Number(amount), balance: balance.quantidade }] }))
    setProduct(null); setAmount(''); setItemError(''); setError(''); setConfirm(false)
  }
  async function review(event) {
    event.preventDefault()
    if (busy || uncertain) return
    const issue = transferError(draft)
    if (issue) { setError(issue); return }
    setBusy(true); setError('')
    try {
      // Refresh every balance before confirmation. The backend still locks and validates all items.
      const stocks = await Promise.all(draft.items.map(item => api.stock(item.product.id, Number(draft.origemId))))
      const current = { ...draft, items: draft.items.map((item, index) => ({ ...item, balance: stocks[index].quantidade })) }
      setDraft(current)
      const stale = transferError(current)
      if (stale) { setError(stale); return }
      setConfirm(true)
    } catch (error) { setError(error.message) }
    finally { setBusy(false) }
  }
  async function execute() {
    if (busy) return
    if (!attempt.current) attempt.current = { key: crypto.randomUUID(), body: transferPayload(draft) }
    setBusy(true); setError('')
    try { const result = await api.createTransfer(attempt.current.body, attempt.current.key); onSaved(result) }
    catch (error) {
      setError(error.message); setConfirm(false); onChanged(); setRefresh(value => value + 1)
      // Keep the original key until its outcome is known, including a failed lookup.
      if (uncertain || !error.status || error.status >= 500) setUncertain(true)
      else { attempt.current = null; setUncertain(false) }
    }
    finally { setBusy(false) }
  }
  const locked = busy || confirm || uncertain
  return <Modal title="Nova transferência" onClose={onClose} busy={busy}><form onSubmit={review}>
    <Notice error>{error}</Notice>
    {uncertain && <Notice error>Não foi possível confirmar o resultado. A transferência pode ter sido recebida. Confira a listagem ou consulte novamente o resultado desta tentativa antes de iniciar outra.</Notice>}
    {uncertain && <button type="button" className="btn secondary" disabled={busy} onClick={execute}>Consultar resultado da tentativa</button>}
    <ResourceView resource={refs}>{([warehouses, people]) => <>
      <fieldset className="form-section" disabled={locked}><legend>Origem, destino e responsável</legend><div className="form-grid">
        <Field label="Almoxarifado de origem *" hint={draft.items.length ? 'Trocar a origem remove os itens deste rascunho.' : undefined}><select required value={draft.origemId} onChange={event => change('origemId', event.target.value)}><option value="">Selecionar origem</option>{warehouses.map(place => <option key={place.id} value={place.id}>{place.nome}</option>)}</select></Field>
        <Field label="Almoxarifado de destino *"><select required value={draft.destinoId} onChange={event => change('destinoId', event.target.value)}><option value="">Selecionar destino</option>{warehouses.map(place => <option key={place.id} value={place.id}>{place.nome}</option>)}</select></Field>
        <Field label="Responsável *"><select required value={draft.responsavelId} onChange={event => change('responsavelId', event.target.value)}><option value="">Selecionar funcionário</option>{people.map(person => <option key={person.id} value={person.id}>{person.nome}</option>)}</select></Field>
      </div></fieldset>
      <fieldset className="form-section" disabled={locked || !draft.origemId}><legend>Materiais do catálogo</legend>
        <ProductPicker value={product} onChange={value => { setProduct(value); setItemError('') }} required={false}/>
        <div className="balance-summary" aria-live="polite"><span>Saldo disponível na origem</span><strong>{loading ? 'Consultando…' : balance ? `${quantity(balance.quantidade)} ${unitLabel(product)}` : 'Selecione um material'}</strong></div>
        <Notice error>{balanceError}</Notice>{balanceError && <button type="button" className="btn text" onClick={() => setRefresh(value => value + 1)}>Consultar saldo novamente</button>}
        <div className="form-grid"><Field label="Quantidade a transferir" error={itemError} hint={product ? `Unidade: ${unitLabel(product)}${product.unidadeMedidaConfigurada?.permiteFracionamento === false ? ' · somente inteiros' : ''}` : undefined}><input type="number" min="0" step="any" value={amount} onChange={event => { setAmount(event.target.value); setItemError('') }}/></Field><div className="field add-item-action"><button type="button" className="btn secondary" disabled={loading || !balance} onClick={add}>Adicionar produto</button></div></div>
      </fieldset>
      <DataTable empty="Nenhum produto adicionado." rows={draft.items.map(item => ({ ...item, id: item.product.id }))} columns={[
        { key: 'codigo', label: 'Código', render: item => <span className="code">{item.product.codigo}</span> },
        { key: 'produto', label: 'Material', render: item => item.product.nome },
        { key: 'unidade', label: 'Unidade', render: item => unitLabel(item.product) },
        { key: 'saldo', label: 'Saldo na origem', render: item => quantity(item.balance) },
        { key: 'quantidade', label: 'Transferir', render: item => quantity(item.amount) },
        { key: 'acoes', label: 'Ações', render: item => <button type="button" className="btn text" disabled={locked} onClick={() => { setDraft(previous => ({ ...previous, items: previous.items.filter(entry => entry.product.id !== item.product.id) })); setError('') }}>Remover</button> }
      ]}/>
      <fieldset className="form-section" disabled={locked}><legend>Observação</legend><Field label="Observação opcional"><textarea maxLength={1000} rows={2} value={draft.observacao} onChange={event => change('observacao', event.target.value)}/></Field></fieldset>
      {confirm && <div className="confirmation" role="alert"><strong>Confirmar transferência?</strong><p>Esta operação irá registrar saída no almoxarifado de origem e entrada no almoxarifado de destino.</p><small>{warehouses.find(place => String(place.id) === draft.origemId)?.nome} → {warehouses.find(place => String(place.id) === draft.destinoId)?.nome} · {draft.items.length} produto(s). Todos os itens serão validados novamente.</small></div>}
      <div className="form-actions"><button type="button" className="btn secondary" disabled={busy} onClick={onClose}>{uncertain ? 'Voltar e conferir transferências' : 'Cancelar'}</button>{confirm ? <><button type="button" className="btn secondary" disabled={busy} onClick={() => setConfirm(false)}>Revisar dados</button><button type="button" className="btn" disabled={busy} onClick={execute}>{busy ? 'Transferindo…' : 'Confirmar transferência'}</button></> : <button className="btn" disabled={busy || uncertain || refs.loading || !!refs.error}>{busy ? 'Conferindo saldos…' : 'Revisar transferência'}</button>}</div>
    </>}</ResourceView>
  </form></Modal>
}
