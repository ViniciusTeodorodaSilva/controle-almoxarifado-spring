package br.com.almoxarifado.repository;

import br.com.almoxarifado.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Integer> {
    java.util.List<Movimentacao> findByProdutoId(Integer produtoId);
    java.util.List<Movimentacao> findByAlmoxarifadoId(Integer almoxarifadoId);
    @org.springframework.data.jpa.repository.Query("select m from Movimentacao m where m.solicitacao.id = :solicitacaoId")
    java.util.List<Movimentacao> findBySolicitacaoId(@org.springframework.data.repository.query.Param("solicitacaoId") Integer solicitacaoId);
    java.util.List<Movimentacao> findByTipo(br.com.almoxarifado.model.TipoMovimentacao tipo);
    boolean existsByProdutoId(Integer produtoId);
}
