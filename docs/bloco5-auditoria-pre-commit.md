# Bloco 5 — auditoria final pré-commit

Data: 06/10/2026. Referência: [Documentação Mestre BES v1.4](Documentacao_Mestre_Plataforma_BES_v1_4.docx), [regras e matriz RF](obras-os-centros-custo.md), [API](api.md), [segurança](security.md), [sessão e autorização](autenticacao-autorizacao.md). Revisão incremental sobre o trabalho existente; não implementa novos módulos. Foram consultados também AGENTS.md, relatório anterior do Bloco 5, docs/compras-recebimento.md, docs/atendimento-solicitacoes.md e frontend/README.md. A inspeção confrontou regras com services, repositories, DTOs, mapeamentos, controllers, views, seletores e testes existentes.

Checkpoint inicial confirmado: feature/bes-frontend, HEAD 354252ab9e3d3ad60cf6ebb692c56b880cff70a5, 28 novos/48 modificados, staging vazio e diff check limpo. As evidências anteriores 385/86/60 são ponto de partida e não substituem a revalidação desta auditoria.

## Checkpoint da continuação após interrupção de limite

Retomada solicitada em 06/10/2026. Branch e HEAD conferidos novamente: `feature/bes-frontend` / `354252ab9e3d3ad60cf6ebb692c56b880cff70a5`. Estado encontrado: 77 arquivos candidatos (29 novos, 48 modificados), staging vazio; main em `0137ddbddf403f8331d1a3f6d632428f7614ee15`. Este relatório foi lido primeiro e o trabalho anterior foi preservado.

| Item | Status no início da continuação | Evidência e ação |
|---|---|---|
| Revisão funcional, segurança e scripts | CONCLUÍDO | Matriz abaixo e testes existentes; conferência incremental, sem reiniciar a auditoria. |
| Seleção invisível e N+1 de necessidades | CONCLUÍDO | Correções presentes em ContextSelector/client e EntityGraph; regressões preservadas. |
| Cinco regressões backend e dois E2E | CONCLUÍDO | Fontes e assertions conferidos; nenhuma remoção ou relaxamento. |
| Validação backend/Node/build/audit anterior | CONCLUÍDO | Evidência anterior preservada; reexecução integral exigida nesta continuação. |
| Playwright integral | PARCIAL | Última execução registrada: 61/62; teste B3 passou isolado; fechamento ainda pendente. |
| Secret scan e fechamento documental | PENDENTE | Repetir sobre todos os candidatos e concluir os relatórios após a suíte. |

Nesta continuação não foram necessárias novas correções de produto ou novas regressões. Foram relidos os RF061–069/071, RF203 e RF205–218 da mestre v1.4; mantém-se a cobertura incremental e as pendências da matriz funcional. Os checks foram retomados do fechamento, sem repetir a revisão visual manual já registrada.

Revalidação Maven: a primeira tentativa restrita falhou no wrapper antes de executar testes; cache local também negou leitura. A execução autorizada fora do sandbox do mesmo `mvnw.cmd clean test` terminou com BUILD SUCCESS e 390 testes. Os 13 XMLs conferidos somam zero falhas, erros e skips. Build e 86 testes Node passaram. A primeira consulta npm audit falhou por rede/cache restritos; a repetição autorizada retornou zero vulnerabilidades. Nenhum teste ou configuração de segurança foi alterado para contornar infraestrutura.

Os servidores H2/preview da auditoria anterior ainda ocupavam 8081/5173. A instância java foi identificada como BES, profile test e JDBC H2 em memória antes de ser encerrada. O novo launcher mantém somente H2 e fixtures fictícias; o preview existente serve o build atual. Readiness confirmado em `/auth/csrf` e `/api/auth/csrf` com HTTP 200 antes de iniciar Playwright. Nenhum banco externo foi consultado.

## Revalidação em 07/10/2026

Checkpoint atual: mesma branch/HEAD, 29 novos e 48 modificados, staging vazio. Auditoria anterior preservada; nova inspeção de ContextoService, EstruturaService, ResumoEstruturaService, mapeamentos, scripts, repositories, views e documentos. RF061–071, RF203 e RF205–218 relidos na mestre v1.4; classificações mantidas. Nenhuma nova correção funcional necessária.

