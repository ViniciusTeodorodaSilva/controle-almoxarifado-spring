# Visão integral e roadmap comercial BES v1.5 candidata

Data de referência: 09/10/2026. Base de código: `f3c6a2229305a5e47ea2730f8b634ba027269ea0`, branch `feature/bes-frontend`. Documento de planejamento, sem autorização de implementação ou produção. A v1.4 continua a única mestre oficial até a auditoria e aprovação expressa da candidata v1.5.

## Produto e governança

A BES é produto próprio e independente, destinado à comercialização para a B&S Engenharia e outras empresas. B&S é potencial cliente, sem propriedade presumida do software. Titularidade, licenças, uso de marca, contratos e responsabilidades devem ser formalizados com orientação jurídica; este planejamento não é parecer jurídico. Não inventar logo nem apresentar a marca do potencial cliente como proprietária da plataforma.

Manter monólito modular, catálogo único e configuração externa. Personalização por cliente não deve introduzir regras exclusivas da B&S no código compartilhado. A política atual de leitura é global por permissão: não existe isolamento multiempresa, tenant ou acesso individual SST. Cadastro de cliente textual em Obra não é cadastro de empresa nem segregação de dados.

Requisitos RF001–RF218 e RNF001–RNF012 são preservados com redação original. Nenhuma definição posterior de RF foi encontrada nos documentos examinados. Decisões adicionais recebem identificadores documentais DP, e oportunidades PR; esses identificadores não renumeram nem ampliam automaticamente a série RF.

IMPLEMENTADO E VALIDADO significa cobertura do comportamento descrito no escopo atual com evidência automatizada anterior e código correspondente, sem homologação operacional ou de produção. PARCIALMENTE IMPLEMENTADO exige identificar exatamente o entregue e o ausente; normaliza a etiqueta anterior IMPLEMENTADO PARCIALMENTE, sem reclassificação dos 73 RF já parciais. RF035 é a única reclassificação: passa de planejado para parcial, preservando COMPRADO/compra externa como lacuna. Totais: 37 validados, 74 parciais, 103 planejados e 4 dependentes; 218 RF.

PLANEJADO não significa implementação iniciada. EM DESENVOLVIMENTO exige evidência de trabalho em curso; nenhuma foi inferida de arquitetura preparada. DEPENDENTE DE VALIDAÇÃO destaca definição DP/contábil/SST/jurídica/técnica ainda necessária. PROPOSTA NOVA depende de aprovação específica. Requisito original aprovado, decisão de produto incluída no planejamento e proposta PR são categorias distintas; nenhuma autorização documental aprova código, cálculo legal, integração ou pagamento.

## Regras de domínio que atravessam todos os módulos

- Produto é cadastro; Estoque é saldo por Produto e local. Ativo individual tem identidade/custódia e não altera saldo de Produto.
- Pedido é compromisso de compra; recebimento físico gera ENTRADA. Aprovação não movimenta estoque; atendimento físico gera SAÍDA. Recebimento não atende automaticamente solicitação.
- Responsável operacional é funcionário; ator autenticado é usuário. Auditoria registra ambos quando aplicável, dentro da transação crítica.
- EPI usa Produto/Estoque e eventos imutáveis, sem saldo paralelo. CA cadastrado, validade de CA e validade física são distintos. Cadastro não certifica aptidão legal.
- Obra/CC/OS são contexto; snapshots históricos não são reescritos por mudanças de cadastro. Entrada de compra não é custo consumido da obra. Valorização exige método aprovado.
- Idempotência, locks ordenados, rollback, autorização HTTP/service e CSRF são critérios por operação, não alegações globais por existência de classes. Transferência quantitativa ainda não possui chave persistida.
- QR identifica ou abre registro; não concede autorização. Aceite exige identidade, evidência e política proporcional. Dano/perda não atribuem culpa automaticamente.
- Mudança de obra não duplica ficha EPI nem caixa se a custódia não mudou. Mudança de função deve abrir nova vigência e preservar anterior; isso ainda é planejamento.
- Digital por padrão; papel quando necessário à lei, contrato ou operação. Não exigir celular pessoal; prever dispositivo corporativo e contingência acessível.

## Mapa integral de capacidades

O inventário técnico e a matriz individual detalham os arquivos e critérios de cada RF. Estes grupos consolidam todos os módulos pedidos e os adicionais encontrados na mestre.

