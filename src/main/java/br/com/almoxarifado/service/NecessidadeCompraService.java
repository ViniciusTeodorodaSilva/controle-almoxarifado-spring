package br.com.almoxarifado.service;
import br.com.almoxarifado.dto.*;
import br.com.almoxarifado.exception.*;
import br.com.almoxarifado.model.*;
import br.com.almoxarifado.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import br.com.almoxarifado.security.Auditar;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
@org.springframework.transaction.annotation.Transactional
public class NecessidadeCompraService {
    private final NecessidadeCompraRepository necessidades;
    private final ItemSolicitacaoRepository itens;
    private final AtendimentoSolicitacaoService operacoes;
    private final ProdutoRepository produtos;
    private final AlmoxarifadoRepository locais;
    private final SolicitacaoRepository solicitacoes;
    private final EstoqueRepository estoques;
    private final br.com.almoxarifado.compras.AlocacaoCompraRepository alocacoes;
    public NecessidadeCompraService(NecessidadeCompraRepository necessidades,ItemSolicitacaoRepository itens,
        AtendimentoSolicitacaoService operacoes,ProdutoRepository produtos,AlmoxarifadoRepository locais,SolicitacaoRepository solicitacoes,EstoqueRepository estoques,br.com.almoxarifado.compras.AlocacaoCompraRepository alocacoes) {
        this.alocacoes=alocacoes;
        this.necessidades=necessidades;this.itens=itens;this.operacoes=operacoes;this.produtos=produtos;this.locais=locais;this.solicitacoes=solicitacoes;this.estoques=estoques;
    }
    @Transactional
    @PreAuthorize("@autorizacao.permite('NECESSIDADE_COMPRA_CRIAR')")
    @Auditar("NECESSIDADECOMPRA_CRIAR")
    public NecessidadeCompraResponse criar(NecessidadeCompraInput input,String chave) {
        AtendimentoSolicitacaoService.validarChave(chave);
        if(input==null||input.itemSolicitacaoId()==null||input.itemSolicitacaoId()<=0) throw new IllegalArgumentException("Item deve ser informado");
        var responsavel=operacoes.pessoa(input.responsavelId());
        // Only a scalar ID is read before the parent lock: never cache stale item quantities.
        var solicitacaoId=itens.encontrarSolicitacaoId(input.itemSolicitacaoId()).orElseThrow(()->new RecursoNaoEncontradoException("Item não encontrado"));
        var s=operacoes.bloquear(solicitacaoId);
        var item=itens.findById(input.itemSolicitacaoId()).orElseThrow(()->new RecursoNaoEncontradoException("Item não encontrado"));
        if(item.getProduto()==null||s.getAlmoxarifado()==null) throw new ConflitoException("Item ou solicitação sem contexto válido");
        String fingerprint=AtendimentoSolicitacaoService.assinatura(item.getId()+":"+responsavel.getId());
        var retry=necessidades.findByChaveIdempotencia(chave);
        if(retry.isPresent()) {
            if(!retry.get().getAssinaturaPayload().equals(fingerprint)) throw new ConflitoException("Idempotency-Key já utilizada com outra necessidade");
            return resposta(retry.get());
        }
        // One persistent need per request item, including after replenishment; never create infinite retries.
        var existente=necessidades.findByItemSolicitacaoId(item.getId());
        if(existente.isPresent()) return resposta(existente.get());
        var produto=produtos.buscarParaAtualizacao(item.getProduto().getId()).orElseThrow(()->new RecursoNaoEncontradoException("Produto não encontrado"));
        if(!produto.isAtivo()) throw new IllegalArgumentException("Produto inativo");
        // Lock an existing stock row; missing stock is serialized with the product-creation protocol.
        estoques.buscarParaAtualizacao(produto.getId(),s.getAlmoxarifado().getId());
        var view=operacoes.snapshot(s); AtendimentoSolicitacaoService.exigirConsistencia(view);
        if(view.status()==StatusSolicitacao.PENDENTE||view.status()==StatusSolicitacao.REJEITADA||view.status()==StatusSolicitacao.ATENDIDA)
            throw new ConflitoException("Necessidade exige demanda aprovada e pendente");
        var detalhe=operacoes.resposta(view).itens().stream().filter(i->i.id().equals(item.getId())).findFirst().orElseThrow();
        double falta=detalhe.quantidadeFaltante();
        QuantidadesOperacionais.positiva(falta); ValidacaoQuantidade.validar(produto,falta);
        var n=new NecessidadeCompra();n.setItemSolicitacao(item);n.setSolicitacao(s);n.setProduto(produto);n.setAlmoxarifado(s.getAlmoxarifado());
        n.setQuantidade(falta);n.setQuantidadeRecebida(0);n.setResponsavel(responsavel);n.setDataHora(AtendimentoSolicitacaoService.agora());n.setStatus(StatusNecessidadeCompra.ABERTA);
        n.setMotivo("FALTA_DE_ESTOQUE");n.setChaveIdempotencia(chave);n.setAssinaturaPayload(fingerprint);
        return resposta(necessidades.saveAndFlush(n));
    }
    @Transactional(readOnly=true)
    @PreAuthorize("@autorizacao.permite('NECESSIDADE_COMPRA_LER')")
    public List<NecessidadeCompraResponse> listar(StatusNecessidadeCompra status,Integer produtoId,Integer almoxarifadoId,Integer solicitacaoId) {
        if(produtoId!=null&&!produtos.existsById(produtoId)) throw new RecursoNaoEncontradoException("Produto não encontrado");
        if(almoxarifadoId!=null&&!locais.existsById(almoxarifadoId)) throw new RecursoNaoEncontradoException("Almoxarifado não encontrado");
        if(solicitacaoId!=null&&!solicitacoes.existsById(solicitacaoId)) throw new RecursoNaoEncontradoException("Solicitação não encontrada");
        var lista=necessidades.filtrar(status,produtoId,almoxarifadoId,solicitacaoId);
        var porNecessidade=new java.util.HashMap<Integer,java.util.List<br.com.almoxarifado.compras.AlocacaoCompra>>();
        for(int start=0;start<lista.size();start+=100) {
            var ids=lista.subList(start,Math.min(start+100,lista.size())).stream().map(NecessidadeCompra::getId).toList();
            for(var a:alocacoes.ativasEm(ids)) porNecessidade.computeIfAbsent(a.getNecessidade().getId(),k->new java.util.ArrayList<>()).add(a);
        }
        return lista.stream().map(n->resposta(n,alocacoes.progresso(n,porNecessidade.getOrDefault(n.getId(),java.util.List.of())))).toList();
    }
    @Transactional(readOnly=true)
    @PreAuthorize("@autorizacao.permite('NECESSIDADE_COMPRA_LER')")
    public NecessidadeCompraResponse buscar(Integer id) { return resposta(necessidades.findById(id).orElseThrow(()->new RecursoNaoEncontradoException("Necessidade não encontrada"))); }
    private NecessidadeCompraResponse resposta(NecessidadeCompra n) { return resposta(n,alocacoes.progresso(n)); }
    private NecessidadeCompraResponse resposta(NecessidadeCompra n,java.util.Map<String,Object> progresso) {
        return new NecessidadeCompraResponse(n.getId(),n.getItemSolicitacao().getId(),n.getSolicitacao().getId(),AtendimentoSolicitacaoService.produto(n.getProduto()),
            AtendimentoSolicitacaoService.referencia(n.getAlmoxarifado()),n.getQuantidade(),n.getStatus().name(),n.getDataHora(),n.getMotivo(),AtendimentoSolicitacaoService.referencia(n.getResponsavel()),progresso);
    }
}
