package br.com.created.connectedbackend.domain.repository.ref;

import br.com.created.connectedbackend.domain.model.ref.EventoStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoStatusEntityRepository extends JpaRepository<EventoStatusEntity, Short> {
}