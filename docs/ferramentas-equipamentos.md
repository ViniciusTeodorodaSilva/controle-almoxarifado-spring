# Ferramentas e equipamentos — Bloco 6

Referência: Documentação Mestre BES v1.4 (RF001–RF218). Escopo incremental autorizado: ativos individuais, custódia, empréstimos, devoluções, transferência com chegada, inspeção e consultas. Dependências: cadastros existentes de funcionário, almoxarifado, categoria e contexto do Bloco 5; sessão, CSRF e auditoria da Security Baseline 2. Materiais, saldos, atendimento e PedidoCompra continuam com seus agregados e protocolos próprios.

## Modelo e integridade

`bes_ativo` representa uma unidade patrimonial; não é quantidade de Produto. Código manual ASCII normalizado e único; prefixo FER reservado à geração `FER-ID` pelo servidor. Número de série não é presumido globalmente único. Cadastro inicial exige almoxarifado e condição. CategoriaMaterial é reutilizada conforme RF005; nenhuma tabela de categoria duplicada. Data de aquisição é informativa, sem preço, depreciação ou lançamento financeiro.

Status: DISPONIVEL, EMPRESTADO, EM_TRANSFERENCIA, INDISPONIVEL, BAIXADO. Condições: NOVO, BOM, REGULAR, DANIFICADO, INOPERANTE. Situação administrativa ativa, reprovação e condição são dimensões separadas. DANIFICADO/INOPERANTE e reprovação impedem novo empréstimo; inspeção vencida ou prevista para hoje também o impede. Não se chama INDISPONIVEL de ordem de manutenção: módulo de manutenção e reserva estão pendentes. Baixa é terminal, exige responsável e motivo, preserva histórico e impede operações; inativação reversível também exige motivo e ausência de custódia aberta. Não há DELETE operacional.

`bes_registro_ativo` é histórico imutável, com tipo, ID de ativo, identificação congelada, condições/status antes e depois, funcionários e nomes, locais e nomes, contextos de origem/destino, usuário autenticado e horário do servidor. Devolução/recebimento são novos registros ligados pelo `origem_id` único, sem atualizar o evento anterior. `pendencia_id` indica a única custódia ou transferência aberta. Chave e assinatura de idempotência são internas e não expostas na API.

## Fluxo

Empréstimo exige ativo disponível e utilizável, entrega e funcionário recebedor; previsão de devolução opcional não pode anteceder retirada. Responsável operacional não substitui ator autenticado. Se o ativo estiver numa Obra, empréstimo para outra Obra exige transferência prévia. Contexto informado deve ser admissível e compatível; OS pode derivar Obra/CC, com regra do Bloco 5. Não existe responsável inferido do login.

Devolução exige empréstimo aberto, quem devolveu, quem recebeu e condição. Destino opcional retorna ao local/contexto congelado da retirada; almoxarifado explícito substitui destino e limpa contexto de Obra. Dano não acusa funcionário automaticamente. Reprovação continua bloqueando após devolução até nova inspeção aprovada. Custódia e pendência são limpas atomicamente.

Transferência exige responsável pela saída e destino exclusivo: almoxarifado ou Obra/OS/CC. Envio muda status para trânsito, mas mantém localização de origem até chegada. Chegada exige recebedor e condição e efetiva o destino previamente congelado, inclusive quando o cadastro contextual foi posteriormente encerrado. Divergência é registrada em observação, sem culpa automática. Não se transfere ativo emprestado/inativo/baixado nem se envia para destino idêntico. Ativo danificado pode ser transportado sem ser liberado para empréstimo.

Inspeção registra inspetor, condição, resultado, observação e próxima data opcional futura. Reprovação/ressalva exige observação; condição danificada exige reprovação. Reprovação bloqueia empréstimo; aprovação libera apenas se ativo administrativamente ativo e condição utilizável. Inspeção durante empréstimo preserva responsável e custódia aberta. Não inspeciona em trânsito ou após baixa. Não entrega checklist configurável, agenda automática por periodicidade ou ordem de manutenção.

