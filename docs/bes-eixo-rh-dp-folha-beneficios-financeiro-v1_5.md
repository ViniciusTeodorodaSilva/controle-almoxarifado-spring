# Eixo RH DP Folha Benefícios e Financeiro BES v1.5

Inclusão no planejamento autorizada em F02, conforme docs/bes-fontes-decisoes-documentais-v1_5.md. Não implementado; sem cálculos legais ou integrações presumidos. IDs RH/FOL/BEN/FIN são capacidades documentais, não RF219 em diante. Preservados 218 RF e 12 RNF. Decisão de produto não equivale a implementação; parâmetros dependem de validação DP/contábil/jurídica/SST e fornecedores.

## Governança e fontes oficiais

PLANEJAMENTO: cada empresa configura seu modelo A (folha nativa BES) ou B (folha externa integrada), com vigência e responsável. A chave de exclusividade da fonte oficial é somente empresa + competência: no máximo uma fonte e uma versão PUBLICADA_OFICIAL vigente nesse escopo, independentemente do modelo, empregado, vínculo, tipo de folha ou número de versão. Antes da primeira publicação pode não existir fonte oficial. A identidade da versão é empresa + competência + versão; ela não é a chave de exclusividade. Folhas regulares, complementares e demais tipos, com seus itens por vínculo/empregado, integram a mesma versão oficial da competência, sem criar fontes oficiais paralelas. Retificações preservam versões anteriores e exigem substituição explícita, aprovação e conciliação. Simulação/comparação paralela não gera obrigação ou pagamento; importação/exportação/remessa não comprova liquidação. Após timeout, consultar o estado antes de repetir.

Pagamento exige segregação de funções e autoridade backend por empresa/alçada, nova aprovação após alteração relevante, CSRF, auditoria e chave persistida. Preparador, conferente e autorizador são papéis de processo, sem inventar novos perfis já existentes. Credenciais externas ficam fora do frontend/logs; autorização de código não é autoridade bancária ou conformidade jurídica. Holerites/recibos têm fonte, competência, versão, retenção e acesso individual; portal/dispositivo compartilhado não expõe outro funcionário.

Leis, instrumentos e regras tributárias/trabalhistas devem ter fonte validada, vigência, parâmetros e responsável. Não são fornecidas alíquotas, tabelas ou fórmulas legais nesta candidata. SST, biometria, aceite, retenção e obrigações externas exigem validação competente. Schema, tenant, motor folha, banco, fornecedores e integrações não são capacidades prontas.

### Estados e transições planejados

Estes são estados futuros do processo de folha, não novos estados de implementação dos requisitos: RASCUNHO → CONFERIDA → APROVADA → FECHADA → PUBLICADA_OFICIAL. FECHADA congela conteúdo aprovado, mas ainda não torna a versão oficial nem autoriza pagamento. A publicação requer fonte conciliada, fechamento, alçada e ausência de outra publicação concorrente. Somente PUBLICADA_OFICIAL é vigente.

Correção antes da publicação volta a RASCUNHO e invalida conferência, aprovação e fechamento afetados; editar FECHADA exige reabertura autorizada e auditada. Uma versão não publicada pode ser CANCELADA com motivo e histórico preservados. Uma PUBLICADA_OFICIAL não é editada, cancelada ou apagada diretamente: a retificação cria nova versão RASCUNHO que percorre todos os gates. Ao publicar a sucessora, a anterior passa a SUBSTITUÍDA na mesma mudança atômica de referência oficial. SUBSTITUÍDA e CANCELADA são terminais; retorno ao modelo/resultado anterior usa nova versão e nova aprovação.

A substituição registra empresa, competência, versão anterior/sucessora, modelo/provedor, responsável operacional, ator autenticado, instante, justificativa, conferências e aprovações. A validação da versão esperada e a exclusividade por empresa/competência devem impedir duas publicações concorrentes. Falha antes da conclusão da troca mantém a anterior oficial e reverte a nova referência e seus efeitos internos; não simular rollback de transferência bancária já realizada.

### Obrigações pagamentos e reprocessamento

A identidade estável da obrigação usa empresa, competência, vínculo/empregado, tipo de folha, rubrica e origem de negócio aprovada, sem usar o número de versão para criar obrigação paralela. A versão registra proveniência e revisão de valores. Agregações de pagamento conservam vínculo com cada obrigação. Reaplicar publicação, importação, título ou remessa exige chave idempotente persistida e conteúdo canônico: mesmo comando retorna o resultado original; mesma chave com conteúdo diferente conflita. Outra chave não permite duplicar a mesma obrigação ou pagamento de negócio.

Retificação concilia valor oficial anterior, novo devido, obrigações abertas e pagamentos confirmados. Só a diferença aprovada pode gerar ajuste complementar ou compensação/restituição sujeita à política validada. Obrigação liquidada e pagamento realizado não são apagados, recriados nem tratados como não pagos. Redução do devido gera pendência de tratamento autorizada, sem estorno bancário ou desconto automático. Provisão, título, remessa, confirmação bancária e conciliação são etapas distintas; publicar folha não executa pagamento.

