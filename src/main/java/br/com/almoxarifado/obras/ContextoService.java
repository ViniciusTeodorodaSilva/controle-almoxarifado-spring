package br.com.almoxarifado.obras;

import br.com.almoxarifado.exception.*;
import jakarta.persistence.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ContextoService {
  private final ObraRepository obras;
  private final OrdemServicoRepository ordens;
  private final CentroCustoRepository centros;
  private final EntityManager em;

  public ContextoService(
      ObraRepository o, OrdemServicoRepository s, CentroCustoRepository c, EntityManager em) {
    obras = o;
    ordens = s;
    centros = c;
    this.em = em;
  }

  @PreAuthorize("@autorizacao.autenticado()")
  public ContextoOperacional resolver(Integer obraId, Integer osId, Integer ccId) {
    if (obraId == null && osId == null && ccId == null) return null;
    OrdemServico os =
        osId == null
            ? null
            : ordens
                .findById(osId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada"));
    CentroCusto cc =
        ccId == null
            ? null
            : centros
                .findById(ccId)
                .orElseThrow(
                    () -> new RecursoNaoEncontradoException("Centro de custo não encontrado"));
    if (os != null) {
      if (obraId != null && !obraId.equals(os.getObraId()))
        throw new ConflitoException("OS pertence a outra obra");
      obraId = os.getObraId();
      if (ccId != null && os.getCentroCustoId() != null && !ccId.equals(os.getCentroCustoId()))
        throw new ConflitoException("Centro de custo difere do definido na OS");
      if (ccId == null && os.getCentroCustoId() != null) {
        ccId = os.getCentroCustoId();
        cc = centros.findById(ccId).orElseThrow();
      }
    }
    if (cc != null && cc.getObraId() != null) {
      if (obraId != null && !obraId.equals(cc.getObraId()))
        throw new ConflitoException("Centro de custo pertence a outra obra");
      obraId = cc.getObraId();
    }
    Obra obra =
        obraId == null
            ? null
            : obras
                .findById(obraId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Obra não encontrada"));
    // Every structure writer uses the same order: obra -> centro -> OS. No stock lock here.
    if (obra != null) {
      em.refresh(obra, LockModeType.PESSIMISTIC_WRITE);
      exigirObra(obra);
    }
    if (cc != null) {
      em.refresh(cc, LockModeType.PESSIMISTIC_WRITE);
      if (!cc.getAtivo()) throw new ConflitoException("Centro de custo inativo");
    }
    if (os != null) {
      em.refresh(os, LockModeType.PESSIMISTIC_WRITE);
      if (os.getStatus() != StatusOrdemServico.ABERTA
          && os.getStatus() != StatusOrdemServico.EM_ANDAMENTO)
        throw new ConflitoException("OS não aceita nova demanda neste status");
    }
    var c = new ContextoOperacional();
    if (obra != null) {
      c.setObraId(obra.getId());
      c.setObraCodigo(obra.getCodigo());
      c.setObraNome(obra.getNome());
    }
    if (os != null) {
      c.setOrdemServicoId(os.getId());
      c.setOrdemServicoNumero(os.getNumero());
    }
    if (cc != null) {
      c.setCentroCustoId(cc.getId());
      c.setCentroCustoCodigo(cc.getCodigo());
      c.setCentroCustoNome(cc.getNome());
    }
    return c;
  }

  @PreAuthorize("@autorizacao.autenticado()")
  public java.util.Map<ObrasInput.Contexto, ContextoOperacional> resolverTodos(
      java.util.List<ObrasInput.Contexto> entradas) {
    var obraIds = new java.util.TreeSet<Integer>();
    var centroIds = new java.util.TreeSet<Integer>();
    var ordemIds = new java.util.TreeSet<Integer>();
    for (var entrada : entradas) {
      if (entrada.obraId != null) obraIds.add(entrada.obraId);
      if (entrada.centroCustoId != null) centroIds.add(entrada.centroCustoId);
      if (entrada.ordemServicoId != null) {
        var os =
            ordens
                .findById(entrada.ordemServicoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("OS não encontrada"));
        ordemIds.add(os.getId());
        obraIds.add(os.getObraId());
        if (os.getCentroCustoId() != null) centroIds.add(os.getCentroCustoId());
      }
    }
    for (var id : centroIds) {
      var cc =
          centros
              .findById(id)
              .orElseThrow(
                  () -> new RecursoNaoEncontradoException("Centro de custo não encontrado"));
      if (cc.getObraId() != null) obraIds.add(cc.getObraId());
    }
    // Lock the complete batch by dimension and ID before resolving individual snapshots.
    for (var id : obraIds)
      em.refresh(
          obras
              .findById(id)
              .orElseThrow(() -> new RecursoNaoEncontradoException("Obra não encontrada")),
          LockModeType.PESSIMISTIC_WRITE);
    for (var id : centroIds)
      em.refresh(centros.findById(id).orElseThrow(), LockModeType.PESSIMISTIC_WRITE);
    for (var id : ordemIds)
      em.refresh(ordens.findById(id).orElseThrow(), LockModeType.PESSIMISTIC_WRITE);
    var resultado = new java.util.IdentityHashMap<ObrasInput.Contexto, ContextoOperacional>();
    for (var entrada : entradas)
      resultado.put(
          entrada, resolver(entrada.obraId, entrada.ordemServicoId, entrada.centroCustoId));
    return resultado;
  }

  static void exigirObra(Obra o) {
    if (o.getStatus() != StatusObra.PLANEJADA && o.getStatus() != StatusObra.ATIVA)
      throw new ConflitoException("Obra não aceita nova demanda neste status");
  }
}
