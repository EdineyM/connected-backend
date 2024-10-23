package br.com.created.connectedbackend.integration.lead;

import br.com.created.connectedbackend.api.dto.request.lead.CreateAudioDescricaoRequest;
import br.com.created.connectedbackend.domain.model.lead.AudioDescricao;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.model.lead.IngressoParticipante;
import br.com.created.connectedbackend.domain.model.user.User;
import br.com.created.connectedbackend.domain.repository.lead.AudioDescricaoRepository;
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
class AudioDescricaoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private AudioDescricaoRepository audioDescricaoRepository;

    @Autowired
    private IngressoParticipanteRepository ingressoParticipanteRepository;

    private Lead lead;
    private CreateAudioDescricaoRequest createRequest;
    private AudioDescricao audioDescricao;

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

        createRequest = CreateAudioDescricaoRequest.builder()
                .leadId(lead.getId())
                .urlS3("s3://bucket/test-audio.mp3")
                .transcricao("Test audio transcription")
                .build();

        audioDescricao = AudioDescricao.builder()
                .lead(lead)
                .urlS3("s3://bucket/existing-audio.mp3")
                .transcricao("Existing transcription")
                .arquivado(false)
                .build();
        audioDescricao = audioDescricaoRepository.save(audioDescricao);
    }

    @Test
    @DisplayName("Deve criar um áudio com sucesso")
    @WithMockUser(roles = "ATENDENTE")
    void createAudioSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/audios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.urlS3", is(createRequest.getUrlS3())))
                .andExpect(jsonPath("$.transcricao", is(createRequest.getTranscricao())))
                .andExpect(jsonPath("$.arquivado", is(false)));
    }

    @Test
    @DisplayName("Deve arquivar um áudio com sucesso")
    @WithMockUser(roles = "ATENDENTE")
    void arquivarAudioSuccess() throws Exception {
        mockMvc.perform(patch("/api/v1/audios/{id}/arquivar", audioDescricao.getId()))
                .andExpect(status().isNoContent());

        // Verificar se foi arquivado
        mockMvc.perform(get("/api/v1/audios/lead/{leadId}/arquivados", lead.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(audioDescricao.getId().toString())));
    }

    @Test
    @DisplayName("Deve listar áudios com transcrição")
    @WithMockUser(roles = "ATENDENTE")
    void listAudiosComTranscricaoSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/audios/lead/{leadId}/transcricoes", lead.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].transcricao", is(audioDescricao.getTranscricao())));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar áudio para lead inexistente")
    @WithMockUser(roles = "ATENDENTE")
    void createAudioLeadNotFoundError() throws Exception {
        createRequest.setLeadId(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/audios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve negar acesso a usuário não autorizado")
    @WithMockUser(roles = "ROLE_INVALID")
    void createAudioDeniedError() throws Exception {
        mockMvc.perform(post("/api/v1/audios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }
}