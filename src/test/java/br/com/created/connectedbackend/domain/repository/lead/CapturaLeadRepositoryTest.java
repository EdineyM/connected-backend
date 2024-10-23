package br.com.created.connectedbackend.domain.repository.lead;

import br.com.created.connectedbackend.domain.model.lead.CapturaLead;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.model.lead.IngressoParticipante;
import br.com.created.connectedbackend.domain.model.event.Evento;
import br.com.created.connectedbackend.domain.model.user.User;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CapturaLeadRepositoryTest {

    @Autowired
    private CapturaLeadRepository capturaLeadRepository;

    @Autowired
    private EntityManager entityManager;

    private Lead lead;
    private CapturaLead capturaLead;
    private Evento evento;

    @BeforeEach
    void setUp() {
        // Criar e persistir dados necessários
        evento = TestDataBuilder.createTestEvento();
        entityManager.persist(evento);

        User user = TestDataBuilder.createTestUser();
        entityManager.persist(user);

        IngressoParticipante ingressoParticipante = TestDataBuilder.createTestIngressoParticipante(evento);
        entityManager.persist(ingressoParticipante);

        lead = TestDataBuilder.createTestLead(user, ingressoParticipante);
        entityManager.persist(lead);

        capturaLead = TestDataBuilder.createTestCapturaLead(lead);
        entityManager.persist(capturaLead);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Deve buscar capturas por lead paginadas")
    void findAllByLeadId() {
        Page<CapturaLead> capturas = capturaLeadRepository.findAllByLeadId(
                lead.getId(),
                PageRequest.of(0, 10)
        );

        assertNotNull(capturas);
        assertFalse(capturas.isEmpty());
        assertEquals(1, capturas.getTotalElements());
        assertEquals(capturaLead.getId(), capturas.getContent().get(0).getId());
    }

    @Test
    @DisplayName("Deve buscar capturas por tipo de interação")
    void findAllByLeadIdAndTipoInteracao() {
        List<CapturaLead> capturas = capturaLeadRepository
                .findAllByLeadIdAndTipoInteracao(lead.getId(), "ABORDAGEM");

        assertFalse(capturas.isEmpty());
        assertEquals(1, capturas.size());
        assertEquals("ABORDAGEM", capturas.get(0).getTipoInteracao());
    }

    @Test
    @DisplayName("Deve contar capturas por período")
    void countCapturasByLeadIdAndPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now().plusDays(1);

        Long count = capturaLeadRepository.countCapturasByLeadIdAndPeriodo(
                lead.getId(),
                inicio,
                fim
        );

        assertEquals(1L, count);
    }

    @Test
    @DisplayName("Deve buscar capturas por evento")
    void findAllByEventoId() {
        List<CapturaLead> capturas = capturaLeadRepository.findAllByEventoId(evento.getId());

        assertFalse(capturas.isEmpty());
        assertEquals(1, capturas.size());
        assertEquals(capturaLead.getId(), capturas.get(0).getId());
    }

    @Test
    @DisplayName("Deve buscar tipos de interação distintos")
    void findDistinctTipoInteracaoByLeadId() {
        List<String> tipos = capturaLeadRepository.findDistinctTipoInteracaoByLeadId(lead.getId());

        assertFalse(tipos.isEmpty());
        assertTrue(tipos.contains("ABORDAGEM"));
    }
}