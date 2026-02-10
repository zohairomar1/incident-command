package com.incidentcommand.controller;

import com.incidentcommand.dto.*;
import com.incidentcommand.model.Incident;
import com.incidentcommand.model.enums.IncidentStatus;
import com.incidentcommand.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Incidents", description = "Incident CRUD operations")
@Transactional
public class IncidentController {

    private final IncidentService incidentService;
    private final IncidentMapper incidentMapper;

    public IncidentController(IncidentService incidentService, IncidentMapper incidentMapper) {
        this.incidentService = incidentService;
        this.incidentMapper = incidentMapper;
    }

    @GetMapping
    @Operation(summary = "Get all incidents")
    public ResponseEntity<List<IncidentResponse>> getAll() {
        List<IncidentResponse> incidents = incidentService.findAll().stream()
                .map(incidentMapper::toResponse)
                .toList();
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get incident by ID")
    public ResponseEntity<IncidentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentMapper.toResponse(incidentService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new incident")
    public ResponseEntity<IncidentResponse> create(@Valid @RequestBody CreateIncidentRequest request,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        Incident incident = incidentService.createIncident(request, userDetails.getUsername());
        return ResponseEntity.ok(incidentMapper.toResponse(incident));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an incident")
    public ResponseEntity<IncidentResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateIncidentRequest request,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        Incident incident = incidentService.updateIncident(id, request, userDetails.getUsername());
        return ResponseEntity.ok(incidentMapper.toResponse(incident));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update incident status")
    public ResponseEntity<IncidentResponse> updateStatus(@PathVariable Long id,
                                                          @RequestParam IncidentStatus status,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        Incident incident = incidentService.updateIncidentStatus(id, status, userDetails.getUsername());
        return ResponseEntity.ok(incidentMapper.toResponse(incident));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an incident (admin only)")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        incidentService.deleteIncident(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
