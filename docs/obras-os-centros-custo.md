# Bloco 5 — Obras, Ordens de Serviço e Centros de Custo

Referência oficial: [Documentação Mestre BES v1.4](Documentacao_Mestre_Plataforma_BES_v1_4.docx), RF001–RF218. Incremento autorizado sobre `354252ab9e3d3ad60cf6ebb692c56b880cff70a5`, em `feature/bes-frontend`. Preserva o monólito modular, a Security Baseline 2 e os fluxos de estoque/compras existentes.

## Escopo incremental e dependências

Entrega cadastros, transições explícitas, contexto validado e histórico na cadeia Obra → OS → CC → Solicitação → Necessidade → Alocação do pedido → Recebimento → estoque → atendimento humano/SAÍDA. Compra geral e registros antigos continuam sem contexto. Obra é obrigatória apenas no cadastro da OS e em CC do tipo OBRA. Não existe obra fictícia para preencher dados desconhecidos.

Cliente é identificação textual opcional na Obra: não existe cadastro adequado de cliente e Fornecedor não representa cliente. Não foi criado CRM. Responsável operacional usa Funcionario existente; ator autenticado usa Usuario e não vem do corpo.

Não entrega orçamento completo, contabilidade, Gantt, contratos, financeiro, NF/XML/IA, Power BI, DW, ferramentas/EPI/logística/manutenção. Suas dependências de contexto ficam preparadas, sem declarar os módulos realizados.

## Modelo e regras

- `bes_obra`: ID INT, código ASCII normalizado por strip/uppercase e único global, nome, descrição, cliente, localidade, observações, responsável opcional, início e término previsto, término real exclusivamente do servidor, status, timestamps, atores e versão otimista.
- `bes_centro_custo`: ID INT, código normalizado único global, nome, descrição, tipo OBRA/ADMINISTRATIVO/OPERACIONAL/OUTRO, ativo e Obra opcional. Tipo OBRA exige vínculo. Ativo/inativo mantém histórico; não há exclusão física.
- `bes_ordem_servico`: ID INT, número único `OS-ano-ID`, Obra obrigatória, CC opcional definido explicitamente, título, descrição, observações, prioridade opcional BAIXA/NORMAL/ALTA/URGENTE, responsável opcional, abertura/início/conclusão do servidor, motivo de interrupção, status, atores e versão. Geração usa IDENTITY e unique, sem MAX+1. Status e tipo persistem como VARCHAR, com CHECK de domínio, alinhados aos dois scripts manuais; não dependem de ENUM nativo do driver.

OS possui uma Obra; Obra possui várias OS. CC pode pertencer à Obra ou ser corporativo. OS aceita CC corporativo ou da própria Obra. Não há CC padrão da Obra: o default é o CC explícito da OS. Selecionar OS na solicitação deriva Obra/CC no backend. CC explicitamente diferente do default da OS é recusado; CC de outra Obra também. Selecionar apenas CC de Obra deriva a Obra. IDs inexistentes produzem 404.

Vínculo de Obra do CC e vínculos Obra/CC da OS são imutáveis desde a criação, mesmo antes de uso. Para reorganizar a estrutura, criar outro cadastro e encerrar/inativar o anterior. É regra deliberadamente conservadora: nenhuma edição pode reclassificar operações antigas. Código/nome descritivos podem mudar em cadastros abertos, enquanto snapshots históricos mantêm identificação da origem.

## Transições

Obra: PLANEJADA → ATIVA ou CANCELADA; ATIVA → SUSPENSA/CONCLUIDA/CANCELADA; SUSPENSA → ATIVA/CANCELADA. Estados terminais não reabrem nem permitem edição. Concluir/cancelar exige ausência de OS abertas e solicitações não atendidas/rejeitadas. Término real é registrado pelo servidor.

