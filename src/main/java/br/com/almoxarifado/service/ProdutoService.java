package br.com.almoxarifado.service;

import br.com.almoxarifado.model.Produto;
import br.com.almoxarifado.repository.*;
import br.com.almoxarifado.model.*;
import java.util.UUID;
import java.util.Objects;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import br.com.almoxarifado.security.Auditar;
import org.springframework.transaction.annotation.Transactional;
import br.com.almoxarifado.exception.*;

import java.util.List;
import java.util.Optional;

@Service
@org.springframework.transaction.annotation.Transactional
public class ProdutoService {

    private final ProdutoRepository repository;

    private final CategoriaMaterialRepository categorias;
    private final UnidadeMedidaRepository unidades;
    private final EstoqueRepository estoques;
    private final ItemSolicitacaoRepository itens;
    private final MovimentacaoRepository movimentos;

    public ProdutoService(ProdutoRepository repository, CategoriaMaterialRepository categorias,
            UnidadeMedidaRepository unidades, EstoqueRepository estoques,
            ItemSolicitacaoRepository itens, MovimentacaoRepository movimentos) {
        this.repository = repository;
        this.categorias = categorias;
        this.unidades = unidades;
        this.estoques = estoques;
        this.itens = itens;
        this.movimentos = movimentos;
    }
    @PreAuthorize("@autorizacao.permite('PRODUTO_LER')")

    public List<Produto> listar() {
        return repository.findAll();
    }
    @PreAuthorize("@autorizacao.permite('PRODUTO_LER')")

    public Optional<Produto> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    @PreAuthorize("@autorizacao.permite('PRODUTO_GERENCIAR')")
    @Auditar("PRODUTO_CADASTRAR")
    public Produto cadastrar(Produto produto) {
        if (produto == null || produto.getId() != null) {
            throw new IllegalArgumentException("Cadastro não permite informar ID");
        }

        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do produto inválido");
        }

