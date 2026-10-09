# Correções documentais BES v1.5 após auditoria independente

09/10/2026. Branch `feature/bes-frontend`; HEAD `f3c6a2229305a5e47ea2730f8b634ba027269ea0`, preservados. Escopo autorizado por [F02](bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria). Este relatório registra correções e verificações do autor; não substitui a nova auditoria independente nem emite aprovação para commit. A v1.4 permanece intacta e oficial.

## Tratamento dos achados

| Achado histórico | Correção documental e evidência | Limite preservado |
|---|---|---|
| D01 — P1, eixo incompleto | DOCX, [eixo detalhado](bes-eixo-rh-dp-folha-beneficios-financeiro-v1_5.md), JSON, matriz e roadmap incluem RH01–RH16, FOL01–FOL11, BEN01–BEN14 e FIN01–FIN15. | 56 capacidades de planejamento; nenhuma implementação nova. |
| D02 — P1, RF035 | Reclassificado para PARCIALMENTE IMPLEMENTADO. Enum `StatusPedidoCompra`, service e tela demonstram seis estados entregues; COMPRADO/compra externa separado permanece lacuna. Redação original, critério anterior e dependências anteriores preservados. | Testes associados são evidência anterior, não reexecução nesta rodada. |
| D03 — P2, revisão visual indisponível | Word instalado exportou cópia PDF em somente leitura; PyMuPDF instalado renderizou 175 páginas, inspecionadas em 22 pranchas. Evidências em TEMP. | A alternativa contorna a ausência de pdf2image; não certifica todos os ambientes de impressão. |
| D04 — P2, navegação | Campo TOC único, links internos, bookmarks e PAGE contínuo; PDF com 46 links e 411 entradas de navegação. | Word atualiza campos em memória para exportação; repaginação em outro editor exige atualização de campos. |
| D05 — P2, dependências/aceite | Removidas autorreferências atuais; separados pré-requisitos documentais e vínculos funcionais. Aceites vinculados à redação, lacuna e comportamento; critérios específicos para os RF entregues e 12 RNFs. RF035 inclui cenários nomeados. Dependências/aceites anteriores conservados no JSON. | Vínculo de suíte não prova teste individual de cada frase. Critérios futuros não são testes aprovados; homologação continua gate separado. |
| D06 — P2, fontes transitórias | [Fontes duráveis](bes-fontes-decisoes-documentais-v1_5.md) transcrevem F01/F02 com hashes; decisões DP, propostas PR e capacidades novas referenciam essas fontes. | Autorização documental não autoriza implementar propostas. |
| D07 — P2, identificação legada | Cabeçalhos/rodapés ativos identificam candidata v1.5 e seção histórica v1.4; PAGE contínuo; metadados atuais, sem caches legados de contagem. | Partes históricas originais permanecem no pacote como fonte; corpo histórico não foi reescrito. |
| D08 — P1, gates do eixo | Regras por empresa/vigência/competência, validação DP/contábil/jurídica/SST, privacidade, autorização, segregação, conciliação e confirmação técnica de fornecedores/bancos documentadas. | Nenhuma taxa, fórmula legal, integração disponível ou conformidade presumida. |

Os tratamentos acima ficam disponíveis para nova auditoria; o parecer anterior permanece histórico e inalterado.

## Requisitos, contagens e novo planejamento

RF001–RF218 e RNF001–RNF012 mantêm identificadores e redação original. A comparação automatizada com o inventário anterior confirmou que RF035 é a única reclassificação semântica. A troca do rótulo antigo IMPLEMENTADO PARCIALMENTE pelo solicitado PARCIALMENTE IMPLEMENTADO nos demais registros é normalização terminológica.

| Estado dos 218 RF | Quantidade |
|---|---:|
| IMPLEMENTADO E VALIDADO | 37 |
| PARCIALMENTE IMPLEMENTADO | 74 |
| PLANEJADO | 103 |
| DEPENDENTE DE VALIDAÇÃO | 4 |

Os 12 RNFs continuam oito parciais e quatro planejados, sem declaração de cumprimento global. Permanecem 10 decisões DP, 21 oportunidades PR (todas PROPOSTA NOVA) e 132 rotas explícitas: duas públicas, duas autenticadas e 128 permissionadas. Não houve endpoint novo. O mapa passa a 41 grupos de capacidades.

O novo eixo tem 56 identificadores documentais separados dos RF: 16 RH/DP, 11 folha, 14 benefícios e 15 financeiro; 20 PLANEJADO e 36 DEPENDENTE DE VALIDAÇÃO. Cada registro contém descrição, fonte, estado, dependências, risco, prioridade, aceite, fase, responsáveis e testes planejados; implementação/testes existentes da capacidade nova permanecem vazios. Seus pré-requisitos entre capacidades foram conferidos sem ciclos.

RH/DP inclui cadastro/histórico, contratos, cargos, lotações, jornadas, ponto, banco de horas, extras, DSR, adicionais, faltas, afastamentos, férias, 13º, admissão/desligamento e portal. Folha A prevê cálculo nativo completo e regras versionadas; folha B prevê eventos/resultados oficiais externos e conciliação. A escolha por empresa/vigência tem fonte oficial única por competência, fechamento, conferência, aprovação, encargos, holerites/recibos e migração controlada. Simulação não cria pagamento; repetição não deve duplicar folha, título ou pagamento.

