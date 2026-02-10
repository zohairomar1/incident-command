package com.incidentcommand.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricsResponse {
    private double meanTimeToResolveMinutes;
    private long totalIncidents;
    private long openIncidents;
    private long resolvedIncidents;
    private Map<String, Long> countBySeverity;
    private Map<String, Long> countByStatus;
    private Map<String, Long> countByTeam;
}