| Grupo | Estado real e limite principal | Dependência para completar |
|---|---|---|
| Administração e identidade | Usuários, quatro perfis, sessão/CSRF e auditoria entregues no núcleo; recuperação de senha e permissões configuráveis incompletas | Política por cliente, escopo de dados e processos de administração |
| Empresas, unidades e departamentos | Sem agregado empresa/tenant; locais e cliente textual não substituem isso | Decisão de implantação, identidade e isolamento |
| Funcionários e vínculos | Nome, matrícula e função; sem setores, contatos, vigências, cargos/vínculos completos | Cadastro e histórico ocupacional definidos |
| Catálogo | Produto/categoria/unidade e busca implementados; texto técnico não é atributos configuráveis | Governança de cadastro, provisórios e conversões |
| Estoque e almoxarifados | Saldo por local, limites e reposição; sem inventário físico/cíclico completo | Precisão DECIMAL, ajustes e reconciliação |
| Movimentações | Entradas/saídas/transferências e protocolos de locks; sem estorno geral | Política de reversão, idempotência quantitativa e homologação |
| Solicitações | Aprovação separada do atendimento, parcial/total, faltas e lista de separação | Hierarquia configurável, retirante terceiro, notificações e devolução de material |
| Compras e fornecedores | Pedido único, alocações, recebimento parcial e histórico de preços | Cotações, divergências completas, NF/XML e política de cancelamento/estorno |
| Obras, OS e centros de custo | Cadastros e contexto congelado; resumos quantitativos | Orçamento, valorização e indicadores financeiros/prázo completos |
| Ferramentas e ativos | Custódia, empréstimo, devolução, transferências com chegada, condição e baixa | Reservas, anexos, ocorrências e manutenção |
| EPI operacional | Entregas/substituições/devoluções/descarte, CA e histórico por funcionário | Cadeia por lote, SST, acesso individual e validação humana |
| Prontuário EPI único | Ficha operacional atual reúne posse/histórico; prontuário com vigências é planejado | Função histórica, política SST e identidade |
| Matriz EPI por função/risco | Não implementada | Matriz versionada, vigências e validação SST |
| Caixas individuais | Não implementadas | Composição real, custódia, quantitativos/ativos e aceite |
| Checklists e mobilização | Inspeção simples de ativo não é motor versionado/checklist | Modelos, estados, evidências, revisão e faltas integradas |
| Termos e aceites | Confirmação atual do operador; sem aceite jurídico do trabalhador | SST/jurídico, identidade, integridade, retenção e contingência |
| Documentos operacionais | Componentes, fichas e impressão/PDF navegador entregues parcialmente | Motor PDF, identidade por cliente e QR protegido |
| Gestão documental e anexos | Central, fotos e retenção não implementadas | Storage, antivírus, metadados, acesso, versão e eliminação controlada |
| Alertas e notificações | Alertas em tela de estoque/ativos/EPI; sem central/envio/escalonamento | Regras, canal, fila, entregas, preferências e privacidade |
| Relatórios e BI | Dashboard e resumos quantitativos; sem análise financeira global/Excel | Métricas, valorização, filtros completos e exportações seguras |
| Inventário móvel e QR | Não implementado; deep link não é QR | Autenticação, conferência física, divergências e ajuste aprovado |
| Manutenção e ciclo de vida | Condição/baixa e próxima inspeção; manutenção não entregue | Preventiva/corretiva, certificados, garantia e calibração |
| Offline e sincronização | Não implementado; SPA responsiva não é offline | Operações elegíveis, identidade, expiração, reconciliação e conflitos |
| Mudanças e desligamento | Snapshots preservam contexto de eventos; sem processo integrado de desligamento | Vigências, custódia, pendências e revogação de acesso |
| SST e treinamentos | Sem treinamentos/ASO/PGR/PCMSO implementados | Responsável técnico, confidencialidade, retenção e validação |
| Integrações e importação | API REST interna; sem integração ERP/importação assistida | Contratos, versionamento, autorização, deduplicação e conciliação |
| Administração comercial | Licenças, planos, cobrança, suporte e contratos não implementados | Política comercial, implantação e continuidade |
| Demo e venda | Planejamento; launcher H2 não é Demo Mode comercial | Homologação, massa fictícia, roteiro e materiais reais |
| Cabos, bobinas e sobras | Estoque quantitativo genérico não controla segmento/bobina | Identidade, unidade/medida, aproveitamento e rastreio |
| Frota e logística | Transferência não é viagem/entrega logística | Veículo, motorista, carga, chegada, coleta e custos |
| Alojamentos | Não implementados | Contratos, ocupação, despesas e contexto |
| Refeições | Não implementadas | Planejamento, solicitação, presença autorizada e custos |
| Mobilidade, viagens e folga | Não implementadas | Tarifas/vigências, trajetos, comprovantes e privacidade |
| Pessoas, feedback e carreira | Não implementados | Acesso RH, participação do funcionário e decisão humana |
| Diário de obra e IA assistiva | Não implementados | Dados estruturados, versões e aprovação humana |
| Engenharia de campo | Não implementada | Parâmetros validados, memória de cálculo e responsabilidade técnica |
| Canal de melhoria e segurança | Não implementado | Confidencialidade/anonimato real e fluxo de ação |
| Gestão RH e departamento pessoal | RH01–RH16 planejados/dependentes; cadastro atual não é DP completo | Vínculos, vigências, jornadas/ponto/horas/DSR/adicionais, faltas, férias/afastamentos/13º, admissão/desligamento e portal |
| Folha nativa ou externa por empresa | FOL01–FOL11 planejados/dependentes; nenhum motor/integração de folha entregue | Fonte única por competência, migração A/B, regras versionadas, encargos, holerites/recibos, fechamento e conciliação |
| Benefícios por empresa | BEN01–BEN14 planejados/dependentes; refeições/passagens operacionais não são VR/VT | Elegibilidade, calendário/tarifas, férias/afastamentos, recargas/descontos, fornecedores, comprovantes e conciliação |
| Financeiro integrado | FIN01–FIN15 planejados/dependentes; preço de compra não é tesouraria | Títulos/agenda/caixa, salários/encargos/benefícios, provisões/custos, alçadas/segregação, banco/conciliação e relatórios |

