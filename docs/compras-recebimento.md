# Bloco 4 — compras e recebimento

Fonte oficial: [Documentação Mestre BES v1.4](Documentacao_Mestre_Plataforma_BES_v1_4.docx), RF001–RF218. Este bloco amplia o monólito modular e mantém o catálogo/estoque, necessidade e atendimento existentes. Checkpoint inicial: branch `feature/bes-frontend`, HEAD `1d699ce1be18765db1e4b5301140afeeb1a6413f`, working tree limpa. Sem commit/push, merge, alteração de main, banco externo ou produção nesta rodada.

## Fluxo e dependências

Solicitação aprovada com falta → NecessidadeCompra existente → rascunho do PedidoCompra com fornecedor → submissão → aprovação humana → recebimento físico confirmado → ENTRADA → atendimento posterior da solicitação pelo fluxo do Bloco 3.

A necessidade representa uma falta confirmada, não uma reserva física de estoque. Compra/recebimento não entrega material ao solicitante. Não existe approval, recebimento ou atendimento automático. Receber não muda status/quantidade atendida da solicitação. A tela pode avisar “Material recebido — solicitação possui pendência”; isso exige conferir o saldo atual, pois outro fluxo pode ter utilizado o estoque.

Dependências funcionais: RF008/009, RF011, RF034–037, RF053–057, RF074/078/079, RF081/082/087, RF148/150, RF193/195/201–204 e documentos RF205–218. Segurança usa [sessão/CSRF/authorities](autenticacao-autorizacao.md). Integração de saldo respeita o protocolo determinístico dos Blocos 2/3.

## Modelo e regras

Pacote `br.com.almoxarifado.compras`: Fornecedor, PedidoCompra, ItemPedidoCompra, AlocacaoCompra, RecebimentoCompra, ItemRecebimentoCompra e DestinacaoRecebimento. PedidoCompra é o único agregado de compra, com itens/alocações; não há agregado concorrente de compra/solicitação de compra. Produtos e NecessidadeCompra são os existentes. DTOs estritos, views sem entidades/proxies ou chaves internas, services transacionais e repositories JPA.

