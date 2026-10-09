# Fontes e decisões documentais BES v1.5

Registro durável das instruções fornecidas pelo titular do projeto em 09/10/2026. Autoriza consolidação/correção documental, sem aprovar execução de funcionalidades ou conformidade jurídica. v1.4 permanece oficial. As transcrições abaixo são fontes históricas; fatos e contagens correntes são os da candidata corrigida e do relatório de correções.

<a id="f01-prompt-mestre-de-consolidacao"></a>

## F01 Prompt mestre de consolidação

SHA-256 do texto recebido: `ce2d1c33ee532c11ac9ba5a16ea88fcdd9665e88dfe6058a22ef7c4d56811c47`.

PLATAFORMA BES — PROMPT MESTRE DEFINITIVO
CONSOLIDAÇÃO DOCUMENTAL, VISÃO COMPLETA E ROADMAP COMERCIAL

CONTEXTO:
A Plataforma BES é um produto próprio, independente, desenvolvido para ser comercializado à B&S Engenharia e a outras empresas. A B&S é potencial cliente, não proprietária presumida do software. O objetivo é um produto empresarial premium, completo, seguro, confiável, configurável e escalável.

CHECKPOINT:
Branch esperada: feature/bes-frontend
HEAD esperado: f3c6a2229305a5e47ea2730f8b634ba027269ea0
Verificar branch, HEAD, status e histórico antes de agir. Se houver divergência ou alterações pré-existentes, preservar e relatar; nunca sobrescrever.

ESCOPO DESTA EXECUÇÃO: EXCLUSIVAMENTE DOCUMENTAÇÃO, INVENTÁRIO E PLANEJAMENTO.
NÃO alterar código, NÃO iniciar Bloco 8, NÃO fazer commit/push, merge, deploy ou usar banco real. NÃO executar comandos destrutivos. NÃO implementar funcionalidades neste momento.

FASE 1 — INVENTÁRIO COMPLETO:
Ler AGENTS.md, docs/README.md, Documentacao_Mestre_Plataforma_BES_v1_4.docx, decisões-produto, relatórios premium, auditorias, documentação de todos os blocos e roadmap. Examinar frontend, backend, migrations, testes e endpoints para verificar estado real.
Inventariar TODOS os requisitos RF001–RF218 e posteriores, sem inventar, renumerar, apagar ou substituir requisitos. Identificar conflitos, lacunas e duplicações.
Criar matriz de rastreabilidade: ID, requisito, módulo, fonte, estado real, arquivos de implementação, testes, dependências, prioridade, risco, critério de aceite.
Estados: IMPLEMENTADO E VALIDADO, IMPLEMENTADO PARCIALMENTE, EM DESENVOLVIMENTO, PLANEJADO, PROPOSTA NOVA, DEPENDENTE DE VALIDAÇÃO.
Não confundir testes existentes com homologação real ou produção.

FASE 2 — CONSOLIDAR TODOS OS MÓDULOS:
1. Administração, usuários, RBAC, autenticação, sessões, CSRF, auditoria.
2. Empresas/clientes, unidades, departamentos, configurações e identidade visual.
3. Funcionários, cargos, funções, vínculos, lotações e histórico.
4. Catálogo de materiais, categorias, unidades, códigos e especificações.
5. Almoxarifados, estoque por local, saldos, mínimos/máximos, inventários e divergências.
6. Entradas, saídas, transferências, rastreabilidade, bloqueios e reversões controladas.
7. Solicitações, aprovações, separação, atendimento parcial/total, devoluções e históricos.
8. Compras, fornecedores, cotações, pedidos, recebimentos, divergências e documentos.
9. Obras, ordens de serviço, centros de custo, alocação e consumo.
10. Ferramentas, ativos individuais, equipamentos, custódia, inspeções, transferências, devoluções e perdas.
11. EPI/SST, estoque físico, entregas, substituições, devoluções, descarte, CA e trilha histórica.
12. Prontuário EPI único por funcionário, com histórico de funções e obras, sem duplicar fichas por obra.
13. Matrizes de EPI por riscos/função, versões, vigências e validação SST.
14. Caixas de ferramentas individuais, composição, identificador, custódia, conferências e histórico.
15. Checklists digitais versionados, inspeções, evidências e não conformidades.
16. Termos, ciência e aceites eletrônicos, com identidade, integridade, versão e validação jurídica/SST.
17. PDFs profissionais, documentos operacionais, QR e verificação segura.
18. Fotos, anexos, comprovantes e gestão documental com retenção.
19. Alertas, notificações, vencimentos, pendências e escalonamento.
20. Relatórios, dashboards, indicadores, filtros, exportações e análises.
21. Inventários móveis, QR/barcode e rastreamento de ativos.
22. Manutenção preventiva/corretiva, calibrações, garantias e ciclo de vida dos equipamentos.
23. Operação offline controlada, sincronização, conflitos e prevenção de duplicidade.
24. Fluxos de troca de obra, troca de função, transferência de responsabilidade e desligamento.
25. Treinamentos, habilitações, inspeções e requisitos SST, quando aplicáveis e validados.
26. Integrações externas, APIs, importação/exportação e interoperabilidade.
27. Central administrativa comercial, licenças, planos, contratos e suporte.
28. Demo Mode, onboarding, tutoriais, FAQ, landing page, vídeos e materiais de venda.
Preservar e incluir quaisquer outros módulos ou requisitos encontrados na documentação, mesmo não citados aqui. Esta lista NÃO substitui a documentação existente.

