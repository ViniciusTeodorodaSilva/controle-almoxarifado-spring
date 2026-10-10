import { contextBody } from './context.js'
import { fitsLegacyQuantity } from './quantities.js'
export const unitLabel = product => product?.unidadeMedidaConfigurada?.sigla || product?.unidadeMedida || '—'
export function quantityError(product, value) {
  const amount = Number(value)
  if (String(value).trim() === '' || !Number.isFinite(amount) || amount <= 0) return 'Informe uma quantidade maior que zero.'
  if (!fitsLegacyQuantity(value)) return 'A quantidade excede a precisão suportada. Nenhum valor foi arredondado.'
  if (product?.unidadeMedidaConfigurada?.permiteFracionamento === false && !Number.isInteger(amount)) return 'Esta unidade permite somente quantidades inteiras.'
  return ''
}
const text = value => String(value ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('pt-BR').trim()
const matches = (values, term) => values.some(value => text(value).includes(text(term)))
export function filterRequests(rows, term) {
  return rows.filter(row => matches([row.id, '#' + row.id, row.status, row.status?.replaceAll('_', ' '), row.solicitante?.nome, row.almoxarifado?.nome,
    ...(row.itens || []).flatMap(item => [item.produto?.codigo, item.produto?.nome, item.produto?.descricao])], term))
}
export function filterStocks(rows, { term = '', product = '', warehouse = '' }) {
  return rows.filter(row => (!product || String(row.produto?.id) === String(product)) &&
    (!warehouse || String(row.almoxarifado?.id) === String(warehouse)) &&
    matches([row.produto?.codigo, row.produto?.nome, row.produto?.descricao], term))
}
export function filterMovements(rows, { term = '', product = '', warehouse = '', from = '', to = '' }) {
  return rows.filter(row => (!product || String(row.produto?.id) === String(product)) &&
    (!warehouse || String(row.almoxarifado?.id) === String(warehouse)) &&
    (!from || (row.dataHora && row.dataHora.slice(0, 10) >= from)) &&
    (!to || (row.dataHora && row.dataHora.slice(0, 10) <= to)) &&
    matches([row.produto?.codigo, row.produto?.nome, row.produto?.descricao, row.solicitante?.nome,
      row.responsavel?.nome, row.solicitacaoId, row.solicitacaoId ? '#' + row.solicitacaoId : ''], term))
}
export class SubmissionError extends Error {
  constructor(cause, progress) { super(cause.message); this.cause = cause; this.progress = progress }
}
// Each POST is independent. Never retry a write with an uncertain outcome automatically.
export async function submitRequest(client, draft, initial = {}, onProgress = () => {}) {
  let progress = { id: null, completed: 0, uncertain: false, ...initial }
  if (progress.uncertain) throw new SubmissionError(new Error('Confira o resultado antes de repetir uma gravação.'), progress)
  try {
    if (!progress.id) {
      const record = await client.createRequest(Number(draft.solicitanteId), Number(draft.almoxarifadoId), ...(Object.keys(contextBody(draft.contexto)).length ? [contextBody(draft.contexto)] : []))
      progress = { ...progress, id: record.id }
      onProgress(progress)
    }
    for (let index = progress.completed; index < draft.items.length; index++) {
      const item = draft.items[index]
      await client.addRequestItem(progress.id, item.product.id, Number(item.amount))
      progress = { ...progress, completed: index + 1 }
      onProgress(progress)
    }
    return progress
  } catch (error) {
    progress = { ...progress, uncertain: !error.status || error.status >= 500 }
    throw new SubmissionError(error, progress)
  }
}
export async function reconcileRequest(client, items, progress) {
  if (!progress.id) throw new Error('O resultado da criação é incerto. Confira a listagem antes de iniciar outra solicitação.')
  const record = await client.get('solicitacoes', progress.id)
  if (record.status !== 'PENDENTE') throw new Error('A solicitação não está mais pendente. Abra seus detalhes para conferir o estado atual.')
  const actual = [...(record.itens || [])].sort((a, b) => a.id - b.id)
  const equal = actual.length >= progress.completed && actual.length <= items.length && actual.every((item, index) =>
    item.produto?.id === items[index].product.id && Number(item.quantidade) === Number(items[index].amount))
  if (!equal) throw new Error('Os itens foram alterados ou não correspondem ao envio. Confira os detalhes; o envio não será repetido.')
  return { id: progress.id, completed: actual.length, uncertain: false }
}
export async function registerMovement(client, type, values, allowCreate, key) {
  let stock
  try { stock = await client.stock(values.produtoId, values.almoxarifadoId) }
  catch (error) {
    if (error.status !== 404) throw error
    if (type !== 'entrada' || !allowCreate) throw error
    try { stock = await client.createStock(values.produtoId, values.almoxarifadoId) }
    catch (error) {
      if (error.status !== 409) throw error
      stock = await client.stock(values.produtoId, values.almoxarifadoId)
    }
  }
  if (type === 'saida' && Number(values.quantidade) > stock.quantidade) {
    const error = new Error('A quantidade de saída é superior ao saldo atual.'); error.status = 400; throw error
  }
  return client.moveStock(type, values, key)
}
