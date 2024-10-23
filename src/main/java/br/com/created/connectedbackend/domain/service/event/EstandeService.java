package br.com.created.connectedbackend.domain.service.event;

import br.com.created.connectedbackend.domain.exception.BusinessException;
import br.com.created.connectedbackend.domain.exception.NotFoundException;
import br.com.created.connectedbackend.domain.model.event.Estande;
import br.com.created.connectedbackend.domain.repository.event.EstandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EstandeService {

    private final EstandeRepository estandeRepository;

    public Estande createEstande(Estande estande) {
        if (estandeRepository.existsByNomeAndEventoIdAndDeletedAtIsNull(
                estande.getNome(), estande.getEvento().getId())) {
            throw new BusinessException("Já existe um estande com este nome para este evento");
        }
        return estandeRepository.save(estande);
    }

    public Estande updateEstande(Estande estande) {
        return estandeRepository.save(estande);
    }

    public Estande findEstandeById(UUID id) {
        return estandeRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new NotFoundException("Estande não encontrado"));
    }

    public List<Estande> findEstandesByEvento(UUID eventoId) {
        return estandeRepository.findAllByEventoIdOrderByCreatedAtDesc(eventoId);
    }

    public void deleteEstande(Estande estande) {
        estande.setDeletedAt(LocalDateTime.now());
        estandeRepository.save(estande);
    }
}