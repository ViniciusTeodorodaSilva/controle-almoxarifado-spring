package br.com.almoxarifado.security;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
public record UsuarioResposta(Long id,String username,String nomeExibicao,Perfil perfil,boolean ativo,Integer funcionarioId,
 Instant criadoEm,Instant atualizadoEm,Instant ultimoLoginEm,Set<String> permissoes) {
 public static UsuarioResposta de(Usuario u){return new UsuarioResposta(u.getId(),u.getUsername(),u.getNomeExibicao(),u.getPerfil(),u.isAtivo(),u.getFuncionario()==null?null:u.getFuncionario().getId(),u.getCriadoEm(),u.getAtualizadoEm(),u.getUltimoLoginEm(),u.getPerfil().permissoes().stream().map(Enum::name).collect(Collectors.toUnmodifiableSet()));}
}
