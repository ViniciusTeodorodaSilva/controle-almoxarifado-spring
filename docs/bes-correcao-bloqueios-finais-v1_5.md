# Correção dos bloqueios documentais finais BES v1.5

09/10/2026. Branch `feature/bes-frontend`; HEAD `f3c6a2229305a5e47ea2730f8b634ba027269ea0`. Escopo: correção documental dos achados A01/P1 e A02/P2 de [auditoria final](bes-auditoria-final-documental-v1_5.md), lida integralmente antes da primeira edição. Este relatório registra a correção e as verificações do autor; não substitui a verificação final independente e não aprova commit.

Os dois bloqueios foram corrigidos nos documentos afetados. A candidata permanece candidata; a v1.4 permanece oficial e intacta. Nenhum código, teste, banco, migração ou funcionalidade foi alterado. Não houve commit, push, deploy ou início do Bloco 8.

## 1. P1 — fonte oficial da folha

**Causa:** a identidade anterior incluía empresa, vínculo/empregado, competência, tipo e versão. Recusar duas versões oficiais “para a mesma chave” não impedia que versões diferentes se tornassem fontes oficiais paralelas. FOL11 também vinculava a unicidade da obrigação à versão, deixando margem para recriação de obrigações em retificação.

**Alteração:** a exclusividade agora é somente **empresa + competência**. Após publicação existe uma única fonte e versão vigente PUBLICADA_OFICIAL, independentemente do modelo A/B, empregado, vínculo, tipo de folha ou número de versão. Antes da primeira publicação pode não existir fonte oficial. A identidade de uma versão é empresa + competência + versão, sem substituir a chave de exclusividade. Os itens e tipos de folha compõem a mesma versão oficial da competência; não criam fontes paralelas. Cada empresa conserva configuração A/B própria, com vigência e responsável.

Definições sincronizadas na candidata, [eixo detalhado](bes-eixo-rh-dp-folha-beneficios-financeiro-v1_5.md), [matriz](matriz-rastreabilidade-bes-v1_5.md) e inventário JSON, nos registros FOL03, FOL04, FOL05, FOL10 e FOL11. A configuração empresarial do roadmap permanece compatível com a regra detalhada. Não houve alteração de identificadores ou estados de implementação.

### Estados e transições futuros

RASCUNHO → CONFERIDA → APROVADA → FECHADA → PUBLICADA_OFICIAL. FECHADA congela conteúdo aprovado, mas não o torna oficial nem autoriza pagamento. Mudança relevante invalida conferência/aprovação/fechamento; reabertura exige autorização e auditoria. Versão não publicada pode ser CANCELADA sem apagar histórico.

Uma oficial não é editada ou apagada: retificação cria sucessora que percorre os gates. Publicação da sucessora troca a referência oficial e marca a anterior SUBSTITUÍDA atomicamente. SUBSTITUÍDA e CANCELADA são terminais; retorno a resultado/modelo anterior exige nova versão aprovada. A publicação concorrente deve conferir a versão esperada e a exclusividade por empresa/competência. Falha interna ou de auditoria conserva a fonte anterior, sem estado parcial. Esses nomes descrevem um processo futuro, não novos estados de implementação dos RFs.

### Obrigações, pagamentos e falhas

A identidade de negócio da obrigação não usa o número de versão para criar dívida paralela; versão identifica proveniência e revisão. Reprocessamentos exigem chave persistida/conteúdo canônico e deduplicação pela origem de negócio, inclusive quando outra chave for usada. Chave repetida com conteúdo diferente conflita.

Retificação compara anterior, novo devido, obrigações abertas e valores pagos. Somente diferença aprovada pode gerar complemento ou tratamento de compensação/restituição. Pagamento realizado e obrigação liquidada permanecem preservados: não apagar, recriar, reclassificar como não pagos ou estornar automaticamente. Publicação, provisão, título, remessa, confirmação bancária e conciliação são etapas distintas.

Falha externa após publicação conserva o fato oficial e a pendência de integração. Timeout/retorno parcial exige consulta de protocolo e conciliação antes de repetição; sem consulta/deduplicação confiáveis, bloquear retry automático e exigir tratamento autorizado. Uma transação interna não desfaz transferência bancária já realizada.

