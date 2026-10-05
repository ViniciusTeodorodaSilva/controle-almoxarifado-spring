package br.com.almoxarifado.compras;

import br.com.almoxarifado.model.*;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(
    name = "bes_item_recebimento_compra",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_recebimento_item",
            columnNames = {"recebimento_id", "item_pedido_id"}),
    indexes = @Index(name = "ix_receb_item_pedido", columnList = "item_pedido_id"))
@org.hibernate.annotations.Check(
    constraints = "quantidade > 0 and saldo_anterior >= 0 and saldo_posterior >= saldo_anterior")
public class ItemRecebimentoCompra {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  public Integer getId() {
    return id;
  }

  public Integer getRecebimentoId() {
    return recebimento.getId();
  }

  public Integer getPedidoId() {
    return recebimento.getPedido().getId();
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  RecebimentoCompra recebimento;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  ItemPedidoCompra itemPedido;

  @Column(nullable = false)
  double quantidade;

  @Column(nullable = false)
  double saldoAnterior;

  @Column(nullable = false)
  double saldoPosterior;

  @OneToMany(mappedBy = "itemRecebimento", cascade = CascadeType.ALL)
  @org.hibernate.annotations.BatchSize(size = 100)
  List<DestinacaoRecebimento> destinacoes = new ArrayList<>();

  public void setId(Integer v) {
    id = v;
  }

  public RecebimentoCompra getRecebimento() {
    return recebimento;
  }

  public void setRecebimento(RecebimentoCompra v) {
    recebimento = v;
  }

  public ItemPedidoCompra getItemPedido() {
    return itemPedido;
  }

  public void setItemPedido(ItemPedidoCompra v) {
    itemPedido = v;
  }

  public double getQuantidade() {
    return quantidade;
  }

  public void setQuantidade(double v) {
    quantidade = v;
  }

  public double getSaldoAnterior() {
    return saldoAnterior;
  }

  public void setSaldoAnterior(double v) {
    saldoAnterior = v;
  }

  public double getSaldoPosterior() {
    return saldoPosterior;
  }

  public void setSaldoPosterior(double v) {
    saldoPosterior = v;
  }

  public List<DestinacaoRecebimento> getDestinacoes() {
    return destinacoes;
  }

  public void setDestinacoes(List<DestinacaoRecebimento> v) {
    destinacoes = v;
  }
}
