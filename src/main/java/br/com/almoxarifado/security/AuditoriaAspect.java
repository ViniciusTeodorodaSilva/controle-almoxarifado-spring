package br.com.almoxarifado.security;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;
import java.lang.reflect.*;
import java.util.*;
import br.com.almoxarifado.model.Estoque;
@Aspect @Component @Order(300)
public class AuditoriaAspect {
 private final AuditoriaService auditoria;
 private final jakarta.persistence.EntityManager entities;
 private final br.com.almoxarifado.repository.MovimentacaoRepository movimentos;
 public AuditoriaAspect(AuditoriaService auditoria,br.com.almoxarifado.repository.MovimentacaoRepository movimentos,jakarta.persistence.EntityManager entities){this.auditoria=auditoria;this.movimentos=movimentos;this.entities=entities;}
 @Around("@annotation(marcador)") public Object registrar(ProceedingJoinPoint call,Auditar marcador)throws Throwable {
  String anterior=antes(call);Object result=call.proceed();String referencia=id(result);Integer responsavel=null;
  var method=((MethodSignature)call.getSignature()).getMethod();var args=call.getArgs();var params=method.getParameters();
  for(int n=0;n<params.length;n++){if(params[n].getName().equals("responsavelId")&&args[n] instanceof Integer i)responsavel=i;Object v=invoke(args[n],"responsavelId");if(v instanceof Integer i)responsavel=i;}
  String antes=anterior,depois=snapshot(result);
  if(result instanceof Estoque e && args.length>2 && args[2] instanceof Double qtd && (method.getName().equals("entradaEstoque")||method.getName().equals("saidaEstoque")))antes="quantidade="+(method.getName().equals("entradaEstoque")?e.getQuantidade()-qtd:e.getQuantidade()+qtd);
  String entidade=call.getTarget().getClass().getSimpleName().replace("Service","");
  if(result instanceof Estoque e && (method.getName().equals("entradaEstoque")||method.getName().equals("saidaEstoque"))){var m=movimentos.findFirstByProdutoIdAndAlmoxarifadoIdOrderByIdDesc(e.getProduto().getId(),e.getAlmoxarifado().getId()).orElseThrow();entidade="Movimentacao";referencia=m.getId().toString();antes="quantidade="+m.getSaldoAnterior();depois="quantidade="+m.getSaldoPosterior();}
  if(method.getName().equals("aprovar")||method.getName().equals("rejeitar"))antes="status=PENDENTE";
  auditoria.registrar(marcador.value(),entidade,referencia,responsavel,antes,depois);return result;
 }
 private String antes(ProceedingJoinPoint call){
  String name=((MethodSignature)call.getSignature()).getMethod().getName();
  Object[] args=call.getArgs();
  if(args.length==0||!(args[0] instanceof Integer id)||!Set.of("atualizar","configurar","iniciarSeparacao","atender").contains(name))return null;
  Class<?> type=switch(call.getTarget().getClass().getSimpleName()){
   case "EstoqueInteligenteService"->Estoque.class;
   case "AtendimentoSolicitacaoService"->br.com.almoxarifado.model.Solicitacao.class;
   case "ProdutoService"->br.com.almoxarifado.model.Produto.class;
   case "CategoriaMaterialService"->br.com.almoxarifado.model.CategoriaMaterial.class;
   case "UnidadeMedidaService"->br.com.almoxarifado.model.UnidadeMedida.class;
   default->null;
  };
  return type==null?null:snapshot(entities.find(type,id,jakarta.persistence.LockModeType.PESSIMISTIC_WRITE));
 }
 private static Object invoke(Object o,String name){if(o==null)return null;try{return o.getClass().getMethod(name).invoke(o);}catch(ReflectiveOperationException e){return null;}}
 private static String id(Object o){Object v=invoke(o,"getId");if(v==null)v=invoke(o,"id");if(v==null)v=invoke(o,"estoqueId");return v instanceof Number?String.valueOf(v):null;}
 // Never serialize a request/body/entity. Only an explicit allowlist of nonsecret state.
 private static String snapshot(Object o){var parts=new ArrayList<String>();for(String n:List.of("getQuantidade","getEstoqueMinimo","getEstoqueMaximo","estoqueMinimo","estoqueMaximo","saldoAtual","getStatus","status","getPerfil","perfil","isAtivo","ativo")){Object v=invoke(o,n);if(v instanceof Number||v instanceof Boolean||v instanceof Enum<?>||n.equals("status")&&v instanceof String)parts.add(n+"="+v);}return parts.isEmpty()?null:String.join(";",parts);}
}
