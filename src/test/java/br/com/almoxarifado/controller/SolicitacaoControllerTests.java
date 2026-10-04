package br.com.almoxarifado.controller;

import br.com.almoxarifado.model.Solicitacao;
import br.com.almoxarifado.model.StatusSolicitacao;
import br.com.almoxarifado.service.SolicitacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SolicitacaoController.class)
class SolicitacaoControllerTests {
    @Autowired MockMvc mvc;
    @MockitoBean SolicitacaoService service;

    @Test
    void endpointAprovarDelegaAoService() throws Exception {
        when(service.aprovar(1, 123)).thenReturn(solicitacao(StatusSolicitacao.APROVADA));
        mvc.perform(put("/solicitacoes/1/aprovar").param("responsavelId", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADA"));
        verify(service).aprovar(1, 123);
    }

    @Test
    void endpointRejeitarDelegaAoService() throws Exception {
        when(service.rejeitar(1)).thenReturn(solicitacao(StatusSolicitacao.REJEITADA));
        mvc.perform(put("/solicitacoes/1/rejeitar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJEITADA"));
        verify(service).rejeitar(1);
    }

    @Test
    void regraDeNegocioInvalidaRetorna400() throws Exception {
        when(service.aprovar(1, 123)).thenThrow(new IllegalArgumentException("Estoque insuficiente"));
        mvc.perform(put("/solicitacoes/1/aprovar").param("responsavelId", "123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Estoque insuficiente"));
    }

    @Test
    void itemAposAprovacaoRetorna409() throws Exception {
        when(service.adicionarItem(1, 2, 3)).thenThrow(new br.com.almoxarifado.exception.ConflitoException("Somente solicitação PENDENTE permite esta operação"));
        mvc.perform(post("/solicitacoes/1/itens").param("produtoId", "2").param("quantidade", "3"))
                .andExpect(status().isConflict());
        verify(service).adicionarItem(1, 2, 3);
    }

    private Solicitacao solicitacao(StatusSolicitacao status) {
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setId(1);
        solicitacao.setStatus(status);
        return solicitacao;
    }

    @Test
    void erroInesperadoNaoExpoeDetalhesInternos() throws Exception {
        when(service.aprovar(1, 123)).thenThrow(new IllegalStateException("Detalhe interno sensível"));
        mvc.perform(put("/solicitacoes/1/aprovar").param("responsavelId", "123"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.mensagem").value("Erro interno ao processar a requisição"))
                .andExpect(jsonPath("$.path").value("/solicitacoes/1/aprovar"))
                .andExpect(jsonPath("$.trace").doesNotExist())
                .andExpect(jsonPath("$.exception").doesNotExist());
    }
}