## Segurança e concorrência

Todos os 18 novos handlers são permissionados. Authorities: ATIVO_LER/GERENCIAR, EMPRESTIMO_LER/GERENCIAR, TRANSFERENCIA_ATIVO_LER/GERENCIAR, INSPECAO_ATIVO_LER/GERENCIAR. ADMIN/GESTOR gerenciam tudo; ALMOXARIFE lê e opera empréstimo, transferência e inspeção, sem gerenciar cadastro/situação; CONSULTA lê. HTTP e service verificam permissão. Não há segregação por Obra/funcionário na política atual: IDs arbitrários não concedem acesso a quem não possui a authority, mas leitores autorizados podem ler o módulo inteiro. Tipo incorreto de registro retorna 404. Escritas exigem CSRF e DTO estrito; rejeitam ator, snapshots, status e campos internos forjados.

Cada operação física, inspeção ou alteração de situação usa Idempotency-Key (16–100 caracteres ASCII permitidos), assinatura SHA-256 do comando normalizado e unique global. Repetir chave/comando retorna registro original sem novo efeito; trocar comando com a mesma chave retorna 409. Cadastro/edição não têm chave persistida: em resposta incerta conferir cadastro antes de repetir. UI preserva comando e chave para reenvio após erro de rede/servidor; campos ficam congelados nessa tentativa.

Transação cobre ativo, evento, chave e auditoria. Ordem: contexto Obra → CC → OS quando necessário, depois lock pessimista do ativo. Não adquirir lock de estrutura depois do ativo. Devolução/chegada usam contexto histórico sem novos locks de estrutura. Não bloqueiam estoque, pedido ou demanda. Unique protege código e fechamento; versão e refresh pessimista protegem estado. Helpers removem proxies Hibernate antes de acessar campos das entidades. Testes concorrentes validam duplo empréstimo, dupla devolução, empréstimo versus transferência e códigos simultâneos; falha de auditoria reverte todos os efeitos.

## Consultas, interface e documentos

Listas e histórico paginados (20 padrão, até 100, ID desc), filtros por estado/condição/categoria/local/funcionário/contexto; histórico de operações aceita período na API e nas listagens. Contexto AND aplicado na mesma linha. Pesquisa de ativo inclui código, nome, fabricante, modelo, série, responsável e nome da Obra. Dashboard mostra empréstimos vencidos e inspeções pendentes. Seletores de ativo nas operações mostram até 100: refinar a pesquisa do seletor ou usar a listagem e seu detalhe. Consultas escalares evitam N+1 de relações.

Telas: ativos, detalhe com história e custódia, empréstimos, transferências e inspeções. Ações obedecem estado e perfil; backend continua autoridade final. Modal com labels, estado de envio e mensagens; tabela horizontal mantém página mobile sem overflow. Documentos autenticados: ficha de ativo, comprovante de empréstimo/devolução e comprovante de transferência/chegada, derivados dos mesmos registros. Comprovantes preservam snapshots e horários, separados da ficha atual. Template OperationalDocument/Brand, fallback textual BES e impressão/salvar PDF do navegador. Logo oficial não disponível: não inventada. Não há PDF backend, QR, acesso público, upload de fotos ou assinatura digital jurídica. Confirmação em tela registra identidades operacionais e ator; não substitui política de assinatura.

## Cobertura funcional incremental

