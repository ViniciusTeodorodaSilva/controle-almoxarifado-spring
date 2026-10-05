package br.com.almoxarifado.security;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import br.com.almoxarifado.repository.FuncionarioRepository;
import br.com.almoxarifado.exception.*;
import java.util.*;
@Service @Transactional
public class UsuarioService {
 private final UsuarioRepository usuarios;private final FuncionarioRepository pessoas;private final PasswordEncoder encoder;private final Autorizacao auth;private final AuditoriaService auditoria;
 public UsuarioService(UsuarioRepository usuarios,FuncionarioRepository pessoas,PasswordEncoder encoder,Autorizacao auth,AuditoriaService auditoria){this.usuarios=usuarios;this.pessoas=pessoas;this.encoder=encoder;this.auth=auth;this.auditoria=auditoria;}
 @PreAuthorize("@autorizacao.permite('USUARIO_GERENCIAR')") @Transactional(readOnly=true)
 public List<UsuarioResposta> listar(){return usuarios.findAllByOrderByIdAsc().stream().map(UsuarioResposta::de).toList();}
 @PreAuthorize("@autorizacao.permite('USUARIO_GERENCIAR')") @Auditar("USUARIO_CRIADO")
 public UsuarioResposta criar(EntradasSeguranca.UsuarioInput input){validar(input);PoliticaSenha.validar(input.password);var u=new Usuario(PoliticaSenha.login(input.username),encoder.encode(input.password),input.nomeExibicao.strip(),input.perfil);u.alterar(u.getUsername(),u.getNomeExibicao(),u.getPerfil(),input.ativo,vinculo(input.funcionarioId));exigirUnico(u.getUsername(),null);return UsuarioResposta.de(usuarios.saveAndFlush(u));}
 @PreAuthorize("@autorizacao.permite('USUARIO_GERENCIAR')") @Auditar("USUARIO_ALTERADO")
 public UsuarioResposta alterar(Long id,EntradasSeguranca.UsuarioInput input){validar(input);if(input.password!=null)throw new IllegalArgumentException("Utilize redefinicao de senha");var todos=usuarios.bloquearUsuarios();var u=todos.stream().filter(x->x.getId().equals(id)).findFirst().orElseThrow(()->new RecursoNaoEncontradoException("Usuario nao encontrado"));
  if((!input.ativo||input.perfil!=Perfil.ADMIN)&&u.getPerfil()==Perfil.ADMIN&&u.isAtivo()&&todos.stream().filter(x->x.isAtivo()&&x.getPerfil()==Perfil.ADMIN).count()==1)throw new ConflitoException("Nao e permitido remover o ultimo ADMIN ativo");
  if(Objects.equals(id,auth.ator())&&(!input.ativo||input.perfil!=u.getPerfil()))throw new ConflitoException("Nao e permitido desativar ou alterar o proprio perfil");
  exigirUnico(PoliticaSenha.login(input.username),id);String antes="perfil="+u.getPerfil()+";ativo="+u.isAtivo();u.alterar(PoliticaSenha.login(input.username),input.nomeExibicao.strip(),input.perfil,input.ativo,vinculo(input.funcionarioId));usuarios.flush();
  auditoria.registrar("USUARIO_PERFIL_SITUACAO", "Usuario",id.toString(),null,antes,"perfil="+u.getPerfil()+";ativo="+u.isAtivo());return UsuarioResposta.de(u);
 }
 @PreAuthorize("@autorizacao.permite('USUARIO_GERENCIAR')") @Auditar("USUARIO_SENHA_REDEFINIDA")
 public UsuarioResposta senha(Long id,String senha){PoliticaSenha.validar(senha);var u=usuarios.findById(id).orElseThrow(()->new RecursoNaoEncontradoException("Usuario nao encontrado"));u.senha(encoder.encode(senha));return UsuarioResposta.de(u);}
 private void exigirUnico(String login,Long id){if(usuarios.findByUsername(login).filter(u->!Objects.equals(u.getId(),id)).isPresent())throw new ConflitoException("Login indisponivel");}
 private void validar(EntradasSeguranca.UsuarioInput i){if(i==null||i.perfil==null||i.ativo==null||i.nomeExibicao==null||i.nomeExibicao.isBlank()||i.nomeExibicao.length()>120)throw new IllegalArgumentException("Nome, perfil e situacao validos sao obrigatorios");PoliticaSenha.login(i.username);}
 private br.com.almoxarifado.model.Funcionario vinculo(Integer id){return id==null?null:pessoas.findById(id).orElseThrow(()->new RecursoNaoEncontradoException("Funcionario nao encontrado"));}
}
