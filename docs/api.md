# API operacional BES

Fonte funcional oficial: [Documentação Mestre BES v1.4](Documentacao_Mestre_Plataforma_BES_v1_4.docx), RF001–RF218. Planejamento e regras de cobertura: [índice de documentação](README.md). Este documento descreve os contratos implementados; não implica conclusão de todo o roadmap.

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
| PUT | `/solicitacoes/{id}/aprovar` | Autorizar sem saída de estoque | `id`; query `responsavelId` obrigatório |
| PUT | `/solicitacoes/{id}/rejeitar` | Rejeitar sem alterar saldo | `id` |
| GET | `/solicitacoes/{id}/movimentacoes` | Histórico de saídas confirmadas e legado | `id` |
| GET | `/solicitacoes/status/{status}` | Filtrar status | `PENDENTE`, `APROVADA`, `EM_SEPARACAO`, `PARCIALMENTE_ATENDIDA`, `ATENDIDA`, `REJEITADA` |
| GET | `/solicitacoes/funcionario/{funcionarioId}` | Filtrar solicitante | `funcionarioId` |
| GET | `/movimentacoes` | Listar histórico | — |
| GET | `/movimentacoes/{id}` | Buscar movimentação | `id` |
| GET | `/movimentacoes/produto/{produtoId}` | Histórico por produto | `produtoId` |
| GET | `/movimentacoes/almoxarifado/{almoxarifadoId}` | Histórico por almoxarifado | `almoxarifadoId` |
| GET | `/movimentacoes/solicitacao/{solicitacaoId}` | Histórico por solicitação | `solicitacaoId` |
| GET | `/movimentacoes/tipo/{tipo}` | Filtrar tipo | `ENTRADA` ou `SAIDA` |

## Catálogo Mestre Bloco 1

Os endpoints anteriores são preservados. Novos campos de Produto: `codigo`, `especificacaoTecnica`, `ativo`, `categoriaMaterial: {"id": 1}` e `unidadeMedidaConfigurada: {"id": 1}`. Os campos textuais legados continuam disponíveis. Código omitido no POST é gerado; omitido no PUT é preservado. Ativo e referências omitidos no PUT também são preservados. Referências informadas devem existir e estar ativas para novos vínculos.

| Método | Endpoint | Descrição | Parâmetros principais |
|---|---|---|---|
| POST | `/categorias` | Cadastrar categoria | JSON: nome obrigatório, descricao opcional, ativo (padrão true) |
| GET | `/categorias` | Listar categorias | Query ativo opcional |
| GET | `/categorias/{id}` | Buscar categoria | id |
| PUT | `/categorias/{id}` | Atualizar/ativar/inativar | JSON completo; sem troca de ID |
| POST | `/unidades-medida` | Cadastrar unidade configurável | JSON: nome, sigla, permiteFracionamento (padrão false), ativo (padrão true) |
| GET | `/unidades-medida` | Listar unidades | Query ativo opcional |
| GET | `/unidades-medida/{id}` | Buscar unidade | id |
| PUT | `/unidades-medida/{id}` | Atualizar/ativar/inativar | JSON completo; sem troca de ID |
| GET | `/produtos/busca` | Pesquisar catálogo | Query termo, categoriaId e ativo opcionais; combinação por AND |
| GET | `/produtos/equivalentes` | Sugerir possíveis cadastros semelhantes | Query termo obrigatório com pelo menos 3 caracteres |
| GET | `/produtos?ativo=true` | Listar somente ativos | ativo opcional; ausência preserva listagem completa |

Exemplo de POST /produtos:

```json
{
  "codigo": "MAT-001",
  "nome": "Luva de proteção",
  "descricao": "Proteção industrial",
  "especificacaoTecnica": "Norma EN388",
  "categoriaMaterial": {"id": 1},
  "unidadeMedidaConfigurada": {"id": 1},
  "tipoControle": "CONSUMO",
  "ativo": true
}
```

