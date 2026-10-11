package com.swapnanil.jobtracker.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("unit-test-secret-key-with-at-least-32-bytes!!".getBytes());

    @Test
    void generatedTokenIsValidAndContainsEmail() {
        JwtService jwtService = new JwtService(SECRET, 60_000);
        String token = jwtService.generateToken("user@example.com", "USER");

        assertEquals("user@example.com", jwtService.extractEmail(token));
        assertTrue(jwtService.isValid(token, "user@example.com"));
        assertFalse(jwtService.isValid(token, "other@example.com"));
    }

    @Test
    void tamperedTokenIsRejected() {
        JwtService jwtService = new JwtService(SECRET, 60_000);
        String token = jwtService.generateToken("user@example.com", "USER");

        assertThrows(JwtException.class, () -> jwtService.extractEmail(token + "x"));
    }
}
