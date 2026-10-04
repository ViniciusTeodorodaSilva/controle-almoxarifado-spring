import { Link, useSearchParams } from 'react-router-dom'
import StockLimitsForm from './StockLimitsForm'
import { stockSituation } from '../utils/stockIntelligence'
import { useCallback, useState } from 'react'
import { Eye, Plus, RefreshCw, Search } from 'lucide-react'
import { api } from '../api/client'
import { useResource } from '../hooks/useResource'
import { PageHeader, Card, ResourceView, DataTable, Badge, Modal, Field, Notice, quantity, dateTime } from '../components/ui'
import RequestForm from './RequestForm'
import StockMovementForm from './StockMovementForm'
import { filterRequests, filterStocks, filterMovements, unitLabel } from '../utils/operations'
function nameOptions(rows) { return (rows || []).map(row => <option key={row.id} value={row.id}>{row.nome}</option>) }
function SearchInput({ label, placeholder, value, onChange }) {
  return <div className="search-input"><Search size={17}/><input type="search" aria-label={label} placeholder={placeholder} value={value} onChange={event => onChange(event.target.value)}/></div>
}
export function Stocks() {
  const [product, setProduct] = useState(''), [warehouse, setWarehouse] = useState(''), [term, setTerm] = useState('')
  const [operation, setOperation] = useState(null), [notice, setNotice] = useState(''), [limits, setLimits] = useState(null)
  const [params, setParams] = useSearchParams()
  const attention = params.get('atencao') === 'true'
  const alerts = useResource(useCallback(signal => api.stockAlerts(null, signal), []))
  const resource = useResource(useCallback(signal => api.list('estoques', null, signal), []))
  const refs = useResource(useCallback(signal => Promise.all(['produtos', 'almoxarifados'].map(name => api.list(name, null, signal))), []))
  return <><PageHeader eyebrow="OPERAÇÃO" title="Estoque" description="Saldos disponíveis por produto e almoxarifado.">
    <button className="btn secondary" onClick={resource.reload} aria-label="Atualizar estoque"><RefreshCw size={16}/><span className="refresh-label">Atualizar</span></button>
    <button className="btn" onClick={() => { setNotice(''); setOperation('entrada') }}><Plus size={16}/>Registrar entrada</button>
    <button className="btn secondary" onClick={() => { setNotice(''); setOperation('saida') }}>Registrar saída</button>
  </PageHeader><Notice>{notice}</Notice><Card><div className="filters">
    <SearchInput label="Pesquisar estoque" placeholder="Código, nome ou descrição do produto…" value={term} onChange={setTerm}/>
    <select aria-label="Filtrar produto" value={product} onChange={event => setProduct(event.target.value)}><option value="">Todos os produtos</option>{nameOptions(refs.data?.[0])}</select>
    <select aria-label="Filtrar almoxarifado" value={warehouse} onChange={event => setWarehouse(event.target.value)}><option value="">Todos os almoxarifados</option>{nameOptions(refs.data?.[1])}</select>
    <select aria-label="Filtrar situação" value={attention ? 'attention' : ''} onChange={event => setParams(event.target.value ? { atencao: 'true' } : {})}><option value="">Todos os estoques</option><option value="attention">Precisam de atenção</option></select>
  </div>{attention && <p className="stock-summary">Saldo no mínimo ou abaixo dele · reposição sugerida até o máximo, quando configurado.</p>}{refs.error && <Notice error>Filtros indisponíveis: {refs.error.message}</Notice>}
  <ResourceView resource={attention ? { ...resource, loading: resource.loading || alerts.loading, error: resource.error || alerts.error, reload: () => { resource.reload(); alerts.reload() } } : resource}>{rows => <DataTable rows={filterStocks(rows, { term, product, warehouse }).filter(stock => !attention || alerts.data?.some(alert => alert.estoqueId === stock.id))} columns={[
    { key: 'codigo', label: 'Código', render: stock => <span className="code">{stock.produto?.codigo || '—'}</span> },
    { key: 'produto', label: 'Produto', render: stock => <strong>{stock.produto?.nome}</strong> },
    { key: 'almoxarifado', label: 'Almoxarifado', render: stock => stock.almoxarifado?.nome },
    { key: 'saldo', label: 'Saldo disponível', render: stock => <strong className="numeric">{quantity(stock.quantidade)}</strong> },
    { key: 'unidade', label: 'Unidade', render: stock => unitLabel(stock.produto) },
    { key: 'minimo', label: 'Mínimo', render: stock => stock.estoqueMinimo == null ? '—' : quantity(stock.estoqueMinimo) },
    { key: 'maximo', label: 'Máximo', render: stock => stock.estoqueMaximo == null ? '—' : quantity(stock.estoqueMaximo) },
    { key: 'situacao', label: 'Situação', render: stock => <Badge value={stockSituation(stock)}/> },
    ...(attention ? [{ key: 'reposicao', label: 'Reposição sugerida', render: stock => { const value = alerts.data?.find(alert => alert.estoqueId === stock.id)?.quantidadeSugerida; return value == null ? '—' : quantity(value) } }] : []),
    { key: 'acoes', label: 'Ações', render: stock => <button className="btn text" onClick={() => { setNotice(''); setLimits(stock) }}>Configurar limites</button> }
  ]}/>}</ResourceView></Card>
  {limits && <StockLimitsForm stock={limits} onClose={() => setLimits(null)} onSaved={message => { setLimits(null); setNotice(message); resource.reload(); alerts.reload() }}/>}
  {operation && <StockMovementForm type={operation} onClose={() => setOperation(null)} onSaved={message => { setOperation(null); setNotice(message); resource.reload(); alerts.reload() }} onChanged={() => { resource.reload(); alerts.reload() }}/>}
  </>
}
export function Requests() {
  const [status, setStatus] = useState(''), [term, setTerm] = useState(''), [selected, setSelected] = useState(null), [creating, setCreating] = useState(false), [notice, setNotice] = useState('')
  const resource = useResource(useCallback(signal => api.requests(status, signal), [status]))
  return <><PageHeader eyebrow="OPERAÇÃO" title="Solicitações" description="Acompanhe demandas, confira itens e decida o atendimento.">
    <button className="btn secondary" onClick={() => { resource.reload(); alerts.reload() }}><RefreshCw size={16}/>Atualizar</button>
    <button className="btn" onClick={() => { setNotice(''); setCreating(true) }}><Plus size={16}/>Nova solicitação</button>
  </PageHeader><Notice>{notice}</Notice><Card><div className="filters">
    <SearchInput label="Pesquisar solicitações" placeholder="Número, solicitante, almoxarifado ou material…" value={term} onChange={setTerm}/>
    <select aria-label="Filtrar status" value={status} onChange={event => setStatus(event.target.value)}><option value="">Todos os status</option>{['PENDENTE', 'APROVADA', 'REJEITADA'].map(value => <option key={value}>{value}</option>)}</select>
  </div><ResourceView resource={resource}>{rows => <DataTable rows={filterRequests(rows, term).sort((a, b) => b.id - a.id)} columns={[
    { key: 'id', label: 'Solicitação', render: row => <strong>#{row.id}</strong> },
    { key: 'solicitante', label: 'Solicitante', render: row => row.solicitante?.nome },
    { key: 'almoxarifado', label: 'Almoxarifado', render: row => row.almoxarifado?.nome },
    { key: 'data', label: 'Data', render: row => dateTime(row.dataSolicitacao) },
    { key: 'status', label: 'Status', render: row => <Badge value={row.status}/> },
    { key: 'action', label: 'Ações', render: row => <button className="btn text" onClick={() => setSelected(row.id)}><Eye size={16}/>Detalhes</button> }
  ]}/>}</ResourceView></Card>
  {creating && <RequestForm onClose={() => { setCreating(false); resource.reload() }} onPartial={progress => { setNotice(progress.id ? `Solicitação #${progress.id} criada com ${progress.completed} itens confirmados. Confira antes de iniciar outra.` : 'Confira a listagem: o resultado do envio não foi confirmado.'); resource.reload() }} onOpen={id => { setCreating(false); setSelected(id) }} onSaved={id => { setCreating(false); setSelected(id); setNotice(`Solicitação #${id} criada com sucesso.`); resource.reload() }}/>}
  {selected && <RequestDetails id={selected} onClose={() => setSelected(null)} onChanged={message => { setNotice(message); resource.reload() }}/>}</>
}
function RequestDetails({id,onClose,onChanged}){
 const resource=useResource(useCallback(()=>api.get('solicitacoes',id),[id]))
 const people=useResource(useCallback(signal=>api.list('funcionarios',null,signal),[]))
 const [responsible,setResponsible]=useState(''),[confirmation,setConfirmation]=useState(''),[busy,setBusy]=useState(false),[error,setError]=useState('')
 async function decide(){setBusy(true);setError('');try{if(confirmation==='approve')await api.approve(id,Number(responsible));else await api.reject(id);setConfirmation('');resource.reload();onChanged(confirmation==='approve'?'Solicitação aprovada. Estoque atualizado.':'Solicitação rejeitada.')}catch(error){setError(error.message);setConfirmation('');resource.reload();onChanged('')}finally{setBusy(false)}}
 return <Modal title={`Solicitação #${id}`} onClose={onClose} busy={busy}><Notice error>{error}</Notice><ResourceView resource={resource}>{s=><><div className="detail-grid"><div><small>Solicitante</small><strong>{s.solicitante?.nome||'—'}</strong></div><div><small>Almoxarifado</small><strong>{s.almoxarifado?.nome||'—'}</strong></div><div><small>Data</small><strong>{dateTime(s.dataSolicitacao)}</strong></div><div><small>Status</small><Badge value={s.status}/></div></div><DataTable rows={s.itens||[]} columns={[{key:'codigo',label:'Código',render:item=><span className="code">{item.produto?.codigo||'—'}</span>},{key:'produto',label:'Material',render:item=>item.produto?.nome},{key:'quantidade',label:'Quantidade',render:item=>quantity(item.quantidade)},{key:'unidade',label:'Unidade',render:item=>item.produto?.unidadeMedidaConfigurada?.sigla||item.produto?.unidadeMedida||'—'}]}/>
 {s.status==='PENDENTE'&&<div className="decision"><h3>Decisão da solicitação</h3><p>A aprovação retira do estoque as quantidades de todos os itens. Selecione o responsável para continuar.</p>{people.error?<Notice error>{people.error.message}</Notice>:<Field label="Responsável pela aprovação"><select value={responsible} onChange={e=>setResponsible(e.target.value)} disabled={busy||people.loading}><option value="">Selecionar funcionário</option>{nameOptions(people.data)}</select></Field>}
 {confirmation?<div className="confirmation" role="alert"><strong>{confirmation==='approve'?'Confirmar aprovação e saída do estoque?':'Confirmar rejeição da solicitação?'}</strong><div className="form-actions"><button className="btn secondary" disabled={busy} onClick={()=>setConfirmation('')}>Voltar</button><button className={confirmation==='approve'?'btn':'btn danger'} disabled={busy} onClick={decide}>{busy?'Processando…':'Confirmar operação'}</button></div></div>:<div className="form-actions"><button className="btn danger" disabled={busy} onClick={()=>setConfirmation('reject')}>Rejeitar</button><button className="btn" disabled={busy||!responsible||!!people.error||people.loading} onClick={()=>setConfirmation('approve')}>Aprovar solicitação</button></div>}</div>}
 </>}</ResourceView></Modal>
}
export function Movements() {
  const [params] = useSearchParams(), transferId = params.get('transferenciaId')
  const [type, setType] = useState(''), [product, setProduct] = useState(''), [warehouse, setWarehouse] = useState(''), [term, setTerm] = useState(''), [from, setFrom] = useState(''), [to, setTo] = useState('')
  const resource = useResource(useCallback(signal => transferId ? api.transferMovements(transferId, signal) : api.list(type ? 'movimentacoes/tipo/' + type : 'movimentacoes', null, signal), [type, transferId]))
  const refs = useResource(useCallback(signal => Promise.all(['produtos', 'almoxarifados'].map(name => api.list(name, null, signal))), []))
  const dateError = from && to && from > to ? 'A data inicial deve ser anterior ou igual à data final.' : ''
  return <><PageHeader eyebrow="RASTREABILIDADE" title="Movimentações" description={transferId ? `Movimentações da transferência #${transferId}.` : "Histórico de entradas, saídas e alterações de saldo."}><button className="btn secondary" onClick={resource.reload}><RefreshCw size={16}/>Atualizar</button></PageHeader><Card><div className="filters">
    <SearchInput label="Pesquisar movimentações" placeholder="Código, material, pessoa ou solicitação…" value={term} onChange={setTerm}/>
    <select aria-label="Filtrar tipo" value={type} onChange={event => setType(event.target.value)}><option value="">Todos os tipos</option><option value="ENTRADA">Entradas</option><option value="SAIDA">Saídas</option></select>
    <select aria-label="Filtrar produto" value={product} onChange={event => setProduct(event.target.value)}><option value="">Todos os produtos</option>{nameOptions(refs.data?.[0])}</select>
    <select aria-label="Filtrar almoxarifado" value={warehouse} onChange={event => setWarehouse(event.target.value)}><option value="">Todos os almoxarifados</option>{nameOptions(refs.data?.[1])}</select>
    <div className="period-filter"><Field label="De"><input type="date" value={from} onChange={event => setFrom(event.target.value)}/></Field><Field label="Até"><input type="date" value={to} onChange={event => setTo(event.target.value)}/></Field></div>
  </div><Notice error>{dateError || (refs.error ? 'Filtros indisponíveis: ' + refs.error.message : '')}</Notice>
  <ResourceView resource={resource}>{rows => <DataTable rows={dateError ? [] : filterMovements(rows, { term, product, warehouse, from, to }).filter(row => !type || row.tipo === type).sort((a, b) => b.id - a.id)} columns={[
    { key: 'tipo', label: 'Tipo', render: row => <Badge value={row.tipo}/> },
    { key: 'codigo', label: 'Código', render: row => <span className="code">{row.produto?.codigo || '—'}</span> },
    { key: 'produto', label: 'Material', render: row => row.produto?.nome },
    { key: 'local', label: 'Almoxarifado', render: row => row.almoxarifado?.nome },
    { key: 'quantidade', label: 'Quantidade', render: row => quantity(row.quantidade) },
    { key: 'saldoAnterior', label: 'Saldo anterior', render: row => quantity(row.saldoAnterior) },
    { key: 'saldoPosterior', label: 'Saldo posterior', render: row => quantity(row.saldoPosterior) },
    { key: 'data', label: 'Data / hora', render: row => dateTime(row.dataHora) },
    { key: 'solicitante', label: 'Solicitante', render: row => row.solicitante?.nome || '—' },
    { key: 'responsavel', label: 'Responsável', render: row => row.responsavel?.nome || '—' },
    { key: 'solicitacaoId', label: 'Solicitação' },
    { key: 'transferencia', label: 'Transferência', render: row => row.transferenciaId ? <Link to={`/transferencias?transferenciaId=${row.transferenciaId}`}>#{row.transferenciaId}</Link> : '—' }
  ]}/>}</ResourceView></Card></>
}
