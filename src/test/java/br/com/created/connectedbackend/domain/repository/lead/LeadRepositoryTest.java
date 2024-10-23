package br.com.created.connectedbackend.domain.repository.lead;

import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.model.lead.IngressoParticipante;
import br.com.created.connectedbackend.domain.model.user.User;
import br.com.created.connectedbackend.domain.model.event.Evento;
import br.com.created.connectedbackend.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LeadRepositoryTest {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private EntityManager entityManager;

    private User user;
    private IngressoParticipante ingressoParticipante;
    private Evento evento;
    private Lead lead;

    @BeforeEach
    void setUp() {
        // Criar e persistir dados necessários
        evento = TestDataBuilder.createTestEvento();
        entityManager.persist(evento);

        user = TestDataBuilder.createTestUser();
        entityManager.persist(user);

        ingressoParticipante = TestDataBuilder.createTestIngressoParticipante(evento);
        entityManager.persist(ingressoParticipante);

        lead = TestDataBuilder.createTestLead(user, ingressoParticipante);
        entityManager.persist(lead);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Deve encontrar leads por usuário sem incluir deletados")
    void findAllByUserIdAndNotDeleted() {
        Page<Lead> leads = leadRepository.findAllByUserIdAndNotDeleted(
                user.getId(),
                PageRequest.of(0, 10)
        );

        assertNotNull(leads);
        assertFalse(leads.isEmpty());
        assertEquals(1, leads.getTotalElements());
        assertEquals(lead.getId(), leads.getContent().get(0).getId());
    }

    @Test
    @DisplayName("Deve encontrar lead por ID com relacionamentos")
    void findByIdWithRelationships() {
        var foundLead = leadRepository.findByIdWithRelationships(lead.getId());

        assertTrue(foundLead.isPresent());
        assertNotNull(foundLead.get().getUser());
        assertNotNull(foundLead.get().getIngressoParticipante());
    }

    @Test
    @DisplayName("Deve encontrar leads por evento")
    void findAllByEventoId() {
        List<Lead> leads = leadRepository.findAllByEventoId(evento.getId());

        assertFalse(leads.isEmpty());
        assertEquals(1, leads.size());
        assertEquals(lead.getId(), leads.get(0).getId());
    }

    @Test
    @DisplayName("Deve contar leads por evento")
    void countLeadsByEventoId() {
        Long count = leadRepository.countLeadsByEventoId(evento.getId());

        assertEquals(1L, count);
    }

    @Test
    @DisplayName("Deve encontrar leads por status")
    void findAllByStatusId() {
        Page<Lead> leads = leadRepository.findAllByStatusId(
                lead.getStatusId(),
                PageRequest.of(0, 10)
        );

        assertFalse(leads.isEmpty());
        assertEquals(1, leads.getTotalElements());
        assertEquals(lead.getStatusId(), leads.getContent().get(0).getStatusId());
    }

    @Test
    @DisplayName("Deve verificar existência de lead por ingresso")
    void existsByIngressoParticipanteIdAndDeletedAtIsNull() {
        boolean exists = leadRepository.existsByIngressoParticipanteIdAndDeletedAtIsNull(
                ingressoParticipante.getId()
        );

        assertTrue(exists);
    }

    @Test
    @DisplayName("Deve encontrar status distintos por usuário")
    void findDistinctStatusByUserId() {
        List<Short> status = leadRepository.findDistinctStatusByUserId(user.getId());

        assertFalse(status.isEmpty());
        assertEquals(1, status.size());
        assertEquals(lead.getStatusId(), status.get(0));
    }
}