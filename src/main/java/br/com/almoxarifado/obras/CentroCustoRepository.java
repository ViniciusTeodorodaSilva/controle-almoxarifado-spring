package br.com.almoxarifado.obras;

import org.springframework.data.jpa.repository.*;

public interface CentroCustoRepository
    extends JpaRepository<CentroCusto, Integer>, JpaSpecificationExecutor<CentroCusto> {}
