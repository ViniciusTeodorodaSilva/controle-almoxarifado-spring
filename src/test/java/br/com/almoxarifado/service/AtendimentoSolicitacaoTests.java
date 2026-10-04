package br.com.almoxarifado.service;

import br.com.almoxarifado.dto.*;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.argThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties="spring.datasource.url=jdbc:h2:mem:bes-bloco3;MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000")
@ActiveProfiles("test") @AutoConfigureMockMvc
class AtendimentoSolicitacaoTests {
    @Autowired AtendimentoSolicitacaoService service;
    @Autowired NecessidadeCompraService compras;
    @Autowired SolicitacaoService solicitacaoService;
    @Autowired EstoqueService estoqueService;
    @Autowired TransferenciaEstoqueService transferencias;
    @Autowired TransferenciaEstoqueRepository transfers;
    @Autowired ItemTransferenciaRepository transferItems;
    @MockitoSpyBean SolicitacaoRepository solicitacoes;
    @MockitoSpyBean ItemSolicitacaoRepository itens;
    @Autowired org.springframework.transaction.support.TransactionTemplate transacao;
    @Autowired EstoqueRepository estoques;
    @Autowired ProdutoRepository produtos;
    @Autowired FuncionarioRepository pessoas;
    @Autowired AlmoxarifadoRepository locais;
    @Autowired MovimentacaoRepository movimentos;
    @Autowired AtendimentoSolicitacaoRepository atendimentos;
    @Autowired ItemAtendimentoSolicitacaoRepository detalhes;
    @Autowired NecessidadeCompraRepository necessidades;
    @Autowired UnidadeMedidaRepository unidades;
    @Autowired MockMvc mvc;
    Produto produto; Funcionario pessoa; Almoxarifado local; Solicitacao s; ItemSolicitacao item;
    @BeforeEach void preparar() {
        movimentos.deleteAll(); transferItems.deleteAll(); transfers.deleteAll(); necessidades.deleteAll(); detalhes.deleteAll(); atendimentos.deleteAll();
        itens.deleteAll(); solicitacoes.deleteAll(); estoques.deleteAll(); produtos.deleteAll(); unidades.deleteAll(); pessoas.deleteAll(); locais.deleteAll();
        produto=new Produto();produto.setNome("Material");produto=produtos.save(produto);
        pessoa=new Funcionario();pessoa.setNome("Operador");pessoa.setMatricula("B3");pessoa=pessoas.save(pessoa);
        local=new Almoxarifado();local.setNome("Central");local=locais.save(local);
        Estoque e=new Estoque();e.setProduto(produto);e.setAlmoxarifado(local);e.setQuantidade(10);estoques.save(e);
        s=solicitacaoService.cadastrar(pessoa.getId(),local.getId());item=solicitacaoService.adicionarItem(s.getId(),produto.getId(),8);
    }
    String chave(){return UUID.randomUUID().toString();}
    AtendimentoInput input(double q){return new AtendimentoInput(pessoa.getId(),List.of(new AtendimentoInput.Item(item.getId(),q)));}
    void aprovar(){solicitacaoService.aprovar(s.getId(),pessoa.getId());}
    void separar(){aprovar();service.iniciarSeparacao(s.getId(),pessoa.getId());}
    double saldo(){return estoques.findByProdutoIdAndAlmoxarifadoId(produto.getId(),local.getId()).orElseThrow().getQuantidade();}
    void saldo(double q){var e=estoques.findAll().get(0);e.setQuantidade(q);estoques.save(e);}
    OperacaoSolicitacaoResponse view(){return service.operacao(s.getId());}
    NecessidadeCompraInput necessidade(){return new NecessidadeCompraInput(item.getId(),pessoa.getId());}
    void intacto(){assertEquals(10,saldo());assertEquals(0,atendimentos.count());assertEquals(0,detalhes.count());assertEquals(0,movimentos.count());}
    @Test void aprovacaoApenasAutoriza(){aprovar();intacto();assertEquals("APROVADA",view().status());assertEquals(pessoa.getId(),view().responsavelAprovacao().id());assertNotNull(view().dataAprovacao());}
    @Test void aprovaDemandaSemSaldo(){saldo(0);aprovar();assertEquals("APROVADA",view().status());assertEquals(8,view().itens().get(0).quantidadeFaltante());}
    @Test void separacaoRegistraResponsavelSemMovimento(){separar();intacto();assertEquals("EM_SEPARACAO",view().status());assertEquals(pessoa.getId(),view().responsavelSeparacao().id());assertNotNull(view().dataSeparacao());}
    @Test void naoSeparaPendente(){assertThrows(IllegalArgumentException.class,()->service.iniciarSeparacao(s.getId(),pessoa.getId()));intacto();}
    @Test void naoSeparaDuasVezes(){separar();assertThrows(IllegalArgumentException.class,()->service.iniciarSeparacao(s.getId(),pessoa.getId()));intacto();}
    @Test void naoAtendeAntesDeSeparar(){aprovar();assertThrows(IllegalArgumentException.class,()->service.atender(s.getId(),input(2),chave()));intacto();}
    @Test void atendimentoParcialMantemPendente(){separar();service.atender(s.getId(),input(3),chave());assertEquals("PARCIALMENTE_ATENDIDA",view().status());assertEquals(3,view().itens().get(0).quantidadeAtendida());assertEquals(5,view().itens().get(0).quantidadePendente());assertEquals(7,saldo());}
    @Test void multiplosAtendimentosConcluemDemanda(){separar();service.atender(s.getId(),input(3),chave());service.atender(s.getId(),input(5),chave());assertEquals("ATENDIDA",view().status());assertEquals(0,view().itens().get(0).quantidadePendente());assertEquals(2,saldo());assertEquals(2,service.historico(s.getId()).size());}
    @Test void atendimentoNaoRetiraTudoAutomaticamente(){separar();service.atender(s.getId(),input(1),chave());assertEquals(9,saldo());assertEquals(7,view().itens().get(0).quantidadePendente());}
    @Test void movimentoRastreiaItemAtendimentoResponsavelESaldos(){separar();var a=service.atender(s.getId(),input(3),chave());var m=movimentos.findAll().get(0);assertEquals(a.id(),m.getAtendimentoId());assertEquals(s.getId(),m.getSolicitacaoId());assertEquals(pessoa.getId(),m.getResponsavel().getId());assertEquals(10,m.getSaldoAnterior());assertEquals(7,m.getSaldoPosterior());assertEquals(a.dataHora(),m.getDataHora());assertEquals(item.getId(),a.itens().get(0).itemSolicitacaoId());}
    @Test void rejeitaQuantidadeMaiorQuePendente(){separar();assertThrows(IllegalArgumentException.class,()->service.atender(s.getId(),input(9),chave()));intacto();}
    @Test void rejeitaQuantidadeMaiorQueSaldo(){saldo(2);separar();assertThrows(IllegalArgumentException.class,()->service.atender(s.getId(),input(3),chave()));assertEquals(2,saldo());assertEquals(0,atendimentos.count());}
    @Test void rejeitaQuantidadesInvalidas(){separar();for(double q:new double[]{0,-1,Double.NaN,Double.POSITIVE_INFINITY})assertThrows(IllegalArgumentException.class,()->service.atender(s.getId(),input(q),chave()));intacto();}
    @Test void rejeitaListaVazia(){separar();assertThrows(IllegalArgumentException.class,()->service.atender(s.getId(),new AtendimentoInput(pessoa.getId(),List.of()),chave()));intacto();}
    @Test void rejeitaItemDeOutraSolicitacao(){separar();var outra=solicitacaoService.cadastrar(pessoa.getId(),local.getId());var i=solicitacaoService.adicionarItem(outra.getId(),produto.getId(),1);assertThrows(IllegalArgumentException.class,()->service.atender(s.getId(),new AtendimentoInput(pessoa.getId(),List.of(new AtendimentoInput.Item(i.getId(),1.0))),chave()));intacto();}
    @Test void rejeitaItemRepetidoNoPayload(){separar();var i=input(1).itens().get(0);assertThrows(IllegalArgumentException.class,()->service.atender(s.getId(),new AtendimentoInput(pessoa.getId(),List.of(i,i)),chave()));intacto();}
    @Test void rejeitaResponsavelInexistente(){separar();assertThrows(IllegalArgumentException.class,()->service.atender(s.getId(),new AtendimentoInput(Integer.MAX_VALUE,input(1).itens()),chave()));intacto();}
    @Test void rejeitaProdutoInativo(){separar();produto.setAtivo(false);produtos.save(produto);assertThrows(IllegalArgumentException.class,()->service.atender(s.getId(),input(1),chave()));intacto();}
    @Test void validaUnidadeNaoFracionaria(){UnidadeMedida u=new UnidadeMedida();u.setNome("Unidade");u.setSigla("UN");u.setPermiteFracionamento(false);u=unidades.save(u);produto.setUnidadeMedidaConfigurada(u);produtos.save(produto);separar();assertThrows(IllegalArgumentException.class,()->service.atender(s.getId(),input(0.5),chave()));intacto();}
    @Test void idempotenciaRetornaOriginalMesmoAposConclusao(){separar();String k=chave();var a=service.atender(s.getId(),input(8),k);assertEquals(a,service.atender(s.getId(),input(8),k));assertEquals(2,saldo());assertEquals(1,atendimentos.count());assertEquals(1,movimentos.count());}
    @Test void mesmaChaveComOutroPayloadRetornaConflito(){separar();String k=chave();service.atender(s.getId(),input(2),k);assertThrows(IllegalArgumentException.class,()->service.atender(s.getId(),input(3),k));assertEquals(8,saldo());assertEquals(1,atendimentos.count());}
    @Test void chaveInvalidaNaoPersiste(){separar();assertThrows(IllegalArgumentException.class,()->service.atender(s.getId(),input(1),"curta"));intacto();}
    @Test void rollbackFinalDesfazTudo(){separar();doThrow(new IllegalStateException("Falha final simulada")).when(solicitacoes).save(argThat(x->x!=null&&x.getStatus()==StatusSolicitacao.PARCIALMENTE_ATENDIDA));assertThrows(IllegalStateException.class,()->service.atender(s.getId(),input(3),chave()));intacto();assertEquals(0,view().itens().get(0).quantidadeAtendida());assertEquals("EM_SEPARACAO",view().status());}
    @Test void faltaNaoEIgualAoPendente(){saldo(5);aprovar();assertEquals(8,view().itens().get(0).quantidadePendente());assertEquals(3,view().itens().get(0).quantidadeFaltante());assertEquals(1,service.faltas(s.getId()).size());}
    @Test void necessidadeUsaFaltaAtualERastreiaContexto(){saldo(3);aprovar();var n=compras.criar(necessidade(),chave());assertEquals(5,n.quantidade());assertEquals(item.getId(),n.itemSolicitacaoId());assertEquals(s.getId(),n.solicitacaoId());assertEquals(produto.getId(),n.produto().id());assertEquals(local.getId(),n.almoxarifado().id());assertEquals(pessoa.getId(),n.responsavel().id());assertEquals("ABERTA",n.status());assertEquals(0,movimentos.count());}
    @Test void naoGeraNecessidadeSemFalta(){aprovar();assertThrows(IllegalArgumentException.class,()->compras.criar(necessidade(),chave()));assertEquals(0,necessidades.count());}
    @Test void naoGeraNecessidadePendente(){saldo(0);assertThrows(IllegalArgumentException.class,()->compras.criar(necessidade(),chave()));assertEquals(0,necessidades.count());}
    @Test void necessidadeIdempotenteENaoDuplicadaAposReposicao(){saldo(3);aprovar();String k=chave();var n=compras.criar(necessidade(),k);saldo(10);assertEquals(n,compras.criar(necessidade(),k));assertEquals(n,compras.criar(necessidade(),chave()));assertEquals(1,necessidades.count());assertEquals("ABERTA",compras.buscar(n.id()).status());assertEquals(5,compras.buscar(n.id()).quantidade());}
    @Test void filtrosDeNecessidadeRespeitamContexto(){saldo(0);aprovar();compras.criar(necessidade(),chave());assertEquals(1,compras.listar(StatusNecessidadeCompra.ABERTA,produto.getId(),local.getId(),s.getId()).size());assertTrue(compras.listar(StatusNecessidadeCompra.CANCELADA,null,null,null).isEmpty());}
    @Test void faltasNaoContamSaldoDuasVezes(){solicitacaoService.adicionarItem(s.getId(),produto.getId(),7);aprovar();var v=view();assertEquals(0,v.itens().get(0).quantidadeFaltante());assertEquals(5,v.itens().get(1).quantidadeFaltante());assertEquals(2,v.itens().get(1).saldoDisponivel());}
    @Test void legadoIntegralNaoDebitaNovamente(){legado(8);assertEquals("ATENDIDA",view().status());assertEquals("APROVADA",view().statusRegistrado());assertEquals("RECONHECIDA",view().compatibilidadeLegada());assertThrows(IllegalArgumentException.class,()->service.atender(s.getId(),input(1),chave()));assertEquals(2,saldo());assertEquals(1,movimentos.count());assertEquals(0,atendimentos.count());assertEquals(StatusSolicitacao.APROVADA,solicitacoes.findById(s.getId()).orElseThrow().getStatus());}
    @Test void legadoParcialPodeAtenderApenasRestante(){legado(3);assertEquals(5,view().itens().get(0).quantidadePendente());service.atender(s.getId(),input(5),chave());assertEquals("ATENDIDA",view().status());assertEquals(2,saldo());assertEquals(2,movimentos.count());}
    @Test void legadoAgregadoAmbiguoBloqueia(){solicitacaoService.adicionarItem(s.getId(),produto.getId(),7);legado(3);assertEquals("INCONSISTENTE",view().compatibilidadeLegada());assertNull(view().itens().get(0).quantidadeAtendida());assertThrows(IllegalArgumentException.class,()->service.atender(s.getId(),input(1),chave()));assertEquals(7,saldo());}
    @Test void aprovacaoAntigaSemEvidenciaBloqueia(){s.setStatus(StatusSolicitacao.APROVADA);solicitacoes.save(s);item.setQuantidadeAtendida(null);itens.save(item);assertEquals("INCONSISTENTE",view().compatibilidadeLegada());assertThrows(IllegalArgumentException.class,()->service.iniciarSeparacao(s.getId(),pessoa.getId()));intacto();}
    @Test void concorrenciaMesmaChaveNaoRepeteSaida() throws Exception {separar();String k=chave();var resultados=concorrentes(()->service.atender(s.getId(),input(3),k),()->service.atender(s.getId(),input(3),k));assertEquals(resultados.get(0),resultados.get(1));assertEquals(7,saldo());assertEquals(1,atendimentos.count());assertEquals(1,movimentos.count());}
    @Test void concorrenciaNecessidadesCriaSomenteUma() throws Exception {saldo(0);aprovar();var resultados=concorrentes(()->compras.criar(necessidade(),chave()),()->compras.criar(necessidade(),chave()));assertEquals(resultados.get(0),resultados.get(1));assertEquals(1,necessidades.count());}
    @Test void contratosHttpEIdempotencia() throws Exception {separar();String k=chave();String json="{\"responsavelId\":"+pessoa.getId()+",\"itens\":[{\"itemSolicitacaoId\":"+item.getId()+",\"quantidade\":3}]}";for(int i=0;i<2;i++)mvc.perform(post("/solicitacoes/{id}/atendimentos",s.getId()).header("Idempotency-Key",k).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk()).andExpect(jsonPath("$.itens[0].quantidade").value(3));mvc.perform(get("/solicitacoes/{id}/operacao",s.getId())).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("PARCIALMENTE_ATENDIDA"));mvc.perform(get("/solicitacoes/{id}/atendimentos",s.getId())).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));mvc.perform(post("/solicitacoes/{id}/atendimentos",s.getId()).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());assertEquals(7,saldo());}
    @Test void endpointsAusentesRetornam404() throws Exception {mvc.perform(get("/necessidades-compra/2147483647")).andExpect(status().isNotFound());mvc.perform(get("/solicitacoes/2147483647/operacao")).andExpect(status().isNotFound());mvc.perform(get("/necessidades-compra").param("status","INVALIDO")).andExpect(status().isBadRequest());}
    @Test void rollbackMultiItemDesfazSaldosItensHistoricoEMovimentos(){
        var outro=solicitacaoService.adicionarItem(s.getId(),produto.getId(),4);separar();
        doThrow(new IllegalStateException("Falha final multi-item")).when(solicitacoes).save(argThat(x->x!=null&&x.getStatus()==StatusSolicitacao.PARCIALMENTE_ATENDIDA));
        var payload=new AtendimentoInput(pessoa.getId(),List.of(new AtendimentoInput.Item(item.getId(),3.0),new AtendimentoInput.Item(outro.getId(),1.0)));
        assertThrows(IllegalStateException.class,()->service.atender(s.getId(),payload,chave()));intacto();assertTrue(view().itens().stream().allMatch(i->i.quantidadeAtendida()==0));
    }
    @Test void fracionamentoDecimalConcluiSemResiduo(){item.setQuantidade(0.3);itens.save(item);saldo(0.3);separar();service.atender(s.getId(),input(0.1),chave());service.atender(s.getId(),input(0.2),chave());assertEquals(0,saldo());assertEquals("ATENDIDA",view().status());assertEquals(0,view().itens().get(0).quantidadePendente());}
    @Test void necessidadeSemEstoqueCadastrado(){estoques.deleteAll();aprovar();var n=compras.criar(necessidade(),chave());assertEquals(8,n.quantidade());assertFalse(view().itens().get(0).estoqueCadastrado());assertEquals(0,estoques.count());}
    @Test void atendimentoConcorrenteComSaidaManualNaoNegativa() throws Exception {separar();competir(()->service.atender(s.getId(),input(6),chave()),()->estoqueService.saidaEstoque(produto.getId(),local.getId(),6,pessoa.getId(),pessoa.getId()));assertEquals(4,saldo());assertEquals(1,movimentos.count());}
    @Test void atendimentoConcorrenteComTransferenciaNaoNegativa() throws Exception {separar();Almoxarifado destino=new Almoxarifado();destino.setNome("Destino");destino=locais.save(destino);Integer destinoId=destino.getId();var payload=new TransferenciaInput(local.getId(),destinoId,pessoa.getId(),null,List.of(new TransferenciaInput.Item(produto.getId(),6.0)));competir(()->service.atender(s.getId(),input(6),chave()),()->transferencias.criar(payload));assertEquals(4,saldo());assertEquals(1,movimentos.findAll().stream().filter(m->m.getTipo()==TipoMovimentacao.SAIDA).count());}
    void competir(Runnable a,Runnable b) throws Exception {var outcomes=concorrentes(()->tentar(a),()->tentar(b));assertEquals(1,outcomes.stream().filter(Boolean::booleanValue).count());}
    boolean tentar(Runnable acao){try{acao.run();return true;}catch(IllegalArgumentException e){return false;}}
    @Test void necessidadeLeItemAtualizadoDepoisDoLock() throws Exception {
        saldo(5);separar();var leuOrigem=new CountDownLatch(1);
        org.mockito.Mockito.doAnswer(invocation->{leuOrigem.countDown();return Optional.of(s.getId());}).when(itens).encontrarSolicitacaoId(item.getId());
        var pool=Executors.newSingleThreadExecutor();java.util.concurrent.atomic.AtomicReference<Future<NecessidadeCompraResponse>> future=new java.util.concurrent.atomic.AtomicReference<>();
        try {
            transacao.executeWithoutResult(tx->{
                solicitacoes.buscarParaAtualizacao(s.getId()).orElseThrow();
                future.set(pool.submit(()->compras.criar(necessidade(),chave())));
                try {assertTrue(leuOrigem.await(5,TimeUnit.SECONDS));} catch(InterruptedException e){Thread.currentThread().interrupt();throw new IllegalStateException(e);}
                service.atender(s.getId(),input(2),chave());
            });
            assertEquals(3,future.get().get(20,TimeUnit.SECONDS).quantidade());assertEquals(3,saldo());assertEquals(6,view().itens().get(0).quantidadePendente());
        } finally {pool.shutdownNow();assertTrue(pool.awaitTermination(5,TimeUnit.SECONDS));}
    }
    void legado(double q){s.setStatus(StatusSolicitacao.APROVADA);solicitacoes.save(s);item.setQuantidadeAtendida(null);itens.save(item);saldo(10-q);Movimentacao m=new Movimentacao();m.setSolicitacao(s);m.setProduto(produto);m.setAlmoxarifado(local);m.setResponsavel(pessoa);m.setSolicitante(pessoa);m.setTipo(TipoMovimentacao.SAIDA);m.setQuantidade(q);m.setSaldoAnterior(10);m.setSaldoPosterior(10-q);movimentos.save(m);}
    <T> List<T> concorrentes(Callable<T> a,Callable<T> b) throws Exception {var pool=Executors.newFixedThreadPool(2);var start=new CountDownLatch(1);try{var x=pool.submit(()->{start.await();return a.call();});var y=pool.submit(()->{start.await();return b.call();});start.countDown();return List.of(x.get(20,TimeUnit.SECONDS),y.get(20,TimeUnit.SECONDS));}finally{pool.shutdownNow();assertTrue(pool.awaitTermination(5,TimeUnit.SECONDS));}}
}
