package br.com.created.connectedbackend.domain.repository.ref;

import br.com.created.connectedbackend.domain.model.ref.ExportacaoStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExportacaoStatusEntityRepository extends JpaRepository<ExportacaoStatusEntity, Short> {
}