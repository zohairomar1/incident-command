package com.incidentcommand.controller;

import com.incidentcommand.dto.CreateTeamRequest;
import com.incidentcommand.dto.TeamMapper;
import com.incidentcommand.dto.TeamResponse;
import com.incidentcommand.model.Team;
import com.incidentcommand.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Teams", description = "Team management (admin only)")
@Transactional(readOnly = true)
public class TeamController {

    private final TeamService teamService;
    private final TeamMapper teamMapper;

    public TeamController(TeamService teamService, TeamMapper teamMapper) {
        this.teamService = teamService;
        this.teamMapper = teamMapper;
    }

    @GetMapping
    @Operation(summary = "Get all teams")
    public ResponseEntity<List<TeamResponse>> getAll() {
        List<TeamResponse> teams = teamService.findAll().stream()
                .map(teamMapper::toResponse)
                .toList();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team by ID")
    public ResponseEntity<TeamResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(teamMapper.toResponse(teamService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new team")
    @Transactional
    public ResponseEntity<TeamResponse> create(@Valid @RequestBody CreateTeamRequest request) {
        Team team = teamService.createTeam(request);
        return ResponseEntity.ok(teamMapper.toResponse(team));
    }
}
