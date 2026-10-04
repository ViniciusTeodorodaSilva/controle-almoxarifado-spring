package br.com.almoxarifado.service;

import br.com.almoxarifado.model.Produto;
import br.com.almoxarifado.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.almoxarifado.exception.*;

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
        if (produto == null || produto.getId() != null) {
            throw new IllegalArgumentException("Cadastro não permite informar ID");
        }

        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do produto inválido");
        }

        return repository.save(produto);
    }
    @Transactional
    public Produto atualizar(Integer id, Produto dados) {
        if (dados == null || dados.getNome() == null || dados.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome deve ser informado");
        }
        if (dados.getId() != null && !dados.getId().equals(id)) {
            throw new IllegalArgumentException("ID do corpo não pode diferir do ID da URL");
        }
        Produto atual = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));
        atual.setNome(dados.getNome());
        atual.setDescricao(dados.getDescricao());
        atual.setUnidadeMedida(dados.getUnidadeMedida());
        atual.setCategoria(dados.getCategoria());
        atual.setTipoControle(dados.getTipoControle());
        return repository.save(atual);
    }

}
