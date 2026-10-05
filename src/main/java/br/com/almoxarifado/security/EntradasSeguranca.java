package br.com.almoxarifado.security;
import com.fasterxml.jackson.annotation.JsonAnySetter;
public final class EntradasSeguranca {
 private EntradasSeguranca(){}
 public static class Estrita {
  @JsonAnySetter public void desconhecido(String nome,Object valor){throw new IllegalArgumentException("Campo nao permitido");}
  @Override public String toString(){return "Entrada de seguranca [REDACTED]";}
 }
 public static final class Login extends Estrita {public String username;public String password;}
 public static final class UsuarioInput extends Estrita {public String username;public String nomeExibicao;public Perfil perfil;public Boolean ativo;public Integer funcionarioId;public String password;}
 public static final class SenhaInput extends Estrita {public String password;}
}
