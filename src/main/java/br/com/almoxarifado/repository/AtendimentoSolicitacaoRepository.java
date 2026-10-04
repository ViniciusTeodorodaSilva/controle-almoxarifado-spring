package br.com.almoxarifado.repository;
import br.com.almoxarifado.model.AtendimentoSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AtendimentoSolicitacaoRepository extends JpaRepository<AtendimentoSolicitacao,Integer> {
    java.util.Optional<AtendimentoSolicitacao> findByChaveIdempotencia(String chave);
    java.util.List<AtendimentoSolicitacao> findBySolicitacaoIdOrderByIdAsc(Integer id);
}
