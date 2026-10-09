# Auditoria Visual Premium Integrada — Plataforma BES

Data: 08/10/2026. Escopo: diagnóstico integrado dos Blocos 1–7, sem implementação.

## 1. Resumo executivo

A BES possui uma base visual industrial coerente: sidebar escura, azul sóbrio, superfícies neutras, ícones da mesma família e foco na operação. A identidade deve ser preservada. O resultado atual é funcional e reconhecível, mas ainda não apresenta acabamento premium consistente entre todos os módulos.

Os principais obstáculos são navegação mobile com foco em elementos invisíveis, colunas comprimidas, filtros de contexto excessivamente extensos, diferenças de espaçamento entre módulos e documentos longos sem identificação em todas as páginas. Testes funcionais anteriores não equivalem a aprovação visual.

Há também **um achado P0 restrito às jornadas observadas no ambiente H2 desta auditoria**: a devolução de ativo e a confirmação de nova entrega de EPI foram bloqueadas por conflito de integridade. O servidor registrou violações de CHECK. A causa raiz não foi determinada e não se deve atribuí-la automaticamente ao frontend, ao ambiente ou à concorrência. A primeira próxima etapa deve esclarecer esse bloqueio antes de executar refinamentos cosméticos nesses fluxos.

Cobertura: **40 telas/rotas funcionais distintas, mais a tela 404**, nas larguras 390, 768 e 1440 px. Foram produzidas **269 capturas únicas de navegador**, abrangendo **93 combinações nomeadas de tela/estado/jornada**, e **9 PDFs, com 12 páginas**. Capturas não representam 269 telas diferentes. Houve inspeção crítica das imagens, comparação entre módulos, leitura dos PDFs renderizados e verificações de teclado/DOM.

Conclusão: **recomenda-se refinamento incremental, precedido da triagem dos bloqueios operacionais observados. Não conceder aprovação premium integral nesta rodada.**

## 2. Ambiente, checkpoint e limitações

| Item | Evidência |
|---|---|
| Branch | `feature/bes-frontend` |
| HEAD de partida e retomadas | `41de102a66c717307392203c7b3fcda77e3ef462` |
| Estado inicial | Working tree limpa; nenhuma alteração local inesperada |
| Retomadas após indisponibilidade do modelo | Branch/HEAD novamente conferidos; capturas, PDFs, scripts temporários e resultados existentes reutilizados |
| Backend | Launcher oficial `frontend/scripts/start-backend-h2.ps1`, com `-WithTestUsers`; profile `test`, H2 em memória `bes-frontend`, porta 8081 |
| Frontend | Build com `VITE_API_URL=/api`; Vite Preview na porta 5173, proxy para H2 |
| Readiness | HTTP 200 em `/auth/csrf` e `/api/auth/csrf` antes da navegação autenticada |
| Navegador | Chromium real, automatizado pelo Playwright instalado no projeto |
| Dados | Cadastros fictícios de demonstração; autenticação de fixture; sessão e CSRF preservados |
| Código | Nenhuma alteração em frontend, backend, CSS, testes ou regras de negócio |
| Escritas permitidas | Dados descartáveis via API/UI no H2; artefatos ignorados; este relatório |

O navegador integrado da skill `browser:control-in-app-browser` foi tentado duas vezes, mas o Node REPL falhou antes da execução com erro de caminho do kernel. Utilizou-se Chromium/Playwright local como alternativa, registrando essa limitação. A skill `pdf:pdf` orientou a revisão dos documentos existentes; Poppler não estava disponível no PATH e a renderização foi feita com PyMuPDF. Nenhum mockup foi usado como evidência.

O build desta rodada concluiu com sucesso. As suítes completas de backend/Node/Playwright não foram repetidas: não houve alteração de código e esta rodada é de diagnóstico. Os resultados anteriores **566 backend, 125 Node e 95 Playwright** permanecem evidências históricas nos relatórios do Bloco 7, sem serem apresentados como nova execução. Os bloqueios novos descritos aqui precisam ser conciliados com aquela baseline.

Limites: apenas Chromium/Windows, sem aparelho físico, leitor de tela ou impressora física; sem certificação WCAG integral. O cenário longo foi exercitado no pedido de compra; os demais documentos receberam revisão com conteúdo representativo curto. Verificações de 320 px e paisagem complementam, mas não substituem teste completo de zoom do navegador. Não foi concluído um ensaio integral de zoom a 200%/400%.

Referências lidas: `AGENTS.md`, índice `docs/README.md`, documentação mestre v1.4 (RF001–RF218, especialmente requisitos transversais e RF205–RF218), README do frontend, segurança/autenticação, documentação dos módulos e os dois relatórios do Bloco 7. Esta auditoria não reclassifica RFs parciais como concluídos.

