# Matriz de rastreabilidade BES v1.5 candidata

218 RF e 12 RNF preservados; nenhuma nova numeração RF atribuída. Os campos completos também estão em inventario-tecnico-bes-v1_5.json. Estados referem-se ao núcleo e evidências anteriores; não à homologação de produção. Prioridades e fases são propostas de planejamento. Testes associados são suites/fontes, não afirmação de cobertura integral de cada RF. PLANEJADO não é ausência do requisito no roadmap.

Fases agregadas F2/F5 preservadas: F2-A é fundação empresarial; E1 exige E0/F2-A; F2-B integra vínculos após E1. Núcleo F5 não exige folha; custos de folha/benefícios integram após E5. Ver sequência executável no roadmap-integral-bes-v1_5.md e governança/cenários CA-P1 no bes-eixo-rh-dp-folha-beneficios-financeiro-v1_5.md. São incrementos de planejamento, sem alteração dos estados de implementação.

| Estado RF | Quantidade |
|---|---:|
| IMPLEMENTADO E VALIDADO | 37 |
| PARCIALMENTE IMPLEMENTADO | 74 |
| EM DESENVOLVIMENTO | 0 |
| PLANEJADO | 103 |
| PROPOSTA NOVA | 0 |
| DEPENDENTE DE VALIDAÇÃO | 4 |

## RF001

Cadastro de funcionários: nome, matrícula, cargo/função, setor, telefone, e-mail e situação.

- Módulo: Funcionários e histórico ocupacional. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: Nome/matrícula/função existentes; setor, telefone, e-mail, situação e vigências ausentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/model/Funcionario.java; src/main/java/br/com/almoxarifado/service/FuncionarioService.java; frontend/src/pages/Registries.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/controller/BackendOperacionalTests.java; frontend/tests/frontend.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF002, RF003..
- Prioridade proposta: P1. Fase proposta: F2. Risco: Dados pessoais e histórico de função.
- Critério de aceite: Demonstrar cenário nominal específico de RF001: Cadastro de funcionários: nome, matrícula, cargo/função, setor, telefone, e-mail e situação. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Nome/matrícula/função existentes; setor, telefone, e-mail, situação e vigências ausentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF002

Cadastro de usuários: usuários vinculados a funcionários.

- Módulo: Administração e segurança. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: Usuário existente com vínculo opcional ao funcionário; não impor vínculo obrigatório fictício.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF053, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Demonstrar cenário nominal específico de RF002: Cadastro de usuários: usuários vinculados a funcionários. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Usuário existente com vínculo opcional ao funcionário; não impor vínculo obrigatório fictício. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF003

Perfis de acesso: administrador, almoxarife, gestor, funcionário e permissões refináveis.

- Módulo: Administração e segurança. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: ADMIN/GESTOR/ALMOXARIFE/CONSULTA; perfil FUNCIONÁRIO e permissões refináveis por cliente não entregues.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF053, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Demonstrar cenário nominal específico de RF003: Perfis de acesso: administrador, almoxarife, gestor, funcionário e permissões refináveis. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: ADMIN/GESTOR/ALMOXARIFE/CONSULTA; perfil FUNCIONÁRIO e permissões refináveis por cliente não entregues. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF004

Cadastro de setores: setores da empresa.

- Módulo: Empresas unidades e departamentos. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): DP01, DP09..
- Prioridade proposta: P2. Fase proposta: F2. Risco: Vazamento entre clientes e regras exclusivas.
- Critério de aceite: Demonstrar cenário nominal específico de RF004: Cadastro de setores: setores da empresa. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF005

Cadastro de categorias: categorias de ferramentas, máquinas, materiais e consumíveis.

- Módulo: Catálogo mestre. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/catalogo-mestre.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/ProdutoService.java; src/main/java/br/com/almoxarifado/service/UnidadeMedidaService.java; src/main/java/br/com/almoxarifado/model/Produto.java; frontend/src/components/ProductPicker.jsx; frontend/src/pages/Products.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/controller/CatalogoMestreTests.java; frontend/tests/frontend.spec.js; frontend/tests/api.test.js.
- Dependências: Pré-requisitos: Unicidade/codificação e unidade aprovadas. Vínculos funcionais (não uma ordem de execução): RF193, RF204..
- Prioridade proposta: P1. Fase proposta: F1/F2. Risco: Duplicação de produtos ou unidade incompatível.
- Critério de aceite: Criar/editar/inativar categoria mantendo ID; normalização equivalente deve retornar conflito e categorias inativas continuam legíveis no histórico. Homologação e gates comuns não são declarados concluídos.

## RF006

Ferramentas e máquinas: nome, patrimônio/TAG, marca, modelo, série, categoria, compra, valor, localização, condição e status.

- Módulo: Ferramentas ativos e custódia. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Ativo identificado e custódia; valor financeiro não entregue.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Demonstrar cenário nominal específico de RF006: Ferramentas e máquinas: nome, patrimônio/TAG, marca, modelo, série, categoria, compra, valor, localização, condição e status. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Ativo identificado e custódia; valor financeiro não entregue. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF007

Materiais consumíveis: nome, categoria, unidade, estoque atual, mínimo, máximo, custo e localização.

- Módulo: Catálogo mestre. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/catalogo-mestre.md.
- Evidência e limite: Produto/unidade/saldo/limites/local entregues; custo não implementado.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/ProdutoService.java; src/main/java/br/com/almoxarifado/service/UnidadeMedidaService.java; src/main/java/br/com/almoxarifado/model/Produto.java; frontend/src/components/ProductPicker.jsx; frontend/src/pages/Products.jsx; src/main/java/br/com/almoxarifado/model/Estoque.java; src/main/java/br/com/almoxarifado/service/EstoqueInteligenteService.java.
- Testes associados: src/test/java/br/com/almoxarifado/controller/CatalogoMestreTests.java; frontend/tests/frontend.spec.js; frontend/tests/api.test.js; src/test/java/br/com/almoxarifado/service/EstoqueInteligenteTransferenciaTests.java.
- Dependências: Pré-requisitos: Unicidade/codificação e unidade aprovadas. Vínculos funcionais (não uma ordem de execução): RF005, RF193, RF204..
- Prioridade proposta: P1. Fase proposta: F1/F2. Risco: Duplicação de produtos ou unidade incompatível.
- Critério de aceite: Demonstrar cenário nominal específico de RF007: Materiais consumíveis: nome, categoria, unidade, estoque atual, mínimo, máximo, custo e localização. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Produto/unidade/saldo/limites/local entregues; custo não implementado. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF008

Fornecedores: dados cadastrais, contatos e materiais/serviços relacionados.

- Módulo: Compras fornecedores e recebimento. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/compras-recebimento.md.
- Evidência e limite: Cadastro/contatos e histórico; serviços/associação independente não entregues.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/compras/PedidoCompraService.java; src/main/java/br/com/almoxarifado/compras/FornecedorService.java; frontend/src/pages/PurchaseOrders.jsx; frontend/src/pages/Suppliers.jsx; frontend/src/pages/PurchaseDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/compras/ComprasTests.java; frontend/tests/purchases.spec.js; frontend/tests/purchases.test.js.
- Dependências: Pré-requisitos: Pedido/necessidade e política de recebimento/divergência aprovados. Vínculos funcionais (não uma ordem de execução): RF079, RF193, RF203..
- Prioridade proposta: P1. Fase proposta: F1/F5. Risco: Entrada duplicada, alocação excessiva ou custo atribuído indevidamente.
- Critério de aceite: Demonstrar cenário nominal específico de RF008: Fornecedores: dados cadastrais, contatos e materiais/serviços relacionados. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Cadastro/contatos e histórico; serviços/associação independente não entregues. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF009

Entrada de materiais: compra, devolução, transferência ou ajuste autorizado.

- Módulo: Estoque e movimentações. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/estoque-inteligente.md.
- Evidência e limite: Entrada manual, transferência e compra entregues; devolução genérica/ajuste formal autorizados pendentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/EstoqueService.java; src/main/java/br/com/almoxarifado/service/EstoqueInteligenteService.java; src/main/java/br/com/almoxarifado/service/TransferenciaEstoqueService.java; frontend/src/pages/Operations.jsx; frontend/src/pages/Transfers.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/EstoqueInteligenteTransferenciaTests.java; src/test/java/br/com/almoxarifado/controller/BackendOperacionalTests.java; frontend/tests/stockIntelligence.spec.js.
- Dependências: Pré-requisitos: Unidade, política de movimento/ajuste, schema e saldo de abertura homologados. Vínculos funcionais (não uma ordem de execução): RF193, RF204, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Saldo incorreto, repetição ou concorrência.
- Critério de aceite: Demonstrar cenário nominal específico de RF009: Entrada de materiais: compra, devolução, transferência ou ajuste autorizado. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Entrada manual, transferência e compra entregues; devolução genérica/ajuste formal autorizados pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF010

Saída de materiais: consumo por funcionário, setor, obra/OS e local, conforme aplicável.

- Módulo: Estoque e movimentações. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/estoque-inteligente.md.
- Evidência e limite: Funcionário/local e snapshots Obra/OS/CC; setor e custeio completo pendentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/EstoqueService.java; src/main/java/br/com/almoxarifado/service/EstoqueInteligenteService.java; src/main/java/br/com/almoxarifado/service/TransferenciaEstoqueService.java; frontend/src/pages/Operations.jsx; frontend/src/pages/Transfers.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/EstoqueInteligenteTransferenciaTests.java; src/test/java/br/com/almoxarifado/controller/BackendOperacionalTests.java; frontend/tests/stockIntelligence.spec.js.
- Dependências: Pré-requisitos: Unidade, política de movimento/ajuste, schema e saldo de abertura homologados. Vínculos funcionais (não uma ordem de execução): RF193, RF204, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Saldo incorreto, repetição ou concorrência.
- Critério de aceite: Demonstrar cenário nominal específico de RF010: Saída de materiais: consumo por funcionário, setor, obra/OS e local, conforme aplicável. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Funcionário/local e snapshots Obra/OS/CC; setor e custeio completo pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF011

Controle de estoque: atualização automática após movimentações confirmadas.

- Módulo: Estoque e movimentações. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/estoque-inteligente.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/EstoqueService.java; src/main/java/br/com/almoxarifado/service/EstoqueInteligenteService.java; src/main/java/br/com/almoxarifado/service/TransferenciaEstoqueService.java; frontend/src/pages/Operations.jsx; frontend/src/pages/Transfers.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/EstoqueInteligenteTransferenciaTests.java; src/test/java/br/com/almoxarifado/controller/BackendOperacionalTests.java; frontend/tests/stockIntelligence.spec.js.
- Dependências: Pré-requisitos: Unidade, política de movimento/ajuste, schema e saldo de abertura homologados. Vínculos funcionais (não uma ordem de execução): RF193, RF204, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Saldo incorreto, repetição ou concorrência.
- Critério de aceite: Uma movimentação confirmada altera saldo exatamente uma vez; falha em movimento/auditoria reverte saldo e evento; disputa pelo último saldo tem um único vencedor. Homologação e gates comuns não são declarados concluídos.

## RF012

Estoque mínimo: alerta ao atingir/ficar abaixo do mínimo.

- Módulo: Estoque e movimentações. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/estoque-inteligente.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/EstoqueService.java; src/main/java/br/com/almoxarifado/service/EstoqueInteligenteService.java; src/main/java/br/com/almoxarifado/service/TransferenciaEstoqueService.java; frontend/src/pages/Operations.jsx; frontend/src/pages/Transfers.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/EstoqueInteligenteTransferenciaTests.java; src/test/java/br/com/almoxarifado/controller/BackendOperacionalTests.java; frontend/tests/stockIntelligence.spec.js.
- Dependências: Pré-requisitos: Unidade, política de movimento/ajuste, schema e saldo de abertura homologados. Vínculos funcionais (não uma ordem de execução): RF193, RF204, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Saldo incorreto, repetição ou concorrência.
- Critério de aceite: Saldo igual/abaixo do mínimo gera alerta; acima ou mínimo ausente não gera; consulta não modifica saldo. Homologação e gates comuns não são declarados concluídos.

## RF013

Sugestão de reposição: quantidade recomendada por mínimo/máximo e regras definidas.

- Módulo: Estoque e movimentações. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/estoque-inteligente.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/EstoqueService.java; src/main/java/br/com/almoxarifado/service/EstoqueInteligenteService.java; src/main/java/br/com/almoxarifado/service/TransferenciaEstoqueService.java; frontend/src/pages/Operations.jsx; frontend/src/pages/Transfers.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/EstoqueInteligenteTransferenciaTests.java; src/test/java/br/com/almoxarifado/controller/BackendOperacionalTests.java; frontend/tests/stockIntelligence.spec.js.
- Dependências: Pré-requisitos: Unidade, política de movimento/ajuste, schema e saldo de abertura homologados. Vínculos funcionais (não uma ordem de execução): RF193, RF204, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Saldo incorreto, repetição ou concorrência.
- Critério de aceite: Repor até máximo gera quantidade não negativa conferível; sem máximo não inventar sugestão; limites inválidos são rejeitados. Homologação e gates comuns não são declarados concluídos.

## RF014

Empréstimo de ferramentas: funcionário, ferramenta, data/hora, responsável e condição.

- Módulo: Ferramentas ativos e custódia. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Registrar empréstimo com funcionário/ativo/instante/ator/condição; segundo empréstimo concorrente do mesmo ativo é recusado e histórico não é sobrescrito. Homologação e gates comuns não são declarados concluídos.

## RF015

Previsão de devolução: data esperada.

- Módulo: Ferramentas ativos e custódia. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Persistir e consultar previsão do empréstimo; retorno encerra origem sem reescrever a previsão histórica; validar data inadmissível conforme política. Homologação e gates comuns não são declarados concluídos.

## RF016

Devolução: data/hora e condição.

- Módulo: Ferramentas ativos e custódia. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Devolução cria novo evento com instante/condição e restaura origem conforme protocolo; replay não duplica fechamento e segunda chave incompatível retorna conflito. Homologação e gates comuns não são declarados concluídos.

## RF017

Histórico de empréstimos: retiradas e devoluções.

- Módulo: Ferramentas ativos e custódia. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Consulta reúne retirada e devolução ligadas à mesma origem, com snapshots preservados após renomear cadastro e sem multiplicar registros. Homologação e gates comuns não são declarados concluídos.

## RF018

Responsável atual: quem está com o equipamento.

- Módulo: Ferramentas ativos e custódia. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Consulta distingue responsável atual de ator autenticado; encerramento remove custódia atual e mantém responsável histórico; não há duas custódias abertas do ativo. Homologação e gates comuns não são declarados concluídos.

## RF019

Status de equipamento: disponível, em uso, reservado, manutenção, danificado, baixado.

- Módulo: Ferramentas ativos e custódia. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Estados operacionais entregues; reserva e manutenção não são workflows implementados.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Demonstrar cenário nominal específico de RF019: Status de equipamento: disponível, em uso, reservado, manutenção, danificado, baixado. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Estados operacionais entregues; reserva e manutenção não são workflows implementados. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF020

Reserva: reserva de ferramenta para uso futuro.

- Módulo: Ferramentas ativos e custódia. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P2. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Demonstrar cenário nominal específico de RF020: Reserva: reserva de ferramenta para uso futuro. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF021

Manutenção: preventiva/corretiva.

- Módulo: Manutenção ciclo de vida e calibração. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF006, RF019, RF051, RF164..
- Prioridade proposta: P2. Fase proposta: F4. Risco: Disponibilidade de equipamento e segurança de uso.
- Critério de aceite: Demonstrar cenário nominal específico de RF021: Manutenção: preventiva/corretiva. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF022

Dados de manutenção: defeito, serviço, responsável, fornecedor, datas e custo.

- Módulo: Manutenção ciclo de vida e calibração. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF006, RF019, RF051, RF164..
- Prioridade proposta: P2. Fase proposta: F4. Risco: Disponibilidade de equipamento e segurança de uso.
- Critério de aceite: Demonstrar cenário nominal específico de RF022: Dados de manutenção: defeito, serviço, responsável, fornecedor, datas e custo. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF023

Histórico de manutenção: por equipamento.

- Módulo: Manutenção ciclo de vida e calibração. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF006, RF019, RF051, RF164..
- Prioridade proposta: P2. Fase proposta: F4. Risco: Disponibilidade de equipamento e segurança de uso.
- Critério de aceite: Demonstrar cenário nominal específico de RF023: Histórico de manutenção: por equipamento. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF024

Preventiva: periodicidade e próxima data.

- Módulo: Manutenção ciclo de vida e calibração. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF006, RF019, RF051, RF164..
- Prioridade proposta: P2. Fase proposta: F4. Risco: Disponibilidade de equipamento e segurança de uso.
- Critério de aceite: Demonstrar cenário nominal específico de RF024: Preventiva: periodicidade e próxima data. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF025

Alerta de manutenção: próxima/vencida.

- Módulo: Manutenção ciclo de vida e calibração. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF006, RF019, RF051, RF164..
- Prioridade proposta: P2. Fase proposta: F4. Risco: Disponibilidade de equipamento e segurança de uso.
- Critério de aceite: Demonstrar cenário nominal específico de RF025: Alerta de manutenção: próxima/vencida. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF026

Equipamento danificado: status e motivo.

- Módulo: Ferramentas ativos e custódia. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Condição danificada e motivo ficam rastreáveis no evento; nova retirada incompatível é bloqueada; nenhuma regra atribui culpa automaticamente. Homologação e gates comuns não são declarados concluídos.

## RF027

Baixa de equipamento: dano, perda, descarte, furto ou fim de vida.

- Módulo: Ferramentas ativos e custódia. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Baixa motivada de ativo livre registra categoria de motivo e bloqueia operações futuras; custódia pendente impede baixa; saldo quantitativo não é alterado. Homologação e gates comuns não são declarados concluídos.

## RF028

Motivo da baixa: justificativa, responsável e data.

- Módulo: Ferramentas ativos e custódia. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Baixa exige motivo/ator/instante e conserva histórico; ausência de justificativa é rejeitada sem evento parcial; leitura autorizada permite conferir origem. Homologação e gates comuns não são declarados concluídos.

## RF029

Valor da perda: impacto financeiro.

- Módulo: Relatórios dashboards e custos. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF063, RF148..
- Prioridade proposta: P2. Fase proposta: F5/F8. Risco: Indicador incorreto ou alegação financeira sem método.
- Critério de aceite: Demonstrar cenário nominal específico de RF029: Valor da perda: impacto financeiro. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF030

Localização: onde cada item está.

- Módulo: Estoque e movimentações. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/estoque-inteligente.md.
- Evidência e limite: Saldo por almoxarifado e localização de ativo; localização fina e todos os futuros itens pendentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/EstoqueService.java; src/main/java/br/com/almoxarifado/service/EstoqueInteligenteService.java; src/main/java/br/com/almoxarifado/service/TransferenciaEstoqueService.java; frontend/src/pages/Operations.jsx; frontend/src/pages/Transfers.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/EstoqueInteligenteTransferenciaTests.java; src/test/java/br/com/almoxarifado/controller/BackendOperacionalTests.java; frontend/tests/stockIntelligence.spec.js.
- Dependências: Pré-requisitos: Unidade, política de movimento/ajuste, schema e saldo de abertura homologados. Vínculos funcionais (não uma ordem de execução): RF193, RF204, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Saldo incorreto, repetição ou concorrência.
- Critério de aceite: Demonstrar cenário nominal específico de RF030: Localização: onde cada item está. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Saldo por almoxarifado e localização de ativo; localização fina e todos os futuros itens pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF031

Transferência: movimentar entre almoxarifados/locais.

- Módulo: Estoque e movimentações. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/estoque-inteligente.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/EstoqueService.java; src/main/java/br/com/almoxarifado/service/EstoqueInteligenteService.java; src/main/java/br/com/almoxarifado/service/TransferenciaEstoqueService.java; frontend/src/pages/Operations.jsx; frontend/src/pages/Transfers.jsx; src/main/java/br/com/almoxarifado/ativos/AtivosService.java.
- Testes associados: src/test/java/br/com/almoxarifado/service/EstoqueInteligenteTransferenciaTests.java; src/test/java/br/com/almoxarifado/controller/BackendOperacionalTests.java; frontend/tests/stockIntelligence.spec.js; src/test/java/br/com/almoxarifado/ativos/AtivosTests.java.
- Dependências: Pré-requisitos: Unidade, política de movimento/ajuste, schema e saldo de abertura homologados. Vínculos funcionais (não uma ordem de execução): RF193, RF204, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Saldo incorreto, repetição ou concorrência.
- Critério de aceite: Transferir quantitativos preserva saldos e pares de eventos; transferência de ativo mantém origem até chegada; falha total reverte tudo e os protocolos não misturam locks. Homologação e gates comuns não são declarados concluídos.

## RF032

Movimentações: entradas, saídas, transferências, ajustes, empréstimos e devoluções.

- Módulo: Estoque e movimentações. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/estoque-inteligente.md.
- Evidência e limite: Eventos dos módulos atuais; ajustes/estornos e visão universal não implementados.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/EstoqueService.java; src/main/java/br/com/almoxarifado/service/EstoqueInteligenteService.java; src/main/java/br/com/almoxarifado/service/TransferenciaEstoqueService.java; frontend/src/pages/Operations.jsx; frontend/src/pages/Transfers.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/EstoqueInteligenteTransferenciaTests.java; src/test/java/br/com/almoxarifado/controller/BackendOperacionalTests.java; frontend/tests/stockIntelligence.spec.js.
- Dependências: Pré-requisitos: Unidade, política de movimento/ajuste, schema e saldo de abertura homologados. Vínculos funcionais (não uma ordem de execução): RF193, RF204, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Saldo incorreto, repetição ou concorrência.
- Critério de aceite: Demonstrar cenário nominal específico de RF032: Movimentações: entradas, saídas, transferências, ajustes, empréstimos e devoluções. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Eventos dos módulos atuais; ajustes/estornos e visão universal não implementados. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF033

Consulta de histórico: filtros por funcionário, item, período, setor, OS, local e tipo.

- Módulo: Estoque e movimentações. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/estoque-inteligente.md.
- Evidência e limite: Filtros nos módulos atuais; setor e consulta universal completa pendentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/EstoqueService.java; src/main/java/br/com/almoxarifado/service/EstoqueInteligenteService.java; src/main/java/br/com/almoxarifado/service/TransferenciaEstoqueService.java; frontend/src/pages/Operations.jsx; frontend/src/pages/Transfers.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/EstoqueInteligenteTransferenciaTests.java; src/test/java/br/com/almoxarifado/controller/BackendOperacionalTests.java; frontend/tests/stockIntelligence.spec.js.
- Dependências: Pré-requisitos: Unidade, política de movimento/ajuste, schema e saldo de abertura homologados. Vínculos funcionais (não uma ordem de execução): RF193, RF204, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Saldo incorreto, repetição ou concorrência.
- Critério de aceite: Demonstrar cenário nominal específico de RF033: Consulta de histórico: filtros por funcionário, item, período, setor, OS, local e tipo. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Filtros nos módulos atuais; setor e consulta universal completa pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF034

Necessidade/pedido de compra: criar reposição/compra a partir de demanda aprovada.

