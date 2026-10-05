package br.com.almoxarifado.compras;

import br.com.almoxarifado.model.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(
    name = "bes_fornecedor",
    indexes = {
      @Index(name = "ix_fornecedor_nome", columnList = "nome"),
      @Index(name = "ix_fornecedor_ativo", columnList = "ativo")
    })
public class Fornecedor {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  public Integer getId() {
    return id;
  }

  @Column(nullable = false, length = 160)
  String nome;

  @Column(length = 160)
  String nomeFantasia;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 2)
  TipoPessoa tipoPessoa;

  @Column(length = 14, unique = true)
  String documento;

  @Column(length = 160)
  String email;

  @Column(length = 40)
  String telefone;

  @Column(length = 120)
  String contato;

  @Column(length = 1000)
  String observacao;

  @Column(nullable = false)
  boolean ativo = true;

  @Column(nullable = false)
  LocalDateTime criadoEm;

  @Column(nullable = false)
  LocalDateTime atualizadoEm;

  @Version long versao;

  public void setId(Integer v) {
    id = v;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String v) {
    nome = v;
  }

  public String getNomeFantasia() {
    return nomeFantasia;
  }

  public void setNomeFantasia(String v) {
    nomeFantasia = v;
  }

  public TipoPessoa getTipoPessoa() {
    return tipoPessoa;
  }

  public void setTipoPessoa(TipoPessoa v) {
    tipoPessoa = v;
  }

  public String getDocumento() {
    return documento;
  }

  public void setDocumento(String v) {
    documento = v;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String v) {
    email = v;
  }

  public String getTelefone() {
    return telefone;
  }

  public void setTelefone(String v) {
    telefone = v;
  }

  public String getContato() {
    return contato;
  }

  public void setContato(String v) {
    contato = v;
  }

  public String getObservacao() {
    return observacao;
  }

  public void setObservacao(String v) {
    observacao = v;
  }

  public boolean getAtivo() {
    return ativo;
  }

  public void setAtivo(boolean v) {
    ativo = v;
  }

  public LocalDateTime getCriadoEm() {
    return criadoEm;
  }

  public void setCriadoEm(LocalDateTime v) {
    criadoEm = v;
  }

  public LocalDateTime getAtualizadoEm() {
    return atualizadoEm;
  }

  public void setAtualizadoEm(LocalDateTime v) {
    atualizadoEm = v;
  }

  public long getVersao() {
    return versao;
  }

  public void setVersao(long v) {
    versao = v;
  }
}
