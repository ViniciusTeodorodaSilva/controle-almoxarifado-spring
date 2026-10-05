# Bloco 2 — estoque inteligente e transferências

## Estado e escopo

Continuação na `feature/bes-frontend`, a partir de `d704806`, preservando Catálogo Mestre (`263658d`), contratos e design aprovados. Monólito modular Spring Boot/JPA, React/Tailwind/Vite, H2 isolado para testes. Não há commit, push, alteração da main, mudança de credenciais ou acesso ao MySQL nesta rodada.

O anexo do Bloco 2 definiu o escopo implementado. Durante sua implementação, apenas a v1.3 estava disponível; a cobertura abaixo conserva esse registro histórico. Desde 04/10/2026, a [Documentação Mestre BES v1.4](Documentacao_Mestre_Plataforma_BES_v1_4.docx) é a fonte oficial principal, abrangendo RF001–RF218. Novos blocos e revisões de cobertura devem consultá-la conforme [docs/README.md](README.md), preservando os requisitos e roadmap anteriores sem assumir implementação automática dos RFs adicionais.

## Limites e reposição

`Estoque.estoqueMinimo` e `estoqueMaximo` são Double anuláveis, por produto+almoxarifado. Ausência de configuração conserva comportamento legado. Valores finitos >= 0; quando ambos presentes, máximo >= mínimo. PUT substitui os dois limites, sob lock de estoque, e nunca muda saldo nem cria movimentação. Cadastro legado também valida os novos campos opcionais.

Alerta: mínimo configurado e saldo <= mínimo. Sugestão: máximo configurado e alerta ativo, `max(0, máximo - saldo)`; caso contrário null. Consulta `/reposicoes` contém somente sugestões positivas, enquanto `/alertas` conserva alerta sem máximo ou sem quantidade positiva sugerida. Não há compra automática, reserva, débito ou outra gravação nas consultas.

Máximo não é capacidade física: não bloqueia entrada ou transferência acima dele. Limites podem ser fracionários mesmo em unidade inteira, pois representam níveis de alerta. Quantidades efetivamente movimentadas respeitam a unidade configurada. Valor zerado sem mínimo configurado recebe badge “Zerado” na UI, mas não entra no alerta do backend. Normal significa saldo positivo fora da condição de mínimo, não avaliação de capacidade máxima.

## Domínio e API

- `TransferenciaEstoque`: ID, origem, destino, funcionário responsável, horário, status CONCLUIDA, observação <= 1000 caracteres, itens.
- `ItemTransferencia`: FK transferência/produto e quantidade; UNIQUE transferência+produto. Vínculo bidirecional JPA com cascade PERSIST, sem cascata de exclusão.
- `Movimentacao.transferencia`: opcional/LAZY, grafo ignorado no JSON; getter transferenciaId conserva rastreabilidade nos contratos legados. Movimentos antigos permanecem com null.
- DTOs específicos para limites/alertas, input/resposta de transferência e histórico; controllers não aceitam saldo/status/entidades arbitrárias como input novo.
- Repositories de transferência/itens e extensões de estoque/movimentação. Services separados de estoque inteligente e transferência mantêm controllers pequenos e uma fronteira clara para futura autorização.
- Sete rotas novas, documentadas em [api.md](api.md), sem PUT/DELETE físico de transferência. Filtros parentais inexistentes são 404; existentes sem correspondências são `[]`.

Transferência representa execução imediatamente confirmada, não logística em trânsito. Não há edição, cancelamento, estorno ou retorno automático. Operação inversa é outra transferência independente; não existe regra inventada de cancelamento. Funcionário responsável é uma referência operacional fornecida pelo cliente. A Security Baseline 2 registra separadamente a identidade autenticada do ator; o funcionário operacional não é automaticamente o usuário logado.

## Transação, locks e integridade

Uma transação engloba transferência, itens, criação eventual de destino, débito, crédito e movimentos. Entrada inválida, referência ausente, insuficiência ou falha de persistência provoca rollback completo. Teste injeta falha após a saída e verifica que nem saldo, vínculo, destino criado ou movimentos permanecem.

Ordem de locks:

1. Todos os produtos por ID crescente, protegendo ativo/unidade e criação de pares ausentes com o mesmo protocolo usado no cadastro existente.
2. Estoques por `(produtoId, almoxarifadoId)` crescente, independentemente da direção da transferência.
3. Validar todos os saldos antes de debitar/creditar.
4. Persistir confirmação e gerar SAIDA/ENTRADA por item, com horário/responsável comuns e saldos anterior/posterior.

