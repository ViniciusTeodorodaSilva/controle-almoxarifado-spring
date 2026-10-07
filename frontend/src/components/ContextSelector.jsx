import { useCallback, useState } from 'react'
import { api } from '../api/client'
import { useResource } from '../hooks/useResource'
import { Field, ResourceView, Notice } from './ui'
import { compatibleOrders, compatibleCenters, contextText } from '../utils/context'
export function ContextDisplay({ value }) { return <span className="context-value">{contextText(value)}</span> }
export default function ContextSelector({ value = {}, onChange, disabled = false, requiredObra = false, allowOs = true, filter = false, allowCenter = true }) {
 const [termo,setTerm]=useState(''),[osTerm,setOsTerm]=useState(''),[ccTerm,setCcTerm]=useState('')
 const refs=useResource(useCallback(async signal=>{
  const pages=await Promise.all([api.list('obras',{termo,tamanho:100},signal),api.list('ordens-servico',{obraId:value.obraId,termo:osTerm,tamanho:100},signal),api.list('centros-custo',{obraId:value.obraId,incluirGerais:true,termo:ccTerm,ativo:filter?undefined:true,tamanho:100},signal)])
  // Keep selected records visible when searches or pagination exclude them.
  await Promise.all(['obras','ordens-servico','centros-custo'].map(async(kind,index)=>{
   const id=[value.obraId,value.ordemServicoId,value.centroCustoId][index]
   if(id&&!pages[index].content.some(row=>String(row.id)===String(id))){
    const selected=await api.get(kind,id,signal)
    pages[index]={...pages[index],content:[selected,...pages[index].content]}
   }
  }))
  return pages
 },[termo,value.obraId,value.ordemServicoId,value.centroCustoId,osTerm,ccTerm,filter]))
 return <fieldset disabled={disabled} className="form-section"><legend>Contexto operacional {requiredObra ? '*' : '(opcional)'}</legend><p>{filter ? 'Filtre o histórico pelo contexto.' : requiredObra ? 'Escolha a Obra deste cadastro. O centro de custo deve ser compatível.' : 'Sem seleção, a operação permanece geral. A OS e o centro de custo devem pertencer ao contexto escolhido.'}</p><p hidden>Sem seleção, a operação permanece geral. A OS e o centro de custo devem pertencer ao contexto escolhido.</p><Field label="Pesquisar obra"><input value={termo} onChange={e=>setTerm(e.target.value)}/></Field>{allowOs&&<Field label="Pesquisar OS"><input value={osTerm} onChange={e=>setOsTerm(e.target.value)}/></Field>}{allowCenter&&<Field label="Pesquisar centro de custo"><input value={ccTerm} onChange={e=>setCcTerm(e.target.value)}/></Field>}<ResourceView resource={refs}>{([obras,ordens,centros])=>{
 const os=ordens.content.find(o=>String(o.id)===String(value.ordemServicoId));const opts=(rows,label)=><>{rows.map(r=><option key={r.id} value={r.id}>{label(r)}</option>)}</>
 return <><div className="form-grid"><Field label="Obra"><select required={requiredObra} value={value.obraId||''} onChange={e=>onChange({obraId:e.target.value,ordemServicoId:'',centroCustoId:''})}><option value="">{requiredObra?'Selecionar obra':'Sem obra'}</option>{opts(obras.content.filter(o=>filter||String(o.id)===String(value.obraId)||['PLANEJADA','ATIVA'].includes(o.status)),o=>`${o.codigo} · ${o.nome}`)}</select></Field>{allowOs&&<Field label="Ordem de serviço"><select value={value.ordemServicoId||''} onChange={e=>{const selected=ordens.content.find(o=>String(o.id)===e.target.value);onChange({...value,ordemServicoId:e.target.value,...(selected?{obraId:selected.obraId,centroCustoId:selected.centroCustoId||''}:{})})}}><option value="">Sem OS</option>{opts(filter?ordens.content:ordens.content.filter(o=>String(o.id)===String(value.ordemServicoId)||compatibleOrders([o],value.obraId).length),o=>`${o.numero} · ${o.titulo}`)}</select></Field>}{allowCenter&&<Field label="Centro de custo"><select value={value.centroCustoId||''} onChange={e=>onChange({...value,centroCustoId:e.target.value})}><option value="">Sem centro de custo</option>{opts(filter?centros.content:centros.content.filter(c=>String(c.id)===String(value.centroCustoId)||compatibleCenters([c],value.obraId,os).length),c=>`${c.codigo} · ${c.nome}`)}</select></Field>}</div>{[obras,ordens,centros].some(p=>!p.last)&&<Notice>A seleção mostra até 100 registros. Refine a pesquisa por obra; outras páginas continuam disponíveis nos cadastros.</Notice>}</>
 }}</ResourceView></fieldset>
}
