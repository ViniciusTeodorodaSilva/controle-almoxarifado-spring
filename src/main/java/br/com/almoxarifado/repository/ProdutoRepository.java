package br.com.almoxarifado.repository;

import br.com.almoxarifado.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository
        extends JpaRepository<Produto, Integer> {
}