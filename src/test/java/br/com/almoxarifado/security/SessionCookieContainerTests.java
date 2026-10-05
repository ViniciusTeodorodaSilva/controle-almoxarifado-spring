package br.com.almoxarifado.security;

import java.net.URI;
import java.net.http.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

/** Real servlet container: MockMvc does not create the browser's Set-Cookie header. */
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,properties={
 "spring.datasource.url=jdbc:h2:mem:bes-cookie-audit;MODE=MySQL;DB_CLOSE_DELAY=-1",
 "server.servlet.session.cookie.secure=true"})
@ActiveProfiles("test")
class SessionCookieContainerTests {
 @LocalServerPort int port;
 @Autowired UsuarioRepository usuarios;
 @Autowired tools.jackson.databind.ObjectMapper json;
 @Autowired jakarta.servlet.ServletContext servlet;
 final HttpClient client=HttpClient.newHttpClient();
 HttpResponse<String> send(String path,String method,String cookie,String token,String body)throws Exception {
  var request=HttpRequest.newBuilder(URI.create("http://localhost:"+port+path));
  if(cookie!=null)request.header("Cookie",cookie);
  if(token!=null)request.header("X-CSRF-TOKEN",token);
  if(body!=null)request.header("Content-Type","application/json");
  return client.send(request.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
 }
 @Test void cookieProducaoEHeadersReais()throws Exception {
  var response=send("/auth/csrf","GET",null,null,null);assertEquals(200,response.statusCode());
  String set=response.headers().firstValue("set-cookie").orElseThrow().toLowerCase(Locale.ROOT);
  assertTrue(set.startsWith("bessession="));assertTrue(set.contains("httponly"));assertTrue(set.contains("secure"));assertTrue(set.contains("samesite=lax"));assertTrue(set.contains("path=/"));assertFalse(set.contains("max-age="));
  assertEquals(Set.of(jakarta.servlet.SessionTrackingMode.COOKIE),servlet.getEffectiveSessionTrackingModes());assertEquals(30,servlet.getSessionTimeout());
  assertEquals("nosniff",response.headers().firstValue("X-Content-Type-Options").orElseThrow());assertEquals("DENY",response.headers().firstValue("X-Frame-Options").orElseThrow());assertEquals("no-referrer",response.headers().firstValue("Referrer-Policy").orElseThrow());assertTrue(response.headers().firstValue("Content-Security-Policy").orElseThrow().contains("frame-ancestors 'none'"));assertTrue(response.headers().firstValue("Strict-Transport-Security").isEmpty());
 }
 @Test void fixationLogoutETokenAntigoNoContainer()throws Exception {
  String password="H2-cookie-audit-passphrase!";usuarios.saveAndFlush(new Usuario("cookie.admin",new BCryptPasswordEncoder(4).encode(password),"H2",Perfil.ADMIN));
  var csrf=send("/auth/csrf","GET",null,null,null);String oldCookie=csrf.headers().firstValue("set-cookie").orElseThrow().split(";",2)[0];String token=json.readTree(csrf.body()).get("token").asText();
  var login=send("/auth/login","POST",oldCookie,token,json.writeValueAsString(Map.of("username","cookie.admin","password",password)));assertEquals(200,login.statusCode());String cookie=login.headers().firstValue("set-cookie").orElseThrow().split(";",2)[0];assertFalse(cookie.equals(oldCookie));
  assertEquals(401,send("/auth/me","GET",oldCookie,null,null).statusCode());assertEquals(200,send("/auth/me","GET",cookie,null,null).statusCode());
  assertEquals(403,send("/auth/logout","POST",cookie,token,null).statusCode());
  var renewed=send("/auth/csrf","GET",cookie,null,null);var logout=send("/auth/logout","POST",cookie,json.readTree(renewed.body()).get("token").asText(),null);assertEquals(204,logout.statusCode());assertTrue(logout.headers().allValues("set-cookie").stream().anyMatch(c->c.startsWith("BESSESSION=")&&c.contains("Max-Age=0")));assertEquals(401,send("/auth/me","GET",cookie,null,null).statusCode());
 }
}
