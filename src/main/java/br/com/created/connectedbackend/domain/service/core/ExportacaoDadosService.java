package br.com.created.connectedbackend.domain.service.core;

import br.com.created.connectedbackend.domain.exception.BusinessException;
import br.com.created.connectedbackend.domain.exception.NotFoundException;
import br.com.created.connectedbackend.domain.model.core.ExportacaoDados;
import br.com.created.connectedbackend.domain.repository.core.ExportacaoDadosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExportacaoDadosService {

    private final ExportacaoDadosRepository exportacaoDadosRepository;

    public ExportacaoDados createExportacao(ExportacaoDados exportacao) {
        if (exportacaoDadosRepository.existsByNomeArquivoAndEventoIdAndDeletedAtIsNull(
                exportacao.getNomeArquivo(), exportacao.getEvento().getId())) {
            throw new BusinessException("Já existe uma exportação com este nome de arquivo para este evento");
        }
        return exportacaoDadosRepository.save(exportacao);
    }

    public ExportacaoDados updateExportacao(ExportacaoDados exportacao) {
        return exportacaoDadosRepository.save(exportacao);
    }

    public ExportacaoDados findExportacaoById(UUID id) {
        return exportacaoDadosRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new NotFoundException("Exportação de dados não encontrada"));
    }

    public List<ExportacaoDados> findExportacoesByEvento(UUID eventoId) {
        return exportacaoDadosRepository.findAllByEventoIdOrderByDataSolicitacaoDesc(eventoId);
    }

    public void deleteExportacao(ExportacaoDados exportacao) {
        exportacao.setDeletedAt(LocalDateTime.now());
        exportacaoDadosRepository.save(exportacao);
    }
}