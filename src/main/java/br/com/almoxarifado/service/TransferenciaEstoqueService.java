package br.com.almoxarifado.service;
import br.com.almoxarifado.dto.*;
import br.com.almoxarifado.exception.*;
import br.com.almoxarifado.model.*;
import br.com.almoxarifado.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import br.com.almoxarifado.security.Auditar;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
@Service
@org.springframework.transaction.annotation.Transactional
public class TransferenciaEstoqueService {
    private final TransferenciaEstoqueRepository transferencias;
    private final EstoqueRepository estoques;
    private final ProdutoRepository produtos;
    private final AlmoxarifadoRepository almoxarifados;
    private final FuncionarioRepository funcionarios;
    private final MovimentacaoRepository movimentos;
    public TransferenciaEstoqueService(TransferenciaEstoqueRepository transferencias, EstoqueRepository estoques,
            ProdutoRepository produtos, AlmoxarifadoRepository almoxarifados, FuncionarioRepository funcionarios, MovimentacaoRepository movimentos) {
        this.transferencias = transferencias; this.estoques = estoques; this.produtos = produtos;
        this.almoxarifados = almoxarifados; this.funcionarios = funcionarios; this.movimentos = movimentos;
    }
    @Transactional
    @PreAuthorize("@autorizacao.permite('ESTOQUE_TRANSFERIR')")
    @Auditar("TRANSFERENCIAESTOQUE_CRIAR")
    public TransferenciaResponse criar(TransferenciaInput dados) {
        if (dados == null || dados.origemId() == null || dados.destinoId() == null || dados.responsavelId() == null
                || dados.origemId() <= 0 || dados.destinoId() <= 0 || dados.responsavelId() <= 0) throw new IllegalArgumentException("Origem, destino e responsável devem ser informados");
        if (dados.origemId().equals(dados.destinoId())) throw new IllegalArgumentException("Origem e destino devem ser diferentes");
        if (dados.itens() == null || dados.itens().isEmpty() || dados.itens().size() > 500) throw new IllegalArgumentException("Informe entre 1 e 500 itens");
        if (dados.observacao() != null && dados.observacao().length() > 1000) throw new IllegalArgumentException("Observação permite até 1000 caracteres");
        var origem = almoxarifados.findById(dados.origemId()).orElseThrow(() -> new RecursoNaoEncontradoException("Almoxarifado de origem não encontrado"));
        var destino = almoxarifados.findById(dados.destinoId()).orElseThrow(() -> new RecursoNaoEncontradoException("Almoxarifado de destino não encontrado"));
        var responsavel = funcionarios.findById(dados.responsavelId()).orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado"));
        Map<Integer, Double> quantidades = new TreeMap<>();
        for (var item : dados.itens()) {
            if (item == null || item.produtoId() == null || item.produtoId() <= 0 || item.quantidade() == null
                    || !Double.isFinite(item.quantidade()) || item.quantidade() <= 0) throw new IllegalArgumentException("Item deve informar produto e quantidade positiva e finita");
            if (quantidades.putIfAbsent(item.produtoId(), item.quantidade()) != null) throw new IllegalArgumentException("Produto duplicado na transferência");
        }
        // Primeiro todos os produtos em ordem crescente: protege criação de pares ausentes.
        Map<Integer, Produto> catalogo = new LinkedHashMap<>();
        for (var id : quantidades.keySet()) {
            var produto = produtos.buscarParaAtualizacao(id).orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));
            if (!produto.isAtivo()) throw new IllegalArgumentException("Produto inativo não pode ser transferido");
            ValidacaoQuantidade.validar(produto, quantidades.get(id)); catalogo.put(id, produto);
        }
        // Depois pares (produtoId, almoxarifadoId), sempre na mesma ordem, inclusive nas transferências inversas.
        Map<Integer, Estoque> saldosOrigem = new LinkedHashMap<>(), saldosDestino = new LinkedHashMap<>();
        var locais = new TreeMap<Integer, Almoxarifado>(); locais.put(origem.getId(), origem); locais.put(destino.getId(), destino);
        for (var produto : catalogo.values()) {
            for (var local : locais.values()) {
                var existente = estoques.buscarParaAtualizacao(produto.getId(), local.getId());
                Estoque saldo;
                if (existente.isPresent()) saldo = existente.get();
                else if (local.getId().equals(origem.getId())) throw new RecursoNaoEncontradoException("Estoque de origem não encontrado");
                else {
                    saldo = new Estoque(); saldo.setProduto(produto); saldo.setAlmoxarifado(destino); saldo.setQuantidade(0);
                    saldo = estoques.saveAndFlush(saldo);
                }
                if (!Double.isFinite(saldo.getQuantidade()) || saldo.getQuantidade() < 0) throw new IllegalArgumentException("Saldo de estoque inválido");
                if (local.getId().equals(origem.getId())) saldosOrigem.put(produto.getId(), saldo); else saldosDestino.put(produto.getId(), saldo);
            }
            double quantidade = quantidades.get(produto.getId());
            double antesOrigem = saldosOrigem.get(produto.getId()).getQuantidade(), antesDestino = saldosDestino.get(produto.getId()).getQuantidade();
            if (quantidade > antesOrigem) throw new IllegalArgumentException("Estoque insuficiente na origem");
            if (!Double.isFinite(antesDestino + quantidade) || antesDestino + quantidade == antesDestino
                    || antesOrigem - quantidade == antesOrigem) throw new IllegalArgumentException("Quantidade excede a precisão ou capacidade do saldo atual");
        }
        TransferenciaEstoque transferencia = new TransferenciaEstoque();
        transferencia.setAlmoxarifadoOrigem(origem); transferencia.setAlmoxarifadoDestino(destino);
        transferencia.setResponsavel(responsavel); transferencia.setDataHora(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MICROS));
        transferencia.setStatus(StatusTransferencia.CONCLUIDA);
        transferencia.setObservacao(dados.observacao() == null || dados.observacao().isBlank() ? null : dados.observacao().strip());
        for (var produto : catalogo.values()) {
            ItemTransferencia item = new ItemTransferencia(); item.setTransferencia(transferencia); item.setProduto(produto);
            item.setQuantidade(quantidades.get(produto.getId())); transferencia.getItens().add(item);
        }
        transferencia = transferencias.saveAndFlush(transferencia);
        for (var item : transferencia.getItens()) {
            var saida = saldosOrigem.get(item.getProduto().getId()); var entrada = saldosDestino.get(item.getProduto().getId());
            movimentar(saida, item.getQuantidade(), TipoMovimentacao.SAIDA, transferencia);
            movimentar(entrada, item.getQuantidade(), TipoMovimentacao.ENTRADA, transferencia);
        }
        movimentos.flush();
        return resposta(transferencia);
    }
    private void movimentar(Estoque estoque, double quantidade, TipoMovimentacao tipo, TransferenciaEstoque transferencia) {
        double anterior = estoque.getQuantidade();
        double posterior = tipo == TipoMovimentacao.SAIDA ? anterior - quantidade : anterior + quantidade;
        estoque.setQuantidade(posterior); estoques.save(estoque);
        Movimentacao movimento = new Movimentacao(); movimento.setProduto(estoque.getProduto()); movimento.setAlmoxarifado(estoque.getAlmoxarifado());
        movimento.setResponsavel(transferencia.getResponsavel()); movimento.setTransferencia(transferencia);
        movimento.setDataHora(transferencia.getDataHora()); movimento.setTipo(tipo); movimento.setQuantidade(quantidade);
        movimento.setSaldoAnterior(anterior); movimento.setSaldoPosterior(posterior); movimentos.save(movimento);
    }
    @Transactional(readOnly = true)
    @PreAuthorize("@autorizacao.permite('ESTOQUE_LER')")
    public List<TransferenciaResponse> listar(Integer origemId, Integer destinoId, Integer produtoId) {
        validarFiltros(origemId, destinoId, produtoId);
        return transferencias.filtrar(origemId, destinoId, produtoId).stream().map(this::resposta).toList();
    }
    private void validarFiltros(Integer origemId, Integer destinoId, Integer produtoId) {
        if (origemId != null && !almoxarifados.existsById(origemId)) throw new RecursoNaoEncontradoException("Origem não encontrada");
        if (destinoId != null && !almoxarifados.existsById(destinoId)) throw new RecursoNaoEncontradoException("Destino não encontrado");
        if (produtoId != null && !produtos.existsById(produtoId)) throw new RecursoNaoEncontradoException("Produto não encontrado");
    }
    @Transactional(readOnly = true)
    @PreAuthorize("@autorizacao.permite('ESTOQUE_LER')")
    public TransferenciaResponse buscar(Integer id) { return resposta(encontrar(id)); }
    private TransferenciaEstoque encontrar(Integer id) { return transferencias.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Transferência não encontrada")); }
    @Transactional(readOnly = true)
    @PreAuthorize("@autorizacao.permite('ESTOQUE_LER')")
    public List<MovimentacaoTransferenciaResponse> movimentacoes(Integer id) {
        var transferencia = encontrar(id);
        return movimentos.findByTransferenciaIdOrderByIdAsc(id).stream().map(m -> new MovimentacaoTransferenciaResponse(m.getId(),
                new MovimentacaoTransferenciaResponse.Produto(m.getProduto().getId(), m.getProduto().getCodigo(), m.getProduto().getNome(), unidade(m.getProduto())),
                referencia(m.getAlmoxarifado()), new TransferenciaResponse.Referencia(m.getResponsavel().getId(), m.getResponsavel().getNome()),
                m.getTipo().name(), m.getQuantidade(), m.getSaldoAnterior(), m.getSaldoPosterior(), m.getDataHora(), id,
                transferencia.getAlmoxarifadoOrigem().getId(), transferencia.getAlmoxarifadoDestino().getId())).toList();
    }
    private TransferenciaResponse resposta(TransferenciaEstoque t) {
        return new TransferenciaResponse(t.getId(), referencia(t.getAlmoxarifadoOrigem()), referencia(t.getAlmoxarifadoDestino()),
                new TransferenciaResponse.Referencia(t.getResponsavel().getId(), t.getResponsavel().getNome()), t.getDataHora(), t.getStatus().name(), t.getObservacao(),
                t.getItens().stream().map(i -> new TransferenciaResponse.Item(i.getId(), i.getProduto().getId(), i.getProduto().getCodigo(), i.getProduto().getNome(), unidade(i.getProduto()), i.getQuantidade())).toList());
    }
    private TransferenciaResponse.Referencia referencia(Almoxarifado a) { return new TransferenciaResponse.Referencia(a.getId(), a.getNome()); }
    private String unidade(Produto p) { return p.getUnidadeMedidaConfigurada() == null ? p.getUnidadeMedida() : p.getUnidadeMedidaConfigurada().getSigla(); }
}