## 3. Inventário de rotas realmente inspecionadas

Todas as linhas abaixo possuem evidências nas três larguras obrigatórias. `:id` foi substituído por registro fictício existente. Detalhes em modal não foram contados como nova rota.

| Área | Rotas funcionais | Quantidade |
|---|---|---:|
| Acesso e visão geral | `/login`, `/dashboard` | 2 |
| Cadastros e estoque | `/produtos`, `/categorias`, `/unidades`, `/almoxarifados`, `/funcionarios`, `/estoques`, `/transferencias`, `/movimentacoes` | 8 |
| Solicitações | `/solicitacoes` | 1 |
| Compras | `/fornecedores`, `/necessidades-compra`, `/pedidos-compra`, `/pedidos-compra/:id` | 4 |
| Estrutura | `/obras`, `/obras/:id`, `/ordens-servico`, `/ordens-servico/:id`, `/centros-custo`, `/centros-custo/:id` | 6 |
| Ativos | `/ativos`, `/ativos/:id`, `/emprestimos`, `/transferencias-ativos`, `/inspecoes-ativos` | 5 |
| EPI/SST | `/epis`, `/epi-entregas`, `/epi-entregas/nova`, `/epi-entregas/:id`, `/epi-funcionarios/:id` | 5 |
| Administração | `/usuarios` | 1 |
| Documentos | `/solicitacoes/:id/lista-separacao`, `/pedidos-compra/:id/documento`, `/recebimentos-compra/:id/documento`, `/ordens-servico/:id/documento`, `/ativos/:id/ficha`, `/emprestimos/:id/documento`, `/transferencias-ativos/:id/documento`, `/epi-entregas/:id/documento` | 8 |
| Tratamento de rota inexistente | `/pagina-inexistente` → 404 | +1 |

`/` é redirecionamento para dashboard, não uma tela adicional. Solicitação nova, detalhes, aprovação, atendimento, devoluções e recebimentos aparecem em modais/estados de rotas existentes. Alertas/reposição usam estoque, dashboard e necessidades; não foi inventada uma tela separada. Permissões são administradas pelo perfil do usuário; não existe editor visual independente de permissões nem painel geral de configurações a avaliar. A ficha do funcionário EPI é uma consulta operacional; o comprovante documental pertence ao registro de EPI.

As rotas funcionais com IDs válidos foram acessadas. A navegação para `/usuarios` com perfil CONSULTA foi capturada como acesso bloqueado; a ação de configurar EPI não apareceu para esse perfil. Isso é cobertura visual amostral de permissões, não nova auditoria completa de autorização.

## 4. Identidade visual atual e pontos fortes

- Sidebar de aproximadamente 226 px, fundo escuro `#182733`, ação principal azul `#285a7c`, fundo neutro `#f7f8fa` e bordas discretas.
- Tipografia de sistema, com Segoe UI; títulos claros e corpo operacional compacto. Não há necessidade demonstrada de trocar a família tipográfica.
- Ícones Lucide consistentes; ausência de gradientes e sombras decorativas excessivas.
- Estrutura compartilhada em `AppShell`, `PageHeader`, `DataTable`, `Field`, `Modal`, estados de recurso e `OperationalDocument`.
- Badges incluem texto, evitando depender exclusivamente de cor. Operações de compra e estoque têm explicação do efeito físico antes da confirmação.
- Documentos têm identificação, referência, responsáveis e ressalvas operacionais. O fallback textual B&S/BES é adequado enquanto o asset oficial está ausente.

O sistema já comunica operação e engenharia. A perda de qualidade acontece na aplicação desigual dos fundamentos: cards sem respiro em compras/estrutura convivem com seções amplas em EPI; tabelas de alguns módulos comprimem nomes; filtros legados têm aparência menos organizada que os filtros recolhíveis de ativos/EPI.

## 5. Avaliação por módulo e jornadas

