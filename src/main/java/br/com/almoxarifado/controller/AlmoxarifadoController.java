package br.com.almoxarifado.controller;

import br.com.almoxarifado.model.Almoxarifado;
import br.com.almoxarifado.service.AlmoxarifadoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/almoxarifados")
public class AlmoxarifadoController {

    private final AlmoxarifadoService service;

    public AlmoxarifadoController(AlmoxarifadoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Almoxarifado> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Almoxarifado buscarPorId(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .orElse(null);
    }

    @PostMapping
    public Almoxarifado cadastrar(@RequestBody Almoxarifado almoxarifado) {
        return service.cadastrar(almoxarifado);
    }
}
