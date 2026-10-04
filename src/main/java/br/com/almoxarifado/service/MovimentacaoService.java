package br.com.almoxarifado.service;

import br.com.almoxarifado.model.Movimentacao;
import br.com.almoxarifado.repository.MovimentacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository repository;

    public MovimentacaoService(MovimentacaoRepository repository) {
        this.repository = repository;
    }

    public List<Movimentacao> listar() {
        return repository.findAll();
    }

    public Optional<Movimentacao> buscarPorId(Integer id) {
        return repository.findById(id);
    }

}
