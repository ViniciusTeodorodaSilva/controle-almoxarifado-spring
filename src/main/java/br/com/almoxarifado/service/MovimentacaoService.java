package br.com.almoxarifado.service;

import br.com.almoxarifado.model.Movimentacao;
import br.com.almoxarifado.repository.*;
import br.com.almoxarifado.model.TipoMovimentacao;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import br.com.almoxarifado.security.Auditar;
import br.com.almoxarifado.exception.*;

import java.util.List;
import java.util.Optional;

@Service
@org.springframework.transaction.annotation.Transactional
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
    @PreAuthorize("@autorizacao.permite('MOVIMENTACAO_LER')")

    public List<Movimentacao> listar(Integer obra,Integer os,Integer cc){return repository.filtrarContexto(obra,os,cc);}
    @PreAuthorize("@autorizacao.permite('MOVIMENTACAO_LER')")
    public List<Movimentacao> listar() {
        return repository.findAll();
    }
    @PreAuthorize("@autorizacao.permite('MOVIMENTACAO_LER')")

    public Optional<Movimentacao> buscarPorId(Integer id) {
        return repository.findById(id);
    }
    @PreAuthorize("@autorizacao.permite('MOVIMENTACAO_LER')")

    public List<Movimentacao> consultarPorProduto(Integer id) {
        if (!produtos.existsById(id)) {
            throw new RecursoNaoEncontradoException("Produto não encontrado");
        }
        return repository.findByProdutoId(id);
    }
    @PreAuthorize("@autorizacao.permite('MOVIMENTACAO_LER')")
    public List<Movimentacao> consultarPorAlmoxarifado(Integer id) {
        if (!almoxarifados.existsById(id)) {
            throw new RecursoNaoEncontradoException("Almoxarifado não encontrado");
        }
        return repository.findByAlmoxarifadoId(id);
    }
    @PreAuthorize("@autorizacao.permite('MOVIMENTACAO_LER')")
    public List<Movimentacao> consultarPorSolicitacao(Integer id) {
        if (!solicitacoes.existsById(id)) {
            throw new RecursoNaoEncontradoException("Solicitação não encontrada");
        }
        return repository.findBySolicitacaoId(id);
    }
    @PreAuthorize("@autorizacao.permite('MOVIMENTACAO_LER')")
    public List<Movimentacao> consultarPorTipo(TipoMovimentacao tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo deve ser informado");
        }
        return repository.findByTipo(tipo);
    }
}
