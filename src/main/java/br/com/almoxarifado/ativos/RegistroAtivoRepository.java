package br.com.almoxarifado.ativos;
import org.springframework.data.jpa.repository.*;
import java.util.Optional;
public interface RegistroAtivoRepository extends JpaRepository<RegistroAtivo,Integer>,JpaSpecificationExecutor<RegistroAtivo> { Optional<RegistroAtivo> findByChaveIdempotencia(String chave); Optional<RegistroAtivo> findByOrigemId(Integer id); }
