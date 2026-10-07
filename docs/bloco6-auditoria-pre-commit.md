# Bloco 6 — auditoria adversarial final pré-commit

Rodada de 07/10/2026. Escopo autorizado: auditar a implementação existente de ativos, reproduzir falhas, corrigir e validar; sem reiniciar o bloco. Referência: mestre BES v1.4, contratos de ativos, Security Baseline 2 e protocolos dos Blocos 4/5. Não altera o mapa de cobertura funcional nem declara completos os RFs parciais. Este relatório complementa e sucede, para validação final, o [relatório da implementação](bloco6-relatorio-final.md).

## Checkpoint e inventário anterior às correções

Branch conferida: `feature/bes-frontend`. HEAD esperado/encontrado: `a07482eaa4c559b0112c218572b0c2703e851a37`. Main: `0137ddbddf403f8331d1a3f6d632428f7614ee15`. O estado inicial desta auditoria era **sujo**, com a entrega do Bloco 6: 21 arquivos novos e 15 modificados, nenhum staged. Isso difere do checkpoint limpo anterior à implementação, sem divergência de branch/HEAD. Conferidos status, diff/stat/check e dez commits antes de alterar código. Nenhum reset/restore/clean de Git/stash/rebase foi usado.

Baseline informada: 448 backend, 96 Node, 71 Playwright. O inventário inicial distinguiu prova existente de comportamento ainda por desafiar; não adotou o relatório como prova suficiente.

| Área | Classificação inicial | Evidência existente e foco da auditoria |
|---|---|---|
| Ativos | IMPLEMENTADO E PROVADO | Agregado separado, sem saldo/movimentação de Produto |
| Identificação | IMPLEMENTADO E PROVADO | Normalização, unique, prefixo reservado e concorrência |
| Status | IMPLEMENTADO E PROVADO | Derivado do fluxo; ainda desafiar combinações impossíveis |
| Condição | IMPLEMENTADO E PROVADO | Separada de situação administrativa e reprovação |
| Custódia | IMPLEMENTADO MAS PRECISA AUDITORIA | Responsável/pendência; ampliar ciclos e rollback |
| Localização | IMPLEMENTADO MAS PRECISA AUDITORIA | Desafiar destino contextual e volta à origem |
| Empréstimo | IMPLEMENTADO E PROVADO | Fluxo básico, bloqueios e dupla operação |
| Devolução | IMPLEMENTADO MAS PRECISA AUDITORIA | Dupla devolução já testada; ampliar destinos/falhas |
| Transferência | IMPLEMENTADO MAS PRECISA AUDITORIA | Envio existente; ampliar dupla transferência |
| Chegada/recebimento | IMPLEMENTADO MAS PRECISA AUDITORIA | Confirmação separada; ampliar concorrência/rollback |
| Inspeção | PARCIAL | Núcleo implementado; checklist/recorrência/manutenção pendentes |
| Histórico | IMPLEMENTADO MAS PRECISA AUDITORIA | Snapshots; desafiar renomeação e novas operações |
| Obra/OS/CC | IMPLEMENTADO MAS PRECISA AUDITORIA | Resolver existente; testar contexto e imutabilidade |
| Funcionário | IMPLEMENTADO E PROVADO | Cadastro reutilizado, diferente do usuário autenticado |
| Almoxarifado | IMPLEMENTADO MAS PRECISA AUDITORIA | Cadastro reutilizado; testar IDs/destino/local atual |
| Documentos | PARCIAL | Três documentos; ampliar histórico e revisão A4 |
| Segurança | IMPLEMENTADO MAS PRECISA AUDITORIA | Matriz existente; ampliar todos os handlers/escritas |
| Auditoria | IMPLEMENTADO MAS PRECISA AUDITORIA | Ator e transação; falha em todos os eventos críticos |
| Concorrência | IMPLEMENTADO MAS PRECISA AUDITORIA | Ampliar envios, chegadas e chave simultânea |
| Idempotência | IMPLEMENTADO MAS PRECISA AUDITORIA | Desafiar representação de ausência/texto e replay |
| Frontend | IMPLEMENTADO MAS PRECISA AUDITORIA | Fluxos básicos; seleção, mensagens e contexto |
| Mobile | IMPLEMENTADO MAS PRECISA AUDITORIA | Capturas/testes anteriores; revisão visual efetiva |
| UX/UI | IMPLEMENTADO MAS PRECISA AUDITORIA | Hierarquia, cores, nomes, ações e feedback |
| Acessibilidade | PARCIAL | Sem certificação completa; verificar teclado/labels/foco |

