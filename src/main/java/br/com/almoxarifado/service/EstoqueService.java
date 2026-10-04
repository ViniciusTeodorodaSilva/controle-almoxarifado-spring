package br.com.almoxarifado.service;

import br.com.almoxarifado.model.*;
import br.com.almoxarifado.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EstoqueService {

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

    public List<Estoque> listar() {
        return repository.findAll();
    }

    public Optional<Estoque> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public Estoque cadastrar(Estoque estoque) {

        Produto produto = produtoRepository
                .findById(estoque.getProduto().getId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        estoque.setProduto(produto);

        Almoxarifado almoxarifado = almoxarifadoRepository
                .findById(estoque.getAlmoxarifado().getId())
                .orElseThrow(() -> new IllegalArgumentException("Almoxarifado não encontrado"));

        estoque.setAlmoxarifado(almoxarifado);

        if (estoque.getQuantidade() < 0) {
            throw new IllegalArgumentException("Quantidade inválida");
        }

        return repository.save(estoque);

    }

    @Transactional
    public Estoque entradaEstoque(Integer produtoId, Integer almoxarifadoId, double quantidade, Integer solicitanteId, Integer responsavelId) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade de entrada deve ser maior que zero");
        }

        Estoque estoque = repository
                .findByProdutoIdAndAlmoxarifadoId(produtoId, almoxarifadoId)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado"));

        Funcionario solicitante = funcionarioRepository
                .findById(solicitanteId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitante não encontrado"));

        Funcionario responsavel = funcionarioRepository
                .findById(responsavelId)
                .orElseThrow(() -> new IllegalArgumentException("Responsável não encontrado"));

        double saldoAnterior = estoque.getQuantidade();

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
    public Estoque saidaEstoque(
            Integer produtoId,
            Integer almoxarifadoId,
            double quantidade,
            Integer solicitanteId,
            Integer responsavelId) {

        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                    "Quantidade de saída deve ser maior que zero"
            );
        }

        Estoque estoque = repository
                .findByProdutoIdAndAlmoxarifadoId(produtoId, almoxarifadoId)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado"));

        Funcionario solicitante = funcionarioRepository
                .findById(solicitanteId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitante não encontrado"));

        Funcionario responsavel = funcionarioRepository
                .findById(responsavelId)
                .orElseThrow(() -> new IllegalArgumentException("Responsável não encontrado"));

        if (quantidade > estoque.getQuantidade()) {
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

}
