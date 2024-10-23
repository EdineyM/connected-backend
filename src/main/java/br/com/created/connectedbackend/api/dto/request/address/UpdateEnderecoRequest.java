package br.com.created.connectedbackend.api.dto.request.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateEnderecoRequest {

    @NotBlank
    @Pattern(regexp = "^\\d{8}$", message = "O CEP deve conter exatamente 8 dígitos numéricos")
    private String cep;

    @NotBlank
    private String logradouro;

    private String numero;

    private String complemento;

    @NotBlank
    private String bairro;

    @NotBlank
    private String cidade;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}$", message = "O estado deve conter exatamente 2 letras maiúsculas")
    private String estado;

    private String pais;
}