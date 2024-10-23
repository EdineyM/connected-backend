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
public class IngressoParticipanteResponse {
    private UUID id;
    private String nome;
    private String sobrenome;
    private String email;
    private String telefone;
    private String cpf;
    private String qrCode;
    private String nomeEmpresa;
    private String cnpjEmpresa;
    private String cargo;
    private String setor;
    private String porteEmpresa;
    private String categoria;
    private UUID eventoId;
    private String eventoNome;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}