Acrescentadas regressões: seis casos de Obra/OS SUSPENSA/CONCLUIDA/CANCELADA com rejeição de nova solicitação e leitura/resumo preservados (substituindo um caso anterior de suspensão), mais CC corporativo/filtros/resumos; aumento líquido de seis testes. O caso corporativo prova demanda sem Obra, demanda com Obra, contagens independentes e rejeição de CC diferente do definido na OS.

Maven completo inicial 390/390; após ampliação, 396/396, zero falhas/erros/skips. Validação final em cópia TEMP de src/.mvn/pom/wrapper: 157 arquivos comparados por SHA-256, zero divergências. Não se modificou target da instância H2 durante essa validação. Build/86 Node aprovados novamente; npm audit zero.

Primeiro Playwright desta revalidação: 61/62 em 5,5 minutos, todos os 15 casos B5 aprovados. Falha de compras anterior na leitura de CSRF pelo preview: trace confirma `net::ERR_NETWORK_CHANGED`; POST do pedido não enviado. Nenhuma alteração de timeout, assertion, retries ou proteção. Repetição integral com H2 novo após Maven.

Segunda execução: 61/62 em 5,3 minutos, todos os casos B5 aprovados. O caso anterior de rascunho recebeu 404 no HTML da rota às 07:49:24, durante a repetição de build concluída às 07:49:25: falha da orquestração da validação, que recriou dist enquanto preview o servia. Terceira execução integral com build estável e H2 novo, sem Maven/build concorrentes; nenhuma mudança no produto ou nos testes de navegador para contornar a falha.

Secret scan inicial desta rodada: 77 candidatos, 125 linhas com palavras de segurança em 14 arquivos, classificadas como documentação/configuração, sessão/CSRF e fixtures/testes; zero padrões fortes de tokens/chaves privadas e zero ocorrências do segredo histórico, comparado silenciosamente em memória. Nenhum valor impresso. Scan heurístico não certifica segurança de produção.

Captura mobile 390px inspecionada e PDF A4 da OS renderizado para inspeção visual: uma página, contexto legível, fallback B&S, sem sidebar/ações; impressão dos três tamanhos exercitada pelos testes.

## Achados e correções anteriores preservados

Ao selecionar Obra/OS/CC e pesquisar outro termo, a resposta paginada podia retirar o registro selecionado do select, deixando o campo visual sem a opção correspondente e conservando o ID em memória. ContextSelector agora consulta o ID selecionado ausente na página pela API autenticada, inclui a opção na lista e conserva sua identificação visível. A consulta respeita cancelamento e não altera authorities. Trocar Obra continua limpando OS e CC; o backend conserva a validação definitiva. Nenhuma regra de consumo, estrutura ou compra foi relaxada.

Segundo achado reproduzido: listagem contextual de necessidades fez 5 consultas para uma demanda e 16 para doze. O EntityGraph específico de filtrarContexto passou a carregar itemSolicitacao, solicitacao, produto/unidade, almoxarifado e responsável utilizados no DTO. Mantém batches de progresso e não altera o fetch global das entidades. A nova regressão compara uma/doze demandas e exige crescimento constante (no máximo uma consulta adicional).

## Oitenta pontos de revisão

