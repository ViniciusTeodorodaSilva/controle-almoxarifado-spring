package br.com.almoxarifado.controller;
import br.com.almoxarifado.dto.*;
import br.com.almoxarifado.service.TransferenciaEstoqueService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/transferencias")
public class TransferenciaEstoqueController {
    private final TransferenciaEstoqueService service;
    public TransferenciaEstoqueController(TransferenciaEstoqueService service) { this.service = service; }
    @PostMapping public TransferenciaResponse criar(@Valid @RequestBody TransferenciaInput dados,
            @RequestHeader(value = "Idempotency-Key", required = false) String chave) { return service.criar(dados, chave); }
    @GetMapping public List<TransferenciaResponse> listar(@RequestParam(required = false) Integer origemId,
            @RequestParam(required = false) Integer destinoId, @RequestParam(required = false) Integer produtoId) { return service.listar(origemId, destinoId, produtoId); }
    @GetMapping("/{id}") public TransferenciaResponse buscar(@PathVariable Integer id) { return service.buscar(id); }
    @GetMapping("/{id}/movimentacoes") public List<MovimentacaoTransferenciaResponse> movimentacoes(@PathVariable Integer id) { return service.movimentacoes(id); }
}
