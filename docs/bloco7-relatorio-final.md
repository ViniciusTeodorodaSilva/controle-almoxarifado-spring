# Bloco 7 — relatório de retomada e consolidação

Auditoria posterior em 08/10/2026: [revisão adversarial pré-commit](bloco7-auditoria-pre-commit.md). Resultados abaixo conservam a evidência da retomada; a auditoria contém a execução atual e o tratamento explícito do risco HTTP 500 não elucidado.

Retomada em 07/10/2026 após interrupção por conectividade. Branch `feature/bes-frontend`, HEAD `e9df6011cd491ba391e458287ce59a924dd592c5`, iguais ao checkpoint oficial. Nenhum arquivo foi descartado. Na retomada havia dez arquivos tracked modificados, seis arquivos frontend novos e diretórios novos de domínio/testes EPI. Staging vazio. Não houve reset/restore/clean/stash/rebase/merge/checkout.

Encontrado pronto: extensão EPI 1:1 Produto, eventos/itens históricos, 12 endpoints, integração EstoqueService, authorities HTTP/service, CSRF, auditoria, idempotência/locks e telas iniciais. A execução anterior registrava 67 testes EPI aprovados, mas alterações posteriores ainda exigiam nova validação. Encontrado parcial: build bloqueado por JSX, revisão adversarial/visual e cobertura final. Faltavam testes Node/E2E, scripts manuais e documentação do bloco. A retomada conservou as entidades/rotas/componentes existentes.

Concluído na retomada: correção JSX; regressões de substituição explícita e rollback; inventário 132/57; quantidades decimais exatas no cliente; pesquisa com seleção preservada; saldo atualizado após conflito; feedback de resposta incerta; ações mobile diretas; contraste e campos de 44 px; testes Node/E2E; propostas SQL e documentação. [Regras completas e classificação dos RFs](epis-seguranca-trabalho.md).

## Evidências de validação

O resultado final e a recomendação são registrados após encerramento das verificações. Logs temporários: `bes-b7-final-backend.log`, `bes-b7-final-node.log`, `bes-b7-final-build.log`, `bes-b7-final-e2e.log`, `bes-b7-final-npm.json`, `bes-b7-final-secrets.json`. Não versionar cookies, sessões, traces ou logs operacionais. Capturas/PDFs de dados fictícios ficam em `frontend/test-results`, ignorado pelo Git.

Execuções intermediárias não foram consideradas aprovação final: primeiro build falhou por JSX; a primeira suite completa E2E terminou com 92 aprovados e uma falha no texto 404 corrompido por encoding, corrigido em UTF-8 sem alterar a assertion; primeira rodada E2E falhou por código de produto com acento na fixture. Repetir fixtures após essas falhas atingiu o rate limit de login; o H2 próprio foi reiniciado, sem desativar a proteção. As rodadas focadas seguintes passaram com 10 e depois 14 E2E. A revisão visual corrigiu botões de remoção e ofereceu cartões de posse no celular. Backend completo inicial: 558 testes aprovados; a regressão adicional de consultas exige nova execução final.

## Resposta aos 88 pontos

