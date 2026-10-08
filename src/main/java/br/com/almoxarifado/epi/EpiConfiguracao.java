package br.com.almoxarifado.epi;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.*;
@Entity @Table(name="bes_epi_config",indexes={@Index(name="ix_epi_ca",columnList="ca"),@Index(name="ix_epi_ativo",columnList="ativo")})
public class EpiConfiguracao {
 @Id @Column(name="produto_id") Integer produtoId;
 @Version long versao;
 @Column(nullable=false,length=20) String ca;
 @Column(length=160) String fabricante;
 @Column(length=160) String modelo;
 @Column(length=50) String tamanho;
 @Column(name="validade_ca") LocalDate validadeCa;
 @Column(name="dias_substituicao") Integer diasSubstituicao;
 @Column(nullable=false) boolean ativo;
 @Column(name="exige_devolucao",nullable=false) boolean exigeDevolucao;
 @Column(name="permite_retorno",nullable=false) boolean permiteRetorno;
 @Column(length=2000) String observacao;
 @Column(name="alterado_por",nullable=false) Long alteradoPor;
 @Column(name="alterado_em",nullable=false) LocalDateTime alteradoEm;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="produto_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.model.Produto produtoRef;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="alterado_por",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.security.Usuario atorRef;
}
