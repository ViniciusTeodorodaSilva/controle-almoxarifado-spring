import { useCallback, useEffect, useState } from 'react'
import { Plus, Pencil, Search } from 'lucide-react'
import { api } from '../api/client'
import { useResource } from '../hooks/useResource'
import { PageHeader, Card, ResourceView, DataTable, Badge, Modal, Field, Notice } from '../components/ui'
export default function Products(){
 const [term,setTerm]=useState(''),[active,setActive]=useState(''),[category,setCategory]=useState(''),[editing,setEditing]=useState(null),[notice,setNotice]=useState('')
 const [debounced,setDebounced]=useState('')
 useEffect(()=>{const timer=setTimeout(()=>setDebounced(term),300);return()=>clearTimeout(timer)},[term])
 const loader=useCallback(signal=>api.searchProducts({termo:debounced,ativo:active,categoriaId:category},signal),[debounced,active,category])
 const resource=useResource(loader)
 const categories=useResource(useCallback(signal=>api.list('categorias',null,signal),[]))
 return <><PageHeader eyebrow="CATÁLOGO MESTRE" title="Produtos" description="Catálogo mestre dos materiais utilizados pela operação."><button className="btn" onClick={()=>{setNotice('');setEditing({nome:'',ativo:true})}}><Plus size={17}/>Novo produto</button></PageHeader><Notice>{notice}</Notice><Card>
  <div className="filters"><div className="search-input"><Search size={17}/><input aria-label="Buscar produtos" value={term} onChange={e=>setTerm(e.target.value)} placeholder="Código, nome, descrição ou especificação…"/></div><select aria-label="Filtrar categoria" value={category} onChange={e=>setCategory(e.target.value)}><option value="">Todas as categorias</option>{(categories.data||[]).map(c=><option key={c.id} value={c.id}>{c.nome}</option>)}</select><select aria-label="Filtrar situação" value={active} onChange={e=>setActive(e.target.value)}><option value="">Todas as situações</option><option value="true">Ativos</option><option value="false">Inativos</option></select></div>
  {categories.error&&<Notice error>Filtro de categoria indisponível: {categories.error.message}</Notice>}
  <ResourceView resource={resource}>{rows=><DataTable rows={rows} columns={[
   {key:'codigo',label:'Código',render:p=><span className="code">{p.codigo||'Legado'}</span>},
   {key:'nome',label:'Material',render:p=><div className="cell-detail"><strong>{p.nome}</strong><small>{p.descricao}</small></div>},
   {key:'categoria',label:'Categoria',render:p=>p.categoriaMaterial?.nome||p.categoria||'—'},
   {key:'unidade',label:'Unidade',render:p=>p.unidadeMedidaConfigurada?.sigla||p.unidadeMedida||'—'},
   {key:'especificacaoTecnica',label:'Especificação'},{key:'ativo',label:'Situação',render:p=><Badge value={p.ativo}/>},
   {key:'action',label:'Ações',render:p=><button className="btn text" onClick={()=>{setNotice('');setEditing(p)}}><Pencil size={15}/>Editar</button>}
  ]}/>}</ResourceView></Card>
  {editing&&<ProductForm initial={editing} onClose={()=>setEditing(null)} onSaved={()=>{setEditing(null);setNotice('Produto salvo com sucesso.');resource.reload()}}/>}
 </>
}
function ProductForm({initial,onClose,onSaved}){
 const [form,setForm]=useState({...initial,categoriaId:initial.categoriaMaterial?.id||'',unidadeId:initial.unidadeMedidaConfigurada?.id||''}),[busy,setBusy]=useState(false),[error,setError]=useState(''),[matches,setMatches]=useState(null),[matchError,setMatchError]=useState('')
 const loader=useCallback(signal=>Promise.all(['categorias','unidades-medida'].map(name=>api.list(name,null,signal))),[])
 const refs=useResource(loader)
 const change=(key,value)=>setForm(previous=>({...previous,[key]:value}))
 async function suggest(){setMatchError('');try{setMatches(await api.equivalents(form.nome))}catch(error){setMatchError(error.message)}}
 async function save(event){event.preventDefault();setBusy(true);setError('');try{
   const body={nome:form.nome,descricao:form.descricao||null,especificacaoTecnica:form.especificacaoTecnica||null,tipoControle:form.tipoControle||null,ativo:form.ativo}
   if(form.id)body.id=form.id
   if(form.codigo?.trim())body.codigo=form.codigo.trim()
   if(form.categoriaId)body.categoriaMaterial={id:Number(form.categoriaId)}
   if(form.unidadeId)body.unidadeMedidaConfigurada={id:Number(form.unidadeId)}
   // Preserve legacy strings when editing a product without configured references.
   if(!form.categoriaId&&initial.id)body.categoria=initial.categoria
   if(!form.unidadeId&&initial.id)body.unidadeMedida=initial.unidadeMedida
   await api.save('produtos',body);onSaved()
 }catch(error){setError(error.message)}finally{setBusy(false)}}
 return <Modal title={form.id?'Editar produto':'Novo produto'} onClose={onClose} busy={busy}><form onSubmit={save}><Notice error>{error}</Notice><ResourceView resource={refs}>{([categories,units])=><>
  <div className="form-grid"><Field label="Código" hint="Em branco: código automático no cadastro; código atual preservado na edição."><input maxLength={64} value={form.codigo||''} onChange={e=>change('codigo',e.target.value)}/></Field><Field label="Nome do material *"><input required maxLength={255} value={form.nome} onChange={e=>change('nome',e.target.value)}/></Field>
  <Field label="Categoria *"><select required={!form.id} value={form.categoriaId} onChange={e=>change('categoriaId',e.target.value)}><option value="" disabled={!!initial.categoriaMaterial}>Selecionar categoria</option>{categories.filter(c=>c.ativo||c.id===initial.categoriaMaterial?.id).map(c=><option key={c.id} value={c.id}>{c.nome}{!c.ativo?' (inativa)':''}</option>)}</select></Field>
  <Field label="Unidade de medida *" hint="Produtos com estoque ou histórico não podem trocar de unidade."><select required={!form.id} value={form.unidadeId} onChange={e=>change('unidadeId',e.target.value)}><option value="" disabled={!!initial.unidadeMedidaConfigurada}>Selecionar unidade</option>{units.filter(u=>u.ativo||u.id===initial.unidadeMedidaConfigurada?.id).map(u=><option key={u.id} value={u.id}>{u.sigla} · {u.nome}{!u.ativo?' (inativa)':''}</option>)}</select></Field>
  <Field label="Descrição"><textarea maxLength={255} value={form.descricao||''} onChange={e=>change('descricao',e.target.value)}/></Field><Field label="Especificação técnica"><textarea maxLength={2000} value={form.especificacaoTecnica||''} onChange={e=>change('especificacaoTecnica',e.target.value)}/></Field>
  <Field label="Tipo de controle"><input maxLength={255} value={form.tipoControle||''} onChange={e=>change('tipoControle',e.target.value)}/></Field><Field label="Produto ativo"><input type="checkbox" checked={form.ativo} onChange={e=>change('ativo',e.target.checked)}/></Field></div>
  <div className="equivalents"><button type="button" className="btn secondary" disabled={busy||form.nome.trim().length<3} onClick={suggest}>Consultar materiais semelhantes</button><Notice error>{matchError}</Notice>{matches&&<div className="suggestions"><strong>{matches.length?'Possíveis equivalentes — revise antes de salvar':'Nenhum equivalente encontrado.'}</strong>{matches.map(p=><p key={p.id}>{p.codigo||'#'+p.id} · {p.nome} · {p.ativo?'Ativo':'Inativo'}</p>)}</div>}</div>
  <div className="form-actions"><button type="button" className="btn secondary" disabled={busy} onClick={onClose}>Cancelar</button><button className="btn" disabled={busy}>{busy?'Salvando…':'Salvar produto'}</button></div>
 </>}</ResourceView></form></Modal>
}