Nome de categoria equivalente, sigla e código duplicados retornam 409. Referência inexistente retorna 404; inativa ou sem ID retorna 400. Unidade não fracionária rejeita frações nos fluxos de quantidade. Categorias/unidades não possuem DELETE; inativação conserva histórico. A busca por termo é case-insensitive e literal; sugestões não bloqueiam criação. Consulte [catalogo-mestre.md](catalogo-mestre.md) para compatibilidade, limites e RFs.

## Contratos

PUT de cadastro mantém a identidade do recurso: `id` no corpo pode ser omitido ou igual ao da URL; um ID diferente é rejeitado. Campos opcionais textuais omitidos ficam nulos, com as exceções de compatibilidade de Produto descritas acima. POST com ID é rejeitado. Não há regra de unicidade de matrícula nesta versão.

Estoque novo inicia zerado. Para inserir saldo inicial, cadastrar o par e fazer uma entrada. Toda alteração de saldo pelas APIs gera movimentação; saldos negativos e quantidades não finitas são rejeitados. O histórico legado não é alterado automaticamente.

Aprovação exige PENDENTE, itens válidos e responsável existente, mas não exige saldo disponível nem cria SAIDA. Registra responsável/data e autoriza. A saída ocorre somente no atendimento confirmado, após iniciar separação, com quantidades explícitas por item. Produtos repetidos compartilham saldo; um movimento é criado por item entregue, vinculado à solicitação e atendimento. Toda a operação é atômica. O responsável é informado pelo cliente; autenticação permanece futura. Separação não reserva saldo. **Mudança deliberada do Bloco 3:** clientes antigos que dependiam do débito na aprovação precisam adotar o novo POST. Legados com saída vinculada são reconhecidos sem repetir débito, conforme [estratégia de compatibilidade](atendimento-solicitacoes.md).

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
6. `PUT /solicitacoes/1/aprovar?responsavelId=1` (apenas autoriza).
7. `PUT /solicitacoes/1/iniciar-separacao?responsavelId=1`.
8. `POST /solicitacoes/1/atendimentos` com Idempotency-Key e quantidades confirmadas (ver contrato abaixo).
9. Consultar saldo e `GET /solicitacoes/1/movimentacoes`.

Antes de usar esta versão no MySQL, revisar e aplicar manualmente o schema preparado em `docs/sql`, incluindo a coluna `movimentacao.solicitacao_id` e o script `catalogo-mestre-manual.sql`. A aplicação mantém `ddl-auto=none` e não aplica migrations automaticamente.


## Bloco 2 — estoque inteligente e transferências

Os contratos existentes continuam válidos. Estoque acrescenta `estoqueMinimo`/`estoqueMaximo` anuláveis; Movimentacao acrescenta `transferenciaId` anulável. Os novos endpoints respondem DTOs, sem serializar o grafo JPA completo. Não existem PUT/DELETE de transferência.

| Método | Rota | Uso |
|---|---|---|
| PUT | `/estoques/{id}/limites` | Substituir ambos os limites, sem alterar saldo/histórico |
| GET | `/estoques/alertas?produtoId=&almoxarifadoId=` | Saldos <= mínimo configurado |
| GET | `/estoques/reposicoes?produtoId=&almoxarifadoId=` | Alertas com sugestão positiva |
| POST | `/transferencias` | Executar transferência atômica de 1 a 500 produtos |
| GET | `/transferencias?origemId=&destinoId=&produtoId=` | Listar confirmadas, ID decrescente |
| GET | `/transferencias/{id}` | Detalhes de todos os itens |
| GET | `/transferencias/{id}/movimentacoes` | Saídas/entradas com saldos, origem/destino e responsável |

PUT limites:

```json
{"estoqueMinimo":10,"estoqueMaximo":20}
```

