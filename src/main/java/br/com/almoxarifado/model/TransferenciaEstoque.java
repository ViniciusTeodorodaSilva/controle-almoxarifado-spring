package br.com.almoxarifado.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "transferencia_estoque")
public class TransferenciaEstoque {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @ManyToOne @JoinColumn(name = "almoxarifado_origem_id", nullable = false) private Almoxarifado almoxarifadoOrigem;
    @ManyToOne @JoinColumn(name = "almoxarifado_destino_id", nullable = false) private Almoxarifado almoxarifadoDestino;
    @ManyToOne @JoinColumn(name = "responsavel_id", nullable = false) private Funcionario responsavel;
    @Column(name = "data_hora", nullable = false) private LocalDateTime dataHora;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private StatusTransferencia status;
    @Column(length = 1000) private String observacao;
    @OneToMany(mappedBy = "transferencia", cascade = CascadeType.PERSIST)
    @OrderBy("id ASC") private List<ItemTransferencia> itens = new ArrayList<>();
    public Integer getId() { return id; }
    public Almoxarifado getAlmoxarifadoOrigem() { return almoxarifadoOrigem; }
    public void setAlmoxarifadoOrigem(Almoxarifado valor) { almoxarifadoOrigem = valor; }
    public Almoxarifado getAlmoxarifadoDestino() { return almoxarifadoDestino; }
    public void setAlmoxarifadoDestino(Almoxarifado valor) { almoxarifadoDestino = valor; }
    public Funcionario getResponsavel() { return responsavel; }
    public void setResponsavel(Funcionario valor) { responsavel = valor; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime valor) { dataHora = valor; }
    public StatusTransferencia getStatus() { return status; }
    public void setStatus(StatusTransferencia valor) { status = valor; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String valor) { observacao = valor; }
    public List<ItemTransferencia> getItens() { return itens; }
}
