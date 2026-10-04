package br.com.almoxarifado.repository;

import br.com.almoxarifado.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

    Optional<Estoque> findByProdutoIdAndAlmoxarifadoId(Integer produtoId, Integer almoxarifadoId);
}
