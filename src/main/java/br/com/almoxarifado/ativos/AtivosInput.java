package br.com.almoxarifado.ativos;
import br.com.almoxarifado.security.EntradasSeguranca;
import br.com.almoxarifado.obras.ObrasInput;
import java.time.LocalDate;

public final class AtivosInput {
  private AtivosInput() {}
  public static final class Cadastro extends EntradasSeguranca.Estrita {
    public String codigoPatrimonial, nome, descricao, fabricante, modelo, numeroSerie, observacao;
    public Integer categoriaId;
    public LocalDate dataAquisicao;
  }
  public static final class Criacao extends EntradasSeguranca.Estrita {
    public Cadastro cadastro;
    public Integer almoxarifadoId;
    public CondicaoAtivo condicao;
    public LocalDate proximaInspecao;
  }
  public static final class Emprestimo extends EntradasSeguranca.Estrita {
    public Integer ativoId, entreguePorId, funcionarioId;
    public ObrasInput.Contexto contexto;
    public CondicaoAtivo condicao;
    public LocalDate previsaoDevolucao;
    public String observacao;
  }
  public static final class Devolucao extends EntradasSeguranca.Estrita {
    public Integer devolvidoPorId, recebidoPorId, almoxarifadoId;
    public CondicaoAtivo condicao;
    public String observacao;
  }
  public static final class Transferencia extends EntradasSeguranca.Estrita {
    public Integer ativoId, entreguePorId, almoxarifadoId;
    public ObrasInput.Contexto contexto;
    public CondicaoAtivo condicao;
    public String observacao;
  }
  public static final class Recebimento extends EntradasSeguranca.Estrita {
    public Integer recebidoPorId;
    public CondicaoAtivo condicao;
    public String observacao;
  }
  public static final class Inspecao extends EntradasSeguranca.Estrita {
    public Integer ativoId, inspetorId;
    public CondicaoAtivo condicao;
    public ResultadoInspecao resultado;
    public LocalDate proximaInspecao;
    public String observacao;
  }
  public static final class Situacao extends EntradasSeguranca.Estrita {
    public TipoRegistroAtivo acao;
    public Integer responsavelId;
    public String motivo;
  }
}
