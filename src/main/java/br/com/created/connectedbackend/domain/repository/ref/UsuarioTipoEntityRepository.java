package br.com.created.connectedbackend.domain.repository.ref;

import br.com.created.connectedbackend.domain.model.ref.UsuarioTipoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioTipoEntityRepository extends JpaRepository<UsuarioTipoEntity, Short> {
}