| Módulo | Avaliação observada |
|---|---|
| Login | Simples, sóbrio e legível; formulário central sem decoração excessiva. Preservar o fluxo de sessão existente. |
| Dashboard | Indicadores úteis e links operacionais. Dois grandes estados vazios deslocam atividade recente para baixo; oito indicadores secundários recebem peso semelhante. Melhorar priorização por ação pendente. |
| Produtos/categorias/unidades | Hierarquia e edição consistentes. Busca funciona; colunas e ações exigem rolagem no mobile. Cadastros pequenos não justificam cards adicionais. |
| Estoque | Filtros e ações explícitos; no smartphone saldo e estado ficam fora da primeira porção visível da tabela. Preservar saldo único, entrada/saída e configuração de limites. |
| Transferências/movimentações | Separação conceitual correta. Formulário de transferência longo; movimentações expõem muitos filtros/contextos antes dos resultados. |
| Solicitações | Aprovação, separação e atendimento parcial concluídos pela UI. Modal concentra identificação, contexto, itens e decisão; em 390 px exige rolagem extensa. Histórico e documento são alcançáveis. |
| Fornecedores | Nome longo quebra em fragmentos de poucas letras, inclusive com espaço amplo em outras colunas. É problema comprovado de distribuição de largura. |
| Compras/recebimentos | Rascunho, submissão, aprovação e recebimento parcial exercitados. Explicações de impacto são boas. Cards/detalhes ficam encostados nas bordas; filtros por ID exigem conhecimento interno. |
| Obras/OS/CC | Vínculos e consultas acessíveis; visual mais próximo de exposição de dados do que de uma ficha operacional organizada. Vários estados vazios alongam detalhes. Títulos genéricos de cadastro e nomes técnicos de status reduzem clareza. |
| Ativos | Localização, condição e custódia distinguíveis; contexto resumido no modal é útil. Devolução repetidamente bloqueada no H2; novo empréstimo do mesmo ativo não pôde ser concluído após esse bloqueio. |
| EPI/SST | Formulário em três seções e avisos de CA/validade ajudam. Modal de EPI mantém foco. Entrega inicial com contexto foi criada pela API para consulta; a entrega pela UI sem contexto falhou na confirmação. Substituição, devolução e descarte foram inspecionados como formulários, sem afirmar conclusão física desses fluxos nesta auditoria. |
| Administração | Usuário e funcionário separados corretamente; perfil e vínculo disponíveis. Recomenda-se explicar o efeito de cada perfil junto à seleção, mantendo as permissões atuais. |

### Jornadas e esforço observado

Contagem a partir da tela inicial indicada, sem incluir login, digitação caractere a caractere, navegação de preparação ou capturas. Uma seleção/preenchimento conta como um campo; abrir a opção nativa não é contado como clique adicional. Não é estudo cronometrado com usuários.

| Jornada | Botões/links | Campos/seleções | Resultado desta execução |
|---|---:|---:|---|
| Localizar material em Produtos | 0 | 1 | Busca pelo código confirmada; primeira tentativa usou seletor de automação incorreto, corrigido sem mudança no produto |
| Solicitar um material com OS | 3 | 6 | Solicitação criada; contexto derivado exibido |
| Aprovar solicitação já aberta | 2 | 1 | Concluída |
| Iniciar separação | 2 | 1 | Concluída |
| Atender parcialmente | 3 | 2 | Concluída; histórico exibido |
| Criar pedido de um item, submeter e aprovar | 7 | 7 | Concluída; inclui confirmação de destinação |
| Receber parcialmente pedido existente | 3 | 2 | Concluída; recebimento apareceu no histórico |
| Devolver ferramenta | 2 | 2 | Bloqueada por conflito; não contar como concluída |
| Emprestar novamente o mesmo ativo | — | — | Continuação bloqueada pela devolução não concluída; empréstimo inicial foi preparado por API |
| Entregar um EPI | 2 | 7 | Confirmação bloqueada por conflito, mesmo com quantidade explícita |
| Consultar ficha EPI desde Funcionários | 1 | 0 | Concluída |

Os passos de aprovação/recebimento preservam decisões importantes. A redução de esforço deve priorizar seleção de material/contexto, filtros e visibilidade de ações, sem remover confirmações físicas ou assumir responsáveis a partir do login.

## 6. Avaliação por viewport

| Viewport | Resultado |
|---|---|
| 1440 × 900 | Boa base de navegação e densidade. Sidebar exige rolagem para grupos inferiores. Dashboard e fichas estruturais desperdiçam altura em estados vazios. Compras/estrutura apresentam pouco padding em áreas de conteúdo. |
| 768 × 900 | Menu passa a drawer. Filtros de contexto ocupam parcela importante da tela. Tabelas mantêm rolagem interna, mas colunas desproporcionais continuam prejudicando fornecedores. |
| 390 × 900 | Formulários refluem em coluna; diálogos longos rolam internamente. Saldo, status e ações de tabelas frequentemente ficam à direita. Nome de fornecedor fica fragmentado; contexto ocupa quase a primeira tela de nova solicitação. |
| Complementos | Dashboard em 320 px; dashboard e formulário EPI em paisagem 844 × 390. Não houve overflow horizontal da página nos registros de medição; isso não significa ausência de rolagem dentro de tabelas. |

Touch: links de navegação medidos com 34 px de altura; diversas ações compactas ficam abaixo de 44 px, enquanto ações EPI e de ativos têm tratamento maior. Isso é oportunidade ergonômica; **44 px não foi usado como limite obrigatório de WCAG AA**. Controles inferiores a 24 px exigiriam análise de espaçamento/exceções antes de declarar falha do critério 2.5.8.

