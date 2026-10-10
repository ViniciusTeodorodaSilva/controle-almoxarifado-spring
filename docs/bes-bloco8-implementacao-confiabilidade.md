# BES — B8 Confiabilidade: implementação e evidências

Data: 09/10/2026. Rodada autorizada: implementação B8, commit separado do plano existente, sem push e sem commit do código B8. Este relatório não promove a v1.5 nem autoriza B9, produção ou banco externo.

## Checkpoint e autorização

- Branch conferida: `feature/bes-frontend`.
- HEAD inicial: `d732f67d5a23248c501c99f6a58de785faa37238`.
- Estado inicial: somente `docs/bes-plano-executivo-desenvolvimento-pos-v1_5.md` não rastreado; staging vazio.
- Lidos AGENTS, plano executivo, regras de segurança/autorização, auditoria B5, serviços/controladores/repositórios e testes dos fluxos afetados. A v1.4 foi consultada diretamente, incluindo RF153–155 e RNF001.
- Verificador do plano confirmou 218 RFs, 12 RNFs, 56 capacidades, 10 DP, 21 PR, 41 grupos, 23 blocos e grafo sem ciclos; redações e links locais preservados.
- Adicionado individualmente somente o plano; conferidos staging, whitespace e escopo.
- Commit documental: `b76d66cfdf808fc8c408b1672250cc073d376ac0`, `docs: registra plano executivo de desenvolvimento BES pos-v1.5`.
- Nenhum push nesta rodada. Código e relatório B8 permanecem fora do staging.

## Código implementado

### Precisão no contrato legado

Entradas/saídas manuais, transferência e ponte física EPI calculam saldo em BigDecimal e validam que o resultado retorna exatamente ao contrato DOUBLE atual. Uma quantidade que não altera o saldo ou exige arredondamento é recusada; saldo negativo não é permitido. A agregação/subtração compartilhada por atendimento e necessidades também rejeita perda de precisão. Compras já possuía verificação equivalente em `ComprasViews.decimal`, preservada.

Foi removida a tolerância por epsilon da validação de saldo EPI e o zeramento aproximado da ponte física. Não foram normalizados saldos históricos nem atribuídos contextos/contadores fictícios.

No frontend, a conversão de texto para Number é conferida antes do envio nos fluxos que usam `quantityError`; valores como `9007199254740993` e `0.10000000000000001` são recusados. Soma de itens do atendimento e comparação do saldo EPI usam inteiros escalados, sem epsilon. A consolidação de necessidades para compra conserva dígitos e frações, substituindo o arredondamento anterior de 15 dígitos. Total não representável é recusado com erro tratado pelo carregamento do formulário.

**Limite explícito:** isso é proteção no contrato existente, não migração integral dos agregados/DTOs para DECIMAL nem garantia de domínio quantitativo arbitrário. Há conversões Number remanescentes em contratos de compra/recebimento. A política integral exige banco alvo, escala por unidade, arredondamento e legado definidos, conforme item B8 do plano.

### Idempotência, concorrência e transação

`POST /transferencias` e `PUT /estoques/entrada|saida` aceitam chave persistida, fingerprint SHA-256 do conteúdo e índice único. A chave aceita 16–100 caracteres ASCII definidos no contrato API. Mesmo conteúdo retorna a operação existente; outro conteúdo conflita com 409. Fingerprint da transferência ordena produtos, normaliza números/observação e distingue ausência de observação de texto literal.

O lock de Produto continua precedendo estoque. Transferência mantém produtos crescentes e pares produto/almoxarifado ordenados. A rechecagem da chave após o lock usa leitura pessimista atual, sem depender de snapshot antigo. A primeira consulta da transferência é apenas um atalho para replay já existente; nenhuma reserva ausente é bloqueada antes dos produtos.

Movimento, saldo, chave/hash, itens e auditoria de sucesso pertencem à mesma transação. A auditoria desses comandos agora é explícita, para replay não gerar outro evento de sucesso. Falha ao gravar movimento ou auditoria reverte o conjunto. Não há retry automático do backend.

