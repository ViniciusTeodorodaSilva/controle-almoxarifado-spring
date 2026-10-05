# Bloco 4 — auditoria final pré-commit

05/10/2026. Escopo: auditar e corrigir somente compras, fornecedores e recebimento já implementados. Consultados AGENTS.md, relatório anterior, compras/recebimento, API, autenticação/autorização, security.md e a Documentação Mestre BES v1.4. Inspeção de código, modelos, scripts e testes; relatório anterior não usado como prova isolada.

Cobertura RF034/036/037/079/081/082/087/148/150/193/195/201–218 mantém os limites registrados em [compras e recebimento](compras-recebimento.md). Nenhum requisito global ou módulo novo foi declarado concluído. RF205–218 reavaliados: documentos e ações atuais preservados; QR, assets oficiais e PDF no servidor continuam pendentes, assim como OS/obra/CC inexistentes.

## Falhas e correções

1. **Contador antigo na necessidade após lock.** Regressão criada antes da correção: um contexto já hidratado com recebido zero, seguido por recebimento concorrente de outro pedido (60) e recebimento local (40), produziu 40 em vez de 100. O estoque tinha 100. PESSIMISTIC_WRITE sozinho não substituía a instância hidratada. Recarregar a necessidade sob lock impede a sobrescrita. Teste determinístico preservado, complementado pelo cenário 30+30+40 e pelo atendimento humano posterior.
2. **Estado cadastral/quantitativo no contexto.** A leitura sob lock de fornecedor, produto e estoque passa a recarregar os dados persistidos; pedido e itens recebem proteção equivalente. Produto é bloqueado em ordem de ID na submissão/aprovação, respeitando o protocolo existente. Regressões usam entidades carregadas antes da alteração concorrente.
3. **N+1 introduzido pelo bloco.** Pedido calculava total carregando itens por linha; recebimento carregava contextos/itens/destinações individualmente; necessidade fazia uma consulta de alocações por linha. Carregamento de agregados limitado aos IDs da página, batches de coleções e consulta de progresso por grupos de 100. A paginação permanece SQL; não é feita com fetch de coleção paginada em memória. Testes medem consultas e verificam o uso de lote.
4. **Soma de vínculos com conversão final sem verificação.** A soma de compromisso agora usa o mesmo cálculo decimal com verificação de representação exata existente no estoque. Não arredonda silenciosamente a quantidade ao retornar ao modelo double.
5. **FKs de atores distintas entre H2/JPA e scripts.** Scripts já previam FKs a bes_usuario, mas o JPA tinha apenas IDs escalares. Relações privadas LAZY somente de leitura, sem getter/cascade, agora geram as FKs mantendo o contrato escalar e sem expor usuário/hash. Campos têm nomes de coluna explícitos para evitar mapeamento duplicado. Scripts não executados.

6. **Documento/contato protegido exposto pelo pedido.** O cadastro de fornecedor já restringia dados integrais a FORNECEDOR_GERENCIAR, mas o detalhe do pedido devolvia seu snapshot completo a qualquer COMPRA_LER. O pedido agora aplica a mesma máscara/null aos leitores sem gerência; os documentos consomem o mesmo contrato protegido. Quatro casos de perfil verificam HTTP e service.

Erros encontrados durante os próprios ajustes foram corrigidos antes da validação final: nomes lógicos duplicados de colunas de ator; tratamento de entidade versionada já hidratada e refresh de agregado em cascata, substituído por recarga explícita de itens/alocações após o lock do pai; rollback-only esperado em teste de transição recusada. Nenhum teste anterior removido, desabilitado ou flexibilizado.

## Os 60 pontos solicitados

