export const epiLabels = { INICIAL:'Entrega inicial', REPOSICAO:'Reposição', SUBSTITUICAO:'Substituição', ENTREGA:'Entrega', DEVOLUCAO:'Devolução', DESCARTE:'Descarte', ESTOQUE:'Estoque utilizável', SEGREGADO:'Segregado / indisponível', PERDA:'Perda', NOVO:'Novo / sem uso', USADO:'Usado', DANIFICADO:'Danificado', PERDIDO:'Perdido' }
export const epiLabel = value => epiLabels[value] || value || '—'
export function epiExceedsStock(value, stock) {
 const available=Number(stock), requested=Number(String(value).replace(',','.'))
 return !Number.isFinite(available)||requested-available>1e-12
}
export function epiQuantity(value, integer = false) {
 const text=String(value??'').trim().replace(',','.')
 if(!/^(?:\d+)(?:\.\d{1,6})?$/.test(text))return 'Informe uma quantidade positiva com até seis decimais.'
 const [whole,fraction='']=text.split('.')
 const scaled=BigInt(whole)*1000000n+BigInt(fraction.padEnd(6,'0'))
 if(scaled<=0n||scaled>9999999999999999999n)return 'Quantidade fora do limite.'
 if(integer&&scaled%1000000n!==0n)return 'A unidade não permite fracionamento.'
 return ''
}
export function epiDeliveryBody(form, lines) {
 const context=Object.fromEntries(['obraId','ordemServicoId','centroCustoId'].map(k=>[k,form.contexto?.[k]?Number(form.contexto[k]):null]))
 return {funcionarioId:Number(form.funcionarioId),responsavelId:Number(form.responsavelId),almoxarifadoId:Number(form.almoxarifadoId),motivo:form.motivo,recebimentoConfirmado:form.recebimentoConfirmado,observacao:form.observacao||null,contexto:Object.values(context).some(v=>v!==null)?context:null,itens:lines.map(l=>({produtoId:l.produtoId,quantidade:String(l.quantidade).replace(',','.'),lote:l.lote||null,fabricacao:l.fabricacao||null,validadeFisica:l.validadeFisica||null,...(form.motivo==='SUBSTITUICAO'?{origemItemId:Number(l.origemItemId),quantidadeSubstituida:String(l.quantidadeSubstituida).replace(',','.'),motivoSubstituicao:l.motivoSubstituicao||null,condicaoAnterior:l.condicaoAnterior,destinoAnterior:l.destinoAnterior}:{})}))}
}
export function validateEpiDelivery(form,lines){
 if(!form.funcionarioId||!form.responsavelId||!form.almoxarifadoId)return 'Selecione funcionário, responsável e almoxarifado.'
 if(!lines.length)return 'Adicione pelo menos um EPI.'
 if(new Set(lines.map(l=>l.produtoId)).size!==lines.length)return 'Cada produto pode aparecer uma vez na entrega.'
 for(const l of lines){const error=epiQuantity(l.quantidade,l.fracionado===false);if(error)return error;if(epiExceedsStock(l.quantidade,l.saldoDisponivel))return `Saldo insuficiente para ${l.nome}.`;if(form.motivo==='SUBSTITUICAO'&&(!l.origemItemId||epiQuantity(l.quantidadeSubstituida)||!l.motivoSubstituicao?.trim()||!l.condicaoAnterior||!l.destinoAnterior))return 'Informe origem, quantidade anterior e motivo da substituição.'}
 if(!form.recebimentoConfirmado)return 'Confirme a entrega e o recebimento operacional.'
 return ''
}
export function epiAlerts(i){return [i.vencido?'Vencido / troca atrasada':null,i.aVencer?'Vence / trocar em até 30 dias':null,i.caVencido?'CA com prazo cadastrado vencido':null].filter(Boolean)}
