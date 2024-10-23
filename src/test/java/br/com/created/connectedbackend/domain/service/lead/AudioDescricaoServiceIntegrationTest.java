package br.com.created.connectedbackend.domain.service.lead;

import br.com.created.connectedbackend.api.dto.request.lead.CreateAudioDescricaoRequest;
import br.com.created.connectedbackend.domain.exception.LeadNotFoundException;
import br.com.created.connectedbackend.domain.model.lead.AudioDescricao;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.model.lead.IngressoParticipante;
import br.com.created.connectedbackend.domain.model.event.Evento;
import br.com.created.connectedbackend.domain.model.user.User;
import br.com.created.connectedbackend.domain.repository.lead.AudioDescricaoRepository;
import br.com.created.connectedbackend.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AudioDescricaoServiceIntegrationTest {

    @Autowired
    private AudioDescricaoService audioDescricaoService;

    @Autowired
    private AudioDescricaoRepository audioDescricaoRepository;

    @Autowired
    private EntityManager entityManager;

    private Lead lead;
    private CreateAudioDescricaoRequest createRequest;
    private AudioDescricao audioDescricao;

    @BeforeEach
    void setUp() {
        // Criar e persistir dados necessários
        Evento evento = TestDataBuilder.createTestEvento();
        entityManager.persist(evento);

        User user = TestDataBuilder.createTestUser();
        entityManager.persist(user);

        IngressoParticipante ingressoParticipante = TestDataBuilder.createTestIngressoParticipante(evento);
        entityManager.persist(ingressoParticipante);

        lead = TestDataBuilder.createTestLead(user, ingressoParticipante);
        entityManager.persist(lead);

        entityManager.flush();

        createRequest = CreateAudioDescricaoRequest.builder()
                .leadId(lead.getId())
                .urlS3("s3://bucket/audio-test.mp3")
                .transcricao("Transcrição do áudio de teste")
                .build();
    }

    @Test
    @DisplayName("Deve criar áudio descrição com sucesso")
    void createAudioDescricaoSuccess() {
        AudioDescricao created = audioDescricaoService.createAudioDescricao(createRequest);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(createRequest.getUrlS3(), created.getUrlS3());
        assertEquals(createRequest.getTranscricao(), created.getTranscricao());
        assertEquals(lead.getId(), created.getLead().getId());
        assertFalse(created.getArquivado());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar áudio para lead inexistente")
    void createAudioDescricaoLeadNotFoundError() {
        createRequest.setLeadId(UUID.randomUUID());

        assertThrows(LeadNotFoundException.class, () ->
                audioDescricaoService.createAudioDescricao(createRequest)
        );
    }

    @Test
    @DisplayName("Deve arquivar e desarquivar áudio com sucesso")
    void arquivarDesarquivarAudioSuccess() {
        AudioDescricao audio = audioDescricaoService.createAudioDescricao(createRequest);

        audioDescricaoService.arquivarAudio(audio.getId());
        entityManager.flush();
        entityManager.clear();

        List<AudioDescricao> arquivados = audioDescricaoService
                .findAudiosByLeadAndArquivado(lead.getId(), true);
        assertFalse(arquivados.isEmpty());
        assertTrue(arquivados.get(0).getArquivado());

        audioDescricaoService.desarquivarAudio(audio.getId());
        entityManager.flush();
        entityManager.clear();

        List<AudioDescricao> ativos = audioDescricaoService
                .findAudiosByLeadAndArquivado(lead.getId(), false);
        assertFalse(ativos.isEmpty());
        assertFalse(ativos.get(0).getArquivado());
    }

    @Test
    @DisplayName("Deve buscar áudios paginados por lead")
    void findAudiosByLeadSuccess() {
        audioDescricaoService.createAudioDescricao(createRequest);

        Page<AudioDescricao> audios = audioDescricaoService.findAudiosByLead(
                lead.getId(),
                PageRequest.of(0, 10)
        );

        assertNotNull(audios);
        assertFalse(audios.isEmpty());
        assertEquals(1, audios.getTotalElements());
    }

    @Test
    @DisplayName("Deve buscar áudios com transcrição")
    void findAudiosWithTranscricaoSuccess() {
        audioDescricaoService.createAudioDescricao(createRequest);

        List<AudioDescricao> audios = audioDescricaoService
                .findAudiosWithTranscricao(lead.getId());

        assertFalse(audios.isEmpty());
        assertEquals(1, audios.size());
        assertNotNull(audios.get(0).getTranscricao());
    }

    @Test
    @DisplayName("Deve contar áudios ativos")
    void countActiveAudiosByLeadSuccess() {
        audioDescricaoService.createAudioDescricao(createRequest);

        Long count = audioDescricaoService.countActiveAudiosByLead(lead.getId());
        assertEquals(1L, count);
    }
}