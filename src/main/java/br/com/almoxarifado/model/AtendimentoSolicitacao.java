package br.com.almoxarifado.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name="atendimento_solicitacao")
public class AtendimentoSolicitacao {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
    public Integer getId() { return id; }
    @ManyToOne @JoinColumn(name="solicitacao_id",nullable=false) private Solicitacao solicitacao;
    public Solicitacao getSolicitacao() { return solicitacao; }
    public void setSolicitacao(Solicitacao value) { solicitacao=value; }
    @ManyToOne @JoinColumn(name="responsavel_id",nullable=false) private Funcionario responsavel;
    public Funcionario getResponsavel() { return responsavel; }
    public void setResponsavel(Funcionario value) { responsavel=value; }
    @Column(name="data_hora",nullable=false) private LocalDateTime dataHora;
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime value) { dataHora=value; }
    @Column(name="chave_idempotencia",length=100,nullable=false,unique=true) private String chaveIdempotencia;
    public String getChaveIdempotencia() { return chaveIdempotencia; }
    public void setChaveIdempotencia(String value) { chaveIdempotencia=value; }
    @Column(name="assinatura_payload",length=64,nullable=false) private String assinaturaPayload;
    public String getAssinaturaPayload() { return assinaturaPayload; }
    public void setAssinaturaPayload(String value) { assinaturaPayload=value; }
}
