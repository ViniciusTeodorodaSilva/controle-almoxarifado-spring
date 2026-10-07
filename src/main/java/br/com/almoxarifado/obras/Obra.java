package br.com.almoxarifado.obras;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(
    name = "bes_obra",
    indexes = @Index(name = "ix_bes_obra_filtros", columnList = "status, cliente"))
@org.hibernate.annotations.Check(
    constraints = "status in ('PLANEJADA','ATIVA','SUSPENSA','CONCLUIDA','CANCELADA')")
public class Obra {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Version @JsonIgnore private long versao;

  @Column(nullable = false, unique = true, length = 50)
  private String codigo;

  @Column(nullable = false, length = 160)
  private String nome;

  @Column(length = 2000)
  private String descricao;

  @Column(length = 160)
  private String cliente;

  @Column(length = 160)
  private String localidade;

  @Column(length = 2000)
  private String observacao;

  @Enumerated(EnumType.STRING)
  @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR)
  @Column(nullable = false, length = 20)
  private StatusObra status;

  @Column(name = "responsavel_id")
  private Integer responsavelId;

  private LocalDate dataInicio;
  private LocalDate dataTerminoPrevisto;
  private LocalDate dataTerminoReal;

  @Column(nullable = false)
  private LocalDateTime criadoEm;

  @Column(nullable = false)
  private LocalDateTime atualizadoEm;

  @Column(nullable = false, name = "criado_por")
  private Long criadoPor;

  @Column(nullable = false, name = "alterado_por")
  private Long alteradoPor;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "responsavel_id", insertable = false, updatable = false)
  @JsonIgnore
  private br.com.almoxarifado.model.Funcionario responsavelRef;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "criado_por", insertable = false, updatable = false)
  @JsonIgnore
  private br.com.almoxarifado.security.Usuario criadorRef;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "alterado_por", insertable = false, updatable = false)
  @JsonIgnore
  private br.com.almoxarifado.security.Usuario editorRef;

  public Integer getId() {
    return id;
  }

  public void setId(Integer v) {
    id = v;
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

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String v) {
    descricao = v;
  }

  public String getCliente() {
    return cliente;
  }

  public void setCliente(String v) {
    cliente = v;
  }

  public String getLocalidade() {
    return localidade;
  }

  public void setLocalidade(String v) {
    localidade = v;
  }

  public String getObservacao() {
    return observacao;
  }

  public void setObservacao(String v) {
    observacao = v;
  }

  public StatusObra getStatus() {
    return status;
  }

  public void setStatus(StatusObra v) {
    status = v;
  }

  public Integer getResponsavelId() {
    return responsavelId;
  }

  public void setResponsavelId(Integer v) {
    responsavelId = v;
  }

  public LocalDate getDataInicio() {
    return dataInicio;
  }

  public void setDataInicio(LocalDate v) {
    dataInicio = v;
  }

  public LocalDate getDataTerminoPrevisto() {
    return dataTerminoPrevisto;
  }

  public void setDataTerminoPrevisto(LocalDate v) {
    dataTerminoPrevisto = v;
  }

  public LocalDate getDataTerminoReal() {
    return dataTerminoReal;
  }

  public void setDataTerminoReal(LocalDate v) {
    dataTerminoReal = v;
  }

  public LocalDateTime getCriadoEm() {
    return criadoEm;
  }

  public void setCriadoEm(LocalDateTime v) {
    criadoEm = v;
  }

  public LocalDateTime getAtualizadoEm() {
    return atualizadoEm;
  }

  public void setAtualizadoEm(LocalDateTime v) {
    atualizadoEm = v;
  }

  public Long getCriadoPor() {
    return criadoPor;
  }

  public void setCriadoPor(Long v) {
    criadoPor = v;
  }

  public Long getAlteradoPor() {
    return alteradoPor;
  }

  public void setAlteradoPor(Long v) {
    alteradoPor = v;
  }
}