No modelo B, importação é candidata até conciliar empresa, competência, vigência/modelo, protocolo, vínculos, rubricas e totais. Divergência, arquivo incompleto ou duplicado não autoriza publicação. Migração A/B exige corte aprovado, conciliação de saldos/acumulados/obrigações/pagamentos, bloqueio de dupla fonte e plano de retorno, preservando acesso e origem histórica.

### Responsabilidades e segurança

Preparador propõe, conferente independente concilia e aprovador/publicador decide por empresa/alçada; tesouraria autoriza pagamento separadamente. Política deve impedir autoaprovação e registrar exceções formalmente validadas, sem ampliar perfis atuais. Autorização backend empresarial, sessão/CSRF, auditoria atômica sem secrets e acesso individual são requisitos futuros. DP/contábil/jurídico valida regras e referências; segurança/privacidade valida escopo e retenção; fornecedor/banco valida capacidade técnica. Não há conformidade legal/LGPD ou autoridade bancária presumida.

### Critérios de aceite verificáveis

Os cenários abaixo estão integralmente no eixo e na candidata, com referências nos aceites das capacidades afetadas. **Foram definidos e conferidos documentalmente, não executados contra uma folha implementada.**

| Cenário | Entrada/falha a exercitar futuramente | Resultado exigido |
|---|---|---|
| CA-P1-01 | Publicações concorrentes com versões, tipos, empregados ou modelos diferentes da mesma empresa/competência | No máximo uma oficial, conflito consultável e nenhuma obrigação duplicada; empresas/competências distintas independentes |
| CA-P1-02 | Retificação, alteração pós-aprovação e falha de auditoria durante substituição | Sucessora aprovada e troca atômica; anterior preservada; aprovação invalidada quando aplicável; falha conserva anterior oficial |
| CA-P1-03 | Replay, outra chave para mesma origem, chave com conteúdo alterado e resultado externo incerto | Deduplicação de obrigação/pagamento; conflito de payload; consulta/conciliação antes de retry |
| CA-P1-04 | Pago 100, retificado para 120 ou 80 | Só complemento aprovado 20 no primeiro caso; pagamento 100 preservado e diferença 20 sujeita a tratamento autorizado no segundo; sem apagar/recriar pagamento |
| CA-P1-05 | Importação divergente/incompleta/duplicada/de empresa errada; corte A/B | Bloquear publicação incorreta; conciliar totais/acumulados/corte; preservar períodos anteriores; impedir dupla fonte |
| CA-P1-06 | Sem alçada, autoaprovação, sessão/CSRF inválidos, acesso cruzado e falha externa após publicação | Rejeitar operação/acesso, manter trilha e pendência recuperável sem recriar pagamento |

Os valores 100/120/80/20 são exemplos de integridade e conciliação, não fórmula de cálculo trabalhista. Leis, taxas, bases, contratos e dados reais continuam sujeitos a validação competente futura.

**Resultado P1:** ambiguidade eliminada documentalmente e sincronização verificada. Implementação e homologação permanecem futuras.

## 2. P2 — ordem F2/E1 e integrações

**Causa:** F2 entregava fundação empresarial e vínculos, exigindo E1 para vínculos; E1 exigia F2 completo. A leitura literal fechava F2 ↔ E1, sem recorte que permitisse iniciar/concluir a cadeia. Menções genéricas à integração de custos também precisavam distinguir desenho de contratos, núcleo independente e integração posterior.

**Alteração:** F2 mantém seu identificador e todo seu escopo, com incrementos internos F2-A e F2-B. Não são RFs novos nem novos estados de implementação.

| Recorte | Entregas | Pré-requisitos obrigatórios | Integração/homologação |
|---|---|---|---|
| F2-A | Empresas/unidades/departamentos, identidade, configuração/isolamento, autoridades e contratos de referência de pessoa/vínculo | F0/F1, implantação e política de dados; não exige E1 | IDs, vigências, snapshots e permissões definidos, sem inventar história funcional |
| E0 | Decisões de modelo/fonte/competência, regras, custos, contratos e responsáveis | F0 e validação competente | Desenho e contratos não exigem motores F5/F8 implementados |
| E1 | RH01–RH04: identidade, vínculos, cargos, funções, lotações, vigências e contratos | E0 e F2-A; não exige F2-B nem F2 completo | Produtores e contratos funcionais com histórico e regras de sobreposição |
| F2-B | Adaptar os consumidores aos vínculos vigentes/históricos de E1 | F2-A e E1 | Homologação conjunta de mudança de função/lotações, isolamento, eventos históricos e acesso |

