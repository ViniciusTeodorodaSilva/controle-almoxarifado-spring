# Security Baseline 2 — auditoria adversarial final pré-commit

05/10/2026. Branch `feature/bes-frontend`, HEAD `6b373feb9cec6748d6d203c1e31b880e662df85b`. A árvore já continha a baseline não commitada; esta rodada preservou essas alterações. Sem novos módulos.

Fonte: Documentação Mestre BES v1.4. RF056/RF057 verificados nos módulos atuais. RF003, RF053/RF148, RF058, RF107, RF154/RF155 continuam parciais nos limites de autenticacao-autorizacao.md. RF205–RF218 avaliados: sem geração documental/QR nova; segurança não conclui RF212. Sem assets oficiais B&S; fallback preservado.

## Achados e correções

- **Bootstrap concorrente:** count em tabela vazia permitia dois administradores iniciais com logins diferentes. Agora o marcador interno nullable bootstrap_chave=ADMIN_INICIAL é único no banco. JPA e scripts manuais alinhados. Teste H2 força os dois runners a observar tabela vazia: só uma transação vence, a outra falha de forma segura. Scripts não executados externamente. Esquema já provisionado exige preparação aprovada dessa coluna/constraint antes da implantação.
- **Log de erro inesperado:** handler registrava mensagem e stack trace completos, potencialmente sensíveis. Substituído por texto fixo com correlação no MDC. Teste captura o logger e verifica ausência de marcador sensível/throwable.
- **Log MVC DEBUG:** a primeira execução revelou resposta CSRF nos logs. Spring Web/Security agora têm INFO explícito, evitando herança de DEBUG do root. Overrides externos não devem reativar DEBUG/TRACE com dados reais. Nenhum valor é reproduzido neste relatório.
- **ID de sessão por URL:** transporte não era restrito explicitamente. Tracking somente COOKIE agora configurado e observado no container; tentativa por URL recusada no browser.
- **HEAD /auth/me:** gate específico negava HEAD apesar do GET funcionar. Corrigido para HEAD com sessão válida.

## Os 41 resultados solicitados

