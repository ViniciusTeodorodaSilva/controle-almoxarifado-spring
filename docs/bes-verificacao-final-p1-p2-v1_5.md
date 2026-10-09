# Verificação independente final P1 e P2 da candidata BES v1.5

Data: 09/10/2026. Resultado: **APROVADO PARA COMMIT DOCUMENTAL v1.5**, limitado ao conjunto documental inventariado abaixo. P1 e P2 foram corrigidos de forma coerente nos documentos correntes examinados. Nenhum bloqueio documental novo foi identificado nesse recorte.

Este parecer não executa nem autoriza automaticamente o commit. Aguardar autorização expressa. A v1.4 permanece a única mestre oficial; armazenar a candidata no Git não a promove a oficial. Não há autorização para implementação, Bloco 8, banco real, pagamentos, produção ou publicação.

## 1. Checkpoint e método independente

Branch observada: `feature/bes-frontend`. HEAD observado: `f3c6a2229305a5e47ea2730f8b634ba027269ea0`. Ambos correspondem ao checkpoint solicitado.

Executados na entrada: `git branch --show-current`, `git rev-parse HEAD`, `git status --short`, `git diff --check` e `git diff --stat`. Estado inicial: três arquivos rastreados modificados e onze não rastreados, todos documentais. Diff rastreado: 21 inserções em três arquivos. Staging vazio, conferido também por `git diff --cached --stat` e `git diff --cached --check`. Nenhum erro de whitespace no diff rastreado; aviso preexistente de normalização LF/CRLF em decisões de produto, sem alteração feita por esta auditoria.

Fontes consultadas diretamente: AGENTS.md; os DOCX v1.4 e v1.5 candidata; matriz; roadmap; auditoria final anterior; correção dos bloqueios finais; correções documentais; docs/README.md. Foram consultados adicionalmente o eixo RH/DP/folha/benefícios/financeiro, inventário JSON, diff dos três documentos rastreados, enum de compras e evidências temporárias de exportação.

Os DOCX foram abertos como pacotes ZIP e seus XMLs extraídos em somente leitura. A conferência automatizada percorreu as 230 redações originais, todos os registros RF/RNF, descrições e aceites das 56 capacidades e células das 17 linhas de fases F/E. Foram comparados também os 494 elementos históricos da v1.4, removendo apenas declarações redundantes de namespace XML para comparação. Os pré-requisitos das capacidades foram percorridos por DFS; o grafo executável das fases foi reconstruído a partir das regras correntes, sem tomar o resultado do autor como prova suficiente.

Não se trata de uma nova auditoria integral de código ou de todas as regras futuras. Suítes de aplicação não foram executadas. Cenários futuros de folha não foram executados. Nesta rodada foi criado somente este relatório no repositório; arquivos existentes foram preservados. Extrações, manifest de hashes e verificações auxiliares ficaram em `C:\Users\vinic\AppData\Local\Temp\bes-verificacao-final-p1-p2-v15-20261009`.

## 2. P1 fonte oficial da folha

| Aspecto | Evidência documental corrente | Conclusão |
|---|---|---|
| Exclusividade | Governança do eixo; FOL03/FOL04 no eixo, matriz, JSON e candidata | A chave é somente empresa + competência. No máximo uma fonte e versão PUBLICADA_OFICIAL vigente; antes da primeira publicação pode não haver oficial |
| Identidade e composição | Governança e FOL04 | Empresa + competência + versão identifica o registro; empregado, vínculo, tipo e modelo não criam outra fonte oficial da competência |
| Histórico e retificação | Estados/transições e FOL10 | Oficial não é editada/apagada; sucessora percorre os gates; troca atômica marca anterior SUBSTITUÍDA e preserva histórico, documentos e aprovações |
| Concorrência e auditoria | Governança, CA-P1-01/02 e aceites FOL04/FOL10 | Conferir versão esperada, impedir dupla publicação e conservar anterior oficial se falhar a troca ou auditoria |
| Nativa ou externa | Modelos A/B, FOL01–FOL05 | Configuração por empresa/vigência; importação externa é candidata até conciliar identidade, protocolo, vínculos, rubricas e totais |
| Aprovação e migração | Responsabilidades, FOL05/FOL10 e CA-P1-05/06 | Preparação/conferência/publicação separadas por política; alteração relevante invalida aprovação; corte A/B aprovado, saldos conciliados e retorno controlado |
| Duplicidade e replay | Obrigações/reprocessamento, FOL11 e CA-P1-03 | Versão é proveniência, não uma nova dívida. Chave persistida e deduplicação por origem; outra chave não permite duplicar; payload diferente conflita |
| Retificação de valores pagos | CA-P1-04 e FOL11 | Pago 100, devido 120: somente complemento aprovado 20. Devido 80: preservar pago 100 e tratar diferença 20 mediante autorização, sem apagar pagamento ou estorno automático |
| Integração e falha externa | Governança, FOL11 | Publicação não paga; provisão/título/remessa/confirmação/conciliação são etapas distintas. Timeout exige consulta/conciliação; sem consulta/deduplicação confiável, retry automático bloqueado |
| Segurança e aceite | CA-P1-01–06, governança e validações | Critérios positivos/negativos incluem alçada, autoaprovação, empresa/empregado, sessão/CSRF, rollback, importação, retificação e recuperação |

