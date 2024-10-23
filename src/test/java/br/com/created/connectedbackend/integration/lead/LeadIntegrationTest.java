package br.com.created.connectedbackend.integration.lead;

import br.com.created.connectedbackend.api.dto.request.lead.CreateLeadRequest;
import br.com.created.connectedbackend.api.dto.response.lead.LeadResponse;
import br.com.created.connectedbackend.domain.model.lead.IngressoParticipante;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.model.user.User;
import br.com.created.connectedbackend.domain.repository.lead.IngressoParticipanteRepository;
import br.com.created.connectedbackend.domain.repository.lead.LeadRepository;
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
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LeadIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private IngressoParticipanteRepository ingressoParticipanteRepository;

    private IngressoParticipante ingressoParticipante;
    private User user;
    private CreateLeadRequest createLeadRequest;

    @BeforeEach
    void setUp() {
        // Criar dados de teste
        user = User.builder()
                .nome("Test User")
                .email("test@example.com")
                .build();

        ingressoParticipante = IngressoParticipante.builder()
                .nome("John")
                .sobrenome("Doe")
                .email("john@example.com")
                .build();

        ingressoParticipante = ingressoParticipanteRepository.save(ingressoParticipante);

        createLeadRequest = CreateLeadRequest.builder()
                .ingressoParticipanteId(ingressoParticipante.getId())
                .statusId((short) 1)
                .descricao("Test lead")
                .tipoInteracaoInicial("ABORDAGEM")
                .build();
    }

    @Test
    @DisplayName("Deve criar um lead com sucesso")
    @WithMockUser(roles = "ATENDENTE")
    void createLeadSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLeadRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.descricao", is(createLeadRequest.getDescricao())));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar lead duplicado")
    @WithMockUser(roles = "ATENDENTE")
    void createLeadDuplicateError() throws Exception {
        // Criar primeiro lead
        mockMvc.perform(post("/api/v1/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLeadRequest)))
                .andExpect(status().isCreated());

        // Tentar criar lead duplicado
        mockMvc.perform(post("/api/v1/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLeadRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve buscar um lead por ID")
    @WithMockUser(roles = "ATENDENTE")
    void getLeadByIdSuccess() throws Exception {
        // Criar lead
        Lead lead = leadRepository.save(Lead.builder()
                .user(user)
                .ingressoParticipante(ingressoParticipante)
                .statusId((short) 1)
                .descricao("Test lead")
                .build());

        mockMvc.perform(get("/api/v1/leads/{id}", lead.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(lead.getId().toString())))
                .andExpect(jsonPath("$.descricao", is(lead.getDescricao())));
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar lead inexistente")
    @WithMockUser(roles = "ATENDENTE")
    void getLeadByIdNotFoundError() throws Exception {
        mockMvc.perform(get("/api/v1/leads/{id}", "550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(status().isNotFound());
    }
}