- Módulo: Compras fornecedores e recebimento. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/compras-recebimento.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/compras/PedidoCompraService.java; src/main/java/br/com/almoxarifado/compras/FornecedorService.java; frontend/src/pages/PurchaseOrders.jsx; frontend/src/pages/Suppliers.jsx; frontend/src/pages/PurchaseDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/compras/ComprasTests.java; frontend/tests/purchases.spec.js; frontend/tests/purchases.test.js.
- Dependências: Pré-requisitos: Pedido/necessidade e política de recebimento/divergência aprovados. Vínculos funcionais (não uma ordem de execução): RF079, RF193, RF203..
- Prioridade proposta: P1. Fase proposta: F1/F5. Risco: Entrada duplicada, alocação excessiva ou custo atribuído indevidamente.
- Critério de aceite: Necessidade oriunda de demanda aprovada preserva vínculo; pedido aloca sem sobrecomprar nem movimentar estoque; autorização e limites são conferidos. Homologação e gates comuns não são declarados concluídos.

## RF035

Status de compra: solicitado, aprovado, comprado, parcialmente recebido, recebido, cancelado.

- Módulo: Compras fornecedores e recebimento. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/compras-recebimento.md.
- Evidência e limite: RASCUNHO, AGUARDANDO_APROVACAO, APROVADO, PARCIALMENTE_RECEBIDO, RECEBIDO e CANCELADO entregues. Estado COMPRADO/compra externa não separado; nenhuma nova transição implementada nesta rodada.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/compras/StatusPedidoCompra.java; src/main/java/br/com/almoxarifado/compras/PedidoCompraService.java; frontend/src/pages/PurchaseOrders.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/compras/ComprasTests.java; frontend/tests/purchases.spec.js.
- Dependências: Pré-requisitos: Pedido/necessidade e política de recebimento/divergência aprovados. Vínculos funcionais (não uma ordem de execução): RF079, RF193, RF203..
- Prioridade proposta: P1. Fase proposta: F1/F5. Risco: Entrada duplicada, alocação excessiva ou custo atribuído indevidamente.
- Critério de aceite: Conferir integralmente a redação de RF035; Receber parcial com ENTRADA atômica e replay; preservar alocação e separar aprovação, recebimento e atendimento. Cobertura parcial deve demonstrar o fechamento da lacuna registrada; homologação real é gate separado. Complemento específico: provar as seis transições entregues, rejeitar transição inválida e registrar COMPRADO/compra externa como lacuna; não presumir aprovação equivalente a compra efetiva.

## RF036

Preços: preço unitário e total.

- Módulo: Compras fornecedores e recebimento. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/compras-recebimento.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/compras/PedidoCompraService.java; src/main/java/br/com/almoxarifado/compras/FornecedorService.java; frontend/src/pages/PurchaseOrders.jsx; frontend/src/pages/Suppliers.jsx; frontend/src/pages/PurchaseDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/compras/ComprasTests.java; frontend/tests/purchases.spec.js; frontend/tests/purchases.test.js.
- Dependências: Pré-requisitos: Pedido/necessidade e política de recebimento/divergência aprovados. Vínculos funcionais (não uma ordem de execução): RF079, RF193, RF203..
- Prioridade proposta: P1. Fase proposta: F1/F5. Risco: Entrada duplicada, alocação excessiva ou custo atribuído indevidamente.
- Critério de aceite: Preço unitário/quantidade/total calculados no servidor com regra decimal/rounding explícita; entrada inválida não persiste; valores históricos sobrevivem à edição posterior de cadastro. Homologação e gates comuns não são declarados concluídos.

## RF037

Histórico de fornecedores: compras, preços e itens.

- Módulo: Compras fornecedores e recebimento. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/compras-recebimento.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/compras/PedidoCompraService.java; src/main/java/br/com/almoxarifado/compras/FornecedorService.java; frontend/src/pages/PurchaseOrders.jsx; frontend/src/pages/Suppliers.jsx; frontend/src/pages/PurchaseDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/compras/ComprasTests.java; frontend/tests/purchases.spec.js; frontend/tests/purchases.test.js.
- Dependências: Pré-requisitos: Pedido/necessidade e política de recebimento/divergência aprovados. Vínculos funcionais (não uma ordem de execução): RF079, RF193, RF203..
- Prioridade proposta: P1. Fase proposta: F1/F5. Risco: Entrada duplicada, alocação excessiva ou custo atribuído indevidamente.
- Critério de aceite: Consultar fornecedor identifica pedidos, itens e preços históricos; inativação não apaga histórico; permissão inferior mantém documento/contato protegidos. Homologação e gates comuns não são declarados concluídos.

## RF038

Dashboard: indicadores conforme perfil.

- Módulo: Relatórios dashboards e custos. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Dashboard operacional; visão executiva/personalizada e todos módulos futuros pendentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/obras/ResumoEstruturaService.java; frontend/src/pages/Dashboard.jsx; frontend/src/pages/Structure.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/obras/ObrasTests.java; frontend/tests/structure.spec.js; frontend/tests/frontend.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF063, RF148..
- Prioridade proposta: P1. Fase proposta: F5/F8. Risco: Indicador incorreto ou alegação financeira sem método.
- Critério de aceite: Demonstrar cenário nominal específico de RF038: Dashboard: indicadores conforme perfil. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Dashboard operacional; visão executiva/personalizada e todos módulos futuros pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF039

Relatório de estoque: quantidade, mínimo, máximo e situação.

- Módulo: Relatórios dashboards e custos. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Consulta de estoque/limites/situação; relatório/exportação dedicado incompleto.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/obras/ResumoEstruturaService.java; frontend/src/pages/Dashboard.jsx; frontend/src/pages/Structure.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/obras/ObrasTests.java; frontend/tests/structure.spec.js; frontend/tests/frontend.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF063, RF148..
- Prioridade proposta: P1. Fase proposta: F5/F8. Risco: Indicador incorreto ou alegação financeira sem método.
- Critério de aceite: Demonstrar cenário nominal específico de RF039: Relatório de estoque: quantidade, mínimo, máximo e situação. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Consulta de estoque/limites/situação; relatório/exportação dedicado incompleto. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF040

Relatório de consumo: materiais mais consumidos.

- Módulo: Relatórios dashboards e custos. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Movimentos/resumos quantitativos; ranking de consumo e relatório completo pendentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/obras/ResumoEstruturaService.java; frontend/src/pages/Dashboard.jsx; frontend/src/pages/Structure.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/obras/ObrasTests.java; frontend/tests/structure.spec.js; frontend/tests/frontend.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF063, RF148..
- Prioridade proposta: P1. Fase proposta: F5/F8. Risco: Indicador incorreto ou alegação financeira sem método.
- Critério de aceite: Demonstrar cenário nominal específico de RF040: Relatório de consumo: materiais mais consumidos. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Movimentos/resumos quantitativos; ranking de consumo e relatório completo pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF041

Relatório por setor: consumo e custos.

- Módulo: Relatórios dashboards e custos. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Contexto CC não é cadastro/setor nem método de custo; relatório por setor incompleto.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/obras/ResumoEstruturaService.java; frontend/src/pages/Dashboard.jsx; frontend/src/pages/Structure.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/obras/ObrasTests.java; frontend/tests/structure.spec.js; frontend/tests/frontend.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF063, RF148..
- Prioridade proposta: P1. Fase proposta: F5/F8. Risco: Indicador incorreto ou alegação financeira sem método.
- Critério de aceite: Demonstrar cenário nominal específico de RF041: Relatório por setor: consumo e custos. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Contexto CC não é cadastro/setor nem método de custo; relatório por setor incompleto. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF042

Relatório por funcionário: ferramentas e movimentações autorizadas.

- Módulo: Relatórios dashboards e custos. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Consulta de custódia/EPI por funcionário; relatório global autorizado incompleto.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/obras/ResumoEstruturaService.java; frontend/src/pages/Dashboard.jsx; frontend/src/pages/Structure.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/obras/ObrasTests.java; frontend/tests/structure.spec.js; frontend/tests/frontend.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF063, RF148..
- Prioridade proposta: P1. Fase proposta: F5/F8. Risco: Indicador incorreto ou alegação financeira sem método.
- Critério de aceite: Demonstrar cenário nominal específico de RF042: Relatório por funcionário: ferramentas e movimentações autorizadas. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Consulta de custódia/EPI por funcionário; relatório global autorizado incompleto. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF043

Relatório de equipamentos: situações dos ativos.

- Módulo: Relatórios dashboards e custos. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Consulta de situação de ativos; relatório global/exportações incompletos.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/obras/ResumoEstruturaService.java; frontend/src/pages/Dashboard.jsx; frontend/src/pages/Structure.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/obras/ObrasTests.java; frontend/tests/structure.spec.js; frontend/tests/frontend.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF063, RF148..
- Prioridade proposta: P1. Fase proposta: F5/F8. Risco: Indicador incorreto ou alegação financeira sem método.
- Critério de aceite: Demonstrar cenário nominal específico de RF043: Relatório de equipamentos: situações dos ativos. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Consulta de situação de ativos; relatório global/exportações incompletos. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF044

Relatório de manutenção: custos, quantidade e histórico.

- Módulo: Relatórios dashboards e custos. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF063, RF148..
- Prioridade proposta: P2. Fase proposta: F5/F8. Risco: Indicador incorreto ou alegação financeira sem método.
- Critério de aceite: Demonstrar cenário nominal específico de RF044: Relatório de manutenção: custos, quantidade e histórico. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF045

Relatório financeiro: custos de materiais, manutenção, baixas e categorias integradas.

- Módulo: Relatórios dashboards e custos. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF063, RF148..
- Prioridade proposta: P2. Fase proposta: F5/F8. Risco: Indicador incorreto ou alegação financeira sem método.
- Critério de aceite: Demonstrar cenário nominal específico de RF045: Relatório financeiro: custos de materiais, manutenção, baixas e categorias integradas. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF046

Atrasos: ferramentas não devolvidas no prazo.

- Módulo: Ferramentas ativos e custódia. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Somente empréstimos abertos além da previsão integram vencidos; fechado não aparece; filtros contraditórios não omitem a regra. Homologação e gates comuns não são declarados concluídos.

## RF047

Alertas de atraso: notificação de empréstimo vencido.

- Módulo: Ferramentas ativos e custódia. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Atrasos visíveis em resumo/consulta; notificação automática não entregue.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Demonstrar cenário nominal específico de RF047: Alertas de atraso: notificação de empréstimo vencido. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Atrasos visíveis em resumo/consulta; notificação automática não entregue. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF048

QR/código de barras: identificação rápida.

- Módulo: QR barcode e inventário móvel. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF095, RF169, RF212..
- Prioridade proposta: P1. Fase proposta: F3/F4. Risco: QR conceder acesso ou executar escrita sem autoridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF048: QR/código de barras: identificação rápida. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF049

Consulta por QR: abrir dados do item.

- Módulo: QR barcode e inventário móvel. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF095, RF169, RF212..
- Prioridade proposta: P1. Fase proposta: F3/F4. Risco: QR conceder acesso ou executar escrita sem autoridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF049: Consulta por QR: abrir dados do item. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF050

Retirada por QR: iniciar empréstimo.

- Módulo: QR barcode e inventário móvel. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF095, RF169, RF212..
- Prioridade proposta: P2. Fase proposta: F3/F4. Risco: QR conceder acesso ou executar escrita sem autoridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF050: Retirada por QR: iniciar empréstimo. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF051

Anexos: fotos, NF, orçamentos e documentos.

- Módulo: Documentos anexos e emissão. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF051: Anexos: fotos, NF, orçamentos e documentos. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF052

Foto do equipamento: imagem no cadastro.

- Módulo: Documentos anexos e emissão. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P2. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF052: Foto do equipamento: imagem no cadastro. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF053

Auditoria: registrar ações relevantes.

- Módulo: Administração e segurança. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: Auditoria nos módulos presentes; trilha de todos os módulos futuros não entregue.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Demonstrar cenário nominal específico de RF053: Auditoria: registrar ações relevantes. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Auditoria nos módulos presentes; trilha de todos os módulos futuros não entregue. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF054

Data/hora: timestamp em operações importantes.

- Módulo: Administração e segurança. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: Timestamps atuais preservados; cobertura transversal futura e política UTC/fuso ainda pendentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF053, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Demonstrar cenário nominal específico de RF054: Data/hora: timestamp em operações importantes. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Timestamps atuais preservados; cobertura transversal futura e política UTC/fuso ainda pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF055

Pesquisa/filtros: nome, patrimônio, categoria, funcionário, setor, OS, status e período.

- Módulo: Busca transversal nos módulos atuais. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: Busca e filtros reais; conjunto universal por setor/OS/patrimônio/período incompleto.
- Arquivos de implementação: frontend/src/pages/Products.jsx; frontend/src/pages/Assets.jsx; frontend/src/pages/Structure.jsx.
- Testes associados: frontend/tests/frontend.spec.js; frontend/tests/assets.spec.js; frontend/tests/structure.spec.js.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF033, RF193, RF057..
- Prioridade proposta: P1. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Demonstrar cenário nominal específico de RF055: Pesquisa/filtros: nome, patrimônio, categoria, funcionário, setor, OS, status e período. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Busca e filtros reais; conjunto universal por setor/OS/patrimônio/período incompleto. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF056

Autenticação: login.

- Módulo: Administração e segurança. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF053, RF148, RF150..
- Prioridade proposta: P0 de liberação. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Login válido estabelece sessão e identidade sem expor senha/hash; inválido e sessão revogada são recusados; cookie e CSRF observam a baseline vigente. Homologação e gates comuns não são declarados concluídos.

## RF057

Autorização: limitar ações por perfil/permissão.

- Módulo: Administração e segurança. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF053, RF148, RF150..
- Prioridade proposta: P0 de liberação. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Cada operação atual é testada com authority positiva/negativa no HTTP e no service; ID/QR não contorna autorização; escrita sem CSRF é rejeitada. Homologação e gates comuns não são declarados concluídos.

## RF058

Recuperação de senha: fluxo seguro.

- Módulo: Administração e segurança. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: Reset administrativo existente; recuperação segura self-service não implementada.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF053, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Demonstrar cenário nominal específico de RF058: Recuperação de senha: fluxo seguro. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Reset administrativo existente; recuperação segura self-service não implementada. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF059

Notificações: estoque, manutenção, atraso, compras, documentos etc.

- Módulo: Alertas notificações e escalonamento. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/estoque-inteligente.md.
- Evidência e limite: Sinais em tela; central, entrega de notificações e escalonamento ausentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/EstoqueInteligenteService.java; src/main/java/br/com/almoxarifado/epi/EpiService.java; src/main/java/br/com/almoxarifado/ativos/AtivosService.java; frontend/src/pages/Dashboard.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/EstoqueInteligenteTransferenciaTests.java; src/test/java/br/com/almoxarifado/epi/EpiTests.java; src/test/java/br/com/almoxarifado/ativos/AtivosTests.java.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF012, RF025, RF047, RF104..
- Prioridade proposta: P1. Fase proposta: F3/F8. Risco: Alerta perdido, duplicado ou exposto a destinatário indevido.
- Critério de aceite: Demonstrar cenário nominal específico de RF059: Notificações: estoque, manutenção, atraso, compras, documentos etc. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sinais em tela; central, entrega de notificações e escalonamento ausentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF060

Exportação: PDF/Excel.

- Módulo: Documentos anexos e emissão. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Print/PDF navegador; Excel e exportação dedicada ausentes.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF060: Exportação: PDF/Excel. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Print/PDF navegador; Excel e exportação dedicada ausentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF061

Cadastro de obras e OS: cliente, obra, OS, período, responsáveis, status e centro de custo.

- Módulo: Obras OS e centros de custo. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/obras/EstruturaService.java; src/main/java/br/com/almoxarifado/obras/ContextoService.java; src/main/java/br/com/almoxarifado/obras/ResumoEstruturaService.java; frontend/src/pages/Structure.jsx; frontend/src/components/ContextSelector.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/obras/ObrasTests.java; frontend/tests/structure.spec.js; frontend/tests/context.test.js.
- Dependências: Pré-requisitos: Vínculos/estados/snapshots e contexto admissível. Vínculos funcionais (não uma ordem de execução): RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F2/F5. Risco: Contexto fictício ou história reescrita.
- Critério de aceite: Cadastrar Obra/OS/CC com vínculos/datas/estado e responsável reais; associação incompatível é rejeitada; contexto histórico permanece após renomear e vínculos estruturais são imutáveis. Homologação e gates comuns não são declarados concluídos.

## RF062

Orçamento da OS: previsões por categoria.

- Módulo: Obras OS e centros de custo. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Vínculos/estados/snapshots e contexto admissível. Vínculos funcionais (não uma ordem de execução): RF148, RF150..
- Prioridade proposta: P2. Fase proposta: F2/F5. Risco: Contexto fictício ou história reescrita.
- Critério de aceite: Demonstrar cenário nominal específico de RF062: Orçamento da OS: previsões por categoria. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF063

Apropriação de custos: vincular custos/consumos à OS.

- Módulo: Obras OS e centros de custo. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Consumo quantitativo por contexto; valorização e apropriação financeira não aprovadas.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/obras/EstruturaService.java; src/main/java/br/com/almoxarifado/obras/ContextoService.java; src/main/java/br/com/almoxarifado/obras/ResumoEstruturaService.java; frontend/src/pages/Structure.jsx; frontend/src/components/ContextSelector.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/obras/ObrasTests.java; frontend/tests/structure.spec.js; frontend/tests/context.test.js.
- Dependências: Pré-requisitos: Vínculos/estados/snapshots e contexto admissível. Vínculos funcionais (não uma ordem de execução): RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F2/F5. Risco: Contexto fictício ou história reescrita.
- Critério de aceite: Demonstrar cenário nominal específico de RF063: Apropriação de custos: vincular custos/consumos à OS. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Consumo quantitativo por contexto; valorização e apropriação financeira não aprovadas. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF064

Previsto × realizado: comparação financeira/operacional.

- Módulo: Obras OS e centros de custo. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Vínculos/estados/snapshots e contexto admissível. Vínculos funcionais (não uma ordem de execução): RF148, RF150..
- Prioridade proposta: P2. Fase proposta: F2/F5. Risco: Contexto fictício ou história reescrita.
- Critério de aceite: Demonstrar cenário nominal específico de RF064: Previsto × realizado: comparação financeira/operacional. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF065

Histórico de OS: consumo, custos, prazo, ocorrências e recursos.

- Módulo: Obras OS e centros de custo. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Contexto e histórico operacional; equipe, ocorrências e custos completos ausentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/obras/EstruturaService.java; src/main/java/br/com/almoxarifado/obras/ContextoService.java; src/main/java/br/com/almoxarifado/obras/ResumoEstruturaService.java; frontend/src/pages/Structure.jsx; frontend/src/components/ContextSelector.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/obras/ObrasTests.java; frontend/tests/structure.spec.js; frontend/tests/context.test.js.
- Dependências: Pré-requisitos: Vínculos/estados/snapshots e contexto admissível. Vínculos funcionais (não uma ordem de execução): RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F2/F5. Risco: Contexto fictício ou história reescrita.
- Critério de aceite: Demonstrar cenário nominal específico de RF065: Histórico de OS: consumo, custos, prazo, ocorrências e recursos. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Contexto e histórico operacional; equipe, ocorrências e custos completos ausentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF066

Indicadores de prazo: início/término previsto e real.

- Módulo: Obras OS e centros de custo. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Datas previstas/reais de estrutura; indicadores de prazo completos ausentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/obras/EstruturaService.java; src/main/java/br/com/almoxarifado/obras/ContextoService.java; src/main/java/br/com/almoxarifado/obras/ResumoEstruturaService.java; frontend/src/pages/Structure.jsx; frontend/src/components/ContextSelector.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/obras/ObrasTests.java; frontend/tests/structure.spec.js; frontend/tests/context.test.js.
- Dependências: Pré-requisitos: Vínculos/estados/snapshots e contexto admissível. Vínculos funcionais (não uma ordem de execução): RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F2/F5. Risco: Contexto fictício ou história reescrita.
- Critério de aceite: Demonstrar cenário nominal específico de RF066: Indicadores de prazo: início/término previsto e real. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Datas previstas/reais de estrutura; indicadores de prazo completos ausentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF067

Motivos de interrupção: material, liberação, projeto e outros, sem atribuição automática de culpa.

- Módulo: Obras OS e centros de custo. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Motivo de suspensão/cancelamento; catálogo de causas/diário completo não entregue.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/obras/EstruturaService.java; src/main/java/br/com/almoxarifado/obras/ContextoService.java; src/main/java/br/com/almoxarifado/obras/ResumoEstruturaService.java; frontend/src/pages/Structure.jsx; frontend/src/components/ContextSelector.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/obras/ObrasTests.java; frontend/tests/structure.spec.js; frontend/tests/context.test.js.
- Dependências: Pré-requisitos: Vínculos/estados/snapshots e contexto admissível. Vínculos funcionais (não uma ordem de execução): RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F2/F5. Risco: Contexto fictício ou história reescrita.
- Critério de aceite: Demonstrar cenário nominal específico de RF067: Motivos de interrupção: material, liberação, projeto e outros, sem atribuição automática de culpa. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Motivo de suspensão/cancelamento; catálogo de causas/diário completo não entregue. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF068

Base histórica para orçamento: referências de OS semelhantes, com decisão humana.

- Módulo: Obras OS e centros de custo. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Histórico quantitativo disponível; orçamento comparável e decisão assistida não entregues.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/obras/EstruturaService.java; src/main/java/br/com/almoxarifado/obras/ContextoService.java; src/main/java/br/com/almoxarifado/obras/ResumoEstruturaService.java; frontend/src/pages/Structure.jsx; frontend/src/components/ContextSelector.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/obras/ObrasTests.java; frontend/tests/structure.spec.js; frontend/tests/context.test.js.
- Dependências: Pré-requisitos: Vínculos/estados/snapshots e contexto admissível. Vínculos funcionais (não uma ordem de execução): RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F2/F5. Risco: Contexto fictício ou história reescrita.
- Critério de aceite: Demonstrar cenário nominal específico de RF068: Base histórica para orçamento: referências de OS semelhantes, com decisão humana. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Histórico quantitativo disponível; orçamento comparável e decisão assistida não entregues. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF069

Painel da OS: custos, equipe, ferramentas, solicitações, documentos e andamento.

- Módulo: Obras OS e centros de custo. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/obras-os-centros-custo.md.
- Evidência e limite: Resumos quantitativos por contexto; custos/equipe/recursos/documentos universais pendentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/obras/EstruturaService.java; src/main/java/br/com/almoxarifado/obras/ContextoService.java; src/main/java/br/com/almoxarifado/obras/ResumoEstruturaService.java; frontend/src/pages/Structure.jsx; frontend/src/components/ContextSelector.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/obras/ObrasTests.java; frontend/tests/structure.spec.js; frontend/tests/context.test.js.
- Dependências: Pré-requisitos: Vínculos/estados/snapshots e contexto admissível. Vínculos funcionais (não uma ordem de execução): RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F2/F5. Risco: Contexto fictício ou história reescrita.
- Critério de aceite: Demonstrar cenário nominal específico de RF069: Painel da OS: custos, equipe, ferramentas, solicitações, documentos e andamento. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Resumos quantitativos por contexto; custos/equipe/recursos/documentos universais pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF070

