import { useState } from 'react'
import { api } from '../api/client'
import { Modal, Field, Notice, quantity } from '../components/ui'
import { limitsInput, limitsError } from '../utils/stockIntelligence'
export default function StockLimitsForm({ stock, onClose, onSaved }) {
  const [minimum, setMinimum] = useState(stock.estoqueMinimo ?? ''), [maximum, setMaximum] = useState(stock.estoqueMaximo ?? '')
  const [error, setError] = useState(''), [busy, setBusy] = useState(false)
  async function save(event) {
    event.preventDefault()
    const input = limitsInput(minimum, maximum), issue = limitsError(input)
    if (issue) { setError(issue); return }
    setBusy(true); setError('')
    try { await api.stockLimits(stock.id, input); onSaved('Limites configurados com sucesso.') }
    catch (error) { setError(error.message) }
    finally { setBusy(false) }
  }
  return <Modal title="Configurar limites" busy={busy} onClose={onClose}><form onSubmit={save}>
    <p className="muted">{stock.produto?.codigo} · {stock.produto?.nome} · {stock.almoxarifado?.nome}</p>
    <div className="balance-summary"><span>Saldo atual</span><strong>{quantity(stock.quantidade)}</strong></div>
    <Notice error>{error}</Notice><fieldset className="form-section" disabled={busy}><legend>Referências de estoque</legend><div className="form-grid">
      <Field label="Estoque mínimo" hint="Mínimo: nível que dispara alerta."><input type="number" min="0" step="any" value={minimum} onChange={event => setMinimum(event.target.value)}/></Field>
      <Field label="Estoque máximo" hint="Máximo: nível utilizado como referência para reposição."><input type="number" min="0" step="any" value={maximum} onChange={event => setMaximum(event.target.value)}/></Field>
    </div><small className="muted">Deixe em branco para remover um limite. O saldo permanece igual.</small></fieldset>
    <div className="form-actions"><button type="button" className="btn secondary" disabled={busy} onClick={onClose}>Cancelar</button><button className="btn" disabled={busy}>{busy ? 'Salvando…' : 'Salvar limites'}</button></div>
  </form></Modal>
}
