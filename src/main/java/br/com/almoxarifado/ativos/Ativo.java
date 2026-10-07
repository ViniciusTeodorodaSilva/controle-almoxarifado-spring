package br.com.almoxarifado.ativos;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import br.com.almoxarifado.obras.ContextoOperacional;
import java.time.*;
@Entity @Table(name="bes_ativo",indexes={@Index(name="ix_ativo_status",columnList="status,ativo"),@Index(name="ix_ativo_local",columnList="almoxarifado_id"),@Index(name="ix_ativo_responsavel",columnList="responsavel_id"),@Index(name="ix_ativo_inspecao",columnList="proxima_inspecao")})
public class Ativo {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Integer id;
 @Version @JsonIgnore long versao;
@Column(name="codigo_patrimonial",nullable=false,length=50,unique=true) String codigoPatrimonial;
@Column(name="nome",nullable=false,length=160) String nome;
@Column(name="descricao",nullable=true,length=2000) String descricao;
@Column(name="fabricante",nullable=true,length=160) String fabricante;
@Column(name="modelo",nullable=true,length=160) String modelo;
@Column(name="numero_serie",nullable=true,length=160) String numeroSerie;
@Column(name="observacao",nullable=true,length=2000) String observacao;
@Column(name="categoria_id",nullable=true) Integer categoriaId;
@Column(name="almoxarifado_id",nullable=true) Integer almoxarifadoId;
@Column(name="responsavel_id",nullable=true) Integer responsavelId;
@Column(name="pendencia_id",nullable=true,unique=true) Integer pendenciaId;
@Column(name="data_aquisicao",nullable=true) LocalDate dataAquisicao;
@Column(name="proxima_inspecao",nullable=true) LocalDate proximaInspecao;
@Column(name="ativo",nullable=false) boolean ativo;
@Column(name="reprovado",nullable=false) boolean reprovado;
@Column(name="criado_em",nullable=false) LocalDateTime criadoEm;
@Column(name="atualizado_em",nullable=false) LocalDateTime atualizadoEm;
@Column(name="criado_por",nullable=false) Long criadoPor;
@Column(name="alterado_por",nullable=false) Long alteradoPor;
@Enumerated(EnumType.STRING) @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR) @Column(name="status",nullable=false,length=30) StatusAtivo status;
@Enumerated(EnumType.STRING) @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR) @Column(name="condicao",nullable=false,length=30) CondicaoAtivo condicao;
@Embedded @AttributeOverrides({@AttributeOverride(name="obraId",column=@Column(name="contexto_obra_id")),@AttributeOverride(name="ordemServicoId",column=@Column(name="contexto_ordem_servico_id")),@AttributeOverride(name="centroCustoId",column=@Column(name="contexto_centro_custo_id")),@AttributeOverride(name="obraCodigo",column=@Column(name="contexto_obra_codigo",length=50)),@AttributeOverride(name="obraNome",column=@Column(name="contexto_obra_nome",length=160)),@AttributeOverride(name="ordemServicoNumero",column=@Column(name="contexto_ordem_servico_numero",length=50)),@AttributeOverride(name="centroCustoCodigo",column=@Column(name="contexto_centro_custo_codigo",length=50)),@AttributeOverride(name="centroCustoNome",column=@Column(name="contexto_centro_custo_nome",length=160))})
 ContextoOperacional contexto;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="categoria_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.model.CategoriaMaterial categoriaRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="almoxarifado_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.model.Almoxarifado localRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="responsavel_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.model.Funcionario pessoaRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="pendencia_id",insertable=false,updatable=false) @JsonIgnore private RegistroAtivo pendenciaRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="criado_por",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.security.Usuario criadorRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="alterado_por",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.security.Usuario editorRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="contexto_obra_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.obras.Obra obraRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="contexto_ordem_servico_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.obras.OrdemServico osRef;
@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="contexto_centro_custo_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.obras.CentroCusto ccRef;
}