OS: ABERTA → EM_ANDAMENTO/CANCELADA; EM_ANDAMENTO → SUSPENSA/CONCLUIDA/CANCELADA; SUSPENSA → EM_ANDAMENTO/CANCELADA. Suspender/cancelar exige motivo. Iniciar/retomar exige Obra PLANEJADA/ATIVA e CC ativo, quando presente. Concluir/cancelar exige solicitações resolvidas. Terminal não reabre, não se edita e não repete evento. Suspensão guarda motivo atual; trilha registra as transições, sem um diário completo de causas/versionamento de justificativas.

Novas solicitações e novos vínculos manuais de compra aceitam Obra PLANEJADA/ATIVA, OS ABERTA/EM_ANDAMENTO e CC ativo. Leitura histórica permanece disponível nos demais estados. Compromissos já existentes não são revalidados para bloquear recebimento/atendimento: suspensão/inativação não deve impedir concluir fisicamente uma demanda já autorizada. Encerramento com demanda pendente é bloqueado.

## Contexto e imutabilidade histórica

`ContextoOperacional` é um embeddable com três IDs nullable e snapshots de código/nome da Obra, número da OS e código/nome do CC. `ContextoService` centraliza coerência e situação admissível. Os FKs ficam nos donos do snapshot, com referências JPA privadas/read-only/LAZY, fora do embeddable, sem serialização de entidades internas.

Na criação da solicitação, o backend resolve e congela o contexto. Não há endpoint para mudar o contexto da demanda. Necessidade deriva o contexto dessa solicitação imutável, sem copiar uma segunda classificação que possa divergir. Alocação conserva FK para necessidade; pedido pode incluir várias obras e várias necessidades do mesmo produto no mesmo item.

No atendimento confirmado, SAÍDA recebe snapshot do contexto da solicitação. Snapshot não é refeito a partir do cadastro atualizado. Entradas manuais, transferências internas, ajustes anteriores e movimentos legados permanecem sem contexto quando desconhecido; não há backfill.

Em compra manual, `itens[].contexto` classifica somente a quantidade sem alocação (`quantidadeEstoque`). Sem essa quantidade, contexto manual é rejeitado. Alocações nunca recebem contexto escolhido pelo cliente: deriva-se da necessidade. Um item pode combinar várias origens e uma quantidade adicional para estoque geral/contextualizado. O Pedido não possui Obra global.

Recebimento conserva cada destinação; `quantidadeEstoque` recebida é a quantidade do recebimento menos suas destinações. A classificação manual do item só aparece nesse recebimento quando essa sobra efetivamente recebida é positiva, sem antecipar contexto de parcelas futuras; movimento de ENTRADA não recebe uma Obra global nem vira consumo. Rastrear ENTRADA por pedido/recebimento e suas destinações; filtros de contexto em `/movimentacoes` selecionam snapshots das SAÍDAS confirmadas. Não significa que uma entrada tenha atendido o solicitante.

## Quantidade e valores

Valor comprado, recebido e consumido são conceitos diferentes. Preço/subtotal/total continuam strings decimais calculadas por BigDecimal conforme Bloco 4. Valor do pedido é compromisso comercial; quantidade recebida acompanha conferência física. Nenhum desses números é custo consumido automaticamente.

Consumo quantitativo por contexto soma somente SAÍDA com snapshot e sem transferência. Não soma ENTRADA, reserva, necessidade ou aprovação. Quantidades são agrupadas por produto e sua unidade, nunca somadas entre produtos/unidades incompatíveis. `custoConsumido` é null, acompanhado de aviso, pois custo médio/FIFO/lote/custo de aquisição apropriado ainda não existe. Não há motor contábil, margem nem orçamento × realizado financeiro.

## Consultas e desempenho

Listagens dos cadastros são paginadas (1–100, padrão 20), ID desc, envelope `ComprasPage` estável. Filtros de texto/estado/Obra/CC são executados no banco. CC admite `incluirGerais=true` para disponibilizar centros corporativos ao seletor sem perder centros da Obra, tanto em novas operações quanto nos filtros históricos.

