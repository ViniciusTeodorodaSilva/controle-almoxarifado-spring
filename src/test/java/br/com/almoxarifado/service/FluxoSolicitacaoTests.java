package br.com.almoxarifado.service;

import br.com.almoxarifado.model.*;
import br.com.almoxarifado.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.concurrent.*;
import java.util.UUID;
import br.com.almoxarifado.dto.AtendimentoInput;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ActiveProfiles("test")
class FluxoSolicitacaoTests {
    @Autowired SolicitacaoService solicitacaoService;
    @Autowired AtendimentoSolicitacaoService atendimentosService;
    @Autowired AtendimentoSolicitacaoRepository atendimentos;
    @Autowired ItemAtendimentoSolicitacaoRepository itensAtendidos;
    @Autowired EstoqueService estoqueService;
    @Autowired MovimentacaoService movimentacaoService;
    @MockitoSpyBean SolicitacaoRepository solicitacoes;
    @Autowired ItemSolicitacaoRepository itens;
    @Autowired EstoqueRepository estoques;
    @Autowired ProdutoRepository produtos;
    @Autowired AlmoxarifadoRepository almoxarifados;
    @Autowired FuncionarioRepository funcionarios;
    @Autowired MovimentacaoRepository movimentacoes;

    private Produto produto;
    private Almoxarifado almoxarifado;
    private Funcionario funcionario;

    @BeforeEach
    void preparar() {
        movimentacoes.deleteAll();
        itensAtendidos.deleteAll();
        atendimentos.deleteAll();
        itens.deleteAll();
        solicitacoes.deleteAll();
        estoques.deleteAll();
        produtos.deleteAll();
        almoxarifados.deleteAll();
        funcionarios.deleteAll();

        produto = novoProduto("Luva");
        Almoxarifado local = new Almoxarifado();
        local.setNome("Central");
        almoxarifado = almoxarifados.save(local);
        Funcionario pessoa = new Funcionario();
        pessoa.setNome("Solicitante");
        pessoa.setMatricula("BES-001");
        funcionario = funcionarios.save(pessoa);
        prepararSaldo(produto, 10);
    }

