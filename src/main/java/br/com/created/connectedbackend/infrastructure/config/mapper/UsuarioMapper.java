package br.com.created.connectedbackend.infrastructure.config.mapper;

import br.com.created.connectedbackend.api.dto.request.core.CreateUsuarioRequest;
import br.com.created.connectedbackend.api.dto.request.core.UpdateUsuarioRequest;
import br.com.created.connectedbackend.api.dto.response.core.UsuarioResponse;
import br.com.created.connectedbackend.domain.model.core.Empresa;
import br.com.created.connectedbackend.domain.model.core.Usuario;
import br.com.created.connectedbackend.domain.model.ref.UsuarioTipo;
import br.com.created.connectedbackend.domain.service.address.EnderecoService;
import br.com.created.connectedbackend.domain.service.core.EmpresaService;
import br.com.created.connectedbackend.domain.service.ref.UsuarioTipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioMapper {

    private final EnderecoService enderecoService;
    private final EmpresaService empresaService;
    private final UsuarioTipoService usuarioTipoService;
    private final EmpresaMapper empresaMapper;
    private final EnderecoMapper enderecoMapper;
    private final UsuarioTipoMapper usuarioTipoMapper;

    public Usuario toUsuario(CreateUsuarioRequest request) {
        return Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senhaHash(request.getSenhaHash())
                .telefone(request.getTelefone())
                .tipo(usuarioTipoService.findById(request.getTipoId()).orElse(null))
                .empresa(empresaService.findById(request.getEmpresaId()).orElse(null))
                .endereco(enderecoService.findById(request.getEnderecoId()).orElse(null))
                .fotoUrl(request.getFotoUrl())
                .bio(request.getBio())
                .build();
    }

    public Usuario toUsuario(UpdateUsuarioRequest request) {
        return Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senhaHash(request.getSenhaHash())
                .telefone(request.getTelefone())
                .tipo(usuarioTipoService.findById(request.getTipoId()).orElse(null))
                .empresa(empresaService.findById(request.getEmpresaId()).orElse(null))
                .endereco(enderecoService.findById(request.getEnderecoId()).orElse(null))
                .fotoUrl(request.getFotoUrl())
                .bio(request.getBio())
                .build();
    }

    public UsuarioResponse toUsuarioResponse(Usuario usuario) {
        Empresa empresa = usuario.getEmpresa();
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .telefone(usuario.getTelefone())
                .tipo(usuarioTipoMapper.toUsuarioTipoResponse(usuario.getTipo()))
                .empresa(empresaMapper.toEmpresaResponse(empresa))
                .endereco(enderecoMapper.toEnderecoResponse(usuario.getEndereco()))
                .fotoUrl(usuario.getFotoUrl())
                .bio(usuario.getBio())
                .ativo(usuario.getAtivo())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(usuario.getUpdatedAt())
                .deletedAt(usuario.getDeletedAt())
                .build();
    }
}