QR, anexos, logo oficial ausente, reserva/manutenção completa, financeiro e produção: **PENDENTE INTENCIONAL**. Nenhuma falha nova estava classificada como provada antes dos experimentos.

## Falhas reproduzidas e corrigidas

| Falha | Impacto | Reprodução antes da correção | Correção/regressão |
|---|---|---|---|
| Ausência e literal `<null>` colidiam na assinatura | Alto: replay podia aceitar comando diferente | Mesmo comando/chave, somente observação alterada: esperava 409, retornava original | Marcadores tipados N/V e comprimento; `observacaoLiteralNullNaoColideComAusenciaNaChave` |
| Empréstimo para Obra mantinha almoxarifado físico | Alto: localização contraditória | Empréstimo contextual confirmado ainda tinha almoxarifado atual | Limpa local físico e congela origem; `emprestimoParaObraEfetivaLocalEDevolucaoRestauraOrigem` |
| Baixado aparecia com inspeção pendente fora do dashboard | Médio: agenda inconsistente | Baixar ativo com data vencida; lista/detalhe divergiam do resumo | Predicado comum exclui BAIXADO; `baixadoNaoGeraAgendaPendenteNaListaDetalheOuResumo` |
| Observação/descrição recusava texto de várias linhas | Médio: textarea não cumpria contrato operacional | LF em observação gerava erro | LF/CR/TAB em campos longos; outros controles e campos curtos estritos; regressão de gravação/replay |
| Pesquisa tratava `_`, `%` e barra invertida como LIKE | Médio: busca retornava ativos indevidos | Buscar TAG_A também encontrava TAGXA | Escape explícito em todos os campos/subconsulta; regressão literal |
| `aberto=false&vencido=true` ignorava fechado | Médio: consulta incoerente | Filtro contraditório devolvia empréstimo aberto vencido | AND cumulativo; regressão combina ambos |
| Inspeção concluída desaparecia com `aberto=false` | Médio: consulta incoerente | Exigia fechamento inexistente para inspeção autônoma | Inspeção concluída por definição, nunca vencida; regressão |

Os logs RED locais `bes-b6-audit-red.log` (3 falhas + 1 erro em quatro testes) e `bes-b6-audit-red2.log` (3 falhas em três testes) registram os sete defeitos antes das correções. Após corrigidos, passaram sem relaxar essas expectativas.

Melhorias adicionais dentro do escopo: nomes legíveis na ficha atual, status/condição localizados, cores com rótulos, resumo do ativo/origem nos modais, destino congelado na chegada, contexto opcional recolhido, seleção preservada fora da pesquisa, feedback de sucesso, proteção síncrona por ref contra duplo clique, espaçamento entre botões, breadcrumb do detalhe e largura mínima da coluna de histórico. Não são apresentadas como sete novas falhas backend. A expectativa inicial de quatro queries no dashboard foi corrigida no teste porque a validação do usuário acrescenta uma consulta constante; compara-se a contagem antes/depois de 15 eventos para detectar crescimento real.

## Evidências e limites de validação

