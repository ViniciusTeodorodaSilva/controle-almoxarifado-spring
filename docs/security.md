# Segurança — BES

A baseline atual é a [Security Baseline 2](autenticacao-autorizacao.md): sessão, CSRF, perfis e auditoria. Os registros abaixo preservam a Security Baseline 1 e a obrigação de rotação histórica.

## Security Baseline 1 — configuração externa

Referência funcional: Documentacao_Mestre_Plataforma_BES_v1_4.docx. Esta entrega cobre parcialmente RF154 (proteção de credenciais), RF155 (isolamento de testes) e RF153 (configuração de logs). Não conclui segurança, HTTPS, monitoramento ou segregação completa de ambientes.

## Configuração externa

O backend requer DB_URL, DB_USERNAME e DB_PASSWORD fora do profile test. Não há defaults de credenciais. A validação ocorre antes da criação dos beans de banco e informa somente o nome da variável ausente. Valores vazios são rejeitados. Não colocar senha na URL JDBC; usar DB_PASSWORD. Não fornecer secrets em argumentos de linha de comando, que podem aparecer na lista de processos.

application.properties contém configuração comum sem credenciais. application-test.properties permanece exclusivo dos testes, com H2 em memória e credenciais públicas de fixture. O profile dev contém apenas a liberação explícita do cookie para HTTP local; desenvolvimento e produção utilizam configuração externa; o profile prod pode ser selecionado pelo ambiente. Nunca distribuir recursos de teste ou ativar test em produção.

.env.example contém somente campos vazios. Spring Boot não carrega .env automaticamente. Definir as variáveis no ambiente do processo pelo gerenciador de secrets da infraestrutura ou configuração local não versionada; não imprimir valores. Em desenvolvimento, após configurá-las, executar .\mvnw.cmd spring-boot:run. Produção deve usar identidade de banco dedicada, menor privilégio e sem permissões de DDL; ddl-auto=none é preservado. Não usar conta administrativa. Planejar provisionamento e migrations separadamente.

