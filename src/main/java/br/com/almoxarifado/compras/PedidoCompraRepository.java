package br.com.almoxarifado.compras;

import jakarta.persistence.LockModeType;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface PedidoCompraRepository
    extends JpaRepository<PedidoCompra, Integer>, JpaSpecificationExecutor<PedidoCompra> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select x from PedidoCompra x where x.id=:id")
  Optional<PedidoCompra> bloquear(@Param("id") Integer id);

  @Query(
      "select distinct p from PedidoCompra p join fetch p.almoxarifado left join fetch p.itens"
          + " where p.id in :ids")
  List<PedidoCompra> carregarItens(@Param("ids") Collection<Integer> ids);
}