## Conflitos e lacunas encontrados

1. A v1.4 cita JWT; a baseline atual e o código usam sessão/CSRF. A candidata descreve a decisão vigente; não migra autenticação nem remove o texto histórico.
2. O próximo passo histórico de aprovação de solicitação está ultrapassado. Aprovação, separação e atendimento já existem; não recomeçar esse bloco.
3. Textos antigos dos Blocos 1–4 dizem que compras, contexto ou autenticação não existem. São estados históricos; a matriz consolida os incrementos posteriores sem apagar relatórios.
4. PostgreSQL é alvo documentado; runtime atual tem driver MySQL e scripts manuais para os dois bancos. Não há migração automática homologada nem autorização para banco real.
5. Identidade B&S/BES em RF206 deve ser preservada no texto, mas personalização comercial e permissão de uso de marca exigem decisão. Não presumir propriedade do potencial cliente.
6. RF053/RF148, RF055/RF033, RF078/RF202 e RF051/RF164–RF168 têm sobreposição funcional. Preservar IDs; reutilizar componentes/serviços, sem contar duas vezes o valor de uma mesma entrega.
7. Print/PDF navegador não equivale a motor PDF, anexos, QR, assinatura eletrônica ou central documental. Dados para um fluxo futuro não o concluem.
8. Cargo/função textual atual não mantém vigências; ficha EPI atual não comprova prontuário jurídico/SST completo. Caixa individual não é lista de saldo nem agrupamento fictício de ativos.
9. Estoque legado usa Double; eventos EPI usam DECIMAL(19,6). Evolução exige migração coordenada de API, unidades, saldos e arredondamento.
10. Não há evidência de tenant, licenciamento, Docker/CI/CD operacional, backup/restore homologado, observabilidade completa ou produção. Propostas comerciais dependem desses gates.

11. A auditoria documental D01/D08 identificou ausência do novo eixo RH/DP/Folha/Benefícios/Financeiro. A autorização F02 determina sua inclusão documental detalhada, sem implementação. São 56 capacidades RH/FOL/BEN/FIN, separadas da série RF e descritas integralmente em `bes-eixo-rh-dp-folha-beneficios-financeiro-v1_5.md` e na matriz/JSON. Não são RF removidos da v1.4 nem sugestão PR já aprovada para execução.

12. A auditoria D02 comprovou estados de compra existentes em RF035. O parcial reconhece RASCUNHO, AGUARDANDO_APROVACAO, APROVADO, PARCIALMENTE_RECEBIDO, RECEBIDO e CANCELADO; não equipara aprovação a COMPRADO. Dependências agora distinguem pré-requisitos e vínculos, sem auto-referências no campo corrente; critérios históricos continuam preservados como rastreabilidade.

## Novas oportunidades sujeitas a aprovação

Cada PR permanece PROPOSTA NOVA, mesmo quando aprofunda um RF já definido. Aprovar o recorte e os critérios antes de implementar; vínculo abaixo não autoriza duplicar entidades.

