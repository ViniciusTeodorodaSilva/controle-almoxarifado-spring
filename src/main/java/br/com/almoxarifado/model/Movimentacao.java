package br.com.almoxarifado.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacao", indexes = {@Index(name="ix_movimentacao_ctx_obra",columnList="contexto_obra_id"),@Index(name="ix_movimentacao_ctx_ordem_servico",columnList="contexto_ordem_servico_id"),@Index(name="ix_movimentacao_ctx_centro_custo",columnList="contexto_centro_custo_id")})
public class Movimentacao {
 @jakarta.persistence.Embedded private br.com.almoxarifado.obras.ContextoOperacional contexto;
 @jakarta.persistence.ManyToOne(fetch=jakarta.persistence.FetchType.LAZY) @jakarta.persistence.JoinColumn(name="contexto_obra_id",insertable=false,updatable=false) @com.fasterxml.jackson.annotation.JsonIgnore private br.com.almoxarifado.obras.Obra contextoObraRef;
 @jakarta.persistence.ManyToOne(fetch=jakarta.persistence.FetchType.LAZY) @jakarta.persistence.JoinColumn(name="contexto_ordem_servico_id",insertable=false,updatable=false) @com.fasterxml.jackson.annotation.JsonIgnore private br.com.almoxarifado.obras.OrdemServico contextoOSRef;
 @jakarta.persistence.ManyToOne(fetch=jakarta.persistence.FetchType.LAZY) @jakarta.persistence.JoinColumn(name="contexto_centro_custo_id",insertable=false,updatable=false) @com.fasterxml.jackson.annotation.JsonIgnore private br.com.almoxarifado.obras.CentroCusto contextoCCRef;
 public br.com.almoxarifado.obras.ContextoOperacional getContexto(){return contexto;}
 public void setContexto(br.com.almoxarifado.obras.ContextoOperacional v){contexto=v;}

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ator_compra_id",insertable=false,updatable=false)
    private br.com.almoxarifado.security.Usuario atorCompraReferencia;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="item_recebimento_compra_id",unique=true) @JsonIgnore
    private br.com.almoxarifado.compras.ItemRecebimentoCompra itemRecebimentoCompra;
    public void setItemRecebimentoCompra(br.com.almoxarifado.compras.ItemRecebimentoCompra v){itemRecebimentoCompra=v;}
    public Integer getRecebimentoCompraId(){return itemRecebimentoCompra==null?null:itemRecebimentoCompra.getRecebimentoId();}
    public Integer getPedidoCompraId(){return itemRecebimentoCompra==null?null:itemRecebimentoCompra.getPedidoId();}
    @Column(name="ator_compra_id")
    private Long atorCompraId;
    public Long getAtorCompraId(){return atorCompraId;}
    public void setAtorCompraId(Long v){atorCompraId=v;}

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="atendimento_id") @JsonIgnore
    private AtendimentoSolicitacao atendimento;
    public AtendimentoSolicitacao getAtendimento() { return atendimento; }
    public void setAtendimento(AtendimentoSolicitacao value) { atendimento=value; }
    public Integer getAtendimentoId() { return atendimento == null ? null : atendimento.getId(); }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transferencia_id", foreignKey = @ForeignKey(name = "fk_movimentacao_transferencia"))
    @JsonIgnore private TransferenciaEstoque transferencia;
    public TransferenciaEstoque getTransferencia() { return transferencia; }
    public void setTransferencia(TransferenciaEstoque valor) { transferencia = valor; }
    public Integer getTransferenciaId() { return transferencia == null ? null : transferencia.getId(); }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_id", foreignKey = @ForeignKey(name = "fk_movimentacao_solicitacao"))
    @JsonIgnore
    private Solicitacao solicitacao;

    public Solicitacao getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(Solicitacao solicitacao) {
        this.solicitacao = solicitacao;
    }

    public Integer getSolicitacaoId() {
        return solicitacao == null ? null : solicitacao.getId();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "almoxarifado_id")
    private Almoxarifado almoxarifado;

    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    private Funcionario solicitante;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Funcionario responsavel;

    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipo;

    private double quantidade;
    @Column(name = "saldo_anterior")
    private double saldoAnterior;
    @Column(name = "saldo_posterior")
    private double saldoPosterior;
    @Column(name = "data_hora")
    private LocalDateTime dataHora;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Almoxarifado getAlmoxarifado() {
        return almoxarifado;
    }

    public void setAlmoxarifado(Almoxarifado almoxarifado) {
        this.almoxarifado = almoxarifado;
    }

    public Funcionario getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Funcionario solicitante) {
        this.solicitante = solicitante;
    }

    public Funcionario getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Funcionario responsavel) {
        this.responsavel = responsavel;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public double getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(double saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public double getSaldoPosterior() {
        return saldoPosterior;
    }

    public void setSaldoPosterior(double saldoPosterior) {
        this.saldoPosterior = saldoPosterior;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
