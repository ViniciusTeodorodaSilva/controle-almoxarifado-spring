package br.com.almoxarifado.service;
import br.com.almoxarifado.model.UnidadeMedida;
import br.com.almoxarifado.repository.*;
import br.com.almoxarifado.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UnidadeMedidaService {
    private final UnidadeMedidaRepository repository;
    private final ProdutoRepository produtos;
    public UnidadeMedidaService(UnidadeMedidaRepository repository, ProdutoRepository produtos) {
        this.repository = repository;
        this.produtos = produtos;
    }
    public List<UnidadeMedida> listar(Boolean ativo) {
        return ativo == null ? repository.findAll() : repository.findByAtivo(ativo);
    }
    public UnidadeMedida buscar(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("UnidadeMedida não encontrado"));
    }
    @Transactional
    public UnidadeMedida cadastrar(UnidadeMedida dados) {
        if (dados == null || dados.getId() != null) throw new IllegalArgumentException("Cadastro não aceita ID");
        validar(dados);
        if (repository.existsBySigla(dados.getSigla())) throw new ConflitoException("Cadastro equivalente já existe");
        return repository.saveAndFlush(dados);
    }
    @Transactional
    public UnidadeMedida atualizar(Integer id, UnidadeMedida dados) {
        if (dados == null || (dados.getId() != null && !id.equals(dados.getId()))) throw new IllegalArgumentException("ID inválido");
        validar(dados);
        UnidadeMedida atual = buscar(id);
        if (repository.existsBySiglaAndIdNot(dados.getSigla(), id)) throw new ConflitoException("Cadastro equivalente já existe");
        if (!atual.getSigla().equals(dados.getSigla()) && produtos.existsByUnidadeMedidaConfiguradaId(id)) {
            throw new ConflitoException("Sigla de unidade já vinculada não pode ser alterada");
        }
        if (atual.isPermiteFracionamento() != dados.isPermiteFracionamento()
                && produtos.existsByUnidadeMedidaConfiguradaId(id)) {
            throw new ConflitoException("Fracionamento de unidade já vinculada não pode ser alterado");
        }
        atual.setSigla(dados.getSigla());
        atual.setPermiteFracionamento(dados.isPermiteFracionamento());
        atual.setNome(dados.getNome());
        atual.setAtivo(dados.isAtivo());
        return repository.saveAndFlush(atual);
    }
    private void validar(UnidadeMedida dados) {
        String nome = NormalizacaoCatalogo.texto(dados.getNome());
        if (nome == null || nome.isBlank() || nome.length() > 255) {
            throw new IllegalArgumentException("Nome obrigatório, com até 255 caracteres");
        }
        dados.setNome(nome);
        dados.setSigla(NormalizacaoCatalogo.identificador(dados.getSigla()));
    }
}
