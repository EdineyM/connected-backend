package br.com.created.connectedbackend.api.dto.response.address;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EnderecoResponse {
    private UUID id;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String pais;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}