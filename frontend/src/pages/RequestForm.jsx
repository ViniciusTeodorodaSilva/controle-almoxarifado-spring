import ContextSelector from '../components/ContextSelector'
import { useCallback, useEffect, useState } from 'react'
import { Plus, Trash2 } from 'lucide-react'
import { api } from '../api/client'
import { useResource } from '../hooks/useResource'
import ProductPicker from '../components/ProductPicker'
import { Modal, Field, Notice, ResourceView, DataTable, quantity } from '../components/ui'
import { quantityError, unitLabel, submitRequest, reconcileRequest } from '../utils/operations'
export default function RequestForm({ onClose, onSaved, onPartial, onOpen }) {
  const refs = useResource(useCallback(signal => Promise.all(['funcionarios', 'almoxarifados'].map(name => api.list(name, null, signal))), []))
  const [draft, setDraft] = useState({ solicitanteId: '', almoxarifadoId: '', items: [], contexto: {} })
  const [product, setProduct] = useState(null), [amount, setAmount] = useState('')
  const [busy, setBusy] = useState(false), [error, setError] = useState(''), [itemError, setItemError] = useState('')
  const [progress, setProgress] = useState({ id: null, completed: 0, uncertain: false, needsCheck: false })
  useEffect(() => {
    if (!busy && !progress.id && !progress.uncertain) return
    const prevent = event => { event.preventDefault(); event.returnValue = '' }
    window.addEventListener('beforeunload', prevent)
    return () => window.removeEventListener('beforeunload', prevent)
  }, [busy, progress.id, progress.uncertain])
  function addItem() {
    const invalid = !product ? 'Selecione um material do catálogo.' : quantityError(product, amount)
    if (invalid) { setItemError(invalid); return }
    setDraft(previous => ({ ...previous, items: [...previous.items, { key: crypto.randomUUID(), product, amount }] }))
    setProduct(null); setAmount(''); setItemError(''); setError('')
  }
  async function save(event) {
    event.preventDefault()
    if (!progress.id && (product || amount)) { setError('Adicione o material em edição à lista de itens antes de criar a solicitação.'); return }
    if (!draft.items.length) { setError('Adicione pelo menos um item antes de criar a solicitação.'); return }
    setBusy(true); setError('')
    try {
      const result = await submitRequest(api, draft, progress, setProgress)
      onSaved(result.id)
    } catch (error) {
      setProgress({ ...error.progress, needsCheck: !!error.progress.id })
      setError(error.message)
      onPartial(error.progress)
    } finally { setBusy(false) }
  }
  async function check() {
    setBusy(true); setError('')
    try {
      const result = await reconcileRequest(api, draft.items, progress)
      setProgress({ ...result, needsCheck: false })
      if (result.completed === draft.items.length) onSaved(result.id)
    } catch (error) { setError(error.message) }
    finally { setBusy(false) }
  }
  return <Modal title="Nova solicitação" onClose={onClose} busy={busy}><form onSubmit={save}>
    <Notice error>{error}</Notice>
    {progress.id && <Notice>Solicitação #{progress.id} já criada. {progress.completed} de {draft.items.length} itens confirmados. Não inicie outra solicitação para repetir este envio.</Notice>}
    {progress.uncertain && <Notice error>{progress.id ? 'O último item pode ter sido recebido. Confira os itens salvos antes de continuar.' : 'A criação pode ter sido recebida, mas não retornou o número. Confira a listagem antes de tentar novamente.'}</Notice>}
    <ResourceView resource={refs}>{([people, warehouses]) => <>
      <fieldset disabled={busy || !!progress.id || progress.uncertain} className="form-section"><legend>Identificação</legend><div className="form-grid">
        <Field label="Solicitante *"><select required value={draft.solicitanteId} onChange={event => setDraft({ ...draft, solicitanteId: event.target.value })}><option value="">Selecionar funcionário</option>{people.map(person => <option key={person.id} value={person.id}>{person.nome}</option>)}</select></Field>
        <Field label="Almoxarifado *"><select required value={draft.almoxarifadoId} onChange={event => setDraft({ ...draft, almoxarifadoId: event.target.value })}><option value="">Selecionar almoxarifado</option>{warehouses.map(place => <option key={place.id} value={place.id}>{place.nome}</option>)}</select></Field>
      </div><ContextSelector value={draft.contexto} onChange={contexto=>setDraft({...draft,contexto})} disabled={busy || !!progress.id || progress.uncertain}/></fieldset>
      {!progress.id && !progress.uncertain && <fieldset disabled={busy} className="form-section"><legend>Adicionar material</legend>
        <ProductPicker value={product} onChange={value => { setProduct(value); setItemError('') }} required={false}/>
        <div className="item-entry"><Field label="Quantidade" error={itemError} hint={product ? `Unidade: ${unitLabel(product)}${product.unidadeMedidaConfigurada?.permiteFracionamento === false ? ' · somente inteiros' : ''}` : undefined}><input type="number" min="0" step={product?.unidadeMedidaConfigurada?.permiteFracionamento === false ? '1' : 'any'} value={amount} onChange={event => { setAmount(event.target.value); setItemError('') }}/></Field><button type="button" className="btn secondary" onClick={addItem}><Plus size={15}/>Adicionar item</button></div>
      </fieldset>}
      <section className="draft-items"><h3>Itens da solicitação <span className="count-label">{draft.items.length}</span></h3><DataTable empty="Nenhum item adicionado." rows={draft.items.map((item, index) => ({ ...item, id: item.key, index }))} columns={[
        { key: 'codigo', label: 'Código', render: item => <span className="code">{item.product.codigo || '—'}</span> },
        { key: 'material', label: 'Material', render: item => item.product.nome },
        { key: 'quantidade', label: 'Quantidade', render: item => quantity(Number(item.amount)) },
        { key: 'unidade', label: 'Unidade', render: item => unitLabel(item.product) },
        { key: 'action', label: 'Ação', render: item => progress.id ? (item.index < progress.completed ? 'Confirmado' : 'Aguardando envio') : <button type="button" className="btn text" disabled={busy} aria-label={`Remover item ${item.index + 1}`} onClick={() => setDraft(previous => ({ ...previous, items: previous.items.filter(row => row.key !== item.key) }))}><Trash2 size={14}/>Remover</button> }
      ]}/></section>
      <div className="form-actions"><button type="button" className="btn secondary" disabled={busy} onClick={onClose}>{progress.id || progress.uncertain ? 'Fechar e conferir listagem' : 'Cancelar'}</button>
        {progress.id && <button type="button" className="btn secondary" disabled={busy} onClick={() => onOpen(progress.id)}>Abrir detalhes</button>}
        {progress.id && progress.needsCheck ? <button type="button" className="btn" disabled={busy} onClick={check}>{busy ? 'Conferindo…' : 'Conferir itens salvos'}</button> : <button className="btn" disabled={busy || progress.uncertain}>{busy ? 'Enviando…' : progress.id ? 'Continuar envio' : 'Criar solicitação'}</button>}
      </div>
    </>}</ResourceView>
  </form></Modal>
}
