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

    private double quantidade;

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

    public double getQuantidade() {
        return quantidade;
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

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
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
}