package br.com.almoxarifado.compras;

import br.com.almoxarifado.exception.*;
import br.com.almoxarifado.model.*;
import br.com.almoxarifado.repository.*;
import br.com.almoxarifado.security.*;
import br.com.almoxarifado.service.ValidacaoQuantidade;
import java.math.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PedidoCompraService {
 @org.springframework.beans.factory.annotation.Autowired private br.com.almoxarifado.obras.ContextoService contextos;

  @jakarta.persistence.PersistenceContext private jakarta.persistence.EntityManager entityManager;

  private final PedidoCompraRepository pedidos;
  private final FornecedorRepository fornecedores;
  private final NecessidadeCompraRepository necessidades;
  private final AlocacaoCompraRepository alocacoes;
  private final RecebimentoCompraRepository recebimentos;
  private final ProdutoRepository produtos;
  private final AlmoxarifadoRepository locais;
  private final FuncionarioRepository pessoas;
  private final EstoqueRepository estoques;
  private final MovimentacaoRepository movimentos;
  private final Autorizacao auth;
  private final AuditoriaService audit;

  public PedidoCompraService(
      PedidoCompraRepository p,
      FornecedorRepository f,
      NecessidadeCompraRepository n,
      AlocacaoCompraRepository a,
      RecebimentoCompraRepository r,
      ProdutoRepository pr,
      AlmoxarifadoRepository l,
      FuncionarioRepository pe,
      EstoqueRepository e,
      MovimentacaoRepository m,
      Autorizacao au,
      AuditoriaService ad) {
    pedidos = p;
    fornecedores = f;
    necessidades = n;
    alocacoes = a;
    recebimentos = r;
    produtos = pr;
    locais = l;
    pessoas = pe;
    estoques = e;
    movimentos = m;
    auth = au;
    audit = ad;
  }

  private PedidoCompra bloquear(Integer id) {
    var p =
        pedidos
            .findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));
    entityManager.refresh(p, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
    // Reload child counters explicitly, after the parent lock, without refresh cascade.
    for (var item : p.getItens()) {
      entityManager.refresh(item);
      for (var allocation : item.getAlocacoes()) entityManager.refresh(allocation);
    }
    return p;
  }

  private Map<String, Object> verPedido(PedidoCompra p, boolean detalhe) {
    var view = ComprasViews.pedido(p, detalhe);
    if (detalhe && !auth.permite("FORNECEDOR_GERENCIAR")) {
      view.put("fornecedorDocumento", DocumentoFornecedor.mascarar(p.getFornecedorDocumento()));
      view.put("fornecedorContato", null);
    }
    return view;
  }

  private void exigirAtor() {
    if (auth.ator() == null || auth.nomeAtor() == null)
      throw new ConflitoException("Identidade autenticada necessária para registrar o ator");
  }

  private static double qty(Double v) {
    if (v == null || !Double.isFinite(v) || v <= 0 || v > 1e12)
      throw new IllegalArgumentException("Quantidade deve ser positiva, finita e até 10¹²");
    return v;
  }

  private Map<Integer, NecessidadeCompra> bloquearNecessidades(Collection<Integer> ids) {
    var result = new TreeMap<Integer, NecessidadeCompra>();
    for (var id : new TreeSet<>(ids)) {
      if (id == null || id <= 0) throw new IllegalArgumentException("Necessidade inválida");
      result.put(
          id,
          necessidades
              .bloquear(id)
              .orElseThrow(() -> new RecursoNaoEncontradoException("Necessidade não encontrada")));
      // A lock does not replace a previously hydrated entity in the first-level cache.
      entityManager.refresh(result.get(id), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
    }
    return result;
  }

  private double reservado(NecessidadeCompra n) {
    double total = 0;
    for (var a : alocacoes.ativas(n.getId()))
      total =
          ComprasViews.decimal(
              total,
              ComprasViews.decimal(a.getQuantidade(), a.getQuantidadeRecebida(), false),
              true);
    return total;
  }

  private void recalcular(NecessidadeCompra n) {
    if (n.getStatus() == StatusNecessidadeCompra.CANCELADA) return;
    var antes = n.getStatus();
    n.setStatus(
        n.getQuantidadeRecebida() >= n.getQuantidade()
            ? StatusNecessidadeCompra.ATENDIDA
            : reservado(n) > 0
                ? StatusNecessidadeCompra.EM_COMPRA
                : StatusNecessidadeCompra.ABERTA);
    if (antes != n.getStatus())
      audit.registrar(
          "NECESSIDADE_COMPRA_" + n.getStatus(),
          "NECESSIDADE_COMPRA",
          n.getId().toString(),
          null,
          antes.name(),
          n.getStatus().name());
  }

  @PreAuthorize("@autorizacao.permite('COMPRA_LER')")
  @Transactional(readOnly = true)
  public Map<String, Object> buscar(Integer id) {
    return verPedido(
        pedidos
            .findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado")),
        true);
  }

  @PreAuthorize("@autorizacao.permite('COMPRA_LER')")
  @Transactional(readOnly = true)
  public Page<Map<String, Object>> listar(
      String numero,
      Integer fornecedorId,
      StatusPedidoCompra status,
      Integer produtoId,
      Integer necessidadeId,
      LocalDate de,
      LocalDate ate,
      int pagina,
      int tamanho) {
    return listar(numero,fornecedorId,status,produtoId,necessidadeId,de,ate,pagina,tamanho,null,null,null);
  }
  @PreAuthorize("@autorizacao.permite('COMPRA_LER')") @Transactional(readOnly=true)
  public Page<Map<String,Object>> listar(String numero,Integer fornecedorId,StatusPedidoCompra status,Integer produtoId,Integer necessidadeId,LocalDate de,LocalDate ate,int pagina,int tamanho,Integer obraId,Integer ordemServicoId,Integer centroCustoId) {
    Specification<PedidoCompra> s = (r, q, c) -> c.conjunction();
    if(obraId!=null || ordemServicoId!=null || centroCustoId!=null) s=s.and((r,q,c)->{
      q.distinct(true);var item=r.join("itens");var allocation=item.join("alocacoes",jakarta.persistence.criteria.JoinType.LEFT);var need=allocation.join("necessidade",jakarta.persistence.criteria.JoinType.LEFT);var demand=need.join("solicitacao",jakarta.persistence.criteria.JoinType.LEFT);
      var manual=new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();var source=new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
      for(var pair: java.util.List.of(new Object[]{"obraId",obraId},new Object[]{"ordemServicoId",ordemServicoId},new Object[]{"centroCustoId",centroCustoId})) {
        String field=(String)pair[0];Integer value=(Integer)pair[1];if(value==null)continue;
        manual.add(c.equal(item.get("contexto").get(field),value));source.add(c.equal(demand.get("contexto").get(field),value));
      }
      return c.and(c.isTrue(item.get("ativo")),c.or(c.and(manual.toArray(jakarta.persistence.criteria.Predicate[]::new)),c.and(source.toArray(jakarta.persistence.criteria.Predicate[]::new))));
    });
    if (numero != null && !numero.isBlank())
      s =
          s.and(
              (r, q, c) ->
                  c.like(
                      c.lower(r.get("numero")),
                      "%"
                          + FornecedorService.texto(numero, 50, false).toLowerCase(Locale.ROOT)
                          + "%"));
    if (fornecedorId != null)
      s = s.and((r, q, c) -> c.equal(r.get("fornecedor").get("id"), fornecedorId));
    if (status != null) s = s.and((r, q, c) -> c.equal(r.get("status"), status));
    if (produtoId != null)
      s =
          s.and(
              (r, q, c) -> {
                q.distinct(true);
                var i = r.join("itens");
                return c.and(
                    c.isTrue(i.get("ativo")), c.equal(i.get("produto").get("id"), produtoId));
              });
    if (necessidadeId != null)
      s =
          s.and(
              (r, q, c) -> {
                q.distinct(true);
                var i = r.join("itens");
                return c.and(
                    c.isTrue(i.get("ativo")),
                    c.equal(i.join("alocacoes").get("necessidade").get("id"), necessidadeId));
              });
    if (de != null)
      s = s.and((r, q, c) -> c.greaterThanOrEqualTo(r.get("criadoEm"), de.atStartOfDay()));
    if (ate != null)
      s = s.and((r, q, c) -> c.lessThan(r.get("criadoEm"), ate.plusDays(1).atStartOfDay()));
    if (de != null && ate != null && de.isAfter(ate))
      throw new IllegalArgumentException("Período inválido");
    var page = pedidos.findAll(s, FornecedorService.pagina(pagina, tamanho));
    if (!page.isEmpty())
      pedidos.carregarItens(page.getContent().stream().map(PedidoCompra::getId).toList());
    return page.map(p -> verPedido(p, false));
  }

  @PreAuthorize("@autorizacao.permite('COMPRA_CRIAR')")
  public Map<String, Object> criar(ComprasInput.Pedido in) {
    exigirAtor();
    var p = new PedidoCompra();
    p.setNumero("NOVO-" + UUID.randomUUID());
    p.setStatus(StatusPedidoCompra.RASCUNHO);
    p.setCriadoEm(LocalDateTime.now());
    p.setCriadoPor(auth.ator());
    p.setCriadoPorNome(auth.nomeAtor());
    preencher(p, in);
    pedidos.saveAndFlush(p);
    p.setNumero(
        "PC-" + p.getCriadoEm().getYear() + "-" + String.format(Locale.ROOT, "%06d", p.getId()));
    pedidos.flush();
    for (var n : necessidadesDoPedido(p)) recalcular(n);
    audit.registrar(
        "PEDIDO_COMPRA_CRIADO",
        "PEDIDO_COMPRA",
        p.getId().toString(),
        null,
        null,
        p.getStatus().name());
    return verPedido(p, true);
  }

  @PreAuthorize("@autorizacao.permite('COMPRA_CRIAR')")
  public Map<String, Object> atualizar(Integer id, ComprasInput.Pedido in) {
    var p = bloquear(id);
    if (p.getStatus() != StatusPedidoCompra.RASCUNHO)
      throw new ConflitoException("Somente rascunho pode ser editado");
    var ids = new TreeSet<Integer>();
    for (var n : necessidadesDoPedido(p)) ids.add(n.getId());
    if (in != null && in.getItens() != null)
      for (var i : in.getItens())
        if (i != null && i.getAlocacoes() != null)
          for (var a : i.getAlocacoes())
            if (a != null && a.necessidadeId != null) ids.add(a.necessidadeId);
    var antigas = bloquearNecessidades(ids);
    p.getItens().forEach(i -> i.setAtivo(false));
    pedidos.flush();
    preencher(p, in);
    pedidos.saveAndFlush(p);
    antigas.values().forEach(this::recalcular);
    audit.registrar(
        "PEDIDO_COMPRA_EDITADO",
        "PEDIDO_COMPRA",
        p.getId().toString(),
        null,
        null,
        p.getStatus().name());
    return verPedido(p, true);
  }

  private List<NecessidadeCompra> necessidadesDoPedido(PedidoCompra p) {
    return p.getItens().stream()
        .filter(i -> i.getAtivo())
        .flatMap(i -> i.getAlocacoes().stream())
        .map(a -> a.getNecessidade())
        .distinct()
        .toList();
  }

  private void preencher(PedidoCompra p, ComprasInput.Pedido in) {
    if (in == null
        || in.fornecedorId == null
        || in.almoxarifadoId == null
        || in.getItens() == null
        || in.getItens().isEmpty()
        || in.getItens().size() > 500)
      throw new IllegalArgumentException("Fornecedor, destino e 1 a 500 itens são obrigatórios");
    var contextosManuais=new java.util.IdentityHashMap<ComprasInput.Item,br.com.almoxarifado.obras.ContextoOperacional>();
    var snapshots=contextos.resolverTodos(in.getItens().stream().filter(it->it!=null && it.contexto!=null).map(it->it.contexto).toList());
    for(var it:in.getItens()) if(it!=null && it.contexto!=null) contextosManuais.put(it,snapshots.get(it.contexto));
    var ids = new TreeSet<Integer>();
    for (var i : in.getItens()) {
      if (i == null || i.produtoId == null)
        throw new IllegalArgumentException("Produto obrigatório");
      if (i.getAlocacoes() != null)
        for (var a : i.getAlocacoes()) {
          if (a == null || a.necessidadeId == null)
            throw new IllegalArgumentException("Necessidade obrigatória");
          ids.add(a.necessidadeId);
        }
    }
    var ns = bloquearNecessidades(ids);
    var f = bloquearFornecedor(in.fornecedorId);
    if (!f.getAtivo()) throw new ConflitoException("Fornecedor inativo");
    p.setFornecedor(f);
    p.setFornecedorNome(f.getNome());
    p.setFornecedorDocumento(f.getDocumento());
    p.setFornecedorContato(f.getContato());
    p.setAlmoxarifado(
        locais
            .findById(in.almoxarifadoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Almoxarifado não encontrado")));
    p.setObservacao(FornecedorService.texto(in.getObservacao(), 1000, false));
    var ps = new TreeMap<Integer, Produto>();
    for (var i : in.getItens()) {
      if (ps.containsKey(i.produtoId))
        throw new IllegalArgumentException(
            "Produto duplicado; consolide suas necessidades em um item");
      ps.put(i.produtoId, null);
    }
    for (var id : ps.keySet())
      ps.put(
          id,
          produtos
              .buscarParaAtualizacao(id)
              .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado")));

    var uso = new HashMap<Integer, Double>();
    for (var input : in.getItens()) {
      var pr = ps.get(input.produtoId);
      entityManager.refresh(pr, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
      if (!pr.isAtivo()) throw new ConflitoException("Produto inativo");
      var i = new ItemPedidoCompra();
      i.setContexto(contextosManuais.get(input));
      i.setPedido(p);
      i.setProduto(pr);
      i.setCodigo(pr.getCodigo() == null ? "P-" + pr.getId() : pr.getCodigo());
      i.setNome(pr.getNome());
      i.setUnidade(
          pr.getUnidadeMedidaConfigurada() != null
              ? pr.getUnidadeMedidaConfigurada().getSigla()
              : pr.getUnidadeMedida());
      i.setQuantidadePedida(qty(input.getQuantidade()));
      ValidacaoQuantidade.validar(pr, i.getQuantidadePedida());
      if (input.getValorUnitario() == null || input.getValorUnitario().signum() < 0)
        throw new IllegalArgumentException("Valor unitário obrigatório e não negativo");
      if ((long) input.getValorUnitario().precision() - input.getValorUnitario().scale() > 15
          || input.getValorUnitario().stripTrailingZeros().scale() > 4)
        throw new IllegalArgumentException("Valor unitário acima do limite de DECIMAL(19,4)");
      try {
        i.setValorUnitario(input.getValorUnitario().setScale(4, RoundingMode.UNNECESSARY));
      } catch (ArithmeticException e) {
        throw new IllegalArgumentException("Valor unitário aceita até quatro casas decimais");
      }
      if (i.getValorUnitario().precision() > 19 || ComprasViews.subtotal(i).precision() > 19)
        throw new IllegalArgumentException("Valor acima do limite monetário");
      i.setObservacao(FornecedorService.texto(input.getObservacao(), 1000, false));
      double destinado = 0;
      var usadas = new HashSet<Integer>();
      if (input.getAlocacoes() != null)
        for (var ai : input.getAlocacoes()) {
          if (!usadas.add(ai.necessidadeId))
            throw new IllegalArgumentException("Necessidade duplicada no item");
          var n = ns.get(ai.necessidadeId);
          double q = qty(ai.getQuantidade());
          ValidacaoQuantidade.validar(pr, q);
          if (!n.isCompraRastreavel()
              || n.getStatus() == StatusNecessidadeCompra.CANCELADA
              || n.getStatus() == StatusNecessidadeCompra.ATENDIDA
              || !n.getProduto().getId().equals(pr.getId())
              || !n.getAlmoxarifado().getId().equals(p.getAlmoxarifado().getId()))
            throw new ConflitoException("Necessidade incompatível com produto, destino ou estado");
          double livre =
              ComprasViews.decimal(
                  ComprasViews.decimal(n.getQuantidade(), n.getQuantidadeRecebida(), false),
                  reservado(n),
                  false);
          double soma = ComprasViews.decimal(uso.getOrDefault(n.getId(), 0d), q, true);
          if (soma > livre)
            throw new ConflitoException("Quantidade da necessidade já vinculada ou excedida");
          uso.put(n.getId(), soma);
          var a = new AlocacaoCompra();
          a.setItem(i);
          a.setNecessidade(n);
          a.setQuantidade(q);
          i.getAlocacoes().add(a);
          destinado = ComprasViews.decimal(destinado, q, true);
        }
      i.setQuantidadeEstoque(ComprasViews.decimal(i.getQuantidadePedida(), destinado, false));
      if(i.getContexto()!=null && i.getQuantidadeEstoque()<=0) throw new IllegalArgumentException("Contexto manual exige quantidade para estoque; alocações conservam suas origens próprias");
      if (i.getQuantidadeEstoque() < 0)
        throw new IllegalArgumentException("Vínculos excedem quantidade pedida");
      if (i.getQuantidadeEstoque() > 0 && !Boolean.TRUE.equals(input.paraEstoque))
        throw new IllegalArgumentException(
            "Quantidade adicional para estoque exige confirmação explícita");
      p.getItens().add(i);
    }
  }

  @PreAuthorize("@autorizacao.permite('COMPRA_CRIAR')")
  public Map<String, Object> submeter(Integer id) {
    var p = bloquear(id);
    if (p.getStatus() != StatusPedidoCompra.RASCUNHO)
      throw new ConflitoException("Submissão exige rascunho");
    validarAtivos(p);
    p.setStatus(StatusPedidoCompra.AGUARDANDO_APROVACAO);
    p.setSubmetidoEm(LocalDateTime.now());
    audit.registrar(
        "PEDIDO_COMPRA_SUBMETIDO",
        "PEDIDO_COMPRA",
        p.getId().toString(),
        null,
        "RASCUNHO",
        p.getStatus().name());
    return verPedido(p, true);
  }

  private Fornecedor bloquearFornecedor(Integer id) {
    var f =
        fornecedores
            .findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado"));
    entityManager.refresh(f, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
    return f;
  }

  private void validarAtivos(PedidoCompra p) {
    if (!bloquearFornecedor(p.getFornecedor().getId()).getAtivo())
      throw new ConflitoException("Fornecedor inativo");
    var ids = new TreeSet<Integer>();
    p.getItens().stream()
        .filter(ItemPedidoCompra::getAtivo)
        .forEach(i -> ids.add(i.getProduto().getId()));
    for (var id : ids) {
      var product = produtos.buscarParaAtualizacao(id).orElseThrow();
      entityManager.refresh(product, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
      if (!product.isAtivo()) throw new ConflitoException("Produto inativo");
    }
  }

  @PreAuthorize("@autorizacao.permite('COMPRA_APROVAR')")
  public Map<String, Object> aprovar(Integer id) {
    exigirAtor();
    var p = bloquear(id);
    if (p.getStatus() == StatusPedidoCompra.APROVADO) return verPedido(p, true);
    if (p.getStatus() != StatusPedidoCompra.AGUARDANDO_APROVACAO)
      throw new ConflitoException("Aprovação exige pedido submetido");
    validarAtivos(p);
    p.setStatus(StatusPedidoCompra.APROVADO);
    p.setAprovadoEm(LocalDateTime.now());
    p.setAprovadoPor(auth.ator());
    p.setAprovadoPorNome(auth.nomeAtor());
    audit.registrar(
        "PEDIDO_COMPRA_APROVADO",
        "PEDIDO_COMPRA",
        p.getId().toString(),
        null,
        "AGUARDANDO_APROVACAO",
        p.getStatus().name());
    return verPedido(p, true);
  }

  @PreAuthorize("@autorizacao.permite('COMPRA_CANCELAR')")
  public Map<String, Object> cancelar(Integer id, ComprasInput.Cancelamento in) {
    exigirAtor();
    String motivo = FornecedorService.texto(in == null ? null : in.motivo, 1000, true);
    var p = bloquear(id);
    if (p.getStatus() == StatusPedidoCompra.CANCELADO) return verPedido(p, true);
    if (p.getItens().stream().anyMatch(i -> i.getQuantidadeRecebida() > 0))
      throw new ConflitoException(
          "Pedido com recebimento não pode ser cancelado; estorno não faz parte deste bloco");
    var ns =
        bloquearNecessidades(
            necessidadesDoPedido(p).stream().map(NecessidadeCompra::getId).toList());
    var antes = p.getStatus();
    p.setStatus(StatusPedidoCompra.CANCELADO);
    p.setCanceladoEm(LocalDateTime.now());
    p.setCanceladoPor(auth.ator());
    p.setCanceladoPorNome(auth.nomeAtor());
    p.setMotivoCancelamento(motivo);
    pedidos.flush();
    ns.values().forEach(this::recalcular);
    audit.registrar(
        "PEDIDO_COMPRA_CANCELADO",
        "PEDIDO_COMPRA",
        p.getId().toString(),
        null,
        antes.name(),
        p.getStatus().name());
    return verPedido(p, true);
  }

  @PreAuthorize("@autorizacao.permite('NECESSIDADE_COMPRA_GERENCIAR')")
  public Map<String, Object> cancelarNecessidade(Integer id, ComprasInput.Cancelamento in) {
    String motivo = FornecedorService.texto(in == null ? null : in.motivo, 1000, true);
    var n = bloquearNecessidades(List.of(id)).get(id);
    if (!n.isCompraRastreavel()
        || n.getStatus() == StatusNecessidadeCompra.ATENDIDA
        || n.getQuantidadeRecebida() > 0
        || reservado(n) > 0)
      throw new ConflitoException("Necessidade recebida ou vinculada não pode ser cancelada");
    n.setStatus(StatusNecessidadeCompra.CANCELADA);
    n.setMotivoCancelamento(motivo);
    audit.registrar(
        "NECESSIDADE_COMPRA_CANCELADA",
        "NECESSIDADE_COMPRA",
        id.toString(),
        null,
        null,
        "CANCELADA");
    return ComprasViews.map("id", id, "status", n.getStatus(), "motivo", motivo);
  }

  @PreAuthorize("@autorizacao.permite('RECEBIMENTO_LER')")
  @Transactional(readOnly = true)
  public org.springframework.data.domain.Page<Map<String, Object>> historico(
      Integer id, int pagina, int tamanho) {
    if (!pedidos.existsById(id)) throw new RecursoNaoEncontradoException("Pedido não encontrado");
    var page = recebimentos.findByPedidoId(id, FornecedorService.pagina(pagina, tamanho));
    carregarRecebimentos(page);
    return page.map(ComprasViews::recebimento);
  }

  @PreAuthorize("@autorizacao.permite('RECEBIMENTO_LER')")
  @Transactional(readOnly = true)
  public Map<String, Object> buscarRecebimento(Integer id) {
    return ComprasViews.recebimento(
        recebimentos
            .findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Recebimento não encontrado")));
  }

  @PreAuthorize("@autorizacao.permite('RECEBIMENTO_LER')")
  @Transactional(readOnly = true)
  public Page<Map<String, Object>> listarRecebimentos(
      Integer pedidoId,
      Integer fornecedorId,
      Integer produtoId,
      Integer almoxarifadoId,
      LocalDate de,
      LocalDate ate,
      int pagina,
      int tamanho) {
    Specification<RecebimentoCompra> s = (r, q, c) -> c.conjunction();
    if (pedidoId != null) s = s.and((r, q, c) -> c.equal(r.get("pedido").get("id"), pedidoId));
    if (fornecedorId != null)
      s = s.and((r, q, c) -> c.equal(r.get("pedido").get("fornecedor").get("id"), fornecedorId));
    if (almoxarifadoId != null)
      s = s.and((r, q, c) -> c.equal(r.get("almoxarifado").get("id"), almoxarifadoId));
    if (produtoId != null)
      s =
          s.and(
              (r, q, c) -> {
                q.distinct(true);
                return c.equal(
                    r.join("itens").get("itemPedido").get("produto").get("id"), produtoId);
              });
    if (de != null)
      s = s.and((r, q, c) -> c.greaterThanOrEqualTo(r.get("dataHora"), de.atStartOfDay()));
    if (ate != null)
      s = s.and((r, q, c) -> c.lessThan(r.get("dataHora"), ate.plusDays(1).atStartOfDay()));
    if (de != null && ate != null && de.isAfter(ate))
      throw new IllegalArgumentException("Período inválido");
    var page = recebimentos.findAll(s, FornecedorService.pagina(pagina, tamanho));
    carregarRecebimentos(page);
    return page.map(ComprasViews::recebimento);
  }

  private void carregarRecebimentos(Page<RecebimentoCompra> page) {
    if (!page.isEmpty())
      recebimentos.carregarItens(page.getContent().stream().map(RecebimentoCompra::getId).toList());
  }

  private static String encodeObservation(String value) {
    var normalized = FornecedorService.texto(value, 1000, false);
    return normalized == null
        ? "NULL"
        : "TEXT:" + Base64.getEncoder().encodeToString(normalized.getBytes(StandardCharsets.UTF_8));
  }

  private String fingerprint(Integer id, ComprasInput.Recebimento in) {
    try {
      var b =
          new StringBuilder()
              .append(id)
              .append('|')
              .append(in.almoxarifadoId)
              .append('|')
              .append(in.responsavelId)
              .append('|')
              .append(encodeObservation(in.getObservacao()));
      var sorted = new TreeMap<Integer, Double>();
      for (var i : in.getItens()) {
        if (i == null
            || i.itemPedidoId == null
            || sorted.putIfAbsent(i.itemPedidoId, qty(i.getQuantidade())) != null)
          throw new IllegalArgumentException("Item recebido inválido ou duplicado");
      }
      sorted.forEach(
          (k, v) ->
              b.append('|')
                  .append(k)
                  .append(':')
                  .append(BigDecimal.valueOf(v).stripTrailingZeros().toPlainString()));
      return HexFormat.of()
          .formatHex(
              MessageDigest.getInstance("SHA-256")
                  .digest(b.toString().getBytes(StandardCharsets.UTF_8)));
    } catch (java.security.NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 indisponível");
    }
  }

  @PreAuthorize("@autorizacao.permite('RECEBIMENTO_REGISTRAR')")
  public Map<String, Object> receber(Integer id, ComprasInput.Recebimento in, String chave) {
    exigirAtor();
    if (chave == null || !chave.matches("[A-Za-z0-9._:-]{16,100}"))
      throw new IllegalArgumentException("Idempotency-Key deve ter 16 a 100 caracteres válidos");
    if (in == null
        || in.almoxarifadoId == null
        || in.responsavelId == null
        || in.getItens() == null
        || in.getItens().isEmpty()
        || in.getItens().size() > 500)
      throw new IllegalArgumentException("Destino, responsável e itens recebidos são obrigatórios");
    String assinatura = fingerprint(id, in);
    var p = bloquear(id);
    var retry = recebimentos.findByChaveIdempotencia(chave);
    if (retry.isPresent()) {
      if (!retry.get().getAssinaturaPayload().equals(assinatura))
        throw new ConflitoException("Idempotency-Key já utilizada com outro recebimento");
      return ComprasViews.recebimento(retry.get());
    }
    if (p.getStatus() != StatusPedidoCompra.APROVADO
        && p.getStatus() != StatusPedidoCompra.PARCIALMENTE_RECEBIDO)
      throw new ConflitoException("Recebimento exige pedido aprovado ou parcialmente recebido");
    if (!p.getAlmoxarifado().getId().equals(in.almoxarifadoId))
      throw new ConflitoException("Destino deve ser o almoxarifado planejado no pedido");
    var ns =
        bloquearNecessidades(
            necessidadesDoPedido(p).stream().map(NecessidadeCompra::getId).toList());
    var itens = new TreeMap<Integer, ItemPedidoCompra>();
    for (var input : in.getItens()) {
      var i =
          p.getItens().stream()
              .filter(x -> x.getAtivo() && x.getId().equals(input.itemPedidoId))
              .findFirst()
              .orElseThrow(() -> new ConflitoException("Item não pertence ao pedido"));
      if (ComprasViews.decimal(i.getQuantidadeRecebida(), input.getQuantidade(), true)
          > i.getQuantidadePedida())
        throw new ConflitoException("Recebimento excede quantidade pendente");
      itens.put(i.getProduto().getId(), i);
    }
    for (var prod : itens.keySet()) {
      var product = produtos.buscarParaAtualizacao(prod).orElseThrow();
      entityManager.refresh(product, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
    }
    var r = new RecebimentoCompra();
    r.setPedido(p);
    r.setAlmoxarifado(p.getAlmoxarifado());
    r.setResponsavel(
        pessoas
            .findById(in.responsavelId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado")));
    r.setRecebidoPor(auth.ator());
    r.setRecebidoPorNome(auth.nomeAtor());
    r.setDataHora(LocalDateTime.now());
    r.setObservacao(FornecedorService.texto(in.getObservacao(), 1000, false));
    r.setChaveIdempotencia(chave);
    r.setAssinaturaPayload(assinatura);

    for (var i : itens.values()) {
      double q =
          in.getItens().stream()
              .filter(x -> x.itemPedidoId.equals(i.getId()))
              .findFirst()
              .orElseThrow()
              .getQuantidade();
      ValidacaoQuantidade.validar(i.getProduto(), q);
      var e =
          estoques
              .buscarParaAtualizacao(i.getProduto().getId(), p.getAlmoxarifado().getId())
              .orElseGet(
                  () -> {
                    var novo = new Estoque();
                    novo.setProduto(i.getProduto());
                    novo.setAlmoxarifado(p.getAlmoxarifado());
                    novo.setQuantidade(0);
                    return estoques.saveAndFlush(novo);
                  });
      entityManager.refresh(e, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
      if (!Double.isFinite(e.getQuantidade()) || e.getQuantidade() < 0)
        throw new ConflitoException("Saldo inválido; recebimento bloqueado");
      var ri = new ItemRecebimentoCompra();
      ri.setRecebimento(r);
      ri.setItemPedido(i);
      ri.setQuantidade(q);
      ri.setSaldoAnterior(e.getQuantidade());
      ri.setSaldoPosterior(ComprasViews.decimal(e.getQuantidade(), q, true));
      if (!Double.isFinite(ri.getSaldoPosterior()))
        throw new ConflitoException("Saldo excede limite");
      e.setQuantidade(ri.getSaldoPosterior());
      i.setQuantidadeRecebida(ComprasViews.decimal(i.getQuantidadeRecebida(), q, true));
      double restante = q;
      for (var a :
          i.getAlocacoes().stream()
              .sorted(Comparator.comparing(x -> x.getNecessidade().getId()))
              .toList()) {
        double parte =
            Math.min(
                restante,
                ComprasViews.decimal(a.getQuantidade(), a.getQuantidadeRecebida(), false));
        if (parte <= 0) continue;
        a.setQuantidadeRecebida(ComprasViews.decimal(a.getQuantidadeRecebida(), parte, true));
        var n = ns.get(a.getNecessidade().getId());
        n.setQuantidadeRecebida(ComprasViews.decimal(n.getQuantidadeRecebida(), parte, true));
        if (n.getQuantidadeRecebida() > n.getQuantidade())
          throw new ConflitoException("Recebimento excede necessidade");
        var d = new DestinacaoRecebimento();
        d.setItemRecebimento(ri);
        d.setAlocacao(a);
        d.setQuantidade(parte);
        ri.getDestinacoes().add(d);
        restante = ComprasViews.decimal(restante, parte, false);
      }
      r.getItens().add(ri);
    }

    recebimentos.saveAndFlush(r);
    for (var ri : r.getItens()) {
      var m = new Movimentacao();
      m.setProduto(ri.getItemPedido().getProduto());
      m.setAlmoxarifado(r.getAlmoxarifado());
      m.setResponsavel(r.getResponsavel());
      m.setTipo(TipoMovimentacao.ENTRADA);
      m.setQuantidade(ri.getQuantidade());
      m.setSaldoAnterior(ri.getSaldoAnterior());
      m.setSaldoPosterior(ri.getSaldoPosterior());
      m.setDataHora(r.getDataHora());
      m.setItemRecebimentoCompra(ri);
      m.setAtorCompraId(r.getRecebidoPor());
      movimentos.save(m);
    }
    p.setStatus(
        p.getItens().stream()
                .filter(i -> i.getAtivo())
                .allMatch(i -> i.getQuantidadeRecebida() >= i.getQuantidadePedida())
            ? StatusPedidoCompra.RECEBIDO
            : StatusPedidoCompra.PARCIALMENTE_RECEBIDO);
    pedidos.flush();
    ns.values().forEach(this::recalcular);
    audit.registrar(
        "RECEBIMENTO_COMPRA_REGISTRADO",
        "RECEBIMENTO_COMPRA",
        r.getId().toString(),
        r.getResponsavel().getId(),
        null,
        "pedido=" + p.getId() + ";itens=" + r.getItens().size());
    return ComprasViews.recebimento(r);
  }
}
