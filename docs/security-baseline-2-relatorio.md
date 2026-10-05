# Relatório — Security Baseline 2

Entrega preparada na branch **feature/bes-frontend**. HEAD inicial e final: **6b373feb9cec6748d6d203c1e31b880e662df85b**. O checkpoint inicial tinha working tree limpo. Esta rodada mantém as alterações para revisão, sem staging, commit ou push.

## Autenticação e identidade

Sessão HTTP Spring Security no servidor, cookie BESSESSION HttpOnly/Secure/SameSite=Lax, estado de usuário somente em memória React. Não há JWT, refresh token, senha ou token no storage do navegador. A escolha evita distribuir segredos e simplifica revogação para a SPA própria do monólito.

CSRF em toda escrita, inclusive login/logout, obtido em GET /auth/csrf e renovado após login. CORS sem wildcard, origens explícitas externas por BES_CORS_ORIGINS; proxy local /api preserva Host. Login muda o id da sessão; logout invalida sessão e expira cookie. Inatividade padrão 30 minutos, duração absoluta 8 horas. Reset, inativação e alteração de cadastro/perfil invalidam identidades anteriores pela auth_version na próxima requisição/chamada de service. Falhas desconhecido/inativo/senha incorreta retornam o mesmo 401 genérico; 403 não desloga o usuário.

Usuario é separado de Funcionario, com vínculo opcional ManyToOne LAZY sem cascade. Login normalizado único, nome, hash, perfil, ativo, datas e versões internas. DTOs estritos rejeitam campos internos/inesperados. Senha com pelo menos 10 caracteres Unicode e no máximo 72 bytes UTF-8, sem NUL; BCrypt custo 12. Sem credencial padrão, hash em resposta ou log de senha.

Bootstrap ADMIN opt-in por variáveis externas, apenas sem usuários existentes. Não sobrescreve cadastro. Validar senha e retirar/desabilitar variáveis depois do provisionamento. Guardas impedem perder o último ADMIN ativo ou desativar/mudar o próprio perfil.

## Autorização e contratos

Quatro perfis iniciais e 25 permissões granulares:

| Operação | ADMIN | GESTOR | ALMOXARIFE | CONSULTA |
|---|---|---|---|---|
| Ler catálogo, funcionários, almoxarifados, estoque, movimentos, solicitações e necessidades | Sim | Sim | Sim | Sim |
| Criar/editar produtos, categorias, unidades, funcionários e almoxarifados | Sim | Não | Não | Não |
| Criar solicitação e adicionar itens | Sim | Sim | Sim | Não |
| Aprovar/rejeitar solicitação | Sim | Sim | Não | Não |
| Cadastrar saldo zerado, entrada/saída e transferência | Sim | Não | Sim | Não |
| Configurar limites de estoque | Sim | Sim | Não | Não |
| Iniciar separação e registrar atendimento | Sim | Não | Sim | Não |
| Criar necessidade de compra | Sim | Sim | Sim | Não |
| Consultar auditoria | Sim | Sim | Não | Não |
| Gerenciar usuários, perfis atribuídos e reset de senha | Sim | Não | Não | Não |


Todas as dez famílias operacionais antigas estão protegidas no HTTP e os **61 métodos públicos operacionais não estáticos** possuem @PreAuthorize. Gestão de usuários e consulta de auditoria também têm gates de service. Métodos estáticos de validação pura não alteram dados. Gate HTTP antes da validação do corpo evita respostas de negócio para chamadas sem permissão. Novas rotas são negadas por padrão. Usuário CONSULTA não se promove a ADMIN nem escolhe ator/version/hash em payload.

Novos endpoints: GET /auth/csrf; POST /auth/login; GET /auth/me; POST /auth/logout; GET/POST /usuarios; PUT /usuarios/{id}; PUT /usuarios/{id}/senha; GET /auditoria?pagina=0&tamanho=30 (1–100). Não há DELETE de usuário/auditoria, edição de auditoria ou /auth/refresh. 401 indica falta/invalidez da sessão; 403 indica permissão ou CSRF; 429 indica limite de login.

