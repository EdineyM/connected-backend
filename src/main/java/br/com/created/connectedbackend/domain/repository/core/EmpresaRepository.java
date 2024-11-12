package br.com.created.connectedbackend.domain.repository.core;

import br.com.created.connectedbackend.domain.model.core.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {

    boolean existsByCnpjAndDeletedAtIsNull(String cnpj);

    List<Empresa> findByAtivoTrueAndDeletedAtIsNull();
}