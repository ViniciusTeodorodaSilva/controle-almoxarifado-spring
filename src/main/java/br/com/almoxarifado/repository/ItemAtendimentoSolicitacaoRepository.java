package br.com.almoxarifado.repository;
import br.com.almoxarifado.model.ItemAtendimentoSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ItemAtendimentoSolicitacaoRepository extends JpaRepository<ItemAtendimentoSolicitacao,Integer> {
    java.util.List<ItemAtendimentoSolicitacao> findByAtendimentoIdOrderByIdAsc(Integer id);
}
