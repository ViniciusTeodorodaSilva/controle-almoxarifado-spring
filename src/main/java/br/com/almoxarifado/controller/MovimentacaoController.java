package br.com.almoxarifado.controller;

import br.com.almoxarifado.model.Movimentacao;
import br.com.almoxarifado.model.TipoMovimentacao;
import br.com.almoxarifado.service.MovimentacaoService;
import org.springframework.web.bind.annotation.*;
import br.com.almoxarifado.exception.RecursoNaoEncontradoException;

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
                .orElseThrow(() -> new RecursoNaoEncontradoException("Movimentação não encontrada"));
    }
    @GetMapping("/produto/{produtoId}")
    public List<Movimentacao> consultarPorProduto(@PathVariable Integer produtoId) {
        return service.consultarPorProduto(produtoId);
    }
    @GetMapping("/almoxarifado/{almoxarifadoId}")
    public List<Movimentacao> consultarPorAlmoxarifado(@PathVariable Integer almoxarifadoId) {
        return service.consultarPorAlmoxarifado(almoxarifadoId);
    }
    @GetMapping("/solicitacao/{solicitacaoId}")
    public List<Movimentacao> consultarPorSolicitacao(@PathVariable Integer solicitacaoId) {
        return service.consultarPorSolicitacao(solicitacaoId);
    }
    @GetMapping("/tipo/{tipo}")
    public List<Movimentacao> consultarPorTipo(@PathVariable TipoMovimentacao tipo) {
        return service.consultarPorTipo(tipo);
    }
}
