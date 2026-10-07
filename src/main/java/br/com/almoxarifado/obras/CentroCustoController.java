package br.com.almoxarifado.obras;

import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/centros-custo")
public class CentroCustoController {
  private final EstruturaService s;
  private final ResumoEstruturaService resumo;

  public CentroCustoController(EstruturaService s, ResumoEstruturaService r) {
    this.s = s;
    resumo = r;
  }

  @GetMapping
  public br.com.almoxarifado.compras.ComprasPage<CentroCusto> listar(
      @RequestParam(required = false) String termo,
      @RequestParam(required = false) TipoCentroCusto tipo,
      @RequestParam(required = false) Boolean ativo,
      @RequestParam(required = false) Integer obraId,
      @RequestParam(defaultValue = "false") boolean incluirGerais,
      @RequestParam(defaultValue = "0") int pagina,
      @RequestParam(defaultValue = "20") int tamanho) {
    return br.com.almoxarifado.compras.ComprasPage.of(
        s.listarCentros(termo, tipo, ativo, obraId, pagina, tamanho, incluirGerais));
  }

  @GetMapping("/{id}")
  public CentroCusto buscar(@PathVariable Integer id) {
    return s.centro(id);
  }

  @PostMapping
  public CentroCusto criar(@RequestBody ObrasInput.Centro in) {
    return s.salvarCentro(null, in);
  }

  @PutMapping("/{id}")
  public CentroCusto atualizar(@PathVariable Integer id, @RequestBody ObrasInput.Centro in) {
    return s.salvarCentro(id, in);
  }

  @GetMapping("/{id}/resumo")
  public Map<String, Object> resumo(@PathVariable Integer id) {
    return resumo.centro(id);
  }
}
