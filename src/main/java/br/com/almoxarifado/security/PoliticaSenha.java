package br.com.almoxarifado.security;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
public final class PoliticaSenha {
 private PoliticaSenha(){}
 public static void validar(String senha){if(senha==null||senha.codePointCount(0,senha.length())<10||senha.getBytes(StandardCharsets.UTF_8).length>72||senha.indexOf('\0')>=0)throw new IllegalArgumentException("Senha deve ter ao menos 10 caracteres e no maximo 72 bytes UTF-8");}
 public static String login(String nome){if(nome==null)throw new IllegalArgumentException("Login invalido");String n=nome.strip().toLowerCase(Locale.ROOT);if(!n.matches("[a-z0-9][a-z0-9._-]{2,79}"))throw new IllegalArgumentException("Login deve ter 3 a 80 caracteres: letras ASCII, numeros, ponto, hifen ou sublinhado");return n;}
}
