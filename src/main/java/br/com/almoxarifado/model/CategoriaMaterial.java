package br.com.almoxarifado.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "categoria_material")
public class CategoriaMaterial {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private String descricao;
    private boolean ativo = true;
    @Column(nullable = false, unique = true, length = 255)
    @JsonIgnore
    private String nomeNormalizado;
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String valor) { this.nome = valor; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String valor) { this.descricao = valor; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean valor) { this.ativo = valor; }
    public String getNomeNormalizado() { return nomeNormalizado; }
    public void setNomeNormalizado(String valor) { this.nomeNormalizado = valor; }
}
