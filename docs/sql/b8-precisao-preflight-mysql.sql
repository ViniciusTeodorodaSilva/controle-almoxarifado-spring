-- Proposta B8: leitura de metadados para execução futura autorizada. NÃO executado em banco real.
-- Preserva MySQL, colunas e dados atuais. Não contém DDL, UPDATE, arredondamento ou backfill.
SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE, COLUMN_TYPE, NUMERIC_PRECISION, NUMERIC_SCALE, IS_NULLABLE
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND (COLUMN_NAME LIKE 'quantidade%' OR COLUMN_NAME LIKE 'saldo_%'
       OR COLUMN_NAME IN ('valor_unitario', 'estoque_minimo', 'estoque_maximo'))
ORDER BY TABLE_NAME, ORDINAL_POSITION;

-- DECIMAL(P,S) permanece sem definição: não converter automaticamente DOUBLE para DECIMAL(19,6).
-- Após aprovação de P/S por unidade: ensaiar expansão com coluna paralela, comparar linha a linha
-- e totais/referências, registrar incompatíveis sem reescrever origem e validar leitores/escritores.
-- Contração somente após reconciliação aprovada, backup/restore ensaiado e corte controlado.
