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

@Service
@org.springframework.transaction.annotation.Transactional
public class EstoqueService {

    /** EPI uses the same physical stock and movement infrastructure, never a parallel balance. */
    @PreAuthorize("@autorizacao.permite('EPI_ENTREGA_GERENCIAR')")
    public Movimentacao movimentarEpi(Integer produtoId, Integer almoxarifadoId, double quantidade,
            Integer funcionarioId, Integer responsavelId, TipoMovimentacao tipo,
            br.com.almoxarifado.obras.ContextoOperacional contexto) {
        if (tipo != TipoMovimentacao.SAIDA && tipo != TipoMovimentacao.ENTRADA) {
            throw new IllegalArgumentException("Movimento EPI invalido");
        }
        Produto produto = produtoRepository.buscarParaAtualizacao(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado"));
        ValidacaoQuantidade.validar(produto, quantidade);
        Estoque estoque = repository.buscarParaAtualizacao(produtoId, almoxarifadoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estoque nao encontrado"));
        double anterior = estoque.getQuantidade();
        double posterior = tipo == TipoMovimentacao.SAIDA ? anterior - quantidade : anterior + quantidade;
        // Zero only negligible binary residue, one millionth of the minimum EPI quantity.
        if (Math.abs(posterior) <= 1e-12) posterior = 0;
        if (!Double.isFinite(anterior) || anterior < 0 || !Double.isFinite(posterior)
                || posterior < 0 || posterior == anterior) {
            throw new ConflitoException("Saldo insuficiente ou quantidade fora da precisao do estoque");
        }
        // Permit ordinary binary roundoff, but never a discrepancy of half the minimum EPI unit.
        var delta = java.math.BigDecimal.valueOf(posterior)
                .subtract(java.math.BigDecimal.valueOf(anterior)).abs();
        if (delta.subtract(java.math.BigDecimal.valueOf(quantidade)).abs()
                .compareTo(new java.math.BigDecimal("0.0000005")) >= 0) {
            throw new ConflitoException("Variacao fora da precisao de seis decimais do EPI");
        }
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionario nao encontrado"));
        Funcionario responsavel = funcionarioRepository.findById(responsavelId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsavel nao encontrado"));
        Movimentacao movimento = new Movimentacao();
        movimento.setProduto(produto); movimento.setAlmoxarifado(estoque.getAlmoxarifado());
        movimento.setSolicitante(funcionario); movimento.setResponsavel(responsavel);
        movimento.setTipo(tipo); movimento.setQuantidade(quantidade);
        movimento.setSaldoAnterior(anterior); movimento.setSaldoPosterior(posterior);
        movimento.setDataHora(LocalDateTime.now()); movimento.setContexto(contexto);
        estoque.setQuantidade(posterior);
        repository.save(estoque);
        return movimentacaoRepository.saveAndFlush(movimento);
    }

    private final EstoqueRepository repository;
    private final ProdutoRepository produtoRepository;
    private final AlmoxarifadoRepository almoxarifadoRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public EstoqueService(EstoqueRepository repository, ProdutoRepository produtoRepository, AlmoxarifadoRepository almoxarifadoRepository, MovimentacaoRepository movimentacaoRepository, FuncionarioRepository funcionarioRepository) {
        this.repository = repository;
        this.produtoRepository = produtoRepository;
        this.almoxarifadoRepository = almoxarifadoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }
    @PreAuthorize("@autorizacao.permite('ESTOQUE_LER')")

    public List<Estoque> listar() {
        return repository.findAll();
    }
    @PreAuthorize("@autorizacao.permite('ESTOQUE_LER')")

    public Optional<Estoque> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    @PreAuthorize("@autorizacao.permite('ESTOQUE_MOVIMENTAR')")
    @Auditar("ESTOQUE_CADASTRAR")
    public Estoque cadastrar(Estoque estoque) {

        if (estoque == null || estoque.getProduto() == null || estoque.getProduto().getId() == null
                || estoque.getAlmoxarifado() == null || estoque.getAlmoxarifado().getId() == null) {
            throw new IllegalArgumentException("Produto e almoxarifado devem ser informados");
        }
        if (estoque.getId() != null) {
            throw new IllegalArgumentException("Cadastro de estoque não permite informar ID; utilize entrada ou saída");
        }

        // Bloquear o produto também protege o par quando ainda não existe linha de estoque.
        Produto produto = produtoRepository
                .buscarParaAtualizacao(estoque.getProduto().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));

        estoque.setProduto(produto);

        Almoxarifado almoxarifado = almoxarifadoRepository
                .findById(estoque.getAlmoxarifado().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Almoxarifado não encontrado"));

        estoque.setAlmoxarifado(almoxarifado);

        if (!Double.isFinite(estoque.getQuantidade()) || estoque.getQuantidade() != 0) {
            throw new IllegalArgumentException("Novo estoque deve iniciar zerado; utilize a operação de entrada");
        }

        if (repository.existsByProdutoIdAndAlmoxarifadoId(produto.getId(), almoxarifado.getId())) {
            throw new ConflitoException("Estoque já cadastrado para este produto e almoxarifado");
        }

        EstoqueInteligenteService.validarLimites(estoque.getEstoqueMinimo(), estoque.getEstoqueMaximo());
        return repository.save(estoque);

    }

    @Transactional
    @PreAuthorize("@autorizacao.permite('ESTOQUE_MOVIMENTAR')")
    @Auditar("ESTOQUE_ENTRADAESTOQUE")
    public Estoque entradaEstoque(Integer produtoId, Integer almoxarifadoId, double quantidade, Integer solicitanteId, Integer responsavelId) {
        if (produtoId == null || almoxarifadoId == null || solicitanteId == null || responsavelId == null) {
            throw new IllegalArgumentException("Produto, almoxarifado, solicitante e responsável devem ser informados");
        }
        if (!Double.isFinite(quantidade) || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade de entrada deve ser maior que zero");
        }

        // Produto antes do estoque, como atendimento/transferência: evita ordem inversa nos FKs.
        produtoRepository.buscarParaAtualizacao(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));
        Estoque estoque = repository
                .buscarParaAtualizacao(produtoId, almoxarifadoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estoque não encontrado"));

        ValidacaoQuantidade.validar(estoque.getProduto(), quantidade);

        Funcionario solicitante = funcionarioRepository
                .findById(solicitanteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitante não encontrado"));

        Funcionario responsavel = funcionarioRepository
                .findById(responsavelId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado"));

        double saldoAnterior = estoque.getQuantidade();

        if (!Double.isFinite(saldoAnterior) || saldoAnterior < 0
                || !Double.isFinite(saldoAnterior + quantidade)) {
            throw new IllegalArgumentException("Saldo de estoque inválido");
        }

        estoque.setQuantidade(estoque.getQuantidade() + quantidade);

        double saldoPosterior = estoque.getQuantidade();

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setProduto(estoque.getProduto());
        movimentacao.setAlmoxarifado(estoque.getAlmoxarifado());
        movimentacao.setQuantidade(quantidade);
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setTipo(TipoMovimentacao.ENTRADA);
        movimentacao.setSaldoAnterior(saldoAnterior);
        movimentacao.setSaldoPosterior(saldoPosterior);
        movimentacao.setSolicitante(solicitante);
        movimentacao.setResponsavel(responsavel);

        movimentacaoRepository.save(movimentacao);


        return repository.save(estoque);
    }

    @Transactional
    @PreAuthorize("@autorizacao.permite('ESTOQUE_MOVIMENTAR')")
    @Auditar("ESTOQUE_SAIDAESTOQUE")
    public Estoque saidaEstoque(
            Integer produtoId,
            Integer almoxarifadoId,
            double quantidade,
            Integer solicitanteId,
            Integer responsavelId) {

        if (produtoId == null || almoxarifadoId == null || solicitanteId == null || responsavelId == null) {
            throw new IllegalArgumentException("Produto, almoxarifado, solicitante e responsável devem ser informados");
        }
        if (!Double.isFinite(quantidade) || quantidade <= 0) {
            throw new IllegalArgumentException(
                    "Quantidade de saída deve ser maior que zero"
            );
        }

        // Produto antes do estoque, como atendimento/transferência: evita ordem inversa nos FKs.
        produtoRepository.buscarParaAtualizacao(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));
        Estoque estoque = repository
                .buscarParaAtualizacao(produtoId, almoxarifadoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estoque não encontrado"));

        ValidacaoQuantidade.validar(estoque.getProduto(), quantidade);

        Funcionario solicitante = funcionarioRepository
                .findById(solicitanteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitante não encontrado"));

        Funcionario responsavel = funcionarioRepository
                .findById(responsavelId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado"));

        if (!Double.isFinite(estoque.getQuantidade()) || quantidade > estoque.getQuantidade()) {
            throw new IllegalArgumentException("Estoque insuficiente");
        }

        double saldoAnterior = estoque.getQuantidade();

        estoque.setQuantidade(estoque.getQuantidade() - quantidade);

        double saldoPosterior = estoque.getQuantidade();

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setProduto(estoque.getProduto());
        movimentacao.setAlmoxarifado(estoque.getAlmoxarifado());
        movimentacao.setQuantidade(quantidade);
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setTipo(TipoMovimentacao.SAIDA);
        movimentacao.setSaldoAnterior(saldoAnterior);
        movimentacao.setSaldoPosterior(saldoPosterior);
        movimentacao.setSolicitante(solicitante);
        movimentacao.setResponsavel(responsavel);

        movimentacaoRepository.save(movimentacao);

        return repository.save(estoque);

    }
    @PreAuthorize("@autorizacao.permite('ESTOQUE_LER')")

    public List<Estoque> consultarPorProduto(Integer produtoId) {
        if (!produtoRepository.existsById(produtoId)) {
            throw new RecursoNaoEncontradoException("Produto não encontrado");
        }
        return repository.findByProdutoId(produtoId);
    }
    @PreAuthorize("@autorizacao.permite('ESTOQUE_LER')")

    public List<Estoque> consultarPorAlmoxarifado(Integer almoxarifadoId) {
        if (!almoxarifadoRepository.existsById(almoxarifadoId)) {
            throw new RecursoNaoEncontradoException("Almoxarifado não encontrado");
        }
        return repository.findByAlmoxarifadoId(almoxarifadoId);
    }
    @PreAuthorize("@autorizacao.permite('ESTOQUE_LER')")

    public Estoque consultarPorProdutoEAlmoxarifado(Integer produtoId, Integer almoxarifadoId) {
        return repository.findByProdutoIdAndAlmoxarifadoId(produtoId, almoxarifadoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estoque não encontrado"));
    }
}
