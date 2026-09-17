package br.com.almoxarifado.service;

import br.com.almoxarifado.model.Produto;
import br.com.almoxarifado.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public List<Produto> listar() {
        return repository.findAll();
    }

    public Optional<Produto> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public Produto cadastrar(Produto produto) {

        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do produto inválido");
        }

        if (produto.getQuantidade() < 0) {
            throw new IllegalArgumentException("Quantidade inválida");
        }

        return repository.save(produto);
    }
}