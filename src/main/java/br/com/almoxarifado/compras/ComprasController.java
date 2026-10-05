package br.com.almoxarifado.compras;

import java.time.LocalDate;
import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class ComprasController {
  private final FornecedorService fornecedores;
  private final PedidoCompraService pedidos;

  public ComprasController(FornecedorService f, PedidoCompraService p) {
    fornecedores = f;
    pedidos = p;
  }

  @GetMapping("/fornecedores")
  public ComprasPage<Map<String, Object>> fornecedores(
      @RequestParam(required = false) String termo,
      @RequestParam(required = false) String documento,
      @RequestParam(required = false) Boolean ativo,
      @RequestParam(defaultValue = "0") int pagina,
      @RequestParam(defaultValue = "20") int tamanho) {
    return ComprasPage.of(fornecedores.listar(termo, documento, ativo, pagina, tamanho));
  }

  @GetMapping("/fornecedores/{id}")
  public Map<String, Object> fornecedor(@PathVariable Integer id) {
    return fornecedores.buscar(id);
  }

  @PostMapping("/fornecedores")
  public Map<String, Object> criarFornecedor(@RequestBody ComprasInput.Fornecedor in) {
    return fornecedores.criar(in);
  }

  @PutMapping("/fornecedores/{id}")
  public Map<String, Object> atualizarFornecedor(
      @PathVariable Integer id, @RequestBody ComprasInput.Fornecedor in) {
    return fornecedores.atualizar(id, in);
  }

  @GetMapping("/pedidos-compra")
  public ComprasPage<Map<String, Object>> pedidos(
      @RequestParam(required = false) String numero,
      @RequestParam(required = false) Integer fornecedorId,
      @RequestParam(required = false) StatusPedidoCompra status,
      @RequestParam(required = false) Integer produtoId,
      @RequestParam(required = false) Integer necessidadeId,
      @RequestParam(required = false) LocalDate de,
      @RequestParam(required = false) LocalDate ate,
      @RequestParam(defaultValue = "0") int pagina,
      @RequestParam(defaultValue = "20") int tamanho) {
    return ComprasPage.of(
        pedidos.listar(
            numero, fornecedorId, status, produtoId, necessidadeId, de, ate, pagina, tamanho));
  }

  @GetMapping("/pedidos-compra/{id}")
  public Map<String, Object> pedido(@PathVariable Integer id) {
    return pedidos.buscar(id);
  }

  @PostMapping("/pedidos-compra")
  public Map<String, Object> criarPedido(@RequestBody ComprasInput.Pedido in) {
    return pedidos.criar(in);
  }

  @PutMapping("/pedidos-compra/{id}")
  public Map<String, Object> atualizarPedido(
      @PathVariable Integer id, @RequestBody ComprasInput.Pedido in) {
    return pedidos.atualizar(id, in);
  }

  @PutMapping("/pedidos-compra/{id}/submeter")
  public Map<String, Object> submeter(@PathVariable Integer id) {
    return pedidos.submeter(id);
  }

  @PutMapping("/pedidos-compra/{id}/aprovar")
  public Map<String, Object> aprovar(@PathVariable Integer id) {
    return pedidos.aprovar(id);
  }

  @PutMapping("/pedidos-compra/{id}/cancelar")
  public Map<String, Object> cancelar(
      @PathVariable Integer id, @RequestBody ComprasInput.Cancelamento in) {
    return pedidos.cancelar(id, in);
  }

  @GetMapping("/pedidos-compra/{id}/recebimentos")
  public ComprasPage<Map<String, Object>> historico(
      @PathVariable Integer id,
      @RequestParam(defaultValue = "0") int pagina,
      @RequestParam(defaultValue = "20") int tamanho) {
    return ComprasPage.of(pedidos.historico(id, pagina, tamanho));
  }

  @PostMapping("/pedidos-compra/{id}/recebimentos")
  public Map<String, Object> receber(
      @PathVariable Integer id,
      @RequestBody ComprasInput.Recebimento in,
      @RequestHeader("Idempotency-Key") String chave) {
    return pedidos.receber(id, in, chave);
  }

  @GetMapping("/recebimentos-compra/{id}")
  public Map<String, Object> recebimento(@PathVariable Integer id) {
    return pedidos.buscarRecebimento(id);
  }

  @GetMapping("/recebimentos-compra")
  public ComprasPage<Map<String, Object>> recebimentos(
      @RequestParam(required = false) Integer pedidoId,
      @RequestParam(required = false) Integer fornecedorId,
      @RequestParam(required = false) Integer produtoId,
      @RequestParam(required = false) Integer almoxarifadoId,
      @RequestParam(required = false) LocalDate de,
      @RequestParam(required = false) LocalDate ate,
      @RequestParam(defaultValue = "0") int pagina,
      @RequestParam(defaultValue = "20") int tamanho) {
    return ComprasPage.of(
        pedidos.listarRecebimentos(
            pedidoId, fornecedorId, produtoId, almoxarifadoId, de, ate, pagina, tamanho));
  }

  @PutMapping("/necessidades-compra/{id}/cancelar")
  public Map<String, Object> cancelarNecessidade(
      @PathVariable Integer id, @RequestBody ComprasInput.Cancelamento in) {
    return pedidos.cancelarNecessidade(id, in);
  }
}
