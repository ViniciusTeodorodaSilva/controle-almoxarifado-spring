package br.com.almoxarifado.service;
import br.com.almoxarifado.model.CategoriaMaterial;
import br.com.almoxarifado.repository.*;
import br.com.almoxarifado.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CategoriaMaterialService {
    private final CategoriaMaterialRepository repository;
    public CategoriaMaterialService(CategoriaMaterialRepository repository) {
        this.repository = repository;
    }
    public List<CategoriaMaterial> listar(Boolean ativo) {
        return ativo == null ? repository.findAll() : repository.findByAtivo(ativo);
    }
    public CategoriaMaterial buscar(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("CategoriaMaterial não encontrado"));
    }
    @Transactional
    public CategoriaMaterial cadastrar(CategoriaMaterial dados) {
        if (dados == null || dados.getId() != null) throw new IllegalArgumentException("Cadastro não aceita ID");
        validar(dados);
        if (repository.existsByNomeNormalizado(dados.getNomeNormalizado())) throw new ConflitoException("Cadastro equivalente já existe");
        return repository.saveAndFlush(dados);
    }
    @Transactional
    public CategoriaMaterial atualizar(Integer id, CategoriaMaterial dados) {
        if (dados == null || (dados.getId() != null && !id.equals(dados.getId()))) throw new IllegalArgumentException("ID inválido");
        validar(dados);
        CategoriaMaterial atual = buscar(id);
        if (repository.existsByNomeNormalizadoAndIdNot(dados.getNomeNormalizado(), id)) throw new ConflitoException("Cadastro equivalente já existe");
        atual.setDescricao(dados.getDescricao());
        atual.setNomeNormalizado(dados.getNomeNormalizado());
        atual.setNome(dados.getNome());
        atual.setAtivo(dados.isAtivo());
        return repository.saveAndFlush(atual);
    }
    private void validar(CategoriaMaterial dados) {
        String nome = NormalizacaoCatalogo.texto(dados.getNome());
        if (nome == null || nome.isBlank() || nome.length() > 255) {
            throw new IllegalArgumentException("Nome obrigatório, com até 255 caracteres");
        }
        dados.setNome(nome);
        dados.setNomeNormalizado(NormalizacaoCatalogo.chave(nome));
        if (dados.getDescricao() != null && dados.getDescricao().length() > 255) {
            throw new IllegalArgumentException("Descrição deve ter até 255 caracteres");
        }
    }
}
