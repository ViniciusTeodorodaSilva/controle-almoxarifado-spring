-- B8 / V008_01. Manual: NÃO executado nem autorizado em banco real nesta rodada.
-- Aplicar somente após revisão, backup restaurável e homologação MySQL autorizada.
-- Pré-condição: tabela transferencia_estoque do Bloco 2 existente.
-- Legado permanece NULL; não inventar chaves para operações anteriores.
ALTER TABLE transferencia_estoque ADD COLUMN chave_idempotencia VARCHAR(100) CHARACTER SET ascii COLLATE ascii_bin NULL;
ALTER TABLE transferencia_estoque ADD COLUMN hash_requisicao VARCHAR(64) NULL;
CREATE UNIQUE INDEX uk_transferencia_chave_idempotencia ON transferencia_estoque(chave_idempotencia);
ALTER TABLE movimentacao ADD COLUMN chave_idempotencia VARCHAR(100) CHARACTER SET ascii COLLATE ascii_bin NULL;
ALTER TABLE movimentacao ADD COLUMN hash_requisicao VARCHAR(64) NULL;
CREATE UNIQUE INDEX uk_movimentacao_chave_idempotencia ON movimentacao(chave_idempotencia);
-- Contrato: executar uma vez; não ignorar erros/reaplicar em schema parcialmente migrado.
-- Verificação prévia: information_schema.columns/statistics; após: chaves não nulas únicas.
-- Rollback exige avaliação dos registros novos; remover colunas perde proteção de replay.
