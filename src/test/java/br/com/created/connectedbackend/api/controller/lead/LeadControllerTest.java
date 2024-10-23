package br.com.created.connectedbackend.api.controller.lead;

import br.com.created.connectedbackend.api.dto.request.lead.CreateLeadRequest;
import br.com.created.connectedbackend.api.dto.request.lead.UpdateLeadRequest;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.model.user.User;
import br.com.created.connectedbackend.domain.service.lead.LeadService;
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
class LeadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LeadService leadService;

    private CreateLeadRequest createRequest;
    private UpdateLeadRequest updateRequest;
    private Lead lead;
    private User user;

    @BeforeEach
    void setUp() {
        user = TestDataBuilder.createTestUser();
        lead = TestDataBuilder.createTestLead(user, TestDataBuilder.createTestIngressoParticipante(TestDataBuilder.createTestEvento()));

        createRequest = CreateLeadRequest.builder()
                .ingressoParticipanteId(UUID.randomUUID())
                .statusId((short) 1)
                .descricao("Test lead")
                .tipoInteracaoInicial("ABORDAGEM")
                .build();

        updateRequest = UpdateLeadRequest.builder()
                .statusId((short) 2)
                .descricao("Updated lead")
                .build();
    }

    @Test
    @DisplayName("Deve criar lead com sucesso")
    @WithMockUser(roles = "ATENDENTE")
    void createLeadSuccess() throws Exception {
        when(leadService.createLead(any(), any())).thenReturn(lead);

        mockMvc.perform(post("/api/v1/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.descricao").value(lead.getDescricao()));

        verify(leadService).createLead(any(), any());
    }

    @Test
    @DisplayName("Deve retornar erro quando request inválido")
    @WithMockUser(roles = "ATENDENTE")
    void createLeadValidationError() throws Exception {
        createRequest.setTipoInteracaoInicial(null);

        mockMvc.perform(post("/api/v1/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(leadService, never()).createLead(any(), any());
    }

    @Test
    @DisplayName("Deve atualizar lead com sucesso")
    @WithMockUser(roles = "ATENDENTE")
    void updateLeadSuccess() throws Exception {
        when(leadService.updateLead(any(), any())).thenReturn(lead);

        mockMvc.perform(put("/api/v1/leads/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        verify(leadService).updateLead(any(), any());
    }

    @Test
    @DisplayName("Deve deletar lead com sucesso")
    @WithMockUser(roles = "REPRESENTANTE")
    void deleteLeadSuccess() throws Exception {
        doNothing().when(leadService).deleteLead(any());

        mockMvc.perform(delete("/api/v1/leads/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());

        verify(leadService).deleteLead(any());
    }

    @Test
    @DisplayName("Deve buscar lead por ID")
    @WithMockUser(roles = "ATENDENTE")
    void getLeadByIdSuccess() throws Exception {
        when(leadService.findLeadById(any())).thenReturn(lead);

        mockMvc.perform(get("/api/v1/leads/{id}", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        verify(leadService).findLeadById(any());
    }

    @Test
    @DisplayName("Deve listar leads do usuário")
    @WithMockUser(roles = "ATENDENTE")
    void getLeadsByCurrentUserSuccess() throws Exception {
        when(leadService.findLeadsByCurrentUser(any(), any()))
                .thenReturn(new PageImpl<>(List.of(lead)));

        mockMvc.perform(get("/api/v1/leads/user")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").exists());

        verify(leadService).findLeadsByCurrentUser(any(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve negar acesso a usuário não autorizado")
    @WithMockUser(roles = "ROLE_INVALID")
    void unauthorizedAccessError() throws Exception {
        mockMvc.perform(post("/api/v1/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());

        verify(leadService, never()).createLead(any(), any());
    }
}