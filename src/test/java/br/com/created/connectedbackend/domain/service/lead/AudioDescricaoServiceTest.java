package br.com.created.connectedbackend.domain.service.lead;

import br.com.created.connectedbackend.api.dto.request.lead.CreateAudioDescricaoRequest;
import br.com.created.connectedbackend.domain.exception.LeadNotFoundException;
import br.com.created.connectedbackend.domain.model.lead.AudioDescricao;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.repository.lead.AudioDescricaoRepository;
import br.com.created.connectedbackend.domain.repository.lead.LeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AudioDescricaoServiceTest {

    @Mock
    private AudioDescricaoRepository audioDescricaoRepository;

    @Mock
    private LeadRepository leadRepository;

    @InjectMocks
    private AudioDescricaoService audioDescricaoService;

    private Lead lead;
    private AudioDescricao audioDescricao;
    private CreateAudioDescricaoRequest createRequest;
    private UUID leadId;
    private UUID audioId;

    @BeforeEach
    void setUp() {
        leadId = UUID.randomUUID();
        audioId = UUID.randomUUID();

        lead = Lead.builder()
                .id(leadId)
                .statusId((short) 1)
                .build();

        audioDescricao = AudioDescricao.builder()
                .id(audioId)
                .lead(lead)
                .urlS3("s3://bucket/audio.mp3")
                .transcricao("Transcrição teste")
                .arquivado(false)
                .build();

        createRequest = CreateAudioDescricaoRequest.builder()
                .leadId(leadId)
                .urlS3("s3://bucket/new-audio.mp3")
                .transcricao("Nova transcrição")
                .build();
    }

    @Test
    @DisplayName("Deve criar um áudio com sucesso")
    void createAudioDescricaoSuccess() {
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(lead));
        when(audioDescricaoRepository.save(any(AudioDescricao.class))).thenReturn(audioDescricao);

        AudioDescricao result = audioDescricaoService.createAudioDescricao(createRequest);

        assertNotNull(result);
        assertEquals(audioDescricao.getId(), result.getId());
        assertEquals(audioDescricao.getUrlS3(), result.getUrlS3());
        assertFalse(result.getArquivado());
        verify(audioDescricaoRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar áudio para lead inexistente")
    void createAudioDescricaoLeadNotFoundError() {
        when(leadRepository.findById(leadId)).thenReturn(Optional.empty());

        assertThrows(LeadNotFoundException.class, () ->
                audioDescricaoService.createAudioDescricao(createRequest));

        verify(audioDescricaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve arquivar um áudio com sucesso")
    void arquivarAudioSuccess() {
        doNothing().when(audioDescricaoRepository).updateArquivadoStatus(audioId, true);

        assertDoesNotThrow(() -> audioDescricaoService.arquivarAudio(audioId));
        verify(audioDescricaoRepository).updateArquivadoStatus(audioId, true);
    }

    @Test
    @DisplayName("Deve desarquivar um áudio com sucesso")
    void desarquivarAudioSuccess() {
        doNothing().when(audioDescricaoRepository).updateArquivadoStatus(audioId, false);

        assertDoesNotThrow(() -> audioDescricaoService.desarquivarAudio(audioId));
        verify(audioDescricaoRepository).updateArquivadoStatus(audioId, false);
    }

    @Test
    @DisplayName("Deve buscar áudios paginados por lead")
    void findAudiosByLeadSuccess() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<AudioDescricao> audios = List.of(audioDescricao);
        Page<AudioDescricao> page = new PageImpl<>(audios, pageRequest, audios.size());

        when(audioDescricaoRepository.findAllByLeadId(leadId, pageRequest)).thenReturn(page);

        Page<AudioDescricao> result = audioDescricaoService.findAudiosByLead(leadId, pageRequest);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Deve buscar áudios por status de arquivamento")
    void findAudiosByLeadAndArquivadoSuccess() {
        when(audioDescricaoRepository.findAllByLeadIdAndArquivado(leadId, false))
                .thenReturn(List.of(audioDescricao));

        List<AudioDescricao> result = audioDescricaoService.findAudiosByLeadAndArquivado(leadId, false);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertFalse(result.get(0).getArquivado());
    }

    @Test
    @DisplayName("Deve contar áudios ativos por lead")
    void countActiveAudiosByLeadSuccess() {
        when(audioDescricaoRepository.countActiveByLeadId(leadId)).thenReturn(5L);

        Long result = audioDescricaoService.countActiveAudiosByLead(leadId);

        assertEquals(5L, result);
    }
}