package br.com.almoxarifado.controller;
import br.com.almoxarifado.model.UnidadeMedida;
import br.com.almoxarifado.service.UnidadeMedidaService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/unidades-medida")
public class UnidadeMedidaController {
    private final UnidadeMedidaService service;
    public UnidadeMedidaController(UnidadeMedidaService service) { this.service = service; }
    @GetMapping public List<UnidadeMedida> listar(@RequestParam(required = false) Boolean ativo) { return service.listar(ativo); }
    @GetMapping("/{id}") public UnidadeMedida buscar(@PathVariable Integer id) { return service.buscar(id); }
    @PostMapping public UnidadeMedida cadastrar(@RequestBody UnidadeMedida dados) { return service.cadastrar(dados); }
    @PutMapping("/{id}") public UnidadeMedida atualizar(@PathVariable Integer id, @RequestBody UnidadeMedida dados) { return service.atualizar(id, dados); }
}
