package br.com.almoxarifado.controller;

import br.com.almoxarifado.model.*;
import br.com.almoxarifado.repository.*;
import br.com.almoxarifado.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.security.test.context.support.WithMockUser(authorities={"USUARIO_GERENCIAR","AUDITORIA_LER","PRODUTO_LER","PRODUTO_GERENCIAR","CATEGORIA_LER","CATEGORIA_GERENCIAR","UNIDADE_LER","UNIDADE_GERENCIAR","ESTOQUE_LER","ESTOQUE_MOVIMENTAR","ESTOQUE_TRANSFERIR","ESTOQUE_CONFIGURAR","SOLICITACAO_LER","SOLICITACAO_CRIAR","SOLICITACAO_APROVAR","SOLICITACAO_REJEITAR","SOLICITACAO_SEPARAR","SOLICITACAO_ATENDER","NECESSIDADE_COMPRA_LER","NECESSIDADE_COMPRA_CRIAR","MOVIMENTACAO_LER","FUNCIONARIO_LER","FUNCIONARIO_GERENCIAR","ALMOXARIFADO_LER","ALMOXARIFADO_GERENCIAR"})
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:bes-api;MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BackendOperacionalTests {
    @Autowired MockMvc mvc;
    @Autowired ProdutoRepository produtos;
    @Autowired FuncionarioRepository funcionarios;
    @Autowired AlmoxarifadoRepository almoxarifados;
    @Autowired EstoqueRepository estoques;
    @Autowired SolicitacaoRepository solicitacoes;
    @Autowired ItemSolicitacaoRepository itens;
    @Autowired MovimentacaoRepository movimentacoes;
    @Autowired SolicitacaoService solicitacaoService;
    @Autowired EstoqueService estoqueService;

    private Produto produto;
    private Funcionario funcionario;
    private Almoxarifado almoxarifado;

    @BeforeEach
    void preparar() {
        movimentacoes.deleteAll();
        itens.deleteAll();
        solicitacoes.deleteAll();
        estoques.deleteAll();
        produtos.deleteAll();
        funcionarios.deleteAll();
        almoxarifados.deleteAll();
        produto = new Produto();
        produto.setNome("Luva");
        produto = produtos.save(produto);
        funcionario = new Funcionario();
        funcionario.setNome("Operador");
        funcionario.setMatricula("BES-001");
        funcionario = funcionarios.save(funcionario);
        almoxarifado = new Almoxarifado();
        almoxarifado.setNome("Central");
        almoxarifado = almoxarifados.save(almoxarifado);
    }

    @Test
    void todosOsGetPorIdInexistenteRetornam404Padronizado() throws Exception {
        for (String recurso : new String[]{"produtos", "funcionarios", "almoxarifados", "estoques", "solicitacoes", "movimentacoes"}) {
            String path = "/" + recurso + "/2147483647";
            mvc.perform(get(path)).andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.erro").exists())
                    .andExpect(jsonPath("$.mensagem").exists())
                    .andExpect(jsonPath("$.path").value(path))
                    .andExpect(jsonPath("$.trace").doesNotExist());
        }
    }

    @Test
    void atualizaProdutoSemTrocarId() throws Exception {
        mvc.perform(put("/produtos/{id}", produto.getId()).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Luva atualizada\",\"descricao\":\"Proteção\",\"unidadeMedida\":\"UN\",\"categoria\":\"EPI\",\"tipoControle\":\"CONSUMO\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(produto.getId()))
                .andExpect(jsonPath("$.nome").value("Luva atualizada"));
        Produto atualizado = produtos.findById(produto.getId()).orElseThrow();
        assertEquals("Proteção", atualizado.getDescricao());
        assertEquals("UN", atualizado.getUnidadeMedida());
        assertEquals("EPI", atualizado.getCategoria());
        assertEquals("CONSUMO", atualizado.getTipoControle());
    }

    @Test
    void atualizaFuncionarioSemTrocarId() throws Exception {
        mvc.perform(put("/funcionarios/{id}", funcionario.getId()).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Nome atualizado\",\"matricula\":\"BES-002\",\"funcao\":\"Almoxarife\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(funcionario.getId()));
        Funcionario atualizado = funcionarios.findById(funcionario.getId()).orElseThrow();
        assertEquals("Nome atualizado", atualizado.getNome());
        assertEquals("BES-002", atualizado.getMatricula());
        assertEquals("Almoxarife", atualizado.getFuncao());
    }

    @Test
    void atualizaAlmoxarifadoSemTrocarId() throws Exception {
        mvc.perform(put("/almoxarifados/{id}", almoxarifado.getId()).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Central atualizada\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(almoxarifado.getId()));
        assertEquals("Central atualizada", almoxarifados.findById(almoxarifado.getId()).orElseThrow().getNome());
    }

    @Test
    void putRejeitaTrocaDeIdNosTresCadastros() throws Exception {
        String[] recursos = {"produtos", "funcionarios", "almoxarifados"};
        Integer[] ids = {produto.getId(), funcionario.getId(), almoxarifado.getId()};
        for (int i = 0; i < recursos.length; i++) {
            mvc.perform(put("/" + recursos[i] + "/" + ids[i]).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":2147483647,\"nome\":\"Alterado\",\"matricula\":\"BES-002\"}"))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        }
        assertEquals("Luva", produtos.findById(produto.getId()).orElseThrow().getNome());
        assertEquals("Operador", funcionarios.findById(funcionario.getId()).orElseThrow().getNome());
        assertEquals("Central", almoxarifados.findById(almoxarifado.getId()).orElseThrow().getNome());
    }

    @Test
    void postEPutValidamNomesObrigatorios() throws Exception {
        String[] recursos = {"produtos", "funcionarios", "almoxarifados"};
        Integer[] ids = {produto.getId(), funcionario.getId(), almoxarifado.getId()};
        for (int i = 0; i < recursos.length; i++) {
            mvc.perform(post("/" + recursos[i]).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\" \"}"))
                    .andExpect(status().isBadRequest());
            mvc.perform(put("/" + recursos[i] + "/" + ids[i]).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void funcionarioExigeMatriculaNoPostEPut() throws Exception {
        mvc.perform(post("/funcionarios").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Novo\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(put("/funcionarios/{id}", funcionario.getId()).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Novo\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrosNovosContinuamFuncionando() throws Exception {
        mvc.perform(post("/produtos").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Máscara\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").isNumber());
        mvc.perform(post("/funcionarios").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Novo\",\"matricula\":\"BES-002\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").isNumber());
        mvc.perform(post("/almoxarifados").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Secundário\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").isNumber());
        assertEquals(2, produtos.count());
        assertEquals(2, funcionarios.count());
        assertEquals(2, almoxarifados.count());
    }

    @Test
    void putInexistenteRetorna404() throws Exception {
        for (String recurso : new String[]{"produtos", "funcionarios", "almoxarifados"}) {
            mvc.perform(put("/" + recurso + "/2147483647").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                            .content("{\"nome\":\"Válido\",\"matricula\":\"BES-002\"}"))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    void cadastroDeEstoqueZeradoEDuplicidade409() throws Exception {
        String json = estoqueJson(0);
        mvc.perform(post("/estoques").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.quantidade").value(0));
        mvc.perform(post("/estoques").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.path").value("/estoques"));
        assertEquals(1, estoques.count());
    }

    @Test
    void cadastroComSaldoArbitrarioRetorna400() throws Exception {
        mvc.perform(post("/estoques").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON).content(estoqueJson(10)))
                .andExpect(status().isBadRequest());
        assertEquals(0, estoques.count());
        assertEquals(0, movimentacoes.count());
    }

    @Test
    void aprovaViaApiSemGerarMovimentacao() throws Exception {
        Solicitacao solicitacao = prepararSolicitacao();
        mvc.perform(put("/solicitacoes/{id}/aprovar", solicitacao.getId()).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).param("responsavelId", funcionario.getId().toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("APROVADA"))
                .andExpect(jsonPath("$.itens[0].quantidade").value(2));
        mvc.perform(get("/solicitacoes/{id}/movimentacoes", solicitacao.getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void aprovacaoDuplicadaEItemAposAprovacaoRetornam409() throws Exception {
        Solicitacao solicitacao = prepararSolicitacao();
        solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId());
        mvc.perform(put("/solicitacoes/{id}/aprovar", solicitacao.getId()).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).param("responsavelId", funcionario.getId().toString()))
                .andExpect(status().isConflict());
        mvc.perform(post("/solicitacoes/{id}/itens", solicitacao.getId()).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).param("produtoId", produto.getId().toString()).param("quantidade", "1"))
                .andExpect(status().isConflict());
        assertEquals(10, estoques.findAll().get(0).getQuantidade());
    }

    @Test
    void aprovacaoSemResponsavelRetorna400ESemAlteracoes() throws Exception {
        Solicitacao solicitacao = prepararSolicitacao();
        mvc.perform(put("/solicitacoes/{id}/aprovar", solicitacao.getId()).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists());
        assertEquals(10, estoques.findAll().get(0).getQuantidade());
        assertEquals(StatusSolicitacao.PENDENTE, solicitacoes.findById(solicitacao.getId()).orElseThrow().getStatus());
    }

    @Test
    void aprovacaoComResponsavelInexistenteRetorna404ESemAlteracoes() throws Exception {
        Solicitacao solicitacao = prepararSolicitacao();
        long movimentosAntes = movimentacoes.count();
        mvc.perform(put("/solicitacoes/{id}/aprovar", solicitacao.getId()).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).param("responsavelId", "2147483647"))
                .andExpect(status().isNotFound());
        assertEquals(10, estoques.findAll().get(0).getQuantidade());
        assertEquals(movimentosAntes, movimentacoes.count());
    }

    @Test
    void parametrosStatusTipoEJsonInvalidosRetornam400Padronizado() throws Exception {
        for (String path : new String[]{"/solicitacoes/status/INVALIDO", "/movimentacoes/tipo/INVALIDO", "/produtos/abc"}) {
            mvc.perform(get(path)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400)).andExpect(jsonPath("$.path").value(path));
        }
        mvc.perform(post("/produtos").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON).content("{invalido"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensagem").value("Requisição inválida"));
        mvc.perform(post("/solicitacoes").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void filtrosDeEstoqueFuncionamViaHttp() throws Exception {
        prepararSolicitacao();
        mvc.perform(get("/estoques/produto/{id}", produto.getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/estoques/almoxarifado/{id}", almoxarifado.getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].produto.id").value(produto.getId()));
        mvc.perform(get("/estoques/produto/{produto}/almoxarifado/{local}", produto.getId(), almoxarifado.getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.quantidade").value(10));
    }

    @Test
    void filtrosDeSolicitacaoEMovimentacaoFuncionamViaHttp() throws Exception {
        Solicitacao solicitacao = prepararSolicitacao();
        solicitacaoService.aprovar(solicitacao.getId(), funcionario.getId());
        mvc.perform(get("/solicitacoes/status/APROVADA"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(solicitacao.getId()));
        mvc.perform(get("/solicitacoes/funcionario/{id}", funcionario.getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].itens.length()").value(1));
        mvc.perform(get("/movimentacoes/produto/{id}", produto.getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/movimentacoes/almoxarifado/{id}", almoxarifado.getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/movimentacoes/solicitacao/{id}", solicitacao.getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/movimentacoes/tipo/ENTRADA"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/movimentacoes/tipo/SAIDA"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/movimentacoes/{id}", movimentacoes.findAll().get(0).getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void filtroPorRecursoInexistenteRetorna404() throws Exception {
        for (String path : new String[]{"/solicitacoes/2147483647/movimentacoes", "/solicitacoes/funcionario/2147483647",
                "/estoques/produto/2147483647", "/estoques/almoxarifado/2147483647",
                "/estoques/produto/2147483647/almoxarifado/2147483647",
                "/movimentacoes/produto/2147483647", "/movimentacoes/almoxarifado/2147483647",
                "/movimentacoes/solicitacao/2147483647"}) {
            mvc.perform(get(path)).andExpect(status().isNotFound());
        }
    }

    private String estoqueJson(double quantidade) {
        return "{\"produto\":{\"id\":" + produto.getId() + "},\"almoxarifado\":{\"id\":"
                + almoxarifado.getId() + "},\"quantidade\":" + quantidade + "}";
    }

    private Solicitacao prepararSolicitacao() {
        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setAlmoxarifado(almoxarifado);
        estoqueService.cadastrar(estoque);
        estoqueService.entradaEstoque(produto.getId(), almoxarifado.getId(), 10, funcionario.getId(), funcionario.getId());
        Solicitacao solicitacao = solicitacaoService.cadastrar(funcionario.getId(), almoxarifado.getId());
        solicitacaoService.adicionarItem(solicitacao.getId(), produto.getId(), 2);
        return solicitacao;
    }
}
