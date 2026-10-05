# Bloco 4 — relatório final de revisão

05/10/2026. Fonte: [Documentação Mestre BES v1.4](Documentacao_Mestre_Plataforma_BES_v1_4.docx). Decisões e matriz de RFs: [compras e recebimento](compras-recebimento.md). Contratos: [API](api.md). [Inventário/matriz de permissões](autenticacao-autorizacao.md).

Entrega integrada: falta aprovada → pedido/fornecedor → aprovação → recebimento físico → ENTRADA → atendimento humano posterior. Validação final: **328 backend, 72 frontend, 47 Playwright**, build aprovado, audit zero e diff check aprovado. Sem commit/push/banco externo/produção.

A revisão pré-commit subsequente identificou e corrigiu falhas adicionais de cache sob lock, consultas por registro, proteção do snapshot de fornecedor e alinhamento das FKs de atores. Provas, novos testes e os 60 pontos: [auditoria final pré-commit](bloco4-auditoria-pre-commit.md). Os números acima e abaixo refletem o gate final dessa auditoria.

## Os 84 pontos solicitados

| Nº | Ponto | Resultado |
|---|---|---|
| 1 | branch | feature/bes-frontend, preservada. |
| 2 | HEAD inicial | 1d699ce1be18765db1e4b5301140afeeb1a6413f; HEAD final igual. |
| 3 | estado inicial | Working tree limpa; checkpoint oficial conferido antes das alterações. |
| 4 | arquitetura do módulo | Monólito modular, pacote compras, DTOs estritos, services transacionais e repositories JPA; catálogo/estoque/necessidade existentes. |
| 5 | entidade Fornecedor | PF/PJ, nome/fantasia, documento opcional, contatos, observação, ativo, datas e versão. |
| 6 | validação CPF/CNPJ | Normalização, formato, dígitos verificadores, unique; CNPJ numérico/alfanumérico conforme Receita. Sem consulta externa. |
| 7 | status fornecedor | Ativo/inativo, sem delete; inativo bloqueia novo pedido, histórico preservado. |
| 8 | modelo PedidoCompra | Agregado único PedidoCompra, número humano único, fornecedor/contexto/snapshots, status, atores e datas. |
| 9 | modelo ItemPedido | Produto compartilhado, snapshots, pedido/recebido/estoque, preço, observação e alocações; itens de edição arquivados. |
| 10 | BigDecimal/DECIMAL | BigDecimal/DECIMAL(19,4), valores monetários como strings decimais no novo contrato e no formulário; quantidades double existentes preservadas. |
| 11 | cálculo monetário | Servidor: unitário × quantidade; subtotal HALF_UP em 2 casas; total soma subtotais; valores forjados rejeitados. |
| 12 | status pedido | RASCUNHO, AGUARDANDO_APROVACAO, APROVADO, PARCIALMENTE_RECEBIDO, RECEBIDO, CANCELADO. COMPRADO separado pendente. |
| 13 | segregação criação/aprovação | Criador e aprovador autenticados separados com datas. Não há proibição extra de autoaprovação por perfil autorizado. |
| 14 | necessidade → pedido | NecessidadeCompra existente vinculada quantitativamente a itens, sem duplicação. |
| 15 | várias necessidades por pedido | Sim, consolidação por produto/destino compatíveis com origens preservadas. |
| 16 | necessidade em vários pedidos | Sim, até o disponível calculado e bloqueado no servidor. |
| 17 | controle de excesso | Extra só com paraEstoque explícito; sem sobrealocação silenciosa. |
| 18 | compra manual | Sim, destinação explícita ao estoque e preço informado; sem solicitação fictícia. |
| 19 | recebimento | Físico: destino, responsável operacional, ator autenticado, itens, observação e data do servidor. |
| 20 | recebimento parcial | Sim, PARCIALMENTE_RECEBIDO e acumulados por item. |
| 21 | múltiplos recebimentos | Sim, datas diferentes, histórico paginado e comprovantes individuais. |
| 22 | prevenção de excesso | Não ultrapassa acumulado pedido; bloqueia item/destino/estado incompatível. |
| 23 | estoque | ENTRADA atômica, estoque ausente iniciado em zero sob lock de produto, saldos anteriores/posteriores. |
| 24 | movimentações | Uma ENTRADA por item; referência única e IDs do pedido/recebimento/ator; contratos antigos preservados. |
| 25 | locks | Pedido → necessidades ordenadas → fornecedor quando necessário → produtos ordenados → estoque. Não bloqueia solicitação. |
| 26 | concorrência | Testado receipt×receipt/mesma chave, compras da mesma falta, receipt×saída/transferência/atendimento e par ausente. |
| 27 | idempotência | Chave única + fingerprint canônico persistido; mesma tentativa retorna original, payload diferente dá 409; sem retry automático. |
| 28 | atualização das necessidades | Status e progresso conforme recebido destinado/vínculos; aprovação não marca atendida. |
| 29 | comportamento da solicitação original | Permanece pendente; nenhum atendimento/retirada automático. Fluxo humano posterior validado. |
| 30 | alerta de material disponível | Sinal em necessidade com recebido destinado e pendência real; conferir saldo atual. Sem reserva ou notificação externa. |
| 31 | endpoints fornecedores | 4 handlers: GET lista/detalhe, POST criar, PUT atualizar/inativar. |
| 32 | endpoints pedidos | 7 handlers: GET lista/detalhe, POST criar, PUT editar/submeter/aprovar/cancelar. |
| 33 | endpoints recebimentos | 4 handlers: GET/POST no pedido e GET global/detalhe; mais 1 PUT para cancelar necessidade. |
| 34 | filtros | Fornecedor nome/documento/ativo; pedido número/fornecedor/status/produto/necessidade/datas; recebimento pedido/fornecedor/produto/local/datas. Páginas até 100. |
| 35 | permissões novas | 9 authorities: FORNECEDOR_LER/GERENCIAR, COMPRA_LER/CRIAR/APROVAR/CANCELAR, RECEBIMENTO_LER/REGISTRAR, NECESSIDADE_COMPRA_GERENCIAR. |
| 36 | matriz por perfil | ADMIN tudo; GESTOR gerencia/cria/aprova/cancela; ALMOXARIFE lê/recebe; CONSULTA lê. Permissões anteriores preservadas; sem nova gestão de usuários. |
| 37 | service-layer security | @PreAuthorize nas operações públicas; matriz HTTP/service positiva/negativa e bypass testados. |
| 38 | CSRF | Obrigatório nas 9 escritas novas e 27 anteriores; inventário cobre 36. |
| 39 | CORS/sessão | Sessão, CSRF, CORS explícito, cookies, revogação e headers da baseline preservados; nenhum secret no frontend. |
| 40 | auditoria | Eventos transacionais de cadastro/pedido/recebimento/necessidade; rollback inclui sucesso de auditoria; sem documentos/contatos/payload/chave/secret nos eventos. |
| 41 | ator x responsável operacional | Ator vem da Identidade; responsável físico é Funcionario informado/validado; ambos rastreáveis. |
| 42 | frontend fornecedores | Cadastro/edição/inativação, filtros/paginação, documento protegido e histórico de compras. |
| 43 | frontend necessidades | Seleção de faltas, progresso, origens/pedidos, aviso humano e cancelamento permissionado sem vínculo/recebimento. |
| 44 | criação de pedido | Rascunho manual ou de faltas; catálogo único, alocações, fornecedor ativo e confirmação de extras. |
| 45 | detalhe | Preços/quantidades/status/atores/datas, ações, origens, histórico e movimentos. |
| 46 | recebimento frontend | Responsável/quantidades explícitos, revisão/confirmar entrada, bloqueio durante envio e recuperação da tentativa em memória. |
| 47 | dashboard | Dois indicadores compactos: pedidos aguardando aprovação e parcialmente recebidos; sem redesign. |
| 48 | Pedido de Compra imprimível | Template existente, dados reais, A4/print/PDF do navegador; sem menu/sidebar na impressão. |
| 49 | Comprovante de Recebimento | Entregue: confirmação física, atores/data, materiais/saldos/origens; não fiscal. |
| 50 | QR | Não implementado. RF212 pendente, sem infraestrutura compartilhada anterior; referências internas presentes. |
| 51 | situação das logos B&S | Assets oficiais ausentes; fallback textual B&S/BES e caminhos preparados, sem logo inventada. |
| 52 | responsividade | 1440×900, 768×1024 e 390×844; sem overflow de página, tabelas com rolagem interna. |
| 53 | acessibilidade | Labels, teclado, scroll focável, modal nativo e estados/notices. Sem auditoria WCAG externa completa. |
| 54 | scripts MySQL | Proposta manual MySQL 8.0.16+, não executada. |
| 55 | scripts PostgreSQL | Proposta manual para schema PostgreSQL equivalente futuro, não executada. |
| 56 | constraints/índices | FKs, unique documento/número/chave/item, checks e índices; somas entre linhas protegidas por service/locks. |
| 57 | compatibilidade histórica | Contratos aditivos/fotos/atores anteriores preservados; contadores desconhecidos NULL/A_CONFERIR, sem backfill inventado. |
| 58 | RFs atendidos | RF034/036/037, integração RF079/202 e confirmação/atomicidade RF087/150 no escopo autorizado; ver matriz, sem encerrar RF001–218. |
| 59 | RFs parciais | RF008/009/011/032/033/035/038/053–059/074/081/082/148/193/195/201/203/204 e documentos RF205–218, conforme limites na matriz. |
| 60 | RFs não atendidos | QR RF212, NF/PDF/XML/IA/mapeamento RF083–086, email RF080, estorno RF149; financeiro/OS/CC/assinatura/SSO fora do bloco. |
| 61 | testes backend — total | 328 = 251 preservados + 77 de compras (45 da implementação + 32 da auditoria); zero failures/errors/skips, clean test H2. |
| 62 | testes frontend — total | 72 = 63 preservados + 9 novos; zero falhas/skips. |
| 63 | Playwright — total | 47 = 36 preservados + 11 novos; zero retries/skips, H2+preview. |
| 64 | build | npm run build concluído. |
| 65 | npm audit | 0 vulnerabilidades, em todos os níveis. |
| 66 | secret scan | Nenhum secret real novo. Uma ocorrência é fixture H2 preexistente/inalterada em teste da baseline; valor não reproduzido. |
| 67 | git diff --check | Passou; novos também revisados para trailing whitespace/marcadores de conflito. |
| 68 | git status | 32 novos, 25 modificados do bloco e auditoria; staging vazio; HEAD/branch preservados. |
| 69 | arquivos criados | 32; manifesto completo abaixo, incluindo este relatório. |
| 70 | arquivos modificados | 25; manifesto completo abaixo. |
| 71 | quantidade de endpoints novos | 16 handlers explícitos, sendo 9 escritas. |
| 72 | inventário de endpoints atualizado | 85 handlers/36 escritas; tabelas método/rota/authority em API e autenticação; teste dinâmico ampliado. |
| 73 | falhas encontradas na revisão adversarial | Proxies lazy acessados por campos, snapshot menor que contrato legado, assinatura com separadores ambíguos, precisão extrema e overflow mobile; conversão monetária para Number no formulário. Nos testes: fixture repetida, sincronização de login e seletor ambíguo. |
| 74 | correções realizadas | Acessores JPA, snapshots compatíveis (255), observação codificada/canonicalização, limites/precisão, strings decimais de ponta a ponta, scroll interno/colunas; fixtures/sessão/seletor de teste corrigidos. Inventário ampliado sem remover testes. |
| 75 | riscos restantes | Chaves diferentes podem duplicar entrega parcial por ação humana; reload perde tentativa local; quantidades double; sem estorno; schema real/legado não homologado. |
| 76 | pendências para produção | Autorizar/homologar DDL, reconciliar legado, configurar HTTPS/cookies/CORS/configuração externa, rotação autorizada de credenciais comprometidas e security gate; logos/QR/PDF conforme roadmap. |
| 77 | confirmação de nenhum MySQL | Confirmado: apenas H2 em memória; nenhum MySQL/banco externo. |
| 78 | confirmação de nenhuma produção | Confirmado: nenhuma produção/deploy/publicação. |
| 79 | confirmação de main intacta | Confirmado: main local continua 0137ddbddf403f8331d1a3f6d632428f7614ee15; nenhuma alteração de main. |
| 80 | confirmação de nenhum commit | Confirmado: nenhum commit, HEAD inicial preservado. |
| 81 | confirmação de nenhum push | Confirmado: nenhum push. |
| 82 | confirmação de nenhum merge | Confirmado: nenhum merge. |
| 83 | confirmação de nenhum secret | Confirmado: nenhum secret real introduzido/impresso/no frontend; navegador usa apenas fixtures fictícias H2 já existentes. |
| 84 | se o BLOCO 4 está pronto para revisão e commit | Sim, pronto para revisão e decisão de commit; commit/push não executados e permanecem não autorizados. Não equivale a prontidão de produção. |

