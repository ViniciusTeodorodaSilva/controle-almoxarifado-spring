package br.com.almoxarifado.repository;
import br.com.almoxarifado.model.UnidadeMedida;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface UnidadeMedidaRepository extends JpaRepository<UnidadeMedida, Integer> {
    boolean existsBySigla(String chave);
    boolean existsBySiglaAndIdNot(String chave, Integer id);
    List<UnidadeMedida> findByAtivo(boolean ativo);
}
