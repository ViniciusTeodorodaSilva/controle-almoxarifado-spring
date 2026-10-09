# Auditoria independente documental BES v1.5

Data: 09/10/2026. Base: `feature/bes-frontend`, HEAD `f3c6a2229305a5e47ea2730f8b634ba027269ea0`.

**Resultado: REQUER CORREÇÕES ANTES DO COMMIT.** A candidata preserva integralmente os requisitos originais, mas não contempla explicitamente o novo eixo RH/DP/Folha/Benefícios/Financeiro solicitado nesta auditoria. Há também erro comprovado de classificação do RF035. A revisão visual permanece sem evidência de renderização. Nenhuma correção foi aplicada aos documentos auditados.

## Escopo e método

Auditoria documental, com leitura direta dos pacotes DOCX, inventário JSON, matriz completa, roadmap, decisões, referências em README/AGENTS e documentação técnica dos blocos. Confrontados os caminhos de implementação/testes de todos os 230 requisitos; examinados os 70 arquivos distintos associados, controllers, política HTTP, modelos, services e testes relevantes. A existência de arquivo ou teste não foi tratada como execução ou cobertura integral.

Não foram executados testes de aplicação, servidores, migrations, banco real ou deploy. Evidências backend 568, Node 125, Playwright 109, premium 14, build e npm audit são registros anteriores, não resultados reexecutados aqui. Esta auditoria não certifica produção, privacidade jurídica, cálculos legais ou comportamento de dispositivos reais.

Severidades: P0 = perda/integridade ou risco crítico comprovado; P1 = impede aceitar o conteúdo no escopo solicitado; P2 = correção de qualidade/rastreabilidade ou ressalva de validação; P3 = melhoria editorial. Não foi identificado P0 documental. Aprovação desta candidata não pode ser presumida a partir da auditoria premium do código.

## Conferência inicial e inventário dos oito arquivos

Branch e HEAD correspondem aos valores esperados. `git diff --check` não apresentou erros; apenas aviso de normalização futura LF/CRLF no documento de decisões. Staging vazio. Alterações já existentes foram preservadas.

| Arquivo auditado | Estado inicial |
|---|---|
| AGENTS.md | Modificado |
| docs/README.md | Modificado |
| docs/decisoes-produto-bs-20261009.md | Modificado |
| docs/Documentacao_Mestre_Plataforma_BES_v1_5_Candidata.docx | Não rastreado |
| docs/bes-consolidacao-documental-v1_5.md | Não rastreado |
| docs/inventario-tecnico-bes-v1_5.json | Não rastreado |
| docs/matriz-rastreabilidade-bes-v1_5.md | Não rastreado |
| docs/roadmap-integral-bes-v1_5.md | Não rastreado |

Os três diffs rastreados são adições de referência à candidata e à visão de produto independente, mantendo v1.4 como única mestre oficial. A candidata não foi promovida a canônica. Não foram encontrados código, build, banco ou screenshots novos nesse conjunto.

## Integridade e coerência entre os documentos

| Verificação independente | Resultado |
|---|---|
| RF001–RF218 | 218 IDs únicos, consecutivos, redações iguais à extração direta da v1.4 |
| RNF001–RNF012 | 12 redações originais preservadas |
| Requisitos originais removidos/renumerados | Nenhum encontrado |
| Campos de rastreabilidade dos 230 registros | Presentes; todos os caminhos de implementação/testes declarados existem |
| Corpo histórico v1.4 na candidata | 495 elementos originais preservados em sequência e comparados estruturalmente |
| Partes do pacote | 19 em cada DOCX; somente word/document.xml difere |
| Tabelas | Uma original preservada; seis no total na candidata |
| ZIP/XML | CRC ZIP e parse de XMLs/relacionamentos aprovados; não é validação formal de schema OOXML nem prova visual |
| Contagens publicadas | 37 RF validados no núcleo, 73 parciais, 104 planejados, 4 dependentes; nenhum em desenvolvimento; soma 218 |
| Decisões e propostas | DP01–DP10 separados dos RF; oito planejados e dois dependentes; PR01–PR21 explicitamente PROPOSTA NOVA |

