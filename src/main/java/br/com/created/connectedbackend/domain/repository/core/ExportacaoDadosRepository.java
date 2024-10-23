package br.com.created.connectedbackend.domain.repository.core;

import br.com.created.connectedbackend.domain.model.core.ExportacaoDados;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExportacaoDadosRepository extends JpaRepository<ExportacaoDados, UUID> {

    @Query("SELECT e FROM ExportacaoDados e " +
            "WHERE e.evento.id = :eventoId " +
            "AND e.deletedAt IS NULL " +
            "ORDER BY e.dataSolicitacao DESC")
    List<ExportacaoDados> findAllByEventoIdOrderByDataSolicitacaoDesc(
            @Param("eventoId") UUID eventoId
    );

    @Query("SELECT e FROM ExportacaoDados e " +
            "WHERE e.id = :id " +
            "AND e.deletedAt IS NULL")
    Optional<ExportacaoDados> findByIdAndNotDeleted(
            @Param("id") UUID id
    );

    boolean existsByNomeArquivoAndEventoIdAndDeletedAtIsNull(
            String nomeArquivo, UUID eventoId
    );
}