Solicitação de campo: material, ferramenta ou EPI via web/PWA.

- Módulo: Solicitações separação e atendimento. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/atendimento-solicitacoes.md.
- Evidência e limite: Solicitação web de material responsiva; ferramenta/EPI como demanda unificada e PWA não entregues.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/SolicitacaoService.java; src/main/java/br/com/almoxarifado/service/AtendimentoSolicitacaoService.java; src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java; frontend/src/pages/RequestDetails.jsx; frontend/src/pages/RequestForm.jsx; frontend/src/pages/SeparationList.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/AtendimentoSolicitacaoTests.java; src/test/java/br/com/almoxarifado/service/FluxoSolicitacaoTests.java; frontend/tests/fulfillment.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF193, RF195, RF203, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F3/F5. Risco: Atendimento excessivo, perda de faltante ou escrita duplicada.
- Critério de aceite: Demonstrar cenário nominal específico de RF070: Solicitação de campo: material, ferramenta ou EPI via web/PWA. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Solicitação web de material responsiva; ferramenta/EPI como demanda unificada e PWA não entregues. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF071

Vínculo da solicitação: solicitante, OS/obra, almoxarifado/local e área.

- Módulo: Solicitações separação e atendimento. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/atendimento-solicitacoes.md.
- Evidência e limite: Solicitante/almoxarifado/Obra/OS/CC; área e destino distinto de entrega não entregues.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/SolicitacaoService.java; src/main/java/br/com/almoxarifado/service/AtendimentoSolicitacaoService.java; src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java; frontend/src/pages/RequestDetails.jsx; frontend/src/pages/RequestForm.jsx; frontend/src/pages/SeparationList.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/AtendimentoSolicitacaoTests.java; src/test/java/br/com/almoxarifado/service/FluxoSolicitacaoTests.java; frontend/tests/fulfillment.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF193, RF195, RF203, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F3/F5. Risco: Atendimento excessivo, perda de faltante ou escrita duplicada.
- Critério de aceite: Demonstrar cenário nominal específico de RF071: Vínculo da solicitação: solicitante, OS/obra, almoxarifado/local e área. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Solicitante/almoxarifado/Obra/OS/CC; área e destino distinto de entrega não entregues. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF072

Múltiplos itens: itens e quantidades.

- Módulo: Solicitações separação e atendimento. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/atendimento-solicitacoes.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/SolicitacaoService.java; src/main/java/br/com/almoxarifado/service/AtendimentoSolicitacaoService.java; src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java; frontend/src/pages/RequestDetails.jsx; frontend/src/pages/RequestForm.jsx; frontend/src/pages/SeparationList.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/AtendimentoSolicitacaoTests.java; src/test/java/br/com/almoxarifado/service/FluxoSolicitacaoTests.java; frontend/tests/fulfillment.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF193, RF195, RF203, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F3/F5. Risco: Atendimento excessivo, perda de faltante ou escrita duplicada.
- Critério de aceite: Solicitação aceita múltiplos itens válidos com quantidades por unidade; item inválido impede gravação parcial; detalhes mantêm todos os itens. Homologação e gates comuns não são declarados concluídos.

## RF073

Fluxo de aprovação: supervisor/encarregado e/ou direção conforme regra.

- Módulo: Solicitações separação e atendimento. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/atendimento-solicitacoes.md.
- Evidência e limite: Authorities e aprovação explícita; hierarquia/política configurável ainda ausentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/SolicitacaoService.java; src/main/java/br/com/almoxarifado/service/AtendimentoSolicitacaoService.java; src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java; frontend/src/pages/RequestDetails.jsx; frontend/src/pages/RequestForm.jsx; frontend/src/pages/SeparationList.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/AtendimentoSolicitacaoTests.java; src/test/java/br/com/almoxarifado/service/FluxoSolicitacaoTests.java; frontend/tests/fulfillment.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF193, RF195, RF203, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F3/F5. Risco: Atendimento excessivo, perda de faltante ou escrita duplicada.
- Critério de aceite: Demonstrar cenário nominal específico de RF073: Fluxo de aprovação: supervisor/encarregado e/ou direção conforme regra. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Authorities e aprovação explícita; hierarquia/política configurável ainda ausentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF074

Aprovar/rejeitar: decisão, responsável, data/hora e justificativa quando necessária.

- Módulo: Solicitações separação e atendimento. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/atendimento-solicitacoes.md.
- Evidência e limite: Aprovação/rejeição existem; metadados/justificativa de rejeição e políticas completas pendentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/SolicitacaoService.java; src/main/java/br/com/almoxarifado/service/AtendimentoSolicitacaoService.java; src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java; frontend/src/pages/RequestDetails.jsx; frontend/src/pages/RequestForm.jsx; frontend/src/pages/SeparationList.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/AtendimentoSolicitacaoTests.java; src/test/java/br/com/almoxarifado/service/FluxoSolicitacaoTests.java; frontend/tests/fulfillment.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF193, RF195, RF203, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F3/F5. Risco: Atendimento excessivo, perda de faltante ou escrita duplicada.
- Critério de aceite: Demonstrar cenário nominal específico de RF074: Aprovar/rejeitar: decisão, responsável, data/hora e justificativa quando necessária. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Aprovação/rejeição existem; metadados/justificativa de rejeição e políticas completas pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF075

Separação: status e responsável.

- Módulo: Solicitações separação e atendimento. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/atendimento-solicitacoes.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/SolicitacaoService.java; src/main/java/br/com/almoxarifado/service/AtendimentoSolicitacaoService.java; src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java; frontend/src/pages/RequestDetails.jsx; frontend/src/pages/RequestForm.jsx; frontend/src/pages/SeparationList.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/AtendimentoSolicitacaoTests.java; src/test/java/br/com/almoxarifado/service/FluxoSolicitacaoTests.java; frontend/tests/fulfillment.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF193, RF195, RF203, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F3/F5. Risco: Atendimento excessivo, perda de faltante ou escrita duplicada.
- Critério de aceite: Somente estado admissível inicia separação, com responsável/instante; não reservar/movimentar estoque nessa etapa; repetição não cria separação extra. Homologação e gates comuns não são declarados concluídos.

## RF076

Pronto para retirada: notificação.

- Módulo: Solicitações separação e atendimento. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/atendimento-solicitacoes.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF193, RF195, RF203, RF150..
- Prioridade proposta: P2. Fase proposta: F1/F3/F5. Risco: Atendimento excessivo, perda de faltante ou escrita duplicada.
- Critério de aceite: Demonstrar cenário nominal específico de RF076: Pronto para retirada: notificação. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF077

Retirada por terceiro: quem solicitou e quem retirou.

- Módulo: Solicitações separação e atendimento. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/atendimento-solicitacoes.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF193, RF195, RF203, RF150..
- Prioridade proposta: P2. Fase proposta: F1/F3/F5. Risco: Atendimento excessivo, perda de faltante ou escrita duplicada.
- Critério de aceite: Demonstrar cenário nominal específico de RF077: Retirada por terceiro: quem solicitou e quem retirou. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF078

Atendimento parcial: parte atendida e saldo pendente.

- Módulo: Solicitações separação e atendimento. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/atendimento-solicitacoes.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/SolicitacaoService.java; src/main/java/br/com/almoxarifado/service/AtendimentoSolicitacaoService.java; src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java; frontend/src/pages/RequestDetails.jsx; frontend/src/pages/RequestForm.jsx; frontend/src/pages/SeparationList.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/AtendimentoSolicitacaoTests.java; src/test/java/br/com/almoxarifado/service/FluxoSolicitacaoTests.java; frontend/tests/fulfillment.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF193, RF195, RF203, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F3/F5. Risco: Atendimento excessivo, perda de faltante ou escrita duplicada.
- Critério de aceite: Atendimento parcial deixa saldo pendente por item; total fecha; tentativa acima do pendente ou sem autoridade é rejeitada atomicamente. Homologação e gates comuns não são declarados concluídos.

## RF079

Necessidade de compra: falta de estoque aprovada para compras.

- Módulo: Solicitações separação e atendimento. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/atendimento-solicitacoes.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/SolicitacaoService.java; src/main/java/br/com/almoxarifado/service/AtendimentoSolicitacaoService.java; src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java; frontend/src/pages/RequestDetails.jsx; frontend/src/pages/RequestForm.jsx; frontend/src/pages/SeparationList.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/AtendimentoSolicitacaoTests.java; src/test/java/br/com/almoxarifado/service/FluxoSolicitacaoTests.java; frontend/tests/fulfillment.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF193, RF195, RF203, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F3/F5. Risco: Atendimento excessivo, perda de faltante ou escrita duplicada.
- Critério de aceite: Falta de solicitação aprovada origina necessidade com quantidade/origem; criar necessidade não compra nem atende automaticamente; excedente ou duplicidade incompatível é recusado. Homologação e gates comuns não são declarados concluídos.

## RF080

Notificação ao comprador: e-mail/notificação.

- Módulo: Alertas notificações e escalonamento. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/estoque-inteligente.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF012, RF025, RF047, RF104..
- Prioridade proposta: P2. Fase proposta: F3/F8. Risco: Alerta perdido, duplicado ou exposto a destinatário indevido.
- Critério de aceite: Demonstrar cenário nominal específico de RF080: Notificação ao comprador: e-mail/notificação. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF081

Recebimento parcial: diferentes datas/documentos.

- Módulo: Compras fornecedores e recebimento. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/compras-recebimento.md.
- Evidência e limite: Recebimentos parciais e datas; associação fiscal/documentos de NF não entregue.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/compras/PedidoCompraService.java; src/main/java/br/com/almoxarifado/compras/FornecedorService.java; frontend/src/pages/PurchaseOrders.jsx; frontend/src/pages/Suppliers.jsx; frontend/src/pages/PurchaseDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/compras/ComprasTests.java; frontend/tests/purchases.spec.js; frontend/tests/purchases.test.js.
- Dependências: Pré-requisitos: Pedido/necessidade e política de recebimento/divergência aprovados. Vínculos funcionais (não uma ordem de execução): RF079, RF193, RF203..
- Prioridade proposta: P1. Fase proposta: F1/F5. Risco: Entrada duplicada, alocação excessiva ou custo atribuído indevidamente.
- Critério de aceite: Demonstrar cenário nominal específico de RF081: Recebimento parcial: diferentes datas/documentos. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Recebimentos parciais e datas; associação fiscal/documentos de NF não entregue. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF082

Conferência pedido × recebido: divergências.

- Módulo: Compras fornecedores e recebimento. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/compras-recebimento.md.
- Evidência e limite: Conferência quantitativa e excesso bloqueado; tratamento de sobras/diferenças completo pendente.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/compras/PedidoCompraService.java; src/main/java/br/com/almoxarifado/compras/FornecedorService.java; frontend/src/pages/PurchaseOrders.jsx; frontend/src/pages/Suppliers.jsx; frontend/src/pages/PurchaseDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/compras/ComprasTests.java; frontend/tests/purchases.spec.js; frontend/tests/purchases.test.js.
- Dependências: Pré-requisitos: Pedido/necessidade e política de recebimento/divergência aprovados. Vínculos funcionais (não uma ordem de execução): RF079, RF193, RF203..
- Prioridade proposta: P1. Fase proposta: F1/F5. Risco: Entrada duplicada, alocação excessiva ou custo atribuído indevidamente.
- Critério de aceite: Demonstrar cenário nominal específico de RF082: Conferência pedido × recebido: divergências. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Conferência quantitativa e excesso bloqueado; tratamento de sobras/diferenças completo pendente. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF083

Captura de NF: foto/PDF.

- Módulo: Compras fornecedores e recebimento. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/compras-recebimento.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Pedido/necessidade e política de recebimento/divergência aprovados. Vínculos funcionais (não uma ordem de execução): RF079, RF193, RF203..
- Prioridade proposta: P2. Fase proposta: F1/F5. Risco: Entrada duplicada, alocação excessiva ou custo atribuído indevidamente.
- Critério de aceite: Demonstrar cenário nominal específico de RF083: Captura de NF: foto/PDF. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF084

Importação XML NF-e: dados estruturados quando disponíveis à empresa.

- Módulo: Compras fornecedores e recebimento. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/compras-recebimento.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Pedido/necessidade e política de recebimento/divergência aprovados. Vínculos funcionais (não uma ordem de execução): RF079, RF193, RF203..
- Prioridade proposta: P2. Fase proposta: F1/F5. Risco: Entrada duplicada, alocação excessiva ou custo atribuído indevidamente.
- Critério de aceite: Demonstrar cenário nominal específico de RF084: Importação XML NF-e: dados estruturados quando disponíveis à empresa. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF085

Extração assistida: fornecedor, número, data, itens, quantidades e valores.

- Módulo: Compras fornecedores e recebimento. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/compras-recebimento.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Pedido/necessidade e política de recebimento/divergência aprovados. Vínculos funcionais (não uma ordem de execução): RF079, RF193, RF203..
- Prioridade proposta: P2. Fase proposta: F1/F5. Risco: Entrada duplicada, alocação excessiva ou custo atribuído indevidamente.
- Critério de aceite: Demonstrar cenário nominal específico de RF085: Extração assistida: fornecedor, número, data, itens, quantidades e valores. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF086

Mapeamento de produtos: descrição/código do fornecedor → cadastro interno.

- Módulo: Compras fornecedores e recebimento. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/compras-recebimento.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Pedido/necessidade e política de recebimento/divergência aprovados. Vínculos funcionais (não uma ordem de execução): RF079, RF193, RF203..
- Prioridade proposta: P2. Fase proposta: F1/F5. Risco: Entrada duplicada, alocação excessiva ou custo atribuído indevidamente.
- Critério de aceite: Demonstrar cenário nominal específico de RF086: Mapeamento de produtos: descrição/código do fornecedor → cadastro interno. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF087

Confirmação humana: IA não efetiva entrada crítica sozinha.

- Módulo: Compras fornecedores e recebimento. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/compras-recebimento.md.
- Evidência e limite: Compra/recebimento exige confirmação humana; pipeline de extração IA ainda inexistente.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/compras/PedidoCompraService.java; src/main/java/br/com/almoxarifado/compras/FornecedorService.java; frontend/src/pages/PurchaseOrders.jsx; frontend/src/pages/Suppliers.jsx; frontend/src/pages/PurchaseDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/compras/ComprasTests.java; frontend/tests/purchases.spec.js; frontend/tests/purchases.test.js.
- Dependências: Pré-requisitos: Pedido/necessidade e política de recebimento/divergência aprovados. Vínculos funcionais (não uma ordem de execução): RF079, RF193, RF203..
- Prioridade proposta: P1. Fase proposta: F1/F5. Risco: Entrada duplicada, alocação excessiva ou custo atribuído indevidamente.
- Critério de aceite: Demonstrar cenário nominal específico de RF087: Confirmação humana: IA não efetiva entrada crítica sozinha. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Compra/recebimento exige confirmação humana; pipeline de extração IA ainda inexistente. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF088

Leitura de certificados/documentos: extrair campos candidatos para conferência.

- Módulo: IA assistiva e automações. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF169..
- Prioridade proposta: P2. Fase proposta: F8. Risco: Ação crítica automática ou resposta sem fonte.
- Critério de aceite: Demonstrar cenário nominal específico de RF088: Leitura de certificados/documentos: extrair campos candidatos para conferência. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF089

Geração assistida de relatório: rascunho profissional revisável.

- Módulo: IA assistiva e automações. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF169..
- Prioridade proposta: P2. Fase proposta: F8. Risco: Ação crítica automática ou resposta sem fonte.
- Critério de aceite: Demonstrar cenário nominal específico de RF089: Geração assistida de relatório: rascunho profissional revisável. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF090

Consulta assistida: perguntas sobre dados autorizados do sistema.

- Módulo: IA assistiva e automações. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF169..
- Prioridade proposta: P2. Fase proposta: F8. Risco: Ação crítica automática ou resposta sem fonte.
- Critério de aceite: Demonstrar cenário nominal específico de RF090: Consulta assistida: perguntas sobre dados autorizados do sistema. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF091

Ferramenta preferencial: priorizar item habitual do funcionário quando disponível.

- Módulo: Ferramentas ativos e custódia. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P2. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Demonstrar cenário nominal específico de RF091: Ferramenta preferencial: priorizar item habitual do funcionário quando disponível. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF092

Histórico de uso/zelo: uso, condição e manutenção sem inferir culpa automaticamente.

- Módulo: Ferramentas ativos e custódia. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Histórico factual de condições; não inferir avaliação de zelo/culpa.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Demonstrar cenário nominal específico de RF092: Histórico de uso/zelo: uso, condição e manutenção sem inferir culpa automaticamente. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Histórico factual de condições; não inferir avaliação de zelo/culpa. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF093

Bobinas de cabo: tipo/bitola, comprimento inicial, saldo e localização.

- Módulo: Cabos bobinas e sobras. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF193, RF204, RF150..
- Prioridade proposta: P2. Fase proposta: F4. Risco: Saldo de comprimento ou reaproveitamento incorreto.
- Critério de aceite: Demonstrar cenário nominal específico de RF093: Bobinas de cabo: tipo/bitola, comprimento inicial, saldo e localização. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF094

Retalhos/sobras: cabos e futuramente tubos/barras reaproveitáveis.

- Módulo: Cabos bobinas e sobras. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF193, RF204, RF150..
- Prioridade proposta: P2. Fase proposta: F4. Risco: Saldo de comprimento ou reaproveitamento incorreto.
- Critério de aceite: Demonstrar cenário nominal específico de RF094: Retalhos/sobras: cabos e futuramente tubos/barras reaproveitáveis. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF095

Inventário por QR: conferência física.

- Módulo: Inventário físico divergências e ajustes. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF011, RF048, RF097, RF148..
- Prioridade proposta: P1. Fase proposta: F4. Risco: Ajuste sem conferência e saldos fictícios.
- Critério de aceite: Demonstrar cenário nominal específico de RF095: Inventário por QR: conferência física. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF096

Divergência de inventário: esperado × encontrado.

- Módulo: Inventário físico divergências e ajustes. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF011, RF048, RF097, RF148..
- Prioridade proposta: P1. Fase proposta: F4. Risco: Ajuste sem conferência e saldos fictícios.
- Critério de aceite: Demonstrar cenário nominal específico de RF096: Divergência de inventário: esperado × encontrado. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF097

Ajuste autorizado: motivo, autorização e auditoria.

- Módulo: Inventário físico divergências e ajustes. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF011, RF048, RF148..
- Prioridade proposta: P1. Fase proposta: F4. Risco: Ajuste sem conferência e saldos fictícios.
- Critério de aceite: Demonstrar cenário nominal específico de RF097: Ajuste autorizado: motivo, autorização e auditoria. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF098

Inventário inicial: reconciliar físico e digital antes da produção.

- Módulo: Inventário físico divergências e ajustes. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF011, RF048, RF097, RF148..
- Prioridade proposta: P1. Fase proposta: F4. Risco: Ajuste sem conferência e saldos fictícios.
- Critério de aceite: Demonstrar cenário nominal específico de RF098: Inventário inicial: reconciliar físico e digital antes da produção. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF099

Cadastro/entrega de EPI: EPI, dados aplicáveis, quantidade, funcionário e responsáveis.

- Módulo: EPI operacional e prontuário. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/epis-seguranca-trabalho.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/epi/EpiService.java; src/main/java/br/com/almoxarifado/epi/RegistroEpi.java; src/main/java/br/com/almoxarifado/epi/ItemEpi.java; frontend/src/pages/EpiDeliveryForm.jsx; frontend/src/pages/EpiEmployee.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/epi/EpiTests.java; frontend/tests/epis.spec.js; frontend/tests/epis.test.js.
- Dependências: Pré-requisitos: Identidade/CA/política de retorno e validação SST para o recorte. Vínculos funcionais (não uma ordem de execução): RF001, RF011, RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F3. Risco: Entrega duplicada, saldo indevido ou inferência de aptidão.
- Critério de aceite: Configuração 1:1 Produto, entrega quantitativa gera SAÍDA no único Estoque com snapshots/funcionário/ator; segundo item inválido reverte todos e replay retorna o mesmo evento. Homologação e gates comuns não são declarados concluídos.

## RF100

Fluxo EPI Central→obra→funcionário: rastreabilidade.

- Módulo: EPI operacional e prontuário. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/epis-seguranca-trabalho.md.
- Evidência e limite: Transferências e entrega rastreáveis; cadeia física dedicada por lote Central→obra→pessoa ausente.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/epi/EpiService.java; src/main/java/br/com/almoxarifado/epi/RegistroEpi.java; src/main/java/br/com/almoxarifado/epi/ItemEpi.java; frontend/src/pages/EpiDeliveryForm.jsx; frontend/src/pages/EpiEmployee.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/epi/EpiTests.java; frontend/tests/epis.spec.js; frontend/tests/epis.test.js.
- Dependências: Pré-requisitos: Identidade/CA/política de retorno e validação SST para o recorte. Vínculos funcionais (não uma ordem de execução): RF001, RF011, RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F3. Risco: Entrega duplicada, saldo indevido ou inferência de aptidão.
- Critério de aceite: Demonstrar cenário nominal específico de RF100: Fluxo EPI Central→obra→funcionário: rastreabilidade. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Transferências e entrega rastreáveis; cadeia física dedicada por lote Central→obra→pessoa ausente. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF101

Histórico de EPI: entregas e substituições.

- Módulo: EPI operacional e prontuário. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/epis-seguranca-trabalho.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/epi/EpiService.java; src/main/java/br/com/almoxarifado/epi/RegistroEpi.java; src/main/java/br/com/almoxarifado/epi/ItemEpi.java; frontend/src/pages/EpiDeliveryForm.jsx; frontend/src/pages/EpiEmployee.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/epi/EpiTests.java; frontend/tests/epis.spec.js; frontend/tests/epis.test.js.
- Dependências: Pré-requisitos: Identidade/CA/política de retorno e validação SST para o recorte. Vínculos funcionais (não uma ordem de execução): RF001, RF011, RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F3. Risco: Entrega duplicada, saldo indevido ou inferência de aptidão.
- Critério de aceite: Histórico de entregas/substituições preserva origem e snapshots após mudar cadastro; substituição liga item antigo/novo e não credita estoque antigo automaticamente. Homologação e gates comuns não são declarados concluídos.

## RF102

Treinamentos: NR-35, NR-33, NR-12 e outros aplicáveis.

- Módulo: Treinamentos documentos ocupacionais e privacidade. Estado: **DEPENDENTE DE VALIDAÇÃO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/epis-seguranca-trabalho.md.
- Evidência e limite: Sem implementação; dados, procedimento ou confidencialidade exigem definição/validação SST ou jurídica.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF107, RF156, RF164..
- Prioridade proposta: P2. Fase proposta: F7. Risco: Exposição de saúde ou conformidade legal presumida.
- Critério de aceite: Demonstrar cenário nominal específico de RF102: Treinamentos: NR-35, NR-33, NR-12 e outros aplicáveis. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação; dados, procedimento ou confidencialidade exigem definição/validação SST ou jurídica. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF103

Documentos ocupacionais/cliente: ASO e controles internos/de clientes.

