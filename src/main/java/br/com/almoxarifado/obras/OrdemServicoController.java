package br.com.almoxarifado.obras;

import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordens-servico")
public class OrdemServicoController {
  private final EstruturaService s;
  private final ResumoEstruturaService resumo;

  public OrdemServicoController(EstruturaService s, ResumoEstruturaService r) {
    this.s = s;
    resumo = r;
  }

  @GetMapping
  public br.com.almoxarifado.compras.ComprasPage<OrdemServico> listar(
      @RequestParam(required = false) String termo,
      @RequestParam(required = false) StatusOrdemServico status,
      @RequestParam(required = false) Integer obraId,
      @RequestParam(required = false) Integer centroCustoId,
      @RequestParam(defaultValue = "0") int pagina,
      @RequestParam(defaultValue = "20") int tamanho) {
    return br.com.almoxarifado.compras.ComprasPage.of(
        s.listarOrdens(termo, status, obraId, centroCustoId, pagina, tamanho));
  }

  @GetMapping("/{id}")
  public OrdemServico buscar(@PathVariable Integer id) {
    return s.ordem(id);
  }

  @PostMapping
  public OrdemServico criar(@RequestBody ObrasInput.Ordem in) {
    return s.salvarOrdem(null, in);
  }

  @PutMapping("/{id}")
  public OrdemServico atualizar(@PathVariable Integer id, @RequestBody ObrasInput.Ordem in) {
    return s.salvarOrdem(id, in);
  }

  @GetMapping("/{id}/resumo")
  public Map<String, Object> resumo(@PathVariable Integer id) {
    return resumo.ordem(id);
  }

  @PutMapping("/{id}/status")
  public OrdemServico status(@PathVariable Integer id, @RequestBody ObrasInput.Transicao in) {
    return s.statusOrdem(id, in);
  }
}