34 casos adicionados ao backend de ativos: 86 no módulo, 482 totais. Cobertura ampliada: envios/chegadas simultâneos, mesma chave simultânea, rollback de cinco tipos de evento, IDs inválidos/estados impossíveis, double counting, queries constantes e todos os 18 handlers com payloads válidos nos quatro perfis. As oito escritas têm verificação de CSRF e campos internos forjados; edição/situação/fechamentos também fazem parte da matriz negativa. Sem testes removidos, ignorados, retries ou aumento de timeout.

Listas de registros e histórico: limite de quatro statements no teste; detalhe: dois. Dashboard compara contagem constante antes/depois (inclui autenticação). Fechamentos da página são consultados em lote; sem join de histórico que multiplique ativos. Isso não substitui homologação de plano/performance em outro dialect ou grande volume.

Node: dois casos novos para rótulos/localização, total 98. Playwright: oito novos casos em `assets-audit.spec.js`, total 79. Verificam comprovante antigo após mudança de Obra, renomeações e devolução; uma escrita no duplo clique; reprovação também pela API; empty/403/rede; seleção preservada e matriz visual. Assertivas antigas de status foram ajustadas ao texto localizado e limitadas ao cartão de estado atual, pois o histórico também contém badges.

Capturas em 1440/768/390: quatro listagens, detalhe, cinco operações (inclui chegada) e três documentos. Revisão visual efetiva de lista/detalhe/cinco modais nas três larguras, listagens operacionais e documentos mobile, além dos cinco documentos A4 renderizados. Página sem overflow; tabelas rolam internamente. Modais ficam dentro da largura, usam scroll interno e confirmação entra na viewport pelo foco de teclado. Botões críticos têm alvo mínimo 44 px; labels, dialog nativo, nomes acessíveis, foco e mensagens status/alert preservados; texto acompanha cores. Os cinco pares de cor dos badges de ativos têm contraste calculado entre 5,59:1 e 7,18:1. Não é certificação WCAG nem teste em dispositivo físico.

11 PDFs: três tipos nas três larguras e dois históricos concluídos, uma página A4 por arquivo nas amostras. Foram renderizados e inspecionados ficha, comprovante de empréstimo, transferência e ambos concluídos: cabeçalho/identificação/dados/rodapé legíveis, sem controles de tela, sem corte. Dados longos/extremos ainda exigem homologação documental; não se declara paginação universal provada. Brand usa fallback textual B&S/BES existente; não foi criada logo ou QR.

Scripts comparados estaticamente às entidades: `bes_ativo` 31 colunas/9 FKs; `bes_registro_ativo` 47 colunas/15 FKs, nas duas propostas. Tipos inteiros de usuário/funcionário distintos, strings/limites, datas, versão, enums em VARCHAR/check, uniques de código/chave/origem/pendência e índices declarados compatíveis. Sem DML/backfill/cascade de exclusão. MySQL exige checks efetivos (8.0.16+); PostgreSQL exige driver/configuração/homologação próprios. Nenhum script foi executado.

A primeira execução integral dos 79 E2E passou. Na repetição após os últimos ajustes de UI, 37 passaram e o seguinte expirou na criação da fixture `page`, antes do corpo do teste, durante salto de aproximadamente uma hora no host. O log H2 registra `Thread starvation or clock leap` no mesmo intervalo. Essa execução não foi contada como aprovada. Servidores reiniciados e suíte integral repetida, sem alterar timeout/retries/assertivas. O resultado definitivo consta no fechamento. O wrapper PowerShell foi ajustado somente para registrar/propagar o código Maven: avisos Java no stderr haviam causado status do shell 1 apesar de BUILD SUCCESS; a repetição final confirmou 482 e `MAVEN_EXIT_CODE=0`.

## Resposta aos 88 pontos

