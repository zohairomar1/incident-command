package com.incidentcommand.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incidentcommand.dto.CreateIncidentRequest;
import com.incidentcommand.dto.LoginRequest;
import com.incidentcommand.dto.RegisterRequest;
import com.incidentcommand.model.EscalationPolicy;
import com.incidentcommand.model.enums.IncidentType;
import com.incidentcommand.model.enums.Severity;
import com.incidentcommand.repository.EscalationPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EscalationControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private EscalationPolicyRepository policyRepository;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest reg = new RegisterRequest("admin", "admin@test.com", "password123", "ADMIN");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)));

        LoginRequest login = new LoginRequest("admin", "password123");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();
        adminToken = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    void triggerEscalation_withValidIds_shouldReturn200() throws Exception {
        // Create an incident
        CreateIncidentRequest incidentReq = new CreateIncidentRequest("Server Down", "Critical",
                Severity.P1, IncidentType.INFRASTRUCTURE, null, null);
        MvcResult incidentResult = mockMvc.perform(post("/api/incidents")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incidentReq)))
                .andReturn();
        Long incidentId = objectMapper.readTree(incidentResult.getResponse().getContentAsString()).get("id").asLong();

        // Create an escalation policy
        EscalationPolicy policy = new EscalationPolicy("Default", "SEVERITY_BASED");
        policy = policyRepository.save(policy);

        mockMvc.perform(post("/api/escalations/trigger")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("incidentId", incidentId.toString())
                        .param("policyId", policy.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Escalation triggered successfully"));
    }

    @Test
    void triggerEscalation_withInvalidIncident_shouldReturn400() throws Exception {
        EscalationPolicy policy = new EscalationPolicy("Default", "SEVERITY_BASED");
        policy = policyRepository.save(policy);

        mockMvc.perform(post("/api/escalations/trigger")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("incidentId", "99999")
                        .param("policyId", policy.getId().toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void triggerEscalation_withInvalidPolicy_shouldReturn400() throws Exception {
        CreateIncidentRequest incidentReq = new CreateIncidentRequest("Test", "Desc",
                Severity.P2, IncidentType.SERVICE_DEGRADATION, null, null);
        MvcResult incidentResult = mockMvc.perform(post("/api/incidents")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incidentReq)))
                .andReturn();
        Long incidentId = objectMapper.readTree(incidentResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/escalations/trigger")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("incidentId", incidentId.toString())
                        .param("policyId", "99999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void triggerEscalation_withoutToken_shouldReturn403() throws Exception {
        mockMvc.perform(post("/api/escalations/trigger")
                        .param("incidentId", "1")
                        .param("policyId", "1"))
                .andExpect(status().isForbidden());
    }
}
