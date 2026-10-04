package br.com.almoxarifado.controller;

import br.com.almoxarifado.model.*;
import br.com.almoxarifado.repository.*;
import br.com.almoxarifado.service.*;
import br.com.almoxarifado.exception.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:bes-catalogo;MODE=MySQL;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CatalogoMestreTests {
    @Autowired MockMvc mvc;
    @Autowired ProdutoService produtos;
    @Autowired CategoriaMaterialService categorias;
    @Autowired UnidadeMedidaService unidades;
    @Autowired ProdutoRepository produtoRepository;
    @Autowired CategoriaMaterialRepository categoriaRepository;
    @Autowired UnidadeMedidaRepository unidadeRepository;
    @Autowired EstoqueRepository estoques;
    @Autowired ItemSolicitacaoRepository itens;
    @Autowired SolicitacaoRepository solicitacoes;
    @Autowired MovimentacaoRepository movimentos;
    @Autowired FuncionarioRepository funcionarios;
    @Autowired AlmoxarifadoRepository almoxarifados;
    @Autowired EstoqueService estoqueService;
    @Autowired SolicitacaoService solicitacaoService;
    CategoriaMaterial categoria;
    UnidadeMedida unidade;

    @BeforeEach void preparar() {
        movimentos.deleteAll(); itens.deleteAll(); solicitacoes.deleteAll(); estoques.deleteAll();
        produtoRepository.deleteAll(); categoriaRepository.deleteAll(); unidadeRepository.deleteAll();
        funcionarios.deleteAll(); almoxarifados.deleteAll();
        categoria = categorias.cadastrar(categoria("Ferramentas"));
        unidade = unidades.cadastrar(unidade("UN", false));
    }

    @Test void categoriaValidaENormalizada() {
        CategoriaMaterial c = categorias.cadastrar(categoria("  Material   Elétrico  "));
        assertEquals("Material Elétrico", c.getNome()); assertTrue(c.isAtivo());
    }
    @Test void categoriaEquivalenteDuplicadaRetorna409() throws Exception {
        categorias.cadastrar(categoria("Material Elétrico"));
        mvc.perform(post("/categorias").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\" material  eletrico \"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }
    @Test void categoriaPodeSerInativadaEFiltrada() throws Exception {
        mvc.perform(put("/categorias/{id}", categoria.getId()).contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Ferramentas\",\"ativo\":false}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ativo").value(false));
        mvc.perform(get("/categorias").param("ativo", "true")).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/categorias/{id}", categoria.getId())).andExpect(status().isOk());
    }
    @Test void categoriaAtualizaSemTrocarIdENaoAceitaColisao() {
        CategoriaMaterial dados = categoria("Nova categoria");
        assertEquals(categoria.getId(), categorias.atualizar(categoria.getId(), dados).getId());
        dados.setId(Integer.MAX_VALUE);
        assertThrows(IllegalArgumentException.class, () -> categorias.atualizar(categoria.getId(), dados));
        categorias.cadastrar(categoria("Outra"));
        assertThrows(ConflitoException.class, () -> categorias.atualizar(categoria.getId(), categoria("OUTRA")));
    }
    @Test void unidadeConfiguravelSemListaFixa() {
        UnidadeMedida nova = unidades.cadastrar(unidade(" custom ", true));
        assertEquals("CUSTOM", nova.getSigla()); assertTrue(nova.isPermiteFracionamento());
    }
    @Test void siglaDuplicadaRetorna409() throws Exception {
        mvc.perform(post("/unidades-medida").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Peça\",\"sigla\":\" un \"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.path").value("/unidades-medida"));
    }
    @Test void unidadePodeSerInativadaEFiltrada() throws Exception {
        mvc.perform(put("/unidades-medida/{id}", unidade.getId()).contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Unidade\",\"sigla\":\"UN\",\"ativo\":false}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ativo").value(false));
        mvc.perform(get("/unidades-medida").param("ativo", "true")).andExpect(jsonPath("$.length()").value(0));
    }
    @Test void unidadeAtualizaSemTrocarId() {
        UnidadeMedida dados = unidade("PC", false);
        assertEquals(unidade.getId(), unidades.atualizar(unidade.getId(), dados).getId());
        dados.setId(Integer.MAX_VALUE);
        assertThrows(IllegalArgumentException.class, () -> unidades.atualizar(unidade.getId(), dados));
    }
    @Test void validaObrigatoriosECadastrosInexistentes() throws Exception {
        mvc.perform(post("/categorias").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/unidades-medida").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Unidade\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/categorias/2147483647")).andExpect(status().isNotFound());
        mvc.perform(get("/unidades-medida/2147483647")).andExpect(status().isNotFound());
    }
    @Test void produtoPossuiCodigoCanonicoEReferenciasReais() {
        Produto p = produtos.cadastrar(produto(" mat-001 "));
        assertEquals("MAT-001", p.getCodigo());
        assertEquals(categoria.getId(), p.getCategoriaMaterial().getId());
        assertEquals(unidade.getId(), p.getUnidadeMedidaConfigurada().getId());
        assertEquals("UN", p.getUnidadeMedida()); assertEquals("Ferramentas", p.getCategoria());
    }
    @Test void codigoDuplicadoRetorna409() throws Exception {
        produtos.cadastrar(produto("MAT-001"));
        mvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON).content("{\"codigo\":\"mat-001\",\"nome\":\"Outra\"}"))
                .andExpect(status().isConflict());
    }
    @Test void nomeParecidoNaoBloqueiaCadastro() {
        produtos.cadastrar(produto("MAT-001")); produtos.cadastrar(produto("MAT-002"));
        assertEquals(2, produtoRepository.count());
    }
    @Test void referenciasInexistentesRetornam404() throws Exception {
        for (String campo : new String[]{"categoriaMaterial", "unidadeMedidaConfigurada"}) {
            mvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nome\":\"Novo\",\"" + campo + "\":{\"id\":2147483647}}"))
                    .andExpect(status().isNotFound());
        }
        assertEquals(0, produtoRepository.count());
    }
    @Test void referenciaSemIdRetorna400() throws Exception {
        mvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Novo\",\"categoriaMaterial\":{}}"))
                .andExpect(status().isBadRequest());
    }
    @Test void categoriaInativaNaoPodeSerUsadaEmNovoProduto() {
        categoria.setAtivo(false); categorias.atualizar(categoria.getId(), categoria);
        assertThrows(IllegalArgumentException.class, () -> produtos.cadastrar(produto("A")));
    }
    @Test void unidadeInativaNaoPodeSerUsadaEmNovoProduto() {
        unidade.setAtivo(false); unidades.atualizar(unidade.getId(), unidade);
        assertThrows(IllegalArgumentException.class, () -> produtos.cadastrar(produto("A")));
    }
    @Test void produtoLegadoGanhaCodigoSemExigirReferencias() {
        Produto p = new Produto(); p.setNome("Legado"); p.setCategoria("EPI"); p.setUnidadeMedida("UN");
        p = produtos.cadastrar(p);
        assertTrue(p.getCodigo().startsWith("BES-")); assertEquals("UN", p.getUnidadeMedida());
        assertNull(p.getUnidadeMedidaConfigurada());
    }
    @Test void atualizacaoLegadaPreservaCodigoReferenciasEInativacao() throws Exception {
        Produto p = produto("A"); p.setAtivo(false); p = produtos.cadastrar(p);
        mvc.perform(put("/produtos/{id}", p.getId()).contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Alterado\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.codigo").value("A"))
                .andExpect(jsonPath("$.ativo").value(false)).andExpect(jsonPath("$.unidadeMedidaConfigurada.id").value(unidade.getId()));
    }
    @Test void atualizacaoProtegeIdECodigoUnico() {
        Produto a = produtos.cadastrar(produto("A")); produtos.cadastrar(produto("B"));
        Produto dados = produto("B");
        assertThrows(ConflitoException.class, () -> produtos.atualizar(a.getId(), dados));
        dados.setId(Integer.MAX_VALUE);
        assertThrows(IllegalArgumentException.class, () -> produtos.atualizar(a.getId(), dados));
    }
    @Test void buscaCodigoCaseInsensitive() throws Exception { verificarBusca("mat-001", "MAT-001"); }
    @Test void buscaNomeEDescricao() throws Exception { verificarBusca("proteção", "MAT-001"); }
    @Test void buscaCategoria() throws Exception { verificarBusca("ferramentas", "MAT-001"); }
    @Test void buscaEspecificacaoTecnica() throws Exception { verificarBusca("EN388", "MAT-001"); }
    @Test void filtroCategoriaEAtivos() throws Exception {
        produtos.cadastrar(produto("A")); Produto inativo = produto("B"); inativo.setAtivo(false); produtos.cadastrar(inativo);
        mvc.perform(get("/produtos").param("ativo", "true")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/produtos/busca").param("categoriaId", categoria.getId().toString()).param("ativo", "false"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].codigo").value("B"));
    }
    @Test void sugereEquivalentesSemBloquearEIncluiInativos() throws Exception {
        produtos.cadastrar(produto("A")); Produto inativo = produto("B"); inativo.setAtivo(false); produtos.cadastrar(inativo);
        mvc.perform(get("/produtos/equivalentes").param("termo", "luva"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/produtos/equivalentes").param("termo", "lu")).andExpect(status().isBadRequest());
    }
    @Test void buscaEscapaWildcardsDoCliente() {
        produtos.cadastrar(produto("A"));
        assertTrue(produtos.buscar("%", null, null).isEmpty());
        assertTrue(produtos.buscar("_", null, null).isEmpty());
    }
    @Test void unidadeNaoFracionariaRejeitaEntradaSaidaEItem() {
        Produto p = produtos.cadastrar(produto("A"));
        Funcionario f = new Funcionario(); f.setNome("Operador"); f.setMatricula("1"); f = funcionarios.save(f);
        Almoxarifado a = new Almoxarifado(); a.setNome("Central"); a = almoxarifados.save(a);
        Estoque e = new Estoque(); e.setProduto(p); e.setAlmoxarifado(a); estoqueService.cadastrar(e);
        Integer funcionarioId = f.getId(), localId = a.getId();
        assertThrows(IllegalArgumentException.class, () -> estoqueService.entradaEstoque(p.getId(), localId, 1.5, funcionarioId, funcionarioId));
        estoqueService.entradaEstoque(p.getId(), localId, 2, funcionarioId, funcionarioId);
        assertThrows(IllegalArgumentException.class, () -> estoqueService.saidaEstoque(p.getId(), localId, 0.5, funcionarioId, funcionarioId));
        Solicitacao s = solicitacaoService.cadastrar(funcionarioId, localId);
        assertThrows(IllegalArgumentException.class, () -> solicitacaoService.adicionarItem(s.getId(), p.getId(), 0.5));
        assertEquals(2, estoques.findAll().get(0).getQuantidade()); assertEquals(1, movimentos.count());
    }
    @Test void unidadeFracionariaELegadoContinuamAceitandoFracoes() {
        UnidadeMedida metros = unidades.cadastrar(unidade("M", true));
        Produto p = produto("A"); p.setUnidadeMedidaConfigurada(metros); p = produtos.cadastrar(p);
        ValidacaoQuantidade.validar(p, 0.25);
        Produto legado = new Produto(); legado.setNome("Legado"); ValidacaoQuantidade.validar(legado, 0.25);
    }
    @Test void unidadeComProdutoNaoPodeMudarFracionamento() {
        produtos.cadastrar(produto("A"));
        UnidadeMedida dados = unidade("UN", true);
        assertThrows(ConflitoException.class, () -> unidades.atualizar(unidade.getId(), dados));
    }
    @Test void produtoComEstoqueNaoPodeTrocarUnidade() {
        Produto p = produtos.cadastrar(produto("A"));
        Almoxarifado a = new Almoxarifado(); a.setNome("Central"); a = almoxarifados.save(a);
        Estoque e = new Estoque(); e.setProduto(p); e.setAlmoxarifado(a); estoqueService.cadastrar(e);
        UnidadeMedida nova = unidades.cadastrar(unidade("PC", false));
        Produto dados = produto("A"); dados.setUnidadeMedidaConfigurada(nova);
        assertThrows(ConflitoException.class, () -> produtos.atualizar(p.getId(), dados));
    }

    private void verificarBusca(String termo, String codigo) throws Exception {
        produtos.cadastrar(produto(codigo));
        mvc.perform(get("/produtos/busca").param("termo", termo))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].codigo").value(codigo));
    }
    private CategoriaMaterial categoria(String nome) { CategoriaMaterial c = new CategoriaMaterial(); c.setNome(nome); return c; }
    private UnidadeMedida unidade(String sigla, boolean fracao) { UnidadeMedida u = new UnidadeMedida(); u.setNome("Unidade " + sigla); u.setSigla(sigla); u.setPermiteFracionamento(fracao); return u; }
    private Produto produto(String codigo) {
        Produto p = new Produto(); p.setCodigo(codigo); p.setNome("Luva"); p.setDescricao("Proteção industrial");
        p.setEspecificacaoTecnica("Norma EN388"); p.setCategoriaMaterial(categoria); p.setUnidadeMedidaConfigurada(unidade); return p;
    }
}
