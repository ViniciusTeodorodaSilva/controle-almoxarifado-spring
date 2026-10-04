package br.com.almoxarifado.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "solicitacao")
public class Solicitacao {
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
