# API operacional BES

Fonte funcional oficial: [Documentação Mestre BES v1.4](Documentacao_Mestre_Plataforma_BES_v1_4.docx), RF001–RF218. Planejamento e regras de cobertura: [índice de documentação](README.md). Este documento descreve os contratos implementados; não implica conclusão de todo o roadmap.

Backend REST, base URL local padrão `http://localhost:8080`. IDs são inteiros. Respostas de sucesso preservam o contrato atual (HTTP 200); cadastros/atualizações recebem JSON e operações de estoque/solicitação usam query parameters. As operações exigem sessão e permissões da Security Baseline 2. A auditoria é paginada; os endpoints operacionais anteriores não têm paginação ou exclusão física.

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

Necessidade retorna `id`, `itemSolicitacaoId`, `solicitacaoId`, `produto`, `almoxarifado`, `quantidade`, `status`, `dataHora`, `motivo`, `responsavel`. O Bloco 4 acrescenta EM_COMPRA, progresso quantitativo e cancelamento permissionado; ver o contrato abaixo. Não existe exclusão de necessidade. Unique por item também deduplica ações com outra chave, preservando fotografia já registrada. Entrada manual não encerra a necessidade. Somente recebimento de compra quantitativamente destinado a ela atualiza o progresso do Bloco 4.

Documento frontend `/solicitacoes/{id}/lista-separacao` é HTML A4 imprimível, sem endpoint PDF. QR, comprovantes e autenticação ainda não entregues. Detalhes, limites, scripts e RFs: [Bloco 3](atendimento-solicitacoes.md).

## Configuração segura do backend

Fora do profile `test`, `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` são obrigatórias e externas. Consulte [Security Baseline 1](security.md) para desenvolvimento, testes, produção e rotação. A API e os contratos operacionais permanecem iguais.

## Autenticação e autorização — Security Baseline 2