| Nº | Ponto | Resultado / evidência |
|---|---|---|
| 1 | Branch | feature/bes-frontend; desenvolvimento vigente. |
| 2 | HEAD | 354252ab9e3d3ad60cf6ebb692c56b880cff70a5; preservado. |
| 3 | Working tree | Inicial: 76 candidatos, 28 novos/48 modificados; staging vazio. Trabalho anterior preservado. |
| 4 | Coerência Obra/OS/CC | ContextoService deriva Obra/default da OS e recusa outra Obra/CC e referências inexistentes. Testes positivos/negativos com H2 e API autenticada. |
| 5 | CC corporativo | Obra nullable é legítima; incluirGerais mantém centros corporativos disponíveis. Regressão adicional confirma filtro estrito/ampliado, demandas com/sem Obra, contagens independentes e rejeição de CC diferente do definido na OS. |
| 6 | Inativos e encerrados | Novos vínculos exigem Obra PLANEJADA/ATIVA, OS ABERTA/EM_ANDAMENTO e CC ativo. Seis regressões parametrizadas exercitam rejeição de novas solicitações nas situações SUSPENSA/CONCLUIDA/CANCELADA de Obra/OS, preservando leitura/resumo; CC inativo também coberto. |
| 7 | Histórico | Contexto congelado na solicitação e copiado na SAÍDA; necessidade/alocação derivam da origem persistida. |
| 8 | Alteração estrutural | Obra do CC e Obra/CC da OS imutáveis desde a criação; não há reclassificação por edição. |
| 9 | Renomeações | Mudanças descritivas em cadastro aberto não alteram snapshots históricos. |
| 10 | Solicitação contextual | Criação mantém contrato anterior e admite três IDs opcionais, validados no servidor. Geral/legado continuam null. |
| 11 | Atendimento parcial | Regressão adicional confirma duas entregas 3+5, dois movimentos e consumo total 8, sem multiplicar contagens dos três resumos. |
| 12 | Movimentações | SAÍDAs confirmadas recebem IDs e identificação histórica. Transferência e ENTRADA não entram como consumo. |
| 13 | Recebimento não é consumo | ENTRADA repõe saldo; solicitação continua APROVADA e consumo vazio até confirmação humana de atendimento. |
| 14 | Recebimentos parciais | Novo teste recebe 100 em parcelas 30/20/50, conserva três recibos e soma exatamente 100 no estoque. |
| 15 | Compra multiobra | Um pedido pode relacionar várias Obras por necessidades/alocações próprias; não existe contexto global do pedido. |
| 16 | Mesmo produto | Novo cenário A=60/B=40 consolida um produto/item de 100; ordem de entrada das alocações foi invertida para testar independência. |
| 17 | Parcial multiobra | Destinação por necessidadeId crescente, persistida por recibo: primeiras 30 e próximas 20 para A; parcela final 10 para A e 40 para B. Somas A=60/B=40, consumo vazio. |
| 18 | Compra geral | Quantidade paraEstoque sem necessidade/contexto é permitida; nenhum vínculo ou necessidade fictícios. |
| 19 | Compra mista | Alocação e sobra paraEstoque podem coexistir; contexto manual só classifica sobra. Recibo parcial não antecipa contexto de parcela manual futura. |
| 20 | Comprado/recebido/consumido | Compromisso comercial, entrada física e saída de material permanecem fatos distintos. Total integral do pedido não é apropriado a cada Obra. |
| 21 | Valorização | custoConsumido=null e aviso explícito; nenhum FIFO/custo médio/contabilidade novo. |
| 22 | Resumo Obra | Projeções escalares, contagens totais e agrupamento por produto/unidade; consumo confirmado, limite de relações informado. |
| 23 | Resumo OS | Mesmos agregados por ordemServicoId, com identificação legível da Obra/CC/responsável. |
| 24 | Resumo CC | Mesmo agregado por centroCustoId; centro corporativo não absorve todos os dados de qualquer Obra. |
| 25 | Dupla contagem | Pedidos usam count distinct; outros fatos são agregados separadamente, sem joins cruzados de coleções. Teste de entregas verifica os três resumos. |
| 26 | Filtros AND | Solicitações/necessidades/movimentos aplicam três dimensões juntas. Pedido exige a mesma origem manual ou mesma alocação, impedindo A+OSB em alocações diferentes. |
| 27 | N+1 | Cadastros usam paginação e campos escalares; resumos projeções/queries constantes. Integrações mantêm EntityGraph e batches de compras/necessidades, sem EAGER indiscriminado. Testes constantes com 12 OS. N+1 reproduzido em necessidades (5→16 consultas ao passar de uma para doze demandas), corrigido com EntityGraph específico e regressão de crescimento constante; não é benchmark de produção. |
| 28 | Código Obra concorrente | Código ASCII strip/uppercase, unique global; teste concorrente exige um vencedor e conflito controlado. |
| 29 | Código CC concorrente | Mesma normalização/unique e regressão concorrente. |
| 30 | Número OS concorrente | IDENTITY + número OS-ano-ID e unique; sem MAX+1, duas criações simultâneas têm números distintos. |
| 31 | Encerrar OS versus criar demanda | Nova integração concorrente compartilha Obra→CC→OS: exatamente uma operação vence; OS concluída não ganha demanda, demanda criada impede conclusão. |
| 32 | Mapeamentos | IDs escalares são donos; referências privadas LAZY/read-only/JsonIgnore geram FKs sem expor grafos. Embedded fora de associações; nullable conforme legado. |
| 33 | FKs | Referências de Obra/OS/CC, responsável e atores preservam integridade. IDs operacionais INT e atores Long/BIGINT. |
| 34 | Cascatas | Sem DELETE da estrutura, sem cascade/orphanRemoval para apagar histórico, sem ON DELETE CASCADE nos scripts. |
| 35 | Legado | Contexto desconhecido continua null; sem backfill. Contratos e testes anteriores permanecem. |
| 36 | Documentos reais | Ficha da OS, lista de separação e documentos de compra usam dados autenticados e contexto da origem real. |
| 37 | Documento multiobra | Alocações/destinações mostram contexto individual; parte manual só sobre quantidade real para estoque. |
| 38 | A4 e impressão | Template existente, print sem sidebar/ações, PDF do navegador e responsividade 1440/768/390; não existe exportador backend. |
| 39 | Logo oficial | PNGs oficiais ausentes; fallback textual B&S/BES mantido, nenhuma logo inventada. |
| 40 | QR | Não implementado; RF212 permanece pendente, sem endpoint público ou bypass de autenticação. |
| 41 | Novos endpoints | 17 handlers, 8 escritas; inventário global 102/44 revisado por teste dinâmico de segurança. |
| 42 | Autorização HTTP | ADMIN/GESTOR gerenciam; ALMOXARIFE/CONSULTA leem; nenhuma nova gestão de usuários concedida. |
| 43 | Service layer | PreAuthorize nas operações públicas de estrutura/resumo e nos serviços integrados; negativos exercitam chamada direta. |
| 44 | CSRF | Todas as novas escritas cobertas pelo inventário dinâmico; ausência rejeitada. Sessão/CSRF/CORS existentes preservados. |
| 45 | Mass assignment | DTOs estritos rejeitam campos internos e contexto forjado em alocação. Status só via transição explícita. |
| 46 | Ator autenticado | SecurityContext fornece Usuario/ator; responsável físico Funcionario distinto. Corpo/query/header não substituem identidade validada. |
| 47 | Troca de IDs/IDOR | Authority e coerência verificadas; leitura global dos quatro perfis é política anterior. Sem isolamento por usuário/Obra ou multitenancy neste bloco. |
| 48 | Rollback | Cadastro/status + auditoria transacionais. Novo teste força falha da auditoria da solicitação e exige ausência de demanda contextual e evento de sucesso. |
| 49 | Máquina de estados Obra | Transições explícitas; terminal não reabre/edita/repete. Encerramento bloqueia OS/demandas abertas. |
| 50 | Máquina de estados OS | Motivo para suspensão/cancelamento, datas do servidor, encerramento com demandas resolvidas, terminais imutáveis. |
| 51 | Seletores frontend | BUG encontrado: opção sumia na pesquisa/paginação, mas ID permanecia no estado. Corrigido com consulta protegida do ID e opção visível; trocar Obra limpa OS/CC. AbortSignal propagado na leitura. |
| 52 | Permissões frontend | Ações dependem de authorities; E2E dos três perfis e ciclo iniciado pelo GESTOR com cadastros por UI; recebimento/atendimento pelo ALMOXARIFE e consulta final pelo GESTOR, conforme authorities existentes. |
| 53 | Erros e dupla ação | Loading/vazio/401/403/CSRF seguem componentes e client existentes; busy bloqueia envio e criação incerta pede conferir listagem. Idempotência de atendimento/recebimento preservada. |
| 54 | Responsividade | Verificação dos três tamanhos em listas/detalhes/formulário/ficha; tabelas com rolagem interna. |
| 55 | Acessibilidade | Labels vinculados, selects nativos, teclado/foco/modal existentes mantidos. Testes usam roles/labels; sem certificação WCAG ou medição completa de contraste. |
| 56 | Preparação BI | Dimensões/fatos por granularidade documentados, incluindo Obra/OS/CC/produto/categoria/almoxarifado/período/fornecedor/pedido/solicitação; sem ETL/DW/Power BI real. |
| 57 | Scripts SQL | Comparação estática de três tabelas, IDs/atores, nomes/nullable, VARCHAR/CHECK, unique/FK/índices e oito colunas de snapshot nos três donos. Não executados; MySQL/PostgreSQL reais não homologados. |
| 58 | Secret scan | Varredura de todos candidatos com keywords e padrões; ocorrências classificadas por arquivo/linha sem imprimir valores. Resultado final no fechamento; credencial histórica continua comprometida até rotação autorizada. |
| 59 | Bugs encontrados | Seletor retinha ID invisível quando pesquisa/paginação removia opção; N+1 na listagem contextual de necessidades. Lacunas de evidência: GESTOR completo, encerramento concorrente, parcelas multiobra, entregas contextuais parciais e rollback contextual. |
| 60 | Correções | Mantém seleção visível via GET permissionado do cadastro e passa AbortSignal. Mantém limpeza de vínculos ao trocar Obra; sem mudança de permissão ou regra backend. EntityGraph de necessidades carrega referências do DTO em uma consulta. |
| 61 | Novas regressões | Cinco casos backend anteriores preservados (encerramento concorrente, 30/20/50 multiobra, 3+5 entrega, rollback contextual, N+1); dois E2E anteriores preservados (ciclo GESTOR e seleção após pesquisa/troca). Nesta rodada, seis casos de Obra/OS não operacional substituem um caso de suspensão, e um caso corporativo é acrescentado: aumento líquido de seis testes backend. |
| 62 | Backend final | 396 = 328 anteriores + 68 de Obras, zero falhas/erros/skips; clean test integral isolado em 07/10/2026, exit 0 e XMLs conferidos. |
| 63 | Node final | 86 testes aprovados; 72 anteriores + 14 de contexto. |
| 64 | Playwright final | 62/62 aprovados em 3,7 minutos em 07/10/2026; .last-run.json com status passed e failedTests vazio, workers=1/retries=0. Detalhe do wrapper PowerShell no fechamento. |
| 65 | Build | Build frontend aprovado sobre a correção final; sem dependências novas. |
| 66 | npm audit | Zero vulnerabilidades em todas as severidades; JSON conferido e exit 0. |
| 67 | git diff --check | Limpo; revisão final será repetida após o fechamento documental. |
| 68 | Git status | 77 candidatos, 29 novos/48 modificados, todos unstaged, staging vazio; checkpoint preservado. |
| 69 | Arquivos novos | 29, incluindo este relatório; inventário final abaixo. |
| 70 | Arquivos modificados | 48; sem descartar alterações preexistentes. |
| 71 | Riscos | Gate produção pendente: rotação, configuração externa/HTTPS, homologação schema/drivers, backups e observabilidade. Sem custo consumido, isolamento por Obra, logos/QR; relações limitadas a 100. |
| 72 | Classificação RF | Nenhuma classificação ampliada nesta auditoria. RF061/203 no fluxo atual; RF062/064/212 pendentes; RF063/065–069/071 e documentação/BI mantêm limites da matriz. |
| 73 | MySQL | Nenhum acesso; somente H2 de teste. |
| 74 | Produção | Nenhum acesso ou publicação. |
| 75 | Main | Referência preservada 0137ddbddf403f8331d1a3f6d632428f7614ee15. |
| 76 | Commit | Nenhum commit, staging vazio. |
| 77 | Push | Nenhum push. |
| 78 | Merge | Nenhum merge/rebase/reset/restore/clean Git/stash/reescrita histórica. |
| 79 | Credenciais reais | Nenhuma credencial real adicionada, impressa, testada ou rotacionada. Fixtures de teste e sessões apenas em memória. |
| 80 | Recomendação | COMMITAR o Bloco 5 após revisão humana deste diff, mantendo o gate separado de produção. Nenhum commit executado nesta rodada. |

