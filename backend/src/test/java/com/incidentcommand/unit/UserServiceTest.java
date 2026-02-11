package com.incidentcommand.unit;

import com.incidentcommand.dto.RegisterRequest;
import com.incidentcommand.model.Role;
import com.incidentcommand.model.User;
import com.incidentcommand.model.enums.RoleName;
import com.incidentcommand.repository.RoleRepository;
import com.incidentcommand.repository.UserRepository;
import com.incidentcommand.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_shouldCreateUserWithDefaultRole() {
        RegisterRequest request = new RegisterRequest("newuser", "new@example.com", "password123", null);
        Role viewerRole = new Role(RoleName.VIEWER);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(roleRepository.findByName(RoleName.VIEWER)).thenReturn(Optional.of(viewerRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerUser(request);

        assertEquals("newuser", result.getUsername());
        assertTrue(result.getRoles().contains(viewerRole));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void registerUser_withDuplicateUsername_shouldThrow() {
        RegisterRequest request = new RegisterRequest("existing", "new@example.com", "pass", null);
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(request));
    }

    @Test
    void registerUser_withDuplicateEmail_shouldThrow() {
        RegisterRequest request = new RegisterRequest("newuser", "existing@example.com", "pass", null);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(request));
    }

    @Test
    void findByUsername_shouldReturnUser() {
        User user = new User("testuser", "test@example.com", "pass");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("testuser");

        assertEquals("testuser", result.getUsername());
    }

    @Test
    void findByUsername_notFound_shouldThrow() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.findByUsername("unknown"));
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        User user1 = new User("alice", "alice@test.com", "pass");
        User user2 = new User("bob", "bob@test.com", "pass");
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = userService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findById_shouldReturnUser() {
        User user = new User("alice", "alice@test.com", "pass");
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("alice", result.getUsername());
    }

    @Test
    void findById_notFound_shouldThrow() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.findById(999L));
    }

    @Test
    void registerUser_withMissingRole_shouldThrow() {
        RegisterRequest request = new RegisterRequest("newuser", "new@example.com", "password123", "ADMIN");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(roleRepository.findByName(RoleName.ADMIN)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(request));
    }
}
