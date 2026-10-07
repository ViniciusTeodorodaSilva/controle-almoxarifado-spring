import test from 'node:test'
import assert from 'node:assert/strict'
import { contextBody, contextText, compatibleOrders, compatibleCenters, transitions } from '../src/utils/context.js'
import { orderBody } from '../src/utils/purchases.js'
import { submitRequest } from '../src/utils/operations.js'
import { routePermission, can } from '../src/auth/permissions.js'
test('contexto geral omite vínculos vazios',()=>assert.deepEqual(contextBody({obraId:'',ordemServicoId:null,centroCustoId:undefined}),{}))
test('contexto envia somente três IDs e não aceita ator do formulário',()=>assert.deepEqual(contextBody({obraId:'12',ordemServicoId:4,centroCustoId:9,criadoPor:42}),{obraId:12,ordemServicoId:4,centroCustoId:9}))
test('histórico sem contexto não inventa obra',()=>assert.equal(contextText(null),'Sem contexto'))
test('contexto histórico apresenta código nome OS e CC',()=>assert.equal(contextText({obraCodigo:'A',obraNome:'Obra',ordemServicoNumero:'OS-1',centroCustoCodigo:'CC',centroCustoNome:'Custo'}),'A · Obra / OS-1 / CC · Custo'))
test('OS filtradas por obra e situação admissível',()=>assert.deepEqual(compatibleOrders([{id:1,obraId:1,status:'ABERTA'},{id:2,obraId:2,status:'ABERTA'},{id:3,obraId:1,status:'CONCLUIDA'}],1).map(o=>o.id),[1]))
test('centros incluem corporativo e obra compatível, excluem inativo',()=>assert.deepEqual(compatibleCenters([{id:1,ativo:true,obraId:null},{id:2,ativo:true,obraId:1},{id:3,ativo:true,obraId:2},{id:4,ativo:false,obraId:1}],1).map(c=>c.id),[1,2]))
test('centro definido na OS limita seleção',()=>assert.deepEqual(compatibleCenters([{id:1,ativo:true},{id:2,ativo:true}],null,{centroCustoId:2}).map(c=>c.id),[2]))
test('status final não oferece transição',()=>{assert.deepEqual(transitions('obras','CONCLUIDA'),[]);assert.deepEqual(transitions('ordens-servico','CANCELADA'),[])})
test('retomada de OS suspensa não oferece conclusão direta',()=>assert.deepEqual(transitions('ordens-servico','SUSPENSA'),['EM_ANDAMENTO','CANCELADA']))
test('compra manual mantém contexto por item sem contexto global',()=>{const p=orderBody({fornecedorId:1,almoxarifadoId:2,itens:[{produtoId:3,quantidade:2,valorUnitario:'1.2500',paraEstoque:true,alocacoes:[],contexto:{obraId:'4'}}]});assert.deepEqual(p.itens[0].contexto,{obraId:4});assert.equal(p.contexto,undefined)})
test('alocações conservam IDs originais e não aceitam contexto forjado',()=>{const p=orderBody({fornecedorId:1,almoxarifadoId:2,itens:[{produtoId:3,quantidade:2,valorUnitario:'1',alocacoes:[{necessidadeId:1,quantidade:1,contexto:{obraId:4}},{necessidadeId:2,quantidade:1,contexto:{obraId:8}}]}]});assert.deepEqual(p.itens[0].alocacoes,[{necessidadeId:1,quantidade:1},{necessidadeId:2,quantidade:1}])})
test('solicitação contextual envia IDs na criação',async()=>{let args;await submitRequest({createRequest:async(...a)=>{args=a;return{id:1}},addRequestItem:async()=>{}},{solicitanteId:1,almoxarifadoId:2,contexto:{ordemServicoId:'3'},items:[]});assert.deepEqual(args,[1,2,{ordemServicoId:3}])})
test('rotas estruturais exigem authorities de leitura',()=>{assert.equal(routePermission['/obras'],'OBRA_LER');assert.equal(routePermission['/ordens-servico'],'ORDEM_SERVICO_LER');assert.equal(routePermission['/centros-custo'],'CENTRO_CUSTO_LER')})
test('leitor não gerencia estrutura por nome do perfil',()=>assert.equal(can({ativo:true,perfil:'ADMIN',permissoes:['OBRA_LER']},'OBRA_GERENCIAR'),false))