| ID | Oportunidade | Vínculo existente | Valor e dependência | Prioridade proposta |
|---|---|---|---|---|
| PR01 | Não conformidades com plano de ação, responsável e prazo | RF106/RF109/RF178 | Fechar ocorrências; depende de evidências e revisão humana | P1 |
| PR02 | Inspeções programadas e manutenção com alertas | RF021–RF025/RF176 | Reduzir esquecimento; política de periodicidade | P1 |
| PR03 | Inventário rotativo com QR | RF095–RF098/RF212 | Conferência frequente; ajuste autorizado e acesso protegido | P1 |
| PR04 | Kits operacionais e reservas | RF020/RF158–RF163 | Planejar mobilização; distinguir previsão, reserva e saldo | P2 |
| PR05 | Lote, série e validade onde necessário | RF006/RF093/RF099/RF104 | Rastreio físico; granularidade/unidades aprovadas | P1 |
| PR06 | Avarias/perdas com revisão humana | RF026–RF029/RF092/RF173 | Evidência e tratamento justo; sem culpa automática | P1 |
| PR07 | Garantias, certificados e calibração | RF022/RF051/RF088/RF164 | Ciclo de vida; responsável técnico e retenção | P2 |
| PR08 | Aprovações por valor, obra ou perfil | RF073/RF074 | Política configurável; limites, substitutos e segregação | P1 |
| PR09 | Portal simplificado da obra | RF069/RF070/RF214 | Menor esforço em campo; escopo de acesso | P2 |
| PR10 | Dashboard multiunidade e custos por obra | RF038/RF041/RF045/RF063 | Visão executiva; isolamento e valorização | P2 |
| PR11 | Histórico único de responsabilidades | RF018/RF092/RF128 | Mudanças/desligamento; vínculos e custódia | P1 |
| PR12 | Motor compartilhado de documentos, checklists, notificações e aceites | RF059/RF164–RF169/RF205–RF218 | Consistência; fronteiras modulares e permissões | P1 |
| PR13 | Offline elegível com reconciliação | RF070/RF214/RNF005 | Continuidade em campo; validade de sessão e conflitos | P2 |
| PR14 | Auditoria exportável e conformidade | RF053/RF060/RF148 | Conferência por auditor; política de dados/retenção | P1 |
| PR15 | Importação assistida de planilhas | RF193/RF198/RF199 | Cadastro inicial; dry-run, validação e deduplicação | P1 |
| PR16 | Busca global com permissões | RF055/RF090/RF164 | Localização de registros; não revelar dados proibidos | P2 |
| PR17 | Templates configuráveis | RF206/RF216 | Identidade por cliente; versão, compatibilidade e governança | P2 |
| PR18 | Acessibilidade real e UX mobile de campo | RF157/RF214/RNF005 | Uso inclusivo; touch, leitor de tela e operadores | P1 |
| PR19 | Observabilidade e suporte técnico | RF153/RNF009 | Diagnóstico/continuidade; logs sem secrets e gestão de incidentes | P0 de liberação |
| PR20 | APIs e integração ERP/financeiro | RF045/RF084/RF127 | Interoperabilidade; contrato, consentimento e conciliação | P2 |
| PR21 | Métricas verificáveis de digitalização | RF038/RF040/RNF007 | Demonstrar resultado; baseline, método e privacidade | P2 |

## Implantação isolada e SaaS

| Critério | Instalação isolada por cliente | SaaS multiempresa |
|---|---|---|
| Isolamento | Instância e banco separados; exige validação de credenciais/backup por cliente | Tenant em todas as fronteiras ou bancos segregados; exige política explícita e testes de vazamento |
| Custo | Cresce com instâncias, suporte, atualização e backups; valores ainda não orçados | Infra compartilhada possível; investimento inicial alto em isolamento, billing e operação |
| Atualizações | Orquestração de versões por cliente e compatibilidade de schema | Atualização central com risco transversal; limites de recursos e rollout controlado |
| Migração | Exportar/restaurar dados e configurações por cliente | Migração/reconciliação de tenant, IDs e escopo de acesso; não apenas coluna empresaId |
| Continuidade | Contrato define responsabilidade por infraestrutura, backup e recuperação | Operador SaaS responde por disponibilidade, incidentes e isolamento |
| Complexidade | Menor mudança inicial de domínio; operação pode ser mais trabalhosa | Maior complexidade de produto/segurança; não está pronta no código atual |

