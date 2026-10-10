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
        double posterior;
        try { posterior = QuantidadesOperacionais.saldo(anterior, quantidade, tipo == TipoMovimentacao.ENTRADA); }
        catch (IllegalArgumentException e) { throw new ConflitoException(e.getMessage()); }
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
    private final br.com.almoxarifado.security.AuditoriaService audit;

    public EstoqueService(EstoqueRepository repository, ProdutoRepository produtoRepository, AlmoxarifadoRepository almoxarifadoRepository, MovimentacaoRepository movimentacaoRepository, FuncionarioRepository funcionarioRepository,
            br.com.almoxarifado.security.AuditoriaService audit) {
        this.repository = repository;
        this.produtoRepository = produtoRepository;
        this.almoxarifadoRepository = almoxarifadoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.audit = audit;
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
    public Estoque entradaEstoque(Integer produtoId, Integer almoxarifadoId, double quantidade, Integer solicitanteId, Integer responsavelId) {
        return entradaEstoque(produtoId, almoxarifadoId, quantidade, solicitanteId, responsavelId, null);
    }
    @Transactional
    @PreAuthorize("@autorizacao.permite('ESTOQUE_MOVIMENTAR')")
    public Estoque entradaEstoque(Integer produtoId, Integer almoxarifadoId, java.math.BigDecimal quantidade, Integer solicitanteId, Integer responsavelId, String chave) {
        return entradaEstoque(produtoId, almoxarifadoId, QuantidadesOperacionais.representar(quantidade), solicitanteId, responsavelId, chave);
    }
    @Transactional
    @PreAuthorize("@autorizacao.permite('ESTOQUE_MOVIMENTAR')")
    public Estoque saidaEstoque(Integer produtoId, Integer almoxarifadoId, java.math.BigDecimal quantidade, Integer solicitanteId, Integer responsavelId, String chave) {
        return saidaEstoque(produtoId, almoxarifadoId, QuantidadesOperacionais.representar(quantidade), solicitanteId, responsavelId, chave);
    }
    @Transactional
    @PreAuthorize("@autorizacao.permite('ESTOQUE_MOVIMENTAR')")
    public Estoque entradaEstoque(Integer produtoId, Integer almoxarifadoId, double quantidade, Integer solicitanteId, Integer responsavelId, String chave) {
        String hash = assinatura(chave, TipoMovimentacao.ENTRADA, produtoId, almoxarifadoId, quantidade, solicitanteId, responsavelId);
        if (produtoId == null || almoxarifadoId == null || solicitanteId == null || responsavelId == null) {
            throw new IllegalArgumentException("Produto, almoxarifado, solicitante e responsável devem ser informados");
        }
        if (!Double.isFinite(quantidade) || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade de entrada deve ser maior que zero");
        }

        // Produto antes do estoque, como atendimento/transferência: evita ordem inversa nos FKs.
        produtoRepository.buscarParaAtualizacao(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));
        Estoque repetido = replay(chave, hash);
        if (repetido != null) return repetido;
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

        estoque.setQuantidade(QuantidadesOperacionais.saldo(saldoAnterior, quantidade, true));

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

        movimentacao.setChaveIdempotencia(chave); movimentacao.setHashRequisicao(hash);
        movimentacaoRepository.saveAndFlush(movimentacao);
        audit.registrar("ESTOQUE_ENTRADAESTOQUE", "Movimentacao", movimentacao.getId().toString(), responsavelId,
                "quantidade=" + saldoAnterior, "quantidade=" + saldoPosterior);


        return repository.save(estoque);
    }

    @Transactional
    @PreAuthorize("@autorizacao.permite('ESTOQUE_MOVIMENTAR')")
    public Estoque saidaEstoque(
            Integer produtoId,
            Integer almoxarifadoId,
            double quantidade,
            Integer solicitanteId,
            Integer responsavelId) {
        return saidaEstoque(produtoId, almoxarifadoId, quantidade, solicitanteId, responsavelId, null);
    }
    @Transactional
    @PreAuthorize("@autorizacao.permite('ESTOQUE_MOVIMENTAR')")
    public Estoque saidaEstoque(Integer produtoId, Integer almoxarifadoId, double quantidade, Integer solicitanteId, Integer responsavelId, String chave) {
        String hash = assinatura(chave, TipoMovimentacao.SAIDA, produtoId, almoxarifadoId, quantidade, solicitanteId, responsavelId);

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
        Estoque repetido = replay(chave, hash);
        if (repetido != null) return repetido;
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

        estoque.setQuantidade(QuantidadesOperacionais.saldo(saldoAnterior, quantidade, false));

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

        movimentacao.setChaveIdempotencia(chave); movimentacao.setHashRequisicao(hash);
        movimentacaoRepository.saveAndFlush(movimentacao);
        audit.registrar("ESTOQUE_SAIDAESTOQUE", "Movimentacao", movimentacao.getId().toString(), responsavelId,
                "quantidade=" + saldoAnterior, "quantidade=" + saldoPosterior);

        return repository.save(estoque);

    }
    private String assinatura(String chave, TipoMovimentacao tipo, Integer produto, Integer local, double qtd, Integer solicitante, Integer responsavel) {
        if (chave == null) return null;
        if (!chave.matches("[A-Za-z0-9._:-]{16,100}")) throw new IllegalArgumentException("Chave de idempotência inválida");
        QuantidadesOperacionais.positiva(qtd);
        String canonico = tipo + "|" + produto + "|" + local + "|" + java.math.BigDecimal.valueOf(qtd).stripTrailingZeros().toPlainString() + "|" + solicitante + "|" + responsavel;
        try { return java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(canonico.getBytes(java.nio.charset.StandardCharsets.UTF_8))); }
        catch (java.security.NoSuchAlgorithmException e) { throw new IllegalStateException("SHA-256 indisponível", e); }
    }
    private Estoque replay(String chave, String hash) {
        if (chave == null) return null;
        // Current read only after the product lock; avoids replay from an old RR snapshot.
        var anterior = movimentacaoRepository.buscarReplayAtual(chave);
        if (anterior.isEmpty()) return null;
        var movimento = anterior.get();
        if (!hash.equals(movimento.getHashRequisicao())) throw new ConflitoException("Chave de idempotência já utilizada com outra movimentação");
        // Replay returns the operation's balance snapshot, never writes an old balance back.
        var atual = repository.findByProdutoIdAndAlmoxarifadoId(movimento.getProduto().getId(), movimento.getAlmoxarifado().getId()).orElseThrow();
        var resposta = new Estoque(); resposta.setId(atual.getId()); resposta.setProduto(movimento.getProduto());
        resposta.setAlmoxarifado(movimento.getAlmoxarifado()); resposta.setQuantidade(movimento.getSaldoPosterior());
        resposta.setEstoqueMinimo(atual.getEstoqueMinimo()); resposta.setEstoqueMaximo(atual.getEstoqueMaximo());
        return resposta;
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
