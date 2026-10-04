-- BLOCO 1: script MySQL para revisão manual. NÃO executar como um lote às cegas.
-- Não executado pela aplicação. Produção permanece com ddl-auto=none.
-- Fazer backup e testar em banco isolado; DDL MySQL possui commit implícito.
-- Conferir SHOW CREATE TABLE e SHOW INDEX antes de cada etapa. Não duplicar objetos.
SHOW CREATE TABLE produto;
SHOW INDEX FROM produto;

-- Criar apenas se as tabelas não existirem. IDs devem ter tipo compatível com produto.id.
CREATE TABLE categoria_material (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(255) NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    nome_normalizado VARCHAR(255) NOT NULL,
    CONSTRAINT uk_categoria_nome_normalizado UNIQUE (nome_normalizado)
) ENGINE=InnoDB;

CREATE TABLE unidade_medida (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    sigla VARCHAR(64) NOT NULL,
    permite_fracionamento BOOLEAN NOT NULL DEFAULT FALSE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_unidade_sigla UNIQUE (sigla)
) ENGINE=InnoDB;

-- Executar apenas para colunas ainda inexistentes. Preservar categoria/unidade_medida textuais.
ALTER TABLE produto ADD COLUMN codigo VARCHAR(64) NULL;
ALTER TABLE produto ADD COLUMN especificacao_tecnica VARCHAR(2000) NULL;
ALTER TABLE produto ADD COLUMN ativo BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE produto ADD COLUMN categoria_material_id INT NULL;
ALTER TABLE produto ADD COLUMN unidade_medida_id INT NULL;

-- Diagnóstico antes de criar unicidade. A aplicação escreve códigos ASCII em maiúsculas.
SELECT UPPER(TRIM(codigo)) AS codigo_normalizado, COUNT(*) AS registros
FROM produto WHERE codigo IS NOT NULL
GROUP BY UPPER(TRIM(codigo)) HAVING COUNT(*) > 1;

-- Etapa OPCIONAL de preenchimento do campo novo, somente após conferir colisões.
-- Não muda IDs, nomes, saldos ou referências; prefixo legado evita confundir código de negócio.
-- Verificar se algum código já usa BES-LEGADO-{id} antes de aprovar este preenchimento.
SELECT p.id, CONCAT('BES-LEGADO-', p.id) AS codigo_proposto
FROM produto p WHERE p.codigo IS NULL;
-- UPDATE produto SET codigo = CONCAT('BES-LEGADO-', id) WHERE codigo IS NULL;

-- Unicidade permite códigos nulos durante transição. A API gera código quando omitido.
ALTER TABLE produto ADD CONSTRAINT uk_produto_codigo UNIQUE (codigo);
CREATE INDEX idx_produto_categoria_material ON produto (categoria_material_id);
CREATE INDEX idx_produto_unidade_medida ON produto (unidade_medida_id);
CREATE INDEX idx_produto_ativo ON produto (ativo);

ALTER TABLE produto ADD CONSTRAINT fk_produto_categoria_material
FOREIGN KEY (categoria_material_id) REFERENCES categoria_material(id) ON DELETE RESTRICT;
ALTER TABLE produto ADD CONSTRAINT fk_produto_unidade_medida
FOREIGN KEY (unidade_medida_id) REFERENCES unidade_medida(id) ON DELETE RESTRICT;

-- Não converter automaticamente textos legados em cadastros oficiais.
-- Cadastrar categorias/unidades pela API para aplicar normalização e revisão humana.
-- Mapear referências em etapa própria, após conferir histórico e fracionamento existente.
-- Não há DROP, DELETE, alteração de saldo ou remoção de campos legados neste script.
