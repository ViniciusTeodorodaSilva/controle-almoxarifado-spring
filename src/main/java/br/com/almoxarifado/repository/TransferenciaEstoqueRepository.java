package br.com.almoxarifado.repository;
import br.com.almoxarifado.model.TransferenciaEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface TransferenciaEstoqueRepository extends JpaRepository<TransferenciaEstoque, Integer> {
    @Query("""
        select t from TransferenciaEstoque t
        where (:origemId is null or t.almoxarifadoOrigem.id = :origemId)
          and (:destinoId is null or t.almoxarifadoDestino.id = :destinoId)
          and (:produtoId is null or exists (select i.id from ItemTransferencia i where i.transferencia = t and i.produto.id = :produtoId))
        order by t.id desc
        """)
    List<TransferenciaEstoque> filtrar(@Param("origemId") Integer origemId, @Param("destinoId") Integer destinoId, @Param("produtoId") Integer produtoId);
}
