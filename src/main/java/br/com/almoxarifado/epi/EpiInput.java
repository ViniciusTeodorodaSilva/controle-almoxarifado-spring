package br.com.almoxarifado.epi;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import br.com.almoxarifado.obras.ObrasInput;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class EpiInput {
 private EpiInput() {}
 public static class Estrito {
  @JsonAnySetter public void desconhecido(String nome,Object valor){throw new IllegalArgumentException("Campo nao permitido");}
 }
 public static class Configuracao extends Estrito {
  public Integer produtoId;
  public String ca, fabricante, modelo, tamanho, observacao;
  public LocalDate validadeCa;
  public Integer diasSubstituicao;
  public Boolean ativo, exigeDevolucao, permiteRetorno;
 }
 public static class Linha extends Estrito {
  public Integer produtoId, origemItemId;
  public BigDecimal quantidade, quantidadeSubstituida;
  public String lote, motivoSubstituicao;
  public CondicaoEpi condicaoAnterior;
  public DestinoEpi destinoAnterior;
  public LocalDate fabricacao, validadeFisica;
 }
 public static class Entrega extends Estrito {
  public Integer funcionarioId, responsavelId, almoxarifadoId;
  public ObrasInput.Contexto contexto;
  public MotivoEntrega motivo;
  public String observacao;
  public Boolean recebimentoConfirmado;
  public List<Linha> itens;
 }
 public static class FechamentoLinha extends Estrito {
  public Integer origemItemId;
  public BigDecimal quantidade;
  public CondicaoEpi condicao;
  public DestinoEpi destino;
 }
 public static class Fechamento extends Estrito {
  public Integer responsavelId, almoxarifadoId;
  public String motivo;
  public List<FechamentoLinha> itens;
 }
}