## 7. Navegação e sidebar

Existem 23 destinos navegáveis para ADMIN, distribuídos em oito grupos funcionais, mais o grupo de próximo módulo com Inventário desabilitado. A organização por domínio é adequada. A coluna inteira aberta, com todos os grupos, não escala bem: em 900 px de altura, Estrutura e Administração exigem rolagem interna.

Proposta: manter os grupos existentes, permitir recolhimento por grupo, abrir automaticamente o grupo atual e manter indicação de seleção. Avaliar busca de destino ou favoritos somente se teste com usuários demonstrar necessidade. Não esconder funções autorizadas nem fundir estoque, ativos e EPI em um único conceito. O item futuro pode ocupar uma área secundária do roadmap, sem iniciar sua implementação.

Problema de teclado comprovado: com o menu mobile fechado, Tab após o link de pular conteúdo alcançou “Fechar menu” em x=-52 e links da sidebar em x=-216, totalmente fora do viewport. Com drawer aberto, Escape não o fechou. Recomendação: remover elementos fechados da navegação/foco, definir gestão de foco na abertura/fechamento e testar retorno ao disparador. Não basta apenas transformar a posição visual da sidebar.

## 8. Componentes e fundamentos a padronizar

| Fundamento | Ajuste recomendado | O que preservar |
|---|---|---|
| Tokens | Centralizar cor de texto secundário, bordas, raios, espaçamento e alturas; reduzir overrides sucessivos por módulo | Azul BES, fundo neutro e sidebar escura |
| Tipografia | Escala previsível para título, seção, label, tabela e ajuda; revisar textos de 9–11 px | Família de sistema e densidade operacional |
| Cards/fichas | Definir padding de conteúdo e distância entre seções; EPI tem mais respiro que compras/estrutura | Cards discretos e poucas superfícies |
| Tabelas | Larguras mínimas por significado, nome legível, números alinhados, ação descobrível | Tabela semântica, cabeçalhos e rolagem acessível |
| Filtros | Busca principal primeiro, filtros avançados recolhíveis e resumo de filtros ativos | Combinações atuais e contexto histórico |
| Formulários | Agrupamentos, obrigatoriedade consistente, mensagens junto do campo e ações alcançáveis | `Field`, associações de label e regras atuais |
| Modais | Padronizar ciclo de foco/retorno e estratégia de rolagem; testar ações no fim de formulário longo | Dialog nativo, Escape, bloqueio durante envio |
| Status | Dicionário legível para tela/documento; mesma semântica cromática | Estados distintos e texto junto da cor |
| Feedback | Empty state contextual, sucesso próximo da ação, erro com orientação segura | `role=status`, `role=alert`, retry explícito |
| Ícones/interação | Mesma dimensão por contexto e alvos confortáveis no touch | Lucide, movimento discreto, reduced motion |

Não criar uma biblioteca nova de componentes apenas por estética. Estender os componentes compartilhados existentes após testes de regressão.

## 9. Acessibilidade

