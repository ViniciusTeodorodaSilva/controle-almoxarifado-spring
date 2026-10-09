# BES — retomada controlada P0/P1, 09/10/2026

## Checkpoint e recuperação

Branch `feature/bes-frontend`; HEAD `41de102a66c717307392203c7b3fcda77e3ef462`, iguais ao checkpoint solicitado. Conferidos branch, HEAD, status, diff completo, stat, check e staging. Oito arquivos rastreados modificados e três arquivos não rastreados foram encontrados; staging vazio. Todas as alterações foram preservadas. Auditoria original intacta: SHA-256 `1B7497BB1D7CA390304EF50C3EECD2424B3EBA36048B3F19E8EC26FC51F9D4FA`.

Encontrados implementados: atualização H2 exclusivamente test-scope, duas regressões, drawer/foco/resize, contraste breadcrumb/rodapé, colunas de fornecedor, Modal compartilhado e testes premium. Encontrados parciais: aprovação final Playwright e conclusão documental. O relatório anterior registra 568 backend/125 Node aprovados, mas E2E reprovado e outra tentativa interrompida; esses resultados são históricos, separados das execuções abaixo. P1 foi preservado e recebeu a correção incremental de resize descrita abaixo. Não há alterações alheias ao escopo identificadas: pom/testes pertencem a V01; componentes/testes a V02–V05.

Referências: AGENTS.md, mestre v1.4 extraída sem alterar o Word, índice, auditoria visual, relatório da etapa 1 integral, B7 final/pré-commit, ativos, EPI, segurança e autenticação/autorização. RF014–RF018, RF099/RF101, RF053–RF057 e RF214/RF215 diretamente envolvidos; B5/contexto e RF205–RF218 transversais. Nenhuma cobertura parcial promovida a concluída. Escopo é corrigir e validar P0/P1 já autorizados e documentar decisões futuras; não implementar essas decisões.

## V01 — causa demonstrada e limite histórico