| # | Ponto | Resultado/evidência |
|---|---|---|
| 1 | Branch | feature/bes-frontend |
| 2 | HEAD | e9df6011cd491ba391e458287ce59a924dd592c5 preservado |
| 3 | Working tree inicial | Trabalho B7 preservado; staging vazio; dez arquivos tracked modificados e novos diretórios/arquivos |
| 4 | RFs encontrados | RF099–101, RF104, RF107; avaliação RF205–218 e dependências reais na mestre v1.4 |
| 5 | Produto x EPI | Configuração 1:1 por produto; catálogo único |
| 6 | EPI x Ativo | Não cria ativo patrimonial ou empréstimo para EPI quantitativo |
| 7 | Modelo | Configuração + registro + item; eventos imutáveis, posse derivada |
| 8 | CA | Manual, do modelo, somente dígitos; sem certificação externa |
| 9 | Histórico CA | Snapshot; testes backend e documento no navegador após mudança |
| 10 | Validade | CA, fabricação, validade física e recomendação separados |
| 11 | Variações/tamanho | Produto por variante; não relabelar estoque positivo |
| 12 | Entrega | Funcionário, responsável, ator, local, contexto e 1–50 itens |
| 13 | Estoque | SAÍDA/ENTRADA via EstoqueService, sem saldo EPI paralelo |
| 14 | Saldo insuficiente | Bloqueio integral backend e aviso cliente; regressões H2/E2E |
| 15 | Concorrência | Último saldo: um vencedor; saldo nunca negativo |
| 16 | Idempotência | Chave única/hash persistidos; replay e comando diferente; resposta perdida E2E |
| 17 | Tipos/motivos | Inicial/reposição/substituição; fechamento devolução/descarte/perda coerente |
| 18 | Substituição | Origem/quantidade/motivo/condição/destino explícitos e saída nova atômica |
| 19 | Devolução | Parcial e vinculada; não apaga entrega |
| 20 | Retorno estoque | NOVO + políticas + identidade + validade; decisão humana explícita |
| 21 | Descarte | Evento de encerramento sem crédito; perda distingue condição/destino |
| 22 | Funcionário | Cadastro existente e ficha contextual reutilizados |
| 23 | Usuario x Funcionario | Ator de sessão separado do recebedor/responsável operacional |
| 24 | Obra/OS/CC | ContextoService e coerência existentes; sem vínculo fictício obrigatório |
| 25 | Histórico contextual | IDs/códigos/nomes congelados também por item |
| 26 | Ficha funcionário | Posse e histórico paginados; cartões mobile com ações diretas |
| 27 | Consultas | Filtros de produto/CA/local/funcionário/tipo/período/contexto e paginação limitada |
| 28 | Alertas | Posse vencida ou próxima até 30 dias; encerramentos não alertam |
| 29 | Limites jurídicos/SST | Não afirma aptidão, conformidade NR ou assinatura jurídica |
| 30 | Documento entrega | Comprovante contextual com dados históricos e template reutilizado |
| 31 | Confirmação | Declaração do operador autenticado sobre recebimento, sem assinatura inventada |
| 32 | Histórico documento | Nomes/CA/contexto históricos; não imprime posse variável |
| 33 | Impressão | Navegador/PDF A4, sem menus/ações; PDFs renderizados para inspeção |
| 34 | Logo | PNGs oficiais ausentes; fallback textual BES/B&S |
| 35 | QR | Pendente, sem implementação improvisada |
| 36 | Anexos | Pendente; registros possuem IDs estáveis, sem storage fictício |
| 37 | Navegação | Dois links em grupo SEGURANÇA / SST; reutiliza Funcionários |
| 38 | Tela EPIs | Pesquisa/CA/situação, política, metadados e configuração permissionada |
| 39 | Tela entregas | Histórico denso, filtros e ações contextuais |
| 40 | UX nova entrega | Página com recebedor/origem, vários itens/saldo e conferência |
| 41 | Múltiplos EPIs | Operação única com itens distintos |
| 42 | Atomicidade | Falha de segundo item/movimento/auditoria não deixa efeitos parciais |
| 43 | Revisão visual | Capturas reais de seis telas por largura e PDFs; inspeção além dos testes |
| 44 | Hierarquia | Destinatário, itens, saldo/CA e confirmação em seções operacionais |
| 45 | Microinterações | Loading/erro/sucesso, ref anti-duplo clique e reenvio da mesma confirmação |
| 46 | Desktop | 1440 px; sidebar/navy, densidade e formulário em duas colunas |
| 47 | Tablet | 768 px; menu existente e campos compatíveis |
| 48 | Mobile | 390 px; campos/ações de 44 px e cartões de posse, sem overflow de página |
| 49 | Acessibilidade | Labels, erros associados, foco, dialog nativo, alertas textuais e tabela rolável |
| 50 | Authorities | EPI_LER/GERENCIAR e EPI_ENTREGA_LER/GERENCIAR na matriz real |
| 51 | Service security | @PreAuthorize nos serviços e integração estoque EPI |
| 52 | CSRF | Cinco mutadores; testes negativos POST e PUT |
| 53 | Auditoria | Configuração/entrega/substituição/devolução/descarte na mesma transação |
| 54 | Mass assignment | DTO estrito; ator/status/saldo rejeitados inclusive PUT |
| 55 | IDOR | IDs não concedem autoridade; origens de outro funcionário/contexto inválido recusadas |
| 56 | Transações | Registro/item/movimento/saldo/chave/auditoria juntos |
| 57 | Concorrência | Testes reais com threads para saldo, chave e dupla devolução |
| 58 | N+1 | Batch em listas e itens/encerramentos; regressões de configs, registros, ficha, detalhe e resumo |
| 59 | Dashboard | Resumo enxuto de linhas em posse/vencidas/a vencer; Home preservada |
| 60 | Banco/scripts | Três tabelas propostas em MySQL/PostgreSQL, FKs/unique/índices/DECIMAL; não executadas |
| 61 | Backend testes | Resultado final registrado na tabela de validação ao fim |
| 62 | Frontend testes | Baseline 98 preservada e testes novos de EPI; resultado final ao fim |
| 63 | Playwright | Baseline 79 preservada e 15 cenários EPI; execução real H2/Preview |
| 64 | E2E principal | Entrega múltipla UI → saldos → ficha → comprovante → devolução |
| 65 | E2E saldo | Lote com item insuficiente não entrega nenhum; cliente também bloqueia |
| 66 | E2E contexto | Coerência Obra/OS/CC e rejeição de incompatibilidade na API real |
| 67 | Documentos criados | Regras, relatório, scripts e atualização dos índices/API/segurança/frontend/AGENTS |
| 68 | RFs atendidos | RF099/RF101 e documentos aplicáveis no módulo conforme tabela de cobertura |
| 69 | RFs parciais | RF100/RF104/RF107/RF206/RF213/RF215 com limites explícitos |
| 70 | RFs pendentes | QR; treinamentos/documentos ocupacionais/anexos/canal SST fora do bloco |
| 71 | Problemas encontrados | JSX; fixture com código inválido; contraste; decimal cliente; seleção e ações mobile |
| 72 | Correções | Fechamento explícito, rollback, JSX, decimal exato/com vírgula, buscas e UI |
| 73 | Regressões | Backend/Node/E2E sem remover testes válidos ou relaxar segurança/assertions |
| 74 | npm audit | Resultado real registrado ao fim; não inferido do build |
| 75 | Secret scan | Heurístico em arquivos textuais/Word, sem imprimir valores; resultado ao fim |
| 76 | git diff --check | Verificação final tracked e whitespace dos novos, staging vazio |
| 77 | Arquivos novos | Inventário ao fim inclui domínio, telas, testes, docs e SQL |
| 78 | Arquivos modificados | Inventário ao fim; nenhum arquivo removido |
| 79 | main intacta | 0137ddbddf403f8331d1a3f6d632428f7614ee15 |
| 80 | Sem banco real | Somente H2 em memória localhost; configuração real não acessada |
| 81 | Sem SQL real | Scripts estáticos não executados |
| 82 | Sem produção | Sem deploy, publicação ou ambiente externo operacional |
| 83 | Sem commit | HEAD checkpoint preservado; staging vazio |
| 84 | Sem push | Nenhum push executado |
| 85 | Sem merge | Sem merge/rebase/history rewrite |
| 86 | Pendências transversais | QR, logo, anexos, valorização, banco, credenciais, HTTPS, migrations, backup/restore, observabilidade e segurança de produção |
| 87 | Riscos restantes | Estoque Double legado; ausência de lote físico; política de reutilização humana; leitura global; homologação real e infraestrutura pendentes |
| 88 | Recomendação | Condicionada à tabela final de verificações, sem autorização de commit |

