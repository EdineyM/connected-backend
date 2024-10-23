package br.com.created.connectedbackend.domain.repository.event;

import br.com.created.connectedbackend.domain.model.event.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventoRepository extends JpaRepository<Evento, UUID> {

    @Query("SELECT e FROM Evento e " +
            "WHERE e.deletedAt IS NULL " +
            "AND (e.id = :id OR e.nome LIKE %:termo%)")
    Optional<Evento> findByIdOrNameContaining(
            @Param("id") UUID id,
            @Param("termo") String termo
    );

    boolean existsByNomeAndDeletedAtIsNull(String nome);
}