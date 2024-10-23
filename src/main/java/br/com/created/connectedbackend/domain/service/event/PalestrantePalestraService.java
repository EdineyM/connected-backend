package br.com.created.connectedbackend.domain.service.event;

import br.com.created.connectedbackend.domain.exception.BusinessException;
import br.com.created.connectedbackend.domain.exception.NotFoundException;
import br.com.created.connectedbackend.domain.model.event.PalestrantePalestraEntity;
import br.com.created.connectedbackend.domain.repository.event.PalestrantePalestraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PalestrantePalestraService {

    private final PalestrantePalestraRepository palestrantePalestraRepository;

    public PalestrantePalestraEntity createPalestrantePalestra(PalestrantePalestraEntity palestrantePalestra) {
        if (palestrantePalestraRepository.existsByPalestraIdAndPalestranteId(
                palestrantePalestra.getPalestra().getId(), palestrantePalestra.getPalestrante().getId())) {
            throw new BusinessException("Este palestrante já está cadastrado para esta palestra");
        }
        return palestrantePalestraRepository.save(palestrantePalestra);
    }

    public PalestrantePalestraEntity updatePalestrantePalestra(PalestrantePalestraEntity palestrantePalestra) {
        return palestrantePalestraRepository.save(palestrantePalestra);
    }

    public PalestrantePalestraEntity findPalestrantePalestraById(UUID id) {
        return palestrantePalestraRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Palestrante-palestra não encontrado"));
    }

    public List<PalestrantePalestraEntity> findPalestrantesPalestraByPalestra(UUID palestraId) {
        return palestrantePalestraRepository.findAllByPalestraIdOrderByCreatedAtDesc(palestraId);
    }

    public void deletePalestrantePalestra(PalestrantePalestraEntity palestrantePalestra) {
        palestrantePalestraRepository.delete(palestrantePalestra);
    }
}