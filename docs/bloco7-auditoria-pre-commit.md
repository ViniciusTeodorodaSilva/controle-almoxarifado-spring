# Bloco 7 — auditoria adversarial pré-commit

Rodada de 08/10/2026. Fonte funcional: Documentação Mestre BES v1.4, RF001–RF218. Auditoria do incremento EPI/SST existente, sem reiniciar implementação ou iniciar Bloco 8. Branch `feature/bes-frontend`, HEAD `e9df6011cd491ba391e458287ce59a924dd592c5`; staging vazio na inspeção inicial. Todos os arquivos preservados. Sem banco real, SQL real, commit, push, merge, deploy ou alteração de main.

## Escopo e RFs

Releitura de AGENTS, mestre, relatório B7, regras EPI, estoque, segurança/autorização, compras, contexto B5 e ativos B6, confrontada com entidades, services, controllers, UI, scripts e testes. RF099/RF101 atendidos no incremento; RF100/RF104/RF107 parciais. RF102/RF103/RF105/RF106 permanecem fora deste bloco. Documentos RF205–RF218 avaliados conforme [cobertura detalhada](epis-seguranca-trabalho.md): QR pendente, logo oficial ausente, impressão/PDF por navegador, confirmação operacional sem assinatura jurídica. Preparação não equivale a requisito concluído.

## Achados e investigação

| ID | Severidade | Evidência, causa e tratamento |
|---|---|---|
| B7-A01 | Risco residual relevante | HTTP 500 anterior em `PUT /obras/13/status`, ativação PLANEJADA → ATIVA pelo teste `structure.spec.js:35`. Log `TEMP/bes-b7-e2e-structure-failure.log`: 92 aprovados/1 falha; dialog permaneceu aberto. O log do servidor daquela execução não fornece stack trace recuperável. **CAUSA NÃO IDENTIFICADA; NÃO REPRODUZIDO; RISCO RESIDUAL.** Não afirmar resolução nem atribuir infraestrutura. |
| B7-A02 | Lacuna de cobertura encerrada | Snapshot de funcionário/matrícula, responsável, almoxarifado e nomes/número Obra/CC/OS após renomear todos os cadastros não tinha uma prova conjunta. Novo teste `auditoriaSnapshotsPessoaEContextoAposRenomearCadastros` confronta registro e item históricos, ator e horário. Nenhuma alteração de regra de produção. |
| B7-A03 | Lacuna de cobertura encerrada | Centro corporativo sem obra precisava de prova no fluxo EPI. Novo teste `auditoriaEntregaCentroCorporativoSemObraFicticia` verifica evento e SAÍDA sem Obra/OS fictícias. Nenhuma alteração de regra de produção. |

Investigação B5: leitura de `EstruturaService.statusObra`, controller, `ResumoEstruturaService`, autorização, auditoria e teste UI original. Ativação bloqueia/atualiza Obra e audita na transação; consultas não adquirem locks de estoque. Novo teste `investigacaoAtivacaoHttpComDetalheEResumoSimultaneos` cria 30 obras, sincroniza três requisições MockMvc autenticadas por barreira, incluindo CSRF no PUT, e exige 90 respostas 200, estado ATIVA e um evento de transição por obra. É teste de investigação da hipótese de consultas concorrentes, não regressão de causa identificada. Não força um interleaving específico dentro do banco nem reproduz todos os fatores ambientais do incidente anterior.

Evidências anteriores conservadas: oito repetições UI aprovadas, duas tentativas interrompidas por login 429; 50 obras/150 respostas 200 em consultas/ativação paralelas; duas suítes completas posteriores aprovadas. Nenhum rate limit desligado, assertion relaxada ou teste válido removido. A rodada atual acrescenta nova execução completa e teste de investigação, sem refatorar B5 ou classificar o incidente como resolvido.

A primeira rodada focada desta auditoria passou o teste B5 e o teste de snapshots; o novo teste corporativo falhou por enum inexistente na fixture (`CORPORATIVO`). Corrigido para o tipo existente `ADMINISTRATIVO`. Essa falha de autoria de teste não foi tratada como aprovação; a validação final usa recompilação limpa.

## Domínio, estoque e histórico

`EpiConfiguracao` é extensão 1:1 do Produto, sem coluna de saldo. `RegistroEpi`/`ItemEpi` imutáveis guardam fatos e snapshots; posse deriva quantidade entregue menos encerramentos. Estoque físico permanece exclusivamente `Estoque`, e movimentos passam por `EstoqueService.movimentarEpi`. Nenhum agregado de material/ativo/estoque duplicado.