As descrições e aceites das cinco capacidades alteradas FOL03/04/05/10/11 coincidem entre JSON, matriz, eixo e candidata. Os seis cenários CA-P1 constam no eixo e na candidata. FOL05 aponta E0/E5 e FOL11 E0/E4, distinguindo desenho inicial de migração e integração futuras.

Não foi encontrada contradição corrente que restabeleça a chave oficial com versão/empregado/tipo. A identidade detalhada da obrigação não contradiz a exclusividade da fonte: são objetos e invariantes diferentes. A auditoria anterior conserva a formulação antiga como achado histórico; suas recomendações não substituem a regra corrigida exigida nesta rodada, empresa + competência.

**P1 aprovado documentalmente.** A implementação deve ainda concretizar exclusividade, transações, concorrência, idempotência e políticas aprovadas. Esta conclusão não comprova motor de folha, homologação legal ou execução bancária.

## 3. P2 precedência das fases

| Recorte | Pré-requisitos obrigatórios | Entrega e gate preservados |
|---|---|---|
| F2-A | F0/F1 e política de implantação/dados | Fundação empresarial, identidade, isolamento, autoridades e contratos de referência; não depende de E1 |
| E0 | F0 e responsáveis competentes | Modelos, fontes, regras e contratos; não exige motores F5/F8 prontos |
| E1 | E0/F2-A e política de dados | RH01–RH04, vínculos/cargos/lotações/vigências/histórico e contratos; não exige F2-B/F2 completo |
| F2-B | F2-A/E1 | Integração dos consumidores aos vínculos; homologação conjunta de isolamento, acesso e preservação histórica |
| Núcleo F5 | F1/F2-B/F3 e método de custo | Compras e custos sem exigir folha |
| Integração F5/E5 | Núcleo F5 e E5, com E4/E5 conciliados | Custos posteriores de folha/benefícios, com homologação conjunta |

Sequência executável conferida: F0 → F1 → F2-A → E0 → E1 → F2-B → F3 → F4 → núcleo F5 → F6 → F7 → E2 → E3 → E4 → E5 → integração F5/E5 → F8 → F9 → F10. É uma ordenação possível; E0 pode preceder F2-A e ramos independentes podem avançar após seus próprios gates.

DFS independente: 19 nós executáveis, nenhum ciclo. F9 conserva pré-requisitos técnicos externos ao grafo, sem aresta de fase inventada. F8 consome produtores homologados e integração financeira quando aplicável; F10 exige os gates da edição prometida. Homologação conjunta não foi convertida em dependência reversa do produtor.

As 17 linhas agregadas de fases F0–F10/E0–E5 têm entregas, dependências e gates presentes na candidata. O resumo E1 da candidata também expressa F2-A → E1 → F2-B. O grafo das 56 capacidades, extraído do campo de pré-requisitos do inventário, é acíclico; vínculos relacionados não foram convertidos em arestas obrigatórias.

Comparação com o inventário anterior à correção: RF/RNF intactos; somente descrição/aceite de FOL03/04/05/10/11 e fase de FOL05/11 alterados nas capacidades. IDs e estados preservados. F2/F5 continuam agrupadores, sem novos RFs e sem remoção de funcionalidades.

