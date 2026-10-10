package br.com.almoxarifado.epi;

import br.com.almoxarifado.compras.ComprasPage;
import br.com.almoxarifado.exception.*;
import br.com.almoxarifado.model.*;
import br.com.almoxarifado.obras.*;
import br.com.almoxarifado.repository.*;
import br.com.almoxarifado.security.*;
import br.com.almoxarifado.service.*;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.math.*;
import java.time.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.hibernate.Hibernate;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional
public class EpiService {
 private final EpiConfiguracaoRepository configs;
 private final RegistroEpiRepository registros;
 private final ItemEpiRepository itens;
 private final ProdutoRepository produtos;
 private final FuncionarioRepository funcionarios;
 private final AlmoxarifadoRepository locais;
 private final EstoqueRepository saldos;
 private final EstoqueService estoque;
 private final ContextoService contextos;
 private final Autorizacao auth;
 private final AuditoriaService auditoria;
 private final EntityManager em;
 public EpiService(EpiConfiguracaoRepository c,RegistroEpiRepository r,ItemEpiRepository i,
     ProdutoRepository p,FuncionarioRepository f,AlmoxarifadoRepository l,EstoqueRepository s,
     EstoqueService e,ContextoService ctx,Autorizacao a,AuditoriaService aud,EntityManager manager){
  configs=c;registros=r;itens=i;produtos=p;funcionarios=f;locais=l;saldos=s;estoque=e;contextos=ctx;auth=a;auditoria=aud;em=manager;
 }
 static String texto(String s,int limite,boolean requerido){s=s==null?null:s.strip();if(s!=null&&s.isEmpty())s=null;
  if(requerido&&s==null||s!=null&&(s.length()>limite||s.chars().anyMatch(c->Character.isISOControl(c)&&!(limite>=1000&&(c=='\n'||c=='\r'||c=='\t')))))throw new IllegalArgumentException("Texto obrigatorio ou invalido");return s;}
 static BigDecimal quantidade(BigDecimal q){if(q==null||q.signum()<=0||q.stripTrailingZeros().scale()>6||q.compareTo(new BigDecimal("9999999999999.999999"))>0)throw new IllegalArgumentException("Quantidade positiva com ate seis decimais obrigatoria");return q.setScale(6,RoundingMode.UNNECESSARY);}
 private Funcionario pessoa(Integer id){if(id==null)throw new IllegalArgumentException("Informe funcionario/responsavel");return funcionarios.findById(id).orElseThrow(()->new RecursoNaoEncontradoException("Funcionario nao encontrado"));}
 private Almoxarifado local(Integer id){if(id==null)throw new IllegalArgumentException("Informe almoxarifado");return locais.findById(id).orElseThrow(()->new RecursoNaoEncontradoException("Almoxarifado nao encontrado"));}
 private EpiConfiguracao config(Integer id){return Hibernate.unproxy(configs.findById(id).orElseThrow(()->new RecursoNaoEncontradoException("EPI nao configurado")),EpiConfiguracao.class);}
 private RegistroEpi registro(Integer id){return Hibernate.unproxy(registros.findById(id).orElseThrow(()->new RecursoNaoEncontradoException("Registro EPI nao encontrado")),RegistroEpi.class);}
 private ItemEpi origem(Integer id){if(id==null)throw new IllegalArgumentException("Informe a entrega de origem");var i=Hibernate.unproxy(itens.findById(id).orElseThrow(()->new RecursoNaoEncontradoException("Item EPI nao encontrado")),ItemEpi.class);em.refresh(i,LockModeType.PESSIMISTIC_WRITE);if(!i.entrega)throw new ConflitoException("Origem deve ser item entregue");return i;}
 private PageRequest pagina(int numero,int tamanho){if(numero<0||tamanho<1||tamanho>100)throw new IllegalArgumentException("Paginacao invalida");return PageRequest.of(numero,tamanho,Sort.by(Sort.Direction.DESC,"id"));}
 private Map<String,Object> map(Object... kv){var m=new LinkedHashMap<String,Object>();for(int i=0;i<kv.length;i+=2)m.put((String)kv[i],kv[i+1]);return m;}
 private String decimal(BigDecimal q){return q.stripTrailingZeros().toPlainString();}
 private String hash(Object... campos){try{var s=new StringBuilder();for(Object c:campos){if(c==null)s.append("|N");else {String v=c instanceof BigDecimal b?decimal(b):c.toString();s.append("|V").append(v.length()).append(':').append(v);}}return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(s.toString().getBytes(StandardCharsets.UTF_8)));}catch(java.security.NoSuchAlgorithmException ex){throw new IllegalStateException(ex);}}
 private RegistroEpi replay(String chave,String assinatura){if(chave==null||!chave.matches("[A-Za-z0-9._:-]{16,100}"))throw new IllegalArgumentException("Idempotency-Key obrigatoria e invalida");var r=registros.findByChaveIdempotencia(chave).orElse(null);if(r!=null){r=Hibernate.unproxy(r,RegistroEpi.class);if(!assinatura.equals(r.assinatura))throw new ConflitoException("Chave usada com outro comando");}return r;}
 private Map<Integer,Produto> carregarProdutos(Collection<Integer> ids){var m=new HashMap<Integer,Produto>();if(!ids.isEmpty())for(var p:em.createQuery("select p from Produto p left join fetch p.unidadeMedidaConfigurada left join fetch p.categoriaMaterial where p.id in :ids",Produto.class).setParameter("ids",ids).getResultList())m.put(p.getId(),p);return m;}
 private Map<Integer,BigDecimal> encerradas(Collection<Integer> ids){var m=new HashMap<Integer,BigDecimal>();if(!ids.isEmpty())for(var row:em.createQuery("select i.origemItemId,sum(i.quantidade) from ItemEpi i where i.entrega=false and i.origemItemId in :ids group by i.origemItemId",Object[].class).setParameter("ids",ids).getResultList())m.put((Integer)row[0],(BigDecimal)row[1]);return m;}
 private BigDecimal restante(ItemEpi i){return i.quantidade.subtract(encerradas(List.of(i.id)).getOrDefault(i.id,BigDecimal.ZERO));}
 private void validarRestante(ItemEpi i,BigDecimal q){if(!i.fracionado&&q.remainder(BigDecimal.ONE).signum()!=0)throw new IllegalArgumentException("Unidade historica nao permite fracionamento");if(q.compareTo(restante(i))>0)throw new ConflitoException("Quantidade excede saldo em posse");}

 @PreAuthorize("@autorizacao.permite('EPI_GERENCIAR')")
 public Map<String,Object> configurar(Integer id,EpiInput.Configuracao in){
  if(in==null)throw new IllegalArgumentException("Informe configuracao");Integer produtoId=id==null?in.produtoId:id;
  if(produtoId==null||id!=null&&in.produtoId!=null&&!id.equals(in.produtoId))throw new IllegalArgumentException("Produto da configuracao e imutavel");
  var p=produtos.buscarParaAtualizacao(produtoId).orElseThrow(()->new RecursoNaoEncontradoException("Produto nao encontrado"));
  var c=configs.findById(produtoId).map(x->Hibernate.unproxy(x,EpiConfiguracao.class)).orElse(null);
  if(id==null&&c!=null)throw new ConflitoException("EPI ja configurado");if(id!=null&&c==null)throw new RecursoNaoEncontradoException("EPI nao configurado");
  String ca=texto(in.ca,20,true),fabricante=texto(in.fabricante,160,false),modelo=texto(in.modelo,160,false),tamanho=texto(in.tamanho,50,false);
  if(!ca.matches("[0-9]{1,20}"))throw new IllegalArgumentException("CA deve conter somente digitos");
  if(in.ativo==null||in.exigeDevolucao==null||in.permiteRetorno==null)throw new IllegalArgumentException("Informe atividade e politica de devolucao/retorno");
  if(in.diasSubstituicao!=null&&(in.diasSubstituicao<1||in.diasSubstituicao>36500))throw new IllegalArgumentException("Prazo de substituicao invalido");
  if(c!=null&&(!Objects.equals(c.ca,ca)||!Objects.equals(c.fabricante,fabricante)||!Objects.equals(c.modelo,modelo)||!Objects.equals(c.tamanho,tamanho))){
   long positivos=em.createQuery("select count(e) from Estoque e where e.produto.id=:id and e.quantidade>0",Long.class).setParameter("id",produtoId).getSingleResult();
   if(positivos>0)throw new ConflitoException("CA/modelo/tamanho nao podem mudar com saldo fisico; use produto distinto");
  }
  if(c==null){c=new EpiConfiguracao();c.produtoId=produtoId;}
  c.ca=ca;c.fabricante=fabricante;c.modelo=modelo;c.tamanho=tamanho;c.validadeCa=in.validadeCa;c.diasSubstituicao=in.diasSubstituicao;
  c.ativo=in.ativo;c.exigeDevolucao=in.exigeDevolucao;c.permiteRetorno=in.permiteRetorno;c.observacao=texto(in.observacao,2000,false);c.alteradoPor=auth.ator();c.alteradoEm=LocalDateTime.now();configs.saveAndFlush(c);
  auditoria.registrar(id==null?"EPI_CONFIGURADO":"EPI_ALTERADO","EPI",produtoId.toString(),null,null,null);return viewConfig(c,p,null);
 }
 private Map<String,Object> viewConfig(EpiConfiguracao c,Produto p,Double saldo){return map("produtoId",c.produtoId,"codigo",p.getCodigo(),"nome",p.getNome(),"unidade",unidade(p),"produtoAtivo",p.isAtivo(),"fracionado",p.getUnidadeMedidaConfigurada()==null||p.getUnidadeMedidaConfigurada().isPermiteFracionamento(),"ativo",c.ativo,"ca",c.ca,"fabricante",c.fabricante,"modelo",c.modelo,"tamanho",c.tamanho,"validadeCa",c.validadeCa,"diasSubstituicao",c.diasSubstituicao,"exigeDevolucao",c.exigeDevolucao,"permiteRetorno",c.permiteRetorno,"observacao",c.observacao,"saldoDisponivel",saldo,"caVencido",c.validadeCa!=null&&c.validadeCa.isBefore(LocalDate.now()));}
 private String unidade(Produto p){return texto(p.getUnidadeMedidaConfigurada()!=null?p.getUnidadeMedidaConfigurada().getSigla():p.getUnidadeMedida(),64,true);}
 @PreAuthorize("@autorizacao.permite('EPI_LER')")
 public Map<String,Object> buscarConfig(Integer id){return viewConfig(config(id),carregarProdutos(List.of(id)).get(id),null);}
 @PreAuthorize("@autorizacao.permite('EPI_LER')")
 public Page<Map<String,Object>> listarConfigs(String termo,String ca,Boolean ativo,Integer almoxarifadoId,int numero,int tamanho){
  if(almoxarifadoId!=null)local(almoxarifadoId);if(numero<0||tamanho<1||tamanho>100)throw new IllegalArgumentException("Paginacao invalida");
  Specification<EpiConfiguracao> spec=(r,q,b)->b.conjunction();
  if(ativo!=null)spec=spec.and((r,q,b)->b.equal(r.get("ativo"),ativo));if(ca!=null&&!ca.isBlank())spec=spec.and((r,q,b)->b.equal(r.get("ca"),texto(ca,20,false)));
  if(termo!=null&&!termo.isBlank()){String v="%"+texto(termo,160,false).toLowerCase(Locale.ROOT).replace("\\","\\\\").replace("%","\\%").replace("_","\\_")+"%";spec=spec.and((r,q,b)->{var sq=q.subquery(Integer.class);var p=sq.from(Produto.class);sq.select(p.get("id")).where(b.or(b.like(b.lower(p.get("nome")),v,'\\'),b.like(b.lower(p.get("codigo")),v,'\\')));return b.or(r.get("produtoId").in(sq),b.like(b.lower(r.get("ca")),v,'\\'));});}
  var page=configs.findAll(spec,PageRequest.of(numero,tamanho,Sort.by(Sort.Direction.DESC,"produtoId")));var ids=page.stream().map(x->x.produtoId).toList();var products=carregarProdutos(ids);var stock=new HashMap<Integer,Double>();
  if(almoxarifadoId!=null&&!ids.isEmpty())for(var row:em.createQuery("select e.produto.id,e.quantidade from Estoque e where e.almoxarifado.id=:local and e.produto.id in :ids",Object[].class).setParameter("local",almoxarifadoId).setParameter("ids",ids).getResultList())stock.put((Integer)row[0],(Double)row[1]);
  return page.map(c->viewConfig(Hibernate.unproxy(c,EpiConfiguracao.class),products.get(c.produtoId),almoxarifadoId==null?null:stock.getOrDefault(c.produtoId,0d)));
 }

 private RegistroEpi novo(TipoRegistroEpi tipo,Funcionario f,Funcionario resp,Almoxarifado w,ContextoOperacional ctx,String obs,String chave,String assinatura){var r=new RegistroEpi();r.tipo=tipo;r.funcionarioId=f.getId();r.funcionarioNome=texto(f.getNome(),255,true);r.funcionarioMatricula=texto(f.getMatricula(),255,false);r.responsavelId=resp.getId();r.responsavelNome=texto(resp.getNome(),255,true);r.atorId=auth.ator();r.atorNome=auth.nomeAtor();r.almoxarifadoId=w.getId();r.almoxarifadoNome=texto(w.getNome(),255,true);r.contexto=ctx;r.observacao=obs;r.dataHora=LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MICROS);r.chaveIdempotencia=chave;r.assinatura=assinatura;return r;}
 private void limitesItens(List<?> linhas){if(linhas==null||linhas.isEmpty()||linhas.size()>50||linhas.stream().anyMatch(Objects::isNull))throw new IllegalArgumentException("Informe de um a cinquenta itens");}
 private ItemEpi copiar(ItemEpi o){var i=new ItemEpi();i.produtoId=o.produtoId;i.produtoCodigo=o.produtoCodigo;i.produtoNome=o.produtoNome;i.unidade=o.unidade;i.ca=o.ca;i.validadeCa=o.validadeCa;i.fabricante=o.fabricante;i.modelo=o.modelo;i.tamanho=o.tamanho;i.lote=o.lote;i.fabricacao=o.fabricacao;i.validadeFisica=o.validadeFisica;i.substituirAte=o.substituirAte;i.exigeDevolucao=o.exigeDevolucao;i.permiteRetorno=o.permiteRetorno;i.fracionado=o.fracionado;i.contexto=o.contexto;return i;}
 @PreAuthorize("@autorizacao.permite('EPI_ENTREGA_GERENCIAR')")
 public Map<String,Object> entregar(EpiInput.Entrega in,String chave){
  if(in==null||in.motivo==null||!Boolean.TRUE.equals(in.recebimentoConfirmado))throw new IllegalArgumentException("Informe motivo e confirme recebimento operacional");limitesItens(in.itens);
  String obs=texto(in.observacao,2000,false);var commands=new ArrayList<Object>();commands.addAll(Arrays.asList("ENTREGA",in.funcionarioId,in.responsavelId,in.almoxarifadoId,in.motivo,obs,in.contexto==null?null:in.contexto.obraId,in.contexto==null?null:in.contexto.ordemServicoId,in.contexto==null?null:in.contexto.centroCustoId));
  var produtosIds=new TreeSet<Integer>();var origensIds=new TreeSet<Integer>();
  for(var l:in.itens){if(l.produtoId==null||!produtosIds.add(l.produtoId))throw new IllegalArgumentException("Produto ausente ou duplicado");l.quantidade=quantidade(l.quantidade);l.lote=texto(l.lote,100,false);l.motivoSubstituicao=texto(l.motivoSubstituicao,2000,false);
   if(in.motivo==MotivoEntrega.SUBSTITUICAO){if(l.origemItemId==null||!origensIds.add(l.origemItemId)||l.motivoSubstituicao==null)throw new IllegalArgumentException("Substituicao exige origem unica e motivo");l.quantidadeSubstituida=quantidade(l.quantidadeSubstituida);if(l.condicaoAnterior==null||l.destinoAnterior==null||l.destinoAnterior==DestinoEpi.ESTOQUE||(l.condicaoAnterior==CondicaoEpi.PERDIDO)!=(l.destinoAnterior==DestinoEpi.PERDA))throw new IllegalArgumentException("Informe condicao e destino anterior coerentes; retorno ao estoque requer devolucao separada");}else if(l.origemItemId!=null||l.quantidadeSubstituida!=null||l.motivoSubstituicao!=null||l.condicaoAnterior!=null||l.destinoAnterior!=null)throw new IllegalArgumentException("Origem somente na substituicao");
   commands.addAll(Arrays.asList(l.produtoId,l.quantidade,l.lote,l.fabricacao,l.validadeFisica,l.origemItemId,l.quantidadeSubstituida,l.motivoSubstituicao,l.condicaoAnterior,l.destinoAnterior));
  }
  String assinatura=hash(commands.toArray());var old=replay(chave,assinatura);if(old!=null)return detalhe(old);
  var ctx=in.contexto==null?null:contextos.resolver(in.contexto.obraId,in.contexto.ordemServicoId,in.contexto.centroCustoId);
  var origens=new HashMap<Integer,ItemEpi>();for(Integer id:origensIds){var o=origem(id);origens.put(id,o);produtosIds.add(o.produtoId);}
  var products=new HashMap<Integer,Produto>();for(Integer id:produtosIds)products.put(id,produtos.buscarParaAtualizacao(id).orElseThrow(()->new RecursoNaoEncontradoException("Produto nao encontrado")));
  old=replay(chave,assinatura);if(old!=null)return detalhe(old);
  var metas=new HashMap<Integer,EpiConfiguracao>();for(Integer id:produtosIds)metas.put(id,config(id));
  var f=pessoa(in.funcionarioId);var resp=pessoa(in.responsavelId);var w=local(in.almoxarifadoId);
  // Products and stock pairs are sorted before any stock mutation.
  var stocks=new HashMap<Integer,Estoque>();for(var id:produtosIds)if(in.itens.stream().anyMatch(l->l.produtoId.equals(id)))stocks.put(id,saldos.buscarParaAtualizacao(id,w.getId()).orElseThrow(()->new RecursoNaoEncontradoException("Estoque nao encontrado")));
  for(var l:in.itens){var p=products.get(l.produtoId);var c=metas.get(l.produtoId);if(!c.ativo||!p.isAtivo())throw new ConflitoException("EPI/produto inativo");
   if(p.getUnidadeMedidaConfigurada()!=null&&!p.getUnidadeMedidaConfigurada().isPermiteFracionamento()&&l.quantidade.remainder(BigDecimal.ONE).signum()!=0)throw new IllegalArgumentException("Unidade nao permite fracionamento");
   if(BigDecimal.valueOf(l.quantidade.doubleValue()).compareTo(l.quantidade)!=0)throw new ConflitoException("Quantidade fora da precisao do estoque legado");
   ValidacaoQuantidade.validar(p,l.quantidade.doubleValue());
   if(l.fabricacao!=null&&l.fabricacao.isAfter(LocalDate.now())||l.validadeFisica!=null&&(l.validadeFisica.isBefore(LocalDate.now())||l.fabricacao!=null&&l.validadeFisica.isBefore(l.fabricacao)))throw new IllegalArgumentException("Datas fisicas invalidas ou item vencido");
   double saldo=stocks.get(l.produtoId).getQuantidade();if(!Double.isFinite(saldo)||BigDecimal.valueOf(saldo).compareTo(l.quantidade)<0)throw new ConflitoException("Estoque insuficiente ou quantidade fora da precisao");
   if(l.origemItemId!=null){var o=origens.get(l.origemItemId);if(!registro(o.registroId).funcionarioId.equals(f.getId()))throw new ConflitoException("Origem pertence a outro funcionario");validarRestante(o,l.quantidadeSubstituida);}
  }
  var r=novo(in.motivo==MotivoEntrega.SUBSTITUICAO?TipoRegistroEpi.SUBSTITUICAO:TipoRegistroEpi.ENTREGA,f,resp,w,ctx,obs,chave,assinatura);r.motivo=in.motivo;r.recebimentoConfirmado=true;registros.saveAndFlush(r);
  for(var l:in.itens){var p=products.get(l.produtoId);var c=metas.get(l.produtoId);var i=new ItemEpi();i.registroId=r.id;i.produtoId=l.produtoId;i.produtoCodigo=texto(p.getCodigo(),64,false);i.produtoNome=texto(p.getNome(),255,true);i.unidade=unidade(p);i.quantidade=l.quantidade;i.entrega=true;i.origemItemId=l.origemItemId;i.ca=c.ca;i.validadeCa=c.validadeCa;i.fabricante=c.fabricante;i.modelo=c.modelo;i.tamanho=c.tamanho;i.lote=l.lote;i.fabricacao=l.fabricacao;i.validadeFisica=l.validadeFisica;i.substituirAte=c.diasSubstituicao==null?null:LocalDate.now().plusDays(c.diasSubstituicao);i.exigeDevolucao=c.exigeDevolucao;i.permiteRetorno=c.permiteRetorno;i.fracionado=p.getUnidadeMedidaConfigurada()==null||p.getUnidadeMedidaConfigurada().isPermiteFracionamento();i.contexto=ctx;
   var movimento=estoque.movimentarEpi(l.produtoId,w.getId(),l.quantidade.doubleValue(),f.getId(),resp.getId(),TipoMovimentacao.SAIDA,ctx);i.movimentoId=movimento.getId();itens.save(i);
   if(l.origemItemId!=null){var fim=copiar(origens.get(l.origemItemId));fim.registroId=r.id;fim.origemItemId=l.origemItemId;fim.quantidade=l.quantidadeSubstituida;fim.entrega=false;fim.destino=l.destinoAnterior;fim.condicao=l.condicaoAnterior;fim.observacao=l.motivoSubstituicao;itens.save(fim);}
  }
  itens.flush();auditoria.registrar("EPI_"+r.tipo,"EPI_REGISTRO",r.id.toString(),r.responsavelId,null,null);return detalhe(r);
 }

 @PreAuthorize("@autorizacao.permite('EPI_ENTREGA_GERENCIAR')")
 public Map<String,Object> fechar(EpiInput.Fechamento in,String chave,boolean descarte){
  if(in==null)throw new IllegalArgumentException("Informe fechamento");limitesItens(in.itens);String motivo=texto(in.motivo,2000,true);var ids=new TreeSet<Integer>();var commands=new ArrayList<Object>(Arrays.asList(descarte?"DESCARTE":"DEVOLUCAO",in.responsavelId,in.almoxarifadoId,motivo));
  for(var l:in.itens){if(l.origemItemId==null||!ids.add(l.origemItemId)||l.condicao==null||l.destino==null)throw new IllegalArgumentException("Origem unica, condicao e destino obrigatorios");l.quantidade=quantidade(l.quantidade);if(descarte&&l.destino!=DestinoEpi.DESCARTE&&l.destino!=DestinoEpi.PERDA||!descarte&&l.destino==DestinoEpi.PERDA||l.condicao==CondicaoEpi.PERDIDO&&l.destino!=DestinoEpi.PERDA||l.destino==DestinoEpi.PERDA&&l.condicao!=CondicaoEpi.PERDIDO)throw new IllegalArgumentException("Condicao/destino incoerentes");commands.addAll(Arrays.asList(l.origemItemId,l.quantidade,l.condicao,l.destino));}
  String assinatura=hash(commands.toArray());var old=replay(chave,assinatura);if(old!=null)return detalhe(old);var origens=new HashMap<Integer,ItemEpi>();for(Integer id:ids)origens.put(id,origem(id));
  var first=origens.get(ids.first());var fonte=registro(first.registroId);var prodIds=new TreeSet<Integer>();origens.values().forEach(o->prodIds.add(o.produtoId));var products=new HashMap<Integer,Produto>();for(Integer id:prodIds)products.put(id,produtos.buscarParaAtualizacao(id).orElseThrow(()->new RecursoNaoEncontradoException("Produto nao encontrado")));
  old=replay(chave,assinatura);if(old!=null)return detalhe(old);var w=local(in.almoxarifadoId);var resp=pessoa(in.responsavelId);
  for(var l:in.itens){var o=origens.get(l.origemItemId);if(!registro(o.registroId).funcionarioId.equals(fonte.funcionarioId))throw new ConflitoException("Fechamento deve ser de um funcionario");validarRestante(o,l.quantidade);var origemContexto=registro(o.registroId).contexto;if(!mesmoContexto(fonte.contexto,origemContexto))throw new ConflitoException("Fechamento exige o mesmo contexto de origem; separe os registros");
   if(l.destino==DestinoEpi.ESTOQUE){if(BigDecimal.valueOf(l.quantidade.doubleValue()).compareTo(l.quantidade)!=0)throw new ConflitoException("Quantidade fora da precisao do estoque legado");var c=config(o.produtoId);var p=products.get(o.produtoId);if(l.condicao!=CondicaoEpi.NOVO||!o.permiteRetorno||!c.permiteRetorno||!c.ativo||!p.isAtivo()||!Objects.equals(c.ca,o.ca)||!Objects.equals(c.modelo,o.modelo)||!Objects.equals(c.fabricante,o.fabricante)||!Objects.equals(c.tamanho,o.tamanho)||o.validadeFisica!=null&&o.validadeFisica.isBefore(LocalDate.now()))throw new ConflitoException("Retorno ao estoque exige NOVO, politica permitida, identidade compativel e validade fisica");}
  }
  // Lock all affected stock pairs in the same order as deliveries, before any credit.
  for(Integer id:prodIds)if(in.itens.stream().anyMatch(l->l.destino==DestinoEpi.ESTOQUE&&origens.get(l.origemItemId).produtoId.equals(id)))saldos.buscarParaAtualizacao(id,w.getId()).orElseThrow(()->new RecursoNaoEncontradoException("Estoque de destino nao encontrado"));
  var r=novo(descarte?TipoRegistroEpi.DESCARTE:TipoRegistroEpi.DEVOLUCAO,pessoa(fonte.funcionarioId),resp,w,fonte.contexto,motivo,chave,assinatura);registros.saveAndFlush(r);
  for(var l:in.itens){var o=origens.get(l.origemItemId);var i=copiar(o);i.registroId=r.id;i.origemItemId=o.id;i.quantidade=l.quantidade;i.entrega=false;i.condicao=l.condicao;i.destino=l.destino;i.observacao=motivo;
   if(l.destino==DestinoEpi.ESTOQUE)i.movimentoId=estoque.movimentarEpi(o.produtoId,w.getId(),l.quantidade.doubleValue(),fonte.funcionarioId,resp.getId(),TipoMovimentacao.ENTRADA,registro(o.registroId).contexto).getId();itens.save(i);
  }
  itens.flush();auditoria.registrar("EPI_"+r.tipo,"EPI_REGISTRO",r.id.toString(),r.responsavelId,null,null);return detalhe(r);
 }

 private boolean mesmoContexto(ContextoOperacional a,ContextoOperacional b){return Objects.equals(a==null?null:a.getObraId(),b==null?null:b.getObraId())&&Objects.equals(a==null?null:a.getOrdemServicoId(),b==null?null:b.getOrdemServicoId())&&Objects.equals(a==null?null:a.getCentroCustoId(),b==null?null:b.getCentroCustoId());}
 private Map<String,Object> viewItem(ItemEpi i,BigDecimal encerrado){i=Hibernate.unproxy(i,ItemEpi.class);BigDecimal pendente=i.entrega?i.quantidade.subtract(encerrado):BigDecimal.ZERO;boolean emPosse=pendente.signum()>0;LocalDate hoje=LocalDate.now();return map("id",i.id,"registroId",i.registroId,"produtoId",i.produtoId,"produtoCodigo",i.produtoCodigo,"produtoNome",i.produtoNome,"unidade",i.unidade,"quantidade",decimal(i.quantidade),"quantidadeEmPosse",decimal(pendente),"entrega",i.entrega,"fracionado",i.fracionado,"contexto",i.contexto,"origemItemId",i.origemItemId,"movimentoId",i.movimentoId,"ca",i.ca,"validadeCa",i.validadeCa,"fabricante",i.fabricante,"modelo",i.modelo,"tamanho",i.tamanho,"lote",i.lote,"fabricacao",i.fabricacao,"validadeFisica",i.validadeFisica,"substituirAte",i.substituirAte,"exigeDevolucao",i.exigeDevolucao,"permiteRetorno",i.permiteRetorno,"condicao",i.condicao,"destino",i.destino,"observacao",i.observacao,"vencido",emPosse&&(i.validadeFisica!=null&&i.validadeFisica.isBefore(hoje)||i.substituirAte!=null&&i.substituirAte.isBefore(hoje)),"aVencer",emPosse&&(i.validadeFisica!=null&&!i.validadeFisica.isBefore(hoje)&&!i.validadeFisica.isAfter(hoje.plusDays(30))||i.substituirAte!=null&&!i.substituirAte.isBefore(hoje)&&!i.substituirAte.isAfter(hoje.plusDays(30))),"caVencido",i.validadeCa!=null&&i.validadeCa.isBefore(hoje));}
 private Map<String,Object> viewRegistro(RegistroEpi r,List<ItemEpi> linhas,Map<Integer,BigDecimal> encerrados){r=Hibernate.unproxy(r,RegistroEpi.class);return map("id",r.id,"tipo",r.tipo,"motivo",r.motivo,"funcionarioId",r.funcionarioId,"funcionarioNome",r.funcionarioNome,"funcionarioMatricula",r.funcionarioMatricula,"responsavelId",r.responsavelId,"responsavelNome",r.responsavelNome,"atorId",r.atorId,"atorNome",r.atorNome,"almoxarifadoId",r.almoxarifadoId,"almoxarifadoNome",r.almoxarifadoNome,"dataHora",r.dataHora,"recebimentoConfirmado",r.recebimentoConfirmado,"observacao",r.observacao,"contexto",r.contexto,"itens",linhas.stream().map(i->viewItem(i,encerrados.getOrDefault(i.id,BigDecimal.ZERO))).toList());}
 private Map<String,Object> detalhe(RegistroEpi r){var linhas=itens.findByRegistroIdOrderById(r.id);return viewRegistro(r,linhas,encerradas(linhas.stream().map(i->i.id).toList()));}
 @PreAuthorize("@autorizacao.permite('EPI_ENTREGA_LER')")
 public Map<String,Object> buscarRegistro(Integer id){return detalhe(registro(id));}
 public record Filtro(Integer funcionarioId,Integer produtoId,Integer obraId,Integer ordemServicoId,Integer centroCustoId,Integer almoxarifadoId,String ca,TipoRegistroEpi tipo,LocalDate de,LocalDate ate){}
 private Specification<RegistroEpi> specRegistro(Filtro f){Specification<RegistroEpi> s=(r,q,b)->b.conjunction();
  if(f.funcionarioId()!=null)s=s.and((r,q,b)->b.equal(r.get("funcionarioId"),f.funcionarioId()));if(f.almoxarifadoId()!=null)s=s.and((r,q,b)->b.equal(r.get("almoxarifadoId"),f.almoxarifadoId()));if(f.tipo()!=null)s=s.and((r,q,b)->b.equal(r.get("tipo"),f.tipo()));
  if(f.de()!=null)s=s.and((r,q,b)->b.greaterThanOrEqualTo(r.get("dataHora"),f.de().atStartOfDay()));if(f.ate()!=null)s=s.and((r,q,b)->b.lessThan(r.get("dataHora"),f.ate().plusDays(1).atStartOfDay()));if(f.de()!=null&&f.ate()!=null&&f.de().isAfter(f.ate()))throw new IllegalArgumentException("Periodo invalido");
  if(f.produtoId()!=null||f.ca()!=null&&!f.ca().isBlank()||f.obraId()!=null||f.ordemServicoId()!=null||f.centroCustoId()!=null)s=s.and((r,q,b)->{var sq=q.subquery(Integer.class);var i=sq.from(ItemEpi.class);var ps=new ArrayList<Predicate>();ps.add(b.equal(i.get("registroId"),r.get("id")));if(f.produtoId()!=null)ps.add(b.equal(i.get("produtoId"),f.produtoId()));if(f.ca()!=null&&!f.ca().isBlank())ps.add(b.equal(i.get("ca"),texto(f.ca(),20,false)));if(f.obraId()!=null)ps.add(b.equal(i.get("contexto").get("obraId"),f.obraId()));if(f.ordemServicoId()!=null)ps.add(b.equal(i.get("contexto").get("ordemServicoId"),f.ordemServicoId()));if(f.centroCustoId()!=null)ps.add(b.equal(i.get("contexto").get("centroCustoId"),f.centroCustoId()));sq.select(i.get("id")).where(ps.toArray(Predicate[]::new));return b.exists(sq);});return s;
 }
 @PreAuthorize("@autorizacao.permite('EPI_ENTREGA_LER')")
 public Page<Map<String,Object>> listarRegistros(Filtro f,int numero,int tamanho){var page=registros.findAll(specRegistro(f),pagina(numero,tamanho));var ids=page.stream().map(r->r.id).toList();var linhas=ids.isEmpty()?List.<ItemEpi>of():itens.findByRegistroIdIn(ids);var encerrados=encerradas(linhas.stream().map(i->i.id).toList());var grouped=new HashMap<Integer,List<ItemEpi>>();linhas.stream().sorted(Comparator.comparing(i->i.id)).forEach(i->grouped.computeIfAbsent(i.registroId,k->new ArrayList<>()).add(i));return page.map(r->viewRegistro(r,grouped.getOrDefault(r.id,List.of()),encerrados));}
 private Specification<ItemEpi> posseSpec(Integer funcionarioId,String alerta){return (r,q,b)->{var ps=new ArrayList<Predicate>();ps.add(b.isTrue(r.get("entrega")));var sq=q.subquery(BigDecimal.class);var c=sq.from(ItemEpi.class);sq.select(b.sum(c.<BigDecimal>get("quantidade"))).where(b.equal(c.get("origemItemId"),r.get("id")),b.isFalse(c.get("entrega")));ps.add(b.greaterThan(r.get("quantidade"),b.coalesce(sq,BigDecimal.ZERO)));
  if(funcionarioId!=null){var reg=q.subquery(Integer.class);var rr=reg.from(RegistroEpi.class);reg.select(rr.get("id")).where(b.equal(rr.get("funcionarioId"),funcionarioId));ps.add(r.get("registroId").in(reg));}
  if(alerta!=null&&!alerta.isBlank()){LocalDate hoje=LocalDate.now();if(alerta.equals("VENCIDO"))ps.add(b.or(b.lessThan(r.get("validadeFisica"),hoje),b.lessThan(r.get("substituirAte"),hoje)));else if(alerta.equals("A_VENCER"))ps.add(b.or(b.between(r.get("validadeFisica"),hoje,hoje.plusDays(30)),b.between(r.get("substituirAte"),hoje,hoje.plusDays(30))));else throw new IllegalArgumentException("Alerta invalido");}return b.and(ps.toArray(Predicate[]::new));};}
 @PreAuthorize("@autorizacao.permite('EPI_ENTREGA_LER')")
 public Page<Map<String,Object>> posse(Integer funcionarioId,String alerta,int numero,int tamanho){if(funcionarioId!=null)pessoa(funcionarioId);var page=itens.findAll(posseSpec(funcionarioId,alerta),pagina(numero,tamanho));var encerrados=encerradas(page.stream().map(i->i.id).toList());return page.map(i->viewItem(i,encerrados.getOrDefault(i.id,BigDecimal.ZERO)));}
 @PreAuthorize("@autorizacao.permite('EPI_ENTREGA_LER')")
 public Map<String,Object> ficha(Integer funcionarioId,int numero,int tamanho){var f=pessoa(funcionarioId);var filtro=new Filtro(funcionarioId,null,null,null,null,null,null,null,null,null);return map("funcionarioId",f.getId(),"funcionarioNome",f.getNome(),"matricula",f.getMatricula(),"posse",ComprasPage.of(posse(funcionarioId,null,numero,tamanho)),"historico",ComprasPage.of(listarRegistros(filtro,numero,tamanho)));}
 @PreAuthorize("@autorizacao.permite('EPI_ENTREGA_LER')")
 public Map<String,Object> resumo(){return map("itensEmPosse",itens.count(posseSpec(null,null)),"itensVencidos",itens.count(posseSpec(null,"VENCIDO")),"itensAVencer",itens.count(posseSpec(null,"A_VENCER")));}
}
