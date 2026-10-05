package br.com.almoxarifado.security;
import org.springframework.stereotype.Component;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
@Component
public class BootstrapAdmin implements ApplicationRunner {
 private final UsuarioRepository usuarios;private final PasswordEncoder encoder;private final AuditoriaService auditoria;
 private final boolean enabled;private final String username,password;
 public BootstrapAdmin(UsuarioRepository usuarios,PasswordEncoder encoder,AuditoriaService auditoria,@Value("${bes.bootstrap.enabled:false}") boolean enabled,@Value("${bes.bootstrap.username:}") String username,@Value("${bes.bootstrap.password:}") String password){this.usuarios=usuarios;this.encoder=encoder;this.auditoria=auditoria;this.enabled=enabled;this.username=username;this.password=password;}
 @Override @Transactional public void run(ApplicationArguments args){if(!enabled||usuarios.count()>0)return;PoliticaSenha.validar(password);var u=new Usuario(PoliticaSenha.login(username),encoder.encode(password),"Administrador inicial",Perfil.ADMIN);u.marcarBootstrap();usuarios.saveAndFlush(u);auditoria.registrar("ADMIN_BOOTSTRAP","Usuario",u.getId().toString(),null,null,"perfil=ADMIN;ativo=true");}
}