A publicação e a intenção interna de integração devem ser rastreáveis; falha externa após publicação mantém o fato oficial e a pendência consultável, sem reenviar automaticamente dinheiro. Em timeout ou retorno parcial, consultar protocolo/status do provedor e conciliar antes de repetir. Se o destino não permitir consulta/deduplicação confiáveis, bloquear retry automático e exigir tratamento autorizado. Conflito, falha de auditoria ou alteração não aprovada impede nova publicação; comprovante sem confirmação não prova pagamento.

### Importação migração e responsabilidades

No modelo B, resultado importado é candidato até conferir identidade da empresa, competência, modelo vigente, versão/protocolo do provedor, vínculos, rubricas, bruto, descontos, líquido, encargos e acumulados com referências aprovadas. Duplicata de arquivo/protocolo não cria nova folha; divergência ou lote incompleto fica pendente e impede publicação. No modelo A, cálculo usa regras e parâmetros versionados validados; comparação A/B permanece simulação sem efeitos pagáveis.

Troca A/B exige competência de corte, inventário de saldos/acumulados, obrigações e pagamentos, conciliação com o provedor, aprovação do corte, bloqueio da dupla fonte e plano de retorno. Competências históricas permanecem acessíveis. Retificação de competência anterior usa fonte e regras históricas autorizadas; não muda a origem pela configuração atual. Retorno ou migração não apaga pagamentos e não autoriza republicar versões antigas como se fossem novas obrigações.

Preparador registra dados e proposta; conferente independente concilia; aprovador/publicador autorizado responde pela decisão por empresa/alçada; tesouraria autoriza pagamentos em processo separado. Política aprovada impede autoaprovação e separa preparação, conferência e autorização; exceção exige validação formal e trilha, sem ampliar perfis atuais. DP/contábil/jurídico valida regras e referências; segurança/privacidade valida escopo, retenção e controles; provedor/banco valida contrato técnico. Nenhuma regra legal, conformidade LGPD ou autoridade bancária é presumida.

Publicação, substituição, importação e consulta exigem autorização backend por empresa e finalidade, sessão/CSRF nas escritas, auditoria atômica sem secrets e acesso individual aos holerites. Testar vazamento entre empresas e empregados, sessão revogada, ausência de alçada e adulteração de empresa/competência/versão. Retenção e direitos sobre dados pessoais exigem política LGPD validada que preserve a trilha necessária; dispositivo compartilhado não expõe documento de outro empregado.

### Critérios e cenários de falha verificáveis

CA-P1-01: duas versões de mesma empresa/competência, com números, empregados, tipos ou modelos diferentes, tentam publicação concorrente; no máximo uma vence, a outra recebe conflito consultável, sem duplicar obrigações. Empresas e competências distintas continuam independentes.

CA-P1-02: retificação percorre RASCUNHO/CONFERIDA/APROVADA/FECHADA; publicação da sucessora troca a fonte de forma atômica e marca a anterior SUBSTITUÍDA, preservando documentos, aprovações e histórico. Alteração após aprovação a invalida. Falha de auditoria durante a troca conserva a versão oficial anterior e nenhum efeito parcial interno.

CA-P1-03: replay de publicação/importação/integração, inclusive com outra chave para a mesma origem de negócio, não duplica obrigação/pagamento. Mesma chave e conteúdo alterado conflitam. Resultado externo incerto permanece pendente até consulta/conciliação; retry não gera nova remessa pagável por presunção.

CA-P1-04: obrigação de 100 já paga é retificada para 120; somente diferença aprovada de 20 pode originar complemento. Retificação para 80 preserva pagamento de 100 e abre tratamento autorizado da diferença de 20; não apaga pagamento nem produz desconto/estorno automático. Valores são exemplos de integridade, não fórmula trabalhista.

CA-P1-05: importação divergente, incompleta, de empresa/competência errada ou duplicada não se torna oficial; validar bruto/descontos/líquido/encargos e referência externa. Troca A/B concilia corte e acumulados, recusa dupla fonte e mantém consulta a períodos anteriores e pagamentos.

CA-P1-06: preparador sem alçada, autoaprovação indevida, acesso de outra empresa/empregado ou sessão/CSRF inválidos não publicam nem consultam dados restritos. Testar rastreabilidade de reabertura/substituição, falha externa após publicação e recuperação sem recriar pagamento. DP/contábil/jurídico e segurança devem aprovar os cenários antes da futura homologação.


## RH e DP

### RH01 Cadastro e histórico funcional

Identidade única por funcionário; dados cadastrais e funcionais com vigência, origem e responsável, sem duplicar pessoa por obra.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RF001, RF057, RF156. Vínculos relacionados: RF001, DP03, RH02.
Critério de aceite: Alterar função/lotação mantendo a consulta ao período anterior e os snapshots de entrega; recusar sobreposição inválida de vigências.
Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### RH02 Contratos e vínculos