CA manual e sua validade não são validade física nem troca recomendada. Bloqueio de identidade com saldo positivo impede relabelar modelo/tamanho/fabricante/CA sem estoque por lote. Substituição exige origem do mesmo funcionário, condição/destino anteriores e quantidade explícitos; encerramento guarda contexto antigo e nova SAÍDA guarda contexto atual admissível. Retorno só credita por decisão explícita ESTOQUE/NOVO, políticas atual/histórica, identidade compatível e validade física. Segregação, descarte e perda não creditam. Nenhuma avaliação automática de aptidão/conformidade legal, assinatura jurídica ou culpa.

Provas H2 existentes reexecutadas: último saldo concorrente com um vencedor, múltiplos itens com insuficiência e falha após primeiro movimento, auditoria revertendo saldo/evento/chave, mesma chave concorrente e replay, chave com payload distinto, dupla devolução concorrente/repetida, substituição atômica e quantidade decimal extrema. Proteções DECIMAL/Double não migram saldo legado; tolerâncias mínimas documentadas e regressão 0.3 → 0.1 + 0.2 preservadas.

Locks confrontados no código: entrega contexto Obra → CC → OS → origens ordenadas → produtos ordenados → pares de estoque; fechamento origens → produtos → estoque sem bloquear contexto histórico; configuração Produto. Preservados protocolos dos Blocos 1–6. Contexto incompatível rejeitado; sem contexto e CC corporativo admitidos. Compra/recebimento não são consumo, e não há custo consumido sem valorização aprovada.

## Inventário HTTP e segurança

Todos os handlers abaixo são **permissionados**, exigem sessão; os cinco mutadores exigem CSRF. Nenhuma rota EPI pública.

| Método e rota | Authority HTTP e service |
|---|---|
| GET `/epis` | EPI_LER |
| GET `/epis/{id}` | EPI_LER |
| POST `/epis` | EPI_GERENCIAR |
| PUT `/epis/{id}` | EPI_GERENCIAR |
| GET `/epi-entregas` | EPI_ENTREGA_LER |
| POST `/epi-entregas` | EPI_ENTREGA_GERENCIAR |
| GET `/epi-entregas/{id}` | EPI_ENTREGA_LER |
| GET `/epi-entregas/posse` | EPI_ENTREGA_LER |
| GET `/epi-entregas/resumo` | EPI_ENTREGA_LER |
| GET `/epi-funcionarios/{id}` | EPI_ENTREGA_LER |
| POST `/epi-devolucoes` | EPI_ENTREGA_GERENCIAR |
| POST `/epi-descartes` | EPI_ENTREGA_GERENCIAR |

ADMIN/GESTOR gerenciam configuração e operação; ALMOXARIFE lê e opera, sem configurar; CONSULTA lê. Os quatro perfis testados no HTTP/service; DTO estrito rejeita mass assignment inclusive PUT; IDs não concedem authority; origem de outro funcionário é recusada. A política atual permite leitura global por módulo, sem isolamento individual ou por Obra/empresa: não prometer IDOR resolvido por tenancy inexistente. Ator autenticado e responsável operacional permanecem separados, auditoria atômica e respostas sem dados internos. Security Baseline 2 mantém sessão/CSRF/revogação e inventário 132 handlers/57 escritas.

## Performance e scripts

Configurações carregam Produto/unidade em lote; histórico busca itens por conjunto de registros e encerramentos agregados; ficha combina páginas de posse/histórico; documento usa detalhe histórico; resumo faz três counts de linhas, sem somar unidades heterogêneas. Consultas de alertas usam subquery agregada, sem hidratar grafos por item. Regressões de contagem Hibernate com aumento de linhas/configs/ficha/detalhe/resumo; sem EAGER indiscriminado. Consulta agregada por linha em comandos de até 50 itens é trabalho limitado de escrita, não N+1 nas listas. H2 não homologa planos/carga do banco alvo.

Revisão estática MySQL/PostgreSQL: três tabelas, 16 FKs, 12 índices por dialeto; identidade/auto_increment, snapshots de contexto, DECIMAL(19,6), checks de quantidade/prazo, unique de chave e movimento, sem CASCADE de exclusão/backfill/saldo duplicado. Não executados. Collation e igualdade case-sensitive da chave, índices de consultas agregadas, locks e tipos do schema legado exigem homologação autorizada. Imutabilidade JPA não impede um DBA de editar fatos: grants/backup são gate de implantação.

## Validação e revisão visual

Resultados da execução interrompida foram conservados e a consolidação da recuperação consta abaixo. Logs anteriores usam prefixo `bes-b7-audit-` no TEMP; os novos usam `bes-b7-recovery-`. Capturas/PDFs fictícios ficam em `frontend/test-results`, ignorado pelo Git. Cookies/traces não são artefatos para versionamento.

