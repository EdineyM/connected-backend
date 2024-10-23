package br.com.created.connectedbackend.domain.repository.event;

import br.com.created.connectedbackend.domain.model.event.FeedbackEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeedbackEventoRepository extends JpaRepository<FeedbackEventoEntity, UUID> {

    @Query("SELECT f FROM FeedbackEventoEntity f " +
            "WHERE f.evento.id = :eventoId " +
            "AND f.ativo = true " +
            "ORDER BY f.createdAt DESC")
    List<FeedbackEventoEntity> findAllByEventoIdAndActiveOrderByCreatedAtDesc(
            @Param("eventoId") UUID eventoId
    );

    @Query("SELECT f FROM FeedbackEventoEntity f " +
            "WHERE f.id = :id " +
            "AND f.ativo = true")
    Optional<FeedbackEventoEntity> findByIdAndActive(
            @Param("id") UUID id
    );

    boolean existsByEventoIdAndUsuarioIdAndActiveTrueAndDeletedAtIsNull(
            UUID eventoId, UUID usuarioId
    );
}