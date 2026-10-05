package br.com.almoxarifado.repository;
import br.com.almoxarifado.model.NecessidadeCompra;
import org.springframework.data.jpa.repository.JpaRepository;
public interface NecessidadeCompraRepository extends JpaRepository<NecessidadeCompra,Integer> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select n from NecessidadeCompra n where n.id=:id")
    java.util.Optional<NecessidadeCompra> bloquear(@org.springframework.data.repository.query.Param("id") Integer id);
    java.util.Optional<NecessidadeCompra> findByChaveIdempotencia(String chave);
    java.util.Optional<NecessidadeCompra> findByItemSolicitacaoId(Integer id);
    @org.springframework.data.jpa.repository.Query("select n from NecessidadeCompra n where (:status is null or n.status=:status) and (:produtoId is null or n.produto.id=:produtoId) and (:almoxarifadoId is null or n.almoxarifado.id=:almoxarifadoId) and (:solicitacaoId is null or n.solicitacao.id=:solicitacaoId) order by n.id desc")
    java.util.List<NecessidadeCompra> filtrar(@org.springframework.data.repository.query.Param("status") br.com.almoxarifado.model.StatusNecessidadeCompra status, @org.springframework.data.repository.query.Param("produtoId") Integer produtoId, @org.springframework.data.repository.query.Param("almoxarifadoId") Integer almoxarifadoId, @org.springframework.data.repository.query.Param("solicitacaoId") Integer solicitacaoId);
}
