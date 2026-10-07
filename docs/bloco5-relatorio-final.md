# Bloco 5 — relatório final de implementação

Checkpoint: `feature/bes-frontend` / `354252ab9e3d3ad60cf6ebb692c56b880cff70a5`, árvore inicial limpa. Fonte mestre v1.4 consultada antes do bloco e relida para cobertura. Decisões e RFs em [Obras/OS/CC](obras-os-centros-custo.md); contratos em [API](api.md); [matriz de segurança](autenticacao-autorizacao.md).

O incremento conecta estrutura, demanda, necessidade, compra multiobra, recebimento físico e consumo quantitativo confirmado. Não atribui custo financeiro ao consumo sem valorização nem implementa módulos futuros por preparação arquitetural.

## Validação e revisão

Backend completo verde (385), frontend Node (86), build e npm audit zero. Playwright integral aprovado: 60 = 47 anteriores + 13 novos, zero falhas, em 5,0 minutos. Onze casos novos já haviam passado isoladamente antes da retomada. Todos os testes de gravação são H2, sem banco externo. Artefatos/capturas/PDFs/logs locais ficam ignorados ou em TEMP, fora do inventário de entrega.

A revisão corrigiu os achados do item 83 durante a implementação. Falhas intermediárias dos novos mapeamentos e testes foram corrigidas; comparação da baseline confirmou os 328 testes backend anteriores, 72 Node e inventário dinâmico de segurança, sem retirar testes ou reduzir assertions existentes. Novo teste de ciclo longo usa timeout próprio de 90s pela quantidade de operações; suíte anterior conserva seus limites, workers=1/retries=0.

## Os 94 pontos solicitados

