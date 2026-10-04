package br.com.almoxarifado.dto;
import jakarta.validation.constraints.*;
public record NecessidadeCompraInput(@NotNull @Positive Integer itemSolicitacaoId,@NotNull @Positive Integer responsavelId) {}