## Fechamento das validações

Fechamento em 07/10/2026: Maven `clean test` integral, BUILD SUCCESS, 396 testes, zero falhas/erros/skips, 13 XMLs conferidos. Build aprovado, 86 Node aprovados, npm audit zero vulnerabilidades. Playwright final: 62/62 em 3,7 minutos; `.last-run.json` contém `status: passed` e nenhuma falha. O shell de redirecionamento retornou 1 por `NativeCommandError` associado ao warning Node de NO_COLOR/FORCE_COLOR; não se atribui exit 0 ao wrapper, nem se confunde esse sinal com falha de assertion. Resultado integral comprovado pelo reporter e pelo arquivo do runner.

Evidências locais em TEMP: `bes-bloco5-maven-final.log`, `bes-bloco5-build-final.log`, `bes-bloco5-node-final.log`, `bes-bloco5-npm-audit-final.json`, `bes-bloco5-playwright-third.log`; relatórios Maven em `bes-bloco5-audit-20261007/target/surefire-reports`. Trace e captura/PDF de teste são ignorados em frontend/test-results. Terceira suíte foi executada somente após readiness 200 direto/proxy e sem build/Maven em paralelo.

Sem alteração de comportamento ou classificação RF nesta rodada; regressões adicionais fecharam lacunas de evidência. Correções anteriores do seletor e N+1 preservadas. Inventário final: 77 candidatos = 29 novos + 48 modificados; staging vazio. Branch/HEAD/main preservados. Nenhum MySQL/PostgreSQL real, produção, SQL manual, commit, push, merge ou rotação. Credencial histórica continua comprometida até rotação externa autorizada; nenhum segredo real encontrado nos candidatos atuais.