FASE 3 — REGRAS FUNDAMENTAIS:
Produto diferente de saldo de estoque; ativo individual diferente de material quantitativo; compras diferente de recebimento; autorização diferente de atendimento; responsável operacional diferente de usuário autenticado; EPI integrado ao estoque; obra/OS/CC contextual; histórico auditável; integridade transacional; idempotência; permissões no backend; nenhuma operação crítica autorizada somente por QR.
Digital por padrão, papel quando legal, contratual ou operacionalmente necessário.
Mudança de obra não cria automaticamente nova ficha de EPI nem nova caixa se a custódia não mudou.
Mudança de função preserva histórico e inicia nova vigência.
Aceite eletrônico não implica automaticamente validade jurídica universal. Não exigir celular pessoal. Avaliar LGPD, biometria e requisitos SST/jurídicos.

FASE 4 — NOVAS OPORTUNIDADES:
Avaliar e documentar propostas de:
- Gestão de não conformidades com plano de ação, responsável e prazo.
- Inspeções programadas e manutenção com alertas.
- Inventário rotativo e conferência por QR.
- Kits operacionais e reservas de materiais.
- Rastreabilidade por lote/série/validade quando aplicável.
- Gestão de avarias, perdas e responsabilização com revisão humana.
- Controle de garantias, certificados e calibração.
- Requisições e aprovações configuráveis por valor, obra ou perfil.
- Portal simplificado para responsáveis de obra.
- Dashboard executivo multiunidade e custos por obra.
- Histórico único de responsabilidades do funcionário.
- Motor compartilhado de documentos, checklists, notificações e aceites.
- Modo offline para atividades adequadas, com segurança e reconciliação.
- Trilhas de auditoria exportáveis e relatórios de conformidade.
- Importação assistida de planilhas com validação e deduplicação.
- Busca global inteligente com permissões.
- Templates configuráveis de relatórios e documentos.
- Acessibilidade real e UX mobile para uso em campo.
- Observabilidade, monitoramento e suporte técnico.
- Estratégia de APIs e integrações com ERP/financeiro.
- Métricas verificáveis de digitalização e eficiência.
Classificar todas como PROPOSTA NOVA até aprovação, sem confundi-las com requisitos já definidos.

FASE 5 — PRODUTO COMERCIAL:
Avaliar instalação isolada por cliente versus SaaS multiempresa, com análise de custos, complexidade, segurança, migração e isolamento de dados. Não presumir arquitetura pronta.
Planejar configuração por cliente, marca, permissões, dados, contratos, backups, atualizações, migrações, licenciamento, cobrança, suspensão segura, exportação de dados, encerramento de contrato, suporte, SLA e continuidade.
Proteger propriedade intelectual e segredos. Evitar regras hardcoded exclusivas da B&S.
Definir versão comercial completa por escopo, sem abandonar o roadmap integral.

FASE 6 — QUALIDADE E SEGURANÇA:
Definir critérios por módulo: testes unitários, integração, regressão, E2E, RBAC, CSRF, sessão, concorrência, rollback, migrations, performance, acessibilidade, responsividade, usabilidade, logs, monitoramento, backup/restore, LGPD, proteção de dados e homologação.
Manter como riscos explícitos o HTTP 500 histórico do Bloco 5 sem causa comprovada, validações em banco real pendentes, touch/leitor de tela e homologação de produção.
Não declarar sistema pronto para produção sem evidências.

FASE 7 — EXPERIÊNCIA PREMIUM:
Preservar identidade corporativa industrial da BES, sidebar escura, azul principal, alta densidade informacional e interface moderna. Design consistente, responsivo, acessível, sem aparência genérica. Mapear jornadas completas, estados vazios, erros, carregamento, confirmações, atalhos e acessibilidade.
Não modificar o frontend nesta execução.