| # | Item | Resultado/evidência |
|---|---|---|
| 1 | Sessão | HttpSession no container, local à JVM; SecurityContext em atributo. Sem JWT/store externo. ID opaco chega em Set-Cookie e retorna em Cookie. |
| 2 | Cookie | BESSESSION, HttpOnly, SameSite=Lax, Path=/, Secure=true comum/produção; false só dev/test. Cookie de sessão sem Max-Age no login. Container real com Secure=true e browser HTTP com false verificados. |
| 3 | Fixation | ChangeSessionIdAuthenticationStrategy troca ID; container rejeita cookie antigo e CSRF anterior. |
| 4 | Revogação | Logout invalida sessão e expira cookie. Reset/desativação/perfil/cadastro incrementam authVersion: sessões antigas são recusadas e invalidadas na próxima requisição, sem remoção proativa do container. Service valida versão/ativo e prazo absoluto. Inatividade 30m configurável; limite absoluto 8h. |
| 5 | CSRF | Todos os 27 modificadores registrados atacados sem token e com token forjado: 403. Inclui login/logout e todos os módulos citados. PATCH/DELETE inexistentes não contornam o gate. GET/HEAD de todas as leituras não alteram dados/auditoria de negócio; /auth/csrf pode criar estado de segurança, exceção de infraestrutura. |
| 6 | CORS | Origem autorizada explícita + credentials=true observados; origem arbitrária recusada em preflight e chamada autenticada. Wildcard rejeitado. CORS autorizado não dispensa CSRF. |
| 7 | Inventário | 69 pares método/rota BES: 42 GET com HEAD implícito e 27 POST/PUT. Inventário estático confrontado com RequestMappingHandlerMapping no teste. |
| 8 | Públicos | GET /auth/csrf (HEAD implícito) e POST /auth/login, que exige CSRF. OPTIONS/preflight é infraestrutura; bootstrap é runner, não endpoint. |
| 9 | Exposição antiga | Nenhum endpoint operacional atual acessível anonimamente no inventário dinâmico. Não afirma proteção anterior à implementação da baseline. |
| 10 | Matriz | Inventário completo com público/autenticado/authority/CSRF em autenticacao-autorizacao.md; matriz de quatro perfis preservada; rotas futuras denyAll. |
| 11 | Service | @PreAuthorize nos services. Ataques diretos contra entrada/saída, transferência, limites, aprovação/rejeição, separação, atendimento, necessidade e usuários negados antes da validação. Autorizações positivas nas suítes operacionais e testes de auditoria. |
| 12 | Escalada | CONSULTA sem escrita; ALMOXARIFE/GESTOR sem gestão de usuários/atribuição de ADMIN, inclusive troca de IDs. roles/authorities/atorAuditoria/versao/bootstrapChave/senhaHash rejeitados em usuário. ADMIN opera pela matriz. |
| 13 | Último ADMIN | Proteção já existente preservada: bloqueio pessimista transacional; último ADMIN não pode perder perfil nem ser desativado. Proteção adicional contra desativar/alterar próprio perfil. Teste específico da regra de último administrador. |
| 14 | Senhas | BCrypt custo 12 real (fixtures rápidas só em teste). Mínimo 10 code points, máximo 72 bytes UTF-8, sem NUL. @JsonIgnore e DTO sem hash; encoder.matches, hash fictício para ausente. Sem senha/hash em respostas/snapshots. |
| 15 | Bootstrap | Opt-in false, tabela vazia, login/senha externos válidos, sem default/overwrite/reset/log de senha. Idempotência/concorrência testadas; unicidade impede dois bootstraps. Instância perdedora deve ser reiniciada. |
| 16 | Brute force | 8/login/5m e 30/IP/15m, memória/processo; buckets limitados e expiram. Sucesso também consome quota e não zera para evitar evasão; sem bloqueio permanente. 429 genérico, Retry-After 300 conservador; IP pode durar 15m. |
| 17 | Auditoria | Ator do contexto, instante, operação, entidade/referência, resultado e requestId quando HTTP; responsável operacional separado. Ator falso via query/header/body não substitui contexto. Sem edição/exclusão na API. |
| 18 | Rollback | Saída inválida, transferência sem saldo e atendimento sem saldo não registram sucesso. Falha da auditoria após entrada/transferência/atendimento reverte negócio, movimentos e evento. Transação externa ao aspecto, registrar MANDATORY. |
| 19 | IDOR | Authority independente do ID. Próprio/outro/inexistente não concede gestão. Leitura global autorizada intencional; escopo obra/almoxarifado/tenant ainda ausente e documentado. Não inventado nesta rodada. |
| 20 | 401/403 | Inventário sem sessão: 401 (CSRF válido nas escritas); sem authority: 403. CSRF inválido autenticado/login: 403; anônimo operacional pode ser 401 pelo handler. Frontend 401 fora de login encerra sessão; 403 preserva. |
| 21 | Headers | Observados nosniff, X-Frame-Options DENY, Referrer-Policy no-referrer, CSP default-src 'none'; frame-ancestors 'none'. HSTS ausente HTTP e presente com request HTTPS em MockMvc. Terminação HTTPS/proxy real não testada; CSP SPA depende do host frontend. |
| 22 | Logs/erros | Handler sanitizado, DTO de segurança toString redigido, SQL/binds/detalhes desativados. Scan final do log backend sem senha de fixture/hash/session ID/cookies/token CSRF. Overrides e terceiros exigem gate de implantação; não prova todos os logs possíveis. |
| 23 | Auth frontend | Identidade/CSRF em memória, credentials=include, token obtido/renovado pós-login. Cookie HttpOnly invisível a JS. |
| 24 | Storage | Busca frontend/src sem localStorage/sessionStorage/document.cookie. Browser confirma storages vazios. Sessão desconhecida mostra loading/erro, sem Outlet operacional; ações por permissão, backend autoritativo. |
| 25 | Falhas encontradas | Corrida bootstrap; exceção completa no log; MVC DEBUG expondo CSRF; COOKIE-only não explícito; inconsistência HEAD /auth/me. Nenhum bypass HTTP/service/CSRF confirmado. |
| 26 | Correções | Marcador único + scripts; log fixo; loggers INFO; tracking COOKIE; gate HEAD. Sem módulo/redesign. |
| 27 | Novos testes | +15 backend (13 no conjunto existente, 2 no container), +1 Playwright. Inventário/CSRF/CORS/HEAD/services/escalada/campos internos/último ADMIN/bootstrap/ator/logs/headers/rollback/cookie/storage/URL. |
| 28 | Backend | .\mvnw.cmd clean test: 251 aprovados, 0 falhas/erros/ignorados. |
| 29 | Frontend | npm.cmd test: 63 aprovados, 0 falhas/ignorados. |
| 30 | Playwright | H2 + vite preview: 36 aprovados, 0 falhas, retries=0; 1,6 minutos. |
| 31 | Build | npm.cmd run build aprovado, 1930 módulos. |
| 32 | npm audit | 0 vulnerabilidades em todas as severidades. Não substitui SCA Maven. |
| 33 | Secret scan | Scan heurístico inicial de 89 textos sem chave/token/URL com credencial ou cópia da senha histórica; .env.example vazio. Scan final abaixo. Histórico comprometido até rotação externa. |
| 34 | diff --check | Aprovado, exit 0; reconferência final abaixo. |
| 35 | git status | Baseline + alterações desta auditoria sem commit; staging vazio. Snapshot abaixo. |
| 36 | Arquivos | Lista abaixo distingue auditoria da implementação anterior. |
| 37 | Limitações | Sem escopo tenant/obra, MFA/OAuth/recuperação e-mail, store/limiter distribuídos, SCA Maven, WORM ou garantia contra DBA. HTTPS/proxy/grants/migrations/backup/rotação histórica dependem de processo externo. Revogação invalida sessão no uso. Bootstrap não coordena criação administrativa simultânea: provisionar antes de abrir administração. |
| 38 | Logo | Assets oficiais ausentes; fallback existente preservado, nenhuma logo criada. |
| 39 | MySQL/produção | Somente H2 em memória; sem conexão MySQL/produção, migration externa ou rotação. |
| 40 | Git | Sem commit/push/merge/fetch/staging/reescrita/main; HEAD preservado. |
| 41 | Recomendação | Recomendo o commit da Security Baseline 2 com estas correções e documentação, após autorização específica. Não é liberação para produção: gate externo permanece obrigatório. |

