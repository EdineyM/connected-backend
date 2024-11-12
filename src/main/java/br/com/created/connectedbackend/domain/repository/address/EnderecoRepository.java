package br.com.created.connectedbackend.domain.repository.address;

import br.com.created.connectedbackend.domain.model.address.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {
}