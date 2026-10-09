# Plataforma BES — referência oficial para desenvolvimento

## Consolidação documental v1.5 candidata — 09/10/2026

A [v1.5 candidata](docs/Documentacao_Mestre_Plataforma_BES_v1_5_Candidata.docx), a [matriz integral](docs/matriz-rastreabilidade-bes-v1_5.md) e o [roadmap](docs/roadmap-integral-bes-v1_5.md) são planejamento sujeito à auditoria. A v1.4 permanece a única mestre oficial até aprovação expressa; a candidata não autoriza novos blocos ou produção. Consultar o [relatório de integridade e limites](docs/bes-consolidacao-documental-v1_5.md). BES é produto independente; B&S é potencial cliente, sem propriedade presumida. Registrar oportunidades como propostas sujeitas à aprovação, sem expansão silenciosa de escopo.

## Ferramentas e equipamentos — Bloco 6

Consultar [ativos e custódia](docs/ferramentas-equipamentos.md). Ativo individual não altera saldo de Produto. Uma pendência por ativo; devolução/chegada são eventos novos que fecham origem única. Contexto usa snapshots do Bloco 5. Locks: Obra → CC → OS quando necessários → ativo; nunca adquirir estrutura depois do ativo nem misturar locks de estoque/pedido/demanda. Condição, situação administrativa e reprovação são separadas. Dano não atribui culpa automática. Operações críticas exigem authority no HTTP/service, CSRF, ator autenticado, auditoria atômica e chave de idempotência. Sem DELETE, manutenção/financeiro/QR/anexos completos, scripts reais, produção, commit ou push nesta rodada.

A documentação mestre funcional atual é [Documentação Mestre BES v1.4](docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx), com escopo **RF001–RF218**. A v1.4 substitui a v1.3 como principal referência; preservar os requisitos, decisões e roadmap anteriores compatíveis. O índice de documentação está em [docs/README.md](docs/README.md).

Antes de cada bloco:

1. Consultar a v1.4 e identificar os RFs envolvidos e suas dependências.
2. Definir o escopo incremental: o que será implementado completamente, parcialmente ou permanecerá pendente.
3. Preservar compatibilidade com o backend, API, frontend, dados e testes existentes, mantendo a estratégia de monólito modular.
4. Tratar segurança, autorização, auditoria, rastreabilidade e integridade como requisitos transversais.
5. Ao concluir, registrar evidências e cobertura dos RFs. Preparação arquitetural não significa requisito concluído.

Roadmap não autoriza implementar RF001–RF218 de uma vez. Implementar somente o bloco autorizado pelo usuário.

Avaliar especialmente **RF205–RF218** em cada módulo: documentos operacionais, identidade oficial B&S/BES, identificação e contexto documental, listas de separação, comprovantes, necessidade de compra, QR com autenticação/permissões, PDF/impressão, alternativa mobile, confirmações/assinaturas proporcionais, template mestre reutilizável e ações documentais no estágio correto do fluxo.

Usar a logo oficial B&S quando os assets oficiais estiverem disponíveis. Não inventar logo. Enquanto ausentes, conservar fallback textual BES e os caminhos de assets preparados.

Trabalhar na branch de desenvolvimento vigente, sem alterar `main`, e seguir as autorizações específicas da rodada para banco, commits e publicação.

## Segurança de configuração

Seguir [docs/security.md](docs/security.md). Nunca versionar ou imprimir secrets; nunca colocá-los no frontend/VITE_*. Usar configuração externa e revisar staging antes de commit. Credenciais já versionadas são consideradas comprometidas até rotação. Security gate obrigatório antes de produção; não acessar banco nem rotacionar credenciais sem autorização específica.

## Segurança atual — Security Baseline 2

Consultar [autenticação e autorização](docs/autenticacao-autorizacao.md) antes de modificar endpoints. Spring Security usa sessão/CSRF; declarar permissão HTTP e de service, auditar operações críticas sem segredo e testar autorização positiva/negativa com H2. Nenhum endpoint operacional novo pode ficar público por conveniência de teste. A fonte mestre continua docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.

