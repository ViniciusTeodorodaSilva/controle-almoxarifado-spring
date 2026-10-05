package br.com.almoxarifado.compras;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:bes-compras;MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=15000",
      "logging.level.root=WARN",
      "debug=false",
      "spring.jpa.properties.hibernate.generate_statistics=true"
    })
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ComprasTests {
  @Autowired FornecedorService suppliers;
  @Autowired PedidoCompraService service;
  @Autowired FornecedorRepository suppliersRepo;
  @Autowired PedidoCompraRepository orders;
  @Autowired RecebimentoCompraRepository receipts;
  @MockitoSpyBean AlocacaoCompraRepository allocations;
  @Autowired NecessidadeCompraRepository needs;
  @Autowired NecessidadeCompraService needsService;
  @Autowired ProdutoRepository products;
  @Autowired AlmoxarifadoRepository warehouses;
  @Autowired FuncionarioRepository people;
  @Autowired EstoqueRepository stocks;
  @Autowired SolicitacaoService demands;
  @Autowired AtendimentoSolicitacaoService fulfillment;
  @Autowired EstoqueService stockService;
  @Autowired UsuarioRepository users;
  @Autowired TransactionTemplate tx;
  @Autowired MockMvc mvc;
  @MockitoSpyBean MovimentacaoRepository movements;
  @MockitoSpyBean AuditoriaService audit;
  Produto product;
  Almoxarifado warehouse;
  Funcionario person;
  Fornecedor supplier;
  Usuario actor;
  long beforeMoves, beforeReceipts;
  org.springframework.security.core.Authentication identity;

  @BeforeEach
  void prepare() {
    actor =
        users.save(
            new Usuario(
                "fixture-" + UUID.randomUUID(),
                "fixture-hash-nao-utilizado",
                "Ator H2",
                Perfil.ADMIN));
    identity = auth(Perfil.ADMIN);
    SecurityContextHolder.getContext().setAuthentication(identity);
    product = new Produto();
    product.setNome("Material H2");
    product.setCodigo("H2-" + UUID.randomUUID());
    product.setUnidadeMedida("UN");
    product = products.save(product);
    warehouse = new Almoxarifado();
    warehouse.setNome("Destino H2");
    warehouse = warehouses.save(warehouse);
    person = new Funcionario();
    person.setNome("Responsável físico H2");
    person.setMatricula(UUID.randomUUID().toString());
    person = people.save(person);
    var input = new ComprasInput.Fornecedor();
    input.setNome("Fornecedor fictício H2");
    input.setTipoPessoa(TipoPessoa.PJ);
    int id = (Integer) suppliers.criar(input).get("id");
    supplier = suppliersRepo.findById(id).orElseThrow();
    beforeMoves = movements.count();
    beforeReceipts = receipts.count();
  }

  org.springframework.security.core.Authentication auth(Perfil profile) {
    return new UsernamePasswordAuthenticationToken(
        new Identidade(
            actor.getId(), actor.getUsername(), actor.getAuthVersion(), System.currentTimeMillis()),
        null,
        profile.permissoes().stream().map(p -> new SimpleGrantedAuthority(p.name())).toList());
  }

  @AfterEach
  void cleanup() {
    reset(movements, audit, allocations);
    SecurityContextHolder.clearContext();
  }

  ComprasInput.Pedido input(double q) {
    var in = new ComprasInput.Pedido();
    in.fornecedorId = supplier.getId();
    in.almoxarifadoId = warehouse.getId();
    var i = new ComprasInput.Item();
    i.produtoId = product.getId();
    i.setQuantidade(q);
    i.setValorUnitario(new BigDecimal("12.3456"));
    i.paraEstoque = true;
    i.setAlocacoes(new ArrayList<>());
    in.setItens(List.of(i));
    return in;
  }

  int create(double q) {
    return (Integer) service.criar(input(q)).get("id");
  }

  int approved(double q) {
    int id = create(q);
    service.submeter(id);
    service.aprovar(id);
    return id;
  }

  ComprasInput.Recebimento receiving(int id, double q) {
    var in = new ComprasInput.Recebimento();
    in.almoxarifadoId = warehouse.getId();
    in.responsavelId = person.getId();
    var i = new ComprasInput.ItemRecebido();
    i.itemPedidoId =
        tx.execute(
            s ->
                orders.findById(id).orElseThrow().getItens().stream()
                    .filter(x -> x.getAtivo())
                    .findFirst()
                    .orElseThrow()
                    .getId());
    i.setQuantidade(q);
    in.setItens(List.of(i));
    return in;
  }

  Map<String, Object> receive(int id, double q) {
    return service.receber(id, receiving(id, q), UUID.randomUUID().toString());
  }

  double balance() {
    return stocks
        .findByProdutoIdAndAlmoxarifadoId(product.getId(), warehouse.getId())
        .map(Estoque::getQuantidade)
        .orElse(0d);
  }

  int need(double q) {
    var s = demands.cadastrar(person.getId(), warehouse.getId());
    var i = demands.adicionarItem(s.getId(), product.getId(), q);
    demands.aprovar(s.getId(), person.getId());
    return needsService
        .criar(new NecessidadeCompraInput(i.getId(), person.getId()), UUID.randomUUID().toString())
        .id();
  }

  ComprasInput.Pedido linked(int need, double q) {
    var in = input(q);
    in.getItens().get(0).paraEstoque = false;
    var a = new ComprasInput.Alocacao();
    a.necessidadeId = need;
    a.setQuantidade(q);
    in.getItens().get(0).setAlocacoes(List.of(a));
    return in;
  }

  @Test
  void fornecedorSemDocumentoECadastroEditavel() {
    assertNull(supplier.getDocumento());
    var i = new ComprasInput.Fornecedor();
    i.setNome("Alterado");
    i.setTipoPessoa(TipoPessoa.PF);
    i.setAtivo(false);
    i.setEmail("h2@example.invalid");
    suppliers.atualizar(supplier.getId(), i);
    assertFalse(suppliersRepo.findById(supplier.getId()).orElseThrow().getAtivo());
    assertThrows(ConflitoException.class, () -> create(2));
  }

  @Test
  void documentoValidadoNormalizadoEUnico() {
    var i = new ComprasInput.Fornecedor();
    i.setNome("Pessoa fictícia");
    i.setTipoPessoa(TipoPessoa.PF);
    i.setDocumento("123.456.789-09");
    int id = (Integer) suppliers.criar(i).get("id");
    assertEquals("12345678909", suppliersRepo.findById(id).orElseThrow().getDocumento());
    assertThrows(ConflitoException.class, () -> suppliers.criar(i));
    i.setDocumento("12345678900");
    assertThrows(IllegalArgumentException.class, () -> suppliers.criar(i));
  }

  @Test
  void documentosNumericosEAlfanumericos() {
    assertEquals(
        "11222333000181", DocumentoFornecedor.normalizar(TipoPessoa.PJ, "11.222.333/0001-81"));
    String base = "12ABC34501DE";
    String d = base;
    for (int x = 0; x < 2; x++) {
      int sum = 0, w = 2;
      for (int k = d.length() - 1; k >= 0; k--) {
        sum += (d.charAt(k) - 48) * w;
        if (++w == 10) w = 2;
      }
      int rem = sum % 11;
      d += rem < 2 ? "0" : Integer.toString(11 - rem);
    }
    assertEquals(d, DocumentoFornecedor.normalizar(TipoPessoa.PJ, d.toLowerCase()));
    assertThrows(
        IllegalArgumentException.class,
        () -> DocumentoFornecedor.normalizar(TipoPessoa.PF, "00000000000"));
  }

  @Test
  void listagemNaoExpoeContatoOuDocumentoIntegral() {
    var i = new ComprasInput.Fornecedor();
    i.setNome("Pessoa fictícia");
    i.setTipoPessoa(TipoPessoa.PF);
    i.setDocumento("11144477735");
    i.setTelefone("fixture");
    int id = (Integer) suppliers.criar(i).get("id");
    var row = suppliers.listar(null, "11144477735", null, 0, 20).getContent().get(0);
    assertEquals("***7735", row.get("documento"));
    assertNull(row.get("telefone"));
    SecurityContextHolder.getContext().setAuthentication(auth(Perfil.CONSULTA));
    assertEquals("***7735", suppliers.buscar(id).get("documento"));
  }

  @Test
  void rascunhoTemNumeroAtoresPrecosENaoMexeEstoque() {
    int id = create(3);
    var p = service.buscar(id);
    assertTrue(p.get("numero").toString().matches("PC-\\d{4}-\\d{6,}"));
    assertEquals(actor.getId(), p.get("criadoPor"));
    assertEquals("37.04", p.get("total"));
    assertEquals(0, balance());
    assertEquals(beforeMoves, movements.count());
  }

  @Test
  void naoAprovaSemSubmeter() {
    int id = create(1);
    assertThrows(ConflitoException.class, () -> service.aprovar(id));
    assertEquals("RASCUNHO", service.buscar(id).get("status").toString());
  }

  @Test
  void aprovacaoNaoCriaEntrada() {
    int id = approved(5);
    assertEquals(0, balance());
    assertEquals(beforeMoves, movements.count());
    assertEquals(actor.getId(), service.buscar(id).get("aprovadoPor"));
    service.aprovar(id);
    assertEquals(beforeMoves, movements.count());
  }

  @Test
  void rascunhoEditavelComHistoricoDeItens() {
    int id = create(1);
    service.atualizar(id, input(2));
    tx.execute(
        s -> {
          var p = orders.findById(id).orElseThrow();
          assertEquals(2, p.getItens().size());
          assertEquals(1, p.getItens().stream().filter(i -> i.getAtivo()).count());
          return null;
        });
    service.submeter(id);
    assertThrows(ConflitoException.class, () -> service.atualizar(id, input(3)));
  }

  @Test
  void produtoInativoBloqueiaPedido() {
    product.setAtivo(false);
    products.save(product);
    assertThrows(ConflitoException.class, () -> create(2));
  }

  @Test
  void estoqueSemOrigemExigeConfirmacaoExplicita() {
    var in = input(2);
    in.getItens().get(0).paraEstoque = false;
    assertThrows(IllegalArgumentException.class, () -> service.criar(in));
  }

  @Test
  void quantidadesEValoresInvalidos() {
    for (double q : new double[] {0, -1, Double.NaN, Double.POSITIVE_INFINITY})
      assertThrows(IllegalArgumentException.class, () -> create(q));
    var in = input(2);
    in.getItens().get(0).setValorUnitario(new BigDecimal("-1"));
    assertThrows(IllegalArgumentException.class, () -> service.criar(in));
    in.getItens().get(0).setValorUnitario(new BigDecimal("0.00001"));
    assertThrows(IllegalArgumentException.class, () -> service.criar(in));
    in.getItens().get(0).setValorUnitario(null);
    assertThrows(IllegalArgumentException.class, () -> service.criar(in));
  }

  @Test
  void necessidadePodeSerDivididaEmPedidosSemSobreCompra() {
    int n = need(8);
    int a = (Integer) service.criar(linked(n, 3)).get("id");
    int b = (Integer) service.criar(linked(n, 5)).get("id");
    assertNotEquals(a, b);
    assertEquals(StatusNecessidadeCompra.EM_COMPRA, needs.findById(n).orElseThrow().getStatus());
    assertThrows(ConflitoException.class, () -> service.criar(linked(n, 1)));
    assertEquals(0, balance());
  }

  @Test
  void cancelamentoLiberaNecessidadeSemApagarVinculo() {
    int n = need(4);
    int id = (Integer) service.criar(linked(n, 4)).get("id");
    var c = new ComprasInput.Cancelamento();
    c.motivo = "Compra dispensada";
    service.cancelar(id, c);
    assertEquals(StatusNecessidadeCompra.ABERTA, needs.findById(n).orElseThrow().getStatus());
    tx.execute(
        s -> {
          assertEquals(
              1, orders.findById(id).orElseThrow().getItens().get(0).getAlocacoes().size());
          return null;
        });
    assertNotNull(service.criar(linked(n, 4)));
  }

  @Test
  void necessidadeIncompativelBloqueada() {
    int n = need(4);
    var other = new Almoxarifado();
    other.setNome("Outro");
    other = warehouses.save(other);
    var in = linked(n, 4);
    in.almoxarifadoId = other.getId();
    assertThrows(ConflitoException.class, () -> service.criar(in));
  }

  @Test
  void cancelamentoNecessidadeVinculadaBloqueado() {
    int n = need(4);
    service.criar(linked(n, 4));
    var c = new ComprasInput.Cancelamento();
    c.motivo = "H2";
    assertThrows(ConflitoException.class, () -> service.cancelarNecessidade(n, c));
  }

  @Test
  void recebimentoParcialEMultiplasEntregas() {
    int id = approved(5);
    receive(id, 2);
    assertEquals(2, balance());
    assertEquals("PARCIALMENTE_RECEBIDO", service.buscar(id).get("status").toString());
    receive(id, 3);
    assertEquals(5, balance());
    assertEquals("RECEBIDO", service.buscar(id).get("status").toString());
    assertEquals(beforeMoves + 2, movements.count());
    assertEquals(2, service.historico(id, 0, 20).getTotalElements());
  }

  @Test
  void recebimentoAtualizaSomenteNecessidadeDestinadaENaoAtendeSolicitacao() {
    int n = need(6);
    int id = (Integer) service.criar(linked(n, 6)).get("id");
    service.submeter(id);
    service.aprovar(id);
    receive(id, 2);
    assertEquals(2, needs.findById(n).orElseThrow().getQuantidadeRecebida());
    assertEquals(StatusNecessidadeCompra.EM_COMPRA, needs.findById(n).orElseThrow().getStatus());
    receive(id, 4);
    var need = needs.findById(n).orElseThrow();
    assertEquals(StatusNecessidadeCompra.ATENDIDA, need.getStatus());
    assertEquals("APROVADA", fulfillment.operacao(need.getSolicitacao().getId()).status());
    assertEquals(
        0, fulfillment.operacao(need.getSolicitacao().getId()).itens().get(0).quantidadeAtendida());
  }

  @Test
  void idempotenciaMesmaChaveRetornaOriginalAposConclusao() {
    int id = approved(2);
    var in = receiving(id, 2);
    String key = UUID.randomUUID().toString();
    var a = service.receber(id, in, key);
    var b = service.receber(id, in, key);
    assertEquals(a.get("id"), b.get("id"));
    assertEquals(2, balance());
    assertEquals(beforeReceipts + 1, receipts.count());
    assertEquals(beforeMoves + 1, movements.count());
  }

  @Test
  void idempotenciaChaveReutilizadaComPayloadDiferente() {
    int id = approved(3);
    String key = UUID.randomUUID().toString();
    service.receber(id, receiving(id, 1), key);
    assertThrows(ConflitoException.class, () -> service.receber(id, receiving(id, 2), key));
    assertEquals(1, balance());
  }

  @Test
  void naoRecebeAcimaDoPendente() {
    int id = approved(3);
    receive(id, 2);
    assertThrows(ConflitoException.class, () -> receive(id, 2));
    assertEquals(2, balance());
    assertEquals(beforeReceipts + 1, receipts.count());
  }

  @Test
  void naoRecebeAntesAprovacaoOuAposCancelamento() {
    int id = create(2);
    assertThrows(ConflitoException.class, () -> receive(id, 1));
    var c = new ComprasInput.Cancelamento();
    c.motivo = "H2";
    service.cancelar(id, c);
    assertThrows(ConflitoException.class, () -> receive(id, 1));
    assertEquals(0, balance());
  }

  @Test
  void naoCancelaAposRecebimento() {
    int id = approved(3);
    receive(id, 1);
    var c = new ComprasInput.Cancelamento();
    c.motivo = "H2";
    assertThrows(ConflitoException.class, () -> service.cancelar(id, c));
    assertEquals(1, balance());
  }

  @Test
  void destinoDiferenteBloqueado() {
    int id = approved(3);
    var in = receiving(id, 1);
    in.almoxarifadoId = -1;
    assertThrows(
        ConflitoException.class, () -> service.receber(id, in, UUID.randomUUID().toString()));
    assertEquals(0, balance());
  }

  @Test
  void movimentoRastreiaPedidoRecebimentoAtorEResponsavel() {
    int id = approved(3);
    var r = receive(id, 2);
    tx.execute(
        s -> {
          var m =
              movements.findAll().stream()
                  .filter(x -> Objects.equals(x.getPedidoCompraId(), id))
                  .findFirst()
                  .orElseThrow();
          assertEquals(r.get("id"), m.getRecebimentoCompraId());
          assertEquals(actor.getId(), m.getAtorCompraId());
          assertEquals(person.getId(), m.getResponsavel().getId());
          assertNull(m.getSolicitacaoId());
          assertEquals(0, m.getSaldoAnterior());
          assertEquals(2, m.getSaldoPosterior());
          return null;
        });
  }

  @Test
  void rollbackSeMovimentoFalha() {
    int id = approved(3);
    doThrow(new IllegalStateException("falha H2")).when(movements).save(any(Movimentacao.class));
    assertThrows(IllegalStateException.class, () -> receive(id, 2));
    assertEquals(0, balance());
    assertEquals(beforeReceipts, receipts.count());
    assertEquals("APROVADO", service.buscar(id).get("status").toString());
  }

  @Test
  void rollbackSeAuditoriaFalha() {
    int n = need(3);
    int id = (Integer) service.criar(linked(n, 3)).get("id");
    service.submeter(id);
    service.aprovar(id);
    tx.execute(
        s -> {
          doThrow(new IllegalStateException("falha audit H2"))
              .when(audit)
              .registrar(
                  eq("RECEBIMENTO_COMPRA_REGISTRADO"),
                  anyString(),
                  anyString(),
                  any(),
                  any(),
                  any());
          return null;
        });
    assertThrows(IllegalStateException.class, () -> receive(id, 2));
    assertEquals(0, balance());
    assertEquals(beforeReceipts, receipts.count());
    assertEquals(beforeMoves, movements.count());
    assertEquals(0, needs.findById(n).orElseThrow().getQuantidadeRecebida());
  }

  @ParameterizedTest
  @EnumSource(Perfil.class)
  void permissoesHttpEService(Perfil profile) throws Exception {
    var a = auth(profile);
    for (String path : List.of("/fornecedores", "/pedidos-compra", "/recebimentos-compra"))
      mvc.perform(get(path).with(authentication(a))).andExpect(status().isOk());
    mvc.perform(
            post("/fornecedores")
                .with(authentication(a))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(
            status().is(profile.permissoes().contains(Permissao.FORNECEDOR_GERENCIAR) ? 400 : 403));
    mvc.perform(
            post("/pedidos-compra")
                .with(authentication(a))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().is(profile.permissoes().contains(Permissao.COMPRA_CRIAR) ? 400 : 403));
    mvc.perform(put("/pedidos-compra/999999/aprovar").with(authentication(a)).with(csrf()))
        .andExpect(
            status().is(profile.permissoes().contains(Permissao.COMPRA_APROVAR) ? 404 : 403));
    mvc.perform(
            post("/pedidos-compra/999999/recebimentos")
                .with(authentication(a))
                .with(csrf())
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(
            status()
                .is(profile.permissoes().contains(Permissao.RECEBIMENTO_REGISTRAR) ? 400 : 403));
    SecurityContextHolder.getContext().setAuthentication(a);
    if (!profile.permissoes().contains(Permissao.COMPRA_CRIAR))
      assertThrows(AccessDeniedException.class, () -> create(1));
    if (!profile.permissoes().contains(Permissao.RECEBIMENTO_REGISTRAR))
      assertThrows(
          AccessDeniedException.class,
          () -> service.receber(1, new ComprasInput.Recebimento(), UUID.randomUUID().toString()));
  }

  @Test
  void massAssignmentPedidoFornecedorRecebimentoRejeitado() throws Exception {
    for (String field :
        List.of(
            "id",
            "status",
            "total",
            "subtotal",
            "ator",
            "auditUser",
            "criadoPor",
            "aprovadoPor",
            "quantidadeRecebida",
            "saldoPosterior"))
      for (String path :
          List.of("/fornecedores", "/pedidos-compra", "/pedidos-compra/999999/recebimentos"))
        mvc.perform(
                post(path)
                    .with(authentication(identity))
                    .with(csrf())
                    .header("Idempotency-Key", UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"" + field + "\":123}"))
            .andExpect(status().isBadRequest());
  }

  @Test
  void filtrosEPaginacaoLimitada() {
    int id = approved(3);
    receive(id, 1);
    assertEquals(
        1,
        service
            .listar(
                null,
                supplier.getId(),
                StatusPedidoCompra.PARCIALMENTE_RECEBIDO,
                product.getId(),
                null,
                null,
                null,
                0,
                20)
            .getTotalElements());
    assertEquals(
        1,
        service
            .listarRecebimentos(
                id, supplier.getId(), product.getId(), warehouse.getId(), null, null, 0, 20)
            .getTotalElements());
    assertThrows(
        IllegalArgumentException.class,
        () -> service.listar(null, null, null, null, null, null, null, 0, 101));
  }

  List<Boolean> concurrent(Callable<Boolean> first, Callable<Boolean> second) throws Exception {
    var start = new CountDownLatch(1);
    var pool = Executors.newFixedThreadPool(2);
    try {
      var futures = new ArrayList<Future<Boolean>>();
      for (var task : List.of(first, second))
        futures.add(
            pool.submit(
                () -> {
                  SecurityContextHolder.getContext().setAuthentication(identity);
                  start.await();
                  try {
                    return task.call();
                  } finally {
                    SecurityContextHolder.clearContext();
                  }
                }));
      start.countDown();
      var results = new ArrayList<Boolean>();
      for (var f : futures) results.add(f.get(30, TimeUnit.SECONDS));
      return results;
    } finally {
      pool.shutdownNow();
    }
  }

  @Test
  void concorrenciaRecebimentosNaoExcedePedido() throws Exception {
    int id = approved(3);
    var in = receiving(id, 2);
    Callable<Boolean> op =
        () -> {
          try {
            service.receber(id, in, UUID.randomUUID().toString());
            return true;
          } catch (ConflitoException e) {
            return false;
          }
        };
    var results = concurrent(op, op);
    assertEquals(1, results.stream().filter(x -> x).count());
    assertEquals(2, balance());
    assertEquals(beforeMoves + 1, movements.count());
  }

  @Test
  void concorrenciaMesmaChaveNaoDuplicaEntrada() throws Exception {
    int id = approved(3);
    var in = receiving(id, 3);
    String key = UUID.randomUUID().toString();
    Callable<Boolean> op =
        () -> {
          service.receber(id, in, key);
          return true;
        };
    assertEquals(List.of(true, true), concurrent(op, op));
    assertEquals(3, balance());
    assertEquals(beforeReceipts + 1, receipts.count());
    assertEquals(beforeMoves + 1, movements.count());
    tx.execute(
        st -> {
          assertEquals(
              3, orders.findById(id).orElseThrow().getItens().get(0).getQuantidadeRecebida());
          return null;
        });
  }

  @Test
  void concorrenciaCompraMesmaNecessidadeNaoSobreAloca() throws Exception {
    int n = need(4);
    Callable<Boolean> op =
        () -> {
          try {
            service.criar(linked(n, 3));
            return true;
          } catch (ConflitoException e) {
            return false;
          }
        };
    assertEquals(1, concurrent(op, op).stream().filter(x -> x).count());
    assertEquals(3d, needsService.buscar(n).compra().get("quantidadeVinculadaPendente"));
  }

  @Test
  void concorrenciaRecebimentoSaidaPreservaSaldo() throws Exception {
    int id = approved(3);
    var e = new Estoque();
    e.setProduto(product);
    e.setAlmoxarifado(warehouse);
    e.setQuantidade(5);
    stocks.save(e);
    var in = receiving(id, 3);
    concurrent(
        () -> {
          service.receber(id, in, UUID.randomUUID().toString());
          return true;
        },
        () -> {
          stockService.saidaEstoque(
              product.getId(), warehouse.getId(), 2, person.getId(), person.getId());
          return true;
        });
    assertEquals(6, balance());
    assertStockChain(5);
    assertEquals(beforeMoves + 2, movements.count());
  }

  @Autowired TransferenciaEstoqueService transferService;

  @Test
  void concorrenciaRecebimentoTransferenciaPreservaSaldos() throws Exception {
    int id = approved(3);
    var e = new Estoque();
    e.setProduto(product);
    e.setAlmoxarifado(warehouse);
    e.setQuantidade(5);
    stocks.save(e);
    var dest = new Almoxarifado();
    dest.setNome("Destino transferência H2");
    int destId = warehouses.save(dest).getId();
    var in = receiving(id, 3);
    concurrent(
        () -> {
          service.receber(id, in, UUID.randomUUID().toString());
          return true;
        },
        () -> {
          transferService.criar(
              new TransferenciaInput(
                  warehouse.getId(),
                  destId,
                  person.getId(),
                  "H2",
                  List.of(new TransferenciaInput.Item(product.getId(), 2d))));
          return true;
        });
    assertEquals(6, balance());
    assertStockChain(5);
    assertEquals(
        2,
        stocks
            .findByProdutoIdAndAlmoxarifadoId(product.getId(), destId)
            .orElseThrow()
            .getQuantidade());
    assertEquals(beforeMoves + 3, movements.count());
  }

  @Test
  void concorrenciaRecebimentoAtendimentoNaoPerdeAtualizacao() throws Exception {
    int n = need(3);
    int id = (Integer) service.criar(linked(n, 3)).get("id");
    service.submeter(id);
    service.aprovar(id);
    var necessidade = needs.findById(n).orElseThrow();
    var e = new Estoque();
    e.setProduto(product);
    e.setAlmoxarifado(warehouse);
    e.setQuantidade(5);
    stocks.save(e);
    int request = necessidade.getSolicitacao().getId(),
        item = necessidade.getItemSolicitacao().getId();
    fulfillment.iniciarSeparacao(request, person.getId());
    var in = receiving(id, 3);
    concurrent(
        () -> {
          service.receber(id, in, UUID.randomUUID().toString());
          return true;
        },
        () -> {
          fulfillment.atender(
              request,
              new AtendimentoInput(person.getId(), List.of(new AtendimentoInput.Item(item, 2d))),
              UUID.randomUUID().toString());
          return true;
        });
    assertEquals(6, balance());
    assertStockChain(5);
    assertEquals(2, fulfillment.operacao(request).itens().get(0).quantidadeAtendida());
    assertEquals(StatusNecessidadeCompra.ATENDIDA, needs.findById(n).orElseThrow().getStatus());
    assertEquals(beforeMoves + 2, movements.count());
  }

  @Test
  void pedidosDiferentesCriamUmUnicoEstoqueAusente() throws Exception {
    int a = approved(3), b = approved(4);
    var ai = receiving(a, 3);
    var bi = receiving(b, 4);
    assertEquals(
        List.of(true, true),
        concurrent(
            () -> {
              service.receber(a, ai, UUID.randomUUID().toString());
              return true;
            },
            () -> {
              service.receber(b, bi, UUID.randomUUID().toString());
              return true;
            }));
    assertEquals(7, balance());
    assertStockChain(0);
    assertEquals(
        1,
        stocks.findAll().stream()
            .filter(
                e ->
                    e.getProduto().getId().equals(product.getId())
                        && e.getAlmoxarifado().getId().equals(warehouse.getId()))
            .count());
  }

  @Test
  void fracionamentoDeUnidadeBloqueado() {
    var unit = new UnidadeMedida();
    unit.setNome("Unidade H2 " + UUID.randomUUID());
    unit.setSigla("X" + product.getId());
    unit.setPermiteFracionamento(false);
    product.setUnidadeMedidaConfigurada(unitRepository.save(unit));
    products.save(product);
    assertThrows(IllegalArgumentException.class, () -> create(1.5));
    int id = approved(3);
    assertThrows(IllegalArgumentException.class, () -> receive(id, 0.5));
    assertEquals(0, balance());
  }

  @Autowired UnidadeMedidaRepository unitRepository;

  @Test
  void itemRecebidoDeOutroPedidoEDuplicadoBloqueados() {
    int a = approved(2), b = approved(3);
    var in = receiving(b, 1);
    assertThrows(
        ConflitoException.class, () -> service.receber(a, in, UUID.randomUUID().toString()));
    var duplicate = receiving(a, 1);
    duplicate.itens = List.of(duplicate.itens.get(0), duplicate.itens.get(0));
    assertThrows(
        IllegalArgumentException.class,
        () -> service.receber(a, duplicate, UUID.randomUUID().toString()));
    assertEquals(0, balance());
  }

  @Test
  void duasNecessidadesDoMesmoProdutoConsolidadasComOrigemPreservada() {
    int a = need(2), b = need(3);
    var in = linked(a, 2);
    in.itens.get(0).quantidade = 5d;
    var allocation = new ComprasInput.Alocacao();
    allocation.necessidadeId = b;
    allocation.quantidade = 3d;
    in.itens.get(0).alocacoes = List.of(in.itens.get(0).alocacoes.get(0), allocation);
    int id = (Integer) service.criar(in).get("id");
    service.submeter(id);
    service.aprovar(id);
    receive(id, 4);
    assertEquals(2, needs.findById(a).orElseThrow().getQuantidadeRecebida());
    assertEquals(2, needs.findById(b).orElseThrow().getQuantidadeRecebida());
    assertEquals(StatusNecessidadeCompra.ATENDIDA, needs.findById(a).orElseThrow().getStatus());
    assertEquals(StatusNecessidadeCompra.EM_COMPRA, needs.findById(b).orElseThrow().getStatus());
  }

  @Test
  void edicaoLiberaVinculosAnteriores() {
    int n = need(4);
    int id = (Integer) service.criar(linked(n, 4)).get("id");
    service.atualizar(id, linked(n, 2));
    assertEquals(2d, needsService.buscar(n).compra().get("quantidadeVinculadaPendente"));
    assertEquals(2d, needsService.buscar(n).compra().get("quantidadeDisponivel"));
  }

  @Test
  void necessidadeLegadaNaoInventaRecebimento() {
    int n = need(4);
    tx.execute(
        s -> {
          var legacy = needs.findById(n).orElseThrow();
          var query =
              entityManager.createNativeQuery(
                  "update necessidade_compra set quantidade_recebida=null where id=:id");
          query.setParameter("id", n).executeUpdate();
          entityManager.clear();
          return null;
        });
    assertEquals("A_CONFERIR", needsService.buscar(n).compra().get("compatibilidadeLegada"));
    assertNull(needsService.buscar(n).compra().get("quantidadeRecebida"));
    assertThrows(ConflitoException.class, () -> service.criar(linked(n, 1)));
  }

  @Autowired jakarta.persistence.EntityManager entityManager;

  @Test
  void decimalTextualNaoPerdePrecisaoNoContratoHttp() throws Exception {
    mvc.perform(
            post("/pedidos-compra")
                .with(authentication(identity))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"fornecedorId\":"
                        + supplier.getId()
                        + ",\"almoxarifadoId\":"
                        + warehouse.getId()
                        + ",\"itens\":[{\"produtoId\":"
                        + product.getId()
                        + ",\"quantidade\":1,\"valorUnitario\":\"999999999999999.9999\",\"paraEstoque\":true}]}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.itens[0].valorUnitario").value("999999999999999.9999"))
        .andExpect(jsonPath("$.total").value("1000000000000000.00"));
  }

  @Test
  void recebimentosDePedidosDiferentesNaoUsamNecessidadeAntigaDoContexto() {
    int n = need(100);
    int a = (Integer) service.criar(linked(n, 60)).get("id");
    int b = (Integer) service.criar(linked(n, 40)).get("id");
    for (int id : List.of(a, b)) {
      service.submeter(id);
      service.aprovar(id);
    }
    tx.execute(
        s -> {
          assertEquals(0, needs.findById(n).orElseThrow().getQuantidadeRecebida());
          try {
            concurrent(
                () -> {
                  receive(a, 60);
                  return true;
                },
                () -> true);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
          receive(b, 40);
          return null;
        });
    assertEquals(100, needs.findById(n).orElseThrow().getQuantidadeRecebida());
    assertEquals(StatusNecessidadeCompra.ATENDIDA, needs.findById(n).orElseThrow().getStatus());
    assertEquals(100, balance());
  }

  @Autowired AuditoriaRepository auditRepo;

  @Test
  void necessidadeCemDivididaSessentaQuarentaSoAtendidaAposReceber() {
    int n = need(100);
    int a = (Integer) service.criar(linked(n, 60)).get("id");
    int b = (Integer) service.criar(linked(n, 40)).get("id");
    assertEquals(100d, needsService.buscar(n).compra().get("quantidadeVinculadaPendente"));
    assertEquals(0d, needsService.buscar(n).compra().get("quantidadeDisponivel"));
    assertThrows(ConflitoException.class, () -> service.criar(linked(n, 1)));
    for (int id : List.of(a, b)) {
      service.submeter(id);
      service.aprovar(id);
    }
    assertEquals(0, needs.findById(n).orElseThrow().getQuantidadeRecebida());
    receive(a, 30);
    assertEquals(30, needs.findById(n).orElseThrow().getQuantidadeRecebida());
    receive(a, 30);
    assertEquals(60, needs.findById(n).orElseThrow().getQuantidadeRecebida());
    assertEquals(StatusNecessidadeCompra.EM_COMPRA, needs.findById(n).orElseThrow().getStatus());
    receive(b, 40);
    assertEquals(100, needs.findById(n).orElseThrow().getQuantidadeRecebida());
    assertEquals(StatusNecessidadeCompra.ATENDIDA, needs.findById(n).orElseThrow().getStatus());
    var need = needs.findById(n).orElseThrow();
    int request = need.getSolicitacao().getId();
    assertEquals("APROVADA", fulfillment.operacao(request).status());
    assertEquals(100, fulfillment.operacao(request).itens().get(0).quantidadePendente());
    assertEquals(0, fulfillment.operacao(request).itens().get(0).quantidadeAtendida());
    assertEquals(beforeMoves + 3, movements.count());
    assertEquals(beforeReceipts + 3, receipts.count());
    fulfillment.iniciarSeparacao(request, person.getId());
    fulfillment.atender(
        request,
        new AtendimentoInput(
            person.getId(),
            List.of(new AtendimentoInput.Item(need.getItemSolicitacao().getId(), 100d))),
        UUID.randomUUID().toString());
    assertEquals("ATENDIDA", fulfillment.operacao(request).status());
    assertEquals(0, balance());
    assertEquals(beforeMoves + 4, movements.count());
  }

  @Test
  void cemRecebidosSessentaExcessoQuarentaUmEConcorrenciaQuarenta() throws Exception {
    int id = approved(100);
    receive(id, 60);
    assertThrows(ConflitoException.class, () -> receive(id, 41));
    var in = receiving(id, 40);
    Callable<Boolean> op =
        () -> {
          try {
            service.receber(id, in, UUID.randomUUID().toString());
            return true;
          } catch (ConflitoException e) {
            return false;
          }
        };
    assertEquals(1, concurrent(op, op).stream().filter(x -> x).count());
    assertEquals(100, balance());
    assertEquals(beforeReceipts + 2, receipts.count());
    assertEquals(beforeMoves + 2, movements.count());
    assertEquals("RECEBIDO", service.buscar(id).get("status").toString());
  }

  @ParameterizedTest
  @org.junit.jupiter.params.provider.ValueSource(booleans = {false, true})
  void rollbackMultiItemIncluiEstoqueNecessidadeMovimentosEAuditoria(boolean falhaAuditoria) {
    int n = need(3);
    var other = new Produto();
    other.setNome("Segundo material H2");
    other.setCodigo("H2-" + UUID.randomUUID());
    other.setUnidadeMedida("UN");
    var second = products.save(other);
    var in = linked(n, 3);
    var secondItem = input(2).itens.get(0);
    secondItem.produtoId = second.getId();
    in.itens = List.of(in.itens.get(0), secondItem);
    int id = (Integer) service.criar(in).get("id");
    service.submeter(id);
    service.aprovar(id);
    var delivery = receiving(id, 3);
    var ri = new ComprasInput.ItemRecebido();
    ri.itemPedidoId = tx.execute(st -> orders.findById(id).orElseThrow().getItens().get(1).getId());
    ri.quantidade = 2d;
    delivery.itens = List.of(delivery.itens.get(0), ri);
    long audits = auditRepo.count();
    long stockRows = stocks.count();
    long allocationsBefore = allocations.count();
    if (falhaAuditoria)
      tx.execute(
          st -> {
            doThrow(new IllegalStateException("falha audit multiitem H2"))
                .when(audit)
                .registrar(
                    eq("RECEBIMENTO_COMPRA_REGISTRADO"),
                    anyString(),
                    anyString(),
                    any(),
                    any(),
                    any());
            return null;
          });
    else
      doThrow(new IllegalStateException("falha segundo movimento H2"))
          .when(movements)
          .save(argThat(m -> m != null && m.getProduto().getId().equals(second.getId())));
    assertThrows(
        IllegalStateException.class,
        () -> service.receber(id, delivery, UUID.randomUUID().toString()));
    assertEquals(stockRows, stocks.count());
    assertEquals(0, balance());
    assertFalse(
        stocks.findByProdutoIdAndAlmoxarifadoId(second.getId(), warehouse.getId()).isPresent());
    assertEquals(beforeReceipts, receipts.count());
    assertEquals(beforeMoves, movements.count());
    assertEquals(audits, auditRepo.count());
    assertEquals(allocationsBefore, allocations.count());
    assertEquals(0, needs.findById(n).orElseThrow().getQuantidadeRecebida());
    assertEquals(StatusNecessidadeCompra.EM_COMPRA, needs.findById(n).orElseThrow().getStatus());
    tx.execute(
        st -> {
          for (var item : orders.findById(id).orElseThrow().getItens())
            assertEquals(0, item.getQuantidadeRecebida());
          return null;
        });
    assertEquals("APROVADO", service.buscar(id).get("status").toString());
  }

  @Test
  void dinheiroDecimalFracionadoEArredondamentoNoServidor() {
    var in = input(0.3);
    in.itens.get(0).valorUnitario = new BigDecimal("0.10");
    assertEquals("0.03", service.criar(in).get("total"));
    in = input(0.1);
    in.itens.get(0).valorUnitario = new BigDecimal("0.20");
    assertEquals("0.02", service.criar(in).get("total"));
    in = input(0.5);
    in.itens.get(0).valorUnitario = new BigDecimal("0.30");
    assertEquals("0.15", service.criar(in).get("total"));
    in = input(0.5);
    in.itens.get(0).valorUnitario = new BigDecimal("0.01");
    assertEquals("0.01", service.criar(in).get("total"));
  }

  @Test
  void massAssignmentAninhadoRejeitaCamposDeAutoridade() throws Exception {
    for (String field :
        List.of(
            "subtotal",
            "total",
            "quantidadeRecebida",
            "status",
            "criadoPor",
            "aprovadoPor",
            "ator",
            "auditUser")) {
      for (String items :
          List.of("[{\"" + field + "\":123}]", "[{\"alocacoes\":[{\"" + field + "\":123}]}]"))
        mvc.perform(
                post("/pedidos-compra")
                    .with(authentication(identity))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"itens\":" + items + "}"))
            .andExpect(status().isBadRequest());
      mvc.perform(
              post("/pedidos-compra/999999/recebimentos")
                  .with(authentication(identity))
                  .with(csrf())
                  .header("Idempotency-Key", UUID.randomUUID().toString())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"itens\":[{\"" + field + "\":123}]}"))
          .andExpect(status().isBadRequest());
    }
  }

  @ParameterizedTest
  @EnumSource(StatusPedidoCompra.class)
  void cancelamentoEmTodosOsEstadosPreservaHistorico(StatusPedidoCompra state) {
    int id = create(3);
    if (state != StatusPedidoCompra.RASCUNHO) {
      service.submeter(id);
      if (state != StatusPedidoCompra.AGUARDANDO_APROVACAO) service.aprovar(id);
    }
    if (state == StatusPedidoCompra.PARCIALMENTE_RECEBIDO) receive(id, 1);
    if (state == StatusPedidoCompra.RECEBIDO) receive(id, 3);
    var c = new ComprasInput.Cancelamento();
    c.motivo = "Auditoria H2";
    if (state == StatusPedidoCompra.CANCELADO) service.cancelar(id, c);
    long audits = auditRepo.count(), moves = movements.count(), receiptsBefore = receipts.count();
    double saldo = balance();
    if (state == StatusPedidoCompra.PARCIALMENTE_RECEBIDO || state == StatusPedidoCompra.RECEBIDO)
      assertThrows(ConflitoException.class, () -> service.cancelar(id, c));
    else assertEquals("CANCELADO", service.cancelar(id, c).get("status").toString());
    if (state == StatusPedidoCompra.CANCELADO) assertEquals(audits, auditRepo.count());
    assertEquals(moves, movements.count());
    assertEquals(receiptsBefore, receipts.count());
    assertEquals(saldo, balance());
  }

  @Test
  void aprovadoBloqueiaEdicaoDeFornecedorProdutoQuantidadePrecoEVinculos() {
    int n = need(3);
    int id = (Integer) service.criar(linked(n, 3)).get("id");
    service.submeter(id);
    service.aprovar(id);
    var original = service.buscar(id);
    var changed = input(1);
    changed.fornecedorId = -1;
    changed.itens.get(0).produtoId = -1;
    changed.itens.get(0).valorUnitario = BigDecimal.ZERO;
    assertThrows(ConflitoException.class, () -> service.atualizar(id, changed));
    assertEquals(original, service.buscar(id));
    assertEquals(3d, needsService.buscar(n).compra().get("quantidadeVinculadaPendente"));
  }

  @Test
  void historicoLegivelAposInativarFornecedorEProduto() {
    int id = approved(3);
    var f = new ComprasInput.Fornecedor();
    f.nome = supplier.getNome();
    f.tipoPessoa = supplier.getTipoPessoa();
    f.ativo = false;
    suppliers.atualizar(supplier.getId(), f);
    product.setAtivo(false);
    products.save(product);
    assertThrows(ConflitoException.class, () -> create(1));
    assertEquals(supplier.getNome(), service.buscar(id).get("fornecedorNome"));
    receive(id, 3);
    assertEquals(3, balance());
    assertEquals("RECEBIDO", service.buscar(id).get("status").toString());
  }

  @Test
  void numeracaoConcorrenteUnicaPersistida() throws Exception {
    var ids = new java.util.concurrent.ConcurrentLinkedQueue<Integer>();
    Callable<Boolean> op =
        () -> {
          ids.add(create(1));
          return true;
        };
    assertEquals(List.of(true, true), concurrent(op, op));
    assertEquals(2, ids.stream().map(i -> service.buscar(i).get("numero")).distinct().count());
    assertEquals(2, ids.stream().distinct().count());
  }

  @Test
  void destinoMaliciosoNaoModificaOutroAlmoxarifado() {
    int id = approved(3);
    var other = new Almoxarifado();
    other.setNome("Outro H2");
    other = warehouses.save(other);
    var stock = new Estoque();
    stock.setProduto(product);
    stock.setAlmoxarifado(other);
    stock.setQuantidade(9);
    stocks.save(stock);
    var in = receiving(id, 3);
    in.almoxarifadoId = other.getId();
    assertThrows(
        ConflitoException.class, () -> service.receber(id, in, UUID.randomUUID().toString()));
    assertEquals(9, stocks.findById(stock.getId()).orElseThrow().getQuantidade());
    assertEquals(0, balance());
    receive(id, 3);
    assertEquals(9, stocks.findById(stock.getId()).orElseThrow().getQuantidade());
  }

  @Test
  void paginaDePedidosCarregaItensSemConsultaPorPedido() {
    for (int i = 0; i < 12; i++) create(1);
    var stats =
        entityManager
            .getEntityManagerFactory()
            .unwrap(org.hibernate.SessionFactory.class)
            .getStatistics();
    stats.clear();
    var page = service.listar(null, supplier.getId(), null, null, null, null, null, 0, 20);
    assertEquals(12, page.getContent().size());
    assertTrue(
        stats.getPrepareStatementCount() <= 3,
        "SQL statements=" + stats.getPrepareStatementCount());
    for (var p : page) assertEquals("12.35", p.get("total"));
  }

  @ParameterizedTest
  @EnumSource(Perfil.class)
  void todasAsRotasNovasRespeitamMatrizEIdsNaoContornamPermissao(Perfil profile) throws Exception {
    var authentication = auth(profile);
    String[] paths = {
      "/fornecedores/999999",
      "/pedidos-compra/999999",
      "/pedidos-compra/999999/recebimentos",
      "/recebimentos-compra/999999"
    };
    for (String path : paths)
      mvc.perform(get(path).with(authentication(authentication))).andExpect(status().isNotFound());
    String[][] writes = {
      {"POST", "/fornecedores", "FORNECEDOR_GERENCIAR"},
      {"PUT", "/fornecedores/999999", "FORNECEDOR_GERENCIAR"},
      {"POST", "/pedidos-compra", "COMPRA_CRIAR"},
      {"PUT", "/pedidos-compra/999999", "COMPRA_CRIAR"},
      {"PUT", "/pedidos-compra/999999/submeter", "COMPRA_CRIAR"},
      {"PUT", "/pedidos-compra/999999/aprovar", "COMPRA_APROVAR"},
      {"PUT", "/pedidos-compra/999999/cancelar", "COMPRA_CANCELAR"},
      {"POST", "/pedidos-compra/999999/recebimentos", "RECEBIMENTO_REGISTRAR"},
      {"PUT", "/necessidades-compra/999999/cancelar", "NECESSIDADE_COMPRA_GERENCIAR"}
    };
    for (var route : writes) {
      var request =
          request(org.springframework.http.HttpMethod.valueOf(route[0]), route[1])
              .with(authentication(authentication))
              .with(csrf())
              .header("Idempotency-Key", UUID.randomUUID().toString())
              .contentType(MediaType.APPLICATION_JSON)
              .content("{}");
      int status = mvc.perform(request).andReturn().getResponse().getStatus();
      if (!profile.permissoes().contains(Permissao.valueOf(route[2])))
        assertEquals(403, status, route[1]);
      else assertTrue(status == 400 || status == 404, route[1] + " " + status);
      mvc.perform(
              request(org.springframework.http.HttpMethod.valueOf(route[0]), route[1])
                  .with(authentication(authentication))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{}"))
          .andExpect(status().isForbidden());
    }
    SecurityContextHolder.getContext().setAuthentication(authentication);
    if (!profile.permissoes().contains(Permissao.COMPRA_APROVAR))
      assertThrows(AccessDeniedException.class, () -> service.aprovar(999999));
    if (!profile.permissoes().contains(Permissao.COMPRA_CANCELAR))
      assertThrows(
          AccessDeniedException.class,
          () -> service.cancelar(999999, new ComprasInput.Cancelamento()));
    if (!profile.permissoes().contains(Permissao.FORNECEDOR_GERENCIAR))
      assertThrows(
          AccessDeniedException.class,
          () -> suppliers.atualizar(999999, new ComprasInput.Fornecedor()));
    if (!profile.permissoes().contains(Permissao.NECESSIDADE_COMPRA_GERENCIAR))
      assertThrows(
          AccessDeniedException.class,
          () -> service.cancelarNecessidade(999999, new ComprasInput.Cancelamento()));
    if (profile == Perfil.GESTOR)
      mvc.perform(get("/usuarios").with(authentication(authentication)))
          .andExpect(status().isForbidden());
  }

  void assertStockChain(double initial) {
    tx.execute(
        st -> {
          double before = initial;
          var rows =
              movements.findAll().stream()
                  .filter(
                      m ->
                          m.getProduto().getId().equals(product.getId())
                              && m.getAlmoxarifado().getId().equals(warehouse.getId()))
                  .sorted(java.util.Comparator.comparing(Movimentacao::getId))
                  .toList();
          for (var m : rows) {
            assertEquals(before, m.getSaldoAnterior());
            before =
                ComprasViews.decimal(
                    before, m.getQuantidade(), m.getTipo() == TipoMovimentacao.ENTRADA);
            assertEquals(before, m.getSaldoPosterior());
          }
          assertEquals(balance(), before);
          return null;
        });
  }

  @Test
  void listagemNecessidadesConsultaAlocacoesEmLote() {
    for (int i = 0; i < 6; i++) {
      int n = need(2);
      service.criar(linked(n, 1));
    }
    clearInvocations(allocations);
    var rows = needsService.listar(null, product.getId(), warehouse.getId(), null);
    assertEquals(6, rows.size());
    for (var row : rows) {
      assertEquals(1d, row.compra().get("quantidadeVinculadaPendente"));
      assertEquals(1d, row.compra().get("quantidadeDisponivel"));
    }
    verify(allocations, times(1)).ativasEm(anyCollection());
    verify(allocations, never()).ativas(anyInt());
  }

  @Test
  void paginaRecebimentosNaoFazConsultaDeItensPorRegistro() {
    int id = approved(12);
    for (int i = 0; i < 12; i++) receive(id, 1);
    var stats =
        entityManager
            .getEntityManagerFactory()
            .unwrap(org.hibernate.SessionFactory.class)
            .getStatistics();
    stats.clear();
    var page = service.listarRecebimentos(id, null, null, null, null, null, 0, 20);
    assertEquals(12, page.getContent().size());
    assertTrue(
        stats.getPrepareStatementCount() <= 5,
        "SQL statements=" + stats.getPrepareStatementCount());
  }

  @Test
  void atorNaoVemDeQueryOuHeaderEEventoPreservaResponsavel() throws Exception {
    int id = approved(1), item = receiving(id, 1).itens.get(0).itemPedidoId;
    var result =
        mvc.perform(
                post("/pedidos-compra/" + id + "/recebimentos")
                    .with(authentication(identity))
                    .with(csrf())
                    .header("Idempotency-Key", UUID.randomUUID().toString())
                    .header("X-Actor-Id", "999999")
                    .param("ator", "999999")
                    .param("auditUser", "999999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"almoxarifadoId\":"
                            + warehouse.getId()
                            + ",\"responsavelId\":"
                            + person.getId()
                            + ",\"itens\":[{\"itemPedidoId\":"
                            + item
                            + ",\"quantidade\":1}]}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.recebidoPor").value(actor.getId()))
            .andExpect(jsonPath("$.responsavelId").value(person.getId()))
            .andReturn();
    var events =
        auditRepo.findAll().stream()
            .filter(
                e ->
                    e.getEvento().equals("RECEBIMENTO_COMPRA_REGISTRADO")
                        && Objects.equals(e.getAtorId(), actor.getId()))
            .toList();
    assertEquals(1, events.size());
    assertEquals(person.getId(), events.get(0).getResponsavelOperacionalId());
  }

  @Test
  void bloqueioProdutoRecarregaEstadoAntesSubmeter() {
    int id = create(1);
    assertThrows(
        ConflitoException.class,
        () ->
            tx.execute(
                st -> {
                  assertTrue(products.findById(product.getId()).orElseThrow().isAtivo());
                  try {
                    concurrent(
                        () -> {
                          var p = products.findById(product.getId()).orElseThrow();
                          p.setAtivo(false);
                          products.saveAndFlush(p);
                          return true;
                        },
                        () -> true);
                  } catch (Exception e) {
                    throw new RuntimeException(e);
                  }
                  service.submeter(id);
                  return null;
                }));
    assertEquals("RASCUNHO", service.buscar(id).get("status").toString());
  }

  @Test
  void recebimentoRecarregaPedidoEEstoqueAntigosSobLock() {
    int id = approved(100);
    var e = new Estoque();
    e.setProduto(product);
    e.setAlmoxarifado(warehouse);
    e.setQuantidade(5);
    stocks.saveAndFlush(e);
    var delivery = receiving(id, 40);
    tx.execute(
        st -> {
          orders.findById(id).orElseThrow().getItens().get(0).getQuantidadeRecebida();
          stocks.findById(e.getId()).orElseThrow().getQuantidade();
          try {
            concurrent(
                () -> {
                  receive(id, 60);
                  return true;
                },
                () -> true);
          } catch (Exception ex) {
            throw new RuntimeException(ex);
          }
          service.receber(id, delivery, UUID.randomUUID().toString());
          return null;
        });
    assertEquals(105, balance());
    assertEquals("RECEBIDO", service.buscar(id).get("status").toString());
    assertEquals(beforeMoves + 2, movements.count());
    assertStockChain(5);
  }

  @Test
  void fornecedorInativadoNaoPodeSerCompradoComCacheAntigo() {
    assertThrows(
        ConflitoException.class,
        () ->
            tx.execute(
                st -> {
                  assertTrue(suppliersRepo.findById(supplier.getId()).orElseThrow().getAtivo());
                  try {
                    concurrent(
                        () -> {
                          var f = new ComprasInput.Fornecedor();
                          f.nome = supplier.getNome();
                          f.tipoPessoa = supplier.getTipoPessoa();
                          f.ativo = false;
                          suppliers.atualizar(supplier.getId(), f);
                          return true;
                        },
                        () -> true);
                  } catch (Exception ex) {
                    throw new RuntimeException(ex);
                  }
                  service.criar(input(1));
                  return null;
                }));
    assertFalse(suppliersRepo.findById(supplier.getId()).orElseThrow().getAtivo());
  }

  @ParameterizedTest
  @EnumSource(Perfil.class)
  void pedidoNaoContornaProtecaoDoDocumentoEContatoDoFornecedor(Perfil profile) throws Exception {
    String doc = String.format(Locale.ROOT, "%09d", actor.getId());
    for (int etapa = 0; etapa < 2; etapa++) {
      int sum = 0;
      for (int k = 0; k < doc.length(); k++) sum += (doc.charAt(k) - 48) * (doc.length() + 1 - k);
      int remainder = sum % 11;
      doc += remainder < 2 ? 0 : 11 - remainder;
    }
    var f = new ComprasInput.Fornecedor();
    f.nome = supplier.getNome();
    f.tipoPessoa = TipoPessoa.PF;
    f.documento = doc;
    f.contato = "Contato protegido H2";
    suppliers.atualizar(supplier.getId(), f);
    int id = create(1);
    boolean full = profile.permissoes().contains(Permissao.FORNECEDOR_GERENCIAR);
    mvc.perform(get("/pedidos-compra/" + id).with(authentication(auth(profile))))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.fornecedorDocumento").value(full ? doc : DocumentoFornecedor.mascarar(doc)))
        .andExpect(
            full
                ? jsonPath("$.fornecedorContato").value("Contato protegido H2")
                : jsonPath("$.fornecedorContato").doesNotExist());
    SecurityContextHolder.getContext().setAuthentication(auth(profile));
    assertEquals(
        full ? doc : DocumentoFornecedor.mascarar(doc),
        service.buscar(id).get("fornecedorDocumento"));
    assertEquals(full ? "Contato protegido H2" : null, service.buscar(id).get("fornecedorContato"));
  }
}
