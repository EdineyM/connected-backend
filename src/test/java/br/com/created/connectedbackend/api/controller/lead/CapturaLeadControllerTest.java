package br.com.created.connectedbackend.api.controller.lead;

import br.com.created.connectedbackend.api.dto.request.lead.CreateCapturaLeadRequest;
import br.com.created.connectedbackend.domain.model.lead.CapturaLead;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.service.lead.CapturaLeadService;
import br.com.created.connectedbackend.util.TestDataBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CapturaLeadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CapturaLeadService capturaLeadService;

    private CreateCapturaLeadRequest createRequest;
    private CapturaLead capturaLead;
    private Lead lead;

    @BeforeEach
    void setUp() {
        lead = TestDataBuilder.createTestLead(
                TestDataBuilder.createTestUser(),
                TestDataBuilder.createTestIngressoParticipante(TestDataBuilder.createTestEvento())
        );
        capturaLead = TestDataBuilder.createTestCapturaLead(lead);

        createRequest = CreateCapturaLeadRequest.builder()
                .leadId(lead.getId())
                .tipoInteracao("REUNIAO")
                .descricao("Captura de teste")
                .build();
    }

    @Test
    @DisplayName("Deve criar captura com sucesso")
    @WithMockUser(roles = "ATENDENTE")
    void createCapturaSuccess() throws Exception {
        when(capturaLeadService.createCaptura(any())).thenReturn(capturaLead);

        mockMvc.perform(post("/api/v1/capturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.tipoInteracao").exists());

        verify(capturaLeadService).createCaptura(any());
    }

    @Test
    @DisplayName("Deve listar capturas por lead")
    @WithMockUser(roles = "ATENDENTE")
    void getCapturasByLeadSuccess() throws Exception {
        when(capturaLeadService.findCapturasByLead(any(), any()))
                .thenReturn(new PageImpl<>(List.of(capturaLead)));

        mockMvc.perform(get("/api/v1/capturas/lead/{leadId}", UUID.randomUUID())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").exists());

        verify(capturaLeadService).findCapturasByLead(any(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve listar capturas por tipo de interação")
    @WithMockUser(roles = "ATENDENTE")
    void getCapturasByTipoInteracaoSuccess() throws Exception {
        when(capturaLeadService.findCapturasByTipoInteracao(any(), any()))
                .thenReturn(List.of(capturaLead));

        mockMvc.perform(get("/api/v1/capturas/lead/{leadId}/tipo/{tipo}",
                        UUID.randomUUID(), "REUNIAO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoInteracao").value("ABORDAGEM"));

        verify(capturaLeadService).findCapturasByTipoInteracao(any(), any());
    }

    @Test
    @DisplayName("Deve listar tipos de interação")
    @WithMockUser(roles = "ATENDENTE")
    void getTiposInteracaoSuccess() throws Exception {
        when(capturaLeadService.findTiposInteracaoByLead(any()))
                .thenReturn(List.of("REUNIAO", "EMAIL"));

        mockMvc.perform(get("/api/v1/capturas/tipos/{leadId}", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());

        verify(capturaLeadService).findTiposInteracaoByLead(any());
    }

    @Test
    @DisplayName("Deve validar request inválido")
    @WithMockUser(roles = "ATENDENTE")
    void createCapturaValidationError() throws Exception {
        createRequest.setTipoInteracao(null);

        mockMvc.perform(post("/api/v1/capturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(capturaLeadService, never()).createCaptura(any());
    }
}