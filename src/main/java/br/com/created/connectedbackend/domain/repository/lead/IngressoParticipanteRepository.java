package br.com.created.connectedbackend.domain.repository.lead;

import br.com.created.connectedbackend.domain.model.lead.IngressoParticipante;
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
public interface IngressoParticipanteRepository extends JpaRepository<IngressoParticipante, UUID> {

    @Query("SELECT ip FROM IngressoParticipante ip " +
            "WHERE ip.deletedAt IS NULL AND ip.evento.id = :eventoId")
    Page<IngressoParticipante> findAllByEventoId(
            @Param("eventoId") UUID eventoId,
            Pageable pageable
    );

    @Query("SELECT ip FROM IngressoParticipante ip " +
            "LEFT JOIN FETCH ip.evento e " +
            "WHERE ip.id = :id AND ip.deletedAt IS NULL")
    Optional<IngressoParticipante> findByIdWithEvento(@Param("id") UUID id);

    @Query("SELECT ip FROM IngressoParticipante ip " +
            "WHERE ip.cpf = :cpf AND ip.evento.id = :eventoId AND ip.deletedAt IS NULL")
    Optional<IngressoParticipante> findByCpfAndEventoId(
            @Param("cpf") String cpf,
            @Param("eventoId") UUID eventoId
    );

    @Query("SELECT ip FROM IngressoParticipante ip " +
            "WHERE ip.qrCode = :qrCode AND ip.deletedAt IS NULL")
    Optional<IngressoParticipante> findByQrCode(@Param("qrCode") String qrCode);

    @Query("SELECT ip FROM IngressoParticipante ip " +
            "WHERE ip.email = :email AND ip.evento.id = :eventoId AND ip.deletedAt IS NULL")
    Optional<IngressoParticipante> findByEmailAndEventoId(
            @Param("email") String email,
            @Param("eventoId") UUID eventoId
    );

    @Query("SELECT COUNT(ip) FROM IngressoParticipante ip " +
            "WHERE ip.evento.id = :eventoId AND ip.deletedAt IS NULL")
    Long countByEventoId(@Param("eventoId") UUID eventoId);

    @Query("SELECT DISTINCT ip.porteEmpresa FROM IngressoParticipante ip " +
            "WHERE ip.evento.id = :eventoId AND ip.deletedAt IS NULL " +
            "AND ip.porteEmpresa IS NOT NULL")
    List<String> findDistinctPorteEmpresaByEventoId(@Param("eventoId") UUID eventoId);

    @Query("SELECT DISTINCT ip.setor FROM IngressoParticipante ip " +
            "WHERE ip.evento.id = :eventoId AND ip.deletedAt IS NULL " +
            "AND ip.setor IS NOT NULL")
    List<String> findDistinctSetorByEventoId(@Param("eventoId") UUID eventoId);

    boolean existsByCpfAndEventoIdAndDeletedAtIsNull(String cpf, UUID eventoId);

    boolean existsByEmailAndEventoIdAndDeletedAtIsNull(String email, UUID eventoId);
}