| Nº | Ponto | Resultado/evidência ou limite |
|---|---|---|
| 1 | Branch | feature/bes-frontend, preservada |
| 2 | HEAD | a07482eaa4c559b0112c218572b0c2703e851a37, preservado |
| 3 | Estado inicial | Entrega existente suja, 21 novos/15 modificados, zero staged; checkpoint correto |
| 4 | Arquivos | Inventário completo ao final; inclui entrega original e correções |
| 5 | Produto x Ativo | Agregados separados; ciclo de ativo não altera estoque/movimentações de material |
| 6 | Identidade | Código normalizado/unique, FER reservado, duplicidade concorrente controlada; série opcional, não declarada unique |
| 7 | Status x condição | Estado operacional, condição física, ativo administrativo e reprovação separados |
| 8 | Fonte da verdade | Ativo atual transacional; RegistroAtivo imutável e pendência única para custódia; UI consulta backend |
| 9 | Empréstimo | Disponibilidade/condição/contexto/responsáveis validados; origem congelada e entrega contextual efetiva |
| 10 | Concorrência empréstimo | Lock do ativo; um vencedor em comandos incompatíveis, sem duas custódias |
| 11 | Idempotência | Unique global, comando normalizado, nulo tipado, replay original; comando diferente 409 |
| 12 | Devolução | Evento novo, condição/destino/responsáveis; fecha origem e limpa custódia na mesma transação |
| 13 | Dupla devolução | Origem unique e lock; sem segundo fechamento/efeito, replay não duplica |
| 14 | Dano | Registra fato/observação/condição; não atribui culpa nem valor financeiro |
| 15 | Transferência | Envio e confirmação separados; origem permanece em trânsito |
| 16 | Transferência x empréstimo | Disputam o mesmo ativo/pendência; um fluxo exclui o outro |
| 17 | Dupla transferência | Regressão concorrente de destinos diferentes, um envio vencedor |
| 18 | Chegada | Regressão de dupla chegada; muda para destino original e fecha envio |
| 19 | Condição transferência | Saída/chegada separadas; divergência rastreável por texto, sem culpa automática |
| 20 | Inspeção | Inspetor/ator/condição/resultado/data/observação/próxima data; concluída autônoma |
| 21 | Reprovação | Bloqueia empréstimo na UI e service/API; aprovação não apaga história |
| 22 | Inspeção vencida | Data até hoje gera pendência, exceto baixado; empréstimo bloqueado conforme regra |
| 23 | Baixa/inativação | Motivo/ator/responsável/auditoria, sem DELETE; baixa terminal e sem pendência |
| 24 | Estados impossíveis | Negativas de operação/status/condição/destino/IDs com estado e registros preservados |
| 25 | Funcionário | Cadastro existente, IDs válidos obrigatórios onde aplicável; não inventado campo ativo ausente |
| 26 | Usuário x funcionário | Ator autenticado separado do executor/custodiante operacional e dos seus IDs |
| 27 | Obra/OS/CC | Resolver do Bloco 5 e locks Obra→CC→OS→ativo; IDs coerentes |
| 28 | Histórico contextual | IDs/nomes/códigos congelados; testes após renomear/encerrar e novas operações |
| 29 | Almoxarifado | Cadastro existente; local contextual exclusivo, retorno padrão à origem |
| 30 | Vencidos | Somente empréstimo aberto com previsão anterior a hoje; filtros cumulativos |
| 31 | Consultas | Paginação/filtros existentes, busca literal, aberto/vencido corretos e campos de situação em lote |
| 32 | Double counting | Histórico repetido não multiplica ativos/indicadores; regressão com múltiplos ciclos |
| 33 | N+1 | Statements limitados/constantes nas consultas testadas; limites acima |
| 34 | Auditoria | Ator autenticado, tipo/ID/contexto e efeitos críticos na mesma transação; sem segredo |
| 35 | Rollback | Falha de auditoria reverte ativo/evento/chave; empréstimo já coberto, mais cinco eventos agora |
| 36 | Authorization | 18 handlers permissionados, matriz HTTP quatro perfis com comandos válidos |
| 37 | Service-layer | Authority no service, chamadas diretas negativas mantidas/ampliadas |
| 38 | CSRF | Oito escritas protegidas, incluindo PUT; sessão/CSRF existentes preservados |
| 39 | Mass assignment | DTO estrito nas oito escritas e contexto; ator/status/snapshots internos recusados |
| 40 | IDOR | Sem authority não lê/opera por ID; tipo errado/inexistente controlado. Leitor autorizado lê módulo inteiro, sem isolamento por Obra |
| 41 | Documentos | Ficha atual, empréstimo/devolução e transferência/chegada autenticados com template mestre |
| 42 | Histórico documentos | Comprovantes usam registros/snapshots, sem consultar cadastro atual para substituir história |
| 43 | A4 | 11 PDFs de uma página nas amostras; cinco renderizados inspecionados |
| 44 | Logo | Fallback textual existente; asset oficial ausente, pendente |
| 45 | QR | Pendente; nenhuma rota pública/token improvisado |
| 46 | Anexos | Fotos/upload/evidências binárias pendentes; observações textuais entregues |
| 47 | Revisão visual | Capturas reais e PDFs inspecionados, matriz acima |
| 48 | Consistência UI | AppShell, cards, forms e template existentes preservados; sem redesign geral |
| 49 | Hierarquia | Estado atual e ações antes do histórico; identidade/origem antes da confirmação |
| 50 | Cores | Disponível verde, custódia/trânsito azul, dano/reprovação vermelho, ressalva âmbar, baixa neutra; sempre com texto |
| 51 | Ícones | Lucide existente na navegação e estados; sem nova biblioteca/decorativo desnecessário |
| 52 | Microinterações | Busy/disabled, sucesso, erro e reenvio da mesma tentativa incerta |
| 53 | Duplo clique | Ref síncrona + busy; E2E conta exatamente um POST |
| 54 | Loading/empty/error | ResourceView/status/alert, vazio, 403, erro rede e tentar novamente testados |
| 55 | UX empréstimo | Identificação/origem/condição/responsáveis/previsão visíveis; contexto opcional recolhido |
| 56 | UX devolução | Custodiante sugerido editável, recebedor obrigatório e retorno padrão explícito |
| 57 | UX transferência | Origem e destino, condição e responsáveis; chegada mostra destino congelado |
| 58 | UX inspeção | Ativo/condição/resultado/observação/próxima data; reprovação com aviso textual |
| 59 | Detalhe ativo | Código/nome/status/condição/local/custódia/contexto/avisos/ações antes do histórico |
| 60 | Navegação | Quatro entradas ATIVOS, permissões existentes, breadcrumb detalhe corrigido e menu mobile preservado |
| 61 | Desktop | 1440 px inspecionado e testado, sem overflow de página |
| 62 | Tablet | 768 px inspecionado e testado; tabelas com scroll interno |
| 63 | Mobile | 390 px inspecionado e testado; modais roláveis/ações acessíveis por foco/alvos 44 px |
| 64 | Acessibilidade | Labels/foco/teclado/dialog/nomes/status/alert/texto com cor; sem certificação WCAG |
| 65 | Documentos visual | Cabeçalho B&S/BES, identificação, metadados/contexto e fechamento; impressão limpa |
| 66 | Dashboard | Dois indicadores úteis preservados, sem gráficos novos; baixado excluído coerentemente |
| 67 | Scripts | Comparação estática de colunas/tipos/FKs/unique/índices/checks; não executados |
| 68 | Pendências transversais | Lista explícita abaixo, sem ocultar rotação/homologação/produção |
| 69 | Problemas encontrados | Sete defeitos backend reproduzidos; ajustes de legibilidade/interação contextual |
| 70 | Correções | Marcadores tipados, local efetivo, pendência coerente, multiline, LIKE literal, filtros e inspeção concluída; UI acima |
| 71 | Regressões adicionadas | +34 backend, +2 Node, +8 E2E; casos/assertivas concretos acima |
| 72 | Backend final | 482, zero falhas/erros/ignorados, BUILD SUCCESS |
| 73 | Frontend final | 98 Node, zero falhas/ignorados |
| 74 | Playwright final | 79, zero falhas, 1 worker, zero retries |
| 75 | Build | Vite aprovado após ajustes finais |
| 76 | npm audit | Zero vulnerabilidades, em todas as severidades |
| 77 | Secret scan | 277 arquivos textuais/Word; zero padrões fortes; nove candidatas são fixtures/launcher inalterados |
| 78 | git diff --check | Aprovado; whitespace dos 23 novos também aprovado, sem staging |
| 79 | Main intacta | Ref preservada; sem checkout/mutação de main |
| 80 | Sem banco real | Apenas H2 em memória nos testes/launcher; não acessados MySQL/PostgreSQL |
| 81 | Sem produção | Sem publicação/deploy/chamada operacional externa |
| 82 | Sem SQL real | Scripts somente revisados; DML de fixtures exclusivamente H2 |
| 83 | Sem commit | HEAD preservado; staging vazio |
| 84 | Sem push | Não executado |
| 85 | Sem merge | Não executado |
| 86 | Riscos restantes | Homologação/produção, amostras documentais e limites de segurança descritos; não bloqueiam commit do escopo validado |
| 87 | RFs alterados | Cobertura mantida; correções reforçam RF014–RF019, RF046, RF053–RF057, RF170–RF178 e documentos aplicáveis; sem promover RF parcial |
| 88 | Recomendação | **COMMITAR** o Bloco 6 validado; esta rodada não executa commit |

