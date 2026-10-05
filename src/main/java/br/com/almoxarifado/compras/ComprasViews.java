package br.com.almoxarifado.compras;

import java.math.*;
import java.util.*;

public final class ComprasViews {
  private ComprasViews() {}

  static Map<String, Object> map(Object... pairs) {
    var m = new LinkedHashMap<String, Object>();
    for (int i = 0; i < pairs.length; i += 2) m.put((String) pairs[i], pairs[i + 1]);
    return m;
  }

  static Map<String, Object> fornecedor(Fornecedor f, boolean detalhe) {
    return map(
        "id",
        f.getId(),
        "nome",
        f.getNome(),
        "nomeFantasia",
        f.getNomeFantasia(),
        "tipoPessoa",
        f.getTipoPessoa(),
        "documento",
        detalhe ? f.getDocumento() : DocumentoFornecedor.mascarar(f.getDocumento()),
        "email",
        detalhe ? f.getEmail() : null,
        "telefone",
        detalhe ? f.getTelefone() : null,
        "contato",
        detalhe ? f.getContato() : null,
        "observacao",
        detalhe ? f.getObservacao() : null,
        "ativo",
        f.getAtivo(),
        "criadoEm",
        f.getCriadoEm(),
        "atualizadoEm",
        f.getAtualizadoEm());
  }

  static BigDecimal subtotal(ItemPedidoCompra i) {
    return i.getValorUnitario()
        .multiply(BigDecimal.valueOf(i.getQuantidadePedida()))
        .setScale(2, RoundingMode.HALF_UP);
  }

  static Map<String, Object> item(ItemPedidoCompra i) {
    return map(
        "id",
        i.getId(),
        "produtoId",
        i.getProduto().getId(),
        "codigo",
        i.getCodigo(),
        "nome",
        i.getNome(),
        "unidade",
        i.getUnidade(),
        "quantidade",
        i.getQuantidadePedida(),
        "quantidadeRecebida",
        i.getQuantidadeRecebida(),
        "quantidadePendente",
        decimal(i.getQuantidadePedida(), i.getQuantidadeRecebida(), false),
        "quantidadeEstoque",
        i.getQuantidadeEstoque(),
        "valorUnitario",
        i.getValorUnitario().toPlainString(),
        "subtotal",
        subtotal(i).toPlainString(),
        "observacao",
        i.getObservacao(),
        "alocacoes",
        i.getAlocacoes().stream()
            .map(
                a ->
                    map(
                        "necessidadeId",
                        a.getNecessidade().getId(),
                        "solicitacaoId",
                        a.getNecessidade().getSolicitacao().getId(),
                        "itemSolicitacaoId",
                        a.getNecessidade().getItemSolicitacao().getId(),
                        "quantidade",
                        a.getQuantidade(),
                        "quantidadeRecebida",
                        a.getQuantidadeRecebida()))
            .toList());
  }

  static Map<String, Object> pedido(PedidoCompra p, boolean detalhe) {
    var ativos = p.getItens().stream().filter(i -> i.getAtivo()).toList();
    return map(
        "id",
        p.getId(),
        "numero",
        p.getNumero(),
        "fornecedorId",
        p.getFornecedor().getId(),
        "fornecedorNome",
        p.getFornecedorNome(),
        "fornecedorDocumento",
        detalhe
            ? p.getFornecedorDocumento()
            : DocumentoFornecedor.mascarar(p.getFornecedorDocumento()),
        "fornecedorContato",
        detalhe ? p.getFornecedorContato() : null,
        "almoxarifadoId",
        p.getAlmoxarifado().getId(),
        "almoxarifadoNome",
        p.getAlmoxarifado().getNome(),
        "status",
        p.getStatus(),
        "criadoEm",
        p.getCriadoEm(),
        "criadoPor",
        p.getCriadoPor(),
        "criadoPorNome",
        p.getCriadoPorNome(),
        "submetidoEm",
        p.getSubmetidoEm(),
        "aprovadoEm",
        p.getAprovadoEm(),
        "aprovadoPor",
        p.getAprovadoPor(),
        "aprovadoPorNome",
        p.getAprovadoPorNome(),
        "canceladoEm",
        p.getCanceladoEm(),
        "canceladoPor",
        p.getCanceladoPor(),
        "canceladoPorNome",
        p.getCanceladoPorNome(),
        "motivoCancelamento",
        p.getMotivoCancelamento(),
        "observacao",
        p.getObservacao(),
        "total",
        ativos.stream()
            .map(ComprasViews::subtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .toPlainString(),
        "itens",
        detalhe ? ativos.stream().map(ComprasViews::item).toList() : null);
  }

  static Map<String, Object> recebimento(RecebimentoCompra r) {
    return map(
        "id",
        r.getId(),
        "pedidoId",
        r.getPedido().getId(),
        "pedidoNumero",
        r.getPedido().getNumero(),
        "fornecedorNome",
        r.getPedido().getFornecedorNome(),
        "almoxarifadoId",
        r.getAlmoxarifado().getId(),
        "almoxarifadoNome",
        r.getAlmoxarifado().getNome(),
        "responsavelId",
        r.getResponsavel().getId(),
        "responsavelNome",
        r.getResponsavel().getNome(),
        "recebidoPor",
        r.getRecebidoPor(),
        "recebidoPorNome",
        r.getRecebidoPorNome(),
        "dataHora",
        r.getDataHora(),
        "observacao",
        r.getObservacao(),
        "itens",
        r.getItens().stream()
            .map(
                i ->
                    map(
                        "id",
                        i.getId(),
                        "itemPedidoId",
                        i.getItemPedido().getId(),
                        "produtoId",
                        i.getItemPedido().getProduto().getId(),
                        "codigo",
                        i.getItemPedido().getCodigo(),
                        "nome",
                        i.getItemPedido().getNome(),
                        "unidade",
                        i.getItemPedido().getUnidade(),
                        "quantidade",
                        i.getQuantidade(),
                        "saldoAnterior",
                        i.getSaldoAnterior(),
                        "saldoPosterior",
                        i.getSaldoPosterior(),
                        "destinacoes",
                        i.getDestinacoes().stream()
                            .map(
                                d ->
                                    map(
                                        "necessidadeId",
                                        d.getAlocacao().getNecessidade().getId(),
                                        "solicitacaoId",
                                        d.getAlocacao().getNecessidade().getSolicitacao().getId(),
                                        "quantidade",
                                        d.getQuantidade()))
                            .toList()))
            .toList());
  }

  static double decimal(double a, double b, boolean somar) {
    if (!Double.isFinite(a) || !Double.isFinite(b))
      throw new br.com.almoxarifado.exception.ConflitoException(
          "Quantidade ou saldo inconsistente");
    var exato =
        somar
            ? BigDecimal.valueOf(a).add(BigDecimal.valueOf(b))
            : BigDecimal.valueOf(a).subtract(BigDecimal.valueOf(b));
    double result = exato.doubleValue();
    if (!Double.isFinite(result) || BigDecimal.valueOf(result).compareTo(exato) != 0)
      throw new br.com.almoxarifado.exception.ConflitoException(
          "Precisão excede a capacidade atual do estoque; nenhuma quantidade foi arredondada"
              + " silenciosamente");
    return result;
  }
}
