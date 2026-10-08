package br.com.almoxarifado.epi;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import br.com.almoxarifado.compras.ComprasPage;
import br.com.almoxarifado.model.*;
import br.com.almoxarifado.repository.*;
import br.com.almoxarifado.service.*;
import br.com.almoxarifado.obras.*;
import br.com.almoxarifado.security.*;
import br.com.almoxarifado.exception.*;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:bes-epi;MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=15000","spring.jpa.properties.hibernate.generate_statistics=true","logging.level.root=WARN","debug=false"})
@ActiveProfiles("test") @AutoConfigureMockMvc
class EpiTests {
 @Autowired EpiService service;
 @Autowired EpiConfiguracaoRepository configs;
 @Autowired RegistroEpiRepository registros;
 @Autowired ItemEpiRepository itens;
 @Autowired ProdutoRepository produtos;
 @Autowired UnidadeMedidaRepository unidades;
 @Autowired FuncionarioRepository pessoas;
 @Autowired AlmoxarifadoRepository locais;
 @Autowired EstoqueRepository saldos;
 @Autowired EstoqueService estoque;
 @Autowired MovimentacaoRepository movimentos;
 @Autowired UsuarioRepository usuarios;
 @Autowired AuditoriaRepository eventos;
 @Autowired EstruturaService estrutura;
 @Autowired EntityManager em;
 @Autowired TransactionTemplate tx;
 @Autowired MockMvc mvc;
 @MockitoSpyBean AuditoriaService audit;
 @MockitoSpyBean EstoqueService stockSpy;
 Usuario user;Integer produto,person,responsavel,warehouse;
 @BeforeEach void setup(){user=usuarios.save(new Usuario("epi-"+key(),"fixture-hash","H2",Perfil.ADMIN));identity(Perfil.ADMIN);var p=new Funcionario();p.setNome("Recebedor H2");p.setMatricula(key());person=pessoas.save(p).getId();var r=new Funcionario();r.setNome("Responsavel H2");r.setMatricula(key());responsavel=pessoas.save(r).getId();var w=new Almoxarifado();w.setNome("Central H2");warehouse=locais.save(w).getId();produto=novoProduto(10);}
 @AfterEach void cleanup(){reset(audit,stockSpy);SecurityContextHolder.clearContext();}
 String key(){return UUID.randomUUID().toString();}
 void identity(Perfil perfil){if(user.getPerfil()!=perfil)user=usuarios.save(new Usuario("epi-"+key(),"fixture-hash","H2",perfil));SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(new Identidade(user.getId(),user.getUsername(),user.getAuthVersion(),System.currentTimeMillis()),null,perfil.permissoes().stream().map(p->new SimpleGrantedAuthority(p.name())).toList()));}
 Integer novoProduto(double saldo){var u=new UnidadeMedida();u.setNome("Unidade");u.setSigla("UN-"+key());u.setPermiteFracionamento(false);u=unidades.save(u);var p=new Produto();p.setNome("Capacete H2");p.setCodigo("EPI-"+key());p.setUnidadeMedidaConfigurada(u);p.setUnidadeMedida(u.getSigla());p=produtos.save(p);var e=new Estoque();e.setProduto(p);e.setAlmoxarifado(locais.findById(warehouse).orElseThrow());estoque.cadastrar(e);if(saldo>0)estoque.entradaEstoque(p.getId(),warehouse,saldo,person,responsavel);service.configurar(null,config(p.getId()));return p.getId();}
 EpiInput.Configuracao config(Integer p){var c=new EpiInput.Configuracao();c.produtoId=p;c.ca="12345";c.fabricante="Fabricante H2";c.modelo="Modelo H2";c.tamanho="M";c.validadeCa=LocalDate.now().plusDays(30);c.diasSubstituicao=90;c.ativo=true;c.exigeDevolucao=true;c.permiteRetorno=true;return c;}
 EpiInput.Linha linha(Integer p,String q){var l=new EpiInput.Linha();l.produtoId=p;l.quantidade=new BigDecimal(q);l.lote="Lote H2";l.fabricacao=LocalDate.now().minusDays(10);l.validadeFisica=LocalDate.now().plusDays(20);return l;}
 EpiInput.Entrega entrega(){var e=new EpiInput.Entrega();e.funcionarioId=person;e.responsavelId=responsavel;e.almoxarifadoId=warehouse;e.motivo=MotivoEntrega.INICIAL;e.recebimentoConfirmado=true;e.itens=List.of(linha(produto,"2"));return e;}
 @SuppressWarnings("unchecked") List<Map<String,Object>> linhas(Map<String,Object> r){return (List<Map<String,Object>>)r.get("itens");}
 Integer entregar(){return (Integer)linhas(service.entregar(entrega(),key())).get(0).get("id");}
 EpiInput.Fechamento fechamento(Integer id,DestinoEpi destino){var f=new EpiInput.Fechamento();f.responsavelId=responsavel;f.almoxarifadoId=warehouse;f.motivo="Conferencia H2";var l=new EpiInput.FechamentoLinha();l.origemItemId=id;l.quantidade=new BigDecimal("2");l.destino=destino;l.condicao=CondicaoEpi.NOVO;f.itens=List.of(l);return f;}
 EpiInput.Entrega substituir(Integer origem){var e=entrega();e.motivo=MotivoEntrega.SUBSTITUICAO;var l=e.itens.get(0);l.origemItemId=origem;l.quantidadeSubstituida=new BigDecimal("2");l.motivoSubstituicao="Desgaste constatado";l.condicaoAnterior=CondicaoEpi.USADO;l.destinoAnterior=DestinoEpi.SEGREGADO;return e;}
 double saldo(){return saldos.findByProdutoIdAndAlmoxarifadoId(produto,warehouse).orElseThrow().getQuantidade();}
 EpiService.Filtro filtro(){return new EpiService.Filtro(person,null,null,null,null,null,null,null,null,null);}
 ObrasInput.Contexto contexto(){var o=new ObrasInput.Obra();o.codigo="OB-"+key();o.nome="Projeto H2";var obra=estrutura.salvarObra(null,o);var c=new ObrasInput.Centro();c.codigo="CC-"+key();c.nome="Centro H2";c.tipo=TipoCentroCusto.OBRA;c.ativo=true;c.obraId=obra.getId();var cc=estrutura.salvarCentro(null,c);var s=new ObrasInput.Ordem();s.obraId=obra.getId();s.centroCustoId=cc.getId();s.titulo="Montagem";var os=estrutura.salvarOrdem(null,s);var ctx=new ObrasInput.Contexto();ctx.obraId=obra.getId();ctx.centroCustoId=cc.getId();ctx.ordemServicoId=os.getId();return ctx;}

 @Test void configuracaoUmParaUmSemSaldoParalelo(){assertEquals(produto,service.buscarConfig(produto).get("produtoId"));assertEquals(10,saldo());assertThrows(ConflitoException.class,()->service.configurar(null,config(produto)));}
 @ParameterizedTest @ValueSource(strings={"","CA123","123 45","<null>","123456789012345678901"}) void caInvalido(String ca){var c=config(produto);c.ca=ca;assertThrows(IllegalArgumentException.class,()->service.configurar(produto,c));}
 @Test void caComSaldoNaoMuda(){var c=config(produto);c.ca="67890";assertThrows(ConflitoException.class,()->service.configurar(produto,c));assertEquals("12345",service.buscarConfig(produto).get("ca"));}
 @Test void caModeloTamanhoHistoricosAposSaldoZerado(){var e=entrega();e.itens=List.of(linha(produto,"10"));var r=service.entregar(e,key());var c=config(produto);c.ca="67890";c.modelo="Novo modelo";c.tamanho="G";service.configurar(produto,c);var antigo=linhas(service.buscarRegistro((Integer)r.get("id"))).get(0);assertEquals("12345",antigo.get("ca"));assertEquals("Modelo H2",antigo.get("modelo"));assertEquals("M",antigo.get("tamanho"));}
 @Test void caVencidoNaoConfundeValidadeFisica(){var c=config(produto);c.validadeCa=LocalDate.now().minusDays(1);service.configurar(produto,c);var r=service.entregar(entrega(),key());assertTrue((Boolean)linhas(r).get(0).get("caVencido"));assertFalse((Boolean)linhas(r).get(0).get("vencido"));}
 @Test void itemFisicoVencidoBloqueia(){var e=entrega();e.itens.get(0).validadeFisica=LocalDate.now().minusDays(1);assertThrows(IllegalArgumentException.class,()->service.entregar(e,key()));assertEquals(10,saldo());}
 @Test void fabricacaoFuturaOuPosteriorAValidadeBloqueia(){var e=entrega();e.itens.get(0).fabricacao=LocalDate.now().plusDays(1);assertThrows(IllegalArgumentException.class,()->service.entregar(e,key()));e.itens.get(0).fabricacao=LocalDate.now().minusDays(1);e.itens.get(0).validadeFisica=LocalDate.now().minusDays(2);assertThrows(IllegalArgumentException.class,()->service.entregar(e,key()));}
 @ParameterizedTest @ValueSource(strings={"0","-1","0.5","0.0000001","99999999999999"}) void quantidadeInvalida(String q){var e=entrega();e.itens=List.of(linha(produto,q));assertThrows(IllegalArgumentException.class,()->service.entregar(e,key()));assertEquals(10,saldo());}
 @Test void entregaGeraSaidaESeparaAtorResponsavel(){var r=service.entregar(entrega(),key());assertEquals(8,saldo());assertEquals(user.getId(),r.get("atorId"));assertEquals(responsavel,r.get("responsavelId"));assertEquals(person,r.get("funcionarioId"));var i=linhas(r).get(0);var m=movimentos.findById((Integer)i.get("movimentoId")).orElseThrow();assertEquals(TipoMovimentacao.SAIDA,m.getTipo());assertEquals(10,m.getSaldoAnterior());assertEquals(8,m.getSaldoPosterior());assertEquals("2",i.get("quantidadeEmPosse"));assertTrue((Boolean)r.get("recebimentoConfirmado"));}
 @Test void multiplaEntregaAtomica(){var p=novoProduto(5);var e=entrega();e.itens=List.of(linha(p,"3"),linha(produto,"2"));var r=service.entregar(e,key());assertEquals(2,linhas(r).size());assertEquals(8,saldo());assertEquals(2,saldos.findByProdutoIdAndAlmoxarifadoId(p,warehouse).orElseThrow().getQuantidade());}
 @Test void saldoInsuficienteEmSegundoItemNaoGeraParcial(){var p=novoProduto(1);var e=entrega();e.itens=List.of(linha(produto,"2"),linha(p,"2"));long rs=registros.count(),ms=movimentos.count(),is=itens.count(),as=eventos.count();assertThrows(ConflitoException.class,()->service.entregar(e,key()));assertEquals(10,saldo());assertEquals(rs,registros.count());assertEquals(ms,movimentos.count());assertEquals(is,itens.count());assertEquals(as,eventos.count());}
 @Test void rejeitaProdutoDuplicado(){var e=entrega();e.itens=List.of(linha(produto,"1"),linha(produto,"1"));assertThrows(IllegalArgumentException.class,()->service.entregar(e,key()));}
 @Test void confirmaRecebimentoObrigatorio(){var e=entrega();e.recebimentoConfirmado=false;assertThrows(IllegalArgumentException.class,()->service.entregar(e,key()));}
 @Test void epiOuProdutoInativoBloqueia(){var c=config(produto);c.ativo=false;service.configurar(produto,c);assertThrows(ConflitoException.class,()->service.entregar(entrega(),key()));c.ativo=true;service.configurar(produto,c);var p=produtos.findById(produto).orElseThrow();p.setAtivo(false);produtos.save(p);assertThrows(ConflitoException.class,()->service.entregar(entrega(),key()));}
 @Test void idsInexistentesNaoGeramEntrega(){var e=entrega();e.funcionarioId=Integer.MAX_VALUE;assertThrows(RecursoNaoEncontradoException.class,()->service.entregar(e,key()));var finalE=entrega();finalE.itens.get(0).produtoId=Integer.MAX_VALUE;assertThrows(RecursoNaoEncontradoException.class,()->service.entregar(finalE,key()));assertEquals(10,saldo());}
 @Test void idempotenciaPersistidaNaoRepeteSaida(){String k=key();var r=service.entregar(entrega(),k);long ms=movimentos.count();assertEquals(r.get("id"),service.entregar(entrega(),k).get("id"));assertEquals(ms,movimentos.count());assertEquals(8,saldo());var e=entrega();e.itens.get(0).quantidade=new BigDecimal("3");assertThrows(ConflitoException.class,()->service.entregar(e,k));}
 @Test void hashNuloNaoColideComLiteral(){String k=key();service.entregar(entrega(),k);var e=entrega();e.observacao="<null>";assertThrows(ConflitoException.class,()->service.entregar(e,k));}
 @Test void quebraLinhaPreservada(){var e=entrega();e.observacao="Conferido\nRecebimento confirmado";var r=service.entregar(e,key());assertEquals(e.observacao,service.buscarRegistro((Integer)r.get("id")).get("observacao"));}
 @Test void devolucaoSegregadaNaoCredita(){var id=entregar();long ms=movimentos.count();service.fechar(fechamento(id,DestinoEpi.SEGREGADO),key(),false);assertEquals(8,saldo());assertEquals(ms,movimentos.count());assertEquals(0,service.posse(person,null,0,20).getTotalElements());}
 @Test void retornoExplicitoNovoCreditaEstoque(){var id=entregar();var r=service.fechar(fechamento(id,DestinoEpi.ESTOQUE),key(),false);assertEquals(10,saldo());var m=movimentos.findById((Integer)linhas(r).get(0).get("movimentoId")).orElseThrow();assertEquals(TipoMovimentacao.ENTRADA,m.getTipo());}
 @Test void retornoUsadoOuNaoPermitidoBloqueia(){var id=entregar();var f=fechamento(id,DestinoEpi.ESTOQUE);f.itens.get(0).condicao=CondicaoEpi.USADO;assertThrows(ConflitoException.class,()->service.fechar(f,key(),false));var c=config(produto);c.permiteRetorno=false;service.configurar(produto,c);assertThrows(ConflitoException.class,()->service.fechar(fechamento(id,DestinoEpi.ESTOQUE),key(),false));assertEquals(8,saldo());}
 @Test void retornoCaDiferenteBloqueia(){var e=entrega();e.itens=List.of(linha(produto,"10"));var id=(Integer)linhas(service.entregar(e,key())).get(0).get("id");var c=config(produto);c.ca="67890";service.configurar(produto,c);assertThrows(ConflitoException.class,()->service.fechar(fechamento(id,DestinoEpi.ESTOQUE),key(),false));}
 @Test void devolucaoDuplicadaEReplay(){var id=entregar();String k=key();var r=service.fechar(fechamento(id,DestinoEpi.ESTOQUE),k,false);assertEquals(r.get("id"),service.fechar(fechamento(id,DestinoEpi.ESTOQUE),k,false).get("id"));assertThrows(ConflitoException.class,()->service.fechar(fechamento(id,DestinoEpi.ESTOQUE),key(),false));assertEquals(10,saldo());}
 @Test void devolucaoParcialTemSaldoEmPosseCorreto(){var id=entregar();var f=fechamento(id,DestinoEpi.SEGREGADO);f.itens.get(0).quantidade=BigDecimal.ONE;service.fechar(f,key(),false);assertEquals("1",service.posse(person,null,0,20).getContent().get(0).get("quantidadeEmPosse"));service.fechar(f,key(),false);assertEquals(0,service.posse(person,null,0,20).getTotalElements());}
 @Test void fechamentoFracionarioDeUnidadeInteiraBloqueia(){var id=entregar();var f=fechamento(id,DestinoEpi.SEGREGADO);f.itens.get(0).quantidade=new BigDecimal("0.5");assertThrows(IllegalArgumentException.class,()->service.fechar(f,key(),false));}
 @Test void descarteEPerdaNaoCredita(){var id=entregar();var f=fechamento(id,DestinoEpi.PERDA);f.itens.get(0).condicao=CondicaoEpi.PERDIDO;var r=service.fechar(f,key(),true);assertEquals(TipoRegistroEpi.DESCARTE,r.get("tipo"));assertEquals(8,saldo());assertEquals(0,service.posse(person,null,0,20).getTotalElements());}
 @Test void substituicaoLigaOrigemENovoItemSemCreditarAntigo(){var id=entregar();var r=service.entregar(substituir(id),key());assertEquals(6,saldo());assertEquals(TipoRegistroEpi.SUBSTITUICAO,r.get("tipo"));assertEquals(2,linhas(r).size());assertTrue(linhas(r).stream().allMatch(i->id.equals(i.get("origemItemId"))));assertEquals(1,service.posse(person,null,0,20).getTotalElements());assertEquals(2,service.listarRegistros(filtro(),0,20).getTotalElements());}
 @Test void substituicaoSemOrigemOuDeOutroFuncionarioBloqueia(){var e=entrega();e.motivo=MotivoEntrega.SUBSTITUICAO;assertThrows(IllegalArgumentException.class,()->service.entregar(e,key()));var id=entregar();var other=new Funcionario();other.setNome("Outra pessoa");other.setMatricula(key());var sub=substituir(id);sub.funcionarioId=pessoas.save(other).getId();assertThrows(ConflitoException.class,()->service.entregar(sub,key()));}
 @Test void contextoCoerenteEHistoricoImutavel(){var e=entrega();e.contexto=contexto();var r=service.entregar(e,key());var ctx=(ContextoOperacional)r.get("contexto");assertEquals(e.contexto.obraId,ctx.getObraId());var movement=movimentos.findById((Integer)linhas(r).get(0).get("movimentoId")).orElseThrow();assertEquals(e.contexto.ordemServicoId,movement.getContexto().getOrdemServicoId());var p=produtos.findById(produto).orElseThrow();p.setNome("Nome novo");produtos.save(p);assertEquals("Capacete H2",linhas(service.buscarRegistro((Integer)r.get("id"))).get(0).get("produtoNome"));}
 @Test void contextoIncompativelBloqueia(){var e=entrega();e.contexto=contexto();e.contexto.centroCustoId=contexto().centroCustoId;assertThrows(ConflitoException.class,()->service.entregar(e,key()));assertEquals(10,saldo());}
 @Test void contextoNuloNaoCriaObra(){var r=service.entregar(entrega(),key());assertNull(r.get("contexto"));assertNull(linhas(r).get(0).get("contexto"));}
 @Test void filtrosPreservamTodasLinhasEDistinguemCa(){var p=novoProduto(5);var e=entrega();e.itens=List.of(linha(produto,"2"),linha(p,"1"));service.entregar(e,key());var f=new EpiService.Filtro(person,produto,null,null,null,warehouse,"12345",TipoRegistroEpi.ENTREGA,null,null);assertEquals(2,linhas(service.listarRegistros(f,0,20).getContent().get(0)).size());assertEquals(0,service.listarRegistros(new EpiService.Filtro(person,produto,null,null,null,null,"999",null,null,null),0,20).getTotalElements());}
 @Test void fichaEAlertasNaoDoubleCount(){var e=entrega();service.entregar(e,key());service.entregar(e,key());assertEquals(2,service.posse(person,"A_VENCER",0,20).getTotalElements());assertEquals(2,((ComprasPage<?>)service.ficha(person,0,20).get("posse")).totalElements());}
 @Test void rollbackAuditoriaEntrega(){long rs=registros.count(),ms=movimentos.count(),is=itens.count();String k=key();tx.executeWithoutResult(x->doThrow(new IllegalStateException("falha H2")).when(audit).registrar(eq("EPI_ENTREGA"),any(),any(),any(),any(),any()));assertThrows(IllegalStateException.class,()->service.entregar(entrega(),k));assertEquals(10,saldo());assertEquals(rs,registros.count());assertEquals(ms,movimentos.count());assertEquals(is,itens.count());assertTrue(registros.findByChaveIdempotencia(k).isEmpty());}
 @ParameterizedTest @ValueSource(booleans={false,true}) void rollbackAuditoriaFechamento(boolean descarte){var id=entregar();long rs=registros.count(),ms=movimentos.count();String k=key();tx.executeWithoutResult(x->doThrow(new IllegalStateException("falha H2")).when(audit).registrar(eq(descarte?"EPI_DESCARTE":"EPI_DEVOLUCAO"),any(),any(),any(),any(),any()));assertThrows(IllegalStateException.class,()->service.fechar(fechamento(id,descarte?DestinoEpi.DESCARTE:DestinoEpi.ESTOQUE),k,descarte));assertEquals(8,saldo());assertEquals(rs,registros.count());assertEquals(ms,movimentos.count());assertEquals(1,service.posse(person,null,0,20).getTotalElements());}
 @Test void rollbackFalhaAposPrimeiraSaida(){var p=novoProduto(5);var e=entrega();e.itens=List.of(linha(produto,"1"),linha(p,"1"));long rs=registros.count(),ms=movimentos.count();doThrow(new IllegalStateException("falha segundo item")).when(stockSpy).movimentarEpi(eq(p),any(),anyDouble(),any(),any(),any(),any());assertThrows(IllegalStateException.class,()->service.entregar(e,key()));assertEquals(10,saldo());assertEquals(rs,registros.count());assertEquals(ms,movimentos.count());}
 private List<Object> simultaneos(Callable<Object> a,Callable<Object> b)throws Exception{var pool=Executors.newFixedThreadPool(2);var start=new CountDownLatch(1);try{Callable<Object> aa=()->{identity(Perfil.ADMIN);start.await();try{return a.call();}catch(Exception x){return x;}finally{SecurityContextHolder.clearContext();}};Callable<Object> bb=()->{identity(Perfil.ADMIN);start.await();try{return b.call();}catch(Exception x){return x;}finally{SecurityContextHolder.clearContext();}};var fa=pool.submit(aa);var fb=pool.submit(bb);start.countDown();return List.of(fa.get(25,TimeUnit.SECONDS),fb.get(25,TimeUnit.SECONDS));}finally{pool.shutdownNow();}}
 @Test void ultimoSaldoConcorrenteUmVencedor()throws Exception{var e=entrega();e.itens=List.of(linha(produto,"10"));var results=simultaneos(()->service.entregar(e,key()),()->service.entregar(e,key()));assertEquals(1,results.stream().filter(Map.class::isInstance).count());assertEquals(0,saldo());assertEquals(1,results.stream().filter(ConflitoException.class::isInstance).count());}
 @Test void chaveConcorrenteMesmoComandoUmRegistro()throws Exception{String k=key();var results=simultaneos(()->service.entregar(entrega(),k),()->service.entregar(entrega(),k));assertTrue(results.stream().allMatch(Map.class::isInstance));assertEquals(((Map<?,?>)results.get(0)).get("id"),((Map<?,?>)results.get(1)).get("id"));assertEquals(8,saldo());}
 @Test void duplaDevolucaoConcorrenteUmVencedor()throws Exception{var id=entregar();var results=simultaneos(()->service.fechar(fechamento(id,DestinoEpi.ESTOQUE),key(),false),()->service.fechar(fechamento(id,DestinoEpi.ESTOQUE),key(),false));assertEquals(1,results.stream().filter(Map.class::isInstance).count());assertEquals(10,saldo());}
 @ParameterizedTest @EnumSource(Perfil.class) void matrizService(Perfil perfil){identity(perfil);assertDoesNotThrow(()->service.buscarConfig(produto));assertDoesNotThrow(()->service.ficha(person,0,20));if(perfil==Perfil.CONSULTA){assertThrows(org.springframework.security.access.AccessDeniedException.class,()->service.entregar(entrega(),key()));assertThrows(org.springframework.security.access.AccessDeniedException.class,()->service.configurar(produto,config(produto)));}else {assertDoesNotThrow(()->service.entregar(entrega(),key()));if(perfil==Perfil.ALMOXARIFE)assertThrows(org.springframework.security.access.AccessDeniedException.class,()->service.configurar(produto,config(produto)));}}
 @Test void consultasSemNMaisUm(){service.entregar(entrega(),key());var stats=em.getEntityManagerFactory().unwrap(org.hibernate.SessionFactory.class).getStatistics();stats.clear();service.listarRegistros(filtro(),0,100);long antes=stats.getPrepareStatementCount();for(int j=0;j<3;j++)service.entregar(entrega(),key());stats.clear();service.listarRegistros(filtro(),0,100);assertTrue(stats.getPrepareStatementCount()<=antes+1);stats.clear();service.listarConfigs(null,null,null,warehouse,0,100);assertTrue(stats.getPrepareStatementCount()<=6);stats.clear();service.resumo();long inicial=stats.getPrepareStatementCount();service.entregar(entrega(),key());stats.clear();service.resumo();assertEquals(inicial,stats.getPrepareStatementCount());}
 @ParameterizedTest @ValueSource(strings={"/epis","/epi-entregas","/epi-entregas/posse","/epi-entregas/resumo"}) void anonimoNaoLe(String path)throws Exception{SecurityContextHolder.clearContext();mvc.perform(get(path)).andExpect(status().isUnauthorized());}
 @ParameterizedTest @ValueSource(strings={"/epis","/epi-entregas","/epi-devolucoes","/epi-descartes"}) void escritasExigemCsrf(String path)throws Exception{mvc.perform(post(path).with(authentication(SecurityContextHolder.getContext().getAuthentication())).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());}
 @Test void putExigeCsrf()throws Exception{mvc.perform(put("/epis/"+produto).with(authentication(SecurityContextHolder.getContext().getAuthentication())).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());}
 @ParameterizedTest @ValueSource(strings={"/epis","/epi-entregas","/epi-devolucoes","/epi-descartes"}) void atorForjadoRejeitado(String path)throws Exception{mvc.perform(post(path).with(authentication(SecurityContextHolder.getContext().getAuthentication())).with(csrf()).header("Idempotency-Key",key()).contentType(MediaType.APPLICATION_JSON).content("{\"atorId\":999,\"status\":\"APROVADO\",\"saldo\":100}")).andExpect(status().isBadRequest());}
 @Test void semAuthorityNemIdAutoriza()throws Exception{var a=new UsernamePasswordAuthenticationToken("fixture",null,List.of());for(String path:List.of("/epis/"+produto,"/epi-entregas/1","/epi-funcionarios/"+person))mvc.perform(get(path).with(authentication(a))).andExpect(status().isForbidden());}

 @Test void substituicaoExigeCondicaoEDestinoExplicitos(){var id=entregar();var e=substituir(id);e.itens.get(0).condicaoAnterior=null;assertThrows(IllegalArgumentException.class,()->service.entregar(e,key()));e.itens.get(0).condicaoAnterior=CondicaoEpi.USADO;e.itens.get(0).destinoAnterior=null;assertThrows(IllegalArgumentException.class,()->service.entregar(e,key()));assertEquals(8,saldo());assertEquals(1,service.posse(person,null,0,20).getTotalElements());}
 @Test void substituicaoNaoPodeCreditarOrigem(){var id=entregar();var e=substituir(id);e.itens.get(0).destinoAnterior=DestinoEpi.ESTOQUE;assertThrows(IllegalArgumentException.class,()->service.entregar(e,key()));assertEquals(8,saldo());}
 @Test void rollbackSubstituicaoPreservaOrigem(){var id=entregar();long rs=registros.count(),ms=movimentos.count();tx.executeWithoutResult(x->doThrow(new IllegalStateException("falha H2")).when(audit).registrar(eq("EPI_SUBSTITUICAO"),any(),any(),any(),any(),any()));assertThrows(IllegalStateException.class,()->service.entregar(substituir(id),key()));assertEquals(rs,registros.count());assertEquals(ms,movimentos.count());assertEquals(8,saldo());assertEquals("2",service.posse(person,null,0,20).getContent().get(0).get("quantidadeEmPosse"));}
 @Test void rollbackConfiguracaoPreservaPolitica(){tx.executeWithoutResult(x->doThrow(new IllegalStateException("falha H2")).when(audit).registrar(eq("EPI_ALTERADO"),any(),any(),any(),any(),any()));var c=config(produto);c.permiteRetorno=false;assertThrows(IllegalStateException.class,()->service.configurar(produto,c));assertEquals(true,service.buscarConfig(produto).get("permiteRetorno"));}
 @ParameterizedTest @EnumSource(Perfil.class) void matrizHttpLeiturasEMutadores(Perfil perfil)throws Exception{var id=entregar();var rid=service.posse(person,null,0,20).getContent().get(0).get("registroId");identity(perfil);var a=SecurityContextHolder.getContext().getAuthentication();for(String path:List.of("/epis","/epis/"+produto,"/epi-entregas","/epi-entregas/"+rid,"/epi-entregas/posse","/epi-entregas/resumo","/epi-funcionarios/"+person))mvc.perform(get(path).with(authentication(a))).andExpect(status().isOk());for(String path:List.of("/epis","/epi-entregas","/epi-devolucoes","/epi-descartes")){boolean denied=perfil==Perfil.CONSULTA||path.equals("/epis")&&perfil==Perfil.ALMOXARIFE;mvc.perform(post(path).with(authentication(a)).with(csrf()).header("Idempotency-Key",key()).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().is(denied?403:400));}mvc.perform(put("/epis/"+produto).with(authentication(a)).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}" )).andExpect(status().is(perfil==Perfil.CONSULTA||perfil==Perfil.ALMOXARIFE?403:400));}
 @Test void putNaoAceitaAtorForjado()throws Exception{mvc.perform(put("/epis/"+produto).with(authentication(SecurityContextHolder.getContext().getAuthentication())).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"atorId\":999}")).andExpect(status().isBadRequest());}
 @Test void fichaEDetalheSemCrescimentoPorLinha(){var first=service.entregar(entrega(),key());var stats=em.getEntityManagerFactory().unwrap(org.hibernate.SessionFactory.class).getStatistics();stats.clear();service.ficha(person,0,100);long ficha=stats.getPrepareStatementCount();stats.clear();service.buscarRegistro((Integer)first.get("id"));long detalhe=stats.getPrepareStatementCount();var p=novoProduto(5);var e=entrega();e.itens=List.of(linha(produto,"1"),linha(p,"1"));var multi=service.entregar(e,key());for(int j=0;j<2;j++)service.entregar(entrega(),key());stats.clear();service.ficha(person,0,100);assertTrue(stats.getPrepareStatementCount()<=ficha+2);stats.clear();service.buscarRegistro((Integer)multi.get("id"));assertEquals(detalhe,stats.getPrepareStatementCount());}
 void fracionar(Integer id){tx.executeWithoutResult(x->produtos.findById(id).orElseThrow().getUnidadeMedidaConfigurada().setPermiteFracionamento(true));}
 @Test void fracaoGrandeNaoViraUnidadeInteiraNaConversao(){estoque.entradaEstoque(produto,warehouse,9999999999990d,person,responsavel);var e=entrega();e.itens=List.of(linha(produto,"9999999999999.999999"));long rs=registros.count();assertThrows(IllegalArgumentException.class,()->service.entregar(e,key()));assertEquals(10000000000000d,saldo());assertEquals(rs,registros.count());}
 @Test void conversaoDecimalEVariacaoFisicaImprecisasBloqueiam(){fracionar(produto);estoque.entradaEstoque(produto,warehouse,9999999999990d,person,responsavel);var e=entrega();e.itens=List.of(linha(produto,"9999999999999.999999"));assertThrows(ConflitoException.class,()->service.entregar(e,key()));e.itens=List.of(linha(produto,"0.001"));String k=key();long rs=registros.count(),ms=movimentos.count(),is=itens.count();assertThrows(ConflitoException.class,()->service.entregar(e,k));assertEquals(10000000000000d,saldo());assertEquals(rs,registros.count());assertEquals(ms,movimentos.count());assertEquals(is,itens.count());assertTrue(registros.findByChaveIdempotencia(k).isEmpty());}
 @Test void decimalComumNaoBloqueiaPorRuidoBinario(){var p=novoProduto(0);fracionar(p);estoque.entradaEstoque(p,warehouse,0.3,person,responsavel);var e=entrega();e.itens=List.of(linha(p,"0.1"));var r=service.entregar(e,key());assertEquals("0.1",linhas(r).get(0).get("quantidade"));assertEquals(0.2,saldos.findByProdutoIdAndAlmoxarifadoId(p,warehouse).orElseThrow().getQuantidade(),1e-9);e.itens=List.of(linha(p,"0.2"));service.entregar(e,key());assertEquals(0d,saldos.findByProdutoIdAndAlmoxarifadoId(p,warehouse).orElseThrow().getQuantidade());}
 @Test void retornoNaoArredondaQuantidadeHistorica(){fracionar(produto);estoque.entradaEstoque(produto,warehouse,9999999999990d,person,responsavel);var e=entrega();e.itens=List.of(linha(produto,"9999999999999"));var id=(Integer)linhas(service.entregar(e,key())).get(0).get("id");var f=fechamento(id,DestinoEpi.ESTOQUE);f.itens.get(0).quantidade=new BigDecimal("9999999999998.999999");long rs=registros.count();assertThrows(ConflitoException.class,()->service.fechar(f,key(),false));assertEquals(1d,saldo());assertEquals(rs,registros.count());assertEquals("9999999999999",service.posse(person,null,0,20).getContent().get(0).get("quantidadeEmPosse"));}
 @Test void auditoriaSnapshotsPessoaEContextoAposRenomearCadastros(){
  var e=entrega();e.contexto=contexto();var r=service.entregar(e,key());
  var original=(ContextoOperacional)r.get("contexto");
  tx.executeWithoutResult(x->{
   pessoas.findById(person).orElseThrow().setNome("Recebedor renomeado");
   pessoas.findById(person).orElseThrow().setMatricula("Matricula alterada");
   pessoas.findById(responsavel).orElseThrow().setNome("Responsavel renomeado");
   locais.findById(warehouse).orElseThrow().setNome("Local renomeado");
   em.find(Obra.class,e.contexto.obraId).setNome("Obra renomeada");
   em.find(CentroCusto.class,e.contexto.centroCustoId).setNome("Centro renomeado");
   em.find(OrdemServico.class,e.contexto.ordemServicoId).setNumero("OS-ALTERADA");
  });
  var antigo=service.buscarRegistro((Integer)r.get("id"));
  for(String campo:List.of("funcionarioNome","funcionarioMatricula","responsavelNome","almoxarifadoNome","atorId","atorNome","dataHora"))assertEquals(r.get(campo),antigo.get(campo));
  var ctx=(ContextoOperacional)antigo.get("contexto");
  assertEquals(original.getObraNome(),ctx.getObraNome());
  assertEquals(original.getCentroCustoNome(),ctx.getCentroCustoNome());
  assertEquals(original.getOrdemServicoNumero(),ctx.getOrdemServicoNumero());
  var itemCtx=(ContextoOperacional)linhas(antigo).get(0).get("contexto");
  assertEquals(original.getObraNome(),itemCtx.getObraNome());
  assertEquals(original.getCentroCustoNome(),itemCtx.getCentroCustoNome());
  assertEquals(original.getOrdemServicoNumero(),itemCtx.getOrdemServicoNumero());
 }
 @Test void auditoriaEntregaCentroCorporativoSemObraFicticia(){
  var c=new ObrasInput.Centro();c.codigo="CORP-"+key();c.nome="Corporativo H2";c.tipo=TipoCentroCusto.ADMINISTRATIVO;c.ativo=true;
  var cc=estrutura.salvarCentro(null,c);var e=entrega();e.contexto=new ObrasInput.Contexto();e.contexto.centroCustoId=cc.getId();
  var r=service.entregar(e,key());var ctx=(ContextoOperacional)r.get("contexto");
  assertEquals(cc.getId(),ctx.getCentroCustoId());assertNull(ctx.getObraId());assertNull(ctx.getOrdemServicoId());
  var m=movimentos.findById((Integer)linhas(r).get(0).get("movimentoId")).orElseThrow();
  assertEquals(cc.getId(),m.getContexto().getCentroCustoId());assertNull(m.getContexto().getObraId());assertEquals(8,saldo());
 }

}
