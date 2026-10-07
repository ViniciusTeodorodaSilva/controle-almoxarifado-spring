package br.com.almoxarifado.obras;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(
    name = "bes_ordem_servico",
    indexes =
        @Index(
            name = "ix_bes_ordem_servico_filtros",
            columnList = "obra_id, centro_custo_id, status"))
@org.hibernate.annotations.Check(
    constraints = "status in ('ABERTA','EM_ANDAMENTO','SUSPENSA','CONCLUIDA','CANCELADA')")
public class OrdemServico {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Version @JsonIgnore private long versao;

  @Column(nullable = false, unique = true, length = 50)
  private String numero;

  @Column(nullable = false, name = "obra_id")
  private Integer obraId;

  @Column(name = "centro_custo_id")
  private Integer centroCustoId;

  @Column(nullable = false, length = 160)
  private String titulo;

  @Column(length = 2000)
  private String descricao;

  @Column(length = 2000)
  private String observacao;

  @Column(length = 20)
  private String prioridade;

  @Column(length = 1000)
  private String motivoInterrupcao;

  @Enumerated(EnumType.STRING)
  @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR)
  @Column(nullable = false, length = 20)
  private StatusOrdemServico status;

  @Column(name = "responsavel_id")
  private Integer responsavelId;

  @Column(nullable = false)
  private LocalDateTime dataAbertura;

  private LocalDateTime dataInicio;
  private LocalDateTime dataConclusao;

  @Column(nullable = false)
  private LocalDateTime criadoEm;

  @Column(nullable = false)
  private LocalDateTime atualizadoEm;

  @Column(nullable = false, name = "criado_por")
  private Long criadoPor;

  @Column(nullable = false, name = "alterado_por")
  private Long alteradoPor;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "obra_id", insertable = false, updatable = false)
  @JsonIgnore
  private Obra obraRef;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "centro_custo_id", insertable = false, updatable = false)
  @JsonIgnore
  private CentroCusto centroRef;

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

  public String getNumero() {
    return numero;
  }

  public void setNumero(String v) {
    numero = v;
  }

  public Integer getObraId() {
    return obraId;
  }

  public void setObraId(Integer v) {
    obraId = v;
  }

  public Integer getCentroCustoId() {
    return centroCustoId;
  }

  public void setCentroCustoId(Integer v) {
    centroCustoId = v;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String v) {
    titulo = v;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String v) {
    descricao = v;
  }

  public String getObservacao() {
    return observacao;
  }

  public void setObservacao(String v) {
    observacao = v;
  }

  public String getPrioridade() {
    return prioridade;
  }

  public void setPrioridade(String v) {
    prioridade = v;
  }

  public String getMotivoInterrupcao() {
    return motivoInterrupcao;
  }

  public void setMotivoInterrupcao(String v) {
    motivoInterrupcao = v;
  }

  public StatusOrdemServico getStatus() {
    return status;
  }

  public void setStatus(StatusOrdemServico v) {
    status = v;
  }

  public Integer getResponsavelId() {
    return responsavelId;
  }

  public void setResponsavelId(Integer v) {
    responsavelId = v;
  }

  public LocalDateTime getDataAbertura() {
    return dataAbertura;
  }

  public void setDataAbertura(LocalDateTime v) {
    dataAbertura = v;
  }

  public LocalDateTime getDataInicio() {
    return dataInicio;
  }

  public void setDataInicio(LocalDateTime v) {
    dataInicio = v;
  }

  public LocalDateTime getDataConclusao() {
    return dataConclusao;
  }

  public void setDataConclusao(LocalDateTime v) {
    dataConclusao = v;
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
