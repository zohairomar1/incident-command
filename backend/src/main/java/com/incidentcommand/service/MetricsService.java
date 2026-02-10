package com.incidentcommand.service;

import com.incidentcommand.dto.MetricsResponse;
import com.incidentcommand.model.enums.IncidentStatus;
import com.incidentcommand.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MetricsService {

    private final IncidentRepository incidentRepository;

    public MetricsService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public MetricsResponse getMetrics() {
        Double mttr = incidentRepository.calculateMeanTimeToResolve();
        long total = incidentRepository.count();
        long open = incidentRepository.countByStatus(IncidentStatus.OPEN);
        long resolved = incidentRepository.countByStatus(IncidentStatus.RESOLVED);

        Map<String, Long> countBySeverity = groupedToMap(incidentRepository.countBySeverityGrouped());
        Map<String, Long> countByStatus = groupedToMap(incidentRepository.countByStatusGrouped());
        Map<String, Long> countByTeam = groupedToMap(incidentRepository.countByTeamGrouped());

        return MetricsResponse.builder()
                .meanTimeToResolveMinutes(mttr != null ? mttr : 0.0)
                .totalIncidents(total)
                .openIncidents(open)
                .resolvedIncidents(resolved)
                .countBySeverity(countBySeverity)
                .countByStatus(countByStatus)
                .countByTeam(countByTeam)
                .build();
    }

    private Map<String, Long> groupedToMap(List<Object[]> results) {
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] row : results) {
            map.put(row[0].toString(), (Long) row[1]);
        }
        return map;
    }
}
