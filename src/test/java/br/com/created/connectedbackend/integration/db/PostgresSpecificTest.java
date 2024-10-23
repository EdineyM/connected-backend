package br.com.created.connectedbackend.integration.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PostgresSpecificTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Deve suportar UUID como tipo de dados")
    void shouldSupportUuidDataType() {
        jdbcTemplate.execute(
                """
                CREATE TEMPORARY TABLE test_uuid (
                    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                    name TEXT
                )
                """
        );

        jdbcTemplate.update(
                "INSERT INTO test_uuid (name) VALUES (?)",
                "Test UUID"
        );

        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT * FROM test_uuid"
        );

        assertNotNull(result.get("id"));
        assertTrue(result.get("id").toString().matches(
                "^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"
        ));
    }

    @Test
    @DisplayName("Deve suportar JSONB para dados dinâmicos")
    void shouldSupportJsonbDataType() {
        jdbcTemplate.execute(
                """
                CREATE TEMPORARY TABLE test_jsonb (
                    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                    data JSONB
                )
                """
        );

        String jsonData = """
            {"key": "value", "nested": {"array": [1, 2, 3]}}
            """;

        jdbcTemplate.update(
                "INSERT INTO test_jsonb (data) VALUES (?::jsonb)",
                jsonData
        );

        String result = jdbcTemplate.queryForObject(
                "SELECT data->>'key' FROM test_jsonb",
                String.class
        );

        assertEquals("value", result);
    }

    @Test
    @DisplayName("Deve implementar full text search")
    void shouldImplementFullTextSearch() {
        jdbcTemplate.execute(
                """
                CREATE TEMPORARY TABLE test_fts (
                    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                    title TEXT,
                    content TEXT,
                    search_vector tsvector GENERATED ALWAYS AS (
                        setweight(to_tsvector('portuguese', coalesce(title, '')), 'A') ||
                        setweight(to_tsvector('portuguese', coalesce(content, '')), 'B')
                    ) STORED
                );
                CREATE INDEX test_fts_search_idx ON test_fts USING GIN (search_vector);
                """
        );

        jdbcTemplate.update(
                """
                INSERT INTO test_fts (title, content) VALUES
                (?, ?), (?, ?)
                """,
                "Reunião com cliente", "Discussão sobre novo projeto",
                "Projeto novo", "Reunião de planejamento com equipe"
        );

        List<Map<String, Object>> results = jdbcTemplate.queryForList(
                """
                SELECT title, content,
                       ts_rank(search_vector, websearch_to_tsquery('portuguese', ?)) as rank
                FROM test_fts
                WHERE search_vector @@ websearch_to_tsquery('portuguese', ?)
                ORDER BY rank DESC
                """,
                "reunião projeto",
                "reunião projeto"
        );

        assertFalse(results.isEmpty());
        assertTrue(results.size() >= 2);
    }

    @Test
    @DisplayName("Deve suportar triggers e funções")
    void shouldSupportTriggersAndFunctions() {
        // Criar função de auditoria
        jdbcTemplate.execute(
                """
                CREATE OR REPLACE FUNCTION test_audit_function()
                RETURNS TRIGGER AS $$
                BEGIN
                    NEW.updated_at = CURRENT_TIMESTAMP;
                    RETURN NEW;
                END;
                $$ LANGUAGE plpgsql;
                """
        );

        // Criar tabela de teste com trigger
        jdbcTemplate.execute(
                """
                CREATE TEMPORARY TABLE test_audit (
                    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                    data TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP
                );
                
                CREATE TRIGGER test_audit_trigger
                    BEFORE UPDATE ON test_audit
                    FOR EACH ROW
                    EXECUTE FUNCTION test_audit_function();
                """
        );

        // Testar
        jdbcTemplate.update(
                "INSERT INTO test_audit (data) VALUES (?)",
                "Initial data"
        );

        jdbcTemplate.update(
                "UPDATE test_audit SET data = ?",
                "Updated data"
        );

        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT * FROM test_audit"
        );

        assertNotNull(result.get("updated_at"));
    }

    @Test
    @DisplayName("Deve suportar funções window")
    void shouldSupportWindowFunctions() {
        jdbcTemplate.execute(
                """
                CREATE TEMPORARY TABLE test_window (
                    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                    grupo TEXT,
                    valor INTEGER
                )
                """
        );

        jdbcTemplate.batchUpdate(
                "INSERT INTO test_window (grupo, valor) VALUES (?, ?)",
                List.of(
                        new Object[]{"A", 10},
                        new Object[]{"A", 20},
                        new Object[]{"B", 30},
                        new Object[]{"B", 40}
                )
        );

        List<Map<String, Object>> results = jdbcTemplate.queryForList(
                """
                SELECT grupo,
                       valor,
                       SUM(valor) OVER (PARTITION BY grupo) as grupo_total,
                       AVG(valor) OVER () as media_geral
                FROM test_window
                """
        );

        assertEquals(4, results.size());
        assertEquals(30, results.stream()
                .filter(r -> r.get("grupo").equals("A"))
                .findFirst()
                .map(r -> r.get("grupo_total"))
                .map(Number.class::cast)
                .map(Number::intValue)
                .orElse(0));
    }
}