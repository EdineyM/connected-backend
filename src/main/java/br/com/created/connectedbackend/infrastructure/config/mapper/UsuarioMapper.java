package br.com.created.connectedbackend.infrastructure.config.mapper;

import br.com.created.connectedbackend.api.dto.request.core.CreateUsuarioRequest;
import br.com.created.connectedbackend.api.dto.request.core.UpdateUsuarioRequest;
import br.com.created.connectedbackend.api.dto.response.core.UsuarioResponse;
import br.com.created.connectedbackend.domain.model.core.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {EmpresaMapper.class, EnderecoMapper.class})
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "senhaSalt", ignore = true)
    Usuario toUsuario(CreateUsuarioRequest request);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateUsuarioFromRequest(UpdateUsuarioRequest request, @MappingTarget Usuario usuario);

    UsuarioResponse toUsuarioResponse(Usuario usuario);
}