Leitura permitida nesta matriz é global aos módulos atuais. Ainda não existe escopo por empresa/obra/almoxarifado. Administradores podem gerenciar outros usuários, conforme permissão; não se presume autorização por identidade de funcionário enviada pelo cliente. Segurança por escopo deve preceder módulos que precisem dela.

## Auditoria, histórico e abuso

Auditoria append-only pela API e @Immutable no JPA, paginada/indexada, sem serialização de corpo ou segredo. Registra login, falha, limite, logout, negação, criação/alteração/reset/perfil de usuários e operações críticas dos módulos atuais: mestres, entrada/saída, transferência, limites, aprovação/rejeição, separação, atendimento e necessidade de compra. Ator e login histórico vêm do SecurityContext; responsável operacional continua sendo o funcionário do processo, podendo ser diferente. Entrada/saída referenciam exatamente a movimentação e seus saldos antes/depois; limites registram referência e estados seguros. Não se propõe um diff completo de todo cadastro.

Sucessos operacionais e auditoria compartilham transação: uma falha do journal reverte estoque e ledger. Eventos de segurança têm transação própria. Login só persiste a sessão após sua auditoria; falha inesperada encerra o contexto/sessão. Históricos anteriores não recebem um ator inventado e responsáveis antigos são preservados. UUID de request válido é devolvido, propagado à auditoria/MDC/logs; valores arbitrários são substituídos.

Login limita 8 tentativas por login/5 minutos e 30 por IP/15 minutos, incluindo sucessos, com até 10.000 buckets e expiração. IP bloqueado não cria buckets para novos logins (teste específico de exaustão). Bloqueio é temporário, sem desativar o usuário. Ainda é por processo, sem rate limiter distribuído/WAF; produção com proxy precisa da configuração de IP confiável e proteção de volume.

Headers: nosniff, DENY/frame-ancestors, Referrer-Policy no-referrer e CSP restrita da API, cache-control de segurança; HSTS apenas HTTPS. Cookie Secure é obrigatório fora de dev/test; profiles locais liberam explicitamente HTTP. CSP da API não deve ser aplicada ao HTML da SPA.

## Frontend e qualidade visual

Login real, identidade obtida por /me, menu com nome/perfil/Sair, tela de usuários com criação/edição/ativação/vínculo/perfil/reset, navegação e ações por permissão. Preserva o visual aprovado, tabelas, fluxos operacionais, Lista de Separação e tratamento de conexão/erros. 401 volta ao login e 403 preserva sessão. Corrigido conflito de redirecionamento após login para respeitar a rota solicitada.

Validados 1440/768/390 px: login, usuários, modal e módulos antigos, sem overflow da página. Screenshot mobile revisado visualmente; labels, autocomplete, foco visível e tabulação de login verificados. Tabelas permitem scroll interno. Sem assets oficiais de logo encontrados: fallback tipográfico B&S Engenharia / Plataforma BES, sem inventar logo; sidebar existente preservada.

## Configuração, banco e requisitos

Novas variáveis externas: BES_CORS_ORIGINS, BES_AUTH_SESSION_TIMEOUT, BES_BOOTSTRAP_ADMIN_ENABLED, BES_BOOTSTRAP_ADMIN_USERNAME e BES_BOOTSTRAP_ADMIN_PASSWORD. DB_URL/DB_USERNAME/DB_PASSWORD permanecem externos. .env.example contém somente campos vazios; fixtures fictícias existem exclusivamente em testes isolados, não em configuração comum/produção. Nenhum valor de credencial real foi exibido. A credencial histórica ainda precisa ser rotacionada externamente; o histórico não foi reescrito.

Scripts manuais preparados em db/manual/security-baseline-2-mysql.sql e security-baseline-2-postgresql.sql: apenas tabelas novas de usuários/auditoria, FK opcional para funcionario, unicidade e índices. Nenhum script foi executado, nenhuma tabela/saldo operacional do banco existente foi alterado, nenhum cadastro histórico reconstruído. PostgreSQL futuro ainda exige driver/configuração e integração real; o runtime atual permanece MySQL. Todas as execuções desta rodada usam H2 em memória, não MySQL/produção.