Proposta de decisão: estudar piloto isolado como primeiro recorte, sem declarar escolha aprovada. A decisão exige orçamento de infraestrutura, suporte, restore, atualização e saída de contrato. Não estimar preço/custo sem dados. SaaS permanece opção da visão integral, dependente de isolamento verificável. Manter monólito modular nas duas opções até necessidade real de mudança arquitetural.

Configuração por cliente deverá abranger identidade visual autorizada, unidades/departamentos, políticas, permissões, documentos, dados de abertura, timezone, canais e termos. Administrar licenças/planos/contratos exige regras de expiração, cobrança, acesso e suporte. Suspensão deve preservar integridade e permitir acesso/exportação conforme contrato; não apagar dados ou bloquear recuperação silenciosamente. Encerramento inclui exportação em formato acordado, retenção/base legal, confirmação humana, eliminação auditável quando cabível e plano de continuidade. SLA/SLO, RPO/RTO, horários/canais de suporte e responsabilidades ainda dependem de decisão e contratação.

Configuração empresarial também deverá escolher folha A nativa BES ou B externa integrada por vigência, com fonte oficial única por competência e migração controlada. Simulação/comparação paralela não gera título ou pagamento. A empresa mantém acesso aos holerites/recibos históricos após troca de modelo. Layout/API bancário, ponto, folha e benefícios exigem confirmação técnica do provedor e homologação autorizada; nenhum conector está presumido disponível.

## Roadmap integral por gates

Prioridades são propostas de planejamento, não autorização de bloco. Fases não são cronograma nem promessa de prazo. Cada fase termina com auditoria, documentação, critérios de aceite e checkpoint autorizado. Segurança, privacidade, testes e operação acompanham todas as fases.

| Fase | Entrega planejada | Dependências | Gate de conclusão |
|---|---|---|---|
| F0 — Evidência e decisões | Auditar v1.5, resolver conflitos de fonte/arquitetura e definir piloto/escopo comercial | Inventário e responsáveis | Candidata aprovada; matriz sem perda de RF; recorte de cada bloco autorizado |
| F1 — Confiabilidade do núcleo | Investigar risco B5, homologar banco escolhido, precisão/idempotência e reversões controladas | F0, massa/schema e políticas | Concorrência/rollback/segurança no banco homologado; recuperação sem duplicações |
| F2 — Produto por cliente e pessoas | F2-A: empresas/unidades/departamentos, identidade, isolamento e contratos de dados; F2-B: integrar vínculos/vigências entregues em E1 | F2-A exige F0/F1 e política de implantação; E1 exige F2-A/E0; F2-B exige F2-A/E1 | F2-A: isolamento/autorizações provados; F2-B: histórico de função/acesso integrado e homologação conjunta com E1. F2 completo exige ambos |
| F3 — Documentos e operação em campo | Central/anexos, template/QR, termos/aceites, checklists, prontuário EPI e caixas | F1/F2-B, SST/jurídico, storage | Rastreabilidade e acesso; QR sem bypass; validação com operador e contingência |
| F4 — Inventário e ciclo de vida | Inventário físico/cíclico, mobilização/reservas, lotes, bobinas/sobras, manutenção/calibração | F1/F3 e políticas | Conferência/ajuste aprovado; custódia/estoque separados; histórico de manutenção |
| F5 — Compras e custos | Núcleo F5: cotações, NF/XML, divergências/reversões, orçamento OS e valorização; integração posterior de custos de folha/benefícios | Núcleo: F1/F2-B/F3 e método de custo; integração de folha/benefícios: núcleo F5 e E5 concluídos | Recebimento separado de consumo; núcleo conferível sem folha; custos integrados somente após E4/E5 conciliados e homologação conjunta |
| F6 — Administração de campo | Frota/logística, alojamentos, refeições, viagens/folgas/deslocamentos | F2-B/F3 e núcleo F5; custos de folha/benefícios só após integração F5/E5 | Custos/contexto rastreáveis; vigências e documentos autorizados |
| F7 — SST e pessoas | Treinamentos/ASO, canal de melhoria, alocação, feedback/PDI/carreira | F2-B/F3 e responsáveis técnicos/RH | Acesso sensível/anonimato quando oferecido; nenhuma decisão automática de punição |
| F8 — Integrações e inteligência | ERP/ponto/importações, BI/relatórios, diário e IA assistiva | Dados homologados de F1–F7; integração de folha/financeiro após E5; contratos técnicos | Confirmação humana, métricas verificáveis e consultas com autorização; fonte/escopo e edição explícitos |
| F9 — Engenharia e offline | Calculadoras/biblioteca/isométrico/lista de fabricação; offline apenas elegível | Validação técnica e protocolo de conflitos | Memória de cálculo verificável; reconciliação segura; operações críticas delimitadas |
| F10 — Operação comercial e lançamento | Planos/licenças/suporte, demo, onboarding, materiais e piloto | Gates F0–F9 e E0–E5 aplicáveis à edição, segurança e infraestrutura | Restore ensaiado, homologação/piloto, suporte/contratos e saída de dados aprovados; capacidades omitidas da edição permanecem no roadmap |

