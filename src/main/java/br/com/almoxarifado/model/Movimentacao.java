package br.com.almoxarifado.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacao")
public class Movimentacao {
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
