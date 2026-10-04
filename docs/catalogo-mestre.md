# Bloco 1 Catálogo Mestre BES

Implementação incremental sobre Produto. Não há catálogo paralelo. CategoriaMaterial e UnidadeMedida são cadastros de classificação e unidade, referenciados pelo mesmo produto utilizado por estoque e solicitações.

## Compatibilidade

Os campos textuais `categoria`, `unidadeMedida` e `tipoControle` continuam disponíveis. As novas referências são `categoriaMaterial` e `unidadeMedidaConfigurada`, com objetos contendo ID. Quando informadas, são resolvidas pelo backend e os campos textuais do produto são sincronizados com nome/sigla oficiais. IDs inexistentes retornam 404; objetos sem ID e referências inativas novas retornam 400.

Produtos sem referências permanecem compatíveis com o contrato antigo. Não recebem validação de fracionamento baseada em texto livre. Isso evita presumir o significado de unidades legadas. Cadastrar novos produtos pelo fluxo atualizado deve usar referências configuradas; o modo legado não implica que textos livres tenham sido validados ou normalizados.

PUT de Produto mantém código, referências e estado ativo quando esses novos campos não forem informados. Referências nulas não removem vínculos existentes. Campos opcionais textuais e especificação mantêm a semântica de substituição; textos de categoria/unidade são derivados quando há vínculo oficial. Ativo explícito permite inativar/reativar. POST/PUT não mudam identidade.

Categoria e unidade usam PUT de substituição: informar os valores de ativo e permiteFracionamento desejados; ativo omitido assume true e permiteFracionamento omitido assume false. Não há DELETE físico. A inativação mantém referências e histórico; produtos já vinculados podem conservar uma referência inativa durante edição. Inatividade dos cadastros não reescreve produtos nem bloqueia retroativamente operações de estoque existentes.

Unidade base não pode ser trocada se o produto já possui estoque, item de solicitação ou movimentação. Sigla e regra de fracionamento não podem mudar quando a unidade está vinculada a produtos. Correção dos vínculos legados exige processo de revisão específico, sem reinterpretar saldos pelo PUT.

## Unicidade e equivalência

Categoria: nome normalizado com remoção de acentos, redução de espaços e comparação em minúsculas; chave interna única. Nome original normalizado em espaços é preservado para exibição. Sigla/código: caracteres ASCII alfanuméricos e `.`, `_`, `/`, `-`, máximo 64, normalizados em maiúsculas. Código omitido recebe `BES-` + UUID, não derivado do nome. Unicidade tem validação no service e constraint de banco; colisões de persistência usam o handler 409.

Sugestões de equivalência usam substring literal case-insensitive de nome/descrição, mínimo de 3 caracteres, incluindo inativos para evitar recriar cadastro abandonado. Não são comparação semântica, não usam IA e não bloqueiam nomes parecidos. A revisão final permanece humana. Busca geral também consulta código, categoria e especificação. `%` e `_` são tratados como texto literal. Busca textual não promete equivalência de acentos entre bancos; normalização de categorias é independente da collation.

## Quantidades

Mantido double nesta rodada. Quantidades positivas/finitas seguem validadas, e unidades configuradas sem fracionamento rejeitam parcelas não inteiras em entrada, saída, inclusão de item e aprovação. Produtos legados sem FK mantêm as regras anteriores. Para vincular unidades em dados existentes, primeiro conferir saldos, solicitações e histórico.

Plano de migração para BigDecimal/DECIMAL:

1. Abranger Estoque.quantidade, ItemSolicitacao.quantidade e Movimentacao.quantidade/saldoAnterior/saldoPosterior, DTOs e somas do SolicitacaoService.
2. Medir valores existentes, precisão industrial necessária, máximos e frações; decidir escala explicitamente. DECIMAL(19,6) é candidato, não decisão final.
3. Comparar saldos e histórico convertido em banco isolado, reportar valores fora da precisão e não arredondar silenciosamente.
4. Migrar cálculo e persistência juntos, preservando JSON numérico e validações de unidade; substituir igualdade/comparações por operações BigDecimal.
5. Conferir rollback/concorrência e saldos antes/depois; repetir em PostgreSQL, banco-alvo da v1.3. Não executar conversão no MySQL real nesta rodada.

Não se introduzem conversões de embalagem, barra, rolo ou metragem neste bloco. BigDecimal não corrige sozinho dados legados incoerentes.

## Cobertura da Documentação Mestre v1.3

| RF | Cobertura |
|---|---|
| RF005 | Cadastro de categorias concluído no backend: criação, consulta, atualização, ativação/inativação e prevenção de equivalência duplicada |
| RF193 | Núcleo do catálogo único implementado sobre Produto; integração com Compras permanece futura, pois o módulo não existe |
| RF194 | Busca/seleção por ID preparada na API; interface de solicitação assistida ainda não implementada |
| RF195 | Quantidade por produto mantida, com validação de fracionamento para unidade configurada; dados legados continuam sem inferência de unidade |
| RF201 | Cadastro central reutilizado pelos módulos existentes; Compras, Recebimentos, Custos e Patrimônio ainda não existem e não são considerados concluídos |
| RF204 | Unidades configuráveis e fracionamento básico implementados; conversão, embalagem e precisão decimal serão evoluções próprias |
| RF199 | Cobertura inicial: sugestões simples por substring de nome/descrição, sem deduplicação automática |
| RF200 | Cobertura inicial: especificacaoTecnica textual; atributos configuráveis por categoria não implementados |

RF196–RF198 (atalhos e análise de novo material) ficam para extensão posterior. tipoControle segue textual; este bloco não implementa patrimônio.

## Banco

`docs/sql/catalogo-mestre-manual.sql` prepara o schema MySQL transitório e diagnósticos. Nada foi executado no banco real. Com ddl-auto=none, revisar/aplicar o schema manualmente antes de executar esta versão contra MySQL. O script é por etapas, não idempotente, e não deve ser rodado integralmente sem verificar os objetos existentes. A migração PostgreSQL terá validação separada.