O mapa fase por RF está na matriz; a cobertura parcial pode atravessar fases. A fase F9 não impõe offline a cálculos ou estoque indistintamente. A versão comercial pode ter recorte explícito e recursos futuros declarados, preservando o roadmap integral. Uma edição que prometa todos os módulos da mestre só será completa quando cada RF/RNF aplicável alcançar seus critérios e homologação; o núcleo atual não atende essa definição.

## Sequência do eixo RH DP Folha Benefícios Financeiro

As fases E complementam F0–F10; não iniciam Bloco 8 nem autorizam implementação. F2 mantém seu escopo integral em dois incrementos: F2-A entrega a fundação empresarial e contratos de dados sem exigir RH completo; E1 entrega vínculos/cargos/lotações sobre F2-A; F2-B integra esses vínculos ao produto. E1 não depende de F2 completo e F2-A não depende de E1. E0 define políticas/contratos, não exige motores F5/F8 prontos. O núcleo de compras/custos F5 funciona sem folha; sua integração posterior usa E4/E5 concluídos. F7 não substitui DP; F8 consome fontes homologadas, sem ser pré-requisito do núcleo E. F10 exige os gates aplicáveis à edição vendida.

| Fase | Capacidades e entrega planejada | Pré-requisitos | Gate de conclusão |
|---|---|---|---|
| E0 | Empresa/isolamento, modelo A/B, fonte/competência, regras, custo, contratos e validação | F0, responsáveis DP/contábil/jurídico/segurança e fornecedores | Fontes, vigências, critérios e integrações possíveis confirmados; sem legislação ou conector presumidos |
| E1 | RH01–RH04, identidade/vínculos/cargos/lotações e contratos | E0, F2-A e política de dados; não exige F2-B/F2 completo | Histórico preservado, acesso empresarial/individual e decisões registradas; contratos prontos para integração F2-B |
| E2 | Jornada/ponto/horas, ocorrências, regras de benefícios e fundações financeiras | E1, regras versionadas e política de ajuste | Casos DP validados; calendário/eventos conferíveis; nenhuma dedução/crédito sem aprovação |
| E3 | Folha A/B, fonte única, conferência/fechamento, encargos, holerites/recibos e portal | E2, competência e contratos confirmados | Referências oficiais conciliadas, versões/retificação, privacidade e replay/rollback |
| E4 | Benefícios/folha no financeiro, títulos/alçadas, canais externos e conciliação | E3 e contrato técnico do banco/provedor | Segregação de funções, retornos/timeout/duplicidade/concorrência homologados; dinheiro real exige autorização distinta |
| E5 | Migração A/B, provisões/custos/caixa/relatórios e piloto | E4, saldos conferidos e restore ensaiado | Corte/retorno aprovados, acumulados e obrigações reconciliados, suporte e nova auditoria |

Os pré-requisitos técnicos por capacidade estão no JSON/matriz; vínculos relacionados não são uma ordem de execução. As políticas/documentos de fonte podem preceder o motor executável: fonte única e alçada são desenho obrigatório, não prova de folha já entregue.

Ordem executável de referência: F0 → F1 → F2-A → E0 → E1 → F2-B → F3 → F4 → núcleo F5 → F6 → F7 → E2 → E3 → E4 → E5 → integração de custos F5/E5 → F8 → F9 → F10. É uma ordenação possível, não cronograma obrigatório: E0 pode anteceder F2-A; E2 e ramos independentes podem avançar após seus gates. Fases F2/F5 são agrupadores, não novos RF nem redução de escopo.

F2-A entrega identidade empresarial, configuração/isolamento, autoridades por empresa e contrato de referência de pessoa/vínculo com IDs, vigências, snapshots e permissões, sem inventar histórico funcional. E1 entrega RH01–RH04 e seus contratos, regras de sobreposição e históricos. F2-B adapta os consumidores ao vínculo vigente e histórico; homologação conjunta verifica mudança de função/lotações, isolamento, preservação de eventos e acesso. Contratos e seus casos podem ser desenhados antes dos produtores; a integração exige produtores e consumidores entregues.

