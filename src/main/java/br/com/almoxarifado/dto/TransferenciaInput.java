package br.com.almoxarifado.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
public record TransferenciaInput(@NotNull @Positive Integer origemId, @NotNull @Positive Integer destinoId,
        @NotNull @Positive Integer responsavelId, @Size(max = 1000) String observacao,
        @NotEmpty @Size(max = 500) List<@NotNull @Valid Item> itens) {
    public record Item(@NotNull @Positive Integer produtoId, @NotNull java.math.BigDecimal quantidade) {
        // Compatibility for Java callers; HTTP decimal values are parsed without a double hop.
        public Item(Integer produtoId, double quantidade) { this(produtoId, java.math.BigDecimal.valueOf(quantidade)); }
    }
}
