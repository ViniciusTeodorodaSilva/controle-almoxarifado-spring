package br.com.almoxarifado.security;
import java.util.*;
public enum Perfil {
 ADMIN, GESTOR, ALMOXARIFE, CONSULTA;
 public Set<Permissao> permissoes() {
  if (this == ADMIN) return Collections.unmodifiableSet(EnumSet.allOf(Permissao.class));
  var result=EnumSet.noneOf(Permissao.class);
  for(var p:Permissao.values()) if(p.name().endsWith("_LER") && p!=Permissao.AUDITORIA_LER) result.add(p);
  if(this==GESTOR) result.addAll(Set.of(Permissao.SOLICITACAO_CRIAR,Permissao.SOLICITACAO_APROVAR,Permissao.SOLICITACAO_REJEITAR,Permissao.ESTOQUE_CONFIGURAR,Permissao.NECESSIDADE_COMPRA_CRIAR,Permissao.AUDITORIA_LER));
  if(this==ALMOXARIFE) result.addAll(Set.of(Permissao.SOLICITACAO_CRIAR,Permissao.ESTOQUE_MOVIMENTAR,Permissao.ESTOQUE_TRANSFERIR,Permissao.SOLICITACAO_SEPARAR,Permissao.SOLICITACAO_ATENDER,Permissao.NECESSIDADE_COMPRA_CRIAR));
  return Collections.unmodifiableSet(result);
 }
}
