package com.incidentcommand.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incidentcommand.dto.CreateIncidentRequest;
import com.incidentcommand.dto.RegisterRequest;
import com.incidentcommand.dto.LoginRequest;
import com.incidentcommand.model.enums.IncidentType;
import com.incidentcommand.model.enums.Severity;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IncidentControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

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

        String responseBody = result.getResponse().getContentAsString();
        adminToken = objectMapper.readTree(responseBody).get("token").asText();
    }

    @Test
    void createIncident_shouldReturn200() throws Exception {
        CreateIncidentRequest request = new CreateIncidentRequest("Server Down", "DB unreachable",
                Severity.P1, IncidentType.INFRASTRUCTURE, null, null);

        mockMvc.perform(post("/api/incidents")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Server Down"))
                .andExpect(jsonPath("$.severity").value("P1"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void getIncidents_shouldReturnList() throws Exception {
        CreateIncidentRequest request = new CreateIncidentRequest("Test", "Desc",
                Severity.P2, IncidentType.SERVICE_DEGRADATION, null, null);
        mockMvc.perform(post("/api/incidents")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/incidents")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getIncidentById_shouldReturn200() throws Exception {
        CreateIncidentRequest request = new CreateIncidentRequest("Specific", "Detail",
                Severity.P3, IncidentType.SECURITY, null, null);
        MvcResult createResult = mockMvc.perform(post("/api/incidents")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        Long id = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/incidents/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Specific"));
    }

    @Test
    void updateIncidentStatus_shouldUpdateToAcknowledged() throws Exception {
        CreateIncidentRequest request = new CreateIncidentRequest("StatusTest", "Desc",
                Severity.P2, IncidentType.INFRASTRUCTURE, null, null);
        MvcResult createResult = mockMvc.perform(post("/api/incidents")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        Long id = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(patch("/api/incidents/" + id + "/status?status=ACKNOWLEDGED")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACKNOWLEDGED"));
    }

    @Test
    void deleteIncident_shouldReturn204() throws Exception {
        CreateIncidentRequest request = new CreateIncidentRequest("ToDelete", "Will be deleted",
                Severity.P4, IncidentType.SERVICE_DEGRADATION, null, null);
        MvcResult createResult = mockMvc.perform(post("/api/incidents")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        Long id = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/incidents/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void accessWithoutToken_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isForbidden());
    }
}
