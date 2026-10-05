package br.com.almoxarifado.security;
import org.junit.jupiter.api.Test;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;
class LoginRateLimiterTests {
 static class TestClock extends Clock {long millis;public ZoneId getZone(){return ZoneOffset.UTC;}public Clock withZone(ZoneId zone){return this;}public Instant instant(){return Instant.ofEpochMilli(millis);}}
 @Test void bloqueioTemporarioPorConta(){var clock=new TestClock();var limiter=new LoginRateLimiter(clock);for(int i=0;i<8;i++)assertTrue(limiter.permitir("ip","user"));assertFalse(limiter.permitir("ip","user"));clock.millis=300001;assertTrue(limiter.permitir("ip","user"));}
 @Test void limiteIpImpedeDistribuirUsernames(){var limiter=new LoginRateLimiter();for(int i=0;i<30;i++)assertTrue(limiter.permitir("ip","user"+i));assertFalse(limiter.permitir("ip","outra"));}
 @Test void limiteMemoria(){var limiter=new LoginRateLimiter();for(int i=0;i<5000;i++)assertTrue(limiter.permitir("ip"+i,"user"+i));assertFalse(limiter.permitir("extra","extra"));}
 @Test void ipBloqueadoNaoExaureMemoriaComNovosLogins(){var limiter=new LoginRateLimiter();for(int i=0;i<10000;i++)limiter.permitir("atacante","user"+i);assertTrue(limiter.permitir("ip-legitimo","usuario-legitimo"));var entries=(java.util.Map<?,?>)org.springframework.test.util.ReflectionTestUtils.getField(limiter,"buckets");assertEquals(33,entries.size());}

}
