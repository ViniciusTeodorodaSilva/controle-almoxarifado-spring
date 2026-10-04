package br.com.almoxarifado.dto;
import java.time.LocalDateTime;
import java.util.List;
public record AtendimentoResponse(Integer id,Integer solicitacaoId,OperacaoSolicitacaoResponse.Referencia responsavel,
    LocalDateTime dataHora,List<Item> itens) {
    public record Item(Integer id,Integer itemSolicitacaoId,OperacaoSolicitacaoResponse.Produto produto,double quantidade) {}
}
