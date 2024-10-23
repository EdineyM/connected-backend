package br.com.created.connectedbackend.domain.service.lead;

import br.com.created.connectedbackend.api.dto.request.lead.CreateCapturaLeadRequest;
import br.com.created.connectedbackend.domain.exception.LeadNotFoundException;
import br.com.created.connectedbackend.domain.model.lead.CapturaLead;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.model.lead.IngressoParticipante;
import br.com.created.connectedbackend.domain.model.event.Evento;
import br.com.created.connectedbackend.domain.model.user.User;
import br.com.created.connectedbackend.domain.repository.lead.CapturaLeadRepository;
import br.com.created.connectedbackend.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CapturaLeadServiceIntegrationTest {

    @Autowired
    private CapturaLeadService capturaLeadService;

    @Autowired
    private CapturaLeadRepository capturaLeadRepository;

    @Autowired
    private EntityManager entityManager;

    private Lead lead;
    private CreateCapturaLeadRequest createRequest;

    @BeforeEach
    void setUp() {
        // Criar e persistir dados necessários
        Evento evento = TestDataBuilder.createTestEvento();
        entityManager.persist(evento);

        User user = TestDataBuilder.createTestUser();
        entityManager.persist(user);

        IngressoParticipante ingressoParticipante = TestDataBuilder.createTestIngressoParticipante(evento);
        entityManager.persist(ingressoParticipante);

        lead = TestDataBuilder.createTestLead(user, ingressoParticipante);
        entityManager.persist(lead);

        entityManager.flush();

        createRequest = CreateCapturaLeadRequest.builder()
                .leadId(lead.getId())
                .tipoInteracao("REUNIAO")
                .descricao("Descrição da reunião de teste")
                .build();
    }

    @Test
    @DisplayName("Deve criar captura com sucesso")
    void createCapturaSuccess() {
        CapturaLead created = capturaLeadService.createCaptura(createRequest);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(createRequest.getTipoInteracao(), created.getTipoInteracao());
        assertEquals(createRequest.getDescricao(), created.getDescricao());
        assertEquals(lead.getId(), created.getLead().getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar captura para lead inexistente")
    void createCapturaLeadNotFoundError() {
        createRequest.setLeadId(UUID.randomUUID());

        assertThrows(LeadNotFoundException.class, () ->
                capturaLeadService.createCaptura(createRequest)
        );
    }

    @Test
    @DisplayName("Deve buscar capturas paginadas por lead")
    void findCapturasByLeadSuccess() {
        // Criar uma captura primeiro
        capturaLeadService.createCaptura(createRequest);

        Page<CapturaLead> capturas = capturaLeadService.findCapturasByLead(
                lead.getId(),
                PageRequest.of(0, 10)
        );

        assertNotNull(capturas);
        assertFalse(capturas.isEmpty());
        assertEquals(1, capturas.getTotalElements());
    }

    @Test
    @DisplayName("Deve buscar capturas por tipo de interação")
    void findCapturasByTipoInteracaoSuccess() {
        // Criar uma captura primeiro
        capturaLeadService.createCaptura(createRequest);

        List<CapturaLead> capturas = capturaLeadService.findCapturasByTipoInteracao(
                lead.getId(),
                "REUNIAO"
        );

        assertFalse(capturas.isEmpty());
        assertEquals(1, capturas.size());
        assertEquals("REUNIAO", capturas.get(0).getTipoInteracao());
    }

    @Test
    @DisplayName("Deve retornar tipos de interação distintos")
    void findTiposInteracaoByLeadSuccess() {
        // Criar algumas capturas com tipos diferentes
        capturaLeadService.createCaptura(createRequest);

        createRequest.setTipoInteracao("EMAIL");
        capturaLeadService.createCaptura(createRequest);

        List<String> tipos = capturaLeadService.findTiposInteracaoByLead(lead.getId());

        assertNotNull(tipos);
        assertEquals(2, tipos.size());
        assertTrue(tipos.contains("REUNIAO"));
        assertTrue(tipos.contains("EMAIL"));
    }

    @Test
    @DisplayName("Deve buscar capturas por evento")
    void findCapturasByEventoSuccess() {
        // Criar uma captura primeiro
        capturaLeadService.createCaptura(createRequest);

        List<CapturaLead> capturas = capturaLeadService.findCapturasByEvento(
                lead.getIngressoParticipante().getEvento().getId()
        );

        assertFalse(capturas.isEmpty());
        assertEquals(1, capturas.size());
    }
}