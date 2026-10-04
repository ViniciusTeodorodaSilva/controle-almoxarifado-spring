import { test } from 'node:test'
import assert from 'node:assert/strict'
import { request, api, ApiError } from '../src/api/client.js'
test('usa mensagem estruturada em 400, 404 e 409', async () => {
 for (const status of [400,404,409]) {
  globalThis.fetch=async()=>new Response(JSON.stringify({mensagem:'Regra do backend'}),{status})
  await assert.rejects(request('/produtos'),error=>error instanceof ApiError&&error.status===status&&error.message==='Regra do backend')
 }
})
test('conexão indisponível não gera dados fictícios',async()=>{
 globalThis.fetch=async()=>{throw new TypeError('offline')}
 await assert.rejects(request('/produtos'),error=>error.status===0&&error.message.includes('conectar'))
})
test('aprovação usa PUT e responsável nos parâmetros',async()=>{
 let url,options
 globalThis.fetch=async(u,o)=>{url=u;options=o;return new Response(JSON.stringify({status:'APROVADA'}))}
 await api.approve(5,9)
 assert.equal(url,'/api/solicitacoes/5/aprovar?responsavelId=9');assert.equal(options.method,'PUT')
})
test('busca codifica texto e conserva filtro booleano false',async()=>{
 let url
 globalThis.fetch=async u=>{url=u;return new Response('[]')}
 await api.searchProducts({termo:'A & B',ativo:false,categoriaId:''})
 assert.equal(url,'/api/produtos/busca?termo=A+%26+B&ativo=false')
})
test('HTML inesperado não é tratado como uma lista válida',async()=>{
 globalThis.fetch=async()=>new Response('<html>Proxy</html>')
 await assert.rejects(request('/produtos'),/Resposta inesperada/)
})
