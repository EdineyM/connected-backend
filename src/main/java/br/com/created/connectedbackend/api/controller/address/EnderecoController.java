package br.com.created.connectedbackend.api.controller.address;

import br.com.created.connectedbackend.api.dto.request.address.CreateEnderecoRequest;
import br.com.created.connectedbackend.api.dto.request.address.UpdateEnderecoRequest;
import br.com.created.connectedbackend.api.dto.response.address.EnderecoResponse;
import br.com.created.connectedbackend.domain.service.address.EnderecoService;
import br.com.created.connectedbackend.infrastructure.config.mapper.EnderecoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/enderecos")
@RequiredArgsConstructor
@Tag(name = "Endereços", description = "API para gerenciamento de endereços")
public class EnderecoController {

    private final EnderecoService enderecoService;
    private final EnderecoMapper enderecoMapper;

    @PostMapping
    @Operation(summary = "Criar novo endereço")
    public ResponseEntity<EnderecoResponse> createEndereco(@Valid @RequestBody CreateEnderecoRequest request) {
        var endereco = enderecoMapper.toEndereco(request);
        var savedEndereco = enderecoService.createEndereco(endereco);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enderecoMapper.toEnderecoResponse(savedEndereco));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar endereço existente")
    public ResponseEntity<EnderecoResponse> updateEndereco(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEnderecoRequest request) {
        var endereco = enderecoService.findById(id);
        enderecoMapper.updateEnderecoFromRequest(request, endereco);
        var updatedEndereco = enderecoService.updateEndereco(endereco);
        return ResponseEntity.ok(enderecoMapper.toEnderecoResponse(updatedEndereco));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar endereço por ID")
    public ResponseEntity<EnderecoResponse> getEnderecoById(@PathVariable UUID id) {
        var endereco = enderecoService.findById(id);
        return ResponseEntity.ok(enderecoMapper.toEnderecoResponse(endereco));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar endereço")
    public ResponseEntity<Void> deleteEndereco(@PathVariable UUID id) {
        enderecoService.deleteEndereco(id);
        return ResponseEntity.noContent().build();
    }
}