SHA-256 da v1.4: `f2c2289fd899834e9868b7102c869e4417fcff460b5d6c4c359bc9f52922865b`. SHA-256 da candidata: `eed8e4c9f5c64dd4e486fcb88082a5e72154758172bc676034ebe91b624e2659`. A v1.4 corresponde também ao blob do HEAD quando normalizado como conteúdo binário; nenhuma edição autorizada ou realizada.

As regras originais estão preservadas na seção histórica, e a consolidação explicita Produto versus Estoque, ativo individual, aprovação versus atendimento, recebimento versus consumo, usuário versus responsável, snapshots, EPI integrado, idempotência/rollback/permissões, QR sem autoridade e aceites sujeitos a SST/jurídico. As ordens detalhadas de locks continuam nas fontes técnicas e AGENTS; não considerar o resumo um substituto desses protocolos.

JWT histórico versus sessão/CSRF atual, PostgreSQL pretendido versus MySQL runtime, próximos passos antigos e identidade B&S são divergências explicitamente identificadas como históricas ou decisões pendentes. Não são autorização de migração nem perda da fonte. Não foi encontrada alteração de regra de domínio escondida pela candidata. O erro RF035 é contradição entre classificação e implementação atual, descrita abaixo.

## Achados classificados

| ID / severidade | Arquivo e evidência | Impacto e risco | Recomendação | Bloqueia commit? |
|---|---|---|---|---|
| D01 — P1 | DOCX parte 1/roadmap §§ Mapa integral, Fases e Critérios comerciais; matriz RF001, RF045, RF063, RF127–RF138, RF156 e PR20 não definem os 36 grupos do novo eixo listados na seção seguinte | Lacuna documental. Gestão operacional/RH de carreira e integração ERP genérica não cobrem DP, folha, benefícios ou tesouraria. Não houve remoção de RF original: este é critério novo da presente auditoria | Documentar capacidades, origem da decisão, estados de planejamento/validação, responsabilidades, aceite e dependências; decidir identificadores sem renumerar RF001–RF218; sincronizar DOCX/JSON/matriz/roadmap após autorização | **Sim** |
| D02 — P1 | Matriz RF035, linha 456 e seguintes, e registro JSON correspondente: PLANEJADO/“Sem implementação localizada”. StatusPedidoCompra.java tem seis estados; PedidoCompraService.java:229, 412, 455, 482, 754–755 implementa transições. docs/compras-recebimento.md:62 declara parcial porque COMPRADO não é separado. ComprasTests.java:376/379 verifica recebimento parcial/total | Inventário subestima entrega existente e distorce contagens/roadmap; pode induzir reimplementação | Corrigir para parcial, explicitar estados entregues e COMPRADO/compra externa ausentes, associar evidências; recalcular todos os resumos. Não alterar código ou adicionar estado por esta auditoria | **Sim** |
| D03 — P2 | DOCX; nova tentativa com render_docx.py falhou ao importar pdf2image. Sem PNG/PDF. soffice/pdftoppm/winword não localizados no PATH e ProgIDs Word.Application/LibreOffice.WriterDocument.1 ausentes | Paginação, cortes, sobreposições e legibilidade real não verificáveis. Integridade estrutural não é aprovação visual | Renderizar em ambiente apropriado e revisar todas as páginas; registrar resultado e hash da versão revisada. Não instalar ferramentas nem abrir produção para contornar | Não isoladamente para guardar uma candidata; **bloqueia aprovação visual e promoção a mestre** |
| D04 — P2 | DOCX parte nova: 1.807 parágrafos, nenhum campo TOC, bookmark ou hyperlink; há somente orientação “Como consultar”. Seção inicial não referencia header/footer; os existentes não têm campo PAGE | Navegação e numeração insuficientes para uma mestre extensa. Não afirmar número real de páginas a partir de metadados | Planejar sumário navegável, referências internas e numeração consistente na candidata, preservando a v1.4 original; validar por renderização | Não isoladamente; corrigir antes de promover a mestre |
| D05 — P2 | JSON/matriz campo dependências: 16 RF incluem o próprio ID; exemplos RF005, RF053, RF148, RF150, RF193, RF203 e RF204. Vários critérios são texto de família repetido, por exemplo RF036/RF037 reutilizam critério de recebimento | Dependência não distingue pré-requisito de vínculo e aceite não oferece prova específica por comportamento; arquivo existente não basta para certificar RF | Separar pré-requisitos, vínculos e regras transversais; remover auto-dependências; associar método/cenário/artefato e critérios observáveis de preço/histórico/busca etc. Preservar os gates comuns | Não isoladamente; ressalva da rastreabilidade |
| D06 — P2 | DP01/DP09/DP10 citam “Prompt mestre de 09/10/2026”; há conteúdo e vínculo RF, mas não um documento-fonte versionado ou identificador verificável da decisão. Critérios DP/PR são majoritariamente genéricos de autorização | Rastreabilidade de origem perde força fora da sessão; dificulta auditoria de mudança e aprovação | Registrar fonte documental durável e estado da decisão, dono, data e aceite específico em rodada autorizada, sem apresentar proposta como aprovada | Não isoladamente |
| D07 — P2 | DOCX word/footer1.xml preservado: “Documento de visão e requisitos — v1.2 — Setembro/2026”; docProps/app.xml contém Pages=1/Words=0; core.xml mantém datas de 2013. São partes herdadas da v1.4 | Rodapé histórico e metadados não identificam a edição atual; Pages=1 não serve para contagem/paginação | Preservar arquivo v1.4; na candidata distinguir rodapé legado/histórico e atualizar metadados próprios quando houver autorização, sem usar cache como evidência visual | Não isoladamente |
| D08 — P1 | Roadmap §§ Riscos e Fases mencionam SST/jurídico, LGPD e ERP/financeiro, mas não têm gates explícitos de legislação trabalhista/tributária, regras por competência, obrigações de folha, integrações bancárias e conciliação | O eixo novo não tem responsáveis, controles ou homologação de cálculo financeiro. Aprovação comercial poderia ser interpretada além da evidência | Acrescentar gates de validação contábil/DP/jurídica, versionamento/vigência, fechamento/reabertura e integrações bancárias, após autorização documental. Não definir alíquotas ou presumir conformidade | **Sim**, ligado à D01 |

