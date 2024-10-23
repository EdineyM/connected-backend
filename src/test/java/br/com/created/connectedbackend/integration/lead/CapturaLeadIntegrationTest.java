package br.com.created.connectedbackend.integration.lead;

import br.com.created.connectedbackend.api.dto.request.lead.CreateCapturaLeadRequest;
import br.com.created.connectedbackend.domain.model.lead.CapturaLead;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.model.lead.IngressoParticipante;
import br.com.created.connectedbackend.domain.model.user.User;
import br.com.created.connectedbackend.domain.repository.lead.CapturaLeadRepository;
import br.com.created.connectedbackend.domain.repository.lead.LeadRepository;
import br.com.created.connectedbackend.domain.repository.lead.IngressoParticipanteRepository;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CapturaLeadIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private CapturaLeadRepository capturaLeadRepository;

    @Autowired
    private IngressoParticipanteRepository ingressoParticipanteRepository;

    private Lead lead;
    private CreateCapturaLeadRequest createRequest;

    @BeforeEach
    void setUp() {
        // Criar dados de teste
        User user = User.builder()
                .nome("Test User")
                .email("test@example.com")
                .build();

        IngressoParticipante ingressoParticipante = IngressoParticipante.builder()
                .nome("John")
                .sobrenome("Doe")
                .email("john@example.com")
                .build();
        ingressoParticipante = ingressoParticipanteRepository.save(ingressoParticipante);

        lead = Lead.builder()
                .user(user)
                .ingressoParticipante(ingressoParticipante)
                .statusId((short) 1)
                .descricao("Test lead")
                .build();
        lead = leadRepository.save(lead);

        createRequest = CreateCapturaLeadRequest.builder()
                .leadId(lead.getId())
                .tipoInteracao("REUNIAO")
                .descricao("Test capture description")
                .build();
    }

    @Test
    @DisplayName("Deve criar uma captura com sucesso")
    @WithMockUser(roles = "ATENDENTE")
    void createCapturaSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/capturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.tipoInteracao", is(createRequest.getTipoInteracao())))
                .andExpect(jsonPath("$.descricao", is(createRequest.getDescricao())));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar captura para lead inexistente")
    @WithMockUser(roles = "ATENDENTE")
    void createCapturaLeadNotFoundError() throws Exception {
        createRequest.setLeadId(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/capturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar capturas paginadas por lead")
    @WithMockUser(roles = "ATENDENTE")
    void listCapturasByLeadSuccess() throws Exception {
        // Criar algumas capturas
        CapturaLead captura = CapturaLead.builder()
                .lead(lead)
                .tipoInteracao("REUNIAO")
                .descricao("Test capture")
                .build();
        capturaLeadRepository.save(captura);

        mockMvc.perform(get("/api/v1/capturas/lead/{leadId}", lead.getId())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.content[0].tipoInteracao", is("REUNIAO")));
    }

    @Test
    @DisplayName("Deve listar capturas por tipo de interação")
    @WithMockUser(roles = "ATENDENTE")
    void listCapturasByTipoInteracaoSuccess() throws Exception {
        // Criar captura com tipo específico
        CapturaLead captura = CapturaLead.builder()
                .lead(lead)
                .tipoInteracao("EMAIL")
                .descricao("Test email capture")
                .build();
        capturaLeadRepository.save(captura);

        mockMvc.perform(get("/api/v1/capturas/lead/{leadId}/tipo/{tipo}",
                        lead.getId(), "EMAIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipoInteracao", is("EMAIL")));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para lead sem capturas")
    @WithMockUser(roles = "ATENDENTE")
    void listCapturasByLeadEmptySuccess() throws Exception {
        mockMvc.perform(get("/api/v1/capturas/lead/{leadId}", lead.getId())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @DisplayName("Deve negar acesso a usuário não autorizado")
    @WithMockUser(roles = "ROLE_INVALID")
    void createCapturaDeniedError() throws Exception {
        mockMvc.perform(post("/api/v1/capturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }
}