Replay manual devolve o saldo posterior da operação original em objeto de resposta separado, sem salvar esse saldo novamente. GET estoque continua sendo a consulta do saldo atual. Nomes e limites associados podem refletir o cadastro atual; não se promete reprodução byte a byte de todo o JSON histórico.

As telas mantêm chave e conteúdo em memória durante resultado incerto, bloqueiam edição e oferecem consulta explícita da mesma tentativa. Saída já efetivada pode ser consultada mesmo se esgotou o saldo; a consulta não passa novamente pela validação local de disponibilidade anterior ao primeiro envio. Fechar/recarregar a tela perde essa tentativa local: consultar histórico antes de iniciar outra. Nenhum dado de sessão/segredo foi persistido no navegador.

**Compatibilidade:** clientes sem header mantêm o contrato anterior e não recebem garantia de replay. Obrigatoriedade universal do header e retirada do caminho legado não foram impostas silenciosamente.

### Erros e segurança

Conflitos otimistas entram na mesma resposta estruturada 409 de conflitos de persistência. Erros inesperados permanecem 500 genéricos; o log inclui requestId validado, classe da exceção e classe da causa, sem mensagem SQL, payload ou segredo.

Nenhuma rota nova/pública foi criada. HTTP/service authorities, sessão, CSRF, CORS e configuração externa foram preservados. Os overloads que aceitam chave declaram `@PreAuthorize`; há regressões de autorização direta no service e CSRF no comando HTTP. Não foram alterados perfis, dados reais ou regras de custódia/contexto.

### Contratos de schema

Os scripts manuais B8/V008_01 expandem `transferencia_estoque` e `movimentacao` com chave/hash nullable e índices únicos. Linhas antigas permanecem NULL. Chaves têm comparação de caixa explícita no SQL nativo (`ascii_bin` MySQL / `C` PostgreSQL). Não há backfill nem aplicação automática: `ddl-auto=none` permanece.

| Script alternativo | SHA-256 |
|---|---|
| `sql/b8-transferencia-idempotencia-mysql-manual.sql` | `465b0b2e0205bcb56fa8e41fab036836fde0705ed448050fc11f1e763c3ea0ec` |
| `sql/b8-transferencia-idempotencia-postgresql-manual.sql` | `ffc01e9d048d610cdf78be49d7bcde60a6e062ad816421c694bf65622d522a0f` |

Os testes de contrato usam tabelas mínimas legadas em H2 nos dois modos, verificando expansão, preservação das linhas/NULL e unicidade. A sintaxe de collation nativa é removida explicitamente na adaptação para H2; não foi ensaiada contra os motores reais. Não se declara homologação nativa, baseline integral de schema, ferramenta de migrations escolhida ou restore executado. MySQL DDL parcial não deve ser reaplicado ignorando erros. O script do banco escolhido precisa de revisão/backup/homologação antes do uso do binário contra schema externo.

