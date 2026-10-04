package br.com.almoxarifado.controller;
import br.com.almoxarifado.dto.*;
import br.com.almoxarifado.service.AtendimentoSolicitacaoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/solicitacoes")
public class AtendimentoSolicitacaoController {
    private final AtendimentoSolicitacaoService service;
    public AtendimentoSolicitacaoController(AtendimentoSolicitacaoService service) { this.service=service; }
    @GetMapping("/{id}/operacao") public OperacaoSolicitacaoResponse operacao(@PathVariable Integer id) { return service.operacao(id); }
    @GetMapping("/{id}/faltas") public List<OperacaoSolicitacaoResponse.Item> faltas(@PathVariable Integer id) { return service.faltas(id); }
    @PutMapping("/{id}/iniciar-separacao") public OperacaoSolicitacaoResponse separar(@PathVariable Integer id,@RequestParam Integer responsavelId) { return service.iniciarSeparacao(id,responsavelId); }
    @PostMapping("/{id}/atendimentos") public AtendimentoResponse atender(@PathVariable Integer id,@Valid @RequestBody AtendimentoInput input,@RequestHeader("Idempotency-Key") String chave) { return service.atender(id,input,chave); }
    @GetMapping("/{id}/atendimentos") public List<AtendimentoResponse> historico(@PathVariable Integer id) { return service.historico(id); }
}
