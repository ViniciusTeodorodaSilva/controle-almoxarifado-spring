# EPI operacional — Bloco 7

Referência: [Documentação Mestre v1.4](Documentacao_Mestre_Plataforma_BES_v1_4.docx). O bloco controla EPIs entregues a funcionários; não implementa medicina ocupacional, eSocial, treinamentos, PGR/PCMSO ou avaliação legal de aptidão.

## Modelo e integridade

`EpiConfiguracao` estende Produto em relação 1:1 com PK produto_id. Produto e Estoque permanecem os únicos cadastros de material e saldo físico. EPI não é Ativo patrimonial. Variantes simultâneas de modelo, tamanho, fabricante ou CA usam produtos distintos. Esses metadados não podem mudar quando houver saldo positivo, pois o estoque atual não distingue lotes. Datas físicas e lote são informações da linha entregue, sem inventar controle de lote do estoque.

`RegistroEpi` e `ItemEpi` são eventos imutáveis. Entrega congela produto, unidade/fracionamento, CA/modelo/fabricante/tamanho, políticas, funcionário/matrícula, responsáveis, ator autenticado, almoxarifado e contexto Obra/OS/CC. Histórico e documento usam os snapshots; renomear/inativar cadastros não reescreve o passado. Quantidade em posse é quantidade entregue menos encerramentos, calculada dos eventos, sem saldo físico paralelo. Não há DELETE.

Entrega múltipla aceita 1–50 produtos distintos e exige recebimento confirmado pelo operador. Valida todos antes de gravar; gera SAÍDA por item via EstoqueService e vincula o ID da Movimentacao. Insuficiência, falha de movimento ou auditoria reverte todos os itens, saldos, movimentos, histórico e chave. Reposição é nova saída. Substituição exige origem em posse do mesmo funcionário, quantidade anterior, motivo, condição e destino explícitos; grava nova entrega e encerramento anterior juntos. Quantidades antiga/nova são independentes, permitindo produto e unidade diferentes. Não credita o anterior durante substituição.

Devolução pode ser parcial. SEGREGADO/DESCARTE não aumentam saldo. ESTOQUE exige condição NOVO explícita, política histórica e atual permitindo retorno, produto/configuração ativos, mesma identidade física e item não vencido. Conferência de reutilização é decisão operacional/SST humana; o sistema não atesta aptidão. Perda exige PERDIDO/PERDA e registra encerramento, sem crédito. Fechamento múltiplo exige mesmo funcionário e contexto de origem; separe contextos diferentes. O fluxo posterior de materiais segregados permanece pendente.

## CA, prazos e alertas