Fonte oficial preservada: Documentacao_Mestre_Plataforma_BES_v1_4.docx, RF001–RF218. RF056 e RF057 implementados para os módulos atuais. RF003, RF038, RF053/RF148, RF058, RF107, RF154/RF155 permanecem parciais no escopo total: quatro perfis iniciais, dashboard atual, auditoria de módulos existentes, reset administrativo, permissão sem escopo por empresa, segredos externos e H2 sem infraestrutura HTTPS completa. RF097, RF156 e RF212 não são marcados completos pela preparação de segurança. Nenhum novo módulo funcional grande foi implementado.

## Revisão adversarial e pendências

Revisados endpoint aberto/role incorreta, senha em resposta/log, hardcode/storage, CSRF/CORS, mass assignment, IDOR/escopo, elevação de privilégio, ator forjado, sessão inválida/inativa/revogada, brute force, N+1 e dependência de banco real. Testes atacam HTTP diretamente e os services; nenhuma autorização depende somente de esconder botão.

Riscos corrigidos nesta rodada: endpoints antes sem identidade; escrita sem CSRF; massa de campos internos em usuário; sessão após reset/perfil/inativação; aprovação/escrita por perfil insuficiente; perda do último ADMIN; auditoria desvinculada da transação; confusão de ator com responsável; proxy tratando mesma origem como CORS externo; redirecionamento concorrente; estado local após 401 em logout/CSRF; exaustão de contador por IP bloqueado; senha aleatória gerada desnecessariamente no teste MVC isolado.

Pendências de produção: rotação histórica externa; HTTPS/reverse proxy confiável; execução aprovada de scripts/grants e backup; store de sessão compartilhado/afinidade se escalar; rate limiter compartilhado/WAF/monitoramento; bootstrap em instância exclusiva (tabela vazia não tem lock distribuído); escopo por obra/empresa e permissões customizadas quando exigidos; retenção/backup/proteção contra DBA do journal. Não há MFA/SSO/e-mail de recuperação ou auditoria criptográfica/WORM. Falhas de negócio não têm um histórico integral de tentativas em todos os módulos; login e negações são auditados. Não é declaração de prontidão irrestrita para produção.

## Validação final

| Verificação | Resultado final |
|---|---|
| .\mvnw.cmd clean test | BUILD SUCCESS — 236 testes, 0 failures, 0 errors, 0 skipped; 01:13 min |
| npm.cmd run build | Vite build aprovado; 1930 módulos; 366,18 kB JS / 108,87 kB gzip |
| npm.cmd test | 63 testes aprovados, 0 falhas/ignorados |
| npm.cmd run test:e2e — H2 + vite preview | 35 testes aprovados; 2,3 minutos; suíte completa sem retries configurados |
| npm.cmd audit | 0 vulnerabilidades |
| git diff --check | Aprovado, exit 0 |
| Staging | Vazio; nenhuma operação git add foi executada |
| Secret scan | Nenhum segredo real/padrão de chave privada/token/cópia da credencial histórica encontrado em 88 arquivos novos/modificados; .env.example somente campos vazios |

Preservados 184 testes backend, 47 frontend e 25 Playwright; acrescentados 52, 16 e 10 respectivamente. A execução anterior de 35 casos teve um timeout no carregamento de um teste antigo de tablet, investigado com respostas HTTP 200; a repetição completa passou mantendo os asserts/timeouts. As falhas de implementação encontradas antes foram corrigidas, incluindo CORS de proxy, redirecionamento e 401 de logout/CSRF. Não há falha restante no resultado final.

**Security Baseline 2 pronta para revisão e consolidação após autorização específica de commit.** Pendências de produção permanecem explicitamente descritas; esta rodada não faz implantação.

Main local permanece 0137ddbddf403f8331d1a3f6d632428f7614ee15 e main remota b2d62a851086af93505a03987fe6edcce9b5eee6, verificadas sem fetch/merge/push. HEAD da branch não mudou. Nenhum commit, push, staging ou reescrita de histórico. Serviços de teste iniciados apenas em H2, com fixtures fictícias e cookies de teste em memória.

## Inventário de arquivos

40 arquivos novos e 48 arquivos rastreados modificados, todos para revisão. Artefatos target/dist/test-results continuam ignorados.

