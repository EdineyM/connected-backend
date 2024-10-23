package br.com.created.connectedbackend.domain.service.address;

import br.com.created.connectedbackend.domain.model.address.Endereco;
import br.com.created.connectedbackend.domain.repository.address.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;

    public Endereco createEndereco(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    public Endereco updateEndereco(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    public Optional<Endereco> findById(UUID id) {
        return enderecoRepository.findById(id);
    }

    public void deleteEndereco(Endereco endereco) {
        enderecoRepository.delete(endereco);
    }
}