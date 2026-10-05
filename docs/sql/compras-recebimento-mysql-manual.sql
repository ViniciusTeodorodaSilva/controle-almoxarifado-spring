-- MySQL 8.0.16+; CHECK efetivo. DDL faz commit implícito.
-- BLOCO 4: proposta manual NÃO executada nesta rodada; não é migração automática/idempotente.
-- Requer schema dos Blocos 1–3 e Security Baseline 2, estoque único por produto/almoxarifado.
-- Revisar tipos reais dos IDs/FKs, constraints, collation, backup e homologação antes de autorização específica.
-- Não altera saldos, não inventa atores, não reconstrói recebimentos nem apaga histórico.
-- Necessidades antigas permanecem com quantidade_recebida NULL (A_CONFERIR); não preencher por inferência.
-- Inspecionar a restrição real de necessidade_compra.status. Ampliar preservando os valores anteriores
-- para ABERTA, EM_COMPRA, ATENDIDA, CANCELADA. Se existir ck_necessidade_status do script B3,
-- substituir APENAS essa constraint após conferir seu DDL; nunca executar DROP genérico.
SELECT id,status,quantidade FROM necessidade_compra;
SELECT produto_id,almoxarifado_id,COUNT(*) FROM estoque GROUP BY produto_id,almoxarifado_id HAVING COUNT(*)>1;
ALTER TABLE necessidade_compra ADD COLUMN quantidade_recebida DOUBLE NULL;
ALTER TABLE necessidade_compra ADD COLUMN motivo_cancelamento VARCHAR(1000) NULL;
ALTER TABLE necessidade_compra ADD CONSTRAINT ck_necessidade_recebida CHECK (quantidade_recebida IS NULL OR (quantidade_recebida >= 0 AND quantidade_recebida <= quantidade));

