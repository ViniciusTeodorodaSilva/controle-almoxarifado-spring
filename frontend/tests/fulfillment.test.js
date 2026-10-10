import { test } from 'node:test'
import assert from 'node:assert/strict'
import { requestStatuses, canFulfill, suggestedAmounts, fulfillmentDraft, filterNeeds } from '../src/utils/fulfillment.js'
import { api } from '../src/api/client.js'
const item = { id: 1, produto: { id: 10, codigo: 'BES-01', nome: 'Válvula', unidadeMedidaConfigurada: { sigla: 'UN', permiteFracionamento: false } }, quantidadePendente: 8, saldoAtual: 5, saldoDisponivel: 5 }
test('status contemplam autorização, separação e entrega', () => assert.deepEqual(requestStatuses, ['PENDENTE','APROVADA','EM_SEPARACAO','PARCIALMENTE_ATENDIDA','ATENDIDA','REJEITADA']))
test('atendimento disponível apenas em estados válidos e histórico consistente', () => { for (const status of requestStatuses) assert.equal(canFulfill({status}), ['EM_SEPARACAO','PARCIALMENTE_ATENDIDA'].includes(status)); assert.equal(canFulfill({status:'EM_SEPARACAO',compatibilidadeLegada:'INCONSISTENTE'}), false) })
test('sugestão respeita pendente e disponibilidade alocada', () => assert.deepEqual(suggestedAmounts([item,{...item,id:2,saldoDisponivel:0}]),{1:'5',2:'0'}))
test('operador pode reduzir sugestão sem entregar saldo completo', () => { const result=fulfillmentDraft([item],{1:'2'},9);assert.deepEqual(result.errors,{});assert.deepEqual(result.body,{responsavelId:9,itens:[{itemSolicitacaoId:1,quantidade:2}]}) })
test('zeros e campos vazios ignorados exigem ao menos um positivo', () => { for (const raw of ['0','']) assert.ok(fulfillmentDraft([item],{1:raw},9).errors.form) })
test('negativo e não finito rejeitados', () => { for(const raw of ['-1','Infinity','NaN']) assert.ok(fulfillmentDraft([item],{1:raw},9).errors[1]) })
test('fracionamento respeita unidade configurada', () => assert.match(fulfillmentDraft([item],{1:'1.5'},9).errors[1],/inteiras/))
test('pendente e saldo são limites diferentes', () => { assert.match(fulfillmentDraft([item],{1:'9'},9).errors[1],/pendente/);assert.match(fulfillmentDraft([item],{1:'6'},9).errors[1],/saldo/) })
test('itens repetidos não somam mais que estoque físico', () => assert.match(fulfillmentDraft([item,{...item,id:2}],{1:'3',2:'3'},9).errors[2],/soma/))
test('soma decimal atende saldo exato mas rejeita excesso menor que epsilon', () => {
 const decimal = {...item, produto:{...item.produto,unidadeMedidaConfigurada:{permiteFracionamento:true}}, saldoAtual:0.3}
 assert.deepEqual(fulfillmentDraft([decimal,{...decimal,id:2}],{1:'0.1',2:'0.2'},9).errors,{})
 assert.match(fulfillmentDraft([decimal,{...decimal,id:2}],{1:'0.10000000000000002',2:'0.2'},9).errors[2],/soma/)
})
test('responsável obrigatório', () => assert.ok(fulfillmentDraft([item],{1:'1'},'').errors.responsible))
test('pesquisa de necessidades por contexto e acentos', () => { const rows=[{id:3,solicitacaoId:7,produto:item.produto,almoxarifado:{nome:'Central'}}];for(const term of ['valvula','BES-01','#7','Central'])assert.equal(filterNeeds(rows,term).length,1);assert.equal(filterNeeds(rows,'ausente').length,0) })
test('atendimento mantém chave em retries e envia somente contrato explícito', async () => { const calls=[];globalThis.fetch=async(url,options)=>{calls.push({url,options});return new Response('{"id":4}')};const body={responsavelId:9,itens:[{itemSolicitacaoId:1,quantidade:2}]};await api.fulfill(7,body,'stable-key-01');await api.fulfill(7,body,'stable-key-01');assert.equal(calls.length,2);for(const {url,options} of calls){assert.equal(url,'/api/solicitacoes/7/atendimentos');assert.equal(options.headers['Idempotency-Key'],'stable-key-01');assert.equal(options.method,'POST');assert.deepEqual(JSON.parse(options.body),body)} })
test('separação preserva contrato PUT e responsável', async () => {let call;globalThis.fetch=async(url,options)=>{call={url,options};return new Response('{}')};await api.separate(7,9);assert.equal(call.url,'/api/solicitacoes/7/iniciar-separacao?responsavelId=9');assert.equal(call.options.method,'PUT')})
test('necessidade envia chave e não permite quantidade calculada no cliente', async () => {let call;globalThis.fetch=async(url,options)=>{call={url,options};return new Response('{}')};await api.createPurchaseNeed({itemSolicitacaoId:1,responsavelId:9},'need-key-01');assert.equal(call.url,'/api/necessidades-compra');assert.equal(call.options.headers['Idempotency-Key'],'need-key-01');assert.deepEqual(JSON.parse(call.options.body),{itemSolicitacaoId:1,responsavelId:9})})
test('consulta de histórico e filtros de necessidades usam GET', async () => {const urls=[];globalThis.fetch=async(url,options)=>{urls.push(url);assert.equal(options.method,'GET');return new Response('[]')};await api.fulfillments(7);await api.purchaseNeeds({status:'ABERTA',solicitacaoId:7});assert.deepEqual(urls,['/api/solicitacoes/7/atendimentos','/api/necessidades-compra?status=ABERTA&solicitacaoId=7'])})
