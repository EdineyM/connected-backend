package br.com.created.connectedbackend.infrastructure.config.mapper;

import br.com.created.connectedbackend.api.dto.response.lead.AudioDescricaoResponse;
import br.com.created.connectedbackend.api.dto.response.lead.CapturaLeadResponse;
import br.com.created.connectedbackend.api.dto.response.lead.IngressoParticipanteResponse;
import br.com.created.connectedbackend.api.dto.response.lead.LeadResponse;
import br.com.created.connectedbackend.domain.model.lead.AudioDescricao;
import br.com.created.connectedbackend.domain.model.lead.CapturaLead;
import br.com.created.connectedbackend.domain.model.lead.IngressoParticipante;
import br.com.created.connectedbackend.domain.model.lead.Lead;
import org.springframework.stereotype.Component;

@Component
public class LeadMapper {

    public LeadResponse toLeadResponse(Lead lead) {
        return LeadResponse.builder()
                .id(lead.getId())
                .userId(lead.getUser().getId())
                .userName(lead.getUser().getNome())
                .ingressoParticipante(toIngressoParticipanteResponse(lead.getIngressoParticipante()))
                .statusId(lead.getStatusId())
                .descricao(lead.getDescricao())
                .localizacao(lead.getLocalizacao())
                .createdAt(lead.getCreatedAt())
                .updatedAt(lead.getUpdatedAt())
                .build();
    }

    public IngressoParticipanteResponse toIngressoParticipanteResponse(IngressoParticipante ingresso) {
        return IngressoParticipanteResponse.builder()
                .id(ingresso.getId())
                .nome(ingresso.getNome())
                .sobrenome(ingresso.getSobrenome())
                .email(ingresso.getEmail())
                .telefone(ingresso.getTelefone())
                .cpf(ingresso.getCpf())
                .qrCode(ingresso.getQrCode())
                .nomeEmpresa(ingresso.getNomeEmpresa())
                .cnpjEmpresa(ingresso.getCnpjEmpresa())
                .cargo(ingresso.getCargo())
                .setor(ingresso.getSetor())
                .porteEmpresa(ingresso.getPorteEmpresa())
                .categoria(ingresso.getCategoria())
                .eventoId(ingresso.getEvento().getId())
                .eventoNome(ingresso.getEvento().getNome())
                .createdAt(ingresso.getCreatedAt())
                .build();
    }

    public CapturaLeadResponse toCapturaLeadResponse(CapturaLead captura) {
        return CapturaLeadResponse.builder()
                .id(captura.getId())
                .leadId(captura.getLead().getId())
                .tipoInteracao(captura.getTipoInteracao())
                .descricao(captura.getDescricao())
                .createdAt(captura.getCreatedAt())
                .build();
    }

    public AudioDescricaoResponse toAudioDescricaoResponse(AudioDescricao audio) {
        return AudioDescricaoResponse.builder()
                .id(audio.getId())
                .leadId(audio.getLead().getId())
                .urlS3(audio.getUrlS3())
                .transcricao(audio.getTranscricao())
                .arquivado(audio.getArquivado())
                .createdAt(audio.getCreatedAt())
                .build();
    }
}