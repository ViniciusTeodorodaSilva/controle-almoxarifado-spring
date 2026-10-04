# BES — Frontend operacional

React + JavaScript/JSX + Vite + Tailwind CSS. Node.js 22.12+ ou 24 LTS, npm, Java 17+ e Maven Wrapper do repositório. Dependências fixadas em package-lock.json; não há TypeScript, autenticação nem dados fictícios na interface.

## Abrir a BES sem acessar MySQL

Terminal 1, na raiz do repositório (PowerShell):

```powershell
powershell -ExecutionPolicy Bypass -File .\frontend\scripts\start-backend-h2.ps1
```

Este comando prepara o classpath Maven e inicia o backend existente na porta **8081**, usando apenas **H2 em memória** e o perfil de testes. As tabelas são criadas no H2; nenhum comando é enviado ao MySQL. O launcher mantém open-in-view=true, como na configuração normal atual, para serializar os relacionamentos das entidades retornadas pela API. O perfil de testes puro desativa essa opção e pode retornar 500 nessas respostas HTTP completas; nenhuma regra do backend foi alterada. O banco começa vazio e seus dados desaparecem ao encerrar o processo. Não usar este perfil em produção. Não popula exemplos automaticamente.

Terminal 2, na raiz:

```powershell
cd frontend
npm.cmd install
Copy-Item .env.example .env
# Edite .env para: VITE_API_URL=http://localhost:8081
npm.cmd run dev
```

Abra **http://localhost:5173**. Ctrl+C encerra cada servidor. Use npm.cmd quando o PowerShell bloquear npm.ps1; não é preciso mudar a política global. Para testar: cadastre categorias, unidades, almoxarifados, funcionários e produtos. Em Solicitações, use Nova solicitação e adicione os materiais do catálogo. Em Estoque, use Registrar entrada ou Registrar saída. Os contratos permanecem os de docs/api.md.

## Usar com o Spring Boot configurado normalmente

Se o schema de produção já foi revisado/aplicado conforme docs/sql e o banco estiver preparado, inicie o backend como de costume, na raiz:

```powershell
.\mvnw.cmd spring-boot:run
```

Use `VITE_API_URL=http://localhost:8080` no frontend/.env e reinicie o Vite. **Esse comando usa a configuração normal do backend e acessa o MySQL**; não é necessário para testar o frontend isoladamente. Nenhuma migration é aplicada pelo frontend.

## API, CORS e publicação futura

VITE_API_URL é o endereço do Spring Boot. Durante `npm run dev`, `/api` é encaminhado pelo proxy Vite ao endereço configurado (8080 por padrão), sem mudar CORS ou regras do backend. Valores VITE_* são públicos; não colocar segredos.

`npm run preview` também disponibiliza proxy /api. Para validar um build local com preview, configure VITE_API_URL=/api no build e use o alvo HTTP do backend em VITE_API_URL ao iniciar preview. O teste de navegador usa o servidor de desenvolvimento.

Em produção: preferir VITE_API_URL=/api e configurar um reverse proxy para encaminhar /api ao Spring Boot, retirando o prefixo. O servidor deve retornar index.html para rotas da SPA. Se usar URL absoluta no build, o backend precisa permitir explicitamente a origem por CORS. O proxy Vite é local e não acompanha os arquivos dist. HTTPS/publicação ainda não configurados.

## Comandos

```powershell
npm.cmd install
npm.cmd run dev
npm.cmd run build
npm.cmd test
npm.cmd run test:e2e
```

Não há lint configurado. `npm test` valida a camada API, pesquisas, quantidades e orquestração operacional com o runner nativo do Node. E2E Playwright depende do backend H2 isolado em localhost:8081 e usa dados reais temporários criados via API, nunca MySQL. Para instalar navegador: `npx.cmd playwright install chromium`. Para iniciar Vite para E2E sem .env: `$env:VITE_API_URL='http://localhost:8081'; npm.cmd run dev`.

## Estrutura

- src/api: único cliente fetch, erros HTTP e contratos.
- src/hooks: carregamento cancelável e atualização de recursos.
- src/components: AppShell, cards, tabelas, estados, modal nativo acessível, campos.
- src/pages: dashboard, catálogo, cadastros, consultas e formulários operacionais.
- src/utils: pesquisas e operações sequenciais com proteção contra repetição incerta.
- src/assets/brand: local dos PNGs oficiais B&S Engenharia, sem marca inventada.
- tests: API e navegação/fluxos E2E.
- scripts: inicialização opcional do backend H2.

## Escopo e limites

Todas as dez páginas usam a API. Dashboard calcula contagens dos registros retornados; não apresenta números offline. Busca de produtos e filtros de ativo/categoria/status/tipo usam contratos existentes. Combinação de filtros de estoque e histórico é local sobre as listas retornadas (a API não possui paginação ou filtros combinados).

Produto preserva textos e vínculos legados na edição; novos cadastros pela interface exigem categoria/unidade ativas. Referência inativa já vinculada continua visível. Tipo de controle é texto livre conforme backend, sem criar enum novo. Sugestões de equivalência não bloqueiam gravação. Categorias/unidades usam PUT completo e enviam explicitamente ativo/fracionamento.

