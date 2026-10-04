package br.com.almoxarifado.service;
import java.math.BigDecimal;
final class QuantidadesOperacionais {
    private QuantidadesOperacionais() {}
    static double somar(double a,double b) { return BigDecimal.valueOf(a).add(BigDecimal.valueOf(b)).doubleValue(); }
    static double subtrair(double a,double b) { return BigDecimal.valueOf(a).subtract(BigDecimal.valueOf(b)).doubleValue(); }
    static void positiva(double quantidade) { if(!Double.isFinite(quantidade)||quantidade<=0) throw new IllegalArgumentException("Quantidade deve ser positiva e finita"); }
}
