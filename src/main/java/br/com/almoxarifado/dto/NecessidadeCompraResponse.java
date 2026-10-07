package br.com.almoxarifado.dto;
import java.time.LocalDateTime;
public record NecessidadeCompraResponse(Integer id,Integer itemSolicitacaoId,Integer solicitacaoId,
    OperacaoSolicitacaoResponse.Produto produto,OperacaoSolicitacaoResponse.Referencia almoxarifado,double quantidade,String status,
    LocalDateTime dataHora,String motivo,OperacaoSolicitacaoResponse.Referencia responsavel,java.util.Map<String,Object> compra,br.com.almoxarifado.obras.ContextoOperacional contexto) {}
