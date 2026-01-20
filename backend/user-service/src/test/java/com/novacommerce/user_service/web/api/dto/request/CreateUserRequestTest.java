package com.novacommerce.user_service.web.api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreateUserRequest DTO Tests")
class CreateUserRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid CreateUserRequest")
    void testValidCreateUserRequest() {
        Set<String> roleIds = new HashSet<>();
        roleIds.add("role-123");

        CreateUserRequest request = new CreateUserRequest(
                "testuser",
                "test@example.com",
                "password123",
                roleIds,
                null
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is blank")
    void testBlankUsername() {
        CreateUserRequest request = new CreateUserRequest(
                "",
                "test@example.com",
                "password123",
                new HashSet<>(),
                null
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("nombre de usuario es obligatorio")));
    }

    @Test
    @DisplayName("Should fail validation when username is too short")
    void testUsernameTooShort() {
        CreateUserRequest request = new CreateUserRequest(
                "ab",
                "test@example.com",
                "password123",
                new HashSet<>(),
                null
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is too long")
    void testUsernameTooLong() {
        String longUsername = "a".repeat(101);
        CreateUserRequest request = new CreateUserRequest(
                longUsername,
                "test@example.com",
                "password123",
                new HashSet<>(),
                null
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when email is blank")
    void testBlankEmail() {
        CreateUserRequest request = new CreateUserRequest(
                "testuser",
                "",
                "password123",
                new HashSet<>(),
                null
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void testInvalidEmail() {
        CreateUserRequest request = new CreateUserRequest(
                "testuser",
                "invalid-email",
                "password123",
                new HashSet<>(),
                null
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("email debe ser válido")));
    }

    @Test
    @DisplayName("Should fail validation when password is blank")
    void testBlankPassword() {
        CreateUserRequest request = new CreateUserRequest(
                "testuser",
                "test@example.com",
                "",
                new HashSet<>(),
                null
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when password is too short")
    void testPasswordTooShort() {
        CreateUserRequest request = new CreateUserRequest(
                "testuser",
                "test@example.com",
                "pass",
                new HashSet<>(),
                null
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("al menos 8 caracteres")));
    }

    @Test
    @DisplayName("Should allow null roleIds")
    void testNullRoleIds() {
        CreateUserRequest request = new CreateUserRequest(
                "testuser",
                "test@example.com",
                "password123",
                null,
                null
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should allow empty roleIds")
    void testEmptyRoleIds() {
        CreateUserRequest request = new CreateUserRequest(
                "testuser",
                "test@example.com",
                "password123",
                new HashSet<>(),
                null
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle multiple roleIds")
    void testMultipleRoleIds() {
        Set<String> roleIds = new HashSet<>();
        roleIds.add("role-1");
        roleIds.add("role-2");
        roleIds.add("role-3");

        CreateUserRequest request = new CreateUserRequest(
                "testuser",
                "test@example.com",
                "password123",
                roleIds,
                null
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        assertEquals(3, request.roleIds().size());
    }
}