### Criados

- db/manual/security-baseline-2-mysql.sql
- db/manual/security-baseline-2-postgresql.sql
- docs/autenticacao-autorizacao.md
- docs/security-baseline-2-relatorio.md
- frontend/src/auth/AuthContext.jsx
- frontend/src/auth/Login.jsx
- frontend/src/auth/permissions.js
- frontend/src/pages/Users.jsx
- frontend/tests/auth.test.js
- frontend/tests/security-support.js
- frontend/tests/security-users.json
- frontend/tests/security.spec.js
- src/main/java/br/com/almoxarifado/security/Auditar.java
- src/main/java/br/com/almoxarifado/security/AuditoriaAspect.java
- src/main/java/br/com/almoxarifado/security/AuditoriaRepository.java
- src/main/java/br/com/almoxarifado/security/AuditoriaService.java
- src/main/java/br/com/almoxarifado/security/AuthController.java
- src/main/java/br/com/almoxarifado/security/Autorizacao.java
- src/main/java/br/com/almoxarifado/security/BootstrapAdmin.java
- src/main/java/br/com/almoxarifado/security/EntradasSeguranca.java
- src/main/java/br/com/almoxarifado/security/EventoAuditoria.java
- src/main/java/br/com/almoxarifado/security/HttpSeguranca.java
- src/main/java/br/com/almoxarifado/security/Identidade.java
- src/main/java/br/com/almoxarifado/security/LoginRateLimiter.java
- src/main/java/br/com/almoxarifado/security/Perfil.java
- src/main/java/br/com/almoxarifado/security/Permissao.java
- src/main/java/br/com/almoxarifado/security/PoliticaSenha.java
- src/main/java/br/com/almoxarifado/security/RequestIdFilter.java
- src/main/java/br/com/almoxarifado/security/RotasPermissao.java
- src/main/java/br/com/almoxarifado/security/SegurancaConfig.java
- src/main/java/br/com/almoxarifado/security/SessaoValidaFilter.java
- src/main/java/br/com/almoxarifado/security/Usuario.java
- src/main/java/br/com/almoxarifado/security/UsuarioAuthenticationProvider.java
- src/main/java/br/com/almoxarifado/security/UsuarioController.java
- src/main/java/br/com/almoxarifado/security/UsuarioRepository.java
- src/main/java/br/com/almoxarifado/security/UsuarioResposta.java
- src/main/java/br/com/almoxarifado/security/UsuarioService.java
- src/main/resources/application-dev.properties
- src/test/java/br/com/almoxarifado/security/LoginRateLimiterTests.java
- src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java

### Modificados

- .env.example
- AGENTS.md
- docs/README.md
- docs/api.md
- docs/estoque-inteligente.md
- docs/security.md
- frontend/README.md
- frontend/package.json
- frontend/scripts/start-backend-h2.ps1
- frontend/src/App.jsx
- frontend/src/api/client.js
- frontend/src/components/AppShell.jsx
- frontend/src/components/Brand.jsx
- frontend/src/pages/Operations.jsx
- frontend/src/pages/Products.jsx
- frontend/src/pages/Registries.jsx
- frontend/src/pages/RequestDetails.jsx
- frontend/src/pages/Transfers.jsx
- frontend/src/styles.css
- frontend/tests/frontend.spec.js
- frontend/tests/fulfillment.spec.js
- frontend/tests/operations.spec.js
- frontend/tests/stockIntelligence.spec.js
- frontend/vite.config.js
- pom.xml
- src/main/java/br/com/almoxarifado/controller/RegraNegocioExceptionHandler.java
- src/main/java/br/com/almoxarifado/repository/MovimentacaoRepository.java
- src/main/java/br/com/almoxarifado/service/AlmoxarifadoService.java
- src/main/java/br/com/almoxarifado/service/AtendimentoSolicitacaoService.java
- src/main/java/br/com/almoxarifado/service/CategoriaMaterialService.java
- src/main/java/br/com/almoxarifado/service/EstoqueInteligenteService.java
- src/main/java/br/com/almoxarifado/service/EstoqueService.java
- src/main/java/br/com/almoxarifado/service/FuncionarioService.java
- src/main/java/br/com/almoxarifado/service/MovimentacaoService.java
- src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java
- src/main/java/br/com/almoxarifado/service/ProdutoService.java
- src/main/java/br/com/almoxarifado/service/SolicitacaoService.java
- src/main/java/br/com/almoxarifado/service/TransferenciaEstoqueService.java
- src/main/java/br/com/almoxarifado/service/UnidadeMedidaService.java
- src/main/resources/application.properties
- src/test/java/br/com/almoxarifado/ControleAlmoxarifadoApplicationTests.java
- src/test/java/br/com/almoxarifado/controller/BackendOperacionalTests.java
- src/test/java/br/com/almoxarifado/controller/CatalogoMestreTests.java
- src/test/java/br/com/almoxarifado/controller/SolicitacaoControllerTests.java
- src/test/java/br/com/almoxarifado/service/AtendimentoSolicitacaoTests.java
- src/test/java/br/com/almoxarifado/service/EstoqueInteligenteTransferenciaTests.java
- src/test/java/br/com/almoxarifado/service/FluxoSolicitacaoTests.java
- src/test/resources/application-test.properties

