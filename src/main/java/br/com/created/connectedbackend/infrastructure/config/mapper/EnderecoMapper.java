package br.com.created.connectedbackend.infrastructure.config.mapper;

import br.com.created.connectedbackend.api.dto.request.address.CreateEnderecoRequest;
import br.com.created.connectedbackend.api.dto.request.address.UpdateEnderecoRequest;
import br.com.created.connectedbackend.api.dto.response.address.EnderecoResponse;
import br.com.created.connectedbackend.domain.model.address.Endereco;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EnderecoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Endereco toEndereco(CreateEnderecoRequest request);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEnderecoFromRequest(UpdateEnderecoRequest request, @MappingTarget Endereco endereco);

    EnderecoResponse toEnderecoResponse(Endereco endereco);
}