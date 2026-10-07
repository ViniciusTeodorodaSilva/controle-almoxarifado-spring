package br.com.almoxarifado.security;
import jakarta.servlet.http.HttpServletRequest;
/** Gate HTTP before request-body validation; service gates remain authoritative too. */
final class RotasPermissao {
 private RotasPermissao() {}
 static boolean permitida(HttpServletRequest req, Autorizacao auth) {
  String path=req.getServletPath(); if(path.isEmpty())path=req.getRequestURI();
  String method=req.getMethod();
  if(path.equals("/auth/me")&&(method.equals("GET")||method.equals("HEAD")) || path.equals("/auth/logout")&&method.equals("POST"))return auth.autenticado();
  String root=path.split("/")[1]; String permission=null;
  boolean read=method.equals("GET")||method.equals("HEAD");
  String prefix=switch(root){case "obras"->"OBRA";case "ordens-servico"->"ORDEM_SERVICO";case "centros-custo"->"CENTRO_CUSTO";case "produtos"->"PRODUTO";case "categorias"->"CATEGORIA";case "unidades-medida"->"UNIDADE";case "funcionarios"->"FUNCIONARIO";case "almoxarifados"->"ALMOXARIFADO";default->null;};
  if(prefix!=null)permission=read?prefix+"_LER":method.equals("POST")||method.equals("PUT")?prefix+"_GERENCIAR":null;
  else if(root.equals("fornecedores"))permission=read?"FORNECEDOR_LER":method.equals("POST")||method.equals("PUT")?"FORNECEDOR_GERENCIAR":null;
  else if(root.equals("recebimentos-compra"))permission=read?"RECEBIMENTO_LER":null;
  else if(root.equals("pedidos-compra")) {
   if(path.endsWith("/recebimentos"))permission=read?"RECEBIMENTO_LER":method.equals("POST")?"RECEBIMENTO_REGISTRAR":null;
   else if(read)permission="COMPRA_LER";
   else if(method.equals("PUT"))permission=path.endsWith("/aprovar")?"COMPRA_APROVAR":path.endsWith("/cancelar")?"COMPRA_CANCELAR":"COMPRA_CRIAR";
   else if(method.equals("POST")&&!path.matches(".*/(aprovar|cancelar|submeter)$"))permission="COMPRA_CRIAR";
  }
  else if(root.equals("usuarios"))permission=read||method.equals("POST")||method.equals("PUT")?"USUARIO_GERENCIAR":null;
  else if(root.equals("auditoria"))permission=read?"AUDITORIA_LER":null;
  else if(root.equals("movimentacoes"))permission=read?"MOVIMENTACAO_LER":null;
  else if(root.equals("transferencias"))permission=read?"ESTOQUE_LER":method.equals("POST")?"ESTOQUE_TRANSFERIR":null;
  else if(root.equals("necessidades-compra"))permission=read?"NECESSIDADE_COMPRA_LER":method.equals("POST")?"NECESSIDADE_COMPRA_CRIAR":method.equals("PUT")&&path.endsWith("/cancelar")?"NECESSIDADE_COMPRA_GERENCIAR":null;
  else if(root.equals("estoques"))permission=read?"ESTOQUE_LER":method.equals("PUT")&&path.endsWith("/limites")?"ESTOQUE_CONFIGURAR":method.equals("POST")||method.equals("PUT")?"ESTOQUE_MOVIMENTAR":null;
  else if(root.equals("solicitacoes")) {
   if(read)permission=path.endsWith("/movimentacoes")?"MOVIMENTACAO_LER":"SOLICITACAO_LER";
   else if(method.equals("POST"))permission=path.endsWith("/atendimentos")?"SOLICITACAO_ATENDER":"SOLICITACAO_CRIAR";
   else if(method.equals("PUT"))permission=path.endsWith("/aprovar")?"SOLICITACAO_APROVAR":path.endsWith("/rejeitar")?"SOLICITACAO_REJEITAR":path.endsWith("/iniciar-separacao")?"SOLICITACAO_SEPARAR":null;
  }
  return permission!=null&&auth.permite(permission);
 }
}
