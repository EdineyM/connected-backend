package br.com.created.connectedbackend.domain.service.core;

import br.com.created.connectedbackend.domain.exception.BusinessException;
import br.com.created.connectedbackend.domain.exception.NotFoundException;
import br.com.created.connectedbackend.domain.model.core.Usuario;
import br.com.created.connectedbackend.domain.repository.core.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public Usuario createUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmailAndDeletedAtIsNull(usuario.getEmail())) {
            throw new BusinessException("Já existe um usuário cadastrado com este e-mail");
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario updateUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario findUsuarioById(UUID id) {
        return usuarioRepository.findByIdOrEmailOrNameContaining(id, null, null)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    public Optional<Usuario> findUsuarioByIdOrEmailOrName(UUID id, String email, String termo) {
        return usuarioRepository.findByIdOrEmailOrNameContaining(id, email, termo);
    }

    public void deleteUsuario(Usuario usuario) {
        usuario.setDeletedAt(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }
}