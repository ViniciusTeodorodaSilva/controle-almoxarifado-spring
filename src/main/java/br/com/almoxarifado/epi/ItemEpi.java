package br.com.almoxarifado.epi;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity @org.hibernate.annotations.Immutable
@Table(name="bes_epi_item",indexes={@Index(name="ix_epi_item_reg",columnList="registro_id"),@Index(name="ix_epi_item_produto",columnList="produto_id"),@Index(name="ix_epi_item_origem",columnList="origem_item_id,entrega"),@Index(name="ix_epi_item_validade",columnList="validade_fisica"),@Index(name="ix_epi_item_troca",columnList="substituir_ate")})
public class ItemEpi {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Integer id;
 @Column(name="registro_id",nullable=false) Integer registroId;
 @Column(name="produto_id",nullable=false) Integer produtoId;
 @Column(name="produto_codigo",length=64) String produtoCodigo;
 @Column(name="produto_nome",nullable=false,length=255) String produtoNome;
 @Column(nullable=false,length=64) String unidade;
 @Column(nullable=false,precision=19,scale=6) BigDecimal quantidade;
 @Column(nullable=false) boolean entrega;
 @Column(nullable=false) boolean fracionado;
 @Embedded br.com.almoxarifado.obras.ContextoOperacional contexto;
 @Column(name="origem_item_id") Integer origemItemId;
 @Column(name="movimento_id",unique=true) Integer movimentoId;
 @Column(nullable=false,length=20) String ca;
 @Column(name="validade_ca") LocalDate validadeCa;
 @Column(length=160) String fabricante;
 @Column(length=160) String modelo;
 @Column(length=50) String tamanho;
 @Column(length=100) String lote;
 @Column(name="fabricacao") LocalDate fabricacao;
 @Column(name="validade_fisica") LocalDate validadeFisica;
 @Column(name="substituir_ate") LocalDate substituirAte;
 @Column(name="exige_devolucao",nullable=false) boolean exigeDevolucao;
 @Column(name="permite_retorno",nullable=false) boolean permiteRetorno;
 @Enumerated(EnumType.STRING) @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR) @Column(length=30) CondicaoEpi condicao;
 @Enumerated(EnumType.STRING) @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR) @Column(length=30) DestinoEpi destino;
 @Column(length=2000) String observacao;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="registro_id",insertable=false,updatable=false) @JsonIgnore private RegistroEpi registroRef;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="produto_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.model.Produto produtoRef;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="origem_item_id",insertable=false,updatable=false) @JsonIgnore private ItemEpi origemRef;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="movimento_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.model.Movimentacao movimentoRef;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="contexto_obra_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.obras.Obra obraRef;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="contexto_ordem_servico_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.obras.OrdemServico osRef;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="contexto_centro_custo_id",insertable=false,updatable=false) @JsonIgnore private br.com.almoxarifado.obras.CentroCusto ccRef;
}
