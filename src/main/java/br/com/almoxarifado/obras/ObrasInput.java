package br.com.almoxarifado.obras;

import br.com.almoxarifado.security.EntradasSeguranca;
import java.time.LocalDate;

public final class ObrasInput {
  public static final class Obra extends EntradasSeguranca.Estrita {
    public String codigo, nome, descricao, cliente, localidade, observacao;
    public Integer responsavelId;
    public LocalDate dataInicio, dataTerminoPrevisto;
  }

  public static final class Centro extends EntradasSeguranca.Estrita {
    public String codigo, nome, descricao;
    public TipoCentroCusto tipo;
    public Boolean ativo;
    public Integer obraId;
  }

  public static final class Ordem extends EntradasSeguranca.Estrita {
    public Integer obraId, centroCustoId, responsavelId;
    public String titulo, descricao, observacao, prioridade;
  }

  public static final class Transicao extends EntradasSeguranca.Estrita {
    public String status, motivo;
  }

  public static final class Contexto extends EntradasSeguranca.Estrita {
    public Integer obraId, ordemServicoId, centroCustoId;
  }
}
