-- Bloco 7. Manual proposal, NOT executed. Require authorization, backup/restore and homologation.

-- No backfill, no physical balance, no DELETE CASCADE. MySQL 8.0.16+ for effective CHECK.

CREATE TABLE bes_epi_config (
  produto_id INT PRIMARY KEY,
  versao BIGINT NOT NULL,
  ca VARCHAR(20) NOT NULL,
  fabricante VARCHAR(160),
  modelo VARCHAR(160),
  tamanho VARCHAR(50),
  validade_ca DATE,
  dias_substituicao INT,
  ativo BOOLEAN NOT NULL,
  exige_devolucao BOOLEAN NOT NULL,
  permite_retorno BOOLEAN NOT NULL,
  observacao VARCHAR(2000),
  alterado_por BIGINT NOT NULL,
  alterado_em DATETIME(6) NOT NULL,
  CHECK (dias_substituicao IS NULL OR dias_substituicao BETWEEN 1 AND 36500),
  FOREIGN KEY (produto_id) REFERENCES produto(id),
  FOREIGN KEY (alterado_por) REFERENCES bes_usuario(id)
);

CREATE INDEX ix_epi_ca ON bes_epi_config (ca);

CREATE INDEX ix_epi_ativo ON bes_epi_config (ativo);

CREATE TABLE bes_epi_registro (
  id INT AUTO_INCREMENT PRIMARY KEY,
  tipo VARCHAR(30) NOT NULL,
  motivo VARCHAR(30),
  funcionario_id INT NOT NULL,
  funcionario_nome VARCHAR(255) NOT NULL,
  funcionario_matricula VARCHAR(255),
  responsavel_id INT NOT NULL,
  responsavel_nome VARCHAR(255) NOT NULL,
  ator_id BIGINT NOT NULL,
  ator_nome VARCHAR(160) NOT NULL,
  almoxarifado_id INT NOT NULL,
  almoxarifado_nome VARCHAR(255) NOT NULL,
  data_hora DATETIME(6) NOT NULL,
  recebimento_confirmado BOOLEAN NOT NULL,
  observacao VARCHAR(2000),
  chave_idempotencia VARCHAR(100) NOT NULL UNIQUE,
  assinatura VARCHAR(64) NOT NULL,
  contexto_obra_id INT,
  contexto_ordem_servico_id INT,
  contexto_centro_custo_id INT,
  contexto_obra_codigo VARCHAR(50),
  contexto_obra_nome VARCHAR(160),
  contexto_ordem_servico_numero VARCHAR(50),
  contexto_centro_custo_codigo VARCHAR(50),
  contexto_centro_custo_nome VARCHAR(160),
  FOREIGN KEY (funcionario_id) REFERENCES funcionario(id),
  FOREIGN KEY (responsavel_id) REFERENCES funcionario(id),
  FOREIGN KEY (ator_id) REFERENCES bes_usuario(id),
  FOREIGN KEY (almoxarifado_id) REFERENCES almoxarifado(id),
  FOREIGN KEY (contexto_obra_id) REFERENCES bes_obra(id),
  FOREIGN KEY (contexto_ordem_servico_id) REFERENCES bes_ordem_servico(id),
  FOREIGN KEY (contexto_centro_custo_id) REFERENCES bes_centro_custo(id)
);

CREATE INDEX ix_epi_reg_func ON bes_epi_registro (funcionario_id,data_hora);

CREATE INDEX ix_epi_reg_tipo ON bes_epi_registro (tipo,data_hora);

CREATE INDEX ix_epi_reg_obra ON bes_epi_registro (contexto_obra_id);

CREATE INDEX ix_epi_reg_os ON bes_epi_registro (contexto_ordem_servico_id);

CREATE INDEX ix_epi_reg_cc ON bes_epi_registro (contexto_centro_custo_id);

CREATE TABLE bes_epi_item (
  id INT AUTO_INCREMENT PRIMARY KEY,
  registro_id INT NOT NULL,
  produto_id INT NOT NULL,
  produto_codigo VARCHAR(64),
  produto_nome VARCHAR(255) NOT NULL,
  unidade VARCHAR(64) NOT NULL,
  quantidade DECIMAL(19,6) NOT NULL,
  entrega BOOLEAN NOT NULL,
  fracionado BOOLEAN NOT NULL,
  contexto_obra_id INT,
  contexto_ordem_servico_id INT,
  contexto_centro_custo_id INT,
  contexto_obra_codigo VARCHAR(50),
  contexto_obra_nome VARCHAR(160),
  contexto_ordem_servico_numero VARCHAR(50),
  contexto_centro_custo_codigo VARCHAR(50),
  contexto_centro_custo_nome VARCHAR(160),
  origem_item_id INT,
  movimento_id INT UNIQUE,
  ca VARCHAR(20) NOT NULL,
  validade_ca DATE,
  fabricante VARCHAR(160),
  modelo VARCHAR(160),
  tamanho VARCHAR(50),
  lote VARCHAR(100),
  fabricacao DATE,
  validade_fisica DATE,
  substituir_ate DATE,
  exige_devolucao BOOLEAN NOT NULL,
  permite_retorno BOOLEAN NOT NULL,
  condicao VARCHAR(30),
  destino VARCHAR(30),
  observacao VARCHAR(2000),
  CHECK (quantidade > 0),
  FOREIGN KEY (registro_id) REFERENCES bes_epi_registro(id),
  FOREIGN KEY (produto_id) REFERENCES produto(id),
  FOREIGN KEY (contexto_obra_id) REFERENCES bes_obra(id),
  FOREIGN KEY (contexto_ordem_servico_id) REFERENCES bes_ordem_servico(id),
  FOREIGN KEY (contexto_centro_custo_id) REFERENCES bes_centro_custo(id),
  FOREIGN KEY (origem_item_id) REFERENCES bes_epi_item(id),
  FOREIGN KEY (movimento_id) REFERENCES movimentacao(id)
);

CREATE INDEX ix_epi_item_reg ON bes_epi_item (registro_id);

CREATE INDEX ix_epi_item_produto ON bes_epi_item (produto_id);

CREATE INDEX ix_epi_item_origem ON bes_epi_item (origem_item_id,entrega);

CREATE INDEX ix_epi_item_validade ON bes_epi_item (validade_fisica);

CREATE INDEX ix_epi_item_troca ON bes_epi_item (substituir_ate);