Registrar tipo de vínculo, empresa, início/fim, condições, aditivos e documentos versionados, com validação DP/jurídica; dados contratuais não concedem acesso automaticamente.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH01. Vínculos relacionados: RH01, RH03.
Critério de aceite: Consultar contrato vigente e aditivos históricos; validar regras de encerramento e acesso privado; não inferir vínculo trabalhista por empréstimo ou cadastro.
Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### RH03 Cargos e funções

Catálogo por empresa, atribuições e função exercida com vigências; relacionar cargos/funções sem sobrescrever histórico. Função de EPI não comprova cargo contratual.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH01, RF004. Vínculos relacionados: RF004, RH01.
Critério de aceite: A mudança futura de cargo/função preserva eventos anteriores e dispara revisão explícita de matriz EPI, sem alterar entregas passadas.
Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### RH04 Obras e lotações

Registrar empresa, unidade, área, equipe, obra/OS/CC, lotação e rateios com vigências. Congelar o contexto operacional no evento e distinguir alocação de apropriação monetária.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH01, RF061. Vínculos relacionados: RF061, RF128, RH01.
Critério de aceite: Trocar obra sem criar prontuário EPI ou caixa duplicados; comprovar origem/destino da lotação e conferir rateios sem dupla contagem.
Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### RH05 Escalas e jornadas

Calendários, escalas, jornada contratual, turnos, intervalos e exceções por vigência/empresa; regras dependem de validação DP/jurídica e instrumentos aplicáveis.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, RH04. Vínculos relacionados: RH02, RH04.
Critério de aceite: Reproduzir jornada de referência por período, incluindo troca de escala e exceções; testar conflitos, limites e aprovação sem fixar parâmetros legais sem fonte.
Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### RH06 Ponto eletrônico

Receber marcações com origem, identidade, timestamp e fuso; preservar registro bruto e ajustes justificados/aprovados. Confirmar requisitos e capacidade técnica de fornecedor; biometria não é obrigatória.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH05, RF127. Vínculos relacionados: RH05, RF127.
Critério de aceite: Importar o mesmo lote sem duplicar; rastrear marcação original, ajuste e aprovador; reconciliar ausências/inconsistências e bloquear exportação de período não conferido.
Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### RH07 Banco de horas

Política validada por empresa/vigência, créditos/débitos, compensações, saldo, prazo e encerramento; preservar memória de cálculo e aprovações.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH06, FOL06. Vínculos relacionados: RH06, RH05, FOL06.
Critério de aceite: Recalcular período de referência com regra versionada; conciliar saldo inicial/eventos/final, ajuste e compensação sem lançar horas duas vezes.
Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### RH08 Horas extras configuráveis

Eventos de horas extras com tipo, quantidade, autorização e parâmetros versionados por empresa; separar apuração, aprovação e pagamento.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH06, FOL06. Vínculos relacionados: RH06, RH05, FOL06.
Critério de aceite: Casos de referência aprovados por DP reproduzem quantidades/valores e arredondamento; evento rejeitado não segue para folha e reenvio não duplica rubrica.
Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### RH09 DSR

Apuração conforme fontes/regras aplicáveis e vigentes, calendário e eventos; sem fórmula ou percentual universal presumido.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH06, RH08, FOL06. Vínculos relacionados: RH06, RH08, FOL06.
Critério de aceite: Responsável DP/jurídico aprova casos com faltas, extras e mudança de regra; memória de cálculo por competência é reprodutível e não retroage silenciosamente.
Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### RH10 Adicionais

Rubricas e critérios de adicionais aplicáveis, base, período, evidência e responsável; parâmetros dependem de DP/jurídico/SST quando necessário.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, RH06, FOL06. Vínculos relacionados: RH02, RH06, FOL06.
Critério de aceite: Validar elegibilidade, incidências e vigências com casos aprovados; alteração de condição preserva cálculo já fechado e exige revisão rastreável.
Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### RH11 Faltas e atrasos

Classificar ocorrências, justificativas, documentos e aprovação antes de descontos/efeitos; não atribuir punição automaticamente.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH06, FOL06. Vínculos relacionados: RH06, RH05, FOL06.
Critério de aceite: Manter marcação original e decisão humana; corrigir justificativa sem apagar trilha e conferir efeito autorizado na competência correta.
Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### RH12 Afastamentos

Tipo/período e documentos com acesso proporcional, efeitos de jornada/folha/benefícios validados; minimizar dados de saúde expostos ao financeiro.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, RH05, FOL06. Vínculos relacionados: RH02, RH05, FOL06, RF156.
Critério de aceite: Calendário e folha refletem caso validado; leitor financeiro recebe efeito necessário, sem documento clínico; retorno preserva vigência anterior.
Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### RH13 Férias

