package br.com.almoxarifado.security;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.Instant;
public final class HttpSeguranca {
 private HttpSeguranca(){}
 public static void erro(HttpServletRequest request,HttpServletResponse response,int status,String mensagem)throws IOException {response.setStatus(status);response.setContentType("application/json");response.setCharacterEncoding("UTF-8");response.setHeader("Cache-Control","no-store");String path=request.getRequestURI().replace("\\","\\\\").replace("\"","\\\"").replace("\n","").replace("\r","");response.getWriter().write("{\"timestamp\":\""+Instant.now()+"\",\"status\":"+status+",\"erro\":\"Seguranca\",\"mensagem\":\""+mensagem+"\",\"path\":\""+path+"\"}");}
}