As evidências recuperadas em `%TEMP%/bes-premium-round2-evidence-20261008` incluem logs JDBC, regressões vermelhas/verdes, sequência HTTP com schema/estados antes/depois e XMLs. Com H2 2.4.240, após fechar/renovar a conexão que criou o DDL, CHECK com IN pode usar uma sessão fechada: cadeia 23514 → 90098. Literais válidos BOM e INICIAL falham. Com H2 2.5.250, os mesmos predicados e operações passam. A [correção oficial #4311](https://github.com/h2database/h2database/pull/4311) e a [release 2.5.250](https://github.com/h2database/h2database/releases/tag/version-2.5.250) foram consultadas nesta retomada.

Correção mínima recuperada: `h2.version=2.5.250` no pom, dependência continua somente test. Não relaxa CHECKs nem muda enums, schema, services, endpoints, HTTP 409, locks, pool lifetime ou saldo. Regressões exercitam `softEvictConnections()` após preparo real, devolução com origem/contexto e entrega EPI sem contexto, além de replay, saldo e histórico. Detalhes de constraints, hipóteses descartadas e sequência reconstruída permanecem no [relatório anterior](bes-premium-etapa1-relatorio.md).

O mecanismo é comprovado no ambiente reconstruído. Sua associação ao incidente original continua **inferência sustentada**, pois aquele processo/H2 foi perdido antes de capturar a exceção encadeada ou a sessão DDL. Não declarar resolução histórica irrestrita nem aprovação premium integral somente por sucesso em banco novo. O HTTP 500 antigo de B5 continua **causa não identificada / não reproduzido / risco residual** e não é explicado por esta correção.

## P1 e revisão adversarial

**Regressão encontrada e corrigida nesta retomada:** a primeira suíte completa terminou com 107 aprovados e duas falhas (drawer 390/768). Em ambas, `premium.spec.js:47` não encontrou foco no botão após desktop → mobile. O CSS oculta a sidebar e o navegador retira o foco antes do evento matchMedia; consultar somente activeElement nesse evento perde a origem. AppShell agora acompanha focusin para conservar essa informação e restaura apenas quando o foco veio da sidebar. Teste existente reproduziu antes; acrescidas verificações de foco inicial em drawer/dialog e preservação do foco no conteúdo durante resize. Não relaxados timeout, assertion, rate limit ou segurança. Traces em `frontend/test-results/retomada-20261009-e2e`; capturas da primeira execução preservadas em `%TEMP%/bes-retomada-20261009-evidence-first-run`.

- V02: drawer fechado mobile inert/invisível, workspace inert durante abertura, Tab/Shift+Tab contidos, Escape/link/backdrop fecham e devolvem foco; breakpoint restaura controle visível. Rotas, permissões, indicação ativa e Inventário/Em breve preservados.
- V03: fornecedor tem colunas de nomes de pelo menos 220 px, tabela com rolagem interna e ação com quebra de texto. Dados operacionais permanecem visíveis; alteração localizada na lista.
- V04: breadcrumb/rodapé usam token secundário #536b79. Testes medem contraste mínimo 4,5:1 nas três larguras, sem declarar conformidade integral.
- V05: dialog nativo mantém showModal/cancel, título único, Tab de controles habilitados/visíveis incluindo summary, fallback de foco e retorno ao acionador/conteúdo. Busy bloqueia Escape/fechar. Formulários conservam prevenção de envio duplicado.

Revisão estática: nenhum código produtivo backend alterado, novas permissões/rotas ou mudança de regra operacional. Regressões e suíte backend cobrem autorização HTTP/service, CSRF, ator autenticado, rollback de auditoria, concorrência, snapshots, custódia e saldo. H2 test-scope não homologa bancos alvo. A identidade industrial, sidebar escura, azul, superfícies neutras e Lucide foram preservados; logo oficial ausente mantém BES textual.

## Verificações desta retomada

| Verificação | Resultado atual |
|---|---|
| `mvnw.cmd clean test` | 568 testes; zero falhas/erros/skips; BUILD SUCCESS; exit 0 |
| Duas regressões com `-Dh2.version=2.4.240` | Duas errors esperadas, ambas 23514/90098; exit 1. Confirmação vermelha do defeito, separada da suíte aprovada |
| `npm.cmd test` | 125 aprovados; zero falhas/cancelados/skips; exit 0 |
| `npm.cmd run build` | Build inicial e build após correção de resize aprovados; exit 0; avisos de tempo de plugin não são erro de build |
| `npm.cmd audit --json` | Zero vulnerabilidades em todas as severidades; exit 0 |
| Scan heurístico | Inicial: 299; final: 300 arquivos textuais atuais rastreados/não ignorados; zero padrões fortes; não varre histórico, binários ou certifica ausência de secrets |
| Lint | Não existe script lint nem plugin Java de lint configurado; não alegado como executado/aprovado |
| SCA backend | Não há scanner CVE configurado; revisão do escopo da dependência não equivale a auditoria completa de CVEs |
| Playwright completo final | 109 aprovados, zero falhas; exit 0; 9,3 minutos; bundle após correção, H2 próprio reiniciado e readiness backend/proxy 200 |
| Playwright premium após correção | 14 aprovados, zero falhas; exit 0; foco inicial, resize, Tab/Shift+Tab, Escape, retorno, busy, permissões e contraste |
| Dependência H2 resolvida | `mvnw.cmd dependency:tree -Dincludes=com.h2database:h2`: 2.5.250:test; BUILD SUCCESS; exit 0 |

Tentativa inicial `npm` foi bloqueada pela policy de `npm.ps1`; execuções efetivas acima usaram `npm.cmd`, sem alterar policy do sistema. Logs em `%TEMP%/bes-retomada-20261009-{backend,node,build}.log` e `audit.json`; XMLs da suíte completa preservados em `%TEMP%/bes-retomada-20261009-backend-568` antes de testes adicionais.

Skill Browser consultada; conexão retornou `No browser is available`. Alternativa autorizada: Chromium real do Playwright. Evidências preexistentes PNG/PDF/JSON e diretório premium copiados para `%TEMP%/bes-retomada-20261009-evidence-before` antes da suíte. Runners usam pasta exclusiva via --output, e premium aceita `BES_VISUAL_EVIDENCE_DIR` para não sobrescrever capturas anteriores. Nenhum artefato anterior foi apagado nesta retomada. A perda de imagens históricas na rodada anterior continua limitação documentada, sem reconstruções apresentadas como originais.

### Evidências visuais atuais

Vinte PNGs premium finais em `frontend/test-results/retomada-20261009-final-visual`, com `manifest.json` (nome, tamanho e SHA-256), copiados também para `%TEMP%/bes-retomada-20261009-final-visual`. Inspeção real de imagens da rodada anterior preservada e de capturas novas de ativos/EPI, fornecedores, drawer e modais. Comparação de resize: traces reprovados na primeira execução versus testes verdes e capturas finais; não há nova imagem apresentada como captura do H2 histórico perdido.

| Evidência final | Larguras / resultado visual |
|---|---|
| `supplier-{390,768,1440}.png` | Nomes longos legíveis; tabela rola internamente em telas menores; identidade preservada |
| `drawer-open-{390,768}.png`, `drawer-closed-{390,768,1440}.png` | Drawer aberto/fechado, foco e navegação validados pelos testes; conteúdo não recebe foco enquanto inert |
| `modal-{produtos,categorias,fornecedores,epis}-{390,768,1440}.png` | Formulários curtos/longos, foco inicial visível e layout responsivo; Tab/Escape/retorno verificados |
| Capturas B6/B7 da suíte em `frontend/test-results` | Devolução, entrega, erro/carregamento, histórico, contexto e documentos; cópias anteriores preservadas |

Capturas full-page podem mostrar conteúdo abaixo do viewport sem backdrop; isso não é prova de cobertura do backdrop fora da tela. Não refeita a auditoria original de 269 capturas, nem declarado atendimento integral a WCAG, touch real, leitor de tela ou impressão física. Logs finais: `%TEMP%/bes-retomada-20261009-{final-e2e,focused,build-final,red,h2-dependency}.log`; saídas de navegador em diretórios exclusivos `retomada-20261009-{e2e,focused,final-e2e}`. XMLs verdes e vermelhos em pastas distintas `%TEMP%/bes-retomada-20261009-{backend-568,red-surefire}`; o último teste Maven em target é intencionalmente o vermelho, não a suíte verde arquivada.

## Documentação e inventário

Novo [complemento canônico de decisões B&S](decisoes-produto-bs-20261009.md): prontuário único por funcionário, vigências ocupacionais/matriz por função, aceite sujeito a validação SST/jurídica e dispositivo corporativo, caixas individualizadas e custódia contínua, digital por padrão/papel quando necessário, estratégia futura comercial problema → fluxo → resultado. Nenhuma dessas novas funcionalidades foi implementada. Mestre v1.4 permanece intacta; índice atualizado com fonte única das decisões e este relatório.

Arquivos recuperados: `pom.xml`, `src/test/java/br/com/almoxarifado/ativos/AtivosTests.java`, `src/test/java/br/com/almoxarifado/epi/EpiTests.java`, `frontend/src/components/AppShell.jsx`, `frontend/src/components/ui.jsx`, `frontend/src/pages/Suppliers.jsx`, `frontend/src/styles.css`, `frontend/tests/frontend.spec.js`; não rastreados `frontend/tests/premium.spec.js` e os dois relatórios anteriores. Nesta retomada: correção de resize em AppShell, reforço dos testes premium/opção de destino de capturas, índice, complemento de decisões, este relatório e link de atualização no relatório anterior. Auditoria original permanece não rastreada e intacta.

## Encerramento e pendências

V01: mecanismo reproduzível corrigido e regressões vermelhas/verdes comprovadas; vínculo ao incidente original conserva ressalva histórica. V02–V05: implementados e validados no escopo das três larguras e cenários registrados. Nenhuma falha na suíte completa final. **Trabalho preparado para auditoria pré-commit independente com as ressalvas documentadas; sem aprovação premium integral ou de produção.**

Sem staging, commit, push, merge, main, banco real, migração real, deploy ou Bloco 8. Permanecem homologação MySQL/PostgreSQL, leitor de tela/touch real/zoom/impressão física, validação de operadores, rotação externa histórica, HTTPS, backups, observabilidade e demais pendências dos blocos anteriores. Auditoria independente deve avaliar diffs/evidências e o limite histórico V01; HTTP 500 B5 segue aberto. Nova funcionalidade planejada exige autorização de bloco própria; qualquer commit/push exige autorização explícita posterior.

Conferência final: branch/HEAD inalterados; nove arquivos tracked modificados e cinco não rastreados (inventário acima), staging vazio; `git diff --check` sem erros. Diff stat tracked: 122 inserções e 19 remoções; arquivos novos são contabilizados separadamente pelo status. Aviso LF→CRLF do CSS é informativo; não houve normalização em massa. Índice e os três relatórios/decisões referenciados foram verificados, sem links locais ausentes. Hash da auditoria original novamente conferido e idêntico. Servidores próprios H2 8081 e preview 5173 encerrados após a validação, com identidade do processo conferida antes de parar; demais processos preservados.
