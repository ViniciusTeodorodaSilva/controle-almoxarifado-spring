-- Bloco 6: manual proposal. DO NOT execute without backup, homologation and authorization.

-- Reuses existing actors, employees, warehouses, categories and structure. No legacy backfill.

-- MySQL 8.0.16+ requires effective CHECK; PostgreSQL needs its own driver/configuration.

CREATE TABLE bes_ativo (
  id INT AUTO_INCREMENT PRIMARY KEY,
  versao BIGINT NOT NULL DEFAULT 0,
  codigo_patrimonial VARCHAR(50) NOT NULL UNIQUE,
  nome VARCHAR(160) NOT NULL,
  descricao VARCHAR(2000),
  fabricante VARCHAR(160),
  modelo VARCHAR(160),
  numero_serie VARCHAR(160),
  observacao VARCHAR(2000),
  categoria_id INT,
  almoxarifado_id INT,
  responsavel_id INT,
  pendencia_id INT UNIQUE,
  data_aquisicao DATE,
  proxima_inspecao DATE,
  ativo BOOLEAN NOT NULL,
  reprovado BOOLEAN NOT NULL,
  criado_em DATETIME(6) NOT NULL,
  atualizado_em DATETIME(6) NOT NULL,
  criado_por BIGINT NOT NULL,
  alterado_por BIGINT NOT NULL,
  status VARCHAR(30) NOT NULL,
  condicao VARCHAR(30) NOT NULL,
  contexto_obra_id INT,
  contexto_ordem_servico_id INT,
  contexto_centro_custo_id INT,
  contexto_obra_codigo VARCHAR(50),
  contexto_obra_nome VARCHAR(160),
  contexto_ordem_servico_numero VARCHAR(50),
  contexto_centro_custo_codigo VARCHAR(50),
  contexto_centro_custo_nome VARCHAR(160),
  CONSTRAINT ck_ativo_status CHECK (status IN ('DISPONIVEL','EMPRESTADO','EM_TRANSFERENCIA','INDISPONIVEL','BAIXADO')),
  CONSTRAINT ck_ativo_condicao CHECK (condicao IN ('NOVO','BOM','REGULAR','DANIFICADO','INOPERANTE'))
);

CREATE INDEX ix_ativo_status ON bes_ativo (status,ativo);

CREATE INDEX ix_ativo_local ON bes_ativo (almoxarifado_id);

CREATE INDEX ix_ativo_responsavel ON bes_ativo (responsavel_id);

CREATE INDEX ix_ativo_inspecao ON bes_ativo (proxima_inspecao);

CREATE INDEX ix_ativo_contexto_obra_id ON bes_ativo (contexto_obra_id);

CREATE INDEX ix_ativo_contexto_ordem_servico_id ON bes_ativo (contexto_ordem_servico_id);

CREATE INDEX ix_ativo_contexto_centro_custo_id ON bes_ativo (contexto_centro_custo_id);

