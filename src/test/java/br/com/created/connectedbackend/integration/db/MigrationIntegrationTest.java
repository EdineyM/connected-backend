package br.com.created.connectedbackend.integration.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MigrationIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Deve verificar as constraints da tabela lead")
    void checkLeadConstraints() {
        String sql = """
            SELECT count(*) 
            FROM information_schema.table_constraints 
            WHERE table_schema = 'lead' 
            AND table_name = 'lead' 
            AND constraint_type = 'FOREIGN KEY'
            """;

        int constraintCount = jdbcTemplate.queryForObject(sql, Integer.class);
        assertEquals(3, constraintCount); // usuario_id, ingresso_id, status_id
    }

    @Test
    @DisplayName("Deve verificar os índices da tabela lead")
    void checkLeadIndices() {
        String sql = """
            SELECT count(*) 
            FROM pg_indexes 
            WHERE schemaname = 'lead' 
            AND tablename = 'lead'
            """;

        int indexCount = jdbcTemplate.queryForObject(sql, Integer.class);
        assertTrue(indexCount >= 4); // PK + 3 FK indices
    }

    @Test
    @DisplayName("Deve verificar as sequences do schema lead")
    void checkLeadSequences() {
        String sql = """
            SELECT count(*) 
            FROM information_schema.sequences 
            WHERE sequence_schema = 'lead'
            """;

        int sequenceCount = jdbcTemplate.queryForObject(sql, Integer.class);
        assertEquals(0, sequenceCount); // Usando UUID, não deve ter sequences
    }

    @Test
    @DisplayName("Deve verificar as funções do schema lead")
    void checkLeadFunctions() {
        String sql = """
            SELECT count(*) 
            FROM information_schema.routines 
            WHERE routine_schema = 'lead' 
            AND routine_type = 'FUNCTION'
            """;

        int functionCount = jdbcTemplate.queryForObject(sql, Integer.class);
        assertEquals(0, functionCount); // Não deve ter funções no schema lead
    }

    @Test
    @DisplayName("Deve verificar os triggers da tabela lead")
    void checkLeadTriggers() {
        String sql = """
            SELECT count(*) 
            FROM information_schema.triggers 
            WHERE event_object_schema = 'lead' 
            AND event_object_table = 'lead'
            """;

        int triggerCount = jdbcTemplate.queryForObject(sql, Integer.class);
        assertEquals(0, triggerCount); // Não deve ter triggers na tabela lead
    }
}