package br.com.almoxarifado.epi;
import java.util.Optional;
public interface RegistroEpiRepository extends org.springframework.data.jpa.repository.JpaRepository<RegistroEpi,Integer>,org.springframework.data.jpa.repository.JpaSpecificationExecutor<RegistroEpi> {
 Optional<RegistroEpi> findByChaveIdempotencia(String chave);
}
