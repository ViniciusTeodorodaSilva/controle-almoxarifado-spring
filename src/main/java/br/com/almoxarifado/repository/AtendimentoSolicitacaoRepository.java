package br.com.almoxarifado.repository;
import br.com.almoxarifado.model.AtendimentoSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AtendimentoSolicitacaoRepository extends JpaRepository<AtendimentoSolicitacao,Integer> {
    java.util.Optional<AtendimentoSolicitacao> findByChaveIdempotencia(String chave);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select a from AtendimentoSolicitacao a where a.chaveIdempotencia=:chave")
    java.util.Optional<AtendimentoSolicitacao> buscarReplayAtual(@org.springframework.data.repository.query.Param("chave") String chave);
    java.util.List<AtendimentoSolicitacao> findBySolicitacaoIdOrderByIdAsc(Integer id);
}
