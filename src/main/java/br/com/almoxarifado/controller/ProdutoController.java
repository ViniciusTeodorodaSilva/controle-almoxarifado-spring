package br.com.almoxarifado.controller;

import br.com.almoxarifado.model.Produto;
import br.com.almoxarifado.service.ProdutoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Produto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Produto buscarPorId(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .orElse(null);
    }

    @PostMapping
    public Produto cadastrar(@RequestBody Produto produto) {
        return service.cadastrar(produto);
    }
}