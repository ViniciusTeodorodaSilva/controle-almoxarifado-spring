package br.com.almoxarifado.ativos;
import org.springframework.data.jpa.repository.*;
public interface AtivoRepository extends JpaRepository<Ativo,Integer>,JpaSpecificationExecutor<Ativo> {}
