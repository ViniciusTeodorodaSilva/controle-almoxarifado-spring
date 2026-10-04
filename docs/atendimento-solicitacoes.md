# Bloco 3 — atendimento de solicitações

Fonte oficial: [Documentação Mestre BES v1.4](Documentacao_Mestre_Plataforma_BES_v1_4.docx), RF001–RF218. Monólito modular; Produto/Catálogo existente continua único. Nenhum módulo completo de Compras, autenticação, ferramentas ou OS é antecipado.

## Escopo e dependências identificados antes da implementação

Depende do catálogo/unidades do Bloco 1, locks, movimentações e integridade de estoque do Bloco 2. RFs centrais: RF011, RF072, RF074–RF079, RF150, RF195, RF202–RF203 e RF205–RF218. RF034, RF053–RF055, RF060, RF070–RF071, RF073, RF080, RF148, RF169, RF193, RF201 e RF204 são relacionados e avaliados sem assumir conclusão transversal.

Entrega completa no núcleo deste bloco: autorização separada da saída, separação com responsável, atendimentos explícitos totais/parciais, falta atual, registro humano de necessidade, histórico vinculado, transação e idempotência persistida, Lista de Separação HTML imprimível e template inicial. Parcial: compras, identidade sem assets oficiais, contexto OS/centro de custo ainda ausente, PDF dedicado, autorização/assinaturas e rastreabilidade transversal. Não entregue: notificações, terceiro retirante, comprovante, documento de necessidade ou QR.

## Mudança deliberada de comportamento

Antes, `PUT /solicitacoes/{id}/aprovar` validava disponibilidade, debitava estoque e criava SAIDA, deixando APROVADA. Agora autoriza e registra responsável/data; não exige saldo disponível, não debita nem gera movimento. Clientes que dependiam da saída no PUT precisam usar separação e POST de atendimento. Contratos de cadastros, criação/adição de itens e operações manuais continuam disponíveis.

Fluxo: `PENDENTE → APROVADA → EM_SEPARACAO → PARCIALMENTE_ATENDIDA → ATENDIDA`; um atendimento total permite `EM_SEPARACAO → ATENDIDA`. `PENDENTE → REJEITADA`. Apenas PENDENTE aceita itens/decisão. Separação registra responsável/data sem reservar estoque. Apenas EM_SEPARACAO ou PARCIALMENTE_ATENDIDA aceita nova entrega. Sem DELETE/edição de atendimento confirmado.

## Quantidades e falta

`quantidade` continua sendo o solicitado no contrato antigo; `quantidadeSolicitada` é seu alias. Novos itens iniciam `quantidadeAtendida = 0`; em dados antigos a coluna pode ser NULL. `quantidadePendente = solicitado - atendido` é derivada, não outra coluna. O snapshot operacional é a fonte para leitura legada segura.

`falta = max(0, pendente - disponibilidade atual)`. Pendente não é falta: pendente 30 com saldo 10 significa falta 20. Para produtos repetidos na mesma solicitação, o saldo é alocado por ID crescente do item: `saldoAtual` é o saldo físico, `saldoDisponivel` é sua parcela de referência; não é reserva. Isso evita sugerir duas vezes o mesmo saldo. Entre solicitações diferentes não há reserva/prioridade global; a necessidade é uma fotografia operacional confirmada, não planejamento agregado de compras.

O formulário sugere min(pendente, saldoDisponivel), permite reduzir e ignora zero/vazio. Servidor rejeita payload vazio, itens alheios/duplicados, quantidade não positiva/não finita, acima do pendente ou saldo, unidade não fracionária, produto inativo, estoque inexistente e transições inválidas. Quantidade/saldo sem precisão para mudança são rejeitados. Mantidos doubles em modelo/API; operações locais usam aritmética decimal intermediária para evitar resíduos como 0,3 - 0,1 - 0,2. Migração coordenada para BigDecimal permanece futura.

## Transação, concorrência e auditoria

Lock pessimista da solicitação, depois todos os produtos em ordem crescente e pares de estoque determinísticos, compatível com transferências. Entradas/saídas manuais agora seguem a mesma ordem produto → estoque, evitando inversão previsível com os FKs de movimentos. Cadastro de par também utiliza o lock do produto. Geração de necessidade lê apenas o ID escalar de origem antes do lock da solicitação; carrega quantidades depois dele, para não usar item antigo do cache da transação. Falha em qualquer etapa desfaz saldo, quantidade atendida, status, atendimento, itens e movimentos. Testes observam o banco após rollback, sem transação envolvendo o teste.

