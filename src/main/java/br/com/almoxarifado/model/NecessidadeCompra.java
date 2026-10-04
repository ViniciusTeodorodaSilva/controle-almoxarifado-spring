package br.com.almoxarifado.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name="necessidade_compra")
public class NecessidadeCompra {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
    public Integer getId() { return id; }
    @ManyToOne @JoinColumn(name="item_solicitacao_id",nullable=false,unique=true) private ItemSolicitacao itemSolicitacao;
    public ItemSolicitacao getItemSolicitacao() { return itemSolicitacao; }
    public void setItemSolicitacao(ItemSolicitacao value) { itemSolicitacao=value; }
    @ManyToOne @JoinColumn(name="produto_id",nullable=false) private Produto produto;
    public Produto getProduto() { return produto; }
    public void setProduto(Produto value) { produto=value; }
    @ManyToOne @JoinColumn(name="solicitacao_id",nullable=false) private Solicitacao solicitacao;
    public Solicitacao getSolicitacao() { return solicitacao; }
    public void setSolicitacao(Solicitacao value) { solicitacao=value; }
    @ManyToOne @JoinColumn(name="almoxarifado_id",nullable=false) private Almoxarifado almoxarifado;
    public Almoxarifado getAlmoxarifado() { return almoxarifado; }
    public void setAlmoxarifado(Almoxarifado value) { almoxarifado=value; }
    @ManyToOne @JoinColumn(name="responsavel_id",nullable=false) private Funcionario responsavel;
    public Funcionario getResponsavel() { return responsavel; }
    public void setResponsavel(Funcionario value) { responsavel=value; }
    @Column(nullable=false) private double quantidade;
    public double getQuantidade() { return quantidade; }
    public void setQuantidade(double value) { quantidade=value; }
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private StatusNecessidadeCompra status;
    public StatusNecessidadeCompra getStatus() { return status; }
    public void setStatus(StatusNecessidadeCompra value) { status=value; }
    @Column(name="data_hora",nullable=false) private LocalDateTime dataHora;
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime value) { dataHora=value; }
    @Column(nullable=false,length=100) private String motivo;
    public String getMotivo() { return motivo; }
    public void setMotivo(String value) { motivo=value; }
    @Column(name="chave_idempotencia",nullable=false,length=100,unique=true) private String chaveIdempotencia;
    public String getChaveIdempotencia() { return chaveIdempotencia; }
    public void setChaveIdempotencia(String value) { chaveIdempotencia=value; }
    @Column(name="assinatura_payload",length=64,nullable=false) private String assinaturaPayload;
    public String getAssinaturaPayload() { return assinaturaPayload; }
    public void setAssinaturaPayload(String value) { assinaturaPayload=value; }
}