Benefícios detalham VT, VR, VA, assistência médica, outros, elegibilidade, calendário, valores, afastamentos, recargas, descontos, fornecedores, comprovantes e conciliação. Financeiro cobre pagar/receber, agenda, caixa, custos por operação/pessoa/Obra/OS/CC, compras, salários/encargos, benefícios, provisões, aprovações, conciliação bancária, instituições, relatórios e previsão. Recebimento físico não é consumo/custo ou pagamento; descontos não comprovam pagamento ao fornecedor. Integrações exigem confirmação técnica e pagamentos reais exigem autorização distinta.

## Integridade e revisão visual realmente realizadas

- ZIP/CRC e XMLs do DOCX válidos; 22 partes. Os 494 elementos originais do corpo histórico, anteriores ao sectPr final, são estruturalmente idênticos à v1.4; estilos originais preservados byte a byte. Na candidata, referências de cabeçalho/rodapé da seção histórica foram atualizadas conforme autorização; isso não altera o arquivo oficial.
- Critérios atuais dos 218 RF, 12 RNFs e 56 capacidades conferidos no JSON, matriz e DOCX; IDs únicos, dependências de RF035 anteriores preservadas e critério atual começa pelo anterior.
- DOCX: 324 bookmarks únicos, 28 links internos em cache apontando para bookmarks existentes e um campo TOC. A exportação pelo Word atualizou o sumário em memória para 46 links/411 entradas no PDF.
- Conversão alternativa: Word instalado, automação VBS com documento somente leitura, atualização de campos em memória, exportação PDF e fechamento sem salvar. A tentativa por COM .NET encontrou erro de biblioteca de tipos; a alternativa VBS funcionou. Nenhum programa/biblioteca instalado.
- PDF final com 175 páginas; todas renderizadas em PNG e inspecionadas em 22 pranchas a 72 dpi nativos. Conferidos paginação, tabelas, sumário, cabeçalhos, rodapés, quebras e legibilidade. Corrigidas larguras das tabelas novas e identificação corrente. Checagem geométrica complementar não encontrou texto fora da página nem páginas com menos de 20 palavras. Texto histórico e suas quebras não foram reescritos para alterar o conteúdo preservado.

A revisão não equivale a auditoria independente, teste de leitor de tela/touch, homologação jurídica ou certificação de impressão em qualquer versão de Office. A paginação pode variar com editor/fontes; atualizar campos após repaginar.

Evidências preservadas em `C:\Users\vinic\AppData\Local\Temp\bes-doc-correcoes-v15-20261009`: cópias anteriores, manifest de hashes, scripts, logs de exportação, PDFs intermediários e final, `qa-final` com 175 PNGs/22 pranchas, `visual-final.json` e `integrity-final.json`. Esses artefatos de QA não foram adicionados ao repositório.

| Evidência | SHA-256 |
|---|---|
| Mestre v1.4, idêntica ao blob de HEAD | `f2c2289fd899834e9868b7102c869e4417fcff460b5d6c4c359bc9f52922865b` |
| Candidata v1.5 corrigida | `55107d913c0062835e3fcef84b399465394211089d72374e3dd9b1db8753d678` |
| PDF de QA final | `5364cde2454258555a44e06cb46e8357e03eaa209cd4909dccac8d7e63e80a53` |
| Auditoria anterior preservada | `1c30d0bc01eaf8861087a2b03335e4302dbb4c3fcd1883eea86646a6440a447f` |

Hashes em fontes_examinadas do inventário registram a leitura histórica anterior às atualizações documentais; não são um manifest atual de todos os documentos modificados.

## Arquivos e estado do Git

Atualizados nesta rodada: candidata DOCX, inventário JSON, matriz, roadmap, relatório de consolidação, docs/README.md e decisões de produto. Criados: eixo detalhado, fontes duráveis e este relatório. AGENTS.md e auditoria anterior têm alterações/arquivo preexistentes preservados, sem edição nesta rodada. Mestre v1.4 preservada.

Estado agregado esperado e conferido ao encerrar: três arquivos rastreados modificados (AGENTS.md, docs/README.md, decisões de produto) e nove documentos não rastreados (candidata, auditoria, consolidação, eixo, fontes, correções, JSON, matriz, roadmap). Staging vazio. Nenhuma alteração de código, testes, banco ou arquivo operacional; nenhuma suíte pesada reexecutada. Evidências backend 568, Node 125, Playwright 109, premium 14, build e npm audit pertencem à etapa anterior, sem nova alegação de execução.

`git diff --check` e `git diff --cached --check` sem erros. Os documentos novos também foram inspecionados e verificados separadamente quanto a whitespace/EOF, conteúdo e escopo, pois não entram no diff comum enquanto não rastreados. Nenhum commit/push, servidor, migração ou deploy executado.

A comparação por SHA-256 com o baseline técnico preservado confirmou 250 arquivos operacionais inalterados. Os nove documentos novos têm extensões/caminhos previstos; a varredura textual de padrões de chaves privadas e tokens conhecidos não encontrou correspondências. Essa varredura é complementar à inspeção de conteúdo, não certificação universal de ausência de segredos.

## Pendências preservadas e próxima ação

Nova auditoria independente pré-commit da candidata e documentos correlatos; somente aprovação expressa pode promover a v1.5 a mestre oficial. HTTP 500 histórico do Bloco 5 permanece sem causa comprovada. Homologação com banco real, auditoria premium integral, touch/leitor de tela reais e produção seguem pendentes. SST/jurídico, regras trabalhistas/tributárias, parâmetros por competência, método de custeio e integrações externas/bancárias exigem validação competente antes de implementação/homologação. As 56 capacidades não foram implementadas e o Bloco 8 não foi iniciado.
