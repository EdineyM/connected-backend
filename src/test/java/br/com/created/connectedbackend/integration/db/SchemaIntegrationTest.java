package br.com.created.connectedbackend.integration.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SchemaIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Deve verificar se todos os schemas foram criados")
    void checkSchemasExist() {
        String sql = """
            SELECT schema_name 
            FROM information_schema.schemata 
            WHERE schema_name IN ('core', 'address', 'event', 'lead', 'audit', 'cache', 'ref')
            ORDER BY schema_name
            """;

        List<String> schemas = jdbcTemplate.queryForList(sql, String.class);

        assertEquals(7, schemas.size());
        assertTrue(schemas.contains("core"));
        assertTrue(schemas.contains("address"));
        assertTrue(schemas.contains("event"));
        assertTrue(schemas.contains("lead"));
        assertTrue(schemas.contains("audit"));
        assertTrue(schemas.contains("cache"));
        assertTrue(schemas.contains("ref"));
    }

    @Test
    @DisplayName("Deve verificar se as tabelas do schema lead foram criadas")
    void checkLeadTablesExist() {
        String sql = """
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'lead' 
            AND table_type = 'BASE TABLE'
            ORDER BY table_name
            """;

        List<String> tables = jdbcTemplate.queryForList(sql, String.class);

        assertTrue(tables.contains("audio_descricao"));
        assertTrue(tables.contains("captura_lead"));
        assertTrue(tables.contains("lead"));
        assertTrue(tables.contains("ingresso_participante"));
    }

    @Test
    @DisplayName("Deve verificar as colunas da tabela lead")
    void checkLeadTableColumns() {
        String sql = """
            SELECT column_name, data_type, is_nullable
            FROM information_schema.columns
            WHERE table_schema = 'lead' AND table_name = 'lead'
            ORDER BY ordinal_position
            """;

        List<String> columns = jdbcTemplate.queryForList(sql, String.class);

        assertTrue(columns.contains("id"));
        assertTrue(columns.contains("usuario_id"));
        assertTrue(columns.contains("ingresso_id"));
        assertTrue(columns.contains("status_id"));
        assertTrue(columns.contains("descricao"));
        assertTrue(columns.contains("localizacao"));
        assertTrue(columns.contains("created_at"));
        assertTrue(columns.contains("updated_at"));
        assertTrue(columns.contains("deleted_at"));
    }
}