CREATE TABLE bes_registro_ativo (
  id INT AUTO_INCREMENT PRIMARY KEY,
  ativo_id INT NOT NULL,
  origem_id INT UNIQUE,
  codigo_patrimonial VARCHAR(50) NOT NULL,
  ativo_nome VARCHAR(160) NOT NULL,
  entregue_por_id INT,
  entregue_por_nome VARCHAR(255),
  recebido_por_id INT,
  recebido_por_nome VARCHAR(255),
  local_origem_id INT,
  local_origem_nome VARCHAR(255),
  local_destino_id INT,
  local_destino_nome VARCHAR(255),
  responsavel_antes_id INT,
  responsavel_antes_nome VARCHAR(255),
  responsavel_depois_id INT,
  responsavel_depois_nome VARCHAR(255),
  data_hora DATETIME(6) NOT NULL,
  previsao_devolucao DATE,
  proxima_inspecao DATE,
  observacao VARCHAR(2000),
  ator_id BIGINT NOT NULL,
  ator_nome VARCHAR(255) NOT NULL,
  chave_idempotencia VARCHAR(100) UNIQUE,
  assinatura VARCHAR(64),
  tipo VARCHAR(30) NOT NULL,
  status_antes VARCHAR(30) NOT NULL,
  status_depois VARCHAR(30) NOT NULL,
  condicao_antes VARCHAR(30) NOT NULL,
  condicao_depois VARCHAR(30) NOT NULL,
  resultado VARCHAR(30),
  origem_contexto_obra_id INT,
  origem_contexto_ordem_servico_id INT,
  origem_contexto_centro_custo_id INT,
  origem_contexto_obra_codigo VARCHAR(50),
  origem_contexto_obra_nome VARCHAR(160),
  origem_contexto_ordem_servico_numero VARCHAR(50),
  origem_contexto_centro_custo_codigo VARCHAR(50),
  origem_contexto_centro_custo_nome VARCHAR(160),
  destino_contexto_obra_id INT,
  destino_contexto_ordem_servico_id INT,
  destino_contexto_centro_custo_id INT,
  destino_contexto_obra_codigo VARCHAR(50),
  destino_contexto_obra_nome VARCHAR(160),
  destino_contexto_ordem_servico_numero VARCHAR(50),
  destino_contexto_centro_custo_codigo VARCHAR(50),
  destino_contexto_centro_custo_nome VARCHAR(160),
  CONSTRAINT ck_registro_ativo_tipo CHECK (tipo IN ('EMPRESTIMO','DEVOLUCAO','TRANSFERENCIA','RECEBIMENTO','INSPECAO','INATIVACAO','REATIVACAO','BAIXA')),
  CONSTRAINT ck_registro_ativo_status_antes CHECK (status_antes IN ('DISPONIVEL','EMPRESTADO','EM_TRANSFERENCIA','INDISPONIVEL','BAIXADO')),
  CONSTRAINT ck_registro_ativo_status_depois CHECK (status_depois IN ('DISPONIVEL','EMPRESTADO','EM_TRANSFERENCIA','INDISPONIVEL','BAIXADO')),
  CONSTRAINT ck_registro_ativo_condicao_antes CHECK (condicao_antes IN ('NOVO','BOM','REGULAR','DANIFICADO','INOPERANTE')),
  CONSTRAINT ck_registro_ativo_condicao_depois CHECK (condicao_depois IN ('NOVO','BOM','REGULAR','DANIFICADO','INOPERANTE')),
  CONSTRAINT ck_registro_ativo_resultado CHECK (resultado IN ('APROVADO','APROVADO_COM_RESSALVA','REPROVADO'))
);

CREATE INDEX ix_registro_ativo ON bes_registro_ativo (ativo_id,tipo,data_hora);

CREATE INDEX ix_registro_previsao ON bes_registro_ativo (tipo,previsao_devolucao);

CREATE INDEX ix_registro_origem ON bes_registro_ativo (origem_id);

CREATE INDEX ix_registro_ativo_origem_contexto_obra_id ON bes_registro_ativo (origem_contexto_obra_id);

CREATE INDEX ix_registro_ativo_origem_contexto_ordem_servico_id ON bes_registro_ativo (origem_contexto_ordem_servico_id);

CREATE INDEX ix_registro_ativo_origem_contexto_centro_custo_id ON bes_registro_ativo (origem_contexto_centro_custo_id);

CREATE INDEX ix_registro_ativo_destino_contexto_obra_id ON bes_registro_ativo (destino_contexto_obra_id);

CREATE INDEX ix_registro_ativo_destino_contexto_ordem_servico_id ON bes_registro_ativo (destino_contexto_ordem_servico_id);

CREATE INDEX ix_registro_ativo_destino_contexto_centro_custo_id ON bes_registro_ativo (destino_contexto_centro_custo_id);

ALTER TABLE bes_ativo ADD CONSTRAINT fk_ativo_categoria_id FOREIGN KEY (categoria_id) REFERENCES categoria_material(id);

ALTER TABLE bes_ativo ADD CONSTRAINT fk_ativo_almoxarifado_id FOREIGN KEY (almoxarifado_id) REFERENCES almoxarifado(id);

ALTER TABLE bes_ativo ADD CONSTRAINT fk_ativo_responsavel_id FOREIGN KEY (responsavel_id) REFERENCES funcionario(id);

ALTER TABLE bes_ativo ADD CONSTRAINT fk_ativo_pendencia_id FOREIGN KEY (pendencia_id) REFERENCES bes_registro_ativo(id);

