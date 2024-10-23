package br.com.created.connectedbackend.domain.repository.lead;

import br.com.created.connectedbackend.domain.model.lead.CapturaLead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CapturaLeadRepository extends JpaRepository<CapturaLead, UUID> {

    @Query("SELECT cl FROM CapturaLead cl WHERE cl.lead.id = :leadId " +
            "ORDER BY cl.createdAt DESC")
    Page<CapturaLead> findAllByLeadId(
            @Param("leadId") UUID leadId,
            Pageable pageable
    );

    @Query("SELECT cl FROM CapturaLead cl " +
            "WHERE cl.lead.id = :leadId " +
            "AND cl.tipoInteracao = :tipoInteracao " +
            "ORDER BY cl.createdAt DESC")
    List<CapturaLead> findAllByLeadIdAndTipoInteracao(
            @Param("leadId") UUID leadId,
            @Param("tipoInteracao") String tipoInteracao
    );

    @Query("SELECT COUNT(cl) FROM CapturaLead cl " +
            "WHERE cl.lead.id = :leadId " +
            "AND cl.createdAt BETWEEN :inicio AND :fim")
    Long countCapturasByLeadIdAndPeriodo(
            @Param("leadId") UUID leadId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    @Query("SELECT cl FROM CapturaLead cl " +
            "WHERE cl.lead.ingressoParticipante.evento.id = :eventoId " +
            "ORDER BY cl.createdAt DESC")
    List<CapturaLead> findAllByEventoId(@Param("eventoId") UUID eventoId);

    @Query("SELECT DISTINCT cl.tipoInteracao FROM CapturaLead cl " +
            "WHERE cl.lead.id = :leadId")
    List<String> findDistinctTipoInteracaoByLeadId(@Param("leadId") UUID leadId);

    @Query("SELECT NEW br.com.created.connectedbackend.api.dto.response.lead.CapturaSummaryDTO(" +
            "cl.tipoInteracao, COUNT(cl)) " +
            "FROM CapturaLead cl " +
            "WHERE cl.lead.ingressoParticipante.evento.id = :eventoId " +
            "GROUP BY cl.tipoInteracao")
    List<CapturaSummaryDTO> summarizeCapturasByEvento(@Param("eventoId") UUID eventoId);
}