| Nº | Ponto | Resultado |
|---|---|---|
| 1 | Branch | feature/bes-frontend. |
| 2 | HEAD | 1d699ce1be18765db1e4b5301140afeeb1a6413f, inicial/final preservado. |
| 3 | Working tree | Inicial: 31 novos e 25 modificados, staging vazio. Final: 32 novos e 25 modificados em relação ao HEAD; staging vazio. |
| 4 | Necessidade em vários pedidos | Necessidade 100, rascunhos A60+B40; disponível zero, C+1 rejeitado. |
| 5 | Várias necessidades no pedido | Duas faltas do mesmo produto consolidadas, origem e destinação FIFO preservadas. |
| 6 | Oversubscription | Bloqueio de necessidade e saldo disponível decimal; excesso rejeitado. Rascunho já compromete. |
| 7 | Concorrência de alocação | Duas compras concorrentes disputando a mesma falta: somente uma alocação válida. |
| 8 | Recebimento parcial | Acumulados e status PARCIALMENTE_RECEBIDO; histórico e movimentos por confirmação. |
| 9 | Recebimento em vários pedidos | A30+A30+B40: recebido 30 → 60 → 100. Regressão de contexto antigo corrigida. |
| 10 | Necessidade por RECEBIDO | Aprovação mantém recebido zero; ATENDIDA somente ao chegar quantitativamente a 100. |
| 11 | Solicitação não automática | Após entrada total: APROVADA, pendente 100, atendido zero, somente 3 ENTRADAs. Atendimento humano posterior gera a SAIDA e conclui a solicitação. |
| 12 | Idempotência | Unique da chave, assinatura canônica, lock de pedido e transação; DTO não expõe chave. |
| 13 | Mesma chave/payload | Resposta original, 1 recibo, 1 conjunto de movimentos, saldo/acumulado uma vez. |
| 14 | Chave/payload diferente | Conflito 409, nenhum efeito adicional. |
| 15 | Retry concorrente | Duas chamadas mesma chave: 1 recibo, 1 movimento, acumulado e estoque uma vez. |
| 16 | Excesso | Pedido100/recebido60 rejeita41; duas confirmações concorrentes40 deixam apenas mais40. |
| 17 | Concorrência estoque | Recebimento × recebimento/saída/transferência/atendimento e par ausente; saldos e cadeia anterior/posterior conferidos. |
| 18 | Locks | Pedido → necessidades por ID → fornecedor quando aplicável → produtos por ID → estoques. Não pede lock da solicitação no recebimento. |
| 19 | Rollback | Dois itens, falha no segundo movimento ou na auditoria final: sem recibos/entradas/saldos/contadores parciais. |
| 20 | Auditoria no rollback | Contagem de auditoria antes/depois igual; mudanças de necessidade e sucesso não sobrevivem ao rollback. |
| 21 | BigDecimal | Preço DTO/entity e cálculo BigDecimal; SQL DECIMAL; JSON e frontend strings decimais. Double somente quantidades compatíveis com modelo anterior. |
| 22 | Escala/arredondamento | Unitário 19,4; subtotal HALF_UP 2 casas; total soma linhas arredondadas. Provas 0.10/0.20/0.30, quantidade fracionada e meia fração de centavo. |
| 23 | Total servidor | Calculado de preço/quantidade persistidos; preço alto conserva precisão; total/subtotal cliente rejeitados. |
| 24 | Mass assignment | Raiz, item, alocação e item recebido rejeitam campos desconhecidos: subtotal/total/recebidos/status/criadoPor/aprovadoPor/ator/auditUser. |
| 25 | Fornecedor inativo | Bloqueia compra nova; normalização/validação/unique CPF/CNPJ; histórico e recebimento de compromisso aprovado preservados. Cache antigo também testado. |
| 26 | Produto inativo | Bloqueia compra/submissão/aprovação; histórico preservado. Receber compromisso já aprovado permanece permitido/documentado. |
| 27 | Editar aprovado | Fornecedor/produto/quantidade/preço/vínculos não mudam; só RASCUNHO editável. |
| 28 | Cancelamento | RASCUNHO/AGUARDANDO/APROVADO permitem antes de receber; parcial/recebido bloqueiam; CANCELADO repetido sem efeito. Não há cancelamento de saldo parcial/estorno. |
| 29 | Número concorrente | Identity do banco + PC-ano-ID e unique; dois pedidos simultâneos com números distintos, sem max+1. |
| 30 | Destino | ID planejado obrigatório; par existente/ausente seguro. Troca por outro local rejeitada, saldo do outro local permanece9. |
| 31 | Autorização | Matriz dos 16 handlers; CONSULTA sem escrita, ALMOXARIFE sem aprovação, GESTOR sem usuários, ADMIN conforme permissions. IDs trocados não mudam authority. |
| 32 | Service-layer | @PreAuthorize e testes de bypass/negativas nas operações críticas; sucessos operacionais reais com identidade. |
| 33 | CSRF | Todas as 9 escritas novas rejeitam token ausente; inventário dinâmico 85/36 preservado, incluindo gate positivo/negativo da baseline. |
| 34 | IDOR | IDs inexistentes/de outro pedido/destino/produto/necessidade não contornam permissionamento nem compatibilidade. Perfil é global; segregação por obra/tenant futura, não inventada. |
| 35 | Ator auditoria | Sessão autenticada; body forjado rejeitado; query/header arbitrários não alteram ator. Usuário e responsável operacional separados no recibo/movimento/evento. |
| 36 | Documentos | GET autenticado de registros persistidos; template compartilhado, A4, print sem ações/sidebar/menu; testes desktop/tablet/mobile. PDF via navegador. |
| 37 | Frontend/retry | Lost response após commit servidor: retry manual mesma chave/payload, sem duplicar. Double click, loading, permissões e estados; 401/403/CSRF tratados pelo cliente existente. Reload perde tentativa local: consultar histórico. |
| 38 | N+1 | Itens/contextos em consultas por página e progresso em lotes; regressões de consultas. Fornecedor sem relações. Sem redesign ou otimização fora do bloco. |
| 39 | Scripts | Modelos/7 tabelas/FKs/uniques/índices/DECIMAL/número/chave/vínculos comparados. Propostas MySQL8.0.16+/PostgreSQL não executadas; tipos/collation/constraints reais ainda precisam homologação. |
| 40 | Falhas | Cache antigo de necessidade reproduzido; proteção cadastral/quantitativa, N+1, soma convertida sem verificação e FK de ator divergente e snapshot de fornecedor contornando proteção de leitura. Ver detalhes acima. |
| 41 | Correções | Reload sob lock, produtos ordenados na aprovação, carregamento por página/lote, soma decimal verificada e FKs privadas de atores; proteção de leitura do fornecedor também no pedido. |
| 42 | Novos testes | 32 casos backend desta auditoria; cenários e nomes abaixo. Nenhum teste anterior removido. |
| 43 | Backend | 328 aprovados (251 anteriores + 77 de compras: 45 implementação + 32 auditoria), zero failures/errors/skips. |
| 44 | Frontend | 72 aprovados, sem falhas/skips. |
| 45 | Playwright | 47 aprovados, zero falhas/retries/skips, H2 em memória + vite preview (2,2 min). |
| 46 | Build | npm.cmd run build aprovado. |
| 47 | npm audit | Zero vulnerabilidades em todos os níveis. |
| 48 | Secret scan | 57 arquivos revisados por padrões de chave privada/provider token/JWT/credenciais literais: nenhum secret novo; única ocorrência é fixture preexistente/inalterada da baseline. Valores não reproduzidos. Scan heurístico não substitui revisão de staging futura. |
| 49 | Diff check | git diff --check aprovado; 32 arquivos novos verificados separadamente, sem whitespace/conflitos e com newline final. |
| 50 | Git status | Staging vazio; HEAD/branch preservados; manifesto final abaixo. |
| 51 | Arquivos auditoria | Manifesto abaixo distingue ajustes desta auditoria do Bloco 4 inteiro. |
| 52 | Logos | PNG oficiais ausentes; Brand mantém fallback textual B&S/BES e caminhos preparados; nenhuma logo inventada. |
| 53 | QR | RF212 pendente; nenhum QR improvisado. |
| 54 | Riscos | Quantidades double; chaves diferentes representam operações distintas; reload perde tentativa; sem estorno/cancelamento parcial; H2 não homologa locks/DDL do banco real; schema/legado/security gate pendentes. |
| 55 | Sem MySQL | Confirmado: apenas H2 em memória, nenhuma conexão/execução externa. |
| 56 | Sem produção | Confirmado: nenhum acesso/deploy/publicação. |
| 57 | Sem commit | Confirmado: HEAD preservado, nenhum git add/commit. |
| 58 | Sem push | Confirmado: nenhum push/merge. |
| 59 | Main | Main local 0137ddbddf403f8331d1a3f6d632428f7614ee15 intacta; não atualizada nem publicada. |
| 60 | Recomendação | **COMMITAR**, após revisão humana do diff/staging; correções materiais concluídas e todos os gates verdes. Nenhum commit executado. Não equivale a prontidão de produção. |