Justificativa técnica: a fundação empresarial pode fornecer identidade/escopo e contratos antes do histórico funcional ampliado. E1 então produz o vínculo e seu histórico; F2-B integra consumidores após ambos os produtores existirem. A homologação conjunta verifica a composição, sem exigir que o consumidor integrado já exista para construir seu produtor. F2 só está completo quando os dois incrementos atingirem seus gates.

O núcleo F5 de compras/custos não depende de folha; a integração de custos de folha/benefícios ocorre após E4/E5 conciliados. F6 pode operar sobre o núcleo F5 e integrar custos posteriores. F8 consome fontes homologadas; não é requisito para construir E1–E5 por mera menção a integração. Os gates comerciais exigem as capacidades prometidas na edição; capacidades futuras não são removidas do roadmap. F9 conserva seus pré-requisitos anteriores de validação técnica e protocolo de conflitos.

FOL05 passa a apontar E0/E5: desenho em E0, migração em E5. FOL11 aponta E0/E4: desenho em E0, integração financeira em E4. A sincronização desses dois campos de fase foi necessária para evitar que JSON/matriz/eixo/candidata ainda indicassem conclusão das integrações em E3. Os demais campos de fase e estados das capacidades permanecem preservados.

### Sequência corrigida

F0 → F1 → F2-A → E0 → E1 → F2-B → F3 → F4 → núcleo F5 → F6 → F7 → E2 → E3 → E4 → E5 → integração de custos F5/E5 → F8 → F9 → F10.

É uma ordenação executável possível, não cronograma obrigatório. E0 pode anteceder F2-A; E2 e ramos independentes podem avançar após seus próprios pré-requisitos. O roadmap e a candidata registram essa distinção.

| Nó executável | Precedência usada na conferência do grafo integral |
|---|---|
| F0 | Fontes e responsáveis externos ao grafo |
| F1 | F0 |
| F2-A | F0, F1 |
| E0 | F0 |
| E1 | E0, F2-A |
| F2-B | F2-A, E1 |
| F3 | F1, F2-B |
| F4 | F1, F3 |
| Núcleo F5 | F1, F2-B, F3 |
| F6 núcleo | F2-B, F3, núcleo F5 |
| F7 | F2-B, F3 |
| E2 | E1 |
| E3 | E2 |
| E4 | E3 |
| E5 | E4 |
| Integração F5/E5 | Núcleo F5, E5 |
| F8 integral | Produtores F1–F7 e integração F5/E5 quando financeiros; contratos técnicos |
| F9 | Validação técnica e protocolo de conflitos; sem nova aresta de fase imposta nesta correção |
| F10 integral | Gates F/E e integrações aplicáveis às capacidades prometidas; segurança e infraestrutura |

DFS/ordenação topológica do modelo de 19 nós executáveis: nenhum ciclo. Verificados também os pré-requisitos entre as 56 capacidades: nenhum ciclo. As 17 linhas de fases agregadas F0–F10/E0–E5, com entregas, dependências e gates, são textualmente iguais entre roadmap e candidata. Vínculos relacionados/funcionais não são tratados como arestas de execução; os próprios documentos explicitam isso. A verificação não presume que desenho ou homologação futura tenha sido executado.

**Resultado P2:** circularidade eliminada, recortes e ordem verificáveis, sem remoção de funcionalidades do planejamento. Não foi encontrado outro ciclo no modelo de precedência obrigatório examinado.

## 3. Integridade e rastreabilidade

| Verificação executada | Resultado |
|---|---|
| RFs e RNFs | 218 RFs consecutivos/únicos e 12 RNFs preservados; comparação integral dos registros com baseline |
| Redações originais | 230 redações extraídas diretamente da v1.4 conferidas no inventário, matriz e candidata |
| Classificação RF | 37 implementados e validados, 74 parciais, 103 planejados, 4 dependentes de validação, sem mudança |
| Capacidades futuras | 56 IDs; 20 planejadas e 36 dependentes de validação; nenhum estado alterado |
| Campos de capacidades | Somente descrição/aceite de FOL03/04/05/10/11 e fase de FOL05/11 |
| Outros dados do inventário | RF/RNF, decisões, propostas, fontes, scripts, módulos e endpoints idênticos ao baseline |
| Histórico no DOCX | 494 elementos originais estruturalmente idênticos à v1.4 |
| v1.4 oficial | Bytes idênticos ao blob de HEAD e hash inicial |
| DOCX candidato | ZIP/CRC/XML válidos; 22 partes; somente word/document.xml modificado |
| Navegação estrutural | Campo TOC único, 324 bookmarks únicos, 28 links internos em cache com destinos existentes; partes de header/footer/settings preservadas |
| Sincronização | Descrições/aceites/fases afetados, resumo E1, governança/cenários e linhas de fase conferidos entre os documentos correspondentes |

