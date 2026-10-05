import { test as base, expect } from '@playwright/test'
import fixtures from './security-users.json' with { type: 'json' }
export { expect, fixtures }
export async function loginApi(request, profile = 'ADMIN') {
 const person = fixtures[profile]
 const before = await request.get('http://localhost:8081/auth/csrf')
 expect(before.ok()).toBeTruthy()
 const {token} = await before.json()
 const login = await request.post('http://localhost:8081/auth/login', {headers:{'X-CSRF-TOKEN':token},data:{username:person.username,password:person.password}})
 expect(login.ok(), 'H2 login must succeed; start the H2 launcher with -WithTestUsers').toBeTruthy()
}
export function protectedRequest(request) {
 return new Proxy(request, {get(target, key) {
  if (['fetch','post','put','patch','delete','get','head'].includes(key)) return async (url, options = {}) => {
   const method = key === 'fetch' ? (options.method || 'GET') : key.toUpperCase()
   if (!['GET','HEAD','OPTIONS'].includes(method)) {
    const response = await target.get('http://localhost:8081/auth/csrf')
    const {token} = await response.json()
    options = {...options,headers:{...options.headers,'X-CSRF-TOKEN':token}}
   }
   return target[key](url, options)
  }
  const value=target[key];return typeof value==='function'?value.bind(target):value
 }})
}
export const test=base.extend({
 adminState:[async({playwright},use)=>{
  const request=await playwright.request.newContext()
  await loginApi(request)
  const safe=protectedRequest(request)
  const users=await (await safe.get('http://localhost:8081/usuarios')).json()
  for(const profile of ['GESTOR','ALMOXARIFE','CONSULTA'])if(!users.some(u=>u.username===fixtures[profile].username))expect((await safe.post('http://localhost:8081/usuarios',{data:fixtures[profile]})).ok()).toBeTruthy()
  // Session cookies stay in worker memory; no storageState file or browser localStorage.
  await use(await request.storageState());await request.dispose()
 },{scope:'worker'}],
 request:async({playwright,adminState},use)=>{const request=await playwright.request.newContext({storageState:adminState});await use(protectedRequest(request));await request.dispose()},
 page:async({page,adminState},use)=>{await page.context().addCookies(adminState.cookies);await use(page)}
})
