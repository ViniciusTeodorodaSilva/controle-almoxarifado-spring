package br.com.almoxarifado.service;

import java.text.Normalizer;
import java.util.Locale;

public final class NormalizacaoCatalogo {
    private NormalizacaoCatalogo() {}
    public static String texto(String valor) {
        return valor == null ? null : valor.strip().replaceAll("[\\s\\p{Z}]+", " ");
    }
    public static String chave(String valor) {
        return Normalizer.normalize(texto(valor), Normalizer.Form.NFKD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
    public static String identificador(String valor) {
        String codigo = texto(valor);
        if (codigo == null || codigo.isBlank() || codigo.length() > 64
                || !codigo.matches("[A-Za-z0-9][A-Za-z0-9._/-]*")) {
            throw new IllegalArgumentException("Código/sigla deve ter até 64 caracteres ASCII, sem espaços");
        }
        return codigo.toUpperCase(Locale.ROOT);
    }
}
