package br.com.created.connectedbackend.security;

import br.com.created.connectedbackend.api.dto.request.auth.LoginRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        user = TestDataBuilder.createTestUser();
        loginRequest = new LoginRequest(user.getEmail(), "password123");
    }

    @Test
    @DisplayName("Deve autenticar usuário com credenciais válidas")
    void authenticateWithValidCredentials() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andReturn();

        String token = extractToken(result);
        assertNotNull(token);
        assertTrue(token.length() > 20);
    }

    @Test
    @DisplayName("Deve prevenir brute force")
    void preventBruteForce() throws Exception {
        // Tentar login várias vezes com senha errada
        LoginRequest invalidRequest = new LoginRequest(user.getEmail(), "wrong_password");

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isUnauthorized());
        }

        // Próxima tentativa deve ser bloqueada temporariamente
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("Deve validar formato do token JWT")
    void validateJwtFormat() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String token = extractToken(result);

        // Validar formato do JWT (header.payload.signature)
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);

        // Verificar se cada parte está em Base64
        for (String part : parts) {
            assertTrue(isBase64(part));
        }
    }

    private String extractToken(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        return JsonPath.read(content, "$.token");
    }

    private boolean isBase64(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}