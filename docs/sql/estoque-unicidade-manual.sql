-- Preparação manual para MySQL. NÃO é executada automaticamente pela aplicação.
-- Fazer backup e revisar o resultado destas duas consultas antes de aplicar a constraint.
SELECT produto_id, almoxarifado_id, COUNT(*) AS quantidade_registros
FROM estoque
GROUP BY produto_id, almoxarifado_id
HAVING COUNT(*) > 1;

SELECT id, produto_id, almoxarifado_id
FROM estoque
WHERE produto_id IS NULL OR almoxarifado_id IS NULL;

-- Aplicar apenas se as consultas não retornarem linhas e a constraint ainda não existir.
-- Não remove, consolida ou modifica registros. Se houver duplicatas, o ALTER falha.
ALTER TABLE estoque
ADD CONSTRAINT uk_estoque_produto_almoxarifado UNIQUE (produto_id, almoxarifado_id);
