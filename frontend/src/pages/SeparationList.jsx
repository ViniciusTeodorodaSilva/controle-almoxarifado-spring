import { ContextDisplay } from '../components/ContextSelector'
import { useCallback, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '../api/client'
import { useResource } from '../hooks/useResource'
import { ResourceView, Notice, quantity, dateTime, Badge } from '../components/ui'
import { unitLabel } from '../utils/operations'
import OperationalDocument from '../components/OperationalDocument'
export default function SeparationList() {
  const { id } = useParams()
  const resource = useResource(useCallback(signal => api.requestOperation(id, signal), [id]))
  const [generated] = useState(() => dateTime(new Date().toISOString()))
  return <main className="document-view"><nav className="document-actions" aria-label="Ações do documento"><Link className="btn secondary" to={`/solicitacoes?solicitacaoId=${id}`}>Voltar à solicitação</Link><button className="btn" disabled={resource.loading || !!resource.error || resource.data?.compatibilidadeLegada === 'INCONSISTENTE'} onClick={() => window.print()}>Imprimir lista</button></nav><ResourceView resource={resource}>{s => s.compatibilidadeLegada === 'INCONSISTENTE' ? <Notice error>{s.aviso}</Notice> : <OperationalDocument title="Lista de Separação" reference={`Solicitação #${s.id}`} generatedAt={generated} metadata={[
    ['Contexto operacional', <ContextDisplay value={s.contexto}/>], ['Solicitante', s.solicitante?.nome], ['Almoxarifado', s.almoxarifado?.nome], ['Solicitada em', dateTime(s.dataSolicitacao)], ['Status', <Badge value={s.status}/>], ['Aprovação', s.responsavelAprovacao ? `${s.responsavelAprovacao.nome} · ${dateTime(s.dataAprovacao)}` : '—'], ['Responsável pela separação', s.responsavelSeparacao ? `${s.responsavelSeparacao.nome} · ${dateTime(s.dataSeparacao)}` : '—']
  ]}><p className="document-note">Conferência dos materiais pendentes. Esta lista não confirma entrega nem altera estoque.</p><div className="document-table-scroll"><table className="document-table"><thead><tr>{['Conf.', 'Código / material', 'Un.', 'Solicitado', 'Atendido', 'Pendente', 'Conferência'].map(h => <th key={h} scope="col">{h}</th>)}</tr></thead><tbody>{s.itens.map(i => <tr key={i.id}><td><span className="check-box" aria-label="Campo para conferência"/></td><td><small>{i.produto?.codigo || '—'}</small><strong>{i.produto?.nome}</strong></td><td>{unitLabel(i.produto)}</td><td>{quantity(i.quantidadeSolicitada)}</td><td>{quantity(i.quantidadeAtendida)}</td><td>{quantity(i.quantidadePendente)}</td><td className="checking-space"/></tr>)}</tbody></table></div><div className="document-checking"><p>Conferido por: <span/></p><p>Data / hora: <span/></p><p>Observações: <span/></p></div><p className="document-reference"><Link to={`/solicitacoes?solicitacaoId=${id}`}>Consultar solicitação #{id} na Plataforma BES</Link></p></OperationalDocument>}</ResourceView></main>
}
