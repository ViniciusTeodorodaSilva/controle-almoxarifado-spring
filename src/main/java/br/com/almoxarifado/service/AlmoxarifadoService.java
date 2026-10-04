package br.com.almoxarifado.service;

import br.com.almoxarifado.model.Almoxarifado;
import br.com.almoxarifado.repository.AlmoxarifadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.almoxarifado.exception.*;

import java.util.List;
import java.util.Optional;

@Service
public class AlmoxarifadoService {

    private final AlmoxarifadoRepository repository;

    public AlmoxarifadoService(AlmoxarifadoRepository repository) {
        this.repository = repository;
    }

    public List<Almoxarifado> listar() {
        return repository.findAll();
    }

    public Optional<Almoxarifado> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public Almoxarifado cadastrar(Almoxarifado almoxarifado) {
        if (almoxarifado == null || almoxarifado.getId() != null) {
            throw new IllegalArgumentException("Cadastro não permite informar ID");
        }
        if (almoxarifado.getNome() == null || almoxarifado.getNome().isBlank()) {

            throw new IllegalArgumentException("Nome do almoxarifado inválido");
        }
        return repository.save(almoxarifado);
    }

    @Transactional
    public Almoxarifado atualizar(Integer id, Almoxarifado dados) {
        if (dados == null || dados.getNome() == null || dados.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome deve ser informado");
        }
        if (dados.getId() != null && !dados.getId().equals(id)) {
            throw new IllegalArgumentException("ID do corpo não pode diferir do ID da URL");
        }
        Almoxarifado atual = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Almoxarifado não encontrado"));
        atual.setNome(dados.getNome());
        return repository.save(atual);
    }

}
