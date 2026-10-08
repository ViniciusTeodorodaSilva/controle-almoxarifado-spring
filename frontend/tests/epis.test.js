import test from 'node:test'
import assert from 'node:assert/strict'
import {epiQuantity,epiDeliveryBody,validateEpiDelivery,epiAlerts,epiLabel} from '../src/utils/epis.js'
const form={funcionarioId:'1',responsavelId:'2',almoxarifadoId:'3',motivo:'INICIAL',recebimentoConfirmado:true}
const line={produtoId:4,nome:'Capacete',quantidade:'2',saldoDisponivel:3,fracionado:false}
for(const value of ['0','-1','NaN','Infinity','1e3','0.0000001','10000000000000','9999999999999.9999999'])test('EPI rejeita quantidade '+value,()=>assert.ok(epiQuantity(value)))
for(const value of ['1','0,5','0.000001','9999999999999.999999'])test('EPI aceita decimal exato '+value,()=>assert.equal(epiQuantity(value),''))
test('unidade inteira rejeita fração mesmo em valor grande',()=>assert.ok(epiQuantity('9999999999999.000001',true)))
test('DTO exclui ator, saldo, CA e nomes do cliente',()=>{const body=epiDeliveryBody({...form,atorId:99},[{...line,ca:'forjado',saldo:99}]);assert.deepEqual(Object.keys(body.itens[0]).sort(),['fabricacao','lote','produtoId','quantidade','validadeFisica']);assert.equal(body.atorId,undefined);assert.equal(body.contexto,null)})
test('contexto transmite somente IDs',()=>{const b=epiDeliveryBody({...form,contexto:{obraId:'5',obraNome:'forjado'}},[line]);assert.deepEqual(b.contexto,{obraId:5,ordemServicoId:null,centroCustoId:null})})
test('entrega múltipla válida',()=>assert.equal(validateEpiDelivery(form,[line,{...line,produtoId:5}]),''))
test('produto repetido rejeitado',()=>assert.ok(validateEpiDelivery(form,[line,line])))
test('saldo insuficiente rejeitado antes da escrita',()=>assert.match(validateEpiDelivery(form,[{...line,quantidade:'4'}]),/Saldo insuficiente/))
test('confirmação operacional obrigatória',()=>assert.ok(validateEpiDelivery({...form,recebimentoConfirmado:false},[line])))
test('substituição exige origem e condição manual',()=>assert.ok(validateEpiDelivery({...form,motivo:'SUBSTITUICAO'},[line])))
test('substituição preserva condição e destino anteriores',()=>{const b=epiDeliveryBody({...form,motivo:'SUBSTITUICAO'},[{...line,origemItemId:8,quantidadeSubstituida:'1',motivoSubstituicao:'Desgaste',condicaoAnterior:'USADO',destinoAnterior:'SEGREGADO'}]);assert.equal(b.itens[0].condicaoAnterior,'USADO');assert.equal(b.itens[0].origemItemId,8)})
test('alerta CA não implica vencimento físico',()=>assert.deepEqual(epiAlerts({caVencido:true}),['CA com prazo cadastrado vencido']))
test('alertas têm texto além da cor',()=>assert.equal(epiAlerts({vencido:true,aVencer:true}).length,2))
test('labels legíveis e fallback',()=>{assert.equal(epiLabel('SEGREGADO'),'Segregado / indisponível');assert.equal(epiLabel(null),'—')})

test('saldo insuficiente tambem com virgula decimal',()=>assert.match(validateEpiDelivery(form,[{...line,fracionado:true,quantidade:'3,5'}]),/Saldo insuficiente/))

test('residuo binario nao bloqueia quantidade disponivel',()=>assert.equal(validateEpiDelivery(form,[{...line,fracionado:true,quantidade:'0.2',saldoDisponivel:0.3-0.1}]),''))
test('diferenca real de um micro continua insuficiente',()=>assert.match(validateEpiDelivery(form,[{...line,fracionado:true,quantidade:'0.2',saldoDisponivel:0.199999}]),/Saldo insuficiente/))
