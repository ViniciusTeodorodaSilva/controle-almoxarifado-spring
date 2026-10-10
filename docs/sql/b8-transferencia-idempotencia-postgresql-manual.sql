-- B8 / V008_01. Manual: NÃO executado nem autorizado em banco real nesta rodada.
-- Pré-condição: tabela transferencia_estoque do Bloco 2 existente; backup e homologação.
-- Legado permanece NULL; não inventar chaves para operações anteriores.
BEGIN;
ALTER TABLE transferencia_estoque ADD COLUMN chave_idempotencia VARCHAR(100) COLLATE "C" NULL;
ALTER TABLE transferencia_estoque ADD COLUMN hash_requisicao VARCHAR(64) NULL;
CREATE UNIQUE INDEX uk_transferencia_chave_idempotencia ON transferencia_estoque(chave_idempotencia);
ALTER TABLE movimentacao ADD COLUMN chave_idempotencia VARCHAR(100) COLLATE "C" NULL;
ALTER TABLE movimentacao ADD COLUMN hash_requisicao VARCHAR(64) NULL;
CREATE UNIQUE INDEX uk_movimentacao_chave_idempotencia ON movimentacao(chave_idempotencia);
COMMIT;
-- Executar uma vez; revisar information_schema antes/depois. Sem aplicação automática.
-- Rollback destrói proteção de replay e exige avaliação dos registros novos.