Referência: [WCAG 2.2](https://www.w3.org/TR/WCAG22/). Avaliação amostral, sem declaração de conformidade integral.

- **Foco invisível no drawer fechado:** reproduzido; ver seção 7 e `checks-extra.json`.
- **Modais:** EPI configuração/devolução mantiveram o foco em 30 Tabs; produto, categoria, devolução de ativo e recebimento tiveram passos com `document.activeElement === BODY`. Escape fechou os seis. Não foi demonstrada interação com o conteúdo de fundo do dialog nativo; a constatação é perda do foco visível/continuidade, não ausência total de isolamento modal.
- **Contraste medido:** texto herdado do breadcrumb `rgb(108,125,138)` sobre branco ≈ **4,25:1**, e rodapé `rgb(105,125,137)` sobre `rgb(247,248,250)` ≈ **4,04:1**. Ambos são textos pequenos; corrigir para pelo menos 4,5:1. A palavra atual em negrito pode usar outra cor e não deve receber automaticamente a mesma medição. [Referência de contraste](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum.html).
- Sidebar: links medidos com contraste aproximado 9,68:1 e texto ativo 11,08:1; títulos de grupo 6,45:1, embora muito pequenos (9 px).
- Labels, cabeçalhos `th`, estados de carregamento e alertas possuem semântica compartilhada. A revisão não cobriu todos os nomes acessíveis com leitor de tela.
- Estados possuem texto; não dependem apenas de cor. Erro de rede simulado em EPI foi anunciado por `role=alert`; “Tentar novamente” recuperou o carregamento.
- `prefers-reduced-motion: reduce` foi exercitado; transição de botão observada em `0s`.
- Reflow de página preservado nas amostras; tabelas exigem rolagem bidimensional. É necessário avaliar conteúdo essencial e operação, não apenas `scrollWidth`. [Referência de reflow](https://www.w3.org/WAI/WCAG22/Understanding/reflow.html).
- Para touch, adotar 44 px como meta ergonômica quando viável; o critério AA 2.5.8 usa 24 px com exceções. [Referência de alvos](https://www.w3.org/WAI/WCAG22/Understanding/target-size-minimum).

## 10. Documentos/PDF/impressão

Foram gerados PDFs reais do navegador para os oito tipos documentais implementados e um pedido longo com 24 materiais de nomes extensos. Todos foram renderizados e inspecionados; total de 12 páginas.

Pontos positivos: A4 retrato, margem CSS de 12 mm, conteúdo textual dentro da página, ações administrativas ausentes na impressão, cabeçalho sóbrio, dados históricos, referência do registro e ressalvas operacionais. Pedido longo preservou linhas inteiras e repetiu o cabeçalho da tabela nas quatro páginas. Não foram observados cortes de texto no conjunto PDF inspecionado.

Pendências: páginas intermediárias do pedido longo não repetem o número do pedido/identidade documental e não têm “página X de Y”. Uma folha separada perde identificação. O rodapé aparece somente ao final do documento. Recomenda-se identificação compacta por página sem ocupar espaço excessivo ou inventar assinatura/QR.

Na tela de 390 px, pedido e separação dependem de rolagem horizontal interna; o PDF é legível, mas a consulta mobile não é equivalente em ergonomia. Propor resumo vertical dos dados essenciais e indicação visível de rolagem, mantendo a tabela completa e o documento A4.

Status de pedido aparece como `PARCIALMENTE_RECEBIDO` no documento, enquanto a interface usa “Parcialmente recebido”. Padronizar apresentação sem alterar o valor de domínio. Não inventar uma ficha impressa de funcionário EPI inexistente: a ficha foi avaliada na tela, e os comprovantes por registro foram avaliados como documentos.

## 11. Achados priorizados

Classificação desta retomada: P0 bloqueante; P1 importante; P2 refinamento relevante; P3 oportunidade futura. Esforço estimado para implementação futura por uma pessoa: pequeno até 1 dia; médio 2–4 dias; grande 5+ dias, incluindo revisão. São estimativas, não autorização ou compromisso.

| ID / prioridade / natureza | Tela e viewport | Problema e impacto | Evidência | Recomendação / frontend / esforço / dependência / risco |
|---|---|---|---|---|
| V01 — **P0**, problema comprovado no H2 | Devolução de ativo e nova entrega EPI; ação exercitada em desktop, resultado capturado nas 3 larguras | Confirmação não termina; aviso de conflito de integridade. Bloqueia as jornadas demonstradas | `checks-final.json`; `devolucao-resultado-validacao-390.png`; `epi-entrega-resultado-final-390.png`; SQLState 23514 no log H2 | Triagem técnica e regressão determinística antes de refinamento desses fluxos. **Não presumir solução só frontend.** Esforço desconhecido; depende de identificar CHECK/causa. Risco alto se corrigir por relaxamento de integridade |
| V02 — **P1**, problema comprovado | Shell mobile/tablet | Elementos da sidebar fechada recebem foco fora da tela; Escape não fecha drawer | `checks-extra.json`, `checks.json`, `mobile-hidden-sidebar-focus-390.png` | Gestão de foco/visibilidade do drawer. Frontend: sim. Médio; depende do AppShell. Risco médio de regressão de navegação/permissões |
| V03 — **P1**, problema comprovado | Fornecedores, sobretudo 390/768 | Nome fragmentado em poucas letras por linha, dificultando identificação | `fornecedores-390.png`, `fornecedores-768.png` | Largura mínima útil para nome, distribuição de colunas e alternativa mobile. Frontend: sim. Pequeno/médio; DataTable. Risco médio para outras tabelas |
| V04 — **P1**, problema comprovado | Breadcrumb/rodapé compartilhados | Contraste pequeno abaixo de 4,5:1 | `checks-extra.json`, medições da seção 9 | Ajustar tokens de texto secundário. Frontend: sim. Pequeno; validar fundos/estados. Risco baixo |
| V05 — **P1**, problema comprovado | Modais produto/categoria/ativos/recebimento | Tab atinge BODY e perde indicação visível; EPI apresenta comportamento diferente | `checks.json`, `checks-extra.json` | Padronizar ciclo e retorno de foco, preservando dialog nativo. Frontend: sim. Médio; componente Modal. Risco médio em formulários e fechamento ocupado |
| V06 — **P2**, inconsistência e ergonomia | Solicitações, movimentações, compras, OS/CC; 3 larguras | Contexto ocupa grande altura; padding e espaçamento pouco consistentes; empurra o objetivo para baixo | `solicitacoes-768.png`, `modal-solicitacao-nova-390.png` | Contexto avançado recolhível com resumo persistente e grid coerente. Frontend: sim. Médio; seletores e defaults atuais. Risco médio de perder filtros/seleções |
| V07 — **P2**, oportunidade comprovada | Estoque e tabelas operacionais; 390 | Saldo, status e ações fora da primeira área visível | `estoques-390.png`, `ativos-390.png` | Priorizar identificação + valor essencial + acesso a detalhes; indicar rolagem. Frontend: sim. Médio/grande; decisões por módulo. Risco médio, sem ocultar informação crítica |
| V08 — **P2**, inconsistência visual | Compras e estrutura versus EPI; 3 larguras | Conteúdo encostado à borda em alguns cards; espaçamento muda entre módulos | `compra-aprovada-1440.png`, `obras-1-1440.png`, `epi-entregas-nova-1440.png` | Tokens de padding e variantes existentes de seção. Frontend: sim. Médio; estilos compartilhados. Risco médio em overflow/print |
| V09 — **P2**, oportunidade | Sidebar; 1440/768/390 | Muitos destinos e grupos sempre abertos; áreas inferiores fora da altura inicial | `dashboard-1440.png`, `sidebar-open-390.png` | Recolhimento por grupo e grupo ativo aberto; preservar todos os destinos autorizados. Frontend: sim. Médio; acessibilidade antes. Risco médio |
| V10 — **P2**, oportunidade | Dashboard e detalhes Obra/OS/CC | Estados vazios grandes consomem mais espaço que ações/atividade útil | `dashboard-1440.png`, `ordens-servico-1-390.png` | Empty state compacto e contextual; priorizar pendências reais. Frontend: sim. Médio; não inventar indicadores. Risco baixo/médio |
| V11 — **P2**, problema documental | Pedido longo, A4, páginas 2–3 | Folhas intermediárias sem referência do pedido ou paginação | `documento-pedido-longo.pdf`, imagens de páginas 2 e 3 | Referência e paginação por página. Frontend/print: sim, validar suporte do navegador. Médio; template compartilhado. Risco médio de quebra de página |
| V12 — **P2**, inconsistência | Documentos e estrutura | Valores de domínio em caixa alta/underscore e labels genéricos reduzem compreensão | `pedidos-compra-1-documento-390.png`, `modal-obras-390.png` | Mapa de rótulos de apresentação; nomear cadastro pelo domínio. Frontend: sim. Pequeno; sem alterar enums/API. Risco baixo |
| V13 — **P2**, oportunidade | Documentos e formulários complexos; 390 | Documento depende de pan horizontal; ações de modais longos exigem rolar até o fim | `pedidos-compra-1-documento-390.png`, `modal-produto-390.png` | Resumo mobile e ações estáveis quando viável, sem sobrepor campos/teclado. Frontend: sim. Médio/grande; testar foco/viewport. Risco médio |
| V14 — **P2**, oportunidade | Filtros de compra/recebimento e cadastro de usuário | IDs internos e nomes de perfis sem explicação exigem memória do operador | `compra-aprovada-1440.png`, `modal-permissoes-390.png` | Seletor pesquisável onde a API já suporta; ajuda breve de perfis. Frontend: em parte, confirmar contrato de busca. Médio; permissões e volume. Risco médio |
| V15 — **P3**, preferência estética/ergonômica | Textos auxiliares e navegação | Escala auxiliar 9–11 px e alvos de 34 px podem ser mais confortáveis | `checks-extra.json` | Ajuste gradual de escala e touch sem perder densidade. Frontend: sim. Médio; teste com usuários/dispositivo. Risco médio de aumentar altura |
| V16 — **P3**, oportunidade futura | Descoberta de destinos | Sidebar crescerá em próximos blocos | Inventário de 23 destinos atual | Avaliar busca de navegação/favoritos somente após validar grupos recolhíveis. Frontend: provavelmente. Médio/grande; pesquisa com operadores. Não iniciar Bloco 8 |

### Detalhamento do bloqueio V01 e limites da conclusão

Ativo de demonstração `FER-000001`, empréstimo #1, contexto Obra/OS/CC. Foram preenchidos “Quem devolveu” e “Quem recebeu”, condição BOM e destino padrão “Retornar ao local de origem”. A UI manteve o modal e exibiu “Conflito de integridade ou operação concorrente”. A última tentativa não tinha campos HTML inválidos. O log registrou CHECK `CONSTRAINT_393` em `bes_registro_ativo`, SQLState 23514, às 12:02:00 locais, requestId `c9082460-fb06-4e00-a9f5-3ac802fd02f9`.

Entrega EPI para funcionário #1, almoxarifado #1, produto `BES-DEMO-2`, quantidade 1 e confirmação marcada, sem contexto selecionado. A UI permaneceu em `/epi-entregas/nova` com o mesmo aviso. O log registrou CHECK `CONSTRAINT_6B` em `bes_epi_registro`, SQLState 23514, às 12:02:03 locais, requestId `15df2b10-e643-4759-b285-56f9851e2488`. Entrega anterior com contexto, criada pela API para preparar a consulta, havia funcionado. Essa diferença é pista de reprodução, **não comprovação de causa**.

As primeiras automações continham limitações: seletor de busca incorreto; espera de URL EPI ampla que também aceitava `/nova`; devolução tratada como terminada antes de verificar o resultado. Os JSONs originais foram preservados, mas seus marcadores `completed` nessas jornadas **não são evidência de sucesso**. Os resultados finais acima e as capturas prevalecem. Não houve correção, relaxamento de constraint ou manipulação direta do banco.

Os bloqueios V01 são distintos do HTTP 500 histórico do Bloco 5. Não se declarou que reproduzem ou explicam aquele incidente.

## 12. Evidências reais e rastreabilidade

Diretório ignorado pelo Git: [`frontend/test-results/premium-20261008/`](../frontend/test-results/premium-20261008/).

Índice consolidado: [`inventory-final.json`](../frontend/test-results/premium-20261008/inventory-final.json). Os arquivos `manifest*.json` relacionam nome, rota, viewport, largura da página, tabelas e alertas. `checks*.json` preservam verificações e tentativas; ler as ressalvas de V01 ao interpretar os primeiros resultados. `pdf-review.json` registra dimensões, páginas e extração textual; a aprovação de layout foi feita nas imagens renderizadas, não apenas no texto extraído.

| Evidência representativa | Arquivo |
|---|---|
| Dashboard desktop | [dashboard-1440.png](../frontend/test-results/premium-20261008/dashboard-1440.png) |
| Estoque mobile | [estoques-390.png](../frontend/test-results/premium-20261008/estoques-390.png) |
| Solicitações tablet | [solicitacoes-768.png](../frontend/test-results/premium-20261008/solicitacoes-768.png) |
| Fornecedor com nome comprimido | [fornecedores-390.png](../frontend/test-results/premium-20261008/fornecedores-390.png) |
| Pedido aprovado pela UI | [compra-aprovada-1440.png](../frontend/test-results/premium-20261008/compra-aprovada-1440.png) |
| Solicitação parcialmente atendida | [jornada-atendida-parcial-390.png](../frontend/test-results/premium-20261008/jornada-atendida-parcial-390.png) |
| Ficha EPI | [epi-funcionarios-1-768.png](../frontend/test-results/premium-20261008/epi-funcionarios-1-768.png) |
| Substituição EPI preparada | [epi-substituicao-preparada-390.png](../frontend/test-results/premium-20261008/epi-substituicao-preparada-390.png) |
| Loading, vazio e erro | `estado-loading-{largura}.png`, `estado-vazio-{largura}.png`, `estado-erro-{largura}.png` |
| Modal com contexto | [modal-solicitacao-nova-390.png](../frontend/test-results/premium-20261008/modal-solicitacao-nova-390.png) |
| Documento longo | [documento-pedido-longo.pdf](../frontend/test-results/premium-20261008/documento-pedido-longo.pdf) |
| EPI impresso | [epi-entregas-1-documento.pdf](../frontend/test-results/premium-20261008/epi-entregas-1-documento.pdf) |

Há pranchas `sheet-*` e `qa-*` para comparação; algumas preservam estados transitórios de carregamento. Capturas posteriores de Produtos e Ativos confirmaram tabelas prontas. Não interpretar uma prancha de loading como tela permanentemente indisponível. Formulários estruturais também tiveram capturas transitórias, além dos estados prontos.

Logs temporários: `%TEMP%/bes-premium-build.log`, `%TEMP%/bes-premium-h2.log`, `%TEMP%/bes-premium-preview.log`. Scripts de auditoria permaneceram em `%TEMP%/bes-premium-*.mjs`. Nenhuma senha, cookie de sessão ou token foi escrito nos relatórios/manifestos. Esses artefatos locais não são adicionados ao Git e podem ser removidos por limpezas futuras do ambiente; arquivá-los separadamente se for necessário manter evidência permanente.

## 13. Plano de refinamento e top 10

Antes das ondas: investigar V01 em H2 com reprodução mínima, identificar a constraint efetiva e confirmar a diferença entre os fluxos de fixture e UI. Correção e regressão dependem de nova autorização de implementação; não foram realizadas nesta rodada.

| Onda | Entrega proposta | Impacto e risco |
|---|---|---|
| A — fundamentos | Contraste, escala, tokens, padding e foco modal; piloto em cadastro simples | Alto alcance; risco médio pela natureza compartilhada. Estimativa 3–5 dias |
| B — navegação | Drawer acessível, retorno de foco, grupos recolhíveis e estado ativo persistente | Alto impacto mobile; risco médio de navegação/permissões. 2–4 dias |
| C — operação | Fornecedores, tabela de estoque, filtros/contexto, empty states, fichas e labels | Alto impacto em velocidade/clareza; risco médio. 5–8 dias, dividido por módulo |
| D — mobile/documentos | Operação de formulários longos, resumo mobile, identificação por página e revisão A4 | Impacto alto em campo/documentos; risco médio de reflow/print. 3–5 dias |

Faixas não incluem V01 e dependem de aprovação do escopo. Cada onda deve produzir comparação antes/depois com a mesma massa e viewports. Não fazer redesign geral em uma única alteração.

Top 10 em ordem recomendada:

1. Esclarecer os bloqueios de confirmação V01 e preparar regressão.
2. Corrigir o foco invisível e o ciclo de abertura/fechamento do drawer.
3. Tornar nomes de fornecedores legíveis em todas as larguras.
4. Corrigir contraste dos textos secundários compartilhados.
5. Uniformizar foco e retorno de foco nos modais.
6. Organizar contexto/filtros avançados com resumo das seleções.
7. Dar prioridade a saldo, status e ação nas tabelas mobile.
8. Padronizar padding e hierarquia entre compras, estrutura e EPI.
9. Reduzir altura de estados vazios e priorizar pendências no dashboard.
10. Identificar cada página dos documentos longos e melhorar consulta mobile.

## 14. Riscos de regressão e itens que não devem ser alterados

Preservar integralmente: saldo único em Estoque, atomicidade, snapshots, idempotência, locks e regras de devolução/substituição; distinção de usuário autenticado e responsável físico; confirmação de recebimento e atendimento humano; contexto Obra/OS/CC; autorização HTTP/service; sessão HttpOnly, CSRF e auditoria.

Não redesenhar o domínio para simplificar a aparência. Não fundir ativo patrimonial com quantidade de produto; não tratar recebimento como consumo; não converter alertas de CA em declaração de aptidão legal; não retirar confirmação de destino antigo do EPI. Não mudar endpoints, migrações ou enumerações para obter rótulos mais bonitos.

Preservar identidade azul/industrial, sidebar escura, neutralidade, ícones atuais, template documental e fallback textual. Não copiar a estética dark/lime do Fitness App. Não substituir tabelas densas por uma grade genérica de cards em desktop. Não criar logo, assinatura ou QR fictício.

Validação futura: testes dirigidos aos componentes/fluxos alterados, perfis ADMIN/GESTOR/ALMOXARIFE/CONSULTA, teclado e retorno de foco, dados longos, erro/retry e prevenção de duplicação, 390/768/1440, print de uma e várias páginas. Após mudanças materiais compartilhadas, executar a regressão completa oficial H2 + Vite Preview com readiness. Esta recomendação não declara que essas execuções futuras já ocorreram.

## 15. Pendências preservadas

QR Code; logo oficial B&S/BES; anexos/evidências; valorização financeira do consumo; homologação em banco real; rotação externa de credenciais históricas; HTTPS/proxy; migrações controladas; backup e restore testado; observabilidade; monitoramento; sessão/rate limiting em produção.

**HTTP 500 do Bloco 5: CAUSA NÃO IDENTIFICADA. NÃO REPRODUZIDO. RISCO RESIDUAL.** A investigação anterior está preservada em `bloco7-auditoria-pre-commit.md`. Os novos conflitos de integridade não explicam aquele HTTP 500. Não há declaração de resolução nem de prontidão para produção.

Pendências específicas desta auditoria: causa de V01; leitor de tela; dispositivos touch reais; zoom efetivo 200%/400%; impressão física; cenários longos dos demais documentos; validação com operadores. Não foram transformadas em alegações de conformidade.

## 16. Recomendação objetiva e encerramento

A BES tem identidade industrial aproveitável e boa base operacional. O próximo passo é **uma rodada delimitada de triagem do bloqueio V01 e correções de acessibilidade/legibilidade de maior impacto**, seguida das ondas de refinamento. A auditoria fornece diagnóstico; nenhuma onda foi implementada.

O único arquivo de trabalho a incorporar futuramente é este relatório. Evidências e builds permanecem ignorados. Branch e HEAD preservados; `main` não foi alterada. Sem staging, commit, push, merge, reescrita histórica, banco real, SQL real, deploy, produção ou Bloco 8. A execução encerra após a documentação e conferência final do Git, aguardando autorização para implementação.
