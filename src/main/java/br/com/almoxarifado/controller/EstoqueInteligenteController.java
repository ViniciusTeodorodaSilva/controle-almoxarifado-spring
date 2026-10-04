package br.com.almoxarifado.controller;
import br.com.almoxarifado.dto.*;
import br.com.almoxarifado.service.EstoqueInteligenteService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/estoques")
public class EstoqueInteligenteController {
    private final EstoqueInteligenteService service;
    public EstoqueInteligenteController(EstoqueInteligenteService service) { this.service = service; }
    @PutMapping("/{id}/limites") public AlertaEstoqueResponse configurar(@PathVariable Integer id, @RequestBody LimitesEstoqueInput dados) { return service.configurar(id, dados); }
    @GetMapping("/alertas") public List<AlertaEstoqueResponse> alertas(@RequestParam(required = false) Integer produtoId,
            @RequestParam(required = false) Integer almoxarifadoId) { return service.alertas(produtoId, almoxarifadoId); }
    @GetMapping("/reposicoes") public List<AlertaEstoqueResponse> reposicoes(@RequestParam(required = false) Integer produtoId,
            @RequestParam(required = false) Integer almoxarifadoId) { return service.reposicoes(produtoId, almoxarifadoId); }
}
