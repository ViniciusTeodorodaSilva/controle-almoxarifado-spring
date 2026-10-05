import { test, afterEach } from 'node:test'
import assert from 'node:assert/strict'
import { request, csrf, configureSessionClient, resetCsrf } from '../src/api/client.js'
import { can, registryPermission } from '../src/auth/permissions.js'
afterEach(()=>{configureSessionClient(false);delete globalThis.window})
test('login usa CSRF, cookie e corpo mínimo',async()=>{
 configureSessionClient(true);const calls=[]
 globalThis.fetch=async(url,options)=>{calls.push([url,options]);return new Response(JSON.stringify(url.endsWith('/csrf')?{token:'fictitious-csrf'}:{id:1}))}
 await request('/auth/login',{method:'POST',body:{username:'h2.user',password:'H2-only-passphrase!'}})
 assert.equal(calls.length,2);assert.equal(calls[1][1].credentials,'include');assert.equal(calls[1][1].headers['X-CSRF-TOKEN'],'fictitious-csrf');assert.deepEqual(JSON.parse(calls[1][1].body),{username:'h2.user',password:'H2-only-passphrase!'})
})
test('renova CSRF após autenticação ou logout',async()=>{configureSessionClient(true);let count=0;globalThis.fetch=async()=>new Response(JSON.stringify({token:'fictitious-'+(++count)}));assert.equal(await csrf(),'fictitious-1');assert.equal(await csrf(),'fictitious-1');resetCsrf();assert.equal(await csrf(),'fictitious-2')})
test('401 expira estado; 403 mantém sessão',async()=>{let expired=0;globalThis.window={dispatchEvent:()=>expired++};globalThis.fetch=async()=>new Response('{}',{status:401});await assert.rejects(request('/produtos'),e=>e.status===401);assert.equal(expired,1);globalThis.fetch=async()=>new Response('{}',{status:403});await assert.rejects(request('/produtos'),e=>e.status===403&&e.message.includes('permissão'));assert.equal(expired,1)})
test('login inválido não cria loop de expiração',async()=>{let expired=0;globalThis.window={dispatchEvent:()=>expired++};globalThis.fetch=async()=>new Response('{"mensagem":"Credenciais invalidas"}',{status:401});await assert.rejects(request('/auth/login',{method:'POST',body:{}}),e=>e.status===401);assert.equal(expired,0)})
test('CSRF indisponível impede envio de escrita',async()=>{configureSessionClient(true);let count=0;globalThis.fetch=async()=>{count++;return new Response('{}',{status:503})};await assert.rejects(request('/usuarios',{method:'POST',body:{}}),e=>e.status===503);assert.equal(count,1)})
test('CSRF obrigatório em PUT e logout',async()=>{configureSessionClient(true);const calls=[];globalThis.fetch=async(url,options)=>{calls.push([url,options]);return url.endsWith('/csrf')?new Response('{"token":"fictitious-only"}'):new Response(null,{status:204})};await request('/usuarios/1',{method:'PUT',body:{ativo:false}});await request('/auth/logout',{method:'POST'});for(const [,o]of calls.filter(([url])=>!url.endsWith('/csrf')))assert.equal(o.headers['X-CSRF-TOKEN'],'fictitious-only')})
for(const [perfil,permission,allowed]of [['ADMIN','USUARIO_GERENCIAR',true],['GESTOR','SOLICITACAO_APROVAR',true],['ALMOXARIFE','ESTOQUE_MOVIMENTAR',true],['CONSULTA','ESTOQUE_MOVIMENTAR',false]])test('UX por permissão: '+perfil,()=>assert.equal(can({ativo:true,perfil,permissoes:allowed?[permission]:[]},permission),allowed))
test('nome do perfil isolado não concede autoridade',()=>assert.equal(can({ativo:true,perfil:'ADMIN',permissoes:[]},'USUARIO_GERENCIAR'),false))
test('usuário inativo não habilita ações',()=>assert.equal(can({ativo:false,permissoes:['PRODUTO_GERENCIAR']},'PRODUTO_GERENCIAR'),false))
test('cadastros usam permissões específicas',()=>assert.equal(registryPermission.funcionarios,'FUNCIONARIO_GERENCIAR'))
test('API me consulta identidade no backend',async()=>{let captured;globalThis.fetch=async(url,options)=>{captured=[url,options];return new Response('{"username":"h2.user","permissoes":[]}')};const me=await request('/auth/me');assert.equal(me.username,'h2.user');assert.equal(captured[0],'/api/auth/me');assert.equal(captured[1].credentials,'include')})

test('401 em me/logout também encerra identidade local',async()=>{let expired=0;globalThis.window={dispatchEvent:()=>expired++};globalThis.fetch=async()=>new Response('{}',{status:401});await assert.rejects(request('/auth/me'),e=>e.status===401);await assert.rejects(request('/auth/logout',{method:'POST'}),e=>e.status===401);assert.equal(expired,2)})
test('401 ao buscar CSRF expira sessão e não envia escrita',async()=>{configureSessionClient(true);let expired=0,calls=0;globalThis.window={dispatchEvent:()=>expired++};globalThis.fetch=async()=>{calls++;return new Response('{}',{status:401})};await assert.rejects(request('/produtos',{method:'POST',body:{}}),e=>e.status===401);assert.equal(calls,1);assert.equal(expired,1)})