Os pré-requisitos obrigatórios são os das colunas Dependências/Pré-requisitos e do grafo explicitado; vínculos relacionados são referências de integração, não novas arestas de precedência. Modelos, fontes e contratos de E0 podem ser aprovados antes de motores de folha, tesouraria ou BI. Núcleo F5 não exige E5; somente o incremento de custos de folha/benefícios o exige. F6 pode operar sobre custos do núcleo F5 e incorpora esses custos posteriores após homologação; F8 não bloqueia E1–E5 por mera menção a integração.

Verificação da sequência: E0 depende de F0; E1 de E0/F2-A; E2 de E1; E3 de E2; E4 de E3; E5 de E4. F2-A depende de F0/F1; F2-B de F2-A/E1. Integração de custos F5/E5 depende de núcleo F5/E5. F8 exige produtores F1–F7 e E5 quando prometer dados financeiros; F10 exige as fases/capacidades prometidas. Nenhuma homologação conjunta adiciona dependência reversa ao núcleo que a precede.

## Critérios da versão comercial completa

Definir uma ficha de edição com módulos incluídos, RFs cobertos, limites, clientes-alvo, implantação e suporte. Para a primeira edição operacional proposta: identidade/acesso, funcionários/vínculos, catálogo/estoque/inventário, solicitações/compras/recebimento, contexto Obra/OS/CC, ativos/custódia, EPI/prontuário/caixas no recorte aprovado, documentos/aceites adequados, relatórios/alertas e administração comercial. Inclusão é planejamento, não venda de recurso ausente; ampliar para os demais módulos por edições/fases sem abandonar os RFs.

Uma edição que inclua RH/DP/folha/benefícios/financeiro deve declarar modelo A/B, capacidades RH/FOL/BEN/FIN cobertas, fornecedores confirmados, regras/vigências validadas, fonte oficial por competência, alçadas/segregação, privacidade individual, holerites/recibos e conciliação. “Completa” exige o aceite de cada capacidade prometida, legislação/obrigações validadas e homologação técnica/operacional. Não vender preço de compra, portal de obra ou integração genérica como folha/tesouraria disponíveis.

Gate funcional: jornadas completas por perfil, regras e migrações no banco homologado, dados de abertura conferidos, auditoria/rollback/idempotência/concorrência, documentos e rastreabilidade, usuário responsável validando aceite. Gate de experiência: 390/768/1440, teclado/Escape/foco, touch real, leitor de tela/zoom, erros/vazios/loading/pendências, uso em campo e acessibilidade contratada. Gate operacional: backup automático e restore ensaiado, monitoramento/incidentes, atualização reversível, suporte/SLA, treinamento e piloto paralelo. Gate comercial/jurídico: titularidade/licença/marca/contrato, privacy/retention/LGPD, responsabilidades SST e política de aceite, suspensão/exportação/encerramento acordados. Nenhum gate é declarado concluído por esta execução.

## Qualidade por módulo

Cada registro da matriz contém critério próprio; os critérios transversais devem ser aplicados ao recorte autorizado. Testes existentes não são homologação real. Suite verde H2 prova os cenários automatizados, não dialect/locks reais, privacidade jurídica ou funcionamento em dispositivo de campo.

| Família | Critérios e testes necessários |
|---|---|
| Cadastros/acesso | Unicidade/validação, RBAC positivo/negativo HTTP/service, sessão/CSRF/revogação, DTOs e privacidade |
| Estoque/compra/demanda | Quantidades/unidades, estados, concorrência, rollback/auditoria, replay/duplicidade, migração e precisão |
| Contexto/custódia | Imutabilidade de vínculos, snapshots, origem/fechamento único, locks na ordem definida, baixa/condição/localização |
| SST/pessoas/aceites | Vigências, acesso sensível, identidade/evento/termo, retenção, contingência e validação SST/jurídica; sem julgamento automático |
| Documentos/QR/anexos | Origem estruturada, permissão na busca/abertura/download, versões, integridade, arquivos inválidos e retenção |
| Offline/integrações | Idempotência persistida, reconciliar conflitos/erros parciais, expiração/revogação, retry controlado, contratos e deduplicação |
| Cálculo/BI/IA | Valores de referência, método aprovado, memória/fonte, precisão, performance e revisão humana |
| Comercial/operação | Isolamento, billing/licenças/suspensão segura, exportação/encerramento, atualização, backup/restore e monitoramento |
| RH/DP e folha | Vigências/contratos, jornada/eventos, cálculo de referência validado, regras versionadas, fonte única por competência, fechamento/retificação e holerites privados |
| Benefícios | Elegibilidade/dias/tarifas vigentes, recargas/descontos autorizados, protocolo/retorno/duplicidade e conciliação por beneficiário/lote |
| Financeiro/banco | Origem única do título, alçadas/segregação, pagamento idempotente, timeout consultável, retorno parcial, extrato/conciliado e método de custo aprovado |

