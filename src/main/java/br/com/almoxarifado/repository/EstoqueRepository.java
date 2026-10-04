package br.com.almoxarifado.repository;

import br.com.almoxarifado.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

    Optional<Estoque> findByProdutoIdAndAlmoxarifadoId(Integer produtoId, Integer almoxarifadoId);

    boolean existsByProdutoIdAndAlmoxarifadoId(Integer produtoId, Integer almoxarifadoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Estoque e where e.produto.id = :produtoId and e.almoxarifado.id = :almoxarifadoId")
    Optional<Estoque> buscarParaAtualizacao(@Param("produtoId") Integer produtoId,
                                          @Param("almoxarifadoId") Integer almoxarifadoId);
    java.util.List<Estoque> findByProdutoId(Integer produtoId);
    java.util.List<Estoque> findByAlmoxarifadoId(Integer almoxarifadoId);
    boolean existsByProdutoId(Integer produtoId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Estoque e where e.id = :id")
    Optional<Estoque> buscarPorIdParaAtualizacao(@Param("id") Integer id);
    @Query("""
        select e from Estoque e where e.estoqueMinimo is not null and e.quantidade <= e.estoqueMinimo
        and (:produtoId is null or e.produto.id = :produtoId)
        and (:almoxarifadoId is null or e.almoxarifado.id = :almoxarifadoId)
        order by e.almoxarifado.id, e.produto.id
        """)
    java.util.List<Estoque> alertas(@Param("produtoId") Integer produtoId, @Param("almoxarifadoId") Integer almoxarifadoId);
}
