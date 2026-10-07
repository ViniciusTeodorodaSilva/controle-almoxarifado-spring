package br.com.almoxarifado.obras;

import jakarta.persistence.*;

/** Immutable historical snapshot, assigned once by the server at origin creation. */
@Embeddable
public class ContextoOperacional {
  @Column(name = "contexto_obra_id", length = 255)
  private Integer obraId;

  @Column(name = "contexto_ordem_servico_id", length = 255)
  private Integer ordemServicoId;

  @Column(name = "contexto_centro_custo_id", length = 255)
  private Integer centroCustoId;

  @Column(name = "contexto_obra_codigo", length = 50)
  private String obraCodigo;

  @Column(name = "contexto_obra_nome", length = 160)
  private String obraNome;

  @Column(name = "contexto_ordem_servico_numero", length = 50)
  private String ordemServicoNumero;

  @Column(name = "contexto_centro_custo_codigo", length = 50)
  private String centroCustoCodigo;

  @Column(name = "contexto_centro_custo_nome", length = 160)
  private String centroCustoNome;

  public Integer getObraId() {
    return obraId;
  }

  public void setObraId(Integer v) {
    obraId = v;
  }

  public Integer getOrdemServicoId() {
    return ordemServicoId;
  }

  public void setOrdemServicoId(Integer v) {
    ordemServicoId = v;
  }

  public Integer getCentroCustoId() {
    return centroCustoId;
  }

  public void setCentroCustoId(Integer v) {
    centroCustoId = v;
  }

  public String getObraCodigo() {
    return obraCodigo;
  }

  public void setObraCodigo(String v) {
    obraCodigo = v;
  }

  public String getObraNome() {
    return obraNome;
  }

  public void setObraNome(String v) {
    obraNome = v;
  }

  public String getOrdemServicoNumero() {
    return ordemServicoNumero;
  }

  public void setOrdemServicoNumero(String v) {
    ordemServicoNumero = v;
  }

  public String getCentroCustoCodigo() {
    return centroCustoCodigo;
  }

  public void setCentroCustoCodigo(String v) {
    centroCustoCodigo = v;
  }

  public String getCentroCustoNome() {
    return centroCustoNome;
  }

  public void setCentroCustoNome(String v) {
    centroCustoNome = v;
  }

  public static boolean corresponde(ContextoOperacional c, Integer obra, Integer os, Integer cc) {
    return (obra == null || c != null && obra.equals(c.obraId))
        && (os == null || c != null && os.equals(c.ordemServicoId))
        && (cc == null || c != null && cc.equals(c.centroCustoId));
  }
}