CREATE TABLE bes_fornecedor (
 id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(160) NOT NULL, nome_fantasia VARCHAR(160),
 tipo_pessoa VARCHAR(2) NOT NULL, documento VARCHAR(14) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
 email VARCHAR(160), telefone VARCHAR(40), contato VARCHAR(120), observacao VARCHAR(1000),
 ativo BOOLEAN NOT NULL DEFAULT TRUE, criado_em DATETIME(6) NOT NULL, atualizado_em DATETIME(6) NOT NULL,
 versao BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT uk_fornecedor_documento UNIQUE(documento),
 CONSTRAINT ck_fornecedor_tipo CHECK(tipo_pessoa IN ('PF','PJ'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE INDEX ix_fornecedor_nome ON bes_fornecedor(nome);
CREATE INDEX ix_fornecedor_ativo ON bes_fornecedor(ativo);
CREATE TABLE bes_pedido_compra (
 id INT AUTO_INCREMENT PRIMARY KEY, numero VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
 fornecedor_id INT NOT NULL, fornecedor_nome VARCHAR(160) NOT NULL,
 fornecedor_documento VARCHAR(14), fornecedor_contato VARCHAR(160), almoxarifado_id INT NOT NULL,
 status VARCHAR(30) NOT NULL, criado_em DATETIME(6) NOT NULL, criado_por BIGINT NOT NULL,
 criado_por_nome VARCHAR(80) NOT NULL, submetido_em DATETIME(6) NULL,
 aprovado_em DATETIME(6) NULL, aprovado_por BIGINT NULL, aprovado_por_nome VARCHAR(80),
 cancelado_em DATETIME(6) NULL, cancelado_por BIGINT NULL, cancelado_por_nome VARCHAR(80),
 motivo_cancelamento VARCHAR(1000), observacao VARCHAR(1000), versao BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT uk_pedido_numero UNIQUE(numero),
 CONSTRAINT fk_pedido_fornecedor FOREIGN KEY(fornecedor_id) REFERENCES bes_fornecedor(id),
 CONSTRAINT fk_pedido_local FOREIGN KEY(almoxarifado_id) REFERENCES almoxarifado(id),
 CONSTRAINT fk_pedido_criador FOREIGN KEY(criado_por) REFERENCES bes_usuario(id),
 CONSTRAINT fk_pedido_aprovador FOREIGN KEY(aprovado_por) REFERENCES bes_usuario(id),
 CONSTRAINT fk_pedido_cancelador FOREIGN KEY(cancelado_por) REFERENCES bes_usuario(id),
 CONSTRAINT ck_pedido_status CHECK(status IN ('RASCUNHO','AGUARDANDO_APROVACAO','APROVADO','PARCIALMENTE_RECEBIDO','RECEBIDO','CANCELADO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE INDEX ix_pedido_status_data ON bes_pedido_compra(status,criado_em);
CREATE INDEX ix_pedido_fornecedor ON bes_pedido_compra(fornecedor_id,criado_em);
CREATE TABLE bes_item_pedido_compra (
 id INT AUTO_INCREMENT PRIMARY KEY, pedido_id INT NOT NULL, produto_id INT NOT NULL,
 codigo VARCHAR(80) NOT NULL, nome VARCHAR(255) NOT NULL, unidade VARCHAR(255),
 quantidade_pedida DOUBLE NOT NULL, quantidade_recebida DOUBLE NOT NULL DEFAULT 0,
 quantidade_estoque DOUBLE NOT NULL, valor_unitario DECIMAL(19,4) NOT NULL,
 observacao VARCHAR(1000), ativo BOOLEAN NOT NULL DEFAULT TRUE,
 CONSTRAINT fk_item_compra_pedido FOREIGN KEY(pedido_id) REFERENCES bes_pedido_compra(id),
 CONSTRAINT fk_item_compra_produto FOREIGN KEY(produto_id) REFERENCES produto(id),
 CONSTRAINT ck_item_compra_quantidade CHECK(quantidade_pedida>0 AND quantidade_recebida>=0 AND quantidade_recebida<=quantidade_pedida AND quantidade_estoque>=0 AND valor_unitario>=0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE INDEX ix_item_compra_produto ON bes_item_pedido_compra(produto_id,pedido_id,ativo);
CREATE TABLE bes_alocacao_compra (
 id INT AUTO_INCREMENT PRIMARY KEY, item_id INT NOT NULL, necessidade_id INT NOT NULL,
 quantidade DOUBLE NOT NULL, quantidade_recebida DOUBLE NOT NULL DEFAULT 0,
 CONSTRAINT uk_alocacao_item_necessidade UNIQUE(item_id,necessidade_id),
 CONSTRAINT fk_alocacao_item FOREIGN KEY(item_id) REFERENCES bes_item_pedido_compra(id),
 CONSTRAINT fk_alocacao_necessidade FOREIGN KEY(necessidade_id) REFERENCES necessidade_compra(id),
 CONSTRAINT ck_alocacao_quantidade CHECK(quantidade>0 AND quantidade_recebida>=0 AND quantidade_recebida<=quantidade)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE INDEX ix_alocacao_necessidade ON bes_alocacao_compra(necessidade_id);
CREATE TABLE bes_recebimento_compra (
 id INT AUTO_INCREMENT PRIMARY KEY, pedido_id INT NOT NULL, almoxarifado_id INT NOT NULL, responsavel_id INT NOT NULL,
 recebido_por BIGINT NOT NULL, recebido_por_nome VARCHAR(80) NOT NULL,
 data_hora DATETIME(6) NOT NULL, observacao VARCHAR(1000),
 chave_idempotencia VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL, assinatura_payload VARCHAR(64) NOT NULL,
 CONSTRAINT uk_recebimento_chave UNIQUE(chave_idempotencia),
 CONSTRAINT fk_recebimento_pedido FOREIGN KEY(pedido_id) REFERENCES bes_pedido_compra(id),
 CONSTRAINT fk_recebimento_local FOREIGN KEY(almoxarifado_id) REFERENCES almoxarifado(id),
 CONSTRAINT fk_recebimento_responsavel FOREIGN KEY(responsavel_id) REFERENCES funcionario(id),
 CONSTRAINT fk_recebimento_ator FOREIGN KEY(recebido_por) REFERENCES bes_usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE INDEX ix_recebimento_pedido ON bes_recebimento_compra(pedido_id,data_hora);
CREATE INDEX ix_recebimento_local ON bes_recebimento_compra(almoxarifado_id,data_hora);
CREATE TABLE bes_item_recebimento_compra (
 id INT AUTO_INCREMENT PRIMARY KEY, recebimento_id INT NOT NULL, item_pedido_id INT NOT NULL,
 quantidade DOUBLE NOT NULL, saldo_anterior DOUBLE NOT NULL, saldo_posterior DOUBLE NOT NULL,
 CONSTRAINT uk_recebimento_item UNIQUE(recebimento_id,item_pedido_id),
 CONSTRAINT fk_receb_item_receb FOREIGN KEY(recebimento_id) REFERENCES bes_recebimento_compra(id),
 CONSTRAINT fk_receb_item_pedido FOREIGN KEY(item_pedido_id) REFERENCES bes_item_pedido_compra(id),
 CONSTRAINT ck_receb_item_quantidade CHECK(quantidade>0 AND saldo_anterior>=0 AND saldo_posterior>=saldo_anterior)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE INDEX ix_receb_item_pedido ON bes_item_recebimento_compra(item_pedido_id);
CREATE TABLE bes_destinacao_recebimento (
 id INT AUTO_INCREMENT PRIMARY KEY, item_recebimento_id INT NOT NULL, alocacao_id INT NOT NULL, quantidade DOUBLE NOT NULL,
 CONSTRAINT uk_destinacao_alocacao UNIQUE(item_recebimento_id,alocacao_id),
 CONSTRAINT fk_destinacao_item FOREIGN KEY(item_recebimento_id) REFERENCES bes_item_recebimento_compra(id),
 CONSTRAINT fk_destinacao_alocacao FOREIGN KEY(alocacao_id) REFERENCES bes_alocacao_compra(id),
 CONSTRAINT ck_destinacao_quantidade CHECK(quantidade>0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE INDEX ix_destinacao_alocacao ON bes_destinacao_recebimento(alocacao_id);
ALTER TABLE movimentacao ADD COLUMN item_recebimento_compra_id INT NULL;
ALTER TABLE movimentacao ADD COLUMN ator_compra_id BIGINT NULL;
ALTER TABLE movimentacao ADD CONSTRAINT uk_mov_item_recebimento UNIQUE(item_recebimento_compra_id);
ALTER TABLE movimentacao ADD CONSTRAINT fk_mov_item_recebimento FOREIGN KEY(item_recebimento_compra_id) REFERENCES bes_item_recebimento_compra(id);
ALTER TABLE movimentacao ADD CONSTRAINT fk_mov_ator_compra FOREIGN KEY(ator_compra_id) REFERENCES bes_usuario(id);
-- Linhas legadas mantêm referências/atores novos NULL.
-- Subtotais/totais são derivados no servidor (BigDecimal, HALF_UP em 2 casas); não copiar valores do cliente.
-- Estado/quantidades são atualizados atomicamente pelo service; somas entre linhas e compatibilidade
-- produto/destino/necessidade exigem locks e validação do service, não apenas constraints isoladas.
