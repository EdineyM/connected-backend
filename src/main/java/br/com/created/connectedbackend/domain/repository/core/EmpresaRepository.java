package br.com.created.connectedbackend.domain.repository.core;

import br.com.created.connectedbackend.domain.model.core.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {

    @Query("SELECT e FROM Empresa e " +
            "WHERE e.deletedAt IS NULL AND e.ativo = true " +
            "AND (e.id = :id OR e.cnpj = :cnpj OR e.razaoSocial LIKE %:termo%)")
    Optional<Empresa> findByIdOrCnpjOrRazaoSocialContaining(
            @Param("id") UUID id,
            @Param("cnpj") String cnpj,
            @Param("termo") String termo
    );

    boolean existsByCnpjAndDeletedAtIsNull(String cnpj);
}