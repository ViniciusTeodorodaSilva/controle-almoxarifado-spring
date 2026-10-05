package br.com.almoxarifado.service;
import br.com.almoxarifado.dto.*;
import br.com.almoxarifado.exception.RecursoNaoEncontradoException;
import br.com.almoxarifado.model.Estoque;
import br.com.almoxarifado.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import br.com.almoxarifado.security.Auditar;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
@org.springframework.transaction.annotation.Transactional
public class EstoqueInteligenteService {
    private final EstoqueRepository estoques;
    private final ProdutoRepository produtos;
    private final AlmoxarifadoRepository almoxarifados;
    public EstoqueInteligenteService(EstoqueRepository estoques, ProdutoRepository produtos, AlmoxarifadoRepository almoxarifados) {
        this.estoques = estoques; this.produtos = produtos; this.almoxarifados = almoxarifados;
    }
    public static void validarLimites(Double minimo, Double maximo) {
        if (minimo != null && (!Double.isFinite(minimo) || minimo < 0)) throw new IllegalArgumentException("Estoque mínimo deve ser finito e maior ou igual a zero");
        if (maximo != null && (!Double.isFinite(maximo) || maximo < 0)) throw new IllegalArgumentException("Estoque máximo deve ser finito e maior ou igual a zero");
        if (minimo != null && maximo != null && maximo < minimo) throw new IllegalArgumentException("Estoque máximo não pode ser menor que o mínimo");
    }
    @Transactional
    @PreAuthorize("@autorizacao.permite('ESTOQUE_CONFIGURAR')")
    @Auditar("ESTOQUEINTELIGENTE_CONFIGURAR")
    public AlertaEstoqueResponse configurar(Integer id, LimitesEstoqueInput dados) {
        if (dados == null) throw new IllegalArgumentException("Limites devem ser informados");
        validarLimites(dados.estoqueMinimo(), dados.estoqueMaximo());
        Estoque estoque = estoques.buscarPorIdParaAtualizacao(id).orElseThrow(() -> new RecursoNaoEncontradoException("Estoque não encontrado"));
        estoque.setEstoqueMinimo(dados.estoqueMinimo()); estoque.setEstoqueMaximo(dados.estoqueMaximo());
        return resumo(estoques.save(estoque));
    }
    @Transactional(readOnly = true)
    @PreAuthorize("@autorizacao.permite('ESTOQUE_LER')")
    public List<AlertaEstoqueResponse> alertas(Integer produtoId, Integer almoxarifadoId) {
        if (produtoId != null && !produtos.existsById(produtoId)) throw new RecursoNaoEncontradoException("Produto não encontrado");
        if (almoxarifadoId != null && !almoxarifados.existsById(almoxarifadoId)) throw new RecursoNaoEncontradoException("Almoxarifado não encontrado");
        return estoques.alertas(produtoId, almoxarifadoId).stream().map(this::resumo).toList();
    }
    @Transactional(readOnly = true)
    @PreAuthorize("@autorizacao.permite('ESTOQUE_LER')")
    public List<AlertaEstoqueResponse> reposicoes(Integer produtoId, Integer almoxarifadoId) {
        return alertas(produtoId, almoxarifadoId).stream().filter(e -> e.quantidadeSugerida() != null && e.quantidadeSugerida() > 0).toList();
    }
    private AlertaEstoqueResponse resumo(Estoque e) {
        boolean baixo = e.getEstoqueMinimo() != null && e.getQuantidade() <= e.getEstoqueMinimo();
        Double sugestao = baixo && e.getEstoqueMaximo() != null ? Math.max(0, e.getEstoqueMaximo() - e.getQuantidade()) : null;
        var p = e.getProduto(); var a = e.getAlmoxarifado();
        String unidade = p.getUnidadeMedidaConfigurada() == null ? p.getUnidadeMedida() : p.getUnidadeMedidaConfigurada().getSigla();
        return new AlertaEstoqueResponse(e.getId(), p.getId(), p.getCodigo(), p.getNome(), a.getId(), a.getNome(), unidade,
                e.getQuantidade(), e.getEstoqueMinimo(), e.getEstoqueMaximo(), sugestao);
    }
}
