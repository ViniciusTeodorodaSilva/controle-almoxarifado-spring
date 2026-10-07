package br.com.almoxarifado.obras;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.almoxarifado.compras.*;
import br.com.almoxarifado.dto.*;
import br.com.almoxarifado.exception.*;
import br.com.almoxarifado.model.*;
import br.com.almoxarifado.repository.*;
import br.com.almoxarifado.security.*;
import br.com.almoxarifado.service.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:bes-obras;MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=15000",
      "logging.level.root=WARN",
      "debug=false",
      "spring.jpa.properties.hibernate.generate_statistics=true"
    })
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ObrasTests {
  @Autowired EstruturaService estrutura;
  @Autowired ContextoService contextos;
  @Autowired ResumoEstruturaService resumos;
  @Autowired ObraRepository obras;
  @Autowired CentroCustoRepository centros;
  @Autowired OrdemServicoRepository ordens;
  @Autowired SolicitacaoService solicitacoes;
  @Autowired NecessidadeCompraService necessidades;
  @Autowired AtendimentoSolicitacaoService atendimento;
  @Autowired PedidoCompraService compras;
  @Autowired FornecedorService fornecedores;
  @Autowired UsuarioRepository usuarios;
  @Autowired ProdutoRepository produtos;
  @Autowired AlmoxarifadoRepository locais;
  @Autowired FuncionarioRepository pessoas;
  @Autowired MovimentacaoRepository movimentos;
  @Autowired EstoqueRepository estoques;
  @Autowired AuditoriaRepository eventos;
  @Autowired TransactionTemplate tx;
  @Autowired MockMvc mvc;
  @Autowired jakarta.persistence.EntityManager em;
  @MockitoSpyBean AuditoriaService audit;
  Usuario actor;
  Produto produto;
  Funcionario pessoa;
  Almoxarifado local;
  Obra obra;
  CentroCusto centro;
  OrdemServico os;

  @BeforeEach
  void setup() {
    actor =
        usuarios.save(
            new Usuario(
                "obras-" + UUID.randomUUID(),
                "fixture-hash-nao-utilizado",
                "Ator H2",
                Perfil.ADMIN));
    identity(Perfil.ADMIN);
    produto = new Produto();
    produto.setNome("Material de obra H2");
    produto.setCodigo(UUID.randomUUID().toString());
    produto.setUnidadeMedida("UN");
    produto = produtos.save(produto);
    pessoa = new Funcionario();
    pessoa.setNome("Responsável físico H2");
    pessoa.setMatricula(UUID.randomUUID().toString());
    pessoa = pessoas.save(pessoa);
    local = new Almoxarifado();
    local.setNome("Central H2");
    local = locais.save(local);
    obra = estrutura.salvarObra(null, obraInput());
    var ci = centroInput();
    ci.obraId = obra.getId();
    ci.tipo = TipoCentroCusto.OBRA;
    centro = estrutura.salvarCentro(null, ci);
    var oi = ordemInput();
    oi.obraId = obra.getId();
    oi.centroCustoId = centro.getId();
    os = estrutura.salvarOrdem(null, oi);
  }

  void identity(Perfil profile) {
    if (actor.getPerfil() != profile)
      actor =
          usuarios.save(
              new Usuario(
                  "obras-" + UUID.randomUUID(), "fixture-hash-nao-utilizado", "Ator H2", profile));
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                new Identidade(
                    actor.getId(),
                    actor.getUsername(),
                    actor.getAuthVersion(),
                    System.currentTimeMillis()),
                null,
                profile.permissoes().stream()
                    .map(p -> new SimpleGrantedAuthority(p.name()))
                    .toList()));
  }

  @AfterEach
  void cleanup() {
    reset(audit);
    SecurityContextHolder.clearContext();
  }

  ObrasInput.Obra obraInput() {
    var i = new ObrasInput.Obra();
    i.codigo = "OB-" + UUID.randomUUID();
    i.nome = "Obra A H2";
    i.cliente = "Cliente identificado H2";
    return i;
  }

  ObrasInput.Centro centroInput() {
    var i = new ObrasInput.Centro();
    i.codigo = "CC-" + UUID.randomUUID();
    i.nome = "Centro H2";
    i.tipo = TipoCentroCusto.ADMINISTRATIVO;
    i.ativo = true;
    return i;
  }

  ObrasInput.Ordem ordemInput() {
    var i = new ObrasInput.Ordem();
    i.obraId = obra.getId();
    i.centroCustoId = centro.getId();
    i.titulo = "Serviço H2";
    i.responsavelId = pessoa.getId();
    return i;
  }

  ObrasInput.Transicao transition(String status) {
    var t = new ObrasInput.Transicao();
    t.status = status;
    t.motivo = "Interrupção conferida H2";
    return t;
  }

  NecessidadeCompraResponse need(Obra o, OrdemServico s, CentroCusto c, double q) {
    var r = solicitacoes.cadastrar(pessoa.getId(), local.getId(), o.getId(), s.getId(), c.getId());
    var item = solicitacoes.adicionarItem(r.getId(), produto.getId(), q);
    solicitacoes.aprovar(r.getId(), pessoa.getId());
    return necessidades.criar(
        new NecessidadeCompraInput(item.getId(), pessoa.getId()), UUID.randomUUID().toString());
  }

  ComprasInput.Pedido pedido(double q, List<NecessidadeCompraResponse> ns) {
    var f = new ComprasInput.Fornecedor();
    f.nome = "Fornecedor de obra H2";
    f.tipoPessoa = TipoPessoa.PJ;
    var p = new ComprasInput.Pedido();
    p.fornecedorId = (Integer) fornecedores.criar(f).get("id");
    p.almoxarifadoId = local.getId();
    var item = new ComprasInput.Item();
    item.produtoId = produto.getId();
    item.quantidade = q;
    item.valorUnitario = new BigDecimal("7.1250");
    item.paraEstoque = ns.isEmpty();
    item.alocacoes = new ArrayList<>();
    for (var n : ns) {
      var a = new ComprasInput.Alocacao();
      a.necessidadeId = n.id();
      a.quantidade = n.quantidade();
      item.alocacoes.add(a);
    }
    p.itens = List.of(item);
    return p;
  }

  int approve(ComprasInput.Pedido input) {
    int id = (Integer) compras.criar(input).get("id");
    compras.submeter(id);
    compras.aprovar(id);
    return id;
  }

  void receive(int id, double q) {
    var i = new ComprasInput.Recebimento();
    i.almoxarifadoId = local.getId();
    i.responsavelId = pessoa.getId();
    var ri = new ComprasInput.ItemRecebido();
    ri.itemPedidoId =
        (Integer) ((List<Map<String, Object>>) compras.buscar(id).get("itens")).get(0).get("id");
    ri.quantidade = q;
    i.itens = List.of(ri);
    compras.receber(id, i, UUID.randomUUID().toString());
  }

  @Test
  void cicloCompletoContextualSemConsumoNoRecebimento() {
    var n = need(obra, os, centro, 8);
    int p = approve(pedido(8, List.of(n)));
    receive(p, 8);
    assertEquals(
        StatusSolicitacao.APROVADA,
        solicitacoes.buscarPorId(n.solicitacaoId()).orElseThrow().getStatus());
    assertTrue(((List<?>) resumos.obra(obra.getId()).get("materiaisConsumidos")).isEmpty());
    assertEquals(1L, resumos.ordem(os.getId()).get("quantidadePedidos"));
    atendimento.iniciarSeparacao(n.solicitacaoId(), pessoa.getId());
    atendimento.atender(
        n.solicitacaoId(),
        new AtendimentoInput(
            pessoa.getId(), List.of(new AtendimentoInput.Item(n.itemSolicitacaoId(), 8d))),
        UUID.randomUUID().toString());
    var saida = movimentos.findBySolicitacaoId(n.solicitacaoId()).get(0);
    assertEquals(obra.getId(), saida.getContexto().getObraId());
    assertEquals(os.getId(), saida.getContexto().getOrdemServicoId());
    assertEquals(centro.getId(), saida.getContexto().getCentroCustoId());
    assertEquals(1, ((List<?>) resumos.obra(obra.getId()).get("materiaisConsumidos")).size());
    assertNull(resumos.centro(centro.getId()).get("custoConsumido"));
    assertEquals(
        0,
        estoques
            .findByProdutoIdAndAlmoxarifadoId(produto.getId(), local.getId())
            .orElseThrow()
            .getQuantidade());
  }

  @Test
  void duasObrasMesmoProdutoPreservamAlocacoes() {
    var n1 = need(obra, os, centro, 3);
    var o2 = estrutura.salvarObra(null, obraInput());
    var c2i = centroInput();
    c2i.obraId = o2.getId();
    var c2 = estrutura.salvarCentro(null, c2i);
    var s2i = ordemInput();
    s2i.obraId = o2.getId();
    s2i.centroCustoId = c2.getId();
    var s2 = estrutura.salvarOrdem(null, s2i);
    var n2 = need(o2, s2, c2, 5);
    int p = approve(pedido(8, List.of(n1, n2)));
    var item = ((List<Map<String, Object>>) compras.buscar(p).get("itens")).get(0);
    var allocations = (List<Map<String, Object>>) item.get("alocacoes");
    assertEquals(2, allocations.size());
    assertEquals(
        obra.getId(), ((ContextoOperacional) allocations.get(0).get("contexto")).getObraId());
    assertEquals(
        o2.getId(), ((ContextoOperacional) allocations.get(1).get("contexto")).getObraId());
    assertEquals(
        1,
        compras
            .listar(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                20,
                obra.getId(),
                os.getId(),
                centro.getId())
            .getTotalElements());
    assertEquals(
        0,
        compras
            .listar(null, null, null, null, null, null, null, 0, 20, obra.getId(), s2.getId(), null)
            .getTotalElements());
    receive(p, 8);
    assertEquals(3d, necessidades.buscar(n1.id()).compra().get("quantidadeRecebida"));
    assertEquals(5d, necessidades.buscar(n2.id()).compra().get("quantidadeRecebida"));
    assertEquals(1L, resumos.obra(o2.getId()).get("quantidadePedidos"));
    assertTrue(((List<?>) resumos.obra(o2.getId()).get("materiaisConsumidos")).isEmpty());
  }

  @Test
  void compraGeralNaoCriaContexto() {
    int id = approve(pedido(2, List.of()));
    receive(id, 2);
    assertNull(
        ((List<Map<String, Object>>) compras.buscar(id).get("itens")).get(0).get("contexto"));
    assertNull(movimentos.findByProdutoId(produto.getId()).get(0).getContexto());
    assertEquals(0L, resumos.obra(obra.getId()).get("quantidadePedidos"));
  }

  @Test
  void compraManualContextualRastreiaSemConsumo() {
    var p = pedido(2, List.of());
    p.itens.get(0).contexto = new ObrasInput.Contexto();
    p.itens.get(0).contexto.ordemServicoId = os.getId();
    int id = approve(p);
    receive(id, 2);
    assertEquals(1L, resumos.obra(obra.getId()).get("quantidadePedidos"));
    assertTrue(((List<?>) resumos.obra(obra.getId()).get("materiaisConsumidos")).isEmpty());
  }

  @Test
  void osOutraObraRejeitada() {
    var o = estrutura.salvarObra(null, obraInput());
    assertThrows(ConflitoException.class, () -> contextos.resolver(o.getId(), os.getId(), null));
  }

  @Test
  void centroOutraObraRejeitado() {
    var o = estrutura.salvarObra(null, obraInput());
    var c = centroInput();
    c.obraId = o.getId();
    var cc = estrutura.salvarCentro(null, c);
    assertThrows(
        ConflitoException.class, () -> contextos.resolver(obra.getId(), os.getId(), cc.getId()));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2})
  void referenciasInexistentes(int kind) {
    assertThrows(
        RecursoNaoEncontradoException.class,
        () ->
            contextos.resolver(
                kind == 0 ? Integer.MAX_VALUE : null,
                kind == 1 ? Integer.MAX_VALUE : null,
                kind == 2 ? Integer.MAX_VALUE : null));
  }

  @Test
  void osSugereCentroNoServidor() {
    var c = contextos.resolver(null, os.getId(), null);
    assertEquals(obra.getId(), c.getObraId());
    assertEquals(centro.getId(), c.getCentroCustoId());
  }

  @Test
  void centroInativoNaoAceitaNovaDemanda() {
    var ci = centroInput();
    ci.codigo = centro.getCodigo();
    ci.obraId = obra.getId();
    ci.ativo = false;
    estrutura.salvarCentro(centro.getId(), ci);
    assertThrows(ConflitoException.class, () -> contextos.resolver(null, os.getId(), null));
    assertNotNull(estrutura.ordem(os.getId()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"SUSPENSA", "CONCLUIDA", "CANCELADA"})
  void obraNaoOperacionalRecusaNovaDemandaMasPreservaLeitura(String status) {
    var outra = estrutura.salvarObra(null, obraInput());
    estrutura.statusObra(outra.getId(), transition("ATIVA"));
    estrutura.statusObra(outra.getId(), transition(status));
    assertThrows(
        ConflitoException.class,
        () -> solicitacoes.cadastrar(pessoa.getId(), local.getId(), outra.getId(), null, null));
    assertEquals(StatusObra.valueOf(status), estrutura.obra(outra.getId()).getStatus());
    assertEquals(0L, resumos.obra(outra.getId()).get("quantidadeSolicitacoes"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"SUSPENSA", "CONCLUIDA", "CANCELADA"})
  void osNaoOperacionalRecusaNovaDemandaMasPreservaLeitura(String status) {
    estrutura.statusOrdem(os.getId(), transition("EM_ANDAMENTO"));
    estrutura.statusOrdem(os.getId(), transition(status));
    assertThrows(
        ConflitoException.class,
        () -> solicitacoes.cadastrar(pessoa.getId(), local.getId(), null, os.getId(), null));
    assertEquals(StatusOrdemServico.valueOf(status), estrutura.ordem(os.getId()).getStatus());
    assertEquals(0L, resumos.ordem(os.getId()).get("quantidadeSolicitacoes"));
  }

  @Test
  void centroCorporativoConservaIdentidadeEFiltraContextosSemAbsorverOutraObra() {
    var corporativo = estrutura.salvarCentro(null, centroInput());
    var outra = estrutura.salvarObra(null, obraInput());
    assertNull(corporativo.getObraId());
    assertFalse(estrutura.listarCentros(null, null, null, obra.getId(), 0, 100)
        .getContent().stream().anyMatch(c -> c.getId().equals(corporativo.getId())));
    assertTrue(estrutura.listarCentros(null, null, null, obra.getId(), 0, 100, true)
        .getContent().stream().anyMatch(c -> c.getId().equals(corporativo.getId())));
    var geral = solicitacoes.cadastrar(
        pessoa.getId(), local.getId(), null, null, corporativo.getId());
    var contextual = solicitacoes.cadastrar(
        pessoa.getId(), local.getId(), outra.getId(), null, corporativo.getId());
    assertNull(geral.getContexto().getObraId());
    assertEquals(outra.getId(), contextual.getContexto().getObraId());
    assertEquals(2L, resumos.centro(corporativo.getId()).get("quantidadeSolicitacoes"));
    assertEquals(0L, resumos.obra(obra.getId()).get("quantidadeSolicitacoes"));
    assertEquals(1L, resumos.obra(outra.getId()).get("quantidadeSolicitacoes"));
    assertThrows(ConflitoException.class,
        () -> solicitacoes.cadastrar(
            pessoa.getId(), local.getId(), obra.getId(), os.getId(), corporativo.getId()));
  }

  @Test
  void historicoNaoMudaQuandoCadastrosRenomeados() {
    var n = need(obra, os, centro, 2);
    var oi = obraInput();
    oi.nome = "Novo nome";
    estrutura.salvarObra(obra.getId(), oi);
    var ci = centroInput();
    ci.obraId = obra.getId();
    ci.nome = "Outro centro";
    estrutura.salvarCentro(centro.getId(), ci);
    assertEquals("Obra A H2", necessidades.buscar(n.id()).contexto().getObraNome());
    assertEquals(obra.getCodigo(), necessidades.buscar(n.id()).contexto().getObraCodigo());
  }

  @Test
  void estruturaOSImutavelMesmoAntesDoUso() {
    var in = ordemInput();
    in.centroCustoId = null;
    assertThrows(ConflitoException.class, () -> estrutura.salvarOrdem(os.getId(), in));
  }

  @Test
  void obraDoCentroImutavel() {
    var in = centroInput();
    in.obraId = null;
    assertThrows(ConflitoException.class, () -> estrutura.salvarCentro(centro.getId(), in));
  }

  @Test
  void encerramentoBloqueiaDemandaPendente() {
    need(obra, os, centro, 2);
    estrutura.statusOrdem(os.getId(), transition("EM_ANDAMENTO"));
    assertThrows(
        ConflitoException.class, () -> estrutura.statusOrdem(os.getId(), transition("CONCLUIDA")));
    assertThrows(
        ConflitoException.class, () -> estrutura.statusOrdem(os.getId(), transition("CANCELADA")));
  }

  @Test
  void obraNaoEncerraComOSAbertas() {
    estrutura.statusObra(obra.getId(), transition("ATIVA"));
    assertThrows(
        ConflitoException.class, () -> estrutura.statusObra(obra.getId(), transition("CONCLUIDA")));
  }

  @Test
  void cicloStatusOSDatasServidor() {
    estrutura.statusOrdem(os.getId(), transition("EM_ANDAMENTO"));
    estrutura.statusOrdem(os.getId(), transition("SUSPENSA"));
    assertThrows(ConflitoException.class, () -> contextos.resolver(null, os.getId(), null));
    estrutura.statusOrdem(os.getId(), transition("EM_ANDAMENTO"));
    var done = estrutura.statusOrdem(os.getId(), transition("CONCLUIDA"));
    assertNotNull(done.getDataInicio());
    assertNotNull(done.getDataConclusao());
    assertThrows(
        ConflitoException.class, () -> estrutura.statusOrdem(os.getId(), transition("CONCLUIDA")));
    assertThrows(ConflitoException.class, () -> contextos.resolver(null, os.getId(), null));
  }

  @Test
  void cancelarOSApenasUmaVez() {
    estrutura.statusOrdem(os.getId(), transition("CANCELADA"));
    assertThrows(
        ConflitoException.class, () -> estrutura.statusOrdem(os.getId(), transition("CANCELADA")));
  }

  @Test
  void concluirObraComOSResolvida() {
    estrutura.statusOrdem(os.getId(), transition("CANCELADA"));
    estrutura.statusObra(obra.getId(), transition("ATIVA"));
    assertNotNull(estrutura.statusObra(obra.getId(), transition("CONCLUIDA")).getDataTerminoReal());
    assertThrows(
        ConflitoException.class, () -> estrutura.statusObra(obra.getId(), transition("ATIVA")));
  }

  @Test
  void auditoriaTemAtorAutenticadoSeparadoDoResponsavel() {
    var ev =
        eventos.findAll().stream()
            .filter(
                e ->
                    e.getEvento().equals("ORDEM_SERVICO_CRIADA")
                        && e.getReferencia().equals(os.getId().toString()))
            .findFirst()
            .orElseThrow();
    assertEquals(actor.getId(), ev.getAtorId());
    assertEquals(pessoa.getId(), ev.getResponsavelOperacionalId());
  }

  @Test
  void falhaAuditoriaReverteCadastro() {
    long before = obras.count();
    tx.executeWithoutResult(
        t ->
            doThrow(new IllegalStateException("falha H2"))
                .when(audit)
                .registrar(eq("OBRA_CRIADA"), anyString(), anyString(), any(), any(), any()));
    assertThrows(IllegalStateException.class, () -> estrutura.salvarObra(null, obraInput()));
    assertEquals(before, obras.count());
  }

  @Test
  void falhaAuditoriaReverteStatus() {
    tx.executeWithoutResult(
        t ->
            doThrow(new IllegalStateException("falha H2"))
                .when(audit)
                .registrar(
                    eq("ORDEM_SERVICO_INICIADA"), anyString(), anyString(), any(), any(), any()));
    assertThrows(
        IllegalStateException.class,
        () -> estrutura.statusOrdem(os.getId(), transition("EM_ANDAMENTO")));
    assertEquals(StatusOrdemServico.ABERTA, ordens.findById(os.getId()).orElseThrow().getStatus());
  }

  @ParameterizedTest
  @EnumSource(Perfil.class)
  void matrizHTTPEService(Perfil perfil) throws Exception {
    identity(perfil);
    var auth = SecurityContextHolder.getContext().getAuthentication();
    for (String path : List.of("obras", "ordens-servico", "centros-custo")) {
      mvc.perform(get("/" + path).with(authentication(auth))).andExpect(status().isOk());
      mvc.perform(
              post("/" + path)
                  .with(authentication(auth))
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{}"))
          .andExpect(status().is(perfil == Perfil.ADMIN || perfil == Perfil.GESTOR ? 400 : 403));
    }
    SecurityContextHolder.getContext().setAuthentication(auth);
    if (perfil == Perfil.CONSULTA || perfil == Perfil.ALMOXARIFE)
      assertThrows(
          org.springframework.security.access.AccessDeniedException.class,
          () -> estrutura.salvarObra(null, obraInput()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"obras", "ordens-servico", "centros-custo"})
  void csrfEIdentidadeObrigatorios(String path) throws Exception {
    mvc.perform(
            post("/" + path)
                .with(authentication(SecurityContextHolder.getContext().getAuthentication()))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isForbidden());
    mvc.perform(get("/" + path).with(anonymous())).andExpect(status().isUnauthorized());
  }

  @ParameterizedTest
  @ValueSource(strings = {"criadoPor", "alteradoPor", "dataConclusao", "status", "versao"})
  void massAssignmentOS(String field) throws Exception {
    mvc.perform(
            post("/ordens-servico")
                .with(authentication(SecurityContextHolder.getContext().getAuthentication()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"" + field + "\":\"forjado\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void filtrosBackendContextuais() {
    var n = need(obra, os, centro, 1);
    assertEquals(1, solicitacoes.listar(obra.getId(), os.getId(), centro.getId()).size());
    assertEquals(
        1,
        necessidades
            .listar(null, null, null, null, obra.getId(), os.getId(), centro.getId())
            .size());
    assertTrue(solicitacoes.listar(Integer.MAX_VALUE, null, null).isEmpty());
    assertEquals(n.solicitacaoId(), solicitacoes.listar(obra.getId(), null, null).get(0).getId());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "x x", "ÇOD", "a"})
  void codigoValidacaoENormalizacao(String codigo) {
    var in = obraInput();
    in.codigo = codigo;
    if (codigo.equals("a")) assertEquals("A", estrutura.salvarObra(null, in).getCodigo());
    else assertThrows(IllegalArgumentException.class, () -> estrutura.salvarObra(null, in));
  }

  @Test
  void duplicidadeCodigoObraECentro() {
    var in = obraInput();
    in.codigo = obra.getCodigo().toLowerCase(Locale.ROOT);
    assertThrows(
        org.springframework.dao.DataIntegrityViolationException.class,
        () -> estrutura.salvarObra(null, in));
    var ci = centroInput();
    ci.codigo = centro.getCodigo();
    assertThrows(
        org.springframework.dao.DataIntegrityViolationException.class,
        () -> estrutura.salvarCentro(null, ci));
  }

  @Test
  void tipoObraExigeVinculo() {
    var ci = centroInput();
    ci.tipo = TipoCentroCusto.OBRA;
    assertThrows(IllegalArgumentException.class, () -> estrutura.salvarCentro(null, ci));
  }

  @Test
  void datasInvalidas() {
    var in = obraInput();
    in.dataInicio = java.time.LocalDate.now();
    in.dataTerminoPrevisto = in.dataInicio.minusDays(1);
    assertThrows(IllegalArgumentException.class, () -> estrutura.salvarObra(null, in));
  }

  @Test
  void numeroOSConcorrenteUnico() throws Exception {
    var pool = Executors.newFixedThreadPool(2);
    var start = new CountDownLatch(1);
    var identity = SecurityContextHolder.getContext().getAuthentication();
    Callable<OrdemServico> task =
        () -> {
          SecurityContextHolder.getContext().setAuthentication(identity);
          try {
            start.await();
            return estrutura.salvarOrdem(null, ordemInput());
          } finally {
            SecurityContextHolder.clearContext();
          }
        };
    var a = pool.submit(task);
    var b = pool.submit(task);
    start.countDown();
    try {
      assertNotEquals(
          a.get(30, TimeUnit.SECONDS).getNumero(), b.get(30, TimeUnit.SECONDS).getNumero());
    } finally {
      pool.shutdownNow();
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"obra", "centro"})
  void codigoConcorrenteTemUmVencedor(String tipo) throws Exception {
    var pool = Executors.newFixedThreadPool(2);
    var start = new CountDownLatch(1);
    var identity = SecurityContextHolder.getContext().getAuthentication();
    String code = "RACE-" + UUID.randomUUID();
    Callable<Boolean> task =
        () -> {
          SecurityContextHolder.getContext().setAuthentication(identity);
          try {
            start.await();
            if (tipo.equals("obra")) {
              var in = obraInput();
              in.codigo = code;
              estrutura.salvarObra(null, in);
            } else {
              var in = centroInput();
              in.codigo = code;
              estrutura.salvarCentro(null, in);
            }
            return true;
          } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return false;
          } finally {
            SecurityContextHolder.clearContext();
          }
        };
    var a = pool.submit(task);
    var b = pool.submit(task);
    start.countDown();
    try {
      assertNotEquals(a.get(30, TimeUnit.SECONDS), b.get(30, TimeUnit.SECONDS));
    } finally {
      pool.shutdownNow();
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"obras", "centros-custo"})
  void massAssignmentCadastros(String path) throws Exception {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    mvc.perform(
            post("/" + path)
                .with(authentication(auth))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"criadoPor\":999}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void contextoForjadoEmAlocacaoRejeitadoHTTP() throws Exception {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    mvc.perform(
            post("/pedidos-compra")
                .with(authentication(auth))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"itens\":[{\"alocacoes\":[{\"necessidadeId\":1,\"quantidade\":1,\"contexto\":{\"obraId\":1}}]}]}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void leituraResumoConstanteSemNMaisUm() {
    var sf = em.getEntityManagerFactory().unwrap(org.hibernate.SessionFactory.class);
    sf.getStatistics().clear();
    resumos.obra(obra.getId());
    long before = sf.getStatistics().getPrepareStatementCount();
    for (int i = 0; i < 12; i++) estrutura.salvarOrdem(null, ordemInput());
    sf.getStatistics().clear();
    resumos.obra(obra.getId());
    assertTrue(sf.getStatistics().getPrepareStatementCount() <= before + 1);
  }

  @Test
  void listagemSemNMaisUm() {
    for (int i = 0; i < 12; i++) estrutura.salvarOrdem(null, ordemInput());
    var sf = em.getEntityManagerFactory().unwrap(org.hibernate.SessionFactory.class);
    sf.getStatistics().clear();
    var page = estrutura.listarOrdens(null, null, obra.getId(), null, 0, 20);
    assertEquals(13, page.getTotalElements());
    assertTrue(sf.getStatistics().getPrepareStatementCount() <= 3);
  }

  @Test
  void suspensaoExigeMotivo() {
    estrutura.statusOrdem(os.getId(), transition("EM_ANDAMENTO"));
    var t = transition("SUSPENSA");
    t.motivo = " ";
    assertThrows(IllegalArgumentException.class, () -> estrutura.statusOrdem(os.getId(), t));
    assertEquals(
        StatusOrdemServico.EM_ANDAMENTO, ordens.findById(os.getId()).orElseThrow().getStatus());
  }

  @Test
  void contextoManualSemQuantidadeEstoqueRejeitado() {
    var n = need(obra, os, centro, 2);
    var p = pedido(2, List.of(n));
    p.itens.get(0).contexto = new ObrasInput.Contexto();
    p.itens.get(0).contexto.obraId = obra.getId();
    assertThrows(IllegalArgumentException.class, () -> compras.criar(p));
  }

  @Test
  void transicaoConcorrenteNaoDuplicaEvento() throws Exception {
    var pool = Executors.newFixedThreadPool(2);
    var start = new CountDownLatch(1);
    var auth = SecurityContextHolder.getContext().getAuthentication();
    Callable<Boolean> task =
        () -> {
          SecurityContextHolder.getContext().setAuthentication(auth);
          try {
            start.await();
            estrutura.statusOrdem(os.getId(), transition("EM_ANDAMENTO"));
            return true;
          } catch (ConflitoException e) {
            return false;
          } finally {
            SecurityContextHolder.clearContext();
          }
        };
    var a = pool.submit(task);
    var b = pool.submit(task);
    start.countDown();
    try {
      assertNotEquals(a.get(30, TimeUnit.SECONDS), b.get(30, TimeUnit.SECONDS));
      long total =
          eventos.findAll().stream()
              .filter(
                  e ->
                      e.getEvento().equals("ORDEM_SERVICO_INICIADA")
                          && e.getReferencia().equals(os.getId().toString()))
              .count();
      assertEquals(1, total);
    } finally {
      pool.shutdownNow();
    }
  }

  @Test
  void recebimentoParcialNaoMostraContextoManualAindaNaoRecebido() {
    var n = need(obra, os, centro, 2);
    var outra = estrutura.salvarObra(null, obraInput());
    var input = pedido(5, List.of(n));
    input.itens.get(0).paraEstoque = true;
    input.itens.get(0).contexto = new ObrasInput.Contexto();
    input.itens.get(0).contexto.obraId = outra.getId();
    int id = approve(input);
    receive(id, 2);
    var primeiro = compras.historico(id, 0, 20).getContent().get(0);
    var item = ((List<Map<String, Object>>) primeiro.get("itens")).get(0);
    assertEquals(0d, item.get("quantidadeEstoque"));
    assertNull(item.get("contexto"));
    var destino = ((List<Map<String, Object>>) item.get("destinacoes")).get(0);
    assertEquals(obra.getId(), ((ContextoOperacional) destino.get("contexto")).getObraId());
    receive(id, 3);
    var segundo = compras.historico(id, 0, 20).getContent().get(0);
    item = ((List<Map<String, Object>>) segundo.get("itens")).get(0);
    assertEquals(3d, item.get("quantidadeEstoque"));
    assertEquals(outra.getId(), ((ContextoOperacional) item.get("contexto")).getObraId());
    assertTrue(((List<?>) item.get("destinacoes")).isEmpty());
    assertTrue(((List<?>) resumos.obra(outra.getId()).get("materiaisConsumidos")).isEmpty());
  }

  @Test
  void comprasManuaisConcorrentesComContextosEmOrdemInversa() throws Exception {
    var outra = estrutura.salvarObra(null, obraInput());
    var produto2 = new Produto();
    produto2.setNome("Segundo material H2");
    produto2.setCodigo(UUID.randomUUID().toString());
    produto2.setUnidadeMedida("UN");
    produto2 = produtos.save(produto2);
    var a = pedido(1, List.of());
    var b = pedido(1, List.of());
    for (var input : List.of(a, b)) {
      var segundo = new ComprasInput.Item();
      segundo.produtoId = produto2.getId();
      segundo.quantidade = 1d;
      segundo.valorUnitario = BigDecimal.ONE;
      segundo.paraEstoque = true;
      input.itens = List.of(input.itens.get(0), segundo);
      for (int index = 0; index < 2; index++) {
        var ctx = new ObrasInput.Contexto();
        ctx.obraId = (input == a ? index == 0 : index != 0) ? obra.getId() : outra.getId();
        input.itens.get(index).contexto = ctx;
      }
    }
    var pool = Executors.newFixedThreadPool(2);
    var start = new CountDownLatch(1);
    var auth = SecurityContextHolder.getContext().getAuthentication();
    java.util.function.Function<ComprasInput.Pedido, Callable<Map<String, Object>>> task =
        input ->
            () -> {
              SecurityContextHolder.getContext().setAuthentication(auth);
              try {
                start.await();
                return compras.criar(input);
              } finally {
                SecurityContextHolder.clearContext();
              }
            };
    var first = pool.submit(task.apply(a));
    var second = pool.submit(task.apply(b));
    start.countDown();
    try {
      var pa = first.get(30, TimeUnit.SECONDS);
      var pb = second.get(30, TimeUnit.SECONDS);
      assertNotEquals(pa.get("id"), pb.get("id"));
      assertEquals(2, ((List<?>) pa.get("itens")).size());
      assertEquals(2, ((List<?>) pb.get("itens")).size());
      assertEquals(
          obra.getId(),
          ((ContextoOperacional)
                  ((List<Map<String, Object>>) pa.get("itens")).get(0).get("contexto"))
              .getObraId());
      assertEquals(
          outra.getId(),
          ((ContextoOperacional)
                  ((List<Map<String, Object>>) pb.get("itens")).get(0).get("contexto"))
              .getObraId());
    } finally {
      pool.shutdownNow();
    }
  }

  @Test
  void dominiosTextuaisAlinhadosAosScriptsMantemConstraints() {
    for (var pair :
        List.of(
            new String[] {"BES_OBRA", "STATUS"},
            new String[] {"BES_CENTRO_CUSTO", "TIPO"},
            new String[] {"BES_ORDEM_SERVICO", "STATUS"})) {
      var tipo =
          em.createNativeQuery(
                  "select data_type from information_schema.columns where table_name=:t and"
                      + " column_name=:c")
              .setParameter("t", pair[0])
              .setParameter("c", pair[1])
              .getSingleResult();
      assertEquals("CHARACTER VARYING", tipo);
    }
    assertThrows(
        jakarta.persistence.PersistenceException.class,
        () ->
            tx.executeWithoutResult(
                t -> {
                  em.createNativeQuery("update bes_obra set status='RASCUNHO' where id=:id")
                      .setParameter("id", obra.getId())
                      .executeUpdate();
                }));
    assertEquals(StatusObra.PLANEJADA, obras.findById(obra.getId()).orElseThrow().getStatus());
  }

  @Test
  void encerramentoConcorrenteComNovaSolicitacaoNaoCriaDemandaEmOsConcluida() throws Exception {
    estrutura.statusOrdem(os.getId(), transition("EM_ANDAMENTO"));
    var auth = SecurityContextHolder.getContext().getAuthentication();
    var start = new CountDownLatch(1);
    var pool = Executors.newFixedThreadPool(2);
    Callable<Boolean> close =
        () -> {
          SecurityContextHolder.getContext().setAuthentication(auth);
          try {
            start.await();
            estrutura.statusOrdem(os.getId(), transition("CONCLUIDA"));
            return true;
          } catch (ConflitoException e) {
            return false;
          } finally {
            SecurityContextHolder.clearContext();
          }
        };
    Callable<Boolean> create =
        () -> {
          SecurityContextHolder.getContext().setAuthentication(auth);
          try {
            start.await();
            solicitacoes.cadastrar(
                pessoa.getId(), local.getId(), obra.getId(), os.getId(), centro.getId());
            return true;
          } catch (ConflitoException e) {
            return false;
          } finally {
            SecurityContextHolder.clearContext();
          }
        };
    var closing = pool.submit(close);
    var creating = pool.submit(create);
    start.countDown();
    try {
      boolean closed = closing.get(30, TimeUnit.SECONDS);
      boolean created = creating.get(30, TimeUnit.SECONDS);
      assertNotEquals(closed, created);
      assertEquals(
          closed ? StatusOrdemServico.CONCLUIDA : StatusOrdemServico.EM_ANDAMENTO,
          ordens.findById(os.getId()).orElseThrow().getStatus());
      assertEquals(created ? 1L : 0L, resumos.ordem(os.getId()).get("quantidadeSolicitacoes"));
    } finally {
      pool.shutdownNow();
    }
  }

  @Test
  void recebimentosMultiobraParciaisPersistemDestinacaoSemConsumo() {
    var outra = estrutura.salvarObra(null, obraInput());
    var ci = centroInput();
    ci.obraId = outra.getId();
    ci.tipo = TipoCentroCusto.OBRA;
    var cc = estrutura.salvarCentro(null, ci);
    var oi = ordemInput();
    oi.obraId = outra.getId();
    oi.centroCustoId = cc.getId();
    var segunda = estrutura.salvarOrdem(null, oi);
    var a = need(obra, os, centro, 60);
    var b = need(outra, segunda, cc, 40);
    int id = approve(pedido(100, List.of(b, a)));
    double acumulado = 0;
    for (double quantidade : List.of(30d, 20d, 50d)) {
      receive(id, quantidade);
      acumulado += quantidade;
      assertEquals(
          Math.min(60d, acumulado), necessidades.buscar(a.id()).compra().get("quantidadeRecebida"));
      assertEquals(
          Math.max(0d, acumulado - 60d),
          necessidades.buscar(b.id()).compra().get("quantidadeRecebida"));
    }
    var historico = compras.historico(id, 0, 20).getContent();
    assertEquals(3, historico.size());
    var destinos = new HashMap<Integer, Double>();
    for (var recibo : historico) {
      var item = ((List<Map<String, Object>>) recibo.get("itens")).get(0);
      assertNull(item.get("contexto"));
      assertEquals(0d, item.get("quantidadeEstoque"));
      for (var destino : (List<Map<String, Object>>) item.get("destinacoes")) {
        var contexto = (ContextoOperacional) destino.get("contexto");
        destinos.merge(
            contexto.getObraId(), ((Number) destino.get("quantidade")).doubleValue(), Double::sum);
      }
    }
    assertEquals(60d, destinos.get(obra.getId()));
    assertEquals(40d, destinos.get(outra.getId()));
    var first = ((List<Map<String, Object>>) historico.get(2).get("itens")).get(0);
    var initial = ((List<Map<String, Object>>) first.get("destinacoes")).get(0);
    assertEquals(obra.getId(), ((ContextoOperacional) initial.get("contexto")).getObraId());
    assertTrue(((List<?>) resumos.obra(obra.getId()).get("materiaisConsumidos")).isEmpty());
    assertTrue(((List<?>) resumos.obra(outra.getId()).get("materiaisConsumidos")).isEmpty());
    assertEquals(
        100d,
        estoques
            .findByProdutoIdAndAlmoxarifadoId(produto.getId(), local.getId())
            .orElseThrow()
            .getQuantidade());
  }

  @Test
  void entregasParciaisMantemSnapshotEmTodasAsSaidasSemDuplaContagem() {
    var n = need(obra, os, centro, 8);
    int id = approve(pedido(8, List.of(n)));
    receive(id, 8);
    atendimento.iniciarSeparacao(n.solicitacaoId(), pessoa.getId());
    for (double quantidade : List.of(3d, 5d)) {
      atendimento.atender(
          n.solicitacaoId(),
          new AtendimentoInput(
              pessoa.getId(),
              List.of(new AtendimentoInput.Item(n.itemSolicitacaoId(), quantidade))),
          UUID.randomUUID().toString());
    }
    var saidas = movimentos.findBySolicitacaoId(n.solicitacaoId());
    assertEquals(2, saidas.size());
    for (var saida : saidas) {
      assertEquals(obra.getId(), saida.getContexto().getObraId());
      assertEquals(os.getId(), saida.getContexto().getOrdemServicoId());
      assertEquals(centro.getId(), saida.getContexto().getCentroCustoId());
      assertEquals(obra.getCodigo(), saida.getContexto().getObraCodigo());
    }
    for (var resumo :
        List.of(
            resumos.obra(obra.getId()),
            resumos.ordem(os.getId()),
            resumos.centro(centro.getId()))) {
      assertEquals(1L, resumo.get("quantidadeSolicitacoes"));
      assertEquals(1L, resumo.get("quantidadeNecessidades"));
      assertEquals(1L, resumo.get("quantidadePedidos"));
      var consumidos = (List<Object[]>) resumo.get("materiaisConsumidos");
      assertEquals(1, consumidos.size());
      assertEquals(8d, ((Number) consumidos.get(0)[3]).doubleValue());
      assertNull(resumo.get("custoConsumido"));
    }
  }

  @Test
  void falhaAuditoriaReverteSolicitacaoEContextoSemEventoDeSucesso() {
    long before = ((Number) resumos.ordem(os.getId()).get("quantidadeSolicitacoes")).longValue();
    long eventosAntes = eventos.count();
    tx.executeWithoutResult(
        t ->
            doThrow(new IllegalStateException("falha H2"))
                .when(audit)
                .registrar(
                    eq("SOLICITACAO_CADASTRAR"), anyString(), anyString(), any(), any(), any()));
    assertThrows(
        IllegalStateException.class,
        () ->
            solicitacoes.cadastrar(
                pessoa.getId(), local.getId(), obra.getId(), os.getId(), centro.getId()));
    assertEquals(before, resumos.ordem(os.getId()).get("quantidadeSolicitacoes"));
    assertEquals(eventosAntes, eventos.count());
  }

  @Test
  void listagemContextualNecessidadesNaoCresceUmaConsultaPorDemanda() {
    need(obra, os, centro, 1);
    var statistics =
        em.getEntityManagerFactory().unwrap(org.hibernate.SessionFactory.class).getStatistics();
    statistics.clear();
    assertEquals(
        1,
        necessidades
            .listar(null, null, null, null, obra.getId(), os.getId(), centro.getId())
            .size());
    long inicial = statistics.getPrepareStatementCount();
    for (int i = 0; i < 11; i++) need(obra, os, centro, 1);
    statistics.clear();
    assertEquals(
        12,
        necessidades
            .listar(null, null, null, null, obra.getId(), os.getId(), centro.getId())
            .size());
    long finalCount = statistics.getPrepareStatementCount();
    assertTrue(
        finalCount <= inicial + 1,
        "Consultas: uma demanda=" + inicial + ", doze demandas=" + finalCount);
  }
}
