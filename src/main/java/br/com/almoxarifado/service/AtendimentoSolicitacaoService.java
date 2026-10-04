package br.com.almoxarifado.service;

import br.com.almoxarifado.dto.*;
import br.com.almoxarifado.exception.*;
import br.com.almoxarifado.model.*;
import br.com.almoxarifado.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class AtendimentoSolicitacaoService {
    private final SolicitacaoRepository solicitacoes;
    private final ItemSolicitacaoRepository itens;
    private final EstoqueRepository estoques;
    private final ProdutoRepository produtos;
    private final FuncionarioRepository pessoas;
    private final MovimentacaoRepository movimentos;
    private final AtendimentoSolicitacaoRepository atendimentos;
    private final ItemAtendimentoSolicitacaoRepository itensAtendimento;
    private final NecessidadeCompraRepository necessidades;
    public AtendimentoSolicitacaoService(SolicitacaoRepository solicitacoes, ItemSolicitacaoRepository itens, EstoqueRepository estoques,
        ProdutoRepository produtos, FuncionarioRepository pessoas, MovimentacaoRepository movimentos,
        AtendimentoSolicitacaoRepository atendimentos, ItemAtendimentoSolicitacaoRepository itensAtendimento, NecessidadeCompraRepository necessidades) {
        this.solicitacoes=solicitacoes; this.itens=itens; this.estoques=estoques; this.produtos=produtos; this.pessoas=pessoas;
        this.movimentos=movimentos; this.atendimentos=atendimentos; this.itensAtendimento=itensAtendimento; this.necessidades=necessidades;
    }
    Solicitacao bloquear(Integer id) { return solicitacoes.buscarParaAtualizacao(id).orElseThrow(()->new RecursoNaoEncontradoException("Solicitação não encontrada")); }
    Funcionario pessoa(Integer id) {
        if(id==null||id<=0) throw new IllegalArgumentException("Responsável deve ser informado");
        return pessoas.findById(id).orElseThrow(()->new RecursoNaoEncontradoException("Responsável não encontrado"));
    }
    static LocalDateTime agora() { return LocalDateTime.now().truncatedTo(ChronoUnit.MICROS); }
    static void validarChave(String chave) {
        if(chave==null||!chave.matches("[A-Za-z0-9._:-]{8,100}")) throw new IllegalArgumentException("Idempotency-Key deve conter de 8 a 100 caracteres ASCII válidos");
    }
    static String assinatura(String texto) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(texto.getBytes(StandardCharsets.UTF_8))); }
        catch(java.security.NoSuchAlgorithmException e) { throw new IllegalStateException("SHA-256 indisponível",e); }
    }
    @Transactional(readOnly=true)
    public OperacaoSolicitacaoResponse operacao(Integer id) {
        var s=solicitacoes.findById(id).orElseThrow(()->new RecursoNaoEncontradoException("Solicitação não encontrada"));
        return resposta(snapshot(s));
    }
    @Transactional(readOnly=true)
    public List<OperacaoSolicitacaoResponse.Item> faltas(Integer id) {
        return operacao(id).itens().stream().filter(i->i.quantidadePendente()!=null&&i.quantidadePendente()>0).toList();
    }
    @Transactional
    public OperacaoSolicitacaoResponse iniciarSeparacao(Integer id,Integer responsavelId) {
        var s=bloquear(id); var view=snapshot(s); exigirConsistencia(view);
        if(view.status()!=StatusSolicitacao.APROVADA) throw new ConflitoException("Somente solicitação APROVADA pode iniciar separação");
        s.setResponsavelSeparacao(pessoa(responsavelId)); s.setDataSeparacao(agora()); s.setStatus(StatusSolicitacao.EM_SEPARACAO);
        solicitacoes.save(s); return resposta(snapshot(s));
    }
    @Transactional
    public AtendimentoResponse atender(Integer id,AtendimentoInput input,String chave) {
        validarChave(chave);
        if(input==null||input.itens()==null||input.itens().isEmpty()||input.itens().size()>500) throw new IllegalArgumentException("Informe de 1 a 500 itens");
        Map<Integer,Double> pedidos=new TreeMap<>();
        for(var item:input.itens()) {
            if(item==null||item.itemSolicitacaoId()==null||item.itemSolicitacaoId()<=0||item.quantidade()==null) throw new IllegalArgumentException("Item inválido");
            QuantidadesOperacionais.positiva(item.quantidade());
            if(pedidos.putIfAbsent(item.itemSolicitacaoId(),item.quantidade())!=null) throw new IllegalArgumentException("Item duplicado no atendimento");
        }
        var responsavel=pessoa(input.responsavelId());
        String fingerprint=assinatura(id+":"+responsavel.getId()+":"+pedidos);
        var s=bloquear(id);
        var anterior=atendimentos.findByChaveIdempotencia(chave);
        if(anterior.isPresent()) {
            var a=anterior.get();
            if(!a.getAssinaturaPayload().equals(fingerprint)) throw new ConflitoException("Idempotency-Key já utilizada com outra operação");
            return respostaAtendimento(a);
        }
        var view=snapshot(s); exigirConsistencia(view);
        if(view.status()!=StatusSolicitacao.EM_SEPARACAO&&view.status()!=StatusSolicitacao.PARCIALMENTE_ATENDIDA)
            throw new ConflitoException("Atendimento exige solicitação EM_SEPARACAO ou PARCIALMENTE_ATENDIDA");
        Map<Integer,ItemSolicitacao> selecionados=new TreeMap<>(); Map<Integer,Double> totalProdutos=new TreeMap<>();
        for(var entrada:pedidos.entrySet()) {
            var item=view.itens().stream().filter(i->i.getId().equals(entrada.getKey())).findFirst()
                .orElseThrow(()->new IllegalArgumentException("Item não pertence à solicitação"));
            double pendente=QuantidadesOperacionais.subtrair(item.getQuantidade(),view.atendidas().get(item.getId()));
            if(entrada.getValue()>pendente) throw new IllegalArgumentException("Quantidade supera o pendente do item");
            selecionados.put(item.getId(),item);
            double total=QuantidadesOperacionais.somar(totalProdutos.getOrDefault(item.getProduto().getId(),0.0),entrada.getValue());
            if(!Double.isFinite(total)) throw new IllegalArgumentException("Quantidade total inválida");
            totalProdutos.put(item.getProduto().getId(),total);
        }
        // Mesmo protocolo das transferências: produtos crescentes antes dos pares de estoque.
        for(var produtoId:totalProdutos.keySet()) {
            var p=produtos.buscarParaAtualizacao(produtoId).orElseThrow(()->new RecursoNaoEncontradoException("Produto não encontrado"));
            if(!p.isAtivo()) throw new IllegalArgumentException("Produto inativo não pode ser atendido");
            for(var item:selecionados.values()) if(item.getProduto().getId().equals(produtoId)) ValidacaoQuantidade.validar(p,pedidos.get(item.getId()));
        }
        Map<Integer,Estoque> saldos=new TreeMap<>();
        for(var entrada:totalProdutos.entrySet()) {
            var estoque=estoques.buscarParaAtualizacao(entrada.getKey(),s.getAlmoxarifado().getId())
                .orElseThrow(()->new RecursoNaoEncontradoException("Estoque não cadastrado no almoxarifado"));
            if(!Double.isFinite(estoque.getQuantidade())||estoque.getQuantidade()<0||entrada.getValue()>estoque.getQuantidade())
                throw new IllegalArgumentException("Estoque insuficiente ou inválido");
            double posterior=QuantidadesOperacionais.subtrair(estoque.getQuantidade(),entrada.getValue());
            if(posterior<0||posterior==estoque.getQuantidade()) throw new IllegalArgumentException("Quantidade excede a precisão do saldo");
            saldos.put(entrada.getKey(),estoque);
        }
        var atendimento=new AtendimentoSolicitacao(); atendimento.setSolicitacao(s); atendimento.setResponsavel(responsavel);
        atendimento.setDataHora(agora()); atendimento.setChaveIdempotencia(chave); atendimento.setAssinaturaPayload(fingerprint);
        atendimento=atendimentos.saveAndFlush(atendimento);
        for(var item:selecionados.values()) {
            double qtd=pedidos.get(item.getId()); double atendida=QuantidadesOperacionais.somar(view.atendidas().get(item.getId()),qtd);
            if(!Double.isFinite(atendida)||atendida>item.getQuantidade()||atendida==view.atendidas().get(item.getId())) throw new IllegalArgumentException("Quantidade atendida inválida ou sem precisão suficiente");
            var detalhe=new ItemAtendimentoSolicitacao(); detalhe.setAtendimento(atendimento); detalhe.setItemSolicitacao(item); detalhe.setQuantidade(qtd); itensAtendimento.save(detalhe);
            var estoque=saldos.get(item.getProduto().getId()); double antes=estoque.getQuantidade();
            double depois=QuantidadesOperacionais.subtrair(antes,qtd);
            if(!Double.isFinite(depois)||depois<0||depois==antes) throw new IllegalArgumentException("Quantidade excede a precisão do saldo do item");
            estoque.setQuantidade(depois); estoques.save(estoque);
            var m=new Movimentacao(); m.setSolicitacao(s); m.setAtendimento(atendimento); m.setProduto(item.getProduto()); m.setAlmoxarifado(s.getAlmoxarifado());
            m.setSolicitante(s.getSolicitante()); m.setResponsavel(responsavel); m.setTipo(TipoMovimentacao.SAIDA); m.setQuantidade(qtd);
            m.setSaldoAnterior(antes); m.setSaldoPosterior(depois); m.setDataHora(atendimento.getDataHora()); movimentos.save(m);
            item.setQuantidadeAtendida(atendida); itens.save(item); view.atendidas().put(item.getId(),atendida);
        }
        boolean completa=view.itens().stream().allMatch(i->Double.compare(i.getQuantidade(),view.atendidas().get(i.getId()))==0);
        s.setStatus(completa?StatusSolicitacao.ATENDIDA:StatusSolicitacao.PARCIALMENTE_ATENDIDA); solicitacoes.save(s);
        itensAtendimento.flush(); movimentos.flush();
        return respostaAtendimento(atendimento);
    }
    @Transactional(readOnly=true)
    public List<AtendimentoResponse> historico(Integer id) {
        if(!solicitacoes.existsById(id)) throw new RecursoNaoEncontradoException("Solicitação não encontrada");
        return atendimentos.findBySolicitacaoIdOrderByIdAsc(id).stream().map(this::respostaAtendimento).toList();
    }
    private AtendimentoResponse respostaAtendimento(AtendimentoSolicitacao a) {
        return new AtendimentoResponse(a.getId(),a.getSolicitacao().getId(),referencia(a.getResponsavel()),a.getDataHora(),
            itensAtendimento.findByAtendimentoIdOrderByIdAsc(a.getId()).stream().map(i->new AtendimentoResponse.Item(i.getId(),i.getItemSolicitacao().getId(),produto(i.getItemSolicitacao().getProduto()),i.getQuantidade())).toList());
    }
    record Snapshot(Solicitacao solicitacao,List<ItemSolicitacao> itens,Map<Integer,Double> atendidas,StatusSolicitacao status,boolean legado,String aviso) {}
    Snapshot snapshot(Solicitacao s) {
        var items=itens.findBySolicitacaoId(s.getId()).stream().sorted(Comparator.comparing(ItemSolicitacao::getId)).toList();
        var antigos=movimentos.findBySolicitacaoId(s.getId()).stream().filter(m->m.getAtendimentoId()==null).toList();
        boolean legado=!antigos.isEmpty(); String aviso=s.getAlmoxarifado()==null||s.getSolicitante()==null||s.getStatus()==null?"Solicitação sem contexto obrigatório; atendimento bloqueado.":null;
        Map<Integer,Double> saidas=new TreeMap<>(), resultado=new HashMap<>();
        for(var m:antigos) {
            if(m.getTipo()!=TipoMovimentacao.SAIDA||m.getProduto()==null||m.getAlmoxarifado()==null||s.getAlmoxarifado()==null
                ||!m.getAlmoxarifado().getId().equals(s.getAlmoxarifado().getId())||!Double.isFinite(m.getQuantidade())||m.getQuantidade()<=0) { aviso="Histórico legado inconsistente; atendimento bloqueado para conferência."; continue; }
            double total=QuantidadesOperacionais.somar(saidas.getOrDefault(m.getProduto().getId(),0.0),m.getQuantidade());
            if(!Double.isFinite(total)) { aviso="Histórico legado com quantidade inválida."; continue; }
            saidas.put(m.getProduto().getId(),total);
        }
        Map<Integer,List<ItemSolicitacao>> grupos=new TreeMap<>();
        for(var item:items) {
            if(item.getProduto()==null||!Double.isFinite(item.getQuantidade())||item.getQuantidade()<=0) { aviso="Item inválido; atendimento bloqueado."; continue; }
            grupos.computeIfAbsent(item.getProduto().getId(),k->new ArrayList<>()).add(item);
        }
        if(saidas.keySet().stream().anyMatch(id->!grupos.containsKey(id))) aviso="Saída legada de produto sem item correspondente.";
        for(var grupo:grupos.entrySet()) {
            double totalSolicitado=0; for(var i:grupo.getValue()) totalSolicitado=QuantidadesOperacionais.somar(totalSolicitado,i.getQuantidade());
            double totalLegado=saidas.getOrDefault(grupo.getKey(),0.0);
            if(!Double.isFinite(totalSolicitado)||totalLegado>totalSolicitado) aviso="Saída legada superior à demanda; atendimento bloqueado.";
            if(totalLegado>0&&totalLegado<totalSolicitado&&grupo.getValue().size()>1) aviso="Saída legada agregada não permite identificar quanto foi atendido em cada item repetido.";
            for(var i:grupo.getValue()) {
                double base=totalLegado==totalSolicitado?i.getQuantidade():grupo.getValue().size()==1?totalLegado:0;
                double realizada=i.getQuantidadeAtendida()==null?base:i.getQuantidadeAtendida();
                if(!Double.isFinite(realizada)||realizada<base||realizada>i.getQuantidade()||realizada<0) aviso="Quantidade atendida inconsistente com histórico.";
                resultado.put(i.getId(),realizada);
            }
        }
        boolean alguma=resultado.values().stream().anyMatch(q->q>0);
        boolean completa=!items.isEmpty()&&resultado.size()==items.size()&&items.stream().allMatch(i->resultado.get(i.getId())==i.getQuantidade());
        var status=s.getStatus();
        if((legado||alguma)&&(status==StatusSolicitacao.PENDENTE||status==StatusSolicitacao.REJEITADA)) aviso="Saída vinculada a solicitação sem aprovação válida.";
        if(!legado&&status==StatusSolicitacao.APROVADA&&s.getResponsavelAprovacao()==null&&items.stream().anyMatch(i->i.getQuantidadeAtendida()==null)) { legado=true; aviso="Aprovação anterior sem evidência vinculada de saída; conferir antes de atender."; }
        if(status==StatusSolicitacao.ATENDIDA&&!completa) aviso="Status ATENDIDA sem quantidades completas; conferir histórico.";
        if(aviso==null&&alguma) status=completa?StatusSolicitacao.ATENDIDA:StatusSolicitacao.PARCIALMENTE_ATENDIDA;
        return new Snapshot(s,items,resultado,status,legado,aviso);
    }
    static void exigirConsistencia(Snapshot view) { if(view.aviso()!=null) throw new ConflitoException(view.aviso()); }
    OperacaoSolicitacaoResponse resposta(Snapshot view) {
        var s=view.solicitacao(); var detalhes=new ArrayList<OperacaoSolicitacaoResponse.Item>();
        // Allocate current availability once per product to avoid double-counting repeated request items.
        Map<Integer,Double> disponivelRestante=new HashMap<>();
        for(var item:view.itens()) {
            var estoque=item.getProduto()==null||s.getAlmoxarifado()==null?Optional.<Estoque>empty():estoques.findByProdutoIdAndAlmoxarifadoId(item.getProduto().getId(),s.getAlmoxarifado().getId());
            double saldo=estoque.map(Estoque::getQuantidade).orElse(0.0);
            Double atendida=view.aviso()==null?view.atendidas().get(item.getId()):null;
            Double pendente=atendida==null?null:QuantidadesOperacionais.subtrair(item.getQuantidade(),atendida);
            Double falta=null; double livre=saldo;
            if(pendente!=null) {
                livre=disponivelRestante.getOrDefault(item.getProduto().getId(),saldo);
                falta=Math.max(0,QuantidadesOperacionais.subtrair(pendente,livre));
                disponivelRestante.put(item.getProduto().getId(),Math.max(0,QuantidadesOperacionais.subtrair(livre,pendente)));
            }
            detalhes.add(new OperacaoSolicitacaoResponse.Item(item.getId(),produto(item.getProduto()),item.getQuantidade(),atendida,pendente,saldo,livre,estoque.isPresent(),falta,
                necessidades.findByItemSolicitacaoId(item.getId()).map(NecessidadeCompra::getId).orElse(null)));
        }
        return new OperacaoSolicitacaoResponse(s.getId(),view.status().name(),s.getStatus().name(),view.aviso()!=null?"INCONSISTENTE":view.legado()?"RECONHECIDA":"ATUAL",
            view.aviso()!=null?view.aviso():view.legado()?"Atendimento legado reconhecido pelas saídas vinculadas. Histórico original preservado.":null,
            referencia(s.getSolicitante()),referencia(s.getAlmoxarifado()),s.getDataSolicitacao(),referencia(s.getResponsavelAprovacao()),s.getDataAprovacao(),referencia(s.getResponsavelSeparacao()),s.getDataSeparacao(),detalhes);
    }
    static OperacaoSolicitacaoResponse.Produto produto(Produto p) {
        if(p==null) return null;
        var u=p.getUnidadeMedidaConfigurada();
        return new OperacaoSolicitacaoResponse.Produto(p.getId(),p.getCodigo(),p.getNome(),p.getDescricao(),p.getUnidadeMedida(),
            u==null?null:new OperacaoSolicitacaoResponse.Unidade(u.getSigla(),u.isPermiteFracionamento()));
    }
    static OperacaoSolicitacaoResponse.Referencia referencia(Funcionario p) { return p==null?null:new OperacaoSolicitacaoResponse.Referencia(p.getId(),p.getNome()); }
    static OperacaoSolicitacaoResponse.Referencia referencia(Almoxarifado p) { return p==null?null:new OperacaoSolicitacaoResponse.Referencia(p.getId(),p.getNome()); }
}
