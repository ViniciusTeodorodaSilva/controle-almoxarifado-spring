package br.com.almoxarifado.controller;

import br.com.almoxarifado.model.ItemSolicitacao;
import br.com.almoxarifado.model.Solicitacao;
import br.com.almoxarifado.service.SolicitacaoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitacoes")
public class SolicitacaoController {

    private final SolicitacaoService service;

    public SolicitacaoController(SolicitacaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Solicitacao> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Solicitacao buscarPorId(@PathVariable Integer id) {
        return service.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Solicitacao cadastrar(
            @RequestParam Integer solicitanteId,
            @RequestParam Integer almoxarifadoId) {

        return service.cadastrar(
                solicitanteId,
                almoxarifadoId
        );
    }

    @PostMapping("/{solicitacaoId}/itens")
    public ItemSolicitacao adicionarItem(
            @PathVariable Integer solicitacaoId,
            @RequestParam Integer produtoId,
            @RequestParam double quantidade) {

        return service.adicionarItem(
                solicitacaoId,
                produtoId,
                quantidade
        );
    }
}
