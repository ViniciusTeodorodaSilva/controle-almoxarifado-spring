import { useState } from 'react'
// Official optional assets only: no generated logo or approximation.
const assets = import.meta.glob('../assets/brand/*.png', { eager: true, query: '?url', import: 'default' })
export default function Brand({ variant = 'sidebar' }) {
  const [failed, setFailed] = useState(false)
  const asset = assets[`../assets/brand/bes-logo-${variant === 'sidebar' ? 'sidebar' : 'full'}.png`]
  return asset && !failed ? <img className={`brand-logo brand-logo-${variant}`} src={asset} alt="B&S Engenharia" onError={() => setFailed(true)}/> : variant === 'document' ? <strong className="document-brand-text">B&S Engenharia</strong> : <div className="brand-mark">BES<span>■</span></div>
}
