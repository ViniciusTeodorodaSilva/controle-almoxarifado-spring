package br.com.almoxarifado.compras;

import jakarta.persistence.LockModeType;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface FornecedorRepository
    extends JpaRepository<Fornecedor, Integer>, JpaSpecificationExecutor<Fornecedor> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select x from Fornecedor x where x.id=:id")
  Optional<Fornecedor> bloquear(@Param("id") Integer id);

  boolean existsByDocumentoAndIdNot(String documento, Integer id);
}
