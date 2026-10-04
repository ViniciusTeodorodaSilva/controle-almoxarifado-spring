import { useCallback, useState } from 'react'
import { Plus, Pencil, Search } from 'lucide-react'
import { api } from '../api/client'
import { useResource } from '../hooks/useResource'
import { PageHeader, Card, ResourceView, DataTable, Badge, Modal, Field, Notice } from '../components/ui'
const settings = {
  categorias: { title:'Categorias', singular:'categoria', description:'Organize o catálogo com classificações compartilhadas.', fields:[['nome','Nome','text',true],['descricao','Descrição','textarea']], active:true },
  'unidades-medida': {title:'Unidades de medida',singular:'unidade de medida',description:'Unidades configuráveis para medir cada material corretamente.',fields:[['nome','Nome','text',true],['sigla','Sigla','text',true],['permiteFracionamento','Permite fracionamento','checkbox']],active:true},
  almoxarifados: {title:'Almoxarifados',singular:'almoxarifado',description:'Locais de armazenamento da sua operação.',fields:[['nome','Nome','text',true]]},
  funcionarios: {title:'Funcionários',singular:'funcionário',description:'Pessoas vinculadas às solicitações e movimentações.',fields:[['nome','Nome','text',true],['matricula','Matrícula','text',true],['funcao','Função','text']]},
}
export default function Registries({ name }) {
  const config = settings[name]
  const [search, setSearch] = useState(''), [active, setActive] = useState('')
  const loader = useCallback(signal => api.list(name, config.active ? {ativo:active} : undefined, signal), [name,active,config.active])
  const resource = useResource(loader)
  const [editing,setEditing] = useState(null), [notice,setNotice] = useState('')
  const columns = config.fields.map(([key,label,type])=>({key,label,render:row=>type==='checkbox' ? (row[key]?'Sim':'Não') : row[key] || '—'}))
  if(config.active) columns.push({key:'ativo',label:'Situação',render:row=><Badge value={row.ativo}/>})
  columns.push({key:'action',label:'Ações',render:row=><button className="btn text" onClick={()=>{setNotice('');setEditing({...row})}}><Pencil size={15}/>Editar</button>})
  return <><PageHeader eyebrow={config.active?'CATÁLOGO':'ESTRUTURA'} title={config.title} description={config.description}><button className="btn" onClick={()=>{setNotice('');setEditing({ativo:true,permiteFracionamento:false})}}><Plus size={17}/>Novo cadastro</button></PageHeader><Notice>{notice}</Notice>
    <Card><div className="filters"><div className="search-input"><Search size={17}/><input aria-label="Buscar registros" placeholder="Buscar por nome…" value={search} onChange={e=>setSearch(e.target.value)}/></div>{config.active && <select aria-label="Filtrar situação" value={active} onChange={e=>setActive(e.target.value)}><option value="">Todas as situações</option><option value="true">Ativos</option><option value="false">Inativos</option></select>}</div>
    <ResourceView resource={resource}>{rows=><DataTable columns={columns} rows={rows.filter(row=>(row.nome+' '+(row.sigla||'')+' '+(row.matricula||'')).toLocaleLowerCase('pt-BR').includes(search.toLocaleLowerCase('pt-BR')))}/>}</ResourceView></Card>
    {editing && <RegistryForm name={name} config={config} initial={editing} onClose={()=>setEditing(null)} onSaved={()=>{setEditing(null);setNotice('Cadastro salvo com sucesso.');resource.reload()}}/>}
  </>
}
function RegistryForm({ name,config,initial,onClose,onSaved }) {
  const [form,setForm]=useState(initial), [busy,setBusy]=useState(false),[error,setError]=useState('')
  const change=(key,value)=>setForm(previous=>({...previous,[key]:value}))
  async function save(event){event.preventDefault();setBusy(true);setError('');try{await api.save(name,form);onSaved()}catch(error){setError(error.message)}finally{setBusy(false)}}
  return <Modal title={`${form.id?'Editar':'Cadastrar'} ${config.singular}`} onClose={onClose} busy={busy}><form onSubmit={save}><Notice error>{error}</Notice><div className="form-grid">{config.fields.map(([key,label,type,required])=><Field label={label+(required?' *':'')} key={key}>{type==='checkbox'?<input type="checkbox" checked={!!form[key]} onChange={e=>change(key,e.target.checked)}/>:type==='textarea'?<textarea value={form[key]||''} maxLength={255} onChange={e=>change(key,e.target.value)}/>:<input required={required} value={form[key]||''} maxLength={key==='sigla'?64:255} onChange={e=>change(key,e.target.value)}/>}</Field>)}{config.active&&<Field label="Cadastro ativo"><input type="checkbox" checked={form.ativo} onChange={e=>change('ativo',e.target.checked)}/></Field>}</div><div className="form-actions"><button type="button" className="btn secondary" disabled={busy} onClick={onClose}>Cancelar</button><button className="btn" disabled={busy}>{busy?'Salvando…':'Salvar cadastro'}</button></div></form></Modal>
}
