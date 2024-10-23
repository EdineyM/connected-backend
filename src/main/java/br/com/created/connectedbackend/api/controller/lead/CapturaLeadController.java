package br.com.created.connectedbackend.api.controller.lead;

import br.com.created.connectedbackend.api.dto.request.lead.CreateCapturaLeadRequest;
import br.com.created.connectedbackend.api.dto.response.lead.CapturaLeadResponse;
import br.com.created.connectedbackend.domain.service.lead.CapturaLeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/capturas")
@RequiredArgsConstructor
@Tag(name = "Capturas", description = "API para gerenciamento de capturas de leads")
public class CapturaLeadController {

    private final CapturaLeadService capturaLeadService;

    @PostMapping
    @Operation(summary = "Criar nova captura")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPRESENTANTE', 'ATENDENTE')")
    public ResponseEntity<CapturaLeadResponse> createCaptura(
            @Valid @RequestBody CreateCapturaLeadRequest request) {

        var captura = capturaLeadService.createCaptura(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CapturaLeadResponse.fromEntity(captura));
    }

    @GetMapping("/lead/{leadId}")
    @Operation(summary = "Listar capturas por lead")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPRESENTANTE', 'ATENDENTE')")
    public ResponseEntity<Page<CapturaLeadResponse>> getCapturasByLead(
            @PathVariable UUID leadId,
            Pageable pageable) {

        var capturas = capturaLeadService.findCapturasByLead(leadId, pageable)
                .map(CapturaLeadResponse::fromEntity);
        return ResponseEntity.ok(capturas);
    }

    @GetMapping("/lead/{leadId}/tipo/{tipoInteracao}")
    @Operation(summary = "Listar capturas por tipo de interação")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPRESENTANTE', 'ATENDENTE')")
    public ResponseEntity<List<CapturaLeadResponse>> getCapturasByTipoInteracao(
            @PathVariable UUID leadId,
            @PathVariable String tipoInteracao) {

        var capturas = capturaLeadService.findCapturasByTipoInteracao(leadId, tipoInteracao)
                .stream()
                .map(CapturaLeadResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(capturas);
    }

    @GetMapping("/tipos/{leadId}")
    @Operation(summary = "Listar tipos de interação por lead")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPRESENTANTE', 'ATENDENTE')")
    public ResponseEntity<List<String>> getTiposInteracao(@PathVariable UUID leadId) {
        var tipos = capturaLeadService.findTiposInteracaoByLead(leadId);
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/event/{eventoId}")
    @Operation(summary = "Listar capturas por evento")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPRESENTANTE')")
    public ResponseEntity<List<CapturaLeadResponse>> getCapturasByEvento(
            @PathVariable UUID eventoId) {

        var capturas = capturaLeadService.findCapturasByEvento(eventoId)
                .stream()
                .map(CapturaLeadResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(capturas);
    }
}