Auto-referências encontradas: RF005, RF053, RF097, RF108, RF109, RF127, RF145, RF148, RF150, RF163, RF168, RF169, RF193, RF203, RF204 e RF212. Isso é deficiência de modelagem documental das dependências, não prova de deadlock ou ciclo de execução no código.

## Novo eixo RH DP Folha Benefícios Financeiro

Cada linha abaixo é **LACUNA DOCUMENTAL** no escopo pedido. “Parcial/relacionado” significa que há menção ou funcionalidade vizinha, sem o detalhamento da capacidade solicitada. Não significa que a capacidade nova esteja parcialmente implementada. Estes itens não são novos RF formalmente numerados por esta auditoria.

| Eixo / capacidade solicitada | Evidência atual e lacuna |
|---|---|
| RH — Funcionários, contratos, cargos e funções | RF001 e DP03 cobrem cadastro/função e vigência futura; não há contrato de trabalho, cargo estruturado, vínculo/aditivos/vigências completos. Funcionario.java:13–15 tem apenas nome/matrícula/função |
| RH — Escalas e jornadas | Não definidas; turno de refeição RF124 não modela jornada ou escala trabalhista |
| RH — Ponto e horas extras | RF127 é importação de presença como apoio a refeições; não é gestão/apuração de ponto nem horas extras |
| RH — DSR, adicionais, faltas e atrasos | Não definidos. Atraso de ferramenta RF046 não é atraso de funcionário |
| RH — Férias, afastamentos e 13º salário | Não definidos. Viagens/folgas de campo RF181–RF192 não são férias/afastamentos/13º |
| RH — Admissão e desligamento | Desligamento é jornada futura de custódia/acesso; admissão e acerto/processo DP não definidos |
| RH — Portal do funcionário | Não definido. PR09 é portal de responsáveis de obra; não é portal individual RH/DP |
| Folha — Modelo nativo BES | Não definido: rubricas, fórmulas, apuração, fechamento e responsabilidade de validação ausentes |
| Folha — Modelo externo integrado | PR20 cita ERP/financeiro genérico; contrato de folha, importação, validação e conciliação ausentes |
| Folha — Escolha configurável por empresa | Configuração de cliente é genérica; não inclui seleção de modelo de folha |
| Folha — Fonte oficial por competência | Não definida: origem, competência, versionamento e fechamento oficial ausentes |
| Folha — Migração controlada entre modelos | Migração de implantação/tenant não é migração de folha; reconciliação/competência de corte não definidas |
| Folha — Holerites digitais | Não definidos; impressão operacional RF205–RF218 não equivale a holerite privado |
| Folha — INSS, IRRF, FGTS e demais encargos | Não definidos; sem regras versionadas, validação competente ou obrigações de integração explicitadas |
| Folha — Adiantamentos e consignados | Não definidos; sem lançamentos, limites acordados, descontos e conciliação |
| Folha — Aprovações e auditoria | RF148/RF150 são transversais; fluxo de revisão/fechamento/reabertura/segregação de folha ausente |
| Folha — Regras trabalhistas versionadas | Não definidas por vigência/competência/empresa, com fonte e validação profissional |
| Benefícios — Vale-transporte | Não definido. Cadastro de trajetos/passagens/folgas não define VT |
| Benefícios — Vale-refeição | Não definido. Solicitação operacional de refeições RF124–RF126 não define VR |
| Benefícios — Vale-alimentação | Não definido |
| Benefícios — Assistência médica | Não definida; ASO RF103 não é benefício de assistência médica |
| Benefícios — Elegibilidade | Não definida por vínculo, vigência, regra/empresa e condição |
| Benefícios — Cálculo de recargas | Não definido; sem calendário, descontos, ajustes ou validação |
| Benefícios — Integrações com fornecedores | Fornecedor de compra não cobre contratos/cargas/retornos de operadora de benefícios |
| Benefícios — Comprovantes e conciliação | Não definidos por lote/competência, cobrança, repasse e empregado |
| Financeiro — Contas a pagar e receber | Não definidas; pedido/recebimento físico e licenciamento/cobrança de produto não são títulos financeiros |
| Financeiro — Agenda de pagamentos | Não definida por vencimento, autorização e execução |
| Financeiro — Fluxo de caixa | Não definido; sem fonte, saldo, previsão e realizado |
| Financeiro — Previsões e provisões | Orçamento OS RF062 é planejamento contextual; provisões contábeis/financeiras não definidas |
| Financeiro — Custos por funcionário | Não definidos; custódia/consumo não calculam remuneração, encargos ou custo total |
| Financeiro — Custos por obra/OS/CC | RF063 e F5 reconhecem custo futuro; sem método completo para folha/benefícios/despesas e reconciliação |
| Financeiro — Compras e despesas operacionais | Compras/frota/alojamento/refeições têm escopo operacional; obrigação financeira, documento, competência e pagamento integrados ausentes |
| Financeiro — Aprovações financeiras | Aprovação de pedido/PR08 não define aprovação de título/pagamento, alçadas e segregação financeira |
| Financeiro — Integração bancária | Não definida; sem contrato técnico, credenciais segregadas, autorização, retorno ou homologação bancária |
| Financeiro — Conciliação | Há menção genérica de conciliação em F5/PR20; sem extrato/título/lote, tolerâncias, exceções e revisão |
| Financeiro — Relatórios gerenciais | RF045 e BI propõem custos; não definem relatórios de caixa, títulos, folha, benefícios ou fontes oficiais conciliadas |

