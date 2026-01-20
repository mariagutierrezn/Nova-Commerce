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

@DisplayName("CreateRoleRequest DTO Tests")
class CreateRoleRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid CreateRoleRequest")
    void testValidCreateRoleRequest() {
        Set<String> permissionIds = new HashSet<>();
        permissionIds.add("perm-123");

        CreateRoleRequest request = new CreateRoleRequest(
                "ADMIN",
                "Administrator role",
                permissionIds
        );

        Set<ConstraintViolation<CreateRoleRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when name is blank")
    void testBlankName() {
        CreateRoleRequest request = new CreateRoleRequest(
                "",
                "Description",
                new HashSet<>()
        );

        Set<ConstraintViolation<CreateRoleRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("nombre del rol es obligatorio")));
    }

    @Test
    @DisplayName("Should fail validation when name is too short")
    void testNameTooShort() {
        CreateRoleRequest request = new CreateRoleRequest(
                "A",
                "Description",
                new HashSet<>()
        );

        Set<ConstraintViolation<CreateRoleRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when name is too long")
    void testNameTooLong() {
        String longName = "A".repeat(101);
        CreateRoleRequest request = new CreateRoleRequest(
                longName,
                "Description",
                new HashSet<>()
        );

        Set<ConstraintViolation<CreateRoleRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should allow null description")
    void testNullDescription() {
        CreateRoleRequest request = new CreateRoleRequest(
                "ADMIN",
                null,
                new HashSet<>()
        );

        Set<ConstraintViolation<CreateRoleRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when description is too long")
    void testDescriptionTooLong() {
        String longDescription = "A".repeat(501);
        CreateRoleRequest request = new CreateRoleRequest(
                "ADMIN",
                longDescription,
                new HashSet<>()
        );

        Set<ConstraintViolation<CreateRoleRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should allow null permissionIds")
    void testNullPermissionIds() {
        CreateRoleRequest request = new CreateRoleRequest(
                "ADMIN",
                "Administrator role",
                null
        );

        Set<ConstraintViolation<CreateRoleRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should allow empty permissionIds")
    void testEmptyPermissionIds() {
        CreateRoleRequest request = new CreateRoleRequest(
                "ADMIN",
                "Administrator role",
                new HashSet<>()
        );

        Set<ConstraintViolation<CreateRoleRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle multiple permissionIds")
    void testMultiplePermissionIds() {
        Set<String> permissionIds = new HashSet<>();
        permissionIds.add("perm-1");
        permissionIds.add("perm-2");
        permissionIds.add("perm-3");

        CreateRoleRequest request = new CreateRoleRequest(
                "ADMIN",
                "Administrator role",
                permissionIds
        );

        Set<ConstraintViolation<CreateRoleRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        assertEquals(3, request.permissionIds().size());
    }

    @Test
    @DisplayName("Should accept valid role names")
    void testValidRoleNames() {
        CreateRoleRequest request1 = new CreateRoleRequest("ADMIN", "Admin", new HashSet<>());
        CreateRoleRequest request2 = new CreateRoleRequest("USER", "User", new HashSet<>());
        CreateRoleRequest request3 = new CreateRoleRequest("SALES", "Sales", new HashSet<>());

        assertTrue(validator.validate(request1).isEmpty());
        assertTrue(validator.validate(request2).isEmpty());
        assertTrue(validator.validate(request3).isEmpty());
    }
}
