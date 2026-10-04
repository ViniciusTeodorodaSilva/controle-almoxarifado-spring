package br.com.almoxarifado.service;

import br.com.almoxarifado.model.Funcionario;
import br.com.almoxarifado.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;

    public FuncionarioService(FuncionarioRepository repository) {
        this.repository = repository;
    }

    public List<Funcionario> listar() {
        return repository.findAll();
    }

    public Optional<Funcionario> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public Funcionario cadastrar(Funcionario funcionario) {

        if (funcionario.getNome() == null || funcionario.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do funcionário inválido");
        }

        if (funcionario.getMatricula() == null || funcionario.getMatricula().isBlank()) {
            throw new IllegalArgumentException("Matrícula do funcionário inválida");
        }

        return repository.save(funcionario);
    }
}