| Nº | Ponto | Resultado |
|---|---|---|
| 1 | Branch | feature/bes-frontend. |
| 2 | HEAD inicial | 354252ab9e3d3ad60cf6ebb692c56b880cff70a5, preservado. |
| 3 | Estado inicial | Árvore limpa; log/checkpoint do usuário conferidos antes de editar. |
| 4 | Arquitetura | Monólito modular; pacote obras; contexto embutido nas origens e consumo, sem duplicar compra/catalogo. |
| 5 | Obra | Cadastro, edição protegida, consulta paginada/detalhe/resumo e transição de status. |
| 6 | Campos da Obra | Código/nome, cliente identificado, descrição/localidade/observação, responsável e datas; atores/timestamps/versão do servidor. |
| 7 | Status da Obra | PLANEJADA, ATIVA, SUSPENSA, CONCLUIDA, CANCELADA; terminais não reabrem. |
| 8 | Centro de custo | Código/nome/descrição/tipo/ativo, Obra opcional, histórico sem delete. |
| 9 | Tipos de CC | OBRA, ADMINISTRATIVO, OPERACIONAL, OUTRO; OBRA exige referência. |
| 10 | CC–Obra | Vínculo opcional e imutável; centros corporativos disponíveis via incluirGerais. |
| 11 | OS | Cadastro com Obra obrigatória, CC/responsável/prioridade opcionais, título/descrição/observações e datas. |
| 12 | Status da OS | ABERTA, EM_ANDAMENTO, SUSPENSA, CONCLUIDA, CANCELADA. |
| 13 | Número da OS | OS-ano-ID, IDENTITY + unique, sem MAX+1; concorrência testada. |
| 14 | Obra–OS | Uma Obra para várias OS; Obra da OS imutável desde criação. |
| 15 | OS–CC | CC corporativo ou da própria Obra; vínculo imutável; default da OS, sem default automático da Obra. |
| 16 | Coerência | Servidor deriva dimensões ausentes e rejeita Obra/OS/CC contraditórios e referências inexistentes. |
| 17 | Contexto reutilizável | ContextoOperacional/ContextoService, seletor e apresentação centralizados. |
| 18 | Solicitação | Criação compatível acrescenta IDs opcionais; contexto congela na origem, sem endpoint para alterar. |
| 19 | Atendimento | Entrega humana explícita preservada; saída recebe snapshot da solicitação. |
| 20 | Movimentação | Snapshot na SAÍDA contextual; ENTRADA/transferência/legado gerais não recebem Obra fictícia. |
| 21 | Histórico/snapshots | IDs e identificações históricas congeladas; renomear estrutura não muda origem; vínculos estruturais bloqueados. |
| 22 | Necessidade | Deriva contexto da solicitação imutável; filtros no banco antes do progresso por lotes. |
| 23 | Pedido de compra | Sem Obra global; contexto por alocação e quantidade adicional do item manual. |
| 24 | Multiobra | Duas necessidades do mesmo produto em um pedido preservam Obra/OS/CC; filtros usam a mesma origem. |
| 25 | Compra geral | Continua com contexto null e declaração paraEstoque; recebimento válido sem vínculos inventados. |
| 26 | Recebimento | Entrada física/idempotente/atômica; destinações preservadas; nenhuma saída/entrega automática. |
| 27 | Comprado/recebido/consumido | Preços/compromisso, conferência de entrada e saída quantitativa são fatos distintos; não somados como custo. |
| 28 | Custo | custoConsumido=null e aviso explícito; sem margem/contabilidade falsa. |
| 29 | Valorização | Custo médio/FIFO/lote/valorização de aquisição dependem de método aprovado; não implementados. |
| 30 | Consultas analíticas | Filtros contextuais e projeções/contagens/agrupamentos quantitativos, sem DW. |
| 31 | Resumo de Obra | OS abertas, demandas, necessidades, pedidos e materiais; contagens totais e listas limitadas a 100. |
| 32 | Resumo de OS | Dados/Obra/CC/responsável/datas, relações e materiais; sem custo monetário de consumo. |
| 33 | Resumo de CC | Cadastro/tipo/situação/Obra, relações e consumo quantitativo. |
| 34 | Filtros | Obra termo/cliente/status; OS termo/Obra/CC/status; CC termo/tipo/ativo/Obra; demanda/necessidade/pedido/movimento por contexto. |
| 35 | Endpoints Obra | GET lista/id/resumo; POST; PUT id/status. |
| 36 | Endpoints OS | GET lista/id/resumo; POST; PUT id/status. |
| 37 | Endpoints CC | GET lista/id/resumo; POST; PUT id; nenhum DELETE. |
| 38 | Consultas contextuais | Filtros nos endpoints existentes e três resumos; sem API pública de BI. |
| 39 | Inventário | 17 novos handlers, 8 escritas; total 102/44; HEAD/OPTIONS implícitos não contados. |
| 40 | Authorities | OBRA_LER/GERENCIAR, ORDEM_SERVICO_LER/GERENCIAR, CENTRO_CUSTO_LER/GERENCIAR. |
| 41 | Matriz | ADMIN/GESTOR gestão; ALMOXARIFE/CONSULTA leitura; nenhuma gestão de usuários concedida. |
| 42 | Service authorization | Todos métodos públicos operacionais protegidos; ContextoService exige identidade validada. |
| 43 | CSRF | Todas 44 escritas protegidas; inventário dinâmico cobre ausência/token inválido; arquitetura preservada. |
| 44 | IDOR | IDs não concedem autoridade; referências/coerência validam no backend. Leitura global da baseline permanece, sem multitenancy inventado. |
| 45 | Mass assignment | DTO estrito; atores, número de OS, versão e datas internas rejeitados; contexto de alocação não é entrada. |
| 46 | Auditoria | Eventos de criação/edição/status/inativação com referência exata, estados seguros e transação compartilhada. |
| 47 | Ator autenticado | SecurityContext validado; responsável Funcionario separado; nenhum ator recebido no body. |
| 48 | Transições | Estado/motivo/demandas pendentes validados; terminal/repetição recusados; datas do servidor. |
| 49 | Concorrência | Unique de códigos e número, PESSIMISTIC_WRITE/refresco e versões; testes paralelos e transição sem evento duplicado. |
| 50 | Constraints | FKs/uniques/checks/índices no JPA e scripts; nullable para contexto legado. |
| 51 | Tela Obra | Pesquisa, cliente/status, cadastro/edição/status e detalhe operacional. |
| 52 | Tela OS | Número/título/Obra/CC/status/responsável, cadastro/edição/transição, detalhes e ficha. |
| 53 | Tela CC | Código/nome/tipo/Obra/ativo, criação/edição/inativação e resumo. |
| 54 | Seletor contextual | Obra filtra OS; OS sugere CC; pesquisa limitada/refinável; novo vínculo exige estado admissível; histórico também aceita filtros encerrados. |
| 55 | Sidebar | ESTRUTURA evoluído com três cadastros, almoxarifados/funcionários; retirado placeholder Obras/OS; sem redesign/command palette. |
| 56 | Dashboard | Dois indicadores compactos: Obras ATIVA e OS ABERTA, sem multiplicar cards. |
| 57 | Documentos | Ficha OS A4 e contexto nos documentos existentes; origens por alocação; template OperationalDocument. |
| 58 | Documentação existente | AGENTS, API, índice, autenticação, atendimento, compras e frontend atualizados; relatórios históricos preservados. |
| 59 | Logo B&S | PNGs oficiais ausentes; fallback B&S Engenharia/Plataforma BES e caminhos preparados; nenhuma logo inventada. |
| 60 | QR | Não implementado; RF212 permanece não atendido. |
| 61 | Preparação BI | Dimensões/fatos, granularidade/alocações, NULL histórico e requisitos futuros documentados; nenhuma integração Power BI. |
| 62 | Indicadores confiáveis | Solicitado/atendido conhecidos, consumo quantitativo confirmado e relações/quantidades recebidas por contexto. |
| 63 | Indicadores não confiáveis | Custo contábil consumido, margem, custo médio e orçamento × realizado completo. |
| 64 | Script MySQL | docs/sql/obras-os-centros-custo-mysql-manual.sql, preparado e não executado. |
| 65 | Script PostgreSQL | docs/sql/obras-os-centros-custo-postgresql-manual.sql, preparado e não executado; migração real não certificada. |
| 66 | Legado | Sem backfill; novas dimensões nullable; origens antigas continuam sem contexto quando desconhecido. |
| 67 | N+1 | Listagens paginadas e resumos por projeções; testes com 12 OS mantêm consultas constantes; 77 testes anteriores de compras preservados. |
| 68 | Responsividade | 1440/768/390 em cadastros/detalhes/seletor/ficha; tabelas rolam localmente; capturas revisadas. |
| 69 | Acessibilidade | Field labels/IDs, selects nativos, foco/modal nativo, estados/avisos; sem alteração das cores/layout aprovado. |
| 70 | RFs atendidos | RF061/RF203 no fluxo atual; RF056/057 baseline preservada; RF207/208/209/216/217/218 no escopo documental atual. |
| 71 | RFs parciais | RF010/033/055/063/065–069/071/003/038/053/148/150/205/206/210/211/213–215; limites por RF no documento funcional. |
| 72 | RFs não atendidos | RF062/064/212 e módulos futuros orçamento/logística/manutenção/mobilização/documentos centrais/mobilidade; preparação não conclui requisitos. |
| 73 | Backend | clean test: 385 = 328 anteriores + 57 novos; zero falhas/erros/skips; inclui teste do tipo físico VARCHAR e CHECK. |
| 74 | Frontend Node | 86 = 72 anteriores + 14 novos; zero falhas. |
| 75 | Playwright | 60 = 47 anteriores + 13 novos, todos aprovados na execução final (5,0 minutos; exit 0). Suíte nova anterior isolada: 11/11. |
| 76 | Build | npm run build aprovado após correção da marcação JSX do bloqueio de resultado incerto. |
| 77 | npm audit | Zero vulnerabilidades em todas as severidades; nenhuma dependência nova. |
| 78 | Secrets | Scan dos 76 candidatos: zero achados de credencial real/segredo histórico; fixtures exclusivamente H2. |
| 79 | Diff check | git diff --check limpo; newline extra de repository corrigido. |
| 80 | Git status | Alterações unstaged e arquivos novos; staging vazio. HEAD permanece no checkpoint. |
| 81 | Arquivos novos | Inventário abaixo, gerado do Git sem staging. |
| 82 | Arquivos modificados | Inventário abaixo, gerado do Git sem staging. |
| 83 | Achados adversariais | Conflito de colunas actor; referência LAZY dentro de embeddable sob refresh; mistura de dimensões de alocações; seleção de CC corporativo; argumento vazio de API legado; resultado incerto em criação; JSX do bloqueio; ordem de locks do lote manual, contexto de parcela ainda não recebida e domínio ENUM/CHECK rejeitando cadastro válido no H2 do navegador. |
| 84 | Correções | Colunas explícitas; FKs read-only fora do embeddable; filtros na MESMA alocação com LEFT joins; incluirGerais; payload legado idêntico; bloqueio de criação incerta e markup corrigido. Lote contextual por dimensão/ID, quantidade/contexto efetivamente recebidos e VARCHAR alinhado aos scripts mantendo CHECK. Nenhum teste anterior removido/afrouxado. |
| 85 | Limitações/riscos | Sem escopo por Obra/usuário, sem valorização, relações de resumo limitadas, ORM/drivers reais ainda dependem de homologação, sem idempotência persistida de criação estrutural. |
| 86 | Gate produção | Não pronta: rotação externa histórica, HTTPS/config segura, schema/backup/restore, observabilidade, sessão/rate limit/deploy reais. |
| 87 | MySQL | Não acessado; testes usam somente H2. |
| 88 | Produção | Não acessada/publicada. |
| 89 | Main | Não alterada: 0137ddbddf403f8331d1a3f6d632428f7614ee15 local. |
| 90 | Commit | Nenhum commit criado; HEAD não mudou. |
| 91 | Push | Nenhum push executado. |
| 92 | Merge | Nenhum merge/rebase/reset/clean/force-push/rewrite executado. |
| 93 | Segredos | Nenhum valor real, cookie/CSRF/sessão foi incluído em código/documentação/relatório ou exibido. |
| 94 | Pronto para auditoria pré-commit | SIM: checks finais aprovados. Não equivale a produção/commit autorizado. |

