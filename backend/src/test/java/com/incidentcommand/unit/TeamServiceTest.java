package com.incidentcommand.unit;

import com.incidentcommand.dto.CreateTeamRequest;
import com.incidentcommand.model.Team;
import com.incidentcommand.model.User;
import com.incidentcommand.repository.TeamRepository;
import com.incidentcommand.repository.UserRepository;
import com.incidentcommand.service.TeamService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock private TeamRepository teamRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private TeamService teamService;

    @Test
    void createTeam_shouldCreateTeamSuccessfully() {
        CreateTeamRequest request = new CreateTeamRequest("Platform", null);
        Team team = new Team("Platform");
        team.setId(1L);

        when(teamRepository.existsByName("Platform")).thenReturn(false);
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        Team result = teamService.createTeam(request);

        assertEquals("Platform", result.getName());
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void createTeam_withMembers_shouldAssignMembers() {
        User user1 = new User("alice", "alice@test.com", "pass");
        user1.setId(1L);
        User user2 = new User("bob", "bob@test.com", "pass");
        user2.setId(2L);

        CreateTeamRequest request = new CreateTeamRequest("Platform", Set.of(1L, 2L));

        when(teamRepository.existsByName("Platform")).thenReturn(false);
        when(userRepository.findAllById(Set.of(1L, 2L))).thenReturn(List.of(user1, user2));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Team result = teamService.createTeam(request);

        assertEquals(2, result.getMembers().size());
    }

    @Test
    void createTeam_withDuplicateName_shouldThrow() {
        CreateTeamRequest request = new CreateTeamRequest("Existing", null);
        when(teamRepository.existsByName("Existing")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> teamService.createTeam(request));
        verify(teamRepository, never()).save(any());
    }

    @Test
    void findAll_shouldReturnTeams() {
        Team team = new Team("Platform");
        when(teamRepository.findAll()).thenReturn(List.of(team));

        List<Team> result = teamService.findAll();

        assertEquals(1, result.size());
        assertEquals("Platform", result.get(0).getName());
    }

    @Test
    void findAll_whenEmpty_shouldReturnEmptyList() {
        when(teamRepository.findAll()).thenReturn(Collections.emptyList());

        List<Team> result = teamService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_shouldReturnTeam() {
        Team team = new Team("Platform");
        team.setId(1L);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        Team result = teamService.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Platform", result.getName());
    }

    @Test
    void findById_notFound_shouldThrow() {
        when(teamRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> teamService.findById(999L));
    }
}