Períodos, saldo, programação, aprovação, recibos e efeitos financeiros conforme regras validadas; não inventar prazos/cálculos legais.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, RH05, FOL06. Vínculos relacionados: RH02, RH05, FOL06.
Critério de aceite: Caso aprovado por DP concilia programação, folha, benefício e título financeiro; alteração/cancelamento gera versão e ajuste, sem pagamento duplicado.
Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### RH14 13º salário

Eventos, provisões, parcelas e ajustes por competência/vigência e regras validadas; integrar recibos e financeiro sem presumir critérios legais.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, FOL06. Vínculos relacionados: RH02, FOL06, FOL08.
Critério de aceite: Conferir referência validada, parcelas/provisões e liquidado; mesma origem reimportada não gera nova obrigação; diferença exige ajuste autorizado.
Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### RH15 Admissão e desligamento

Checklist DP com identidade, contrato, datas, documentos e responsáveis; desligamento reconcilia custódia/EPI, acesso, folha/benefícios e obrigações, sem atribuir culpa ou quitar pendência automaticamente.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH01, RH02, RF018, RF099. Vínculos relacionados: RH01, RH02, RF018, RF099, FOL06.
Critério de aceite: Simular admissão e saída com pendência de ferramenta; preservar histórico, revogar acesso conforme política e submeter acerto ao aprovador competente.
Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### RH16 Portal do funcionário

Consulta individual autorizada de cadastro, ponto, saldo de horas, férias, holerites e recibos; pedidos de correção/ciente sem editar fonte oficial. Dispositivo compartilhado e contingência acessível.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH01, RF056, RF057, FOL07, BEN13. Vínculos relacionados: RF056, RF057, RF156, RH01, FOL07, BEN13.
Critério de aceite: Funcionário A não acessa B por troca de ID/URL; operador não se passa por empregado; consultas deixam trilha proporcional e aceite não presume validade jurídica universal.
Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.


## Folha

### FOL01 Modelo A Folha nativa BES

Motor completo planejado: rubricas, eventos, bases/incidências, memória de cálculo, líquido, conferência, aprovação, fechamento, encargos, holerites e obrigações aplicáveis; responsável DP/contábil valida regras.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH06, FOL03, FOL06, FOL10. Vínculos relacionados: RH02, RH05, RH06, FOL03, FOL06, FOL08, FOL10.
Critério de aceite: Massa de referência validada externamente reproduz cálculo e fechamento; divergência bloqueia oficialização; nenhuma previsão de cálculo significa motor já implementado.
Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FOL02 Modelo B Folha externa integrada

Exportar eventos autorizados e importar resultados/holerites oficiais com identificador do provedor, competência, versão e protocolo; confirmar layout/API/capacidade técnica antes de prometer integração.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH06, FOL03, FOL04, FOL10. Vínculos relacionados: RH06, FOL03, FOL04, FOL07, FOL11.
Critério de aceite: Reenvio/importação duplicada não cria eventos, holerites ou títulos novos; totais de empregado/rubrica/encargo conciliados; lote inválido não vira fonte oficial.
Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FOL03 Escolha do modelo por empresa

Cada empresa configura modelo A nativo ou B externo, com vigência e responsável. Por empresa + competência existe no máximo uma fonte e uma versão PUBLICADA_OFICIAL vigente, independente de empregado, tipo ou versão; antes de publicar pode não haver oficial. Simulação não gera obrigação.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, DP09. Vínculos relacionados: RH02, RF003, DP09.
Critério de aceite: Empresa/competência com modelos A/B ou tipos diferentes recusa fontes oficiais paralelas; empresas/competências distintas são independentes. Configuração vigente e histórico permanecem auditáveis; alteração não reclassifica competência anterior.
Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FOL04 Fonte oficial por competência

Fonte oficial exclusiva por empresa + competência, sem versão, empregado ou tipo na chave de exclusividade. Identidade da versão é empresa + competência + versão; seus itens por vínculo/empregado e tipo compõem a mesma fonte. Estados futuros RASCUNHO/CONFERIDA/APROVADA/FECHADA/PUBLICADA_OFICIAL/SUBSTITUÍDA/CANCELADA; simulação não gera obrigação pagável. Substituição explícita e atômica preserva anterior.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FOL03, FOL10. Vínculos relacionados: FOL03, FOL10.
Critério de aceite: CA-P1-01/02: versões e modelos distintos da mesma empresa/competência não permanecem oficiais simultaneamente, inclusive sob concorrência; troca autorizada e auditada publica sucessora e marca anterior SUBSTITUÍDA atomicamente. Falha conserva anterior oficial; histórico/documentos e pagamentos preservados. Ver governança no eixo RH/DP para os seis cenários.
Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FOL05 Migração controlada A B

