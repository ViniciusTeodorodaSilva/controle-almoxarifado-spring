package br.com.almoxarifado.compras;

import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface AlocacaoCompraRepository
    extends JpaRepository<AlocacaoCompra, Integer>, JpaSpecificationExecutor<AlocacaoCompra> {
  @Query(
      "select a from AlocacaoCompra a where a.necessidade.id=:id and a.item.ativo=true and"
          + " a.item.pedido.status<>br.com.almoxarifado.compras.StatusPedidoCompra.CANCELADO")
  List<AlocacaoCompra> ativas(@Param("id") Integer id);

  default java.util.Map<String, Object> progresso(br.com.almoxarifado.model.NecessidadeCompra n) {
    return progresso(n, ativas(n.getId()));
  }

  @Query(
      "select a from AlocacaoCompra a join fetch a.item i join fetch i.pedido where"
          + " a.necessidade.id in :ids and i.ativo=true and"
          + " i.pedido.status<>br.com.almoxarifado.compras.StatusPedidoCompra.CANCELADO")
  List<AlocacaoCompra> ativasEm(@Param("ids") Collection<Integer> ids);

  default java.util.Map<String, Object> progresso(
      br.com.almoxarifado.model.NecessidadeCompra n, List<AlocacaoCompra> a) {
    double reservado = 0;
    for (var x : a)
      reservado =
          ComprasViews.decimal(
              reservado,
              ComprasViews.decimal(x.getQuantidade(), x.getQuantidadeRecebida(), false),
              true);
    return ComprasViews.map(
        "compatibilidadeLegada",
        n.isCompraRastreavel() ? "RASTREAVEL" : "A_CONFERIR",
        "motivoCancelamento",
        n.getMotivoCancelamento(),
        "quantidadeVinculadaPendente",
        reservado,
        "quantidadeRecebida",
        n.isCompraRastreavel() ? n.getQuantidadeRecebida() : null,
        "quantidadeDisponivel",
        !n.isCompraRastreavel()
            ? 0
            : ComprasViews.decimal(
                ComprasViews.decimal(n.getQuantidade(), n.getQuantidadeRecebida(), false),
                reservado,
                false),
        "quantidadeSolicitacaoPendente",
        n.getItemSolicitacao().getQuantidadePendente(),
        "pedidos",
        a.stream()
            .map(
                x ->
                    ComprasViews.map(
                        "pedidoId",
                        x.getItem().getPedido().getId(),
                        "numero",
                        x.getItem().getPedido().getNumero(),
                        "status",
                        x.getItem().getPedido().getStatus(),
                        "itemPedidoId",
                        x.getItem().getId(),
                        "quantidade",
                        x.getQuantidade(),
                        "quantidadeRecebida",
                        x.getQuantidadeRecebida()))
            .toList());
  }
}