ALTER TABLE bes_ativo ADD CONSTRAINT fk_ativo_criado_por FOREIGN KEY (criado_por) REFERENCES bes_usuario(id);

ALTER TABLE bes_ativo ADD CONSTRAINT fk_ativo_alterado_por FOREIGN KEY (alterado_por) REFERENCES bes_usuario(id);

ALTER TABLE bes_ativo ADD CONSTRAINT fk_ativo_contexto_obra_id FOREIGN KEY (contexto_obra_id) REFERENCES bes_obra(id);

ALTER TABLE bes_ativo ADD CONSTRAINT fk_ativo_contexto_ordem_servico_id FOREIGN KEY (contexto_ordem_servico_id) REFERENCES bes_ordem_servico(id);

ALTER TABLE bes_ativo ADD CONSTRAINT fk_ativo_contexto_centro_custo_id FOREIGN KEY (contexto_centro_custo_id) REFERENCES bes_centro_custo(id);

ALTER TABLE bes_registro_ativo ADD CONSTRAINT fk_registro_ativo_ativo_id FOREIGN KEY (ativo_id) REFERENCES bes_ativo(id);

ALTER TABLE bes_registro_ativo ADD CONSTRAINT fk_registro_ativo_origem_id FOREIGN KEY (origem_id) REFERENCES bes_registro_ativo(id);

ALTER TABLE bes_registro_ativo ADD CONSTRAINT fk_registro_ativo_ator_id FOREIGN KEY (ator_id) REFERENCES bes_usuario(id);

ALTER TABLE bes_registro_ativo ADD CONSTRAINT fk_registro_ativo_entregue_por_id FOREIGN KEY (entregue_por_id) REFERENCES funcionario(id);

ALTER TABLE bes_registro_ativo ADD CONSTRAINT fk_registro_ativo_recebido_por_id FOREIGN KEY (recebido_por_id) REFERENCES funcionario(id);

ALTER TABLE bes_registro_ativo ADD CONSTRAINT fk_registro_ativo_responsavel_antes_id FOREIGN KEY (responsavel_antes_id) REFERENCES funcionario(id);

ALTER TABLE bes_registro_ativo ADD CONSTRAINT fk_registro_ativo_responsavel_depois_id FOREIGN KEY (responsavel_depois_id) REFERENCES funcionario(id);

ALTER TABLE bes_registro_ativo ADD CONSTRAINT fk_registro_ativo_local_origem_id FOREIGN KEY (local_origem_id) REFERENCES almoxarifado(id);

ALTER TABLE bes_registro_ativo ADD CONSTRAINT fk_registro_ativo_local_destino_id FOREIGN KEY (local_destino_id) REFERENCES almoxarifado(id);

ALTER TABLE bes_registro_ativo ADD CONSTRAINT fk_registro_ativo_origem_contexto_obra_id FOREIGN KEY (origem_contexto_obra_id) REFERENCES bes_obra(id);

ALTER TABLE bes_registro_ativo ADD CONSTRAINT fk_registro_ativo_origem_contexto_ordem_servico_id FOREIGN KEY (origem_contexto_ordem_servico_id) REFERENCES bes_ordem_servico(id);

ALTER TABLE bes_registro_ativo ADD CONSTRAINT fk_registro_ativo_origem_contexto_centro_custo_id FOREIGN KEY (origem_contexto_centro_custo_id) REFERENCES bes_centro_custo(id);

ALTER TABLE bes_registro_ativo ADD CONSTRAINT fk_registro_ativo_destino_contexto_obra_id FOREIGN KEY (destino_contexto_obra_id) REFERENCES bes_obra(id);

ALTER TABLE bes_registro_ativo ADD CONSTRAINT fk_registro_ativo_destino_contexto_ordem_servico_id FOREIGN KEY (destino_contexto_ordem_servico_id) REFERENCES bes_ordem_servico(id);

ALTER TABLE bes_registro_ativo ADD CONSTRAINT fk_registro_ativo_destino_contexto_centro_custo_id FOREIGN KEY (destino_contexto_centro_custo_id) REFERENCES bes_centro_custo(id);

-- No DELETE CASCADE. Business history is append-only through the API. Restrict DML grants in deployment.
-- No cost/depreciation, stock balance, maintenance, QR, attachment storage or real database changes in this block.
