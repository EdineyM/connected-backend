package br.com.created.connectedbackend.domain.repository.core;

import br.com.created.connectedbackend.domain.model.core.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    @Query("SELECT u FROM Usuario u " +
            "WHERE u.deletedAt IS NULL AND u.ativo = true " +
            "AND (u.id = :id OR u.email = :email OR u.nome LIKE %:termo%)")
    Optional<Usuario> findByIdOrEmailOrNameContaining(
            @Param("id") UUID id,
            @Param("email") String email,
            @Param("termo") String termo
    );

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmailAndDeletedAtIsNull(String email);
}