    @Test
    void aprovaSolicitacaoValidaSemSaidaDeEstoque() {
        Solicitacao solicitacao = solicitar(produto, 4);
        Solicitacao aprovada = solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId());
        assertEquals(StatusSolicitacao.APROVADA, aprovada.getStatus());
        assertEquals(funcionario.getId(), aprovada.getResponsavelAprovacao().getId());
        assertNotNull(aprovada.getDataAprovacao());
        assertEquals(10, saldo(produto));
        assertEquals(0, movimentacoes.count());
    }

    @Test
    void naoAprovaSemItens() {
        Solicitacao solicitacao = criarSolicitacao();
        assertThrows(IllegalArgumentException.class, () -> solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId()));
        assertIntacto(solicitacao);
    }

    @Test
    void autorizaMesmoSemEstoqueSuficiente() {
        Solicitacao solicitacao = solicitar(produto, 11);
        assertEquals(StatusSolicitacao.APROVADA, solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId()).getStatus());
        assertEquals(10, saldo(produto));
        assertEquals(0, movimentacoes.count());
    }

    @Test
    void naoAprovaDuasVezes() {
        Solicitacao solicitacao = solicitar(produto, 4);
        solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId());
        assertThrows(IllegalArgumentException.class, () -> solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId()));
        assertEquals(10, saldo(produto));
        assertEquals(0, movimentacoes.count());
    }

    @Test
    void rejeitaPendenteSemAlterarEstoque() {
        Solicitacao solicitacao = solicitar(produto, 4);
        assertEquals(StatusSolicitacao.REJEITADA, solicitacaoService.rejeitar(solicitacao.getId()).getStatus());
        assertEquals(StatusSolicitacao.REJEITADA, status(solicitacao));
        assertEquals(10, saldo(produto));
        assertEquals(0, movimentacoes.count());
    }

    @Test
    void naoRejeitaSolicitacaoAprovada() {
        Solicitacao solicitacao = solicitar(produto, 4);
        solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId());
        assertThrows(IllegalArgumentException.class, () -> solicitacaoService.rejeitar(solicitacao.getId()));
        assertEquals(StatusSolicitacao.APROVADA, status(solicitacao));
        assertEquals(10, saldo(produto));
    }

    @Test
    void naoRejeitaDuasVezes() {
        Solicitacao solicitacao = criarSolicitacao();
        solicitacaoService.rejeitar(solicitacao.getId());
        assertThrows(IllegalArgumentException.class, () -> solicitacaoService.rejeitar(solicitacao.getId()));
    }

    @Test
    void naoAdicionaItemAposAprovacao() {
        Solicitacao solicitacao = solicitar(produto, 4);
        solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId());
        assertThrows(IllegalArgumentException.class,
                () -> solicitacaoService.adicionarItem(solicitacao.getId(), produto.getId(), 1));
        assertEquals(1, itens.findBySolicitacaoId(solicitacao.getId()).size());
    }

    @Test
    void naoAdicionaItemAposRejeicao() {
        Solicitacao solicitacao = criarSolicitacao();
        solicitacaoService.rejeitar(solicitacao.getId());
        assertThrows(IllegalArgumentException.class,
                () -> solicitacaoService.adicionarItem(solicitacao.getId(), produto.getId(), 1));
        assertEquals(0, itens.count());
    }

    @Test
    void saidaNaoPermiteSaldoNegativo() {
        assertThrows(IllegalArgumentException.class, () -> estoqueService.saidaEstoque(
                produto.getId(), almoxarifado.getId(), 11, funcionario.getId(), funcionario.getId()));
        assertEquals(10, saldo(produto));
        assertEquals(0, movimentacoes.count());
    }

    @Test
    void permiteConsumirExatamenteOSaldo() {
        Solicitacao solicitacao = solicitar(produto, 10);
        solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId());
        atenderTudo(solicitacao);
        assertEquals(0, saldo(produto));
    }

    @Test
    void verificaSomaDosItensDoMesmoProduto() {
        Solicitacao solicitacao = solicitar(produto, 6);
        solicitacaoService.adicionarItem(solicitacao.getId(), produto.getId(), 5);
        solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId());
        assertThrows(IllegalArgumentException.class, () -> atenderTudo(solicitacao));
        assertEquals(StatusSolicitacao.EM_SEPARACAO, status(solicitacao));
        assertEquals(10, saldo(produto));
        assertEquals(0, movimentacoes.count());
    }

    @Test
    void aprovaItensRepetidosDentroDoSaldo() {
        Solicitacao solicitacao = solicitar(produto, 2);
        solicitacaoService.adicionarItem(solicitacao.getId(), produto.getId(), 3);
        solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId());
        atenderTudo(solicitacao);
        assertEquals(5, saldo(produto));
        assertEquals(2, movimentacoes.count());
        assertEquals(5, movimentacoes.findAll().stream().mapToDouble(Movimentacao::getQuantidade).sum());
    }

    @Test
    void falhaNoSegundoProdutoNaoAlteraPrimeiro() {
        Produto outro = novoProduto("Máscara");
        prepararSaldo(outro, 1);
        Solicitacao solicitacao = solicitar(produto, 4);
        solicitacaoService.adicionarItem(solicitacao.getId(), outro.getId(), 2);
        solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId());
        assertThrows(IllegalArgumentException.class, () -> atenderTudo(solicitacao));
        assertEquals(StatusSolicitacao.EM_SEPARACAO, status(solicitacao));
        assertEquals(10, saldo(produto));
        assertEquals(0, movimentacoes.count());
        assertEquals(1, saldo(outro));
    }

    @Test
    void rollbackDesfazSaldosEMovimentacoesSePersistenciaFinalFalhar() {
        Produto outro = novoProduto("Máscara");
        prepararSaldo(outro, 5);
        Solicitacao solicitacao = solicitar(produto, 4);
        solicitacaoService.adicionarItem(solicitacao.getId(), outro.getId(), 2);
        doThrow(new IllegalStateException("Falha simulada na persistência final"))
                .when(solicitacoes).save(argThat(s -> s != null && s.getStatus() == StatusSolicitacao.APROVADA));
        assertThrows(IllegalStateException.class, () -> solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId()));
        // Sem transação no teste: estas consultas observam o banco após o rollback do service.
        assertIntacto(solicitacao);
        assertEquals(5, saldo(outro));
    }

    @Test
    void autorizaProdutoSemEstoqueCadastrado() {
        Produto outro = novoProduto("Sem estoque");
        Solicitacao solicitacao = solicitar(outro, 1);
        assertEquals(StatusSolicitacao.APROVADA, solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId()).getStatus());
        assertFalse(estoques.existsByProdutoIdAndAlmoxarifadoId(outro.getId(), almoxarifado.getId()));
        assertEquals(0, movimentacoes.count());
    }

    @Test
    void naoAprovaItemLegadoComQuantidadeInvalida() {
        Solicitacao solicitacao = solicitar(produto, 1);
        ItemSolicitacao item = itens.findBySolicitacaoId(solicitacao.getId()).get(0);
        item.setQuantidade(-1);
        itens.save(item);
        assertThrows(IllegalArgumentException.class, () -> solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId()));
        assertIntacto(solicitacao);
    }

    @Test
    void validaExistenciaDeSolicitacaoEProduto() {
        Solicitacao solicitacao = criarSolicitacao();
        assertThrows(IllegalArgumentException.class, () -> solicitacaoService.aprovar(Integer.MAX_VALUE, funcionario.getId()));
        assertThrows(IllegalArgumentException.class, () -> solicitacaoService.rejeitar(Integer.MAX_VALUE));
        assertThrows(IllegalArgumentException.class,
                () -> solicitacaoService.adicionarItem(Integer.MAX_VALUE, produto.getId(), 1));
        assertThrows(IllegalArgumentException.class,
                () -> solicitacaoService.adicionarItem(solicitacao.getId(), Integer.MAX_VALUE, 1));
        assertEquals(0, itens.count());
    }

    @Test
    void rejeitaQuantidadesZeroNegativasENaoFinitas() {
        Solicitacao solicitacao = criarSolicitacao();
        for (double quantidade : new double[]{0, -1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class,
                    () -> solicitacaoService.adicionarItem(solicitacao.getId(), produto.getId(), quantidade));
            assertThrows(IllegalArgumentException.class, () -> estoqueService.saidaEstoque(
                    produto.getId(), almoxarifado.getId(), quantidade, funcionario.getId(), funcionario.getId()));
            assertThrows(IllegalArgumentException.class, () -> estoqueService.entradaEstoque(
                    produto.getId(), almoxarifado.getId(), quantidade, funcionario.getId(), funcionario.getId()));
        }
        assertIntacto(solicitacao);
        assertEquals(0, itens.count());
    }

    @Test
    void naoCadastraEstoqueDuplicado() {
        assertThrows(IllegalArgumentException.class, () -> estoqueService.cadastrar(novoEstoque(produto, 0)));
        assertEquals(1, estoques.count());
        assertEquals(10, saldo(produto));
    }

    @Test
    void constraintProtegeContraDuplicacaoDiretaNoRepository() {
        assertThrows(DataIntegrityViolationException.class, () -> estoques.saveAndFlush(novoEstoque(produto, 2)));
        assertEquals(1, estoques.count());
    }

    @Test
    void naoPermiteSobrescreverSaldoPeloCadastro() {
        Estoque estoque = novoEstoque(produto, 2);
        estoque.setId(estoques.findByProdutoIdAndAlmoxarifadoId(produto.getId(), almoxarifado.getId()).orElseThrow().getId());
        assertThrows(IllegalArgumentException.class, () -> estoqueService.cadastrar(estoque));
        assertEquals(10, saldo(produto));
    }

    @Test
    void aprovacaoConcorrenteNaoDebitaDuasVezes() throws Exception {
        Solicitacao solicitacao = solicitar(produto, 4);
        assertEquals(1, executarConcorrentes(
                () -> solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId()),
                () -> solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId())));
        assertEquals(10, saldo(produto));
        assertEquals(0, movimentacoes.count());
        assertEquals(StatusSolicitacao.APROVADA, status(solicitacao));
    }

    @Test
    void solicitacoesConcorrentesNaoConsomemMaisQueOSaldo() throws Exception {
        Solicitacao primeira = solicitar(produto, 6);
        Solicitacao segunda = solicitar(produto, 6);
        solicitacaoService.aprovar(primeira.getId(), funcionario.getId());
        solicitacaoService.aprovar(segunda.getId(), funcionario.getId());
        atendimentosService.iniciarSeparacao(primeira.getId(), funcionario.getId());
        atendimentosService.iniciarSeparacao(segunda.getId(), funcionario.getId());
        assertEquals(1, executarConcorrentes(
                () -> atendimentosService.atender(primeira.getId(), atendimentoTotal(primeira), UUID.randomUUID().toString()),
                () -> atendimentosService.atender(segunda.getId(), atendimentoTotal(segunda), UUID.randomUUID().toString())));
        assertEquals(4, saldo(produto));
        assertEquals(1, movimentacoes.count());
        assertEquals(1, solicitacoes.findAll().stream().filter(s -> s.getStatus() == StatusSolicitacao.ATENDIDA).count());
    }

    @Test
    void cadastroConcorrenteNaoCriaEstoqueDuplicado() throws Exception {
        Produto outro = novoProduto("Novo");
        assertEquals(1, executarConcorrentes(
                () -> estoqueService.cadastrar(novoEstoque(outro, 0)),
                () -> estoqueService.cadastrar(novoEstoque(outro, 0))));
        assertEquals(2, estoques.count());
    }

    private int executarConcorrentes(Runnable primeira, Runnable segunda) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch iniciar = new CountDownLatch(1);
        try {
            Future<Boolean> a = executor.submit(() -> executar(iniciar, primeira));
            Future<Boolean> b = executor.submit(() -> executar(iniciar, segunda));
            iniciar.countDown();
            return (a.get(20, TimeUnit.SECONDS) ? 1 : 0) + (b.get(20, TimeUnit.SECONDS) ? 1 : 0);
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    @Test
    void aprovacaoRegistraResponsavelSemCriarMovimentacao() {
        Funcionario aprovador = new Funcionario();
        aprovador.setNome("Aprovador");
        aprovador.setMatricula("BES-002");
        aprovador = funcionarios.save(aprovador);
        Solicitacao solicitacao = solicitar(produto, 4);
        Solicitacao resultado = solicitacaoService.aprovar(solicitacao.getId(), aprovador.getId());
        assertEquals(aprovador.getId(), resultado.getResponsavelAprovacao().getId());
        assertNotNull(resultado.getDataAprovacao());
        assertEquals(0, movimentacoes.count());
    }

    @Test
    void responsavelInexistenteNaoAlteraEstoqueNemStatus() {
        Solicitacao solicitacao = solicitar(produto, 4);
        assertThrows(br.com.almoxarifado.exception.RecursoNaoEncontradoException.class,
                () -> solicitacaoService.aprovar(solicitacao.getId(), Integer.MAX_VALUE));
        assertIntacto(solicitacao);
    }

    @Test
    void responsavelNuloNaoAlteraEstoqueNemStatus() {
        Solicitacao solicitacao = solicitar(produto, 4);
        assertThrows(IllegalArgumentException.class, () -> solicitacaoService.aprovar(solicitacao.getId(), null));
        assertIntacto(solicitacao);
    }

    @Test
    void consultaMovimentacoesDaSolicitacaoSemMisturarOutras() {
        Solicitacao primeira = solicitar(produto, 2);
        Solicitacao segunda = solicitar(produto, 3);
        solicitacaoService.aprovar(primeira.getId(), funcionario.getId());
        solicitacaoService.aprovar(segunda.getId(), funcionario.getId());
        atenderTudo(primeira);
        atenderTudo(segunda);
        assertEquals(1, solicitacaoService.consultarMovimentacoes(primeira.getId()).size());
        assertEquals(2, solicitacaoService.consultarMovimentacoes(primeira.getId()).get(0).getQuantidade());
        assertEquals(1, movimentacaoService.consultarPorSolicitacao(segunda.getId()).size());
        assertEquals(segunda.getId(), movimentacaoService.consultarPorSolicitacao(segunda.getId()).get(0).getSolicitacaoId());
    }

    @Test
    void estoqueNovoIniciaZeradoSemMovimentacao() {
        Produto outro = novoProduto("Novo");
        Estoque criado = estoqueService.cadastrar(novoEstoque(outro, 0));
        assertEquals(0, criado.getQuantidade());
        assertEquals(0, saldo(outro));
        assertEquals(0, movimentacoes.count());
    }

    @Test
    void cadastroRejeitaSaldoArbitrarioSemPersistir() {
        Produto outro = novoProduto("Novo");
        for (double quantidade : new double[]{1, -1, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> estoqueService.cadastrar(novoEstoque(outro, quantidade)));
        }
        assertFalse(estoques.existsByProdutoIdAndAlmoxarifadoId(outro.getId(), almoxarifado.getId()));
        assertEquals(0, movimentacoes.count());
    }

    @Test
    void entradaManualGeraMovimentacaoSemSolicitacao() {
        estoqueService.entradaEstoque(produto.getId(), almoxarifado.getId(), 3,
                funcionario.getId(), funcionario.getId());
        Movimentacao m = movimentacoes.findAll().get(0);
        assertEquals(TipoMovimentacao.ENTRADA, m.getTipo());
        assertEquals(3, m.getQuantidade());
        assertEquals(10, m.getSaldoAnterior());
        assertEquals(13, m.getSaldoPosterior());
        assertEquals(13, saldo(produto));
        assertNull(m.getSolicitacaoId());
        assertEquals(funcionario.getId(), m.getResponsavel().getId());
    }

    @Test
    void saidaManualGeraMovimentacaoSemSolicitacao() {
        estoqueService.saidaEstoque(produto.getId(), almoxarifado.getId(), 3,
                funcionario.getId(), funcionario.getId());
        Movimentacao m = movimentacoes.findAll().get(0);
        assertEquals(TipoMovimentacao.SAIDA, m.getTipo());
        assertEquals(3, m.getQuantidade());
        assertEquals(10, m.getSaldoAnterior());
        assertEquals(7, m.getSaldoPosterior());
        assertEquals(7, saldo(produto));
        assertNull(m.getSolicitacaoId());
    }

    @Test
    void filtrosDeMovimentacaoRespeitamProdutoAlmoxarifadoETipo() {
        Solicitacao solicitacao = solicitar(produto, 2);
        solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId());
        atenderTudo(solicitacao);
        estoqueService.entradaEstoque(produto.getId(), almoxarifado.getId(), 1,
                funcionario.getId(), funcionario.getId());
        assertEquals(2, movimentacaoService.consultarPorProduto(produto.getId()).size());
        assertEquals(2, movimentacaoService.consultarPorAlmoxarifado(almoxarifado.getId()).size());
        assertEquals(1, movimentacaoService.consultarPorTipo(TipoMovimentacao.ENTRADA).size());
        assertEquals(1, movimentacaoService.consultarPorTipo(TipoMovimentacao.SAIDA).size());
        Produto semHistorico = novoProduto("Sem histórico");
        assertTrue(movimentacaoService.consultarPorProduto(semHistorico.getId()).isEmpty());
    }

    @Test
    void filtrosDeEstoqueSelecionamSomenteOParEsperado() {
        Produto outro = novoProduto("Outro");
        estoqueService.cadastrar(novoEstoque(outro, 0));
        Almoxarifado local = new Almoxarifado();
        local.setNome("Secundário");
        local = almoxarifados.save(local);
        Estoque secundario = novoEstoque(produto, 0);
        secundario.setAlmoxarifado(local);
        estoqueService.cadastrar(secundario);
        assertEquals(2, estoqueService.consultarPorProduto(produto.getId()).size());
        assertEquals(2, estoqueService.consultarPorAlmoxarifado(almoxarifado.getId()).size());
        assertEquals(10, estoqueService.consultarPorProdutoEAlmoxarifado(produto.getId(), almoxarifado.getId()).getQuantidade());
        assertEquals(0, estoqueService.consultarPorProdutoEAlmoxarifado(produto.getId(), local.getId()).getQuantidade());
    }

    @Test
    void filtrosDeSolicitacaoRespeitamStatusEFuncionario() {
        Solicitacao aprovada = solicitar(produto, 2);
        solicitacaoService.aprovar(aprovada.getId(), funcionario.getId());
        criarSolicitacao();
        Funcionario outro = new Funcionario();
        outro.setNome("Outro");
        outro.setMatricula("BES-002");
        outro = funcionarios.save(outro);
        Solicitacao rejeitada = solicitacaoService.cadastrar(outro.getId(), almoxarifado.getId());
        solicitacaoService.rejeitar(rejeitada.getId());
        assertEquals(1, solicitacaoService.consultarPorStatus(StatusSolicitacao.APROVADA).size());
        assertEquals(1, solicitacaoService.consultarPorStatus(StatusSolicitacao.PENDENTE).size());
        assertEquals(1, solicitacaoService.consultarPorStatus(StatusSolicitacao.REJEITADA).size());
        assertEquals(2, solicitacaoService.consultarPorFuncionario(funcionario.getId()).size());
        assertEquals(rejeitada.getId(), solicitacaoService.consultarPorFuncionario(outro.getId()).get(0).getId());
    }

    private AtendimentoInput atendimentoTotal(Solicitacao s) {
        return new AtendimentoInput(funcionario.getId(), itens.findBySolicitacaoId(s.getId()).stream()
                .map(i -> new AtendimentoInput.Item(i.getId(), i.getQuantidade())).toList());
    }

    private void atenderTudo(Solicitacao s) {
        atendimentosService.iniciarSeparacao(s.getId(), funcionario.getId());
        atendimentosService.atender(s.getId(), atendimentoTotal(s), UUID.randomUUID().toString());
    }

    private boolean executar(CountDownLatch iniciar, Runnable acao) throws InterruptedException {
        iniciar.await();
        try {
            acao.run();
            return true;
        } catch (IllegalArgumentException esperada) {
            return false;
        }
    }

    private void prepararSaldo(Produto produto, double quantidade) {
        estoqueService.cadastrar(novoEstoque(produto, 0));
        estoqueService.entradaEstoque(produto.getId(), almoxarifado.getId(), quantidade,
                funcionario.getId(), funcionario.getId());
        // Somente H2: limpar movimentações da fixture para preservar os cenários anteriores.
        movimentacoes.deleteAll();
    }

    private Produto novoProduto(String nome) {
        Produto novo = new Produto();
        novo.setNome(nome);
        return produtos.save(novo);
    }

    private Estoque novoEstoque(Produto produto, double quantidade) {
        Estoque novo = new Estoque();
        novo.setProduto(produto);
        novo.setAlmoxarifado(almoxarifado);
        novo.setQuantidade(quantidade);
        return novo;
    }

    private Solicitacao criarSolicitacao() {
        return solicitacaoService.cadastrar(funcionario.getId(), almoxarifado.getId());
    }

    private Solicitacao solicitar(Produto produto, double quantidade) {
        Solicitacao solicitacao = criarSolicitacao();
        solicitacaoService.adicionarItem(solicitacao.getId(), produto.getId(), quantidade);
        return solicitacao;
    }

    private double saldo(Produto produto) {
        return estoques.findByProdutoIdAndAlmoxarifadoId(produto.getId(), almoxarifado.getId()).orElseThrow().getQuantidade();
    }

    private StatusSolicitacao status(Solicitacao solicitacao) {
        return solicitacoes.findById(solicitacao.getId()).orElseThrow().getStatus();
    }

    private void assertIntacto(Solicitacao solicitacao) {
        assertEquals(StatusSolicitacao.PENDENTE, status(solicitacao));
        assertEquals(10, saldo(produto));
        assertEquals(0, movimentacoes.count());
    }
}