## Pendências preservadas

QR autenticado/permissionado, logo oficial, fotos/anexos com controles de segurança, valorização financeira/método de custo, reserva/manutenção completa, checklist e periodicidade automática, notificações/scheduler, assinatura jurídica e mobilização completa permanecem pendentes. RF205–RF218 seguem o mapa incremental de [ativos](ferramentas-equipamentos.md): documentos existentes e contexto provados no módulo; dependências de identidade oficial/QR/PDF especializado/assinaturas não promovidas a completas.

Antes de produção: homologar banco real/dialect/locks/constraints/plano, autorizar migrações controladas, confirmar rotação das credenciais históricas comprometidas, configuração externa, HTTPS/proxy, backup e restore testado, observabilidade/monitoramento/retenção, validar sessão/rate limiting e SCA backend. Nenhuma dessas ações foi realizada ou autorizada por esta auditoria. Não iniciado Bloco 7, financeiro, NF/XML/IA, BI, EPI, frota ou RH; não alterada arquitetura de sessão/CSRF.

## Fechamento e inventário final

**COMMITAR.** As falhas materiais reproduzidas foram corrigidas e a validação integral final passou. Essa recomendação abrange a entrega e correções listadas, preservando as pendências e sem autorizar banco real/produção. Nenhum commit foi executado.