## Limites e riscos preservados

O retorno ao estoque não avalia segurança de reutilização; a política e a conferência são responsabilidade humana. O estoque não distingue lotes/modelos dentro do mesmo produto; por isso a identidade não muda com saldo positivo. Eventos DECIMAL não migram o saldo legado Double. O hash da idempotência normaliza comando, não certifica assinatura legal. Chave/corpo de reenvio da UI ficam em memória da tela; após recarregar, conferir histórico antes de nova operação. Não há isolamento por obra/empresa/funcionário além das permissões de módulo. Scripts precisam de homologação e autorização, não são migrations aplicadas.

Permanecem pendentes QR, logo oficial, anexos, valorização, banco real, rotação de credenciais históricas, HTTPS/proxy, migrações controladas, backups/restore testado, observabilidade/monitoramento e segurança de sessão/rate limiting em produção. Não iniciado Bloco 8.

## Verificacoes consolidadas

| Verificacao | Execucao real observada |
|---|---|
| Backend | `.\mvnw.cmd clean test`: 563 testes, 0 failures/errors/skipped; exit 0 |
| EPI backend | 81 casos; inclui concorrencia real, rollback e crescimento de consultas |
| Node | `npm.cmd test`: 125 testes, 0 falhas/cancelled/skipped/todo; exit 0 |
| Build | `npm.cmd run build`: Vite concluido; exit 0 |
| Regressao navegador | 18 aprovados (14 EPI + 4 frontend), incluindo 404 e estados de UI |
| npm audit | 0 vulnerabilidades em todos os niveis; exit 0 |
| Secret scan | 303 arquivos textuais/Word, 74 com palavras de seguranca; zero padroes fortes |
| Candidatas de segredo | Nove literais em cinco arquivos, todas existentes no HEAD: fixtures de autenticacao e senha vazia H2; nenhum valor impresso |
| Scripts estaticos | Cada dialeto: 3 tabelas, 16 FKs, 12 indices, unique para chave/movimento, DECIMAL(19,6), sem CASCADE destrutivo |
| Whitespace | git diff --check sem apontamentos; novos arquivos tambem sem trailing whitespace |

