package br.com.almoxarifado.dto;
import java.time.LocalDateTime;
import java.util.List;
public record TransferenciaResponse(Integer id, Referencia almoxarifadoOrigem, Referencia almoxarifadoDestino,
        Referencia responsavel, LocalDateTime dataHora, String status, String observacao, List<Item> itens) {
    public record Referencia(Integer id, String nome) {}
    public record Item(Integer id, Integer produtoId, String codigo, String produto, String unidade, double quantidade) {}
}
