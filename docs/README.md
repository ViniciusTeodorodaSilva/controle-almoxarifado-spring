# Documentação da Plataforma BES

- [Ferramentas, equipamentos e custódia — Bloco 6](ferramentas-equipamentos.md): domínio, fluxos, segurança, RFs, documentos e limites.
- [Relatório final — Bloco 6](bloco6-relatorio-final.md): evidências e resposta aos 84 pontos solicitados.

## Fonte mestre oficial atual

Desde 04/10/2026, a [Documentação Mestre Plataforma BES v1.4](Documentacao_Mestre_Plataforma_BES_v1_4.docx) é a **referência funcional oficial principal**, abrangendo **RF001–RF218**. Ela substitui a v1.3 como fonte mestre sem descartar requisitos, decisões ou roadmap anteriores compatíveis.

O documento recebido foi comparado byte a byte com o arquivo canônico, confirmando conteúdo idêntico. A cópia redundante foi removida; somente `Documentacao_Mestre_Plataforma_BES_v1_4.docx` permanece como fonte oficial, sem edição do conteúdo Word. Usar esse caminho para referências futuras.

O documento define o escopo e o roadmap, não o estado de implementação nem autorização para construir tudo imediatamente. A evolução permanece incremental em monólito modular, com compatibilidade e segurança, auditoria, rastreabilidade e integridade transversais.

## Planejamento e cobertura por bloco

Antes de cada bloco, identificar RFs, dependências, entregas completas, entregas parciais e pendências. Ao finalizar, registrar o comportamento entregue e a validação correspondente. Não marcar RF concluído apenas porque entidades, interfaces ou arquitetura foram preparadas. Coberturas históricas registradas com v1.3 continuam sendo histórico; novas análises devem consultar v1.4.

## RF205–RF218 — documentos e operação em campo

| RF | Diretriz oficial a considerar |
|---|---|
| RF205 | Documentos operacionais derivados de registros e fluxos, quando agregarem valor |
| RF206 | Identidade visual oficial B&S Engenharia/BES, reutilizável |
| RF207 | Tipo, número/identificador, status, data/hora e responsáveis |
| RF208 | Contexto de obra, OS, centro de custo, almoxarifado e local, quando aplicável |
| RF209 | Lista de separação de solicitação com itens e conferência |
| RF210 | Comprovantes de retirada e entrega conforme necessidade do processo |
| RF211 | Documento de saldo faltante/necessidade de compra vinculado à solicitação e produto |
| RF212 | QR Code para identificação/acesso ao registro, respeitando autenticação e permissões |
| RF213 | PDF e impressão sem obrigatoriedade de papel |
| RF214 | Etapas equivalentes em tela/mobile quando documento físico não for necessário |
| RF215 | Confirmações/assinaturas proporcionais ao risco e à política do processo |
| RF216 | Template mestre com estrutura e componentes visuais comuns |
| RF217 | Avaliação dos documentos úteis em cada módulo |
| RF218 | Ações de geração, impressão e confirmação contextualizadas no fluxo |

Essa diretriz não declara os RFs implementados. O Bloco 2 preparou dados para futuros comprovantes, mas não entregou PDF, QR ou assinaturas. Cada novo bloco deve avaliar cobertura real e dependências antes de assumir conclusão.

Logo: utilizar os assets oficiais B&S quando disponíveis; não criar identidade substituta. Conservar fallback textual BES enquanto os PNGs não estiverem presentes. Caminhos preparados: `frontend/src/assets/brand/bes-logo-sidebar.png` e `bes-logo-full.png`.

## Documentos técnicos existentes

- [API operacional](api.md).
- [Estabilização do backend](estabilizacao-backend.md).
- [Catálogo Mestre](catalogo-mestre.md).
- [Estoque inteligente e transferências](estoque-inteligente.md).
- [Atendimento, separação, faltas e necessidade de compra — Bloco 3](atendimento-solicitacoes.md).
- [Frontend](../frontend/README.md).
- [Orientações para futuras sessões Codex](../AGENTS.md).

O registro da fonte oficial foi documental. A implementação incremental do Bloco 3 está descrita em [atendimento-solicitacoes.md](atendimento-solicitacoes.md), incluindo mudança deliberada da aprovação, compatibilidade legada, cobertura real dos RFs e scripts manuais não executados.

- [Configuração segura e Security Baseline 1](security.md).

## Security Baseline 2

[Autenticação, autorização e auditoria](autenticacao-autorizacao.md): contratos auth/usuários, matriz dos quatro perfis, sessão/CSRF, bootstrap externo, auditoria e scripts manuais de banco. Implementação incremental subordinada à Documentacao_Mestre_Plataforma_BES_v1_4.docx; preparação não conclui RFs futuros.

[Relatório da Security Baseline 2](security-baseline-2-relatorio.md): validações, revisão adversarial, RFs, pendências e inventário completo de arquivos.

[Auditoria adversarial final](security-baseline-2-auditoria-final.md): correções, inventário de 69 endpoints e evidências finais pré-commit.

## Bloco 4 — compras e recebimento

[Compras e recebimento](compras-recebimento.md): fornecedores PF/PJ, pedido único com aprovação explícita, vínculos quantitativos de necessidades, recebimento físico parcial/idempotente e entrada atômica. [Relatório final](bloco4-relatorio-final.md) registra os 84 pontos de revisão. Contratos em [API](api.md); matriz/inventário em [autenticação e autorização](autenticacao-autorizacao.md). Scripts [MySQL](sql/compras-recebimento-mysql-manual.sql) e [PostgreSQL](sql/compras-recebimento-postgresql-manual.sql) são propostas manuais não executadas. O estágio não autoriza banco externo, produção, commit ou push.

- [Bloco 4 — auditoria final pré-commit](bloco4-auditoria-pre-commit.md): integridade, concorrência, segurança e evidências finais; sem commit/push.

## Bloco 5 — Obras, OS e Centros de Custo

[Contexto operacional e cobertura RF](obras-os-centros-custo.md): estrutura, histórico congelado, integração da demanda à compra/saída, resumos quantitativos e preparação para BI. [Relatório final](bloco5-relatorio-final.md) registra arquitetura, validação, auditoria e inventário. Scripts [MySQL](sql/obras-os-centros-custo-mysql-manual.sql) e [PostgreSQL](sql/obras-os-centros-custo-postgresql-manual.sql) são manuais e não executados. Não autoriza commit, push, merge, main ou produção.

- [Bloco 5 — auditoria final pré-commit](bloco5-auditoria-pre-commit.md): revisão adversarial, correções, concorrência, segurança e evidências; sem commit/push.
- [Bloco 6 — auditoria adversarial pré-commit](bloco6-auditoria-pre-commit.md): correções, regressões, revisão visual e relatório de 88 pontos; sem commit/publicação/banco real.

## Bloco 7 - EPI/SST operacional

[Regras, RFs e limites](epis-seguranca-trabalho.md), [relatorio final](bloco7-relatorio-final.md) e propostas SQL manuais MySQL/PostgreSQL em docs/sql/epis-*. Sem banco real ou publicacao.

[Auditoria adversarial pré-commit do Bloco 7](bloco7-auditoria-pre-commit.md): revisão de código, investigação do HTTP 500 anterior, testes, documentos, revisão visual e decisão sem execução de commit.