| Gate final | Resultado |
|---|---|
| `mvnw.cmd clean test` | 482 testes, zero falhas/erros/ignorados; BUILD SUCCESS; 2:04; exit 0 |
| `npm.cmd test` | 98/98, zero falhas/ignorados; exit 0 |
| `npm.cmd run build` | Vite aprovado após todas as alterações de código; exit 0 |
| `npx.cmd playwright test --max-failures=1` | 79/79, 6,1 minutos, 1 worker/zero retries; exit 0 |
| `npm.cmd audit --json` | Zero vulnerabilidades; exit 0; executado após E2E final |
| Secret scan | 277 arquivos textuais/Word; palavras de segurança revisadas, zero padrões fortes; nove atribuições literais em testes/launcher, todas inalteradas no HEAD |
| `git diff --check` + novos | Aprovados; zero erros de whitespace nos 23 novos, sem staging |
| Checkpoint final | Branch/HEAD/main preservados; zero staged; 23 novos e 18 modificados |

Scan procurou password/senha/secret/token/API key/authorization/bearer/private key/DB_PASSWORD/cookie/session/JDBC sem imprimir valores. Configuração datasource de usuário/senha permanece externa. As candidatas são `start-backend-h2.ps1` (senha vazia H2), testes de autenticação/cookies/Baseline 2 e quatro perfis de `security-users.json` (fixtures fictícias existentes). É scan heurístico, não garantia universal de ausência de segredos ou auditoria do histórico Git. A rotação histórica continua pendente e não foi executada.

