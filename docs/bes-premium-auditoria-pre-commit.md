# BES — auditoria independente pré-commit Premium V01–V05 e documentação

Data: 09/10/2026. **APROVADO PARA COMMIT**, no escopo incremental revisado, com ressalvas não bloqueantes abaixo. Esta conclusão técnica não autoriza executar commit, push, deploy, banco real, Bloco 8 ou novas funcionalidades. Não é aprovação premium integral nem de produção.

## Método, checkpoint e preservação

Branch conferida: `feature/bes-frontend`. HEAD conferido: `41de102a66c717307392203c7b3fcda77e3ef462`. Staging vazio. Estado inicial: nove arquivos rastreados modificados e cinco não rastreados; diff rastreado com 122 inserções e 19 remoções. Executados branch, rev-parse, status, diff check, diff stat e cached stat. Nenhuma alteração em main ou código durante a auditoria. Única adição ao repositório nesta rodada: este relatório.

Revisados os diffs, os cinco arquivos novos e as fontes referenciadas: AGENTS.md, índice, auditoria visual original, relatório da etapa 1, retomada controlada e decisões de produto. Também examinados services, entidades, segurança HTTP/service, testes, configuração Maven/frontend, launcher H2 e artefatos locais. A documentação mestre foi lida por extração do XML interno, sem editar o DOCX. Não se iniciou bloco funcional novo. RF014–RF018, RF099/RF101, RF053–RF057 e RF214/RF215 são relacionados; contexto do B5 e RF205–RF218 permanecem transversais, sem promoção de requisito parcial a concluído.

Inventário inicial:

- Modificados: `docs/README.md`; `frontend/src/components/AppShell.jsx`; `frontend/src/components/ui.jsx`; `frontend/src/pages/Suppliers.jsx`; `frontend/src/styles.css`; `frontend/tests/frontend.spec.js`; `pom.xml`; `src/test/java/br/com/almoxarifado/ativos/AtivosTests.java`; `src/test/java/br/com/almoxarifado/epi/EpiTests.java`.
- Não rastreados: `docs/auditoria-visual-premium-bes.md`; `docs/bes-premium-etapa1-relatorio.md`; `docs/bes-retomada-controlada-20261009.md`; `docs/decisoes-produto-bs-20261009.md`; `frontend/tests/premium.spec.js`.

Não identificadas mudanças alheias ao escopo: pom/regressões pertencem a V01; frontend/testes a V02–V05; documentação registra histórico e planejamento. Probes Java/JS, flags e runners em TEMP são evidências diagnósticas locais, não funcionalidades entregues. Não foram apagados. Nenhum servidor iniciado nesta auditoria.

## P0: mecanismo reproduzível versus incidente histórico

Foram reexecutados, sequencialmente e sem `clean`, os testes `AtivosTests#regressaoP0DevolucaoAposRenovarConexoesH2` e `EpiTests#regressaoP0EntregaSemContextoAposRenovarConexoesH2`:

1. Configuração atual H2 2.5.250: dois testes, zero falhas/erros/skips; BUILD SUCCESS; exit 0.
2. Override `-Dh2.version=2.4.240`: dois testes, duas errors, nenhuma failure/skipped; BUILD FAILURE; exit 1 esperado. Ambos apresentam 23514 encadeado com 90098. XMLs confirmam classpath H2 2.4.240.

O preparo usa services reais, empréstimo/entrega inicial com contexto e `softEvictConnections()` antes da operação alvo. O ativo é devolvido com restauração da origem, fechamento de pendência, histórico e replay sem duplicação; chave nova para devolução já fechada conflita. EPI sem contexto reduz saldo uma única vez; replay mantém saldo e identidade do evento; mesma chave com quantidade diferente conflita. Não são testes que apenas conferem uma propriedade de configuração.

Inspecionadas também as evidências JDBC antigas/novas em `%TEMP%/bes-premium-round2-evidence-20261008`: fechamento da conexão DDL, literais BOM/INICIAL válidos, erro 23514/90098 no engine antigo e sucesso no novo. O probe novo registra rejeição de condição inválida por CHECK inalterado, SQLState 23513. Os JSONs da sequência HTTP registram 409 no antigo e 200 no corrigido, incluindo replays; permanecem evidência histórica, não reexecução HTTP nesta auditoria.

