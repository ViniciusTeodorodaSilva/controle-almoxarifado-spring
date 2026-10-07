package br.com.almoxarifado.ativos;

import br.com.almoxarifado.exception.*;
import br.com.almoxarifado.obras.*;
import br.com.almoxarifado.repository.*;
import br.com.almoxarifado.security.*;
import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional
public class AtivosService {
  private final AtivoRepository ativos;
  private final RegistroAtivoRepository registros;
  private final FuncionarioRepository pessoas;
  private final AlmoxarifadoRepository locais;
  private final CategoriaMaterialRepository categorias;
  private final ContextoService contextos;
  private final Autorizacao auth;
  private final AuditoriaService audit;
  private final EntityManager em;
  public AtivosService(AtivoRepository a, RegistroAtivoRepository r, FuncionarioRepository p,
      AlmoxarifadoRepository l, CategoriaMaterialRepository c, ContextoService ctx,
      Autorizacao auth, AuditoriaService audit, EntityManager em) {
    ativos=a; registros=r; pessoas=p; locais=l; categorias=c; contextos=ctx;
    this.auth=auth; this.audit=audit; this.em=em;
  }
  static String texto(String s, int max, boolean obrigatorio) {
    s=s==null?null:s.strip(); if(s!=null&&s.isEmpty())s=null;
    if(obrigatorio&&s==null||s!=null&&(s.length()>max||s.chars().anyMatch(c->Character.isISOControl(c)&&!(max>=1000&&(c=='\n'||c=='\r'||c=='\t')))))
      throw new IllegalArgumentException("Texto obrigatorio, invalido ou acima do limite");
    return s;
  }
  private Ativo ativo(Integer id, boolean lock) {
    if(id==null)throw new IllegalArgumentException("Informe o ativo");
    var a=org.hibernate.Hibernate.unproxy(ativos.findById(id).orElseThrow(()->new RecursoNaoEncontradoException("Ativo nao encontrado")),Ativo.class);
    if(lock)em.refresh(a,LockModeType.PESSIMISTIC_WRITE); return a;
  }
  private RegistroAtivo registro(Integer id) {
    return org.hibernate.Hibernate.unproxy(registros.findById(id).orElseThrow(()->new RecursoNaoEncontradoException("Registro nao encontrado")),RegistroAtivo.class);
  }
  private String pessoa(Integer id) {
    if(id==null)throw new IllegalArgumentException("Informe o responsavel operacional");
    return pessoas.findById(id).orElseThrow(()->new RecursoNaoEncontradoException("Funcionario nao encontrado")).getNome();
  }
  private String local(Integer id) {
    return id==null?null:locais.findById(id).orElseThrow(()->new RecursoNaoEncontradoException("Almoxarifado nao encontrado")).getNome();
  }
  private void condicao(CondicaoAtivo c) { if(c==null)throw new IllegalArgumentException("Informe a condicao fisica"); }
  private void dataFutura(LocalDate d) { if(d!=null&&!d.isAfter(LocalDate.now()))throw new IllegalArgumentException("Proxima inspecao deve ser futura"); }
  private StatusAtivo livre(Ativo a) { return a.ativo&&!a.reprovado&&a.condicao.utilizavel()?StatusAtivo.DISPONIVEL:StatusAtivo.INDISPONIVEL; }
  private void semCustodia(Ativo a) {
    if(a.pendenciaId!=null)throw new ConflitoException("Ativo possui emprestimo ou transferencia em aberto");
    if(a.status==StatusAtivo.BAIXADO)throw new ConflitoException("Ativo baixado");
  }
  private void atualizado(Ativo a) { a.atualizadoEm=LocalDateTime.now();a.alteradoPor=auth.ator(); }
  private void cadastro(Ativo a,AtivosInput.Cadastro c,boolean novo) {
    if(c==null)throw new IllegalArgumentException("Informe o cadastro");
    String codigo=texto(c.codigoPatrimonial,50,false);
    if(!novo&&!Objects.equals(codigo==null?null:codigo.toUpperCase(Locale.ROOT),a.codigoPatrimonial))
      throw new ConflitoException("Codigo patrimonial e imutavel");
    if(codigo!=null){codigo=codigo.toUpperCase(Locale.ROOT);if(!codigo.matches("[A-Z0-9][A-Z0-9._-]{0,49}"))throw new IllegalArgumentException("Codigo patrimonial invalido");
      if(novo&&codigo.startsWith("FER-"))throw new IllegalArgumentException("Prefixo FER- reservado a identificacao automatica");}
    if(novo)a.codigoPatrimonial=codigo==null?"NOVO-"+UUID.randomUUID():codigo;
    a.nome=texto(c.nome,160,true);a.descricao=texto(c.descricao,2000,false);
    a.fabricante=texto(c.fabricante,160,false);a.modelo=texto(c.modelo,160,false);
    a.numeroSerie=texto(c.numeroSerie,160,false);a.observacao=texto(c.observacao,2000,false);
    if(c.categoriaId!=null&&(!Objects.equals(c.categoriaId,a.categoriaId)||novo)){
      var categoria=categorias.findById(c.categoriaId).orElseThrow(()->new RecursoNaoEncontradoException("Categoria nao encontrada"));
      if(!categoria.isAtivo())throw new ConflitoException("Categoria inativa");}
    a.categoriaId=c.categoriaId;a.dataAquisicao=c.dataAquisicao;
  }
  @PreAuthorize("@autorizacao.permite('ATIVO_GERENCIAR')")
  public Map<String,Object> criar(AtivosInput.Criacao in) {
    if(in==null||in.almoxarifadoId==null)throw new IllegalArgumentException("Informe o almoxarifado inicial");
    local(in.almoxarifadoId);condicao(in.condicao);dataFutura(in.proximaInspecao);
    var a=new Ativo();cadastro(a,in.cadastro,true);a.almoxarifadoId=in.almoxarifadoId;
    a.condicao=in.condicao;a.ativo=true;a.status=livre(a);a.proximaInspecao=in.proximaInspecao;
    atualizado(a);a.criadoEm=a.atualizadoEm;a.criadoPor=auth.ator();ativos.saveAndFlush(a);
    if(in.cadastro.codigoPatrimonial==null||in.cadastro.codigoPatrimonial.isBlank()){
      a.codigoPatrimonial=String.format(Locale.ROOT,"FER-%06d",a.id);ativos.flush();}
    audit.registrar("ATIVO_CRIADO","ATIVO",a.id.toString(),null,null,a.status.name());return view(a);
  }
  @PreAuthorize("@autorizacao.permite('ATIVO_GERENCIAR')")
  public Map<String,Object> editar(Integer id,AtivosInput.Cadastro in) {
    var a=ativo(id,true);if(a.status==StatusAtivo.BAIXADO)throw new ConflitoException("Ativo baixado nao pode ser editado");
    cadastro(a,in,false);atualizado(a);audit.registrar("ATIVO_ALTERADO","ATIVO",id.toString(),null,null,a.status.name());return view(a);
  }
  private String assinatura(String operacao,Integer id,Object... campos) {
    try {
      // Length-prefixed values avoid collisions caused by separators in user observations.
      var s=new StringBuilder(operacao+":"+id);
      for(var c:campos){if(c==null)s.append("|N");else {String v=c.toString();s.append("|V").append(v.length()).append(':').append(v);}}
      return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(s.toString().getBytes(StandardCharsets.UTF_8)));
    }catch(java.security.NoSuchAlgorithmException e){throw new IllegalStateException(e);}
  }
  private RegistroAtivo replay(String chave,String hash) {
    if(chave==null||!chave.matches("[A-Za-z0-9._:-]{16,100}"))throw new IllegalArgumentException("Idempotency-Key obrigatoria e invalida");
    var r=registros.findByChaveIdempotencia(chave).orElse(null);
    if(r!=null)r=org.hibernate.Hibernate.unproxy(r,RegistroAtivo.class);
    if(r!=null&&!hash.equals(r.assinatura))throw new ConflitoException("Chave reutilizada em outra operacao");return r;
  }
  private RegistroAtivo inicio(Ativo a,TipoRegistroAtivo tipo) {
    var r=new RegistroAtivo();r.ativoId=a.id;r.codigoPatrimonial=a.codigoPatrimonial;r.ativoNome=a.nome;
    r.tipo=tipo;r.dataHora=LocalDateTime.now();r.atorId=auth.ator();r.atorNome=auth.nomeAtor();
    r.localOrigemId=a.almoxarifadoId;r.localOrigemNome=local(a.almoxarifadoId);r.contextoOrigem=a.contexto;
    r.responsavelAntesId=a.responsavelId;r.responsavelAntesNome=a.responsavelId==null?null:pessoa(a.responsavelId);
    r.condicaoAntes=a.condicao;r.statusAntes=a.status;return r;
  }
  private Map<String,Object> fim(Ativo a,RegistroAtivo r,String chave,String hash) {
    r.condicaoDepois=a.condicao;r.statusDepois=a.status;
    if(r.tipo!=TipoRegistroAtivo.TRANSFERENCIA){r.localDestinoId=a.almoxarifadoId;r.localDestinoNome=local(a.almoxarifadoId);r.contextoDestino=a.contexto;}
    r.responsavelDepoisId=a.responsavelId;r.responsavelDepoisNome=a.responsavelId==null?null:pessoa(a.responsavelId);
    r.chaveIdempotencia=chave;r.assinatura=hash;registros.saveAndFlush(r);atualizado(a);
    audit.registrar("ATIVO_"+r.tipo.name(),"REGISTRO_ATIVO",r.id.toString(),r.recebidoPorId==null?r.entreguePorId:r.recebidoPorId,r.statusAntes.name(),r.statusDepois.name());
    return view(r);
  }
  private Object[] ids(ObrasInput.Contexto c) { return c==null?new Object[]{null,null,null}:new Object[]{c.obraId,c.ordemServicoId,c.centroCustoId}; }
  private ContextoOperacional resolver(ObrasInput.Contexto c) {
    return c==null?null:contextos.resolver(c.obraId,c.ordemServicoId,c.centroCustoId);
  }
  @PreAuthorize("@autorizacao.permite('EMPRESTIMO_GERENCIAR')")
  public Map<String,Object> emprestar(AtivosInput.Emprestimo in,String chave) {
    if(in==null)throw new IllegalArgumentException("Informe o emprestimo");
    String obs=texto(in.observacao,2000,false);var ids=ids(in.contexto);
    String hash=assinatura("EMPRESTIMO",in.ativoId,in.entreguePorId,in.funcionarioId,in.condicao,in.previsaoDevolucao,obs,ids[0],ids[1],ids[2]);
    var old=replay(chave,hash);if(old!=null)return view(old);
    var before=ativo(in.ativoId,false);var origem=before.contexto;
    // Structure locks always precede the asset lock. Refresh and compare after locking the asset.
    var ctx=in.contexto==null?(origem==null?null:contextos.resolver(origem.getObraId(),origem.getOrdemServicoId(),origem.getCentroCustoId())):resolver(in.contexto);
    var a=ativo(in.ativoId,true);old=replay(chave,hash);if(old!=null)return view(old);
    if(!mesmoContexto(origem,a.contexto))throw new ConflitoException("Localizacao contextual mudou; confira o ativo");
    semCustodia(a);if(!a.ativo||a.status!=StatusAtivo.DISPONIVEL||a.reprovado||!a.condicao.utilizavel())throw new ConflitoException("Ativo indisponivel para emprestimo");
    if(a.proximaInspecao!=null&&!a.proximaInspecao.isAfter(LocalDate.now()))throw new ConflitoException("Inspecao pendente; inspecione antes de emprestar");
    if(origem!=null&&origem.getObraId()!=null&&(ctx==null||!origem.getObraId().equals(ctx.getObraId())))throw new ConflitoException("Ativo localizado em outra obra; transfira primeiro");
    condicao(in.condicao);if(!in.condicao.utilizavel())throw new ConflitoException("Condicao impede emprestimo");
    if(in.previsaoDevolucao!=null&&in.previsaoDevolucao.isBefore(LocalDate.now()))throw new IllegalArgumentException("Previsao de devolucao anterior a retirada");
    var r=inicio(a,TipoRegistroAtivo.EMPRESTIMO);r.entreguePorId=in.entreguePorId;r.entreguePorNome=pessoa(in.entreguePorId);
    r.recebidoPorId=in.funcionarioId;r.recebidoPorNome=pessoa(in.funcionarioId);r.previsaoDevolucao=in.previsaoDevolucao;r.observacao=obs;
    a.responsavelId=in.funcionarioId;a.contexto=ctx;if(ctx!=null&&ctx.getObraId()!=null)a.almoxarifadoId=null;a.condicao=in.condicao;a.status=StatusAtivo.EMPRESTADO;
    var v=fim(a,r,chave,hash);a.pendenciaId=r.id;ativos.flush();return v;
  }
  private boolean mesmoContexto(ContextoOperacional a,ContextoOperacional b) {
    return Objects.equals(a==null?null:a.getObraId(),b==null?null:b.getObraId())&&Objects.equals(a==null?null:a.getOrdemServicoId(),b==null?null:b.getOrdemServicoId())&&Objects.equals(a==null?null:a.getCentroCustoId(),b==null?null:b.getCentroCustoId());
  }
  @PreAuthorize("@autorizacao.permite('EMPRESTIMO_GERENCIAR')")
  public Map<String,Object> devolver(Integer id,AtivosInput.Devolucao in,String chave) {
    if(in==null)throw new IllegalArgumentException("Informe a devolucao");String obs=texto(in.observacao,2000,false);
    String hash=assinatura("DEVOLUCAO",id,in.devolvidoPorId,in.recebidoPorId,in.almoxarifadoId,in.condicao,obs);
    var old=replay(chave,hash);if(old!=null)return view(old);var origem=registro(id);var a=ativo(origem.ativoId,true);
    old=replay(chave,hash);if(old!=null)return view(old);
    if(origem.tipo!=TipoRegistroAtivo.EMPRESTIMO||!Objects.equals(a.pendenciaId,id)||a.status!=StatusAtivo.EMPRESTADO)throw new ConflitoException("Emprestimo nao esta aberto");
    condicao(in.condicao);var r=inicio(a,TipoRegistroAtivo.DEVOLUCAO);r.origemId=id;
    r.entreguePorId=in.devolvidoPorId;r.entreguePorNome=pessoa(in.devolvidoPorId);r.recebidoPorId=in.recebidoPorId;r.recebidoPorNome=pessoa(in.recebidoPorId);r.observacao=obs;
    a.almoxarifadoId=in.almoxarifadoId==null?origem.localOrigemId:in.almoxarifadoId;local(a.almoxarifadoId);
    a.contexto=in.almoxarifadoId==null?origem.contextoOrigem:null;a.responsavelId=null;a.condicao=in.condicao;a.pendenciaId=null;a.status=livre(a);
    return fim(a,r,chave,hash);
  }
  @PreAuthorize("@autorizacao.permite('TRANSFERENCIA_ATIVO_GERENCIAR')")
  public Map<String,Object> transferir(AtivosInput.Transferencia in,String chave) {
    if(in==null)throw new IllegalArgumentException("Informe a transferencia");String obs=texto(in.observacao,2000,false);var ids=ids(in.contexto);
    String hash=assinatura("TRANSFERENCIA",in.ativoId,in.entreguePorId,in.almoxarifadoId,in.condicao,obs,ids[0],ids[1],ids[2]);
    var old=replay(chave,hash);if(old!=null)return view(old);var ctx=resolver(in.contexto);
    if((in.almoxarifadoId==null)==(ctx==null||ctx.getObraId()==null))throw new IllegalArgumentException("Destino exige almoxarifado ou obra, exclusivamente");
    if(in.almoxarifadoId!=null&&ctx!=null)throw new IllegalArgumentException("Almoxarifado de destino nao recebe contexto de obra");
    var a=ativo(in.ativoId,true);old=replay(chave,hash);if(old!=null)return view(old);semCustodia(a);
    if(!a.ativo)throw new ConflitoException("Ativo inativo nao pode ser transferido");
    if(Objects.equals(a.almoxarifadoId,in.almoxarifadoId)&&mesmoContexto(a.contexto,ctx))throw new ConflitoException("Origem e destino iguais");
    condicao(in.condicao);var r=inicio(a,TipoRegistroAtivo.TRANSFERENCIA);r.entreguePorId=in.entreguePorId;r.entreguePorNome=pessoa(in.entreguePorId);
    r.localDestinoId=in.almoxarifadoId;r.localDestinoNome=local(in.almoxarifadoId);r.contextoDestino=ctx;r.observacao=obs;
    a.status=StatusAtivo.EM_TRANSFERENCIA;a.condicao=in.condicao;var v=fim(a,r,chave,hash);a.pendenciaId=r.id;ativos.flush();return v;
  }
  @PreAuthorize("@autorizacao.permite('TRANSFERENCIA_ATIVO_GERENCIAR')")
  public Map<String,Object> receber(Integer id,AtivosInput.Recebimento in,String chave) {
    if(in==null)throw new IllegalArgumentException("Informe a chegada");String obs=texto(in.observacao,2000,false);
    String hash=assinatura("RECEBIMENTO",id,in.recebidoPorId,in.condicao,obs);var old=replay(chave,hash);if(old!=null)return view(old);
    var origem=registro(id);var a=ativo(origem.ativoId,true);old=replay(chave,hash);if(old!=null)return view(old);
    if(origem.tipo!=TipoRegistroAtivo.TRANSFERENCIA||!Objects.equals(a.pendenciaId,id)||a.status!=StatusAtivo.EM_TRANSFERENCIA)throw new ConflitoException("Transferencia nao esta enviada");
    condicao(in.condicao);var r=inicio(a,TipoRegistroAtivo.RECEBIMENTO);r.origemId=id;r.recebidoPorId=in.recebidoPorId;r.recebidoPorNome=pessoa(in.recebidoPorId);r.observacao=obs;
    a.almoxarifadoId=origem.localDestinoId;a.contexto=origem.contextoDestino;a.responsavelId=null;a.pendenciaId=null;a.condicao=in.condicao;a.status=livre(a);return fim(a,r,chave,hash);
  }
  @PreAuthorize("@autorizacao.permite('INSPECAO_ATIVO_GERENCIAR')")
  public Map<String,Object> inspecionar(AtivosInput.Inspecao in,String chave) {
    if(in==null||in.resultado==null)throw new IllegalArgumentException("Informe resultado da inspecao");String obs=texto(in.observacao,2000,false);
    String hash=assinatura("INSPECAO",in.ativoId,in.inspetorId,in.condicao,in.resultado,in.proximaInspecao,obs);var old=replay(chave,hash);if(old!=null)return view(old);
    var a=ativo(in.ativoId,true);old=replay(chave,hash);if(old!=null)return view(old);
    if(a.status==StatusAtivo.BAIXADO||a.status==StatusAtivo.EM_TRANSFERENCIA)throw new ConflitoException("Ativo baixado ou em transito nao pode ser inspecionado");
    condicao(in.condicao);dataFutura(in.proximaInspecao);
    if(in.resultado!=ResultadoInspecao.REPROVADO&&!in.condicao.utilizavel())throw new ConflitoException("Condicao danificada exige reprovacao");
    if(in.resultado!=ResultadoInspecao.APROVADO&&obs==null)throw new IllegalArgumentException("Registre ressalva ou motivo da reprovacao");
    var r=inicio(a,TipoRegistroAtivo.INSPECAO);r.recebidoPorId=in.inspetorId;r.recebidoPorNome=pessoa(in.inspetorId);r.resultado=in.resultado;r.observacao=obs;r.proximaInspecao=in.proximaInspecao;
    a.condicao=in.condicao;a.reprovado=in.resultado==ResultadoInspecao.REPROVADO;a.proximaInspecao=in.proximaInspecao;if(a.pendenciaId==null)a.status=livre(a);
    return fim(a,r,chave,hash);
  }
  @PreAuthorize("@autorizacao.permite('ATIVO_GERENCIAR')")
  public Map<String,Object> situacao(Integer id,AtivosInput.Situacao in,String chave) {
    if(in==null||in.acao==null||!Set.of(TipoRegistroAtivo.INATIVACAO,TipoRegistroAtivo.REATIVACAO,TipoRegistroAtivo.BAIXA).contains(in.acao))throw new IllegalArgumentException("Acao de situacao invalida");
    String motivo=texto(in.motivo,2000,true);String hash=assinatura("SITUACAO",id,in.acao,in.responsavelId,motivo);var old=replay(chave,hash);if(old!=null)return view(old);
    var a=ativo(id,true);old=replay(chave,hash);if(old!=null)return view(old);semCustodia(a);
    if(in.acao==TipoRegistroAtivo.REATIVACAO&&a.ativo||in.acao==TipoRegistroAtivo.INATIVACAO&&!a.ativo)throw new ConflitoException("Situacao ja aplicada");
    var r=inicio(a,in.acao);r.entreguePorId=in.responsavelId;r.entreguePorNome=pessoa(in.responsavelId);r.observacao=motivo;
    a.ativo=in.acao==TipoRegistroAtivo.REATIVACAO;a.status=in.acao==TipoRegistroAtivo.BAIXA?StatusAtivo.BAIXADO:livre(a);return fim(a,r,chave,hash);
  }
  private Map<String,Object> map(Object... kv) {var m=new LinkedHashMap<String,Object>();for(int i=0;i<kv.length;i+=2)m.put((String)kv[i],kv[i+1]);return m;}
  private boolean inspecaoPendente(Ativo a){return a.status!=StatusAtivo.BAIXADO&&a.proximaInspecao!=null&&!a.proximaInspecao.isAfter(LocalDate.now());}
  private Map<String,Object> view(Ativo a) {
    a=org.hibernate.Hibernate.unproxy(a,Ativo.class);
    return map("id",a.id,"codigoPatrimonial",a.codigoPatrimonial,"nome",a.nome,"descricao",a.descricao,"fabricante",a.fabricante,"modelo",a.modelo,"numeroSerie",a.numeroSerie,"categoriaId",a.categoriaId,"dataAquisicao",a.dataAquisicao,"observacao",a.observacao,"ativo",a.ativo,"status",a.status,"condicao",a.condicao,"reprovado",a.reprovado,"almoxarifadoId",a.almoxarifadoId,"responsavelId",a.responsavelId,"contexto",a.contexto,"pendenciaId",a.pendenciaId,"proximaInspecao",a.proximaInspecao,"inspecaoPendente",inspecaoPendente(a),"criadoEm",a.criadoEm,"atualizadoEm",a.atualizadoEm,"criadoPor",a.criadoPor,"alteradoPor",a.alteradoPor);
  }
  private Map<String,Object> view(RegistroAtivo r) {
    r=org.hibernate.Hibernate.unproxy(r,RegistroAtivo.class);
    return map("id",r.id,"tipo",r.tipo,"ativoId",r.ativoId,"codigoPatrimonial",r.codigoPatrimonial,"ativoNome",r.ativoNome,"origemId",r.origemId,"dataHora",r.dataHora,"atorId",r.atorId,"atorNome",r.atorNome,"entreguePorId",r.entreguePorId,"entreguePorNome",r.entreguePorNome,"recebidoPorId",r.recebidoPorId,"recebidoPorNome",r.recebidoPorNome,"responsavelAntesId",r.responsavelAntesId,"responsavelAntesNome",r.responsavelAntesNome,"responsavelDepoisId",r.responsavelDepoisId,"responsavelDepoisNome",r.responsavelDepoisNome,"localOrigemId",r.localOrigemId,"localOrigemNome",r.localOrigemNome,"localDestinoId",r.localDestinoId,"localDestinoNome",r.localDestinoNome,"contextoOrigem",r.contextoOrigem,"contextoDestino",r.contextoDestino,"condicaoAntes",r.condicaoAntes,"condicaoDepois",r.condicaoDepois,"statusAntes",r.statusAntes,"statusDepois",r.statusDepois,"resultado",r.resultado,"observacao",r.observacao,"previsaoDevolucao",r.previsaoDevolucao,"proximaInspecao",r.proximaInspecao);
  }
  @PreAuthorize("@autorizacao.permite('ATIVO_LER')") @Transactional(readOnly=true)
  public Map<String,Object> buscar(Integer id) {return view(ativo(id,false));}
  private Pageable pagina(int p,int t){if(p<0||t<1||t>100)throw new IllegalArgumentException("Paginacao invalida");return PageRequest.of(p,t,Sort.by(Sort.Direction.DESC,"id"));}
  public record Filtro(String termo,StatusAtivo status,CondicaoAtivo condicao,Integer categoriaId,Integer funcionarioId,Integer almoxarifadoId,Integer obraId,Integer ordemServicoId,Integer centroCustoId,Boolean ativo,Boolean inspecaoPendente){}
  @PreAuthorize("@autorizacao.permite('ATIVO_LER')") @Transactional(readOnly=true)
  public Page<Map<String,Object>> listar(Filtro f,int p,int t) {
    Specification<Ativo> s=(r,q,c)->c.conjunction();
    if(f.termo()!=null&&!f.termo().isBlank()){String v="%"+texto(f.termo(),160,false).toLowerCase(Locale.ROOT).replace("\\","\\\\").replace("%","\\%").replace("_","\\_")+"%";
      s=s.and((r,q,c)->{var ps=new ArrayList<jakarta.persistence.criteria.Predicate>();for(String field:List.of("codigoPatrimonial","nome","fabricante","modelo","numeroSerie"))ps.add(c.like(c.lower(r.get(field)),v,'\\'));
        var sub=q.subquery(Integer.class);var pessoa=sub.from(br.com.almoxarifado.model.Funcionario.class);sub.select(pessoa.get("id")).where(c.like(c.lower(pessoa.get("nome")),v,'\\'));ps.add(r.get("responsavelId").in(sub));ps.add(c.like(c.lower(r.get("contexto").get("obraNome")),v,'\\'));return c.or(ps.toArray(jakarta.persistence.criteria.Predicate[]::new));});}
    Object[][] values={{"status",f.status()},{"condicao",f.condicao()},{"categoriaId",f.categoriaId()},{"responsavelId",f.funcionarioId()},{"almoxarifadoId",f.almoxarifadoId()},{"ativo",f.ativo()}};
    for(var pair:values)if(pair[1]!=null)s=s.and((r,q,c)->c.equal(r.get((String)pair[0]),pair[1]));
    Object[][] ctx={{"obraId",f.obraId()},{"ordemServicoId",f.ordemServicoId()},{"centroCustoId",f.centroCustoId()}};
    for(var pair:ctx)if(pair[1]!=null)s=s.and((r,q,c)->c.equal(r.get("contexto").get((String)pair[0]),pair[1]));
    if(f.inspecaoPendente()!=null)s=s.and((r,q,c)->{var due=c.and(c.notEqual(r.get("status"),StatusAtivo.BAIXADO),c.isNotNull(r.get("proximaInspecao")),c.lessThanOrEqualTo(r.get("proximaInspecao"),LocalDate.now()));return f.inspecaoPendente()?due:c.not(due);});
    return ativos.findAll(s,pagina(p,t)).map(this::view);
  }
  @PreAuthorize("@autorizacao.permite('ATIVO_LER')") @Transactional(readOnly=true)
  public Page<Map<String,Object>> historico(Integer id,int p,int t) {ativo(id,false);return registros.findAll((r,q,c)->c.equal(r.get("ativoId"),id),pagina(p,t)).map(this::view);}
  private String permissao(TipoRegistroAtivo tipo){return switch(tipo){case EMPRESTIMO->"EMPRESTIMO_LER";case TRANSFERENCIA->"TRANSFERENCIA_ATIVO_LER";case INSPECAO->"INSPECAO_ATIVO_LER";default->throw new IllegalArgumentException("Tipo de consulta invalido");};}
  @PreAuthorize("@autorizacao.autenticado()") @Transactional(readOnly=true)
  public Map<String,Object> buscarRegistro(Integer id,TipoRegistroAtivo tipo) {
    if(!auth.permite(permissao(tipo)))throw new org.springframework.security.access.AccessDeniedException("Operacao nao autorizada");
    var r=registro(id);if(r.tipo!=tipo)throw new RecursoNaoEncontradoException("Registro de outro tipo");
    var out=view(r);var fim=registros.findByOrigemId(id).orElse(null);out.put("encerramento",fim==null?null:view(fim));
    out.put("aberto",tipo!=TipoRegistroAtivo.INSPECAO&&fim==null);out.put("vencido",tipo==TipoRegistroAtivo.EMPRESTIMO&&fim==null&&r.previsaoDevolucao!=null&&r.previsaoDevolucao.isBefore(LocalDate.now()));return out;
  }
  @PreAuthorize("@autorizacao.autenticado()") @Transactional(readOnly=true)
  public Page<Map<String,Object>> listarRegistros(TipoRegistroAtivo tipo,Integer ativoId,Integer funcionarioId,Integer obraId,Integer ordemServicoId,Integer centroCustoId,Boolean aberto,Boolean vencido,LocalDate de,LocalDate ate,int p,int t) {
    if(!auth.permite(permissao(tipo)))throw new org.springframework.security.access.AccessDeniedException("Operacao nao autorizada");
    if(de!=null&&ate!=null&&ate.isBefore(de))throw new IllegalArgumentException("Periodo invertido");
    Specification<RegistroAtivo> s=(r,q,c)->c.equal(r.get("tipo"),tipo);
    if(ativoId!=null)s=s.and((r,q,c)->c.equal(r.get("ativoId"),ativoId));
    if(funcionarioId!=null)s=s.and((r,q,c)->c.or(c.equal(r.get("recebidoPorId"),funcionarioId),c.equal(r.get("entreguePorId"),funcionarioId)));
    Object[][] ctx={{"obraId",obraId},{"ordemServicoId",ordemServicoId},{"centroCustoId",centroCustoId}};
    for(var pair:ctx)if(pair[1]!=null)s=s.and((r,q,c)->c.equal(r.get("contextoDestino").get((String)pair[0]),pair[1]));
    if(tipo==TipoRegistroAtivo.INSPECAO){if(Boolean.TRUE.equals(aberto)||Boolean.TRUE.equals(vencido))s=s.and((r,q,c)->c.disjunction());}
    else {
      if(aberto!=null)s=s.and((r,q,c)->{var sub=q.subquery(Integer.class);var end=sub.from(RegistroAtivo.class);sub.select(end.get("id")).where(c.equal(end.get("origemId"),r.get("id")));return aberto?c.not(c.exists(sub)):c.exists(sub);});
      if(Boolean.TRUE.equals(vencido))s=s.and((r,q,c)->{var sub=q.subquery(Integer.class);var end=sub.from(RegistroAtivo.class);sub.select(end.get("id")).where(c.equal(end.get("origemId"),r.get("id")));return c.not(c.exists(sub));});
    }
    if(vencido!=null)s=s.and((r,q,c)->vencido?c.lessThan(r.get("previsaoDevolucao"),LocalDate.now()):c.or(c.isNull(r.get("previsaoDevolucao")),c.greaterThanOrEqualTo(r.get("previsaoDevolucao"),LocalDate.now())));
    if(de!=null)s=s.and((r,q,c)->c.greaterThanOrEqualTo(r.get("dataHora"),de.atStartOfDay()));
    if(ate!=null)s=s.and((r,q,c)->c.lessThan(r.get("dataHora"),ate.plusDays(1).atStartOfDay()));
    var page=registros.findAll(s,pagina(p,t));
    var idsPagina=page.getContent().stream().map(r->org.hibernate.Hibernate.unproxy(r,RegistroAtivo.class).id).toList();
    Set<Integer> fechados=idsPagina.isEmpty()?Set.of():new HashSet<>(em.createQuery("select r.origemId from RegistroAtivo r where r.origemId in :ids",Integer.class).setParameter("ids",idsPagina).getResultList());
    return page.map(r->{r=org.hibernate.Hibernate.unproxy(r,RegistroAtivo.class);var out=view(r);boolean open=tipo!=TipoRegistroAtivo.INSPECAO&&!fechados.contains(r.id);out.put("aberto",open);out.put("vencido",tipo==TipoRegistroAtivo.EMPRESTIMO&&open&&r.previsaoDevolucao!=null&&r.previsaoDevolucao.isBefore(LocalDate.now()));return out;});
  }
  @PreAuthorize("@autorizacao.permite('ATIVO_LER')") @Transactional(readOnly=true)
  public Map<String,Object> resumo() {
    return map("emprestimosAbertos",ativos.count((r,q,c)->c.equal(r.get("status"),StatusAtivo.EMPRESTADO)),
      "emprestimosVencidos",em.createQuery("select count(r) from RegistroAtivo r where r.tipo=:tipo and r.previsaoDevolucao<:hoje and not exists (select f.id from RegistroAtivo f where f.origemId=r.id)",Long.class).setParameter("tipo",TipoRegistroAtivo.EMPRESTIMO).setParameter("hoje",LocalDate.now()).getSingleResult(),
      "indisponiveis",ativos.count((r,q,c)->c.equal(r.get("status"),StatusAtivo.INDISPONIVEL)),
      "inspecoesPendentes",ativos.count((r,q,c)->c.and(c.notEqual(r.get("status"),StatusAtivo.BAIXADO),c.lessThanOrEqualTo(r.get("proximaInspecao"),LocalDate.now()))));
  }
}
