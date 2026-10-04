import { quantityError } from './operations.js'
export const requestStatuses = ['PENDENTE', 'APROVADA', 'EM_SEPARACAO', 'PARCIALMENTE_ATENDIDA', 'ATENDIDA', 'REJEITADA']
export const canFulfill = request => request.compatibilidadeLegada !== 'INCONSISTENTE' && ['EM_SEPARACAO', 'PARCIALMENTE_ATENDIDA'].includes(request.status)
export function suggestedAmounts(items) {
  return Object.fromEntries(items.map(item => [item.id, String(Math.max(0, Math.min(item.quantidadePendente ?? 0, item.saldoDisponivel ?? 0)))]))
}
export function fulfillmentDraft(items, amounts, responsible) {
  const errors = {}, selected = []
  if (!responsible) errors.responsible = 'Selecione o responsável pelo atendimento.'
  for (const item of items) {
    const raw = amounts[item.id] ?? '', amount = Number(raw)
    if (String(raw).trim() === '' || amount === 0) continue
    const invalid = quantityError(item.produto, raw)
    if (invalid) errors[item.id] = invalid
    else if (amount > item.quantidadePendente) errors[item.id] = 'Quantidade superior ao pendente.'
    else if (amount > item.saldoAtual) errors[item.id] = 'Quantidade superior ao saldo atual.'
    else selected.push({ itemSolicitacaoId: item.id, quantidade: amount })
  }
  const byProduct = new Map()
  for (const selectedItem of selected) {
    const item = items.find(row => row.id === selectedItem.itemSolicitacaoId)
    const total = (byProduct.get(item.produto.id) || 0) + selectedItem.quantidade
    byProduct.set(item.produto.id, total)
    if (total - item.saldoAtual > Number.EPSILON * Math.max(1, total) * selected.length) errors[item.id] = 'A soma dos itens deste material supera o saldo atual.'
  }
  if (!selected.length && !Object.keys(errors).length) errors.form = 'Informe ao menos uma quantidade positiva.'
  return { errors, body: { responsavelId: Number(responsible), itens: selected } }
}
export function filterNeeds(rows, term = '') {
  const normalize = value => String(value ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase()
  const query = normalize(term).trim()
  return rows.filter(row => [row.id, row.solicitacaoId, '#' + row.solicitacaoId, row.produto?.codigo, row.produto?.nome, row.almoxarifado?.nome, row.responsavel?.nome].some(value => normalize(value).includes(query)))
}