- Módulo: Treinamentos documentos ocupacionais e privacidade. Estado: **DEPENDENTE DE VALIDAÇÃO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/epis-seguranca-trabalho.md.
- Evidência e limite: Sem implementação; dados, procedimento ou confidencialidade exigem definição/validação SST ou jurídica.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF107, RF156, RF164..
- Prioridade proposta: P2. Fase proposta: F7. Risco: Exposição de saúde ou conformidade legal presumida.
- Critério de aceite: Demonstrar cenário nominal específico de RF103: Documentos ocupacionais/cliente: ASO e controles internos/de clientes. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação; dados, procedimento ou confidencialidade exigem definição/validação SST ou jurídica. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF104

Validade e alertas: vencimentos configurados.

- Módulo: Treinamentos documentos ocupacionais e privacidade. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/epis-seguranca-trabalho.md.
- Evidência e limite: Alertas físicos/troca EPI; treinamentos/ASO e central de vencimentos ausentes.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF107, RF156, RF164..
- Prioridade proposta: P1. Fase proposta: F7. Risco: Exposição de saúde ou conformidade legal presumida.
- Critério de aceite: Demonstrar cenário nominal específico de RF104: Validade e alertas: vencimentos configurados. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Alertas físicos/troca EPI; treinamentos/ASO e central de vencimentos ausentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF105

Anexos de segurança: certificados/documentos com acesso controlado.

- Módulo: Treinamentos documentos ocupacionais e privacidade. Estado: **DEPENDENTE DE VALIDAÇÃO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/epis-seguranca-trabalho.md.
- Evidência e limite: Sem implementação; dados, procedimento ou confidencialidade exigem definição/validação SST ou jurídica.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF107, RF156, RF164..
- Prioridade proposta: P2. Fase proposta: F7. Risco: Exposição de saúde ou conformidade legal presumida.
- Critério de aceite: Demonstrar cenário nominal específico de RF105: Anexos de segurança: certificados/documentos com acesso controlado. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação; dados, procedimento ou confidencialidade exigem definição/validação SST ou jurídica. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF106

Canal de melhoria/segurança: sugestões e relatos.

- Módulo: Canal de melhoria segurança e ações. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF107, RF108, RF109, RF156..
- Prioridade proposta: P2. Fase proposta: F7. Risco: Identificação indevida ou relato sem encaminhamento.
- Critério de aceite: Demonstrar cenário nominal específico de RF106: Canal de melhoria/segurança: sugestões e relatos. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF107

Confidencialidade: acesso restrito.

- Módulo: Administração e segurança. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md; docs/epis-seguranca-trabalho.md.
- Evidência e limite: Authorities de módulo; sem isolamento individual/obra/empresa nem privacidade RH/SST completa.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF053, RF148, RF150..
- Prioridade proposta: P0 de liberação. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Demonstrar cenário nominal específico de RF107: Confidencialidade: acesso restrito. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Authorities de módulo; sem isolamento individual/obra/empresa nem privacidade RH/SST completa. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF108

Anonimato real quando oferecido: não expor identidade nos dados disponibilizados ao processo.

- Módulo: Canal de melhoria segurança e ações. Estado: **DEPENDENTE DE VALIDAÇÃO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação; dados, procedimento ou confidencialidade exigem definição/validação SST ou jurídica.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF107, RF109, RF156..
- Prioridade proposta: P2. Fase proposta: F7. Risco: Identificação indevida ou relato sem encaminhamento.
- Critério de aceite: Demonstrar cenário nominal específico de RF108: Anonimato real quando oferecido: não expor identidade nos dados disponibilizados ao processo. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação; dados, procedimento ou confidencialidade exigem definição/validação SST ou jurídica. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF109

Acompanhamento da melhoria: recebida, análise, ação, responsável, prazo e conclusão.

- Módulo: Canal de melhoria segurança e ações. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF107, RF108, RF156..
- Prioridade proposta: P2. Fase proposta: F7. Risco: Identificação indevida ou relato sem encaminhamento.
- Critério de aceite: Demonstrar cenário nominal específico de RF109: Acompanhamento da melhoria: recebida, análise, ação, responsável, prazo e conclusão. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF110

Veículos: próprios/alugados, placa, tipo, responsável e situação.

- Módulo: Frota logística e entregas. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF008, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Entrega/custo atribuídos ao contexto errado.
- Critério de aceite: Demonstrar cenário nominal específico de RF110: Veículos: próprios/alugados, placa, tipo, responsável e situação. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF111

Locações: locadora, contrato, mensalidade e vencimentos.

- Módulo: Frota logística e entregas. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF008, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Entrega/custo atribuídos ao contexto errado.
- Critério de aceite: Demonstrar cenário nominal específico de RF111: Locações: locadora, contrato, mensalidade e vencimentos. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF112

Frota própria: documentos, manutenção, km e custos.

- Módulo: Frota logística e entregas. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF008, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Entrega/custo atribuídos ao contexto errado.
- Critério de aceite: Demonstrar cenário nominal específico de RF112: Frota própria: documentos, manutenção, km e custos. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF113

Motoristas: vínculo a viagens/entregas.

- Módulo: Frota logística e entregas. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF008, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Entrega/custo atribuídos ao contexto errado.
- Critério de aceite: Demonstrar cenário nominal específico de RF113: Motoristas: vínculo a viagens/entregas. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF114

Entrega entre almoxarifados: origem, destino, carga, saída, chegada e recebimento.

- Módulo: Frota logística e entregas. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF008, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Entrega/custo atribuídos ao contexto errado.
- Critério de aceite: Demonstrar cenário nominal específico de RF114: Entrega entre almoxarifados: origem, destino, carga, saída, chegada e recebimento. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF115

Coleta em fornecedor: vínculo a pedido/fornecedor.

- Módulo: Frota logística e entregas. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF008, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Entrega/custo atribuídos ao contexto errado.
- Critério de aceite: Demonstrar cenário nominal específico de RF115: Coleta em fornecedor: vínculo a pedido/fornecedor. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF116

Custos logísticos: combustível, pedágio e outros.

- Módulo: Frota logística e entregas. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF008, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Entrega/custo atribuídos ao contexto errado.
- Critério de aceite: Demonstrar cenário nominal específico de RF116: Custos logísticos: combustível, pedágio e outros. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF117

Apropriação logística: custos à OS quando aplicável.

- Módulo: Frota logística e entregas. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF008, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Entrega/custo atribuídos ao contexto errado.
- Critério de aceite: Demonstrar cenário nominal específico de RF117: Apropriação logística: custos à OS quando aplicável. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF118

Alojamentos: imóvel, endereço, proprietário/imobiliária, capacidade e situação.

- Módulo: Alojamentos ocupação e despesas. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Ocupação/custo errado ou exposição pessoal.
- Critério de aceite: Demonstrar cenário nominal específico de RF118: Alojamentos: imóvel, endereço, proprietário/imobiliária, capacidade e situação. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF119

Contratos de alojamento: aluguel, caução, datas e documentos.

- Módulo: Alojamentos ocupação e despesas. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Ocupação/custo errado ou exposição pessoal.
- Critério de aceite: Demonstrar cenário nominal específico de RF119: Contratos de alojamento: aluguel, caução, datas e documentos. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF120

Ocupação: funcionários hospedados e histórico.

- Módulo: Alojamentos ocupação e despesas. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Ocupação/custo errado ou exposição pessoal.
- Critério de aceite: Demonstrar cenário nominal específico de RF120: Ocupação: funcionários hospedados e histórico. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF121

Despesas de alojamento: água, energia, internet e outras.

- Módulo: Alojamentos ocupação e despesas. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Ocupação/custo errado ou exposição pessoal.
- Critério de aceite: Demonstrar cenário nominal específico de RF121: Despesas de alojamento: água, energia, internet e outras. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF122

Itens de alojamento: sabão, limpeza e outros fornecidos.

- Módulo: Alojamentos ocupação e despesas. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Ocupação/custo errado ou exposição pessoal.
- Critério de aceite: Demonstrar cenário nominal específico de RF122: Itens de alojamento: sabão, limpeza e outros fornecidos. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF123

Custo de alojamento por OS: apropriação quando aplicável.

- Módulo: Alojamentos ocupação e despesas. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Ocupação/custo errado ou exposição pessoal.
- Critério de aceite: Demonstrar cenário nominal específico de RF123: Custo de alojamento por OS: apropriação quando aplicável. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF124

Planejamento de refeições: quantidades por obra/turno/alojamento conforme regra.

- Módulo: Refeições e presença. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF127..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Quantidade/custo incorreto ou dados de ponto sem autorização.
- Critério de aceite: Demonstrar cenário nominal específico de RF124: Planejamento de refeições: quantidades por obra/turno/alojamento conforme regra. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF125

Solicitação de refeições: substituir Excel/e-mail por fluxo rastreável.

- Módulo: Refeições e presença. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF127..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Quantidade/custo incorreto ou dados de ponto sem autorização.
- Critério de aceite: Demonstrar cenário nominal específico de RF125: Solicitação de refeições: substituir Excel/e-mail por fluxo rastreável. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF126

Custos de refeições: quantidade, valor e apropriação.

- Módulo: Refeições e presença. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF127..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Quantidade/custo incorreto ou dados de ponto sem autorização.
- Critério de aceite: Demonstrar cenário nominal específico de RF126: Custos de refeições: quantidade, valor e apropriação. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF127

Integração/importação de ponto: quando tecnicamente disponível, usar presença autorizada como apoio.

- Módulo: Refeições e presença. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Quantidade/custo incorreto ou dados de ponto sem autorização.
- Critério de aceite: Demonstrar cenário nominal específico de RF127: Integração/importação de ponto: quando tecnicamente disponível, usar presença autorizada como apoio. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF128

Alocação operacional: funcionário por obra, área, equipe e atividade.

- Módulo: Alocação feedback PDI e carreira. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF107, RF156..
- Prioridade proposta: P2. Fase proposta: F7. Risco: Ranking punitivo ou histórico pessoal exposto.
- Critério de aceite: Demonstrar cenário nominal específico de RF128: Alocação operacional: funcionário por obra, área, equipe e atividade. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF129

Status de atividade: atividade, aguardando material/liberação, treinamento, disponível etc.

- Módulo: Alocação feedback PDI e carreira. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF107, RF156..
- Prioridade proposta: P2. Fase proposta: F7. Risco: Ranking punitivo ou histórico pessoal exposto.
- Critério de aceite: Demonstrar cenário nominal específico de RF129: Status de atividade: atividade, aguardando material/liberação, treinamento, disponível etc. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF130

Carga de trabalho: apoiar distribuição equilibrada sem julgamento automático.

- Módulo: Alocação feedback PDI e carreira. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF107, RF156..
- Prioridade proposta: P2. Fase proposta: F7. Risco: Ranking punitivo ou histórico pessoal exposto.
- Critério de aceite: Demonstrar cenário nominal específico de RF130: Carga de trabalho: apoiar distribuição equilibrada sem julgamento automático. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF131

Feedback periódico: pontos positivos, desenvolvimento, metas e acompanhamento.

- Módulo: Alocação feedback PDI e carreira. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF107, RF156..
- Prioridade proposta: P2. Fase proposta: F7. Risco: Ranking punitivo ou histórico pessoal exposto.
- Critério de aceite: Demonstrar cenário nominal específico de RF131: Feedback periódico: pontos positivos, desenvolvimento, metas e acompanhamento. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF132

Feedback de mão dupla: resposta/comentário do funcionário.

- Módulo: Alocação feedback PDI e carreira. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF107, RF156..
- Prioridade proposta: P2. Fase proposta: F7. Risco: Ranking punitivo ou histórico pessoal exposto.
- Critério de aceite: Demonstrar cenário nominal específico de RF132: Feedback de mão dupla: resposta/comentário do funcionário. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF133

Histórico de feedback: evolução com acesso adequado.

- Módulo: Alocação feedback PDI e carreira. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF107, RF156..
- Prioridade proposta: P2. Fase proposta: F7. Risco: Ranking punitivo ou histórico pessoal exposto.
- Critério de aceite: Demonstrar cenário nominal específico de RF133: Histórico de feedback: evolução com acesso adequado. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF134

PDI: Plano de Desenvolvimento Individual.

- Módulo: Alocação feedback PDI e carreira. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF107, RF156..
- Prioridade proposta: P2. Fase proposta: F7. Risco: Ranking punitivo ou histórico pessoal exposto.
- Critério de aceite: Demonstrar cenário nominal específico de RF134: PDI: Plano de Desenvolvimento Individual. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF135

Interesse de carreira: objetivos profissionais.

- Módulo: Alocação feedback PDI e carreira. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF107, RF156..
- Prioridade proposta: P2. Fase proposta: F7. Risco: Ranking punitivo ou histórico pessoal exposto.
- Critério de aceite: Demonstrar cenário nominal específico de RF135: Interesse de carreira: objetivos profissionais. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF136

Trilhas de carreira: caminhos e requisitos definidos pela empresa.

- Módulo: Alocação feedback PDI e carreira. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF107, RF156..
- Prioridade proposta: P2. Fase proposta: F7. Risco: Ranking punitivo ou histórico pessoal exposto.
- Critério de aceite: Demonstrar cenário nominal específico de RF136: Trilhas de carreira: caminhos e requisitos definidos pela empresa. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF137

Metas de desenvolvimento: acompanhamento de metas/treinamentos.

- Módulo: Alocação feedback PDI e carreira. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF107, RF156..
- Prioridade proposta: P2. Fase proposta: F7. Risco: Ranking punitivo ou histórico pessoal exposto.
- Critério de aceite: Demonstrar cenário nominal específico de RF137: Metas de desenvolvimento: acompanhamento de metas/treinamentos. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF138

Apoio à decisão humana: não decidir automaticamente aumento, promoção ou punição.

- Módulo: Alocação feedback PDI e carreira. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF107, RF156..
- Prioridade proposta: P2. Fase proposta: F7. Risco: Ranking punitivo ou histórico pessoal exposto.
- Critério de aceite: Demonstrar cenário nominal específico de RF138: Apoio à decisão humana: não decidir automaticamente aumento, promoção ou punição. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF139

Diário/relatório de obra: equipe, serviços, ocorrências, materiais, fotos e observações.

- Módulo: Diário de obra e relatórios oficiais. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF164, RF169..
- Prioridade proposta: P2. Fase proposta: F8. Risco: Texto assistido tratado como oficial sem revisão.
- Critério de aceite: Demonstrar cenário nominal específico de RF139: Diário/relatório de obra: equipe, serviços, ocorrências, materiais, fotos e observações. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF140

Rascunho assistido: texto-base a partir dos dados.

- Módulo: Diário de obra e relatórios oficiais. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF164, RF169..
- Prioridade proposta: P2. Fase proposta: F8. Risco: Texto assistido tratado como oficial sem revisão.
- Critério de aceite: Demonstrar cenário nominal específico de RF140: Rascunho assistido: texto-base a partir dos dados. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF141

Revisão/aprovação: relatório oficial revisado antes de emissão.

- Módulo: Diário de obra e relatórios oficiais. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF164, RF169..
- Prioridade proposta: P2. Fase proposta: F8. Risco: Texto assistido tratado como oficial sem revisão.
- Critério de aceite: Demonstrar cenário nominal específico de RF141: Revisão/aprovação: relatório oficial revisado antes de emissão. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF142

Versões: histórico e rastreabilidade.

- Módulo: Diário de obra e relatórios oficiais. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF164, RF169..
- Prioridade proposta: P2. Fase proposta: F8. Risco: Texto assistido tratado como oficial sem revisão.
- Critério de aceite: Demonstrar cenário nominal específico de RF142: Versões: histórico e rastreabilidade. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF143

Calculadoras de tubulação: offset, avanço, elevação, ângulo, hipotenusa, take-off/desconto e cortes.

- Módulo: Engenharia de campo e fabricação. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF145, RNF012..
- Prioridade proposta: P2. Fase proposta: F9. Risco: Cálculo/corte incorreto com consequência física.
- Critério de aceite: Demonstrar cenário nominal específico de RF143: Calculadoras de tubulação: offset, avanço, elevação, ângulo, hipotenusa, take-off/desconto e cortes. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF144

Cálculos de campo: escadas, guarda-corpos, suportes, vigas e cortes dentro do escopo técnico.

- Módulo: Engenharia de campo e fabricação. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF145, RNF012..
- Prioridade proposta: P2. Fase proposta: F9. Risco: Cálculo/corte incorreto com consequência física.
- Critério de aceite: Demonstrar cenário nominal específico de RF144: Cálculos de campo: escadas, guarda-corpos, suportes, vigas e cortes dentro do escopo técnico. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF145

Biblioteca técnica configurável: parâmetros/take-offs cadastrados, não fixos no código.

- Módulo: Engenharia de campo e fabricação. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RNF012..
- Prioridade proposta: P2. Fase proposta: F9. Risco: Cálculo/corte incorreto com consequência física.
- Critério de aceite: Demonstrar cenário nominal específico de RF145: Biblioteca técnica configurável: parâmetros/take-offs cadastrados, não fixos no código. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF146

Isométrico simplificado: entrada/desenho de trechos e conexões.

- Módulo: Engenharia de campo e fabricação. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF145, RNF012..
- Prioridade proposta: P2. Fase proposta: F9. Risco: Cálculo/corte incorreto com consequência física.
- Critério de aceite: Demonstrar cenário nominal específico de RF146: Isométrico simplificado: entrada/desenho de trechos e conexões. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF147

Lista de fabricação: niples/trechos, conexões e memória de cálculo.

- Módulo: Engenharia de campo e fabricação. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF145, RNF012..
- Prioridade proposta: P2. Fase proposta: F9. Risco: Cálculo/corte incorreto com consequência física.
- Critério de aceite: Demonstrar cenário nominal específico de RF147: Lista de fabricação: niples/trechos, conexões e memória de cálculo. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF148

Trilha de auditoria: usuário, ação, data/hora e contexto.

- Módulo: Administração e segurança. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: Auditoria dos módulos presentes; cobertura integral futura não entregue.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF053, RF150..
- Prioridade proposta: P0 de liberação. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Demonstrar cenário nominal específico de RF148: Trilha de auditoria: usuário, ação, data/hora e contexto. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Auditoria dos módulos presentes; cobertura integral futura não entregue. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF149

Estorno rastreável: preferir correção/estorno a exclusão destrutiva.

- Módulo: Estoque e movimentações. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/estoque-inteligente.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Unidade, política de movimento/ajuste, schema e saldo de abertura homologados. Vínculos funcionais (não uma ordem de execução): RF193, RF204, RF150..
- Prioridade proposta: P2. Fase proposta: F1/F4. Risco: Saldo incorreto, repetição ou concorrência.
- Critério de aceite: Demonstrar cenário nominal específico de RF149: Estorno rastreável: preferir correção/estorno a exclusão destrutiva. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF150

Transações críticas: tudo ou nada em operações multi-etapas.

- Módulo: Administração e segurança. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: Operações atuais transacionais; protocolos específicos de módulos futuros ainda não entregues.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF053, RF148..
- Prioridade proposta: P0 de liberação. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Demonstrar cenário nominal específico de RF150: Transações críticas: tudo ou nada em operações multi-etapas. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Operações atuais transacionais; protocolos específicos de módulos futuros ainda não entregues. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF151

Backups: rotina e retenção.

- Módulo: Administração e segurança. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF053, RF148, RF150..
- Prioridade proposta: P0 de liberação. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Demonstrar cenário nominal específico de RF151: Backups: rotina e retenção. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF152

Restauração testada: validar backups.

- Módulo: Administração e segurança. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF053, RF148, RF150..
- Prioridade proposta: P0 de liberação. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Demonstrar cenário nominal específico de RF152: Restauração testada: validar backups. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF153

Logs/monitoramento: erros e saúde da aplicação.

- Módulo: Administração e segurança. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: RequestId/logs e testes; monitoramento/métricas/incidentes de produção não homologados.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF053, RF148, RF150..
- Prioridade proposta: P0 de liberação. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Demonstrar cenário nominal específico de RF153: Logs/monitoramento: erros e saúde da aplicação. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: RequestId/logs e testes; monitoramento/métricas/incidentes de produção não homologados. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF154

HTTPS/segredos: proteção de tráfego e credenciais.

- Módulo: Administração e segurança. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: Configuração externa e proteção de cookie; HTTPS/proxy e rotação histórica pendentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF053, RF148, RF150..
- Prioridade proposta: P0 de liberação. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Demonstrar cenário nominal específico de RF154: HTTPS/segredos: proteção de tráfego e credenciais. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Configuração externa e proteção de cookie; HTTPS/proxy e rotação histórica pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF155

Ambientes separados: desenvolvimento, homologação e produção.

- Módulo: Administração e segurança. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: H2 test profile separado; ambientes de homologação/produção não preparados/homologados.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF053, RF148, RF150..
- Prioridade proposta: P0 de liberação. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Demonstrar cenário nominal específico de RF155: Ambientes separados: desenvolvimento, homologação e produção. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: H2 test profile separado; ambientes de homologação/produção não preparados/homologados. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF156

Dados sensíveis: permissões específicas para RH, saúde ocupacional, relatos e finanças.

- Módulo: Administração e segurança. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: Permissões operacionais atuais; escopo sensível RH/saúde/finanças ainda não definido/implementado.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF053, RF148, RF150..
- Prioridade proposta: P0 de liberação. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Demonstrar cenário nominal específico de RF156: Dados sensíveis: permissões específicas para RH, saúde ocupacional, relatos e finanças. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Permissões operacionais atuais; escopo sensível RH/saúde/finanças ainda não definido/implementado. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF157

Uso opcional em celular pessoal: sem exigir acesso desnecessário ao aparelho.

- Módulo: Administração e segurança. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/autenticacao-autorizacao.md.
- Evidência e limite: Web responsiva opcional; validação em dispositivo real e política de dispositivo corporativo pendentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js.
- Dependências: Pré-requisitos: Política vigente de identidade/authority e recorte de dados. Vínculos funcionais (não uma ordem de execução): RF053, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F2. Risco: Acesso indevido ou exposição sensível.
- Critério de aceite: Demonstrar cenário nominal específico de RF157: Uso opcional em celular pessoal: sem exigir acesso desnecessário ao aparelho. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Web responsiva opcional; validação em dispositivo real e política de dispositivo corporativo pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF158

Modelo de mobilização: cadastrar modelos reutilizáveis de materiais, ferramentas, equipamentos, EPI, consumíveis e itens de apoio por tipo de obra/serviço.

- Módulo: Mobilização kits e checklists. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF193, RF163..
- Prioridade proposta: P2. Fase proposta: F4. Risco: Falta operacional ou reserva confundida com saldo.
- Critério de aceite: Demonstrar cenário nominal específico de RF158: Modelo de mobilização: cadastrar modelos reutilizáveis de materiais, ferramentas, equipamentos, EPI, consumíveis e itens de apoio por tipo de obra/serviço. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF159

Checklist de mobilização da OS: gerar checklist para nova obra a partir de modelo e/ou histórico semelhante, permitindo revisão e ajuste humano.

