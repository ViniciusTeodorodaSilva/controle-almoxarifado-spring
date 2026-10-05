package br.com.almoxarifado.service;

import br.com.almoxarifado.model.Almoxarifado;
import br.com.almoxarifado.repository.AlmoxarifadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import br.com.almoxarifado.security.Auditar;
import org.springframework.transaction.annotation.Transactional;
import br.com.almoxarifado.exception.*;

import java.util.List;
import java.util.Optional;

@Service
@org.springframework.transaction.annotation.Transactional
public class AlmoxarifadoService {

    private final AlmoxarifadoRepository repository;

    public AlmoxarifadoService(AlmoxarifadoRepository repository) {
        this.repository = repository;
    }
    @PreAuthorize("@autorizacao.permite('ALMOXARIFADO_LER')")

    public List<Almoxarifado> listar() {
        return repository.findAll();
    }
    @PreAuthorize("@autorizacao.permite('ALMOXARIFADO_LER')")

    public Optional<Almoxarifado> buscarPorId(Integer id) {
        return repository.findById(id);
    }
    @PreAuthorize("@autorizacao.permite('ALMOXARIFADO_GERENCIAR')")
    @Auditar("ALMOXARIFADO_CADASTRAR")

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
    @PreAuthorize("@autorizacao.permite('ALMOXARIFADO_GERENCIAR')")
    @Auditar("ALMOXARIFADO_ATUALIZAR")
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
