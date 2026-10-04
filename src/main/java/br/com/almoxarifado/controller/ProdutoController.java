package br.com.almoxarifado.controller;

import br.com.almoxarifado.model.Produto;
import br.com.almoxarifado.service.ProdutoService;
import org.springframework.web.bind.annotation.*;
import br.com.almoxarifado.exception.RecursoNaoEncontradoException;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Produto> listar(@RequestParam(required = false) Boolean ativo) {
        return service.listar(ativo);
    }

    @GetMapping("/{id}")
    public Produto buscarPorId(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));
    }

    @PostMapping
    public Produto cadastrar(@RequestBody Produto produto) {
        return service.cadastrar(produto);
    }
    @PutMapping("/{id}")
    public Produto atualizar(@PathVariable Integer id, @RequestBody Produto dados) {
        return service.atualizar(id, dados);
    }
    @GetMapping("/busca")
    public List<Produto> buscar(@RequestParam(required = false) String termo,
            @RequestParam(required = false) Integer categoriaId, @RequestParam(required = false) Boolean ativo) {
        return service.buscar(termo, categoriaId, ativo);
    }
    @GetMapping("/equivalentes")
    public List<Produto> equivalentes(@RequestParam String termo) { return service.equivalentes(termo); }
}
