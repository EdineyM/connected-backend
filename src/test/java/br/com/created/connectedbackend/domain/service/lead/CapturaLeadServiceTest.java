package br.com.created.connectedbackend.domain.service.lead;

import br.com.created.connectedbackend.api.dto.request.lead.CreateCapturaLeadRequest;
import br.com.created.connectedbackend.domain.exception.LeadNotFoundException;
import br.com.created.connectedbackend.domain.model.lead.CapturaLead;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.repository.lead.CapturaLeadRepository;
import br.com.created.connectedbackend.domain.repository.lead.LeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CapturaLeadServiceTest {

    @Mock
    private CapturaLeadRepository capturaLeadRepository;

    @Mock
    private LeadRepository leadRepository;

    @InjectMocks
    private CapturaLeadService capturaLeadService;

    private Lead lead;
    private CapturaLead capturaLead;
    private CreateCapturaLeadRequest createRequest;
    private UUID leadId;

    @BeforeEach
    void setUp() {
        leadId = UUID.randomUUID();

        lead = Lead.builder()
                .id(leadId)
                .statusId((short) 1)
                .build();

        capturaLead = CapturaLead.builder()
                .id(UUID.randomUUID())
                .lead(lead)
                .tipoInteracao("ABORDAGEM")
                .descricao("Test capture")
                .build();

        createRequest = CreateCapturaLeadRequest.builder()
                .leadId(leadId)
                .tipoInteracao("ABORDAGEM")
                .descricao("New capture")
                .build();
    }

    @Test
    @DisplayName("Deve criar uma captura com sucesso")
    void createCapturaSuccess() {
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(lead));
        when(capturaLeadRepository.save(any(CapturaLead.class))).thenReturn(capturaLead);

        CapturaLead result = capturaLeadService.createCaptura(createRequest);

        assertNotNull(result);
        assertEquals(capturaLead.getId(), result.getId());
        assertEquals(createRequest.getTipoInteracao(), result.getTipoInteracao());
        verify(capturaLeadRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar captura para lead inexistente")
    void createCapturaLeadNotFoundError() {
        when(leadRepository.findById(leadId)).thenReturn(Optional.empty());

        assertThrows(LeadNotFoundException.class, () ->
                capturaLeadService.createCaptura(createRequest));

        verify(capturaLeadRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar capturas paginadas por lead")
    void findCapturasByLeadSuccess() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<CapturaLead> capturas = List.of(capturaLead);
        Page<CapturaLead> page = new PageImpl<>(capturas, pageRequest, capturas.size());

        when(capturaLeadRepository.findAllByLeadId(leadId, pageRequest)).thenReturn(page);

        Page<CapturaLead> result = capturaLeadService.findCapturasByLead(leadId, pageRequest);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Deve buscar capturas por tipo de interação")
    void findCapturasByTipoInteracaoSuccess() {
        String tipoInteracao = "ABORDAGEM";
        when(capturaLeadRepository.findAllByLeadIdAndTipoInteracao(leadId, tipoInteracao))
                .thenReturn(List.of(capturaLead));

        List<CapturaLead> result = capturaLeadService.findCapturasByTipoInteracao(leadId, tipoInteracao);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(tipoInteracao, result.get(0).getTipoInteracao());
    }
}