FASE 8 — APRESENTAÇÃO E COMERCIALIZAÇÃO:
Após estabilização e homologação: Demo Mode com dados fictícios; roteiro problema → fluxo → resultado; vídeo principal e versões curtas; landing page; screenshots reais; QR/links rastreáveis; FAQ, tutoriais e onboarding; propostas comerciais, treinamento e materiais B2B.
Nunca expor dados reais de clientes nem inventar métricas de economia.

FASE 9 — DOCUMENTAÇÃO:
Preservar integralmente v1.4 como histórico. Produzir v1.5 DOCX como candidata à nova documentação mestre, com inventário integral de requisitos existentes, decisões novas, propostas separadas, matriz de rastreabilidade, roadmap por fases, dependências, critérios de aceite, riscos, estratégia comercial e changelog.
Atualizar docs/README.md e referências relevantes. Evitar múltiplas versões simultaneamente canônicas. Não perder tabelas, formatação, requisitos ou anexos. Verificar integridade do DOCX e renderização visual quando ferramentas permitirem; relatar limites reais.
Não inventar conteúdo ausente. Marcar pontos sem fonte ou pendentes de decisão.

FASE 10 — ENTREGA:
Apresentar:
A. Mapa integral de módulos.
B. Quantidade real de requisitos preservados e novos.
C. Matriz implementado/parcial/planejado/proposto.
D. Lacunas e dependências.
E. Roadmap integral priorizado.
F. Critérios da versão comercial completa.
G. Riscos técnicos, jurídicos e operacionais.
H. Arquivos criados/alterados.
I. Validação da integridade da v1.4 e da v1.5.
J. Git status e git diff --check.

Não fazer commit ou push. Não implementar. Encerrar com:
DOCUMENTAÇÃO MESTRE BES v1.5 — CANDIDATA À AUDITORIA PRÉ-COMMIT.

REGRA PERMANENTE:
Ao longo do projeto, propor melhorias de alto valor e registrar como sugestões sujeitas à aprovação, sem expansão silenciosa de escopo. A visão integral permanece; implementação ocorre em etapas auditáveis e com checkpoints.

<a id="f02-autorizacao-de-correcoes-apos-auditoria"></a>

## F02 Autorização de correções após auditoria

SHA-256 do texto recebido: `5f99d263030b7e46aa586d99a555035dcea52b7bb19c7c1deb5fe8cbbc124aa1`.

PLATAFORMA BES — CORREÇÕES DOCUMENTAIS v1.5
APÓS AUDITORIA INDEPENDENTE

AUTORIZAÇÃO:
Corrigir as pendências documentais identificadas
na auditoria independente da candidata v1.5.

REFERÊNCIA:
docs/bes-auditoria-documental-v1_5.md

CHECKPOINT:
Branch: feature/bes-frontend
HEAD: f3c6a2229305a5e47ea2730f8b634ba027269ea0

OBJETIVO:
Produzir uma documentação mestre v1.5 completa,
coerente, rastreável e pronta para nova auditoria.

RESTRIÇÕES:
- Não alterar frontend.
- Não alterar backend.
- Não alterar banco ou migrations.
- Não executar deploy.
- Não iniciar Bloco 8.
- Não fazer commit ou push.
- Não modificar a v1.4.
- Não descartar arquivos existentes.

1. LER A AUDITORIA

Ler integralmente:
docs/bes-auditoria-documental-v1_5.md

Inspecionar os documentos v1.4 e v1.5,
matriz de rastreabilidade e roadmap.

Corrigir todos os achados documentais
com evidência e rastreabilidade.

2. CORRIGIR RF035

Atualizar o estado de RF035 para:

PARCIALMENTE IMPLEMENTADO

Preservar:
- Identificador;
- Redação original;
- Evidências;
- Dependências;
- Critérios de aceite.

Recalcular os totais da matriz.

Se essa for a única reclassificação, os totais
esperados passam a ser:

37 implementados e validados;
74 parcialmente implementados;
103 planejados;
4 dependentes de validação.

Total: 218 RFs.

3. COMPLETAR RH E DEPARTAMENTO PESSOAL

Documentar detalhadamente:

- Cadastro e histórico funcional;
- Contratos e vínculos;
- Cargos e funções;
- Obras e lotações;
- Escalas e jornadas;
- Ponto eletrônico;
- Banco de horas;
- Horas extras configuráveis;
- DSR;
- Adicionais;
- Faltas e atrasos;
- Afastamentos;
- Férias;
- 13º salário;
- Admissões e desligamentos;
- Portal do funcionário;
- Holerites digitais;
- Disponibilização e histórico de recibos.