## Inventário de entrega

28 arquivos novos e 48 modificados (76 candidatos), todos unstaged.

### Novos

- `docs/bloco5-relatorio-final.md`
- `docs/obras-os-centros-custo.md`
- `docs/sql/obras-os-centros-custo-mysql-manual.sql`
- `docs/sql/obras-os-centros-custo-postgresql-manual.sql`
- `frontend/src/components/ContextSelector.jsx`
- `frontend/src/pages/ServiceOrderDocument.jsx`
- `frontend/src/pages/Structure.jsx`
- `frontend/src/utils/context.js`
- `frontend/tests/context.test.js`
- `frontend/tests/structure.spec.js`
- `src/main/java/br/com/almoxarifado/obras/CentroCusto.java`
- `src/main/java/br/com/almoxarifado/obras/CentroCustoController.java`
- `src/main/java/br/com/almoxarifado/obras/CentroCustoRepository.java`
- `src/main/java/br/com/almoxarifado/obras/ContextoOperacional.java`
- `src/main/java/br/com/almoxarifado/obras/ContextoService.java`
- `src/main/java/br/com/almoxarifado/obras/EstruturaService.java`
- `src/main/java/br/com/almoxarifado/obras/Obra.java`
- `src/main/java/br/com/almoxarifado/obras/ObraController.java`
- `src/main/java/br/com/almoxarifado/obras/ObraRepository.java`
- `src/main/java/br/com/almoxarifado/obras/ObrasInput.java`
- `src/main/java/br/com/almoxarifado/obras/OrdemServico.java`
- `src/main/java/br/com/almoxarifado/obras/OrdemServicoController.java`
- `src/main/java/br/com/almoxarifado/obras/OrdemServicoRepository.java`
- `src/main/java/br/com/almoxarifado/obras/ResumoEstruturaService.java`
- `src/main/java/br/com/almoxarifado/obras/StatusObra.java`
- `src/main/java/br/com/almoxarifado/obras/StatusOrdemServico.java`
- `src/main/java/br/com/almoxarifado/obras/TipoCentroCusto.java`
- `src/test/java/br/com/almoxarifado/obras/ObrasTests.java`

