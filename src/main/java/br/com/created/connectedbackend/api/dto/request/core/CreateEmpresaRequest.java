package br.com.created.connectedbackend.api.dto.request.core;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateEmpresaRequest {

    @NotBlank
    private String razaoSocial;

    private String nomeFantasia;

    @NotBlank
    @Pattern(regexp = "^\\d{14}$", message = "O CNPJ deve conter exatamente 14 dígitos numéricos")
    private String cnpj;

    @Pattern(regexp = "^\\+?[1-9]\\d{10,14}$", message = "O telefone deve conter entre 10 e 15 dígitos, incluindo o código do país (opcional)")
    private String telefone;

    private UUID enderecoId;
}