### git status --short

```text
 M .env.example
 M AGENTS.md
 M docs/README.md
 M docs/api.md
 M docs/estoque-inteligente.md
 M docs/security.md
 M frontend/README.md
 M frontend/package.json
 M frontend/scripts/start-backend-h2.ps1
 M frontend/src/App.jsx
 M frontend/src/api/client.js
 M frontend/src/components/AppShell.jsx
 M frontend/src/components/Brand.jsx
 M frontend/src/pages/Operations.jsx
 M frontend/src/pages/Products.jsx
 M frontend/src/pages/Registries.jsx
 M frontend/src/pages/RequestDetails.jsx
 M frontend/src/pages/Transfers.jsx
 M frontend/src/styles.css
 M frontend/tests/frontend.spec.js
 M frontend/tests/fulfillment.spec.js
 M frontend/tests/operations.spec.js
 M frontend/tests/stockIntelligence.spec.js
 M frontend/vite.config.js
 M pom.xml
 M src/main/java/br/com/almoxarifado/controller/RegraNegocioExceptionHandler.java
 M src/main/java/br/com/almoxarifado/repository/MovimentacaoRepository.java
 M src/main/java/br/com/almoxarifado/service/AlmoxarifadoService.java
 M src/main/java/br/com/almoxarifado/service/AtendimentoSolicitacaoService.java
 M src/main/java/br/com/almoxarifado/service/CategoriaMaterialService.java
 M src/main/java/br/com/almoxarifado/service/EstoqueInteligenteService.java
 M src/main/java/br/com/almoxarifado/service/EstoqueService.java
 M src/main/java/br/com/almoxarifado/service/FuncionarioService.java
 M src/main/java/br/com/almoxarifado/service/MovimentacaoService.java
 M src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java
 M src/main/java/br/com/almoxarifado/service/ProdutoService.java
 M src/main/java/br/com/almoxarifado/service/SolicitacaoService.java
 M src/main/java/br/com/almoxarifado/service/TransferenciaEstoqueService.java
 M src/main/java/br/com/almoxarifado/service/UnidadeMedidaService.java
 M src/main/resources/application.properties
 M src/test/java/br/com/almoxarifado/ControleAlmoxarifadoApplicationTests.java
 M src/test/java/br/com/almoxarifado/controller/BackendOperacionalTests.java
 M src/test/java/br/com/almoxarifado/controller/CatalogoMestreTests.java
 M src/test/java/br/com/almoxarifado/controller/SolicitacaoControllerTests.java
 M src/test/java/br/com/almoxarifado/service/AtendimentoSolicitacaoTests.java
 M src/test/java/br/com/almoxarifado/service/EstoqueInteligenteTransferenciaTests.java
 M src/test/java/br/com/almoxarifado/service/FluxoSolicitacaoTests.java
 M src/test/resources/application-test.properties
?? db/
?? docs/autenticacao-autorizacao.md
?? docs/security-baseline-2-relatorio.md
?? frontend/src/auth/
?? frontend/src/pages/Users.jsx
?? frontend/tests/auth.test.js
?? frontend/tests/security-support.js
?? frontend/tests/security-users.json
?? frontend/tests/security.spec.js
?? src/main/java/br/com/almoxarifado/security/
?? src/main/resources/application-dev.properties
?? src/test/java/br/com/almoxarifado/security/
```

