package br.com.created.connectedbackend.domain.service.lead;

import br.com.created.connectedbackend.api.dto.request.lead.CreateLeadRequest;
import br.com.created.connectedbackend.api.dto.request.lead.UpdateLeadRequest;
import br.com.created.connectedbackend.domain.exception.DuplicatedLeadException;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.model.lead.IngressoParticipante;
import br.com.created.connectedbackend.domain.model.event.Evento;
import br.com.created.connectedbackend.domain.model.user.User;
import br.com.created.connectedbackend.domain.repository.lead.LeadRepository;
import br.com.created.connectedbackend.domain.repository.lead.IngressoParticipanteRepository;
import br.com.created.connectedbackend.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LeadServiceIntegrationTest {

    @Autowired
    private LeadService leadService;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private IngressoParticipanteRepository ingressoParticipanteRepository;

    @Autowired
    private EntityManager entityManager;

    private User user;
    private IngressoParticipante ingressoParticipante;
    private Evento evento;
    private CreateLeadRequest createRequest;
    private UpdateLeadRequest updateRequest;

    @BeforeEach
    void setUp() {
        // Criar e persistir dados necessários
        evento = TestDataBuilder.createTestEvento();
        entityManager.persist(evento);

        user = TestDataBuilder.createTestUser();
        entityManager.persist(user);

        ingressoParticipante = TestDataBuilder.createTestIngressoParticipante(evento);
        entityManager.persist(ingressoParticipante);

        entityManager.flush();

        createRequest = CreateLeadRequest.builder()
                .ingressoParticipanteId(ingressoParticipante.getId())
                .statusId((short) 1)
                .descricao("Test lead description")
                .tipoInteracaoInicial("ABORDAGEM")
                .build();

        updateRequest = UpdateLeadRequest.builder()
                .statusId((short) 2)
                .descricao("Updated description")
                .build();
    }

    @Test
    @DisplayName("Deve criar lead com sucesso")
    void createLeadSuccess() {
        Lead created = leadService.createLead(createRequest, user);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(createRequest.getDescricao(), created.getDescricao());
        assertEquals(ingressoParticipante.getId(), created.getIngressoParticipante().getId());
        assertEquals(user.getId(), created.getUser().getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar lead duplicado")
    void createLeadDuplicateError() {
        leadService.createLead(createRequest, user);

        assertThrows(DuplicatedLeadException.class, () ->
                leadService.createLead(createRequest, user)
        );
    }

    @Test
    @DisplayName("Deve atualizar lead com sucesso")
    void updateLeadSuccess() {
        Lead lead = leadService.createLead(createRequest, user);

        Lead updated = leadService.updateLead(lead.getId(), updateRequest);

        assertNotNull(updated);
        assertEquals(updateRequest.getStatusId(), updated.getStatusId());
        assertEquals(updateRequest.getDescricao(), updated.getDescricao());
    }

    @Test
    @DisplayName("Deve deletar lead com sucesso")
    void deleteLeadSuccess() {
        Lead lead = leadService.createLead(createRequest, user);

        leadService.deleteLead(lead.getId());

        Lead deleted = leadRepository.findById(lead.getId()).orElse(null);
        assertNotNull(deleted);
        assertNotNull(deleted.getDeletedAt());
    }

    @Test
    @DisplayName("Deve buscar leads por usuário")
    void findLeadsByCurrentUser() {
        leadService.createLead(createRequest, user);

        var leads = leadService.findLeadsByCurrentUser(user.getId(), PageRequest.of(0, 10));

        assertFalse(leads.isEmpty());
        assertEquals(1, leads.getTotalElements());
    }
}