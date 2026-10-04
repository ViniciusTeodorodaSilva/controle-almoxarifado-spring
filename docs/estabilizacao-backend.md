# Estabilização do núcleo BES

O snapshot anterior às correções está no commit `6435d1e`, na branch `feature/bes-backend`.

## Solicitações

- `PUT /solicitacoes/{id}/aprovar?responsavelId=123`: somente PENDENTE, com responsável existente, itens válidos e saldo suficiente. Debita o estoque, registra uma saída por produto e persiste APROVADA em uma única transação.
- `PUT /solicitacoes/{id}/rejeitar`: somente PENDENTE; persiste REJEITADA sem alterar estoque.
- Inclusão de itens também bloqueia a solicitação e exige PENDENTE.
- Itens repetidos do mesmo produto são somados. Estoques são bloqueados por ordem de produto para reduzir deadlocks.
- Aprovar, rejeitar e incluir itens usam o mesmo bloqueio pessimista na solicitação. Entradas, saídas e aprovação usam o mesmo bloqueio de estoque.

A aprovação exige `responsavelId` explicitamente na API; isso identifica o funcionário, mas não autentica quem chamou o endpoint. Todas as saídas geradas recebem o responsável e o vínculo JPA com a solicitação. Movimentações manuais mantêm o vínculo nulo. O JSON de movimentação expõe `solicitacaoId`, sem expandir a entidade de solicitação, para evitar recursão e dependência de sessão JPA durante serialização.

## Estoque e banco existente

Cadastro aceita apenas novos estoques, sem ID, com saldo zero. Quantidade omitida permanece zero; valores positivos, negativos ou não finitos são rejeitados com 400. Alterar saldo existente deve passar por entrada/saída, sempre com movimentação correspondente. O produto é bloqueado durante o cadastro, inclusive quando não existe estoque, e a existência do par produto/almoxarifado é verificada antes de salvar. Nenhum saldo legado é modificado por esta mudança.

A constraint única está declarada no modelo. Como produção continua com `ddl-auto=none`, isso não altera o MySQL. A proteção entre chamadas do backend funciona pelo bloqueio do produto; escritas externas só terão proteção equivalente após a aplicação da constraint no banco.

`sql/estoque-unicidade-manual.sql` prepara consultas de diagnóstico e o ALTER para revisão manual. Não há exclusão nem consolidação automática. Não executar o ALTER se houver duplicatas ou relacionamentos nulos. Conferir constraints existentes e fazer backup antes de aplicar em uma janela apropriada. A resolução de duplicatas legadas exige análise dos saldos e históricos; não somar ou apagar automaticamente.

`sql/movimentacao-solicitacao-manual.sql` prepara a coluna opcional, FK e índices para revisão. Essa alteração de schema precisa ser aplicada manualmente antes de executar esta versão contra o MySQL: o novo mapeamento consulta `solicitacao_id`. Nenhum SQL foi executado contra o banco real. Os scripts não possuem DELETE, UPDATE de dados existentes ou DROP.

## Cadastros e erros

Produto, Funcionário e Almoxarifado possuem POST, GET lista, GET por ID e PUT por ID. POST não aceita ID; PUT aceita ID omitido ou igual ao da URL, nunca diferente, e atualiza os campos do registro existente. Nome é obrigatório nos três cadastros; matrícula também é obrigatória para funcionário. Campos opcionais omitidos no PUT ficam nulos, pois PUT representa substituição dos dados cadastrais. Não foi inventada regra de unicidade de nome ou matrícula.

Não há DELETE físico: produtos, funcionários e almoxarifados são referenciados por estoques, solicitações e históricos. Uma futura inativação exige contrato específico para preservar rastreabilidade.

Erros usam `timestamp`, `status`, `erro`, `mensagem` e `path`: 400 para validação/requisição inválida; 404 para recurso inexistente; 409 para solicitação encerrada, estoque duplicado ou conflito de persistência/concorrência. Erros inesperados retornam 500 com mensagem genérica, sem stack trace ou detalhes SQL no JSON. O diagnóstico fica nos logs do servidor.

Consulte [api.md](api.md) para a tabela completa dos endpoints e exemplos.

## Testes

Executar `./mvnw.cmd clean test`. H2 tem escopo exclusivo de teste. Os testes usam explicitamente o perfil `test`, com banco em memória e `create-drop`; a configuração MySQL de produção não foi alterada.

Os testes de serviço não possuem transação envolvendo o método de teste: verificam o estado persistido após commit/rollback real do serviço. Incluem concorrência, constraint única e falha simulada na persistência final da aprovação.

H2 valida as regras e a transação, mas não substitui testes de integração com MySQL isolado para conferir schema, engine InnoDB, isolamento e comportamento de bloqueios em produção.