Aprovar exige seleção de funcionário e confirmação; rejeitar exige confirmação. Erros 400/404/409 exibem mensagem do backend. Nenhuma sessão/autenticação é presumida. Não há exclusão física ou edição de saldo. Módulos futuros aparecem como Em breve, sem ações fictícias.

Responsividade: sidebar recolhível até 850px, indicadores em duas colunas no mobile, formulários em uma coluna e tabelas com rolagem horizontal acessível. Modal usa dialog nativo, captura foco e suporta Escape; ações pendentes impedem fechar. Menu de navegação fecha ao selecionar página.

Pendências: paginação, autenticação/permissões, deployment, testes completos de acessibilidade e validação no banco alvo. Não há justificativa de rejeição, edição/exclusão de itens persistidos, idempotency key nem criação transacional com todos os itens nos contratos atuais; os fluxos existentes foram preservados. A nova transferência executa todos os itens em uma única transação no backend.

## Direção visual e verificação

Identidade tipográfica BES, azul principal, fundo neutro, radius 3–6px e sombras mínimas. Dashboard em faixa de indicadores e tabelas hierarquizadas: pendências, saldos e atividade recente. Sem gradientes, glassmorphism, animação decorativa ou cartões de acesso rápido. Sidebar compacta de 226px, filtros densos, foco visível e ações consistentes.

Validação realizada em Chromium: desktop 1440px, tablet 768px e mobile 390px. E2E valida cadastros, edição, busca, aprovação com responsável, débito do saldo, rejeição, navegação mobile, Escape no modal, ausência de overflow da página e estados erro/offline/vazio/404. Capturas em test-results são locais, ignoradas pelo Git. Testes de dados temporários não substituem testes completos de acessibilidade ou testes em Safari/Firefox.

