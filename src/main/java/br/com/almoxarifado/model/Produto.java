package br.com.almoxarifado.model;

import jakarta.persistence.*;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;

    private String descricao;


    @Column(name = "unidade_medida")
    private String unidadeMedida;

    private String categoria;

    @Column(name = "tipo_controle")
    private String tipoControle;

    public Produto() {
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }


    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getTipoControle() {
        return tipoControle;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setTipoControle(String tipoControle) {
        this.tipoControle = tipoControle;
    }
    @Column(unique = true, length = 64)
    private String codigo;
    @Column(name = "especificacao_tecnica", length = 2000)
    private String especificacaoTecnica;
    private boolean ativo = true;
    @ManyToOne
    @JoinColumn(name = "categoria_material_id")
    private CategoriaMaterial categoriaMaterial;
    @ManyToOne
    @JoinColumn(name = "unidade_medida_id")
    private UnidadeMedida unidadeMedidaConfigurada;
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getEspecificacaoTecnica() { return especificacaoTecnica; }
    public void setEspecificacaoTecnica(String valor) { this.especificacaoTecnica = valor; }
    public boolean isAtivo() { return ativo; }
    @Transient
    @com.fasterxml.jackson.annotation.JsonIgnore
    private boolean ativoInformado;
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isAtivoInformado() { return ativoInformado; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; this.ativoInformado = true; }
    public CategoriaMaterial getCategoriaMaterial() { return categoriaMaterial; }
    public void setCategoriaMaterial(CategoriaMaterial categoria) { this.categoriaMaterial = categoria; }
    public UnidadeMedida getUnidadeMedidaConfigurada() { return unidadeMedidaConfigurada; }
    public void setUnidadeMedidaConfigurada(UnidadeMedida unidade) { this.unidadeMedidaConfigurada = unidade; }
}