Fornecedor PF/PJ: nome/razão social, nome fantasia, documento opcional, email, telefone, contato, observação, ativo e datas. Documento único, normalizado e com dígitos verificadores. CPF numérico e CNPJ numérico/alfanumérico são aceitos. O cálculo alfanumérico segue o [manual técnico oficial da Receita Federal](https://www.gov.br/receitafederal/pt-br/centrais-de-conteudo/publicacoes/documentos-tecnicos/cnpj/manual-dv-cnpj.pdf); a [página oficial](https://www.gov.br/receitafederal/pt-br/acesso-a-informacao/acoes-e-programas/programas-e-atividades/cnpj-alfanumerico) documenta a evolução. Não consultar a Receita nem validar situação cadastral online. Lista e detalhe somente leitura mascaram documento/retiram contatos; cadastro integral exige gerenciamento. Sem delete. Inativo não participa de novos pedidos; pedidos aprovados e snapshots históricos permanecem recebíveis/consultáveis.

Número humano único: `PC-ano-ID`, derivado do ID gerado em transação (nenhum “max+1” concorrente). Criador/submissão/aprovador/cancelador têm campos separados. Fornecedor, documento/contato e código/nome/unidade do material são fotografados para preservar a compra após edição cadastral. Almoxarifado planejado mantém FK real; não fabricar OS/obra/CC.

Preço BigDecimal/DECIMAL(19,4), com contrato monetário novo em strings decimais sem Number no envio/apresentação, obrigatório e não negativo (zero explícito permitido); subtotal por item = quantidade × preço, HALF_UP em 2 casas; total soma subtotais. DTO não aceita total/subtotal forjado. Limites/escala são validados antes de escalonar expoentes extremos. Quantidades existentes permanecem double; cálculo decimal evita soma binária simples e rejeita resultados de saldo/acumulados que perderiam precisão no modelo. Não houve migração geral de estoque para BigDecimal.

RASCUNHO → AGUARDANDO_APROVACAO → APROVADO → PARCIALMENTE_RECEBIDO → RECEBIDO. Cancelamento possível antes de qualquer recebimento, com motivo e ator/data. Não existe COMPRADO separado, emissão ao fornecedor, aceite externo, estorno nem cancelamento do saldo de pedido parcialmente recebido neste estágio. Edição somente de rascunho: itens anteriores ficam arquivados, com vínculos históricos preservados, e suas reservas são liberadas.

Alocação permite várias necessidades compatíveis num item/pedido e uma necessidade em vários pedidos. Mesmo produto e destino; sem duplicar produto dentro do pedido. O rascunho já compromete suas alocações imediatamente; submissão/aprovação não duplica esse compromisso. Cancelamento antes do primeiro recebimento ou edição do rascunho libera os vínculos anteriores. Disponível = quantidade da necessidade − recebido destinado − vínculos ativos ainda não recebidos. Bloqueio da necessidade impede duas compras simultâneas da mesma falta. Extra sem vínculo só com `paraEstoque=true`; compra manual também exige essa confirmação. Na chegada, distribuir entre alocações por necessidadeId crescente (determinístico) e depois para estoque declarado. Não há reserva física exclusiva para a solicitação.

Necessidade: ABERTA, EM_COMPRA, ATENDIDA, CANCELADA. Status acompanha quantidade efetivamente recebida, não aprovação do pedido. Cancelamento só sem vínculo/recebimento. Dados pré-bloco sem contador físico ficam NULL/A_CONFERIR e não podem ser alocados/cancelados como se recebessem zero; reconciliar histórico com autorização específica. Fotos da falta e responsáveis/datas antigos continuam preservados.

## Recebimento, concorrência e auditoria

Recebimento aprovado/parcial exige destino planejado, responsável físico e itens/quantidades realmente recebidos, até a pendência acumulada. Não existe campo cliente para ator, saldo, recebido acumulado ou status. O serviço registra ator autenticado e data/hora do servidor. Cada item recebido possui saldo anterior/posterior, destinações e um movimento ENTRADA associado; uma referência nullable acrescentada ao movimento preserva contratos antigos.

Transação única: registro/itens/destinações, estoque ausente criado em zero sob lock de produto, entradas, acumulados do pedido/necessidade, status e auditoria. Falha em movimento/auditoria reverte todos esses efeitos. Sucesso de auditoria usa propagação MANDATORY, eventos sem documento/contato ou payload/chave/hash/secret. Ator da sessão e responsável físico permanecem distintos.

Locks: pedido existente → todas as necessidades ordenadas → fornecedor quando necessário → produtos ordenados → pares de estoque. Recebimento nunca pede lock da solicitação, evitando inversão com o atendimento (solicitação → produtos → estoque). Após adquirir o lock, o serviço recarrega pedido/itens, necessidades, fornecedor, produto e estoque: adquirir PESSIMISTIC_WRITE não atualiza automaticamente entidades já hidratadas no contexto JPA. Isso impede que um contador ou cadastro antigo substitua alterações concorrentes. Produto serializa criação de par ausente; estoque usa o repository/protocolo anterior. Receipt contra receipt/saída/transferência/atendimento e criação concorrente de par ausente foram testados em H2.

Idempotency-Key única e payload canônico persistido (SHA-256). Mesmo pedido/payload/chave retorna recibo original, inclusive após RECEBIDO; chave reaproveitada com operação diferente dá 409. Ordem de itens é canônica; observação normalizada/codificada impede colisão por separadores. Chave/assinatura não aparecem nas respostas. Chaves diferentes representam operações diferentes: não prometer deduplicação universal de entregas parciais fisicamente indistinguíveis.

Frontend preserva a tentativa em memória em caso de resposta perdida e oferece reenvio manual com mesma chave/conteúdo. Não altera a tentativa incerta e não faz retry automático. Recarregar/fechar perde memória local: consultar histórico antes de nova tentativa. Double click permanece protegido por UI + idempotência server-side.

## Interface, documentos e segurança

Grupo COMPRAS: fornecedores, necessidades e pedidos; rascunho manual/gerado de faltas; detalhe com ações por estado/authority; links às origens, históricos, preços, recebimentos e entradas. Listagens novas usam página até 100, filtros reais e envelope HTTP estável. Dashboard recebe apenas dois indicadores compactos: pedidos aguardando aprovação e parcialmente recebidos.

Pedido de compra e comprovante de recebimento reutilizam OperationalDocument/Brand, dados reais, A4/PDF via impressão do navegador, sem menu/sidebar na impressão. Confirmação de entrada ocorre em tela. Desktop 1440, tablet 768, mobile 390, rolagem interna das tabelas, labels, teclado e modal nativo. Não são documentos fiscais. Não implementar assinatura digital, aprovação externa ou geração server-side de PDF neste bloco.

Logos oficiais PNG continuam ausentes nos caminhos preparados; usar fallback textual B&S/BES. QR não entregue: não há infraestrutura compartilhada anterior e o bloco não criou biblioteca/serviço de QR. Referências internas e controles de acesso estão presentes; RF212 permanece pendente. Não imprimir OS/CC inexistente, slogan ou logo inventada.

16 endpoints novos/9 escritas, todos permissionados, inventário 85/36 incluindo rotas anteriores. Novas authorities: FORNECEDOR_LER/GERENCIAR; COMPRA_LER/CRIAR/APROVAR/CANCELAR; RECEBIMENTO_LER/REGISTRAR; NECESSIDADE_COMPRA_GERENCIAR. ADMIN tudo; GESTOR gerencia fornecedor/cria/aprova/cancela pedido/necessidade; ALMOXARIFE recebe; CONSULTA lê. Todos preservam permissões antigas; nenhuma nova autoridade amplia gestão de usuários. HTTP gate e @PreAuthorize, CSRF, sessão/CORS/cookies/revogação da baseline preservados. [API](api.md) descreve cada rota/payload/erro; [inventário e matriz](autenticacao-autorizacao.md).

## RFs e limites de cobertura

“Completo” abaixo refere-se ao escopo funcional desta entrega e à evidência operacional; não encerra os demais módulos RF001–218.

| RF | Cobertura e limite |
|---|---|
| RF034, RF036, RF037 | Entregues: necessidade → pedido, preços calculados e histórico por fornecedor/itens/preços |
| RF079, RF202 | Fluxo anterior preservado e integrado: falta aprovada, atendimento parcial e compra da pendência |
| RF087, RF150 | Confirmação humana e atomicidade operacional entregues no fluxo de compras |
| RF008 | Parcial: cadastro/contatos e materiais pelo histórico; catálogo de serviços/associação independente não entregue |
| RF009/011/032/033 | Parcial no sistema: entrada por compra e histórico; demais origens/módulos não ampliados |
| RF035 | Fluxo autorizado entregue; mestre ainda parcial pelo estado COMPRADO/compra externa não separado |
| RF038/055/059/080 | Parcial/pendente: filtros e indicadores/sinal em tela; sem central de notificações/email ao comprador |
| RF053/054/056/057/074/148 | Segurança/auditoria/datas/perfis/aprovação preservados e ampliados para compras; não concluir todos os módulos |
| RF081 | Parcial: entregas em datas diferentes e comprovantes; associação fiscal/NF não entregue |
| RF082 | Parcial: conferência quantitativa e excesso bloqueado; tratamento completo de divergência/sobra não entregue |
| RF193/195/201 | Catálogo único e frações preservados; custos realizados/financeiro permanecem pendentes |
| RF203 | Parcial: solicitação, falta, material, fornecedor, pedido/recebimento/entrada; obra/OS/CC não existem no estágio |
| RF204 | Parcial: unidades/fracionamento existente; sem conversões novas |
| RF205/207/213/214/216/217/218 | Pedido e recibo, identificação, A4/PDF navegador, mobile, template e ações contextuais entregues neste módulo; cobertura global permanece incremental |
| RF206 | Parcial: template/fallback textual; assets oficiais ausentes |
| RF208 | Parcial: almoxarifado/origem reais; OS/obra/CC pendentes |
| RF209 | Lista de separação anterior preservada; nenhuma nova conclusão declarada |
| RF210 | Parcial: recibo de chegada; retirada/entrega e demais comprovantes não ampliados |
| RF211 | Parcial: falta e vínculos em tela e nos itens/documentos de compra; sem documento autônomo de necessidade |
| RF212 | Não entregue: QR |
| RF215 | Parcial: confirmação explícita/ator/data; sem assinatura digital/política ampliada |
| RF083–086, RF149 | Não entregues: NF/PDF/XML, extração IA/mapeamento fornecedor e estorno |

Sem financeiro, contas a pagar, pagamento, banco, impostos, emissão/importação NF/XML, notificações externas, obra/OS/centro de custo, assinatura digital, SSO/OIDC ou redesign da plataforma.

## Schema e revisão

[MySQL](sql/compras-recebimento-mysql-manual.sql) e [PostgreSQL](sql/compras-recebimento-postgresql-manual.sql): propostas manuais, sem execução, com FKs, documentos/números/chaves únicos, quantidade/checks e índices de filtros/rastreio. MySQL 8.0.16+ usa CHECK efetivo e commit implícito de DDL; PostgreSQL requer schema equivalente e homologação. Não são migração entre bancos nem scripts idempotentes.

Antes de aplicar: inspecionar tipos reais, nomes de constraints, collation e status legado, backup, homologar e obter autorização de banco. A restrição de necessidade.status do Bloco 3 precisa incluir EM_COMPRA sem remover estados antigos. Não fazer UPDATE indiscriminado em saldos/status/atores/contadores legados. JPA de teste cria schema apenas H2 em memória.

Evidências finais, arquivos, comandos e os 84 pontos solicitados: [relatório de revisão](bloco4-relatorio-final.md). Pronto para revisão funcional não significa autorizado/pronto para produção: faltam schema real homologado, reconciliação legada quando aplicável, assets/QR/PDF conforme evolução, configuração externa/rotação de credenciais já comprometidas e security gate de produção.

## Auditoria pré-commit do Bloco 4

[A auditoria final](bloco4-auditoria-pre-commit.md) registra a reprodução de contador antigo (100 esperado, 40 observado antes da correção), regressões, resultados e os 60 pontos de revisão. Paginação continua no banco; os itens/contextos de pedidos e recebimentos são carregados em segunda consulta limitada aos IDs da página, sem fetch de coleção na consulta paginada. Destinações/alocações usam batches de 100; progresso de necessidades usa uma consulta de alocações por grupo de até 100 necessidades. Fornecedores não carregam relações. As consultas legadas de necessidades permanecem compatíveis, sem mudar seu contrato para paginação.

As somas de compromisso também passam pela verificação de representação decimal exata, sem conversão final silenciosa de BigDecimal para double. IDs de atores mantêm o contrato escalar e recebem relações JPA privadas, LAZY e somente de leitura para gerar as mesmas FKs a bes_usuario previstas nos scripts. Nenhuma relação de usuário/senha é exposta em DTO ou no histórico de movimentos. Não há cascade de usuário nem alteração de schema externo nesta auditoria.

Documento e contato de fornecedor no snapshot do pedido também exigem FORNECEDOR_GERENCIAR para leitura integral; demais leitores recebem documento mascarado/contato null, incluindo o documento imprimível. Não é possível contornar a proteção do cadastro lendo outro endpoint. A recarga de pedido sob lock não usa cascade REFRESH: itens/alocações são recarregados explicitamente após o lock do pai, preservando os demais cascades.
