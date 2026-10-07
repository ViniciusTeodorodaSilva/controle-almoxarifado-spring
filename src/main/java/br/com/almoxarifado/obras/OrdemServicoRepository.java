package br.com.almoxarifado.obras;

import org.springframework.data.jpa.repository.*;

public interface OrdemServicoRepository
    extends JpaRepository<OrdemServico, Integer>, JpaSpecificationExecutor<OrdemServico> {}