## Arquivos tocados por esta auditoria

- src/main/java/br/com/almoxarifado/controller/RegraNegocioExceptionHandler.java
- src/main/java/br/com/almoxarifado/security/BootstrapAdmin.java
- src/main/java/br/com/almoxarifado/security/Usuario.java
- src/main/java/br/com/almoxarifado/security/RotasPermissao.java
- src/main/resources/application.properties
- db/manual/security-baseline-2-mysql.sql
- db/manual/security-baseline-2-postgresql.sql
- src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java
- src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java (novo)
- frontend/tests/security.spec.js
- docs/autenticacao-autorizacao.md
- docs/security.md
- docs/security-baseline-2-relatorio.md (link para complemento)
- docs/security-baseline-2-auditoria-final.md (novo)

O relatório anterior registra a implementação e números históricos. Este complemento contém a auditoria final, sem reutilizar testes antigos como resultados atuais. Logs técnicos ficam temporários/ignorados, sem conteúdo sensível reproduzido aqui.

## Conferência final

- Secret scan final: 90 arquivos de texto novos/modificados; nenhuma ocorrência nos padrões verificados, nenhuma cópia da credencial histórica; .env.example vazio. Scan heurístico, não garantia absoluta.
- Log final backend: sem payload CSRF, senha de fixture, hash BCrypt, ID de sessão ou headers Cookie/Authorization/CSRF nos padrões verificados.
- git diff --check aprovado; staging vazio; HEAD preservado.
- main local e origin/main locais observados: 0137ddbddf403f8331d1a3f6d632428f7614ee15; nenhuma atualização/fetch realizado.
- H2 e vite preview desta rodada encerrados; demais processos preservados.
- A revisão automática rejeitou a tentativa de lançamento em background sem motivo específico apresentado. A política PowerShell local também impediu executar o .ps1 diretamente. A suíte foi concluída por sessões controladas: mesmos parâmetros isolados de H2/fixtures em memória e vite preview, sem mudar política do sistema.

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
?? docs/security-baseline-2-auditoria-final.md
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

## Revalidação para consolidação autorizada — 05/10/2026

A rodada posterior de consolidação foi expressamente autorizada a commitar e publicar em feature/bes-frontend; as referências anteriores a ausência de staging/commit/push descrevem as rodadas históricas de implementação e auditoria.

Reexecutados nesta consolidação: .\mvnw.cmd clean test (251, zero falhas/erros/ignorados), npm.cmd test (63), npm.cmd run build, Playwright H2 + vite preview (36, zero falhas, retries=0, 1,3 minutos), npm.cmd audit (0 vulnerabilidades) e git diff --check. Nenhuma funcionalidade/refatoração adicional; apenas regra explícita de novos endpoints em AGENTS.md e link da auditoria no índice. Staging integralmente revisto: 90 arquivos (42 novos, 48 modificados), sem artefatos temporários; git diff --cached --check aprovado. Scan de todos os blobs do índice sem achados após classificação: referências de implementação/configuração, documentação e fixtures fictícias exclusivas dos testes H2. Sem secret/bootstrap/session ID/cookie/CSRF token real, private key ou segredo VITE_*. O falso positivo do launcher H2 foi classificado como argumento de senha vazio.

Mensagem autorizada: security: implementa autenticacao autorizacao e auditoria. Publicação restrita a origin/feature/bes-frontend, sem force/merge/main. Esta consolidação não autoriza implantação. ROTACIONAÇÃO EXTERNA AINDA PENDENTE. Sem MySQL/produção/migrations reais; logo oficial ausente, fallback preservado.