**P2 aprovado documentalmente.** A circularidade F2 ↔ E1 foi eliminada e a ordem corrigida preserva os contratos, integrações e gates futuros.

## 4. Integridade

| Conferência independente | Resultado |
|---|---|
| RFs | 218 IDs únicos/consecutivos, RF001–RF218 |
| RNFs | 12 IDs únicos/consecutivos, RNF001–RNF012 |
| Redação original | 230 redações da v1.4 iguais ao inventário e presentes na matriz/candidata |
| Aceites RF/RNF | Presentes na matriz e candidata conforme inventário |
| Estados RF | 37 IMPLEMENTADO E VALIDADO; 74 PARCIALMENTE IMPLEMENTADO; 103 PLANEJADO; 4 DEPENDENTE DE VALIDAÇÃO |
| Estados RNF | 8 parciais e 4 planejados; nenhum cumprimento global presumido |
| Capacidades futuras | 56: RH 16, FOL 11, BEN 14, FIN 15; 20 planejadas e 36 dependentes de validação |
| Sincronização de capacidades | Todas as 56 descrições e aceites presentes no eixo, matriz e candidata conforme JSON |
| RF035 | Parcial no inventário/matriz/candidata; COMPRADO/compra externa continua lacuna explícita. Leitura do enum confirmou ausência de COMPRADO |
| Histórico da v1.4 na candidata | 494 elementos preservados, sem diferença estrutural na comparação descrita |
| Pacotes DOCX | v1.4: 19 partes/18 XMLs ou relações válidos; candidata: 22 partes/21 XMLs ou relações válidos |
| v1.4 no disco contra HEAD | Mesmo hash Git de conteúdo: `581ebd7a85ea6249854f165c6c56e93ddc094897` |

Hashes SHA-256 conferidos diretamente:

- v1.4: `f2c2289fd899834e9868b7102c869e4417fcff460b5d6c4c359bc9f52922865b`.
- Candidata corrigida: `74c5aa1e442c0006678c2d7ebabd6aefe81755cf9a7a1657f33a1810a00a936e`.
- PDF de entrega preservado: `d6e1189a027d91cebc014b6947ae35cdadda09edfb83850a5ca1a5b04e95bb26`.

As classificações conservam o alcance das evidências anteriores. Nenhum resultado histórico de backend, Node, Playwright, build ou auditoria premium foi apresentado como teste novo.

## 5. Exportação e inspeção visual efetivamente realizada

Foi lido diretamente o PDF existente `C:\Users\vinic\AppData\Local\Temp\bes-correcao-bloqueios-finais-v15-20261009\candidata-entrega-word.pdf`. Contagem independente: **178 páginas**, 46 links e 415 entradas de outline. Existem 178 PNGs em `qa-entrega`. O log preservado contém `EXPORT_OK PAGES=178`; hashes do DOCX corrente e PDF coincidem com os registrados na correção final.

Foram abertas e inspecionadas individualmente as imagens das páginas **2, 3, 12, 13, 14, 18, 19, 20, 29, 30, 32, 33, 47 e 178**. Cobertura: sumário; tabelas de fases e ordem; governança, transições, obrigações, importação e cenários; FOL03/04/05/10/11; resumo E1; término do histórico. Não foram vistos cortes, sobreposições impeditivas ou perda de conteúdo nessas páginas.

Para vincular as imagens ao PDF examinado, cada uma dessas 14 páginas foi novamente rasterizada em memória; seus pixels coincidem exatamente com o respectivo PNG de `qa-entrega` (612 × 792). Não foi feita nova exportação pelo Word nem salvo qualquer DOCX.

Limites: esta rodada não inspecionou visualmente todas as 178 páginas, nem repetiu a revisão geral em pranchas do autor. A inspeção independente foi das 14 páginas enumeradas, em imagens de 72 dpi. Não certifica cada glifo em alta resolução, impressão física, outros editores ou leitor de tela. A contagem de links/outline não equivale a clicar em cada destino. O cache TOC do DOCX continua exigindo atualização de campos no editor; a exportação examinada contém o sumário atualizado. Esses limites não reabrem P1/P2.

