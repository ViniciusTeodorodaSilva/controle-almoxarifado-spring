import { test, expect } from '@playwright/test'
const backend = 'http://localhost:8081'
async function call(request, path, options = {}) { const response = await request.fetch(backend + path, options); expect(response.ok(), await response.text()).toBeTruthy(); return response.json() }
async function fixture(request, amount = 8, stock = 5) {
  const stamp = Date.now() + '-' + Math.random().toString(36).slice(2, 6)
  const unit = await call(request, '/unidades-medida', { method:'POST', data:{nome:'Peças B3 '+stamp,sigla:'B'+stamp,permiteFracionamento:false,ativo:true} })
  const category = await call(request, '/categorias', { method:'POST', data:{nome:'B3 '+stamp,ativo:true} })
  const product = await call(request, '/produtos', { method:'POST', data:{codigo:'B3-'+stamp,nome:'Válvula B3 '+stamp,ativo:true,categoriaMaterial:{id:category.id},unidadeMedidaConfigurada:{id:unit.id}} })
  const person = await call(request, '/funcionarios', { method:'POST', data:{nome:'Operador B3 '+stamp,matricula:stamp} })
  const warehouse = await call(request, '/almoxarifados', { method:'POST', data:{nome:'Central B3 '+stamp} })
  await call(request, '/estoques', {method:'POST',data:{produto:{id:product.id},almoxarifado:{id:warehouse.id}}})
  if(stock) await call(request, `/estoques/entrada?produtoId=${product.id}&almoxarifadoId=${warehouse.id}&quantidade=${stock}&solicitanteId=${person.id}&responsavelId=${person.id}`, {method:'PUT'})
  const solicitation = await call(request, `/solicitacoes?solicitanteId=${person.id}&almoxarifadoId=${warehouse.id}`, {method:'POST'})
  const item = await call(request, `/solicitacoes/${solicitation.id}/itens?produtoId=${product.id}&quantidade=${amount}`, {method:'POST'})
  return { product, person, warehouse, solicitation, item }
}
async function open(page, f) { await page.goto('/solicitacoes?solicitacaoId='+f.solicitation.id); await expect(page.getByRole('dialog').getByRole('heading',{name:'Solicitação #'+f.solicitation.id,exact:true})).toBeVisible() }
async function authorize(page, f) {
  await page.getByLabel('Responsável pela aprovação').selectOption(String(f.person.id)); await page.getByRole('button',{name:'Aprovar solicitação',exact:true}).click();await page.getByRole('button',{name:'Confirmar operação',exact:true}).click();await expect(page.getByRole('dialog').getByText('Aprovada',{exact:true})).toBeVisible()
  await page.getByLabel('Responsável pela separação').selectOption(String(f.person.id));await page.getByRole('button',{name:'Iniciar separação',exact:true}).click();await page.getByRole('button',{name:'Confirmar operação',exact:true}).click();await expect(page.getByRole('dialog').getByText('Em separação',{exact:true})).toBeVisible()
}
async function startFulfillment(page, f, amount) {
  await page.getByRole('button',{name:'Registrar atendimento',exact:true}).click();await page.getByLabel('Entregar item #'+f.item.id,{exact:true}).fill(String(amount));await page.getByLabel('Responsável pelo atendimento',{exact:true}).selectOption(String(f.person.id));await page.getByRole('button',{name:'Revisar atendimento',exact:true}).click()
}
async function confirmFulfillment(page) { const [response] = await Promise.all([page.waitForResponse(response => /\/solicitacoes\/\d+\/atendimentos$/.test(response.url()) && response.request().method() === 'POST'), page.getByRole('button',{name:'Confirmar atendimento',exact:true}).click()]); expect(response.ok(), await response.text()).toBeTruthy() }
async function stock(request,f) {return (await call(request,`/estoques/produto/${f.product.id}/almoxarifado/${f.warehouse.id}`)).quantidade}
test('B3: aprovação, separação, parcial, falta, necessidade, impressão, reposição e conclusão', async ({page,request})=>{
  const errors=[];page.on('pageerror',error=>errors.push(error.message));const f=await fixture(request)
  await page.setViewportSize({width:1440,height:1000});await open(page,f)
  await page.getByLabel('Responsável pela aprovação').selectOption(String(f.person.id));await page.getByRole('button',{name:'Aprovar solicitação',exact:true}).click();await page.getByRole('button',{name:'Confirmar operação',exact:true}).click();await expect(page.getByRole('dialog').getByText('Aprovada',{exact:true})).toBeVisible();expect(await stock(request,f)).toBe(5)
  await page.getByLabel('Responsável pela separação').selectOption(String(f.person.id));await page.getByRole('button',{name:'Iniciar separação',exact:true}).click();await page.getByRole('button',{name:'Confirmar operação',exact:true}).click();await expect(page.getByRole('dialog').getByText('Em separação',{exact:true})).toBeVisible();expect(await stock(request,f)).toBe(5)
  await startFulfillment(page,f,3);await expect(page.getByText('Confirmar atendimento?',{exact:true})).toBeVisible();await confirmFulfillment(page);await expect(page.getByRole('dialog').getByText('Parcialmente atendida',{exact:true})).toBeVisible();expect(await stock(request,f)).toBe(2)
  let op=await call(request,`/solicitacoes/${f.solicitation.id}/operacao`);expect(op.itens[0].quantidadePendente).toBe(5);expect(op.itens[0].quantidadeFaltante).toBe(3)
  await page.getByRole('button',{name:'Gerar necessidade',exact:true}).click();await page.getByLabel('Responsável pela necessidade').selectOption(String(f.person.id));await page.getByRole('button',{name:'Revisar necessidade',exact:true}).click();await page.getByRole('button',{name:'Confirmar necessidade',exact:true}).click();await expect(page.getByText(/Necessidade #\d+ registrada\./)).toBeVisible()
  const needs=await call(request,`/necessidades-compra?solicitacaoId=${f.solicitation.id}`);expect(needs).toHaveLength(1);expect(needs[0].quantidade).toBe(3)
  await page.screenshot({path:'test-results/b3-parcial-desktop.png'})
  await page.getByRole('link',{name:'Lista de separação',exact:true}).click();await expect(page.getByRole('heading',{name:'Lista de Separação',exact:true})).toBeVisible();await expect(page.getByText('B&S Engenharia',{exact:true})).toBeVisible()
  await page.evaluate(()=>{window.__besPrinted=false;window.print=()=>{window.__besPrinted=true}});await page.getByRole('button',{name:'Imprimir lista'}).click();expect(await page.evaluate(()=>window.__besPrinted)).toBeTruthy();await page.emulateMedia({media:'print'});await expect(page.getByRole('navigation',{name:'Ações do documento'})).not.toBeVisible();await page.pdf({path:'test-results/b3-lista-separacao.pdf',format:'A4',printBackground:true});await page.emulateMedia({media:'screen'})
  await call(request,`/estoques/entrada?produtoId=${f.product.id}&almoxarifadoId=${f.warehouse.id}&quantidade=3&solicitanteId=${f.person.id}&responsavelId=${f.person.id}`,{method:'PUT'})
  await page.getByRole('link',{name:'Voltar à solicitação'}).click();await startFulfillment(page,f,5);await confirmFulfillment(page);await expect(page.getByRole('dialog').getByText('Atendida',{exact:true})).toBeVisible();expect(await stock(request,f)).toBe(0)
  const history=await call(request,`/solicitacoes/${f.solicitation.id}/atendimentos`);expect(history.map(a=>a.itens[0].quantidade)).toEqual([3,5]);const moves=await call(request,`/solicitacoes/${f.solicitation.id}/movimentacoes`);expect(moves).toHaveLength(2);expect(moves.every(m=>m.atendimentoId&&m.responsavel.id===f.person.id)).toBeTruthy();expect((await call(request,`/necessidades-compra?solicitacaoId=${f.solicitation.id}`))[0].status).toBe('ABERTA')
  await page.getByRole('button',{name:'Fechar',exact:true}).click();await page.goto('/necessidades-compra?solicitacaoId='+f.solicitation.id);await page.getByLabel('Pesquisar necessidades').fill(f.product.codigo);await expect(page.locator('tbody tr')).toHaveCount(1);expect(errors).toEqual([])
})
test('B3: resposta perdida e duplo clique repetem a chave sem repetir saída',async({page,request})=>{
  const f=await fixture(request);await open(page,f);await authorize(page,f);let writes=0;const keys=[]
  await page.route(/\/api\/solicitacoes\/\d+\/atendimentos$/,async route=>{writes++;keys.push(route.request().headers()['idempotency-key']);if(writes===1){await route.fetch();await route.abort()}else await route.continue()})
  await startFulfillment(page,f,2);await page.getByRole('button',{name:'Confirmar atendimento',exact:true}).dblclick();await expect(page.getByText('A resposta não foi confirmada.',{exact:false})).toBeVisible();expect(writes).toBe(1);await expect(page.getByRole('button',{name:'Fechar',exact:true})).toBeDisabled();await page.getByRole('button',{name:'Conferir resultado',exact:true}).click();await expect(page.getByRole('dialog').getByText('Parcialmente atendida',{exact:true})).toBeVisible();expect(keys[0]).toBe(keys[1]);expect(await stock(request,f)).toBe(3);expect(await call(request,`/solicitacoes/${f.solicitation.id}/atendimentos`)).toHaveLength(1)
})
test('B3: erro de saldo concorrente é exibido sem gravar atendimento',async({page,request})=>{
  const f=await fixture(request);await open(page,f);await authorize(page,f);await startFulfillment(page,f,5)
  await call(request,`/estoques/saida?produtoId=${f.product.id}&almoxarifadoId=${f.warehouse.id}&quantidade=4&solicitanteId=${f.person.id}&responsavelId=${f.person.id}`,{method:'PUT'})
  await page.getByRole('button',{name:'Confirmar atendimento',exact:true}).click();await expect(page.getByText('Estoque insuficiente ou inválido',{exact:true})).toBeVisible();expect(await stock(request,f)).toBe(1);expect(await call(request,`/solicitacoes/${f.solicitation.id}/atendimentos`)).toHaveLength(0)
})
for(const width of [768,390]) test(`B3: atendimento, necessidade e documento a ${width}px sem overflow`,async({page,request})=>{
  const f=await fixture(request);await page.setViewportSize({width,height:900});await open(page,f);await authorize(page,f)
  await page.getByRole('button',{name:'Registrar atendimento',exact:true}).click();await expect(page.getByLabel('Entregar item #'+f.item.id,{exact:true})).toHaveValue('5');expect(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth)).toBeTruthy();await page.screenshot({path:`test-results/b3-atendimento-${width}.png`});await page.getByRole('button',{name:'Cancelar',exact:true}).click()
  await page.getByRole('button',{name:'Gerar necessidade',exact:true}).click();await expect(page.getByLabel('Responsável pela necessidade')).toBeVisible();expect(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth)).toBeTruthy();await page.getByRole('button',{name:'Cancelar',exact:true}).click();await page.getByRole('link',{name:'Lista de separação',exact:true}).click();await expect(page.getByRole('heading',{name:'Lista de Separação',exact:true})).toBeVisible();expect(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth)).toBeTruthy();await page.screenshot({path:`test-results/b3-documento-${width}.png`,fullPage:true})
})
