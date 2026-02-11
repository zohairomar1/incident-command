package com.incidentcommand.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incidentcommand.dto.LoginRequest;
import com.incidentcommand.dto.RegisterRequest;
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
class MetricsControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest reg = new RegisterRequest("metricsuser", "metrics@test.com", "password123", "ADMIN");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)));

        LoginRequest login = new LoginRequest("metricsuser", "password123");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    void getMetrics_shouldReturnMetricsResponse() throws Exception {
        mockMvc.perform(get("/api/metrics")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncidents").exists())
                .andExpect(jsonPath("$.openIncidents").exists())
                .andExpect(jsonPath("$.meanTimeToResolveMinutes").exists())
                .andExpect(jsonPath("$.countBySeverity").exists())
                .andExpect(jsonPath("$.countByStatus").exists());
    }
}
