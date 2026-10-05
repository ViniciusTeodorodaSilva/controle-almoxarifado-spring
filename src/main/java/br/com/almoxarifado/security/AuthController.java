package br.com.almoxarifado.security;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.*;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import jakarta.servlet.http.*;
import java.util.Map;
@RestController @RequestMapping("/auth")
public class AuthController {
 private final AuthenticationManager manager;private final SecurityContextRepository contexts;private final CsrfTokenRepository csrf;private final UsuarioRepository usuarios;private final LoginRateLimiter limiter;private final AuditoriaService audit;
 public AuthController(AuthenticationManager manager,SecurityContextRepository contexts,CsrfTokenRepository csrf,UsuarioRepository usuarios,LoginRateLimiter limiter,AuditoriaService audit){this.manager=manager;this.contexts=contexts;this.csrf=csrf;this.usuarios=usuarios;this.limiter=limiter;this.audit=audit;}
 @GetMapping("/csrf") public ResponseEntity<Map<String,String>> csrf(HttpServletRequest request){var token=(CsrfToken)request.getAttribute(CsrfToken.class.getName());return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(Map.of("token",token.getToken(),"headerName",token.getHeaderName()));}
 @PostMapping("/login") public ResponseEntity<?> login(@RequestBody EntradasSeguranca.Login input,HttpServletRequest request,HttpServletResponse response){String login="";try{login=PoliticaSenha.login(input.username);}catch(IllegalArgumentException e){}
  if(!limiter.permitir(request.getRemoteAddr(),login)){audit.resultado("LOGIN_LIMITADO","NEGADO");return ResponseEntity.status(429).header("Retry-After","300").body(Map.of("mensagem","Muitas tentativas. Tente novamente mais tarde."));}
  try{var a=manager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(input.username==null?"":input.username,input.password==null?"":input.password));new ChangeSessionIdAuthenticationStrategy().onAuthentication(a,request,response);new CsrfAuthenticationStrategy(csrf).onAuthentication(a,request,response);var context=SecurityContextHolder.createEmptyContext();context.setAuthentication(a);SecurityContextHolder.setContext(context);audit.resultado("LOGIN","SUCESSO");contexts.saveContext(context,request,response);return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(UsuarioResposta.de(usuarios.findById(((Identidade)a.getPrincipal()).id()).orElseThrow()));}
  catch(AuthenticationException e){audit.resultado("LOGIN","FALHA");return ResponseEntity.status(401).cacheControl(CacheControl.noStore()).body(Map.of("mensagem","Credenciais invalidas"));}
  catch(RuntimeException e){new SecurityContextLogoutHandler().logout(request,response,SecurityContextHolder.getContext().getAuthentication());throw e;}
 }
 @GetMapping("/me") public ResponseEntity<UsuarioResposta> me(Authentication auth){var i=(Identidade)auth.getPrincipal();return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(UsuarioResposta.de(usuarios.findById(i.id()).orElseThrow()));}
 @PostMapping("/logout") public ResponseEntity<Void> logout(HttpServletRequest req,HttpServletResponse res,Authentication auth){audit.resultado("LOGOUT","SUCESSO");new SecurityContextLogoutHandler().logout(req,res,auth);res.addHeader("Set-Cookie",ResponseCookie.from("BESSESSION","").path("/").httpOnly(true).secure(req.isSecure()).sameSite("Lax").maxAge(0).build().toString());return ResponseEntity.noContent().build();}
}
