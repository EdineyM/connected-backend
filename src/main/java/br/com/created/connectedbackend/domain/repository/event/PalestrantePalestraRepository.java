package br.com.created.connectedbackend.domain.repository.event;

import br.com.created.connectedbackend.domain.model.event.PalestrantePalestraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PalestrantePalestraRepository extends JpaRepository<PalestrantePalestraEntity, UUID> {

    @Query("SELECT pp FROM PalestrantePalestraEntity pp " +
            "WHERE pp.palestra.id = :palestraId " +
            "ORDER BY pp.createdAt DESC")
    List<PalestrantePalestraEntity> findAllByPalestraIdOrderByCreatedAtDesc(
            @Param("palestraId") UUID palestraId
    );

    @Query("SELECT pp FROM PalestrantePalestra pp " +
            "WHERE pp.id = :id")
    Optional<PalestrantePalestraEntity> findById(
            @Param("id") UUID id
    );

    boolean existsByPalestraIdAndPalestranteId(
            UUID palestraId, UUID palestranteId
    );
}