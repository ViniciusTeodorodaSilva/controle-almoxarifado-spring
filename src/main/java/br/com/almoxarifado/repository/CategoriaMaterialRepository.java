package br.com.almoxarifado.repository;
import br.com.almoxarifado.model.CategoriaMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CategoriaMaterialRepository extends JpaRepository<CategoriaMaterial, Integer> {
    boolean existsByNomeNormalizado(String chave);
    boolean existsByNomeNormalizadoAndIdNot(String chave, Integer id);
    List<CategoriaMaterial> findByAtivo(boolean ativo);
}
