package br.com.almoxarifado.repository;

import br.com.almoxarifado.model.ItemSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemSolicitacaoRepository extends JpaRepository<ItemSolicitacao, Integer> {
    @Query("select i.solicitacao.id from ItemSolicitacao i where i.id = :id")
    Optional<Integer> encontrarSolicitacaoId(@Param("id") Integer id);
    List<ItemSolicitacao> findBySolicitacaoId(Integer solicitacaoId);
    boolean existsByProdutoId(Integer produtoId);
}
