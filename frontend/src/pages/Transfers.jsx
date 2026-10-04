import { useCallback, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { Eye, Plus, RefreshCw, Search } from 'lucide-react'
import { api } from '../api/client'
import { useResource } from '../hooks/useResource'
import { PageHeader, Card, ResourceView, DataTable, Badge, Modal, Notice, quantity, dateTime } from '../components/ui'
import TransferForm from './TransferForm'
import { filterTransfers } from '../utils/stockIntelligence'
export default function Transfers() {
  const [origin, setOrigin] = useState(''), [destination, setDestination] = useState(''), [product, setProduct] = useState(''), [term, setTerm] = useState('')
  const [creating, setCreating] = useState(false), [notice, setNotice] = useState('')
  const [params, setParams] = useSearchParams(), selected = params.get('transferenciaId')
  const resource = useResource(useCallback(signal => api.transfers({ origemId: origin, destinoId: destination, produtoId: product }, signal), [origin, destination, product]))
  const refs = useResource(useCallback(signal => Promise.all(['almoxarifados', 'produtos'].map(name => api.list(name, null, signal))), []))
  const options = rows => (rows || []).map(row => <option key={row.id} value={row.id}>{row.codigo ? row.codigo + ' · ' : ''}{row.nome}</option>)
  return <><PageHeader eyebrow="OPERAÇÃO" title="Transferências" description="Movimentações confirmadas entre almoxarifados, com rastreabilidade por item.">
    <button className="btn secondary" onClick={resource.reload}><RefreshCw size={16}/>Atualizar</button><button className="btn" onClick={() => { setNotice(''); setCreating(true) }}><Plus size={16}/>Nova transferência</button>
  </PageHeader><Notice>{notice}</Notice><Card><div className="filters">
    <div className="search-input"><Search size={17}/><input type="search" aria-label="Pesquisar transferências" placeholder="Número, material, local ou responsável…" value={term} onChange={event => setTerm(event.target.value)}/></div>
    <select aria-label="Filtrar origem" value={origin} onChange={event => setOrigin(event.target.value)}><option value="">Todas as origens</option>{options(refs.data?.[0])}</select>
    <select aria-label="Filtrar destino" value={destination} onChange={event => setDestination(event.target.value)}><option value="">Todos os destinos</option>{options(refs.data?.[0])}</select>
    <select aria-label="Filtrar produto" value={product} onChange={event => setProduct(event.target.value)}><option value="">Todos os produtos</option>{options(refs.data?.[1])}</select>
  </div>{refs.error && <Notice error>Filtros indisponíveis: {refs.error.message}</Notice>}
  <ResourceView resource={resource}>{rows => <DataTable empty="Nenhuma transferência encontrada." rows={filterTransfers(rows, term)} columns={[
    { key: 'id', label: 'Transferência', render: row => <strong>#{row.id}</strong> },
    { key: 'origem', label: 'Origem', render: row => row.almoxarifadoOrigem.nome },
    { key: 'destino', label: 'Destino', render: row => row.almoxarifadoDestino.nome },
    { key: 'responsavel', label: 'Responsável', render: row => row.responsavel.nome },
    { key: 'itens', label: 'Produtos', render: row => row.itens.length },
    { key: 'data', label: 'Data / hora', render: row => dateTime(row.dataHora) },
    { key: 'status', label: 'Status', render: row => <Badge value={row.status}/> },
    { key: 'acoes', label: 'Ações', render: row => <button className="btn text" onClick={() => setParams({ transferenciaId: row.id })}><Eye size={16}/>Detalhes</button> }
  ]}/>}</ResourceView></Card>
  {creating && <TransferForm onClose={() => setCreating(false)} onChanged={resource.reload} onSaved={result => { setCreating(false); setNotice(`Transferência #${result.id} concluída com sucesso.`); resource.reload(); setParams({ transferenciaId: result.id }) }}/>}
  {selected && <TransferDetails id={selected} onClose={() => setParams({})}/>}
  </>
}
// The read-only DTO also supplies future transfer receipt/romaneio rendering.
// Document generation and signatures will be separate actions after their rules are defined.
function TransferDetails({ id, onClose }) {
  const resource = useResource(useCallback(() => api.get('transferencias', id), [id]))
  return <Modal title={`Transferência #${id}`} onClose={onClose}><ResourceView resource={resource}>{record => <>
    <dl className="detail-grid"><div><dt>Origem</dt><dd>{record.almoxarifadoOrigem.nome}</dd></div><div><dt>Destino</dt><dd>{record.almoxarifadoDestino.nome}</dd></div><div><dt>Responsável</dt><dd>{record.responsavel.nome}</dd></div><div><dt>Data / hora</dt><dd>{dateTime(record.dataHora)}</dd></div><div><dt>Status</dt><dd><Badge value={record.status}/></dd></div></dl>
    {record.observacao && <p className="transfer-observation">{record.observacao}</p>}
    <DataTable rows={record.itens} columns={[{key:'codigo',label:'Código'},{key:'produto',label:'Material'},{key:'unidade',label:'Unidade'},{key:'quantidade',label:'Quantidade',render:item=>quantity(item.quantidade)}]}/>
    <div className="form-actions transfer-detail-actions"><button className="btn secondary" onClick={onClose}>Fechar</button><Link className="btn" to={`/movimentacoes?transferenciaId=${record.id}`}>Ver movimentações</Link></div>
  </>}</ResourceView></Modal>
}
