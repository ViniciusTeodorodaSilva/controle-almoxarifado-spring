import { contextBody } from './context.js'
import { quantityNumber, sumQuantities } from './quantities.js'
export const purchaseStatuses = ['RASCUNHO', 'AGUARDANDO_APROVACAO', 'APROVADO', 'PARCIALMENTE_RECEBIDO', 'RECEBIDO', 'CANCELADO']
export const money = value => { const [whole, fractional = ''] = String(value ?? '0').split('.'); const decimals = fractional.replace(/0+$/, '').padEnd(2, '0'); return `R$ ${whole.replace(/\B(?=(\d{3})+(?!\d))/g, '.')},${decimals}` }
export function needsToItems(needs) {
 const groups = new Map()
 for (const n of needs) {
  const q = n.compra?.quantidadeDisponivel ?? n.quantidade
  if (!(q > 0)) continue
  const item = groups.get(n.produto.id) || { produto: n.produto, produtoId: n.produto.id, quantidade: 0, valorUnitario: '', paraEstoque: false, observacao: '', alocacoes: [] }
  item.quantidade = quantityNumber(sumQuantities(item.quantidade, q))
  item.alocacoes.push({ necessidadeId: n.id, solicitacaoId: n.solicitacaoId, quantidade: q })
  groups.set(n.produto.id, item)
 }
 return [...groups.values()]
}
export function orderBody(form) {
 return { fornecedorId: Number(form.fornecedorId), almoxarifadoId: Number(form.almoxarifadoId), observacao: form.observacao, itens: form.itens.map(i => ({ ...(Object.keys(contextBody(i.contexto)).length ? {contexto:contextBody(i.contexto)} : {}), produtoId: Number(i.produtoId), quantidade: Number(i.quantidade), valorUnitario: i.valorUnitario === '' ? null : String(i.valorUnitario).trim(), observacao: i.observacao, paraEstoque: i.paraEstoque, alocacoes: i.alocacoes.map(a => ({ necessidadeId: a.necessidadeId, quantidade: Number(a.quantidade) })) })) }
}
export function receiptBody(order, responsible, values, observation) { return { almoxarifadoId: order.almoxarifadoId, responsavelId: Number(responsible), observacao: observation, itens: order.itens.filter(i => Number(values[i.id]) > 0).map(i => ({ itemPedidoId: i.id, quantidade: Number(values[i.id]) })) } }