A alteração oficial [H2 #4311](https://github.com/h2database/h2database/pull/4311) trata especificamente condições IN constantes em CHECK usadas por outras sessões. A [release 2.5.250](https://github.com/h2database/h2database/releases/tag/version-2.5.250) foi conferida. Essa evidência é coerente com a cadeia reproduzida; não demonstra correção universal de todos os CHECKs do H2.

**Causa do mecanismo reconstruído: comprovada. Associação ao incidente original: inferência sustentada.** O processo original não teve sua sessão DDL e exceção encadeada capturadas. Não afirmar certeza histórica nem atribuir o HTTP 500 do B5 ao mesmo defeito.

No pom, H2 continua explicitamente em `scope=test`; somente sua versão muda. O parent Spring Boot é 4.1.1. A inicialização real das duas regressões nesta auditoria e os 568 testes arquivados sustentam compatibilidade observada com esse stack; override não é certificação de toda configuração possível. Nenhum diff em `src/main`, scripts SQL, enums, DTOs, schema ou configuração de produção. Constraints não foram removidas nem relaxadas. MySQL/runtime permanece inalterado.

## Segurança e integridade

Revisão direta: `SegurancaConfig` mantém sessão HTTP, repositório CSRF, autorização de rotas operacionais e deny-all para rotas não admitidas. `RotasPermissao` exige EMPRESTIMO_GERENCIAR para a devolução e EPI_ENTREGA_GERENCIAR para entrega; os services declaram as mesmas permissões via `@PreAuthorize`. Não foram criados endpoints públicos ou novas rotas.

Services permanecem transacionais. Devolução bloqueia ativo, repete verificação de replay, exige origem/pendência única aberta, preserva snapshots e gera evento novo. Não movimenta saldo de Produto. Entrega mantém locks contexto → origens ordenadas → produtos ordenados → estoque ordenado; valida saldo antes de gerar SAÍDA e item com snapshots. Chaves persistidas têm restrição única e assinatura do comando. O ator vem da identidade autenticada, separado do responsável operacional. `AuditoriaService.registrar` usa propagation MANDATORY; falha de auditoria participa do rollback.

A suíte arquivada inclui testes de autorização positiva/negativa, CSRF, ator forjado, concorrência, rollback de auditoria, saldo e histórico; foram lidos testes correspondentes e conferidos XMLs verdes. Essas verificações amplas não foram reexecutadas nesta auditoria. As duas regressões P0 novas foram reexecutadas, mas não substituem esses testes de segurança.

Frontend mantém permissões na navegação, sessão/CSRF em memória e prevenção busy de envio; o diff não introduz armazenamento de secrets, nova exposição de dados ou alteração da arquitetura de autenticação. Scan heurístico atual de 300 arquivos textuais rastreados/não ignorados: zero ocorrências nos padrões examinados de chave privada, token GitHub/AWS ou senha em URL JDBC. Não cobre todo secret possível, histórico Git ou binários. Fixtures fictícias de teste não são credenciais de produção. Rotação externa de credenciais históricas continua pendente.

## V02–V05: revisão do código e das evidências

| Item | Verificação independente | Resultado/limite |
|---|---|---|
| V02 | AppShell: inert no drawer fechado/workspace aberto, role dialog mobile, foco inicial, Tab/Shift+Tab, Escape, backdrop/link e matchMedia; testes de resize/perfil CONSULTA | Implementado; testes históricos focados e completos aprovados. Ressalva A02 abaixo |
| V03 | CSS localizado em supplier-list, colunas de nomes 220px, tabela 1040px, quebra na ação; DataTable restante preservada | Capturas 390/768/1440 inspecionadas: nomes legíveis, rolagem interna nas telas menores; demais colunas exigem rolagem |
| V04 | Token #536b79 aplicado a breadcrumb/rodapé; teste calcula contraste sobre fundo computado nas três larguras | Evidência histórica aprovada para mínimo 4,5:1; não equivale a WCAG integral |
| V05 | Modal: showModal/cancel, ID de título único, trapFocus padrão, summary incluído, retorno/fallback em content, Escape/X bloqueados por busy | Testes históricos de ciclos longos, retorno e gravação pendente aprovados; não introduz retry de escrita |

Inspecionados `premium.spec.js` e os seletores alterados em `frontend.spec.js`. Os 14 testes premium cobrem três larguras, permissões, drawer, modais curtos/longos, disclosure de contexto de ativo e busy. A lista de controles do Modal filtra desabilitados, tabindex negativo e elementos sem retângulo; não certifica todas as possibilidades futuras de CSS ou widgets complexos. Não encontrado widget atual que torne essa limitação bloqueante.

Conferidos os 20 hashes do manifest final; todos correspondem aos PNGs existentes. Inspeção visual independente de fornecedores 390/768/1440, drawer aberto 390, modal produto 768 e fornecedor 1440. Foco visível, nomes legíveis e identidade industrial preservada. Imagens full-page mostram áreas abaixo do viewport sem backdrop; isso não prova falha de cobertura do backdrop na tela. Não apresentadas como novas capturas nesta auditoria.

Não houve nova sessão interativa nem reexecução Playwright: validação dinâmica nas três larguras é a evidência anterior conferida, complementada por leitura do código/testes e inspeção dos PNGs. Touch real, leitor de tela e auditoria visual premium integral permanecem pendentes.

## Testes: reexecutados e históricos

| Verificação | Nesta auditoria | Evidência |
|---|---|---|
| Backend completo 568 | Artefatos anteriores verificados; não reexecutado | 15 XMLs da pasta backend-568 somam 568, zero falhas/erros/skips; log BUILD SUCCESS |
| Regressões P0 corrigidas | **Reexecutado: 2 aprovadas, exit 0** | `%TEMP%/bes-independent-audit-20261009/green.log` |
| Regressões P0 antigas | **Reexecutado: 2 errors esperadas, exit 1** | `red.log` e cópias dos dois XMLs em `red` na mesma pasta |
| Node 125 | **Reexecutado: 125 aprovados, exit 0** | `node.log` |
| Build | **Reexecutado: aprovado, exit 0** | `build.log`; saída isolada em TEMP/dist, sem sobrescrever bundle anterior |
| npm audit | **Reexecutado: zero vulnerabilidades reportadas, exit 0** | `audit.json`; não equivale a SCA completo do backend |
| Playwright completo 109 | Artefatos anteriores verificados; não reexecutado | Log final: 109 passed; .last-run.json passed/failedTests vazio |
| Premium 14 | Artefatos anteriores verificados; não reexecutado | Log focused: 14 passed; .last-run.json passed/failedTests vazio |

Execuções adicionais foram sequenciais: regressões verdes → vermelhas → Node → build → npm audit. Sem suítes pesadas simultâneas nem backend de desenvolvimento ativo. O parâmetro de destino de relatórios passado ao Maven não isolou os XMLs; os dois XMLs vermelhos emitidos em target foram copiados para TEMP/red. Logs de ambas as execuções estão separados. A suíte verde histórica e os XMLs vermelhos históricos em TEMP foram preservados; o resultado verde novo tem log, não cópia separada de XML anterior à reexecução vermelha.

Falhas anteriores não foram ocultadas: primeira rodada 95 aprovados/13 falhas e tentativa interrompida; primeira retomada 107 aprovados/2 falhas de resize. Não contabilizadas como aprovação. Suíte final posterior: 109 aprovados. Evidências locais têm valor de diagnóstico, sem assinatura de CI ou garantia independente de origem.

## Documentação e mestre

`decisoes-produto-bs-20261009.md` registra explicitamente PLANEJAMENTO: prontuário único por funcionário; nova vigência quando muda função; matriz EPI versionada validada por SST; aceite vinculado ao evento sujeito a validação SST/jurídica; alternativa com dispositivo corporativo; caixas individualizadas/composição/custódia; digital por padrão, impressão conforme necessidade; demo/comercialização problema → fluxo → resultado. Diferencia confirmação operacional atual de aceite do trabalhador, e teste H2 de Demo Mode comercial.

O documento nega implementação nesta rodada e exige autorização de bloco futuro. Índice aponta para fonte única. Integração formal à mestre **pendente**, explicitada como eventual versão futura. Arquivo mestre v1.4 byte a byte idêntico ao HEAD; SHA-256 `f2c2289fd899834e9868b7102c869e4417fcff460b5d6c4c359bc9f52922865b`. Não editado. Auditoria original conserva SHA-256 `1b7497bb1d7ca390304ef50c3eecd2424b3eba36048b3f19e8ec26fc51f9d4fa`.

O relatório da etapa 1 conserva conclusões históricas pendentes/reprovadas, mas seu cabeçalho remete à retomada posterior. Não é contradição suficiente para impedir commit quando lido com a cronologia indicada.

## Achados classificados

P0: integridade/segurança crítica; P1: falha operacional relevante; P2: limitação localizada ou lacuna de validação; P3: melhoria de apresentação/manutenção. Não identificado P0/P1 novo bloqueante no diff auditado.

| ID / prioridade | Arquivo/evidência | Impacto e risco | Recomendação | Bloqueia commit? |
|---|---|---|---|---|
| A01 — P0 original, corrigido no cenário reproduzível | pom.xml; AtivosTests/EpiTests; verde/vermelho reexecutados | Engine antigo impede devolução/entrega. Risco histórico residual: sessão original não capturada | Manter regressões e versão test-scope; conservar distinção causal/histórica | Não, correção validada no escopo |
| A02 — P2 | AppShell.jsx, dois alvos `.nav-link.active`; App.jsx contém `/epi-funcionarios/:id` sem link correspondente na sidebar | Ao abrir drawer nessa rota e ampliar para desktop, o alvo de retorno pode ser null; não há fallback visível. Risco localizado de perda de foco, identificado por leitura; não reproduzido ao vivo. O teste de resize usa rota com link ativo | Em rodada autorizada, testar rota sem item ativo em 390/768 → 1440; oferecer destino visível alternativo | Não; navegação/fluxo operacional permanece acessível, ressalva de acessibilidade localizada |
| A03 — P2 | `%TEMP%/bes-retomada-20261009-red-surefire`, XMLs | Pasta contém XMLs residuais de outras classes: soma bruta 399 testes e 2 errors. Apenas os dois XMLs P0 são da execução focada vermelha. Risco de contagem equivocada | Ler log da execução e filtrar as duas classes; usar diretórios efetivamente isolados em execuções futuras | Não; log e XMLs alvo sustentam as duas errors |
| A04 — P2 | Testes premium, PNGs/manifest e relatórios | Browser nesta auditoria não reexecutado; evidência Chromium anterior não valida touch/leitor de tela/zoom/operadores. Risco de alegação excessiva de conformidade | Manter limites; programar validação real específica antes de aprovação integral | Não para commit incremental; impede alegação premium integral |
| A05 — P2 residual | docs/bloco7-auditoria-pre-commit.md e relatórios premium | HTTP 500 histórico B5 sem causa comprovada/não reproduzido; risco operacional ainda aberto, sem evidência causal nova | Investigar separadamente, sem vincular automaticamente a H2/409 | Não para este diff; permanece risco de liberação |
| A06 — P2 residual | docs/security.md; pom.xml; docs de módulos | H2 não homologa MySQL/PostgreSQL; rotação histórica, SCA backend e gates de produção pendentes | Homologação e security gate em rodada autorizada; nunca usar banco real nesta auditoria | Não para commit; produção não autorizada |
| A07 — P3 | docs/decisoes-produto-bs-20261009.md e mestre | Planejamento está em complemento; integração formal na mestre futura pendente. Risco baixo de consulta incompleta | Preservar link e incorporar somente na revisão documental autorizada | Não |

## Conclusão e encerramento

**APROVADO PARA COMMIT** do incremento V01–V05 e documentação revisado, com A02–A07 explicitamente preservados e nenhuma autorização de commit concedida por este relatório. Correção do mecanismo P0 tem reprodução independente antes/depois; versão permanece test-scope; artefatos completos sustentam regressões amplas e não houve alteração produtiva backend. Aprovação não transforma inferência histórica em certeza.

Permanecem: HTTP 500 B5 sem causa comprovada, homologação com banco real pendente, auditoria premium integral não concluída, touch/leitor de tela/zoom/impressão física e operadores pendentes, produção não autorizada. Mestre intacta e decisões futuras não implementadas.

`git diff --check` executado após criar este arquivo: sem erros; apenas aviso informativo LF→CRLF do CSS preexistente. Branch/HEAD inalterados; staging vazio; nove arquivos rastreados modificados e seis não rastreados, incluindo este relatório. Sem correções automáticas, staging, commit, push, deploy, banco real ou alteração de branch. Próxima etapa depende de autorização do usuário.
