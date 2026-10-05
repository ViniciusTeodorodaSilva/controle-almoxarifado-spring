package br.com.almoxarifado.security;
import org.springframework.stereotype.Component;
import java.time.*;
import java.util.*;
@Component
public class LoginRateLimiter {
 private final Map<String,Bucket> buckets=new HashMap<>();private final Clock clock;
 public LoginRateLimiter(){this(Clock.systemUTC());} LoginRateLimiter(Clock clock){this.clock=clock;}
 private record Bucket(long inicio,int quantidade){}
 public synchronized boolean permitir(String ip,String login){long now=clock.millis();buckets.entrySet().removeIf(e->now-e.getValue().inicio()>=900000);int novos=(buckets.containsKey("ip:"+ip)?0:1)+(buckets.containsKey("login:"+login)?0:1);if(buckets.size()+novos>10000)return false;
  return consumir("ip:"+ip,30,900000,now)&&consumir("login:"+login,8,300000,now);
 }
 private boolean consumir(String key,int max,long janela,long now){var b=buckets.get(key);if(b==null||now-b.inicio()>=janela){buckets.put(key,new Bucket(now,1));return true;}if(b.quantidade()>=max)return false;buckets.put(key,new Bucket(b.inicio(),b.quantidade()+1));return true;}
}
