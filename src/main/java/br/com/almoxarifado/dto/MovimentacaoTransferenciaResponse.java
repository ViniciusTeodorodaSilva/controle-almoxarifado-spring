package br.com.almoxarifado.dto;
import java.time.LocalDateTime;
public record MovimentacaoTransferenciaResponse(Integer id, Produto produto,
        TransferenciaResponse.Referencia almoxarifado, TransferenciaResponse.Referencia responsavel,
        String tipo, double quantidade, double saldoAnterior, double saldoPosterior, LocalDateTime dataHora,
        Integer transferenciaId, Integer origemId, Integer destinoId) {
    public record Produto(Integer id, String codigo, String nome, String unidadeMedida) {}
}