Origem e destino devem existir e diferir. Origem exige estoque; destino ausente é criado zerado dentro da transação. Produto ativo, 1–500 produtos distintos, quantidades positivas/finitas e fracionamento conforme unidade. UNIQUE estoque+almoxarifado já existente e UNIQUE item+produto complementam os locks. Locks de produto podem serializar transferências de um mesmo material mesmo em almoxarifados distintos: opção conservadora para integridade, a otimizar após medição.

Testes concorrentes H2 verificam duas transferências contra o mesmo saldo, duas direções inversas e transferência concorrendo com saída manual. Não há repetição automática de gravações em conflitos. Esses testes não substituem validação de isolamento/locks no banco de produção em ambiente homologado; nenhum banco externo foi usado aqui.

`double` permanece por compatibilidade. São rejeitados NaN/Infinity, overflow de destino e quantidades que desaparecem na soma/subtração por precisão. Não se usa epsilon para permitir débito acima do saldo. Ainda existe imprecisão binária em frações: manter a migração coordenada futura BigDecimal/DECIMAL descrita em [catalogo-mestre.md](catalogo-mestre.md), com unidade, escala, API e dados legados alinhados. Horário de transferência é LocalDateTime truncado a microssegundos para corresponder ao armazenamento; fuso/Instant são evolução coordenada futura.

## Frontend e revisão visual

Estoque mantém busca por código/nome/descrição e filtros produto/almoxarifado, acrescentando mínimo/máximo, badge, configurar limites e filtro “Precisam de atenção”. Visão de atenção usa a API real, com estado de loading/erro e reposição sugerida. Atualizar e gravar limites/movimentos recarrega estoques e alertas.

Dashboard preserva indicadores/composição e acrescenta seção compacta de estoque baixo com dados reais e acesso direto à atenção. Transferências usa sidebar/tabelas/formulários existentes; pesquisa por número/material/local/responsável e filtros server-side de origem/destino/produto. Filtrar produto mantém todos os itens de cada transferência.

Nova transferência permite catálogo pesquisável, vários materiais, saldo da origem, quantidade/unidade e remoção de itens no rascunho. Trocar origem limpa itens, avisando junto ao campo. Antes de confirmar, saldos são consultados novamente. Backend continua sendo autoridade final. Sucesso abre detalhes com número/locais/responsável/data/status/itens e “Ver movimentações”; links do histórico permitem retornar aos detalhes.

Resposta de POST perdida/5xx bloqueia novo envio no rascunho, recomenda conferência e não faz retry. Não existe chave de idempotência neste bloco: repetição manual após fechar o modal continua sendo risco residual. Identificação do responsável não substitui autorização futura.

Capturas e testes em 1440/768/390px cobrem estoque/alertas, limites, dashboard, rascunho, confirmação e detalhes. Tabelas rolam internamente e não produzem overflow da página. Polimento visual preservou sidebar escura, azul, componentes, densidade, foco e marcas existentes; ajustou apenas margens do rodapé novo e estados de alerta. Capturas locais ficam em frontend/test-results, ignorado pelo Git.

## Schema manual e implantação futura

Scripts preparados **não executados**:

- [MySQL](sql/estoque-inteligente-transferencias-mysql-manual.sql): InnoDB, AUTO_INCREMENT, DOUBLE, DATETIME(6); CHECK exige MySQL >= 8.0.16. DDL tem commit implícito.
- [PostgreSQL](sql/estoque-inteligente-transferencias-postgresql-manual.sql): proposta equivalente, IDENTITY, DOUBLE PRECISION, TIMESTAMP(6) e checks adicionais para NaN/Infinity. Não migra automaticamente os dados existentes.

Adicionar colunas nullable conserva todos os saldos e vínculos antigos. Novas tabelas/FKs/índices/checks não possuem exclusão em cascata. Scripts não são idempotentes: inspecionar schema/versão/tipos/nomes reais e estado de execução antes de aplicar. A unicidade do par de estoque precisa estar aplicada previamente, após auditoria de duplicatas; script antigo de unicidade não consolida dados. Alterações aditivas também podem bloquear tabelas; exigir backup/ensaio/plano de aplicação e aprovação específica antes de acessar produção. Aplicar schema revisado antes de subir código novo, pois `ddl-auto=none` está preservado. Nenhum desses passos foi executado.

## Segurança e limites de produção