Cada atendimento cria cabeçalho e itens, uma SAIDA por item efetivamente entregue, com solicitação/atendimento/produto/almoxarifado/responsável/solicitante, quantidade, instante e saldos anterior/posterior. Aprovação e separação guardam seus responsáveis/instantes. Responsável ainda é um funcionário explicitamente selecionado; não constitui identidade autenticada. Autorização por perfil e auditoria de usuário/sessão continuam necessárias antes de uso público.

## Idempotência

Os dois POSTs novos exigem `Idempotency-Key` ASCII com 8–100 caracteres (`A-Z a-z 0-9 . _ : -`). Cabeçalho persistido com UNIQUE e assinatura SHA-256 do payload normalizado. Mesma chave + mesmo payload devolve registro original, inclusive após conclusão/reposição; mesma chave + conteúdo diferente gera 409. Itens de atendimento são normalizados por ID, sem depender da ordem no JSON.

O lock da solicitação serializa retries da mesma operação. Colisões simultâneas de chave entre solicitações diferentes são contidas pela constraint; a operação perdedora faz rollback e retorna conflito. Chaves são globais dentro de cada tipo de operação e sensíveis a maiúsculas/minúsculas (scripts manuais usam collation binária/C). Não expirar chaves sem política futura de retenção.

Necessidade também tem UNIQUE por item da solicitação: repetir com outra chave devolve a necessidade já registrada. Uma chave alternativa não é adicionada como alias ao registro; o vínculo único do item preserva a deduplicação. O frontend mantém payload/chave durante a confirmação, bloqueia envio simultâneo e, em conexão/5xx, oferece “Conferir resultado” usando exatamente a mesma operação/chave. Não há retry automático. Fechar modal é bloqueado enquanto o resultado permanece incerto; navegação/reload avisa pelo navegador. Rascunho/chave não são recuperados após recarregar/encerrar sessão; conferir histórico antes de iniciar nova operação.

## Compatibilidade legada

Movimentos vinculados à solicitação sem atendimento representam evidência anterior. Saídas válidas integrais por produto identificam entrega integral, inclusive itens repetidos. Uma saída parcial só é alocável com segurança quando existe um único item daquele produto. A leitura calcula status efetivo ATENDIDA/PARCIALMENTE_ATENDIDA sem reescrever status APROVADA original ou criar atendimento/movimento sintético. Um legado parcial inequívoco pode entregar apenas o restante.

Saída parcial agregada com itens repetidos, saída maior que demanda, produto/local inválido, divergência entre atendido e evidência, ou aprovação antiga sem saída vinculada nem metadados novos bloqueiam atendimento para conferência (409). A API retorna `compatibilidadeLegada = INCONSISTENTE`, aviso e quantidades derivadas nulas; não inventa distribuição. `RECONHECIDA` identifica legado inequívoco. Nada é backfilled automaticamente e nenhum histórico é apagado.

`GET /solicitacoes` e filtros antigos preservam o status registrado. Detalhe/documento operacional usa `GET /solicitacoes/{id}/operacao` e mostra status efetivo/aviso. Por isso a lista/filtro/dashboard de estados pode não refletir o estado efetivo de legados até conciliação formal futura. Não substituir status em massa sem revisão humana.

## Necessidade de compra

Ação humana calcula falta novamente sob locks e cria uma necessidade ABERTA com produto, item, solicitação, almoxarifado, responsável/data, quantidade e motivo FALTA_DE_ESTOQUE. Não aceita quantidade arbitrária enviada pelo cliente nem cria pedido. Sem falta positiva ou demanda aprovada/pendente, não cria.

Uma necessidade por item preserva fotografia e origem. Reposição/entrega posterior não apaga nem altera automaticamente quantidade/status. ATENDIDA e CANCELADA estão definidos para evolução; este bloco só cria ABERTA e consulta. Futuro módulo Compras deverá reavaliar demanda/saldo e definir transições explícitas autorizadas, consolidação de demandas e vínculo com pedidos/recebimentos, preservando origem e histórico. Não tratar ABERTA como ordem de compra automática nem promessa de falta ainda atual.