Solicitações e necessidades mantêm contrato de lista anterior e acrescentam filtros AND `obraId`, `ordemServicoId`, `centroCustoId`. Necessidades filtram antes de carregar progresso em lotes; EntityGraph específico da consulta contextual carrega referências do DTO sem uma consulta extra por demanda. Pedidos usam joins com contexto manual ou da MESMA alocação, DISTINCT no agregado e paginação anterior; combinar Obra A com OS da Obra B não faz corresponder duas alocações diferentes do mesmo pedido. Movimentos acrescentam filtros dos snapshots, com EntityGraph de suas referências de leitura.

Resumos de Obra/OS/CC retornam cadastro, contagens totais, OS abertas, relações de OS/solicitações/necessidades/pedidos, materiais solicitados/atendidos e consumo quantitativo. Usam projeções escalares e agregações, sem carregar todo o histórico como grafo JPA. Cada lista de relações e produtos é limitada a 100, informa `limiteRelacoes`; contagens totais continuam disponíveis. Consultar os endpoints filtrados/paginados para continuar. Não são exportação completa de BI nem arquivo histórico ilimitado.

Nomes de produto/unidade nos agregados representam o catálogo atual; snapshots de compra e contexto continuam históricos. Mudanças de unidade do catálogo exigem governança anterior; não foi implementado versionamento dimensional. Período/setor/categoria já existentes poderão integrar consultas posteriores, sem declarar um datamart pronto.

## Concorrência e transação

Estrutura/contexto bloqueiam Obra → CC → OS, recarregando estado sob PESSIMISTIC_WRITE. Os vínculos estruturais imutáveis permitem descobrir IDs antes do lock sem possibilidade de mudança concorrente. Encerramento da Obra/OS e nova origem contextualizada compartilham o lock da Obra. Versão otimista e unique de códigos/número complementam o protocolo. Unicidade concorrente tem um vencedor; transição simultânea não repete evento.

Compra continua pedido → necessidades por ID → fornecedor → produtos por ID → estoque. Resolução do contexto manual ocorre antes dos locks de necessidade/produto; todo o lote bloqueia primeiro todas as Obras por ID, depois todos os CC por ID e todas as OS por ID, independentemente da ordem dos itens; pode seguir lock de pedido existente na edição. Writers de estrutura não bloqueiam pedidos, solicitações ou estoques, evitando ciclo de locks. Recebimento nunca adquire lock de solicitação ou estrutura; atendimento não bloqueia necessidade. Não houve alteração desses protocolos existentes.

Criação/edição/status e auditoria de sucesso são uma única transação. Falha da auditoria reverte cadastro/status. Não existe exclusão destrutiva, cascata sobre estoque, realocação retrospectiva ou estorno novo.

## Segurança

Seis authorities: OBRA_LER/GERENCIAR, ORDEM_SERVICO_LER/GERENCIAR, CENTRO_CUSTO_LER/GERENCIAR. ADMIN/GESTOR gerenciam; ALMOXARIFE/CONSULTA leem. Não concede gestão de usuários. Todas as 17 rotas novas são permissionadas no HTTP e no service; 8 escritas exigem CSRF. Inventário total 102 handlers / 44 escritas. Sessão/CSRF/CORS existentes permanecem.

Entradas são DTOs estritos; não aceitam id, versão, ator, número de OS, status interno, término real ou conclusão. Transição usa DTO exclusivo de status/motivo, com regra de servidor. Ator é o SecurityContext validado; responsável operacional permanece separado. Eventos OBRA_CRIADA/ALTERADA/STATUS_ALTERADO, CENTRO_CUSTO_CRIADO/ALTERADO/INATIVADO e ORDEM_SERVICO_CRIADA/ALTERADA/INICIADA/SUSPENSA/CONCLUIDA/CANCELADA têm referência exata e estados seguros. Auditoria de demanda contextual também inclui os três IDs pela allowlist, sem serializar corpo/grafo.