O scan e heuristico: nao garante ausencia universal de segredos nem revisa todo o historico Git. Rotacao externa permanece pendente. Testes de autorizacao preservam os quatro perfis, gates HTTP/service, cinco mutadores CSRF e DTO estrito. O inventario reflexivo passou com 132 handlers/57 escritas; somente os totais necessarios foram atualizados, sem remover assertions.

Revisao visual real: telas EPIs, entregas, nova entrega, ficha, modal e documento em 1440/768/390; estados loading/empty/error nas mesmas larguras. Campos EPI com 44 px, labels/foco, texto dos alertas, modal navegavel, layout mobile sem overflow da pagina. Tabelas historicas conservam rolagem horizontal acessivel; posse mobile usa cartoes com acoes diretas. PDFs dos tres viewports foram renderizados com PyMuPDF (renderer disponivel; Poppler ausente), cada um com uma pagina A4 de dois itens: cabecalho, identificacao, tabela, confirmacao e rodape legiveis, sem sidebar/botoes/cortes. Essa amostra nao homologa impressoras reais ou conteudo extremo de 50 itens.


## Investigacao adicional e resultado final

A revisao numerica identificou perda de precisao na conversao BigDecimal/Double em quantidades extremas e risco de fracao parecer inteira. Corrigido com fracionamento decimal antes da conversao, round-trip decimal nas saidas/retornos e verificacao da variacao efetiva do saldo. Tolerancias minimas documentadas nas regras EPI evitam bloquear ruido binario comum; 0.3 consumido em 0.1 + 0.2 termina em zero. Quatro regressoes backend e tres Node adicionais, mais E2E pela UI. Suite completa final: **563 backend (482 + 81), 125 Node (98 + 27), 94 Playwright (79 + 15), sem falhas ou skips**. Build e comandos terminaram com exit 0. Nenhum teste valido foi removido, skipado ou afrouxado.

A segunda suite E2E completa registrou 92 aprovados e um 500 em PUT /obras/13/status (teste B5). Log preservado em TEMP/bes-b7-e2e-structure-failure.log. O handler anterior registrava somente requestId; acrescentamos exclusivamente nomes de classes da excecao/causa ao log servidor, sem corpo, mensagem da excecao ou valores sensiveis; resposta HTTP continua generica. A causa desse episodio nao foi identificada e NAO foi classificada como infraestrutura. Oito repeticoes de UI passaram; duas outras tentativas pararam no login com 429, comprovado nos traces, sem desligar rate limiting. Cinquenta novas obras foram ativadas enquanto detalhe/resumo eram consultados em paralelo: 150 respostas 200, zero falhas (bes-b7-structure-parallel.log). Na suite final, o teste B5 e todos os demais passaram. A ocorrencia nao elucidada permanece risco para revisao independente; nao alegamos ter corrigido sua causa.

**Recomendacao: PRONTO PARA AUDITORIA PRE-COMMIT**, considerando a implementacao incremental e os gates finais aprovados. A auditoria independente deve revisar tambem o episodio 500 registrado. Isso nao autoriza commit, push, banco real ou producao. H2/Preview proprios foram encerrados depois da validacao; processos preexistentes nao foram alterados. Staging vazio; HEAD e main preservados. Pendencias transversais continuam abertas.

## Inventario de arquivos

Novos: 26 arquivos. Modificados: 19 arquivos tracked. Sem arquivos removidos. git diff --stat inclui apenas tracked; novos inventariados sem staging.

Novos:

