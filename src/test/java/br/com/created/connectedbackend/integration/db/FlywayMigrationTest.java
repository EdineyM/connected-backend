package br.com.created.connectedbackend.integration.db;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class FlywayMigrationTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Flyway flyway;

    @BeforeEach
    void setUp() {
        flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .schemas("core", "address", "event", "lead", "audit", "cache", "ref")
                .load();
    }

    @Test
    @DisplayName("Deve executar todas as migrações com sucesso")
    void shouldExecuteAllMigrations() {
        flyway.clean();
        flyway.migrate();

        MigrationInfo[] appliedMigrations = flyway.info().applied();
        assertNotNull(appliedMigrations);
        assertTrue(appliedMigrations.length > 0);

        // Verificar se não há migrações pendentes
        assertEquals(0, flyway.info().pending().length);

        // Verificar se não há migrações com erro
        Arrays.stream(appliedMigrations).forEach(migration -> {
            assertNull(migration.getError(),
                    "Migration " + migration.getScript() + " failed: " + migration.getError());
        });
    }

    @Test
    @DisplayName("Deve criar todos os schemas necessários")
    void shouldCreateAllSchemas() {
        List<String> expectedSchemas = Arrays.asList(
                "core", "address", "event", "lead", "audit", "cache", "ref"
        );

        List<String> existingSchemas = jdbcTemplate.queryForList(
                "SELECT schema_name FROM information_schema.schemata WHERE schema_name = ANY(?)",
                String[].class,
                expectedSchemas.toArray()
        );

        assertEquals(expectedSchemas.size(), existingSchemas.size());
        assertTrue(existingSchemas.containsAll(expectedSchemas));
    }

    @Test
    @DisplayName("Deve criar todas as tabelas necessárias")
    void shouldCreateAllTables() {
        Map<String, List<String>> expectedTables = Map.of(
                "lead", Arrays.asList("lead", "ingresso_participante", "captura_lead", "audio_descricao"),
                "ref", Arrays.asList("usuario_tipos", "evento_tipos", "lead_status"),
                "core", Arrays.asList("usuario", "empresa", "exportacao_dados")
        );

        expectedTables.forEach((schema, tables) -> {
            List<String> existingTables = jdbcTemplate.queryForList(
                    """
                    SELECT table_name 
                    FROM information_schema.tables 
                    WHERE table_schema = ? 
                    AND table_type = 'BASE TABLE'
                    """,
                    String.class,
                    schema
            );

            assertTrue(existingTables.containsAll(tables),
                    "Schema " + schema + " missing tables: " +
                            tables.stream()
                                    .filter(table -> !existingTables.contains(table))
                                    .collect(Collectors.joining(", ")));
        });
    }

    @Test
    @DisplayName("Deve criar todas as constraints necessárias")
    void shouldCreateAllConstraints() {
        List<Map<String, Object>> constraints = jdbcTemplate.queryForList(
                """
                SELECT tc.table_schema, tc.table_name, tc.constraint_type, 
                       tc.constraint_name, kcu.column_name
                FROM information_schema.table_constraints tc
                JOIN information_schema.key_column_usage kcu 
                    ON tc.constraint_name = kcu.constraint_name
                    AND tc.table_schema = kcu.table_schema
                WHERE tc.table_schema = 'lead'
                """
        );

        // Verificar PKs
        assertTrue(constraints.stream()
                .anyMatch(c -> c.get("constraint_type").equals("PRIMARY KEY") &&
                        c.get("table_name").equals("lead")));

        // Verificar FKs
        assertTrue(constraints.stream()
                .anyMatch(c -> c.get("constraint_type").equals("FOREIGN KEY") &&
                        c.get("table_name").equals("lead") &&
                        c.get("column_name").equals("usuario_id")));

        // Verificar Unique constraints
        assertTrue(constraints.stream()
                .anyMatch(c -> c.get("constraint_type").equals("UNIQUE") &&
                        c.get("table_name").equals("ingresso_participante") &&
                        c.get("column_name").equals("qr_code")));
    }

    @Test
    @DisplayName("Deve criar todos os índices necessários")
    void shouldCreateAllIndexes() {
        List<Map<String, Object>> indexes = jdbcTemplate.queryForList(
                """
                SELECT schemaname, tablename, indexname, indexdef
                FROM pg_indexes
                WHERE schemaname = 'lead'
                """
        );

        // Verificar índices específicos
        assertTrue(indexes.stream()
                .anyMatch(idx -> idx.get("indexname").toString().contains("idx_lead_usuario")));
        assertTrue(indexes.stream()
                .anyMatch(idx -> idx.get("indexname").toString().contains("idx_ingresso_participante_evento")));
    }

    @Test
    @DisplayName("Deve criar as extensões necessárias")
    void shouldCreateExtensions() {
        List<String> extensions = jdbcTemplate.queryForList(
                "SELECT extname FROM pg_extension",
                String.class
        );

        assertTrue(extensions.contains("uuid-ossp"));
        assertTrue(extensions.contains("pgcrypto"));
    }

    @Test
    @DisplayName("Deve inserir dados iniciais nas tabelas de referência")
    void shouldInsertReferenceData() {
        int userTypesCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ref.usuario_tipos",
                Integer.class
        );
        assertTrue(userTypesCount >= 4, "Deve ter pelo menos 4 tipos de usuário");

        int leadStatusCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ref.lead_status",
                Integer.class
        );
        assertTrue(leadStatusCount >= 5, "Deve ter pelo menos 5 status de lead");
    }
}