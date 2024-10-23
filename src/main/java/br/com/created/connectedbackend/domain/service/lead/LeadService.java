package br.com.created.connectedbackend.domain.service.lead;

import br.com.created.connectedbackend.api.dto.request.lead.CreateLeadRequest;
import br.com.created.connectedbackend.api.dto.request.lead.UpdateLeadRequest;
import br.com.created.connectedbackend.domain.exception.BusinessException;
import br.com.created.connectedbackend.domain.exception.NotFoundException;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import br.com.created.connectedbackend.domain.model.lead.CapturaLead;
import br.com.created.connectedbackend.domain.model.user.User;
import br.com.created.connectedbackend.domain.repository.lead.LeadRepository;
import br.com.created.connectedbackend.domain.repository.lead.CapturaLeadRepository;
import br.com.created.connectedbackend.domain.repository.lead.IngressoParticipanteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;
    private final CapturaLeadRepository capturaLeadRepository;
    private final IngressoParticipanteRepository ingressoParticipanteRepository;

    @Transactional
    public Lead createLead(CreateLeadRequest request, User currentUser) {
        var ingressoParticipante = ingressoParticipanteRepository.findById(request.getIngressoParticipanteId())
                .orElseThrow(() -> new NotFoundException("Ingresso participante não encontrado"));

        if (leadRepository.existsByIngressoParticipanteIdAndDeletedAtIsNull(request.getIngressoParticipanteId())) {
            throw new BusinessException("Já existe um lead ativo para este participante");
        }

        var lead = Lead.builder()
                .user(currentUser)
                .ingressoParticipante(ingressoParticipante)
                .statusId(request.getStatusId())
                .descricao(request.getDescricao())
                .localizacao(request.getLocalizacao())
                .build();

        lead = leadRepository.save(lead);

        var capturaInicial = CapturaLead.builder()
                .lead(lead)
                .tipoInteracao(request.getTipoInteracaoInicial())
                .descricao(request.getDescricaoInteracao())
                .build();

        capturaLeadRepository.save(capturaInicial);

        return lead;
    }

    @Transactional
    public Lead updateLead(UUID id, UpdateLeadRequest request) {
        var lead = findLeadById(id);

        if (request.getStatusId() != null) {
            lead.setStatusId(request.getStatusId());
        }
        if (request.getDescricao() != null) {
            lead.setDescricao(request.getDescricao());
        }
        if (request.getLocalizacao() != null) {
            lead.setLocalizacao(request.getLocalizacao());
        }

        return leadRepository.save(lead);
    }

    @Transactional
    public void deleteLead(UUID id) {
        Lead lead = findLeadById(id);
        lead.softDelete();
        leadRepository.save(lead);
    }

    public Lead findLeadById(UUID id) {
        return leadRepository.findByIdWithRelationships(id)
                .orElseThrow(() -> new NotFoundException("Lead não encontrado"));
    }

    public Page<Lead> findLeadsByCurrentUser(UUID userId, Pageable pageable) {
        return leadRepository.findAllByUserIdAndNotDeleted(userId, pageable);
    }

    public List<Lead> findLeadsByEvento(UUID eventoId) {
        return leadRepository.findAllByEventoId(eventoId);
    }

    public List<Short> findLeadStatusByUser(UUID userId) {
        return leadRepository.findDistinctStatusByUserId(userId);
    }

    public boolean hasActiveLeadForParticipante(UUID ingressoId) {
        return leadRepository.existsByIngressoParticipanteIdAndDeletedAtIsNull(ingressoId);
    }
}