Valores devem ser finitos e >= 0; máximo >= mínimo quando ambos presentes. `null` ou campo omitido remove aquele limite: **PUT substitui ambos**, não é atualização parcial. `{}` remove os dois. Configuração não é movimentação. Máximo é referência de reposição, não bloqueio de entrada/transferência acima dele. Limites podem ser decimais mesmo em unidade inteira: são parâmetros de alerta, não quantidades movimentadas.

Alerta/resposta do PUT (exemplo):

```json
{"estoqueId":1,"produtoId":1,"codigo":"MAT-001","produto":"Parafuso","almoxarifadoId":1,"almoxarifado":"Central","unidade":"UN","saldoAtual":10,"estoqueMinimo":10,"estoqueMaximo":20,"quantidadeSugerida":10}
```

Sugestão = `max(0, máximo - saldo)`, somente se saldo <= mínimo e máximo presente. Ausente máximo: `null`; saldo acima do mínimo: PUT retorna sugestão `null`. `/reposicoes` exclui sugestões nulas/zero; `/alertas` mantém esses alertas. Consultas não geram compra, pedido ou movimentação. Filtros combinados por AND; pai inexistente retorna 404 e pai existente sem correspondência retorna `[]`.

POST transferência:

```json
{"origemId":1,"destinoId":2,"responsavelId":1,"observacao":"Conferido na origem","itens":[{"produtoId":1,"quantidade":3}]}
```

Origem/destino distintos, referências existentes, produto ativo, quantidade positiva/finita, fracionamento conforme unidade configurada, produtos não repetidos, observação <= 1000 caracteres. Origem exige estoque cadastrado e suficiente; destino ausente é criado zerado dentro da mesma transação. Sem cadastro duplicado, saldo negativo ou resultado parcial. Saldo numericamente inválido/overflow ou quantidade que não altera o double por perda de precisão é rejeitado. Não se infere fracionamento de textos legados.

Resposta POST/GET (200, conforme padrão atual):

```json
{"id":1,"almoxarifadoOrigem":{"id":1,"nome":"Central"},"almoxarifadoDestino":{"id":2,"nome":"Obra A"},"responsavel":{"id":1,"nome":"Operador"},"dataHora":"2026-10-04T12:00:00.123456","status":"CONCLUIDA","observacao":"Conferido na origem","itens":[{"id":1,"produtoId":1,"codigo":"MAT-001","produto":"Parafuso","unidade":"UN","quantidade":3}]}
```

Histórico específico retorna `id`, `produto:{id,codigo,nome,unidadeMedida}`, `almoxarifado:{id,nome}`, `responsavel:{id,nome}`, `tipo:SAIDA|ENTRADA`, `quantidade`, `saldoAnterior`, `saldoPosterior`, `dataHora`, `transferenciaId`, `origemId`, `destinoId`. Cada item produz saída na origem e entrada no destino com mesmo horário/responsável. Movimentações manuais/solicitações permanecem com `transferenciaId:null`; transferência não inventa solicitante. Filtrar uma transferência por produto mantém **todos** os seus itens nos detalhes.

Erros: 400 para regras inválidas; 404 para referência/estoque de origem inexistentes; 409 para integridade/lock; 500 genérico para falha interna. O POST executa imediatamente, não aceita ID/status/saldo escolhidos pelo cliente e não possui chave de idempotência. Em resposta perdida, consultar listagem/histórico antes de repetir; frontend bloqueia reenvio incerto.

Schema é manual: consultar [estoque-inteligente.md](estoque-inteligente.md). Scripts MySQL e PostgreSQL foram preparados, **não executados**. Produção segue `ddl-auto=none`; endpoints novos exigem aplicação revisada do schema antes de deployment.


## Bloco 3 — separação, atendimento e necessidade

