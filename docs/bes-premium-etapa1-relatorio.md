# Atualização da retomada de 09/10/2026

Os resultados abaixo são históricos e foram preservados. Estado recuperado, novas execuções e conclusão atual: [retomada controlada](bes-retomada-controlada-20261009.md). Novas decisões exclusivamente documentais: [complemento canônico B&S](decisoes-produto-bs-20261009.md).

# BES — Premium Etapa 1 — investigação P0, segunda rodada

Data: 08/10/2026. **Validação final em andamento.** A conclusão histórica da primeira rodada está preservada integralmente ao final deste arquivo. Ela não descreve os resultados da segunda rodada.

## Checkpoint, autorização e preservação

Branch confirmada: `feature/bes-frontend`; HEAD `41de102a66c717307392203c7b3fcda77e3ef462`. Staging inicialmente vazio; somente os dois relatórios não rastreados esperados. Não houve divergência inesperada. A auditoria original permaneceu sem alterações: SHA-256 `1B7497BB1D7CA390304EF50C3EECD2424B3EBA36048B3F19E8EC26FC51F9D4FA`.

Ambos os relatórios foram lidos integralmente, incluindo as limitações dos probes. Eles demonstravam sucesso em preparação mínima nova e falha no processo antigo, porém não reconstruíam a sequência completa, não capturavam a exceção H2 encadeada e não renovavam as conexões criadoras do schema. Por isso, não eram regressões de uma causa corrigida.

O PID original 44716/8081 ainda existia na primeira conferência da segunda rodada. Foram copiados logs, scripts, configuração sanitizada e evidências disponíveis antes de qualquer reinício. Enquanto se preparava um diagnóstico restrito, esse processo deixou de existir; nenhum comando para pará-lo/reiniciá-lo foi enviado. O agente diagnóstico não chegou a ser instalado e não produziu metadados. Seu H2 in-memory foi perdido. Não se afirma nova reprodução no H2 original, captura dos bindings originais, da sessão DDL original ou de suas constraints em execução.

Configuração original preservada: profile test; H2 2.4.240; `jdbc:h2:mem:bes-frontend;MODE=MySQL;DB_CLOSE_DELAY=-1`; create-drop; OSIV true; SQL false; heap 384 MB; porta 8081. Última linha original disponível: 14:42:27. Os dois 409 históricos continuam documentados na primeira rodada e no log original `%TEMP%/bes-premium-h2.log`.

**Falha de preservação durante a validação:** o primeiro runner Playwright usou o diretório descartável padrão `frontend/test-results`, apagando as cópias ali colocadas e as capturas/PDFs antigos desse diretório. Os relatórios e os logs/scripts originais em %TEMP% não foram alterados. As imagens antigas e o JSON bruto do probe original não foram recuperados; não foram substituídos por imagens apresentadas como históricas. As reproduções determinísticas, regressões e verificações frontend foram regeneradas. Os XMLs da execução completa de 568 testes foram recuperados antes das novas regressões. A localização protegida passou a ser `%TEMP%/bes-premium-round2-evidence-20261008`; runners posteriores usam subpastas exclusivas com `--output`. Essa perda é uma limitação de rastreabilidade, não um resultado de teste.

## Causa demonstrada e alcance da conclusão

**Defeito reproduzível do H2 2.4.240 na avaliação de CHECK com IN após fechamento da conexão/sessão criadora do DDL.** O comparador de `ConditionInConstantSet` retém `SessionLocal`; depois de fechar essa conexão, uma conexão nova continua acessando o mesmo banco, mas a avaliação do CHECK tenta utilizar a sessão fechada. A cadeia é `CHECK_CONSTRAINT_INVALID / 23514 → database has been closed / 90098`. Isso explica por que BOM e INICIAL, ambos permitidos, podem falhar sem existir enum inválido.

A reprodução JDBC mínima, sem JPA, inseriu valores válidos, abriu outra conexão, fechou a conexão DDL e tentou inserir novamente. H2 2.4.240 falhou nas duas constraints; entrega EPI com e sem contexto falhou. H2 2.5.250 executou o mesmo mecanismo sem falha. Dois testes reais de service também falharam na versão antiga após `HikariPoolMXBean.softEvictConnections()`, com os mesmos nomes de constraints, 23514 e causa 90098. Na versão corrigida os mesmos testes passaram.