A configuração externa segue a [documentação oficial do Spring Boot](https://docs.spring.io/spring-boot/reference/features/external-config.html). Overrides externos têm precedência: sua revisão faz parte do gate de implantação.

## Testes

Executar .\mvnw.cmd clean test sem DB_*; as suítes usam H2 e profile test. Os testes de validação externa não criam datasource. No frontend: npm.cmd run build e npm.cmd test. Playwright utiliza o script scripts/start-backend-h2.ps1 e vite preview com proxy público para H2 na porta 8081; usar o flag -WithTestUsers e aguardar HTTP 200 em /auth/csrf e /api/auth/csrf antes da suíte; endpoints operacionais agora exigem login. Nunca usar MySQL para essas verificações.

## Frontend e novos secrets

VITE_* é público no navegador. VITE_API_URL pode conter apenas endereço público, sem credenciais, tokens ou query string sensível. Nenhum secret pertence ao frontend.

Para adicionar um secret: definir nome e consumidor no backend, carregar externamente, validar ausência sem imprimir conteúdo, documentar somente o nome, criar testes com placeholders e revisar arquivos novos e staging antes do commit. Nunca colar valores em issues, logs, documentação ou exemplos. .gitignore protege .env, configurações locais, chaves privadas e bancos locais, mas não protege arquivos já rastreados. Revisar também arquivos ignorados antes de compartilhar diretórios.

## Exposição histórica e rotação

Credenciais de banco estavam em src/main/resources/application.properties, linhas 4–5 antes desta rodada. Foram introduzidas no commit 0137ddbddf403f8331d1a3f6d632428f7614ee15, datado de 17/09/2026, e permanecem no histórico publicado. Remover do working tree não revoga a credencial nem limpa o histórico. Tratar como potencialmente comprometidas até rotação.

Rotacionar fora do código, pelo responsável autorizado: gerar nova credencial, atualizar o gerenciador de secrets, verificar a implantação, revogar a antiga e revisar acessos. Esta rodada não acessa banco, não testa credenciais e não reescreve histórico. Eventual limpeza histórica precisa de plano coordenado separado e não substitui rotação.

## Logs, HTTP e gate de produção

SQL e bindings foram desativados na configuração comum; debug/trace e detalhes de requisição também. Erros HTTP padrão não incluem stack trace, mensagem interna ou binding errors. A auditoria final da Baseline 2 substituiu o log da exceção completa por mensagem fixa com correlação no MDC e fixou Spring Web/Security em INFO: o handler não registra mensagem, URI ou stack trace. Drivers/bibliotecas e overrides externos ainda exigem revisão de implantação. Restringir acesso e retenção de logs; nunca incluir credenciais na URL. A ausência de logs explícitos de secrets não equivale a sanitização completa dos logs de terceiros.

Não existem Actuator, DevTools ou CORS permissivo configurados. O frontend usa proxy de mesma origem. No momento da entrega original da Baseline 1, os endpoints operacionais ainda não tinham autenticação/autorização: manter o sistema em ambiente controlado, sem exposição pública até security gate. A autenticação foi adicionada posteriormente na Baseline 2, documentada no início deste arquivo.

Gate antes de produção: rotação confirmada, secrets externos, HTTPS, autenticação/autorização, política CORS explícita se houver origens diferentes, acesso a logs restrito, auditoria, backup/restauração e revisão de dependências. Próxima baseline: autenticação, permissões por operação e auditoria atribuída à identidade autenticada.

## Dependências

npm audit não encontrou vulnerabilidades nesta rodada. Maven foi revisado por dependências declaradas/resolvidas; não há scanner SCA de backend configurado. Isso não comprova ausência de CVEs de backend. Não foram feitas atualizações amplas nem introduzidas dependências. Adotar SCA automatizado e monitoramento contínuo em rodada própria.

## Evidências da rodada — 05/10/2026

- Checkpoint: feature/bes-frontend, HEAD ea13d8bd2af4916b0ed6838b80df1c5f3dda07f5, inicialmente limpo.
- .\mvnw.cmd clean test: BUILD SUCCESS, 184 testes, zero falhas/erros/ignorados; cinco testes novos de configuração, sem datasource externo.
- Frontend: build aprovado; 47 testes aprovados.
- Playwright: 25 aprovados em 5,6 minutos, H2 + vite preview, código de saída 0.
- npm audit: zero vulnerabilidades em todas as severidades; dependency:tree Maven concluído, sem auditoria de CVEs do backend.
- Scan heurístico dos arquivos atuais, novos e texto dos documentos Word: nenhum token, chave privada ou credencial adicional identificado. Nenhuma ocorrência da senha antiga nos arquivos atuais candidatos a versionamento. Fixtures H2, placeholders e variáveis de autenticação do wrapper Maven não são credenciais reais adicionadas.
- Sem acesso ao MySQL/produção, alteração de main, staging, commit, push, rotação ou reescrita histórica. A baseline permanece para revisão; não autoriza implantação pública.


## Bloco 6 - custodia de ativos

18 handlers permissionados em HTTP e service, oito novas authorities, CSRF nas escritas e DTOs estritos. Estado, evento, chave de idempotencia e auditoria sao atomicos. Sem upload, QR publico ou assinatura juridica. Politica de leitura por modulo, sem isolamento por Obra; ID nao substitui authority. Scripts MySQL/PostgreSQL manuais nao executados; somente H2 nos testes. Security gate e homologacao continuam necessarios antes de producao. [Regras e limites](ferramentas-equipamentos.md).
## Evidências e pendências transversais da auditoria de ativos

Assinatura idempotente diferencia nulo e texto literal; leitura continua permissionada por módulo. Regressões cobrem os 18 handlers nos quatro perfis, as oito escritas com CSRF/mass assignment e reversão de estado/evento/chave quando a auditoria falha. Nenhuma nova dependência, authority ou rota pública foi criada na auditoria. [Relatório](bloco6-auditoria-pre-commit.md).

Antes de produção permanecem: rotação comprovada de credenciais históricas comprometidas (não realizada nesta rodada), configuração externa, HTTPS/proxy, homologação de dialect/locks/constraints e migração autorizada, backup e restauração testados, observabilidade/retenção de logs, revisão operacional de sessões/rate limiting e SCA do backend. Zero no npm audit não substitui esses gates. QR autenticado, upload seguro de evidências e logo oficial permanecem pendentes funcionais, sem atalhos públicos ou assets inventados.

## Bloco 7 - limites mantidos

O diagnóstico do incidente B5 acrescentou ao log genérico somente os nomes das classes da exceção e da causa imediata. A resposta HTTP permanece genérica, sem stack trace; log não inclui mensagem da exceção, URI ou corpo. Esses nomes ajudam investigação, mas não identificaram a causa do 500 anterior. Evidências e risco residual na [auditoria B7](bloco7-auditoria-pre-commit.md).

EPI usa sessao/CSRF/authorities e auditoria existentes. Propostas SQL nao executadas, testes somente H2 isolado. QR, anexos, logo oficial, valorizacao, homologacao real, rotacao externa das credenciais historicas, HTTPS/proxy, migracoes controladas, backup/restore testado, observabilidade/monitoramento e sessao/rate limiting de producao continuam pendentes. Nenhuma preparacao arquitetural substitui security gate antes de producao.
