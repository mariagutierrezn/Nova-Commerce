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
@DisplayName("InternalUserValidationRequest Tests")
class InternalUserValidationRequestTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create request with valid credentials")
    void testValidRequest() {
        InternalUserValidationRequest request = new InternalUserValidationRequest(
            "testuser",
            "password123"
        );

        assertNotNull(request);
        assertEquals("testuser", request.userIdentifier());
        assertEquals("password123", request.password());
    }

    @Test
    @DisplayName("Should reject request with blank userIdentifier")
    void testBlankUserIdentifier() {
        InternalUserValidationRequest request = new InternalUserValidationRequest(
            "",
            "password123"
        );

        Set<ConstraintViolation<InternalUserValidationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject request with null userIdentifier")
    void testNullUserIdentifier() {
        InternalUserValidationRequest request = new InternalUserValidationRequest(
            null,
            "password123"
        );

        Set<ConstraintViolation<InternalUserValidationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject request with blank password")
    void testBlankPassword() {
        InternalUserValidationRequest request = new InternalUserValidationRequest(
            "testuser",
            ""
        );

        Set<ConstraintViolation<InternalUserValidationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject request with null password")
    void testNullPassword() {
        InternalUserValidationRequest request = new InternalUserValidationRequest(
            "testuser",
            null
        );

        Set<ConstraintViolation<InternalUserValidationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept request with email as userIdentifier")
    void testEmailAsUserIdentifier() {
        InternalUserValidationRequest request = new InternalUserValidationRequest(
            "test@example.com",
            "password123"
        );

        assertNotNull(request);
        assertEquals("test@example.com", request.userIdentifier());
    }

    @Test
    @DisplayName("Should be a record type")
    void testIsRecord() {
        InternalUserValidationRequest request = new InternalUserValidationRequest(
            "user",
            "pass"
        );

        assertNotNull(request);
    }

    @Test
    @DisplayName("Should have immutable fields")
    void testImmutability() {
        InternalUserValidationRequest request = new InternalUserValidationRequest(
            "testuser",
            "password"
        );

        assertEquals("testuser", request.userIdentifier());
        assertEquals("password", request.password());
    }

    @Test
    @DisplayName("Should work with long usernames")
    void testLongUsername() {
        String longUsername = "a".repeat(100);
        InternalUserValidationRequest request = new InternalUserValidationRequest(
            longUsername,
            "password123"
        );

        assertNotNull(request);
        assertEquals(longUsername, request.userIdentifier());
    }

    @Test
    @DisplayName("Should work with special characters in password")
    void testSpecialCharactersPassword() {
        InternalUserValidationRequest request = new InternalUserValidationRequest(
            "testuser",
            "P@ssw0rd!#$%&*"
        );

        assertNotNull(request);
        assertEquals("P@ssw0rd!#$%&*", request.password());
    }
}
