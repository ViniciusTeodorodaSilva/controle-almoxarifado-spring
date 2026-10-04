package br.com.almoxarifado.controller;
import br.com.almoxarifado.model.CategoriaMaterial;
import br.com.almoxarifado.service.CategoriaMaterialService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/categorias")
public class CategoriaMaterialController {
    private final CategoriaMaterialService service;
    public CategoriaMaterialController(CategoriaMaterialService service) { this.service = service; }
    @GetMapping public List<CategoriaMaterial> listar(@RequestParam(required = false) Boolean ativo) { return service.listar(ativo); }
    @GetMapping("/{id}") public CategoriaMaterial buscar(@PathVariable Integer id) { return service.buscar(id); }
    @PostMapping public CategoriaMaterial cadastrar(@RequestBody CategoriaMaterial dados) { return service.cadastrar(dados); }
    @PutMapping("/{id}") public CategoriaMaterial atualizar(@PathVariable Integer id, @RequestBody CategoriaMaterial dados) { return service.atualizar(id, dados); }
}
