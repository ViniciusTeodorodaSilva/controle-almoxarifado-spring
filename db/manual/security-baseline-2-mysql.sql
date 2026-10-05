-- SECURITY BASELINE 2: manual preparation only, NOT executed.
-- Review backup/schema and funcionario.id type first. No operational data changes.
-- No initial user, hash, password or credentials. Bootstrap externally afterward.
-- Roles/permissions are Java enums; profile column stores their approved names.
-- MySQL 8+: run on the intended schema only after deployment approval.
-- CREATE fails if tables already exist: investigate rather than silently accepting drift.
CREATE TABLE bes_usuario (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 username VARCHAR(80) NOT NULL,
 senha_hash VARCHAR(100) NOT NULL,
 bootstrap_chave VARCHAR(20) NULL,
 nome_exibicao VARCHAR(120) NOT NULL,
 perfil VARCHAR(20) NOT NULL,
 ativo BOOLEAN NOT NULL,
 funcionario_id INT NULL,
 criado_em DATETIME(6) NOT NULL,
 atualizado_em DATETIME(6) NOT NULL,
 ultimo_login_em DATETIME(6) NULL,
 auth_version BIGINT NOT NULL,
 versao BIGINT NOT NULL,
 CONSTRAINT uk_bes_usuario_username UNIQUE (username),
 CONSTRAINT uk_bes_usuario_bootstrap UNIQUE (bootstrap_chave),
 CONSTRAINT fk_bes_usuario_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionario(id),
 CONSTRAINT ck_bes_usuario_perfil CHECK (perfil IN ('ADMIN','GESTOR','ALMOXARIFE','CONSULTA'))
) ENGINE=InnoDB;
CREATE TABLE bes_auditoria (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 instante DATETIME(6) NOT NULL,
 ator_id BIGINT NULL,
 ator_username VARCHAR(80) NULL,
 evento VARCHAR(100) NOT NULL,
 entidade VARCHAR(100) NULL,
 referencia VARCHAR(80) NULL,
 resultado VARCHAR(20) NOT NULL,
 responsavel_operacional_id INT NULL,
 request_id VARCHAR(36) NULL,
 antes VARCHAR(1500) NULL,
 depois VARCHAR(1500) NULL,
 INDEX ix_bes_auditoria_instante (instante),
 INDEX ix_bes_auditoria_ator (ator_id)
) ENGINE=InnoDB;
-- Actor/responsible ids are historical snapshots, not cascading relations.
-- Use UTC for application/JDBC/database time; review grants (audit INSERT/SELECT only).
-- No DROP/DELETE/UPDATE or ALTER on legacy tables; do not enable ddl-auto=update.
