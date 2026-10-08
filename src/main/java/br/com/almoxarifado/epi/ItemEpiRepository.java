package br.com.almoxarifado.epi;
import java.util.List;
public interface ItemEpiRepository extends org.springframework.data.jpa.repository.JpaRepository<ItemEpi,Integer>,org.springframework.data.jpa.repository.JpaSpecificationExecutor<ItemEpi> {
 List<ItemEpi> findByRegistroIdOrderById(Integer id);
 List<ItemEpi> findByRegistroIdIn(List<Integer> ids);
}