### Modificados

- `AGENTS.md`
- `docs/README.md`
- `docs/api.md`
- `docs/atendimento-solicitacoes.md`
- `docs/autenticacao-autorizacao.md`
- `docs/compras-recebimento.md`
- `frontend/README.md`
- `frontend/package.json`
- `frontend/src/App.jsx`
- `frontend/src/api/client.js`
- `frontend/src/auth/permissions.js`
- `frontend/src/components/AppShell.jsx`
- `frontend/src/pages/Dashboard.jsx`
- `frontend/src/pages/Operations.jsx`
- `frontend/src/pages/PurchaseDocument.jsx`
- `frontend/src/pages/PurchaseNeeds.jsx`
- `frontend/src/pages/PurchaseOrders.jsx`
- `frontend/src/pages/RequestDetails.jsx`
- `frontend/src/pages/RequestForm.jsx`
- `frontend/src/pages/SeparationList.jsx`
- `frontend/src/styles.css`
- `frontend/src/utils/operations.js`
- `frontend/src/utils/purchases.js`
- `src/main/java/br/com/almoxarifado/compras/ComprasController.java`
- `src/main/java/br/com/almoxarifado/compras/ComprasInput.java`
- `src/main/java/br/com/almoxarifado/compras/ComprasViews.java`
- `src/main/java/br/com/almoxarifado/compras/ItemPedidoCompra.java`
- `src/main/java/br/com/almoxarifado/compras/PedidoCompraService.java`
- `src/main/java/br/com/almoxarifado/controller/MovimentacaoController.java`
- `src/main/java/br/com/almoxarifado/controller/NecessidadeCompraController.java`
- `src/main/java/br/com/almoxarifado/controller/SolicitacaoController.java`
- `src/main/java/br/com/almoxarifado/dto/NecessidadeCompraResponse.java`
- `src/main/java/br/com/almoxarifado/dto/OperacaoSolicitacaoResponse.java`
- `src/main/java/br/com/almoxarifado/model/Movimentacao.java`
- `src/main/java/br/com/almoxarifado/model/Solicitacao.java`
- `src/main/java/br/com/almoxarifado/repository/MovimentacaoRepository.java`
- `src/main/java/br/com/almoxarifado/repository/NecessidadeCompraRepository.java`
- `src/main/java/br/com/almoxarifado/repository/SolicitacaoRepository.java`
- `src/main/java/br/com/almoxarifado/security/AuditoriaAspect.java`
- `src/main/java/br/com/almoxarifado/security/Perfil.java`
- `src/main/java/br/com/almoxarifado/security/Permissao.java`
- `src/main/java/br/com/almoxarifado/security/RotasPermissao.java`
- `src/main/java/br/com/almoxarifado/security/SegurancaConfig.java`
- `src/main/java/br/com/almoxarifado/service/AtendimentoSolicitacaoService.java`
- `src/main/java/br/com/almoxarifado/service/MovimentacaoService.java`
- `src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java`
- `src/main/java/br/com/almoxarifado/service/SolicitacaoService.java`
- `src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java`


