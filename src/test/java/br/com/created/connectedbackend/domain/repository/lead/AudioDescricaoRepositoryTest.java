package br.com.created.connectedbackend.domain.repository.lead;

import br.com.created.connectedbackend.domain.model.lead.AudioDescricao;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.model.lead.IngressoParticipante;
import br.com.created.connectedbackend.domain.model.event.Evento;
import br.com.created.connectedbackend.domain.model.user.User;
import br.com.created.connectedbackend.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AudioDescricaoRepositoryTest {

    @Autowired
    private AudioDescricaoRepository audioDescricaoRepository;

    @Autowired
    private EntityManager entityManager;

    private Lead lead;
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

        audioDescricao = TestDataBuilder.createTestAudioDescricao(lead);
        entityManager.persist(audioDescricao);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Deve buscar áudios por lead paginados")
    void findAllByLeadId() {
        Page<AudioDescricao> audios = audioDescricaoRepository.findAllByLeadId(
                lead.getId(),
                PageRequest.of(0, 10)
        );

        assertNotNull(audios);
        assertFalse(audios.isEmpty());
        assertEquals(1, audios.getTotalElements());
        assertEquals(audioDescricao.getId(), audios.getContent().get(0).getId());
    }

    @Test
    @DisplayName("Deve buscar áudios por status de arquivamento")
    void findAllByLeadIdAndArquivado() {
        List<AudioDescricao> audios = audioDescricaoRepository
                .findAllByLeadIdAndArquivado(lead.getId(), false);

        assertFalse(audios.isEmpty());
        assertEquals(1, audios.size());
        assertFalse(audios.get(0).getArquivado());
    }

    @Test
    @DisplayName("Deve buscar áudios com transcrição")
    void findAllByLeadIdWithTranscricao() {
        List<AudioDescricao> audios = audioDescricaoRepository
                .findAllByLeadIdWithTranscricao(lead.getId());

        assertFalse(audios.isEmpty());
        assertEquals(1, audios.size());
        assertNotNull(audios.get(0).getTranscricao());
    }

    @Test
    @DisplayName("Deve atualizar status de arquivamento")
    void updateArquivadoStatus() {
        audioDescricaoRepository.updateArquivadoStatus(audioDescricao.getId(), true);
        entityManager.flush();
        entityManager.clear();

        AudioDescricao updated = entityManager.find(AudioDescricao.class, audioDescricao.getId());
        assertTrue(updated.getArquivado());
    }

    @Test
    @DisplayName("Deve contar áudios ativos por lead")
    void countActiveByLeadId() {
        Long count = audioDescricaoRepository.countActiveByLeadId(lead.getId());
        assertEquals(1L, count);
    }

    @Test
    @DisplayName("Deve encontrar áudio não arquivado por ID e lead")
    void findByIdAndLeadIdAndNotArchived() {
        var found = audioDescricaoRepository.findByIdAndLeadIdAndNotArchived(
                audioDescricao.getId(),
                lead.getId()
        );

        assertTrue(found.isPresent());
        assertFalse(found.get().getArquivado());
    }
}