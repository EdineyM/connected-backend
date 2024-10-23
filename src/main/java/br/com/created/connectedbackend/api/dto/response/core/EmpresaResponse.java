package br.com.created.connectedbackend.api.dto.response.core;

import br.com.created.connectedbackend.api.dto.response.address.EnderecoResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EmpresaResponse {
    private UUID id;
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String telefone;
    private EnderecoResponse endereco;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Boolean ativo;
}