- Módulo: Mobilização kits e checklists. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF193, RF163..
- Prioridade proposta: P2. Fase proposta: F4. Risco: Falta operacional ou reserva confundida com saldo.
- Critério de aceite: Demonstrar cenário nominal específico de RF159: Checklist de mobilização da OS: gerar checklist para nova obra a partir de modelo e/ou histórico semelhante, permitindo revisão e ajuste humano. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF160

Situação dos itens de mobilização: acompanhar previsto, solicitado, separado, conferido, carregado, enviado, recebido, faltante e não aplicável conforme fluxo.

- Módulo: Mobilização kits e checklists. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF193, RF163..
- Prioridade proposta: P2. Fase proposta: F4. Risco: Falta operacional ou reserva confundida com saldo.
- Critério de aceite: Demonstrar cenário nominal específico de RF160: Situação dos itens de mobilização: acompanhar previsto, solicitado, separado, conferido, carregado, enviado, recebido, faltante e não aplicável conforme fluxo. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF161

Item não previsto: registrar material/ferramental necessário que não constava da mobilização inicial.

- Módulo: Mobilização kits e checklists. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF193, RF163..
- Prioridade proposta: P2. Fase proposta: F4. Risco: Falta operacional ou reserva confundida com saldo.
- Critério de aceite: Demonstrar cenário nominal específico de RF161: Item não previsto: registrar material/ferramental necessário que não constava da mobilização inicial. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF162

Evolução do modelo: ao revisar a obra, sugerir inclusão de itens não previstos no modelo; alteração somente após confirmação autorizada.

- Módulo: Mobilização kits e checklists. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF193, RF163..
- Prioridade proposta: P2. Fase proposta: F4. Risco: Falta operacional ou reserva confundida com saldo.
- Critério de aceite: Demonstrar cenário nominal específico de RF162: Evolução do modelo: ao revisar a obra, sugerir inclusão de itens não previstos no modelo; alteração somente após confirmação autorizada. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF163

Integração mobilização-estoque-compras: faltas identificadas podem originar transferência, solicitação ou necessidade de compra.

- Módulo: Mobilização kits e checklists. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF061, RF193..
- Prioridade proposta: P2. Fase proposta: F4. Risco: Falta operacional ou reserva confundida com saldo.
- Critério de aceite: Demonstrar cenário nominal específico de RF163: Integração mobilização-estoque-compras: faltas identificadas podem originar transferência, solicitação ou necessidade de compra. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF164

Central de documentos: oferecer pesquisa centralizada de documentos de diferentes módulos, respeitando permissões.

- Módulo: Documentos anexos e emissão. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF164: Central de documentos: oferecer pesquisa centralizada de documentos de diferentes módulos, respeitando permissões. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF165

Documento contextual: vincular documento a funcionário, OS, produto, ferramenta/equipamento, compra, fornecedor, manutenção, segurança, frota, alojamento ou outra entidade suportada.

- Módulo: Documentos anexos e emissão. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Documentos emitidos contextualizados; vínculos de arquivos/central para todos domínios pendentes.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF165: Documento contextual: vincular documento a funcionário, OS, produto, ferramenta/equipamento, compra, fornecedor, manutenção, segurança, frota, alojamento ou outra entidade suportada. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Documentos emitidos contextualizados; vínculos de arquivos/central para todos domínios pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF166

Metadados documentais: registrar categoria, responsável, data, versão, status, validade e demais metadados aplicáveis.

- Módulo: Documentos anexos e emissão. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Identificadores/status/contexto de documentos emitidos; repositório de metadados/versão/validade ausente.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF166: Metadados documentais: registrar categoria, responsável, data, versão, status, validade e demais metadados aplicáveis. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Identificadores/status/contexto de documentos emitidos; repositório de metadados/versão/validade ausente. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF167

Versionamento documental: preservar versões relevantes e estados de revisão/aprovação/arquivamento quando o tipo de documento exigir.

- Módulo: Documentos anexos e emissão. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF167: Versionamento documental: preservar versões relevantes e estados de revisão/aprovação/arquivamento quando o tipo de documento exigir. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF168

Controle de acesso documental: aplicar permissões específicas, principalmente a documentos pessoais, ocupacionais, financeiros, confidenciais e de segurança.

- Módulo: Documentos anexos e emissão. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Rotas documentais autenticadas; central, download/anexos e acesso sensível não entregues.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF168: Controle de acesso documental: aplicar permissões específicas, principalmente a documentos pessoais, ocupacionais, financeiros, confidenciais e de segurança. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Rotas documentais autenticadas; central, download/anexos e acesso sensível não entregues. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF169

Registro estruturado + documento: manter dados operacionais estruturados no banco quando necessários a cálculos/alertas, podendo gerar ou anexar documento correspondente.

- Módulo: Documentos anexos e emissão. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Eventos estruturados geram documentos atuais; anexos/cálculos/alertas de todos módulos pendentes.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF169: Registro estruturado + documento: manter dados operacionais estruturados no banco quando necessários a cálculos/alertas, podendo gerar ou anexar documento correspondente. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Eventos estruturados geram documentos atuais; anexos/cálculos/alertas de todos módulos pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF170

Inspeção de expedição de ferramental: na transferência Central→Obra, registrar condição, responsável, data/hora, observações e evidências conforme regra.

- Módulo: Ferramentas ativos e custódia. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Condição/responsável/horário na expedição; fotos e evidências anexas ausentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Demonstrar cenário nominal específico de RF170: Inspeção de expedição de ferramental: na transferência Central→Obra, registrar condição, responsável, data/hora, observações e evidências conforme regra. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Condição/responsável/horário na expedição; fotos e evidências anexas ausentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF171

Conferência de recebimento: na chegada à obra, confirmar ferramentas recebidas e registrar divergências de condição/carga.

- Módulo: Ferramentas ativos e custódia. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Chegada confirmada e condição registrada; tratamento completo de divergência/carga pendente.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Demonstrar cenário nominal específico de RF171: Conferência de recebimento: na chegada à obra, confirmar ferramentas recebidas e registrar divergências de condição/carga. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Chegada confirmada e condição registrada; tratamento completo de divergência/carga pendente. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF172

Evidência de avaria: permitir fotos e observações para documentar avaria preexistente, percebida no recebimento ou ocorrência posterior.

- Módulo: Ferramentas ativos e custódia. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Observação factual registrada; fotos/evidências anexas ausentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Demonstrar cenário nominal específico de RF172: Evidência de avaria: permitir fotos e observações para documentar avaria preexistente, percebida no recebimento ou ocorrência posterior. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Observação factual registrada; fotos/evidências anexas ausentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF173

Rastreabilidade sem julgamento automático: usar registros de condição como evidência, sem determinar automaticamente responsabilidade pelo dano.

- Módulo: Ferramentas ativos e custódia. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Condição e avaria ficam em fatos históricos; nenhuma tela/API classifica culpa automática; permissões e motivo de baixa são preservados. Homologação e gates comuns não são declarados concluídos.

## RF174

Empréstimo rápido na obra: manter retirada/devolução ao funcionário com fluxo simples e sem foto obrigatória por padrão.

- Módulo: Ferramentas ativos e custódia. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Retirada/devolução na obra funcionam com funcionário e contexto válido sem foto obrigatória; contexto incompatível é recusado; custódia e origem permanecem rastreáveis. Homologação e gates comuns não são declarados concluídos.

## RF175

Ocorrência no empréstimo: permitir evidência adicional quando houver dano, perda ou divergência na retirada/devolução.

- Módulo: Ferramentas ativos e custódia. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Observação no evento; ocorrência formal/anexos ausentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Demonstrar cenário nominal específico de RF175: Ocorrência no empréstimo: permitir evidência adicional quando houver dano, perda ou divergência na retirada/devolução. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Observação no evento; ocorrência formal/anexos ausentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF176

Inspeção periódica BES: criar inspeção periódica configurável das ferramentas alocadas por obra/local.

- Módulo: Ferramentas ativos e custódia. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Próxima inspeção individual; periodicidade/motor de agenda configurável por obra ausentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Demonstrar cenário nominal específico de RF176: Inspeção periódica BES: criar inspeção periódica configurável das ferramentas alocadas por obra/local. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Próxima inspeção individual; periodicidade/motor de agenda configurável por obra ausentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF177

Checklist por tipo de equipamento: permitir modelos de pontos de inspeção diferentes conforme classe/tipo de ferramenta/equipamento.

- Módulo: Ferramentas ativos e custódia. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Inspeção com resultado; modelos versionados de pontos por classe ausentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Demonstrar cenário nominal específico de RF177: Checklist por tipo de equipamento: permitir modelos de pontos de inspeção diferentes conforme classe/tipo de ferramenta/equipamento. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Inspeção com resultado; modelos versionados de pontos por classe ausentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF178

Resultado da inspeção: consolidar itens verificados, com avaria, não localizados e pendentes e encaminhar ocorrências/manutenção quando aplicável.

- Módulo: Ferramentas ativos e custódia. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/ferramentas-equipamentos.md.
- Evidência e limite: Resultado operacional; checklist consolidado/não localizado/encaminhamento manutenção pendentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/ativos/AtivosService.java; src/main/java/br/com/almoxarifado/ativos/RegistroAtivo.java; frontend/src/pages/Assets.jsx; frontend/src/pages/AssetDocument.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/ativos/AtivosTests.java; frontend/tests/assets.spec.js; frontend/tests/assets-audit.spec.js.
- Dependências: Pré-requisitos: Identidade individual, origem/custódia e estados admissíveis. Vínculos funcionais (não uma ordem de execução): RF061, RF148, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F4. Risco: Custódia/localização errada ou fechamento duplicado.
- Critério de aceite: Demonstrar cenário nominal específico de RF178: Resultado da inspeção: consolidar itens verificados, com avaria, não localizados e pendentes e encaminhar ocorrências/manutenção quando aplicável. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Resultado operacional; checklist consolidado/não localizado/encaminhamento manutenção pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF179

Registro de documentação física do cliente: quando útil, registrar existência, data, responsável e localização de APR/PT/checklist físico sem substituir o procedimento oficial.

- Módulo: Checklists BES e documentos do cliente. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF164, RF168, RF176, RF177..
- Prioridade proposta: P1. Fase proposta: F3/F4. Risco: Checklist interno confundido com procedimento obrigatório.
- Critério de aceite: Demonstrar cenário nominal específico de RF179: Registro de documentação física do cliente: quando útil, registrar existência, data, responsável e localização de APR/PT/checklist físico sem substituir o procedimento oficial. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF180

Separação entre checklist BES e cliente: distinguir inspeções/checklists internos dos documentos obrigatórios exigidos por cliente/contrato.

- Módulo: Checklists BES e documentos do cliente. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF164, RF168, RF176, RF177..
- Prioridade proposta: P1. Fase proposta: F3/F4. Risco: Checklist interno confundido com procedimento obrigatório.
- Critério de aceite: Demonstrar cenário nominal específico de RF180: Separação entre checklist BES e cliente: distinguir inspeções/checklists internos dos documentos obrigatórios exigidos por cliente/contrato. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF181

Cadastro de deslocamento: registrar deslocamentos de funcionário com origem, destino, modalidade, obra/OS, centro de custo e período aplicável.

- Módulo: Viagens deslocamentos e folgas de campo. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Tarifa/histórico errado ou trajetos expostos.
- Critério de aceite: Demonstrar cenário nominal específico de RF181: Cadastro de deslocamento: registrar deslocamentos de funcionário com origem, destino, modalidade, obra/OS, centro de custo e período aplicável. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF182

Histórico de trajetos: preservar os trajetos anteriormente utilizados pelo funcionário para consulta e reaproveitamento assistido.

- Módulo: Viagens deslocamentos e folgas de campo. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Tarifa/histórico errado ou trajetos expostos.
- Critério de aceite: Demonstrar cenário nominal específico de RF182: Histórico de trajetos: preservar os trajetos anteriormente utilizados pelo funcionário para consulta e reaproveitamento assistido. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF183

Histórico de valores: manter os valores válidos em cada período sem sobrescrever registros anteriores quando houver alteração de tarifa ou condição.

- Módulo: Viagens deslocamentos e folgas de campo. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Tarifa/histórico errado ou trajetos expostos.
- Critério de aceite: Demonstrar cenário nominal específico de RF183: Histórico de valores: manter os valores válidos em cada período sem sobrescrever registros anteriores quando houver alteração de tarifa ou condição. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF184

Sugestão de reutilização: ao identificar retorno do funcionário a localidade/obra semelhante, permitir sugerir trajeto anterior para confirmação ou atualização humana.

- Módulo: Viagens deslocamentos e folgas de campo. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Tarifa/histórico errado ou trajetos expostos.
- Critério de aceite: Demonstrar cenário nominal específico de RF184: Sugestão de reutilização: ao identificar retorno do funcionário a localidade/obra semelhante, permitir sugerir trajeto anterior para confirmação ou atualização humana. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF185

Modalidades configuráveis: suportar diferentes meios e tipos de deslocamento conforme política da empresa, incluindo transporte público, rodoviário, aéreo, fretado e outros autorizados.

- Módulo: Viagens deslocamentos e folgas de campo. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Tarifa/histórico errado ou trajetos expostos.
- Critério de aceite: Demonstrar cenário nominal específico de RF185: Modalidades configuráveis: suportar diferentes meios e tipos de deslocamento conforme política da empresa, incluindo transporte público, rodoviário, aéreo, fretado e outros autorizados. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF186

Viagem de ida e retorno: registrar datas, trechos, modalidade, custos e situação das viagens relacionadas ao trabalho em campo.

- Módulo: Viagens deslocamentos e folgas de campo. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Tarifa/histórico errado ou trajetos expostos.
- Critério de aceite: Demonstrar cenário nominal específico de RF186: Viagem de ida e retorno: registrar datas, trechos, modalidade, custos e situação das viagens relacionadas ao trabalho em campo. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF187

Comprovantes de viagem: permitir anexar bilhetes, recibos, comprovantes e outros documentos relacionados, integrados à Gestão Documental.

- Módulo: Viagens deslocamentos e folgas de campo. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Tarifa/histórico errado ou trajetos expostos.
- Critério de aceite: Demonstrar cenário nominal específico de RF187: Comprovantes de viagem: permitir anexar bilhetes, recibos, comprovantes e outros documentos relacionados, integrados à Gestão Documental. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF188

Folga de campo: registrar períodos, programação e histórico de folgas de campo conforme regras definidas pela empresa para cada situação aplicável.

- Módulo: Viagens deslocamentos e folgas de campo. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Tarifa/histórico errado ou trajetos expostos.
- Critério de aceite: Demonstrar cenário nominal específico de RF188: Folga de campo: registrar períodos, programação e histórico de folgas de campo conforme regras definidas pela empresa para cada situação aplicável. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF189

Custos de mobilidade por OS: permitir apropriar passagens, viagens, deslocamentos e despesas autorizadas à obra/OS e ao centro de custo.

- Módulo: Viagens deslocamentos e folgas de campo. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Tarifa/histórico errado ou trajetos expostos.
- Critério de aceite: Demonstrar cenário nominal específico de RF189: Custos de mobilidade por OS: permitir apropriar passagens, viagens, deslocamentos e despesas autorizadas à obra/OS e ao centro de custo. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF190

Integração com alojamento e refeições: permitir visão consolidada dos custos relacionados à permanência do funcionário em campo quando vinculados à mesma obra/OS.

- Módulo: Viagens deslocamentos e folgas de campo. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Tarifa/histórico errado ou trajetos expostos.
- Critério de aceite: Demonstrar cenário nominal específico de RF190: Integração com alojamento e refeições: permitir visão consolidada dos custos relacionados à permanência do funcionário em campo quando vinculados à mesma obra/OS. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF191

Permissões de mobilidade: restringir dados pessoais, trajetos, custos e documentos de viagem aos perfis autorizados.

- Módulo: Viagens deslocamentos e folgas de campo. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Tarifa/histórico errado ou trajetos expostos.
- Critério de aceite: Demonstrar cenário nominal específico de RF191: Permissões de mobilidade: restringir dados pessoais, trajetos, custos e documentos de viagem aos perfis autorizados. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF192

Rastreabilidade e decisão humana: registrar histórico de alterações e confirmações, mantendo políticas, autorizações, exceções e decisões sob responsabilidade humana.

- Módulo: Viagens deslocamentos e folgas de campo. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF001, RF061, RF063, RF164..
- Prioridade proposta: P2. Fase proposta: F6. Risco: Tarifa/histórico errado ou trajetos expostos.
- Critério de aceite: Demonstrar cenário nominal específico de RF192: Rastreabilidade e decisão humana: registrar histórico de alterações e confirmações, mantendo políticas, autorizações, exceções e decisões sob responsabilidade humana. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF193

Catálogo Mestre: manter catálogo único de materiais/produtos reutilizado pelos módulos de Almoxarifado e Compras.

- Módulo: Catálogo mestre. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/catalogo-mestre.md; docs/compras-recebimento.md; docs/obras-os-centros-custo.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/ProdutoService.java; src/main/java/br/com/almoxarifado/service/UnidadeMedidaService.java; src/main/java/br/com/almoxarifado/model/Produto.java; frontend/src/components/ProductPicker.jsx; frontend/src/pages/Products.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/controller/CatalogoMestreTests.java; frontend/tests/frontend.spec.js; frontend/tests/api.test.js.
- Dependências: Pré-requisitos: Unicidade/codificação e unidade aprovadas. Vínculos funcionais (não uma ordem de execução): RF005, RF204..
- Prioridade proposta: P1. Fase proposta: F1/F2. Risco: Duplicação de produtos ou unidade incompatível.
- Critério de aceite: Produto único por ID/código é reutilizado por demanda/estoque/compra/recebimento; não criar catálogo paralelo; histórico mantém origem mesmo após inativação. Homologação e gates comuns não são declarados concluídos.

## RF194

Busca assistida: permitir pesquisar e selecionar produtos cadastrados durante a criação da solicitação, evitando redigitação desnecessária.

- Módulo: Catálogo mestre. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/catalogo-mestre.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/ProdutoService.java; src/main/java/br/com/almoxarifado/service/UnidadeMedidaService.java; src/main/java/br/com/almoxarifado/model/Produto.java; frontend/src/components/ProductPicker.jsx; frontend/src/pages/Products.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/controller/CatalogoMestreTests.java; frontend/tests/frontend.spec.js; frontend/tests/api.test.js.
- Dependências: Pré-requisitos: Unicidade/codificação e unidade aprovadas. Vínculos funcionais (não uma ordem de execução): RF005, RF193, RF204..
- Prioridade proposta: P1. Fase proposta: F1/F2. Risco: Duplicação de produtos ou unidade incompatível.
- Critério de aceite: Pesquisa por código/nome/categoria retorna candidato selecionável por ID; seleção preenche o produto da solicitação sem redigitação e resultado indevido não é exposto. Homologação e gates comuns não são declarados concluídos.

## RF195

Quantidade solicitada: permitir informar a quantidade desejada para cada produto selecionado, respeitando sua unidade de medida.

- Módulo: Catálogo mestre. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/catalogo-mestre.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/ProdutoService.java; src/main/java/br/com/almoxarifado/service/UnidadeMedidaService.java; src/main/java/br/com/almoxarifado/model/Produto.java; frontend/src/components/ProductPicker.jsx; frontend/src/pages/Products.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/controller/CatalogoMestreTests.java; frontend/tests/frontend.spec.js; frontend/tests/api.test.js.
- Dependências: Pré-requisitos: Unicidade/codificação e unidade aprovadas. Vínculos funcionais (não uma ordem de execução): RF005, RF193, RF204..
- Prioridade proposta: P1. Fase proposta: F1/F2. Risco: Duplicação de produtos ou unidade incompatível.
- Critério de aceite: Quantidade por item respeita unidade/fracionamento; valor inválido/ruído não admitido não cria solicitação parcial; unidade histórica fica explícita. Homologação e gates comuns não são declarados concluídos.

## RF196

Materiais recentes/frequentes: permitir apresentar atalhos de produtos utilizados recentemente ou com frequência, sem duplicar cadastros.

- Módulo: Catálogo mestre. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/catalogo-mestre.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Unicidade/codificação e unidade aprovadas. Vínculos funcionais (não uma ordem de execução): RF005, RF193, RF204..
- Prioridade proposta: P2. Fase proposta: F1/F2. Risco: Duplicação de produtos ou unidade incompatível.
- Critério de aceite: Demonstrar cenário nominal específico de RF196: Materiais recentes/frequentes: permitir apresentar atalhos de produtos utilizados recentemente ou com frequência, sem duplicar cadastros. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF197

Solicitação de novo material: quando o produto não existir, permitir registrar um item provisório para análise e atendimento.

- Módulo: Catálogo mestre. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/catalogo-mestre.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Unicidade/codificação e unidade aprovadas. Vínculos funcionais (não uma ordem de execução): RF005, RF193, RF204..
- Prioridade proposta: P2. Fase proposta: F1/F2. Risco: Duplicação de produtos ou unidade incompatível.
- Critério de aceite: Demonstrar cenário nominal específico de RF197: Solicitação de novo material: quando o produto não existir, permitir registrar um item provisório para análise e atendimento. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF198

Aprovação de novo cadastro: impedir que texto livre de uma solicitação crie automaticamente um produto oficial sem validação por perfil autorizado.

- Módulo: Catálogo mestre. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/catalogo-mestre.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Unicidade/codificação e unidade aprovadas. Vínculos funcionais (não uma ordem de execução): RF005, RF193, RF204..
- Prioridade proposta: P2. Fase proposta: F1/F2. Risco: Duplicação de produtos ou unidade incompatível.
- Critério de aceite: Demonstrar cenário nominal específico de RF198: Aprovação de novo cadastro: impedir que texto livre de uma solicitação crie automaticamente um produto oficial sem validação por perfil autorizado. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF199

Prevenção de duplicidade: antes de cadastrar novo produto, permitir localizar e sugerir possíveis produtos equivalentes para vinculação.

- Módulo: Catálogo mestre. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/catalogo-mestre.md.
- Evidência e limite: Equivalência por substring e decisão humana; deduplicação/atributos avançados pendentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/ProdutoService.java; src/main/java/br/com/almoxarifado/service/UnidadeMedidaService.java; src/main/java/br/com/almoxarifado/model/Produto.java; frontend/src/components/ProductPicker.jsx; frontend/src/pages/Products.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/controller/CatalogoMestreTests.java; frontend/tests/frontend.spec.js; frontend/tests/api.test.js.
- Dependências: Pré-requisitos: Unicidade/codificação e unidade aprovadas. Vínculos funcionais (não uma ordem de execução): RF005, RF193, RF204..
- Prioridade proposta: P1. Fase proposta: F1/F2. Risco: Duplicação de produtos ou unidade incompatível.
- Critério de aceite: Demonstrar cenário nominal específico de RF199: Prevenção de duplicidade: antes de cadastrar novo produto, permitir localizar e sugerir possíveis produtos equivalentes para vinculação. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Equivalência por substring e decisão humana; deduplicação/atributos avançados pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF200

Padronização técnica: permitir evolução do catálogo com atributos técnicos configuráveis por categoria de material.

