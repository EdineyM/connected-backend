package br.com.created.connectedbackend.domain.service.event;

import br.com.created.connectedbackend.domain.exception.BusinessException;
import br.com.created.connectedbackend.domain.exception.NotFoundException;
import br.com.created.connectedbackend.domain.model.event.Palestra;
import br.com.created.connectedbackend.domain.repository.event.PalestraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PalestraService {

    private final PalestraRepository palestraRepository;

    public Palestra createPalestra(Palestra palestra) {
        if (palestraRepository.existsByTituloAndEventoIdAndDeletedAtIsNull(
                palestra.getTitulo(), palestra.getEvento().getId())) {
            throw new BusinessException("Já existe uma palestra com este título para este evento");
        }
        return palestraRepository.save(palestra);
    }

    public Palestra updatePalestra(Palestra palestra) {
        return palestraRepository.save(palestra);
    }

    public Palestra findPalestraById(UUID id) {
        return palestraRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new NotFoundException("Palestra não encontrada"));
    }

    public List<Palestra> findPalestrasByEvento(UUID eventoId) {
        return palestraRepository.findAllByEventoIdOrderByDataHoraInicioDesc(eventoId);
    }

    public void deletePalestra(Palestra palestra) {
        palestra.setDeletedAt(LocalDateTime.now());
        palestraRepository.save(palestra);
    }
}