package br.com.almoxarifado.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class MigrationIdempotenciaTests {
    @ParameterizedTest
    @CsvSource({"MySQL,mysql", "PostgreSQL,postgresql"})
    void contratoManualPreservaLegadoEImpedeChaveDuplicada(String mode, String script) throws Exception {
        // H2 compatibility is a contract test, not homologation of either real engine.
        try (var conn = DriverManager.getConnection("jdbc:h2:mem:b8-" + UUID.randomUUID() + ";MODE=" + mode)) {
            try (var stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE transferencia_estoque(id INTEGER PRIMARY KEY)");
                stmt.execute("CREATE TABLE movimentacao(id INTEGER PRIMARY KEY)");
                stmt.execute("INSERT INTO transferencia_estoque VALUES (1),(2)");
                stmt.execute("INSERT INTO movimentacao VALUES (1),(2)");
                String sql = Files.readString(Path.of("docs/sql/b8-transferencia-idempotencia-" + script + "-manual.sql"));
                // Native collation syntax is not supported by H2. This test checks expansion,
                // nullable legacy data and uniqueness only; native collation needs homologation.
                sql = sql.replace(" CHARACTER SET ascii COLLATE ascii_bin", "").replace(" COLLATE \"C\"", "");
                for (String command : sql.replaceAll("(?m)--[^\\r\\n]*", "").split(";"))
                    if (!command.isBlank()) stmt.execute(command);
                for (String table : new String[]{"transferencia_estoque", "movimentacao"}) {
                    try (var rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table + " WHERE chave_idempotencia IS NULL")) {
                        assertTrue(rs.next()); assertEquals(2, rs.getInt(1));
                    }
                    stmt.execute("INSERT INTO " + table + " VALUES (3,'b8-migration-key-001','" + "a".repeat(64) + "')");
                    var erro = assertThrows(SQLException.class, () -> stmt.execute("INSERT INTO " + table + " VALUES (4,'b8-migration-key-001','" + "b".repeat(64) + "')"));
                    assertTrue(erro.getSQLState().startsWith("23"));
                    stmt.execute("INSERT INTO " + table + " VALUES (5,'B8-MIGRATION-KEY-001','" + "a".repeat(64) + "')");
                }
            }
        }
    }
}
