-- PREPARAÇÃO PARA REVISÃO HUMANA. Não executado nem carregado pela aplicação.
-- Banco esperado: MySQL com tabelas InnoDB. Fazer backup antes de aplicar.
-- Executar cada etapa manualmente, após conferir schema, nomes e índices existentes.
-- DDL MySQL faz commit implícito: não contar com ROLLBACK de ALTER TABLE.

SHOW CREATE TABLE solicitacao;
SHOW CREATE TABLE movimentacao;
SHOW CREATE TABLE estoque;
SHOW INDEX FROM movimentacao;
SHOW INDEX FROM estoque;
SHOW INDEX FROM solicitacao;

-- ETAPA 1: somente se solicitacao_id ainda não existir.
-- Confirmar que solicitacao.id é INT assinado; se diferente, adaptar o tipo para ser idêntico.
-- Coluna nullable preserva movimentações manuais e registros históricos, sem backfill fictício.
ALTER TABLE movimentacao ADD COLUMN solicitacao_id INT NULL;

-- ETAPA 2: após criar/conferir a coluna, diagnosticar referências órfãs.
-- Esta consulta deve retornar zero linhas antes de aplicar a FK.
SELECT m.id, m.solicitacao_id
FROM movimentacao m
LEFT JOIN solicitacao s ON s.id = m.solicitacao_id
WHERE m.solicitacao_id IS NOT NULL AND s.id IS NULL;

-- ETAPA 3: somente se o índice e a FK equivalentes não existirem.
CREATE INDEX idx_movimentacao_solicitacao ON movimentacao (solicitacao_id);
ALTER TABLE movimentacao
ADD CONSTRAINT fk_movimentacao_solicitacao
FOREIGN KEY (solicitacao_id) REFERENCES solicitacao (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ETAPA 4: índices opcionais para os filtros da API.
-- O MySQL pode já ter criado índices de FK: não criar índices equivalentes duplicados.
CREATE INDEX idx_movimentacao_produto ON movimentacao (produto_id);
CREATE INDEX idx_movimentacao_almoxarifado ON movimentacao (almoxarifado_id);
CREATE INDEX idx_movimentacao_tipo ON movimentacao (tipo);
CREATE INDEX idx_solicitacao_status ON solicitacao (status);
CREATE INDEX idx_solicitacao_solicitante ON solicitacao (solicitante_id);
CREATE INDEX idx_estoque_almoxarifado ON estoque (almoxarifado_id);

-- Aplicar unicidade de estoque separadamente: estoque-unicidade-manual.sql.
-- Não excluir nem consolidar dados legados para fazer qualquer ALTER passar.
