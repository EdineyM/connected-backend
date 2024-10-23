package br.com.created.connectedbackend.domain.service.event;

import br.com.created.connectedbackend.domain.exception.BusinessException;
import br.com.created.connectedbackend.domain.exception.NotFoundException;
import br.com.created.connectedbackend.domain.model.event.FeedbackEventoEntity;
import br.com.created.connectedbackend.domain.repository.event.FeedbackEventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackEventoService {

    private final FeedbackEventoRepository feedbackEventoRepository;

    public FeedbackEventoEntity createFeedback(FeedbackEventoEntity feedback) {
        if (feedbackEventoRepository.existsByEventoIdAndUsuarioIdAndActiveTrueAndDeletedAtIsNull(
                feedback.getEvento().getId(), feedback.getUsuario().getId())) {
            throw new BusinessException("Você já enviou um feedback para este evento.");
        }
        return feedbackEventoRepository.save(feedback);
    }

    public FeedbackEventoEntity updateFeedback(FeedbackEventoEntity feedback) {
        return feedbackEventoRepository.save(feedback);
    }

    public FeedbackEventoEntity findFeedbackById(UUID id) {
        return feedbackEventoRepository.findByIdAndActive(id)
                .orElseThrow(() -> new NotFoundException("Feedback não encontrado"));
    }

    public List<FeedbackEventoEntity> findFeedbacksByEvento(UUID eventoId) {
        return feedbackEventoRepository.findAllByEventoIdAndActiveOrderByCreatedAtDesc(eventoId);
    }

    public void deleteFeedback(FeedbackEventoEntity feedback) {
        feedback.setAtivo(false);
        feedbackEventoRepository.save(feedback);
    }
}