## 6. Achados e riscos residuais

Não foi identificado novo bloqueio no escopo P1/P2 e integridade solicitado. A01/P1 e A02/P2 da auditoria anterior estão encerrados documentalmente por esta verificação. O parecer anterior permanece histórico e deve entrar no conjunto para conservar a trilha de revisão, sem ser tomado como estado corrente.

Permanecem as ressalvas anteriores não bloqueantes: A03, detalhamento de parte dos aceites futuros ao autorizar incrementos; A04, cache do sumário; A05, título de propostas isolado na exportação anterior. A05 não foi reinspecionado nesta rodada. Textos anteriores que aguardavam nova auditoria são registros da situação de sua emissão, não promoção automática da candidata.

Permanecem também os riscos técnicos/operacionais já documentados: causa do HTTP 500 histórico B5, homologação de banco/schema/locks, precisão/custeio e idempotência fora do recorte, isolamento e dados pessoais, validações DP/contábil/jurídica/SST, regras vigentes e fornecedores, autorização bancária, rotação de credenciais históricas, HTTPS, SCA backend, backup/restore e dispositivos reais. Nenhum foi declarado resolvido aqui. Produção permanece não autorizada.

## 7. Inventário e escopo admissível do commit

Todos os arquivos abaixo podem compor um **commit exclusivamente documental**, após autorização, conservando a condição de candidata e o histórico das auditorias. Não usar staging indiscriminado; o conjunto elegível é o inventariado.

| Estado Git final | Arquivo | Papel |
|---|---|---|
| M | `AGENTS.md` | Regras de governança da candidata |
| M | `docs/README.md` | Índice e limites da fonte oficial |
| M | `docs/decisoes-produto-bs-20261009.md` | Decisões e fontes de planejamento |
| ?? | `docs/Documentacao_Mestre_Plataforma_BES_v1_5_Candidata.docx` | Candidata corrigida |
| ?? | `docs/bes-auditoria-documental-v1_5.md` | Auditoria histórica inicial |
| ?? | `docs/bes-auditoria-final-documental-v1_5.md` | Auditoria histórica dos bloqueios |
| ?? | `docs/bes-consolidacao-documental-v1_5.md` | Consolidação e limites |
| ?? | `docs/bes-correcao-bloqueios-finais-v1_5.md` | Correções P1/P2 e evidências do autor |
| ?? | `docs/bes-correcoes-documentais-v1_5.md` | Correções documentais anteriores |
| ?? | `docs/bes-eixo-rh-dp-folha-beneficios-financeiro-v1_5.md` | Governança e 56 capacidades futuras |
| ?? | `docs/bes-fontes-decisoes-documentais-v1_5.md` | Fontes duráveis F01/F02 |
| ?? | `docs/inventario-tecnico-bes-v1_5.json` | Inventário estruturado documental |
| ?? | `docs/matriz-rastreabilidade-bes-v1_5.md` | Rastreabilidade |
| ?? | `docs/roadmap-integral-bes-v1_5.md` | Fases, dependências e gates |
| ?? | `docs/bes-verificacao-final-p1-p2-v1_5.md` | Este parecer independente |

Fora do commit: v1.4 (inalterada), código, testes, configurações operacionais, scripts SQL, PDFs/PNGs e scripts auxiliares em TEMP. O inventário JSON documenta endpoints existentes; não adiciona endpoints operacionais.

## 8. Estado final e decisão

Estado final conferido após criar este relatório: branch `feature/bes-frontend`; HEAD `f3c6a2229305a5e47ea2730f8b634ba027269ea0`; três rastreados modificados e doze não rastreados; staging vazio. Diff rastreado permanece três arquivos/21 inserções. `git diff --check` e `git diff --cached --check` sem erros. Os não rastreados não entram no stat comum e estão discriminados acima.

Comparação dos hashes com o manifest inicial desta rodada: todos os arquivos preexistentes preservados; único acréscimo no repositório é este relatório. Sem commit, push, alteração da v1.4, descarte, banco real, deploy ou início do Bloco 8.

**APROVADO PARA COMMIT DOCUMENTAL v1.5**

Aguardar autorização expressa do usuário para o commit. A v1.4 continua oficial.