## Testes acrescentados

Os métodos de [ComprasTests](../src/test/java/br/com/almoxarifado/compras/ComprasTests.java) acrescentados nesta auditoria são: recebimentosDePedidosDiferentesNaoUsamNecessidadeAntigaDoContexto; necessidadeCemDivididaSessentaQuarentaSoAtendidaAposReceber; cemRecebidosSessentaExcessoQuarentaUmEConcorrenciaQuarenta; rollbackMultiItemIncluiEstoqueNecessidadeMovimentosEAuditoria (2 casos); dinheiroDecimalFracionadoEArredondamentoNoServidor; massAssignmentAninhadoRejeitaCamposDeAutoridade; cancelamentoEmTodosOsEstadosPreservaHistorico (6); aprovadoBloqueiaEdicaoDeFornecedorProdutoQuantidadePrecoEVinculos; historicoLegivelAposInativarFornecedorEProduto; numeracaoConcorrenteUnicaPersistida; destinoMaliciosoNaoModificaOutroAlmoxarifado; paginaDePedidosCarregaItensSemConsultaPorPedido; todasAsRotasNovasRespeitamMatrizEIdsNaoContornamPermissao (4); listagemNecessidadesConsultaAlocacoesEmLote; paginaRecebimentosNaoFazConsultaDeItensPorRegistro; atorNaoVemDeQueryOuHeaderEEventoPreservaResponsavel; bloqueioProdutoRecarregaEstadoAntesSubmeter; recebimentoRecarregaPedidoEEstoqueAntigosSobLock; fornecedorInativadoNaoPodeSerCompradoComCacheAntigo; pedidoNaoContornaProtecaoDoDocumentoEContatoDoFornecedor (4). Cadeia de saldos e contagem de movimentos também reforçadas nos testes concorrentes existentes.

