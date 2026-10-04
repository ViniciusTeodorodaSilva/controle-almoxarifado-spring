package br.com.almoxarifado.model;
import jakarta.persistence.*;
@Entity
@Table(name = "item_transferencia", uniqueConstraints = @UniqueConstraint(name = "uk_item_transferencia_produto", columnNames = {"transferencia_id", "produto_id"}))
public class ItemTransferencia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "transferencia_id", nullable = false) private TransferenciaEstoque transferencia;
    @ManyToOne @JoinColumn(name = "produto_id", nullable = false) private Produto produto;
    @Column(nullable = false) private double quantidade;
    public Integer getId() { return id; }
    public TransferenciaEstoque getTransferencia() { return transferencia; }
    public void setTransferencia(TransferenciaEstoque valor) { transferencia = valor; }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto valor) { produto = valor; }
    public double getQuantidade() { return quantidade; }
    public void setQuantidade(double valor) { quantidade = valor; }
}
