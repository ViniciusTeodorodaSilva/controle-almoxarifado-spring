import { quantityError } from './operations.js'
export function stockSituation(stock) {
  if (stock.quantidade === 0) return 'ZERADO'
  return stock.estoqueMinimo != null && stock.quantidade <= stock.estoqueMinimo ? 'BAIXO' : 'NORMAL'
}
export function limitsInput(minimum, maximum) {
  const value = text => String(text).trim() === '' ? null : Number(text)
  return { estoqueMinimo: value(minimum), estoqueMaximo: value(maximum) }
}
export function limitsError(input) {
  if ([input.estoqueMinimo, input.estoqueMaximo].some(value => value != null && (!Number.isFinite(value) || value < 0))) return 'Os limites devem ser números maiores ou iguais a zero.'
  if (input.estoqueMinimo != null && input.estoqueMaximo != null && input.estoqueMaximo < input.estoqueMinimo) return 'O máximo não pode ser menor que o mínimo.'
  return ''
}
export function transferItemError(product, amount, stock, items = []) {
  if (!product) return 'Selecione um produto do catálogo.'
  if (product.ativo === false) return 'O produto está inativo.'
  if (items.some(item => item.product.id === product.id)) return 'Este produto já foi adicionado.'
  const error = quantityError(product, amount)
  if (error) return error
  if (!stock || Number(amount) > stock.quantidade) return 'A quantidade é superior ao saldo disponível na origem.'
  return ''
}
export function transferError(draft) {
  if (!draft.origemId || !draft.destinoId || !draft.responsavelId) return 'Selecione origem, destino e responsável.'
  if (String(draft.origemId) === String(draft.destinoId)) return 'Origem e destino devem ser diferentes.'
  if (!draft.items.length) return 'Adicione pelo menos um produto.'
  const seen = []
  for (const item of draft.items) {
    const error = transferItemError(item.product, item.amount, { quantidade: item.balance }, seen)
    if (error) return error
    seen.push(item)
  }
  return ''
}
export function transferPayload(draft) {
  return { origemId: Number(draft.origemId), destinoId: Number(draft.destinoId), responsavelId: Number(draft.responsavelId), observacao: draft.observacao.trim() || null,
    itens: draft.items.map(item => ({ produtoId: item.product.id, quantidade: Number(item.amount) })) }
}
export function filterTransfers(rows, term = '') {
  const normalize = value => String(value ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase()
  return rows.filter(row => [row.id, '#' + row.id, row.almoxarifadoOrigem?.nome, row.almoxarifadoDestino?.nome, row.responsavel?.nome, row.observacao,
    ...(row.itens || []).flatMap(item => [item.codigo, item.produto])].some(value => normalize(value).includes(normalize(term.trim()))))
}
