package br.com.created.connectedbackend.api.controller.core;

import br.com.created.connectedbackend.api.dto.request.core.CreateEmpresaRequest;
import br.com.created.connectedbackend.api.dto.request.core.UpdateEmpresaRequest;
import br.com.created.connectedbackend.api.dto.response.core.EmpresaResponse;
import br.com.created.connectedbackend.domain.service.core.EmpresaService;
import br.com.created.connectedbackend.infrastructure.config.mapper.EmpresaMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/empresas")
@RequiredArgsConstructor
@Tag(name = "Empresas", description = "API para gerenciamento de empresas")
public class EmpresaController {

    private final EmpresaService empresaService;
    private final EmpresaMapper empresaMapper;

    @PostMapping
    @Operation(summary = "Criar nova empresa")
    public ResponseEntity<EmpresaResponse> createEmpresa(@Valid @RequestBody CreateEmpresaRequest request) {
        var empresa = empresaMapper.toEmpresa(request);
        var savedEmpresa = empresaService.createEmpresa(empresa);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(empresaMapper.toEmpresaResponse(savedEmpresa));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar empresa existente")
    public ResponseEntity<EmpresaResponse> updateEmpresa(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmpresaRequest request) {
        var empresa = empresaService.findById(id);
        empresaMapper.updateEmpresaFromRequest(request, empresa);
        var updatedEmpresa = empresaService.updateEmpresa(empresa);
        return ResponseEntity.ok(empresaMapper.toEmpresaResponse(updatedEmpresa));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar empresa por ID")
    public ResponseEntity<EmpresaResponse> getEmpresaById(@PathVariable UUID id) {
        var empresa = empresaService.findById(id);
        return ResponseEntity.ok(empresaMapper.toEmpresaResponse(empresa));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar empresa")
    public ResponseEntity<Void> deleteEmpresa(@PathVariable UUID id) {
        empresaService.deleteEmpresa(id);
        return ResponseEntity.noContent().build();
    }
}