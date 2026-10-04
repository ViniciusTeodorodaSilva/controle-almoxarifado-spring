package br.com.almoxarifado.repository;

import br.com.almoxarifado.model.ItemSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemSolicitacaoRepository extends JpaRepository<ItemSolicitacao, Integer> {
    List<ItemSolicitacao> findBySolicitacaoId(Integer solicitacaoId);
}
