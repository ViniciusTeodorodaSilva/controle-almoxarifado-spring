package br.com.almoxarifado.service;
import br.com.almoxarifado.dto.*;
import br.com.almoxarifado.exception.*;
import br.com.almoxarifado.model.*;
import br.com.almoxarifado.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:bes-bloco2;MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000")
@AutoConfigureMockMvc @ActiveProfiles("test")
class EstoqueInteligenteTransferenciaTests {
    @Autowired EstoqueInteligenteService limites;
    @Autowired TransferenciaEstoqueService service;
    @Autowired EstoqueService estoqueService;
    @Autowired EstoqueRepository estoques;
    @Autowired ProdutoRepository produtos;
    @Autowired AlmoxarifadoRepository locais;
    @Autowired FuncionarioRepository pessoas;
    @Autowired UnidadeMedidaRepository unidades;
    @Autowired TransferenciaEstoqueRepository transferencias;
    @Autowired ItemTransferenciaRepository itens;
    @MockitoSpyBean MovimentacaoRepository movimentos;
    @Autowired MockMvc mvc;
    Produto produto; Almoxarifado origem, destino; Funcionario responsavel; Estoque saldoOrigem, saldoDestino;
    @BeforeEach void preparar() {
        movimentos.deleteAll(); itens.deleteAll(); transferencias.deleteAll(); estoques.deleteAll(); produtos.deleteAll(); unidades.deleteAll(); pessoas.deleteAll(); locais.deleteAll();
        UnidadeMedida unidade = new UnidadeMedida(); unidade.setNome("Unidade"); unidade.setSigla("UN"); unidade.setPermiteFracionamento(false); unidade = unidades.save(unidade);
        produto = new Produto(); produto.setNome("Parafuso"); produto.setCodigo("P-1"); produto.setUnidadeMedidaConfigurada(unidade); produto = produtos.save(produto);
        origem = local("Central"); destino = local("Obra");
        responsavel = new Funcionario(); responsavel.setNome("Responsável"); responsavel.setMatricula("R1"); responsavel = pessoas.save(responsavel);
        saldoOrigem = estoque(produto, origem, 10); saldoDestino = estoque(produto, destino, 2);
    }
    @Test void minimoValidoNaoMudaSaldoNemHistorico() { var resultado = configurar(3.0, null); assertEquals(3, resultado.estoqueMinimo()); assertEquals(10, saldo(produto, origem)); assertEquals(0, movimentos.count()); }
    @Test void maximoValidoSemMinimo() { assertEquals(20, configurar(null, 20.0).estoqueMaximo()); }
    @Test void maximoMenorQueMinimo() { assertThrows(IllegalArgumentException.class, () -> configurar(10.0, 2.0)); }
    @Test void minimoNegativo() { assertThrows(IllegalArgumentException.class, () -> configurar(-1.0, null)); }
    @Test void maximoNegativo() { assertThrows(IllegalArgumentException.class, () -> configurar(null, -1.0)); }
    @Test void limitesNaoFinitos() { assertThrows(IllegalArgumentException.class, () -> configurar(Double.NaN, null)); assertThrows(IllegalArgumentException.class, () -> configurar(null, Double.POSITIVE_INFINITY)); }
    @Test void zeroENulosSaoPermitidos() { configurar(0.0, 0.0); var r = configurar(null, null); assertNull(r.estoqueMinimo()); assertNull(r.estoqueMaximo()); }
    @Test void saldoIgualMinimoGeraAlerta() { configurar(10.0, 20.0); assertEquals(1, limites.alertas(null, null).size()); }
    @Test void saldoAbaixoMinimoGeraAlerta() { configurar(11.0, 20.0); assertEquals(1, limites.alertas(null, null).size()); }
    @Test void saldoAcimaMinimoSemAlerta() { configurar(9.0, 20.0); assertTrue(limites.alertas(null, null).isEmpty()); }
    @Test void semMinimoNaoGeraAlerta() { configurar(null, 20.0); assertTrue(limites.alertas(null, null).isEmpty()); }
    @Test void reposicaoVaiAteMaximo() { configurar(10.0, 20.0); assertEquals(10, limites.reposicoes(null, null).get(0).quantidadeSugerida()); assertEquals(10, saldo(produto, origem)); }
    @Test void semMaximoMantemAlertaSemSugestao() { configurar(10.0, null); assertNull(limites.alertas(null, null).get(0).quantidadeSugerida()); assertTrue(limites.reposicoes(null, null).isEmpty()); }
    @Test void sugestaoZeroNaoGeraReposicaoNegativa() { configurar(10.0, 10.0); assertEquals(0, limites.alertas(null, null).get(0).quantidadeSugerida()); assertTrue(limites.reposicoes(null, null).isEmpty()); }
    @Test void filtrosAlertasValidamPais() { configurar(10.0, 20.0); assertEquals(1, limites.alertas(produto.getId(), origem.getId()).size()); assertTrue(limites.alertas(produto.getId(), destino.getId()).isEmpty()); assertThrows(RecursoNaoEncontradoException.class, () -> limites.alertas(999999, null)); }
    @Test void estoqueInexistenteNosLimites() { assertThrows(RecursoNaoEncontradoException.class, () -> limites.configurar(999999, new LimitesEstoqueInput(0.0, 1.0))); }
    @Test void cadastroLegadoNaoContornaValidacaoDeLimites() { Estoque e = new Estoque(); e.setProduto(produto); e.setAlmoxarifado(local("Extra")); e.setEstoqueMinimo(-1.0); assertThrows(IllegalArgumentException.class, () -> estoqueService.cadastrar(e)); }
    @Test void contratosHttpLimitesEAlertasSemEntidadesCompletas() throws Exception {
        mvc.perform(put("/estoques/{id}/limites", saldoOrigem.getId()).contentType(MediaType.APPLICATION_JSON).content("{\"estoqueMinimo\":10,\"estoqueMaximo\":20,\"quantidade\":999}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.saldoAtual").value(10)).andExpect(jsonPath("$.estoqueMinimo").value(10));
        mvc.perform(get("/estoques/alertas")).andExpect(status().isOk()).andExpect(jsonPath("$[0].quantidadeSugerida").value(10)).andExpect(jsonPath("$[0].produto").value("Parafuso"));
        mvc.perform(get("/estoques/reposicoes")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }
    @Test void transferenciaSimples() { var t = transferir(produto, 3); assertEquals("CONCLUIDA", t.status()); assertEquals(7, saldo(produto, origem)); assertEquals(5, saldo(produto, destino)); assertEquals(1, transferencias.count()); }
    @Test void transferenciaVariosItens() { Produto outro = produto("P-2"); estoque(outro, origem, 5); var t = service.criar(input(List.of(item(produto, 2), item(outro, 4)))); assertEquals(2, t.itens().size()); assertEquals(1, saldo(outro, origem)); assertEquals(4, saldo(outro, destino)); assertEquals(4, movimentos.count()); }
    @Test void origemIgualDestino() { assertThrows(IllegalArgumentException.class, () -> service.criar(new TransferenciaInput(origem.getId(), origem.getId(), responsavel.getId(), null, List.of(item(produto, 1))))); }
    @Test void transferenciaVazia() { assertThrows(IllegalArgumentException.class, () -> service.criar(input(List.of()))); }
    @Test void quantidadeZero() { assertThrows(IllegalArgumentException.class, () -> transferir(produto, 0)); }
    @Test void quantidadeNegativa() { assertThrows(IllegalArgumentException.class, () -> transferir(produto, -1)); }
    @Test void quantidadeNaoFinita() { assertThrows(IllegalArgumentException.class, () -> transferir(produto, Double.NaN)); }
    @Test void quantidadeAcimaSaldo() { assertThrows(IllegalArgumentException.class, () -> transferir(produto, 11)); assertIntacto(); }
    @Test void unidadeNaoFracionaria() { assertThrows(IllegalArgumentException.class, () -> transferir(produto, 1.5)); }
    @Test void unidadeFracionaria() { var u = produto.getUnidadeMedidaConfigurada(); u.setPermiteFracionamento(true); unidades.save(u); transferir(produto, 0.5); assertEquals(9.5, saldo(produto, origem)); }
    @Test void produtoInexistente() { assertThrows(RecursoNaoEncontradoException.class, () -> service.criar(input(List.of(new TransferenciaInput.Item(999999, 1.0))))); }
    @Test void produtoInativo() { produto.setAtivo(false); produtos.save(produto); assertThrows(IllegalArgumentException.class, () -> transferir(produto, 1)); }
    @Test void almoxarifadoInexistente() { assertThrows(RecursoNaoEncontradoException.class, () -> service.criar(new TransferenciaInput(999999, destino.getId(), responsavel.getId(), null, List.of(item(produto, 1))))); }
    @Test void destinoInexistente() { assertThrows(RecursoNaoEncontradoException.class, () -> service.criar(new TransferenciaInput(origem.getId(), 999999, responsavel.getId(), null, List.of(item(produto, 1))))); }
    @Test void responsavelInexistente() { assertThrows(RecursoNaoEncontradoException.class, () -> service.criar(new TransferenciaInput(origem.getId(), destino.getId(), 999999, null, List.of(item(produto, 1))))); }
    @Test void itemDuplicado() { assertThrows(IllegalArgumentException.class, () -> service.criar(input(List.of(item(produto, 1), item(produto, 2))))); }
    @Test void estoqueOrigemAusente() { estoques.delete(saldoOrigem); assertThrows(RecursoNaoEncontradoException.class, () -> transferir(produto, 1)); assertEquals(0, transferencias.count()); }
    @Test void destinoAusenteCriadoNaMesmaTransacao() { estoques.delete(saldoDestino); transferir(produto, 3); assertEquals(3, saldo(produto, destino)); }
    @Test void rollbackTambemRemoveNovoDestino() { Produto outro = produto("P-2"); estoque(outro, origem, 1); assertThrows(IllegalArgumentException.class, () -> service.criar(input(List.of(item(produto, 2), item(outro, 4))))); assertIntacto(); assertFalse(estoques.existsByProdutoIdAndAlmoxarifadoId(outro.getId(), destino.getId())); }
    @Test void rollbackAposMovimentacaoGravada() { doThrow(new IllegalStateException("Falha simulada")).when(movimentos).save(argThat(m -> m != null && m.getTransferencia() != null && m.getTipo() == TipoMovimentacao.ENTRADA)); assertThrows(IllegalStateException.class, () -> transferir(produto, 3)); assertIntacto(); }
    @Test void movimentacoesVinculadasResponsavelSaldosEData() { var t = transferir(produto, 3); var ms = service.movimentacoes(t.id()); assertEquals(2, ms.size()); assertEquals("SAIDA", ms.get(0).tipo()); assertEquals("ENTRADA", ms.get(1).tipo()); assertEquals(10, ms.get(0).saldoAnterior()); assertEquals(7, ms.get(0).saldoPosterior()); assertEquals(2, ms.get(1).saldoAnterior()); assertEquals(5, ms.get(1).saldoPosterior()); for (var m : ms) { assertEquals(t.id(), m.transferenciaId()); assertEquals(responsavel.getId(), m.responsavel().id()); assertEquals(t.dataHora(), m.dataHora()); assertEquals(origem.getId(), m.origemId()); assertEquals(destino.getId(), m.destinoId()); } }
    @Test void filtrosTransferenciaE404() { transferir(produto, 1); assertEquals(1, service.listar(origem.getId(), destino.getId(), produto.getId()).size()); assertTrue(service.listar(destino.getId(), origem.getId(), null).isEmpty()); assertThrows(RecursoNaoEncontradoException.class, () -> service.buscar(999999)); assertThrows(RecursoNaoEncontradoException.class, () -> service.listar(null, null, 999999)); }
    @Test void contratosHttpTransferencia() throws Exception {
        String json = "{\"origemId\":" + origem.getId() + ",\"destinoId\":" + destino.getId() + ",\"responsavelId\":" + responsavel.getId() + ",\"itens\":[{\"produtoId\":" + produto.getId() + ",\"quantidade\":2}]}";
        mvc.perform(post("/transferencias").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CONCLUIDA")).andExpect(jsonPath("$.itens[0].produto").value("Parafuso"));
        Integer id = transferencias.findAll().get(0).getId();
        mvc.perform(get("/transferencias/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.responsavel.matricula").doesNotExist());
        mvc.perform(get("/transferencias/{id}/movimentacoes", id)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2)).andExpect(jsonPath("$[0].transferenciaId").value(id));
        mvc.perform(post("/transferencias").contentType(MediaType.APPLICATION_JSON).content("{\"origemId\":0,\"itens\":[]}")).andExpect(status().isBadRequest());
    }
    @Test void concorrenciaMesmoSaldoNaoFicaNegativo() throws Exception { var results = concorrentes(() -> transferir(produto, 7), () -> transferir(produto, 7)); assertEquals(1, results.stream().filter(Boolean::booleanValue).count()); assertEquals(3, saldo(produto, origem)); assertEquals(9, saldo(produto, destino)); assertEquals(1, transferencias.count()); }
    @Test void transferenciasInversasSemDeadlock() throws Exception { saldoDestino.setQuantidade(10); estoques.save(saldoDestino); var results = concorrentes(() -> transferir(produto, 2), () -> service.criar(new TransferenciaInput(destino.getId(), origem.getId(), responsavel.getId(), null, List.of(item(produto, 2))))); assertTrue(results.stream().allMatch(Boolean::booleanValue)); assertEquals(10, saldo(produto, origem)); assertEquals(10, saldo(produto, destino)); assertEquals(4, movimentos.count()); }
    @Test void transferenciaESaidaManualDisputamMesmoSaldo() throws Exception { var results = concorrentes(() -> transferir(produto, 7), () -> estoqueService.saidaEstoque(produto.getId(), origem.getId(), 7, responsavel.getId(), responsavel.getId())); assertEquals(1, results.stream().filter(Boolean::booleanValue).count()); assertEquals(3, saldo(produto, origem)); }
    private List<Boolean> concorrentes(Callable<?> a, Callable<?> b) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2); CountDownLatch start = new CountDownLatch(1);
        try { var fa = pool.submit(() -> executar(a, start)); var fb = pool.submit(() -> executar(b, start)); start.countDown(); return List.of(fa.get(20, TimeUnit.SECONDS), fb.get(20, TimeUnit.SECONDS)); }
        finally { pool.shutdownNow(); }
    }
    private boolean executar(Callable<?> action, CountDownLatch start) throws Exception { start.await(); try { action.call(); return true; } catch (IllegalArgumentException e) { return false; } }
    private AlertaEstoqueResponse configurar(Double min, Double max) { return limites.configurar(saldoOrigem.getId(), new LimitesEstoqueInput(min, max)); }
    private TransferenciaInput input(List<TransferenciaInput.Item> values) { return new TransferenciaInput(origem.getId(), destino.getId(), responsavel.getId(), "Teste", values); }
    private TransferenciaInput.Item item(Produto p, double q) { return new TransferenciaInput.Item(p.getId(), q); }
    private TransferenciaResponse transferir(Produto p, double q) { return service.criar(input(List.of(item(p, q)))); }
    private Almoxarifado local(String nome) { Almoxarifado a = new Almoxarifado(); a.setNome(nome); return locais.save(a); }
    private Produto produto(String code) { Produto p = new Produto(); p.setNome(code); p.setCodigo(code); p.setUnidadeMedidaConfigurada(produto.getUnidadeMedidaConfigurada()); return produtos.save(p); }
    private Estoque estoque(Produto p, Almoxarifado a, double q) { Estoque e = new Estoque(); e.setProduto(p); e.setAlmoxarifado(a); e.setQuantidade(q); return estoques.save(e); }
    private double saldo(Produto p, Almoxarifado a) { return estoques.findByProdutoIdAndAlmoxarifadoId(p.getId(), a.getId()).orElseThrow().getQuantidade(); }
    private void assertIntacto() { assertEquals(10, saldo(produto, origem)); assertEquals(2, saldo(produto, destino)); assertEquals(0, movimentos.count()); assertEquals(0, transferencias.count()); assertEquals(0, itens.count()); }
}
