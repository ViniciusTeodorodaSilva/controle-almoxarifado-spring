package br.com.almoxarifado.epi;
import br.com.almoxarifado.compras.ComprasPage;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.time.LocalDate;
@RestController
public class EpiController {
 private final EpiService service;
 public EpiController(EpiService s){service=s;}
 @GetMapping("/epis") public ComprasPage<Map<String,Object>> listar(@RequestParam(required=false) String termo,@RequestParam(required=false) String ca,@RequestParam(required=false) Boolean ativo,@RequestParam(required=false) Integer almoxarifadoId,@RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="20") int tamanho){return ComprasPage.of(service.listarConfigs(termo,ca,ativo,almoxarifadoId,pagina,tamanho));}
 @GetMapping("/epis/{id}") public Map<String,Object> buscar(@PathVariable Integer id){return service.buscarConfig(id);}
 @PostMapping("/epis") public Map<String,Object> criar(@RequestBody EpiInput.Configuracao in){return service.configurar(null,in);}
 @PutMapping("/epis/{id}") public Map<String,Object> editar(@PathVariable Integer id,@RequestBody EpiInput.Configuracao in){return service.configurar(id,in);}
 @PostMapping("/epi-entregas") public Map<String,Object> entregar(@RequestBody EpiInput.Entrega in,@RequestHeader("Idempotency-Key") String chave){return service.entregar(in,chave);}
 @PostMapping("/epi-devolucoes") public Map<String,Object> devolver(@RequestBody EpiInput.Fechamento in,@RequestHeader("Idempotency-Key") String chave){return service.fechar(in,chave,false);}
 @PostMapping("/epi-descartes") public Map<String,Object> descartar(@RequestBody EpiInput.Fechamento in,@RequestHeader("Idempotency-Key") String chave){return service.fechar(in,chave,true);}
 @GetMapping("/epi-entregas/{id}") public Map<String,Object> detalhe(@PathVariable Integer id){return service.buscarRegistro(id);}
 @GetMapping("/epi-entregas") public ComprasPage<Map<String,Object>> entregas(@RequestParam(required=false) Integer funcionarioId,@RequestParam(required=false) Integer produtoId,@RequestParam(required=false) Integer obraId,@RequestParam(required=false) Integer ordemServicoId,@RequestParam(required=false) Integer centroCustoId,@RequestParam(required=false) Integer almoxarifadoId,@RequestParam(required=false) String ca,@RequestParam(required=false) TipoRegistroEpi tipo,@RequestParam(required=false) LocalDate de,@RequestParam(required=false) LocalDate ate,@RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="20") int tamanho){return ComprasPage.of(service.listarRegistros(new EpiService.Filtro(funcionarioId,produtoId,obraId,ordemServicoId,centroCustoId,almoxarifadoId,ca,tipo,de,ate),pagina,tamanho));}
 @GetMapping("/epi-entregas/posse") public ComprasPage<Map<String,Object>> posse(@RequestParam(required=false) Integer funcionarioId,@RequestParam(required=false) String alerta,@RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="20") int tamanho){return ComprasPage.of(service.posse(funcionarioId,alerta,pagina,tamanho));}
 @GetMapping("/epi-entregas/resumo") public Map<String,Object> resumo(){return service.resumo();}
 @GetMapping("/epi-funcionarios/{id}") public Map<String,Object> ficha(@PathVariable Integer id,@RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="20") int tamanho){return service.ficha(id,pagina,tamanho);}
}
