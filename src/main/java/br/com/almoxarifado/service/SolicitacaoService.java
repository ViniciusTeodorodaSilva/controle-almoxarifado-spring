package br.com.almoxarifado.service;

import br.com.almoxarifado.model.*;
import br.com.almoxarifado.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository repository;
    private final ItemSolicitacaoRepository itemSolicitacaoRepository;
    private final AlmoxarifadoRepository almoxarifadoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ProdutoRepository produtoRepository;

    public SolicitacaoService(SolicitacaoRepository repository, ItemSolicitacaoRepository itemSolicitacaoRepository, AlmoxarifadoRepository almoxarifadoRepository, FuncionarioRepository funcionarioRepository, ProdutoRepository produtoRepository) {
        this.repository = repository;
        this.itemSolicitacaoRepository = itemSolicitacaoRepository;
        this.almoxarifadoRepository = almoxarifadoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<Solicitacao> listar() {
        return repository.findAll();
    }

    public Optional<Solicitacao> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public Solicitacao cadastrar(
            Integer solicitanteId,
            Integer almoxarifadoId) {

        Funcionario solicitante = funcionarioRepository
                .findById(solicitanteId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitante não encontrado"));

        Almoxarifado almoxarifado = almoxarifadoRepository
                .findById(almoxarifadoId)
                .orElseThrow(() -> new IllegalArgumentException("Almoxarifado não encontrado"));

        Solicitacao solicitacao = new Solicitacao();

        solicitacao.setSolicitante(solicitante);
        solicitacao.setAlmoxarifado(almoxarifado);
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setDataSolicitacao(LocalDateTime.now());

        return repository.save(solicitacao);

    }

    public ItemSolicitacao adicionarItem(
            Integer solicitacaoId,
            Integer produtoId,
            double quantidade) {

        Solicitacao solicitacao = repository
                .findById(solicitacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));

        Produto produto = produtoRepository
                .findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        ItemSolicitacao item = new ItemSolicitacao();

        item.setSolicitacao(solicitacao);
        item.setProduto(produto);
        item.setQuantidade(quantidade);

        return itemSolicitacaoRepository.save(item);

    }

    public Solicitacao aprovar(Integer solicitacaoId) {
        Solicitacao solicitacao = repository.findById(solicitacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));

        if (!= solicitacao.getStatus(StatusSolicitacao.PENDENTE)) {
            throw new IllegalArgumentException("Status não é pendente");

    }
}