### git diff --stat

```text
 .env.example                                       |  7 ++++
 AGENTS.md                                          |  4 ++
 docs/README.md                                     |  6 +++
 docs/api.md                                        |  6 ++-
 docs/estoque-inteligente.md                        |  2 +-
 docs/security.md                                   | 12 ++++--
 frontend/README.md                                 | 14 +++++--
 frontend/package.json                              |  2 +-
 frontend/scripts/start-backend-h2.ps1              | 18 ++++++++-
 frontend/src/App.jsx                               |  5 ++-
 frontend/src/api/client.js                         | 29 ++++++++++++--
 frontend/src/components/AppShell.jsx               | 10 +++--
 frontend/src/components/Brand.jsx                  |  2 +-
 frontend/src/pages/Operations.jsx                  |  7 ++--
 frontend/src/pages/Products.jsx                    |  5 ++-
 frontend/src/pages/Registries.jsx                  |  6 ++-
 frontend/src/pages/RequestDetails.jsx              | 10 +++--
 frontend/src/pages/Transfers.jsx                   |  1 +
 frontend/src/styles.css                            |  8 ++++
 frontend/tests/frontend.spec.js                    |  4 +-
 frontend/tests/fulfillment.spec.js                 |  2 +-
 frontend/tests/operations.spec.js                  |  2 +-
 frontend/tests/stockIntelligence.spec.js           |  2 +-
 frontend/vite.config.js                            |  2 +-
 pom.xml                                            |  3 ++
 .../controller/RegraNegocioExceptionHandler.java   |  8 ++++
 .../repository/MovimentacaoRepository.java         |  2 +
 .../almoxarifado/service/AlmoxarifadoService.java  |  9 +++++
 .../service/AtendimentoSolicitacaoService.java     | 10 +++++
 .../service/CategoriaMaterialService.java          |  9 +++++
 .../service/EstoqueInteligenteService.java         |  7 ++++
 .../com/almoxarifado/service/EstoqueService.java   | 14 +++++++
 .../almoxarifado/service/FuncionarioService.java   |  9 +++++
 .../almoxarifado/service/MovimentacaoService.java  |  9 +++++
 .../service/NecessidadeCompraService.java          |  7 ++++
 .../com/almoxarifado/service/ProdutoService.java   | 12 ++++++
 .../almoxarifado/service/SolicitacaoService.java   | 16 ++++++++
 .../service/TransferenciaEstoqueService.java       |  8 ++++
 .../almoxarifado/service/UnidadeMedidaService.java |  9 +++++
 src/main/resources/application.properties          | 12 ++++++
 .../ControleAlmoxarifadoApplicationTests.java      |  1 +
 .../controller/BackendOperacionalTests.java        | 45 +++++++++++-----------
 .../controller/CatalogoMestreTests.java            | 21 +++++-----
 .../controller/SolicitacaoControllerTests.java     | 13 ++++---
 .../service/AtendimentoSolicitacaoTests.java       |  7 ++--
 .../EstoqueInteligenteTransferenciaTests.java      |  9 +++--
 .../service/FluxoSolicitacaoTests.java             |  3 +-
 src/test/resources/application-test.properties     |  2 +
 48 files changed, 327 insertions(+), 84 deletions(-)
```

O diff acima inclui somente os 48 arquivos já rastreados; os 40 novos estão no inventário anterior e ainda não foram adicionados ao índice.

Os processos temporários de H2 e vite preview iniciados para validação foram encerrados após a suíte; os demais processos do usuário foram preservados. Conferência final: índice vazio e git diff --check aprovado.

## Complemento de auditoria final pré-commit

A auditoria adversarial posterior, correções e validações finais estão em [security-baseline-2-auditoria-final.md](security-baseline-2-auditoria-final.md). Os números acima são históricos da implementação; consultar o complemento para os totais atuais e recomendação.
