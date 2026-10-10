let csrfToken = null, csrfPromise = null
let sessionClient = typeof window !== 'undefined'
export function configureSessionClient(enabled = true) { sessionClient = enabled; resetCsrf() }
export function resetCsrf() { csrfToken = null; csrfPromise = null }
function expireSession(status, path) {
 if (status === 401 && path !== '/auth/login') { resetCsrf(); if (typeof window !== 'undefined') window.dispatchEvent(new Event('bes:session-expired')) }
}
const apiBase = () => (import.meta.env?.DEV ? '/api' : (import.meta.env?.VITE_API_URL || '/api')).replace(/\/$/, '')
export async function csrf() {
 if (csrfToken) return csrfToken
 if (!csrfPromise) csrfPromise = (async () => {
  const response = await fetch(apiBase() + '/auth/csrf', { credentials: 'include', headers: { Accept: 'application/json' }, cache: 'no-store' })
  if (!response.ok) { expireSession(response.status, '/auth/csrf'); throw new ApiError('Não foi possível iniciar a sessão.', response.status) }
  const value = await response.json()
  if (typeof value.token !== 'string' || !value.token) throw new ApiError('Resposta inesperada da API.')
  csrfToken = value.token; return csrfToken
 })().finally(() => { csrfPromise = null })
 return csrfPromise
}
export class ApiError extends Error {
  constructor(message, status = 0) { super(message); this.status = status }
}
export async function request(path, { method = 'GET', body, query, signal, headers = {} } = {}) {
  const params = new URLSearchParams()
  Object.entries(query || {}).forEach(([key, value]) => { if (value !== '' && value != null) params.set(key, value) })
  // Vite proxy in development; production can use a same-origin /api reverse proxy.
  const base = apiBase()
  let response
  try {
    const csrfHeaders = sessionClient && !['GET', 'HEAD', 'OPTIONS'].includes(method.toUpperCase()) ? { 'X-CSRF-TOKEN': await csrf() } : {}
    response = await fetch(`${base}${path}${params.size ? '?' + params : ''}`, {
      method, signal, credentials: 'include', headers: { ...headers, ...csrfHeaders, Accept: 'application/json', ...(body !== undefined ? { 'Content-Type': 'application/json' } : {}) },
      ...(body !== undefined ? { body: JSON.stringify(body) } : {})
    })
  } catch (error) {
    if (error.name === 'AbortError' || error instanceof ApiError) throw error
    throw new ApiError('Não foi possível conectar à BES. Confira se o backend está em execução e tente novamente.')
  }
  const text = await response.text()
  let data = null
  try { data = text ? JSON.parse(text) : null } catch { /* Proxy/offline HTML is not an API response. */ }
  if (!response.ok) {
    expireSession(response.status, path)
    const fallback = { 401: 'Sua sessão expirou. Entre novamente.', 403: 'Você não tem permissão para esta operação.', 400: 'Confira os dados informados.', 404: 'Registro não encontrado.', 409: 'A operação entrou em conflito com o estado atual.' }
    throw new ApiError(data?.mensagem || fallback[response.status] || 'Não foi possível concluir a operação. Tente novamente.', response.status)
  }
  if (text && data === null) throw new ApiError('Resposta inesperada da API. Confira a configuração do servidor.', response.status)
  return data
}
export const api = {
  requestOperation: (id, signal) => request(`/solicitacoes/${id}/operacao`, { signal }),
  separate: (id, responsavelId) => request(`/solicitacoes/${id}/iniciar-separacao`, { method: 'PUT', query: { responsavelId } }),
  fulfill: (id, body, key) => request(`/solicitacoes/${id}/atendimentos`, { method: 'POST', body, headers: { 'Idempotency-Key': key } }),
  fulfillments: (id, signal) => request(`/solicitacoes/${id}/atendimentos`, { signal }),
  purchaseNeeds: (query, signal) => request('/necessidades-compra', { query, signal }),
  createPurchaseNeed: (body, key) => request('/necessidades-compra', { method: 'POST', body, headers: { 'Idempotency-Key': key } }),
  stockLimits: (id, body) => request(`/estoques/${id}/limites`, { method: 'PUT', body }),
  stockAlerts: (query, signal) => request('/estoques/alertas', { query, signal }),
  restock: (query, signal) => request('/estoques/reposicoes', { query, signal }),
  transfers: (query, signal) => request('/transferencias', { query, signal }),
  createTransfer: (body, key) => request('/transferencias', { method: 'POST', body, headers: key ? { 'Idempotency-Key': key } : {} }),
  transferMovements: (id, signal) => request(`/transferencias/${id}/movimentacoes`, { signal }),
  list: (resource, query, signal) => request(`/${resource}`, { query, signal }),
  get: (resource, id, signal) => request(`/${resource}/${id}`, { signal }),
  save: (resource, data) => request(`/${resource}${data.id ? '/' + data.id : ''}`, { method: data.id ? 'PUT' : 'POST', body: data }),
  searchProducts: (query, signal) => request('/produtos/busca', { query, signal }),
  equivalents: termo => request('/produtos/equivalentes', { query: { termo } }),
  approve: (id, responsavelId) => request(`/solicitacoes/${id}/aprovar`, { method: 'PUT', query: { responsavelId } }),
  reject: id => request(`/solicitacoes/${id}/rejeitar`, { method: 'PUT' }),
  createRequest: (solicitanteId, almoxarifadoId, contexto = {}) => request('/solicitacoes', { method: 'POST', query: { solicitanteId, almoxarifadoId, ...contexto } }),
  addRequestItem: (id, produtoId, quantidade) => request(`/solicitacoes/${id}/itens`, { method: 'POST', query: { produtoId, quantidade } }),
  stock: (produtoId, almoxarifadoId, signal) => request(`/estoques/produto/${produtoId}/almoxarifado/${almoxarifadoId}`, { signal }),
  createStock: (produtoId, almoxarifadoId) => request('/estoques', { method: 'POST', body: { produto: { id: produtoId }, almoxarifado: { id: almoxarifadoId }, quantidade: 0 } }),
  moveStock: (type, query, key) => request(`/estoques/${type}`, { method: 'PUT', query, headers: key ? { 'Idempotency-Key': key } : {} }),
  requests: (status, signal) => request(status ? `/solicitacoes/status/${status}` : '/solicitacoes', { signal }),
}