Riscos remanescentes: homologação dos scripts/drivers em banco real, rotação histórica e gate de produção; valorização de consumo, logos oficiais, QR e demais RFs parciais permanecem pendentes. Cobertura de acessibilidade é funcional, sem certificação WCAG; resumos limitam relações/produtos a 100. Recomendação **COMMITAR**, sem executar commit e sem autorizar produção.

## Ocorrências intermediárias da auditoria

- A execução integral no target compartilhado falhou em dez casos de Obras com NoClassDefFoundError de EstruturaService$1. A saída tinha EstruturaService.class com gravação posterior à compilação e sem a classe auxiliar referenciada. A causa do processo que alterou a saída não foi determinada; não se alteraram regras para contornar o erro. Fontes/pom/wrapper foram copiados para TEMP e comparados por SHA-256, isolando classes geradas da saída usada pela IDE.
- Primeira execução isolada: uma falha do novo teste de rollback, pois configurar o spy fora de transação invocava o proxy MANDATORY. Configuração movida para TransactionTemplate, conforme outros testes de auditoria. Nenhuma alteração de propagação em produção.
- Primeiro ciclo GESTOR aguardou uma ação de recebimento que sua authority não permite. Teste corrigido para a divisão vigente de responsabilidades: GESTOR prepara/aprova, ALMOXARIFE recebe/atende, GESTOR consulta. Reexecução focalizada passou em 35 segundos, preservando permissions.
- A regressão N+1 falhou antes da correção (5 versus 16 consultas). Esses resultados intermediários não substituem a validação integral sobre o estado final.

