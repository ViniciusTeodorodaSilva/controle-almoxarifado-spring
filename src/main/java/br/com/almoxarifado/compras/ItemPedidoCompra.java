package br.com.almoxarifado.compras;

import br.com.almoxarifado.model.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(
    name = "bes_item_pedido_compra",
    indexes = {@Index(name = "ix_item_compra_produto", columnList = "produto_id,pedido_id,ativo"), @Index(name="ix_bes_item_pedido_compra_ctx_obra",columnList="contexto_obra_id"),@Index(name="ix_bes_item_pedido_compra_ctx_ordem_servico",columnList="contexto_ordem_servico_id"),@Index(name="ix_bes_item_pedido_compra_ctx_centro_custo",columnList="contexto_centro_custo_id")})
@org.hibernate.annotations.Check(
    constraints =
        "quantidade_pedida > 0 and quantidade_recebida >= 0 and quantidade_recebida <="
            + " quantidade_pedida and quantidade_estoque >= 0 and valor_unitario >= 0")
public class ItemPedidoCompra {
 @jakarta.persistence.Embedded private br.com.almoxarifado.obras.ContextoOperacional contexto;
 @jakarta.persistence.ManyToOne(fetch=jakarta.persistence.FetchType.LAZY) @jakarta.persistence.JoinColumn(name="contexto_obra_id",insertable=false,updatable=false) @com.fasterxml.jackson.annotation.JsonIgnore private br.com.almoxarifado.obras.Obra contextoObraRef;
 @jakarta.persistence.ManyToOne(fetch=jakarta.persistence.FetchType.LAZY) @jakarta.persistence.JoinColumn(name="contexto_ordem_servico_id",insertable=false,updatable=false) @com.fasterxml.jackson.annotation.JsonIgnore private br.com.almoxarifado.obras.OrdemServico contextoOSRef;
 @jakarta.persistence.ManyToOne(fetch=jakarta.persistence.FetchType.LAZY) @jakarta.persistence.JoinColumn(name="contexto_centro_custo_id",insertable=false,updatable=false) @com.fasterxml.jackson.annotation.JsonIgnore private br.com.almoxarifado.obras.CentroCusto contextoCCRef;
 public br.com.almoxarifado.obras.ContextoOperacional getContexto(){return contexto;}
 public void setContexto(br.com.almoxarifado.obras.ContextoOperacional v){contexto=v;}

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
  @JoinColumn(nullable = false)
  Produto produto;

  @Column(nullable = false, length = 80)
  String codigo;

  @Column(nullable = false, length = 255)
  String nome;

  @Column(length = 255)
  String unidade;

  @Column(nullable = false)
  double quantidadePedida;

  @Column(nullable = false)
  double quantidadeRecebida;

  @Column(nullable = false)
  double quantidadeEstoque;

  @Column(nullable = false, precision = 19, scale = 4)
  BigDecimal valorUnitario;

  @Column(length = 1000)
  String observacao;

  @Column(nullable = false)
  boolean ativo = true;

  @OneToMany(
      mappedBy = "item",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.DETACH})
  @OrderBy("id ASC")
  @org.hibernate.annotations.BatchSize(size = 100)
  List<AlocacaoCompra> alocacoes = new ArrayList<>();

  public void setId(Integer v) {
    id = v;
  }

  public PedidoCompra getPedido() {
    return pedido;
  }

  public void setPedido(PedidoCompra v) {
    pedido = v;
  }

  public Produto getProduto() {
    return produto;
  }

  public void setProduto(Produto v) {
    produto = v;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setCodigo(String v) {
    codigo = v;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String v) {
    nome = v;
  }

  public String getUnidade() {
    return unidade;
  }

  public void setUnidade(String v) {
    unidade = v;
  }

  public double getQuantidadePedida() {
    return quantidadePedida;
  }

  public void setQuantidadePedida(double v) {
    quantidadePedida = v;
  }

  public double getQuantidadeRecebida() {
    return quantidadeRecebida;
  }

  public void setQuantidadeRecebida(double v) {
    quantidadeRecebida = v;
  }

  public double getQuantidadeEstoque() {
    return quantidadeEstoque;
  }

  public void setQuantidadeEstoque(double v) {
    quantidadeEstoque = v;
  }

  public BigDecimal getValorUnitario() {
    return valorUnitario;
  }

  public void setValorUnitario(BigDecimal v) {
    valorUnitario = v;
  }

  public String getObservacao() {
    return observacao;
  }

  public void setObservacao(String v) {
    observacao = v;
  }

  public boolean getAtivo() {
    return ativo;
  }

  public void setAtivo(boolean v) {
    ativo = v;
  }

  public List<AlocacaoCompra> getAlocacoes() {
    return alocacoes;
  }

  public void setAlocacoes(List<AlocacaoCompra> v) {
    alocacoes = v;
  }
}
