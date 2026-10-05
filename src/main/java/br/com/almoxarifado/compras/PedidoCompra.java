package br.com.almoxarifado.compras;

import br.com.almoxarifado.model.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(
    name = "bes_pedido_compra",
    indexes = {
      @Index(name = "ix_pedido_status_data", columnList = "status,criado_em"),
      @Index(name = "ix_pedido_fornecedor", columnList = "fornecedor_id,criado_em")
    })
public class PedidoCompra {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  public Integer getId() {
    return id;
  }

  @Column(nullable = false, length = 50, unique = true)
  String numero;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  Fornecedor fornecedor;

  @Column(nullable = false, length = 160)
  String fornecedorNome;

  @Column(length = 14)
  String fornecedorDocumento;

  @Column(length = 160)
  String fornecedorContato;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "almoxarifado_id", nullable = false)
  Almoxarifado almoxarifado;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  StatusPedidoCompra status;

  @Column(nullable = false)
  LocalDateTime criadoEm;

  @Column(name = "criado_por", nullable = false)
  Long criadoPor;

  @Column(nullable = false, length = 80)
  String criadoPorNome;

  LocalDateTime submetidoEm;
  LocalDateTime aprovadoEm;

  @Column(name = "aprovado_por")
  Long aprovadoPor;

  @Column(length = 80)
  String aprovadoPorNome;

  LocalDateTime canceladoEm;

  @Column(name = "cancelado_por")
  Long canceladoPor;

  @Column(length = 80)
  String canceladoPorNome;

  @Column(length = 1000)
  String motivoCancelamento;

  @Column(length = 1000)
  String observacao;

  @OneToMany(
      mappedBy = "pedido",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.DETACH})
  @OrderBy("id ASC")
  List<ItemPedidoCompra> itens = new ArrayList<>();

  @Version long versao;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "criado_por", insertable = false, updatable = false)
  private br.com.almoxarifado.security.Usuario criadorReferencia;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "aprovado_por", insertable = false, updatable = false)
  private br.com.almoxarifado.security.Usuario aprovadorReferencia;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cancelado_por", insertable = false, updatable = false)
  private br.com.almoxarifado.security.Usuario canceladorReferencia;

  public void setId(Integer v) {
    id = v;
  }

  public String getNumero() {
    return numero;
  }

  public void setNumero(String v) {
    numero = v;
  }

  public Fornecedor getFornecedor() {
    return fornecedor;
  }

  public void setFornecedor(Fornecedor v) {
    fornecedor = v;
  }

  public String getFornecedorNome() {
    return fornecedorNome;
  }

  public void setFornecedorNome(String v) {
    fornecedorNome = v;
  }

  public String getFornecedorDocumento() {
    return fornecedorDocumento;
  }

  public void setFornecedorDocumento(String v) {
    fornecedorDocumento = v;
  }

  public String getFornecedorContato() {
    return fornecedorContato;
  }

  public void setFornecedorContato(String v) {
    fornecedorContato = v;
  }

  public Almoxarifado getAlmoxarifado() {
    return almoxarifado;
  }

  public void setAlmoxarifado(Almoxarifado v) {
    almoxarifado = v;
  }

  public StatusPedidoCompra getStatus() {
    return status;
  }

  public void setStatus(StatusPedidoCompra v) {
    status = v;
  }

  public LocalDateTime getCriadoEm() {
    return criadoEm;
  }

  public void setCriadoEm(LocalDateTime v) {
    criadoEm = v;
  }

  public Long getCriadoPor() {
    return criadoPor;
  }

  public void setCriadoPor(Long v) {
    criadoPor = v;
  }

  public String getCriadoPorNome() {
    return criadoPorNome;
  }

  public void setCriadoPorNome(String v) {
    criadoPorNome = v;
  }

  public LocalDateTime getSubmetidoEm() {
    return submetidoEm;
  }

  public void setSubmetidoEm(LocalDateTime v) {
    submetidoEm = v;
  }

  public LocalDateTime getAprovadoEm() {
    return aprovadoEm;
  }

  public void setAprovadoEm(LocalDateTime v) {
    aprovadoEm = v;
  }

  public Long getAprovadoPor() {
    return aprovadoPor;
  }

  public void setAprovadoPor(Long v) {
    aprovadoPor = v;
  }

  public String getAprovadoPorNome() {
    return aprovadoPorNome;
  }

  public void setAprovadoPorNome(String v) {
    aprovadoPorNome = v;
  }

  public LocalDateTime getCanceladoEm() {
    return canceladoEm;
  }

  public void setCanceladoEm(LocalDateTime v) {
    canceladoEm = v;
  }

  public Long getCanceladoPor() {
    return canceladoPor;
  }

  public void setCanceladoPor(Long v) {
    canceladoPor = v;
  }

  public String getCanceladoPorNome() {
    return canceladoPorNome;
  }

  public void setCanceladoPorNome(String v) {
    canceladoPorNome = v;
  }

  public String getMotivoCancelamento() {
    return motivoCancelamento;
  }

  public void setMotivoCancelamento(String v) {
    motivoCancelamento = v;
  }

  public String getObservacao() {
    return observacao;
  }

  public void setObservacao(String v) {
    observacao = v;
  }

  public List<ItemPedidoCompra> getItens() {
    return itens;
  }

  public void setItens(List<ItemPedidoCompra> v) {
    itens = v;
  }

  public long getVersao() {
    return versao;
  }

  public void setVersao(long v) {
    versao = v;
  }
}
