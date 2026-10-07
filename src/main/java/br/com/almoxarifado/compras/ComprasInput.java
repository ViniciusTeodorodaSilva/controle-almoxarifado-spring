package br.com.almoxarifado.compras;

import br.com.almoxarifado.security.EntradasSeguranca;
import java.math.BigDecimal;
import java.util.List;

public final class ComprasInput {
  private ComprasInput() {}

  public static final class Fornecedor extends EntradasSeguranca.Estrita {
    public String nome, nomeFantasia, documento, email, telefone, contato, observacao;
    public TipoPessoa tipoPessoa;
    public Boolean ativo;

    public String getNome() {
      return nome;
    }

    public void setNome(String v) {
      nome = v;
    }

    public String getNomeFantasia() {
      return nomeFantasia;
    }

    public void setNomeFantasia(String v) {
      nomeFantasia = v;
    }

    public String getDocumento() {
      return documento;
    }

    public void setDocumento(String v) {
      documento = v;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String v) {
      email = v;
    }

    public String getTelefone() {
      return telefone;
    }

    public void setTelefone(String v) {
      telefone = v;
    }

    public String getContato() {
      return contato;
    }

    public void setContato(String v) {
      contato = v;
    }

    public String getObservacao() {
      return observacao;
    }

    public void setObservacao(String v) {
      observacao = v;
    }

    public TipoPessoa getTipoPessoa() {
      return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoa v) {
      tipoPessoa = v;
    }

    public Boolean getAtivo() {
      return ativo;
    }

    public void setAtivo(Boolean v) {
      ativo = v;
    }
  }

  public static final class Pedido extends EntradasSeguranca.Estrita {
    public Integer fornecedorId, almoxarifadoId;
    public String observacao;
    public List<Item> itens;

    public Integer getFornecedorId() {
      return fornecedorId;
    }

    public void setFornecedorId(Integer v) {
      fornecedorId = v;
    }

    public Integer getAlmoxarifadoId() {
      return almoxarifadoId;
    }

    public void setAlmoxarifadoId(Integer v) {
      almoxarifadoId = v;
    }

    public String getObservacao() {
      return observacao;
    }

    public void setObservacao(String v) {
      observacao = v;
    }

    public List<Item> getItens() {
      return itens;
    }

    public void setItens(List<Item> v) {
      itens = v;
    }
  }

  public static final class Item extends EntradasSeguranca.Estrita {
    public br.com.almoxarifado.obras.ObrasInput.Contexto contexto;
    public Integer produtoId;
    public Double quantidade;
    public BigDecimal valorUnitario;
    public String observacao;
    public Boolean paraEstoque;
    public List<Alocacao> alocacoes;

    public Integer getProdutoId() {
      return produtoId;
    }

    public void setProdutoId(Integer v) {
      produtoId = v;
    }

    public Double getQuantidade() {
      return quantidade;
    }

    public void setQuantidade(Double v) {
      quantidade = v;
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

    public Boolean getParaEstoque() {
      return paraEstoque;
    }

    public void setParaEstoque(Boolean v) {
      paraEstoque = v;
    }

    public List<Alocacao> getAlocacoes() {
      return alocacoes;
    }

    public void setAlocacoes(List<Alocacao> v) {
      alocacoes = v;
    }
  }

  public static final class Alocacao extends EntradasSeguranca.Estrita {
    public Integer necessidadeId;
    public Double quantidade;

    public Integer getNecessidadeId() {
      return necessidadeId;
    }

    public void setNecessidadeId(Integer v) {
      necessidadeId = v;
    }

    public Double getQuantidade() {
      return quantidade;
    }

    public void setQuantidade(Double v) {
      quantidade = v;
    }
  }

  public static final class Recebimento extends EntradasSeguranca.Estrita {
    public Integer almoxarifadoId, responsavelId;
    public String observacao;
    public List<ItemRecebido> itens;

    public Integer getAlmoxarifadoId() {
      return almoxarifadoId;
    }

    public void setAlmoxarifadoId(Integer v) {
      almoxarifadoId = v;
    }

    public Integer getResponsavelId() {
      return responsavelId;
    }

    public void setResponsavelId(Integer v) {
      responsavelId = v;
    }

    public String getObservacao() {
      return observacao;
    }

    public void setObservacao(String v) {
      observacao = v;
    }

    public List<ItemRecebido> getItens() {
      return itens;
    }

    public void setItens(List<ItemRecebido> v) {
      itens = v;
    }
  }

  public static final class ItemRecebido extends EntradasSeguranca.Estrita {
    public Integer itemPedidoId;
    public Double quantidade;

    public Integer getItemPedidoId() {
      return itemPedidoId;
    }

    public void setItemPedidoId(Integer v) {
      itemPedidoId = v;
    }

    public Double getQuantidade() {
      return quantidade;
    }

    public void setQuantidade(Double v) {
      quantidade = v;
    }
  }

  public static final class Cancelamento extends EntradasSeguranca.Estrita {
    public String motivo;

    public String getMotivo() {
      return motivo;
    }

    public void setMotivo(String v) {
      motivo = v;
    }
  }
}