Metas numéricas de performance, disponibilidade, volume, SLA/RPO/RTO e acessibilidade precisam ser aprovadas com base na operação; não inventadas aqui. Logs não devem conter senha, token, sessão ou dados pessoais desnecessários. LGPD/biometria/retenção são decisões dependentes de validação, não conformidade presumida.

## Experiência premium e comercialização

Preservar sidebar escura, azul principal, identidade industrial e densidade informacional. Usar logo somente com asset oficial/autorizado. Mapear jornadas: solicitar → aprovar → separar → atender parcialmente → comprar faltante → receber → atender; expedir ativo → chegar → emprestar → devolver → inspecionar; configurar EPI → entregar → substituir/devolver → histórico; conferir inventário → revisar divergência → ajustar; mudar obra/função → revisar vigência/custódia; desligar → reconciliar pendências → revogar acesso. Jornadas futuras não são telas já entregues.

Em cada etapa incluir loading, vazio, erro recuperável, conflito, permissões, operação ocupada, resposta incerta, confirmação e conclusão verificável. Não criar atalhos que bypassam autorização. A02 da auditoria premium (resize sem link ativo) segue pendente; touch/leitor de tela/zoom/impressão física e operadores precisam de validação real.

Após estabilização/homologação, preparar Demo Mode isolado com massa fictícia, reset seguro de demonstração e nenhum dado de cliente. Criar roteiro problema → fluxo → resultado, vídeo principal e versões curtas, landing page, screenshots reais, QR/links apropriados, FAQ/tutoriais/onboarding, proposta comercial e treinamento B2B. Separar produto disponível de roadmap. Medir digitalização/eficiência com baseline e método antes de afirmar economia. Rastreamento precisa de finalidade/política; não expor sessões, secrets ou dados reais.

## Riscos e decisões ainda necessárias

HTTP 500 histórico B5 continua causa não identificada/não reproduzida. O mecanismo P0 H2 foi reproduzido/corrigido, mas vínculo ao incidente histórico segue inferencial. Permanecem homologação de banco/schema/locks, precisão Double, estornos/idempotência não universais, isolamento por cliente/obra/indivíduo, dependências/backend SCA, rotação histórica, HTTPS, backup/restore, observabilidade, touch/leitor de tela/zoom/impressão física, validação de operador e limites jurídicos/SST. Produção não autorizada.

Decisões pendentes: opção de implantação, escopo da edição/piloto, modelo empresa/escopo de dados, método de custo, políticas de estoque/retorno/reserva/estorno, identidade/aceite, retenção, governança de função/matriz, níveis de suporte/licença/saída, parâmetros técnicos de engenharia e critérios de dados/performance. Não resolvidas por este documento.

Riscos explícitos do eixo novo: legislação trabalhista/tributária e instrumentos aplicáveis, encargos/obrigações por competência, atualização/retificação de regras, migração A/B e dupla oficialização/pagamento, dados de saúde/folha e bancários, elegibilidade/recarga, alçadas/segregação e integrações bancárias/fornecedores ainda não confirmadas. Validação DP/contábil/jurídica/SST e homologação técnica têm responsáveis e gates E0–E5, sem alíquotas, fórmulas ou conformidade inventadas.

## Changelog da candidata

Consolidada visão de produto independente; preservados 218 RF e 12 RNF; incorporadas decisões futuras com DP; propostas PR separadas; matriz individual e inventário técnico atual; conflitos históricos explicitados; fases e gates comerciais definidos. v1.4 permanece byte a byte intacta e mestre oficial. Nenhum código, endpoint, schema ou módulo implementado nesta execução.

Correções autorizadas F02 após D01–D08: RF035 parcial e contagens 37/74/103/4; etiqueta parcial normalizada; fonte documental durável; pré-requisitos/vínculos e critérios específicos; 56 capacidades do eixo novo em quatro famílias; gates E0–E5 e riscos legais/bancários. A candidata DOCX recebe sumário/navegação, numeração e identificação de seções; revisão visual depende de ferramenta disponível e não é aprovada por integridade estrutural.
