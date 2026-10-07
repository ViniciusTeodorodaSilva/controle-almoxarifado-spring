-- Plataforma BES — Bloco 5. MANUAL, NÃO EXECUTADO.

-- Exige autorização, backup restaurável, homologação e conferência do schema real.

-- Não contém backfill: contexto histórico desconhecido permanece NULL.

-- DDL não é uma execução atômica em MySQL. Não reaplicar sem conferir objetos existentes.

-- Tipos esperados: IDs operacionais INT; bes_usuario.id BIGINT. Ajustar somente após inspeção autorizada.

CREATE TABLE bes_obra (
  id INT AUTO_INCREMENT PRIMARY KEY,
  versao BIGINT NOT NULL DEFAULT 0,
  codigo VARCHAR(50) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE,
  nome VARCHAR(160) NOT NULL,
  descricao VARCHAR(2000),
  cliente VARCHAR(160),
  localidade VARCHAR(160),
  observacao VARCHAR(2000),
  status VARCHAR(20) NOT NULL,
  responsavel_id INT,
  data_inicio DATE,
  data_termino_previsto DATE,
  data_termino_real DATE,
  criado_em DATETIME(6) NOT NULL,
  atualizado_em DATETIME(6) NOT NULL,
  criado_por BIGINT NOT NULL,
  alterado_por BIGINT NOT NULL,
  CONSTRAINT fk_bes_obra_criado_por FOREIGN KEY (criado_por) REFERENCES bes_usuario(id),
  CONSTRAINT fk_bes_obra_alterado_por FOREIGN KEY (alterado_por) REFERENCES bes_usuario(id),
  CONSTRAINT fk_bes_obra_responsavel FOREIGN KEY (responsavel_id) REFERENCES funcionario(id),
  CONSTRAINT ck_bes_obra_dominio CHECK (status in ('PLANEJADA','ATIVA','SUSPENSA','CONCLUIDA','CANCELADA'))
) ENGINE=InnoDB;

CREATE INDEX ix_bes_obra_filtros ON bes_obra (status, cliente);

CREATE TABLE bes_centro_custo (
  id INT AUTO_INCREMENT PRIMARY KEY,
  versao BIGINT NOT NULL DEFAULT 0,
  codigo VARCHAR(50) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE,
  nome VARCHAR(160) NOT NULL,
  descricao VARCHAR(2000),
  tipo VARCHAR(20) NOT NULL,
  ativo BOOLEAN NOT NULL,
  obra_id INT,
  criado_em DATETIME(6) NOT NULL,
  atualizado_em DATETIME(6) NOT NULL,
  criado_por BIGINT NOT NULL,
  alterado_por BIGINT NOT NULL,
  CONSTRAINT fk_bes_centro_custo_criado_por FOREIGN KEY (criado_por) REFERENCES bes_usuario(id),
  CONSTRAINT fk_bes_centro_custo_alterado_por FOREIGN KEY (alterado_por) REFERENCES bes_usuario(id),
  CONSTRAINT fk_bes_centro_custo_obra FOREIGN KEY (obra_id) REFERENCES bes_obra(id),
  CONSTRAINT ck_bes_centro_custo_dominio CHECK (tipo in ('OBRA','ADMINISTRATIVO','OPERACIONAL','OUTRO') and (tipo <> 'OBRA' or obra_id is not null))
) ENGINE=InnoDB;

CREATE INDEX ix_bes_centro_custo_filtros ON bes_centro_custo (obra_id, ativo);

