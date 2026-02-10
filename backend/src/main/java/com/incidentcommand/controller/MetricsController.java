package com.incidentcommand.controller;

import com.incidentcommand.dto.MetricsResponse;
import com.incidentcommand.service.MetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Metrics", description = "Incident metrics and analytics")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping
    @Operation(summary = "Get incident metrics (MTTR, counts by severity/status/team)")
    public ResponseEntity<MetricsResponse> getMetrics() {
        return ResponseEntity.ok(metricsService.getMetrics());
    }
}
