package br.com.almoxarifado.security;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import org.springframework.web.context.request.*;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
@Service
public class AuditoriaService {
 private final AuditoriaRepository repository;private final Autorizacao auth;
 public AuditoriaService(AuditoriaRepository r,Autorizacao auth){repository=r;this.auth=auth;}
 @Transactional(propagation=Propagation.MANDATORY)
 public void registrar(String evento,String entidade,String ref,Integer responsavel,String antes,String depois){salvar(auth.ator(),auth.nomeAtor(),evento,entidade,ref,"SUCESSO",responsavel,antes,depois);}
 @Transactional(propagation=Propagation.REQUIRES_NEW)
 public void resultado(String evento,String resultado){salvar(auth.ator(),auth.nomeAtor(),evento,"SEGURANCA",null,resultado,null,null,null);}
 private void salvar(Long ator,String nome,String evento,String entidade,String ref,String resultado,Integer responsavel,String antes,String depois){var attrs=RequestContextHolder.getRequestAttributes();String requestId=attrs instanceof ServletRequestAttributes s?(String)s.getRequest().getAttribute("bes.requestId"):null;repository.save(new EventoAuditoria(ator,nome,evento,entidade,ref,resultado,responsavel,requestId,antes,depois));}
 @PreAuthorize("@autorizacao.permite('AUDITORIA_LER')") @Transactional(readOnly=true)
 public Page<EventoAuditoria> listar(int pagina,int tamanho){if(pagina<0||tamanho<1||tamanho>100)throw new IllegalArgumentException("Paginacao invalida");return repository.findAll(PageRequest.of(pagina,tamanho,Sort.by(Sort.Direction.DESC,"id")));}
}
