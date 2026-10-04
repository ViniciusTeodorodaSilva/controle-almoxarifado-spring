package br.com.almoxarifado.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "unidade_medida")
public class UnidadeMedida {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    @Column(nullable = false, unique = true, length = 64)
    private String sigla;
    private boolean permiteFracionamento;
    private boolean ativo = true;
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String valor) { this.nome = valor; }
    public String getSigla() { return sigla; }
    public void setSigla(String valor) { this.sigla = valor; }
    public boolean isPermiteFracionamento() { return permiteFracionamento; }
    public void setPermiteFracionamento(boolean valor) { this.permiteFracionamento = valor; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean valor) { this.ativo = valor; }
}
