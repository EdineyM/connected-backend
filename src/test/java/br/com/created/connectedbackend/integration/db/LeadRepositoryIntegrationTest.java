package br.com.created.connectedbackend.integration.db;

import br.com.created.connectedbackend.domain.model.event.Evento;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.model.lead.IngressoParticipante;
import br.com.created.connectedbackend.domain.model.user.User;
import br.com.created.connectedbackend.domain.repository.lead.LeadRepository;
import br.com.created.connectedbackend.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LeadRepositoryIntegrationTest {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User user;
    private Evento evento;
    private IngressoParticipante ingressoParticipante;

    @BeforeEach
    void setUp() {
        // Limpar dados existentes
        jdbcTemplate.execute("TRUNCATE TABLE lead.lead CASCADE");

        // Criar dados de teste
        evento = TestDataBuilder.createTestEvento();
        entityManager.persist(evento);

        user = TestDataBuilder.createTestUser();
        entityManager.persist(user);

        ingressoParticipante = TestDataBuilder.createTestIngressoParticipante(evento);
        entityManager.persist(ingressoParticipante);

        entityManager.flush();
    }

    @Test
    @DisplayName("Deve persistir e recuperar lead com relacionamentos")
    void persistAndRetrieveLeadWithRelationships() {
        Lead lead = TestDataBuilder.createTestLead(user, ingressoParticipante);
        lead = leadRepository.save(lead);
        entityManager.flush();
        entityManager.clear();

        Lead retrieved = leadRepository.findById(lead.getId()).orElseThrow();

        assertNotNull(retrieved);
        assertEquals(lead.getId(), retrieved.getId());
        assertEquals(user.getId(), retrieved.getUser().getId());
        assertEquals(ingressoParticipante.getId(), retrieved.getIngressoParticipante().getId());
    }

    @Test
    @DisplayName("Deve implementar soft delete corretamente")
    void softDeleteImplementation() {
        Lead lead = TestDataBuilder.createTestLead(user, ingressoParticipante);
        lead = leadRepository.save(lead);

        // Soft delete
        lead.setDeletedAt(LocalDateTime.now());
        leadRepository.save(lead);
        entityManager.flush();
        entityManager.clear();

        // Verificar que não aparece em consultas normais
        var leads = leadRepository.findAllByUserIdAndNotDeleted(
                user.getId(),
                PageRequest.of(0, 10)
        );
        assertTrue(leads.isEmpty());

        // Verificar que ainda existe no banco
        assertTrue(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM lead.lead WHERE id = ?",
                Integer.class,
                lead.getId()
        ) > 0);
    }

    @Test
    @DisplayName("Deve manter integridade referencial")
    void referentialIntegrity() {
        Lead lead = TestDataBuilder.createTestLead(user, ingressoParticipante);
        lead = leadRepository.save(lead);
        entityManager.flush();

        // Tentar deletar user (deve falhar)
        assertThrows(Exception.class, () -> {
            jdbcTemplate.execute("DELETE FROM core.usuario WHERE id = '" + user.getId() + "'");
        });

        // Tentar deletar ingresso (deve falhar)
        assertThrows(Exception.class, () -> {
            jdbcTemplate.execute("DELETE FROM lead.ingresso_participante WHERE id = '" +
                    ingressoParticipante.getId() + "'");
        });
    }

    @Test
    @DisplayName("Deve usar índices nas consultas")
    void useIndexesInQueries() {
        // Criar dados de teste
        createTestLeads(100);

        // Executar explain analyze
        String explainResult = jdbcTemplate.queryForObject("""
            EXPLAIN ANALYZE
            SELECT l.* FROM lead.lead l
            WHERE l.usuario_id = ?
            AND l.deleted_at IS NULL
            """,
                String.class,
                user.getId()
        );

        // Verificar uso de índice
        assertTrue(explainResult.contains("Index Scan") || explainResult.contains("Bitmap Index Scan"));
    }

    @Test
    @DisplayName("Deve gerenciar transações corretamente")
    void transactionManagement() {
        // Iniciar transação que irá falhar
        try {
            entityManager.getTransaction().begin();

            // Salvar lead válido
            Lead lead1 = TestDataBuilder.createTestLead(user, ingressoParticipante);
            leadRepository.save(lead1);

            // Tentar salvar lead inválido
            Lead lead2 = new Lead(); // Lead sem dados obrigatórios
            leadRepository.save(lead2);

            entityManager.getTransaction().commit();
            fail("Deveria ter lançado exceção");
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }

        // Verificar que nenhum lead foi salvo
        assertEquals(0, leadRepository.count());
    }

    private void createTestLeads(int count) {
        for (int i = 0; i < count; i++) {
            Lead lead = TestDataBuilder.createTestLead(user, ingressoParticipante);
            leadRepository.save(lead);
        }
        entityManager.flush();
    }
}