Migrar A/B com competência de corte, modelo vigente, conciliação de saldos/acumulados, obrigações e pagamentos, aprovação e plano de retorno; bloquear dupla fonte por empresa/competência, preservar consulta e origem histórica.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FOL03, FOL04, FOL11. Vínculos relacionados: FOL03, FOL04, FOL02, FOL10, FOL11.
Critério de aceite: CA-P1-05: conciliar saldos/eventos/encargos/recibos e pagamentos antes da virada; recusar publicação paralela A/B por empresa/competência. Retorno e retificação não apagam nem recriam pagamentos; períodos anteriores e evidência de aceite permanecem acessíveis.
Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E5.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FOL06 Regras trabalhistas e tributárias versionadas

Registrar fonte normativa/contratual validada, vigência, empresa, rubrica, parâmetros, precisão/arredondamento e aprovador; não fixar alíquotas ou inferir legislação automaticamente.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, RF156. Vínculos relacionados: RH02, RF156.
Critério de aceite: Reproduzir uma competência fechada com sua versão; regra futura não muda passado; atualização retroativa exige processo e revisão DP/contábil/jurídica registrados.
Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FOL07 Holerites digitais e histórico de recibos

Emitir/importar holerite ligado à fonte oficial; versões, disponibilização, recibos de férias/13º/adiantamento, acesso individual e histórico de ciência. Documento digital e assinatura têm políticas distintas.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FOL04, RF167, RF168. Vínculos relacionados: FOL04, RF164, RF167, RF168, RH16.
Critério de aceite: Empregado consulta somente seus documentos; substituição não apaga versão anterior; fonte/competência/valores identificáveis; ciência ou assinatura validada conforme política sem exigir celular pessoal.
Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FOL08 Encargos e obrigações aplicáveis

Planejar INSS, IRRF, FGTS e demais encargos/obrigações conforme validação competente; identificar base, rubrica, competência, apuração, documento e integração disponível, sem alegar certificação legal.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FOL06, FOL04. Vínculos relacionados: FOL06, FOL04, FIN03.
Critério de aceite: DP/contábil/jurídico aprova referências e concilia base/valor/obrigação; ausência de confirmação técnica externa permanece bloqueio, não sucesso presumido.
Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FOL09 Adiantamentos e consignados

Origem, autorização, contrato/limite aplicável, parcelas, saldo e desconto vinculado à competência; retorno/cancelamento/ajuste auditáveis e sem duplicar obrigação.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, FOL06, FIN01. Vínculos relacionados: RH02, FOL06, FIN01.
Critério de aceite: Conferir empréstimo/adiantamento e descontos por competência; saldo e pagamentos reconciliados; parcela inválida ou repetida não é efetivada.
Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FOL10 Conferência aprovação fechamento e auditoria

Preparador, conferente e aprovador/publicador separados por política e alçada empresarial. RASCUNHO → CONFERIDA → APROVADA → FECHADA → PUBLICADA_OFICIAL; mudança invalida aprovação. Retificação cria sucessora e torna anterior SUBSTITUÍDA por publicação atômica, com ator, justificativa e histórico. Publicação não executa pagamento.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FOL06, RF148, RF150. Vínculos relacionados: RF148, RF150, FOL06.
Critério de aceite: CA-P1-02/06: sem alçada, autoaprovação indevida, sessão/CSRF inválidos ou empresa adulterada impedem publicação. Alteração invalida aprovação; reabertura/retificação auditáveis; falha interna e replay preservam fonte única. Versão não publicada pode ser cancelada com histórico; oficial só é substituída por sucessora aprovada.
Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FOL11 Integração folha financeiro e conciliação

Integrar obrigações por identidade estável empresa/competência/vínculo/tipo/rubrica/origem, sem usar versão para duplicar obrigação; versão é proveniência. Reprocessamento idempotente, conciliação importado/oficial/devido/pago e ajustes somente por diferença aprovada. Preservar pagamentos realizados; timeout exige consulta antes de retry.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FOL04, FOL10, FIN01, FIN11. Vínculos relacionados: FOL04, FOL10, FIN01, FIN11.
Critério de aceite: CA-P1-03/04: mesma origem reaplicada, mesmo sob outra chave, não duplica título/pagamento; chave com conteúdo diferente conflita. Pago 100 retificado para 120 admite só complemento aprovado 20; retificado para 80 preserva 100 pago e abre tratamento autorizado 20, sem apagar/recriar pagamento ou estornar automaticamente. Falha externa mantém pendência consultável.
Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E4.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.


## Benefícios

### BEN01 Vale transporte

Itinerário elegível, modalidade, tarifas e dias por vigência, opções validadas e desconto aplicável; não confundir passagem de folga com benefício regular.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH04, RH05, BEN06, BEN08. Vínculos relacionados: RH04, RH05, BEN06, BEN08.
Critério de aceite: Caso validado concilia dias/trechos/tarifas, elegibilidade e desconto; mudança de obra gera nova vigência e não sobrescreve recarga anterior.
Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### BEN02 Vale refeição

