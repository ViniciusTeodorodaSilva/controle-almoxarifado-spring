import test from 'node:test'
import assert from 'node:assert/strict'
import { assetActions, operationBody, assetOperation, assetLabel, assetLocation } from '../src/utils/assets.js'
const available={id:1,ativo:true,status:'DISPONIVEL',condicao:'BOM',reprovado:false,inspecaoPendente:false,pendenciaId:null}
const all=()=>true
test('consulta não recebe ações de custódia ou administração',()=>assert.deepEqual(assetActions(available,()=>false),[]))
test('retirada exige disponibilidade, condição e inspeção em dia',()=>{
 assert.ok(assetActions(available,all).includes('emprestar'))
 for(const change of [{ativo:false},{status:'INDISPONIVEL'},{condicao:'DANIFICADO'},{reprovado:true},{inspecaoPendente:true}])assert.ok(!assetActions({...available,...change},all).includes('emprestar'))
})
test('custódia e trânsito permitem somente fechamento compatível',()=>{
 const loan=assetActions({...available,status:'EMPRESTADO',pendenciaId:4},all)
 assert.ok(loan.includes('devolver'));assert.ok(!loan.includes('transferir'));assert.ok(!loan.includes('baixar'))
 const transfer=assetActions({...available,status:'EM_TRANSFERENCIA',pendenciaId:5},all)
 assert.ok(transfer.includes('receber'));assert.ok(!transfer.includes('devolver'));assert.ok(!transfer.includes('inspecionar'))
})
test('ativo baixado permanece sem ações operacionais',()=>assert.deepEqual(assetActions({...available,status:'BAIXADO'},all),[]))
test('almoxarife opera empréstimo mas não recebe administração de ativos',()=>{
 const actions=assetActions(available,p=>['EMPRESTIMO_GERENCIAR','TRANSFERENCIA_ATIVO_GERENCIAR','INSPECAO_ATIVO_GERENCIAR'].includes(p))
 assert.ok(actions.includes('emprestar'));assert.ok(!actions.includes('editar'));assert.ok(!actions.includes('baixar'))
})
test('snapshot selecionado envia apenas IDs de contexto, sem nomes derivados',()=>{
 const body=operationBody({ativoId:'3',funcionarioId:'4',previsaoDevolucao:'',contexto:{obraId:'7',ordemServicoId:'8',centroCustoId:'',obraNome:'Projeto Alfa',obraCodigo:'ALFA'}})
 assert.deepEqual(body,{ativoId:3,funcionarioId:4,previsaoDevolucao:null,contexto:{obraId:7,ordemServicoId:8,centroCustoId:null}})
})
test('contexto vazio conserva operação geral e datas opcionais nulas',()=>assert.deepEqual(operationBody({contexto:{},almoxarifadoId:'',proximaInspecao:''}),{contexto:null,almoxarifadoId:null,proximaInspecao:null}))
test('devolução referencia empréstimo aberto e não o ID do ativo',()=>assert.deepEqual(assetOperation('devolver',9,17),{path:'/emprestimos/17/devolucao',method:'POST'}))
test('recebimento referencia envio aberto e não transferência de estoque',()=>assert.deepEqual(assetOperation('receber',9,18),{path:'/transferencias-ativos/18/recebimento',method:'POST'}))
test('baixa é transição protegida do ativo, sem DELETE',()=>assert.deepEqual(assetOperation('baixar',9,null),{path:'/ativos/9/situacao',method:'PUT'}))
test('situação, condição e resultado têm rótulos legíveis distintos',()=>{
 assert.equal(assetLabel('EM_TRANSFERENCIA'),'Em trânsito');assert.equal(assetLabel('DANIFICADO'),'Danificado');assert.equal(assetLabel('REPROVADO'),'Reprovado');assert.equal(assetLabel('DISPONIVEL'),'Disponível')
})
test('referência ainda não carregada nunca inventa localização Obra',()=>{
 assert.equal(assetLocation({almoxarifadoId:7}),'Almoxarifado #7');assert.equal(assetLocation({almoxarifadoId:7},[{id:7,nome:'Central'}]),'Central');assert.equal(assetLocation({contexto:{obraId:3,obraNome:'Alfa'}}),'Obra: Alfa');assert.equal(assetLocation({}),'Local não informado')
})
