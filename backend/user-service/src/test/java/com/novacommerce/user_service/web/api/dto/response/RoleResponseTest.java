package com.novacommerce.user_service.web.api.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RoleResponse DTO Tests")
class RoleResponseTest {

    private RoleResponse roleResponse;
    private String roleId;

    @BeforeEach
    void setUp() {
        roleId = "role-1";
        Set<String> permissionIds = new HashSet<>();
        permissionIds.add("perm-1");
        roleResponse = new RoleResponse(
            roleId,
            "ADMIN",
            "Administrator role",
            permissionIds
        );
    }

    @Test
    @DisplayName("Should create RoleResponse with all fields")
    void testRoleResponseCreation() {
        assertNotNull(roleResponse);
        assertEquals(roleId, roleResponse.id());
        assertEquals("ADMIN", roleResponse.name());
        assertEquals("Administrator role", roleResponse.description());
        assertNotNull(roleResponse.permissionIds());
        assertEquals(1, roleResponse.permissionIds().size());
    }

    @Test
    @DisplayName("Should handle null description")
    void testNullDescription() {
        RoleResponse response = new RoleResponse(
            "role-2",
            "USER",
            null,
            new HashSet<>()
        );

        assertNull(response.description());
    }

    @Test
    @DisplayName("Should handle null permissions")
    void testNullPermissions() {
        RoleResponse response = new RoleResponse(
            "role-3",
            "USER",
            "User role",
            null
        );
        assertNull(response.permissionIds());
    }

    @Test
    @DisplayName("Should handle empty permissions")
    void testEmptyPermissions() {
        RoleResponse response = new RoleResponse(
            "role-4",
            "USER",
            "User role",
            new HashSet<>()
        );
        assertNotNull(response.permissionIds());
        assertTrue(response.permissionIds().isEmpty());
    }

    @Test
    @DisplayName("Should handle multiple permissions")
    void testMultiplePermissions() {
        Set<String> permissionIds = new HashSet<>();
        permissionIds.add("perm-2");
        permissionIds.add("perm-3");
        permissionIds.add("perm-4");
        RoleResponse response = new RoleResponse(
            "role-5",
            "ADMIN",
            "Administrator role",
            permissionIds
        );
        assertEquals(3, response.permissionIds().size());
    }

    @Test
    @DisplayName("Should handle different role names")
    void testDifferentRoleNames() {
        RoleResponse admin = new RoleResponse("role-6", "ADMIN", "Admin", new HashSet<>());
        assertEquals("ADMIN", admin.name());
        RoleResponse user = new RoleResponse("role-7", "USER", "User", new HashSet<>());
        assertEquals("USER", user.name());
        RoleResponse sales = new RoleResponse("role-8", "SALES", "Sales", new HashSet<>());
        assertEquals("SALES", sales.name());
    }

    @Test
    @DisplayName("Should preserve UUID format")
    void testUUIDFormat() {
        String testId = "role-uuid";
        RoleResponse response = new RoleResponse(testId, "TEST", "Test role", new HashSet<>());
        assertEquals(testId, response.id());
    }

    @Test
    @DisplayName("Should handle long descriptions")
    void testLongDescription() {
        String longDesc = "This is a very long description that explains in detail what this role can do in the system";
        RoleResponse response = new RoleResponse("role-9", "ADMIN", longDesc, new HashSet<>());

        assertEquals(longDesc, response.description());
    }

    @Test
    @DisplayName("Should create role with complete permission set")
    void testCompletePermissionSet() {
        Set<String> permissionIds = new HashSet<>();
        permissionIds.add("perm-5");
        permissionIds.add("perm-6");
        permissionIds.add("perm-7");
        permissionIds.add("perm-8");
        RoleResponse response = new RoleResponse(
            "role-10",
            "ADMIN",
            "Full admin access",
            permissionIds
        );
        assertEquals(4, response.permissionIds().size());
        assertTrue(response.permissionIds().contains("perm-5"));
        assertTrue(response.permissionIds().contains("perm-6"));
    }

    @Test
    @DisplayName("Should handle empty name")
    void testEmptyName() {
        RoleResponse response = new RoleResponse("role-11", "", "Description", new HashSet<>());
        assertEquals("", response.name());
    }
}