Referências técnicas: [Vite proxy](https://vite.dev/config/server-options) e [Tailwind com Vite](https://tailwindcss.com/docs/installation/using-vite).

## Pesquisas operacionais

- Solicitações: número/ID (com ou sem #), solicitante, almoxarifado e código/nome/descrição dos materiais nos itens. A pesquisa é client-side sobre a lista do status selecionado na API.
- Estoque: código, nome e descrição do produto; combina por AND com produto e almoxarifado.
- Movimentações: código/nome/descrição do material, solicitante, responsável e número da solicitação. Combina por AND com tipo (API), produto, almoxarifado e período (client-side).
- Texto local ignora caixa e acentos. Datas são inclusivas e usam o dia em dataHora, sem reinterpretar o timestamp local do backend como UTC. De > Até apresenta erro. Registros sem data não atendem a filtro de período.
- Catálogo continua usando /produtos/busca, com as regras de comparação do backend. Não há paginação/novo endpoint de pesquisa operacional.

## Nova solicitação e falhas intermediárias

1. Selecionar solicitante e almoxarifado reais.
2. Pesquisar o catálogo; selecionar material, informar quantidade e adicionar à lista. A lista apresenta código, nome, unidade e quantidade. Remover itens somente antes do envio.
3. Criar solicitação: POST /solicitacoes com query parameters, seguido de um POST por item. Não existe criação de produto nesse fluxo.
4. Na conclusão, atualizar listagem e abrir detalhes. Aprovação mantém responsável explícito e confirmação; rejeição também confirma, sem justificativa porque o contrato não aceita esse campo.

Quantidades positivas/finitas são validadas antes de adicionar; unidades configuradas não fracionárias exigem inteiros. Legado sem referência continua sem inferência pela sigla. O backend mantém a autoridade final.

Cada POST é independente: se um item falhar, a solicitação e os itens anteriores permanecem. A interface conserva o ID e o progresso, bloqueia edição do lote enviado e informa claramente o resultado parcial. Conferir itens salvos faz somente GET e compara o prefixo de itens persistidos; somente depois é possível Continuar envio, sem recriar solicitação ou reenviar itens confirmados. Se todos tiverem sido recebidos, a conferência encerra com sucesso.

Uma falha de conexão ou HTTP 5xx pode esconder uma gravação já concluída. Não há repetição automática. Se a criação não retornar ID, o frontend bloqueia o reenvio nessa janela e orienta conferir a listagem. Divergência dos itens ou status alterado também bloqueia continuidade. Sair/recarregar durante envio ou após criação parcial dispara aviso nativo de navegação; o progresso não é restaurado após fechar/recarregar a página. Os registros persistidos continuam acessíveis nos detalhes. Recuperação durável entre sessões/idempotência exige evolução futura do contrato, sem presumir exclusão/rollback de itens pela interface.

## Entrada e saída

Selecionar material, almoxarifado, quantidade, solicitante e responsável. A tela consulta o saldo atual, valida a quantidade e apresenta revisão antes da confirmação. Ao confirmar, consulta novamente o saldo e chama PUT /estoques/entrada ou /estoques/saida. Nenhum saldo é editado diretamente.

Entrada para um par sem estoque exige marcar explicitamente Criar estoque zerado. Usa o POST /estoques existente antes da entrada; se o par for criado simultaneamente (409), consulta o registro e prossegue. Se a entrada falhar depois do cadastro, pode permanecer um registro zerado: não se exclui nem desfaz saldo pela interface.

Saída nunca cria estoque e bloqueia quantidade acima do saldo consultado. O backend revalida saldo e fracionamento sob transação. Sucesso atualiza estoque; o histórico reflete a movimentação criada pelo backend ao abrir Movimentações. Sem edição direta, mínimo/máximo ou reposição.

Em falha 400/404/409, a mensagem é exibida e o saldo é consultado novamente. Em resultado incerto (conexão/5xx), o reenvio na janela é bloqueado; conferir estoque e histórico antes de iniciar outra operação. Não é possível garantir exactly-once sem suporte de idempotência no backend.

## Identidade B&S Engenharia

Design aprovado preservado: sidebar escura, azul #285a7c, tabelas compactas e agrupamento atual dos menus. Refinamentos restritos a contraste, divisores, hover/focus, campos de erro e ações primárias. Dashboard conserva a composição e usa ATENÇÃO OPERACIONAL somente para solicitações pendentes reais.

Nenhuma logo oficial foi encontrada. Continua BES textual. Colocar PNGs oficiais em src/assets/brand/bes-logo-sidebar.png (compacto/transparente para fundo escuro) e src/assets/brand/bes-logo-full.png (completa). Brand.jsx aceita sidebar/full, conserva fallback se ausente/inválido e não cria retângulo branco nem aproximação da marca. Ver src/assets/brand/README.md. Suporte de imagem completa preparado, sem implementar login/documentos/relatórios.

Testes desta rodada ampliam a suíte existente: pesquisas e combinações, quantidade/fracionamento, falha intermediária, resposta perdida após item recebido, cadastro de estoque zerado, entrada/saída reais e formulários operacionais a 1440/768/390px. Todas as gravações E2E são no H2 temporário da porta 8081. Após os testes, comparar visualmente Dashboard, Solicitações, Estoque, Movimentações e Produtos com capturas locais, incluindo formulários e estados.

### Resultado da rodada de polimento operacional

- npm.cmd run build: sucesso.
- npm.cmd test: 19 testes, sem falhas (5 existentes + 14 novos).
- Playwright: 10 testes, sem falhas (4 existentes + 6 novos), com H2 isolado.
- Backend clean test: 91 testes, sem falhas/erros/ignorados; BUILD SUCCESS.
- git diff --check: sem problemas. Nenhuma alteração de código/configuração/schema do backend, nenhum acesso ao MySQL, commit ou push.
- Revisão visual após testes: Dashboard, Solicitações, Estoque, Movimentações e Produtos comparados no navegador. Sidebar, azul, composição e densidade preservados. Formulários novos revisados em desktop e mobile, com erro próximo ao campo e controles nativos consistentes. Nenhum overflow horizontal da página inteira ou erro de runtime nas cinco páginas. Capturas locais revisao-*.png e de formulários em test-results/ não são versionadas.


## Bloco 2 — estoque inteligente e transferências

- Estoque mostra mínimo/máximo e situação Normal/Baixo/Zerado, com modal de configuração e filtro “Precisam de atenção”. Busca anterior e filtros continuam combinados. Saldo <= mínimo configurado é alerta; sugestão usa máximo - saldo ou fica ausente sem máximo. Limites não alteram saldo.
- Dashboard acrescenta tabela compacta de alertas reais; link abre `/estoques?atencao=true`. Falha de carregamento apresenta erro e retry de leitura, sem números inventados.
- `/transferencias`: busca por número/material/local/responsável, filtros origem/destino/produto via API, criação com catálogo e vários materiais, consulta de saldo e validações. Revisão confere novamente os saldos e exige confirmação explícita. Sucesso abre detalhes; “Ver movimentações” navega a `/movimentacoes?transferenciaId=...`, com vínculo de retorno.
- Gravação de transferência é um POST atômico; resposta perdida/5xx impede repetição no rascunho e orienta conferir a listagem. Não há chave de idempotência persistida, edição/cancelamento/exclusão, PDF ou autenticação nesta rodada.
- Estilos aprovados/Brand foram preservados. PNGs oficiais ainda ausentes: fallback textual e caminhos mantidos, sem logo criada.
- Testes: 32 Node (19 anteriores + 13 novos), 20 Playwright (10 anteriores + 10 novos), além de 135 backend (91 anteriores + 44 novos). Usar H2 isolado pelo script existente; **não iniciar servidor de produção/MySQL para E2E**. Novos fluxos/capturas cobrem 1440/768/390px.

Contratos e limitações: [API](../docs/api.md) e [documentação do Bloco 2](../docs/estoque-inteligente.md). Schema novo é manual e não foi aplicado a banco externo. Documento oficial encontrado é v1.3; associação à v1.4 aguarda o texto oficial. Prontidão de revisão funcional não significa prontidão para publicar sem autorização/idempotência/schema homologado.
