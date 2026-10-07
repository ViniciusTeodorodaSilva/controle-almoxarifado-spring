package br.com.almoxarifado.obras;

import org.springframework.data.jpa.repository.*;

public interface ObraRepository
    extends JpaRepository<Obra, Integer>, JpaSpecificationExecutor<Obra> {}