Não foram encontradas essas definições completas no corpo histórico v1.4, na parte nova do DOCX, na matriz, no JSON ou no roadmap. Contratos de aluguel, salários mencionados como decisão humana, repouso de campo e termo de EPI não preenchem as lacunas. Não recomendar copiar obrigações legais como fórmulas sem validação: registrar planejamento e responsáveis antes da implementação.

## Cobertura dos módulos e comercialização

Todos os módulos operacionais listados no item 3 da solicitação aparecem no mapa ou nas decisões: catálogo/estoque, demanda/movimentos, compras/fornecedores, contexto, ativos, EPI/SST, prontuário, caixas, checklists/inspeções, aceites, documentos/PDF, QR, alertas, relatórios, administração/segurança, empresas e comercialização. A presença documental não representa entrega. Anexos, QR, manutenção, offline, isolamento, matriz EPI e aceite do trabalhador são tratados como futuros, sem falsa conclusão global. Os módulos adicionais da v1.4 também permanecem no mapa e nas redações individuais.

| Critério comercial | Resultado documental |
|---|---|
| Produto independente/B&S potencial cliente | Explícito, sem titularidade presumida; contrato e marca dependem de decisão |
| Múltiplas empresas e isolamento | Planejamento explícito; reconhece ausência de tenant e leitura global por permissão |
| Configuração por cliente | Identidade, unidades, políticas, permissões, documentos e dados planejados; folha não contemplada |
| Licenciamento/cobrança | DP10/F10 e política de suspensão/exportação/encerramento; valores/planos não inventados |
| Suporte/atualizações | Planejados, SLA/SLO/RPO/RTO ainda sujeitos a definição |
| Backup/recuperação | Gates e RF151/RF152 preservados; nenhum restore homologado alegado |
| Demo seguro | Fictício e isolado; launcher H2 explicitamente não é demo comercial |
| Landing page/materiais | Planejados após estabilização/homologação, sem métricas falsas |
| Treinamento/onboarding | Planejados em F10, não apresentados como entregues |