## Retomada após interrupção — 06/10/2026

Checkpoint confirmado antes de editar: branch/HEAD oficiais preservados; 76 arquivos candidatos encontrados, 48 modificados e 28 novos, staging vazio. Nenhum trabalho descartado/reiniciado. A implementação havia parado no fechamento dos checks e do relatório: 382 backend, 86 Node, build/audit aprovados, 11 E2E novos isolados aprovados, execução integral 57/58 com timeout no teste anterior de login inválido.

Inspeção confrontou os arquivos candidatos, entidades/services/DTOs/consultas/rotas/componentes/scripts, testes e documentação obrigatória com a mestre v1.4. O estado inicial por grupo, antes de novo código, está abaixo. “Implementado” é o escopo incremental autorizado; preparação BI não declara Power BI entregue.

| Grupo | Área | Estado encontrado | Continuação necessária |
|---|---|---|---|
| A | Obras | IMPLEMENTADO | Preservar implementação e validar regressão. |
| B | Centros de Custo | IMPLEMENTADO | Preservar implementação e validar regressão. |
| C | Ordens de Serviço | IMPLEMENTADO | Preservar implementação e validar regressão. |
| D | Status/transições | IMPLEMENTADO | Preservar implementação e validar regressão. |
| E | Códigos/números únicos | IMPLEMENTADO | Preservar implementação e validar regressão. |
| F | Concorrência | PARCIAL | Ordenar locks do lote de contextos manuais. |
| G | Contexto Obra/OS/CC | IMPLEMENTADO | Preservar implementação e validar regressão. |
| H | Coerência | IMPLEMENTADO | Preservar implementação e validar regressão. |
| I | Solicitações | IMPLEMENTADO | Preservar implementação e validar regressão. |
| J | Atendimento | IMPLEMENTADO | Preservar implementação e validar regressão. |
| K | Movimentações | IMPLEMENTADO | Preservar implementação e validar regressão. |
| L | Necessidades | IMPLEMENTADO | Preservar implementação e validar regressão. |
| M | Pedidos | IMPLEMENTADO | Preservar implementação e validar regressão. |
| N | Compra multiobra | IMPLEMENTADO | Preservar implementação e validar regressão. |
| O | Compra geral | IMPLEMENTADO | Preservar implementação e validar regressão. |
| P | Recebimento | PARCIAL | Não antecipar contexto manual em recebimento parcial. |
| Q | Histórico/snapshot | IMPLEMENTADO | Preservar implementação e validar regressão. |
| R | Resumos | IMPLEMENTADO | Preservar implementação e validar regressão. |
| S | Filtros | IMPLEMENTADO | Preservar implementação e validar regressão. |
| T | Authorities | IMPLEMENTADO | Preservar implementação e validar regressão. |
| U | Service security | IMPLEMENTADO | Preservar implementação e validar regressão. |
| V | CSRF | IMPLEMENTADO | Preservar implementação e validar regressão. |
| W | Auditoria | IMPLEMENTADO | Preservar implementação e validar regressão. |
| X | Frontend | IMPLEMENTADO | Preservar implementação e validar regressão. |
| Y | Sidebar | IMPLEMENTADO | Preservar implementação e validar regressão. |
| Z | Dashboard | IMPLEMENTADO | Preservar implementação e validar regressão. |
| AA | Documentos | PARCIAL | Ajustar apresentação das parcelas realmente recebidas. |
| AB | Scripts SQL | IMPLEMENTADO | Preservar implementação e validar regressão. |
| AC | Documentação | PARCIAL | Concluir evidências/relatório da retomada. |
| AD | Testes backend | IMPLEMENTADO | Preservar implementação e validar regressão. |
| AE | Testes frontend | IMPLEMENTADO | Preservar implementação e validar regressão. |
| AF | Playwright | PARCIAL | Investigar timeout e repetir suíte integral. |
| AG | Preparação BI/Power BI | IMPLEMENTADO | Preservar implementação e validar regressão. |

Não implementado, por escopo: Power BI integrado/DW, valorização contábil/financeiro, orçamento completo, QR, logos oficiais ausentes, PDF backend e módulos futuros. Esses itens permanecem explicitados na matriz RF, sem serem confundidos com lacunas da implementação autorizada.

Continuação: locks determinísticos de todo o lote manual antes dos locks anteriores de compras; quantidade/contexto real do recebimento parcial no DTO e documento; testes backend concorrente e misto e E2E do comprovante misto. Removido texto oculto duplicado do seletor e corrigido comentário MySQL indevido no script PostgreSQL, sem execução. Toda implementação válida anterior foi preservada.

