package br.com.almoxarifado.obras;

import jakarta.persistence.EntityManager;
import java.util.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ResumoEstruturaService {
  private final EntityManager em;
  private final EstruturaService estrutura;

  public ResumoEstruturaService(EntityManager em, EstruturaService e) {
    this.em = em;
    estrutura = e;
  }

  @PreAuthorize("@autorizacao.permite('OBRA_LER')")
  public Map<String, Object> obra(Integer id) {
    return resumo(estrutura.obra(id), "obraId", id);
  }

  @PreAuthorize("@autorizacao.permite('ORDEM_SERVICO_LER')")
  public Map<String, Object> ordem(Integer id) {
    return resumo(estrutura.ordem(id), "ordemServicoId", id);
  }

  @PreAuthorize("@autorizacao.permite('CENTRO_CUSTO_LER')")
  public Map<String, Object> centro(Integer id) {
    return resumo(estrutura.centro(id), "centroCustoId", id);
  }

  private List<?> rows(String query, Integer id, int limit) {
    return em.createQuery(query).setParameter("id", id).setMaxResults(limit).getResultList();
  }

  private long count(String query, Integer id) {
    return em.createQuery(query, Long.class).setParameter("id", id).getSingleResult();
  }

  private Map<String, Object> resumo(Object cadastro, String dimension, Integer id) {
    String ctx = "contexto." + dimension;
    var out = new LinkedHashMap<String, Object>();
    out.put("cadastro", cadastro);
    if (cadastro instanceof OrdemServico os) {
      out.put("obra", rows("select o.codigo,o.nome from Obra o where o.id=:id", os.getObraId(), 1));
      if (os.getCentroCustoId() != null)
        out.put(
            "centroCusto",
            rows(
                "select c.codigo,c.nome from CentroCusto c where c.id=:id",
                os.getCentroCustoId(),
                1));
      if (os.getResponsavelId() != null)
        out.put(
            "responsavel",
            rows("select f.nome from Funcionario f where f.id=:id", os.getResponsavelId(), 1));
    }

    String requests = " from Solicitacao s where s." + ctx + "=:id";
    out.put("quantidadeSolicitacoes", count("select count(s)" + requests, id));
    out.put(
        "solicitacoes",
        rows("select s.id,s.status,s.dataSolicitacao" + requests + " order by s.id desc", id, 100));
    out.put(
        "quantidadeNecessidades",
        count("select count(n) from NecessidadeCompra n where n.solicitacao." + ctx + "=:id", id));
    out.put(
        "necessidades",
        rows(
            "select n.id,n.solicitacao.id,n.status,n.quantidade,n.quantidadeRecebida from"
                + " NecessidadeCompra n where n.solicitacao."
                + ctx
                + "=:id order by n.id desc",
            id,
            100));
    String orderPredicate =
        "i.ativo=true and (i."
            + ctx
            + "=:id or exists (select a.id from AlocacaoCompra a where a.item=i and"
            + " a.necessidade.solicitacao."
            + ctx
            + "=:id))";
    out.put(
        "quantidadePedidos",
        count(
            "select count(distinct p.id) from PedidoCompra p join p.itens i where "
                + orderPredicate,
            id));
    out.put(
        "pedidos",
        rows(
            "select distinct p.id,p.numero,p.status from PedidoCompra p join p.itens i where "
                + orderPredicate
                + " order by p.id desc",
            id,
            100));
    String osPredicate =
        dimension.equals("ordemServicoId") ? "o.id=:id" : "o." + dimension + "=:id";
    out.put(
        "quantidadeOrdensServico",
        count("select count(o) from OrdemServico o where " + osPredicate, id));
    out.put(
        "ordensServicoAbertas",
        count(
            "select count(o) from OrdemServico o where "
                + osPredicate
                + " and o.status not in"
                + " (br.com.almoxarifado.obras.StatusOrdemServico.CONCLUIDA,br.com.almoxarifado.obras.StatusOrdemServico.CANCELADA)",
            id));
    out.put(
        "ordensServico",
        rows(
            "select o.id,o.numero,o.titulo,o.status from OrdemServico o where "
                + osPredicate
                + " order by o.id desc",
            id,
            100));
    out.put(
        "materiaisSolicitados",
        rows(
            "select"
                + " i.produto.id,i.produto.nome,i.produto.unidadeMedida,sum(i.quantidade),sum(i.quantidadeAtendida)"
                + " from ItemSolicitacao i where i.solicitacao."
                + ctx
                + "=:id group by i.produto.id,i.produto.nome,i.produto.unidadeMedida order by"
                + " i.produto.id",
            id,
            100));
    // Only immutable withdrawal snapshots, never purchased/received quantities, represent
    // consumption.
    out.put(
        "materiaisConsumidos",
        rows(
            "select m.produto.id,m.produto.nome,m.produto.unidadeMedida,sum(m.quantidade) from"
                + " Movimentacao m where m."
                + ctx
                + "=:id and m.tipo=br.com.almoxarifado.model.TipoMovimentacao.SAIDA and"
                + " m.transferencia is null group by"
                + " m.produto.id,m.produto.nome,m.produto.unidadeMedida order by m.produto.id",
            id,
            100));
    out.put("limiteRelacoes", 100);
    out.put("custoConsumido", null);
    out.put(
        "avisoCustos",
        "Valores comprados e recebidos não são custo consumido. Valorização do estoque ainda não"
            + " definida.");
    return out;
  }
}
