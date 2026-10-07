package br.com.almoxarifado.service;

import br.com.almoxarifado.model.*;
import br.com.almoxarifado.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import br.com.almoxarifado.security.Auditar;
import br.com.almoxarifado.exception.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedHashMap;

@Service
@org.springframework.transaction.annotation.Transactional
public class SolicitacaoService {

    @org.springframework.beans.factory.annotation.Autowired private br.com.almoxarifado.obras.ContextoService contextos;
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
    @PreAuthorize("@autorizacao.permite('SOLICITACAO_LER')")
    public List<Solicitacao> listar(Integer obra,Integer os,Integer cc){return repository.filtrarContexto(obra,os,cc);}
    @PreAuthorize("@autorizacao.permite('SOLICITACAO_LER')")
    public List<Solicitacao> listar() {
        return repository.findAll();
    }
    @PreAuthorize("@autorizacao.permite('SOLICITACAO_LER')")

    public Optional<Solicitacao> buscarPorId(Integer id) {
        return repository.findById(id);
    }
    @PreAuthorize("@autorizacao.permite('SOLICITACAO_CRIAR')")
    @Auditar("SOLICITACAO_CADASTRAR")

    public Solicitacao cadastrar(
            Integer solicitanteId,
            Integer almoxarifadoId) {

        return cadastrar(solicitanteId,almoxarifadoId,null,null,null);
    }
    @PreAuthorize("@autorizacao.permite('SOLICITACAO_CRIAR')")
    @Auditar("SOLICITACAO_CADASTRAR")
    public Solicitacao cadastrar(Integer solicitanteId,Integer almoxarifadoId,Integer obraId,Integer ordemServicoId,Integer centroCustoId) {
        var contexto=contextos.resolver(obraId,ordemServicoId,centroCustoId);
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

        solicitacao.setContexto(contexto);
        solicitacao.setSolicitante(solicitante);
        solicitacao.setAlmoxarifado(almoxarifado);
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setDataSolicitacao(LocalDateTime.now());

        return repository.save(solicitacao);

    }

    @Transactional
    @PreAuthorize("@autorizacao.permite('SOLICITACAO_CRIAR')")
    @Auditar("SOLICITACAO_ADICIONARITEM")
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

        ValidacaoQuantidade.validar(produto, quantidade);

        ItemSolicitacao item = new ItemSolicitacao();

        item.setSolicitacao(solicitacao);
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setQuantidadeAtendida(0.0);

        return itemSolicitacaoRepository.save(item);

    }

    @Transactional
    @PreAuthorize("@autorizacao.permite('SOLICITACAO_APROVAR')")
    @Auditar("SOLICITACAO_APROVAR")
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

        // Valida a demanda autorizada. Aprovação não consulta, reserva ou retira estoque.
        Map<Integer, Double> quantidades = new TreeMap<>();
        for (ItemSolicitacao item : itens) {
            if (item.getProduto() == null || item.getProduto().getId() == null
                    || !Double.isFinite(item.getQuantidade()) || item.getQuantidade() <= 0) {
                throw new IllegalArgumentException("Item da solicitação inválido");
            }
            ValidacaoQuantidade.validar(item.getProduto(), item.getQuantidade());
            double total = quantidades.getOrDefault(item.getProduto().getId(), 0.0) + item.getQuantidade();
            if (!Double.isFinite(total)) {
                throw new IllegalArgumentException("Quantidade total inválida");
            }
            quantidades.put(item.getProduto().getId(), total);
        }

        solicitacao.setResponsavelAprovacao(responsavel);
        solicitacao.setDataAprovacao(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MICROS));

        solicitacao.getItens().size();
        solicitacao.setStatus(StatusSolicitacao.APROVADA);
        return repository.save(solicitacao);
    }

    @Transactional
    @PreAuthorize("@autorizacao.permite('SOLICITACAO_REJEITAR')")
    @Auditar("SOLICITACAO_REJEITAR")
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
    @PreAuthorize("@autorizacao.permite('SOLICITACAO_LER')")
    public List<Solicitacao> consultarPorStatus(StatusSolicitacao status) {
        if (status == null) {
            throw new IllegalArgumentException("Status deve ser informado");
        }
        return repository.findByStatus(status);
    }
    @PreAuthorize("@autorizacao.permite('SOLICITACAO_LER')")

    public List<Solicitacao> consultarPorFuncionario(Integer funcionarioId) {
        if (!funcionarioRepository.existsById(funcionarioId)) {
            throw new RecursoNaoEncontradoException("Funcionário não encontrado");
        }
        return repository.findBySolicitanteId(funcionarioId);
    }
    @PreAuthorize("@autorizacao.permite('MOVIMENTACAO_LER')")

    public List<Movimentacao> consultarMovimentacoes(Integer solicitacaoId) {
        if (!repository.existsById(solicitacaoId)) {
            throw new RecursoNaoEncontradoException("Solicitação não encontrada");
        }
        return movimentacaoRepository.findBySolicitacaoId(solicitacaoId);
    }
}
