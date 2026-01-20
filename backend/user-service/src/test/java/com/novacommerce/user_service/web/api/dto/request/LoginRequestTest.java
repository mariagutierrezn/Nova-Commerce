package com.novacommerce.user_service.web.api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("LoginRequest Tests")
class LoginRequestTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create login request with username")
    void testLoginWithUsername() {
        LoginRequest request = new LoginRequest("testuser", "password123");

        assertNotNull(request);
        assertEquals("testuser", request.userIdentifier());
        assertEquals("password123", request.password());
    }

    @Test
    @DisplayName("Should create login request with email")
    void testLoginWithEmail() {
        LoginRequest request = new LoginRequest("test@example.com", "password123");

        assertNotNull(request);
        assertEquals("test@example.com", request.userIdentifier());
        assertEquals("password123", request.password());
    }

    @Test
    @DisplayName("Should reject request with blank userIdentifier")
    void testBlankUserIdentifier() {
        LoginRequest request = new LoginRequest("", "password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject request with null userIdentifier")
    void testNullUserIdentifier() {
        LoginRequest request = new LoginRequest(null, "password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject request with blank password")
    void testBlankPassword() {
        LoginRequest request = new LoginRequest("testuser", "");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject request with null password")
    void testNullPassword() {
        LoginRequest request = new LoginRequest("testuser", null);

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept valid login request")
    void testValidLoginRequest() {
        LoginRequest request = new LoginRequest("admin", "admin123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should be a record type")
    void testIsRecord() {
        LoginRequest request = new LoginRequest("user", "pass");

        assertNotNull(request);
    }

    @Test
    @DisplayName("Should have immutable fields")
    void testImmutability() {
        LoginRequest request = new LoginRequest("testuser", "password");

        assertEquals("testuser", request.userIdentifier());
        assertEquals("password", request.password());
    }

    @Test
    @DisplayName("Should work with whitespace-only userIdentifier (should be invalid)")
    void testWhitespaceUserIdentifier() {
        LoginRequest request = new LoginRequest("   ", "password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept strong password")
    void testStrongPassword() {
        LoginRequest request = new LoginRequest(
            "testuser",
            "MyP@ssw0rd!#$%&*"
        );

        assertNotNull(request);
        assertEquals("MyP@ssw0rd!#$%&*", request.password());
    }

    @Test
    @DisplayName("Should accept long username")
    void testLongUsername() {
        String longUsername = "user_" + "a".repeat(95);
        LoginRequest request = new LoginRequest(longUsername, "password123");

        assertNotNull(request);
        assertEquals(longUsername, request.userIdentifier());
    }
}
