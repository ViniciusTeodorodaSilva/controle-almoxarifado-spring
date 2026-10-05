package br.com.almoxarifado.security;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
@Component("autorizacao")
public class Autorizacao {
 private final UsuarioRepository usuarios;
 public Autorizacao(UsuarioRepository usuarios){this.usuarios=usuarios;}
 public boolean autenticado(){var a=org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();return a!=null&&a.isAuthenticated()&&!(a instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);}
 public boolean permite(String permissao){var a=SecurityContextHolder.getContext().getAuthentication();if(a==null||!a.isAuthenticated()||a instanceof AnonymousAuthenticationToken)return false;
  if(a.getPrincipal() instanceof Identidade i && !valida(i))return false;
  return a.getAuthorities().stream().anyMatch(p->p.getAuthority().equals(permissao));
 }
 public boolean valida(Identidade i){return System.currentTimeMillis()-i.autenticadoEm()<28800000L && usuarios.findById(i.id()).filter(u->u.isAtivo()&&u.getAuthVersion()==i.authVersion()).isPresent();}
 public Long ator(){var a=SecurityContextHolder.getContext().getAuthentication();return a!=null&&a.getPrincipal() instanceof Identidade i?i.id():null;}
 public String nomeAtor(){var a=SecurityContextHolder.getContext().getAuthentication();return a!=null&&a.getPrincipal() instanceof Identidade i?i.username():null;}
}