Fontes primárias: [reprodução oficial #4291](https://github.com/h2database/h2database/issues/4291), [relato de CHECK após fechar a sessão #4302](https://github.com/h2database/h2database/issues/4302), [correção #4311](https://github.com/h2database/h2database/pull/4311), [release H2 2.5.250](https://github.com/h2database/h2database/releases/tag/version-2.5.250). A correção upstream troca a referência de sessão por Database no comparador.

A associação com os incidentes históricos é uma **inferência sustentada** pela versão, configuração, nomes/predicados, erro de avaliação, transações equivalentes e reprodução determinística. Não é observação da exceção encadeada ou do fechamento da conexão no processo original perdido. O mecanismo e a correção estão demonstrados nos ambientes reconstruídos; a causa interna histórica exata conserva essa limitação.

## Constraints efetivas, estados e mapeamentos

Metadados foram consultados em H2 isolados 2.4.240 e 2.5.250 através de INFORMATION_SCHEMA, com o código e mapeamentos reais da aplicação. As expressões são idênticas entre versões; apenas a implementação do engine mudou.

| Tabela | Constraint | Coluna e predicado IN |
|---|---|---|
| BES_REGISTRO_ATIVO | CONSTRAINT_393 | CONDICAO_ANTES: NOVO, BOM, REGULAR, DANIFICADO, INOPERANTE |
| BES_REGISTRO_ATIVO | CONSTRAINT_393C | CONDICAO_DEPOIS: mesma lista |
| BES_REGISTRO_ATIVO | CONSTRAINT_393C1 | RESULTADO: APROVADO, APROVADO_COM_RESSALVA, REPROVADO |
| BES_REGISTRO_ATIVO | CONSTRAINT_393C14 | STATUS_ANTES: DISPONIVEL, EMPRESTADO, EM_TRANSFERENCIA, INDISPONIVEL, BAIXADO |
| BES_REGISTRO_ATIVO | CONSTRAINT_393C145 | STATUS_DEPOIS: mesma lista |
| BES_REGISTRO_ATIVO | CONSTRAINT_393C1452 | TIPO: EMPRESTIMO, DEVOLUCAO, TRANSFERENCIA, RECEBIMENTO, INSPECAO, INATIVACAO, REATIVACAO, BAIXA |
| BES_EPI_REGISTRO | CONSTRAINT_6B | MOTIVO: INICIAL, REPOSICAO, SUBSTITUICAO |
| BES_EPI_REGISTRO | CONSTRAINT_6B7 | TIPO: ENTREGA, SUBSTITUICAO, DEVOLUCAO, DESCARTE |

RegistroAtivo utiliza `@Enumerated(STRING)`, JDBC VARCHAR; condição antes/depois e estados/tipo são NOT NULL; resultado permite null. RegistroEpi utiliza o mesmo mapeamento de enum; tipo é NOT NULL e motivo permite null. H2 permite CHECK de resultado nulo. Contexto opcional não integra os dois predicados que falham.

Scripts MySQL/PostgreSQL foram comparados **somente estaticamente**: ativos têm listas compatíveis; EPI possui motivo VARCHAR nullable sem esse CHECK enum gerado pelo Hibernate. Não houve alteração/execução de migrations ou SQL real, nem conclusão sobre equivalência dos bancos alvo.

## Sequência determinística, hipóteses e controles

A preparação foi extraída do script original de auditoria: funcionário, almoxarifados origem/destino, categoria/unidade, três produtos e estoques, duas configurações EPI, Obra/CC/OS, solicitação com aprovação/necessidade/separação/atendimento parcial, fornecedor, pedido aprovado/recebimento parcial, ativo emprestado com contexto, segundo ativo transferido e primeira entrega EPI com contexto. Foram reconstruídas as escritas intermediárias dos scripts posteriores: 24 produtos longos/pedido, segunda solicitação com OS e atendimento parcial, segundo recebimento e outro pedido aprovado, além dos perfis fictícios e leituras relevantes. Não foi repetida a auditoria visual de 269 capturas.

Antes da tentativa final, o ativo estava EMPRESTADO/BOM, pendência #1, sem almoxarifado atual, com Obra/CC/OS #1; saldo, histórico e EPI anterior foram capturados. As massas antigas/nova reconstruídas foram comparadas antes da operação, com timestamps excluídos. Na primeira execução também se confrontou o ativo com o snapshot original completo; esse JSON local foi posteriormente perdido no incidente de preservação acima. As comparações regeneradas conservam os estados históricos documentados e a equivalência entre os H2 reconstruídos.

Após renovação determinística do pool, foram enviados os corpos HTTP da UI original, incluindo condição BOM, destino null, observação vazia; motivo INICIAL, contexto null, quantidade textual 1 e lote/datas null. H2 antigo: dois 409; repetição com mesma chave também 409; contexto informado também 409; snapshot completo permanece idêntico após rollback. H2 novo: dois 200; mesma chave retorna o mesmo evento sem nova alteração; devolução restaura origem e fecha pendência; saldo EPI diminui somente uma vez; nova chave de devolução já fechada retorna 409; entrega com contexto também passa.

| Hipótese | Evidência / conclusão |
|---|---|
| Enum ou transição operacional inválida | BOM/INICIAL estão nos predicados; inserts JDBC válidos e services reais falham somente após fechar/renovar a conexão antiga. Não explica o mecanismo reproduzido. |
| Nulidade/contexto opcional | EPI com null e com contexto falha no engine antigo e passa no novo. Não é condição necessária da falha. |
| Ordem de flush/updates JPA | Falha sem JPA em INSERT JDBC simples; stack aponta avaliação do comparador. Não é necessária para reproduzir. Não alterado flush/order do service. |
| Estado transitório inválido | Repro mínima insere literais constantes válidos sem transição intermediária; versão nova preserva CHECKs. Descartado para o mecanismo demonstrado. |
| Fixtures/API/UI divergentes | Corpos originais reconstruídos e enums tipados; quantidade textual normaliza corretamente; mesmo fluxo passa antes da renovação e na versão nova. Não demonstrada falha de serialização. |
| Estado prévio/ordem operacional | Sequência persistida reconstruída; snapshots equivalentes; ausência da renovação fazia os probes antigos passarem. Volume/contexto anterior não é necessário na reprodução JDBC. |
| Rollback/retry/idempotência | Falhas preservam estado/histórico/saldo; replays com mesma chave não duplicam eventos/estoque; confirmação fechada com chave nova conflita. Não introduzido retry. |
| Configuração/schema H2 | Mesmos argumentos, mapeamentos e predicados; o comparador defeituoso versus corrigido é a variável causal demonstrada. |
| Sessão DDL fechada | JDBC + Hikari/services + API completa: confirma 23514 encadeado com 90098 na versão antiga; mesmos experimentos passam na nova. Fechamento no processo histórico não foi capturado. |

## Correção mínima e regressões

`pom.xml` fixa **h2.version=2.5.250**, ainda exclusivamente em scope test. Não mudaram services, endpoints, DTOs, schema, enums, CHECKs, protocolos de locks, pool lifetime, saldo ou estratégia de monólito modular.

Novas regressões: `AtivosTests.regressaoP0DevolucaoAposRenovarConexoesH2` e `EpiTests.regressaoP0EntregaSemContextoAposRenovarConexoesH2`. Ambas falharam antes da correção com 23514/90098 e passaram depois. A regeneração das evidências usa override Maven `-Dh2.version=2.4.240`, sem modificar arquivos, para repetir o vermelho; a execução normal usa a propriedade corrigida. Não foram repetidos indiscriminadamente os 169 testes para buscar causa.

## V02–V05 implementados

- V02: sidebar fechado mobile com inert/visibility; workspace inert durante abertura; foco inicial no fechar, ciclo Tab/Shift+Tab, Escape, backdrop e links fecham/restauram; transição de breakpoint restaura foco para um controle visível; desktop permanece navegável; filtros de permissão e Inventário/Em breve preservados.
- V03: tabela de fornecedores mantém colunas nome/nome fantasia com mínimo 220 px e tabela 1040 px; overflow horizontal contido; nomes e ação longos quebram dentro da coluna. Sem alteração global de DataTable/documentos.
- V04: token secundário #536b79 aplicado a breadcrumb/rodapé. Teste mede contraste sobre o fundo computado em 390/768/1440, mínimo 4,5:1; sem declaração WCAG integral.
- V05: Modal compartilhado passa a proteger foco por padrão, usa título/ID próprio, ciclo de controles visíveis/habilitados, fallback no próprio dialog, retorno ao acionador conectado ou conteúdo, proteção busy para Escape/fechar. Comportamento EPI permanece coberto pelos testes B7.

Treze novos testes Playwright cobrem esses controles, inclusive perfil CONSULTA, resize, 30 Tabs e 30 Shift+Tabs, Escape/X/retorno e gravação pendente. Dois seletores antigos de menu foram atualizados de complementary para dialog, conforme a semântica acessível do drawer aberto. O seletor novo de fechar EPI foi limitado ao cabeçalho, pois o formulário possui outro botão Fechar.

## Verificações executadas e limites de aprovação

| Verificação | Resultado efetivamente executado |
|---|---|
| Backend `mvnw.cmd clean test` | **568 testes**, zero falhas/erros/skips, BUILD SUCCESS, exit 0; baseline 566 + duas regressões. XMLs preservados em backend-568-surefire. |
| Regressões antes/depois | Vermelho: duas errors com 23514/90098 e exit 1; verde: dois testes, zero falhas/erros/skips e exit 0. |
| API completa H2 antigo/novo | PASS nas duas versões: 409 versus 200; rollback, replays, saldo/histórico e contexto verificados. |
| Node | **125 aprovados**, zero falhas; exit 0. |
| Build | Vite aprovado; exit 0; bundle final recompilado após refinamento de resize. |
| npm audit | Zero vulnerabilidades; exit 0. |
| Playwright completo, primeira execução concluída | **95 aprovados / 13 falhas**, exit 1. Não é aprovação da suíte. |
| Execução posterior | Ambiente registrou starvation/clock leap de 10m41s; falhas de espera e de ciclo de teclado por timeout. Execução interrompida e preservada; não aprovada. |
| Playwright final | Pendente de conclusão nesta versão do relatório. |
| Secret scan | Heurístico de 306 arquivos textuais atuais rastreados/não ignorados, zero padrões fortes de token/chave privada/JDBC com senha; não varre histórico nem documentos binários. Conferência final será registrada ao concluir. |
| Diff check/estado Git | Conferência final pendente. Sem staging/commit/push. |

A execução reprovada identificou três seletores de teste ajustados acima. Duas falhas de atendimento possuem traces com respostas 200 e latência superior aos 5 segundos da assertion (uma requisição de início de separação: 5.882 ms). Falhas subsequentes de login mostram HTTP 429/mensagem de muitas tentativas após reinicializações de workers. Não foram relaxados timeout, rate limit, sessão, CSRF ou autorização para obter verde. Nenhuma dessas falhas esclarece o HTTP 500 histórico do B5.

Inspeção real já realizada das imagens de fornecedores em 390/768/1440, drawer aberto 390/768, modais de produto 390/768 e fornecedor 1440: nomes legíveis, rolagem interna, ausência de overflow global, foco visível e identidade industrial preservada. Capturas full-page são imagens reais do Chromium; regiões abaixo do viewport não representam cobertura visual do backdrop. Uma captura adicional por viewport das jornadas P0 foi tentada, mas a espera de login excedeu timeout; não conta como aprovação de UI pós-renovação. A validação final ainda deve distinguir essas tentativas das execuções aprovadas.

## RFs, arquivos e riscos residuais

Escopo incremental mantido: RF014–RF018, RF099/RF101, RF053–RF057, RF214/RF215; B5 e RF205–RF218 como dependências transversais. Nenhum requisito parcial foi promovido a completo. Auditoria, ator autenticado, snapshots, idempotência, custódia, estoque e histórico permanecem sob as regressões e suites existentes. Sem endpoints novos/inventário alterado, scripts reais, produção, Bloco 8, commit ou push.

Arquivos alterados: pom.xml; os dois testes Java; AppShell.jsx; ui.jsx; Suppliers.jsx; styles.css; frontend.spec.js; novo premium.spec.js; este relatório. Auditoria original preservada sem modificação. Identidade permanece BES textual enquanto a logo oficial B&S não está disponível.

Riscos: impossibilidade de observar novamente o H2 original; perda dos artefatos históricos em test-results; dependência de ambiente estável para aprovação final de navegador; H2 test-only não homologa MySQL/PostgreSQL. **HTTP 500 histórico do B5 continua CAUSA NÃO IDENTIFICADA / NÃO REPRODUZIDO / RISCO RESIDUAL**, separado dos dois P0. Demais pendências anteriores, incluindo QR/anexos/manutenção/financeiro/produção/rotação externa, permanecem.

**Conclusão final pendente das verificações restantes.**

---

## Histórico integral — primeira rodada (conclusões válidas naquela ocasião)

# BES — Premium Etapa 1: investigação P0 e interrupção obrigatória

Data: 08/10/2026. **AINDA REQUER CORREÇÃO.** V01 permanece aberto; causa raiz não identificada. Não avançou para V02–V05.

## Checkpoint e escopo

Branch `feature/bes-frontend`; HEAD `41de102a66c717307392203c7b3fcda77e3ef462`, ambos iguais aos esperados. Conferidos branch, HEAD, status, diff stat e cinco commits recentes. Estado inicial: somente `docs/auditoria-visual-premium-bes.md` não rastreado, preservado integralmente. Sem alteração preexistente de código.

Referências consultadas: AGENTS.md, índice, auditoria original, mestre v1.4, relatórios final/pré-commit B7, ativos, EPI, autenticação/autorização e segurança; entidades, DTOs, services, testes existentes, launcher H2 e scripts SQL manuais. Evidências originais disponíveis em `frontend/test-results/premium-20261008`, incluindo checks, manifests, screenshots e PDFs. Não refeita a auditoria de 269 capturas.

Escopo incremental: investigação V01; correção condicionada à reprodução determinística em H2 novo e à causa identificada. RF014–RF018, RF099/RF101, RF053–RF057 e RF214/RF215 são diretamente relacionados; contexto do B5 e RF205–RF218 permanecem dependências transversais. Nenhum RF foi reclassificado como concluído. Sem alterações de API, schema, segurança, saldo, locks, snapshots, idempotência ou regras de negócio.

A missão determina: **“Se não reproduzir ou não identificar causa, documentar e PARAR antes de avançar para refinamentos cosméticos.”** A falha se repetiu no processo H2 original; a preparação mínima não reproduziu a falha em H2 novo. A causa não foi identificada. Este relatório registra a interrupção exigida, sem declarar os incidentes resolvidos.

## V01: evidências e diferença entre ambientes

O processo original da auditoria continuava ativo: Java PID 44716, H2 em memória na porta 8081, Preview PID 63224 na 5173. Preservados processos e dados. O servidor original já registrava os requestIds `c9082460-fb06-4e00-a9f5-3ac802fd02f9` e `15df2b10-e643-4759-b285-56f9851e2488` em `%TEMP%/bes-premium-h2.log`.

Um backend adicional foi iniciado a partir dos argumentos do launcher oficial, com profile test, H2 `mem:bes-premium-etapa1`, create-drop, usuários fictícios e OSIV habilitado como na auditoria. Porta 8097; Preview independente na 5197, com proxy para 8097 e bundle existente usando `/api`. Readiness direto e pelo proxy: HTTP 200 em `/auth/csrf`. Não houve banco real nem execução dos scripts SQL manuais.

Preparação H2 nova: funcionário e almoxarifado #1; ativo `FER-000001`; Obra, CC e OS #1; empréstimo #1 criado pela API com contexto; produto de código `BES-DEMO-2`, saldo inicial 5, configuração EPI CA 12345; entrega #1 preparada pela API com contexto. Depois exercitadas as duas operações pela UI, com sessão e CSRF reais.

| Jornada | H2 original / UI | H2 novo / UI | Valores enviados |
|---|---|---|---|
| Devolução do empréstimo #1 | HTTP 409; aviso de conflito; ativo segue EMPRESTADO com pendência #1 | HTTP 200; evento #2; ativo DISPONIVEL, almoxarifado #1, responsável/pendência/contexto limpos | devolvidoPorId=1, recebidoPorId=1, almoxarifadoId=null, condicao=BOM, observacao vazia |
| Entrega EPI sem contexto | HTTP 409; permanece em `/epi-entregas/nova` | HTTP 200; navega para `/epi-entregas/2`; registro confirma item e SAÍDA vinculada | funcionário/responsável/local #1, motivo INICIAL, confirmação true, contexto null, quantidade 1, lote/datas null |

O produto é #2 na massa original e #1 no H2 novo; conserva o mesmo código. Os nomes, fabricante/modelo e volume de dados anteriores não são idênticos entre as massas. A reprodução mínima não reconstrói toda a sequência temporal da auditoria. Não é prova de que reiniciar o servidor corrige o defeito nem prova de causa ambiental.

Os DTOs recebem enums tipados e campos estritos. O service normaliza observação vazia para nulo. Devolução obtém condição anterior do ativo e restaura a origem congelada; entrega exige motivo e confirmação, permite contexto ausente e resolve/valida contexto quando informado. As operações permanecem transacionais, auditadas e permissionadas. Não se encontrou evidência de enum inválido enviado pela UI. As diferenças fixture/API versus UI foram confrontadas nos corpos reais; ambas as operações passam no H2 novo sem alteração de aplicação.

## Constraints efetivas e limite da investigação

Dois probes temporários JUnit consultaram `INFORMATION_SCHEMA.CHECK_CONSTRAINTS` ligado a `TABLE_CONSTRAINTS` em H2 isolado, com o mesmo código/mapeamento. Nomes e expressões coincidem com os identificadores dos logs originais:

| Tabela / constraint | Expressão efetiva gerada pelo Hibernate |
|---|---|
| BES_REGISTRO_ATIVO / CONSTRAINT_393 | `CONDICAO_ANTES IN ('NOVO','BOM','REGULAR','DANIFICADO','INOPERANTE')` |
| BES_EPI_REGISTRO / CONSTRAINT_6B | `MOTIVO IN ('INICIAL','REPOSICAO','SUBSTITUICAO')` |

BOM e INICIAL pertencem às listas permitidas. Não foram capturados os bindings internos nem a exceção encadeada do processo original; portanto, não se afirma que esses eram os valores efetivamente avaliados dentro do CHECK original. A condição exibida e o corpo HTTP são evidências distintas dos bindings JDBC.

O log diz **“Check constraint invalid”**, SQLState 23514, e não prova por si só um valor fora da lista. O [código oficial do H2 2.4.240](https://github.com/h2database/h2database/blob/version-2.4.240/h2/src/main/org/h2/constraint/ConstraintCheck.java) distingue falha ao avaliar a expressão (`CHECK_CONSTRAINT_INVALID`) de resultado falso (`CHECK_CONSTRAINT_VIOLATED_1`), e aceita resultado nulo. O handler HTTP traduz conflitos de persistência para 409 genérico; os logs existentes não fornecem a causa encadeada necessária para esclarecer a falha de avaliação.

**Causa raiz: NÃO IDENTIFICADA.** Não removidos/relaxados CHECKs, não alterados enums, não introduzido retry, não ocultado erro e não alterada regra operacional. Os probes diagnósticos passaram antes de qualquer correção; não são testes de regressão de uma causa corrigida. Foram retirados dos arquivos de teste após execução, preservando o código original e os logs locais.

Revisão estática de scripts MySQL/PostgreSQL: ativos possuem CHECK de condição anterior com a mesma lista; scripts EPI usam motivo VARCHAR(30) nullable e não possuem o CHECK enum gerado pelo Hibernate nessa coluna. Não há evidência que permita atribuir o incidente à nulidade do contexto ou transferir sua causa aos bancos alvo. Scripts não executados; homologação de dialect/schema/locks permanece pendente. Não justificado alterar migrations/scripts nesta rodada.

## V02–V05

| Achado | Estado nesta rodada |
|---|---|
| V02: drawer fechado recebe foco, Escape/retorno inadequados | Pendente; diagnóstico original preservado; sem implementação |
| V03: nome longo de fornecedor fragmentado em 390/768 | Pendente; evidências originais preservadas; sem alteração de tabelas |
| V04: breadcrumb/rodapé abaixo de 4,5:1 | Pendente; sem alteração de tokens; sem declaração WCAG integral |
| V05: ciclo/retorno de foco em modais | Pendente; diferenças Modal/trapFocus existentes verificadas; sem alteração do componente/EPI |

Não houve refinamento cosmético, novas funcionalidades, redesign, ondas A/B/C/D ou Bloco 8.

## Execuções, evidências e limitações

| Verificação | Resultado desta rodada | Evidência local ignorada |
|---|---|---|
| Backend focado: `mvnw.cmd -Dtest=AtivosTests,EpiTests test` | **169 testes**, 86 ativos + 83 EPI; zero falhas/erros/skips; BUILD SUCCESS | `frontend/test-results/premium-etapa1-p0-baseline.log` |
| Probes de constraint e serviços | **2 testes**, zero falhas/erros/skips; BUILD SUCCESS; sem reprodução do erro | `premium-etapa1-constraints.log` |
| Jornadas Chromium reais | Original: dois 409; novo: dois 200; corpos e estados registrados | `premium-etapa1-probe.mjs` e `premium-etapa1-probe.json` |
| Readiness H2/Preview isolados | HTTP 200 direto e proxy | Saída da sessão; `premium-etapa1-h2-isolado-8097.log`, `premium-etapa1-preview-isolado.log` |
| Suítes completas backend/Node/Playwright, build, npm audit | **Não executados nesta rodada**: gate P0 interrompeu a implementação | Baselines 566/125/95 são somente históricas |
| Git diff check | Executado ao concluir; sem erros | Conferência de sessão |
| Secret scan do novo relatório | Heurístico, zero padrões fortes; não é scan do histórico nem rotação | Conferência de sessão |

Os comandos Maven foram redirecionados por PowerShell. Warnings do JVM/Mockito foram encapsulados como NativeCommandError; a sessão do probe apresentou exit 1 apesar de o log Maven registrar BUILD SUCCESS e zero falhas. A aprovação focada refere-se aos resultados de teste/log Maven, sem afirmar exit 0 desses wrappers.

Tentativas intermediárias não contam como aprovação: login de automação inicialmente enviou campos adicionais e recebeu 400, corrigido para username/password; porta 8082 já ocupada e preservada, movendo o novo H2 para 8097; primeira chamada precedeu readiness e recebeu ECONNREFUSED; roteamento manual entre servidores produziu 403, substituído por Preview isolado com sessão/CSRF normal, sem relaxar a segurança. Probes/saídas intermediárias não representam regressão da aplicação nem reprodução dos P0.

Browser skill consultada; runtime não encontrou navegador disponível (`No browser is available`, lista vazia). Alternativa: Chromium real do Playwright instalado, headless, Windows. Quatro screenshots novos, viewport padrão 1280×720 com captura full-page, foram produzidos e os dois resultados positivos foram inspecionados visualmente:

- `premium-etapa1-return-8081.png`, `premium-etapa1-epi-8081.png`: incidentes originais;
- `premium-etapa1-return-8097.png`, `premium-etapa1-epi-8097.png`: resultados positivos no H2 novo.

Essas imagens não são mockups nem comparação antes/depois de correção. Não realizada nesta rodada a revisão completa 390/768/1440, teclado/touch, PDFs ou todos os componentes, pois o gate P0 permaneceu aberto. A auditoria original nas três larguras permanece histórico, sem ser reapresentada como nova aprovação. Artefatos ficam em `frontend/test-results`, ignorado pelo Git; não contêm sessão/CSRF/senha gravados pelo probe.

## Arquivos, riscos e próxima etapa

Único arquivo novo solicitado nesta rodada: `docs/bes-premium-etapa1-relatorio.md`. Auditoria original não rastreada preservada. Nenhuma alteração final de código, CSS, testes, build config, dependências ou SQL. Staging vazio; sem commit/push, merge, reset/restore/clean/stash/rebase, alteração de main, banco real ou publicação. Servidores originais e processo preexistente da porta 8082 preservados; servidores adicionais desta investigação encerrados ao concluir.

V01 continua bloqueante no servidor original. Próxima investigação recomendada: obter diagnóstico restrito da exceção H2 encadeada e schema efetivo do processo que falha, e reconstruir a sequência de preparação/uso que produz o estado inválido em H2 novo. Somente após reprodução determinística e causa identificada cabe correção mínima, regressão que falhe antes da correção, suites completas e revisão visual nas três larguras; depois V02–V05.

**HTTP 500 histórico do Bloco 5: CAUSA NÃO IDENTIFICADA / NÃO REPRODUZIDO / RISCO RESIDUAL**, separado de V01. Não foi explicado por estes conflitos.

Preservadas pendências: QR, logo oficial B&S/BES, anexos/evidências, valorização financeira, homologação banco real, rotação externa de credenciais históricas, HTTPS/proxy, migrações controladas, backup/restore, observabilidade/monitoramento e sessão/rate limiting de produção. Não há liberação de produção nem conclusão de RFs parciais.

**Recomendação: AINDA REQUER CORREÇÃO.** Interrupção no gate P0 conforme a missão. Não pronto para auditoria pré-commit das correções premium.
