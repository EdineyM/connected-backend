package br.com.created.connectedbackend.domain.repository.ref;

import br.com.created.connectedbackend.domain.model.ref.EventoTipoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoTipoEntityRepository extends JpaRepository<EventoTipoEntity, Short> {
}