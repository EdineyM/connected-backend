package br.com.created.connectedbackend.api.controller.core;

import br.com.created.connectedbackend.api.dto.request.core.CreateUsuarioRequest;
import br.com.created.connectedbackend.api.dto.request.core.UpdateUsuarioRequest;
import br.com.created.connectedbackend.api.dto.response.core.UsuarioResponse;
import br.com.created.connectedbackend.domain.model.core.Usuario;
import br.com.created.connectedbackend.domain.service.core.UsuarioService;
import br.com.created.connectedbackend.infrastructure.config.mapper.UsuarioMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "API para gerenciamento de usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    @PostMapping
    @Operation(summary = "Criar novo usuário")
    public ResponseEntity<UsuarioResponse> createUsuario(
            @Valid @RequestBody CreateUsuarioRequest request) {
        Usuario usuario = usuarioMapper.toUsuario(request);
        Usuario savedUsuario = usuarioService.createUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioMapper.toUsuarioResponse(savedUsuario));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário existente")
    public ResponseEntity<UsuarioResponse> updateUsuario(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUsuarioRequest request) {
        Usuario usuario = usuarioMapper.toUsuario(request);
        usuario.setId(id);
        Usuario updatedUsuario = usuarioService.updateUsuario(usuario);
        return ResponseEntity.ok(usuarioMapper.toUsuarioResponse(updatedUsuario));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<UsuarioResponse> getUsuarioById(@PathVariable UUID id) {
        Usuario usuario = usuarioService.findUsuarioById(id);
        return ResponseEntity.ok(usuarioMapper.toUsuarioResponse(usuario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar usuário")
    public ResponseEntity<Void> deleteUsuario(@PathVariable UUID id) {
        Usuario usuario = usuarioService.findUsuarioById(id);
        usuarioService.deleteUsuario(usuario);
        return ResponseEntity.noContent().build();
    }
}