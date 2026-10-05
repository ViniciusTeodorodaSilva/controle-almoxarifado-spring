# Security Baseline 2 — autenticação, autorização e auditoria

Fonte funcional oficial: [Documentação Mestre BES v1.4](Documentacao_Mestre_Plataforma_BES_v1_4.docx), RF001–RF218. Esta baseline preserva o monólito modular, o catálogo compartilhado e os blocos operacionais já entregues. Não implementa compras completas, inventário, novos módulos ou frontend redesenhado.

## Decisão de arquitetura

Spring Security, sessão HTTP mantida no servidor e cookie BESSESSION. A SPA guarda somente a identidade retornada por GET /auth/me em memória React; não guarda senha, hash, JWT, refresh token ou sessão em localStorage/sessionStorage. Escolhemos sessão porque a BES é uma SPA própria e não precisa de federação ou de tokens entre serviços nesta fase. Usamos SecurityContextRepository com salvamento explícito e BCrypt (custo 12). A identidade serializada na sessão contém id, login, versão de autenticação e instante do login, nunca hash.

Referências: [persistência do contexto](https://docs.spring.io/spring-security/reference/servlet/authentication/persistence.html), [CSRF](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html) e [armazenamento de senha](https://docs.spring.io/spring-security/reference/7.0/features/authentication/password-storage.html).

O cookie é HttpOnly, SameSite=Lax, Path=/ e Secure por padrão; o container emite o ID em Set-Cookie. Tracking somente COOKIE impede autenticação por URL (;jsessionid). Fora dos profiles explícitos dev/test, uma configuração Secure=false impede o startup. Produção deve terminar HTTPS e configurar encaminhamento de protocolo somente por proxy confiável. O profile dev libera HTTP local explicitamente, sem fornecer conexão ou credencial de banco. Nunca ativar dev/test em produção. A sessão tem inatividade padrão de 30 minutos (BES_AUTH_SESSION_TIMEOUT) e duração absoluta de 8 horas. O limite absoluto é verificado em cada requisição e na autorização dos services; não há refresh token.

Login muda o id da sessão, renova CSRF e salva o contexto somente depois da auditoria de sucesso. Logout exige CSRF, invalida a sessão e expira o cookie. Desativação, reset de senha e alteração de cadastro/perfil incrementam auth_version: a próxima requisição ou chamada de service com a identidade antiga é recusada. Dados de usuário são consultados no banco; alterações de perfil não dependem da confiança no frontend.

## CSRF e CORS

GET /auth/csrf cria/obtém a sessão e devolve {token, headerName}. Toda escrita, incluindo login/logout, deve enviar o token atual em X-CSRF-TOKEN. A implementação mantém o tratamento mascarado padrão do Spring Security. Após login o cliente busca outro token, porque o anterior é invalidado. O frontend usa credentials=include e guarda CSRF somente em memória. Falha ao buscar o token impede a escrita; não há retry automático de POST/atendimento.

Preferir frontend e API na mesma origem, através do proxy /api. BES_CORS_ORIGINS é uma lista de origens externas explícitas separadas por vírgula; o padrão vazio não autoriza origens externas. Wildcard é rejeitado inclusive com credenciais. CORS não substitui CSRF nem autorização. Para outra origem, planejar SameSite/domínio/HTTPS; esta baseline suporta a implantação na mesma origem e não relaxa o cookie para qualquer domínio.

## Usuário e funcionário

bes_usuario é a identidade de acesso: id Long, username normalizado único (ASCII minúsculo, 3–80 caracteres, letras/números/ponto/underscore/hífen), nomeExibicao, senhaHash, perfil, ativo, funcionário opcional, criadoEm, atualizadoEm, ultimoLoginEm, authVersion e versão otimista. Usuario -> Funcionario é ManyToOne LAZY, sem cascade: não cria, exclui ou modifica funcionários. O vínculo é opcional e não concede permissão. A listagem carrega funcionários com EntityGraph para evitar N+1; respostas contêm apenas funcionarioId.

DTOs específicos e estritos rejeitam propriedades desconhecidas, id, senhaHash, timestamps, ator ou versões internas. Edição não aceita senha: usar o endpoint separado de redefinição. Apenas ADMIN gerencia usuários; não há DELETE. Não é possível desativar/alterar o próprio perfil ou remover o último ADMIN ativo. A verificação do último administrador bloqueia as linhas em uma transação, evitando duas desativações simultâneas. Login é normalizado antes da checagem e também protegido por restrição única no banco.

Senhas: mínimo de 10 caracteres Unicode, máximo de 72 bytes UTF-8 (limite do BCrypt), sem NUL. Não há truncamento silencioso nem exigência artificial de símbolo. Não retornar ou registrar senha/hash. Não há senha padrão. Reset administrativo revoga sessões anteriores; recuperação por e-mail, MFA, SSO e OAuth permanecem fora desta rodada.

## Perfis e matriz

Permissões são autoridades granulares centralizadas em Permissao e Perfil. O nome do perfil isolado no frontend não concede autorização. A matriz inicial é conservadora; permissões customizadas por usuário/perfil editável poderão ser adicionadas sem substituir o domínio operacional.

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

Autoridades: USUARIO_GERENCIAR, AUDITORIA_LER, PRODUTO_LER/GERENCIAR, CATEGORIA_LER/GERENCIAR, UNIDADE_LER/GERENCIAR, FUNCIONARIO_LER/GERENCIAR, ALMOXARIFADO_LER/GERENCIAR, ESTOQUE_LER/MOVIMENTAR/TRANSFERIR/CONFIGURAR, MOVIMENTACAO_LER, SOLICITACAO_LER/CRIAR/APROVAR/REJEITAR/SEPARAR/ATENDER e NECESSIDADE_COMPRA_LER/CRIAR.

RotasPermissao aplica o gate HTTP antes de validar o corpo; todos os services operacionais têm @PreAuthorize com a mesma autoridade. Rotas futuras não explicitamente admitidas são negadas por padrão. As regras de status, saldo, quantidade, idempotência e transação continuam obrigatórias depois da autorização. Leitura é global na plataforma atual, não limitada por funcionário/almoxarifado/empresa: isso é uma decisão explícita desta matriz, não uma autorização baseada em ids enviados pelo cliente. Caso a BES exija múltiplas empresas ou escopo por obra, implementar escopo de dados antes de disponibilizar esses módulos.

Sem sessão válida: 401 e redirecionamento para login, inclusive ao buscar CSRF ou encerrar sessão revogada (login inválido permanece na própria tela). Sem permissão: 403 e mensagem de acesso restrito, preservando a sessão. CSRF inválido em login: 403. Identidade desconhecida, senha incorreta e usuário inativo compartilham o mesmo erro genérico de login 401. Não há enumeração por mensagem; o provider faz comparação com hash fictício gerado em memória quando não encontra usuário.

## Endpoints novos

| Método | Rota | Contrato/permissão |
|---|---|---|
| GET | /auth/csrf | Público, token de CSRF da sessão, no-store |
| POST | /auth/login | {username,password}, CSRF, identidade pública ou 401/429 |
| GET | /auth/me | Sessão, identidade e permissões efetivas |
| POST | /auth/logout | Sessão e CSRF, 204 |
| GET | /usuarios | USUARIO_GERENCIAR, lista sem hash |
| POST | /usuarios | USUARIO_GERENCIAR, {username,nomeExibicao,perfil,ativo,funcionarioId?,password} |
| PUT | /usuarios/{id} | USUARIO_GERENCIAR, mesmos campos sem password |
| PUT | /usuarios/{id}/senha | USUARIO_GERENCIAR, {password} |
| GET | /auditoria?pagina=0&tamanho=30 | AUDITORIA_LER, página, tamanho 1–100 |

Sem PUT/DELETE de auditoria, DELETE de usuário ou endpoint /auth/refresh. Resposta de usuário: id, username, nomeExibicao, perfil, ativo, funcionarioId, criadoEm, atualizadoEm, ultimoLoginEm e permissoes.

## Bootstrap e configuração

DB_URL/DB_USERNAME/DB_PASSWORD continuam externos. Variáveis novas: BES_CORS_ORIGINS (padrão vazio), BES_AUTH_SESSION_TIMEOUT (padrão 30m), BES_BOOTSTRAP_ADMIN_ENABLED (padrão false), BES_BOOTSTRAP_ADMIN_USERNAME e BES_BOOTSTRAP_ADMIN_PASSWORD (sem defaults). .env.example contém somente campos vazios; Spring não lê .env automaticamente. Fornecer secrets pelo ambiente/gerenciador autorizado, nunca argumento de comando, VITE_* ou documentação.

Habilitar bootstrap apenas para provisionamento com tabela de usuários vazia, login válido e senha forte fornecidos externamente. Se houver qualquer usuário, não cria/recria/sobrescreve nada. Depois do primeiro acesso, retirar as variáveis de bootstrap e desabilitar a opção. O usuário inicial recebe bootstrap_chave=ADMIN_INICIAL, única e interna. A restrição do banco impede dois bootstraps concorrentes, mesmo com logins diferentes; a instância perdedora falha de forma segura e deve ser reiniciada após o provisionamento. Não há lock distribuído nem coordenação com criação administrativa simultânea; provisionar antes de abrir a administração. Não executar scripts ou bootstrap em produção sem processo de implantação aprovado.

## Auditoria e histórico

bes_auditoria é append-only na API e @Immutable no JPA. Cada evento contém instante UTC, atorId e username histórico (derivados do SecurityContext), evento, entidade, referência, resultado, responsável operacional quando informado, requestId e estados seguros quando aplicáveis. Não serializa request/entity inteira. Login/falha/limite/logout/acesso negado têm transação própria; auditoria de sucesso operacional participa da mesma transação do negócio: falhar a auditoria reverte estoque e movimentação.

A auditoria cobre usuários e perfil/situação/reset; catálogo/categorias/unidades/funcionários/almoxarifados; entrada/saída (referência exata da movimentação e saldos reais antes/depois); transferências; limites; aprovação/rejeição; separação; atendimento; necessidade de compra. Snapshot usa allowlist de estado/quantidade/limites/perfil/ativo, sem senha, hash ou corpo de requisição. Não é event sourcing nem diff completo de todos os campos de cadastro. Falhas de regra de negócio não geram um histórico completo de tentativas de cada módulo; login e negações de segurança são auditados.

Usuário autenticado não substitui responsável operacional. Quem opera a tela pode ser diferente do funcionário que recebeu/aprovou/retirou material. O cliente não pode escolher o ator de auditoria. Ator é nullable para eventos de sistema e registros anteriores à baseline não recebem usuário inventado. Atores mantêm id e login histórico mesmo após alteração de nome; não há exclusão física pela API.

Request ID: reutiliza UUID válido de 36 caracteres em X-Request-ID ou gera UUID no servidor, devolve no header e propaga ao MDC, ao padrão de nível dos logs e à auditoria. Não aceita texto arbitrário ou campos enormes. Não registrar corpos de login/usuários nem habilitar SQL/bind/debug com dados reais. Spring Web/Security ficam explicitamente em INFO para evitar payloads/tokens em DEBUG herdado. O handler de erro inesperado registra mensagem fixa sem URI, mensagem da exceção ou stack trace; requestId permanece no MDC. O backend é uma API: CSP default-src 'none'; frame-ancestors 'none', X-Frame-Options DENY, nosniff, Referrer-Policy no-referrer e cache-control da segurança. HSTS só em HTTPS. A CSP da SPA deve ser configurada no servidor do frontend, não copiada da API.

## Abuso e limites de implantação

Login: 8 tentativas por login/5 minutos, 30 por IP/15 minutos; inclui tentativas bem-sucedidas. 429 com Retry-After conservador de 300 segundos; o limite de IP pode durar mais. Buckets expiram, até 10.000 entradas; IP já bloqueado não cria contadores para logins adicionais; não há bloqueio permanente do usuário. Limite em memória por processo: reinício limpa contadores e vários nós não compartilham proteção. Proxy confiável deve tratar IP e limites distribuídos/WAF antes de escala. Não confiar em X-Forwarded-For arbitrário. Endpoints administrativos continuam protegidos por ADMIN; limites de volume adicionais e monitoramento ficam para implantação.

Sessões são locais à instância: escala horizontal exige afinidade ou Spring Session/store compartilhado, sem mudar o contrato SPA. Não há MFA, e-mail de recuperação, controle por obra/empresa, catálogo editável de permissões, SIEM, WORM/assinatura criptográfica da auditoria ou garantia contra DBA privilegiado. Ajustar grants do banco para impedir UPDATE/DELETE em auditoria e planejar retenção/backup de acordo com o processo aprovado. Não criar vínculo histórico retroativo. **A credencial antiga no histórico Git ainda deve ser rotacionada externamente.**

## Banco e scripts

Scripts manuais: db/manual/security-baseline-2-mysql.sql e db/manual/security-baseline-2-postgresql.sql. Criam apenas bes_usuario e bes_auditoria, com FK opcional para funcionario, unicidade e índices. Não executados, não incluem INSERT de usuário/senha, não alteram tabelas operacionais ou saldos, não removem dados. Perfis/permissões são enums/política Java nesta baseline; não há tabela fictícia de roles sem consumidor. Validar backup, esquema, versão do banco e tipos de funcionario.id antes da execução autorizada. MySQL é o driver runtime atual; PostgreSQL é preparado por script, mas exige driver/config/testes de integração na futura migração. Manter ddl-auto=none na produção.

## Frontend, testes e RFs

Login com labels/autocomplete/feedback genérico e teclado; menu com nome de /me, perfil e Sair; tela /usuarios exclusiva ADMIN para criação/edição/ativação/vínculo/perfil/reset. Botões e navegação usam permissões, backend continua autoridade. Mantém o design existente e usa fallback tipográfico B&S Engenharia / Plataforma BES; os assets oficiais ainda não estão disponíveis, não foi criada logo. Tabelas mantêm scroll interno e modais limitados à viewport.

Testes antigos permanecem com identidade explícita @WithMockUser e CSRF; não há disable global de segurança. Novos testes usam sessões reais, BCrypt e H2, cobrem matriz HTTP e service, revogação, payload interno, login genérico, limitação, último ADMIN, histórico e rollback por falha de auditoria. No browser, todos os módulos antigos usam login real; testes de segurança verificam quatro perfis, HTTP negado, usuário inativo e 1440/768/390 px. Fixtures fictícias vivem somente em src/test e frontend/tests. scripts/start-backend-h2.ps1 -WithTestUsers provisiona somente o H2 de teste; nunca usar contra MySQL. Cookies dos testes ficam em memória, sem arquivo storageState.

Cobertura funcional: RF056 (login) e RF057 (autorização) implementados nesta baseline para os módulos atuais. RF003 parcial (quatro perfis iniciais, funcionário/perfis customizados futuros); RF038 parcial (dashboard existente com sessão, não painel personalizado por perfil); RF053/RF148 parcial no produto total (auditoria implementada para operações presentes, módulos futuros não existem); RF058 parcial (reset administrativo, sem recuperação por e-mail); RF107 parcial (acesso por permissão, sem escopo de dados/empresa); RF154/RF155 parcial (segredos externos e testes isolados, HTTPS/ambientes dependem de infraestrutura). RF097 (ajustes/inventário) não atendido; RF156 e RF212 não concluídos por preparação de segurança. Não declara RF001–RF218 concluídos.

Verificações finais e inventário da entrega são registrados no relatório desta rodada. Não houve acesso ao MySQL, execução de migration real, alteração de main, commit, push ou reescrita do histórico.

## Inventário completo pré-commit (05/10/2026)

69 pares método/rota de controllers BES; GET também admite HEAD pelo Spring MVC. OPTIONS é preflight/infra, sem operação de negócio. Não há PATCH/DELETE operacionais. /error é infraestrutura do framework, sem permissão pública adicionada. Qualquer rota não admitida permanece denyAll.

| Método | Rota | Público | Autenticado | Permissão | CSRF |
|---|---|---|---|---|---|
| GET | `/almoxarifados` | Não | Sim | ALMOXARIFADO_LER | Não |
| POST | `/almoxarifados` | Não | Sim | ALMOXARIFADO_GERENCIAR | Sim |
| GET | `/almoxarifados/{id}` | Não | Sim | ALMOXARIFADO_LER | Não |
| PUT | `/almoxarifados/{id}` | Não | Sim | ALMOXARIFADO_GERENCIAR | Sim |
| GET | `/auditoria` | Não | Sim | AUDITORIA_LER | Não |
| GET | `/auth/csrf` | Sim | Não | Público | Não |
| POST | `/auth/login` | Sim | Não | Público | Sim |
| POST | `/auth/logout` | Não | Sim | Sessão válida | Sim |
| GET | `/auth/me` | Não | Sim | Sessão válida | Não |
| GET | `/categorias` | Não | Sim | CATEGORIA_LER | Não |
| POST | `/categorias` | Não | Sim | CATEGORIA_GERENCIAR | Sim |
| GET | `/categorias/{id}` | Não | Sim | CATEGORIA_LER | Não |
| PUT | `/categorias/{id}` | Não | Sim | CATEGORIA_GERENCIAR | Sim |
| GET | `/estoques` | Não | Sim | ESTOQUE_LER | Não |
| POST | `/estoques` | Não | Sim | ESTOQUE_MOVIMENTAR | Sim |
| GET | `/estoques/alertas` | Não | Sim | ESTOQUE_LER | Não |
| GET | `/estoques/almoxarifado/{almoxarifadoId}` | Não | Sim | ESTOQUE_LER | Não |
| PUT | `/estoques/entrada` | Não | Sim | ESTOQUE_MOVIMENTAR | Sim |
| GET | `/estoques/produto/{produtoId}` | Não | Sim | ESTOQUE_LER | Não |
| GET | `/estoques/produto/{produtoId}/almoxarifado/{almoxarifadoId}` | Não | Sim | ESTOQUE_LER | Não |
| GET | `/estoques/reposicoes` | Não | Sim | ESTOQUE_LER | Não |
| PUT | `/estoques/saida` | Não | Sim | ESTOQUE_MOVIMENTAR | Sim |
| GET | `/estoques/{id}` | Não | Sim | ESTOQUE_LER | Não |
| PUT | `/estoques/{id}/limites` | Não | Sim | ESTOQUE_CONFIGURAR | Sim |
| GET | `/funcionarios` | Não | Sim | FUNCIONARIO_LER | Não |
| POST | `/funcionarios` | Não | Sim | FUNCIONARIO_GERENCIAR | Sim |
| GET | `/funcionarios/{id}` | Não | Sim | FUNCIONARIO_LER | Não |
| PUT | `/funcionarios/{id}` | Não | Sim | FUNCIONARIO_GERENCIAR | Sim |
| GET | `/movimentacoes` | Não | Sim | MOVIMENTACAO_LER | Não |
| GET | `/movimentacoes/almoxarifado/{almoxarifadoId}` | Não | Sim | MOVIMENTACAO_LER | Não |
| GET | `/movimentacoes/produto/{produtoId}` | Não | Sim | MOVIMENTACAO_LER | Não |
| GET | `/movimentacoes/solicitacao/{solicitacaoId}` | Não | Sim | MOVIMENTACAO_LER | Não |
| GET | `/movimentacoes/tipo/{tipo}` | Não | Sim | MOVIMENTACAO_LER | Não |
| GET | `/movimentacoes/{id}` | Não | Sim | MOVIMENTACAO_LER | Não |
| GET | `/necessidades-compra` | Não | Sim | NECESSIDADE_COMPRA_LER | Não |
| POST | `/necessidades-compra` | Não | Sim | NECESSIDADE_COMPRA_CRIAR | Sim |
| GET | `/necessidades-compra/{id}` | Não | Sim | NECESSIDADE_COMPRA_LER | Não |
| GET | `/produtos` | Não | Sim | PRODUTO_LER | Não |
| POST | `/produtos` | Não | Sim | PRODUTO_GERENCIAR | Sim |
| GET | `/produtos/busca` | Não | Sim | PRODUTO_LER | Não |
| GET | `/produtos/equivalentes` | Não | Sim | PRODUTO_LER | Não |
| GET | `/produtos/{id}` | Não | Sim | PRODUTO_LER | Não |
| PUT | `/produtos/{id}` | Não | Sim | PRODUTO_GERENCIAR | Sim |
| GET | `/solicitacoes` | Não | Sim | SOLICITACAO_LER | Não |
| POST | `/solicitacoes` | Não | Sim | SOLICITACAO_CRIAR | Sim |
| GET | `/solicitacoes/funcionario/{funcionarioId}` | Não | Sim | SOLICITACAO_LER | Não |
| GET | `/solicitacoes/status/{status}` | Não | Sim | SOLICITACAO_LER | Não |
| GET | `/solicitacoes/{id}` | Não | Sim | SOLICITACAO_LER | Não |
| PUT | `/solicitacoes/{id}/aprovar` | Não | Sim | SOLICITACAO_APROVAR | Sim |
| GET | `/solicitacoes/{id}/atendimentos` | Não | Sim | SOLICITACAO_LER | Não |
| POST | `/solicitacoes/{id}/atendimentos` | Não | Sim | SOLICITACAO_ATENDER | Sim |
| GET | `/solicitacoes/{id}/faltas` | Não | Sim | SOLICITACAO_LER | Não |
| PUT | `/solicitacoes/{id}/iniciar-separacao` | Não | Sim | SOLICITACAO_SEPARAR | Sim |
| GET | `/solicitacoes/{id}/movimentacoes` | Não | Sim | MOVIMENTACAO_LER | Não |
| GET | `/solicitacoes/{id}/operacao` | Não | Sim | SOLICITACAO_LER | Não |
| PUT | `/solicitacoes/{id}/rejeitar` | Não | Sim | SOLICITACAO_REJEITAR | Sim |
| POST | `/solicitacoes/{solicitacaoId}/itens` | Não | Sim | SOLICITACAO_CRIAR | Sim |
| GET | `/transferencias` | Não | Sim | ESTOQUE_LER | Não |
| POST | `/transferencias` | Não | Sim | ESTOQUE_TRANSFERIR | Sim |
| GET | `/transferencias/{id}` | Não | Sim | ESTOQUE_LER | Não |
| GET | `/transferencias/{id}/movimentacoes` | Não | Sim | ESTOQUE_LER | Não |
| GET | `/unidades-medida` | Não | Sim | UNIDADE_LER | Não |
| POST | `/unidades-medida` | Não | Sim | UNIDADE_GERENCIAR | Sim |
| GET | `/unidades-medida/{id}` | Não | Sim | UNIDADE_LER | Não |
| PUT | `/unidades-medida/{id}` | Não | Sim | UNIDADE_GERENCIAR | Sim |
| GET | `/usuarios` | Não | Sim | USUARIO_GERENCIAR | Não |
| POST | `/usuarios` | Não | Sim | USUARIO_GERENCIAR | Sim |
| PUT | `/usuarios/{id}` | Não | Sim | USUARIO_GERENCIAR | Sim |
| PUT | `/usuarios/{id}/senha` | Não | Sim | USUARIO_GERENCIAR | Sim |

## Extensão permissionada — Bloco 4

Compras amplia o inventário para 85 handlers/36 escritas (16 novos/9 escritas), mantendo sessão, CSRF, CORS explícito, cookies, revogação, auditoria e gates da Security Baseline 2. Nenhuma rota operacional ficou pública.

| Método | Caminho | Authority HTTP e service |
|---|---|---|
| GET | `/fornecedores` | `FORNECEDOR_LER` |
| GET | `/fornecedores/{id}` | `FORNECEDOR_LER` |
| POST | `/fornecedores` | `FORNECEDOR_GERENCIAR` |
| PUT | `/fornecedores/{id}` | `FORNECEDOR_GERENCIAR` |
| GET | `/pedidos-compra` | `COMPRA_LER` |
| GET | `/pedidos-compra/{id}` | `COMPRA_LER` |
| POST | `/pedidos-compra` | `COMPRA_CRIAR` |
| PUT | `/pedidos-compra/{id}` | `COMPRA_CRIAR` |
| PUT | `/pedidos-compra/{id}/submeter` | `COMPRA_CRIAR` |
| PUT | `/pedidos-compra/{id}/aprovar` | `COMPRA_APROVAR` |
| PUT | `/pedidos-compra/{id}/cancelar` | `COMPRA_CANCELAR` |
| GET | `/pedidos-compra/{id}/recebimentos` | `RECEBIMENTO_LER` |
| POST | `/pedidos-compra/{id}/recebimentos` | `RECEBIMENTO_REGISTRAR` |
| GET | `/recebimentos-compra` | `RECEBIMENTO_LER` |
| GET | `/recebimentos-compra/{id}` | `RECEBIMENTO_LER` |
| PUT | `/necessidades-compra/{id}/cancelar` | `NECESSIDADE_COMPRA_GERENCIAR` |

| Perfil | Novas capacidades |
|---|---|
| ADMIN | Todas as novas authorities |
| GESTOR | Leitura de fornecedores/compras/recebimentos; FORNECEDOR_GERENCIAR, COMPRA_CRIAR/APROVAR/CANCELAR, NECESSIDADE_COMPRA_GERENCIAR |
| ALMOXARIFE | Leitura de fornecedores/compras/recebimentos; RECEBIMENTO_REGISTRAR |
| CONSULTA | Somente leitura de fornecedores/compras/recebimentos |

Os perfis preservam permissões anteriores. GESTOR não recebe RECEBIMENTO_REGISTRAR; ALMOXARIFE não cria/aprova/cancela compras; nenhuma nova authority concede USUARIO_GERENCIAR. @PreAuthorize nos serviços permanece obrigatório. Ator autenticado e responsável físico são separados. Eventos de sucesso usam AuditoriaService com transação MANDATORY, sem dados de documento/contato, payload integral, chaves, hashes ou secrets. Falha no recebimento reverte também auditoria de sucesso e atualizações de necessidade.

Cobertura adversarial: matriz positiva/negativa HTTP/service, CSRF em todas as 36 escritas, mass assignment, concorrência, rollback, idempotência e resposta perdida. Ver [Bloco 4](compras-recebimento.md).

No Bloco 4, a proteção de documento/contato do fornecedor vale também para snapshots retornados no pedido: documento integral e contato apenas com FORNECEDOR_GERENCIAR; demais leitores recebem máscara/null. Documento imprimível consome esse mesmo GET permissionado. Evidências: [auditoria pré-commit do Bloco 4](bloco4-auditoria-pre-commit.md).
