package com.incidentcommand.controller;

import com.incidentcommand.service.EscalationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/escalations")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Escalations", description = "Trigger incident escalations")
public class EscalationController {

    private final EscalationService escalationService;

    public EscalationController(EscalationService escalationService) {
        this.escalationService = escalationService;
    }

    @PostMapping("/trigger")
    @Operation(summary = "Trigger escalation for an incident using a specific policy")
    public ResponseEntity<String> triggerEscalation(@RequestParam Long incidentId, @RequestParam Long policyId) {
        escalationService.triggerEscalation(incidentId, policyId);
        return ResponseEntity.ok("Escalation triggered successfully");
    }
}