| Método | Endpoint | Contrato |
|---|---|---|
| GET | `/solicitacoes/{id}/operacao` | Snapshot seguro com status efetivo/registrado, metadados, itens/quantidades/saldo/falta e aviso legado |
| PUT | `/solicitacoes/{id}/iniciar-separacao` | Query `responsavelId`; somente APROVADA, sem saída |
| POST | `/solicitacoes/{id}/atendimentos` | Header `Idempotency-Key`; JSON abaixo; confirma saída atômica |
| GET | `/solicitacoes/{id}/atendimentos` | Histórico de atendimentos confirmados, itens, responsável e instante |
| GET | `/solicitacoes/{id}/faltas` | Itens com pendente positivo, inclusive falta atual zero; quantitativos/saldo/necessidade |
| POST | `/necessidades-compra` | Header `Idempotency-Key`; JSON `{ "itemSolicitacaoId": 10, "responsavelId": 123 }`; falta calculada no servidor |
| GET | `/necessidades-compra` | Filtros AND opcionais `status`, `produtoId`, `almoxarifadoId`, `solicitacaoId` |
| GET | `/necessidades-compra/{id}` | Contexto, quantidade registrada, status, instante, motivo e responsável |

```json
{
  "responsavelId": 123,
  "itens": [
    { "itemSolicitacaoId": 10, "quantidade": 7 },
    { "itemSolicitacaoId": 11, "quantidade": 2 }
  ]
}
```

Todos os novos contratos retornam HTTP 200 em sucesso. 400: payload/quantidade/unidade inválidos, falta inexistente ou saldo insuficiente; 404: recurso inexistente; 409: transição inválida, inconsistência legada, colisão de idempotência/constraint/lock. Falha de infraestrutura 500 usa resposta genérica, sem stack trace. Ausência do header obrigatório é 400. Header ASCII 8–100, caracteres alfanuméricos e `. _ : -`. Mesma chave/payload devolve original; conteúdo diferente é 409; ordem dos itens não altera assinatura. Constraint e transação complementam a proteção. Chaves dos POSTs são persistidas, não retornadas ao usuário em DTO.

`GET /operacao` retorna `status`, `statusRegistrado`, `compatibilidadeLegada` (ATUAL, RECONHECIDA, INCONSISTENTE), `aviso`, solicitante/almoxarifado, responsáveis/datas de aprovação/separação e `itens`. Cada item tem `quantidadeSolicitada`, `quantidadeAtendida`, `quantidadePendente`, `saldoAtual`, `saldoDisponivel`, `estoqueCadastrado`, `quantidadeFaltante`, `necessidadeCompraId` e produto/unidade mínimos. `saldoDisponivel` aloca o saldo entre itens repetidos, sem reserva. Em legado ambíguo as quantidades derivadas são nulas, nunca números inventados. `/faltas` vazio não prova consistência do legado: consultar `/operacao` antes de agir. GETs antigos preservam status registrado e contrato `quantidade`; leitura operacional deve usar o snapshot.

Atendimento aceita EM_SEPARACAO/PARCIALMENTE_ATENDIDA; produto ativo, itens pertencentes à solicitação, quantidade positiva finita e respeitando unidade/pendente/saldo. Zero deve ser omitido do payload, não enviado. História de atendimento tem `id`, `solicitacaoId`, `responsavel`, `dataHora`, itens com `id`, `itemSolicitacaoId`, `produto`, `quantidade`. Movimentos adicionam `atendimentoId` nullable ao vínculo existente; movimentos legados permanecem sem esse vínculo.

Necessidade retorna `id`, `itemSolicitacaoId`, `solicitacaoId`, `produto`, `almoxarifado`, `quantidade`, `status`, `dataHora`, `motivo`, `responsavel`. Estados ABERTA/ATENDIDA/CANCELADA, sem endpoint de alteração/exclusão nesta etapa. Unique por item também deduplica ações com outra chave, preservando fotografia já registrada. Reposição não apaga nem encerra automaticamente a necessidade.

Documento frontend `/solicitacoes/{id}/lista-separacao` é HTML A4 imprimível, sem endpoint PDF. QR, comprovantes e autenticação ainda não entregues. Detalhes, limites, scripts e RFs: [Bloco 3](atendimento-solicitacoes.md).
