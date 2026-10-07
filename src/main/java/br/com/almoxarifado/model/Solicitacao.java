package br.com.almoxarifado.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "solicitacao", indexes = {@Index(name="ix_solicitacao_ctx_obra",columnList="contexto_obra_id"),@Index(name="ix_solicitacao_ctx_ordem_servico",columnList="contexto_ordem_servico_id"),@Index(name="ix_solicitacao_ctx_centro_custo",columnList="contexto_centro_custo_id")})
public class Solicitacao {
 @jakarta.persistence.Embedded private br.com.almoxarifado.obras.ContextoOperacional contexto;
 @jakarta.persistence.ManyToOne(fetch=jakarta.persistence.FetchType.LAZY) @jakarta.persistence.JoinColumn(name="contexto_obra_id",insertable=false,updatable=false) @com.fasterxml.jackson.annotation.JsonIgnore private br.com.almoxarifado.obras.Obra contextoObraRef;
 @jakarta.persistence.ManyToOne(fetch=jakarta.persistence.FetchType.LAZY) @jakarta.persistence.JoinColumn(name="contexto_ordem_servico_id",insertable=false,updatable=false) @com.fasterxml.jackson.annotation.JsonIgnore private br.com.almoxarifado.obras.OrdemServico contextoOSRef;
 @jakarta.persistence.ManyToOne(fetch=jakarta.persistence.FetchType.LAZY) @jakarta.persistence.JoinColumn(name="contexto_centro_custo_id",insertable=false,updatable=false) @com.fasterxml.jackson.annotation.JsonIgnore private br.com.almoxarifado.obras.CentroCusto contextoCCRef;
 public br.com.almoxarifado.obras.ContextoOperacional getContexto(){return contexto;}
 public void setContexto(br.com.almoxarifado.obras.ContextoOperacional v){contexto=v;}

    @ManyToOne @JoinColumn(name="responsavel_aprovacao_id") private Funcionario responsavelAprovacao;
    public Funcionario getResponsavelAprovacao() { return responsavelAprovacao; }
    public void setResponsavelAprovacao(Funcionario value) { responsavelAprovacao=value; }
    @Column(name="data_aprovacao") private LocalDateTime dataAprovacao;
    public LocalDateTime getDataAprovacao() { return dataAprovacao; }
    public void setDataAprovacao(LocalDateTime value) { dataAprovacao=value; }
    @ManyToOne @JoinColumn(name="responsavel_separacao_id") private Funcionario responsavelSeparacao;
    public Funcionario getResponsavelSeparacao() { return responsavelSeparacao; }
    public void setResponsavelSeparacao(Funcionario value) { responsavelSeparacao=value; }
    @Column(name="data_separacao") private LocalDateTime dataSeparacao;
    public LocalDateTime getDataSeparacao() { return dataSeparacao; }
    public void setDataSeparacao(LocalDateTime value) { dataSeparacao=value; }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    private Funcionario solicitante;

    @ManyToOne
    @JoinColumn(name = "almoxarifado_id")
    private Almoxarifado almoxarifado;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacao status;

    @Column(name = "data_solicitacao")
    private LocalDateTime dataSolicitacao;

    @OneToMany(mappedBy = "solicitacao")
    private List<ItemSolicitacao> itens;

    public List<ItemSolicitacao> getItens() {
        return itens;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Funcionario getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Funcionario solicitante) {
        this.solicitante = solicitante;
    }

    public Almoxarifado getAlmoxarifado() {
        return almoxarifado;
    }

    public void setAlmoxarifado(Almoxarifado almoxarifado) {
        this.almoxarifado = almoxarifado;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }
}
