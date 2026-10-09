# Consolidação documental BES v1.5 candidata — estado após correções

Atualizado em 09/10/2026 na branch `feature/bes-frontend`, HEAD `f3c6a2229305a5e47ea2730f8b634ba027269ea0`. A v1.4 permanece intacta e oficial. A candidata aguarda nova auditoria independente, sem commit/push ou implementação.

O estado corrente é: 218 RF (37 implementados e validados, 74 parcialmente implementados, 103 planejados e quatro dependentes de validação), 12 RNFs preservados, 10 decisões DP, 21 propostas PR e 56 capacidades futuras RH/DP/folha/benefícios/financeiro (20 planejadas e 36 dependentes de validação). RF035 é a única reclassificação semântica; o mapa tem 41 grupos. A revisão visual alternativa pelo Word instalado foi realizada sobre 175 páginas.

Candidata corrigida SHA-256: `55107d913c0062835e3fcef84b399465394211089d72374e3dd9b1db8753d678`. Evidências, D01–D08 e limites estão no [relatório de correções](bes-correcoes-documentais-v1_5.md), com o [eixo detalhado](bes-eixo-rh-dp-folha-beneficios-financeiro-v1_5.md) e [fontes duráveis](bes-fontes-decisoes-documentais-v1_5.md).

## Registro histórico anterior às correções

O registro abaixo permanece como evidência da consolidação inicial. Suas contagens 37/73/104/4, 37 grupos, hash anterior e limitação inicial de renderização descrevem aquele instante e foram superados pelas correções acima; não representam o estado corrente. A auditoria anterior também permanece histórica e inalterada.

### Consolidação inicial

Referência: 09/10/2026, branch `feature/bes-frontend`, HEAD `f3c6a2229305a5e47ea2730f8b634ba027269ea0`. Execução exclusivamente documental; nenhum código, teste, endpoint, script de banco ou funcionalidade alterado. Sem commit, push, deploy, Bloco 8 ou banco real. Git estava limpo na conferência inicial. O commit premium existente foi usado como base, sem reiniciar sua implementação.

## Entregas e fontes

A [candidata DOCX](Documentacao_Mestre_Plataforma_BES_v1_5_Candidata.docx) reúne visão independente do produto, mapa integral, decisões futuras, oportunidades, estratégia comercial, fases, critérios, riscos, rastreabilidade individual e conteúdo histórico integral da v1.4. A [matriz Markdown](matriz-rastreabilidade-bes-v1_5.md) permite revisão por RF; o [inventário JSON](inventario-tecnico-bes-v1_5.json) inclui requisitos, fontes e rotas; o [roadmap](roadmap-integral-bes-v1_5.md) detalha capacidades e gates.

Consultados AGENTS, índice, mestre v1.4, decisões de produto, documentação dos Blocos 1–7, segurança/autenticação/API, relatórios de investigação/retomada, auditorias premium e documentos de cobertura. Examinados modelos, services, controllers, segurança, frontend, testes e scripts manuais. O inventário registra caminhos e hashes das fontes textuais examinadas antes da atualização das referências nesta rodada; hashes são evidência daquele instante, não promessa de permanecer iguais após a atualização documental. Associação de suíte a RF não comprova que cada frase do requisito possui teste dedicado; estados parciais explicitam o ausente.

Não foram encontradas definições individuais de RF posteriores a RF218. Repetições e intervalos em relatórios não foram contados como novos requisitos. RNFs existentes são preservados separadamente. DP e PR são identificadores documentais, sem ampliar ou renumerar RF.

## Quantidades e estado real

| Inventário | Quantidade |
|---|---:|
| RF preservados com redação original | 218 |
| RNF preservados com redação original | 12 |
| Novos RF definidos nesta execução | 0 |
| Decisões de planejamento DP | 10 |
| Oportunidades PR, todas PROPOSTA NOVA | 21 |
| Grupos de capacidades do mapa integral | 37 |
| Rotas HTTP explicitamente anotadas | 132 |

