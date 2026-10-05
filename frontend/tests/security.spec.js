import { test, expect, fixtures, loginApi, protectedRequest } from './security-support.js'
const server='http://localhost:8081'
test('cookie real é HttpOnly e não há autenticação no storage ou URL',async({page})=>{
 await page.goto('/produtos');await expect(page.getByRole('heading',{name:'Produtos',exact:true})).toBeVisible()
 const cookies=await page.context().cookies();const session=cookies.find(c=>c.name==='BESSESSION')
 expect(Boolean(session)).toBeTruthy();expect(session.httpOnly).toBe(true);expect(session.sameSite).toBe('Lax');expect(session.path).toBe('/');expect(session.secure).toBe(false)
 const stored=await page.evaluate(()=>({local:Object.keys(localStorage),session:Object.keys(sessionStorage),cookieNames:document.cookie.split(';').map(c=>c.split('=')[0].trim())}))
 expect(stored.local).toEqual([]);expect(stored.session).toEqual([]);expect(stored.cookieNames).not.toContain('BESSESSION')
 expect(await page.locator('a[href*=";jsessionid"]').count()).toBe(0)
 // The same live session id must not authenticate through servlet URL rewriting.
 const anonymous=await page.context().browser().newContext()
 try{const response=await anonymous.request.get(server+'/auth/me;jsessionid='+encodeURIComponent(session.value));expect(response.status()).toBeGreaterThanOrEqual(400)}finally{await anonymous.close()}
})
let states={}
test.beforeAll(async({playwright,adminState})=>{
 states.ADMIN=adminState
 for(const role of ['GESTOR','ALMOXARIFE','CONSULTA']){
  const api=await playwright.request.newContext();await loginApi(api,role);states[role]=await api.storageState();await api.dispose()
 }
})
async function identity(page,role){await page.context().clearCookies();await page.context().addCookies(states[role].cookies)}
test('login real, identidade de /me e logout invalidam a sessão',async({page})=>{
 await page.context().clearCookies();await page.goto('/produtos')
 await expect(page.getByRole('heading',{name:'Entrar',exact:true})).toBeVisible()
 await page.getByLabel('Usuário',{exact:true}).fill(fixtures.ADMIN.username)
 await page.getByLabel('Senha',{exact:true}).fill(fixtures.ADMIN.password)
 await page.getByRole('button',{name:'Entrar',exact:true}).click()
 await expect(page.getByRole('heading',{name:'Produtos',exact:true})).toBeVisible()
 await expect(page.getByText('Administrador inicial',{exact:true})).toBeVisible()
 await page.getByRole('button',{name:'Sair',exact:true}).click()
 await expect(page.getByRole('heading',{name:'Entrar',exact:true})).toBeVisible()
 await page.goto('/estoques');await expect(page.getByRole('heading',{name:'Entrar',exact:true})).toBeVisible()
})
test('login inválido retorna mensagem genérica sem enumerar usuário',async({page})=>{
 await page.context().clearCookies();await page.goto('/login')
 await page.getByLabel('Usuário',{exact:true}).fill('h2.inexistente')
 await page.getByLabel('Senha',{exact:true}).fill('H2-only-invalid-passphrase!')
 await page.getByRole('button',{name:'Entrar',exact:true}).click()
 await expect(page.getByRole('alert')).toHaveText('Usuário ou senha inválidos.')
 await expect(page.getByLabel('Senha',{exact:true})).toHaveValue('')
})
test('CONSULTA lê, não vê ações administrativas e recebe 403 HTTP',async({page,playwright})=>{
 await identity(page,'CONSULTA');await page.goto('/produtos')
 await expect(page.getByRole('heading',{name:'Produtos',exact:true})).toBeVisible()
 await expect(page.getByRole('button',{name:'Novo produto'})).toHaveCount(0)
 await expect(page.getByRole('link',{name:'Usuários',exact:true})).toHaveCount(0)
 await page.goto('/usuarios');await expect(page.getByText('Você não tem permissão para acessar esta página.')).toBeVisible()
 const api=await playwright.request.newContext({storageState:states.CONSULTA});const safe=protectedRequest(api)
 expect((await safe.post(server+'/produtos',{data:{nome:'H2 forbidden'}})).status()).toBe(403)
 expect((await safe.put(server+'/usuarios/1',{data:{perfil:'ADMIN'}})).status()).toBe(403);await api.dispose()
})
test('ALMOXARIFE vê operações de estoque, não gerencia usuários ou catálogo',async({page,playwright})=>{
 await identity(page,'ALMOXARIFE');await page.goto('/estoques')
 await expect(page.getByRole('button',{name:'Registrar entrada'})).toBeVisible()
 await expect(page.getByRole('button',{name:'Registrar saída'})).toBeVisible()
 await page.goto('/produtos');await expect(page.getByRole('button',{name:'Novo produto'})).toHaveCount(0)
 const api=await playwright.request.newContext({storageState:states.ALMOXARIFE});expect((await api.get(server+'/usuarios')).status()).toBe(403);await api.dispose()
})
test('GESTOR acessa auditoria e não altera catálogo ou usuários',async({page,playwright})=>{
 await identity(page,'GESTOR');await page.goto('/solicitacoes');await expect(page.getByRole('heading',{name:'Solicitações',exact:true})).toBeVisible()
 const api=await playwright.request.newContext({storageState:states.GESTOR});expect((await api.get(server+'/auditoria')).ok()).toBeTruthy()
 expect((await protectedRequest(api).post(server+'/categorias',{data:{nome:'H2 forbidden'}})).status()).toBe(403);await api.dispose()
})
test('ADMIN cria usuário inativo e autenticação continua bloqueada',async({page,playwright})=>{
 await page.goto('/usuarios');await page.getByRole('button',{name:'Novo usuário',exact:true}).click()
 const username='h2.inativo.'+Date.now()
 await page.getByRole('dialog').getByLabel('Usuário',{exact:true}).fill(username)
 await page.getByLabel('Nome de exibição').fill('Usuário fictício H2')
 await page.getByLabel('Senha inicial').fill(fixtures.CONSULTA.password)
 await page.getByLabel('Usuário ativo').uncheck()
 await page.getByRole('button',{name:'Salvar usuário'}).click()
 await expect(page.getByText('Usuário salvo com sucesso.')).toBeVisible()
 await expect(page.getByRole('row').filter({hasText:username})).toContainText('Inativo')
 const api=await playwright.request.newContext();const {token}=await (await api.get(server+'/auth/csrf')).json()
 expect((await api.post(server+'/auth/login',{headers:{'X-CSRF-TOKEN':token},data:{username,password:fixtures.CONSULTA.password}})).status()).toBe(401);await api.dispose()
})
for(const width of [1440,768,390])test('login e usuários sem overflow em '+width+'px',async({page})=>{
 await page.setViewportSize({width,height:900});await page.context().clearCookies();await page.goto('/login')
 await expect(page.getByRole('heading',{name:'Entrar',exact:true})).toBeVisible()
 expect(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth)).toBeTruthy()
 await page.getByLabel('Usuário',{exact:true}).focus();await page.keyboard.press('Tab');await expect(page.getByLabel('Senha',{exact:true})).toBeFocused()
 await page.screenshot({path:'test-results/security-login-'+width+'.png',fullPage:true})
 await identity(page,'ADMIN');await page.goto('/usuarios');await expect(page.getByRole('heading',{name:'Usuários',exact:true})).toBeVisible()
 expect(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth)).toBeTruthy()
 await page.getByRole('button',{name:'Novo usuário',exact:true}).click();await expect(page.getByRole('dialog')).toBeVisible()
 expect(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth)).toBeTruthy()
 await page.screenshot({path:'test-results/security-users-'+width+'.png',fullPage:true})
})

test('sessão revogada ao sair retorna ao login mesmo sem CSRF em cache',async({page,request,playwright})=>{
 const data={...fixtures.CONSULTA,username:'h2.revogado.'+Date.now(),nomeExibicao:'Usuário revogado H2'}
 const response=await request.post(server+'/usuarios',{data});expect(response.ok()).toBeTruthy();const user=await response.json()
 const api=await playwright.request.newContext();const {token}=await(await api.get(server+'/auth/csrf')).json()
 expect((await api.post(server+'/auth/login',{headers:{'X-CSRF-TOKEN':token},data:{username:data.username,password:data.password}})).ok()).toBeTruthy()
 const state=await api.storageState();await page.context().clearCookies();await page.context().addCookies(state.cookies)
 await page.goto('/produtos');await expect(page.getByRole('heading',{name:'Produtos',exact:true})).toBeVisible()
 const {password,...update}=data;expect((await request.put(server+'/usuarios/'+user.id,{data:{...update,ativo:false}})).ok()).toBeTruthy()
 await page.getByRole('button',{name:'Sair',exact:true}).click();await expect(page.getByRole('heading',{name:'Entrar',exact:true})).toBeVisible();await api.dispose()
})
