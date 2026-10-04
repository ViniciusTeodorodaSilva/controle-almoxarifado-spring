package br.com.almoxarifado.service;
import br.com.almoxarifado.model.Produto;
public final class ValidacaoQuantidade {
    private ValidacaoQuantidade() {}
    public static void validar(Produto produto, double quantidade) {
        if (!Double.isFinite(quantidade) || quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva e finita");
        if (produto.getUnidadeMedidaConfigurada() != null
                && !produto.getUnidadeMedidaConfigurada().isPermiteFracionamento()
                && quantidade != Math.rint(quantidade)) {
            throw new IllegalArgumentException("Unidade não permite quantidade fracionária");
        }
    }
}