CREATE TABLE bes_ordem_servico (
  id INT AUTO_INCREMENT PRIMARY KEY,
  versao BIGINT NOT NULL DEFAULT 0,
  numero VARCHAR(50) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE,
  obra_id INT NOT NULL,
  centro_custo_id INT,
  titulo VARCHAR(160) NOT NULL,
  descricao VARCHAR(2000),
  observacao VARCHAR(2000),
  prioridade VARCHAR(20),
  motivo_interrupcao VARCHAR(1000),
  status VARCHAR(20) NOT NULL,
  responsavel_id INT,
  data_abertura DATETIME(6) NOT NULL,
  data_inicio DATETIME(6),
  data_conclusao DATETIME(6),
  criado_em DATETIME(6) NOT NULL,
  atualizado_em DATETIME(6) NOT NULL,
  criado_por BIGINT NOT NULL,
  alterado_por BIGINT NOT NULL,
  CONSTRAINT fk_bes_ordem_servico_criado_por FOREIGN KEY (criado_por) REFERENCES bes_usuario(id),
  CONSTRAINT fk_bes_ordem_servico_alterado_por FOREIGN KEY (alterado_por) REFERENCES bes_usuario(id),
  CONSTRAINT fk_bes_ordem_servico_responsavel FOREIGN KEY (responsavel_id) REFERENCES funcionario(id),
  CONSTRAINT fk_bes_ordem_servico_obra FOREIGN KEY (obra_id) REFERENCES bes_obra(id),
  CONSTRAINT fk_bes_os_cc FOREIGN KEY (centro_custo_id) REFERENCES bes_centro_custo(id),
  CONSTRAINT ck_bes_ordem_servico_dominio CHECK (status in ('ABERTA','EM_ANDAMENTO','SUSPENSA','CONCLUIDA','CANCELADA'))
) ENGINE=InnoDB;

CREATE INDEX ix_bes_ordem_servico_filtros ON bes_ordem_servico (obra_id, centro_custo_id, status);

ALTER TABLE solicitacao ADD COLUMN contexto_obra_id INT NULL;

ALTER TABLE solicitacao ADD COLUMN contexto_ordem_servico_id INT NULL;

ALTER TABLE solicitacao ADD COLUMN contexto_centro_custo_id INT NULL;

ALTER TABLE solicitacao ADD COLUMN contexto_obra_codigo VARCHAR(50) NULL;

ALTER TABLE solicitacao ADD COLUMN contexto_obra_nome VARCHAR(160) NULL;

ALTER TABLE solicitacao ADD COLUMN contexto_ordem_servico_numero VARCHAR(50) NULL;

ALTER TABLE solicitacao ADD COLUMN contexto_centro_custo_codigo VARCHAR(50) NULL;

ALTER TABLE solicitacao ADD COLUMN contexto_centro_custo_nome VARCHAR(160) NULL;

ALTER TABLE solicitacao ADD CONSTRAINT fk_solicitacao_ctx_obra FOREIGN KEY (contexto_obra_id) REFERENCES bes_obra(id);

CREATE INDEX ix_solicitacao_ctx_obra ON solicitacao (contexto_obra_id);

ALTER TABLE solicitacao ADD CONSTRAINT fk_solicitacao_ctx_ordem_servico FOREIGN KEY (contexto_ordem_servico_id) REFERENCES bes_ordem_servico(id);

CREATE INDEX ix_solicitacao_ctx_ordem_servico ON solicitacao (contexto_ordem_servico_id);

ALTER TABLE solicitacao ADD CONSTRAINT fk_solicitacao_ctx_centro_custo FOREIGN KEY (contexto_centro_custo_id) REFERENCES bes_centro_custo(id);

CREATE INDEX ix_solicitacao_ctx_centro_custo ON solicitacao (contexto_centro_custo_id);

ALTER TABLE movimentacao ADD COLUMN contexto_obra_id INT NULL;

ALTER TABLE movimentacao ADD COLUMN contexto_ordem_servico_id INT NULL;

ALTER TABLE movimentacao ADD COLUMN contexto_centro_custo_id INT NULL;

ALTER TABLE movimentacao ADD COLUMN contexto_obra_codigo VARCHAR(50) NULL;

ALTER TABLE movimentacao ADD COLUMN contexto_obra_nome VARCHAR(160) NULL;

