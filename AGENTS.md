# Plataforma BES — referência oficial para desenvolvimento

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