O trace do primeiro E2E integral mostrou POST de login sem resposta (status -1), com tela ainda em “Entrando…”, sem mensagem retornada. Isso evidencia timeout de requisição; não estabelece uma causa única. A nova execução usa H2 novo e mantém assertions/timeouts anteriores, workers=1 e retries=0. Não houve alteração do fluxo de autenticação.

## Os 67 pontos da retomada

| Nº | Ponto | Resultado |
|---|---|---|
| 1 | Branch | feature/bes-frontend. |
| 2 | HEAD | 354252ab9e3d3ad60cf6ebb692c56b880cff70a5, preservado. |
| 3 | Arquivos iniciais | 76 candidatos: 48 modificados + 28 novos, unstaged. |
| 4 | Ponto de interrupção | Fechamento dos checks/relatório; backend 382 e Node 86 verdes, Playwright integral 57/58. |
| 5 | Já pronto | Cadastros, cadeia contextual, segurança, snapshots, resumos, filtros e frontend; matriz A–AG acima. |
| 6 | Parcial | Concorrência do lote manual, documento de recebimento parcial, relatório e E2E integral. |
| 7 | Faltava | Fechar revisão adversarial, validações integrais e evidências; sem reiniciar implementação. |
| 8 | Continuação | Ordenação global dos locks contextuais, contexto real de cada parcela recebida e novos testes; evidências finais abaixo. |
| 9 | Obra | Cadastro/status/datas/ator/unique/versão; sem delete. |
| 10 | Centro de Custo | Tipos OBRA/ADMINISTRATIVO/OPERACIONAL/OUTRO, ativo e Obra opcional imutável. |
| 11 | OS | Obra obrigatória, CC coerente imutável, número por IDENTITY/unique, responsável, datas/status servidor. |
| 12 | Coerência | ContextoService deriva referências e rejeita vínculos contraditórios/inativos/encerrados. |
| 13 | Histórico | Snapshot de IDs/nomes/códigos na origem e saída; nenhuma reclassificação retroativa. |
| 14 | Solicitações | Contexto opcional nullable, backend valida/congela; API anterior compatível. |
| 15 | Atendimento | Entrega humana mantém protocolo; só confirmação gera SAÍDA contextual. |
| 16 | Movimentações | Filtros por snapshot; ENTRADA geral não inventa consumo/Obra. |
| 17 | Necessidades | Origem deriva da solicitação imutável. |
| 18 | Pedidos | Contexto por alocação e por quantidade manual sem alocação; sem Obra global. |
| 19 | Multiobra | Mesmo produto/mesmo item em pedido para duas Obras; origem individual e filtros na mesma alocação. |
| 20 | Compra geral | Contexto null válido; paraEstoque preservado. |
| 21 | Recebimento | Físico/atômico/idempotente; contexto manual apenas sobre sobra realmente recebida, não sobre parcela futura. |
| 22 | Comprado × recebido × consumido | Compromisso comercial, entrada física e saída quantitativa são fatos separados. |
| 23 | Base analítica | IDs/dimensões/fatos granularizados; não duplica valor integral de pedido por Obra. |
| 24 | Power BI | Preparação documentada; nenhuma integração/DW/ETL implementada. |
| 25 | Resumos | Cadastros, contagens, relações limitadas a 100, material solicitado/atendido/consumido e custo null. |
| 26 | Filtros | Obra/OS/CC em solicitações, necessidades, pedidos e movimentações; cadastros paginados. |
| 27 | Endpoints | 17 novos, 8 escritas; total 102/44, inventário em api.md. |
| 28 | Authorities | Seis: leitura/gestão de Obra, OS e CC. |
| 29 | Perfis | ADMIN/GESTOR gestão; ALMOXARIFE/CONSULTA leitura; sem gestão adicional de usuários. |
| 30 | Service layer | PreAuthorize nas operações de estrutura/resumo/contexto e integrações anteriores preservadas. |
| 31 | CSRF | Exigido nas escritas; ausência testada com rejeição. |
| 32 | Auditoria | Ator autenticado, responsável separado e mesma transação; rollback sem falso sucesso. |
| 33 | IDOR | IDs não conferem authority; política de leitura global da baseline mantida, sem isolamento por Obra/usuário. |
| 34 | Mass assignment | DTOs estritos; ator/status interno/versão/número/datas servidor rejeitados. |
| 35 | Concorrência | Unique/IDENTITY/PESSIMISTIC_WRITE/versão; lote Obra→CC→OS por IDs ordenados; testes concorrentes. |
| 36 | N+1 | Projeções e paginação/EntityGraphs; testes constantes com 12 OS e baseline compras preservada. |
| 37 | Frontend | Três cadastros, seletor reutilizável, filtros/status/resumos; design atual preservado. |
| 38 | Sidebar | Grupo ESTRUTURA evoluído, sem lista genérica de módulos futuros. |
| 39 | Dashboard | Somente Obras ATIVA e OS ABERTA, compactos. |
| 40 | Documentos | Ficha OS A4, lista de separação e compra/recebimento com contexto real por origem/quantidade. |
| 41 | Logos | Assets oficiais ausentes; fallback B&S Engenharia/Plataforma BES, nenhuma logo inventada. |
| 42 | QR | Não implementado; RF212 permanece pendente. |
| 43 | SQL | MySQL/PostgreSQL manuais alinhados às entidades, FKs/unique/índices/atores e nullable; não executados. |
| 44 | Compatibilidade | 328 backend/72 Node/47 E2E anteriores preservados; nenhum backfill falso. |
| 45 | Backend total | 385 = 328 anteriores + 57 novos; zero falhas/erros/skips; inclui teste do tipo físico VARCHAR e CHECK. |
| 46 | Frontend total | 86 = 72 + 14, zero falhas. |
| 47 | Playwright total | 60 = 47 anteriores + 13 novos, zero falhas, 5,0 minutos, exit 0. |
| 48 | Build | Aprovado na retomada. |
| 49 | npm audit | Zero em todas as severidades; sem dependências novas. |
| 50 | Secret scan | Zero achados nos 76 candidatos; nenhum segredo histórico presente. |
| 51 | Diff check | git diff --check limpo. |
| 52 | Git status | 76 arquivos candidatos (28 novos/48 modificados), todos unstaged, staging vazio; checkpoint preservado. |
| 53 | Arquivos novos | 28, inventário nominal acima. |
| 54 | Arquivos modificados | 48, inventário nominal acima. |
| 55 | Falhas | Achados originais no item 83; nesta retomada locks em ordem dos itens e contexto prematuro de recebimento; timeout antigo de login e rejeição de domínio no H2 do navegador. |
| 56 | Correções | Lote determinístico e DTO/documento quantitativo de recebimento, cobertos por novos testes; VARCHAR explícito e teste do tipo físico/constraint; autenticação/assertions anteriores preservadas. |
| 57 | Riscos | Sem valorização/escopo por Obra; limite de resumos; drivers/schema reais não homologados e gate produção pendente. |
| 58 | RFs atendidos | RF061/203 no fluxo atual; baseline RF056/057; RF207/208/209/216/217/218 nos documentos presentes. |
| 59 | RFs parciais | RF010/033/055/063/065–069/071/003/038/053/148/150/205/206/210/211/213–215: limites na matriz funcional. |
| 60 | RFs não atendidos | RF062/064/212 e módulos futuros listados; preparação não encerra requisitos. |
| 61 | Sem MySQL | Nenhum acesso; somente H2. |
| 62 | Sem produção | Nenhum acesso/publicação. |
| 63 | Main intacta | 0137ddbddf403f8331d1a3f6d632428f7614ee15, não alterada. |
| 64 | Sem commit | Nenhum commit; checkpoint preservado. |
| 65 | Sem push | Nenhum push. |
| 66 | Sem merge | Nenhum merge/rebase/reset/restore/clean/stash/rewrite. |
| 67 | Auditoria pré-commit | SIM: pronto para auditoria pré-commit; não autoriza commit/produção. |

