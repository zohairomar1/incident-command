package com.incidentcommand.dto;

import com.incidentcommand.model.Team;
import com.incidentcommand.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(target = "memberUsernames", source = "members", qualifiedByName = "usersToUsernames")
    TeamResponse toResponse(Team team);

    @Named("usersToUsernames")
    default Set<String> usersToUsernames(Set<User> users) {
        return users.stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());
    }
}
