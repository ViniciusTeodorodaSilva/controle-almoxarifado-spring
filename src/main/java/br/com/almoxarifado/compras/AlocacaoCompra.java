package br.com.almoxarifado.compras;

import br.com.almoxarifado.model.*;
import jakarta.persistence.*;
import java.util.*;

@Entity
@org.hibernate.annotations.BatchSize(size = 100)
@Table(
    name = "bes_alocacao_compra",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_alocacao_item_necessidade",
            columnNames = {"item_id", "necessidade_id"}),
    indexes = @Index(name = "ix_alocacao_necessidade", columnList = "necessidade_id"))
@org.hibernate.annotations.Check(
    constraints =
        "quantidade > 0 and quantidade_recebida >= 0 and quantidade_recebida <= quantidade")
public class AlocacaoCompra {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  public Integer getId() {
    return id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  ItemPedidoCompra item;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  NecessidadeCompra necessidade;

  @Column(nullable = false)
  double quantidade;

  @Column(nullable = false)
  double quantidadeRecebida;

  public void setId(Integer v) {
    id = v;
  }

  public ItemPedidoCompra getItem() {
    return item;
  }

  public void setItem(ItemPedidoCompra v) {
    item = v;
  }

  public NecessidadeCompra getNecessidade() {
    return necessidade;
  }

  public void setNecessidade(NecessidadeCompra v) {
    necessidade = v;
  }

  public double getQuantidade() {
    return quantidade;
  }

  public void setQuantidade(double v) {
    quantidade = v;
  }

  public double getQuantidadeRecebida() {
    return quantidadeRecebida;
  }

  public void setQuantidadeRecebida(double v) {
    quantidadeRecebida = v;
  }
}