## Lista de Separação e mobile

Rota `/solicitacoes/{id}/lista-separacao` usa o snapshot da solicitação. Template `OperationalDocument` centraliza identidade, cabeçalho, metadados e rodapé; itens exibem código/material/unidade/solicitado/atendido/pendente e campo de conferência. Data de geração vem do navegador, distinta dos timestamps operacionais persistidos. Lista é fotografia no instante de consulta; não é documento arquivado/versionado nem prova de entrega. Ação contextual aparece em separação/parcial, sem papel obrigatório.

Impressão A4 via `window.print()`, sem sidebar/topbar, cabeçalho da tabela repetível e quebras protegidas. Navegador pode salvar PDF; não existe API/exportador dedicado de PDF. Teste automatizado verifica mídia print e gera PDF local ignorado para revisão. Documento permanece consultável em tela; formulários e necessidades funcionam a 1440/768/390px.

Assets oficiais `bes-logo-sidebar.png` e `bes-logo-full.png` continuam ausentes. Documento usa fallback textual **B&S Engenharia / Plataforma BES**, substituído pelo full oficial no próximo build quando adicionado, sem logo inventada. QR não implementado: existe rota identificável/deep link; renderização de QR e acesso autenticado continuam pendentes (RF212 parcial apenas na preparação; sem QR). Dados estruturados de atendimento preparam comprovante futuro, mas RF210 não é concluído.

## Banco e implantação

Scripts [MySQL](sql/atendimento-solicitacoes-mysql-manual.sql) e [PostgreSQL](sql/atendimento-solicitacoes-postgresql-manual.sql) são propostas manuais não executadas. Somente colunas nullable novas para legado, novas tabelas/FKs/UNIQUE/CHECK/índices; nenhum saldo/status regravado, DROP, DELETE ou TRUNCATE. Validar DDL real, tipos INT/FKs, ENUM/CHECK de status, collation de chave e constraints existentes antes de aplicar uma vez. MySQL >= 8.0.16 para CHECK; DDL tem commit implícito. PostgreSQL usa identity/double precision/timestamp(6), sem converter dados MySQL. Produção mantém ddl-auto=none. Não publicar backend novo sobre schema antigo sem aplicar versão homologada.

## Cobertura funcional oficial v1.4

Classificação conservadora do requisito inteiro, não só da arquitetura preparada:

| RF | Estado | Evidência ou limite |
|---|---|---|
| RF011 | ATENDIDO no núcleo | Saldo muda somente por movimentos confirmados; atendimento gera SAIDA |
| RF072 | ATENDIDO | Solicitação multi-item existente preservada |
| RF075 | ATENDIDO | Estado, responsável e instante de separação |
| RF078 | ATENDIDO | Entregas parciais e pendente por item |
| RF079 | ATENDIDO no registro da necessidade | Falta de demanda aprovada registrada; módulo Compras é futuro |
| RF150 | ATENDIDO neste fluxo | Rollback total e locks; não declara outros módulos futuros |
| RF195 | ATENDIDO | Quantidades respeitam unidades configuradas |
| RF202 | ATENDIDO | Parcial + encaminhamento humano da falta à necessidade |
| RF209 | ATENDIDO | Lista imprimível com códigos/unidades/quantidades/conferência |
| RF216 | ATENDIDO na base inicial | Template comum utilizado no primeiro documento |
| RF034 | PARCIALMENTE ATENDIDO | Necessidade criada, pedido/compra ainda não |
| RF053, RF054, RF148 | PARCIALMENTE ATENDIDO | Responsáveis/instantes/contexto preservados; identidade autenticada e trilha transversal faltam |
| RF055 | PARCIALMENTE ATENDIDO | Filtros reais, sem OS/setor/patrimônio em todo produto |
| RF060 | PARCIALMENTE ATENDIDO | Impressão/salvar PDF pelo navegador; exportação dedicada/Excel faltam |
| RF070, RF071 | PARCIALMENTE ATENDIDO | Web material/mobile; sem PWA/ferramentas/OS/área |
| RF073, RF074 | PARCIALMENTE ATENDIDO | Aprovação explícita; sem hierarquia/perfil, rejeição ainda sem responsável/instante/justificativa |
| RF169 | PARCIALMENTE ATENDIDO | Dados estruturados e lista no fluxo; demais registros/documentos faltam |
| RF193, RF201 | PARCIALMENTE ATENDIDO | Catálogo único reutilizado, compras/recebimento/custos futuros |
| RF203 | PARCIALMENTE ATENDIDO | Item/solicitação/produto/local rastreados; OS/centro de custo ainda não modelados |
| RF204 | PARCIALMENTE ATENDIDO | Unidades/fracionamento existente; conversões futuras |
| RF205, RF206, RF207, RF208 | PARCIALMENTE ATENDIDO | Primeiro documento identificado/contextual; demais documentos, logo oficial e OS/CC pendentes |
| RF213, RF214, RF215 | PARCIALMENTE ATENDIDO | Impressão e mobile deste fluxo/confirmação explícita; PDF dedicado e políticas de assinatura/perfil futuras |
| RF212 | PARCIALMENTE ATENDIDO na preparação | Deep link disponível; QR não implementado, acesso autenticado pendente |
| RF217, RF218 | PARCIALMENTE ATENDIDO | Documentos avaliados e ação contextual neste módulo; módulos futuros não implementados |
| RF076, RF077, RF080 | NÃO ATENDIDO | Notificações e retirante terceiro não entregues |
| RF210, RF211 | NÃO ATENDIDO | Comprovante e documento da necessidade não entregues |

