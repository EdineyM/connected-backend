package br.com.created.connectedbackend.domain.repository.event;

import br.com.created.connectedbackend.domain.model.event.UsuarioEstandeEntity;
import br.com.created.connectedbackend.domain.model.event.UsuarioEstandeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioEstandeRepository extends JpaRepository<UsuarioEstandeEntity, UsuarioEstandeId> {

    @Query("SELECT ue FROM UsuarioEstandeEntity ue " +
            "WHERE ue.estande.id = :estandeId")
    List<UsuarioEstandeId> findAllByEstandeId(
            @Param("estandeId") UUID estandeId
    );

    @Query("SELECT ue FROM UsuarioEstande ue " +
            "WHERE ue.usuario.id = :usuarioId " +
            "AND ue.estande.id = :estandeId")
    Optional<UsuarioEstandeEntity> findByUsuarioIdAndEstandeId(
            @Param("usuarioId") UUID usuarioId,
            @Param("estandeId") UUID estandeId
    );
}