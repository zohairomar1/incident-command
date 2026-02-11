package com.incidentcommand.unit;

import com.incidentcommand.dto.MetricsResponse;
import com.incidentcommand.model.enums.IncidentStatus;
import com.incidentcommand.repository.IncidentRepository;
import com.incidentcommand.service.MetricsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsServiceTest {

    @Mock private IncidentRepository incidentRepository;

    @InjectMocks
    private MetricsService metricsService;

    @Test
    void getMetrics_shouldReturnCorrectCounts() {
        when(incidentRepository.calculateMeanTimeToResolve()).thenReturn(45.0);
        when(incidentRepository.count()).thenReturn(10L);
        when(incidentRepository.countByStatus(IncidentStatus.OPEN)).thenReturn(3L);
        when(incidentRepository.countByStatus(IncidentStatus.RESOLVED)).thenReturn(5L);
        List<Object[]> severityData = new ArrayList<>();
        severityData.add(new Object[]{"P1", 2L});
        severityData.add(new Object[]{"P2", 3L});
        when(incidentRepository.countBySeverityGrouped()).thenReturn(severityData);
        List<Object[]> statusData = new ArrayList<>();
        statusData.add(new Object[]{"OPEN", 3L});
        statusData.add(new Object[]{"RESOLVED", 5L});
        when(incidentRepository.countByStatusGrouped()).thenReturn(statusData);
        List<Object[]> teamData = new ArrayList<>();
        teamData.add(new Object[]{"Platform", 4L});
        when(incidentRepository.countByTeamGrouped()).thenReturn(teamData);

        MetricsResponse result = metricsService.getMetrics();

        assertEquals(45.0, result.getMeanTimeToResolveMinutes());
        assertEquals(10, result.getTotalIncidents());
        assertEquals(3, result.getOpenIncidents());
        assertEquals(5, result.getResolvedIncidents());
        assertEquals(2, result.getCountBySeverity().size());
        assertEquals(1, result.getCountByTeam().size());
    }

    @Test
    void getMetrics_withNullMttr_shouldReturnZero() {
        when(incidentRepository.calculateMeanTimeToResolve()).thenReturn(null);
        when(incidentRepository.count()).thenReturn(0L);
        when(incidentRepository.countByStatus(any())).thenReturn(0L);
        when(incidentRepository.countBySeverityGrouped()).thenReturn(List.of());
        when(incidentRepository.countByStatusGrouped()).thenReturn(List.of());
        when(incidentRepository.countByTeamGrouped()).thenReturn(List.of());

        MetricsResponse result = metricsService.getMetrics();

        assertEquals(0.0, result.getMeanTimeToResolveMinutes());
        assertEquals(0, result.getTotalIncidents());
    }
}