Política por empresa/vínculo/período, dias elegíveis, valor e descontos validados; manter separado da compra/solicitação de refeições operacionais RF124–RF126.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: BEN06, BEN07, BEN08. Vínculos relacionados: RH05, BEN06, BEN07, BEN08.
Critério de aceite: Conferir cálculo de dias, valor e ausência/férias com responsável DP; não duplicar VR e refeição operacional automaticamente.
Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### BEN03 Vale alimentação

Política versionada de elegibilidade e valor por competência, critérios e desconto aplicável; sem pressupor cálculo por dia ou isenção legal.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: BEN06, BEN08. Vínculos relacionados: BEN06, BEN08, RH02.
Critério de aceite: Caso validado reproduz valor/abatimento e vigência; reprocessar lote não duplica crédito e período antigo conserva parâmetros.
Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### BEN04 Assistência médica

Plano, titulares/dependentes quando autorizado, adesão, vigência, custo e coparticipação; minimizar exposição de dados de saúde, distinta de ASO/SST.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, BEN06, BEN12. Vínculos relacionados: RH02, BEN06, BEN12, RF156.
Critério de aceite: Conciliar pessoas elegíveis e fatura do provedor; financeiro consulta custo necessário, sem conteúdo clínico; exclusão/alteração conserva histórico autorizado.
Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### BEN05 Outros benefícios

Catálogo configurável de benefício, regra/fonte/versão, elegibilidade, período, fornecedor, custo e descontos aprovados; não presumir natureza tributária.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, BEN06, BEN08. Vínculos relacionados: RH02, BEN06, BEN08.
Critério de aceite: Cadastrar regra futura com aprovação competente; cálculo de referência verificável e integração financeira sem classificar incidência legal automaticamente.
Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### BEN06 Elegibilidade

Regras por empresa, vínculo, função/lotação e vigência, exceções justificadas e aprovadas; desligamento/afastamento avaliados proporcionalmente.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, RH03, RH04, FOL06. Vínculos relacionados: RH02, RH03, RH04, FOL06.
Critério de aceite: Dois casos por regra mostram elegível/ineligível com motivo; alteração retroativa segue ajuste autorizado e preserva benefício já concedido.
Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### BEN07 Dias previstos e calendário

Planejar dias elegíveis e conferir dias efetivos/autorizados, calendários e exceções; previsão não é crédito já entregue.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH05, RH06, BEN06. Vínculos relacionados: RH05, RH06, BEN06.
Critério de aceite: Calendário versionado distingue previsto/realizado e dias abatidos; simulação não envia recarga nem gera pagamento.
Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### BEN08 Tarifas e valores versionados

Tarifa/valor por benefício, fornecedor, trajeto/plano e vigência; base documental, moeda, precisão e aprovador; registrar mudanças sem recalcular passado silenciosamente.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: BEN06. Vínculos relacionados: BEN06.
Critério de aceite: Reproduzir lote antigo com valores históricos; tarifa nova afeta apenas vigência aprovada; divergência de fatura exige conferência.
Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### BEN09 Férias e afastamentos nos benefícios

Efeitos por tipo de benefício conforme política validada; distinguir previsão, crédito já enviado, devolução/ajuste e desconto autorizado.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH12, RH13, BEN06, BEN07. Vínculos relacionados: RH12, RH13, BEN06, BEN07.
Critério de aceite: Caso de férias/afastamento altera somente benefício/período permitido; ajuste de recarga exige evidência e não presume suspensão universal.
Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### BEN10 Recargas e lotes

Simular, conferir, aprovar e enviar lote com empresa/benefício/competência/versão; chave persistida, limites, retorno e estado de execução confirmados pelo provedor.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: BEN07, BEN08, BEN12, FIN11. Vínculos relacionados: BEN07, BEN08, BEN12, FIN11.
Critério de aceite: Envio repetido mantém um único lote/efeito; timeout vira pendência a consultar, sem reenvio cego; retorno parcial concilia por beneficiário.
Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### BEN11 Descontos

Evento vinculado à concessão/custo e política validada; limites, base, competência, autorização e destino folha/financeiro explícitos.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: BEN06, FOL06, FOL09. Vínculos relacionados: BEN06, FOL06, FOL09.
Critério de aceite: Conferir desconto aprovado na folha oficial e benefício de origem; correção referencia anterior e não desconta duas vezes.
Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### BEN12 Fornecedores e integrações de benefícios

Reutilizar fornecedor quando adequado; contrato, layouts/API, autenticação externa segregada, SLA, habilitação e retorno confirmados tecnicamente. Sem conector presumido.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RF008, DP09. Vínculos relacionados: RF008, DP09, BEN08.
Critério de aceite: Provedor confirma ambiente de homologação e contrato; lote de teste autorizado retorna protocolo reconciliável; dado sensível não aparece em logs.
Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### BEN13 Comprovantes e disponibilização

Protocolos de recarga/concessão/desconto e recibos por evento, versões, retenção e acesso individual; integrar portal sem criar fonte paralela.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RF167, RF168, BEN10. Vínculos relacionados: RF167, RF168, RH16, BEN10.
Critério de aceite: Documento liga beneficiário/lote/período/valor/fonte; trabalhador vê apenas seus registros; versões e ciência preservadas conforme política validada.
Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### BEN14 Conciliação de benefícios

