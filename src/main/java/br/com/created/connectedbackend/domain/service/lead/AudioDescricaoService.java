package br.com.created.connectedbackend.domain.service.lead;

import br.com.created.connectedbackend.api.dto.request.lead.CreateAudioDescricaoRequest;
import br.com.created.connectedbackend.domain.exception.NotFoundException;
import br.com.created.connectedbackend.domain.model.lead.AudioDescricao;
import br.com.created.connectedbackend.domain.repository.lead.AudioDescricaoRepository;
import br.com.created.connectedbackend.domain.repository.lead.LeadRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AudioDescricaoService {

    private final AudioDescricaoRepository audioDescricaoRepository;
    private final LeadRepository leadRepository;

    @Transactional
    public AudioDescricao createAudioDescricao(CreateAudioDescricaoRequest request) {
        var lead = leadRepository.findById(request.getLeadId())
                .orElseThrow(() -> new NotFoundException("Lead não encontrado"));

        var audioDescricao = AudioDescricao.builder()
                .lead(lead)
                .urlS3(request.getUrlS3())
                .transcricao(request.getTranscricao())
                .arquivado(false)
                .build();

        return audioDescricaoRepository.save(audioDescricao);
    }

    @Transactional
    public void arquivarAudio(UUID audioId) {
        audioDescricaoRepository.updateArquivadoStatus(audioId, true);
    }

    @Transactional
    public void desarquivarAudio(UUID audioId) {
        audioDescricaoRepository.updateArquivadoStatus(audioId, false);
    }

    public Page<AudioDescricao> findAudiosByLead(UUID leadId, Pageable pageable) {
        return audioDescricaoRepository.findAllByLeadId(leadId, pageable);
    }

    public List<AudioDescricao> findAudiosByLeadAndArquivado(UUID leadId, boolean arquivado) {
        return audioDescricaoRepository.findAllByLeadIdAndArquivado(leadId, arquivado);
    }

    public List<AudioDescricao> findAudiosWithTranscricao(UUID leadId) {
        return audioDescricaoRepository.findAllByLeadIdWithTranscricao(leadId);
    }

    public Long countActiveAudiosByLead(UUID leadId) {
        return audioDescricaoRepository.countActiveByLeadId(leadId);
    }
}