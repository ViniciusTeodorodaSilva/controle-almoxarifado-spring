package br.com.almoxarifado.security;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.context.*;
import org.springframework.security.web.csrf.*;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.*;
import java.util.*;
@Configuration @EnableMethodSecurity @EnableTransactionManagement(order=0)
public class SegurancaConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder(12);}
 @Bean AuthenticationManager authenticationManager(UsuarioAuthenticationProvider provider){return new ProviderManager(provider);}
 @Bean SecurityContextRepository securityContextRepository(){return new HttpSessionSecurityContextRepository();}
 @Bean CsrfTokenRepository csrfTokenRepository(){return new HttpSessionCsrfTokenRepository();}
 @Bean static BeanFactoryPostProcessor cookiesProducao(Environment env){return beans->{boolean local=Arrays.asList(env.getActiveProfiles()).stream().anyMatch(p->p.equals("test")||p.equals("dev"));boolean prod=Arrays.asList(env.getActiveProfiles()).contains("prod");if((prod||!local)&&!env.getProperty("server.servlet.session.cookie.secure",Boolean.class,true))throw new IllegalStateException("Cookie Secure obrigatorio fora de dev/test");};}
 @Bean CorsConfigurationSource cors(@Value("${bes.cors.origins:}") String origins){var c=new CorsConfiguration();var allowed=Arrays.stream(origins.split(",")).map(String::strip).filter(s->!s.isEmpty()).toList();if(allowed.stream().anyMatch(s->s.contains("*")))throw new IllegalStateException("CORS exige origens explicitas");c.setAllowedOrigins(allowed);c.setAllowCredentials(true);c.setAllowedMethods(List.of("GET","HEAD","POST","PUT","OPTIONS"));c.setAllowedHeaders(List.of("Content-Type","Accept","X-CSRF-TOKEN","X-Request-ID","Idempotency-Key"));c.setExposedHeaders(List.of("X-Request-ID"));var source=new UrlBasedCorsConfigurationSource();source.registerCorsConfiguration("/**",c);return source;}
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http,Autorizacao auth,AuditoriaService auditoria,SecurityContextRepository contexts,CsrfTokenRepository csrf,CorsConfigurationSource cors)throws Exception {
  http.cors(c->c.configurationSource(cors)).csrf(c->c.csrfTokenRepository(csrf))
   .securityContext(c->c.securityContextRepository(contexts).requireExplicitSave(true))
   .sessionManagement(c->c.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
   .requestCache(c->c.disable()).formLogin(c->c.disable()).httpBasic(c->c.disable()).logout(c->c.disable())
   .authorizeHttpRequests(c->c.requestMatchers("/auth/csrf","/auth/login").permitAll()
    .requestMatchers("/auth/me","/auth/logout","/usuarios","/usuarios/**","/auditoria","/produtos","/produtos/**","/categorias","/categorias/**","/unidades-medida","/unidades-medida/**","/estoques","/estoques/**","/movimentacoes","/movimentacoes/**","/transferencias","/transferencias/**","/solicitacoes","/solicitacoes/**","/necessidades-compra","/necessidades-compra/**","/funcionarios","/funcionarios/**","/almoxarifados","/almoxarifados/**").access((a,r)->new org.springframework.security.authorization.AuthorizationDecision(RotasPermissao.permitida(r.getRequest(),auth))).anyRequest().denyAll())
   .exceptionHandling(c->c.authenticationEntryPoint((req,res,e)->HttpSeguranca.erro(req,res,401,"Autenticacao necessaria"))
    .accessDeniedHandler((req,res,e)->{var a=SecurityContextHolder.getContext().getAuthentication();boolean anon=a==null||a instanceof AnonymousAuthenticationToken;if(!anon)auditoria.resultado("ACESSO_NEGADO","NEGADO");HttpSeguranca.erro(req,res,anon&&!req.getRequestURI().startsWith("/auth/")?401:403,"Operacao nao autorizada");}))
   .headers(c->c.contentSecurityPolicy(p->p.policyDirectives("default-src 'none'; frame-ancestors 'none'"))
    .referrerPolicy(p->p.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)));
  http.addFilterBefore(new RequestIdFilter(),SecurityContextHolderFilter.class);
  http.addFilterAfter(new SessaoValidaFilter(auth),SecurityContextHolderFilter.class);
  return http.build();
 }
}