CA é metadado do modelo, registrado manualmente e congelado em cada entrega. O formato aceita somente 1–20 dígitos; não consulta nem certifica sua autenticidade. Validade cadastrada do CA, fabricação, validade física e recomendação de troca são campos distintos. Data física passada/fabricação futura ou validade anterior à fabricação bloqueiam entrega. Prazo do CA vencido gera informação, sem presumir vencimento físico: a orientação do [MTE sobre CA e validade do EPI](https://www.gov.br/trabalho-e-emprego/pt-br/assuntos/inspecao-do-trabalho/seguranca-e-saude-no-trabalho/equipamentos-de-protecao-individual-epi/perguntas_e_respostas) distingue esses conceitos. O cadastro não comprova aquisição com CA válido nem conformidade normativa.

Alertas de posse consideram validade física e troca recomendada, vencidos ou até 30 dias. Encerramentos não geram alertas ativos. Resumo conta linhas em posse, não soma unidades heterogêneas. Prazos ausentes ficam explicitamente não informados, sem inferência. Ficha reúne posse e histórico paginados; o parâmetro de página aplica-se às duas seções.

## Locks, idempotência e segurança

Entrega: Obra → CC → OS quando aplicável → origens por ID (substituição) → produtos por ID → pares de estoque determinísticos → gravações. Fechamento: origens → produtos → estoque, sem bloquear contexto histórico. Configuração bloqueia Produto; não bloqueia origens. Preserva os protocolos dos blocos anteriores. Último saldo e duplo fechamento são protegidos por locks pessimistas. Quantidades dos eventos usam DECIMAL(19,6); estoque legado usa Double, com validação de finitude e rejeição de alterações perdidas na precisão. Migração coordenada do saldo legado permanece pendente.

`Idempotency-Key` obrigatório nas três operações críticas, 16–100 caracteres ASCII letras/números/ponto/underscore/dois-pontos/hífen. Chave única persistida e hash do comando normalizado distinguem nulo de texto literal. Replay idêntico retorna o registro; comando diferente conflita. A UI bloqueia submissão concorrente e conserva chave/corpo para reenvio após resposta incerta. Essa conservação dura enquanto a tela estiver aberta; recarregar exige conferir o histórico antes de nova operação. Configuração não usa chave: criação é única por produto, edição atualiza metadados com auditoria.

Todos os 12 endpoints são permissionados, sem rota pública nova. EPI_LER e EPI_ENTREGA_LER para os quatro perfis; EPI_GERENCIAR para ADMIN/GESTOR; EPI_ENTREGA_GERENCIAR para ADMIN/GESTOR/ALMOXARIFE. Escritas exigem sessão e CSRF. Services também exigem autoridade. DTOs estritos rejeitam ator, saldo, status e propriedades desconhecidas. Ator vem do SecurityContext, responsável operacional é funcionário informado. Auditoria participa da transação. Não há isolamento por obra/empresa na política atual; IDs não concedem autoridade, e não se declara confidencialidade individual de RH/SST.

## Documentos e interface

Grupo SEGURANÇA / SST com EPIs e Entregas; Funcionários conserva cadastro existente e ganha link contextual para ficha. Nova entrega usa página com destinatário/contexto, itens e confirmação. Lista filtra funcionário/produto/CA/local/tipo/período/contexto. Documento contextual de entrega, substituição, devolução e descarte usa OperationalDocument, dados históricos, identificação, responsáveis e confirmação operacional; não contém assinatura jurídica. Impressão A4/PDF pelo navegador, sem sidebar/ações administrativas. Não exige papel. Fallback textual BES/B&S conserva identidade existente; assets oficiais ausentes não são substituídos por logo inventada.

## Cobertura funcional

| RF | Estado no escopo EPI | Evidência/limite |
|---|---|---|
| RF099 | ATENDIDO | Configuração, entrega quantitativa e responsáveis |
| RF100 | PARCIAL | Transferências existentes e saída ao funcionário rastreáveis; sem cadeia dedicada por lote Central→obra |
| RF101 | ATENDIDO | Histórico imutável e substituição vinculada |
| RF104 | PARCIAL | Alertas de EPI configurados; treinamentos/ASO fora do bloco |
| RF107 | PARCIAL | Autoridades e sessão; sem isolamento individual/por obra |
| RF102, RF103, RF105, RF106 | NÃO ATENDIDO neste bloco | Treinamentos, documentos ocupacionais, anexos e canal SST não implementados |
| RF205, RF207, RF208, RF210 | ATENDIDO no módulo | Comprovantes com identificação/contexto históricos |
| RF206 | PARCIAL | Template BES; logo oficial ausente |
| RF209, RF211 | Não aplicável à entrega EPI | Listas de solicitação/necessidade seguem blocos anteriores |
| RF212 | NÃO ATENDIDO | QR autenticado pendente |
| RF213 | PARCIAL | Impressão/PDF via navegador; sem serviço PDF dedicado |
| RF214 | ATENDIDO no fluxo EPI | Operação em tela sem papel, sujeita à validação responsiva |
| RF215 | PARCIAL | Confirmação operacional; assinatura jurídica pendente |
| RF216, RF217, RF218 | ATENDIDO no módulo | Template reutilizado, documentos avaliados e ações contextuais |

Dependências reutilizadas: funcionário RF001, estoque RF007, movimentos RF009/RF010, transferências RF031 e infraestrutura de auditoria/contexto dos blocos anteriores. A classificação não declara esses RFs globalmente concluídos.

## Banco e pendências

Propostas manuais [MySQL](sql/epis-mysql-manual.sql) e [PostgreSQL](sql/epis-postgresql-manual.sql): três tabelas, FKs restritivas, índices, chave única e DECIMAL; sem backfill ou saldo duplicado. Não executadas. Homologar tipos e schema legado, constraints, driver PostgreSQL e protocolo de locks antes de uso real. Imutabilidade é garantida na aplicação; restringir DML e proteger backups na implantação.

Permanecem pendentes QR, logo oficial, anexos/evidências, valorização financeira, homologação em banco real, rotação externa de credenciais históricas, HTTPS/proxy, migrações controladas, backup/restore testado, observabilidade/monitoramento e sessão/rate limiting em produção. Não houve autorização para banco real, produção, commit, push ou Bloco 8.

### Precisao de quantidades no estoque legado

Entrega valida fracionamento em BigDecimal antes de converter para Double. Entrega e retorno fisico rejeitam quantidades cuja conversao mude o valor decimal. A variacao efetiva do saldo nao pode divergir da quantidade em meio micro (0.0000005) ou mais; ruido binario ordinario abaixo disso nao bloqueia. Residuo de saldo de ate 1e-12 e zerado no fluxo EPI, e a verificacao de insuficiencia admite somente esse residuo minimo. Essa tolerancia e um milionesimo da menor quantidade EPI (0.000001), nao autoriza saldo negativo nem arredondamento de quantidades do evento. Regressao consome 0.3 em 0.1 + 0.2 e confirma saldo final zero; valores extremos com perda de precisao sao bloqueados com rollback. Nao altera metodos dos blocos anteriores ou migra o saldo legado para DECIMAL.
