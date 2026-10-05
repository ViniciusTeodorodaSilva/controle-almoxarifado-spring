package br.com.almoxarifado.compras;

import java.util.List;
import org.springframework.data.domain.Page;

/** Stable HTTP envelope independent of Spring PageImpl serialization internals. */
public record ComprasPage<T>(
    List<T> content,
    int number,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last) {
  public static <T> ComprasPage<T> of(Page<T> p) {
    return new ComprasPage<>(
        p.getContent(),
        p.getNumber(),
        p.getSize(),
        p.getTotalElements(),
        p.getTotalPages(),
        p.isFirst(),
        p.isLast());
  }
}
