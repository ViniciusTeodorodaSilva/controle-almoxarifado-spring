package br.com.almoxarifado.obras;

import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/obras")
public class ObraController {
  private final EstruturaService s;
  private final ResumoEstruturaService resumo;

  public ObraController(EstruturaService s, ResumoEstruturaService r) {
    this.s = s;
    resumo = r;
  }

  @GetMapping
  public br.com.almoxarifado.compras.ComprasPage<Obra> listar(
      @RequestParam(required = false) String termo,
      @RequestParam(required = false) String cliente,
      @RequestParam(required = false) StatusObra status,
      @RequestParam(defaultValue = "0") int pagina,
      @RequestParam(defaultValue = "20") int tamanho) {
    return br.com.almoxarifado.compras.ComprasPage.of(
        s.listarObras(termo, cliente, status, pagina, tamanho));
  }

  @GetMapping("/{id}")
  public Obra buscar(@PathVariable Integer id) {
    return s.obra(id);
  }

  @PostMapping
  public Obra criar(@RequestBody ObrasInput.Obra in) {
    return s.salvarObra(null, in);
  }

  @PutMapping("/{id}")
  public Obra atualizar(@PathVariable Integer id, @RequestBody ObrasInput.Obra in) {
    return s.salvarObra(id, in);
  }

  @GetMapping("/{id}/resumo")
  public Map<String, Object> resumo(@PathVariable Integer id) {
    return resumo.obra(id);
  }

  @PutMapping("/{id}/status")
  public Obra status(@PathVariable Integer id, @RequestBody ObrasInput.Transicao in) {
    return s.statusObra(id, in);
  }
}