## Evidências finais

- `.mvnw.cmd clean test`: 328 testes, zero failures/errors/skipped; XML Surefire somado e ComprasTests77. Checkpoint inicial296 confirmado como251 anteriores+45 de compras; crescimento32 nesta auditoria.
- `npm.cmd run build`: exit0, build concluído.
- `npm.cmd test`:72 testes, zero failures/skips, exit0.
- `npm.cmd run test:e2e`:47 passed(2,2min), exit0, sem retries; H2 e preview responderam200 antes de iniciar. Nenhum teste apontou para backend de produção.
- `npm.cmd audit --json`: info/low/moderate/high/critical/total=0, exit0.
- Scan heurístico:57 arquivos novos/modificados; nenhum novo secret; única ocorrência classificada como fixture antiga. Não reproduzir valor.
- `git diff --check`:exit0. Verificação adicional dos32 arquivos novos: zero problemas de whitespace/marcadores/newline/UTF-8.
- `git status --porcelain=v1 -uall`:25 modificados+32 novos; staging vazio. `git diff --stat`:25 arquivos rastreados,216 inserções/28 exclusões (não inclui novos). Manifesto abaixo inclui todos os57.
- Branch/HEAD/main final conferidos; master v1.4 sem alteração. Nenhum commit, push, merge, git add ou script manual SQL executado.

Logs e artefatos de execução em TEMP/target/caminhos Playwright ignorados. Nunca registrar valores de fixtures de login, CSRF, cookie, sessão ou segredo no relatório. Servidores temporários encerrados ao concluir. Não houve execução dos scripts SQL, alteração da documentação mestre nem expansão de escopo.

## Arquivos ajustados nesta auditoria

19 arquivos (18 ajustes em arquivos do Bloco 4 e 1 relatório novo):

- `docs/README.md`
- `docs/api.md`
- `docs/autenticacao-autorizacao.md`
- `docs/compras-recebimento.md`
- `docs/bloco4-relatorio-final.md`
- `docs/bloco4-auditoria-pre-commit.md`
- `src/main/java/br/com/almoxarifado/compras/AlocacaoCompra.java`
- `src/main/java/br/com/almoxarifado/compras/AlocacaoCompraRepository.java`
- `src/main/java/br/com/almoxarifado/compras/ItemPedidoCompra.java`
- `src/main/java/br/com/almoxarifado/compras/ItemRecebimentoCompra.java`
- `src/main/java/br/com/almoxarifado/compras/PedidoCompra.java`
- `src/main/java/br/com/almoxarifado/compras/PedidoCompraRepository.java`
- `src/main/java/br/com/almoxarifado/compras/PedidoCompraService.java`
- `src/main/java/br/com/almoxarifado/compras/RecebimentoCompra.java`
- `src/main/java/br/com/almoxarifado/compras/RecebimentoCompraRepository.java`
- `src/main/java/br/com/almoxarifado/model/Movimentacao.java`
- `src/main/java/br/com/almoxarifado/model/NecessidadeCompra.java`
- `src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java`
- `src/test/java/br/com/almoxarifado/compras/ComprasTests.java`

