package br.com.almoxarifado.service;
import java.math.BigDecimal;
final class QuantidadesOperacionais {
    private QuantidadesOperacionais() {}
    static double somar(double a,double b) { return representar(decimal(a).add(decimal(b))); }
    static double subtrair(double a,double b) { return representar(decimal(a).subtract(decimal(b))); }
    private static BigDecimal decimal(double value) {
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Quantidade ou saldo deve ser finito");
        return BigDecimal.valueOf(value);
    }
    static double representar(BigDecimal exato) {
        if (exato == null) throw new IllegalArgumentException("Quantidade deve ser informada");
        double value = exato.doubleValue();
        if (!Double.isFinite(value) || BigDecimal.valueOf(value).compareTo(exato) != 0)
            throw new IllegalArgumentException("Quantidade excede a precisão ou capacidade do saldo atual");
        return value;
    }
    static void positiva(double quantidade) { if(!Double.isFinite(quantidade)||quantidade<=0) throw new IllegalArgumentException("Quantidade deve ser positiva e finita"); }
    /** Exact decimal arithmetic at the legacy DOUBLE boundary; never silently lose a movement. */
    static double saldo(double anterior, double quantidade, boolean entrada) {
        positiva(quantidade);
        if (!Double.isFinite(anterior) || anterior < 0) throw new IllegalArgumentException("Saldo de estoque inválido");
        var antes = BigDecimal.valueOf(anterior);
        var delta = BigDecimal.valueOf(quantidade);
        var resultado = entrada ? antes.add(delta) : antes.subtract(delta);
        if (resultado.signum() < 0) throw new IllegalArgumentException("Estoque insuficiente");
        double convertido = representar(resultado);
        if (convertido == anterior) {
            throw new IllegalArgumentException("Quantidade excede a precisão ou capacidade do saldo atual");
        }
        return convertido;
    }
}