Logs locais em TEMP: `bes-b6-audit-final-backend.log`, `bes-b6-audit-final-node.log`, `bes-b6-audit-final-build.log`, `bes-b6-audit-final-e2e-rerun.log`, `bes-b6-audit-final-npm.json` e `bes-b6-audit-final-secrets.json`. A execução interrompida está em `bes-b6-audit-final-e2e.log`, correlacionada a `bes-b6-audit-final-h2.log`; não foi ocultada nem considerada aprovada. Processos H2/preview criados pela auditoria são encerrados após a validação. Estatística tracked: 18 arquivos, 127 inserções/14 remoções; arquivos novos constam separadamente abaixo, pois `git diff --stat` não os inclui sem staging.

### Arquivos novos (23)

- `docs/bloco6-auditoria-pre-commit.md`
- `docs/bloco6-relatorio-final.md`
- `docs/ferramentas-equipamentos.md`
- `docs/sql/ferramentas-equipamentos-mysql-manual.sql`
- `docs/sql/ferramentas-equipamentos-postgresql-manual.sql`
- `frontend/src/pages/AssetDocument.jsx`
- `frontend/src/pages/Assets.jsx`
- `frontend/src/utils/assets.js`
- `frontend/tests/assets-audit.spec.js`
- `frontend/tests/assets.spec.js`
- `frontend/tests/assets.test.js`
- `src/main/java/br/com/almoxarifado/ativos/Ativo.java`
- `src/main/java/br/com/almoxarifado/ativos/AtivoRepository.java`
- `src/main/java/br/com/almoxarifado/ativos/AtivosController.java`
- `src/main/java/br/com/almoxarifado/ativos/AtivosInput.java`
- `src/main/java/br/com/almoxarifado/ativos/AtivosService.java`
- `src/main/java/br/com/almoxarifado/ativos/CondicaoAtivo.java`
- `src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java`
- `src/main/java/br/com/almoxarifado/ativos/RegistroAtivoRepository.java`
- `src/main/java/br/com/almoxarifado/ativos/ResultadoInspecao.java`
- `src/main/java/br/com/almoxarifado/ativos/StatusAtivo.java`
- `src/main/java/br/com/almoxarifado/ativos/TipoRegistroAtivo.java`
- `src/test/java/br/com/almoxarifado/ativos/AtivosTests.java`

### Arquivos modificados (18)

- `AGENTS.md`
- `docs/README.md`
- `docs/api.md`
- `docs/autenticacao-autorizacao.md`
- `docs/security.md`
- `frontend/README.md`
- `frontend/package.json`
- `frontend/src/App.jsx`
- `frontend/src/auth/permissions.js`
- `frontend/src/components/AppShell.jsx`
- `frontend/src/components/ui.jsx`
- `frontend/src/pages/Dashboard.jsx`
- `frontend/src/styles.css`
- `src/main/java/br/com/almoxarifado/security/Perfil.java`
- `src/main/java/br/com/almoxarifado/security/Permissao.java`
- `src/main/java/br/com/almoxarifado/security/RotasPermissao.java`
- `src/main/java/br/com/almoxarifado/security/SegurancaConfig.java`
- `src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java`

O inventário inclui a entrega original não commitada. Artefatos PNG/PDF/trace ficam ignorados em `frontend/test-results`; logs em TEMP. Sem cookie/storageState no repositório, sem edição da documentação mestre.
