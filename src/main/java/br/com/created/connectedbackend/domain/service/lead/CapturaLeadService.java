package br.com.created.connectedbackend.domain.service.lead;

import br.com.created.connectedbackend.api.dto.request.lead.CreateCapturaLeadRequest;
import br.com.created.connectedbackend.domain.exception.NotFoundException;
import br.com.created.connectedbackend.domain.model.lead.CapturaLead;
import br.com.created.connectedbackend.domain.repository.lead.CapturaLeadRepository;
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
public class CapturaLeadService {

    private final CapturaLeadRepository capturaLeadRepository;
    private final LeadRepository leadRepository;

    @Transactional
    public CapturaLead createCaptura(CreateCapturaLeadRequest request) {
        var lead = leadRepository.findById(request.getLeadId())
                .orElseThrow(() -> new NotFoundException("Lead não encontrado"));

        var captura = CapturaLead.builder()
                .lead(lead)
                .tipoInteracao(request.getTipoInteracao())
                .descricao(request.getDescricao())
                .build();

        return capturaLeadRepository.save(captura);
    }

    public Page<CapturaLead> findCapturasByLead(UUID leadId, Pageable pageable) {
        return capturaLeadRepository.findAllByLeadId(leadId, pageable);
    }

    public List<CapturaLead> findCapturasByTipoInteracao(UUID leadId, String tipoInteracao) {
        return capturaLeadRepository.findAllByLeadIdAndTipoInteracao(leadId, tipoInteracao);
    }

    public List<String> findTiposInteracaoByLead(UUID leadId) {
        return capturaLeadRepository.findDistinctTipoInteracaoByLeadId(leadId);
    }

    public List<CapturaLead> findCapturasByEvento(UUID eventoId) {
        return capturaLeadRepository.findAllByEventoId(eventoId);
    }
}