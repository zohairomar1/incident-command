package com.incidentcommand.unit;

import com.incidentcommand.model.Role;
import com.incidentcommand.model.User;
import com.incidentcommand.model.enums.RoleName;
import com.incidentcommand.repository.UserRepository;
import com.incidentcommand.security.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_shouldReturnUserDetailsWithRoles() {
        User user = new User("admin", "admin@test.com", "encoded_pass");
        Role adminRole = new Role(RoleName.ADMIN);
        Role responderRole = new Role(RoleName.RESPONDER);
        user.getRoles().add(adminRole);
        user.getRoles().add(responderRole);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("admin");

        assertEquals("admin", result.getUsername());
        assertEquals("encoded_pass", result.getPassword());
        assertEquals(2, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_RESPONDER")));
    }

    @Test
    void loadUserByUsername_notFound_shouldThrowUsernameNotFoundException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknown"));
    }

    @Test
    void loadUserByUsername_withNoRoles_shouldReturnEmptyAuthorities() {
        User user = new User("viewer", "viewer@test.com", "encoded_pass");

        when(userRepository.findByUsername("viewer")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("viewer");

        assertEquals("viewer", result.getUsername());
        assertTrue(result.getAuthorities().isEmpty());
    }
}
