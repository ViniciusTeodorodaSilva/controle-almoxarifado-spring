import test from 'node:test'
import assert from 'node:assert/strict'
import { api } from '../src/api/client.js'
import { stockSituation, limitsInput, limitsError, transferItemError, transferError, transferPayload, filterTransfers } from '../src/utils/stockIntelligence.js'
const product = { id: 1, codigo: 'MAT-1', nome: 'Válvula', ativo: true, unidadeMedidaConfigurada: { sigla: 'UN', permiteFracionamento: false } }
const item = { product, amount: 3, balance: 10 }
const draft = { origemId: '1', destinoId: '2', responsavelId: '3', observacao: '  Conferido  ', items: [item] }
test('situação: normal, limite atingido, abaixo e zerado', () => {
  assert.equal(stockSituation({ quantidade: 8, estoqueMinimo: 7 }), 'NORMAL')
  assert.equal(stockSituation({ quantidade: 7, estoqueMinimo: 7 }), 'BAIXO')
  assert.equal(stockSituation({ quantidade: 6, estoqueMinimo: 7 }), 'BAIXO')
  assert.equal(stockSituation({ quantidade: 0, estoqueMinimo: null }), 'ZERADO')
  assert.equal(stockSituation({ quantidade: 1, estoqueMinimo: null }), 'NORMAL')
})
test('limites vazios removem configuração e zero permanece zero', () => {
  assert.deepEqual(limitsInput('', ''), { estoqueMinimo: null, estoqueMaximo: null })
  assert.deepEqual(limitsInput('0', '15'), { estoqueMinimo: 0, estoqueMaximo: 15 })
})
test('limites válidos: ambos, somente mínimo e somente máximo', () => {
  assert.equal(limitsError(limitsInput(7, 20)), '')
  assert.equal(limitsError(limitsInput(7, '')), '')
  assert.equal(limitsError(limitsInput('', 20)), '')
})
test('limites negativos, não finitos e máximo menor são rejeitados', () => {
  for (const values of [[-1, 2], [1, -2], ['Infinity', 10], [0, 'NaN'], [5, 4]]) assert.ok(limitsError(limitsInput(...values)))
})
test('transferência aceita produto ativo com saldo e inteiro', () => assert.equal(transferItemError(product, 3, { quantidade: 10 }), ''))
test('transferência rejeita zero, negativo e quantidade não finita', () => {
  for (const value of [0, -1, 'Infinity', '', 'NaN']) assert.ok(transferItemError(product, value, { quantidade: 10 }))
})
test('transferência valida fracionamento', () => {
  assert.match(transferItemError(product, 1.5, { quantidade: 10 }), /inteiras/)
  assert.equal(transferItemError({ ...product, unidadeMedidaConfigurada: { permiteFracionamento: true } }, 1.5, { quantidade: 10 }), '')
})
test('transferência rejeita inativo, duplicado, sem estoque e saldo insuficiente', () => {
  assert.match(transferItemError({ ...product, ativo: false }, 1, { quantidade: 10 }), /inativo/)
  assert.match(transferItemError(product, 1, { quantidade: 10 }, [item]), /adicionado/)
  assert.ok(transferItemError(product, 1, null))
  assert.ok(transferItemError(product, 11, { quantidade: 10 }))
})
test('revisão exige identificação, origem diferente e itens', () => {
  assert.equal(transferError(draft), '')
  assert.ok(transferError({ ...draft, origemId: '' }))
  assert.ok(transferError({ ...draft, destinoId: '1' }))
  assert.ok(transferError({ ...draft, items: [] }))
  assert.ok(transferError({ ...draft, items: [item, item] }))
})
test('payload contém apenas IDs, quantidades e observação', () => assert.deepEqual(transferPayload(draft), { origemId: 1, destinoId: 2, responsavelId: 3, observacao: 'Conferido', itens: [{ produtoId: 1, quantidade: 3 }] }))
test('pesquisa de transferências: número, material, código, local, responsável e acentos', () => {
  const rows = [{ id: 9, almoxarifadoOrigem: { nome: 'Central' }, almoxarifadoDestino: { nome: 'Obra A' }, responsavel: { nome: 'José' }, itens: [{ codigo: 'MAT-1', produto: 'Válvula' }] }]
  for (const term of ['#9', 'mat-1', 'valvula', 'central', 'obra', 'jose']) assert.equal(filterTransfers(rows, term).length, 1)
  assert.equal(filterTransfers(rows, 'inexistente').length, 0)
})
test('API de limites envia PUT sem alterar saldo e consultas enviam filtros', async () => {
  const original = global.fetch, calls = []
  global.fetch = async (url, options) => { calls.push([url, options]); return { ok: true, status: 200, text: async () => '[]' } }
  try {
    await api.stockLimits(7, limitsInput(3, 12)); await api.stockAlerts({ produtoId: 1, almoxarifadoId: 2 }); await api.restock({ almoxarifadoId: 2 })
    assert.equal(calls[0][0], '/api/estoques/7/limites'); assert.equal(calls[0][1].method, 'PUT')
    assert.deepEqual(JSON.parse(calls[0][1].body), { estoqueMinimo: 3, estoqueMaximo: 12 })
    assert.equal(calls[1][0], '/api/estoques/alertas?produtoId=1&almoxarifadoId=2')
    assert.equal(calls[2][0], '/api/estoques/reposicoes?almoxarifadoId=2')
  } finally { global.fetch = original }
})
test('API cria transferência uma vez e consulta histórico vinculado', async () => {
  const original = global.fetch, calls = []
  global.fetch = async (url, options) => { calls.push([url, options]); return { ok: true, status: 200, text: async () => '{"id":5}' } }
  try {
    await api.createTransfer(transferPayload(draft)); await api.transfers({ origemId: 1, destinoId: 2, produtoId: 3 }); await api.transferMovements(5)
    assert.equal(calls[0][0], '/api/transferencias'); assert.equal(calls[0][1].method, 'POST')
    assert.equal(calls[1][0], '/api/transferencias?origemId=1&destinoId=2&produtoId=3')
    assert.equal(calls[2][0], '/api/transferencias/5/movimentacoes')
  } finally { global.fetch = original }
})