## Evidências e comandos

- `.mvnw.cmd clean test`: 328 testes, zero failures/errors/skipped; JUnit em target/surefire-reports, ignorado.
- `npm test`: 72 testes, zero falhas/skips.
- `npm run build`: concluído; dependências/lockfile não alterados.
- `npm audit --json`: total zero.
- `npm run test:e2e`: 47 testes, zero retries/skips; H2 em memória 8081, preview 5173 com proxy H2.
- `git diff --check`: aprovado; staging vazio; HEAD/branch confirmados ao finalizar.
- Inspeção visual local desktop/mobile e mídia print: template/campos reais, preços precisos, tabela contida e sem menu/sidebar. Não equivale à homologação de todas as impressoras nem a PDF server-side.
- Secret scan dos novos/modificados: padrões de chave privada, provider token, JWT e credenciais literais. Apenas fixture H2 de teste preexistente/inalterada; nenhum secret novo/valor reproduzido.

Logs/screenshots temporários fora do repositório; artefatos Playwright em caminhos ignorados. Servidores temporários H2/preview encerrados ao concluir. Scripts SQL não executados.

## Manifesto dos arquivos

### Criados (32)

- `docs/bloco4-relatorio-final.md`
- `docs/bloco4-auditoria-pre-commit.md`
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

### Modificados (25)

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
