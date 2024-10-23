package br.com.created.connectedbackend.api.controller.lead;

import br.com.created.connectedbackend.api.dto.request.lead.CreateAudioDescricaoRequest;
import br.com.created.connectedbackend.domain.model.lead.AudioDescricao;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.service.lead.AudioDescricaoService;
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
class AudioDescricaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AudioDescricaoService audioDescricaoService;

    private CreateAudioDescricaoRequest createRequest;
    private AudioDescricao audioDescricao;
    private Lead lead;

    @BeforeEach
    void setUp() {
        lead = TestDataBuilder.createTestLead(
                TestDataBuilder.createTestUser(),
                TestDataBuilder.createTestIngressoParticipante(TestDataBuilder.createTestEvento())
        );
        audioDescricao = TestDataBuilder.createTestAudioDescricao(lead);

        createRequest = CreateAudioDescricaoRequest.builder()
                .leadId(lead.getId())
                .urlS3("s3://bucket/test-audio.mp3")
                .transcricao("Transcrição do áudio de teste")
                .build();
    }

    @Test
    @DisplayName("Deve criar áudio descrição com sucesso")
    @WithMockUser(roles = "ATENDENTE")
    void createAudioDescricaoSuccess() throws Exception {
        when(audioDescricaoService.createAudioDescricao(any())).thenReturn(audioDescricao);

        mockMvc.perform(post("/api/v1/audios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.urlS3").value(audioDescricao.getUrlS3()))
                .andExpect(jsonPath("$.transcricao").value(audioDescricao.getTranscricao()));

        verify(audioDescricaoService).createAudioDescricao(any());
    }

    @Test
    @DisplayName("Deve arquivar áudio com sucesso")
    @WithMockUser(roles = "ATENDENTE")
    void arquivarAudioSuccess() throws Exception {
        doNothing().when(audioDescricaoService).arquivarAudio(any());

        mockMvc.perform(patch("/api/v1/audios/{id}/arquivar", UUID.randomUUID()))
                .andExpect(status().isNoContent());

        verify(audioDescricaoService).arquivarAudio(any());
    }

    @Test
    @DisplayName("Deve desarquivar áudio com sucesso")
    @WithMockUser(roles = "ATENDENTE")
    void desarquivarAudioSuccess() throws Exception {
        doNothing().when(audioDescricaoService).desarquivarAudio(any());

        mockMvc.perform(patch("/api/v1/audios/{id}/desarquivar", UUID.randomUUID()))
                .andExpect(status().isNoContent());

        verify(audioDescricaoService).desarquivarAudio(any());
    }

    @Test
    @DisplayName("Deve listar áudios por lead")
    @WithMockUser(roles = "ATENDENTE")
    void getAudiosByLeadSuccess() throws Exception {
        when(audioDescricaoService.findAudiosByLead(any(), any()))
                .thenReturn(new PageImpl<>(List.of(audioDescricao)));

        mockMvc.perform(get("/api/v1/audios/lead/{leadId}", UUID.randomUUID())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].urlS3").exists());

        verify(audioDescricaoService).findAudiosByLead(any(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve listar áudios arquivados")
    @WithMockUser(roles = "ATENDENTE")
    void getAudiosArquivadosSuccess() throws Exception {
        when(audioDescricaoService.findAudiosByLeadAndArquivado(any(), eq(true)))
                .thenReturn(List.of(audioDescricao));

        mockMvc.perform(get("/api/v1/audios/lead/{leadId}/arquivados", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].arquivado").value(true));

        verify(audioDescricaoService).findAudiosByLeadAndArquivado(any(), eq(true));
    }

    @Test
    @DisplayName("Deve listar áudios com transcrição")
    @WithMockUser(roles = "ATENDENTE")
    void getAudiosComTranscricaoSuccess() throws Exception {
        when(audioDescricaoService.findAudiosWithTranscricao(any()))
                .thenReturn(List.of(audioDescricao));

        mockMvc.perform(get("/api/v1/audios/lead/{leadId}/transcricoes", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].transcricao").exists());

        verify(audioDescricaoService).findAudiosWithTranscricao(any());
    }

    @Test
    @DisplayName("Deve validar request inválido")
    @WithMockUser(roles = "ATENDENTE")
    void createAudioValidationError() throws Exception {
        createRequest.setUrlS3(null);

        mockMvc.perform(post("/api/v1/audios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(audioDescricaoService, never()).createAudioDescricao(any());
    }

    @Test
    @DisplayName("Deve negar acesso a usuário não autorizado")
    @WithMockUser(roles = "ROLE_INVALID")
    void unauthorizedAccessError() throws Exception {
        mockMvc.perform(post("/api/v1/audios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());

        verify(audioDescricaoService, never()).createAudioDescricao(any());
    }
}