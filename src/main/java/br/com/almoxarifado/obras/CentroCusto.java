package br.com.almoxarifado.obras;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(
    name = "bes_centro_custo",
    indexes = @Index(name = "ix_bes_centro_custo_filtros", columnList = "obra_id, ativo"))
@org.hibernate.annotations.Check(
    constraints =
        "tipo in ('OBRA','ADMINISTRATIVO','OPERACIONAL','OUTRO') and (tipo <> 'OBRA' or obra_id is"
            + " not null)")
public class CentroCusto {
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

  @Enumerated(EnumType.STRING)
  @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR)
  @Column(nullable = false, length = 20)
  private TipoCentroCusto tipo;

  @Column(nullable = false)
  private Boolean ativo;

  @Column(name = "obra_id")
  private Integer obraId;

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

  public TipoCentroCusto getTipo() {
    return tipo;
  }

  public void setTipo(TipoCentroCusto v) {
    tipo = v;
  }

  public Boolean getAtivo() {
    return ativo;
  }

  public void setAtivo(Boolean v) {
    ativo = v;
  }

  public Integer getObraId() {
    return obraId;
  }

  public void setObraId(Integer v) {
    obraId = v;
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
