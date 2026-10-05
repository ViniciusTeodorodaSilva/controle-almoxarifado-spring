package br.com.almoxarifado.compras;

import br.com.almoxarifado.exception.*;
import br.com.almoxarifado.security.*;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FornecedorService {

  private final FornecedorRepository repo;
  private final AuditoriaService audit;
  private final Autorizacao auth;

  public FornecedorService(FornecedorRepository r, AuditoriaService a, Autorizacao au) {
    repo = r;
    audit = a;
    auth = au;
  }

  static String texto(String s, int max, boolean obrigatorio) {
    s = s == null ? null : s.strip();
    if (s != null && s.isEmpty()) s = null;
    if (obrigatorio && s == null
        || s != null
            && (s.length() > max
                || s.chars()
                    .anyMatch(
                        c -> Character.isISOControl(c) && c != '\n' && c != '\r' && c != '\t')))
      throw new IllegalArgumentException(
          "Texto obrigatório, inválido ou acima do limite de " + max + " caracteres");
    return s;
  }

  static PageRequest pagina(int p, int t) {
    if (p < 0 || t < 1 || t > 100) throw new IllegalArgumentException("Paginação inválida");
    return PageRequest.of(p, t, Sort.by(Sort.Direction.DESC, "id"));
  }

  @PreAuthorize("@autorizacao.permite('FORNECEDOR_LER')")
  @Transactional(readOnly = true)
  public Page<Map<String, Object>> listar(
      String termo, String documento, Boolean ativo, int pagina, int tamanho) {
    Specification<Fornecedor> spec = (r, q, c) -> c.conjunction();
    if (termo != null && !termo.isBlank()) {
      String term = texto(termo, 160, false).toLowerCase(Locale.ROOT);
      spec =
          spec.and(
              (r, q, c) ->
                  c.or(
                      c.like(c.lower(r.get("nome")), "%" + term + "%"),
                      c.like(c.lower(r.get("nomeFantasia")), "%" + term + "%")));
    }
    if (documento != null && !documento.isBlank()) {
      String d = texto(documento, 30, false).toUpperCase(Locale.ROOT).replaceAll("[./ -]", "");
      spec = spec.and((r, q, c) -> c.equal(r.get("documento"), d));
    }
    if (ativo != null) spec = spec.and((r, q, c) -> c.equal(r.get("ativo"), ativo));
    return repo.findAll(spec, pagina(pagina, tamanho)).map(f -> ComprasViews.fornecedor(f, false));
  }

  @PreAuthorize("@autorizacao.permite('FORNECEDOR_LER')")
  @Transactional(readOnly = true)
  public Map<String, Object> buscar(Integer id) {
    return ComprasViews.fornecedor(
        repo.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado")),
        auth.permite("FORNECEDOR_GERENCIAR"));
  }

  @PreAuthorize("@autorizacao.permite('FORNECEDOR_GERENCIAR')")
  public Map<String, Object> criar(ComprasInput.Fornecedor in) {
    return salvar(new Fornecedor(), in, true);
  }

  @PreAuthorize("@autorizacao.permite('FORNECEDOR_GERENCIAR')")
  public Map<String, Object> atualizar(Integer id, ComprasInput.Fornecedor in) {
    return salvar(
        repo.bloquear(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado")),
        in,
        false);
  }

  private Map<String, Object> salvar(Fornecedor f, ComprasInput.Fornecedor in, boolean novo) {
    if (in == null || in.getTipoPessoa() == null)
      throw new IllegalArgumentException("Tipo de pessoa obrigatório");
    String doc = DocumentoFornecedor.normalizar(in.getTipoPessoa(), in.getDocumento());
    if (doc != null && repo.existsByDocumentoAndIdNot(doc, novo ? -1 : f.getId()))
      throw new ConflitoException("Documento já cadastrado");
    f.setNome(texto(in.getNome(), 160, true));
    f.setNomeFantasia(texto(in.getNomeFantasia(), 160, false));
    f.setTipoPessoa(in.getTipoPessoa());
    f.setDocumento(doc);
    f.setEmail(texto(in.getEmail(), 160, false));
    if (f.getEmail() != null && !f.getEmail().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
      throw new IllegalArgumentException("Email inválido");
    f.setTelefone(texto(in.getTelefone(), 40, false));
    f.setContato(texto(in.getContato(), 120, false));
    f.setObservacao(texto(in.getObservacao(), 1000, false));
    f.setAtivo(in.getAtivo() == null ? f.getAtivo() : in.getAtivo());
    if (novo) f.setCriadoEm(LocalDateTime.now());
    f.setAtualizadoEm(LocalDateTime.now());
    repo.saveAndFlush(f);
    audit.registrar(
        novo ? "FORNECEDOR_CRIADO" : "FORNECEDOR_ATUALIZADO",
        "FORNECEDOR",
        f.getId().toString(),
        null,
        null,
        "ativo=" + f.getAtivo());
    return ComprasViews.fornecedor(f, true);
  }
}