## Pendências preservadas

QR, logo oficial B&S, anexos/evidências, valorização financeira, homologação banco real, rotação externa de credenciais históricas, HTTPS/proxy, migrações controladas, backup/restore testado, observabilidade/monitoramento e validação de sessão/rate limiting em produção. Estoque Double e ausência de lote físico, política humana de retorno, leitura global e chave de reenvio em memória da tela permanecem limites explícitos. Sem Bloco 8.

## Recuperação após travamento — 08/10/2026

Checkpoint recuperado: branch `feature/bes-frontend`, HEAD `e9df6011cd491ba391e458287ce59a924dd592c5`. Os seis comandos de recuperação e a inspeção explícita do diff staged/arquivos não rastreados confirmaram staging vazio, 20 arquivos tracked modificados e 27 arquivos novos (47 candidatos, incluindo os diretórios EPI). Diff sem erros de whitespace. Nenhuma alteração descartada; evidências anteriores preservadas.

O log `TEMP/bes-b7-audit-backend.log` comprova execução completa de `clean test`: **566 testes, zero falhas/erros/ignorados, BUILD SUCCESS**, encerrada às 00:19:48. Node comprova 125/125; build/npm/scan também completos. A falha de fixture corporativa focada, descrita acima, foi superada pela suíte limpa. O E2E da auditoria ficou interrompido sem resumo/exit final e não é aprovação. O relatório estava parcial na consolidação da validação, revisão visual e decisão. Não se reiniciou a auditoria de domínio nem a investigação B5 já documentadas.

Portas 8081/5173 livres na inspeção inicial; processos antigos de outros trabalhos preservados. H2 exclusivamente em memória, pelo launcher de teste com usuários fictícios, e Vite Preview temporário. A execução direta do script foi impedida pela ExecutionPolicy local e a tentativa via Start-Process rejeitada pela ferramenta. O conteúdo do launcher foi executado em sessão PowerShell, sem mudar políticas persistentes e mantendo os mesmos argumentos H2. Readiness exigiu HTTP 200 direto e pelo proxy.

A primeira tentativa recuperada de E2E encontrou falha ambiental comprovada pelo trace: o bundle pré-existente chamava `http://localhost:8081/auth/me` de outra origem, com status de rede -1 no navegador. API direta autenticada funcionava; a tela mostrava falha ao verificar sessão. Não é evidência do HTTP 500 B5. A tentativa foi interrompida, com logs/traces conservados. Bundle reconstruído com `VITE_API_URL=/api`, Preview com proxy H2 na 8081. Só processos temporários identificados desta recuperação foram encerrados/reiniciados.

### Achado visual adicional

**B7-A04 — legibilidade do CA no catálogo mobile (corrigido).** Captura real a 390 px mostrou coluna CA comprimida, com `12345` quebrado em dígitos verticais e linhas excessivamente altas. Classe específica `epi-config-list`, largura mínima de 120 px na coluna e número sem quebra preservam a leitura dentro da tabela rolável existente. Nenhuma mudança de layout de outros módulos. Regressão nas três larguras exige CA `12345`, altura menor que 24 px e ausência de overflow da página. Capturas de listas passam a aguardar tabela e término do carregamento; imagens anteriores de 768 px mostravam loading e não provavam tabela carregada. Artefatos anteriores copiados para `frontend/test-results/recovery-before-ca-fix`, sem exclusão.

Formulários, ficha, modal e documentos inspecionados em 390/768/1440 preservam hierarquia, espaçamentos, identidade BES e confirmação operacional. Campos/ações de 44 px, foco de teclado/Escape, estados loading/vazio/erro e reenvio cobertos. Tabelas largas usam rolagem interna; posse mobile conserva cartões. Três PDFs reais renderizados com PyMuPDF (Poppler ausente do PATH): uma página A4 por largura, identificação, CA, responsáveis e aviso de confirmação legíveis, sem corte ou sobreposição. Prova com dois itens fictícios; não equivale a homologação de todo volume documental nem auditoria WCAG completa.

**B7-A05 — foco de teclado saía do modal EPI (corrigido).** A primeira suíte completa recuperada com proxy correto terminou com 94 aprovados/1 falha, exit 1, no teste novo de 20 Tabs do modal EPI. O diálogo nativo permitia foco fora do conteúdo no ciclo. `Modal` ganhou opção `trapFocus`, habilitada somente nas telas EPI (configuração e encerramento); Tab/Shift+Tab circulam pelos controles visíveis e habilitados. Comportamento dos demais módulos preservado. Nenhuma assertion enfraquecida, teste removido ou skip. Esse resultado intermediário não é aprovação final; evidência em `bes-b7-recovery-e2e-final.log` e diretório `recovery-final-20261008`.