Validação server-side, queries parametrizadas, DTOs mínimos novos, transação/locks, respostas HTTP já padronizadas e vínculos de auditoria foram aplicados. Nenhum secret novo, dados fictícios de fallback ou credencial frontend. Não se introduziu log de payload/responsável; logs DEBUG já existentes devem ser revisados no perfil de produção futuro.

Pendentes: autenticação/autorização e menor privilégio efetivos; derivar responsável da identidade autenticada ou validar sua autorização; idempotência persistida; paginação; auditoria de configuração de limites; snapshot de nomes/unidades nos documentos; UTC/fuso coordenado; observabilidade e homologação no banco alvo. Listagens atuais usam referências cadastrais vivas: renomear material/funcionário/local muda a descrição exibida, sem mudar a transferência original em quantidade/ID. Não afirmar prontidão para acesso público sem essas proteções.

## Preparação de documentos operacionais

DTO e componente de detalhes reúnem os dados necessários a um futuro comprovante/romaneio: número, origem, destino, responsável, horário, itens/quantidades e observação. Uma ação futura poderá consumir esse contrato, com revisão de snapshot histórico e autorização, sem acoplar renderização ao service transacional. Não há botão fictício, PDF, impressão, QR, assinatura ou confirmação de conferência implementados.

Os PNGs oficiais não estavam presentes. `Brand` conserva fallback textual BES e os caminhos `src/assets/brand/bes-logo-sidebar.png` / `bes-logo-full.png`, com proporção/object-fit e fallback preparados. Não houve logo inventada. Logo completa sobre fundo claro permanece preparada para futuros documentos/login, sem marcar esses requisitos concluídos.

## Rastreabilidade histórica do Bloco 2 (registrada com v1.3)

| RF | Situação após este bloco |
|---|---|
| RF007 — materiais consumíveis, saldo, mínimo/máximo/custo/localização | Parcial: catálogo, unidade e estoques com limites atendidos; custo não implementado |
| RF009 — entradas por compra/devolução/transferência/ajuste | Parcial: entradas existentes e entrada por transferência atendidas; compra/devolução/ajuste específico pendentes |
| RF010 — consumo por funcionário/setor/obra/OS/local | Parcial existente; novo bloco não implementa setor/OS/custeio |
| RF030 — onde cada item está | Parcial: saldo por almoxarifado; patrimônio/TAG/localização fina fora do bloco |
| RF031 — transferência entre almoxarifados/locais | Atendido no escopo básico de almoxarifados: vários itens, execução atômica, responsável e histórico; logística em trânsito não implementada |
| RF032 — entradas/saídas/transferências/ajustes/empréstimos/devoluções | Parcial: transferências somadas a entradas/saídas; ajustes/empréstimos/devoluções específicos pendentes |
| RF033 — filtros funcionário/item/período/setor/OS/local/tipo | Parcial: filtros atuais e filtros novos de transferência; setor/OS e filtros completos server-side pendentes |
| RF034 — necessidade/pedido de compra a partir de demanda aprovada | Não concluído: sugestão de reposição somente informativa, sem pedido de compra |
| RF038 — dashboard conforme perfil | Parcial: alertas reais; perfil/autorização pendentes |
| RF205–RF218 — documentos operacionais (fonte atual v1.4) | Preparação de dados apenas; PDF, QR, impressão, assinaturas e template mestre não foram entregues neste bloco. Avaliar cobertura por RF nos próximos blocos, sem declarar conclusão pela preparação arquitetural |

## Verificações

- 91 testes backend anteriores preservados; 44 novos (135 no total): limites/alertas/reposições, REST, referências, unidades, duplicidade, transferência simples/múltipla, destino ausente, rollback por validação e persistência, filtros/rastreio e três cenários concorrentes.
- 19 testes frontend anteriores preservados; 13 novos (32): situação, limites, quantidade/unidade/saldo/duplicidade, payload, pesquisa e contratos API.
- 10 E2E anteriores preservados; 10 novos (20): configurar limites/alerta/dashboard, transferência completa, validações, erro de negócio, resposta perdida, filtros/detalhes e três viewports. H2 temporário na porta 8081; nenhum MySQL.
- Comandos finais e resultados estão no relatório entregue ao final da rodada. Primeira execução E2E detectou timeout de carregamento frio do dashboard, com 19/20 passando; suíte repetida após revisão visual, sem remover ou relaxar testes existentes.

## Próximo bloco recomendado

