package br.com.created.connectedbackend.security;

import br.com.created.connectedbackend.api.dto.request.lead.CreateLeadRequest;
import br.com.created.connectedbackend.domain.model.user.User;
import br.com.created.connectedbackend.util.TestDataBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LeadSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateLeadRequest createLeadRequest;
    private User user;

    @BeforeEach
    void setUp() {
        user = TestDataBuilder.createTestUser();
        createLeadRequest = CreateLeadRequest.builder()
                .ingressoParticipanteId(UUID.randomUUID())
                .statusId((short) 1)
                .descricao("Test lead")
                .tipoInteracaoInicial("ABORDAGEM")
                .build();
    }

    @Test
    @DisplayName("Deve negar acesso a usuário anônimo")
    @WithAnonymousUser
    void denyAnonymousAccess() throws Exception {
        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve permitir acesso a admin para todas operações")
    @WithMockUser(roles = "ADMIN")
    void allowAdminAccess() throws Exception {
        mockMvc.perform(post("/api/v1/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLeadRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/leads/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve permitir acesso limitado a atendente")
    @WithMockUser(roles = "ATENDENTE")
    void allowLimitedAtendenteAccess() throws Exception {
        // Pode criar e ler leads
        mockMvc.perform(post("/api/v1/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLeadRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isOk());

        // Não pode deletar leads
        mockMvc.perform(delete("/api/v1/leads/" + UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve validar CORS")
    @WithMockUser(roles = "ADMIN")
    void validateCors() throws Exception {
        mockMvc.perform(options("/api/v1/leads")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().exists("Access-Control-Allow-Methods"));
    }

    @Test
    @DisplayName("Deve validar CSRF")
    @WithMockUser(roles = "ADMIN")
    void validateCsrf() throws Exception {
        mockMvc.perform(post("/api/v1/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLeadRequest))
                        .with(csrf()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLeadRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve validar rate limiting")
    @WithMockUser(roles = "ADMIN")
    void validateRateLimiting() throws Exception {
        // Fazer várias requisições em sequência
        for (int i = 0; i < 50; i++) {
            mockMvc.perform(get("/api/v1/leads"))
                    .andExpect(status().isOk());
        }

        // A próxima requisição deve ser bloqueada
        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("Deve validar headers de segurança")
    @WithMockUser(roles = "ADMIN")
    void validateSecurityHeaders() throws Exception {
        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(header().exists("X-Content-Type-Options"))
                .andExpect(header().exists("X-Frame-Options"))
                .andExpect(header().exists("X-XSS-Protection"))
                .andExpect(header().exists("Strict-Transport-Security"));
    }
}