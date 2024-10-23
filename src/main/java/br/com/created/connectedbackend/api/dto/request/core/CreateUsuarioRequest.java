package br.com.created.connectedbackend.api.dto.request.core;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateUsuarioRequest {

    @NotBlank
    private String nome;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String senhaHash;

    @Pattern(regexp = "^\\+?[1-9]\\d{10,14}$", message = "O telefone deve conter entre 10 e 15 dígitos, incluindo o código do país (opcional)")
    private String telefone;

    private UUID tipoId;

    private UUID empresaId;

    private UUID enderecoId;

    private String fotoUrl;

    private String bio;
}