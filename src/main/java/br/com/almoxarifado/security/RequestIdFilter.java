package br.com.almoxarifado.security;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.MDC;
import org.springframework.web.context.request.*;
import java.util.UUID;
import java.io.IOException;
public class RequestIdFilter extends OncePerRequestFilter {
 @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException {
  String id=request.getHeader("X-Request-ID");if(id==null||!id.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}"))id=UUID.randomUUID().toString();request.setAttribute("bes.requestId",id);response.setHeader("X-Request-ID",id);MDC.put("requestId",id);
  var old=RequestContextHolder.getRequestAttributes();RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request,response));try{chain.doFilter(request,response);}finally{MDC.remove("requestId");if(old==null)RequestContextHolder.resetRequestAttributes();else RequestContextHolder.setRequestAttributes(old);}
 }
}