- docs/bloco7-relatorio-final.md
- docs/epis-seguranca-trabalho.md
- docs/sql/epis-mysql-manual.sql
- docs/sql/epis-postgresql-manual.sql
- frontend/src/pages/EpiDeliveries.jsx
- frontend/src/pages/EpiDeliveryForm.jsx
- frontend/src/pages/EpiDocument.jsx
- frontend/src/pages/EpiEmployee.jsx
- frontend/src/pages/Epis.jsx
- frontend/src/utils/epis.js
- frontend/tests/epis.spec.js
- frontend/tests/epis.test.js
- src/main/java/br/com/almoxarifado/epi/CondicaoEpi.java
- src/main/java/br/com/almoxarifado/epi/DestinoEpi.java
- src/main/java/br/com/almoxarifado/epi/EpiConfiguracao.java
- src/main/java/br/com/almoxarifado/epi/EpiConfiguracaoRepository.java
- src/main/java/br/com/almoxarifado/epi/EpiController.java
- src/main/java/br/com/almoxarifado/epi/EpiInput.java
- src/main/java/br/com/almoxarifado/epi/EpiService.java
- src/main/java/br/com/almoxarifado/epi/ItemEpi.java
- src/main/java/br/com/almoxarifado/epi/ItemEpiRepository.java
- src/main/java/br/com/almoxarifado/epi/MotivoEntrega.java
- src/main/java/br/com/almoxarifado/epi/RegistroEpi.java
- src/main/java/br/com/almoxarifado/epi/RegistroEpiRepository.java
- src/main/java/br/com/almoxarifado/epi/TipoRegistroEpi.java
- src/test/java/br/com/almoxarifado/epi/EpiTests.java

Modificados:

- AGENTS.md
- docs/README.md
- docs/api.md
- docs/autenticacao-autorizacao.md
- docs/security.md
- frontend/README.md
- frontend/package.json
- frontend/src/App.jsx
- frontend/src/auth/permissions.js
- frontend/src/components/AppShell.jsx
- frontend/src/pages/Registries.jsx
- frontend/src/styles.css
- src/main/java/br/com/almoxarifado/controller/RegraNegocioExceptionHandler.java
- src/main/java/br/com/almoxarifado/security/Perfil.java
- src/main/java/br/com/almoxarifado/security/Permissao.java
- src/main/java/br/com/almoxarifado/security/RotasPermissao.java
- src/main/java/br/com/almoxarifado/security/SegurancaConfig.java
- src/main/java/br/com/almoxarifado/service/EstoqueService.java
- src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java

## Conferencia final da retomada

A ultima alteracao esclarece no comprovante que a confirmacao foi declarada pelo operador autenticado, separado do responsavel operacional. Nova suite integral Playwright: 94/94 aprovados em 3.9 minutos, exit 0; nenhum 500 registrado no servidor desta execucao. Os tres PDFs regenerados (390/768/1440) foram renderizados e inspecionados: uma pagina A4 cada, com o novo texto. npm audit repetido: zero vulnerabilidades. Scan final: 303 arquivos textuais/Word, zero padroes fortes de segredo; nenhum whitespace em arquivos novos. As nove ocorrencias literais fracas previamente revisadas permanecem fixtures existentes em HEAD. Servidores H2/Preview desta rodada encerrados; portas 8081/5173 livres. Branch feature/bes-frontend e checkpoint e9df6011cd491ba391e458287ce59a924dd592c5 preservados, main intacta e staging vazio.

## Consolidação após auditoria adversarial — 08/10/2026

A [auditoria pré-commit concluída](bloco7-auditoria-pre-commit.md) sucede as evidências históricas acima e recomenda **COMMITAR**. Validação final: 566 testes backend, 125 Node, 95 Playwright (exit 0); build aprovado, npm audit com zero vulnerabilidades, secret scan e diff check aprovados. Visual/PDF inspecionado em 390, 768 e 1440 px. Correções verificadas de legibilidade do CA e foco dos modais EPI; nenhum requisito novo nesta consolidação.

Inventário atualizado: 27 arquivos novos e 21 modificados, sem exclusões. Além do inventário anterior, entram `docs/bloco7-auditoria-pre-commit.md`, o ajuste opcional de foco EPI em `frontend/src/components/ui.jsx` e o teste de investigação em `src/test/java/br/com/almoxarifado/obras/ObrasTests.java`. Os resultados anteriores foram preservados como histórico, sem representar o resultado final mais recente.

**HTTP 500 do Bloco 5: CAUSA NÃO IDENTIFICADA. NÃO REPRODUZIDO. RISCO RESIDUAL.** Não foi declarado resolvido; o teste investigativo e os logs limitados não alteram regras de negócio do Bloco 5. Não há homologação ou liberação de produção.

Preservadas as pendências de QR Code, logo oficial B&S/BES, anexos/evidências, valorização financeira do consumo, homologação em banco real, rotação externa de credenciais históricas, HTTPS/proxy, migrações controladas, backup e restore testado, observabilidade, monitoramento, sessão/rate limiting em produção e HTTP 500 não explicado. Bloco 8 não iniciado. A rodada de consolidação autoriza exclusivamente revisão, staging, um commit e push de `feature/bes-frontend`, sem merge, reescrita histórica, banco real, SQL real ou deploy.
