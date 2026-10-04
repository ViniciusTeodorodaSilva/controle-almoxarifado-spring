export class ApiError extends Error {
  constructor(message, status = 0) { super(message); this.status = status }
}
export async function request(path, { method = 'GET', body, query, signal } = {}) {
  const params = new URLSearchParams()
  Object.entries(query || {}).forEach(([key, value]) => { if (value !== '' && value != null) params.set(key, value) })
  // Vite proxy in development; production can use a same-origin /api reverse proxy.
  const base = (import.meta.env?.DEV ? '/api' : (import.meta.env?.VITE_API_URL || '/api')).replace(/\/$/, '')
  let response
  try {
    response = await fetch(`${base}${path}${params.size ? '?' + params : ''}`, {
      method, signal, headers: { Accept: 'application/json', ...(body !== undefined ? { 'Content-Type': 'application/json' } : {}) },
      ...(body !== undefined ? { body: JSON.stringify(body) } : {})
    })
  } catch (error) {
    if (error.name === 'AbortError') throw error
    throw new ApiError('Não foi possível conectar à BES. Confira se o backend está em execução e tente novamente.')
  }
  const text = await response.text()
  let data = null
  try { data = text ? JSON.parse(text) : null } catch { /* Proxy/offline HTML is not an API response. */ }
  if (!response.ok) {
    const fallback = { 400: 'Confira os dados informados.', 404: 'Registro não encontrado.', 409: 'A operação entrou em conflito com o estado atual.' }
    throw new ApiError(data?.mensagem || fallback[response.status] || 'Não foi possível concluir a operação. Tente novamente.', response.status)
  }
  if (text && data === null) throw new ApiError('Resposta inesperada da API. Confira a configuração do servidor.', response.status)
  return data
}
export const api = {
  stockLimits: (id, body) => request(`/estoques/${id}/limites`, { method: 'PUT', body }),
  stockAlerts: (query, signal) => request('/estoques/alertas', { query, signal }),
  restock: (query, signal) => request('/estoques/reposicoes', { query, signal }),
  transfers: (query, signal) => request('/transferencias', { query, signal }),
  createTransfer: body => request('/transferencias', { method: 'POST', body }),
  transferMovements: (id, signal) => request(`/transferencias/${id}/movimentacoes`, { signal }),
  list: (resource, query, signal) => request(`/${resource}`, { query, signal }),
  get: (resource, id) => request(`/${resource}/${id}`),
  save: (resource, data) => request(`/${resource}${data.id ? '/' + data.id : ''}`, { method: data.id ? 'PUT' : 'POST', body: data }),
  searchProducts: (query, signal) => request('/produtos/busca', { query, signal }),
  equivalents: termo => request('/produtos/equivalentes', { query: { termo } }),
  approve: (id, responsavelId) => request(`/solicitacoes/${id}/aprovar`, { method: 'PUT', query: { responsavelId } }),
  reject: id => request(`/solicitacoes/${id}/rejeitar`, { method: 'PUT' }),
  createRequest: (solicitanteId, almoxarifadoId) => request('/solicitacoes', { method: 'POST', query: { solicitanteId, almoxarifadoId } }),
  addRequestItem: (id, produtoId, quantidade) => request(`/solicitacoes/${id}/itens`, { method: 'POST', query: { produtoId, quantidade } }),
  stock: (produtoId, almoxarifadoId, signal) => request(`/estoques/produto/${produtoId}/almoxarifado/${almoxarifadoId}`, { signal }),
  createStock: (produtoId, almoxarifadoId) => request('/estoques', { method: 'POST', body: { produto: { id: produtoId }, almoxarifado: { id: almoxarifadoId }, quantidade: 0 } }),
  moveStock: (type, query) => request(`/estoques/${type}`, { method: 'PUT', query }),
  requests: (status, signal) => request(status ? `/solicitacoes/status/${status}` : '/solicitacoes', { signal }),
}