| Estado dos RF | Quantidade |
|---|---:|
| IMPLEMENTADO E VALIDADO | 37 |
| IMPLEMENTADO PARCIALMENTE | 73 |
| EM DESENVOLVIMENTO | 0 |
| PLANEJADO | 104 |
| PROPOSTA NOVA | 0 |
| DEPENDENTE DE VALIDAÇÃO | 4 |

As 21 oportunidades permanecem PROPOSTA NOVA fora dos 218 RF. Dos 10 registros DP, oito são PLANEJADO e dois DEPENDENTE DE VALIDAÇÃO (matriz EPI e aceite). Todos são decisões futuras sem implementação nesta rodada. Nenhum trabalho foi rotulado EM DESENVOLVIMENTO apenas por haver arquitetura preparada. Os 12 RNFs têm análise própria: oito parciais e quatro planejados, com os mesmos campos de rastreabilidade; não foram promovidos a cumprimento global.

As 132 rotas têm 2 classificações públicas, 2 autenticadas e 128 permissionadas conforme o inventário de controllers e a política de segurança. São pares método/caminho explicitamente declarados; não incluem HEAD/OPTIONS implícitos nem são prova de isolamento multiempresa. API operacional interna não significa integração ERP pronta.

## Lacunas e dependências

A v1.4 contém JWT e próximos passos históricos ultrapassados; o código atual usa sessão/CSRF e aprovação separada do atendimento. Ambos os fatos estão explicitados sem apagar a fonte histórica. PostgreSQL é alvo documental; MySQL é dependência runtime e scripts permanecem manuais, sem migração automática homologada. Identidade B&S em RF206 não prova propriedade ou autoriza uso de marca. Sobreposições entre RFs não justificam eliminar IDs nem contar duas vezes a mesma entrega.

Ausências importantes: empresa/tenant/isolamento de dados, cadastro ocupacional com vigências, matriz EPI, caixas individuais, aceites do trabalhador, anexos/QR, inventário físico/ajustes, estornos gerais, manutenção, offline, integrações, licenciamento e operação comercial. Estoque legado Double e EPI DECIMAL exigem evolução coordenada; transferência quantitativa ainda não tem idempotência persistida. Métodos financeiros/custeio precisam de decisão antes de afirmar custo consumido. A lista completa por RF está na matriz, inclusive módulos históricos de frota, alojamentos, refeições, viagens/folgas, pessoas/carreira e engenharia.

## Roadmap e versão comercial

O [roadmap integral](roadmap-integral-bes-v1_5.md) propõe F0 evidências/decisões; F1 confiabilidade; F2 cliente/pessoas; F3 documentos/campo; F4 inventário/ciclo de vida; F5 compras/custos; F6 administração de campo; F7 SST/pessoas; F8 integrações/inteligência; F9 engenharia/offline; F10 operação comercial. Fases são dependências e gates, sem cronograma, autorização de implementação ou obrigação de executar tudo de uma vez.

Comparação isolado versus SaaS abrange custo ainda não orçado, isolamento, migração, atualização, suporte e continuidade. Piloto isolado é sugestão, sem decisão arquitetural aprovada. O recorte comercial deve listar RFs incluídos, exclusões, suporte e roadmap; não vender recurso ausente como disponível. A edição completa depende de jornadas homologadas, segurança, dados iniciais, backup/restore, monitoramento, SLA, licenças/contratos, saída/exportação de dados, validações SST/jurídicas, acessibilidade e piloto. Nenhum gate de lançamento foi declarado concluído.

Materiais comerciais somente após estabilização/homologação: Demo Mode com dados fictícios, roteiro problema → fluxo → resultado, vídeos, landing page, screenshots reais, FAQ, tutoriais, onboarding, proposta e treinamento. Métricas de eficiência precisam de baseline verificável. Propriedade intelectual, uso de marca, privacidade/LGPD/biometria, retenção, assinatura e responsabilidade SST continuam sujeitos a decisão profissional competente; este documento não é parecer jurídico.

## Evidências anteriores e verificações desta rodada

