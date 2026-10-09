# Decisões de produto B&S — evolução planejada

Registro canônico complementar à [Documentação Mestre BES v1.4](Documentacao_Mestre_Plataforma_BES_v1_4.docx), em 09/10/2026. Preserva RF001–RF218 e as decisões compatíveis anteriores; não altera a cobertura implementada nem autoriza implementação, Bloco 8, publicação ou lançamento. Correções em andamento são registradas no [relatório de retomada](bes-retomada-controlada-20261009.md).

## Prontuário digital único de EPI

Planejar um histórico consolidado por funcionário, independente da obra. Mudança de obra altera o contexto operacional, sem criar automaticamente outra ficha nem fragmentar ou apagar entregas anteriores. Mudança de função cria nova vigência ocupacional, preservando a função histórica em cada entrega e permitindo revisão dos EPIs exigidos. Prever matriz versionada de EPI por função, validada pelo responsável SST; alterações não reescrevem vigências ou eventos anteriores.

Preservar CA, validade do CA e validade física separadamente, quantidades, identidade/unidade dos itens, entregas, substituições, devoluções, responsáveis e contexto histórico. Reutilizar funcionário, Produto, Estoque e eventos existentes; não criar saldo paralelo. A ficha operacional atual já reúne posse e histórico por funcionário; vigências ocupacionais e matriz por função são futuras e não estão implementadas. Dependências: RF001, RF099–RF101, RF104, RF107, contexto RF061 e rastreabilidade RF053–RF057.

## Aceite eletrônico de EPI — sujeito a validação SST/jurídica

Planejar aceite vinculado individualmente ao evento de entrega/devolução quando aplicável. Não exigir celular pessoal: considerar dispositivo corporativo compartilhado e alternativa operacional acessível. Registrar evidências de identidade, evento, itens, data/hora, responsável, versão do termo e integridade do aceite. Definir autenticação/identificação, acesso proporcional, retenção e proteção dos dados antes da implementação; o dispositivo compartilhado não deve expor o prontuário de outros trabalhadores.

Distinguir confirmação operacional de assinatura eletrônica com requisitos jurídicos específicos. A confirmação atual é do operador autenticado e não equivale automaticamente ao aceite do trabalhador. Imagem de assinatura, PIN ou biometria não são automaticamente assinatura juridicamente suficiente. Procedimento, nível de evidência, contingência, guarda e uso de dados pessoais devem passar por validação SST/jurídica. Este registro não oferece conclusão jurídica nem certifica conformidade. Dependências: RF053–RF057, RF105/RF107 e RF205–RF218, especialmente RF215.

## Caixas de ferramentas digitais

Planejar identificação individual de cada caixa, por exemplo `CX-FER-0042`. Distinguir composição padrão por função dos itens efetivamente presentes. Prever funcionário responsável, localização/obra, histórico de custódia, checklist periódico, ausências, danos, substituições, observações, fotos/evidências, termo de responsabilidade e aceite eletrônico proporcional. PDF/impressão serão sob demanda; QR será futuro e respeitará autenticação/permissões.

Mudança de obra não fragmenta o histórico quando a responsabilidade permanece com a mesma pessoa. Alterações de composição e responsabilidade precisam ser rastreáveis, sem apagar custódias anteriores. Não confundir caixa com quantidade de Produto nem duplicar ativos patrimoniais: a modelagem e o tratamento de itens quantitativos serão definidos no bloco autorizado. Dano/ausência não atribuem culpa automaticamente. Caixas, checklist específico, fotos, termos e QR não estão implementados. Dependências: RF006, RF014–RF018, RF030–RF033, RF048–RF052, RF092, RF170–RF178 e RF205–RF218.

## Digital por padrão; papel quando necessário

Princípio transversal complementar ao capítulo 18 da v1.4. Cada processo documental deverá responder:

1. Qual evidência é realmente necessária?
2. Precisa de assinatura?
3. Qual nível de aceite é apropriado?
4. Pode ser digital?
5. Quais registros precisam ser preservados?
6. Quando impressão continua necessária?

Evitar reproduzir burocracia de papel no software. Gerar documentos profissionais sob demanda quando necessários à operação, segurança, contrato ou auditoria, com identidade oficial, identificação/contexto e template reutilizável. O registro e o histórico permanecem preservados mesmo com apoio em papel. As saídas atuais por impressão/PDF do navegador não significam aceite eletrônico, QR ou serviço PDF dedicado concluídos. RF205–RF218 continuam com a cobertura real registrada em cada módulo; logo oficial B&S somente quando disponível, mantendo fallback textual BES.

## Finalização e comercialização — estratégia futura

Planejar Demo Mode seguro com dados fictícios e isolados, roteiro de demonstração, vídeo comercial principal, versões curtas, landing page profissional, screenshots, materiais de divulgação, QR/links rastreáveis quando apropriado, FAQ, tutoriais, onboarding e materiais de prospecção B2B. O launcher H2 de testes não constitui Demo Mode comercial entregue. Nenhum desses materiais foi criado/publicado nesta rodada.

Demonstrações devem seguir **problema → fluxo operacional → resultado**. Separar capacidade disponível, limitações e roadmap; funcionalidades planejadas não podem ser divulgadas como implementadas. Não inventar ganhos financeiros ou produtividade sem evidências. Proteger segurança e privacidade em vídeos, imagens, documentos, links e dados demonstrados; retirar identificadores reais, segredos, sessões e informações pessoais. Métricas/rastreamento exigem finalidade e política apropriadas. O lançamento depende de validação funcional/operacional, auditoria independente e gates de segurança existentes; este planejamento não declara produção pronta.

## Governança e próximo escopo

Este arquivo é a fonte única destas novas decisões até sua eventual incorporação formal em versão futura da documentação mestre. Não renumerar RFs nem atribuir conclusão por preparação arquitetural. Planejamento de vigências/matriz/aceite/caixas/comercialização fica separado das correções P0/P1; cada implementação futura exige bloco específico autorizado, critérios de aceitação, análise de dados, segurança, concorrência, auditoria e validação documental proporcional.
