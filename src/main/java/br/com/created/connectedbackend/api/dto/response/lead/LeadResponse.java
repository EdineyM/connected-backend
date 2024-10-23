package br.com.created.connectedbackend.api.dto.response.lead;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadResponse {
    private UUID id;
    private UUID userId;
    private String userName;
    private IngressoParticipanteResponse ingressoParticipante;
    private Short statusId;
    private String statusDescricao;
    private String descricao;
    private String localizacao;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private Long totalCapturas;
    private Long totalAudios;
    private CapturaLeadResponse ultimaCaptura;
}