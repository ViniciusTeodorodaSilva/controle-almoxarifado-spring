package br.com.almoxarifado.compras;

import java.util.Locale;

public final class DocumentoFornecedor {
  private DocumentoFornecedor() {}

  public static String normalizar(TipoPessoa tipo, String entrada) {
    if (entrada == null || entrada.isBlank()) return null;
    if (entrada.length() > 30 || !entrada.matches("[A-Za-z0-9./ -]+"))
      throw new IllegalArgumentException("Documento inválido");
    String d = entrada.toUpperCase(Locale.ROOT).replaceAll("[./ -]", "");
    if (tipo == null
        || !(tipo == TipoPessoa.PF ? d.matches("[0-9]{11}") : d.matches("[A-Z0-9]{12}[0-9]{2}"))
        || d.chars().distinct().count() == 1)
      throw new IllegalArgumentException("Documento inválido");
    int base = tipo == TipoPessoa.PF ? 9 : 12;
    for (int etapa = 0; etapa < 2; etapa++) {
      int sum = 0, peso = 2;
      for (int i = base + etapa - 1; i >= 0; i--) {
        sum += (d.charAt(i) - 48) * peso;
        peso++;
        if (tipo == TipoPessoa.PJ && peso == 10) peso = 2;
      }
      int resto = sum % 11, dv = resto < 2 ? 0 : 11 - resto;
      if (d.charAt(base + etapa) - 48 != dv)
        throw new IllegalArgumentException("Documento inválido");
    }
    return d;
  }

  public static String mascarar(String d) {
    return d == null ? null : "***" + d.substring(d.length() - 4);
  }
}
