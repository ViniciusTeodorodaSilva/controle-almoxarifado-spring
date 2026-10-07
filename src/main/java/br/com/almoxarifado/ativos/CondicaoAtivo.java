package br.com.almoxarifado.ativos;
public enum CondicaoAtivo {
  NOVO, BOM, REGULAR, DANIFICADO, INOPERANTE;
  public boolean utilizavel() { return this != DANIFICADO && this != INOPERANTE; }
}
