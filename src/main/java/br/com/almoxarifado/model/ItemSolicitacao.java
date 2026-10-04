package br.com.almoxarifado.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "item_solicitacao")
public class ItemSolicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "solicitacao_id")
    @JsonIgnore
    private Solicitacao solicitacao;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    private double quantidade;
    @Column(name="quantidade_atendida") private Double quantidadeAtendida;
    public Double getQuantidadeAtendida() { return quantidadeAtendida; }
    public void setQuantidadeAtendida(Double value) { quantidadeAtendida=value; }
    public double getQuantidadeSolicitada() { return quantidade; }
    // Null in pre-Bloco-3 rows means legacy fulfillment must be checked against linked movements.
    public Double getQuantidadePendente() { return quantidadeAtendida == null ? null : Math.max(0, java.math.BigDecimal.valueOf(quantidade).subtract(java.math.BigDecimal.valueOf(quantidadeAtendida)).doubleValue()); }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Solicitacao getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(Solicitacao solicitacao) {
        this.solicitacao = solicitacao;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }
}