        preparar(produto, null);
        return repository.saveAndFlush(produto);
    }
    @Transactional
    @PreAuthorize("@autorizacao.permite('PRODUTO_GERENCIAR')")
    @Auditar("PRODUTO_ATUALIZAR")
    public Produto atualizar(Integer id, Produto dados) {
        if (dados == null || dados.getNome() == null || dados.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome deve ser informado");
        }
        if (dados.getId() != null && !dados.getId().equals(id)) {
            throw new IllegalArgumentException("ID do corpo não pode diferir do ID da URL");
        }
        Produto atual = repository.buscarParaAtualizacao(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));
        preparar(dados, atual);
        atual.setCodigo(dados.getCodigo());
        atual.setEspecificacaoTecnica(dados.getEspecificacaoTecnica());
        if (dados.isAtivoInformado()) atual.setAtivo(dados.isAtivo());
        atual.setCategoriaMaterial(dados.getCategoriaMaterial());
        atual.setUnidadeMedidaConfigurada(dados.getUnidadeMedidaConfigurada());
        atual.setNome(dados.getNome());
        atual.setDescricao(dados.getDescricao());
        atual.setUnidadeMedida(dados.getUnidadeMedida());
        atual.setCategoria(dados.getCategoria());
        atual.setTipoControle(dados.getTipoControle());
        return repository.saveAndFlush(atual);
    }

    private void preparar(Produto dados, Produto atual) {
        if (dados.getNome().length() > 255 || (dados.getDescricao() != null && dados.getDescricao().length() > 255)
                || (dados.getEspecificacaoTecnica() != null && dados.getEspecificacaoTecnica().length() > 2000)
                || (dados.getCategoria() != null && dados.getCategoria().length() > 255)
                || (dados.getUnidadeMedida() != null && dados.getUnidadeMedida().length() > 255)
                || (dados.getTipoControle() != null && dados.getTipoControle().length() > 255)) {
            throw new IllegalArgumentException("Campos excedem o tamanho permitido");
        }
        String codigo = dados.getCodigo();
        if (codigo == null) codigo = atual == null ? "BES-" + UUID.randomUUID() : atual.getCodigo();
        if (codigo == null) codigo = "BES-" + UUID.randomUUID();
        codigo = NormalizacaoCatalogo.identificador(codigo);
        if (atual == null ? repository.existsByCodigo(codigo) : repository.existsByCodigoAndIdNot(codigo, atual.getId())) {
            throw new ConflitoException("Código de produto já cadastrado");
        }
        dados.setCodigo(codigo);
        CategoriaMaterial categoria = dados.getCategoriaMaterial();
        if (categoria == null && atual != null) categoria = atual.getCategoriaMaterial();
        if (categoria != null) {
            if (categoria.getId() == null) throw new IllegalArgumentException("Categoria deve informar ID");
            categoria = categorias.findById(categoria.getId()).orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada"));
            boolean mesma = atual != null && atual.getCategoriaMaterial() != null && atual.getCategoriaMaterial().getId().equals(categoria.getId());
            if (!categoria.isAtivo() && !mesma) throw new IllegalArgumentException("Categoria inativa");
            dados.setCategoria(categoria.getNome());
        }
        dados.setCategoriaMaterial(categoria);
        UnidadeMedida unidade = dados.getUnidadeMedidaConfigurada();
        if (unidade == null && atual != null) unidade = atual.getUnidadeMedidaConfigurada();
        if (unidade != null) {
            if (unidade.getId() == null) throw new IllegalArgumentException("Unidade deve informar ID");
            unidade = unidades.findById(unidade.getId()).orElseThrow(() -> new RecursoNaoEncontradoException("Unidade não encontrada"));
            boolean mesma = atual != null && atual.getUnidadeMedidaConfigurada() != null && atual.getUnidadeMedidaConfigurada().getId().equals(unidade.getId());
            if (!unidade.isAtivo() && !mesma) throw new IllegalArgumentException("Unidade inativa");
            dados.setUnidadeMedida(unidade.getSigla());
        }
        dados.setUnidadeMedidaConfigurada(unidade);
        if (atual != null) {
            Integer antes = atual.getUnidadeMedidaConfigurada() == null ? null : atual.getUnidadeMedidaConfigurada().getId();
            Integer depois = unidade == null ? null : unidade.getId();
            if (!Objects.equals(antes, depois) && (estoques.existsByProdutoId(atual.getId())
                    || itens.existsByProdutoId(atual.getId()) || movimentos.existsByProdutoId(atual.getId()))) {
                throw new ConflitoException("Unidade de produto com estoque ou histórico não pode ser alterada");
            }
        }
    }
    @PreAuthorize("@autorizacao.permite('PRODUTO_LER')")

    public List<Produto> listar(Boolean ativo) {
        return ativo == null ? listar() : repository.findByAtivo(ativo);
    }
    @PreAuthorize("@autorizacao.permite('PRODUTO_LER')")

    public List<Produto> buscar(String termo, Integer categoriaId, Boolean ativo) {
        if (categoriaId != null && !categorias.existsById(categoriaId)) throw new RecursoNaoEncontradoException("Categoria não encontrada");
        return pesquisar(termo, categoriaId, ativo, false);
    }
    @PreAuthorize("@autorizacao.permite('PRODUTO_LER')")

    public List<Produto> equivalentes(String termo) {
        if (termo == null || termo.strip().length() < 3) throw new IllegalArgumentException("Informe pelo menos 3 caracteres para comparar nome/descrição");
        return pesquisar(termo, null, null, true);
    }

    private List<Produto> pesquisar(String termo, Integer categoriaId, Boolean ativo, boolean equivalencia) {
        return repository.findAll((root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();
            if (ativo != null) filtros.add(cb.equal(root.get("ativo"), ativo));
            if (categoriaId != null) filtros.add(cb.equal(root.get("categoriaMaterial").get("id"), categoriaId));
            if (termo != null && !termo.isBlank()) {
                String literal = termo.strip().toLowerCase(java.util.Locale.ROOT)
                        .replace("!", "!!").replace("%", "!%").replace("_", "!_");
                String padrao = "%" + literal + "%";
                List<Predicate> textos = new ArrayList<>();
                textos.add(cb.like(cb.lower(root.get("nome")), padrao, '!'));
                textos.add(cb.like(cb.lower(root.get("descricao")), padrao, '!'));
                if (!equivalencia) {
                    textos.add(cb.like(cb.lower(root.get("codigo")), padrao, '!'));
                    textos.add(cb.like(cb.lower(root.get("especificacaoTecnica")), padrao, '!'));
                    textos.add(cb.like(cb.lower(root.get("categoria")), padrao, '!'));
                    textos.add(cb.like(cb.lower(root.join("categoriaMaterial", JoinType.LEFT).get("nome")), padrao, '!'));
                }
                filtros.add(cb.or(textos.toArray(Predicate[]::new)));
            }
            query.orderBy(cb.asc(root.get("nome")), cb.asc(root.get("id")));
            return cb.and(filtros.toArray(Predicate[]::new));
        });
    }
}
