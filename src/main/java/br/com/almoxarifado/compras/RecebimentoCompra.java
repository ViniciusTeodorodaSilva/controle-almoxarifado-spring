package br.com.almoxarifado.compras;

import br.com.almoxarifado.model.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(
    name = "bes_recebimento_compra",
    indexes = {
      @Index(name = "ix_recebimento_pedido", columnList = "pedido_id,data_hora"),
      @Index(name = "ix_recebimento_local", columnList = "almoxarifado_id,data_hora")
    })
public class RecebimentoCompra {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  public Integer getId() {
    return id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  PedidoCompra pedido;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "almoxarifado_id", nullable = false)
  Almoxarifado almoxarifado;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "responsavel_id", nullable = false)
  Funcionario responsavel;

  @Column(name = "recebido_por", nullable = false)
  Long recebidoPor;

  @Column(nullable = false, length = 80)
  String recebidoPorNome;

  @Column(nullable = false)
  LocalDateTime dataHora;

  @Column(length = 1000)
  String observacao;

  @Column(nullable = false, length = 100, unique = true)
  String chaveIdempotencia;

  @Column(nullable = false, length = 64)
  String assinaturaPayload;

  @OneToMany(mappedBy = "recebimento", cascade = CascadeType.ALL)
  @OrderBy("id ASC")
  List<ItemRecebimentoCompra> itens = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recebido_por", insertable = false, updatable = false)
  private br.com.almoxarifado.security.Usuario atorReferencia;

  public void setId(Integer v) {
    id = v;
  }

  public PedidoCompra getPedido() {
    return pedido;
  }

  public void setPedido(PedidoCompra v) {
    pedido = v;
  }

  public Almoxarifado getAlmoxarifado() {
    return almoxarifado;
  }

  public void setAlmoxarifado(Almoxarifado v) {
    almoxarifado = v;
  }

  public Funcionario getResponsavel() {
    return responsavel;
  }

  public void setResponsavel(Funcionario v) {
    responsavel = v;
  }

  public Long getRecebidoPor() {
    return recebidoPor;
  }

  public void setRecebidoPor(Long v) {
    recebidoPor = v;
  }

  public String getRecebidoPorNome() {
    return recebidoPorNome;
  }

  public void setRecebidoPorNome(String v) {
    recebidoPorNome = v;
  }

  public LocalDateTime getDataHora() {
    return dataHora;
  }

  public void setDataHora(LocalDateTime v) {
    dataHora = v;
  }

  public String getObservacao() {
    return observacao;
  }

  public void setObservacao(String v) {
    observacao = v;
  }

  public String getChaveIdempotencia() {
    return chaveIdempotencia;
  }

  public void setChaveIdempotencia(String v) {
    chaveIdempotencia = v;
  }

  public String getAssinaturaPayload() {
    return assinaturaPayload;
  }

  public void setAssinaturaPayload(String v) {
    assinaturaPayload = v;
  }

  public List<ItemRecebimentoCompra> getItens() {
    return itens;
  }

  public void setItens(List<ItemRecebimentoCompra> v) {
    itens = v;
  }
}