IDs são referências, não autoridades. Trocar ID não elimina controle por operação. Leitura continua global para os quatro perfis, como a baseline: não há multitenancy, restrição por usuário/Obra ou política de dados por cliente. Implantação que exija esse isolamento depende de rodada própria.

## Frontend e documentos

ESTRUTURA inclui Obras, OS e CC ao lado de almoxarifados/funcionários. Retirado apenas o placeholder Obras/OS de futuros módulos. Pesquisa, paginação, formulários, detalhes e status são permissionados. Seletor reutilizável permite pesquisa de Obra/OS/CC, filtra coerência, sugere CC e distingue operação geral. Pesquisas e paginação conservam a opção selecionada visível por consulta autenticada do ID; trocar Obra limpa OS/CC anteriores. Backend valida tudo. Formulário bloqueia envio enquanto ocupado; resultado incerto na criação exige conferir listagem.

Dashboard acrescenta dois indicadores compactos: Obras ATIVA e OS ABERTA (não soma EM_ANDAMENTO sob o rótulo ABERTA). Nenhuma reformulação visual. Histórico pode ser filtrado também por contexto encerrado/inativo. Seletores de novas operações limitam situações admissíveis; listas de seleção têm limite 100 e pesquisa explícita para refinar.

Ficha da OS em `/ordens-servico/:id/documento` usa OperationalDocument/Brand, metadados reais, título/descrição, relações quantitativas e aviso de custo. A4, impressão/PDF pelo navegador, sem sidebar/ações em print. Lista de Separação, Pedido e Recebimento exibem contexto apenas na origem real; compra multiobra mostra contexto por alocação.

PNGs oficiais continuam ausentes nos caminhos preparados. Fallback B&S Engenharia / Plataforma BES, sem logo inventada. QR não implementado; assinatura legal, exportador PDF backend, anexos e versionamento documental permanecem pendentes. Fluxo web/mobile não exige impressão. Rotas de documento exigem permissões de leitura e só usam API autenticada.

## Preparação para BI / Power BI

Dimensões preparadas: Obra, OS, CC, produto/categoria/unidade do catálogo, almoxarifado, funcionário/responsável/ator, fornecedor, período, solicitação e pedido. Fatos operacionais: itens solicitados/quantidade atendida, necessidade/quantidade recebida, alocação de compra, item do pedido/preço, item recebido/destinação e movimento/saldo anterior/posterior.

Indicadores confiáveis dentro do modelo atual: demandas por contexto, materiais solicitados/atendidos conhecidos, consumo quantitativo confirmado por snapshot, faltas, pedidos relacionados e quantidade destinada recebida. Preservar granularidade do item/alocação e separar estado de compra/atendimento. Um pedido multiobra pode ser relacionado a várias obras; contar seu total monetário integral em cada obra produziria dupla contagem. Filtrar pedido não redistribui automaticamente seu total.

Não confiáveis: custo contábil consumido, custo médio, margem, orçamento × realizado completo, apropriação de outros módulos, produtividade global e custos antigos sem contexto. Agregações não inventam dimensão para NULL. Quantidade de material em uma unidade não é custo, nem se soma a unidades distintas.

Evolução futura: política aprovada de valorização de estoque, granularidade de lotes/custos, datas de vigência/dimensões, rateios explícitos, orçamento previsto, fatos de logística/manutenção/ferramentas e APIs/extrações autenticadas com escopo de dados. Nenhum Power BI Embedded, ETL, DW, cubo ou endpoint público foi implementado.

## Banco, legado e produção

Scripts [MySQL](sql/obras-os-centros-custo-mysql-manual.sql) e [PostgreSQL](sql/obras-os-centros-custo-postgresql-manual.sql) preparados, não executados. Criam as três tabelas, FKs/uniques/checks/índices, snapshots nullable em solicitação/movimentação/item de pedido; não modificam legado nem tabelas de auditoria. Necessidade/alocação reutilizam as FKs existentes. Não houve conexão externa. H2 create-drop é somente de teste.