## Manifesto final do working tree

Modificados em relação ao HEAD (25):

- `AGENTS.md`
- `docs/README.md`
- `docs/api.md`
- `docs/autenticacao-autorizacao.md`
- `frontend/README.md`
- `frontend/package.json`
- `frontend/src/App.jsx`
- `frontend/src/auth/permissions.js`
- `frontend/src/components/AppShell.jsx`
- `frontend/src/components/ui.jsx`
- `frontend/src/pages/Dashboard.jsx`
- `frontend/src/pages/Operations.jsx`
- `frontend/src/pages/PurchaseNeeds.jsx`
- `frontend/src/styles.css`
- `src/main/java/br/com/almoxarifado/dto/NecessidadeCompraResponse.java`
- `src/main/java/br/com/almoxarifado/model/Movimentacao.java`
- `src/main/java/br/com/almoxarifado/model/NecessidadeCompra.java`
- `src/main/java/br/com/almoxarifado/model/StatusNecessidadeCompra.java`
- `src/main/java/br/com/almoxarifado/repository/NecessidadeCompraRepository.java`
- `src/main/java/br/com/almoxarifado/security/Perfil.java`
- `src/main/java/br/com/almoxarifado/security/Permissao.java`
- `src/main/java/br/com/almoxarifado/security/RotasPermissao.java`
- `src/main/java/br/com/almoxarifado/security/SegurancaConfig.java`
- `src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java`
- `src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java`

Novos ainda não rastreados (32):

- `docs/bloco4-auditoria-pre-commit.md`
- `docs/bloco4-relatorio-final.md`
- `docs/compras-recebimento.md`
- `docs/sql/compras-recebimento-mysql-manual.sql`
- `docs/sql/compras-recebimento-postgresql-manual.sql`
- `frontend/src/pages/PurchaseDocument.jsx`
- `frontend/src/pages/PurchaseOrders.jsx`
- `frontend/src/pages/Suppliers.jsx`
- `frontend/src/utils/purchases.js`
- `frontend/tests/purchases.spec.js`
- `frontend/tests/purchases.test.js`
- `src/main/java/br/com/almoxarifado/compras/AlocacaoCompra.java`
- `src/main/java/br/com/almoxarifado/compras/AlocacaoCompraRepository.java`
- `src/main/java/br/com/almoxarifado/compras/ComprasController.java`
- `src/main/java/br/com/almoxarifado/compras/ComprasInput.java`
- `src/main/java/br/com/almoxarifado/compras/ComprasPage.java`
- `src/main/java/br/com/almoxarifado/compras/ComprasViews.java`
- `src/main/java/br/com/almoxarifado/compras/DestinacaoRecebimento.java`
- `src/main/java/br/com/almoxarifado/compras/DocumentoFornecedor.java`
- `src/main/java/br/com/almoxarifado/compras/Fornecedor.java`
- `src/main/java/br/com/almoxarifado/compras/FornecedorRepository.java`
- `src/main/java/br/com/almoxarifado/compras/FornecedorService.java`
- `src/main/java/br/com/almoxarifado/compras/ItemPedidoCompra.java`
- `src/main/java/br/com/almoxarifado/compras/ItemRecebimentoCompra.java`
- `src/main/java/br/com/almoxarifado/compras/PedidoCompra.java`
- `src/main/java/br/com/almoxarifado/compras/PedidoCompraRepository.java`
- `src/main/java/br/com/almoxarifado/compras/PedidoCompraService.java`
- `src/main/java/br/com/almoxarifado/compras/RecebimentoCompra.java`
- `src/main/java/br/com/almoxarifado/compras/RecebimentoCompraRepository.java`
- `src/main/java/br/com/almoxarifado/compras/StatusPedidoCompra.java`
- `src/main/java/br/com/almoxarifado/compras/TipoPessoa.java`
- `src/test/java/br/com/almoxarifado/compras/ComprasTests.java`
