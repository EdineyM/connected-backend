package br.com.created.connectedbackend.domain.repository.event;

import br.com.created.connectedbackend.domain.model.event.Estande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EstandeRepository extends JpaRepository<Estande, UUID> {

    @Query("SELECT e FROM Estande e " +
            "WHERE e.evento.id = :eventoId " +
            "AND e.deletedAt IS NULL " +
            "ORDER BY e.createdAt DESC")
    List<Estande> findAllByEventoIdOrderByCreatedAtDesc(
            @Param("eventoId") UUID eventoId
    );

    @Query("SELECT e FROM Estande e " +
            "WHERE e.id = :id " +
            "AND e.deletedAt IS NULL")
    Optional<Estande> findByIdAndNotDeleted(
            @Param("id") UUID id
    );

    boolean existsByNomeAndEventoIdAndDeletedAtIsNull(
            String nome, UUID eventoId
    );
}