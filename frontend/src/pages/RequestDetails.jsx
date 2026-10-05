import { Can, useAuth } from '../auth/AuthContext'
import { useCallback, useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import { useResource } from '../hooks/useResource'
import { Modal, ResourceView, Notice, Field, Badge, DataTable, dateTime, quantity } from '../components/ui'
import { unitLabel } from '../utils/operations'
import { canFulfill, fulfillmentDraft, suggestedAmounts } from '../utils/fulfillment'
const options = rows => (rows || []).map(row => <option key={row.id} value={row.id}>{row.nome}</option>)
const amount = value => value == null ? 'Conferir' : quantity(value)
export default function RequestDetails({ id, onClose, onChanged }) {
  const auth = useAuth()
  const resource = useResource(useCallback(signal => api.requestOperation(id, signal), [id]))
  const people = useResource(useCallback(signal => api.list('funcionarios', null, signal), []))
  const history = useResource(useCallback(signal => api.fulfillments(id, signal), [id]))
  const [responsible, setResponsible] = useState(''), [confirmation, setConfirmation] = useState(''), [mode, setMode] = useState(null)
  const [busy, setBusy] = useState(false), [error, setError] = useState('')
  const refresh = message => { setMode(null); setError(''); resource.reload(); history.reload(); onChanged(message) }
  async function decide() {
    setBusy(true); setError('')
    try {
      if (confirmation === 'approve') await api.approve(id, Number(responsible))
      else if (confirmation === 'separate') await api.separate(id, Number(responsible))
      else await api.reject(id)
      refresh(confirmation === 'approve' ? 'Solicitação aprovada. Saldo preservado.' : confirmation === 'separate' ? 'Separação iniciada. Saldo preservado.' : 'Solicitação rejeitada.')
    } catch (cause) { setError(cause.message); resource.reload(); onChanged('') }
    finally { setBusy(false); setConfirmation('') }
  }
  return <Modal title={`Solicitação #${id}`} onClose={onClose} busy={busy}><Notice error>{error}</Notice><ResourceView resource={resource}>{s => mode ?
    <ConfirmedOperation request={s} mode={mode} people={people} onCancel={() => setMode(null)} onBusy={setBusy} onSaved={refresh}/> : <>
    <div className="detail-grid"><div><small>Solicitante</small><strong>{s.solicitante?.nome || '—'}</strong></div><div><small>Almoxarifado</small><strong>{s.almoxarifado?.nome || '—'}</strong></div><div><small>Data</small><strong>{dateTime(s.dataSolicitacao)}</strong></div><div><small>Status</small><Badge value={s.status}/></div></div>
    <Notice error={s.compatibilidadeLegada === 'INCONSISTENTE'}>{s.aviso}</Notice>
    {s.responsavelAprovacao && <p className="stock-summary">Aprovada por {s.responsavelAprovacao.nome} · {dateTime(s.dataAprovacao)}</p>}
    {s.responsavelSeparacao && <p className="stock-summary">Separação: {s.responsavelSeparacao.nome} · {dateTime(s.dataSeparacao)}</p>}
    <DataTable rows={s.itens} columns={[
      { key: 'codigo', label: 'Código', render: i => <span className="code">{i.produto?.codigo || '—'}</span> },
      { key: 'material', label: 'Material', render: i => i.produto?.nome }, { key: 'unidade', label: 'Un.', render: i => unitLabel(i.produto) },
      { key: 'solicitada', label: 'Solicitado', render: i => amount(i.quantidadeSolicitada) },
      { key: 'atendida', label: 'Atendido', render: i => amount(i.quantidadeAtendida) }, { key: 'pendente', label: 'Pendente', render: i => amount(i.quantidadePendente) },
      { key: 'saldo', label: 'Saldo atual', render: i => amount(i.saldoAtual) }, { key: 'falta', label: 'Falta atual', render: i => amount(i.quantidadeFaltante) },
      { key: 'necessidade', label: 'Necessidade', render: i => i.necessidadeCompraId ? <Link to={`/necessidades-compra?solicitacaoId=${id}`}>#{i.necessidadeCompraId}</Link> : i.quantidadeFaltante > 0 && !['PENDENTE', 'REJEITADA', 'ATENDIDA'].includes(s.status) && s.compatibilidadeLegada !== 'INCONSISTENTE' ? <Can permission="NECESSIDADE_COMPRA_CRIAR"><button className="btn text" onClick={() => setMode({ type: 'need', item: i })}>Gerar necessidade</button></Can> : '—' }
    ]}/>
    <p className="stock-summary">Pendente é a quantidade ainda não entregue. Falta atual considera o saldo disponível; itens repetidos compartilham esse saldo. A separação não reserva estoque.</p>
    {s.compatibilidadeLegada !== 'INCONSISTENTE' && ['PENDENTE', 'APROVADA'].includes(s.status) && (s.status === 'PENDENTE' ? (auth.can('SOLICITACAO_APROVAR') || auth.can('SOLICITACAO_REJEITAR')) : auth.can('SOLICITACAO_SEPARAR')) && <section className="decision"><h3>{s.status === 'PENDENTE' ? 'Decisão da solicitação' : 'Separação'}</h3><p>{s.status === 'PENDENTE' ? 'A aprovação autoriza a demanda. A saída ocorre somente ao confirmar um atendimento.' : 'Inicie a conferência dos materiais antes de registrar a entrega.'}</p>
      {people.error ? <Notice error>{people.error.message}</Notice> : <Field label={s.status === 'PENDENTE' ? 'Responsável pela aprovação' : 'Responsável pela separação'}><select disabled={busy || people.loading} value={responsible} onChange={e => setResponsible(e.target.value)}><option value="">Selecionar funcionário</option>{options(people.data)}</select></Field>}
      {confirmation ? <div className="confirmation" role="alert"><strong>{confirmation === 'approve' ? 'Confirmar aprovação da solicitação?' : confirmation === 'separate' ? 'Confirmar início da separação?' : 'Confirmar rejeição da solicitação?'}</strong><p>Esta operação não altera o saldo do estoque.</p><div className="form-actions"><button className="btn secondary" disabled={busy} onClick={() => setConfirmation('')}>Voltar</button><button className={confirmation === 'reject' ? 'btn danger' : 'btn'} disabled={busy} onClick={decide}>{busy ? 'Processando…' : 'Confirmar operação'}</button></div></div> : <div className="form-actions">{s.status === 'PENDENTE' && <Can permission="SOLICITACAO_REJEITAR"><button className="btn danger" disabled={busy} onClick={() => setConfirmation('reject')}>Rejeitar</button></Can>}<Can permission={s.status === 'PENDENTE' ? 'SOLICITACAO_APROVAR' : 'SOLICITACAO_SEPARAR'}><button className="btn" disabled={busy || !responsible || people.loading || !!people.error} onClick={() => setConfirmation(s.status === 'PENDENTE' ? 'approve' : 'separate')}>{s.status === 'PENDENTE' ? 'Aprovar solicitação' : 'Iniciar separação'}</button></Can></div>}
    </section>}
    <div className="form-actions operation-links">{['EM_SEPARACAO', 'PARCIALMENTE_ATENDIDA'].includes(s.status) && s.compatibilidadeLegada !== 'INCONSISTENTE' && <Link className="btn secondary" to={`/solicitacoes/${id}/lista-separacao`}>Lista de separação</Link>}<Link className="btn secondary" to={`/movimentacoes?solicitacaoId=${id}`}>Ver movimentações</Link>{canFulfill(s) && <Can permission="SOLICITACAO_ATENDER"><button className="btn" onClick={() => setMode({ type: 'fulfill' })}>Registrar atendimento</button></Can>}</div>
    <section className="decision"><h3>Histórico de atendimentos</h3><ResourceView resource={history}>{rows => rows.length ? rows.map(a => <section key={a.id} className="fulfillment-history"><h4>Atendimento #{a.id} · {dateTime(a.dataHora)}</h4><p>Responsável: {a.responsavel?.nome}</p><DataTable rows={a.itens} columns={[{ key: 'material', label: 'Material', render: i => i.produto?.nome }, { key: 'quantidade', label: 'Entregue', render: i => `${quantity(i.quantidade)} ${unitLabel(i.produto)}` }]}/></section>) : <p>Nenhum atendimento registrado neste fluxo.</p>}</ResourceView></section>
  </>}</ResourceView></Modal>
}
function ConfirmedOperation({ request, mode, people, onCancel, onBusy, onSaved }) {
  const items = request.itens.filter(i => i.quantidadePendente > 0)
  const [amounts, setAmounts] = useState(() => suggestedAmounts(items)), [responsible, setResponsible] = useState(''), [errors, setErrors] = useState({}), [error, setError] = useState('')
  const [confirming, setConfirming] = useState(false), [busy, setBusy] = useState(false), [uncertain, setUncertain] = useState(false)
  const key = useRef(crypto.randomUUID()), payload = useRef(null), sending = useRef(false)
  const need = mode.type === 'need'
  useEffect(() => {
    if (!busy && !uncertain) return
    const guard = event => { event.preventDefault(); event.returnValue = '' }
    window.addEventListener('beforeunload', guard)
    return () => window.removeEventListener('beforeunload', guard)
  }, [busy, uncertain])
  function review(e) {
    e.preventDefault()
    const result = need ? { errors: responsible ? {} : { responsible: 'Selecione o responsável.' }, body: { itemSolicitacaoId: mode.item.id, responsavelId: Number(responsible) } } : fulfillmentDraft(items, amounts, responsible)
    setErrors(result.errors)
    if (!Object.keys(result.errors).length) { payload.current = result.body; setConfirming(true); setError('') }
  }
  async function send() {
    if (sending.current) return
    sending.current = true; setBusy(true); onBusy(true); setError(''); let unknown = false
    try {
      const result = need ? await api.createPurchaseNeed(payload.current, key.current) : await api.fulfill(request.id, payload.current, key.current)
      setUncertain(false); onSaved(need ? `Necessidade #${result.id} registrada.` : `Atendimento #${result.id} confirmado.`)
    } catch (cause) {
      unknown = !cause.status || cause.status >= 500
      setUncertain(unknown); setError(unknown ? 'A resposta não foi confirmada. Confira o resultado para evitar repetir a operação.' : cause.message)
      if (!unknown) setConfirming(false)
    } finally { sending.current = false; setBusy(false); onBusy(unknown) }
  }
  return <form className="operational-form" onSubmit={review}><h3>{need ? 'Gerar necessidade de compra' : 'Registrar atendimento'}</h3><Notice error>{error || errors.form}</Notice>
    {need ? <><div className="detail-grid"><div><small>Solicitação</small><strong>#{request.id}</strong></div><div><small>Almoxarifado</small><strong>{request.almoxarifado?.nome}</strong></div><div><small>Pendente</small><strong>{quantity(mode.item.quantidadePendente)}</strong></div><div><small>Saldo atual</small><strong>{quantity(mode.item.saldoAtual)}</strong></div></div><p>{mode.item.produto?.nome} · Falta atual: <strong>{quantity(mode.item.quantidadeFaltante)} {unitLabel(mode.item.produto)}</strong>. A quantidade será conferida novamente pelo servidor na confirmação.</p></> : <><p>Confira as quantidades físicas. A sugestão pode ser alterada; zero deixa o item para outro atendimento.</p><div className="fulfillment-items">{items.map(i => <div className="fulfillment-line" key={i.id}><div><strong>{i.produto?.nome}</strong><small>{i.produto?.codigo || '—'} · {unitLabel(i.produto)} · Pendente {quantity(i.quantidadePendente)} · Saldo {quantity(i.saldoAtual)}</small></div><Field label={`Entregar item #${i.id}`} error={errors[i.id]}><input type="number" min="0" step={i.produto?.unidadeMedidaConfigurada?.permiteFracionamento === false ? '1' : 'any'} disabled={busy || confirming || uncertain} value={amounts[i.id]} onChange={e => setAmounts({ ...amounts, [i.id]: e.target.value })}/></Field></div>)}</div></>}
    <Field label={need ? 'Responsável pela necessidade' : 'Responsável pelo atendimento'} error={errors.responsible}><select value={responsible} disabled={busy || confirming || uncertain || people.loading || !!people.error} onChange={e => setResponsible(e.target.value)}><option value="">Selecionar funcionário</option>{options(people.data)}</select></Field><Notice error>{people.error?.message}</Notice>
    {confirming && <div className="confirmation" role="alert"><strong>{need ? 'Confirmar necessidade de compra?' : 'Confirmar atendimento?'}</strong><p>{need ? 'Esta operação registra a falta para acompanhamento, sem efetuar uma compra ou alterar estoque.' : 'Esta operação dará saída do estoque nas quantidades informadas e criará histórico de atendimento.'}</p>{!need && <ul>{payload.current?.itens.map(i => <li key={i.itemSolicitacaoId}>{items.find(row => row.id === i.itemSolicitacaoId)?.produto?.nome}: {quantity(i.quantidade)}</li>)}</ul>}</div>}
    <div className="form-actions">{!uncertain && <button type="button" className="btn secondary" disabled={busy} onClick={() => confirming ? setConfirming(false) : onCancel()}>{confirming ? 'Voltar' : 'Cancelar'}</button>}{confirming ? <button type="button" className="btn" disabled={busy} onClick={send}>{busy ? 'Processando…' : uncertain ? 'Conferir resultado' : need ? 'Confirmar necessidade' : 'Confirmar atendimento'}</button> : <button className="btn" disabled={busy || people.loading || !!people.error}>Revisar {need ? 'necessidade' : 'atendimento'}</button>}</div>
  </form>
}
