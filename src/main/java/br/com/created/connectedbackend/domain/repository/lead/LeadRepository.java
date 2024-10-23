package br.com.created.connectedbackend.domain.repository.lead;

import br.com.created.connectedbackend.domain.model.lead.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID> {

    @Query("SELECT l FROM Lead l WHERE l.deletedAt IS NULL AND l.user.id = :userId")
    Page<Lead> findAllByUserIdAndNotDeleted(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT l FROM Lead l " +
            "LEFT JOIN FETCH l.ingressoParticipante ip " +
            "LEFT JOIN FETCH l.user u " +
            "WHERE l.id = :id AND l.deletedAt IS NULL")
    Optional<Lead> findByIdWithRelationships(@Param("id") UUID id);

    @Query("SELECT l FROM Lead l " +
            "WHERE l.ingressoParticipante.evento.id = :eventoId " +
            "AND l.deletedAt IS NULL")
    List<Lead> findAllByEventoId(@Param("eventoId") UUID eventoId);

    @Query("SELECT COUNT(l) FROM Lead l " +
            "WHERE l.ingressoParticipante.evento.id = :eventoId " +
            "AND l.deletedAt IS NULL")
    Long countLeadsByEventoId(@Param("eventoId") UUID eventoId);

    @Query("SELECT l FROM Lead l " +
            "WHERE l.statusId = :statusId " +
            "AND l.deletedAt IS NULL")
    Page<Lead> findAllByStatusId(@Param("statusId") Short statusId, Pageable pageable);

    @Query("SELECT l FROM Lead l " +
            "WHERE l.user.id = :userId " +
            "AND l.ingressoParticipante.evento.id = :eventoId " +
            "AND l.deletedAt IS NULL")
    List<Lead> findAllByUserIdAndEventoId(
            @Param("userId") UUID userId,
            @Param("eventoId") UUID eventoId
    );

    @Query("SELECT DISTINCT l.statusId FROM Lead l " +
            "WHERE l.user.id = :userId " +
            "AND l.deletedAt IS NULL")
    List<Short> findDistinctStatusByUserId(@Param("userId") UUID userId);

    boolean existsByIngressoParticipanteIdAndDeletedAtIsNull(UUID ingressoId);
}