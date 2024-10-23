package br.com.created.connectedbackend.domain.service.lead;

import br.com.created.connectedbackend.api.dto.request.lead.CreateLeadRequest;
import br.com.created.connectedbackend.api.dto.request.lead.UpdateLeadRequest;
import br.com.created.connectedbackend.domain.exception.DuplicatedLeadException;
import br.com.created.connectedbackend.domain.exception.LeadNotFoundException;
import br.com.created.connectedbackend.domain.model.lead.IngressoParticipante;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.model.user.User;
import br.com.created.connectedbackend.domain.repository.lead.CapturaLeadRepository;
import br.com.created.connectedbackend.domain.repository.lead.IngressoParticipanteRepository;
import br.com.created.connectedbackend.domain.repository.lead.LeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private CapturaLeadRepository capturaLeadRepository;

    @Mock
    private IngressoParticipanteRepository ingressoParticipanteRepository;

    @InjectMocks
    private LeadService leadService;

    private User user;
    private IngressoParticipante ingressoParticipante;
    private Lead lead;
    private CreateLeadRequest createRequest;
    private UpdateLeadRequest updateRequest;
    private UUID leadId;
    private UUID ingressoId;

    @BeforeEach
    void setUp() {
        leadId = UUID.randomUUID();
        ingressoId = UUID.randomUUID();

        user = User.builder()
                .id(UUID.randomUUID())
                .nome("Test User")
                .email("test@example.com")
                .build();

        ingressoParticipante = IngressoParticipante.builder()
                .id(ingressoId)
                .nome("John")
                .sobrenome("Doe")
                .email("john@example.com")
                .build();

        lead = Lead.builder()
                .id(leadId)
                .user(user)
                .ingressoParticipante(ingressoParticipante)
                .statusId((short) 1)
                .descricao("Test lead")
                .build();

        createRequest = CreateLeadRequest.builder()
                .ingressoParticipanteId(ingressoId)
                .statusId((short) 1)
                .descricao("New lead")
                .tipoInteracaoInicial("ABORDAGEM")
                .build();

        updateRequest = UpdateLeadRequest.builder()
                .statusId((short) 2)
                .descricao("Updated description")
                .build();
    }

    @Test
    @DisplayName("Deve criar um lead com sucesso")
    void createLeadSuccess() {
        when(ingressoParticipanteRepository.findById(ingressoId))
                .thenReturn(Optional.of(ingressoParticipante));
        when(leadRepository.existsByIngressoParticipanteIdAndDeletedAtIsNull(ingressoId))
                .thenReturn(false);
        when(leadRepository.save(any(Lead.class))).thenReturn(lead);

        Lead result = leadService.createLead(createRequest, user);

        assertNotNull(result);
        assertEquals(lead.getId(), result.getId());
        verify(capturaLeadRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar lead duplicado")
    void createLeadDuplicateError() {
        when(ingressoParticipanteRepository.findById(ingressoId))
                .thenReturn(Optional.of(ingressoParticipante));
        when(leadRepository.existsByIngressoParticipanteIdAndDeletedAtIsNull(ingressoId))
                .thenReturn(true);

        assertThrows(DuplicatedLeadException.class, () ->
                leadService.createLead(createRequest, user));

        verify(leadRepository, never()).save(any());
        verify(capturaLeadRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar um lead com sucesso")
    void updateLeadSuccess() {
        when(leadRepository.findByIdWithRelationships(leadId))
                .thenReturn(Optional.of(lead));
        when(leadRepository.save(any(Lead.class))).thenReturn(lead);

        Lead result = leadService.updateLead(leadId, updateRequest);

        assertNotNull(result);
        assertEquals(updateRequest.getStatusId(), result.getStatusId());
        assertEquals(updateRequest.getDescricao(), result.getDescricao());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar lead inexistente")
    void updateLeadNotFoundError() {
        when(leadRepository.findByIdWithRelationships(leadId))
                .thenReturn(Optional.empty());

        assertThrows(LeadNotFoundException.class, () ->
                leadService.updateLead(leadId, updateRequest));

        verify(leadRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar um lead com sucesso")
    void deleteLeadSuccess() {
        when(leadRepository.findByIdWithRelationships(leadId))
                .thenReturn(Optional.of(lead));
        when(leadRepository.save(any(Lead.class))).thenReturn(lead);

        assertDoesNotThrow(() -> leadService.deleteLead(leadId));
        verify(leadRepository).save(any());
        assertNotNull(lead.getDeletedAt());
    }
}