Primeira execução integral do navegador sobre H2 reutilizado terminou com 47/62 aprovados: uma falha anterior na espera da tela após logout (login havia retornado 200) e falhas posteriores de login/fixture. Os traces de login inspecionados registraram HTTP 429; o processo acumulava logins das execuções focalizadas e inicializações anteriores. Não se alteraram rate limit, sessões, assertions ou retries. A validação final usa H2 novo depois da suíte Maven, sem executar ambas em paralelo.

## Inventário nominal final

Novos (29):

- `docs/bloco5-auditoria-pre-commit.md`
- `docs/bloco5-relatorio-final.md`
- `docs/obras-os-centros-custo.md`
- `docs/sql/obras-os-centros-custo-mysql-manual.sql`
- `docs/sql/obras-os-centros-custo-postgresql-manual.sql`
- `frontend/src/components/ContextSelector.jsx`
- `frontend/src/pages/ServiceOrderDocument.jsx`
- `frontend/src/pages/Structure.jsx`
- `frontend/src/utils/context.js`
- `frontend/tests/context.test.js`
- `frontend/tests/structure.spec.js`
- `src/main/java/br/com/almoxarifado/obras/CentroCusto.java`
- `src/main/java/br/com/almoxarifado/obras/CentroCustoController.java`
- `src/main/java/br/com/almoxarifado/obras/CentroCustoRepository.java`
- `src/main/java/br/com/almoxarifado/obras/ContextoOperacional.java`
- `src/main/java/br/com/almoxarifado/obras/ContextoService.java`
- `src/main/java/br/com/almoxarifado/obras/EstruturaService.java`
- `src/main/java/br/com/almoxarifado/obras/Obra.java`
- `src/main/java/br/com/almoxarifado/obras/ObraController.java`
- `src/main/java/br/com/almoxarifado/obras/ObraRepository.java`
- `src/main/java/br/com/almoxarifado/obras/ObrasInput.java`
- `src/main/java/br/com/almoxarifado/obras/OrdemServico.java`
- `src/main/java/br/com/almoxarifado/obras/OrdemServicoController.java`
- `src/main/java/br/com/almoxarifado/obras/OrdemServicoRepository.java`
- `src/main/java/br/com/almoxarifado/obras/ResumoEstruturaService.java`
- `src/main/java/br/com/almoxarifado/obras/StatusObra.java`
- `src/main/java/br/com/almoxarifado/obras/StatusOrdemServico.java`
- `src/main/java/br/com/almoxarifado/obras/TipoCentroCusto.java`
- `src/test/java/br/com/almoxarifado/obras/ObrasTests.java`

