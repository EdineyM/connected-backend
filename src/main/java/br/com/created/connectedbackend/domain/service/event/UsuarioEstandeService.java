package br.com.created.connectedbackend.domain.service.event;

import br.com.created.connectedbackend.domain.exception.BusinessException;
import br.com.created.connectedbackend.domain.exception.NotFoundException;
import br.com.created.connectedbackend.domain.model.event.UsuarioEstandeEntity;
import br.com.created.connectedbackend.domain.model.event.UsuarioEstandeId;
import br.com.created.connectedbackend.domain.repository.event.UsuarioEstandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioEstandeService {

    private final UsuarioEstandeRepository usuarioEstandeRepository;

    public UsuarioEstandeEntity createUsuarioEstande(UsuarioEstandeEntity usuarioEstande) {
        UsuarioEstandeId id = usuarioEstande.getId();
        if (usuarioEstandeRepository.findByUsuarioIdAndEstandeId(id.getUsuarioId(), id.getEstandeId()).isPresent()) {
            throw new BusinessException("Usuário já está cadastrado neste estande");
        }
        return usuarioEstandeRepository.save(usuarioEstande);
    }

    public UsuarioEstandeEntity updateUsuarioEstande(UsuarioEstandeEntity usuarioEstande) {
        return usuarioEstandeRepository.save(usuarioEstande);
    }

    public UsuarioEstandeEntity findUsuarioEstandeById(UsuarioEstandeId id) {
        return usuarioEstandeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Relação usuário-estande não encontrada"));
    }

    public List<UsuarioEstandeEntity> findUsuariosEstandeByEstande(UUID estandeId) {
        return usuarioEstandeRepository.findAllByEstandeId(estandeId);
    }

    public void deleteUsuarioEstande(UsuarioEstande usuarioEstande) {
        usuarioEstandeRepository.delete(usuarioEstande);
    }
}