A sequência F0–F10 é razoável para o escopo operacional existente e preserva monólito modular, revisão humana, segurança e homologação. Não cobre a sequência necessária de vínculo/jornada → apuração → competência/folha → benefícios → provisões/títulos → banco/conciliação. “F8 ERP/ponto” e “F5 custos” não substituem essa modelagem. Novo eixo deve ter fases e gates próprios ou integração expressa, mantendo os 218 RF anteriores.

## Revisão visual e formatação

Nova tentativa de renderização realizada nesta auditoria, sem instalações. O renderer empacotado falhou antes de produzir arquivos: `ModuleNotFoundError: No module named 'pdf2image'`. Sem ferramentas de conversão no PATH e sem registro COM Word/LibreOffice consultado, não houve alternativa local utilizável nesta inspeção. Isso não prova ausência de todo software em qualquer diretório, mas registra o limite observado. Não foram abertas sessões pessoais de terceiros nem modificado DOCX para tentar renderizar.

| Item pedido | Resultado |
|---|---|
| Quantidade/paginação de páginas | Não validada; Pages=1 herdado não é evidência |
| Sumário | Não há TOC/campos ou links internos na parte nova; orientação de leitura em texto |
| Cabeçalhos/rodapés | Componentes antigos preservados; parte nova sem referência; rodapé legado v1.2 requer identificação histórica |
| Tabelas | Seis estruturas parseáveis, tabela original intacta; largura/altura e legibilidade visual pendentes |
| Quebras | Duas seções estruturais, Letter, margens preservadas; posição renderizada não verificada |
| Numeração | IDs RF/RNF preservados; não encontrado campo PAGE nos cabeçalhos/rodapés existentes |
| Legibilidade/consistência | Estilos preservados; não aprovados visualmente |
| Conteúdo cortado/sobreposto | Não é possível afirmar presença ou ausência sem renderização |

Não se conclui que o DOCX tenha corte ou sobreposição. As deficiências de navegação/numeração são observações estruturais; demais aspectos permanecem não verificados.

## Riscos mantidos e correções recomendadas

HTTP 500 histórico do Bloco 5 sem causa comprovada; P0 H2 reconstruído não demonstra certeza histórica; banco real/schema/locks e inventário inicial pendentes; acessibilidade/touch/leitor de tela/uso físico e auditoria premium integral pendentes; validação SST/jurídica dos aceites/matriz; confidencialidade e isolamento empresariais não entregues; produção não autorizada. As novas lacunas acrescentam riscos de legislação trabalhista/tributária, obrigações por competência e integrações bancárias, sem taxas, prazos legais ou certificação presumidos.

Correções recomendadas, sem execução: documentar o novo eixo e seus riscos/gates; corrigir RF035 e sincronizar contagens; fortalecer critérios/evidências/dependências e fonte de decisões; providenciar revisão visual e navegação/identificação da candidata. Não modificar v1.4, não promover recursos futuros a entregues e não iniciar implementação como consequência da auditoria.

