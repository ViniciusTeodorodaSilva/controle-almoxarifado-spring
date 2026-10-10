package br.com.almoxarifado.controller;

import br.com.almoxarifado.security.AuditoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.dao.OptimisticLockingFailureException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ErrosConfiabilidadeTests {
    @org.springframework.web.bind.annotation.RestController
    static class ConflitosJpaController {
        @org.springframework.web.bind.annotation.GetMapping("/teste-jpa/{tipo}")
        public void conflito(@org.springframework.web.bind.annotation.PathVariable String tipo) {
            switch (tipo) {
                case "timeout" -> throw new jakarta.persistence.LockTimeoutException("SQL confidencial");
                case "pessimista" -> throw new jakarta.persistence.PessimisticLockException("SQL confidencial");
                default -> throw new jakarta.persistence.OptimisticLockException("SQL confidencial");
            }
        }
    }
    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings={"timeout","pessimista","otimista"})
    void conflitoJpaDiretoNaoViraHttp500(String tipo) throws Exception {
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(new ConflitosJpaController())
                .setControllerAdvice(handler).build();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/teste-jpa/" + tipo))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isConflict())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string(
                        org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("confidencial"))));
    }
    private final RegraNegocioExceptionHandler handler = new RegraNegocioExceptionHandler(mock(AuditoriaService.class));
    @Test void conflitoOtimistaNaoRetorna500NemDetalhesInternos() {
        var request = new MockHttpServletRequest("PUT", "/obras/1/status");
        var resposta = handler.tratarConflitoPersistencia(new OptimisticLockingFailureException("SQL segredo"), request);
        assertEquals(409, resposta.getStatusCode().value());
        assertFalse(resposta.getBody().toString().contains("segredo"));
    }
    @Test void falhaInesperadaNaoExpoeMensagemDaCausa() {
        var request = new MockHttpServletRequest("GET", "/obras/1/resumo");
        request.setAttribute("bes.requestId", "ea74abc4-44b6-4fcb-946f-cacbbba8b712");
        var resposta = handler.tratarInesperado(new IllegalStateException("credencial", new RuntimeException("SQL interno")), request);
        assertEquals(500, resposta.getStatusCode().value());
        assertFalse(resposta.getBody().toString().contains("credencial"));
        assertFalse(resposta.getBody().toString().contains("SQL interno"));
    }
}