## Validação

Baseline: 135 backend, 32 Node e 20 Playwright. Testes anteriores preservados; expectativas antigas de débito na aprovação foram alteradas explicitamente ou passaram a realizar atendimento confirmado antes de verificar saída. Novos testes cobrem contratos HTTP, total/parcial, unidades, falta, rollback multi-item, idempotência, legado e concorrência com manual/transferência. E2E usa exclusivamente H2 isolado porta 8081 e vite preview. Resultados finais registrados após execução na seção abaixo.


## Resultado final da retomada — 04/10/2026

A retomada preservou o backend já iniciado (entidades, DTOs, serviços, endpoints e aprovação separada da saída), sem reset/recriação. Foram concluídos frontend, documento, scripts e documentação, e resolvidas as verificações restantes: leitura da necessidade após lock, ordem comum de locks das operações manuais, testes de concorrência e sincronização do E2E com a resposta do atendimento. Não foram removidos testes anteriores; a mudança de expectativa do débito na aprovação é explícita.

| Verificação | Resultado final |
|---|---|
| `.\mvnw.cmd clean test` | BUILD SUCCESS; 179 testes, 0 failures, 0 errors, 0 skipped; 54.471 s |
| `npm.cmd run build` | Sucesso; Vite; 13.54 s |
| `npm.cmd test` | 47 testes, 47 pass, 0 fail, 0 skipped |
| `npm.cmd run test:e2e` | 25 passed (1.7m), 1 worker, retries 0; H2 + vite preview |
| `git diff --check` | Exit code 0, sem saída/erros |

Ampliação sobre baseline: +44 backend, +15 Node, +5 Playwright. Cobertura E2E inclui autorização sem saída, separação, entrega parcial, falta, necessidade confirmada, impressão, reposição, conclusão, histórico/movimentos, resposta perdida com mesma chave e bloqueio de double click, saldo alterado por outra operação e 768/390px. As suítes anteriores cobrem também 1440px e regressões do catálogo/estoque/transferências.

Revisão visual: formulário mobile e documento tablet inspecionados; PDF de teste renderizado e inspecionado em uma página A4 (~595,92 x 842,88 pt), sem cortes/sobreposição/sidebar. Artefatos locais de QA permanecem ignorados; nenhum PDF ou screenshot entra no conjunto de arquivos do bloco. Aviso Node NO_COLOR/FORCE_COLOR é de apresentação do runner, sem falha de testes.

Branch `feature/bes-frontend`; HEAD continua `8eb9e1e984526f9c5877b8d7b2e5b74589ea1e17`. `main` continua `0137ddbddf403f8331d1a3f6d632428f7614ee15`. Sem commit, push, merge, staging, reset/restore/clean ou acesso ao MySQL. Schema externo não aplicado e configuração de produção não alterada. Working tree contém as alterações válidas do Bloco 3.

