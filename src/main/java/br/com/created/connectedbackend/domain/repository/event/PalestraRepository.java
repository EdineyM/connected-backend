package br.com.created.connectedbackend.domain.repository.event;

import br.com.created.connectedbackend.domain.model.event.Palestra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PalestraRepository extends JpaRepository<Palestra, UUID> {

    @Query("SELECT p FROM Palestra p " +
            "WHERE p.evento.id = :eventoId " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.dataHoraInicio DESC")
    List<Palestra> findAllByEventoIdOrderByDataHoraInicioDesc(
            @Param("eventoId") UUID eventoId
    );

    @Query("SELECT p FROM Palestra p " +
            "WHERE p.id = :id " +
            "AND p.deletedAt IS NULL")
    Optional<Palestra> findByIdAndNotDeleted(
            @Param("id") UUID id
    );

    boolean existsByTituloAndEventoIdAndDeletedAtIsNull(
            String titulo, UUID eventoId
    );
}