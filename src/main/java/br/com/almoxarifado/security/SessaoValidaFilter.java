package br.com.almoxarifado.security;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
public class SessaoValidaFilter extends OncePerRequestFilter {
 private final Autorizacao auth;
 public SessaoValidaFilter(Autorizacao auth){this.auth=auth;}
 @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException {var a=SecurityContextHolder.getContext().getAuthentication();if(a!=null&&a.getPrincipal() instanceof Identidade i&&!auth.valida(i)){new SecurityContextLogoutHandler().logout(request,response,a);HttpSeguranca.erro(request,response,401,"Sessao expirada ou invalida");return;}chain.doFilter(request,response);}
}
