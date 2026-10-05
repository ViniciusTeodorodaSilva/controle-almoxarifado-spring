package br.com.almoxarifado.security;
import java.io.Serializable;
public record Identidade(Long id,String username,long authVersion,long autenticadoEm) implements Serializable {}
