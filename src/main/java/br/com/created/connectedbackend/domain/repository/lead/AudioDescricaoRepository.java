package br.com.created.connectedbackend.domain.repository.lead;

import br.com.created.connectedbackend.domain.model.lead.AudioDescricao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AudioDescricaoRepository extends JpaRepository<AudioDescricao, UUID> {

    @Query("SELECT ad FROM AudioDescricao ad WHERE ad.lead.id = :leadId " +
            "ORDER BY ad.createdAt DESC")
    Page<AudioDescricao> findAllByLeadId(
            @Param("leadId") UUID leadId,
            Pageable pageable
    );

    @Query("SELECT ad FROM AudioDescricao ad " +
            "WHERE ad.lead.id = :leadId " +
            "AND ad.arquivado = :arquivado " +
            "ORDER BY ad.createdAt DESC")
    List<AudioDescricao> findAllByLeadIdAndArquivado(
            @Param("leadId") UUID leadId,
            @Param("arquivado") Boolean arquivado
    );

    @Query("SELECT ad FROM AudioDescricao ad " +
            "WHERE ad.lead.id = :leadId " +
            "AND ad.transcricao IS NOT NULL " +
            "ORDER BY ad.createdAt DESC")
    List<AudioDescricao> findAllByLeadIdWithTranscricao(@Param("leadId") UUID leadId);

    @Query("SELECT ad FROM AudioDescricao ad " +
            "WHERE ad.lead.ingressoParticipante.evento.id = :eventoId " +
            "ORDER BY ad.createdAt DESC")
    List<AudioDescricao> findAllByEventoId(@Param("eventoId") UUID eventoId);

    @Modifying
    @Query("UPDATE AudioDescricao ad SET ad.arquivado = :arquivado " +
            "WHERE ad.id = :audioId")
    void updateArquivadoStatus(
            @Param("audioId") UUID audioId,
            @Param("arquivado") Boolean arquivado
    );

    @Query("SELECT ad FROM AudioDescricao ad " +
            "WHERE ad.lead.id = :leadId " +
            "AND ad.id = :audioId " +
            "AND ad.arquivado = false")
    Optional<AudioDescricao> findByIdAndLeadIdAndNotArchived(
            @Param("audioId") UUID audioId,
            @Param("leadId") UUID leadId
    );

    @Query("SELECT COUNT(ad) FROM AudioDescricao ad " +
            "WHERE ad.lead.id = :leadId AND ad.arquivado = false")
    Long countActiveByLeadId(@Param("leadId") UUID leadId);

    @Query("SELECT DISTINCT ad.urlS3 FROM AudioDescricao ad " +
            "WHERE ad.lead.ingressoParticipante.evento.id = :eventoId " +
            "AND ad.arquivado = false")
    List<String> findAllActiveAudioUrlsByEventoId(@Param("eventoId") UUID eventoId);
}