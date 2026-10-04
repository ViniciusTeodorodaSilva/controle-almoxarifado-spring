package br.com.almoxarifado.controller;

import br.com.almoxarifado.model.Estoque;
import br.com.almoxarifado.service.EstoqueService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoques")
public class EstoqueController {

    private final EstoqueService service;

    public EstoqueController(EstoqueService service) {
        this.service = service;
    }

    @GetMapping
    public List<Estoque> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Estoque buscarPorId(@PathVariable Integer  id) {
        return service.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Estoque cadastrar(@RequestBody Estoque estoque) {
        return service.cadastrar(estoque);
    }

    @PutMapping("/entrada")
    public Estoque entradaEstoque(
            @RequestParam Integer produtoId,
            @RequestParam Integer almoxarifadoId,
            @RequestParam double quantidade,
            @RequestParam Integer solicitanteId,
            @RequestParam Integer responsavelId) {

        return service.entradaEstoque(
                produtoId,
                almoxarifadoId,
                quantidade,
                solicitanteId,
                responsavelId);
    }

    @PutMapping("/saida")
    public Estoque saidaEstoque(
            @RequestParam Integer produtoId,
            @RequestParam Integer almoxarifadoId,
            @RequestParam double quantidade,
            @RequestParam Integer solicitanteId,
            @RequestParam Integer responsavelId) {

        return service.saidaEstoque(
                produtoId,
                almoxarifadoId,
                quantidade,
                solicitanteId,
                responsavelId
        );
    }
}