Confrontar previsto, aprovado, enviado, creditado, faturado e pago; tratar divergências, rejeições, créditos/ajustes e responsável sem assumir sucesso por arquivo gerado.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: BEN10, BEN12, BEN13, FIN13. Vínculos relacionados: BEN10, BEN12, BEN13, FIN13.
Critério de aceite: Totais/lotes por beneficiário conferíveis; diferença abre pendência aprovada; duplicado não altera saldo nem título e fechamento guarda evidência.
Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.


## Financeiro

### FIN01 Contas a pagar e receber

Títulos com empresa, origem única, contraparte, documento, competência, vencimento, parcela e histórico; distinguir previsto, aprovado, pago/recebido e ajustado sem exclusão destrutiva.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: DP09, RF148, RF150. Vínculos relacionados: DP09, RF148, RF150.
Critério de aceite: Mesma origem não cria título duplicado; lançamento e ajuste são auditados/atômicos; posição por empresa/competência concilia com fontes.
Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FIN02 Agenda financeira

Vencimentos, prioridades, disponibilidade e programação; agendar não autoriza nem comprova pagamento; alteração relevante exige nova aprovação.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FIN01, FIN11. Vínculos relacionados: FIN01, FIN11.
Critério de aceite: Agenda mostra previsto/autorizado/executado/confirmado distintos; alteração de valor/destino revoga aprovação anterior conforme política.
Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FIN03 Fluxo de caixa

Saldo inicial verificado, entradas/saídas previstas e realizadas, contas e período; origem por título/movimento e revisão de premissas.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FIN01, FIN02, FIN13. Vínculos relacionados: FIN01, FIN02, FIN13.
Critério de aceite: Saldo inicial mais entradas menos saídas confere ao saldo final reconciliado; projeção identifica hipótese e não conta provisão como saída realizada.
Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FIN04 Custos operacionais

Método de valorização e classificação aprovado, origem de consumo/despesa, competência e ajustes; entrada física não é consumo/custo automaticamente.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RF063, FIN01. Vínculos relacionados: RF063, RF045, FIN01, FIN06.
Critério de aceite: Referências aprovadas distinguem entrada, consumo, despesa e pagamento; custo reprocessado conserva método/versão e evita dupla contagem.
Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FIN05 Custos por funcionário

Remuneração, encargos, benefícios e despesas autorizadas, com vigências/rateios, competência e acesso proporcional; não atribuir valor de custódia como gasto pessoal.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH04, FOL11, BEN14, FIN04. Vínculos relacionados: RH04, FOL11, BEN14, FIN04.
Critério de aceite: Somar componentes conciliados e ratear pelo método validado; histórico por pessoa não expõe holerite a leitor sem permissão específica.
Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FIN06 Custos por obra OS CC

Contexto real e rateio versionado de materiais, despesas, folha/benefícios; valores previstos/realizados separados e Centro corporativo preservado.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RF061, FIN04, RH04. Vínculos relacionados: RF061, RF063, FIN04, RH04.
Critério de aceite: Caso multiobra não duplica custo consolidado; rateios totalizam origem com ajuste de arredondamento explícito e origem congelada.
Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FIN07 Compras fornecedores e despesas

Pedido, recebimento físico, documento, obrigação e pagamento vinculados sem substituir agregados existentes; devolução/divergência afeta financeiro por regra aprovada, sem estorno físico automático.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RF034, RF036, RF081, FIN01, FIN04. Vínculos relacionados: RF034, RF036, RF081, FIN01, FIN04.
Critério de aceite: Compra parcialmente recebida pode ter obrigação conforme política documentada; conciliar origem/documento/título/pago sem atender solicitação nem consumir estoque por pagamento.
Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FIN08 Salários e encargos

Títulos de salários/encargos ligados à folha oficial e obrigações validadas, segregados por beneficiário/competência; informação bancária protegida.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FOL04, FOL08, FOL11, FIN11. Vínculos relacionados: FOL04, FOL08, FOL11, FIN11.
Critério de aceite: Uma obrigação por origem; total líquido/encargos corresponde à competência aprovada; correção não duplica remessa e pagamento requer alçada.
Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FIN09 Benefícios no financeiro

Compromissos/faturas/recargas/descontos separados, com conciliação por lote e origem; descontado do empregado não significa fatura já paga.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: BEN14, FIN01, FIN11. Vínculos relacionados: BEN14, BEN11, FIN01, FIN11.
Critério de aceite: Fatura/recarga/repasse reconciliados sem duplicar despesa; diferença ou devolução gera ajuste aprovado e histórico consultável.
Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FIN10 Provisões