Modificados (48):

- `AGENTS.md`
- `docs/README.md`
- `docs/api.md`
- `docs/atendimento-solicitacoes.md`
- `docs/autenticacao-autorizacao.md`
- `docs/compras-recebimento.md`
- `frontend/README.md`
- `frontend/package.json`
- `frontend/src/App.jsx`
- `frontend/src/api/client.js`
- `frontend/src/auth/permissions.js`
- `frontend/src/components/AppShell.jsx`
- `frontend/src/pages/Dashboard.jsx`
- `frontend/src/pages/Operations.jsx`
- `frontend/src/pages/PurchaseDocument.jsx`
- `frontend/src/pages/PurchaseNeeds.jsx`
- `frontend/src/pages/PurchaseOrders.jsx`
- `frontend/src/pages/RequestDetails.jsx`
- `frontend/src/pages/RequestForm.jsx`
- `frontend/src/pages/SeparationList.jsx`
- `frontend/src/styles.css`
- `frontend/src/utils/operations.js`
- `frontend/src/utils/purchases.js`
- `src/main/java/br/com/almoxarifado/compras/ComprasController.java`
- `src/main/java/br/com/almoxarifado/compras/ComprasInput.java`
- `src/main/java/br/com/almoxarifado/compras/ComprasViews.java`
- `src/main/java/br/com/almoxarifado/compras/ItemPedidoCompra.java`
- `src/main/java/br/com/almoxarifado/compras/PedidoCompraService.java`
- `src/main/java/br/com/almoxarifado/controller/MovimentacaoController.java`
- `src/main/java/br/com/almoxarifado/controller/NecessidadeCompraController.java`
- `src/main/java/br/com/almoxarifado/controller/SolicitacaoController.java`
- `src/main/java/br/com/almoxarifado/dto/NecessidadeCompraResponse.java`
- `src/main/java/br/com/almoxarifado/dto/OperacaoSolicitacaoResponse.java`
- `src/main/java/br/com/almoxarifado/model/Movimentacao.java`
- `src/main/java/br/com/almoxarifado/model/Solicitacao.java`
- `src/main/java/br/com/almoxarifado/repository/MovimentacaoRepository.java`
- `src/main/java/br/com/almoxarifado/repository/NecessidadeCompraRepository.java`
- `src/main/java/br/com/almoxarifado/repository/SolicitacaoRepository.java`
- `src/main/java/br/com/almoxarifado/security/AuditoriaAspect.java`
- `src/main/java/br/com/almoxarifado/security/Perfil.java`
- `src/main/java/br/com/almoxarifado/security/Permissao.java`
- `src/main/java/br/com/almoxarifado/security/RotasPermissao.java`
- `src/main/java/br/com/almoxarifado/security/SegurancaConfig.java`
- `src/main/java/br/com/almoxarifado/service/AtendimentoSolicitacaoService.java`
- `src/main/java/br/com/almoxarifado/service/MovimentacaoService.java`
- `src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java`
- `src/main/java/br/com/almoxarifado/service/SolicitacaoService.java`
- `src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java`

Reexecução integral seguinte: 61/62 aprovados, com timeout de 5s no teste anterior B3 ao aguardar Em separação. Trace mostrou aprovação/leitura 200 e chamada de iniciar-separacao ainda pendente/abortada na conclusão do teste; não se atribuiu causa ao contexto. O mesmo teste passou focalizado em 38,3s sem mudar código/assertions/timeouts. O fechamento repete a suíte em H2 novo, inicializando consultas e um atendimento fictício parcial/total antes dos testes; rate limit permanece habilitado.