- Módulo: Catálogo mestre. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/catalogo-mestre.md.
- Evidência e limite: Especificação textual; atributos configuráveis por categoria ausentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/ProdutoService.java; src/main/java/br/com/almoxarifado/service/UnidadeMedidaService.java; src/main/java/br/com/almoxarifado/model/Produto.java; frontend/src/components/ProductPicker.jsx; frontend/src/pages/Products.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/controller/CatalogoMestreTests.java; frontend/tests/frontend.spec.js; frontend/tests/api.test.js.
- Dependências: Pré-requisitos: Unicidade/codificação e unidade aprovadas. Vínculos funcionais (não uma ordem de execução): RF005, RF193, RF204..
- Prioridade proposta: P1. Fase proposta: F1/F2. Risco: Duplicação de produtos ou unidade incompatível.
- Critério de aceite: Demonstrar cenário nominal específico de RF200: Padronização técnica: permitir evolução do catálogo com atributos técnicos configuráveis por categoria de material. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Especificação textual; atributos configuráveis por categoria ausentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF201

Catálogo compartilhado: utilizar o mesmo produto cadastrado nos fluxos de estoque, solicitação, compra, recebimento e custos.

- Módulo: Catálogo mestre. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/catalogo-mestre.md; docs/compras-recebimento.md; docs/obras-os-centros-custo.md.
- Evidência e limite: Mesmo Produto em estoque/demanda/compra/recebimento; integração de custos ainda pendente.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/ProdutoService.java; src/main/java/br/com/almoxarifado/service/UnidadeMedidaService.java; src/main/java/br/com/almoxarifado/model/Produto.java; frontend/src/components/ProductPicker.jsx; frontend/src/pages/Products.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/controller/CatalogoMestreTests.java; frontend/tests/frontend.spec.js; frontend/tests/api.test.js.
- Dependências: Pré-requisitos: Unicidade/codificação e unidade aprovadas. Vínculos funcionais (não uma ordem de execução): RF005, RF193, RF204..
- Prioridade proposta: P1. Fase proposta: F1/F2. Risco: Duplicação de produtos ou unidade incompatível.
- Critério de aceite: Demonstrar cenário nominal específico de RF201: Catálogo compartilhado: utilizar o mesmo produto cadastrado nos fluxos de estoque, solicitação, compra, recebimento e custos. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Mesmo Produto em estoque/demanda/compra/recebimento; integração de custos ainda pendente. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF202

Atendimento parcial: permitir atender parte da quantidade pelo estoque e encaminhar o saldo faltante como necessidade de compra.

- Módulo: Solicitações separação e atendimento. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/atendimento-solicitacoes.md; docs/compras-recebimento.md; docs/obras-os-centros-custo.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/SolicitacaoService.java; src/main/java/br/com/almoxarifado/service/AtendimentoSolicitacaoService.java; src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java; frontend/src/pages/RequestDetails.jsx; frontend/src/pages/RequestForm.jsx; frontend/src/pages/SeparationList.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/AtendimentoSolicitacaoTests.java; src/test/java/br/com/almoxarifado/service/FluxoSolicitacaoTests.java; frontend/tests/fulfillment.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF193, RF195, RF203, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F3/F5. Risco: Atendimento excessivo, perda de faltante ou escrita duplicada.
- Critério de aceite: Atender parcela pelo saldo e encaminhar faltante por decisão humana preserva quantidades/vínculos; compra não gera segunda entrega automática. Homologação e gates comuns não são declarados concluídos.

## RF203

Rastreabilidade da necessidade de compra: preservar vínculo entre saldo faltante, solicitação original, produto, obra/OS e centro de custo quando aplicável.

- Módulo: Solicitações separação e atendimento. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/atendimento-solicitacoes.md; docs/compras-recebimento.md; docs/obras-os-centros-custo.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/SolicitacaoService.java; src/main/java/br/com/almoxarifado/service/AtendimentoSolicitacaoService.java; src/main/java/br/com/almoxarifado/service/NecessidadeCompraService.java; frontend/src/pages/RequestDetails.jsx; frontend/src/pages/RequestForm.jsx; frontend/src/pages/SeparationList.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/service/AtendimentoSolicitacaoTests.java; src/test/java/br/com/almoxarifado/service/FluxoSolicitacaoTests.java; frontend/tests/fulfillment.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF193, RF195, RF150..
- Prioridade proposta: P1. Fase proposta: F1/F3/F5. Risco: Atendimento excessivo, perda de faltante ou escrita duplicada.
- Critério de aceite: Rastrear falta até solicitação/produto/contexto e alocação/recebimento multiobra; mesmo produto em obras distintas não funde origens nem duplica consumo. Homologação e gates comuns não são declarados concluídos.

## RF204

Unidades configuráveis: suportar unidades de medida adequadas aos materiais industriais e permitir evolução para regras de conversão/fracionamento quando necessário.

- Módulo: Catálogo mestre. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/catalogo-mestre.md.
- Evidência e limite: Unidades configuráveis/fracionamento; conversão/embalagem/precisão coordenada pendentes.
- Arquivos de implementação: src/main/java/br/com/almoxarifado/service/ProdutoService.java; src/main/java/br/com/almoxarifado/service/UnidadeMedidaService.java; src/main/java/br/com/almoxarifado/model/Produto.java; frontend/src/components/ProductPicker.jsx; frontend/src/pages/Products.jsx.
- Testes associados: src/test/java/br/com/almoxarifado/controller/CatalogoMestreTests.java; frontend/tests/frontend.spec.js; frontend/tests/api.test.js.
- Dependências: Pré-requisitos: Unicidade/codificação e unidade aprovadas. Vínculos funcionais (não uma ordem de execução): RF005, RF193..
- Prioridade proposta: P1. Fase proposta: F1/F2. Risco: Duplicação de produtos ou unidade incompatível.
- Critério de aceite: Demonstrar cenário nominal específico de RF204: Unidades configuráveis: suportar unidades de medida adequadas aos materiais industriais e permitir evolução para regras de conversão/fracionamento quando necessário. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Unidades configuráveis/fracionamento; conversão/embalagem/precisão coordenada pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF205

Documentos operacionais: permitir gerar documentos operacionais a partir dos registros e fluxos da plataforma quando houver valor para execução, conferência, transporte, entrega ou arquivo.

- Módulo: Documentos anexos e emissão. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Documentos dos módulos presentes; cobertura dos módulos futuros não entregue.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF205: Documentos operacionais: permitir gerar documentos operacionais a partir dos registros e fluxos da plataforma quando houver valor para execução, conferência, transporte, entrega ou arquivo. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Documentos dos módulos presentes; cobertura dos módulos futuros não entregue. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF206

Padrão visual BES: aplicar identidade visual oficial B&S Engenharia/BES aos documentos gerados, com template reutilizável.

- Módulo: Documentos anexos e emissão. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Template e fallback BES; assets oficiais e marca por cliente não disponíveis/definidos.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF206: Padrão visual BES: aplicar identidade visual oficial B&S Engenharia/BES aos documentos gerados, com template reutilizável. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Template e fallback BES; assets oficiais e marca por cliente não disponíveis/definidos. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF207

Identificação documental: incluir tipo, número/identificador, status, data/hora e responsáveis conforme o documento.

- Módulo: Documentos anexos e emissão. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Documentos atuais exibem tipo/ID/status/emissão/responsáveis reais; impressão não cria efeito operacional; campos ausentes não são inventados. Homologação e gates comuns não são declarados concluídos.

## RF208

Contexto operacional: incluir obra, OS, centro de custo, almoxarifado, local ou outros vínculos quando aplicáveis.

- Módulo: Documentos anexos e emissão. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Documento mostra contexto congelado aplicável e almoxarifado/local; renomear cadastro não muda evento histórico e ausência legítima não cria obra fictícia. Homologação e gates comuns não são declarados concluídos.

## RF209

Lista de separação: permitir gerar lista de separação de solicitação com itens, códigos, unidades, quantidades e campos de conferência.

- Módulo: Documentos anexos e emissão. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Lista contém código/unidade/quantidades/pendências e campos de conferência; pode imprimir sem confirmar entrega; acesso depende da permissão da solicitação. Homologação e gates comuns não são declarados concluídos.

## RF210

Comprovantes operacionais: permitir gerar comprovantes de retirada e entrega quando o processo exigir.

- Módulo: Documentos anexos e emissão. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Comprovantes compra/ativos/EPI; retirada/entrega universal e casos pendentes.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF210: Comprovantes operacionais: permitir gerar comprovantes de retirada e entrega quando o processo exigir. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Comprovantes compra/ativos/EPI; retirada/entrega universal e casos pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF211

Documento de necessidade de compra: permitir representar o saldo faltante/necessidade de compra preservando vínculo com a solicitação e o produto.

- Módulo: Documentos anexos e emissão. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Faltas/origens em tela/documentos de compra; documento autônomo de necessidade ausente.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF211: Documento de necessidade de compra: permitir representar o saldo faltante/necessidade de compra preservando vínculo com a solicitação e o produto. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Faltas/origens em tela/documentos de compra; documento autônomo de necessidade ausente. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF212

QR em documentos: permitir QR Code para identificação ou abertura do registro correspondente, respeitando autenticação e permissões.

- Módulo: QR barcode e inventário móvel. Estado: **PLANEJADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver.
- Arquivos de implementação: Não localizada implementação do requisito; arquivos de dependência não o concluem.
- Testes associados: Nenhum específico localizado; critérios futuros não são testes executados.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF095, RF169..
- Prioridade proposta: P2. Fase proposta: F3/F4. Risco: QR conceder acesso ou executar escrita sem autoridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF212: QR em documentos: permitir QR Code para identificação ou abertura do registro correspondente, respeitando autenticação e permissões. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Sem implementação localizada para este requisito; planejar bloco e aceite antes de desenvolver. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF213

Impressão e PDF: oferecer saída adequada para impressão e PDF sem tornar o papel obrigatório no fluxo.

- Módulo: Documentos anexos e emissão. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Impressão/salvar PDF do navegador; serviço PDF e impressão física não homologados.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF213: Impressão e PDF: oferecer saída adequada para impressão e PDF sem tornar o papel obrigatório no fluxo. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Impressão/salvar PDF do navegador; serviço PDF e impressão física não homologados. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF214

Fluxo mobile equivalente: permitir, quando aplicável, executar em tela/mobile as etapas que não exigem documento físico.

- Módulo: Documentos anexos e emissão. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Telas responsivas atuais; offline/PWA, touch e todos os futuros fluxos pendentes.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF214: Fluxo mobile equivalente: permitir, quando aplicável, executar em tela/mobile as etapas que não exigem documento físico. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Telas responsivas atuais; offline/PWA, touch e todos os futuros fluxos pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF215

Assinatura/confirmação proporcional: suportar confirmação ou assinatura conforme risco, política e exigência do processo, sem impor burocracia desnecessária.

- Módulo: Documentos anexos e emissão. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Confirmação autenticada do operador; aceite do trabalhador e política jurídica/SST pendentes.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF215: Assinatura/confirmação proporcional: suportar confirmação ou assinatura conforme risco, política e exigência do processo, sem impor burocracia desnecessária. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Confirmação autenticada do operador; aceite do trabalhador e política jurídica/SST pendentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF216

Template mestre: centralizar componentes visuais e estruturais comuns dos documentos gerados pela plataforma.

- Módulo: Documentos anexos e emissão. Estado: **IMPLEMENTADO E VALIDADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Comportamento entregue no núcleo atual; validação automatizada anterior, sem homologação real ou de produção.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Documentos dos módulos atuais reutilizam componentes comuns; alteração de template não remove identificação/contexto e impressão permanece consistente no recorte. Homologação e gates comuns não são declarados concluídos.

## RF217

Documentos por módulo: avaliar em cada novo módulo quais documentos, relatórios, comprovantes, checklists ou termos agregam valor operacional.

- Módulo: Documentos anexos e emissão. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Avaliação nos blocos implementados; novos módulos ainda exigem análise documental própria.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF217: Documentos por módulo: avaliar em cada novo módulo quais documentos, relatórios, comprovantes, checklists ou termos agregam valor operacional. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Avaliação nos blocos implementados; novos módulos ainda exigem análise documental própria. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## RF218

Ações documentais contextuais: disponibilizar geração, impressão ou confirmação no estágio correto do fluxo, evitando menus genéricos desconectados da operação.

- Módulo: Documentos anexos e emissão. Estado: **PARCIALMENTE IMPLEMENTADO**.
- Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx; docs/README.md.
- Evidência e limite: Ações contextuais atuais; estágios/documentos dos módulos futuros ausentes.
- Arquivos de implementação: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx.
- Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js.
- Dependências: Pré-requisitos: Recorte funcional, autoridade, política de dados e responsáveis definidos. Vínculos funcionais (não uma ordem de execução): RF056, RF057, RF168, RF169..
- Prioridade proposta: P1. Fase proposta: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.
- Critério de aceite: Demonstrar cenário nominal específico de RF218: Ações documentais contextuais: disponibilizar geração, impressão ou confirmação no estágio correto do fluxo, evitando menus genéricos desconectados da operação. Demonstrar cenário negativo de dados/estado/autoridade conforme a operação, com resultado esperado sem exposição ou efeito indevido. Conferir histórico e limites: Ações contextuais atuais; estágios/documentos dos módulos futuros ausentes. Fechar explicitamente a parcela ausente antes de concluir o requisito; homologação é gate separado.

## Requisitos não funcionais preservados

### RNF001

Segurança: menor privilégio, autenticação e autorização.

Estado: PARCIALMENTE IMPLEMENTADO. Não concluir por teste H2, responsividade, configuração externa ou classe preparada. Aceite: Conferir autorização positiva/negativa por endpoint/service e perfil/empresa/indivíduo no recorte; sessão revogada/CSRF ausente não permite escrita; nenhum segredo exposto em DTO ou log.. Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.

Módulo: Transversal; Administração e segurança. Arquivos associados à parcela atual: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx. Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js. Dependências: Pré-requisitos: política e métricas do recorte aprovadas; segurança, dados e ambiente definidos. Vínculos relacionados: RF053, RF148, RF150.. Prioridade proposta: P0 de liberação. Fase: F1/F2. Risco: Acesso indevido ou exposição sensível.

### RNF002

Integridade: transações/validações contra saldos incoerentes.

Estado: PARCIALMENTE IMPLEMENTADO. Não concluir por teste H2, responsividade, configuração externa ou classe preparada. Aceite: Casos de referência conferem saldo/unidade e estados; falha após primeira etapa reverte todas as etapas/auditoria; concorrência e replay não excedem origem; banco homologado é gate separado.. Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.

Módulo: Transversal; Estoque e movimentações. Arquivos associados à parcela atual: src/main/java/br/com/almoxarifado/service/EstoqueService.java; src/main/java/br/com/almoxarifado/service/EstoqueInteligenteService.java; src/main/java/br/com/almoxarifado/service/TransferenciaEstoqueService.java; frontend/src/pages/Operations.jsx; frontend/src/pages/Transfers.jsx. Testes associados: src/test/java/br/com/almoxarifado/service/EstoqueInteligenteTransferenciaTests.java; src/test/java/br/com/almoxarifado/controller/BackendOperacionalTests.java; frontend/tests/stockIntelligence.spec.js. Dependências: Pré-requisitos: política e métricas do recorte aprovadas; segurança, dados e ambiente definidos. Vínculos relacionados: RF193, RF204, RF150.. Prioridade proposta: P0 de liberação. Fase: F1/F4. Risco: Saldo incorreto, repetição ou concorrência.

### RNF003

Rastreabilidade: operações críticas auditáveis.

Estado: PARCIALMENTE IMPLEMENTADO. Não concluir por teste H2, responsividade, configuração externa ou classe preparada. Aceite: Operação crítica registra ator autenticado, responsável quando aplicável, ação, instante e snapshot contextual; leitura autorizada reconstrói origem; falha de auditoria não deixa escrita parcial.. Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.

Módulo: Transversal; Administração e segurança. Arquivos associados à parcela atual: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx. Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js. Dependências: Pré-requisitos: política e métricas do recorte aprovadas; segurança, dados e ambiente definidos. Vínculos relacionados: RF053, RF148, RF150.. Prioridade proposta: P0 de liberação. Fase: F1/F2. Risco: Acesso indevido ou exposição sensível.

### RNF004

Disponibilidade: recuperação de falhas e backup.

Estado: PLANEJADO. Não concluir por teste H2, responsividade, configuração externa ou classe preparada. Aceite: Responsáveis aprovam RPO/RTO/retenção e plano de continuidade; executar restore autorizado com massa apropriada, comparar dados/versões e tempo observado à meta, sem declarar sucesso por backup criado.. Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.

Módulo: Transversal; Administração e segurança. Arquivos associados à parcela atual: Nenhuma implementação específica localizada. Testes associados: Nenhum específico localizado. Dependências: Pré-requisitos: política e métricas do recorte aprovadas; segurança, dados e ambiente definidos. Vínculos relacionados: RF053, RF148, RF150.. Prioridade proposta: P0 de liberação. Fase: F1/F2. Risco: Acesso indevido ou exposição sensível.

### RNF005

Usabilidade: telas simples e responsivas no campo.

Estado: PARCIALMENTE IMPLEMENTADO. Não concluir por teste H2, responsividade, configuração externa ou classe preparada. Aceite: Jornadas por perfil funcionam em 390/768/1440, teclado/Escape/foco, zoom, touch real e leitor de tela; vazio/loading/erro/conflito explicam recuperação sem escrita duplicada ou exigência de celular pessoal.. Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.

Módulo: Transversal; Documentos anexos e emissão. Arquivos associados à parcela atual: frontend/src/components/OperationalDocument.jsx; frontend/src/pages/SeparationList.jsx; frontend/src/pages/PurchaseDocument.jsx; frontend/src/pages/AssetDocument.jsx; frontend/src/pages/EpiDocument.jsx. Testes associados: frontend/tests/fulfillment.spec.js; frontend/tests/purchases.spec.js; frontend/tests/assets.spec.js; frontend/tests/epis.spec.js. Dependências: Pré-requisitos: política e métricas do recorte aprovadas; segurança, dados e ambiente definidos. Vínculos relacionados: RF056, RF057, RF168, RF169.. Prioridade proposta: P1. Fase: F3. Risco: Documento errado, acesso indevido ou prova sem integridade.

### RNF006

Privacidade: acesso restrito a dados pessoais/ocupacionais.

Estado: PARCIALMENTE IMPLEMENTADO. Não concluir por teste H2, responsividade, configuração externa ou classe preparada. Aceite: Negar acesso cruzado entre empresas e funcionários aos dados sensíveis; conferir minimização de campos/logs e retenção validada; portal/QR/documento não contorna autoridade.. Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.

Módulo: Transversal; Administração e segurança. Arquivos associados à parcela atual: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx. Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js. Dependências: Pré-requisitos: política e métricas do recorte aprovadas; segurança, dados e ambiente definidos. Vínculos relacionados: RF053, RF148, RF150.. Prioridade proposta: P0 de liberação. Fase: F1/F2. Risco: Acesso indevido ou exposição sensível.

### RNF007

Desempenho: consultas comuns responsivas com crescimento do histórico.

Estado: PARCIALMENTE IMPLEMENTADO. Não concluir por teste H2, responsividade, configuração externa ou classe preparada. Aceite: Definir volume/metas com responsáveis e medir consultas/latência/recursos sob histórico crescente; comparar ao baseline, registrar gargalos e não transformar contagem constante de queries em SLA aprovado.. Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.

Módulo: Transversal; Relatórios dashboards e custos. Arquivos associados à parcela atual: src/main/java/br/com/almoxarifado/obras/ResumoEstruturaService.java; frontend/src/pages/Dashboard.jsx; frontend/src/pages/Structure.jsx. Testes associados: src/test/java/br/com/almoxarifado/obras/ObrasTests.java; frontend/tests/structure.spec.js; frontend/tests/frontend.spec.js. Dependências: Pré-requisitos: política e métricas do recorte aprovadas; segurança, dados e ambiente definidos. Vínculos relacionados: RF061, RF063, RF148.. Prioridade proposta: P1. Fase: F5/F8. Risco: Indicador incorreto ou alegação financeira sem método.

### RNF008

Manutenibilidade: módulos claros, testes e documentação.

Estado: PARCIALMENTE IMPLEMENTADO. Não concluir por teste H2, responsividade, configuração externa ou classe preparada. Aceite: Revisor independente identifica fronteiras/protocolos/fontes; regressão do recorte e documentação coerente sustentam mudança incremental; não duplicar agregados ou expandir bloco sem autorização.. Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.

Módulo: Transversal; Catálogo mestre. Arquivos associados à parcela atual: src/main/java/br/com/almoxarifado/service/ProdutoService.java; src/main/java/br/com/almoxarifado/service/UnidadeMedidaService.java; src/main/java/br/com/almoxarifado/model/Produto.java; frontend/src/components/ProductPicker.jsx; frontend/src/pages/Products.jsx. Testes associados: src/test/java/br/com/almoxarifado/controller/CatalogoMestreTests.java; frontend/tests/frontend.spec.js; frontend/tests/api.test.js. Dependências: Pré-requisitos: política e métricas do recorte aprovadas; segurança, dados e ambiente definidos. Vínculos relacionados: RF005, RF193, RF204.. Prioridade proposta: P1. Fase: F1/F2. Risco: Duplicação de produtos ou unidade incompatível.

### RNF009

Observabilidade: logs, métricas e alertas técnicos.

Estado: PARCIALMENTE IMPLEMENTADO. Não concluir por teste H2, responsividade, configuração externa ou classe preparada. Aceite: Erro/correlação e saúde podem ser investigados sem segredo; simular falha para verificar alerta/encaminhamento/retorno, preservar evidências e confirmar monitoramento operacional antes de produção.. Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.

Módulo: Transversal; Administração e segurança. Arquivos associados à parcela atual: src/main/java/br/com/almoxarifado/security/SegurancaConfig.java; src/main/java/br/com/almoxarifado/security/RotasPermissao.java; src/main/java/br/com/almoxarifado/security/UsuarioService.java; frontend/src/auth/AuthContext.jsx. Testes associados: src/test/java/br/com/almoxarifado/security/SecurityBaselineTests.java; src/test/java/br/com/almoxarifado/security/SessionCookieContainerTests.java; frontend/tests/security.spec.js. Dependências: Pré-requisitos: política e métricas do recorte aprovadas; segurança, dados e ambiente definidos. Vínculos relacionados: RF053, RF148, RF150.. Prioridade proposta: P1. Fase: F1/F2. Risco: Acesso indevido ou exposição sensível.

### RNF010

Portabilidade: Docker e configuração externa.

Estado: PLANEJADO. Não concluir por teste H2, responsividade, configuração externa ou classe preparada. Aceite: Empacotamento e configuração externa permitem ambiente isolado reproduzível; validar variáveis/segredos/migração por ambiente e recuperação, sem alegar Docker/CI implantados antes de executar o gate.. Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.

Módulo: Transversal; Empresas unidades e departamentos. Arquivos associados à parcela atual: Nenhuma implementação específica localizada. Testes associados: Nenhum específico localizado. Dependências: Pré-requisitos: política e métricas do recorte aprovadas; segurança, dados e ambiente definidos. Vínculos relacionados: DP01, DP09.. Prioridade proposta: P1. Fase: F2. Risco: Vazamento entre clientes e regras exclusivas.

### RNF011

IA confiável: IA sugere/extrai/rascunha; humano confirma ações críticas.