Riscos/pendências antes de produção: homologar scripts/DDL real e collation de chave; revisar legados ambíguos; adaptar consumidores antigos da aprovação; implementar identidade autenticada/autorização antes de exposição pública; conferir histórico após reload com resultado incerto, pois rascunhos não são duráveis entre sessões. A configuração de produção preexistente contém credenciais locais versionadas e foi preservada conforme escopo; sua externalização/rotação precisa de rodada própria, sem reproduzir valores na documentação. Nenhuma nova credencial/API key foi adicionada neste bloco.

Necessidades permanecem fotografias ABERTAS após reposição; transições/compras/consolidação futuras exigem regra explícita. Lista é fotografia de consulta, não comprovante assinado/versionado; produto/nome/unidade são referências cadastrais atuais. QR, comprovante, documento da necessidade, exportação PDF dedicada, logo oficial, OS/centro de custo e autorização completa continuam pendentes conforme matriz. Os filtros antigos de status conservam status registrado dos legados, enquanto detalhe usa o efetivo.

**Pronto para revisão e, após aprovação, commit do Bloco 3.** Não representa autorização de deployment nem conclusão dos módulos futuros. Próximo passo recomendado: revisão operacional e homologação do schema em ambiente isolado; depois o próximo bloco do núcleo (contexto Obra/OS/Centro de Custo e regras de acesso), preservando a ponte de necessidade para Compras.

## Inventário de arquivos da rodada

`git status`: 23 arquivos modificados, 28 novos, nenhum staged; nenhuma exclusão. `git diff --stat` de arquivos rastreados: 23 arquivos, 280 inserções e 119 exclusões (remoção do débito antigo e transferência do detalhe para componente próprio; funcionalidades preservadas). Arquivos novos não entram nesse stat até staging.

### Arquivos alterados

- [docs/README.md](../docs/README.md).
- [docs/api.md](../docs/api.md).
- [frontend/README.md](../frontend/README.md).
- [frontend/package.json](../frontend/package.json).
- [frontend/src/App.jsx](../frontend/src/App.jsx).
- [frontend/src/api/client.js](../frontend/src/api/client.js).
- [frontend/src/components/AppShell.jsx](../frontend/src/components/AppShell.jsx).
- [frontend/src/components/Brand.jsx](../frontend/src/components/Brand.jsx).
- [frontend/src/components/ui.jsx](../frontend/src/components/ui.jsx).
- [frontend/src/pages/Dashboard.jsx](../frontend/src/pages/Dashboard.jsx).
- [frontend/src/pages/Operations.jsx](../frontend/src/pages/Operations.jsx).
- [frontend/src/styles.css](../frontend/src/styles.css).
- [frontend/src/utils/operations.js](../frontend/src/utils/operations.js).
- [frontend/tests/frontend.spec.js](../frontend/tests/frontend.spec.js).
- [src/main/java/br/com/almoxarifado/model/ItemSolicitacao.java](../src/main/java/br/com/almoxarifado/model/ItemSolicitacao.java).
- [src/main/java/br/com/almoxarifado/model/Movimentacao.java](../src/main/java/br/com/almoxarifado/model/Movimentacao.java).
- [src/main/java/br/com/almoxarifado/model/Solicitacao.java](../src/main/java/br/com/almoxarifado/model/Solicitacao.java).
- [src/main/java/br/com/almoxarifado/model/StatusSolicitacao.java](../src/main/java/br/com/almoxarifado/model/StatusSolicitacao.java).
- [src/main/java/br/com/almoxarifado/repository/ItemSolicitacaoRepository.java](../src/main/java/br/com/almoxarifado/repository/ItemSolicitacaoRepository.java).
- [src/main/java/br/com/almoxarifado/service/EstoqueService.java](../src/main/java/br/com/almoxarifado/service/EstoqueService.java).
- [src/main/java/br/com/almoxarifado/service/SolicitacaoService.java](../src/main/java/br/com/almoxarifado/service/SolicitacaoService.java).
- [src/test/java/br/com/almoxarifado/controller/BackendOperacionalTests.java](../src/test/java/br/com/almoxarifado/controller/BackendOperacionalTests.java).
- [src/test/java/br/com/almoxarifado/service/FluxoSolicitacaoTests.java](../src/test/java/br/com/almoxarifado/service/FluxoSolicitacaoTests.java).

### Arquivos criados

