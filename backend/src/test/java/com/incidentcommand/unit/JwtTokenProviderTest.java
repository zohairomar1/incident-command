package com.incidentcommand.unit;

import com.incidentcommand.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        String secret = "Y2hhbmdlLXRoaXMtaW4tcHJvZHVjdGlvbi10by1hLXNlY3VyZS1yYW5kb20ta2V5LXRoYXQtaXMtYXQtbGVhc3QtMjU2LWJpdHM=";
        tokenProvider = new JwtTokenProvider(secret, 86400000);
    }

    @Test
    void generateToken_shouldReturnNonNullToken() {
        String token = tokenProvider.generateToken("testuser");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getUsernameFromToken_shouldReturnCorrectUsername() {
        String token = tokenProvider.generateToken("testuser");
        String username = tokenProvider.getUsernameFromToken(token);
        assertEquals("testuser", username);
    }

    @Test
    void validateToken_withValidToken_shouldReturnTrue() {
        String token = tokenProvider.generateToken("testuser");
        assertTrue(tokenProvider.validateToken(token));
    }

    @Test
    void validateToken_withInvalidToken_shouldReturnFalse() {
        assertFalse(tokenProvider.validateToken("invalid.token.here"));
    }

    @Test
    void validateToken_withNullToken_shouldReturnFalse() {
        assertFalse(tokenProvider.validateToken(null));
    }

    @Test
    void validateToken_withEmptyToken_shouldReturnFalse() {
        assertFalse(tokenProvider.validateToken(""));
    }
}
