package br.com.created.connectedbackend.infrastructure.config.mapper;

import br.com.created.connectedbackend.api.dto.request.address.CreateEnderecoRequest;
import br.com.created.connectedbackend.api.dto.request.address.UpdateEnderecoRequest;
import br.com.created.connectedbackend.api.dto.response.address.EnderecoResponse;
import br.com.created.connectedbackend.domain.model.address.Endereco;
import org.springframework.stereotype.Component;

@Component
public class EnderecoMapper {

    public Endereco toEndereco(CreateEnderecoRequest request) {
        return Endereco.builder()
                .cep(request.getCep())
                .logradouro(request.getLogradouro())
                .numero(request.getNumero())
                .complemento(request.getComplemento())
                .bairro(request.getBairro())
                .cidade(request.getCidade())
                .estado(request.getEstado())
                .pais(request.getPais())
                .build();
    }

    public Endereco toEndereco(UpdateEnderecoRequest request) {
        return Endereco.builder()
                .cep(request.getCep())
                .logradouro(request.getLogradouro())
                .numero(request.getNumero())
                .complemento(request.getComplemento())
                .bairro(request.getBairro())
                .cidade(request.getCidade())
                .estado(request.getEstado())
                .pais(request.getPais())
                .build();
    }

    public EnderecoResponse toEnderecoResponse(Endereco endereco) {
        return EnderecoResponse.builder()
                .id(endereco.getId())
                .cep(endereco.getCep())
                .logradouro(endereco.getLogradouro())
                .numero(endereco.getNumero())
                .complemento(endereco.getComplemento())
                .bairro(endereco.getBairro())
                .cidade(endereco.getCidade())
                .estado(endereco.getEstado())
                .pais(endereco.getPais())
                .createdAt(endereco.getCreatedAt())
                .updatedAt(endereco.getUpdatedAt())
                .build();
    }
}