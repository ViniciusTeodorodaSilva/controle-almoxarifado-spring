package br.com.almoxarifado.service;

import br.com.almoxarifado.model.Movimentacao;
import br.com.almoxarifado.repository.*;
import br.com.almoxarifado.model.TipoMovimentacao;
import org.springframework.stereotype.Service;
import br.com.almoxarifado.exception.*;

import java.util.List;
import java.util.Optional;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository repository;

    private final ProdutoRepository produtos;
    private final AlmoxarifadoRepository almoxarifados;
    private final SolicitacaoRepository solicitacoes;

    public MovimentacaoService(MovimentacaoRepository repository, ProdutoRepository produtos,
                              AlmoxarifadoRepository almoxarifados, SolicitacaoRepository solicitacoes) {
        this.repository = repository;
        this.produtos = produtos;
        this.almoxarifados = almoxarifados;
        this.solicitacoes = solicitacoes;
    }

    public List<Movimentacao> listar() {
        return repository.findAll();
    }

    public Optional<Movimentacao> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public List<Movimentacao> consultarPorProduto(Integer id) {
        if (!produtos.existsById(id)) {
            throw new RecursoNaoEncontradoException("Produto não encontrado");
        }
        return repository.findByProdutoId(id);
    }
    public List<Movimentacao> consultarPorAlmoxarifado(Integer id) {
        if (!almoxarifados.existsById(id)) {
            throw new RecursoNaoEncontradoException("Almoxarifado não encontrado");
        }
        return repository.findByAlmoxarifadoId(id);
    }
    public List<Movimentacao> consultarPorSolicitacao(Integer id) {
        if (!solicitacoes.existsById(id)) {
            throw new RecursoNaoEncontradoException("Solicitação não encontrada");
        }
        return repository.findBySolicitacaoId(id);
    }
    public List<Movimentacao> consultarPorTipo(TipoMovimentacao tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo deve ser informado");
        }
        return repository.findByTipo(tipo);
    }
}