Previsões/provisões por competência e critérios contábeis validados, reversão por novo evento e conexão à obrigação realizada; provisão não é pagamento.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH13, RH14, FOL08, FIN01. Vínculos relacionados: RH13, RH14, FOL08, FIN03.
Critério de aceite: Caso validado concilia abertura/constituição/reversão/realização; cálculo antigo mantém regra e não soma provisão e obrigação como duas despesas.
Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FIN11 Aprovações financeiras e segregação

Preparador, conferente e autorizador com alçadas por empresa/valor/origem; quem cadastra ou altera beneficiário não confirma sozinho pagamento quando política exige segregação. Aplicar autorização backend, CSRF, idempotência e trilha transacional.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RF057, RF148, RF150, DP09. Vínculos relacionados: RF057, RF148, RF150, DP09.
Critério de aceite: Usuário sem autoridade não aprova/efetiva; mudança de valor/conta exige nova conferência; replay conserva um efeito; rollback não deixa aprovação/pagamento parcial.
Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FIN12 Integração com instituições financeiras

Confirmar contrato, canal/API/layout, autenticação externa, ambiente, limites e retorno. Guardar credenciais externamente; remessa/arquivo não prova liquidação. Após timeout consultar estado antes de reenviar.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FIN11, FIN01. Vínculos relacionados: FIN11, FIN01, FIN13.
Critério de aceite: Banco confirma capacidade técnica e homologação autorizada; protocolo vincula origem/chave/lote; rejeição e retorno parcial preservam histórico; nenhum dinheiro real nesta etapa documental.
Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FIN13 Conciliação bancária

Extrato/retorno com origem e integridade, correlação de títulos/lotes, duplicados, tarifas e divergências; tolerâncias aprovadas e decisão humana para exceções.

Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FIN01, FIN12. Vínculos relacionados: FIN01, FIN12.
Critério de aceite: Importar duas vezes sem duplicar; fechar saldo contra extrato aprovado; diferença abre pendência e baixa só ocorre com evidência/autoridade apropriadas.
Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FIN14 Relatórios gerenciais

Posição a pagar/receber, caixa, despesas/custos, folha/benefícios e previsões por empresa/competência/contexto; filtros/permissões, fórmula/fonte e exportação segura.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FIN03, FIN05, FIN06, FIN13, RF060. Vínculos relacionados: FIN03, FIN05, FIN06, FIN13, RF060.
Critério de aceite: Reconciliar totais ao livro de origens/competências; impedir cruzamento entre empresas e acesso indevido a dados individuais; marcar não conciliado explicitamente.
Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

### FIN15 Previsão de despesas

Cenários de compras, salários/encargos/benefícios, operação e investimento com hipótese, período, versão e responsável; separar orçamento, provisão e obrigação efetiva.

Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.
Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FIN02, FIN03, FIN10, RF062. Vínculos relacionados: FIN02, FIN03, FIN10, RF062.
Critério de aceite: Cenário é reproduzível e tem origem/premissas; aprovado ou alterado não envia pagamento; comparar previsto versus realizado sem alegar economia sem baseline.
Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Prioridade proposta: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5.
Arquivos de implementação: nenhum para a capacidade nova; dependências existentes não a concluem. Testes existentes: nenhum específico entregue. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.


## Fases e gates do novo eixo

E0: titular do produto e responsáveis definem empresa/isolamento, regras/fontes/competência, modelo de folha, método de custo, integração confirmada, dados e validação. Gate: decisões registradas, sem conformidade presumida.
E1: identidade/vínculos/cargos/lotações/vigências e contratos sobre E0 e fundação F2-A; não exige F2 completo. F2-A entrega empresas/isolamento/contratos; F2-B integra vínculos depois de E1 e exige homologação conjunta. Gate: histórico imutável e acesso empresarial/individual comprovado; admissão/desligamento têm responsáveis e pendências. Ordem e integrações posteriores estão no roadmap integral.
E2: calendário/jornada/ponto, banco de horas, eventos e política de benefícios; fundações de títulos/alçadas. Gate: casos de referência aprovados e nenhuma dedução/crédito automático sem regra validada.
E3: ambos os modelos de folha, fonte oficial/fechamento, encargos, holerites/recibos e portal; benefícios calculáveis. Gate: referência DP/contábil concilia eventos, valores e competência; fonte única, privacidade e replay/rollback testados.
E4: folha/benefícios integrados ao financeiro, faturas, pagamentos e canais externos tecnicamente confirmados. Gate: segregação, homologação de retorno/duplicidade/timeout/concorrência e conciliação; pagamentos reais exigem autorização distinta.
E5: migração A/B, provisões/custos/caixa/relatórios completos e piloto. Gate: corte aprovado, saldos/obrigações conciliados, restore ensaiado, suporte e nova auditoria; nenhuma produção autorizada.

Gates transversais: RBAC e sessão/CSRF; acesso sensível/tenant; unidades/decimais; concorrência/idempotência/rollback; migrations homologadas; versões/retificação; performance; acessibilidade e responsividade; logs sem secrets; backup/restore e monitoramento. H2 e arquivos de testes não certificam legislação, banco/fornecedor real ou produção.
