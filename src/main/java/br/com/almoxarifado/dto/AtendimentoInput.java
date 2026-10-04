package br.com.almoxarifado.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
public record AtendimentoInput(@NotNull @Positive Integer responsavelId,
    @NotEmpty @Size(max=500) List<@NotNull @Valid Item> itens) {
    public record Item(@NotNull @Positive Integer itemSolicitacaoId,@NotNull Double quantidade) {}
}
