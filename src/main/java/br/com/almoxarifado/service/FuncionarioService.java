package br.com.almoxarifado.service;

import br.com.almoxarifado.model.Funcionario;
import br.com.almoxarifado.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import br.com.almoxarifado.security.Auditar;
import org.springframework.transaction.annotation.Transactional;
import br.com.almoxarifado.exception.*;

import java.util.List;
import java.util.Optional;

@Service
@org.springframework.transaction.annotation.Transactional
public class FuncionarioService {

    private final FuncionarioRepository repository;

    public FuncionarioService(FuncionarioRepository repository) {
        this.repository = repository;
    }
    @PreAuthorize("@autorizacao.permite('FUNCIONARIO_LER')")

    public List<Funcionario> listar() {
        return repository.findAll();
    }
    @PreAuthorize("@autorizacao.permite('FUNCIONARIO_LER')")

    public Optional<Funcionario> buscarPorId(Integer id) {
        return repository.findById(id);
    }
    @PreAuthorize("@autorizacao.permite('FUNCIONARIO_GERENCIAR')")
    @Auditar("FUNCIONARIO_CADASTRAR")

    public Funcionario cadastrar(Funcionario funcionario) {
        if (funcionario == null || funcionario.getId() != null) {
            throw new IllegalArgumentException("Cadastro não permite informar ID");
        }

        if (funcionario.getNome() == null || funcionario.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do funcionário inválido");
        }

        if (funcionario.getMatricula() == null || funcionario.getMatricula().isBlank()) {
            throw new IllegalArgumentException("Matrícula do funcionário inválida");
        }

        return repository.save(funcionario);
    }
    @Transactional
    @PreAuthorize("@autorizacao.permite('FUNCIONARIO_GERENCIAR')")
    @Auditar("FUNCIONARIO_ATUALIZAR")
    public Funcionario atualizar(Integer id, Funcionario dados) {
        if (dados == null || dados.getNome() == null || dados.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome deve ser informado");
        }
        if (dados.getMatricula() == null || dados.getMatricula().isBlank()) {
            throw new IllegalArgumentException("Matrícula deve ser informada");
        }
        if (dados.getId() != null && !dados.getId().equals(id)) {
            throw new IllegalArgumentException("ID do corpo não pode diferir do ID da URL");
        }
        Funcionario atual = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado"));
        atual.setNome(dados.getNome());
        atual.setMatricula(dados.getMatricula());
        atual.setFuncao(dados.getFuncao());
        return repository.save(atual);
    }

}
