package br.com.almoxarifado.compras;

import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface RecebimentoCompraRepository
    extends JpaRepository<RecebimentoCompra, Integer>, JpaSpecificationExecutor<RecebimentoCompra> {
  Optional<RecebimentoCompra> findByChaveIdempotencia(String chave);

  org.springframework.data.domain.Page<RecebimentoCompra> findByPedidoId(
      Integer id, org.springframework.data.domain.Pageable pageable);

  @Query(
      "select distinct r from RecebimentoCompra r join fetch r.pedido join fetch r.almoxarifado"
          + " join fetch r.responsavel left join fetch r.itens i left join fetch i.itemPedido where"
          + " r.id in :ids")
  List<RecebimentoCompra> carregarItens(
      @org.springframework.data.repository.query.Param("ids") Collection<Integer> ids);
}
