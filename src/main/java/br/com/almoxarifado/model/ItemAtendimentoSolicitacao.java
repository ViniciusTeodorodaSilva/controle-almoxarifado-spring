package br.com.almoxarifado.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name="item_atendimento_solicitacao", uniqueConstraints=@UniqueConstraint(name="uk_atendimento_item",columnNames={"atendimento_id","item_solicitacao_id"}))
public class ItemAtendimentoSolicitacao {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
    public Integer getId() { return id; }
    @ManyToOne @JoinColumn(name="atendimento_id",nullable=false) private AtendimentoSolicitacao atendimento;
    public AtendimentoSolicitacao getAtendimento() { return atendimento; }
    public void setAtendimento(AtendimentoSolicitacao value) { atendimento=value; }
    @ManyToOne @JoinColumn(name="item_solicitacao_id",nullable=false) private ItemSolicitacao itemSolicitacao;
    public ItemSolicitacao getItemSolicitacao() { return itemSolicitacao; }
    public void setItemSolicitacao(ItemSolicitacao value) { itemSolicitacao=value; }
    @Column(nullable=false) private double quantidade;
    public double getQuantidade() { return quantidade; }
    public void setQuantidade(double value) { quantidade=value; }
}