### Ocorrências ambientais na validação da retomada

Na primeira repetição integral, o primeiro teste anterior de dashboard encontrou a tela ainda em carregamento no limite de 5s; as consultas do trace retornaram HTTP 200. Mais tarde, uma chamada de inspeção e o teste anterior de fornecedor ficaram parados por cerca de 81 minutos (o runner registrou 1,4h), indicando pausa prolongada do ambiente de execução. Após retomada, a medição local encontrou aproximadamente 158 MB de RAM física livre em 7,9 GB. Não foram encerrados processos do usuário/IDE nem ampliados timeouts/assertions/retries. Esses resultados intermediários não certificam a suíte; o resultado final será registrado após nova execução integral em H2 local, com consultas previamente inicializadas.

A mesma execução intermediária encontrou rejeição 409/SQLState 23514 no cadastro válido de Obra e CC no H2 do navegador. O DDL inspecionado mostrou ENUM nativo com CHECK textual, diferente do VARCHAR dos scripts manuais. O mapeamento foi explicitado como VARCHAR nos três domínios (status de Obra/OS e tipo de CC), conservando CHECK/validação de servidor. Novo teste confronta o tipo físico H2 com os scripts e prova que status de outro domínio continua rejeitado com rollback. Uma reprodução SQL mínima isolada com ENUM/CHECK aceitou a inserção, portanto não se atribui o incidente a uma causa interna específica do driver apenas pela mensagem. O fechamento exige nova suíte backend e E2E completas sobre o mapeamento final.