ALTER TABLE movimentacao ADD COLUMN contexto_ordem_servico_numero VARCHAR(50) NULL;

ALTER TABLE movimentacao ADD COLUMN contexto_centro_custo_codigo VARCHAR(50) NULL;

ALTER TABLE movimentacao ADD COLUMN contexto_centro_custo_nome VARCHAR(160) NULL;

ALTER TABLE movimentacao ADD CONSTRAINT fk_movimentacao_ctx_obra FOREIGN KEY (contexto_obra_id) REFERENCES bes_obra(id);

CREATE INDEX ix_movimentacao_ctx_obra ON movimentacao (contexto_obra_id);

ALTER TABLE movimentacao ADD CONSTRAINT fk_movimentacao_ctx_ordem_servico FOREIGN KEY (contexto_ordem_servico_id) REFERENCES bes_ordem_servico(id);

CREATE INDEX ix_movimentacao_ctx_ordem_servico ON movimentacao (contexto_ordem_servico_id);

ALTER TABLE movimentacao ADD CONSTRAINT fk_movimentacao_ctx_centro_custo FOREIGN KEY (contexto_centro_custo_id) REFERENCES bes_centro_custo(id);

CREATE INDEX ix_movimentacao_ctx_centro_custo ON movimentacao (contexto_centro_custo_id);

ALTER TABLE bes_item_pedido_compra ADD COLUMN contexto_obra_id INT NULL;

ALTER TABLE bes_item_pedido_compra ADD COLUMN contexto_ordem_servico_id INT NULL;

ALTER TABLE bes_item_pedido_compra ADD COLUMN contexto_centro_custo_id INT NULL;

ALTER TABLE bes_item_pedido_compra ADD COLUMN contexto_obra_codigo VARCHAR(50) NULL;

ALTER TABLE bes_item_pedido_compra ADD COLUMN contexto_obra_nome VARCHAR(160) NULL;

ALTER TABLE bes_item_pedido_compra ADD COLUMN contexto_ordem_servico_numero VARCHAR(50) NULL;

ALTER TABLE bes_item_pedido_compra ADD COLUMN contexto_centro_custo_codigo VARCHAR(50) NULL;

ALTER TABLE bes_item_pedido_compra ADD COLUMN contexto_centro_custo_nome VARCHAR(160) NULL;

ALTER TABLE bes_item_pedido_compra ADD CONSTRAINT fk_bes_item_pedido_compra_ctx_obra FOREIGN KEY (contexto_obra_id) REFERENCES bes_obra(id);

CREATE INDEX ix_bes_item_pedido_compra_ctx_obra ON bes_item_pedido_compra (contexto_obra_id);

ALTER TABLE bes_item_pedido_compra ADD CONSTRAINT fk_bes_item_pedido_compra_ctx_ordem_servico FOREIGN KEY (contexto_ordem_servico_id) REFERENCES bes_ordem_servico(id);

CREATE INDEX ix_bes_item_pedido_compra_ctx_ordem_servico ON bes_item_pedido_compra (contexto_ordem_servico_id);

ALTER TABLE bes_item_pedido_compra ADD CONSTRAINT fk_bes_item_pedido_compra_ctx_centro_custo FOREIGN KEY (contexto_centro_custo_id) REFERENCES bes_centro_custo(id);

CREATE INDEX ix_bes_item_pedido_compra_ctx_centro_custo ON bes_item_pedido_compra (contexto_centro_custo_id);

-- Necessidade e alocação obtêm contexto da solicitação imutável já referenciada por FKs.

-- Recebimentos conservam destinações por alocação, sem um contexto global fictício.

-- Movimentação recebe snapshot somente na SAÍDA confirmada do atendimento.

-- Auditoria reutiliza bes_auditoria. Não modifica saldos, status antigos ou atores históricos.

-- PostgreSQL requer driver/configuração/testes próprios; este script não certifica migração.