A rodada focada intermediária terminou 2/4, exit 1: a contenção já passou, mas Escape não restaurava o foco; também houve timeout de carregamento da lista em 768 px. O cleanup passou a restaurar explicitamente o elemento de abertura quando conectado. Após isso, novo H2 isolado e execução integral: teste de teclado e as três inspeções responsivas passaram, incluindo tabela carregada e CA legível. Não foi relaxado timeout nem removida assertion. O timeout anterior permanece registrado como falha intermediária de carregamento, sem atribuição de causa não demonstrada. Evidência: `bes-b7-recovery-focused.log`.

## Encerramento e validação final da recuperação

Após as correções materiais de frontend, executada novamente a validação completa solicitada. A suíte backend anterior permanece como evidência válida; sua repetição nesta etapa atende à validação final após correções, sem repetir investigação ou implementação já concluídas.

| Verificação | Resultado final | Evidência no TEMP |
|---|---|---|
| Backend: `mvnw.cmd clean test` | **566 aprovados**, zero falhas/erros/ignorados, BUILD SUCCESS, exit 0 | `bes-b7-recovery-backend-final.log` |
| Frontend: `npm.cmd run build` | Aprovado, exit 0; bundle com `/api` | `bes-b7-recovery-build-complete.log` |
| Frontend: `npm.cmd test` | **125 aprovados**, zero falhas/cancelados/ignorados, exit 0 | `bes-b7-recovery-node-complete.log` |
| Playwright completo | **95 aprovados**, 5,9 minutos, exit 0, sem retries/skips | `bes-b7-recovery-e2e-complete.log` |
| H2 + Vite Preview | Readiness HTTP 200 direto/proxy, H2 em memória, usuários fictícios | `bes-b7-recovery-h2-final.log`; saída da sessão E2E |
| `npm audit` | Zero vulnerabilidades, exit 0 | `bes-b7-recovery-npm-final.json` |
| Secret scan dos candidatos atuais | 48 arquivos (21 modificados/27 novos), zero achados fortes/credencial histórica/whitespace, staging vazio | `bes-b7-recovery-secrets-final.json` |
| Git diff check/status/stat | Sem erros; branch/HEAD preservados; staging vazio | Conferência final em sessão |

O scan final é heurístico dos arquivos candidatos, complementando a evidência recuperada do scan de 304 arquivos textuais/Word. Não revoga credenciais históricas nem constitui scanner SCA de backend. Diff stat tracked: 21 arquivos, 172 inserções/13 exclusões; arquivos novos são inventariados separadamente pelo Git. Logs/traces/cookies e dados H2 não serão versionados.

Evidências visuais finais: 27 capturas `b7-*` (seis telas e loading/vazio/erro nas três larguras), três PDFs A4 e suas renderizações `b7-documento-*-final-render-1.png` em `frontend/test-results`. Revisão confirmou CA sem quebra vertical; listas carregadas; formulários, modal e cartões mobile operáveis; documentos sem corte/sobreposição. Teste de teclado mantém foco no diálogo e restaura o botão ao fechar com Escape. Testes de posse/encerramento, retries, permissões e contexto preservados.

**HTTP 500 B5: CAUSA NÃO IDENTIFICADA / NÃO REPRODUZIDO / RISCO RESIDUAL.** Investigação existente preservada. Nova suíte limpa backend inclui a investigação de 30 obras/90 respostas; E2E final inclui novamente a transição estrutural original, aprovada. Log do servidor final sem ocorrência da mensagem de erro interno. Isso aumenta a evidência de não reprodução, sem identificar causa raiz nem comprovar resolução do incidente histórico.

### Recomendação final: COMMITAR

Recomenda-se versionar o incremento Bloco 7 na branch de desenvolvimento: correções materiais de legibilidade/acessibilidade verificadas, regressões preservadas e todas as suítes finais completas aprovadas. O risco histórico B5 permanece documentado para investigação se reaparecer; nenhum defeito reprodutível desse incidente fundamenta correção adicional nesta rodada. A recomendação não conclui RFs parciais/pendentes nem libera produção.

Permanecem todas as pendências globais listadas acima. Nenhum banco real, SQL manual, produção, deploy, Bloco 8, git add, commit, push, merge ou alteração de main. Trabalho existente preservado; staging vazio. Servidores temporários da recuperação encerrados ao concluir, mantendo processos de outros trabalhos. Auditoria encerrada.
