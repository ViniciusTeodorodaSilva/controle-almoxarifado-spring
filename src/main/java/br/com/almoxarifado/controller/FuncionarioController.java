package br.com.almoxarifado.controller;

import br.com.almoxarifado.model.Funcionario;
import br.com.almoxarifado.service.FuncionarioService;
import org.springframework.web.bind.annotation.*;
import br.com.almoxarifado.exception.RecursoNaoEncontradoException;

import java.util.List;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioService service;

    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<Funcionario> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Funcionario buscarPorId(@PathVariable Integer id) {
        return service.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado"));
    }

    @PostMapping
    public Funcionario cadastrar(@RequestBody Funcionario funcionario) {
        return service.cadastrar(funcionario);
    }

    @PutMapping("/{id}")
    public Funcionario atualizar(@PathVariable Integer id, @RequestBody Funcionario dados) {
        return service.atualizar(id, dados);
    }
}
