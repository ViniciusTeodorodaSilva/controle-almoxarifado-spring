package br.com.almoxarifado.controller;
import br.com.almoxarifado.dto.*;
import br.com.almoxarifado.model.StatusNecessidadeCompra;
import br.com.almoxarifado.service.NecessidadeCompraService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/necessidades-compra")
public class NecessidadeCompraController {
    private final NecessidadeCompraService service;
    public NecessidadeCompraController(NecessidadeCompraService service) { this.service=service; }
    @PostMapping public NecessidadeCompraResponse criar(@Valid @RequestBody NecessidadeCompraInput input,@RequestHeader("Idempotency-Key") String chave) { return service.criar(input,chave); }
    @GetMapping public List<NecessidadeCompraResponse> listar(@RequestParam(required=false) StatusNecessidadeCompra status,
        @RequestParam(required=false) Integer produtoId,@RequestParam(required=false) Integer almoxarifadoId,@RequestParam(required=false) Integer solicitacaoId,@RequestParam(required=false) Integer obraId,@RequestParam(required=false) Integer ordemServicoId,@RequestParam(required=false) Integer centroCustoId) { return service.listar(status,produtoId,almoxarifadoId,solicitacaoId,obraId,ordemServicoId,centroCustoId); }
    @GetMapping("/{id}") public NecessidadeCompraResponse buscar(@PathVariable Integer id) { return service.buscar(id); }
}