RF035 permanece parcial; suas evidências e lacuna COMPRADO não foram alteradas. Os estados futuros da folha não contam como entrega de funcionalidade. Não foi encontrada nova contradição nos recortes alterados; ressalvas não bloqueantes anteriores permanecem explícitas, sem alegação de auditoria integral renovada de todas as regras futuras.

## 4. Exportação e inspeção visual

O renderizador empacotado foi tentado e falhou por ausência de `pdf2image`; log preservado. Nenhum pacote foi instalado. Nesta sessão Windows, sem loader de dependências de workspace disponível, foi usada a alternativa já existente: edição determinística de OOXML com biblioteca padrão, Word instalado para exportação em somente leitura e PyMuPDF instalado para renderização. Word atualizou campos em memória e fechou sem salvar; não regravou a candidata nem a v1.4.

Exportação de entrega: **178 páginas**, observadas em `EXPORT_OK PAGES=178`. O valor anterior 175 não foi presumido nem mantido como contagem corrente. Foram renderizadas as 178 páginas e 23 pranchas. A revisão geral percorreu todas as pranchas; a última sincronização do resumo E1 alterou somente o bitmap da página 47, reinspecionada individualmente. Os bitmaps das outras 177 páginas coincidem com a exportação imediatamente anterior já examinada.

Revisão individual adicional a 120 dpi: páginas 2–3 (sumário), 12–14 (tabelas/ordem), 18–20 (governança, falhas e critérios), 29–30/32–33 (capacidades afetadas) e 47 (resumo dependente). Foram também conferidas nas pranchas a passagem para matriz, RNFs e história, continuidade de cabeçalhos/rodapés e término do documento. Nenhum conteúdo novo cortado ou sobreposição impeditiva foi encontrado nessas páginas.

| Navegação/layout da nova exportação | Evidência |
|---|---|
| Sumário | 46 links internos; destinos conferidos; roadmap p.12, sequência p.13, governança p.18, folha p.28, matriz p.48, RNFs p.146 e seção histórica p.154 |
| Estrutura navegável | 415 entradas no outline PDF; aumento de quatro subtítulos novos da governança |
| Paginação | Contínua até 178; corpo histórico inicia com capa v1.4 em p.155, preservado |
| Checagem geométrica | Nenhuma caixa de palavra fora da página; nenhuma página com menos de 20 palavras |
| Identificação | Candidata v1.5 e seção histórica v1.4 mantidas, sem modificação dos arquivos oficiais |

Limites preservados: a inspeção geral em pranchas não equivale a leitura ampliada individual de cada glifo; não houve impressão física ou teste de outros editores/leitor de tela. O DOCX conserva cache de sumário que exige atualização de campos no editor, como já documentado em A04. Cabeçalho de propostas isolado em p.9 (A05) e aceites futuros genéricos (A03) permanecem ressalvas não bloqueantes; não foram corrigidos fora do escopo autorizado.

## 5. Evidências preservadas

Diretório: `C:\Users\vinic\AppData\Local\Temp\bes-correcao-bloqueios-finais-v15-20261009`.

- `manifest-inicial.json`: hashes dos 320 arquivos rastreados/não rastreados existentes no início. Foram alterados somente cinco documentos preexistentes; os demais 315 arquivos permaneceram idênticos, inclusive código, testes, documentos oficiais, auditorias e fontes anteriores.
- `antes-*`: cópias dos cinco documentos afetados; diffs textuais finais de cada Markdown/JSON.
- `corrigir.py`, `sincronizar-fases.py`, `verificar-final.py`: procedimentos de edição e conferência; nenhuma cópia adicionada ao repositório.
- `grafo-fases.json`: 19 nós, pré-requisitos e ordenação topológica; `integridade-final.json`: contagens, hashes, preservação e sincronização.
- `render-packaged.log`, logs Word intermediários e `render-entrega-word.log`: falha da ferramenta empacotada e exportações alternativas.
- `candidata-entrega-word.pdf`, `qa-entrega`, `visual-final.json` e `comparacao-exportacoes.json`: PDF de entrega, PNGs/pranchas, métricas e comparação dos bitmaps.

