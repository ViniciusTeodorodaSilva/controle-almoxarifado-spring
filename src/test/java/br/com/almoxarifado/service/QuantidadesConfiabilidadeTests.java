package br.com.almoxarifado.service;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuantidadesConfiabilidadeTests {
    @Test void agregacaoNaoDescartaQuantidadePequenaEmTotalGrande() {
        assertThrows(IllegalArgumentException.class, () -> QuantidadesOperacionais.somar(1e16, 1));
        assertThrows(IllegalArgumentException.class, () -> QuantidadesOperacionais.subtrair(1e16, 1));
    }
    @Test void faltaDecimalNaoProduzResiduoNemArredondaExcesso() {
        assertEquals(0.2, QuantidadesOperacionais.subtrair(0.3, 0.1));
        assertThrows(IllegalArgumentException.class, () -> QuantidadesOperacionais.saldo(0.3, 0.30000000000000004, false));
    }
    @Test void somaDeItensDecimaisPermiteEsgotarSaldo() {
        double pedido = QuantidadesOperacionais.somar(0.1, 0.2);
        assertEquals(0, QuantidadesOperacionais.saldo(0.3, pedido, false));
    }
}