Todos os endpoints operacionais descritos neste documento agora exigem sessão e permissão. Consultar [contratos e matriz](autenticacao-autorizacao.md#endpoints-novos). GET /auth/csrf e POST /auth/login são públicos; toda escrita exige X-CSRF-TOKEN. Novos: GET /auth/me, POST /auth/logout, GET/POST /usuarios, PUT /usuarios/{id}, PUT /usuarios/{id}/senha e GET /auditoria?pagina=0&tamanho=30. Não há DELETE de usuário/auditoria. 401 = sessão ausente/inválida; 403 = permissão/CSRF; 429 = limite de login. Contratos operacionais anteriores permanecem iguais.

## Bloco 4 — fornecedores, pedidos e recebimento

Todas as 16 rotas novas são **permissionadas**, sem exceção pública. HEAD acompanha GET. As 9 escritas exigem sessão válida e CSRF, além da authority. Inventário total: **85 handlers explícitos, 36 escritas**, preservados os 69 handlers e 27 escritas anteriores. Contagem não inclui HEAD/OPTIONS implícitos.

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

Listagens de fornecedores, pedidos, recebimentos e histórico de recebimentos retornam envelope estável `{content,number,size,totalElements,totalPages,first,last}`. Query `pagina=0`, `tamanho=20`, máximo 100, ordem decrescente de ID. Filtros AND: fornecedor (`termo`, `documento`, `ativo`); pedido (`numero`, `fornecedorId`, `status`, `produtoId`, `necessidadeId`, `de`, `ate`); recebimento (`pedidoId`, `fornecedorId`, `produtoId`, `almoxarifadoId`, `de`, `ate`). Datas ISO, limites inclusivos por dia. Período invertido/página inválida: 400.

Fornecedor: JSON `{nome,nomeFantasia,tipoPessoa,documento,email,telefone,contato,observacao,ativo}`; nome/tipo PF ou PJ obrigatórios, documento opcional normalizado e único. Listagens mascaram documento e omitem contatos; detalhe integral exige FORNECEDOR_GERENCIAR. Inativação por PUT, sem delete. O histórico de compras permanece consultável pelo filtro fornecedorId.

Pedido de criação/edição:

```json
{"fornecedorId":1,"almoxarifadoId":2,"observacao":"Compra conferida","itens":[{"produtoId":3,"quantidade":10,"valorUnitario":12.3456,"paraEstoque":true,"observacao":"","alocacoes":[{"necessidadeId":4,"quantidade":6}]}]}
```

`paraEstoque=true` confirma explicitamente o excedente sem vínculo (4 neste exemplo). Para comprar exatamente uma falta, informar a quantidade alocada e paraEstoque=false. Sem alocações, toda quantidade exige paraEstoque=true. Até 500 itens, produto não repetido: consolidar suas necessidades no mesmo item. Necessidades vinculadas precisam compartilhar produto e almoxarifado planejado; quantidades disponíveis são calculadas e bloqueadas no servidor. Produto/fornecedor ativo é obrigatório na criação e submissão/aprovação. Recebimento de um pedido já aprovado preserva o compromisso histórico, mesmo se o fornecedor tiver sido posteriormente inativado.

Resposta: ID e número `PC-ano-ID`, fornecedor/contexto e snapshots, status, criador/submissão/aprovador/cancelador e datas, itens ativos com quantidades pedida/recebida/pendente/para estoque, preços, subtotais e total calculados. Subtotais HALF_UP em 2 casas, preço DECIMAL(19,4). Valores negativos, preço ausente, precisão/limites inválidos são rejeitados. Quantidades continuam double, com cálculo decimal e bloqueio de resultado que perderia precisão ao retornar ao modelo existente.

Estados: RASCUNHO → AGUARDANDO_APROVACAO → APROVADO → PARCIALMENTE_RECEBIDO → RECEBIDO. Só rascunho editável. Edições arquivam itens anteriores, sem apagá-los. Cancelamento exige JSON `{motivo:"..."}` e é bloqueado após qualquer quantidade recebida. Não há estorno/cancelamento parcial de saldo remanescente. Aprovar/cancelar novamente não repete efeito/auditoria; submeter fora de rascunho é conflito. Autorizar compra não cria estoque.

Recebimento: POST com `Idempotency-Key` (16–100 caracteres `[A-Za-z0-9._:-]`) e JSON:

```json
{"almoxarifadoId":2,"responsavelId":5,"observacao":"Conferência física","itens":[{"itemPedidoId":6,"quantidade":4}]}
```

Exige pedido aprovado/parcial, destino planejado, itens pertencentes ao pedido e quantidade positiva até a pendência acumulada. Ator vem da sessão; responsável físico é cadastro operacional. Reenvio com mesma chave e payload canônico retorna o recebimento original, inclusive após conclusão; outra operação com a chave: 409. Ordem dos itens e espaços externos da observação não mudam o significado. Observação é codificada antes da assinatura para evitar colisão por delimitadores. Não retornar chave/assinatura em DTOs.

Recebimento, itens, destinações, criação segura de estoque ausente, ENTRADA por item, acumulados do pedido/necessidade e auditoria são uma única transação. Movimento acrescenta `pedidoCompraId`, `recebimentoCompraId`, `atorCompraId`; campos históricos preservados. Quantidade destinada atende alocações em ordem de necessidadeId; sobra declarada vai ao estoque. Solicitação não recebe saída/atendimento automático.

Necessidade conserva campos antigos e acrescenta `compra`: quantidade vinculada ainda pendente, recebida, disponível para novo vínculo, pendência atual da solicitação, pedidos/itens/status/quantidades e motivo de cancelamento. Estados ABERTA/EM_COMPRA/ATENDIDA/CANCELADA. ATENDIDA mede recebimento comprado destinado à falta, não entrega ao solicitante. Cancelar necessidade exige ausência de vínculo/recebimento e motivo. Legado sem contador fica A_CONFERIR com recebido null/disponível zero, sem comprar/cancelar por suposição.

Campos desconhecidos, inclusive status, total, ator, aprovadoPor, saldo e recebidos acumulados enviados pelo cliente, são rejeitados em todos os níveis dos novos DTOs. 401 sem identidade; 403 authority/CSRF; 400 payload; 404 referência inexistente; 409 estado/quantidade concorrente/integridade. Detalhes SQL e segredos não são retornados.

Decisões, RFs, evidências e restrições: [compras e recebimento](compras-recebimento.md).

O contrato monetário novo responde `valorUnitario`, `subtotal` e `total` como **strings decimais**, sem notação científica. O frontend envia o valor unitário como string decimal e apresenta os dígitos sem conversão a Number; BigDecimal também aceita o JSON numérico mostrado no exemplo. Isso conserva a precisão nas quatro casas e nos valores grandes durante criação/edição/leitura.

As alocações já comprometem a necessidade enquanto o pedido é RASCUNHO. Cancelar sem recebimentos ou editar o rascunho libera os vínculos anteriores. A leitura sob lock recarrega os contadores persistidos para evitar uso de estado antigo do contexto JPA. Evidências: [auditoria pré-commit](bloco4-auditoria-pre-commit.md).

O snapshot de documento/contato do fornecedor no detalhe e documento do pedido respeita FORNECEDOR_GERENCIAR: sem essa authority, documento mascarado e contato null. A consulta do pedido não contorna a proteção existente no cadastro de fornecedor.

## Bloco 5 — Obras, OS, CC e contexto operacional

17 novos handlers permissionados, 8 escritas. Inventário atual: **102 handlers / 44 escritas**. HEAD acompanha GET; OPTIONS é infraestrutura. Matriz ADMIN/GESTOR gestão e ALMOXARIFE/CONSULTA leitura; sem endpoint operacional público.

| Método | Rota | Público | Sessão | Authority HTTP/service | CSRF |
|---|---|---|---|---|---|
| GET | `/obras` | Não | Sim | `OBRA_LER` | Não |
| GET | `/obras/{id}` | Não | Sim | `OBRA_LER` | Não |
| GET | `/obras/{id}/resumo` | Não | Sim | `OBRA_LER` | Não |
| POST | `/obras` | Não | Sim | `OBRA_GERENCIAR` | Sim |
| PUT | `/obras/{id}` | Não | Sim | `OBRA_GERENCIAR` | Sim |
| PUT | `/obras/{id}/status` | Não | Sim | `OBRA_GERENCIAR` | Sim |
| GET | `/ordens-servico` | Não | Sim | `ORDEM_SERVICO_LER` | Não |
| GET | `/ordens-servico/{id}` | Não | Sim | `ORDEM_SERVICO_LER` | Não |
| GET | `/ordens-servico/{id}/resumo` | Não | Sim | `ORDEM_SERVICO_LER` | Não |
| POST | `/ordens-servico` | Não | Sim | `ORDEM_SERVICO_GERENCIAR` | Sim |
| PUT | `/ordens-servico/{id}` | Não | Sim | `ORDEM_SERVICO_GERENCIAR` | Sim |
| PUT | `/ordens-servico/{id}/status` | Não | Sim | `ORDEM_SERVICO_GERENCIAR` | Sim |
| GET | `/centros-custo` | Não | Sim | `CENTRO_CUSTO_LER` | Não |
| GET | `/centros-custo/{id}` | Não | Sim | `CENTRO_CUSTO_LER` | Não |
| GET | `/centros-custo/{id}/resumo` | Não | Sim | `CENTRO_CUSTO_LER` | Não |
| POST | `/centros-custo` | Não | Sim | `CENTRO_CUSTO_GERENCIAR` | Sim |
| PUT | `/centros-custo/{id}` | Não | Sim | `CENTRO_CUSTO_GERENCIAR` | Sim |

Listagens retornam `{content,number,size,totalElements,totalPages,first,last}`, pagina=0/tamanho=20 (1–100), ID desc. Obras: `termo` código/nome, `cliente`, `status`. OS: `termo` número/título, `obraId`, `centroCustoId`, `status`. CC: `termo` código/nome, `tipo`, `ativo`, `obraId`, `incluirGerais=false`; true inclui centros sem Obra para seletores. IDs de responsáveis e vínculos são operacionais INT; ator autenticado Long é derivado do servidor.

POST/PUT de Obra: `{codigo,nome,descricao?,cliente?,localidade?,observacao?,responsavelId?,dataInicio?,dataTerminoPrevisto?}`. Código ASCII uppercase único global, nome obrigatório, código ≤50/nome/cliente/localidade ≤160 e textos ≤2000. Datas ISO; previsão de término não antecede início.

CC: `{codigo,nome,descricao?,tipo,ativo,obraId?}`. Tipos OBRA/ADMINISTRATIVO/OPERACIONAL/OUTRO; tipo OBRA exige vínculo. Obra do CC é imutável, mesmo antes de uso.

OS: `{obraId,centroCustoId?,titulo,descricao?,observacao?,prioridade?,responsavelId?}`. Obra obrigatória; CC corporativo ou da mesma Obra, vínculos imutáveis. Número `OS-ano-ID`, abertura/início/conclusão, atores/timestamps e versão não são entradas. Prioridade BAIXA/NORMAL/ALTA/URGENTE. Respostas têm campos públicos desses cadastros e auditoria de atores por ID, sem referências internas/hash/versão.

PUT /status: `{status,motivo?}`, exclusivamente transição protegida. Regras: [contexto operacional](obras-os-centros-custo.md#transições). Obra PLANEJADA→ATIVA/CANCELADA; ATIVA→SUSPENSA/CONCLUIDA/CANCELADA; SUSPENSA→ATIVA/CANCELADA. OS ABERTA→EM_ANDAMENTO/CANCELADA; EM_ANDAMENTO→SUSPENSA/CONCLUIDA/CANCELADA; SUSPENSA→EM_ANDAMENTO/CANCELADA. OS suspensa/cancelada exige motivo ≤1000; terminais não reabrem. Encerramento exige demandas resolvidas e, para Obra, OS encerradas.

Criação existente de solicitação mantém query `solicitanteId`,`almoxarifadoId` e admite `obraId`,`ordemServicoId`,`centroCustoId` opcionais. OS deriva Obra e CC; IDs contraditórios são 409. Referência inexistente 404; contexto inativo/suspenso/encerrado para nova demanda 409. Sem contexto continua válido. Não há edição posterior desse contexto. GET antigo, `/operacao` e necessidade acrescentam `contexto`, null no legado/geral, ou snapshot `{obraId,ordemServicoId,centroCustoId,obraCodigo,obraNome,ordemServicoNumero,centroCustoCodigo,centroCustoNome}`.

GET `/solicitacoes`, `/necessidades-compra`, `/pedidos-compra` acrescenta os três filtros de contexto AND. Pedido considera a mesma origem/alocação para todas as dimensões, mantém todos os itens no detalhe e não transforma total do agregado em custo da obra filtrada. GET `/movimentacoes` filtra snapshots de saídas; ENTRADA de compra é rastreada por seus IDs de pedido/recebimento/destinações, sem atribuição global de Obra.

Compra manual acrescenta `itens[].contexto:{obraId?,ordemServicoId?,centroCustoId?}` para classificar somente quantidade sem alocação do item. Exige quantidadeEstoque positiva, conserva declaração paraEstoque do Bloco 4. Alocação aceita somente necessidadeId/quantidade; contexto vem da origem persistida. Respostas de item, alocação e destinação de recebimento mostram contexto próprio, sem obra global no pedido. Cada item recebido acrescenta `quantidadeEstoque` efetivamente recebida (recebido menos destinações); seu `contexto` manual é null quando essa quantidade é zero. Não antecipa contexto de parcela futura. ENTRADA continua sem consumo; SAÍDA de atendimento copia o snapshot da solicitação.

GET `/resumo` retorna `cadastro`, `quantidadeSolicitacoes`, `quantidadeNecessidades`, `quantidadePedidos`, `quantidadeOrdensServico`, `ordensServicoAbertas`, listas `ordensServico`, `solicitacoes`, `necessidades`, `pedidos`, `materiaisSolicitados`, `materiaisConsumidos`, `limiteRelacoes:100`, `custoConsumido:null` e `avisoCustos`. Para OS inclui referências legíveis Obra/CC/responsável disponíveis. Relações são tuplas escalares: OS `[id,numero,titulo,status]`; solicitação `[id,status,data]`; necessidade `[id,solicitacaoId,status,quantidade,recebida]`; pedido `[id,numero,status]`; material solicitado `[produtoId,nome,unidade,solicitado,atendidoConhecido]`; consumo `[produtoId,nome,unidade,saidas]`. Contagens totais não sofrem o limite da amostra. Para histórico completo, usar filtros/listagens da operação. Nenhum indicador monetário consumido é inventado.

Escritas usam DTO estrito em todos os níveis e rejeitam campo interno/ator/status fora da ação de transição. Erros: 400 entrada inválida, 401 identidade, 403 permissão/CSRF, 404 referência, 409 estado/integridade/concorrência. Sucesso segue padrão 200. Criação de estrutura não tem chave persistida de idempotência; em resposta incerta consultar listagem antes de repetir. Unique protege código/número; transição terminal repetida é 409.

Documentos frontend: `/ordens-servico/{id}/documento`, lista de separação, pedido e comprovante exibem contextos reais. Não há endpoint público de PDF/BI/QR. Scripts manuais, cobertura RF e limites: [Bloco 5](obras-os-centros-custo.md).


## Bloco 6 - ativos individuais

Contratos e regras: [ferramentas e equipamentos](ferramentas-equipamentos.md). Sucessos 200; erros 400/401/403/404/409 conforme entrada/identidade/permissão/referência/estado. IDs operacionais INT, ator Long derivado do servidor.

POST `/ativos`: `{cadastro:{codigoPatrimonial?,nome,descricao?,fabricante?,modelo?,numeroSerie?,categoriaId?,dataAquisicao?,observacao?},almoxarifadoId,condicao,proximaInspecao?}`. PUT `/ativos/{id}` recebe somente campos de `cadastro`; código patrimonial imutável. PUT `/ativos/{id}/situacao`: `{acao:INATIVACAO|REATIVACAO|BAIXA,responsavelId,motivo}`.

POST `/emprestimos`: `{ativoId,entreguePorId,funcionarioId,contexto?:{obraId?,ordemServicoId?,centroCustoId?},condicao,previsaoDevolucao?,observacao?}`. POST `/emprestimos/{id}/devolucao`: `{devolvidoPorId,recebidoPorId,almoxarifadoId?,condicao,observacao?}`; ID é do empréstimo, não do ativo.

POST `/transferencias-ativos`: `{ativoId,entreguePorId,almoxarifadoId?,contexto?,condicao,observacao?}`; destino exclusivo almoxarifado/Obra. POST `/transferencias-ativos/{id}/recebimento`: `{recebidoPorId,condicao,observacao?}`. POST `/inspecoes-ativos`: `{ativoId,inspetorId,condicao,resultado:APROVADO|APROVADO_COM_RESSALVA|REPROVADO,proximaInspecao?,observacao?}`. Esses comandos e `/situacao` exigem `Idempotency-Key`; cadastro/edição não o persistem. Datas ISO LocalDate, horário do servidor LocalDateTime. DTOs estritos, inclusive contexto.

Listas/histórico: envelope paginado `content,totalElements,totalPages,number,size,first,last`, pagina=0/tamanho=20 (máximo 100), ID desc. Ativos: `termo,status,condicao,categoriaId,funcionarioId,almoxarifadoId,obraId,ordemServicoId,centroCustoId,ativo,inspecaoPendente`. Registros: `ativoId,funcionarioId,obraId,ordemServicoId,centroCustoId,aberto,vencido,de,ate`; contexto de destino na mesma linha; período inclusivo. `vencido=true` exige aberto e previsão anterior a hoje. Detalhe de operação acrescenta `encerramento,aberto,vencido`; tipo incorreto retorna 404. Sem entidades, chave/hash ou versão nas respostas.

Resumo: `emprestimosAbertos,emprestimosVencidos,indisponiveis,inspecoesPendentes`. Documentos protegidos frontend: `/ativos/{id}/ficha`, `/emprestimos/{id}/documento`, `/transferencias-ativos/{id}/documento`; usam as mesmas APIs permissionadas, sem endpoint PDF/QR público.

18 handlers permissionados, 8 escritas; inventario total: 120 handlers e 52 escritas. Nenhum endpoint operacional publico.

| Metodo | Rota | Classificacao | Authority HTTP/service | CSRF |
|---|---|---|---|---|
| GET | `/ativos` | Permissionado | `ATIVO_LER` | Nao |
| GET | `/ativos/resumo` | Permissionado | `ATIVO_LER` | Nao |
| GET | `/ativos/{id}` | Permissionado | `ATIVO_LER` | Nao |
| GET | `/ativos/{id}/historico` | Permissionado | `ATIVO_LER` | Nao |
| POST | `/ativos` | Permissionado | `ATIVO_GERENCIAR` | Sim |
| PUT | `/ativos/{id}` | Permissionado | `ATIVO_GERENCIAR` | Sim |
| PUT | `/ativos/{id}/situacao` | Permissionado | `ATIVO_GERENCIAR` | Sim |
| GET | `/emprestimos` | Permissionado | `EMPRESTIMO_LER` | Nao |
| GET | `/emprestimos/{id}` | Permissionado | `EMPRESTIMO_LER` | Nao |
| POST | `/emprestimos` | Permissionado | `EMPRESTIMO_GERENCIAR` | Sim |
| GET | `/transferencias-ativos` | Permissionado | `TRANSFERENCIA_ATIVO_LER` | Nao |
| GET | `/transferencias-ativos/{id}` | Permissionado | `TRANSFERENCIA_ATIVO_LER` | Nao |
| POST | `/transferencias-ativos` | Permissionado | `TRANSFERENCIA_ATIVO_GERENCIAR` | Sim |
| GET | `/inspecoes-ativos` | Permissionado | `INSPECAO_ATIVO_LER` | Nao |
| GET | `/inspecoes-ativos/{id}` | Permissionado | `INSPECAO_ATIVO_LER` | Nao |
| POST | `/inspecoes-ativos` | Permissionado | `INSPECAO_ATIVO_GERENCIAR` | Sim |
| POST | `/emprestimos/{id}/devolucao` | Permissionado | `EMPRESTIMO_GERENCIAR` | Sim |
| POST | `/transferencias-ativos/{id}/recebimento` | Permissionado | `TRANSFERENCIA_ATIVO_GERENCIAR` | Sim |
## Auditoria dos contratos de ativos — Bloco 6

Sem novos handlers nesta auditoria. Nas listagens de registros de ativos, `aberto` e `vencido` são campos de resposta calculados em lote. `aberto`/`vencido` são filtros cumulativos; inspeções são concluídas sem evento de fechamento. `aberto=false&vencido=true` não transforma um empréstimo fechado em vencido. Inspeção pendente exclui BAIXADO em todos os canais. A pesquisa textual de ativos interpreta `%`, `_` e barra invertida como caracteres literais.

Empréstimo com Obra confirmada efetiva a localização nesse contexto e limpa o almoxarifado atual; a origem congelada permanece no registro e é restaurada por devolução sem outro destino. Transferência efetiva localização somente na chegada. Observações/descrições longas admitem LF/CR/TAB, sem admitir outros controles nem ampliar campos curtos. Idempotência diferencia nulo de texto literal `<null>`. Campos internos e snapshots continuam somente de resposta. [Evidências da auditoria](bloco6-auditoria-pre-commit.md).