## Estado final e preservação

Único arquivo produzido nesta rodada: `docs/bes-auditoria-documental-v1_5.md`. Os oito arquivos pendentes recebidos, a v1.4 e código/testes/scripts permaneceram sem alterações desta auditoria. Manifest de hashes e resultados técnicos auxiliares foram guardados em TEMP, fora do staging e do repositório. Git final deve ter três arquivos modificados e seis não rastreados, incluindo este relatório; staging vazio, branch/HEAD inalterados. Verificações finais constam após o apêndice.

## Apêndice de conferência dos 218 RF

Esta tabela registra a conferência integral de texto/ID, campos, existência de fontes de código/testes e concordância de estado entre DOCX/matriz/JSON. “Fontes existentes” não significa execução ou prova semântica integral. As contagens são as da candidata recebida e não foram corrigidas. Achados D01–D08 continuam aplicáveis.

| RF | Estado recebido | Arquivos / testes associados existentes | Resultado documental |
|---|---|---:|---|
| RF001 | IMPLEMENTADO PARCIALMENTE | 3 / 2 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF002 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF003 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF004 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF005 | IMPLEMENTADO E VALIDADO | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF006 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF007 | IMPLEMENTADO PARCIALMENTE | 7 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF008 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF009 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF010 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF011 | IMPLEMENTADO E VALIDADO | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF012 | IMPLEMENTADO E VALIDADO | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF013 | IMPLEMENTADO E VALIDADO | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF014 | IMPLEMENTADO E VALIDADO | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF015 | IMPLEMENTADO E VALIDADO | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF016 | IMPLEMENTADO E VALIDADO | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF017 | IMPLEMENTADO E VALIDADO | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF018 | IMPLEMENTADO E VALIDADO | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF019 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF020 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF021 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF022 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF023 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF024 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF025 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF026 | IMPLEMENTADO E VALIDADO | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF027 | IMPLEMENTADO E VALIDADO | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF028 | IMPLEMENTADO E VALIDADO | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF029 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF030 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF031 | IMPLEMENTADO E VALIDADO | 6 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF032 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF033 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF034 | IMPLEMENTADO E VALIDADO | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF035 | PLANEJADO | 0 / 0 | D02: estado contradiz código e cobertura do Bloco 4 |
| RF036 | IMPLEMENTADO E VALIDADO | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF037 | IMPLEMENTADO E VALIDADO | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF038 | IMPLEMENTADO PARCIALMENTE | 3 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF039 | IMPLEMENTADO PARCIALMENTE | 3 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF040 | IMPLEMENTADO PARCIALMENTE | 3 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF041 | IMPLEMENTADO PARCIALMENTE | 3 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF042 | IMPLEMENTADO PARCIALMENTE | 3 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF043 | IMPLEMENTADO PARCIALMENTE | 3 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF044 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF045 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF046 | IMPLEMENTADO E VALIDADO | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF047 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF048 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF049 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF050 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF051 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF052 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF053 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF054 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF055 | IMPLEMENTADO PARCIALMENTE | 3 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF056 | IMPLEMENTADO E VALIDADO | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF057 | IMPLEMENTADO E VALIDADO | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF058 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF059 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF060 | IMPLEMENTADO PARCIALMENTE | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF061 | IMPLEMENTADO E VALIDADO | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF062 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF063 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF064 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF065 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF066 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF067 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF068 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF069 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF070 | IMPLEMENTADO PARCIALMENTE | 6 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF071 | IMPLEMENTADO PARCIALMENTE | 6 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF072 | IMPLEMENTADO E VALIDADO | 6 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF073 | IMPLEMENTADO PARCIALMENTE | 6 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF074 | IMPLEMENTADO PARCIALMENTE | 6 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF075 | IMPLEMENTADO E VALIDADO | 6 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF076 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF077 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF078 | IMPLEMENTADO E VALIDADO | 6 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF079 | IMPLEMENTADO E VALIDADO | 6 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF080 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF081 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF082 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF083 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF084 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF085 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF086 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF087 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF088 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF089 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF090 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF091 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF092 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF093 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF094 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF095 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF096 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF097 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF098 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF099 | IMPLEMENTADO E VALIDADO | 6 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF100 | IMPLEMENTADO PARCIALMENTE | 6 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF101 | IMPLEMENTADO E VALIDADO | 6 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF102 | DEPENDENTE DE VALIDAÇÃO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF103 | DEPENDENTE DE VALIDAÇÃO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF104 | IMPLEMENTADO PARCIALMENTE | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF105 | DEPENDENTE DE VALIDAÇÃO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF106 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF107 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF108 | DEPENDENTE DE VALIDAÇÃO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF109 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF110 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF111 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF112 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF113 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF114 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF115 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF116 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF117 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF118 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF119 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF120 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF121 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF122 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF123 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF124 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF125 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF126 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF127 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF128 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF129 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF130 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF131 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF132 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF133 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF134 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF135 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF136 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF137 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF138 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF139 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF140 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF141 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF142 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF143 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF144 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF145 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF146 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF147 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF148 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF149 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF150 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF151 | PLANEJADO | 4 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF152 | PLANEJADO | 4 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF153 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF154 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF155 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF156 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF157 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF158 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF159 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF160 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF161 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF162 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF163 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF164 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF165 | IMPLEMENTADO PARCIALMENTE | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF166 | IMPLEMENTADO PARCIALMENTE | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF167 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF168 | IMPLEMENTADO PARCIALMENTE | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF169 | IMPLEMENTADO PARCIALMENTE | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF170 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF171 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF172 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF173 | IMPLEMENTADO E VALIDADO | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF174 | IMPLEMENTADO E VALIDADO | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF175 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF176 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF177 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF178 | IMPLEMENTADO PARCIALMENTE | 4 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF179 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF180 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF181 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF182 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF183 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF184 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF185 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF186 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF187 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF188 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF189 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF190 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF191 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF192 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF193 | IMPLEMENTADO E VALIDADO | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF194 | IMPLEMENTADO E VALIDADO | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF195 | IMPLEMENTADO E VALIDADO | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF196 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF197 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF198 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF199 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF200 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF201 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF202 | IMPLEMENTADO E VALIDADO | 6 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF203 | IMPLEMENTADO E VALIDADO | 6 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF204 | IMPLEMENTADO PARCIALMENTE | 5 / 3 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF205 | IMPLEMENTADO PARCIALMENTE | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF206 | IMPLEMENTADO PARCIALMENTE | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF207 | IMPLEMENTADO E VALIDADO | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF208 | IMPLEMENTADO E VALIDADO | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF209 | IMPLEMENTADO E VALIDADO | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF210 | IMPLEMENTADO PARCIALMENTE | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF211 | IMPLEMENTADO PARCIALMENTE | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF212 | PLANEJADO | 0 / 0 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF213 | IMPLEMENTADO PARCIALMENTE | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF214 | IMPLEMENTADO PARCIALMENTE | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF215 | IMPLEMENTADO PARCIALMENTE | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF216 | IMPLEMENTADO E VALIDADO | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF217 | IMPLEMENTADO PARCIALMENTE | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |
| RF218 | IMPLEMENTADO PARCIALMENTE | 5 / 4 | Texto/estado entre documentos conferidos; alcance sujeito aos achados gerais |

## Verificação final

Conferidos novamente os hashes dos oito arquivos recebidos e da v1.4: todos inalterados. O binário v1.4 também coincide com o blob de HEAD. Estados e redações dos 218 RF concordam entre DOCX/matriz/JSON; isso preserva, mas não sana, a contradição RF035. Conferidas 132 anotações explicitas em controllers e os 132 pares únicos do inventario, com arquivo/linha existentes. Branch/HEAD inalterados; staging vazio; somente este relatório foi acrescentado. git diff --check e git diff --cached --check sem erros de whitespace. Textos não rastreados conferidos separadamente, sem linhas com espaços excedentes ou EOF vazio. O aviso LF/CRLF do documento de decisões permanece não bloqueante.

Nenhuma correção documental ou de código aplicada. Sem commit/push. Aguardar autorização para correções e nova auditoria.

REQUER CORREÇÕES ANTES DO COMMIT.