Estado: PLANEJADO. Não concluir por teste H2, responsividade, configuração externa ou classe preparada. Aceite: Proposta assistida indica fontes/confiança e permite revisão; entrada incorreta não efetiva escrita crítica; humano autorizado confirma e auditoria distingue sugestão de decisão, sem expor dado não autorizado.. Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.

Módulo: Transversal; IA assistiva e automações. Arquivos associados à parcela atual: Nenhuma implementação específica localizada. Testes associados: Nenhum específico localizado. Dependências: Pré-requisitos: política e métricas do recorte aprovadas; segurança, dados e ambiente definidos. Vínculos relacionados: RF056, RF057, RF169.. Prioridade proposta: P1. Fase: F8. Risco: Ação crítica automática ou resposta sem fonte.

### RNF012

Cálculos conferíveis: parâmetros e memória de cálculo visíveis.

Estado: PLANEJADO. Não concluir por teste H2, responsividade, configuração externa ou classe preparada. Aceite: Responsável técnico aprova parâmetros e casos de referência; memória reproduz entradas/unidades/versão/arredondamento/resultado; valor inválido é recusado e versão nova não reescreve cálculo anterior.. Fonte: docs/Documentacao_Mestre_Plataforma_BES_v1_4.docx.

Módulo: Transversal; Engenharia de campo e fabricação. Arquivos associados à parcela atual: Nenhuma implementação específica localizada. Testes associados: Nenhum específico localizado. Dependências: Pré-requisitos: política e métricas do recorte aprovadas; segurança, dados e ambiente definidos. Vínculos relacionados: RF145.. Prioridade proposta: P1. Fase: F9. Risco: Cálculo/corte incorreto com consequência física.


## Decisões de planejamento sem novo RF

### DP01

Produto independente para B&S e outras empresas; cliente não é proprietário presumido. Estado: PLANEJADO. Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f01-prompt-mestre-de-consolidacao; docs/decisoes-produto-bs-20261009.md quando aplicável. Dependências: RF206/RNF008. Fase: F0/F2/F10. Não implementada como capacidade nova nesta execução. Conferir decisão específica: Produto independente para B&S e outras empresas; cliente não é proprietário presumido. Demonstrar critérios do recorte correspondente na matriz e plano de homologação; políticas, titularidade, SST/jurídico e implantação dependem dos responsáveis..

### DP02

Prontuário EPI único por funcionário independentemente da obra. Estado: PLANEJADO. Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f01-prompt-mestre-de-consolidacao; docs/decisoes-produto-bs-20261009.md quando aplicável. Dependências: RF001/RF099/RF101/RF107. Fase: F3. Não implementada como capacidade nova nesta execução. Conferir decisão específica: Prontuário EPI único por funcionário independentemente da obra. Demonstrar critérios do recorte correspondente na matriz e plano de homologação; políticas, titularidade, SST/jurídico e implantação dependem dos responsáveis..

### DP03

Mudança de função com nova vigência e preservação do histórico. Estado: PLANEJADO. Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f01-prompt-mestre-de-consolidacao; docs/decisoes-produto-bs-20261009.md quando aplicável. Dependências: RF001/RF101/RF128. Fase: F2/F3. Não implementada como capacidade nova nesta execução. Conferir decisão específica: Mudança de função com nova vigência e preservação do histórico. Demonstrar critérios do recorte correspondente na matriz e plano de homologação; políticas, titularidade, SST/jurídico e implantação dependem dos responsáveis..

### DP04

Matriz versionada EPI por risco/função e vigências validada por SST. Estado: DEPENDENTE DE VALIDAÇÃO. Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f01-prompt-mestre-de-consolidacao; docs/decisoes-produto-bs-20261009.md quando aplicável. Dependências: RF099/RF104/RF107. Fase: F3. Não implementada como capacidade nova nesta execução. Conferir decisão específica: Matriz versionada EPI por risco/função e vigências validada por SST. Demonstrar critérios do recorte correspondente na matriz e plano de homologação; políticas, titularidade, SST/jurídico e implantação dependem dos responsáveis..

### DP05

Aceite por evento com identidade/integridade/termo sem exigir celular pessoal. Estado: DEPENDENTE DE VALIDAÇÃO. Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f01-prompt-mestre-de-consolidacao; docs/decisoes-produto-bs-20261009.md quando aplicável. Dependências: RF105/RF107/RF157/RF215. Fase: F3. Não implementada como capacidade nova nesta execução. Conferir decisão específica: Aceite por evento com identidade/integridade/termo sem exigir celular pessoal. Demonstrar critérios do recorte correspondente na matriz e plano de homologação; políticas, titularidade, SST/jurídico e implantação dependem dos responsáveis..

### DP06

Caixas individuais com composição real identificador e custódia contínua. Estado: PLANEJADO. Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f01-prompt-mestre-de-consolidacao; docs/decisoes-produto-bs-20261009.md quando aplicável. Dependências: RF006/RF014/RF018/RF170/RF177. Fase: F3/F4. Não implementada como capacidade nova nesta execução. Conferir decisão específica: Caixas individuais com composição real identificador e custódia contínua. Demonstrar critérios do recorte correspondente na matriz e plano de homologação; políticas, titularidade, SST/jurídico e implantação dependem dos responsáveis..

### DP07

Digital por padrão com papel proporcional à necessidade. Estado: PLANEJADO. Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f01-prompt-mestre-de-consolidacao; docs/decisoes-produto-bs-20261009.md quando aplicável. Dependências: RF205/RF213/RF214/RF215. Fase: F3. Não implementada como capacidade nova nesta execução. Conferir decisão específica: Digital por padrão com papel proporcional à necessidade. Demonstrar critérios do recorte correspondente na matriz e plano de homologação; políticas, titularidade, SST/jurídico e implantação dependem dos responsáveis..

### DP08

Demo onboarding e materiais comerciais após estabilização/homologação. Estado: PLANEJADO. Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f01-prompt-mestre-de-consolidacao; docs/decisoes-produto-bs-20261009.md quando aplicável. Dependências: RF038/RNF005/RNF006. Fase: F10. Não implementada como capacidade nova nesta execução. Conferir decisão específica: Demo onboarding e materiais comerciais após estabilização/homologação. Demonstrar critérios do recorte correspondente na matriz e plano de homologação; políticas, titularidade, SST/jurídico e implantação dependem dos responsáveis..

### DP09

Avaliar implantação isolada versus SaaS e configuração por cliente. Estado: PLANEJADO. Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f01-prompt-mestre-de-consolidacao; docs/decisoes-produto-bs-20261009.md quando aplicável. Dependências: RF003/RF154/RF155/RF156. Fase: F0/F2/F10. Não implementada como capacidade nova nesta execução. Conferir decisão específica: Avaliar implantação isolada versus SaaS e configuração por cliente. Demonstrar critérios do recorte correspondente na matriz e plano de homologação; políticas, titularidade, SST/jurídico e implantação dependem dos responsáveis..

### DP10

Licenças planos contratos suporte suspensão exportação e continuidade. Estado: PLANEJADO. Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f01-prompt-mestre-de-consolidacao; docs/decisoes-produto-bs-20261009.md quando aplicável. Dependências: RF060/RF151/RF152/RNF004. Fase: F10. Não implementada como capacidade nova nesta execução. Conferir decisão específica: Licenças planos contratos suporte suspensão exportação e continuidade. Demonstrar critérios do recorte correspondente na matriz e plano de homologação; políticas, titularidade, SST/jurídico e implantação dependem dos responsáveis..


## Oportunidades não aprovadas

### PR01

Não conformidades com plano de ação, responsável e prazo. Estado: PROPOSTA NOVA. Vínculo: RF106/RF109/RF178. Fechar ocorrências; depende de evidências e revisão humana. Prioridade proposta: P1. Antes de implementar PR01, aprovar recorte de Não conformidades com plano de ação, responsável e prazo, fontes e resultado verificável: Fechar ocorrências; depende de evidências e revisão humana. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR02

Inspeções programadas e manutenção com alertas. Estado: PROPOSTA NOVA. Vínculo: RF021–RF025/RF176. Reduzir esquecimento; política de periodicidade. Prioridade proposta: P1. Antes de implementar PR02, aprovar recorte de Inspeções programadas e manutenção com alertas, fontes e resultado verificável: Reduzir esquecimento; política de periodicidade. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR03

Inventário rotativo com QR. Estado: PROPOSTA NOVA. Vínculo: RF095–RF098/RF212. Conferência frequente; ajuste autorizado e acesso protegido. Prioridade proposta: P1. Antes de implementar PR03, aprovar recorte de Inventário rotativo com QR, fontes e resultado verificável: Conferência frequente; ajuste autorizado e acesso protegido. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR04

Kits operacionais e reservas. Estado: PROPOSTA NOVA. Vínculo: RF020/RF158–RF163. Planejar mobilização; distinguir previsão, reserva e saldo. Prioridade proposta: P2. Antes de implementar PR04, aprovar recorte de Kits operacionais e reservas, fontes e resultado verificável: Planejar mobilização; distinguir previsão, reserva e saldo. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR05

Lote, série e validade onde necessário. Estado: PROPOSTA NOVA. Vínculo: RF006/RF093/RF099/RF104. Rastreio físico; granularidade/unidades aprovadas. Prioridade proposta: P1. Antes de implementar PR05, aprovar recorte de Lote, série e validade onde necessário, fontes e resultado verificável: Rastreio físico; granularidade/unidades aprovadas. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR06

Avarias/perdas com revisão humana. Estado: PROPOSTA NOVA. Vínculo: RF026–RF029/RF092/RF173. Evidência e tratamento justo; sem culpa automática. Prioridade proposta: P1. Antes de implementar PR06, aprovar recorte de Avarias/perdas com revisão humana, fontes e resultado verificável: Evidência e tratamento justo; sem culpa automática. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR07

Garantias, certificados e calibração. Estado: PROPOSTA NOVA. Vínculo: RF022/RF051/RF088/RF164. Ciclo de vida; responsável técnico e retenção. Prioridade proposta: P2. Antes de implementar PR07, aprovar recorte de Garantias, certificados e calibração, fontes e resultado verificável: Ciclo de vida; responsável técnico e retenção. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR08

Aprovações por valor, obra ou perfil. Estado: PROPOSTA NOVA. Vínculo: RF073/RF074. Política configurável; limites, substitutos e segregação. Prioridade proposta: P1. Antes de implementar PR08, aprovar recorte de Aprovações por valor, obra ou perfil, fontes e resultado verificável: Política configurável; limites, substitutos e segregação. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR09

Portal simplificado da obra. Estado: PROPOSTA NOVA. Vínculo: RF069/RF070/RF214. Menor esforço em campo; escopo de acesso. Prioridade proposta: P2. Antes de implementar PR09, aprovar recorte de Portal simplificado da obra, fontes e resultado verificável: Menor esforço em campo; escopo de acesso. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR10

Dashboard multiunidade e custos por obra. Estado: PROPOSTA NOVA. Vínculo: RF038/RF041/RF045/RF063. Visão executiva; isolamento e valorização. Prioridade proposta: P2. Antes de implementar PR10, aprovar recorte de Dashboard multiunidade e custos por obra, fontes e resultado verificável: Visão executiva; isolamento e valorização. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR11

Histórico único de responsabilidades. Estado: PROPOSTA NOVA. Vínculo: RF018/RF092/RF128. Mudanças/desligamento; vínculos e custódia. Prioridade proposta: P1. Antes de implementar PR11, aprovar recorte de Histórico único de responsabilidades, fontes e resultado verificável: Mudanças/desligamento; vínculos e custódia. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR12

Motor compartilhado de documentos, checklists, notificações e aceites. Estado: PROPOSTA NOVA. Vínculo: RF059/RF164–RF169/RF205–RF218. Consistência; fronteiras modulares e permissões. Prioridade proposta: P1. Antes de implementar PR12, aprovar recorte de Motor compartilhado de documentos, checklists, notificações e aceites, fontes e resultado verificável: Consistência; fronteiras modulares e permissões. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR13

Offline elegível com reconciliação. Estado: PROPOSTA NOVA. Vínculo: RF070/RF214/RNF005. Continuidade em campo; validade de sessão e conflitos. Prioridade proposta: P2. Antes de implementar PR13, aprovar recorte de Offline elegível com reconciliação, fontes e resultado verificável: Continuidade em campo; validade de sessão e conflitos. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR14

Auditoria exportável e conformidade. Estado: PROPOSTA NOVA. Vínculo: RF053/RF060/RF148. Conferência por auditor; política de dados/retenção. Prioridade proposta: P1. Antes de implementar PR14, aprovar recorte de Auditoria exportável e conformidade, fontes e resultado verificável: Conferência por auditor; política de dados/retenção. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR15

Importação assistida de planilhas. Estado: PROPOSTA NOVA. Vínculo: RF193/RF198/RF199. Cadastro inicial; dry-run, validação e deduplicação. Prioridade proposta: P1. Antes de implementar PR15, aprovar recorte de Importação assistida de planilhas, fontes e resultado verificável: Cadastro inicial; dry-run, validação e deduplicação. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR16

Busca global com permissões. Estado: PROPOSTA NOVA. Vínculo: RF055/RF090/RF164. Localização de registros; não revelar dados proibidos. Prioridade proposta: P2. Antes de implementar PR16, aprovar recorte de Busca global com permissões, fontes e resultado verificável: Localização de registros; não revelar dados proibidos. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR17

Templates configuráveis. Estado: PROPOSTA NOVA. Vínculo: RF206/RF216. Identidade por cliente; versão, compatibilidade e governança. Prioridade proposta: P2. Antes de implementar PR17, aprovar recorte de Templates configuráveis, fontes e resultado verificável: Identidade por cliente; versão, compatibilidade e governança. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR18

Acessibilidade real e UX mobile de campo. Estado: PROPOSTA NOVA. Vínculo: RF157/RF214/RNF005. Uso inclusivo; touch, leitor de tela e operadores. Prioridade proposta: P1. Antes de implementar PR18, aprovar recorte de Acessibilidade real e UX mobile de campo, fontes e resultado verificável: Uso inclusivo; touch, leitor de tela e operadores. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR19

Observabilidade e suporte técnico. Estado: PROPOSTA NOVA. Vínculo: RF153/RNF009. Diagnóstico/continuidade; logs sem secrets e gestão de incidentes. Prioridade proposta: P0 de liberação. Antes de implementar PR19, aprovar recorte de Observabilidade e suporte técnico, fontes e resultado verificável: Diagnóstico/continuidade; logs sem secrets e gestão de incidentes. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR20

APIs e integração ERP/financeiro. Estado: PROPOSTA NOVA. Vínculo: RF045/RF084/RF127. Interoperabilidade; contrato, consentimento e conciliação. Prioridade proposta: P2. Antes de implementar PR20, aprovar recorte de APIs e integração ERP/financeiro, fontes e resultado verificável: Interoperabilidade; contrato, consentimento e conciliação. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..

### PR21

Métricas verificáveis de digitalização. Estado: PROPOSTA NOVA. Vínculo: RF038/RF040/RNF007. Demonstrar resultado; baseline, método e privacidade. Prioridade proposta: P2. Antes de implementar PR21, aprovar recorte de Métricas verificáveis de digitalização, fontes e resultado verificável: Demonstrar resultado; baseline, método e privacidade. Plano de testes, responsáveis e limites devem constar do bloco autorizado; sem conclusão por preparação..


## Capacidades novas incluídas no planejamento

56 capacidades separadas dos RF; inclusão documental autorizada por F02, sem implementação. A etiqueta PARCIALMENTE IMPLEMENTADO normaliza a anterior sem reclassificar 73 RF; somente RF035 muda de planejado para parcial. Fontes duráveis: bes-fontes-decisoes-documentais-v1_5.md. Pré-requisitos não são os vínculos de família.

### RH01 Cadastro e histórico funcional

Módulo: RH e DP. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Identidade única por funcionário; dados cadastrais e funcionais com vigência, origem e responsável, sem duplicar pessoa por obra.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RF001, RF057, RF156. Vínculos relacionados: RF001, DP03, RH02.

Critério de aceite: Alterar função/lotação mantendo a consulta ao período anterior e os snapshots de entrega; recusar sobreposição inválida de vigências.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3. Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### RH02 Contratos e vínculos

Módulo: RH e DP. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Registrar tipo de vínculo, empresa, início/fim, condições, aditivos e documentos versionados, com validação DP/jurídica; dados contratuais não concedem acesso automaticamente.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH01. Vínculos relacionados: RH01, RH03.

Critério de aceite: Consultar contrato vigente e aditivos históricos; validar regras de encerramento e acesso privado; não inferir vínculo trabalhista por empréstimo ou cadastro.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3. Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### RH03 Cargos e funções

Módulo: RH e DP. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Catálogo por empresa, atribuições e função exercida com vigências; relacionar cargos/funções sem sobrescrever histórico. Função de EPI não comprova cargo contratual.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH01, RF004. Vínculos relacionados: RF004, RH01.

Critério de aceite: A mudança futura de cargo/função preserva eventos anteriores e dispara revisão explícita de matriz EPI, sem alterar entregas passadas.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3. Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### RH04 Obras e lotações

Módulo: RH e DP. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Registrar empresa, unidade, área, equipe, obra/OS/CC, lotação e rateios com vigências. Congelar o contexto operacional no evento e distinguir alocação de apropriação monetária.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH01, RF061. Vínculos relacionados: RF061, RF128, RH01.

Critério de aceite: Trocar obra sem criar prontuário EPI ou caixa duplicados; comprovar origem/destino da lotação e conferir rateios sem dupla contagem.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3. Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### RH05 Escalas e jornadas

Módulo: RH e DP. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Calendários, escalas, jornada contratual, turnos, intervalos e exceções por vigência/empresa; regras dependem de validação DP/jurídica e instrumentos aplicáveis.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, RH04. Vínculos relacionados: RH02, RH04.

Critério de aceite: Reproduzir jornada de referência por período, incluindo troca de escala e exceções; testar conflitos, limites e aprovação sem fixar parâmetros legais sem fonte.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3. Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### RH06 Ponto eletrônico

Módulo: RH e DP. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Receber marcações com origem, identidade, timestamp e fuso; preservar registro bruto e ajustes justificados/aprovados. Confirmar requisitos e capacidade técnica de fornecedor; biometria não é obrigatória.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH05, RF127. Vínculos relacionados: RH05, RF127.

Critério de aceite: Importar o mesmo lote sem duplicar; rastrear marcação original, ajuste e aprovador; reconciliar ausências/inconsistências e bloquear exportação de período não conferido.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3. Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### RH07 Banco de horas

Módulo: RH e DP. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Política validada por empresa/vigência, créditos/débitos, compensações, saldo, prazo e encerramento; preservar memória de cálculo e aprovações.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH06, FOL06. Vínculos relacionados: RH06, RH05, FOL06.

Critério de aceite: Recalcular período de referência com regra versionada; conciliar saldo inicial/eventos/final, ajuste e compensação sem lançar horas duas vezes.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3. Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### RH08 Horas extras configuráveis

Módulo: RH e DP. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Eventos de horas extras com tipo, quantidade, autorização e parâmetros versionados por empresa; separar apuração, aprovação e pagamento.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH06, FOL06. Vínculos relacionados: RH06, RH05, FOL06.

Critério de aceite: Casos de referência aprovados por DP reproduzem quantidades/valores e arredondamento; evento rejeitado não segue para folha e reenvio não duplica rubrica.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3. Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### RH09 DSR

Módulo: RH e DP. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Apuração conforme fontes/regras aplicáveis e vigentes, calendário e eventos; sem fórmula ou percentual universal presumido.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH06, RH08, FOL06. Vínculos relacionados: RH06, RH08, FOL06.

Critério de aceite: Responsável DP/jurídico aprova casos com faltas, extras e mudança de regra; memória de cálculo por competência é reprodutível e não retroage silenciosamente.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3. Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### RH10 Adicionais

Módulo: RH e DP. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Rubricas e critérios de adicionais aplicáveis, base, período, evidência e responsável; parâmetros dependem de DP/jurídico/SST quando necessário.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, RH06, FOL06. Vínculos relacionados: RH02, RH06, FOL06.

Critério de aceite: Validar elegibilidade, incidências e vigências com casos aprovados; alteração de condição preserva cálculo já fechado e exige revisão rastreável.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3. Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### RH11 Faltas e atrasos

Módulo: RH e DP. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Classificar ocorrências, justificativas, documentos e aprovação antes de descontos/efeitos; não atribuir punição automaticamente.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH06, FOL06. Vínculos relacionados: RH06, RH05, FOL06.

Critério de aceite: Manter marcação original e decisão humana; corrigir justificativa sem apagar trilha e conferir efeito autorizado na competência correta.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3. Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### RH12 Afastamentos

Módulo: RH e DP. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Tipo/período e documentos com acesso proporcional, efeitos de jornada/folha/benefícios validados; minimizar dados de saúde expostos ao financeiro.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, RH05, FOL06. Vínculos relacionados: RH02, RH05, FOL06, RF156.

Critério de aceite: Calendário e folha refletem caso validado; leitor financeiro recebe efeito necessário, sem documento clínico; retorno preserva vigência anterior.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3. Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### RH13 Férias

Módulo: RH e DP. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Períodos, saldo, programação, aprovação, recibos e efeitos financeiros conforme regras validadas; não inventar prazos/cálculos legais.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, RH05, FOL06. Vínculos relacionados: RH02, RH05, FOL06.

Critério de aceite: Caso aprovado por DP concilia programação, folha, benefício e título financeiro; alteração/cancelamento gera versão e ajuste, sem pagamento duplicado.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3. Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### RH14 13º salário

Módulo: RH e DP. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Eventos, provisões, parcelas e ajustes por competência/vigência e regras validadas; integrar recibos e financeiro sem presumir critérios legais.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, FOL06. Vínculos relacionados: RH02, FOL06, FOL08.

Critério de aceite: Conferir referência validada, parcelas/provisões e liquidado; mesma origem reimportada não gera nova obrigação; diferença exige ajuste autorizado.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3. Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### RH15 Admissão e desligamento

Módulo: RH e DP. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Checklist DP com identidade, contrato, datas, documentos e responsáveis; desligamento reconcilia custódia/EPI, acesso, folha/benefícios e obrigações, sem atribuir culpa ou quitar pendência automaticamente.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH01, RH02, RF018, RF099. Vínculos relacionados: RH01, RH02, RF018, RF099, FOL06.

Critério de aceite: Simular admissão e saída com pendência de ferramenta; preservar histórico, revogar acesso conforme política e submeter acerto ao aprovador competente.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3. Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### RH16 Portal do funcionário

Módulo: RH e DP. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Consulta individual autorizada de cadastro, ponto, saldo de horas, férias, holerites e recibos; pedidos de correção/ciente sem editar fonte oficial. Dispositivo compartilhado e contingência acessível.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH01, RF056, RF057, FOL07, BEN13. Vínculos relacionados: RF056, RF057, RF156, RH01, FOL07, BEN13.

Critério de aceite: Funcionário A não acessa B por troca de ID/URL; operador não se passa por empregado; consultas deixam trilha proporcional e aceite não presume validade jurídica universal.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E1/E2/E3. Risco: Exposição pessoal, vigência inválida ou apuração trabalhista incorreta. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FOL01 Modelo A Folha nativa BES

