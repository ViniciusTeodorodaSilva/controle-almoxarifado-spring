package br.com.almoxarifado.obras;

import br.com.almoxarifado.exception.*;
import br.com.almoxarifado.repository.FuncionarioRepository;
import br.com.almoxarifado.security.*;
import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EstruturaService {
  private final ObraRepository obras;
  private final CentroCustoRepository centros;
  private final OrdemServicoRepository ordens;
  private final FuncionarioRepository pessoas;
  private final ContextoService contextos;
  private final AuditoriaService audit;
  private final Autorizacao auth;
  private final EntityManager em;

  public EstruturaService(
      ObraRepository o,
      CentroCustoRepository c,
      OrdemServicoRepository s,
      FuncionarioRepository p,
      ContextoService ctx,
      AuditoriaService a,
      Autorizacao auth,
      EntityManager em) {
    obras = o;
    centros = c;
    ordens = s;
    pessoas = p;
    contextos = ctx;
    audit = a;
    this.auth = auth;
    this.em = em;
  }

  static String texto(String s, int max, boolean required) {
    s = s == null ? null : s.strip();
    if (s != null && s.isEmpty()) s = null;
    if (required && s == null
        || s != null && (s.length() > max || s.chars().anyMatch(Character::isISOControl)))
      throw new IllegalArgumentException("Texto obrigatório, inválido ou acima do limite");
    return s;
  }

  static String codigo(String s) {
    s = texto(s, 50, true).toUpperCase(Locale.ROOT);
    if (!s.matches("[A-Z0-9][A-Z0-9._-]{0,49}"))
      throw new IllegalArgumentException(
          "Código usa letras ASCII, números, ponto, hífen ou underscore");
    return s;
  }

  static Pageable pagina(int p, int t) {
    if (p < 0 || t < 1 || t > 100) throw new IllegalArgumentException("Paginação inválida");
    return PageRequest.of(p, t, Sort.by(Sort.Direction.DESC, "id"));
  }

  private void pessoa(Integer id) {
    if (id != null && !pessoas.existsById(id))
      throw new RecursoNaoEncontradoException("Responsável não encontrado");
  }

  private void evento(
      String ev, String ent, Integer id, Integer resp, String before, String after) {
    audit.registrar(ev, ent, id.toString(), resp, before, after);
  }

  @PreAuthorize("@autorizacao.permite('OBRA_LER')")
  @Transactional(readOnly = true)
  public Obra obra(Integer id) {
    return obras
        .findById(id)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Obra não encontrada"));
  }

  @PreAuthorize("@autorizacao.permite('CENTRO_CUSTO_LER')")
  @Transactional(readOnly = true)
  public CentroCusto centro(Integer id) {
    return centros
        .findById(id)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Centro de custo não encontrado"));
  }

  @PreAuthorize("@autorizacao.permite('ORDEM_SERVICO_LER')")
  @Transactional(readOnly = true)
  public OrdemServico ordem(Integer id) {
    return ordens
        .findById(id)
        .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada"));
  }

  @PreAuthorize("@autorizacao.permite('OBRA_LER')")
  @Transactional(readOnly = true)
  public Page<Obra> listarObras(
      String termo, String cliente, StatusObra status, int pagina, int tamanho) {
    Specification<Obra> s = (r, q, c) -> c.conjunction();
    if (termo != null && !termo.isBlank()) {
      String v = "%" + texto(termo, 160, false).toLowerCase(Locale.ROOT) + "%";
      s =
          s.and(
              (r, q, c) ->
                  c.or(c.like(c.lower(r.get("codigo")), v), c.like(c.lower(r.get("nome")), v)));
    }
    if (cliente != null && !cliente.isBlank()) {
      String v = "%" + texto(cliente, 160, false).toLowerCase(Locale.ROOT) + "%";
      s = s.and((r, q, c) -> c.like(c.lower(r.get("cliente")), v));
    }
    if (status != null) s = s.and((r, q, c) -> c.equal(r.get("status"), status));
    return obras.findAll(s, pagina(pagina, tamanho));
  }

  @PreAuthorize("@autorizacao.permite('CENTRO_CUSTO_LER')")
  @Transactional(readOnly = true)
  public Page<CentroCusto> listarCentros(
      String termo, TipoCentroCusto tipo, Boolean ativo, Integer obraId, int pagina, int tamanho) {
    return listarCentros(termo, tipo, ativo, obraId, pagina, tamanho, false);
  }

  @PreAuthorize("@autorizacao.permite('CENTRO_CUSTO_LER')")
  @Transactional(readOnly = true)
  public Page<CentroCusto> listarCentros(
      String termo,
      TipoCentroCusto tipo,
      Boolean ativo,
      Integer obraId,
      int pagina,
      int tamanho,
      boolean incluirGerais) {
    Specification<CentroCusto> s = (r, q, c) -> c.conjunction();
    if (termo != null && !termo.isBlank()) {
      String v = "%" + texto(termo, 160, false).toLowerCase(Locale.ROOT) + "%";
      s =
          s.and(
              (r, q, c) ->
                  c.or(c.like(c.lower(r.get("codigo")), v), c.like(c.lower(r.get("nome")), v)));
    }
    if (tipo != null) s = s.and((r, q, c) -> c.equal(r.get("tipo"), tipo));
    if (ativo != null) s = s.and((r, q, c) -> c.equal(r.get("ativo"), ativo));
    if (obraId != null)
      s =
          s.and(
              (r, q, c) ->
                  incluirGerais
                      ? c.or(c.equal(r.get("obraId"), obraId), c.isNull(r.get("obraId")))
                      : c.equal(r.get("obraId"), obraId));
    return centros.findAll(s, pagina(pagina, tamanho));
  }

  @PreAuthorize("@autorizacao.permite('ORDEM_SERVICO_LER')")
  @Transactional(readOnly = true)
  public Page<OrdemServico> listarOrdens(
      String termo,
      StatusOrdemServico status,
      Integer obraId,
      Integer centroCustoId,
      int pagina,
      int tamanho) {
    Specification<OrdemServico> s = (r, q, c) -> c.conjunction();
    if (termo != null && !termo.isBlank()) {
      String v = "%" + texto(termo, 160, false).toLowerCase(Locale.ROOT) + "%";
      s =
          s.and(
              (r, q, c) ->
                  c.or(c.like(c.lower(r.get("numero")), v), c.like(c.lower(r.get("titulo")), v)));
    }
    if (status != null) s = s.and((r, q, c) -> c.equal(r.get("status"), status));
    if (obraId != null) s = s.and((r, q, c) -> c.equal(r.get("obraId"), obraId));
    if (centroCustoId != null)
      s = s.and((r, q, c) -> c.equal(r.get("centroCustoId"), centroCustoId));
    return ordens.findAll(s, pagina(pagina, tamanho));
  }

  @PreAuthorize("@autorizacao.permite('OBRA_GERENCIAR')")
  public Obra salvarObra(Integer id, ObrasInput.Obra in) {
    if (in == null) throw new IllegalArgumentException("Informe a obra");
    Obra o =
        id == null
            ? new Obra()
            : obras
                .findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Obra não encontrada"));
    if (id != null) {
      em.refresh(o, LockModeType.PESSIMISTIC_WRITE);
      if (o.getStatus() == StatusObra.CONCLUIDA || o.getStatus() == StatusObra.CANCELADA)
        throw new ConflitoException("Obra encerrada não pode ser editada");
    }
    String before = id == null ? null : o.getCodigo();
    o.setCodigo(codigo(in.codigo));
    o.setNome(texto(in.nome, 160, true));
    o.setDescricao(texto(in.descricao, 2000, false));
    o.setCliente(texto(in.cliente, 160, false));
    o.setLocalidade(texto(in.localidade, 160, false));
    o.setObservacao(texto(in.observacao, 2000, false));
    pessoa(in.responsavelId);
    o.setResponsavelId(in.responsavelId);
    if (in.dataInicio != null
        && in.dataTerminoPrevisto != null
        && in.dataTerminoPrevisto.isBefore(in.dataInicio))
      throw new IllegalArgumentException("Término previsto anterior ao início");
    o.setDataInicio(in.dataInicio);
    o.setDataTerminoPrevisto(in.dataTerminoPrevisto);
    o.setAtualizadoEm(LocalDateTime.now());
    o.setAlteradoPor(auth.ator());
    if (id == null) {
      o.setStatus(StatusObra.PLANEJADA);
      o.setCriadoEm(o.getAtualizadoEm());
      o.setCriadoPor(auth.ator());
    }
    obras.saveAndFlush(o);
    evento(
        id == null ? "OBRA_CRIADA" : "OBRA_ALTERADA",
        "OBRA",
        o.getId(),
        o.getResponsavelId(),
        before,
        o.getCodigo());
    return o;
  }

  @PreAuthorize("@autorizacao.permite('CENTRO_CUSTO_GERENCIAR')")
  public CentroCusto salvarCentro(Integer id, ObrasInput.Centro in) {
    if (in == null || in.tipo == null || in.ativo == null)
      throw new IllegalArgumentException("Tipo e situação obrigatórios");
    CentroCusto cc =
        id == null
            ? new CentroCusto()
            : centros
                .findById(id)
                .orElseThrow(
                    () -> new RecursoNaoEncontradoException("Centro de custo não encontrado"));
    if (id != null && !Objects.equals(cc.getObraId(), in.obraId))
      throw new ConflitoException("Obra do centro de custo é imutável; crie outro centro");
    if (in.obraId != null) {
      var o =
          obras
              .findById(in.obraId)
              .orElseThrow(() -> new RecursoNaoEncontradoException("Obra não encontrada"));
      em.refresh(o, LockModeType.PESSIMISTIC_WRITE);
      if (id == null) ContextoService.exigirObra(o);
    }
    if (id != null) em.refresh(cc, LockModeType.PESSIMISTIC_WRITE);
    if (in.tipo == TipoCentroCusto.OBRA && in.obraId == null)
      throw new IllegalArgumentException("Centro do tipo OBRA exige obra");
    boolean inativando = id != null && cc.getAtivo() && !in.ativo;
    String before = id == null ? null : cc.getCodigo() + ":" + cc.getAtivo();
    cc.setCodigo(codigo(in.codigo));
    cc.setNome(texto(in.nome, 160, true));
    cc.setDescricao(texto(in.descricao, 2000, false));
    cc.setTipo(in.tipo);
    cc.setAtivo(in.ativo);
    cc.setObraId(in.obraId);
    cc.setAtualizadoEm(LocalDateTime.now());
    cc.setAlteradoPor(auth.ator());
    if (id == null) {
      cc.setCriadoEm(cc.getAtualizadoEm());
      cc.setCriadoPor(auth.ator());
    }
    centros.saveAndFlush(cc);
    evento(
        id == null
            ? "CENTRO_CUSTO_CRIADO"
            : inativando ? "CENTRO_CUSTO_INATIVADO" : "CENTRO_CUSTO_ALTERADO",
        "CENTRO_CUSTO",
        cc.getId(),
        null,
        before,
        cc.getCodigo() + ":" + cc.getAtivo());
    return cc;
  }

  @PreAuthorize("@autorizacao.permite('ORDEM_SERVICO_GERENCIAR')")
  public OrdemServico salvarOrdem(Integer id, ObrasInput.Ordem in) {
    if (in == null || in.obraId == null) throw new IllegalArgumentException("OS exige obra");
    OrdemServico os =
        id == null
            ? new OrdemServico()
            : ordens
                .findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada"));
    if (id != null
        && (!Objects.equals(os.getObraId(), in.obraId)
            || !Objects.equals(os.getCentroCustoId(), in.centroCustoId)))
      throw new ConflitoException("Obra e centro de custo da OS são imutáveis");
    contextos.resolver(in.obraId, null, in.centroCustoId);
    if (id != null) {
      em.refresh(os, LockModeType.PESSIMISTIC_WRITE);
      if (os.getStatus() == StatusOrdemServico.CONCLUIDA
          || os.getStatus() == StatusOrdemServico.CANCELADA)
        throw new ConflitoException("OS encerrada não pode ser editada");
    }
    pessoa(in.responsavelId);
    os.setObraId(in.obraId);
    os.setCentroCustoId(in.centroCustoId);
    os.setTitulo(texto(in.titulo, 160, true));
    os.setDescricao(texto(in.descricao, 2000, false));
    os.setObservacao(texto(in.observacao, 2000, false));
    String priority = texto(in.prioridade, 20, false);
    if (priority != null && !Set.of("BAIXA", "NORMAL", "ALTA", "URGENTE").contains(priority))
      throw new IllegalArgumentException("Prioridade inválida");
    os.setPrioridade(priority);
    os.setResponsavelId(in.responsavelId);
    os.setAtualizadoEm(LocalDateTime.now());
    os.setAlteradoPor(auth.ator());
    if (id == null) {
      os.setNumero("NOVO-" + UUID.randomUUID());
      os.setStatus(StatusOrdemServico.ABERTA);
      os.setDataAbertura(os.getAtualizadoEm());
      os.setCriadoEm(os.getAtualizadoEm());
      os.setCriadoPor(auth.ator());
    }
    ordens.saveAndFlush(os);
    if (id == null) {
      os.setNumero(
          "OS-"
              + os.getCriadoEm().getYear()
              + "-"
              + String.format(Locale.ROOT, "%06d", os.getId()));
      ordens.flush();
    }
    evento(
        id == null ? "ORDEM_SERVICO_CRIADA" : "ORDEM_SERVICO_ALTERADA",
        "ORDEM_SERVICO",
        os.getId(),
        os.getResponsavelId(),
        null,
        os.getStatus().name());
    return os;
  }

  @PreAuthorize("@autorizacao.permite('OBRA_GERENCIAR')")
  public Obra statusObra(Integer id, ObrasInput.Transicao in) {
    var o =
        obras
            .findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Obra não encontrada"));
    em.refresh(o, LockModeType.PESSIMISTIC_WRITE);
    StatusObra next;
    try {
      next = StatusObra.valueOf(in.status);
    } catch (Exception e) {
      throw new IllegalArgumentException("Status inválido");
    }
    var old = o.getStatus();
    boolean valid =
        switch (old) {
          case PLANEJADA -> next == StatusObra.ATIVA || next == StatusObra.CANCELADA;
          case ATIVA ->
              next == StatusObra.SUSPENSA
                  || next == StatusObra.CONCLUIDA
                  || next == StatusObra.CANCELADA;
          case SUSPENSA -> next == StatusObra.ATIVA || next == StatusObra.CANCELADA;
          default -> false;
        };
    if (!valid) throw new ConflitoException("Transição de obra inválida");
    if (next == StatusObra.CONCLUIDA || next == StatusObra.CANCELADA) {
      long open =
          em.createQuery(
                  "select count(s) from OrdemServico s where s.obraId=:id and s.status not in"
                      + " (:done,:cancel)",
                  Long.class)
              .setParameter("id", id)
              .setParameter("done", StatusOrdemServico.CONCLUIDA)
              .setParameter("cancel", StatusOrdemServico.CANCELADA)
              .getSingleResult();
      long pending =
          em.createQuery(
                  "select count(s) from Solicitacao s where s.contexto.obraId=:id and s.status not"
                      + " in (:done,:rejected)",
                  Long.class)
              .setParameter("id", id)
              .setParameter("done", br.com.almoxarifado.model.StatusSolicitacao.ATENDIDA)
              .setParameter("rejected", br.com.almoxarifado.model.StatusSolicitacao.REJEITADA)
              .getSingleResult();
      if (open > 0 || pending > 0)
        throw new ConflitoException("Encerre OS e demandas pendentes antes de encerrar a obra");
      o.setDataTerminoReal(LocalDate.now());
    }
    o.setStatus(next);
    o.setAtualizadoEm(LocalDateTime.now());
    o.setAlteradoPor(auth.ator());
    evento("OBRA_STATUS_ALTERADO", "OBRA", id, o.getResponsavelId(), old.name(), next.name());
    return o;
  }

  @PreAuthorize("@autorizacao.permite('ORDEM_SERVICO_GERENCIAR')")
  public OrdemServico statusOrdem(Integer id, ObrasInput.Transicao in) {
    var os =
        ordens
            .findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada"));
    var obra = obras.findById(os.getObraId()).orElseThrow();
    em.refresh(obra, LockModeType.PESSIMISTIC_WRITE);
    if (os.getCentroCustoId() != null) {
      var cc = centros.findById(os.getCentroCustoId()).orElseThrow();
      em.refresh(cc, LockModeType.PESSIMISTIC_WRITE);
    }
    em.refresh(os, LockModeType.PESSIMISTIC_WRITE);
    StatusOrdemServico next;
    try {
      next = StatusOrdemServico.valueOf(in.status);
    } catch (Exception e) {
      throw new IllegalArgumentException("Status inválido");
    }
    var old = os.getStatus();
    boolean valid =
        switch (old) {
          case ABERTA ->
              next == StatusOrdemServico.EM_ANDAMENTO || next == StatusOrdemServico.CANCELADA;
          case EM_ANDAMENTO ->
              next == StatusOrdemServico.SUSPENSA
                  || next == StatusOrdemServico.CONCLUIDA
                  || next == StatusOrdemServico.CANCELADA;
          case SUSPENSA ->
              next == StatusOrdemServico.EM_ANDAMENTO || next == StatusOrdemServico.CANCELADA;
          default -> false;
        };
    if (!valid) throw new ConflitoException("Transição de OS inválida");
    if (next == StatusOrdemServico.EM_ANDAMENTO) {
      ContextoService.exigirObra(obra);
      if (os.getCentroCustoId() != null
          && !centros.findById(os.getCentroCustoId()).orElseThrow().getAtivo())
        throw new ConflitoException("Centro de custo inativo");
      if (os.getDataInicio() == null) os.setDataInicio(LocalDateTime.now());
    }
    if (next == StatusOrdemServico.SUSPENSA || next == StatusOrdemServico.CANCELADA)
      os.setMotivoInterrupcao(texto(in.motivo, 1000, true));
    if (next == StatusOrdemServico.CONCLUIDA || next == StatusOrdemServico.CANCELADA) {
      long pending =
          em.createQuery(
                  "select count(s) from Solicitacao s where s.contexto.ordemServicoId=:id and"
                      + " s.status not in (:done,:rejected)",
                  Long.class)
              .setParameter("id", id)
              .setParameter("done", br.com.almoxarifado.model.StatusSolicitacao.ATENDIDA)
              .setParameter("rejected", br.com.almoxarifado.model.StatusSolicitacao.REJEITADA)
              .getSingleResult();
      if (pending > 0) throw new ConflitoException("OS possui solicitações pendentes");
      os.setDataConclusao(LocalDateTime.now());
    }
    os.setStatus(next);
    os.setAtualizadoEm(LocalDateTime.now());
    os.setAlteradoPor(auth.ator());
    String ev =
        switch (next) {
          case EM_ANDAMENTO -> "INICIADA";
          case SUSPENSA -> "SUSPENSA";
          case CONCLUIDA -> "CONCLUIDA";
          case CANCELADA -> "CANCELADA";
          default -> "ALTERADA";
        };
    evento(
        "ORDEM_SERVICO_" + ev, "ORDEM_SERVICO", id, os.getResponsavelId(), old.name(), next.name());
    return os;
  }
}
