package br.com.almoxarifado.controller;

import br.com.almoxarifado.model.ItemSolicitacao;
import br.com.almoxarifado.model.Movimentacao;
import br.com.almoxarifado.model.StatusSolicitacao;
import br.com.almoxarifado.model.Solicitacao;
import br.com.almoxarifado.service.SolicitacaoService;
import org.springframework.web.bind.annotation.*;
import br.com.almoxarifado.exception.RecursoNaoEncontradoException;

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
        return service.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada"));
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

    @PutMapping("/{id}/aprovar")
    public Solicitacao aprovar(@PathVariable Integer id, @RequestParam Integer responsavelId) {
        return service.aprovar(id, responsavelId);
    }

    @PutMapping("/{id}/rejeitar")
    public Solicitacao rejeitar(@PathVariable Integer id) {
        return service.rejeitar(id);
    }
    @GetMapping("/{id}/movimentacoes")
    public List<Movimentacao> consultarMovimentacoes(@PathVariable Integer id) {
        return service.consultarMovimentacoes(id);
    }

    @GetMapping("/status/{status}")
    public List<Solicitacao> consultarPorStatus(@PathVariable StatusSolicitacao status) {
        return service.consultarPorStatus(status);
    }

    @GetMapping("/funcionario/{funcionarioId}")
    public List<Solicitacao> consultarPorFuncionario(@PathVariable Integer funcionarioId) {
        return service.consultarPorFuncionario(funcionarioId);
    }
}
