package com.incidentcommand.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incidentcommand.dto.CreateTeamRequest;
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
class TeamControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String adminToken;
    private String responderToken;

    @BeforeEach
    void setUp() throws Exception {
        // Register and login as admin
        RegisterRequest adminReg = new RegisterRequest("admin", "admin@test.com", "password123", "ADMIN");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminReg)));

        LoginRequest adminLogin = new LoginRequest("admin", "password123");
        MvcResult adminResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andReturn();
        adminToken = objectMapper.readTree(adminResult.getResponse().getContentAsString()).get("token").asText();

        // Register and login as responder
        RegisterRequest responderReg = new RegisterRequest("responder", "resp@test.com", "password123", "RESPONDER");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(responderReg)));

        LoginRequest responderLogin = new LoginRequest("responder", "password123");
        MvcResult responderResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responderLogin)))
                .andReturn();
        responderToken = objectMapper.readTree(responderResult.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    void createTeam_asAdmin_shouldReturn200() throws Exception {
        CreateTeamRequest request = new CreateTeamRequest("Platform", null);

        mockMvc.perform(post("/api/teams")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Platform"));
    }

    @Test
    void createTeam_withDuplicateName_shouldReturn400() throws Exception {
        CreateTeamRequest request = new CreateTeamRequest("Platform", null);

        mockMvc.perform(post("/api/teams")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/teams")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTeams_asAdmin_shouldReturnList() throws Exception {
        CreateTeamRequest request = new CreateTeamRequest("Infra", null);
        mockMvc.perform(post("/api/teams")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/api/teams")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Infra"));
    }

    @Test
    void getTeams_asResponder_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/teams")
                        .header("Authorization", "Bearer " + responderToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTeams_withoutToken_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isForbidden());
    }
}
