import Brand from './Brand'
export default function OperationalDocument({ title, reference, generatedAt, metadata, children }) {
  return <article className="operational-document"><header className="document-header"><div className="document-brand"><Brand variant="document"/><span>Plataforma BES</span></div><div><h1>{title}</h1><strong>{reference}</strong><p>Emitida em {generatedAt}</p></div></header><dl className="document-metadata">{metadata.map(([label, value]) => <div key={label}><dt>{label}</dt><dd>{value || '—'}</dd></div>)}</dl>{children}<footer className="document-footer"><span>B&S Engenharia · Plataforma BES</span><span>{reference}</span></footer></article>
}
