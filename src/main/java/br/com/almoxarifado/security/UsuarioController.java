package br.com.almoxarifado.security;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import java.util.List;
@RestController
public class UsuarioController {
 private final UsuarioService usuarios;private final AuditoriaService auditoria;
 public UsuarioController(UsuarioService usuarios,AuditoriaService auditoria){this.usuarios=usuarios;this.auditoria=auditoria;}
 @GetMapping("/usuarios") public List<UsuarioResposta> listar(){return usuarios.listar();}
 @PostMapping("/usuarios") public UsuarioResposta criar(@RequestBody EntradasSeguranca.UsuarioInput input){return usuarios.criar(input);}
 @PutMapping("/usuarios/{id}") public UsuarioResposta alterar(@PathVariable Long id,@RequestBody EntradasSeguranca.UsuarioInput input){return usuarios.alterar(id,input);}
 @PutMapping("/usuarios/{id}/senha") public UsuarioResposta senha(@PathVariable Long id,@RequestBody EntradasSeguranca.SenhaInput input){return usuarios.senha(id,input.password);}
 @GetMapping("/auditoria") public Page<EventoAuditoria> auditoria(@RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="30") int tamanho){return auditoria.listar(pagina,tamanho);}
}
