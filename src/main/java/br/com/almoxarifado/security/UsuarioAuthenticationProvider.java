package br.com.almoxarifado.security;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class UsuarioAuthenticationProvider implements AuthenticationProvider {
 private final UsuarioRepository usuarios;private final PasswordEncoder encoder;private final String dummyHash;
 public UsuarioAuthenticationProvider(UsuarioRepository usuarios,PasswordEncoder encoder){this.usuarios=usuarios;this.encoder=encoder;dummyHash=encoder.encode(UUID.randomUUID().toString());}
 @Override @Transactional public Authentication authenticate(Authentication authentication){String login;try{login=PoliticaSenha.login(authentication.getName());}catch(IllegalArgumentException e){login="";}
  var u=usuarios.findByUsername(login).orElse(null);String senha=authentication.getCredentials() instanceof String s?s:"";boolean ok=false;
  if(senha.getBytes(java.nio.charset.StandardCharsets.UTF_8).length<=72)try{ok=encoder.matches(senha,u==null?dummyHash:u.getSenhaHash());}catch(IllegalArgumentException e){ok=false;}
  if(!ok||u==null||!u.isAtivo())throw new BadCredentialsException("Credenciais invalidas");u.login();return UsernamePasswordAuthenticationToken.authenticated(new Identidade(u.getId(),u.getUsername(),u.getAuthVersion(),System.currentTimeMillis()),null,u.getPerfil().permissoes().stream().map(p->new SimpleGrantedAuthority(p.name())).toList());
 }
 @Override public boolean supports(Class<?> type){return UsernamePasswordAuthenticationToken.class.isAssignableFrom(type);}
}
