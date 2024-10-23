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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DetailedSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateLeadRequest createRequest;
    private User user;

    @BeforeEach
    void setUp() {
        user = TestDataBuilder.createTestUser();
        createRequest = CreateLeadRequest.builder()
                .ingressoParticipanteId(UUID.randomUUID())
                .statusId((short) 1)
                .descricao("Test lead")
                .tipoInteracaoInicial("ABORDAGEM")
                .build();
    }

    @Test
    @DisplayName("Deve validar cabeçalhos de segurança")
    @WithMockUser(roles = "ADMIN")
    void validateSecurityHeaders() throws Exception {
        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("X-XSS-Protection", "1; mode=block"))
                .andExpect(header().string("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate"))
                .andExpect(header().string("Pragma", "no-cache"))
                .andExpect(header().string("Expires", "0"))
                .andExpect(header().string("Strict-Transport-Security", "max-age=31536000 ; includeSubDomains"));
    }

    @Test
    @DisplayName("Deve prevenir XSS")
    @WithMockUser(roles = "ADMIN")
    void preventXssAttack() throws Exception {
        createRequest.setDescricao("<script>alert('xss')</script>");

        MvcResult result = mockMvc.perform(post("/api/v1/leads")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertFalse(response.contains("<script>"));
        assertTrue(response.contains("&lt;script&gt;"));
    }

    @Test
    @DisplayName("Deve validar CORS")
    @WithMockUser(roles = "ADMIN")
    void validateCorsConfiguration() throws Exception {
        mockMvc.perform(options("/api/v1/leads")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"))
                .andExpect(header().exists("Access-Control-Max-Age"));
    }

    @Test
    @DisplayName("Deve validar Rate Limiting")
    @WithMockUser(roles = "ADMIN")
    void validateRateLimiting() throws Exception {
        // Executar requisições até atingir o limite
        for (int i = 0; i < 100; i++) {
            mockMvc.perform(get("/api/v1/leads"))
                    .andExpect(status().isOk());
        }

        // Próxima requisição deve ser bloqueada
        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("X-RateLimit-Remaining"))
                .andExpect(header().string("X-RateLimit-Remaining", "0"));
    }

    @Test
    @DisplayName("Deve validar proteção contra SQL Injection")
    @WithMockUser(roles = "ADMIN")
    void preventSqlInjection() throws Exception {
        String maliciousId = "1' OR '1'='1";

        mockMvc.perform(get("/api/v1/leads/{id}", maliciousId))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/leads")
                        .param("search", "1' OR '1'='1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @DisplayName("Deve validar proteção contra CSRF")
    @WithMockUser(roles = "ADMIN")
    void validateCsrfProtection() throws Exception {
        // Requisição sem token CSRF deve falhar
        mockMvc.perform(post("/api/v1/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());

        // Requisição com token CSRF deve funcionar
        mockMvc.perform(post("/api/v1/leads")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve validar autorização granular")
    @WithMockUser(roles = "ATENDENTE")
    void validateGranularAuthorization() throws Exception {
        // Atendente pode criar lead
        mockMvc.perform(post("/api/v1/leads")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        // Atendente não pode deletar lead
        mockMvc.perform(delete("/api/v1/leads/{id}", UUID.randomUUID())
                        .with(csrf()))
                .andExpect(status().isForbidden());

        // Atendente pode visualizar leads
        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve prevenir ataques de Path Traversal")
    @WithMockUser(roles = "ADMIN")
    void preventPathTraversal() throws Exception {
        mockMvc.perform(get("/api/v1/leads/../../../etc/passwd"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve validar Content Security Policy")
    @WithMockUser(roles = "ADMIN")
    void validateContentSecurityPolicy() throws Exception {
        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(header().string("Content-Security-Policy",
                        containsString("default-src 'self'")))
                .andExpect(header().string("Content-Security-Policy",
                        containsString("frame-ancestors 'none'")));
    }

    @Test
    @DisplayName("Deve validar autenticação com diferentes métodos HTTP")
    void validateAuthenticationWithDifferentMethods() throws Exception {
        // GET sem autenticação
        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isUnauthorized());

        // POST sem autenticação
        mockMvc.perform(post("/api/v1/leads")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());

        // PUT sem autenticação
        mockMvc.perform(put("/api/v1/leads/{id}", UUID.randomUUID())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());

        // DELETE sem autenticação
        mockMvc.perform(delete("/api/v1/leads/{id}", UUID.randomUUID())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve validar tentativas de escalação de privilégios")
    @WithMockUser(roles = "ATENDENTE")
    void validatePrivilegeEscalation() throws Exception {
        // Tentar acessar endpoint administrativo
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isForbidden());

        // Tentar modificar roles através da API
        mockMvc.perform(put("/api/v1/users/{id}/roles", UUID.randomUUID())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"ROLE_ADMIN\"]"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve validar proteção contra Mass Assignment")
    @WithMockUser(roles = "ADMIN")
    void preventMassAssignment() throws Exception {
        String requestWithRestrictedFields = """
        {
            "id": "550e8400-e29b-41d4-a716-446655440000",
            "ingressoParticipanteId": "550e8400-e29b-41d4-a716-446655440001",
            "statusId": 1,
            "descricao": "Test lead",
            "createdAt": "2024-01-01T00:00:00Z",
            "roles": ["ROLE_ADMIN"]
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/v1/leads")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestWithRestrictedFields))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertFalse(response.contains("550e8400-e29b-41d4-a716-446655440000"));
        assertFalse(response.contains("2024-01-01T00:00:00Z"));
    }

    @Test
    @DisplayName("Deve validar proteção contra Method Tampering")
    @WithMockUser(roles = "ADMIN")
    void preventMethodTampering() throws Exception {
        mockMvc.perform(post("/api/v1/leads/{id}", UUID.randomUUID())
                        .with(csrf())
                        .header("X-HTTP-Method-Override", "DELETE"))
                .andExpect(status().isMethodNotAllowed());
    }
}