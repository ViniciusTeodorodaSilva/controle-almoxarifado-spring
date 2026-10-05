package br.com.almoxarifado.security;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;
import java.time.Instant;
@Entity @Immutable @Table(name="bes_auditoria",indexes={@Index(name="ix_bes_auditoria_instante",columnList="instante"),@Index(name="ix_bes_auditoria_ator",columnList="ator_id")})
public class EventoAuditoria {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,updatable=false) private Instant instante=Instant.now();
 @Column(name="ator_id",updatable=false) private Long atorId;
 @Column(name="ator_username",length=80,updatable=false) private String atorUsername;
 @Column(nullable=false,length=100,updatable=false) private String evento;
 @Column(length=100,updatable=false) private String entidade;
 @Column(length=80,updatable=false) private String referencia;
 @Column(nullable=false,length=20,updatable=false) private String resultado;
 @Column(name="responsavel_operacional_id",updatable=false) private Integer responsavelOperacionalId;
 @Column(name="request_id",length=36,updatable=false) private String requestId;
 @Column(length=1500,updatable=false) private String antes;
 @Column(length=1500,updatable=false) private String depois;
 protected EventoAuditoria(){}
 EventoAuditoria(Long ator,String nome,String evento,String entidade,String ref,String resultado,Integer responsavel,String requestId,String antes,String depois){this.atorId=ator;this.atorUsername=nome;this.evento=evento;this.entidade=entidade;this.referencia=ref;this.resultado=resultado;this.responsavelOperacionalId=responsavel;this.requestId=requestId;this.antes=antes;this.depois=depois;}
 public Long getId(){return id;}public Instant getInstante(){return instante;}public Long getAtorId(){return atorId;}public String getAtorUsername(){return atorUsername;}
 public String getEvento(){return evento;}public String getEntidade(){return entidade;}public String getReferencia(){return referencia;}public String getResultado(){return resultado;}
 public Integer getResponsavelOperacionalId(){return responsavelOperacionalId;}public String getRequestId(){return requestId;}public String getAntes(){return antes;}public String getDepois(){return depois;}
}
