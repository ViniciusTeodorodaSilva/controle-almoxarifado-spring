package br.com.almoxarifado.repository;

import br.com.almoxarifado.model.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Solicitacao s where s.id = :id")
    Optional<Solicitacao> buscarParaAtualizacao(@Param("id") Integer id);
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"itens", "itens.produto", "solicitante", "almoxarifado"})
    java.util.List<Solicitacao> findByStatus(br.com.almoxarifado.model.StatusSolicitacao status);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"itens", "itens.produto", "solicitante", "almoxarifado"})
    java.util.List<Solicitacao> findBySolicitanteId(Integer funcionarioId);

    @Override
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"itens", "itens.produto", "solicitante", "almoxarifado"})
    java.util.List<Solicitacao> findAll();

    @Override
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"itens", "itens.produto", "solicitante", "almoxarifado"})
    Optional<Solicitacao> findById(Integer id);
}