Todo novo endpoint deve ser classificado explicitamente como público, autenticado ou permissionado e incluído no inventário de endpoints. Escritas autenticadas exigem CSRF conforme a arquitetura de sessão atual. Operações críticas devem declarar authority e autorização no service, registrar auditoria com ator autenticado separado do responsável operacional e avaliar idempotência e concorrência quando aplicáveis.

## Compras e recebimento — Bloco 4

Consultar [compras e recebimento](docs/compras-recebimento.md) e o inventário em [API](docs/api.md). PedidoCompra é o único agregado de compra; não duplicar NecessidadeCompra nem produtos. Vínculos são quantitativos e bloqueados por necessidade. Aprovação não altera estoque; recebimento físico gera ENTRADA atômica/idempotente e não atende a solicitação. Ordem de locks: pedido existente → necessidades por ID → fornecedor quando necessário → produtos por ID → pares de estoque determinísticos. Recebimento nunca bloqueia solicitação; atendimento preserva seu protocolo anterior. Não inverter essa ordem nem substituir locks por leituras seguidas de save.

Pedido com qualquer recebimento não pode ser cancelado neste estágio. Não inferir atores/quantidades do legado. Scripts SQL são manuais, sujeitos a homologação e autorização específica. RFs/documentos parciais e pendências permanecem explícitos; o bloco não autoriza financeiro, NF/XML/IA, OS/CC, estorno, produção ou publicação.

## Obras, OS e Centros de Custo — Bloco 5

Consultar [contexto operacional](docs/obras-os-centros-custo.md). Obra do CC e Obra/CC da OS são imutáveis desde criação. Solicitação congela IDs/nomes/códigos; necessidade/alocação derivam dessa origem; atendimento congela o contexto na SAÍDA. Compra multiobra conserva contexto por alocação e contexto manual somente na quantidade sem alocação do item, nunca um obraId global no pedido. ENTRADA de recebimento não é consumo/custo da obra. Não calcular custo consumido sem método de valorização aprovado.

Estrutura/contexto bloqueiam Obra → CC → OS; não bloqueiam estoque/pedido/demanda ao encerrar cadastros. Preservar protocolos do Bloco 4 e de atendimento. Inativo/encerrado continua legível; nova demanda exige contexto admissível. Scripts manuais não executados, sem backfill de contexto fictício. O Bloco 5 não autoriza financeiro, BI integrado, bancos reais, publicação ou commits.
## Bloco 6 — regras confirmadas em auditoria

Consultar [ativos](docs/ferramentas-equipamentos.md) e [auditoria](docs/bloco6-auditoria-pre-commit.md). Empréstimo para Obra confirmado efetiva essa localização e limpa almoxarifado atual; devolução padrão restaura origem congelada. Transferência mantém origem até chegada. Idempotência deve distinguir nulo de texto literal. Inspeção é concluída sem evento de fechamento; baixado não gera agenda pendente. Preservar locks contexto → ativo, snapshots e atomicidade de estado/evento/chave/auditoria. Preparação de QR/anexos/manutenção não significa requisito concluído.

## EPI operacional - Bloco 7

Consultar [EPI/SST](docs/epis-seguranca-trabalho.md). Configuracao 1:1 Produto, saldo somente Estoque. Historico imutavel por evento/item com snapshots de CA, identidade, unidade e contexto. Locks entrega: contexto -> origens por ID -> produtos por ID -> estoque ordenado; fechamento nao bloqueia estrutura. Substituicao exige condicao/destino antigos explicitos; devolucao so credita ESTOQUE com NOVO e politicas/identidade/validade compativeis. Chave persistida e auditoria atomica; jamais inferir aptidao legal ou reutilizacao automatica. Scripts manuais nao executados; sem QR/anexos/SST completo, banco real, producao ou commit/push nesta rodada.
