package br.com.almoxarifado.epi;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import br.com.almoxarifado.obras.ContextoOperacional;
@Entity @org.hibernate.annotations.Immutable
@Table(name="bes_epi_registro",indexes={@Index(name="ix_epi_reg_func",columnList="funcionario_id,data_hora"),@Index(name="ix_epi_reg_tipo",columnList="tipo,data_hora"),@Index(name="ix_epi_reg_obra",columnList="contexto_obra_id"),@Index(name="ix_epi_reg_os",columnList="contexto_ordem_servico_id"),@Index(name="ix_epi_reg_cc",columnList="contexto_centro_custo_id")})
public class RegistroEpi {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Integer id;
 @Enumerated(EnumType.STRING) @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR) @Column(nullable=false,length=30) TipoRegistroEpi tipo;
 @Enumerated(EnumType.STRING) @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR) @Column(length=30) MotivoEntrega motivo;
 @Column(name="funcionario_id",nullable=false) Integer funcionarioId;
 @Column(name="funcionario_nome",nullable=false,length=255) String funcionarioNome;
 @Column(name="funcionario_matricula",length=255) String funcionarioMatricula;
 @Column(name="responsavel_id",nullable=false) Integer responsavelId;
 @Column(name="responsavel_nome",nullable=false,length=255) String responsavelNome;
 @Column(name="ator_id",nullable=false) Long atorId;
 @Column(name="ator_nome",nullable=false,length=160) String atorNome;
 @Column(name="almoxarifado_id",nullable=false) Integer almoxarifadoId;
 @Column(name="almoxarifado_nome",nullable=false,length=255) String almoxarifadoNome;
 @Column(name="data_hora",nullable=false) LocalDateTime dataHora;
 @Column(name="recebimento_confirmado",nullable=false) boolean recebimentoConfirmado;
 @Column(length=2000) String observacao;
 @Column(name="chave_idempotencia",nullable=false,unique=true,length=100) String chaveIdempotencia;
 @Column(nullable=false,length=64) String assinatura;
 @Embedded ContextoOperacional contexto;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="funcionario_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.model.Funcionario funcionarioRef;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="responsavel_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.model.Funcionario responsavelRef;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="ator_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.security.Usuario atorRef;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="almoxarifado_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.model.Almoxarifado localRef;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="contexto_obra_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.obras.Obra obraRef;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="contexto_ordem_servico_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.obras.OrdemServico osRef;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="contexto_centro_custo_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.obras.CentroCusto ccRef;
}
