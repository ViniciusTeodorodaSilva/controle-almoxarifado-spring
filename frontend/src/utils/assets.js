export const conditions = ['NOVO', 'BOM', 'REGULAR', 'DANIFICADO', 'INOPERANTE']
export const assetLabels = { DISPONIVEL:'Disponível', EMPRESTADO:'Emprestado', EM_TRANSFERENCIA:'Em trânsito', INDISPONIVEL:'Indisponível', BAIXADO:'Baixado', NOVO:'Novo', BOM:'Bom', REGULAR:'Regular', DANIFICADO:'Danificado', INOPERANTE:'Inoperante', APROVADO:'Aprovado', APROVADO_COM_RESSALVA:'Aprovado com ressalva', REPROVADO:'Reprovado', EMPRESTIMO:'Empréstimo', DEVOLUCAO:'Devolução', TRANSFERENCIA:'Transferência', RECEBIMENTO:'Chegada', INSPECAO:'Inspeção', INATIVACAO:'Inativação', REATIVACAO:'Reativação', BAIXA:'Baixa' }
export const assetLabel = value => assetLabels[value] || value || '—'
export function assetLocation(asset, warehouses = []) {
 if (asset?.almoxarifadoId) return warehouses.find(w=>String(w.id)===String(asset.almoxarifadoId))?.nome || `Almoxarifado #${asset.almoxarifadoId}`
 if (asset?.contexto?.obraId) return `Obra: ${asset.contexto.obraNome || '#'+asset.contexto.obraId}`
 return 'Local não informado'
}
export const assetStatuses = ['DISPONIVEL', 'EMPRESTADO', 'EM_TRANSFERENCIA', 'INDISPONIVEL', 'BAIXADO']
export const assetKinds = {
 ativos: { title: 'Ferramentas e equipamentos', read: 'ATIVO_LER', manage: 'ATIVO_GERENCIAR' },
 emprestimos: { title: 'Empréstimos', read: 'EMPRESTIMO_LER', manage: 'EMPRESTIMO_GERENCIAR' },
 'transferencias-ativos': { title: 'Transferências de ativos', read: 'TRANSFERENCIA_ATIVO_LER', manage: 'TRANSFERENCIA_ATIVO_GERENCIAR' },
 'inspecoes-ativos': { title: 'Inspeções', read: 'INSPECAO_ATIVO_LER', manage: 'INSPECAO_ATIVO_GERENCIAR' },
}
export function assetActions(a, can) {
 if (!a || a.status === 'BAIXADO') return []
 const actions = []
 if (can('ATIVO_GERENCIAR')) actions.push('editar')
 if (a.pendenciaId) {
  if (a.status === 'EMPRESTADO' && can('EMPRESTIMO_GERENCIAR')) actions.push('devolver')
  if (a.status === 'EM_TRANSFERENCIA' && can('TRANSFERENCIA_ATIVO_GERENCIAR')) actions.push('receber')
 } else {
  if (a.ativo && a.status === 'DISPONIVEL' && !a.reprovado && !a.inspecaoPendente && !['DANIFICADO','INOPERANTE'].includes(a.condicao) && can('EMPRESTIMO_GERENCIAR')) actions.push('emprestar')
  if (a.ativo && can('TRANSFERENCIA_ATIVO_GERENCIAR')) actions.push('transferir')
  if (can('ATIVO_GERENCIAR')) actions.push(a.ativo ? 'inativar' : 'reativar', 'baixar')
 }
 if (a.status !== 'EM_TRANSFERENCIA' && can('INSPECAO_ATIVO_GERENCIAR')) actions.push('inspecionar')
 return actions
}
export function operationBody(form) {
 const body = { ...form }
 for (const key of Object.keys(body)) {
  if (key.endsWith('Id')) body[key] = body[key] === '' || body[key] == null ? null : Number(body[key])
  else if (['previsaoDevolucao','proximaInspecao','dataAquisicao'].includes(key) && !body[key]) body[key] = null
 }
 if (body.contexto) {
  body.contexto = Object.fromEntries(['obraId','ordemServicoId','centroCustoId'].map(k=>[k,body.contexto[k]===''||body.contexto[k]==null?null:Number(body.contexto[k])]))
  if (Object.values(body.contexto).every(v=>v==null)) body.contexto = null
 }
 return body
}
export function assetOperation(action, assetId, pendingId) {
 const paths = { emprestar:'/emprestimos', transferir:'/transferencias-ativos', inspecionar:'/inspecoes-ativos', devolver:`/emprestimos/${pendingId}/devolucao`, receber:`/transferencias-ativos/${pendingId}/recebimento`, inativar:`/ativos/${assetId}/situacao`, reativar:`/ativos/${assetId}/situacao`, baixar:`/ativos/${assetId}/situacao` }
 return { path: paths[action], method: ['inativar','reativar','baixar'].includes(action) ? 'PUT' : 'POST' }
}