| Artefato corrente | SHA-256 |
|---|---|
| v1.4 oficial intacta | `f2c2289fd899834e9868b7102c869e4417fcff460b5d6c4c359bc9f52922865b` |
| DOCX candidato corrigido | `74c5aa1e442c0006678c2d7ebabd6aefe81755cf9a7a1657f33a1810a00a936e` |
| PDF da exportação de entrega | `d6e1189a027d91cebc014b6947ae35cdadda09edfb83850a5ca1a5b04e95bb26` |

Não foram reexecutados backend 568, Node 125, Playwright 109, premium 14, build ou npm audit. Esses resultados permanecem históricos da etapa premium; não são validação nova desta correção documental. Não foi necessário iniciar servidor.

## 6. Riscos residuais e pendências

Os dois bloqueios documentais não permanecem pendentes na verificação do autor. Resta a verificação final independente, sem aprovação automática de commit.

Permanecem: HTTP 500 histórico do Bloco 5 sem causa comprovada; homologação real de banco/schema/locks; auditoria premium integral; touch/leitor de tela/dispositivos/impressão reais; validação SST/jurídica e trabalhista/contábil/tributária; LGPD/retenção e isolamento empresarial/individual; integrações externas e financeiras, autorização de pagamentos reais; custeio/precisão/idempotência não universais; rotação de credenciais históricas, HTTPS, SCA backend, observabilidade e backup/restore homologados. Nenhuma dessas pendências foi declarada resolvida pela documentação. Produção continua não autorizada.

Os cenários CA-P1 exigem futura implementação e testes positivos/negativos de concorrência, replay, rollback, autorização, privacidade, importação, retificação e conciliação, com validação competente. A explicitação desses critérios não prova sua execução.

## 7. Arquivos e Git final

Atualizados nesta rodada, somente por necessidade de P1/P2:

1. `docs/Documentacao_Mestre_Plataforma_BES_v1_5_Candidata.docx` — governança e capacidades da folha, fases F/E e resumo E1.
2. `docs/bes-eixo-rh-dp-folha-beneficios-financeiro-v1_5.md` — invariante, estados/transições, responsabilidades, critérios/falhas e fases sincronizadas.
3. `docs/inventario-tecnico-bes-v1_5.json` — dez campos descrição/aceite e dois campos de fase; formatação anterior preservada.
4. `docs/matriz-rastreabilidade-bes-v1_5.md` — mesmos campos e nota de rastreabilidade dos incrementos F2/F5.
5. `docs/roadmap-integral-bes-v1_5.md` — dependências, recortes, gates e ordem executável.

Criado: `docs/bes-correcao-bloqueios-finais-v1_5.md`. Seis documentos escritos nesta rodada; nenhum outro arquivo preexistente modificado. Relatórios anteriores continuam históricos e preservados, inclusive o parecer que solicitou estas correções.

Estado agregado final: branch/HEAD acima preservados; staging vazio; **três rastreados modificados e onze não rastreados**. Os três modificados preexistentes são AGENTS.md, docs/README.md e decisões de produto; não foram editados nesta rodada. O novo arquivo desta rodada é exclusivamente o relatório de correção. Os outros dez documentos não rastreados já existiam; cinco deles foram atualizados conforme escopo.

`git status --short`, `git diff --check` e `git diff --stat` executados. Diff rastreado: três arquivos/21 inserções preexistentes; documentos não rastreados não aparecem nesse stat, por isso foram comparados separadamente ao manifest/cópias iniciais. `git diff --check` e `git diff --cached --check` sem erro; aviso preexistente LF/CRLF de decisões de produto mantido. Whitespace/EOF dos documentos novos/afetados conferidos separadamente.

Encerrado sem commit/push, preservando as alterações e aguardando verificação independente e autorização para a próxima etapa.

BLOQUEIOS DOCUMENTAIS P1/P2 CORRIGIDOS —
AGUARDANDO VERIFICAÇÃO FINAL INDEPENDENTE.