A execução intermediária da retomada terminou com 47 aprovados de 59: dois testes anteriores afetados por carregamento/pausa e dez novos afetados pela rejeição de domínio no H2. O cadastro foi reproduzido na inicialização final após explicitar VARCHAR: Obra, CC e OS retornaram HTTP 200; login inválido retornou 401. A inicialização usou apenas fixtures H2 e consultas locais, cookies/CSRF em memória; não alterou configuração de testes ou arquivos de sessão. O schema diagnóstico e scripts auxiliares ficaram somente em TEMP, sem endpoint diagnóstico público nem conexão externa.

## Fechamento da retomada

Todos os grupos A–AF ficaram IMPLEMENTADOS e validados dentro do escopo incremental; AG é preparação documental/modelo implementada, com Power BI real explicitamente NÃO IMPLEMENTADO por escopo. A matriz inicial acima registra o ponto de partida e não é o estado pendente final.

- Backend final: `.\mvnw.cmd clean test`, 385 = 328 anteriores + 57 novos, zero falhas/erros/skips, BUILD SUCCESS (XML surefire conferido).
- Frontend final: `npm.cmd run build` aprovado; `npm.cmd test`, 86 = 72 + 14, zero falhas; `npm.cmd audit`, zero em todas as severidades.
- Navegador final: `npm.cmd run test:e2e`, H2 novo + vite preview com consultas inicializadas, 60 = 47 + 13, todos aprovados em 5,0 minutos, exit 0. workers=1/retries=0 e assertions/timeouts anteriores preservados.
- Scan heurístico dos 76 candidatos e comparação silenciosa com o segredo histórico: zero achados; nenhuma ocorrência de credencial histórica; sem caractere inválido/espaço final; staging vazio. Scan não é certificação de ausência de CVEs de backend.
- `git diff --check` limpo. Branch/HEAD/main preservados; 28 novos e 48 modificados, sem staging/commit/push/merge. Inventário nominal acima inclui todo o trabalho preservado e concluído.
- Captura final de formulário mobile (390px) inspecionada; testes responsivos 1440/768/390 e A4/print/PDF navegador aprovados. Artefatos/logs auxiliares somente ignorados/TEMP.
- Pronto para auditoria pré-commit do Bloco 5. Gate de produção permanece pendente e separado; scripts MySQL/PostgreSQL não executados, nenhum banco externo/produção acessado.

Evidências locais em TEMP: `bes-bloco5-resume-clean-test3.log`, `bes-bloco5-resume-node3.log`, `bes-bloco5-resume-build3.log`, `bes-bloco5-resume-audit3.json`, `bes-bloco5-resume-playwright3.log` e `bes-bloco5-resume-scan.json`. Falhas intermediárias e correções estão registradas acima, sem substituir o resultado final ou retirar testes anteriores.

Revisão textual final corrigiu a codificação de acentos na documentação e em três mensagens de referência inexistente do resolver de lote. O backend completo foi repetido após essa correção textual, sem mudar regras/rotas/DTOs. H2 e vite preview próprios foram encerrados ao terminar os testes, preservando processos do usuário/IDE.

Último achado de filtro: CC corporativo era omitido ao escolher Obra no seletor histórico, apesar de válido no backend. O seletor passou a incluir centros gerais tanto em filtros quanto em novas operações; E2E específico confirma a solicitação Obra + CC corporativo. Após essa mudança, build/86 Node/audit zero e suíte integral de 60 E2E (47 anteriores + 13 novos) passaram. Não houve novas mudanças funcionais depois dessa validação.

## Auditoria pré-commit encerrada — 07/10/2026

Ver [auditoria com os 80 pontos](bloco5-auditoria-pre-commit.md). Estado atual preservado: feature/bes-frontend, HEAD 354252ab9e3d3ad60cf6ebb692c56b880cff70a5, 29 novos/48 modificados e staging vazio. Acrescentada cobertura de Obra/OS suspensa/concluída/cancelada e CC corporativo/filtros/resumos: aumento líquido de seis testes, sem nova mudança funcional.

Resultados atuais substituem somente os totais anteriores: 396 backend (328 anteriores + 68 Obras), zero falhas/erros/skips; 86 frontend; build aprovado; npm audit zero; 62 Playwright (47 anteriores + 15 B5) aprovados em 3,7 minutos e .last-run.json passed, sem falhas. O wrapper PowerShell sinalizou warning Node de cores como NativeCommandError; detalhes e falhas intermediárias de infraestrutura/orquestração registrados na auditoria, sem relaxar testes ou segurança.

Secret scan de todos os 77 candidatos sem achado de segredo real ou ocorrência da credencial histórica; comparação silenciosa. Main intacta; sem banco real, produção, SQL manual, commit, push ou merge. Classificações RF mantidas e gate de produção pendente. Recomendação da auditoria: COMMITAR; nenhum commit realizado.
