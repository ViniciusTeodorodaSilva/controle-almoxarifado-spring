package br.com.almoxarifado.controller;

import br.com.almoxarifado.model.Movimentacao;
import br.com.almoxarifado.service.MovimentacaoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimentacoes")
public class MovimentacaoController {

    private final MovimentacaoService service;

    public MovimentacaoController(MovimentacaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Movimentacao> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Movimentacao buscarPorId(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .orElse(null);
    }
}
