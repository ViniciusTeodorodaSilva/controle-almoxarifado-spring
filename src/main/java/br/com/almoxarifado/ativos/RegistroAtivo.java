package br.com.almoxarifado.ativos;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import br.com.almoxarifado.obras.ContextoOperacional;
import java.time.*;
@Entity @org.hibernate.annotations.Immutable @Table(name="bes_registro_ativo",indexes={@Index(name="ix_registro_ativo",columnList="ativo_id,tipo,data_hora"),@Index(name="ix_registro_previsao",columnList="tipo,previsao_devolucao"),@Index(name="ix_registro_origem",columnList="origem_id")})
public class RegistroAtivo {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Integer id;
@Column(name="ativo_id",nullable=false) Integer ativoId;
@Column(name="origem_id",nullable=true,unique=true) Integer origemId;
@Column(name="codigo_patrimonial",nullable=false,length=50) String codigoPatrimonial;
@Column(name="ativo_nome",nullable=false,length=160) String ativoNome;
@Column(name="entregue_por_id",nullable=true) Integer entreguePorId;
@Column(name="entregue_por_nome",nullable=true,length=255) String entreguePorNome;
@Column(name="recebido_por_id",nullable=true) Integer recebidoPorId;
@Column(name="recebido_por_nome",nullable=true,length=255) String recebidoPorNome;
@Column(name="local_origem_id",nullable=true) Integer localOrigemId;
@Column(name="local_origem_nome",nullable=true,length=255) String localOrigemNome;
@Column(name="local_destino_id",nullable=true) Integer localDestinoId;
@Column(name="local_destino_nome",nullable=true,length=255) String localDestinoNome;
@Column(name="responsavel_antes_id",nullable=true) Integer responsavelAntesId;
@Column(name="responsavel_antes_nome",nullable=true,length=255) String responsavelAntesNome;
@Column(name="responsavel_depois_id",nullable=true) Integer responsavelDepoisId;
@Column(name="responsavel_depois_nome",nullable=true,length=255) String responsavelDepoisNome;
@Column(name="data_hora",nullable=false) LocalDateTime dataHora;
@Column(name="previsao_devolucao",nullable=true) LocalDate previsaoDevolucao;
@Column(name="proxima_inspecao",nullable=true) LocalDate proximaInspecao;
@Column(name="observacao",nullable=true,length=2000) String observacao;
@Column(name="ator_id",nullable=false) Long atorId;
@Column(name="ator_nome",nullable=false,length=255) String atorNome;
@Column(name="chave_idempotencia",nullable=true,length=100,unique=true) String chaveIdempotencia;
@Column(name="assinatura",nullable=true,length=64) String assinatura;
@Enumerated(EnumType.STRING) @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR) @Column(name="tipo",nullable=false,length=30) TipoRegistroAtivo tipo;
@Enumerated(EnumType.STRING) @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR) @Column(name="status_antes",nullable=false,length=30) StatusAtivo statusAntes;
@Enumerated(EnumType.STRING) @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR) @Column(name="status_depois",nullable=false,length=30) StatusAtivo statusDepois;
@Enumerated(EnumType.STRING) @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR) @Column(name="condicao_antes",nullable=false,length=30) CondicaoAtivo condicaoAntes;
@Enumerated(EnumType.STRING) @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR) @Column(name="condicao_depois",nullable=false,length=30) CondicaoAtivo condicaoDepois;
@Enumerated(EnumType.STRING) @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR) @Column(name="resultado",nullable=true,length=30) ResultadoInspecao resultado;
@Embedded @AttributeOverrides({@AttributeOverride(name="obraId",column=@Column(name="origem_contexto_obra_id")),@AttributeOverride(name="ordemServicoId",column=@Column(name="origem_contexto_ordem_servico_id")),@AttributeOverride(name="centroCustoId",column=@Column(name="origem_contexto_centro_custo_id")),@AttributeOverride(name="obraCodigo",column=@Column(name="origem_contexto_obra_codigo",length=50)),@AttributeOverride(name="obraNome",column=@Column(name="origem_contexto_obra_nome",length=160)),@AttributeOverride(name="ordemServicoNumero",column=@Column(name="origem_contexto_ordem_servico_numero",length=50)),@AttributeOverride(name="centroCustoCodigo",column=@Column(name="origem_contexto_centro_custo_codigo",length=50)),@AttributeOverride(name="centroCustoNome",column=@Column(name="origem_contexto_centro_custo_nome",length=160))})
 ContextoOperacional contextoOrigem;
@Embedded @AttributeOverrides({@AttributeOverride(name="obraId",column=@Column(name="destino_contexto_obra_id")),@AttributeOverride(name="ordemServicoId",column=@Column(name="destino_contexto_ordem_servico_id")),@AttributeOverride(name="centroCustoId",column=@Column(name="destino_contexto_centro_custo_id")),@AttributeOverride(name="obraCodigo",column=@Column(name="destino_contexto_obra_codigo",length=50)),@AttributeOverride(name="obraNome",column=@Column(name="destino_contexto_obra_nome",length=160)),@AttributeOverride(name="ordemServicoNumero",column=@Column(name="destino_contexto_ordem_servico_numero",length=50)),@AttributeOverride(name="centroCustoCodigo",column=@Column(name="destino_contexto_centro_custo_codigo",length=50)),@AttributeOverride(name="centroCustoNome",column=@Column(name="destino_contexto_centro_custo_nome",length=160))})
 ContextoOperacional contextoDestino;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="ativo_id",insertable=false,updatable=false) @JsonIgnore private Ativo ativoRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="origem_id",insertable=false,updatable=false) @JsonIgnore private RegistroAtivo origemRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="ator_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.security.Usuario atorRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="entregue_por_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.model.Funcionario entregueporidRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="recebido_por_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.model.Funcionario recebidoporidRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="responsavel_antes_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.model.Funcionario responsavelantesidRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="responsavel_depois_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.model.Funcionario responsaveldepoisidRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="local_origem_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.model.Almoxarifado localorigemidRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="local_destino_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.model.Almoxarifado localdestinoidRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="origem_contexto_obra_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.obras.Obra origem_contextoobra_idRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="origem_contexto_ordem_servico_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.obras.OrdemServico origem_contextoordem_servico_idRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="origem_contexto_centro_custo_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.obras.CentroCusto origem_contextocentro_custo_idRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="destino_contexto_obra_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.obras.Obra destino_contextoobra_idRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="destino_contexto_ordem_servico_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.obras.OrdemServico destino_contextoordem_servico_idRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="destino_contexto_centro_custo_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.obras.CentroCusto destino_contextocentro_custo_idRef;
}
