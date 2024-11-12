package br.com.created.connectedbackend.infrastructure.config.mapper;

import br.com.created.connectedbackend.api.dto.request.core.CreateEmpresaRequest;
import br.com.created.connectedbackend.api.dto.request.core.UpdateEmpresaRequest;
import br.com.created.connectedbackend.api.dto.response.core.EmpresaResponse;
import br.com.created.connectedbackend.domain.model.core.Empresa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Empresa toEmpresa(CreateEmpresaRequest request);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEmpresaFromRequest(UpdateEmpresaRequest request, @MappingTarget Empresa empresa);

    EmpresaResponse toEmpresaResponse(Empresa empresa);
}