4. DOIS MODELOS DE FOLHA

MODELO A — FOLHA NATIVA BES

Prever cálculo completo, regras versionadas,
fechamento, conferência, aprovação, encargos,
holerites e obrigações aplicáveis.

MODELO B — FOLHA EXTERNA INTEGRADA

Prever exportação de eventos, importação
de resultados oficiais, holerites, conciliação
e integração financeira.

Cada empresa poderá escolher o modelo.

Definir fonte oficial por competência e
migração controlada entre modelos.

Evitar folhas e pagamentos duplicados.

Não presumir integrações disponíveis sem
confirmação técnica dos fornecedores.

5. BENEFÍCIOS

Documentar:
- Vale-transporte;
- Vale-refeição;
- Vale-alimentação;
- Assistência médica;
- Outros benefícios;
- Elegibilidade;
- Dias previstos;
- Tarifas e valores;
- Férias e afastamentos;
- Recargas;
- Descontos;
- Fornecedores;
- Comprovantes;
- Conciliação.

6. FINANCEIRO INTEGRADO

Documentar:
- Contas a pagar e receber;
- Agenda financeira;
- Fluxo de caixa;
- Custos operacionais;
- Custos por funcionário;
- Custos por obra/OS/CC;
- Compras e fornecedores;
- Salários e encargos;
- Benefícios;
- Provisões;
- Aprovações;
- Conciliação bancária;
- Integração com instituições financeiras;
- Relatórios gerenciais;
- Previsão de despesas.

Pagamentos deverão exigir autorização
adequada e segregação de funções.

7. PRESERVAÇÃO DOS REQUISITOS

Manter integralmente:
RF001–RF218
e os 12 RNFs existentes.

Não renumerar requisitos anteriores.

Para novas capacidades, criar identificadores
novos e únicos somente se a estrutura documental
assim exigir.

Distinguir:
- Requisito já aprovado;
- Decisão de produto;
- Proposta nova;
- Dependente de validação.

Não classificar propostas como implementadas.

8. QUALIDADE DOCUMENTAL

Corrigir:
- Sumário;
- Numeração;
- Referências;
- Dependências;
- Critérios de aceite;
- Rodapé legado;
- Inconsistências apontadas na auditoria.

Preservar o conteúdo histórico e a identidade
visual do documento.

9. REVISÃO VISUAL

Tentar revisão visual da candidata DOCX
com ferramentas alternativas disponíveis.

Não depender exclusivamente de pdf2image.

Se necessário, usar ferramenta de conversão
já instalada no ambiente.

Não instalar programas ou bibliotecas sem
necessidade.

Conferir:
- Paginação;
- Tabelas;
- Sumário;
- Cabeçalhos;
- Rodapés;
- Quebras;
- Texto cortado;
- Legibilidade.

Se a revisão visual não for possível,
registrar a limitação sem declarar aprovação.

10. ATUALIZAR DOCUMENTOS

Atualizar:
- Documentacao_Mestre_Plataforma_BES_v1_5_Candidata.docx;
- matriz-rastreabilidade-bes-v1_5.md;
- roadmap-integral-bes-v1_5.md;
- documentos de consolidação pertinentes;
- índice documental, se necessário.

Manter v1.4 intacta e oficial até aprovação.

11. VERIFICAÇÃO FINAL

Executar:
git status --short
git diff --check
git diff --stat

Inspecionar também os arquivos novos.

Verificar que nenhuma alteração de código
ou arquivo fora do escopo ocorreu.

12. RELATÓRIO FINAL

Informar:
- Achados corrigidos;
- Requisitos preservados;
- Novas capacidades documentadas;
- Totais atualizados;
- Resultado da revisão visual;
- Pendências remanescentes;
- Arquivos alterados;
- Estado do Git.

NÃO fazer commit.
NÃO fazer push.

Encerrar com:

CORREÇÕES DOCUMENTAIS BES v1.5 CONCLUÍDAS —
AGUARDANDO NOVA AUDITORIA PRÉ-COMMIT.

## F03 Auditoria documental recebida

Fonte versionável: docs/bes-auditoria-documental-v1_5.md. SHA-256 preservado: `1c30d0bc01eaf8861087a2b03335e4302dbb4c3fcd1883eea86646a6440a447f`. D01–D08 permanecem como diagnóstico histórico da candidata anterior; seu fechamento é registrado separadamente, sem reescrever o parecer.