- [docs/atendimento-solicitacoes.md](../docs/atendimento-solicitacoes.md).
- [docs/sql/atendimento-solicitacoes-mysql-manual.sql](../docs/sql/atendimento-solicitacoes-mysql-manual.sql).
- [docs/sql/atendimento-solicitacoes-postgresql-manual.sql](../docs/sql/atendimento-solicitacoes-postgresql-manual.sql).
- [frontend/src/components/OperationalDocument.jsx](../frontend/src/components/OperationalDocument.jsx).
- [frontend/src/pages/PurchaseNeeds.jsx](../frontend/src/pages/PurchaseNeeds.jsx).
- [frontend/src/pages/RequestDetails.jsx](../frontend/src/pages/RequestDetails.jsx).
- [frontend/src/pages/SeparationList.jsx](../frontend/src/pages/SeparationList.jsx).
- [frontend/src/utils/fulfillment.js](../frontend/src/utils/fulfillment.js).
- [frontend/tests/fulfillment.spec.js](../frontend/tests/fulfillment.spec.js).
- [frontend/tests/fulfillment.test.js](../frontend/tests/fulfillment.test.js).
- [src/main/java/br/com/almoxarifado/controller/AtendimentoSolicitacaoController.java](../src/main/java/br/com/almoxarifado/controller/AtendimentoSolicitacaoController.java).
- [src/main/java/br/com/almoxarifado/controller/NecessidadeCompraController.java](../src/main/java/br/com/almoxarifado/controller/NecessidadeCompraController.java).
- [src/main/java/br/com/almoxarifado/dto/AtendimentoInput.java](../src/main/java/br/com/almoxarifado/dto/AtendimentoInput.java).
- [src/main/java/br/com/almoxarifado/dto/AtendimentoResponse.java](../src/main/java/br/com/almoxarifado/dto/AtendimentoResponse.java).
- [src/main/java/br/com/almoxarifado/dto/NecessidadeCompraInput.java](../src/main/java/br/com/almoxarifado/dto/NecessidadeCompraInput.java).
- [src/main/java/br/com/almoxarifado/dto/NecessidadeCompraResponse.java](../src/main/java/br/com/almoxarifado/dto/NecessidadeCompraResponse.java).
- [src/main/java/br/com/almoxarifado/dto/OperacaoSolicitacaoResponse.java](../src/main/java/br/com/almoxarifado/dto/OperacaoSolicitacaoResponse.java).
- [src/main/java/br/com/almoxarifado/model/AtendimentoSolicitacao.java](../src/main/java/br/com/almoxarifado/model/AtendimentoSolicitacao.java).
- [src/main/java/br/com/almoxarifado/model/ItemAtendimentoSolicitacao.java](../src/main/java/br/com/almoxarifado/model/ItemAtendimentoSolicitacao.java).
- [src/main/java/br/com/almoxarifado/model/NecessidadeCompra.java](../src/main/java/br/com/almoxarifado/model/NecessidadeCompra.java).
- [src/main/java/br/com/almoxarifado/model/StatusNecessidadeCompra.java](../src/main/java/br/com/almoxarifado/model/StatusNecessidadeCompra.java).
- [src/main/java/br/com/almoxarifado/repository/AtendimentoSolicitacaoRepository.java](../src/main/java/br/com/almoxarifado/repository/AtendimentoSolicitacaoRepository.java).
- [src/main/java/br/com/almoxarifado/repository/ItemAtendimentoSolicitacaoRepository.java](../src/main/java/br/com/almoxarifado/repository/ItemAtendimentoSolicitacaoRepository.java).
- [src/main/java/br/com/almoxarifado/repository/NecessidadeCompraRepository.java](../src/main/java/br/com/almoxarifado/repository/NecessidadeCompraRepository.java).
- [src/main/java/br/com/almoxarifado/service/AtendimentoSolicitacaoService.java](../src/main/java/br/com/almoxarifado/service/AtendimentoSolicitacaoService.java).
- [src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java](../src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java).
- [src/main/java/br/com/almoxarifado/service/QuantidadesOperacionais.java](../src/main/java/br/com/almoxarifado/service/QuantidadesOperacionais.java).
- [src/test/java/br/com/almoxarifado/service/AtendimentoSolicitacaoTests.java](../src/test/java/br/com/almoxarifado/service/AtendimentoSolicitacaoTests.java).
