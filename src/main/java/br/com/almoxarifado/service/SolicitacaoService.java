package br.com.almoxarifado.service;

import br.com.almoxarifado.model.*;
import br.com.almoxarifado.repository.*;
import org.springframework.stereotype.Service;
import br.com.almoxarifado.exception.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedHashMap;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository repository;
    private final ItemSolicitacaoRepository itemSolicitacaoRepository;
    private final AlmoxarifadoRepository almoxarifadoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public SolicitacaoService(SolicitacaoRepository repository, ItemSolicitacaoRepository itemSolicitacaoRepository, AlmoxarifadoRepository almoxarifadoRepository, FuncionarioRepository funcionarioRepository, ProdutoRepository produtoRepository, EstoqueRepository estoqueRepository, MovimentacaoRepository movimentacaoRepository) {
        this.repository = repository;
        this.itemSolicitacaoRepository = itemSolicitacaoRepository;
        this.almoxarifadoRepository = almoxarifadoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
        this.movimentacaoRepository = movimentacaoRepository;
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

        if (solicitanteId == null || almoxarifadoId == null) {
            throw new IllegalArgumentException("Solicitante e almoxarifado devem ser informados");
        }
        Funcionario solicitante = funcionarioRepository
                .findById(solicitanteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitante não encontrado"));

        Almoxarifado almoxarifado = almoxarifadoRepository
                .findById(almoxarifadoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Almoxarifado não encontrado"));

        Solicitacao solicitacao = new Solicitacao();

        solicitacao.setSolicitante(solicitante);
        solicitacao.setAlmoxarifado(almoxarifado);
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setDataSolicitacao(LocalDateTime.now());

        return repository.save(solicitacao);

    }

    @Transactional
    public ItemSolicitacao adicionarItem(
            Integer solicitacaoId,
            Integer produtoId,
            double quantidade) {

        Solicitacao solicitacao = buscarPendenteParaAtualizacao(solicitacaoId);

        if (produtoId == null) {
            throw new IllegalArgumentException("Produto deve ser informado");
        }

        Produto produto = produtoRepository
                .findById(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));

        if (!Double.isFinite(quantidade) || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        ItemSolicitacao item = new ItemSolicitacao();

        item.setSolicitacao(solicitacao);
        item.setProduto(produto);
        item.setQuantidade(quantidade);

        return itemSolicitacaoRepository.save(item);

    }

    @Transactional
    public Solicitacao aprovar(Integer solicitacaoId, Integer responsavelId) {
        Solicitacao solicitacao = buscarPendenteParaAtualizacao(solicitacaoId);
        if (responsavelId == null) {
            throw new IllegalArgumentException("Responsável deve ser informado");
        }
        Funcionario responsavel = funcionarioRepository.findById(responsavelId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado"));
        List<ItemSolicitacao> itens = itemSolicitacaoRepository.findBySolicitacaoId(solicitacaoId);
        if (itens.isEmpty()) {
            throw new IllegalArgumentException("Solicitação sem itens não pode ser aprovada");
        }
        if (solicitacao.getAlmoxarifado() == null || solicitacao.getSolicitante() == null) {
            throw new IllegalArgumentException("Solicitação sem almoxarifado ou solicitante");
        }

        // Soma itens repetidos e bloqueia os estoques sempre na mesma ordem.
        Map<Integer, Double> quantidades = new TreeMap<>();
        for (ItemSolicitacao item : itens) {
            if (item.getProduto() == null || item.getProduto().getId() == null
                    || !Double.isFinite(item.getQuantidade()) || item.getQuantidade() <= 0) {
                throw new IllegalArgumentException("Item da solicitação inválido");
            }
            double total = quantidades.getOrDefault(item.getProduto().getId(), 0.0) + item.getQuantidade();
            if (!Double.isFinite(total)) {
                throw new IllegalArgumentException("Quantidade total inválida");
            }
            quantidades.put(item.getProduto().getId(), total);
        }

        Map<Integer, Estoque> estoques = new LinkedHashMap<>();
        for (Map.Entry<Integer, Double> entrada : quantidades.entrySet()) {
            Estoque estoque = estoqueRepository.buscarParaAtualizacao(
                    entrada.getKey(), solicitacao.getAlmoxarifado().getId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Estoque não encontrado"));
            if (!Double.isFinite(estoque.getQuantidade()) || estoque.getQuantidade() < entrada.getValue()) {
                throw new IllegalArgumentException("Estoque insuficiente ou saldo inválido");
            }
            estoques.put(entrada.getKey(), estoque);
        }

        for (Map.Entry<Integer, Estoque> entrada : estoques.entrySet()) {
            Estoque estoque = entrada.getValue();
            double quantidade = quantidades.get(entrada.getKey());
            double saldoAnterior = estoque.getQuantidade();
            estoque.setQuantidade(saldoAnterior - quantidade);
            estoqueRepository.save(estoque);

            Movimentacao movimentacao = new Movimentacao();
            movimentacao.setProduto(estoque.getProduto());
            movimentacao.setAlmoxarifado(estoque.getAlmoxarifado());
            movimentacao.setSolicitante(solicitacao.getSolicitante());
            movimentacao.setResponsavel(responsavel);
            movimentacao.setSolicitacao(solicitacao);
            movimentacao.setTipo(TipoMovimentacao.SAIDA);
            movimentacao.setQuantidade(quantidade);
            movimentacao.setSaldoAnterior(saldoAnterior);
            movimentacao.setSaldoPosterior(estoque.getQuantidade());
            movimentacao.setDataHora(LocalDateTime.now());
            movimentacaoRepository.save(movimentacao);
        }

        solicitacao.getItens().size();
        solicitacao.setStatus(StatusSolicitacao.APROVADA);
        return repository.save(solicitacao);
    }

    @Transactional
    public Solicitacao rejeitar(Integer solicitacaoId) {
        Solicitacao solicitacao = buscarPendenteParaAtualizacao(solicitacaoId);
        solicitacao.getItens().size();
        solicitacao.setStatus(StatusSolicitacao.REJEITADA);
        return repository.save(solicitacao);
    }

    private Solicitacao buscarPendenteParaAtualizacao(Integer solicitacaoId) {
        if (solicitacaoId == null) {
            throw new IllegalArgumentException("Solicitação deve ser informada");
        }
        Solicitacao solicitacao = repository.buscarParaAtualizacao(solicitacaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada"));
        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new ConflitoException("Somente solicitação PENDENTE permite esta operação");
        }
        return solicitacao;
    }
    public List<Solicitacao> consultarPorStatus(StatusSolicitacao status) {
        if (status == null) {
            throw new IllegalArgumentException("Status deve ser informado");
        }
        return repository.findByStatus(status);
    }

    public List<Solicitacao> consultarPorFuncionario(Integer funcionarioId) {
        if (!funcionarioRepository.existsById(funcionarioId)) {
            throw new RecursoNaoEncontradoException("Funcionário não encontrado");
        }
        return repository.findBySolicitanteId(funcionarioId);
    }

    public List<Movimentacao> consultarMovimentacoes(Integer solicitacaoId) {
        if (!repository.existsById(solicitacaoId)) {
            throw new RecursoNaoEncontradoException("Solicitação não encontrada");
        }
        return movimentacaoRepository.findBySolicitacaoId(solicitacaoId);
    }
}