Atendimento parcial de solicitações e saldo faltante/necessidade de compra, conservando catálogo compartilhado e separando aprovação de atendimento físico. Primeiro definir quantidades solicitadas/atendidas/pendentes, estados, responsável e política de reserva; então implementar atendimentos transacionais, histórico e testes de concorrência/rollback. Não transformar sugestão de reposição deste bloco em compra automática.


## Invent?rio dos arquivos desta rodada

Os caminhos abaixo s?o relativos ? raiz do reposit?rio. Nenhum arquivo foi exclu?do.

| Estado | Arquivo |
|---|---|
| Alterado | `docs/api.md` |
| Alterado | `frontend/README.md` |
| Alterado | `frontend/package.json` |
| Alterado | `frontend/src/App.jsx` |
| Alterado | `frontend/src/api/client.js` |
| Alterado | `frontend/src/components/AppShell.jsx` |
| Alterado | `frontend/src/components/ui.jsx` |
| Alterado | `frontend/src/pages/Dashboard.jsx` |
| Alterado | `frontend/src/pages/Operations.jsx` |
| Alterado | `frontend/src/styles.css` |
| Alterado | `src/main/java/br/com/almoxarifado/model/Estoque.java` |
| Alterado | `src/main/java/br/com/almoxarifado/model/Movimentacao.java` |
| Alterado | `src/main/java/br/com/almoxarifado/repository/EstoqueRepository.java` |
| Alterado | `src/main/java/br/com/almoxarifado/repository/MovimentacaoRepository.java` |
| Alterado | `src/main/java/br/com/almoxarifado/service/EstoqueService.java` |
| Criado | `docs/estoque-inteligente.md` |
| Criado | `docs/sql/estoque-inteligente-transferencias-mysql-manual.sql` |
| Criado | `docs/sql/estoque-inteligente-transferencias-postgresql-manual.sql` |
| Criado | `frontend/src/pages/StockLimitsForm.jsx` |
| Criado | `frontend/src/pages/TransferForm.jsx` |
| Criado | `frontend/src/pages/Transfers.jsx` |
| Criado | `frontend/src/utils/stockIntelligence.js` |
| Criado | `frontend/tests/stockIntelligence.spec.js` |
| Criado | `frontend/tests/stockIntelligence.test.js` |
| Criado | `src/main/java/br/com/almoxarifado/controller/EstoqueInteligenteController.java` |
| Criado | `src/main/java/br/com/almoxarifado/controller/TransferenciaEstoqueController.java` |
| Criado | `src/main/java/br/com/almoxarifado/dto/AlertaEstoqueResponse.java` |
| Criado | `src/main/java/br/com/almoxarifado/dto/LimitesEstoqueInput.java` |
| Criado | `src/main/java/br/com/almoxarifado/dto/MovimentacaoTransferenciaResponse.java` |
| Criado | `src/main/java/br/com/almoxarifado/dto/TransferenciaInput.java` |
| Criado | `src/main/java/br/com/almoxarifado/dto/TransferenciaResponse.java` |
| Criado | `src/main/java/br/com/almoxarifado/model/ItemTransferencia.java` |
| Criado | `src/main/java/br/com/almoxarifado/model/StatusTransferencia.java` |
| Criado | `src/main/java/br/com/almoxarifado/model/TransferenciaEstoque.java` |
| Criado | `src/main/java/br/com/almoxarifado/repository/ItemTransferenciaRepository.java` |
| Criado | `src/main/java/br/com/almoxarifado/repository/TransferenciaEstoqueRepository.java` |
| Criado | `src/main/java/br/com/almoxarifado/service/EstoqueInteligenteService.java` |
| Criado | `src/main/java/br/com/almoxarifado/service/TransferenciaEstoqueService.java` |
| Criado | `src/test/java/br/com/almoxarifado/service/EstoqueInteligenteTransferenciaTests.java` |

## Resultado final de build e testes

- `npm.cmd run build`: sucesso, Vite compilou 1921 m?dulos; comando retornou 0.
- `npm.cmd test`: 32 testes, 32 passando, nenhuma falha/ignorado; comando retornou 0.
- `.\mvnw.cmd clean test`: 135 testes, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS; comando retornou 0.
- `git diff --check`: sem erros, retorno 0; novos arquivos tamb?m revisados para whitespace.
- Nenhum teste anterior foi removido ou relaxado; Playwright final cobre 20 testes, com H2 isolado, resultados registrados ap?s concluir a captura visual dos detalhes carregados.
- Working tree: 15 arquivos alterados e 24 novos, sem staging/commit/push. HEAD continua d704806; main local permanece 0137ddbddf403f8331d1a3f6d632428f7614ee15.
