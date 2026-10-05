import { test, expect, fixtures } from './security-support.js'
const base = 'http://localhost:8081'
async function create(request, path, data) {const r=await request.post(base+path,{data});expect(r.ok(),path+' status '+r.status()).toBeTruthy();return r.json()}
async function fixture(request) {
 const stamp=Date.now()+Math.random().toString(36).slice(2,6)
 const product=await create(request,'/produtos',{nome:'Compra H2 '+stamp,unidadeMedida:'UN',codigo:'PC-'+stamp})
 const warehouse=await create(request,'/almoxarifados',{nome:'Destino compra '+stamp})
 const person=await create(request,'/funcionarios',{nome:'Responsável compra '+stamp,matricula:'PC-'+stamp})
 const supplier=await create(request,'/fornecedores',{nome:'Fornecedor fictício '+stamp,tipoPessoa:'PJ',ativo:true})
 return {product,warehouse,person,supplier}
}
async function order(request,f,qty=5){return create(request,'/pedidos-compra',{fornecedorId:f.supplier.id,almoxarifadoId:f.warehouse.id,itens:[{produtoId:f.product.id,quantidade:qty,valorUnitario:12.3456,paraEstoque:true,alocacoes:[]}]})}
async function approve(request,p){expect((await request.put(base+`/pedidos-compra/${p.id}/submeter`)).ok()).toBeTruthy();expect((await request.put(base+`/pedidos-compra/${p.id}/aprovar`)).ok()).toBeTruthy()}
async function uiReceipt(page,f,qty){await page.getByRole('button',{name:'Registrar recebimento',exact:true}).click();await page.getByLabel('Responsável físico *').selectOption(String(f.person.id));await page.getByLabel('Receber '+f.product.nome,{exact:true}).fill(String(qty));await page.getByRole('button',{name:'Revisar recebimento',exact:true}).click();await expect(page.getByText('Esta ação adicionará fisicamente ao estoque')).toBeVisible();await page.getByRole('button',{name:'Confirmar entrada no estoque',exact:true}).click();await expect(page.getByRole('dialog')).not.toBeVisible()}
test('ciclo humano de necessidade, compra, recebimento parcial e atendimento posterior',async({page,request})=>{
 const f=await fixture(request)
 const s=await (await request.post(base+'/solicitacoes?solicitanteId='+f.person.id+'&almoxarifadoId='+f.warehouse.id)).json()
 const item=await (await request.post(base+`/solicitacoes/${s.id}/itens?produtoId=${f.product.id}&quantidade=5`)).json()
 expect((await request.put(base+`/solicitacoes/${s.id}/aprovar?responsavelId=${f.person.id}`)).ok()).toBeTruthy()
 const n=await (await request.post(base+'/necessidades-compra',{headers:{'Idempotency-Key':crypto.randomUUID()},data:{itemSolicitacaoId:item.id,responsavelId:f.person.id}})).json()
 await page.goto('/necessidades-compra?solicitacaoId='+s.id)
 await page.getByLabel('Selecionar necessidade '+n.id,{exact:true}).check()
 await page.getByRole('button',{name:'Criar pedido das necessidades selecionadas'}).click()
 await expect(page.getByRole('dialog')).toBeVisible()
 await page.getByLabel('Fornecedor *',{exact:true}).selectOption(String(f.supplier.id))
 await page.getByLabel('Valor unitário (R$) *').fill('12.3456')
 await page.getByRole('button',{name:'Salvar rascunho',exact:true}).click()
 await expect(page).toHaveURL(/pedidos-compra\/\d+$/)
 const id=Number(page.url().split('/').pop())
 await page.getByRole('button',{name:'Submeter para aprovação',exact:true}).click()
 await page.getByRole('button',{name:'Confirmar submissão',exact:true}).click()
 await expect(page.getByRole('dialog')).not.toBeVisible()
 await page.getByRole('button',{name:'Aprovar pedido',exact:true}).click()
 await expect(page.getByText('Nenhum material será adicionado ao estoque.')).toBeVisible()
 await page.getByRole('button',{name:'Confirmar aprovação',exact:true}).click()
 await expect(page.getByRole('dialog')).not.toBeVisible()
 await uiReceipt(page,f,2)
 await expect(page.getByText('Parcialmente recebido',{exact:true})).toBeVisible()
 let need=await (await request.get(base+'/necessidades-compra/'+n.id)).json()
 expect(need.compra.quantidadeRecebida).toBe(2);expect(need.status).toBe('EM_COMPRA')
 await uiReceipt(page,f,3)
 await expect(page.locator('.badge-recebido')).toBeVisible()
 need=await (await request.get(base+'/necessidades-compra/'+n.id)).json();expect(need.status).toBe('ATENDIDA')
 let operation=await (await request.get(base+`/solicitacoes/${s.id}/operacao`)).json();expect(operation.status).toBe('APROVADA');expect(operation.itens[0].quantidadeAtendida).toBe(0)
 expect((await request.put(base+`/solicitacoes/${s.id}/iniciar-separacao?responsavelId=${f.person.id}`)).ok()).toBeTruthy()
 expect((await request.post(base+`/solicitacoes/${s.id}/atendimentos`,{headers:{'Idempotency-Key':crypto.randomUUID()},data:{responsavelId:f.person.id,itens:[{itemSolicitacaoId:item.id,quantidade:5}]}})).ok()).toBeTruthy()
 operation=await (await request.get(base+`/solicitacoes/${s.id}/operacao`)).json();expect(operation.status).toBe('ATENDIDA')
 const p=await (await request.get(base+'/pedidos-compra/'+id)).json();expect(p.status).toBe('RECEBIDO')
})
test('fornecedor cadastrado e inativado preserva histórico',async({page,request})=>{
 await page.goto('/fornecedores');await page.getByRole('button',{name:'Novo fornecedor',exact:true}).click()
 const name='Fornecedor UI fictício '+Date.now();await page.getByLabel('Nome / razão social *').fill(name);await page.getByLabel('Email',{exact:true}).fill('fixture@example.invalid');await page.getByRole('button',{name:'Salvar fornecedor',exact:true}).click();await expect(page.getByRole('dialog')).not.toBeVisible();await page.getByLabel('Pesquisar fornecedores').fill(name);await page.getByRole('button',{name:'Editar '+name,exact:true}).click();await page.getByLabel('Situação',{exact:true}).selectOption('false');await page.getByRole('button',{name:'Salvar fornecedor',exact:true}).click();await expect(page.getByText('Inativo',{exact:true})).toBeVisible();await expect(page.getByRole('link',{name:'Compras e preços'})).toBeVisible()
})
test('rascunho manual exige declaração de estoque e pode ser editado e cancelado',async({page,request})=>{
 const f=await fixture(request),p=await order(request,f)
 await page.goto('/pedidos-compra/'+p.id);await page.getByRole('button',{name:'Editar rascunho',exact:true}).click();await page.getByLabel('Quantidade pedida *').fill('6');await page.getByRole('button',{name:'Salvar rascunho',exact:true}).click();await expect(page.getByRole('dialog')).not.toBeVisible();expect((await (await request.get(base+'/pedidos-compra/'+p.id)).json()).itens[0].quantidade).toBe(6)
 await page.getByRole('button',{name:'Cancelar pedido',exact:true}).click();await page.getByLabel('Motivo do cancelamento *').fill('Dispensado em teste H2');await page.getByRole('button',{name:'Confirmar cancelamento',exact:true}).click();await expect(page.getByText('Cancelado',{exact:true})).toBeVisible();await expect(page.getByRole('button',{name:'Registrar recebimento',exact:true})).toHaveCount(0)
})
test('resposta perdida preserva chave e reenvio não duplica entrada',async({page,request})=>{
 const f=await fixture(request),p=await order(request,f,4);await approve(request,p)
 let dropped=false;const keys=[]
 await page.route('**/pedidos-compra/*/recebimentos',async route=>{if(route.request().method()!=='POST')return route.continue();keys.push(route.request().headers()['idempotency-key']);if(!dropped){dropped=true;const response=await route.fetch();expect(response.ok()).toBeTruthy();await route.abort('failed')}else await route.continue()})
 await page.goto('/pedidos-compra/'+p.id);await page.getByRole('button',{name:'Registrar recebimento',exact:true}).click();await page.getByLabel('Responsável físico *').selectOption(String(f.person.id));await page.getByLabel('Receber '+f.product.nome,{exact:true}).fill('2');await page.getByRole('button',{name:'Revisar recebimento',exact:true}).click();await page.getByRole('button',{name:'Confirmar entrada no estoque',exact:true}).click();await expect(page.getByRole('button',{name:'Reenviar mesma confirmação'})).toBeVisible();await expect(page.getByLabel('Receber '+f.product.nome,{exact:true})).toBeDisabled();await page.getByRole('button',{name:'Reenviar mesma confirmação'}).click();await expect(page.getByRole('dialog')).not.toBeVisible();expect(keys.length).toBe(2);expect(keys[0]).toBe(keys[1]);const history=await (await request.get(base+`/pedidos-compra/${p.id}/recebimentos`)).json();expect(history.totalElements).toBe(1);const stock=await (await request.get(base+`/estoques/produto/${f.product.id}/almoxarifado/${f.warehouse.id}`)).json();expect(stock.quantidade).toBe(2)
})
test('duplo clique de confirmação não duplica recebimento',async({page,request})=>{
 const f=await fixture(request),p=await order(request,f,4);await approve(request,p);await page.goto('/pedidos-compra/'+p.id);await page.getByRole('button',{name:'Registrar recebimento',exact:true}).click();await page.getByLabel('Responsável físico *').selectOption(String(f.person.id));await page.getByLabel('Receber '+f.product.nome,{exact:true}).fill('1');await page.getByRole('button',{name:'Revisar recebimento',exact:true}).click();await page.getByRole('button',{name:'Confirmar entrada no estoque',exact:true}).evaluate(button=>{button.click();button.click()});await expect(page.getByRole('dialog')).not.toBeVisible();expect((await (await request.get(base+`/pedidos-compra/${p.id}/recebimentos`)).json()).totalElements).toBe(1)
})
for(const [width,height] of [[1440,900],[768,1024],[390,844]])test('compras e documentos sem overflow '+width,async({page,request})=>{
 const f=await fixture(request),p=await order(request,f);await approve(request,p);const r=await (await request.post(base+`/pedidos-compra/${p.id}/recebimentos`,{headers:{'Idempotency-Key':crypto.randomUUID()},data:{almoxarifadoId:f.warehouse.id,responsavelId:f.person.id,itens:[{itemPedidoId:p.itens[0].id,quantidade:2}]}})).json()
 await page.setViewportSize({width,height})
 for(const path of ['/fornecedores','/necessidades-compra','/pedidos-compra','/pedidos-compra/'+p.id,`/pedidos-compra/${p.id}/documento`,`/recebimentos-compra/${r.id}/documento`]){await page.goto(path);await expect(page.getByRole('heading',{level:1})).toBeVisible();expect(await page.evaluate(()=>document.documentElement.scrollWidth<=window.innerWidth)).toBeTruthy()}
 await page.emulateMedia({media:'print'});await expect(page.locator('.document-actions')).toBeHidden();await expect(page.locator('.operational-document')).toContainText('Comprovante de recebimento');expect(await page.locator('.sidebar').count()).toBe(0)
 await page.emulateMedia({media:'screen'});await page.goto(`/pedidos-compra/${p.id}/documento`);await expect(page.locator('.operational-document')).toContainText(p.numero);await expect(page.locator('.operational-document')).toContainText(f.product.nome)
})
for(const profile of ['GESTOR','ALMOXARIFE','CONSULTA'])test('ações de compra respeitam perfil '+profile,async({browser,request})=>{
 const context=await browser.newContext({baseURL:'http://localhost:5173'}),page=await context.newPage()
 const f=await fixture(request),p=await order(request,f);await approve(request,p);await page.goto('/login');await expect(page.getByRole('heading',{name:'Entrar',exact:true})).toBeVisible();await page.getByLabel('Usuário',{exact:true}).fill(fixtures[profile].username);await page.getByLabel('Senha',{exact:true}).fill(fixtures[profile].password);await page.getByRole('button',{name:'Entrar',exact:true}).click();await expect(page).toHaveURL(/dashboard$/);await page.goto('/pedidos-compra/'+p.id);await expect(page.getByRole('heading',{name:'Pedido de compra',exact:true})).toBeVisible();await expect(page.getByRole('button',{name:'Registrar recebimento',exact:true})).toHaveCount(profile==='ALMOXARIFE'?1:0);await expect(page.getByRole('button',{name:'Cancelar pedido',exact:true})).toHaveCount(profile==='GESTOR'?1:0);await page.goto('/fornecedores');await expect(page.getByRole('button',{name:'Novo fornecedor',exact:true})).toHaveCount(profile==='GESTOR'?1:0);await context.close()
})
