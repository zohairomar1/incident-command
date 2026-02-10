package com.incidentcommand.service;

import com.incidentcommand.dto.CreateTeamRequest;
import com.incidentcommand.model.Team;
import com.incidentcommand.model.User;
import com.incidentcommand.repository.TeamRepository;
import com.incidentcommand.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Team createTeam(CreateTeamRequest request) {
        if (teamRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Team name already exists");
        }

        Team team = new Team(request.getName());

        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            Set<User> members = new HashSet<>(userRepository.findAllById(request.getMemberIds()));
            team.setMembers(members);
        }

        return teamRepository.save(team);
    }

    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    public Team findById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Team not found with id: " + id));
    }
}
