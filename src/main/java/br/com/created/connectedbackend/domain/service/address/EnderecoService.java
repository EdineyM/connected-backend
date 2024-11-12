package br.com.created.connectedbackend.domain.service.address;

import br.com.created.connectedbackend.domain.exception.NotFoundException;
import br.com.created.connectedbackend.domain.model.address.Endereco;
import br.com.created.connectedbackend.domain.repository.address.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;

    @Transactional
    public Endereco createEndereco(Endereco endereco) {
        endereco.setCreatedAt(LocalDateTime.now());
        return enderecoRepository.save(endereco);
    }

    @Transactional
    public Endereco updateEndereco(Endereco endereco) {
        var existingEndereco = findById(endereco.getId());
        endereco.setCreatedAt(existingEndereco.getCreatedAt());
        endereco.setUpdatedAt(LocalDateTime.now());
        return enderecoRepository.save(endereco);
    }

    @Transactional(readOnly = true)
    public Endereco findById(UUID id) {
        return enderecoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Endereço não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Endereco> findAll() {
        return enderecoRepository.findAll();
    }

    @Transactional
    public void deleteEndereco(UUID id) {
        var endereco = findById(id);
        enderecoRepository.delete(endereco);
    }
}