# API operacional BES

Backend REST, base URL local padrão `http://localhost:8080`. IDs são inteiros. Respostas de sucesso preservam o contrato atual (HTTP 200); cadastros/atualizações recebem JSON e operações de estoque/solicitação usam query parameters. Não há autenticação nesta rodada. Não há paginação nem exclusão física.

## Todos os endpoints

| Método | Endpoint | Descrição | Parâmetros principais |
|---|---|---|---|
| POST | `/produtos` | Cadastrar produto | JSON: `nome` obrigatório; `descricao`, `unidadeMedida`, `categoria`, `tipoControle` opcionais; sem ID |
| GET | `/produtos` | Listar produtos | — |
| GET | `/produtos/{id}` | Buscar produto | `id` |
| PUT | `/produtos/{id}` | Atualizar produto existente | `id`; JSON cadastral; nome obrigatório |
| POST | `/funcionarios` | Cadastrar funcionário | JSON: `nome`, `matricula` obrigatórios; `funcao` opcional; sem ID |
| GET | `/funcionarios` | Listar funcionários | — |
| GET | `/funcionarios/{id}` | Buscar funcionário | `id` |
| PUT | `/funcionarios/{id}` | Atualizar funcionário | `id`; JSON cadastral; nome/matrícula obrigatórios |
| POST | `/almoxarifados` | Cadastrar almoxarifado | JSON: `nome` obrigatório; sem ID |
| GET | `/almoxarifados` | Listar almoxarifados | — |
| GET | `/almoxarifados/{id}` | Buscar almoxarifado | `id` |
| PUT | `/almoxarifados/{id}` | Atualizar almoxarifado | `id`; JSON com nome obrigatório |
| POST | `/estoques` | Cadastrar estoque zerado | JSON: `produto.id`, `almoxarifado.id`, `quantidade` zero ou omitida; sem ID |
| GET | `/estoques` | Listar estoques | — |
| GET | `/estoques/{id}` | Buscar estoque | `id` |
| GET | `/estoques/produto/{produtoId}` | Estoques por produto | `produtoId` |
| GET | `/estoques/almoxarifado/{almoxarifadoId}` | Estoques por almoxarifado | `almoxarifadoId` |
| GET | `/estoques/produto/{produtoId}/almoxarifado/{almoxarifadoId}` | Buscar saldo de um par | `produtoId`, `almoxarifadoId` |
| PUT | `/estoques/entrada` | Entrada manual com histórico | Query: `produtoId`, `almoxarifadoId`, `quantidade`, `solicitanteId`, `responsavelId` |
| PUT | `/estoques/saida` | Saída manual com histórico | Mesmos parâmetros de entrada |
| POST | `/solicitacoes` | Criar solicitação PENDENTE | Query: `solicitanteId`, `almoxarifadoId` |
| GET | `/solicitacoes` | Listar solicitações com itens | — |
| GET | `/solicitacoes/{id}` | Buscar solicitação com itens | `id` |
| POST | `/solicitacoes/{solicitacaoId}/itens` | Adicionar item à PENDENTE | `solicitacaoId`; query `produtoId`, `quantidade` |
| PUT | `/solicitacoes/{id}/aprovar` | Aprovar e debitar atomicamente | `id`; query `responsavelId` obrigatório |
| PUT | `/solicitacoes/{id}/rejeitar` | Rejeitar sem alterar saldo | `id` |
| GET | `/solicitacoes/{id}/movimentacoes` | Histórico originado pela aprovação | `id` |
| GET | `/solicitacoes/status/{status}` | Filtrar status | `PENDENTE`, `APROVADA` ou `REJEITADA` |
| GET | `/solicitacoes/funcionario/{funcionarioId}` | Filtrar solicitante | `funcionarioId` |
| GET | `/movimentacoes` | Listar histórico | — |
| GET | `/movimentacoes/{id}` | Buscar movimentação | `id` |
| GET | `/movimentacoes/produto/{produtoId}` | Histórico por produto | `produtoId` |
| GET | `/movimentacoes/almoxarifado/{almoxarifadoId}` | Histórico por almoxarifado | `almoxarifadoId` |
| GET | `/movimentacoes/solicitacao/{solicitacaoId}` | Histórico por solicitação | `solicitacaoId` |
| GET | `/movimentacoes/tipo/{tipo}` | Filtrar tipo | `ENTRADA` ou `SAIDA` |

## Contratos

PUT de cadastro mantém a identidade do recurso: `id` no corpo pode ser omitido ou igual ao da URL; um ID diferente é rejeitado. Campos opcionais omitidos ficam nulos. POST com ID é rejeitado. Não há regra de unicidade de matrícula nesta versão.

Estoque novo inicia zerado. Para inserir saldo inicial, cadastrar o par e fazer uma entrada. Toda alteração de saldo pelas APIs gera movimentação; saldos negativos e quantidades não finitas são rejeitados. O histórico legado não é alterado automaticamente.

Aprovação exige solicitação PENDENTE, itens positivos, saldo suficiente e responsável existente. Quantidades repetidas de um produto são somadas e geram uma movimentação por produto. Saldo, movimentos e status são persistidos atomicamente. O responsável é informado pelo cliente, sem autenticação provisoriamente. Saídas manuais podem ocorrer independentemente de solicitações; aprovação exige disponibilidade no momento em que é executada e não reserva saldo.

Movimentação expõe `solicitacaoId`, que é nulo nas operações manuais. O objeto completo `solicitacao` não é serializado no histórico para evitar recursão. As entidades restantes continuam sendo o contrato de resposta.

Filtros por ID exigem que o recurso pai exista (404 se inexistente); retornam `[]` se existir sem registros correspondentes. A consulta de estoque por produto+almoxarifado retorna 404 se não existir o par. Enum inválido retorna 400. GET de recurso inexistente retorna 404.

## Erros

| Status | Situação |
|---|---|
| 400 | Campos/parâmetros ausentes ou inválidos, JSON inválido, quantidade/saldo inicial inválido, saldo insuficiente, solicitação sem itens |
| 404 | Recurso ou referência informada não existe |
| 409 | Estoque duplicado, solicitação não PENDENTE, conflito de integridade ou bloqueio concorrente |
| 500 | Falha interna inesperada; mensagem genérica |

```json
{
  "timestamp": "2026-10-04T12:00:00Z",
  "status": 409,
  "erro": "Conflict",
  "mensagem": "Somente solicitação PENDENTE permite esta operação",
  "path": "/solicitacoes/1/aprovar"
}
```

Stack traces e mensagens SQL não são enviados ao cliente.

## Sequência operacional

1. Cadastrar produto, funcionário e almoxarifado.
2. `POST /estoques` com `{"produto":{"id":1},"almoxarifado":{"id":1},"quantidade":0}`.
3. `PUT /estoques/entrada?produtoId=1&almoxarifadoId=1&quantidade=10&solicitanteId=1&responsavelId=1`.
4. `POST /solicitacoes?solicitanteId=1&almoxarifadoId=1`.
5. `POST /solicitacoes/1/itens?produtoId=1&quantidade=2`.
6. `PUT /solicitacoes/1/aprovar?responsavelId=1`.
7. Consultar saldo e `GET /solicitacoes/1/movimentacoes`.

Antes de usar esta versão no MySQL, revisar e aplicar manualmente o schema preparado em `docs/sql`, particularmente a coluna `movimentacao.solicitacao_id`. A aplicação mantém `ddl-auto=none` e não aplica migrations automaticamente.
