package br.com.almoxarifado.dto;
import java.time.LocalDateTime;
import java.util.List;
public record OperacaoSolicitacaoResponse(Integer id,String status,String statusRegistrado,String compatibilidadeLegada,String aviso,
    Referencia solicitante,Referencia almoxarifado,LocalDateTime dataSolicitacao,Referencia responsavelAprovacao,LocalDateTime dataAprovacao,
    Referencia responsavelSeparacao,LocalDateTime dataSeparacao,List<Item> itens) {
    public record Referencia(Integer id,String nome) {}
    public record Unidade(String sigla,boolean permiteFracionamento) {}
    public record Produto(Integer id,String codigo,String nome,String descricao,String unidadeMedida,Unidade unidadeMedidaConfigurada) {}
    public record Item(Integer id,Produto produto,double quantidadeSolicitada,Double quantidadeAtendida,Double quantidadePendente,
        double saldoAtual,double saldoDisponivel,boolean estoqueCadastrado,Double quantidadeFaltante,Integer necessidadeCompraId) {}
}
