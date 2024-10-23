package br.com.created.connectedbackend.api.controller.lead;

import br.com.created.connectedbackend.api.dto.request.lead.CreateLeadRequest;
import br.com.created.connectedbackend.api.dto.request.lead.UpdateLeadRequest;
import br.com.created.connectedbackend.api.dto.response.lead.LeadResponse;
import br.com.created.connectedbackend.domain.model.user.User;
import br.com.created.connectedbackend.domain.service.lead.LeadService;
import br.com.created.connectedbackend.infrastructure.security.CurrentUser;
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
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
@Tag(name = "Leads", description = "API para gerenciamento de leads")
public class LeadController {

    private final LeadService leadService;

    @PostMapping
    @Operation(summary = "Criar novo lead")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPRESENTANTE', 'ATENDENTE')")
    public ResponseEntity<LeadResponse> createLead(
            @Valid @RequestBody CreateLeadRequest request,
            @CurrentUser User currentUser) {

        var lead = leadService.createLead(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(LeadResponse.fromEntity(lead));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar lead existente")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPRESENTANTE', 'ATENDENTE')")
    public ResponseEntity<LeadResponse> updateLead(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateLeadRequest request) {

        var lead = leadService.updateLead(id, request);
        return ResponseEntity.ok(LeadResponse.fromEntity(lead));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar lead")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPRESENTANTE')")
    public ResponseEntity<Void> deleteLead(@PathVariable UUID id) {
        leadService.deleteLead(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar lead por ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPRESENTANTE', 'ATENDENTE')")
    public ResponseEntity<LeadResponse> getLeadById(@PathVariable UUID id) {
        var lead = leadService.findLeadById(id);
        return ResponseEntity.ok(LeadResponse.fromEntity(lead));
    }

    @GetMapping("/user")
    @Operation(summary = "Listar leads do usuário atual")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPRESENTANTE', 'ATENDENTE')")
    public ResponseEntity<Page<LeadResponse>> getLeadsByCurrentUser(
            @CurrentUser User currentUser,
            Pageable pageable) {

        var leads = leadService.findLeadsByCurrentUser(currentUser.getId(), pageable)
                .map(LeadResponse::fromEntity);
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/event/{eventoId}")
    @Operation(summary = "Listar leads por evento")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPRESENTANTE')")
    public ResponseEntity<List<LeadResponse>> getLeadsByEvento(@PathVariable UUID eventoId) {
        var leads = leadService.findLeadsByEvento(eventoId)
                .stream()
                .map(LeadResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/status")
    @Operation(summary = "Listar status de leads do usuário")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPRESENTANTE', 'ATENDENTE')")
    public ResponseEntity<List<Short>> getLeadStatusByUser(@CurrentUser User currentUser) {
        var status = leadService.findLeadStatusByUser(currentUser.getId());
        return ResponseEntity.ok(status);
    }
}