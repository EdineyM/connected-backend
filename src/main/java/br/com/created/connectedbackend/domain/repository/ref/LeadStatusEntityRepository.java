package br.com.created.connectedbackend.domain.repository.ref;

import br.com.created.connectedbackend.domain.model.ref.LeadStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadStatusEntityRepository extends JpaRepository<LeadStatusEntity, Short> {
}