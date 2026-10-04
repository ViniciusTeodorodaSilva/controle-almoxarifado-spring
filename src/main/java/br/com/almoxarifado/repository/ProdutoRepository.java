package br.com.almoxarifado.repository;

import br.com.almoxarifado.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface ProdutoRepository
        extends JpaRepository<Produto, Integer>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Produto> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Produto p where p.id = :id")
    Optional<Produto> buscarParaAtualizacao(@Param("id") Integer id);
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Integer id);
    boolean existsByUnidadeMedidaConfiguradaId(Integer unidadeId);
    java.util.List<Produto> findByAtivo(boolean ativo);
}
