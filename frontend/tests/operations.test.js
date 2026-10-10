import { test } from 'node:test'
import assert from 'node:assert/strict'
import { ApiError } from '../src/api/client.js'
import { fitsLegacyQuantity, compareQuantities } from '../src/utils/quantities.js'
test('entrada decimal não perde dígitos ao converter para contrato legado', () => {
 for (const value of ['0.1', '0.20', '.5', '1e-6', '1000.00']) assert.equal(fitsLegacyQuantity(value), true)
 for (const value of ['9007199254740993', '0.10000000000000001', '0x10', '1e309']) assert.equal(fitsLegacyQuantity(value), false)
 assert.match(quantityError(null,'9007199254740993'),/precisão/)
 assert.equal(compareQuantities('0.000001','0.0000009999995'),1)
})
import { filterRequests, filterStocks, filterMovements, quantityError, submitRequest, reconcileRequest, registerMovement, SubmissionError } from '../src/utils/operations.js'
const product = { id: 1, codigo: 'MAT-01', nome: 'Válvula', descricao: 'Aço industrial', unidadeMedidaConfigurada: { sigla: 'UN', permiteFracionamento: false } }
const rows = [{ id: 12, solicitante: { nome: 'João' }, responsavel: { nome: 'Maria' }, almoxarifado: { id: 4, nome: 'Central' }, produto: product, solicitacaoId: 12, itens: [{ produto: product }], quantidade: 8, dataHora: '2026-10-04T23:59:59' }]
test('solicitações: pesquisa por número, pessoa, almoxarifado e material, sem diferença de acento', () => {
 for (const term of ['#12', 'joao', 'central', 'mat-01', 'valvula', 'aco']) assert.equal(filterRequests(rows, term).length, 1)
 assert.equal(filterRequests(rows, 'inexistente').length, 0)
})
test('estoque: busca e filtros são combinados por AND', () => {
 assert.equal(filterStocks(rows, { term: 'ACO', product: '1', warehouse: '4' }).length, 1)
 assert.equal(filterStocks(rows, { term: 'ACO', product: '2' }).length, 0)
 assert.equal(filterStocks(rows, { term: 'MAT-01', warehouse: '9' }).length, 0)
})
test('movimentações: material, pessoas e solicitação respeitam período inclusivo', () => {
 for (const term of ['valvula', 'MAT-01', 'joao', 'maria', '#12']) assert.equal(filterMovements(rows, { term, from: '2026-10-04', to: '2026-10-04' }).length, 1)
 assert.equal(filterMovements(rows, { from: '2026-10-05' }).length, 0)
 assert.equal(filterMovements(rows, { to: '2026-10-03' }).length, 0)
 assert.equal(filterMovements(rows, { product: '2' }).length, 0)
})
test('quantidade deve ser positiva e finita', () => {
 for (const value of ['', ' ', '0', '-1', 'NaN', 'Infinity']) assert.ok(quantityError(product, value))
 assert.equal(quantityError(product, '2'), '')
})
test('fracionamento: unidade inteira rejeita frações sem inferir regra para legado', () => {
 assert.ok(quantityError(product, '0.5'))
 assert.equal(quantityError({ unidadeMedidaConfigurada: { permiteFracionamento: true } }, '0.5'), '')
 assert.equal(quantityError({ unidadeMedida: 'UN' }, '0.5'), '')
})
const draft = { solicitanteId: '2', almoxarifadoId: '4', items: [{ product, amount: '2' }, { product, amount: '3' }] }
test('cria solicitação uma vez e envia itens sequencialmente', async () => {
 const calls = []
 const client = { createRequest: async (...args) => { calls.push(['create', ...args]); return { id: 9 } }, addRequestItem: async (...args) => calls.push(['item', ...args]) }
 const result = await submitRequest(client, draft)
 assert.deepEqual(calls, [['create', 2, 4], ['item', 9, 1, 2], ['item', 9, 1, 3]])
 assert.equal(result.completed, 2)
})
test('falha intermediária conserva ID e itens confirmados; continuação não duplica o primeiro item', async () => {
 let writes = 0, creates = 0, progress
 const client = { createRequest: async () => { creates++; return { id: 9 } }, addRequestItem: async () => { if (++writes === 2) throw new ApiError('Inválido', 400) } }
 await assert.rejects(submitRequest(client, draft), error => { progress = error.progress; return error instanceof SubmissionError && progress.id === 9 && progress.completed === 1 })
 const result = await submitRequest(client, draft, progress)
 assert.equal(creates, 1); assert.equal(writes, 3); assert.equal(result.completed, 2)
})
test('resultado incerto bloqueia repetição de POST', async () => {
 let calls = 0, progress
 const client = { createRequest: async () => ({ id: 9 }), addRequestItem: async () => { calls++; throw new ApiError('Offline', 0) } }
 await assert.rejects(submitRequest(client, draft), error => { progress = error.progress; return progress.uncertain })
 await assert.rejects(submitRequest(client, draft, progress), /Confira o resultado/)
 assert.equal(calls, 1)
})
test('reconciliação lê itens recebidos e libera somente o restante', async () => {
 const client = { get: async () => ({ status: 'PENDENTE', itens: [{ id: 10, produto: product, quantidade: 2 }] }) }
 assert.deepEqual(await reconcileRequest(client, draft.items, { id: 9, completed: 0 }), { id: 9, completed: 1, uncertain: false })
})
test('reconciliação bloqueia estado incompatível, alteração externa e criação sem ID', async () => {
 await assert.rejects(reconcileRequest({}, draft.items, {}), /resultado da criação é incerto/)
 await assert.rejects(reconcileRequest({ get: async () => ({ status: 'APROVADA' }) }, draft.items, { id: 9 }), /não está mais pendente/)
 await assert.rejects(reconcileRequest({ get: async () => ({ status: 'PENDENTE', itens: [{ id: 1, produto: product, quantidade: 7 }] }) }, draft.items, { id: 9, completed: 0 }), /não correspondem/)
})
test('entrada cria estoque zerado apenas com autorização explícita e usa movimentação', async () => {
 const calls = []
 const client = { stock: async () => { throw new ApiError('Ausente', 404) }, createStock: async () => { calls.push('zero'); return { quantidade: 0 } }, moveStock: async (type, values) => { calls.push([type, values.quantidade]); return { quantidade: 7 } } }
 const values = { produtoId: 1, almoxarifadoId: 4, quantidade: 7 }
 await assert.rejects(registerMovement(client, 'entrada', values, false), /Ausente/)
 assert.deepEqual(calls, [])
 await registerMovement(client, 'entrada', values, true)
 assert.deepEqual(calls, ['zero', ['entrada', 7]])
})
test('estoque criado simultaneamente: conflito 409 é conferido antes da entrada', async () => {
 let reads = 0, moved = false
 const client = { stock: async () => { if (++reads === 1) throw new ApiError('Ausente', 404); return { quantidade: 0 } }, createStock: async () => { throw new ApiError('Já existe', 409) }, moveStock: async () => { moved = true } }
 await registerMovement(client, 'entrada', { produtoId: 1, almoxarifadoId: 4, quantidade: 2 }, true)
 assert.equal(reads, 2); assert.equal(moved, true)
})
test('saída nunca cria estoque e não grava acima do saldo consultado', async () => {
 let writes = 0
 const client = { stock: async () => ({ quantidade: 2 }), moveStock: async () => { writes++ } }
 await assert.rejects(registerMovement(client, 'saida', { quantidade: 3 }), error => error.status === 400)
 assert.equal(writes, 0)
 await registerMovement(client, 'saida', { quantidade: 1 }); assert.equal(writes, 1)
})
test('erro de gravação da movimentação é propagado sem repetição', async () => {
 let writes = 0
 const client = { stock: async () => ({ quantidade: 5 }), moveStock: async () => { writes++; throw new ApiError('Resultado incerto', 0) } }
 await assert.rejects(registerMovement(client, 'entrada', { quantidade: 2 }), /Resultado incerto/)
 assert.equal(writes, 1)
})
