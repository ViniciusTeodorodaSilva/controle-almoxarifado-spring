package br.com.almoxarifado.compras;

import br.com.almoxarifado.model.*;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(
    name = "bes_destinacao_recebimento",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_destinacao_alocacao",
            columnNames = {"item_recebimento_id", "alocacao_id"}),
    indexes = @Index(name = "ix_destinacao_alocacao", columnList = "alocacao_id"))
@org.hibernate.annotations.Check(constraints = "quantidade > 0")
public class DestinacaoRecebimento {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  public Integer getId() {
    return id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  ItemRecebimentoCompra itemRecebimento;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  AlocacaoCompra alocacao;

  @Column(nullable = false)
  double quantidade;

  public void setId(Integer v) {
    id = v;
  }

  public ItemRecebimentoCompra getItemRecebimento() {
    return itemRecebimento;
  }

  public void setItemRecebimento(ItemRecebimentoCompra v) {
    itemRecebimento = v;
  }

  public AlocacaoCompra getAlocacao() {
    return alocacao;
  }

  public void setAlocacao(AlocacaoCompra v) {
    alocacao = v;
  }

  public double getQuantidade() {
    return quantidade;
  }

  public void setQuantidade(double v) {
    quantidade = v;
  }
}