Módulo: Folha. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Motor completo planejado: rubricas, eventos, bases/incidências, memória de cálculo, líquido, conferência, aprovação, fechamento, encargos, holerites e obrigações aplicáveis; responsável DP/contábil valida regras.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH06, FOL03, FOL06, FOL10. Vínculos relacionados: RH02, RH05, RH06, FOL03, FOL06, FOL08, FOL10.

Critério de aceite: Massa de referência validada externamente reproduz cálculo e fechamento; divergência bloqueia oficialização; nenhuma previsão de cálculo significa motor já implementado.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3. Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FOL02 Modelo B Folha externa integrada

Módulo: Folha. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Exportar eventos autorizados e importar resultados/holerites oficiais com identificador do provedor, competência, versão e protocolo; confirmar layout/API/capacidade técnica antes de prometer integração.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH06, FOL03, FOL04, FOL10. Vínculos relacionados: RH06, FOL03, FOL04, FOL07, FOL11.

Critério de aceite: Reenvio/importação duplicada não cria eventos, holerites ou títulos novos; totais de empregado/rubrica/encargo conciliados; lote inválido não vira fonte oficial.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3. Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FOL03 Escolha do modelo por empresa

Módulo: Folha. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Cada empresa configura modelo A nativo ou B externo, com vigência e responsável. Por empresa + competência existe no máximo uma fonte e uma versão PUBLICADA_OFICIAL vigente, independente de empregado, tipo ou versão; antes de publicar pode não haver oficial. Simulação não gera obrigação.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, DP09. Vínculos relacionados: RH02, RF003, DP09.

Critério de aceite: Empresa/competência com modelos A/B ou tipos diferentes recusa fontes oficiais paralelas; empresas/competências distintas são independentes. Configuração vigente e histórico permanecem auditáveis; alteração não reclassifica competência anterior.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3. Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FOL04 Fonte oficial por competência

Módulo: Folha. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Fonte oficial exclusiva por empresa + competência, sem versão, empregado ou tipo na chave de exclusividade. Identidade da versão é empresa + competência + versão; seus itens por vínculo/empregado e tipo compõem a mesma fonte. Estados futuros RASCUNHO/CONFERIDA/APROVADA/FECHADA/PUBLICADA_OFICIAL/SUBSTITUÍDA/CANCELADA; simulação não gera obrigação pagável. Substituição explícita e atômica preserva anterior.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FOL03, FOL10. Vínculos relacionados: FOL03, FOL10.

Critério de aceite: CA-P1-01/02: versões e modelos distintos da mesma empresa/competência não permanecem oficiais simultaneamente, inclusive sob concorrência; troca autorizada e auditada publica sucessora e marca anterior SUBSTITUÍDA atomicamente. Falha conserva anterior oficial; histórico/documentos e pagamentos preservados. Ver governança no eixo RH/DP para os seis cenários.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3. Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FOL05 Migração controlada A B

Módulo: Folha. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Migrar A/B com competência de corte, modelo vigente, conciliação de saldos/acumulados, obrigações e pagamentos, aprovação e plano de retorno; bloquear dupla fonte por empresa/competência, preservar consulta e origem histórica.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FOL03, FOL04, FOL11. Vínculos relacionados: FOL03, FOL04, FOL02, FOL10, FOL11.

Critério de aceite: CA-P1-05: conciliar saldos/eventos/encargos/recibos e pagamentos antes da virada; recusar publicação paralela A/B por empresa/competência. Retorno e retificação não apagam nem recriam pagamentos; períodos anteriores e evidência de aceite permanecem acessíveis.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E5. Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FOL06 Regras trabalhistas e tributárias versionadas

Módulo: Folha. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Registrar fonte normativa/contratual validada, vigência, empresa, rubrica, parâmetros, precisão/arredondamento e aprovador; não fixar alíquotas ou inferir legislação automaticamente.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, RF156. Vínculos relacionados: RH02, RF156.

Critério de aceite: Reproduzir uma competência fechada com sua versão; regra futura não muda passado; atualização retroativa exige processo e revisão DP/contábil/jurídica registrados.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3. Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FOL07 Holerites digitais e histórico de recibos

Módulo: Folha. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Emitir/importar holerite ligado à fonte oficial; versões, disponibilização, recibos de férias/13º/adiantamento, acesso individual e histórico de ciência. Documento digital e assinatura têm políticas distintas.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FOL04, RF167, RF168. Vínculos relacionados: FOL04, RF164, RF167, RF168, RH16.

Critério de aceite: Empregado consulta somente seus documentos; substituição não apaga versão anterior; fonte/competência/valores identificáveis; ciência ou assinatura validada conforme política sem exigir celular pessoal.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3. Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FOL08 Encargos e obrigações aplicáveis

Módulo: Folha. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Planejar INSS, IRRF, FGTS e demais encargos/obrigações conforme validação competente; identificar base, rubrica, competência, apuração, documento e integração disponível, sem alegar certificação legal.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FOL06, FOL04. Vínculos relacionados: FOL06, FOL04, FIN03.

Critério de aceite: DP/contábil/jurídico aprova referências e concilia base/valor/obrigação; ausência de confirmação técnica externa permanece bloqueio, não sucesso presumido.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3. Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FOL09 Adiantamentos e consignados

Módulo: Folha. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Origem, autorização, contrato/limite aplicável, parcelas, saldo e desconto vinculado à competência; retorno/cancelamento/ajuste auditáveis e sem duplicar obrigação.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, FOL06, FIN01. Vínculos relacionados: RH02, FOL06, FIN01.

Critério de aceite: Conferir empréstimo/adiantamento e descontos por competência; saldo e pagamentos reconciliados; parcela inválida ou repetida não é efetivada.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3. Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FOL10 Conferência aprovação fechamento e auditoria

Módulo: Folha. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Preparador, conferente e aprovador/publicador separados por política e alçada empresarial. RASCUNHO → CONFERIDA → APROVADA → FECHADA → PUBLICADA_OFICIAL; mudança invalida aprovação. Retificação cria sucessora e torna anterior SUBSTITUÍDA por publicação atômica, com ator, justificativa e histórico. Publicação não executa pagamento.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FOL06, RF148, RF150. Vínculos relacionados: RF148, RF150, FOL06.

Critério de aceite: CA-P1-02/06: sem alçada, autoaprovação indevida, sessão/CSRF inválidos ou empresa adulterada impedem publicação. Alteração invalida aprovação; reabertura/retificação auditáveis; falha interna e replay preservam fonte única. Versão não publicada pode ser cancelada com histórico; oficial só é substituída por sucessora aprovada.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E3. Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FOL11 Integração folha financeiro e conciliação

Módulo: Folha. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Integrar obrigações por identidade estável empresa/competência/vínculo/tipo/rubrica/origem, sem usar versão para duplicar obrigação; versão é proveniência. Reprocessamento idempotente, conciliação importado/oficial/devido/pago e ajustes somente por diferença aprovada. Preservar pagamentos realizados; timeout exige consulta antes de retry.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FOL04, FOL10, FIN01, FIN11. Vínculos relacionados: FOL04, FOL10, FIN01, FIN11.

Critério de aceite: CA-P1-03/04: mesma origem reaplicada, mesmo sob outra chave, não duplica título/pagamento; chave com conteúdo diferente conflita. Pago 100 retificado para 120 admite só complemento aprovado 20; retificado para 80 preserva 100 pago e abre tratamento autorizado 20, sem apagar/recriar pagamento ou estornar automaticamente. Falha externa mantém pendência consultável.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E0/E4. Risco: Fonte por competência incorreta, cálculo sem validação ou pagamento duplicado. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### BEN01 Vale transporte

Módulo: Benefícios. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Itinerário elegível, modalidade, tarifas e dias por vigência, opções validadas e desconto aplicável; não confundir passagem de folga com benefício regular.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH04, RH05, BEN06, BEN08. Vínculos relacionados: RH04, RH05, BEN06, BEN08.

Critério de aceite: Caso validado concilia dias/trechos/tarifas, elegibilidade e desconto; mudança de obra gera nova vigência e não sobrescreve recarga anterior.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4. Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### BEN02 Vale refeição

Módulo: Benefícios. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Política por empresa/vínculo/período, dias elegíveis, valor e descontos validados; manter separado da compra/solicitação de refeições operacionais RF124–RF126.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: BEN06, BEN07, BEN08. Vínculos relacionados: RH05, BEN06, BEN07, BEN08.

Critério de aceite: Conferir cálculo de dias, valor e ausência/férias com responsável DP; não duplicar VR e refeição operacional automaticamente.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4. Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### BEN03 Vale alimentação

Módulo: Benefícios. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Política versionada de elegibilidade e valor por competência, critérios e desconto aplicável; sem pressupor cálculo por dia ou isenção legal.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: BEN06, BEN08. Vínculos relacionados: BEN06, BEN08, RH02.

Critério de aceite: Caso validado reproduz valor/abatimento e vigência; reprocessar lote não duplica crédito e período antigo conserva parâmetros.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4. Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### BEN04 Assistência médica

Módulo: Benefícios. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Plano, titulares/dependentes quando autorizado, adesão, vigência, custo e coparticipação; minimizar exposição de dados de saúde, distinta de ASO/SST.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, BEN06, BEN12. Vínculos relacionados: RH02, BEN06, BEN12, RF156.

Critério de aceite: Conciliar pessoas elegíveis e fatura do provedor; financeiro consulta custo necessário, sem conteúdo clínico; exclusão/alteração conserva histórico autorizado.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4. Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### BEN05 Outros benefícios

Módulo: Benefícios. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Catálogo configurável de benefício, regra/fonte/versão, elegibilidade, período, fornecedor, custo e descontos aprovados; não presumir natureza tributária.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, BEN06, BEN08. Vínculos relacionados: RH02, BEN06, BEN08.

Critério de aceite: Cadastrar regra futura com aprovação competente; cálculo de referência verificável e integração financeira sem classificar incidência legal automaticamente.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4. Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### BEN06 Elegibilidade

Módulo: Benefícios. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Regras por empresa, vínculo, função/lotação e vigência, exceções justificadas e aprovadas; desligamento/afastamento avaliados proporcionalmente.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH02, RH03, RH04, FOL06. Vínculos relacionados: RH02, RH03, RH04, FOL06.

Critério de aceite: Dois casos por regra mostram elegível/ineligível com motivo; alteração retroativa segue ajuste autorizado e preserva benefício já concedido.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4. Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### BEN07 Dias previstos e calendário

Módulo: Benefícios. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Planejar dias elegíveis e conferir dias efetivos/autorizados, calendários e exceções; previsão não é crédito já entregue.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH05, RH06, BEN06. Vínculos relacionados: RH05, RH06, BEN06.

Critério de aceite: Calendário versionado distingue previsto/realizado e dias abatidos; simulação não envia recarga nem gera pagamento.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4. Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### BEN08 Tarifas e valores versionados

Módulo: Benefícios. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Tarifa/valor por benefício, fornecedor, trajeto/plano e vigência; base documental, moeda, precisão e aprovador; registrar mudanças sem recalcular passado silenciosamente.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: BEN06. Vínculos relacionados: BEN06.

Critério de aceite: Reproduzir lote antigo com valores históricos; tarifa nova afeta apenas vigência aprovada; divergência de fatura exige conferência.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4. Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### BEN09 Férias e afastamentos nos benefícios

Módulo: Benefícios. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Efeitos por tipo de benefício conforme política validada; distinguir previsão, crédito já enviado, devolução/ajuste e desconto autorizado.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH12, RH13, BEN06, BEN07. Vínculos relacionados: RH12, RH13, BEN06, BEN07.

Critério de aceite: Caso de férias/afastamento altera somente benefício/período permitido; ajuste de recarga exige evidência e não presume suspensão universal.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4. Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### BEN10 Recargas e lotes

Módulo: Benefícios. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Simular, conferir, aprovar e enviar lote com empresa/benefício/competência/versão; chave persistida, limites, retorno e estado de execução confirmados pelo provedor.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: BEN07, BEN08, BEN12, FIN11. Vínculos relacionados: BEN07, BEN08, BEN12, FIN11.

Critério de aceite: Envio repetido mantém um único lote/efeito; timeout vira pendência a consultar, sem reenvio cego; retorno parcial concilia por beneficiário.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4. Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### BEN11 Descontos

Módulo: Benefícios. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Evento vinculado à concessão/custo e política validada; limites, base, competência, autorização e destino folha/financeiro explícitos.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: BEN06, FOL06, FOL09. Vínculos relacionados: BEN06, FOL06, FOL09.

Critério de aceite: Conferir desconto aprovado na folha oficial e benefício de origem; correção referencia anterior e não desconta duas vezes.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4. Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### BEN12 Fornecedores e integrações de benefícios

Módulo: Benefícios. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Reutilizar fornecedor quando adequado; contrato, layouts/API, autenticação externa segregada, SLA, habilitação e retorno confirmados tecnicamente. Sem conector presumido.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RF008, DP09. Vínculos relacionados: RF008, DP09, BEN08.

Critério de aceite: Provedor confirma ambiente de homologação e contrato; lote de teste autorizado retorna protocolo reconciliável; dado sensível não aparece em logs.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4. Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### BEN13 Comprovantes e disponibilização

Módulo: Benefícios. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Protocolos de recarga/concessão/desconto e recibos por evento, versões, retenção e acesso individual; integrar portal sem criar fonte paralela.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RF167, RF168, BEN10. Vínculos relacionados: RF167, RF168, RH16, BEN10.

Critério de aceite: Documento liga beneficiário/lote/período/valor/fonte; trabalhador vê apenas seus registros; versões e ciência preservadas conforme política validada.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4. Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### BEN14 Conciliação de benefícios

Módulo: Benefícios. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Confrontar previsto, aprovado, enviado, creditado, faturado e pago; tratar divergências, rejeições, créditos/ajustes e responsável sem assumir sucesso por arquivo gerado.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: BEN10, BEN12, BEN13, FIN13. Vínculos relacionados: BEN10, BEN12, BEN13, FIN13.

Critério de aceite: Totais/lotes por beneficiário conferíveis; diferença abre pendência aprovada; duplicado não altera saldo nem título e fechamento guarda evidência.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E3/E4. Risco: Elegibilidade/crédito/desconto incorreto, integração não confirmada ou vazamento de dados. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FIN01 Contas a pagar e receber

Módulo: Financeiro. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Títulos com empresa, origem única, contraparte, documento, competência, vencimento, parcela e histórico; distinguir previsto, aprovado, pago/recebido e ajustado sem exclusão destrutiva.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: DP09, RF148, RF150. Vínculos relacionados: DP09, RF148, RF150.

Critério de aceite: Mesma origem não cria título duplicado; lançamento e ajuste são auditados/atômicos; posição por empresa/competência concilia com fontes.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5. Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FIN02 Agenda financeira

Módulo: Financeiro. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Vencimentos, prioridades, disponibilidade e programação; agendar não autoriza nem comprova pagamento; alteração relevante exige nova aprovação.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FIN01, FIN11. Vínculos relacionados: FIN01, FIN11.

Critério de aceite: Agenda mostra previsto/autorizado/executado/confirmado distintos; alteração de valor/destino revoga aprovação anterior conforme política.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5. Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FIN03 Fluxo de caixa

Módulo: Financeiro. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Saldo inicial verificado, entradas/saídas previstas e realizadas, contas e período; origem por título/movimento e revisão de premissas.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FIN01, FIN02, FIN13. Vínculos relacionados: FIN01, FIN02, FIN13.

Critério de aceite: Saldo inicial mais entradas menos saídas confere ao saldo final reconciliado; projeção identifica hipótese e não conta provisão como saída realizada.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5. Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FIN04 Custos operacionais

Módulo: Financeiro. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Método de valorização e classificação aprovado, origem de consumo/despesa, competência e ajustes; entrada física não é consumo/custo automaticamente.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RF063, FIN01. Vínculos relacionados: RF063, RF045, FIN01, FIN06.

Critério de aceite: Referências aprovadas distinguem entrada, consumo, despesa e pagamento; custo reprocessado conserva método/versão e evita dupla contagem.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5. Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FIN05 Custos por funcionário

Módulo: Financeiro. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Remuneração, encargos, benefícios e despesas autorizadas, com vigências/rateios, competência e acesso proporcional; não atribuir valor de custódia como gasto pessoal.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH04, FOL11, BEN14, FIN04. Vínculos relacionados: RH04, FOL11, BEN14, FIN04.

Critério de aceite: Somar componentes conciliados e ratear pelo método validado; histórico por pessoa não expõe holerite a leitor sem permissão específica.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5. Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FIN06 Custos por obra OS CC

Módulo: Financeiro. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Contexto real e rateio versionado de materiais, despesas, folha/benefícios; valores previstos/realizados separados e Centro corporativo preservado.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RF061, FIN04, RH04. Vínculos relacionados: RF061, RF063, FIN04, RH04.

Critério de aceite: Caso multiobra não duplica custo consolidado; rateios totalizam origem com ajuste de arredondamento explícito e origem congelada.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5. Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FIN07 Compras fornecedores e despesas

Módulo: Financeiro. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Pedido, recebimento físico, documento, obrigação e pagamento vinculados sem substituir agregados existentes; devolução/divergência afeta financeiro por regra aprovada, sem estorno físico automático.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RF034, RF036, RF081, FIN01, FIN04. Vínculos relacionados: RF034, RF036, RF081, FIN01, FIN04.

Critério de aceite: Compra parcialmente recebida pode ter obrigação conforme política documentada; conciliar origem/documento/título/pago sem atender solicitação nem consumir estoque por pagamento.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5. Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FIN08 Salários e encargos

Módulo: Financeiro. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Títulos de salários/encargos ligados à folha oficial e obrigações validadas, segregados por beneficiário/competência; informação bancária protegida.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FOL04, FOL08, FOL11, FIN11. Vínculos relacionados: FOL04, FOL08, FOL11, FIN11.

Critério de aceite: Uma obrigação por origem; total líquido/encargos corresponde à competência aprovada; correção não duplica remessa e pagamento requer alçada.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5. Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FIN09 Benefícios no financeiro

Módulo: Financeiro. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Compromissos/faturas/recargas/descontos separados, com conciliação por lote e origem; descontado do empregado não significa fatura já paga.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: BEN14, FIN01, FIN11. Vínculos relacionados: BEN14, BEN11, FIN01, FIN11.

Critério de aceite: Fatura/recarga/repasse reconciliados sem duplicar despesa; diferença ou devolução gera ajuste aprovado e histórico consultável.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5. Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FIN10 Provisões

Módulo: Financeiro. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Previsões/provisões por competência e critérios contábeis validados, reversão por novo evento e conexão à obrigação realizada; provisão não é pagamento.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RH13, RH14, FOL08, FIN01. Vínculos relacionados: RH13, RH14, FOL08, FIN03.

Critério de aceite: Caso validado concilia abertura/constituição/reversão/realização; cálculo antigo mantém regra e não soma provisão e obrigação como duas despesas.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5. Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FIN11 Aprovações financeiras e segregação

Módulo: Financeiro. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Preparador, conferente e autorizador com alçadas por empresa/valor/origem; quem cadastra ou altera beneficiário não confirma sozinho pagamento quando política exige segregação. Aplicar autorização backend, CSRF, idempotência e trilha transacional.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: RF057, RF148, RF150, DP09. Vínculos relacionados: RF057, RF148, RF150, DP09.

Critério de aceite: Usuário sem autoridade não aprova/efetiva; mudança de valor/conta exige nova conferência; replay conserva um efeito; rollback não deixa aprovação/pagamento parcial.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5. Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FIN12 Integração com instituições financeiras

Módulo: Financeiro. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Confirmar contrato, canal/API/layout, autenticação externa, ambiente, limites e retorno. Guardar credenciais externamente; remessa/arquivo não prova liquidação. Após timeout consultar estado antes de reenviar.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FIN11, FIN01. Vínculos relacionados: FIN11, FIN01, FIN13.

Critério de aceite: Banco confirma capacidade técnica e homologação autorizada; protocolo vincula origem/chave/lote; rejeição e retorno parcial preservam histórico; nenhum dinheiro real nesta etapa documental.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5. Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FIN13 Conciliação bancária

Módulo: Financeiro. Estado: **DEPENDENTE DE VALIDAÇÃO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Extrato/retorno com origem e integridade, correlação de títulos/lotes, duplicados, tarifas e divergências; tolerâncias aprovadas e decisão humana para exceções.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FIN01, FIN12. Vínculos relacionados: FIN01, FIN12.

Critério de aceite: Importar duas vezes sem duplicar; fechar saldo contra extrato aprovado; diferença abre pendência e baixa só ocorre com evidência/autoridade apropriadas.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5. Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FIN14 Relatórios gerenciais

Módulo: Financeiro. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Posição a pagar/receber, caixa, despesas/custos, folha/benefícios e previsões por empresa/competência/contexto; filtros/permissões, fórmula/fonte e exportação segura.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FIN03, FIN05, FIN06, FIN13, RF060. Vínculos relacionados: FIN03, FIN05, FIN06, FIN13, RF060.

Critério de aceite: Reconciliar totais ao livro de origens/competências; impedir cruzamento entre empresas e acesso indevido a dados individuais; marcar não conciliado explicitamente.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5. Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.

### FIN15 Previsão de despesas

Módulo: Financeiro. Estado: **PLANEJADO**. Tipo: DECISÃO DE PRODUTO — inclusão no planejamento autorizada. F02 autoriza documentação, não implementação; parâmetros e validações permanecem pendentes.

Cenários de compras, salários/encargos/benefícios, operação e investimento com hipótese, período, versão e responsável; separar orçamento, provisão e obrigação efetiva.

Fonte: docs/bes-fontes-decisoes-documentais-v1_5.md#f02-autorizacao-de-correcoes-apos-auditoria. Pré-requisitos: FIN02, FIN03, FIN10, RF062. Vínculos relacionados: FIN02, FIN03, FIN10, RF062.

Critério de aceite: Cenário é reproduzível e tem origem/premissas; aprovado ou alterado não envia pagamento; comparar previsto versus realizado sem alegar economia sem baseline.

Prioridade: P1 de definição/homologação; execução depende de recorte autorizado. Fase: E2/E4/E5. Risco: Obrigação/pagamento duplicado, alçada insuficiente, conciliação ou custo incorreto. Validação: Titular do produto e responsáveis DP/contábil/jurídico; SST, segurança e fornecedores/banco quando aplicável.

Implementação/testes existentes: nenhum específico da nova capacidade. Testes planejados: Casos de referência e limites; RBAC HTTP/service por empresa/indivíduo, sessão/CSRF, histórico, concorrência/replay/rollback quando houver escrita, privacidade, exportação, backup/restore e homologação competente.


## Evidências específicas e preservação de critérios

Critérios anteriores/dependências originais estão preservados no JSON para auditoria; os campos correntes separam pré-requisitos e vínculos sem auto-referências. RF035 conserva seu critério anterior e acrescenta transições, casos verificáveis e lacuna COMPRADO. Não declarar cenários executados nesta rodada.

RF035: ComprasTests.recebimentoParcialEMultiplasEntregas; naoAprovaSemSubmeter; aprovacaoNaoCriaEntrada; naoCancelaAposRecebimento; docs/compras-recebimento.md:62. Dependências originais preservadas: RF079/RF193/RF203; pedido → necessidades → produtos → estoque; política de divergência.