A distinção entre snapshot consistente e leitura com lock foi conferida na documentação primária de [leituras consistentes MySQL](https://dev.mysql.com/doc/refman/8.4/en/innodb-consistent-read.html) e [leituras com lock](https://dev.mysql.com/doc/refman/8.4/en/innodb-locking-reads.html). Essa fundamentação não substitui ensaio de isolamento, gap locks e deadlocks no ambiente escolhido.

## Investigação do HTTP 500 B5

O registro histórico não fornece uma causa técnica demonstrada para o incidente. A auditoria B5 contém um evento separado de `NoClassDefFoundError` em saída de compilação compartilhada e dificuldades de orquestração de testes; não se atribuiu automaticamente o HTTP 500 a esses eventos ou à correção anterior do H2.

Nesta rodada:

1. Executados os 69 testes existentes de Obras, incluindo 30 rodadas de ativação/detalhe/resumo simultâneos com verificação de um evento de status.
2. Adicionado cenário de renovação das conexões Hikari, alternando transições válidas ATIVA/SUSPENSA e consultando detalhe/resumo de Obra, Centro de Custo e OS em cinco rodadas: 35 requisições MockMvc com HTTP 200.
3. Executadas jornadas Playwright B5 em H2 novo, incluindo criação, ativação pela interface, contextos, pedidos multiobra, documentos e perfis.

O HTTP 500 não foi reproduzido nesses cenários. **Não há correção de causa histórica alegada.** Diagnóstico adicional depende de rota/ação, estado de dados e evidência técnica do incidente original; o aceite do risco histórico permanece pendente. Não houve banco externo, redução de segurança ou alteração de timeout/retry para facilitar a aprovação.

## Testes e evidências

Validação sequencial: backend → Node → build → H2/preview → Playwright. Antes de compilar novamente, as instâncias locais H2/preview desta rodada foram identificadas e encerradas; nenhuma suíte Maven/build rodou enquanto o navegador dependia da saída em reconstrução.

| Execução | Resultado |
|---|---|
| Quatro regressões quantitativas antes da correção | Vermelho: 3 falhas e 1 erro; perda de saldo/resíduo e saída decimal rejeitada comprovados |
| Estoque/transferência + Obras inicial | 117 testes aprovados |
| Idempotência/EPI intermediário | 1 falha: tipo de erro EPI mudou indevidamente; corrigido preservando Conflito/409 |
| Direcionados EPI/estoque/erros/segurança | 202 aprovados |
| Integral inicial com renovação B5 nova | 584, 1 falha do teste: tentou ATIVA → ATIVA; corrigido para transições válidas, sem mudar a regra |
| Integral após agregação exata | 587 aprovados |
| Integral com rollback de auditoria/RBAC/CSRF novos | 592 aprovados |
| Integral após leitura atual de replay | 592 aprovados; zero falhas, erros ou skips |
| Contrato de migration final adaptado H2 | 2 aprovados; sem validação de collation/motor nativos |
| Node inicial | 126/127; expectativa antiga aceitava excesso por epsilon, substituída por rejeição explícita |
| Node final | 131/131; zero falhas/skips |
| Build final | Aprovado; CSS premium preservado |
| Playwright integral inicial | 110/110, incluindo 14 premium; 3,8 minutos |
| Playwright integral final | 110/110 em 3,1 minutos; `.last-run.json`: passed, nenhuma falha; H2 novo e binário/build finais |

Exemplos das novas regressões: 0,1 + 0,2, esgotamento decimal, operação sem variação representável, soma grande que perde unidade, replay após movimento posterior, conteúdo conflitante, duas threads com mesma chave, rollback de chave/movimentos/saldos/auditoria, authority no overload, CSRF e resposta perdida na transferência/saída total.

Logs locais em `%TEMP%`: `bes-b8-quantidades-red.log`, `bes-b8-quantidades-obras.log`, `bes-b8-idempotencia-epi.log`, `bes-b8-direcionados.log`, `bes-b8-backend-integral.log`, `bes-b8-backend-final.log`, `bes-b8-backend-validado.log`, `bes-b8-backend-fechamento.log`, `bes-b8-migration-final.log`, `bes-b8-node-fechamento.log`, `bes-b8-build-fechamento.log`, `bes-b8-playwright.log`, `bes-b8-playwright-final.log`. XMLs em `target/surefire-reports`; resultado/capturas Playwright em `frontend/test-results`, ignorados pelo Git.

Foram inspecionadas visualmente duas capturas da primeira execução: `premium-p0-round2/drawer-closed-1440.png` e `drawer-open-390.png`. Demais capturas têm asserts automatizados; não se alega inspeção manual de todas. Não houve modificação de CSS, identidade visual ou logo.

O lançamento com `ExecutionPolicy Bypass` foi rejeitado pela revisão automática com razão genérica de política. A alternativa concluída foi execução direta Java com classpath de teste e H2 em memória, sem alterar a política do PowerShell. Credenciais usadas são exclusivamente fixtures fictícias dos testes e não foram impressas. H2 permanece dependência somente de teste.

## Pendências que impedem declarar B8 integral concluído

| Pendência | Estado e efeito |
|---|---|
| Banco alvo, escala por unidade, arredondamento e legado | O plano registra decisões pendentes. Solicitada definição durante a execução; não inventada. Bloqueia migração quantitativa integral coordenada e seus DTOs. |
| Baseline/migrations de todo o schema e reconciliação | Apenas expansão idempotente implementada/testada em H2. Sem inventário/baseline executável integral, conversão de dados ou totais reais reconciliados. |
| B8-C homologação | Banco externo expressamente proibido nesta rodada; locks/isolamento/collation/DDL/restore nativos não comprovados. |
| Incidente histórico B5 | Não reproduzido nos cenários executados; causa e aceite formal do risco continuam pendentes. |
| Compatibilidade de comandos antigos | Header opcional, proteção condicionada à chave. Revisar política de obrigatoriedade antes de expansão comercial. |
| Tentativa após fechar/recarregar | Chave/conteúdo permanecem apenas no formulário em memória; histórico é a alternativa após perda do estado local. |
| Foco resize residual | Cenários premium existentes passaram; nenhum defeito novo reproduzido nem correção de foco alegada. |

Não se promove cobertura RF/RNF nem se inicia B9 para encobrir essas pendências. O recorte de código implementado é concreto e auditável; a passagem de gate integral depende dos itens acima.

## Inventário B8 fora do staging

Arquivos modificados:

- `docs/api.md`
- `frontend/src/api/client.js`
- `frontend/src/pages/StockMovementForm.jsx`
- `frontend/src/pages/TransferForm.jsx`
- `frontend/src/utils/epis.js`
- `frontend/src/utils/fulfillment.js`
- `frontend/src/utils/operations.js`
- `frontend/src/utils/purchases.js`
- `frontend/tests/api.test.js`
- `frontend/tests/epis.test.js`
- `frontend/tests/fulfillment.test.js`
- `frontend/tests/operations.spec.js`
- `frontend/tests/operations.test.js`
- `frontend/tests/purchases.test.js`
- `frontend/tests/stockIntelligence.spec.js`
- `src/main/java/br/com/almoxarifado/controller/EstoqueController.java`
- `src/main/java/br/com/almoxarifado/controller/RegraNegocioExceptionHandler.java`
- `src/main/java/br/com/almoxarifado/controller/TransferenciaEstoqueController.java`
- `src/main/java/br/com/almoxarifado/epi/EpiService.java`
- `src/main/java/br/com/almoxarifado/model/Movimentacao.java`
- `src/main/java/br/com/almoxarifado/model/TransferenciaEstoque.java`
- `src/main/java/br/com/almoxarifado/repository/MovimentacaoRepository.java`
- `src/main/java/br/com/almoxarifado/repository/TransferenciaEstoqueRepository.java`
- `src/main/java/br/com/almoxarifado/service/EstoqueService.java`
- `src/main/java/br/com/almoxarifado/service/QuantidadesOperacionais.java`
- `src/main/java/br/com/almoxarifado/service/TransferenciaEstoqueService.java`
- `src/test/java/br/com/almoxarifado/obras/ObrasTests.java`
- `src/test/java/br/com/almoxarifado/service/EstoqueInteligenteTransferenciaTests.java`

Arquivos novos:

- `docs/bes-bloco8-implementacao-confiabilidade.md`
- `docs/sql/b8-transferencia-idempotencia-mysql-manual.sql`
- `docs/sql/b8-transferencia-idempotencia-postgresql-manual.sql`
- `frontend/src/utils/quantities.js`
- `src/test/java/br/com/almoxarifado/config/MigrationIdempotenciaTests.java`
- `src/test/java/br/com/almoxarifado/controller/ErrosConfiabilidadeTests.java`
- `src/test/java/br/com/almoxarifado/service/QuantidadesConfiabilidadeTests.java`

## Git e encerramento

HEAD final conferido nesta rodada: `b76d66cfdf808fc8c408b1672250cc073d376ac0`, branch `feature/bes-frontend`, um commit documental local à frente de `origin/feature/bes-frontend`. Staging vazio. Working tree deliberadamente contém os 35 arquivos B8 acima (28 modificados e 7 novos), sem descarte. `git diff --check` aprovado; inventário comparado ao status Git, sem divergência. Os 18 XMLs somam 592 testes, zero falhas, erros ou skips. As instâncias locais H2/preview da validação final foram encerradas ao concluir.

v1.4 intacta: SHA-256 `f2c2289fd899834e9868b7102c869e4417fcff460b5d6c4c359bc9f52922865b`; nenhum diff da mestre. A v1.5 candidata também não foi alterada. Não houve commit de código, push, merge, deploy, banco externo ou B9.

**B8 PARCIAL — INFORMAR BLOQUEIOS.** Implementação disponível para revisão; migração quantitativa integral, homologação nativa e fechamento do incidente B5 continuam pendentes.

## Continuação autorizada sem contrato definitivo de escala

Preservados driver/configuração MySQL, schema e dados. Inspeção dos mapeamentos/scripts: quantidades operacionais DOUBLE, preço unitário DECIMAL(19,4), EPI DECIMAL(19,6); colunas reais não foram consultadas. Proposta curta e somente leitura em `sql/b8-precisao-preflight-mysql.sql`, sem definição automática de P/S ou execução.

Corrigidos HTTP 500 de conflitos JPA diretos (timeout/lock pessimista/otimista → 409 genérico) e auditoria duplicada no replay de atendimento/necessidade. Registros de sucesso explícitos e atômicos; regressões de rollback e leitura atual após lock. Cinco regressões demonstraram essas falhas antes da correção; isso não prova a causa do incidente histórico B5.

Estoque manual recebe BigDecimal na query e transferência no DTO/mapa, sem descarte antes da validação. Duas regressões demonstraram que `0.10000000000000001` era aceito após conversão silenciosa. Agora valor incompatível é recusado antes de escrever; não se arredonda. Sugestão de reposição usa subtração BigDecimal. Regressão preserva saldo legado com oito casas; schema/API de saída DOUBLE ainda exigem migração controlada para abandonar o contrato legado. Valores monetários continuam BigDecimal e suas regras existentes não foram modificadas.

Validação desta continuação: 269 direcionados; integral 603/603, zero falhas/erros/skips; Node 131/131 e build aprovados. Playwright integral: 110/110 aprovados em 8,7 minutos sobre H2 novo, incluindo os cenários premium e B5; processo retornou zero e relatório final sem testes falhos. Instâncias H2 e preview desta validação encerradas. Logs `%TEMP%/bes-b8-continuacao-{red,direcionados,backend,node,build,playwright}.log` e `bes-b8-api-precisao-red.log`.

Além do inventário anterior, modificados `src/main/java/br/com/almoxarifado/dto/TransferenciaInput.java`, os repositórios `AtendimentoSolicitacaoRepository`/`NecessidadeCompraRepository`, os serviços `AtendimentoSolicitacaoService`/`NecessidadeCompraService`/`EstoqueInteligenteService` e `src/test/java/br/com/almoxarifado/service/AtendimentoSolicitacaoTests.java`; novo preflight SQL acima. Total atual: 43 arquivos (35 modificados, 8 novos), staging vazio, mesmo HEAD documental, nenhum commit/push/deploy nesta continuação. Escala/arredondamento/política de legado permanecem pendentes; trabalho independente dessas decisões foi executado.

## Auditoria técnica pré-commit B8

Classificação **A — pronto para commit parcial seguro**, sem autorização de implantação. Revisado o diff dos 43 arquivos; sem alterações pré-B8 não consolidadas no checkpoint, temporários ou indícios de secrets novos. Configuração, mestres e HEAD preservados; staging vazio e `git diff --check` aprovado.

Defeito reproduzido e corrigido nas duas telas: resposta perdida seguida de 409 durante consulta liberava edição e descartava a chave original. Resultado incerto agora conserva chave/conteúdo até confirmação, inclusive se a consulta falhar. Regressão Playwright falhou antes da correção; os dois cenários depois verificam três envios com uma chave e um conteúdo, apenas uma operação efetivada e saldo correto.

Nesta auditoria: Node 131/131, build e Playwright dos dois arquivos afetados 17/17 (1,2 min), sem banco real; instâncias de teste encerradas. Backend não alterado: reaproveitados e conferidos 603/603 e execução integral anterior de 110/110, sem alegar nova execução integral. Logs `%TEMP%/bes-b8-auditoria-{red,node,build,playwright}.log`. Os 43 arquivos do inventário continuam aptos para commit parcial; scripts SQL somente como propostas manuais não executadas. Precisão definitiva, homologação MySQL/colunas reais e causa histórica do B5 permanecem pendentes. Nenhum commit, push, merge, deploy ou B9.