Os relatórios de retomada e auditoria pré-commit preservam backend 568, Node 125, Playwright 109, premium 14, build aprovado e npm audit sem vulnerabilidades reportadas naquele checkpoint. São evidências anteriores examinadas, **não reexecuções nesta rodada**. Não foram iniciados servidores nem suítes pesadas. Os verdes e vermelhos H2 intencionais anteriores têm finalidades diferentes; testes vermelhos de demonstração não são uma suíte corrente reprovada nem comprovam causa histórica universal.

A regressão reconstruída comprova o mecanismo H2 corrigido em test scope; associação com o incidente original permanece inferência sustentada. O HTTP 500 histórico do Bloco 5 continua sem causa comprovada. Homologação com banco real/schema/locks, auditoria premium integral, touch/leitor de tela/zoom, segurança de produção e produção autorizada permanecem pendentes. A ressalva A02 de foco no resize sem link ativo permanece documentada, sem correção nesta rodada.

Verificações executadas agora: leitura estática, contagens/unicidade de IDs, igualdade das redações RF/RNF, existência dos caminhos de evidência, integridade ZIP/XML, preservação da seção histórica, hashes da v1.4 e de código/testes/scripts contra o inventário inicial, Git e `git diff --check`. Isso não equivale a reexecutar testes ou homologar operação.

## Integridade documental e limite visual

- v1.4 permanece byte a byte intacta. SHA-256: `f2c2289fd899834e9868b7102c869e4417fcff460b5d6c4c359bc9f52922865b`.
- v1.5 candidata SHA-256: `eed8e4c9f5c64dd4e486fcb88082a5e72154758172bc676034ebe91b624e2659`.
- Os dois pacotes têm 19 partes. Na candidata, apenas `word/document.xml` difere; estilos, relacionamentos, headers/footers e demais partes originais foram preservados.
- Os 495 elementos originais do corpo, inclusive a tabela histórica, foram preservados em sequência e comparados estruturalmente. v1.5 tem seis tabelas, uma histórica e cinco de consolidação. As 230 redações foram conferidas contra a extração da mestre; IDs RF são únicos na matriz individual.
- CRC ZIP, XMLs e relacionamentos são parseáveis. Fontes, margens e estilos históricos permanecem nos componentes originais. Isso verifica preservação estrutural, não reprodução visual de paginação.
- Tentativas de renderizar v1.4 e v1.5 com `render_docx.py` falharam por `ModuleNotFoundError: No module named 'pdf2image'`. Nenhum PNG/PDF foi gerado e nenhuma página foi aprovada visualmente. Revisão em Word/renderer com ferramentas disponíveis continua pendente antes da promoção da candidata.

Não há anexos de mídia na v1.4 examinada; nenhuma mídia foi inventada. Logs auxiliares e scripts de geração/verificação foram mantidos somente em TEMP, fora do repositório. Não instalaram dependências nem alteraram código do projeto.

## Arquivos e estado final

Criados: candidata DOCX, inventário JSON, matriz Markdown, roadmap Markdown e este relatório. Atualizados: `docs/README.md`, `AGENTS.md` e `docs/decisoes-produto-bs-20261009.md`, com referências à candidata e manutenção expressa da v1.4 como única mestre oficial. Total conferido: oito arquivos documentais, três modificados e cinco não rastreados; nenhum arquivo staged, código ou artefato de build.

Branch/HEAD permanecem no checkpoint. `git diff --check` e `git diff --cached --check` foram executados sem erros; staging vazio. Os quatro textos novos também passaram por verificação de espaços no fim das linhas, EOF e caracteres de substituição, pois o diff padrão não inclui arquivos não rastreados. Git emitiu apenas aviso de normalização futura LF/CRLF no documento de decisões, sem erro de conteúdo. As alterações ficam no diretório de trabalho para auditoria; não há commit/push. Próxima ação: revisão documental independente, especialmente rastreabilidade por RF e inspeção visual do DOCX; promoção da candidata e implementação posterior exigem autorização específica.

DOCUMENTAÇÃO MESTRE BES v1.5 — CANDIDATA À AUDITORIA PRÉ-COMMIT.