| RFs | Cobertura neste bloco |
|---|---|
| RF005 | Reutilização de categoria para ferramenta/equipamento; classificação global parcial |
| RF006 | Identificação, série, fabricante/modelo, aquisição, condição/local/status; valor financeiro pendente |
| RF014–RF018 | Empréstimo, previsão, devolução, histórico e custódia entregues |
| RF019 | Disponível/em uso/indisponível/baixado; reserva e manutenção parciais/pendentes |
| RF026–RF028 | Condição danificada e baixa motivada rastreável entregues no escopo operacional |
| RF032–RF033 | Eventos e filtros entregues para ativos; setor e visão universal de movimentos parciais |
| RF042–RF043 | Consulta por funcionário e situação entregue; relatórios globais parciais |
| RF046 | Empréstimos abertos vencidos entregues; alertas automáticos RF047 parciais (dashboard) |
| RF053–RF057 | Auditoria, horários, pesquisa e segurança aplicados aos ativos; cobertura global não declarada |
| RF092 | Histórico factual de condições sem culpa automática; avaliação de cuidado parcial |
| RF170–RF171 | Condição de saída/chegada, responsáveis e horário entregues; evidências anexas parciais |
| RF172 | Observação e registro vinculável a evidência futura; fotos/anexos pendentes |
| RF173–RF174 | Sem culpa automática e empréstimo rápido contextual em tela entregues |
| RF175–RF178 | Observações, resultado e próxima inspeção entregues parcialmente; ocorrência formal, periodicidade, checklist e encaminhamento à manutenção pendentes |
| RF205, RF207–RF208, RF210, RF213–RF218 | Documentos/contexto/template/ações/mobile/confirmar entregues no módulo; PDF por navegador e assinatura simples parciais |
| RF206 | Brand reutilizado; aplicação da logo oficial depende de asset oficial |
| RF209, RF211 | Documentos de material/compra anteriores preservados; não são fluxo de ativo |
| RF212 | QR pendente; sem endpoint público substituto |

RF020–RF025 manutenção/reserva, RF029 financeiro de perdas, RF048–RF052 QR/fotos, RF091 favoritos e RF158–RF163 mobilização completa permanecem pendentes neste bloco. Arquitetura preparada não equivale a requisito concluído. Não entrega financeiro, BI integrado, manutenção completa, produção ou Bloco 7.

## Contratos confirmados pela auditoria pré-commit

O empréstimo confirmado para Obra efetiva a entrega nesse contexto: limpa o almoxarifado físico atual, mantém o responsável e congela a origem no registro. Devolução sem destino explícito restaura essa origem, mesmo depois de renomear ou encerrar o contexto. Empréstimo geral conserva o almoxarifado. Transferência continua em duas etapas: conserva a origem em trânsito e somente a chegada efetiva o destino congelado.

A assinatura idempotente distingue ausência de texto e qualquer texto literal, inclusive `<null>`, com marcadores tipados e comprimento. Observação/descrição longas aceitam quebra de linha e tabulação; campos curtos e outros controles continuam rejeitados. Pesquisa trata `%`, `_` e barra invertida literalmente. Filtros `aberto` e `vencido` são cumulativos: fechado + vencido não retorna empréstimo aberto. Inspeção é registro concluído autônomo; `aberto=false` a inclui, sem exigir evento de fechamento. Listagens operacionais retornam `aberto` e `vencido`, calculados em lote; baixados não compõem inspeção pendente em lista, detalhe ou dashboard.

A interface apresenta estado/condição em português, origem, identificação e destino planejado nas confirmações, com cores acompanhadas de texto. Contexto opcional de empréstimo começa recolhido; a pesquisa conserva o ativo selecionado mesmo fora dos novos resultados. A ficha atual resolve nomes de almoxarifado/funcionário com consultas limitadas; comprovantes usam somente snapshots históricos. Revisão, regressões, inventário e limites completos: [auditoria pré-commit](bloco6-auditoria-pre-commit.md).

## Banco e homologação

Scripts manuais: [MySQL](sql/ferramentas-equipamentos-mysql-manual.sql) e [PostgreSQL](sql/ferramentas-equipamentos-postgresql-manual.sql). Criam duas tabelas, índices, checks/unique e FKs sem cascade de exclusão; ordem acomoda referência circular do evento pendente. Não possuem backfill nem estoque fictício. Não executados. Testes usam exclusivamente H2; validar dialect, migração, locks, collation, plano e desempenho em homologação autorizada antes de qualquer produção. Sem valorização ou custo consumido; ENTRADA de compras continua sem consumo/custo da Obra.