Produção continua bloqueada por rotação externa da credencial histórica, HTTPS/configuração segura, schema homologado, backup/restauração, observabilidade, sessão/rate limit reais e processo de deployment. Código testado em H2 não certifica MySQL/PostgreSQL real. Nenhuma autorização de commit, push, merge, main, publicação ou banco real neste bloco.

## Cobertura RF após releitura da v1.4

| RF | Estado | Evidência / limite |
|---|---|---|
| RF061 | ATENDIDO no cadastro operacional deste bloco | Obra/OS, cliente identificado, datas, responsável, estado e CC; não inclui orçamento |
| RF062, RF064 | NÃO ATENDIDO | orçamento e previsto × realizado financeiro ausentes |
| RF010, RF033, RF055 | PARCIALMENTE ATENDIDO no produto | SAÍDA/contexto e filtros; inventário de tipos/setores/períodos não é todo implementado |
| RF063 | PARCIALMENTE ATENDIDO | consumo quantitativo por OS; sem valorização/apropriação financeira |
| RF065–RF066 | PARCIALMENTE ATENDIDO | relações e datas de operação/Obra; recursos, ocorrências e indicadores de prazo completos futuros |
| RF067 | PARCIALMENTE ATENDIDO | motivo de suspensão/cancelamento de OS; sem catálogo de causas/diário completo |
| RF068–RF069 | PARCIALMENTE ATENDIDO | base histórica e painel quantitativo; orçamento comparável, custos/equipe/ferramentas não entregues |
| RF071 | PARCIALMENTE ATENDIDO | Obra/OS/CC, solicitante e almoxarifado; área/local de entrega distinto futuros |
| RF034–RF037, RF078–RF082, RF193–RF202, RF204 | COBERTURA ANTERIOR PRESERVADA | limites documentados dos blocos 3/4 e catálogo; não ampliados por preparação |
| RF203 | ATENDIDO no fluxo de materiais deste bloco | falta → demanda/produto/contexto → alocação/recebimento; multiobra real |
| RF003, RF038, RF053, RF148, RF150 | PARCIALMENTE ATENDIDO no produto | matriz, dois indicadores, audit/transação dos módulos presentes; módulos futuros pendentes |
| RF056–RF057 | ATENDIDO na baseline dos módulos presentes | novos endpoints HTTP/service/CSRF integrados sem alterar autenticação |
| RF117, RF123–RF128, RF139, RF158–RF180, RF181–RF192 | NÃO ATENDIDO por este incremento | dependências futuras; não se confunde contexto com implementação desses módulos |
| RF205 | PARCIALMENTE ATENDIDO | ficha OS e documentos existentes; cobertura de todos módulos futura |
| RF206 | PARCIALMENTE ATENDIDO | template e fallback; logos oficiais ausentes |
| RF207–RF208 | ATENDIDO nos documentos deste fluxo | identificador, status, emissão, responsáveis e contexto real |
| RF209 | ATENDIDO no fluxo de lista existente | preservada com contexto, sem confirmar entrega |
| RF210–RF211 | PARCIALMENTE ATENDIDO | recebimento e representação de faltas/origens; retirada/entrega e documento independente de necessidade futuros |
| RF212 | NÃO ATENDIDO | QR ausente |
| RF213–RF215 | PARCIALMENTE ATENDIDO | A4/PDF navegador, mobile e confirmação; sem motor PDF/PWA/assinatura qualificada |
| RF216 | ATENDIDO no template atual | componentes reutilizados, sem gerador PDF backend |
| RF217–RF218 | ATENDIDO neste módulo | ficha útil e ação contextual na OS; não declara todos módulos futuros concluídos |

Evidências e inventário final: [relatório Bloco 5](bloco5-relatorio-final.md). A classificação é incremental e não declara RF001–RF218 concluídos.

Auditoria final independente e regressões adicionais: [pré-commit do Bloco 5](bloco5-auditoria-pre-commit.md).
