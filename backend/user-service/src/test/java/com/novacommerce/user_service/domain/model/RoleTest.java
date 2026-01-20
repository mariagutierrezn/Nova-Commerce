package com.novacommerce.user_service.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Role Domain Model Tests")
class RoleTest {

    private Role role;
    private Permission permission;

    @BeforeEach
    void setUp() {
        permission = Permission.builder()
                .id("perm-123")
                .name("USER_READ")
                .description("Permission to read users")
                .build();

        role = Role.builder()
                .id("role-123")
                .name("ADMIN")
                .description("Administrator role")
                .permissionIds(new HashSet<>())
                .build();
    }

    @Test
    @DisplayName("Should create role with all required fields")
    void testRoleCreation() {
        assertNotNull(role);
        assertNotNull(role.getId());
        assertEquals("ADMIN", role.getName());
        assertEquals("Administrator role", role.getDescription());
        assertNotNull(role.getPermissionIds());
        assertTrue(role.getPermissionIds().isEmpty());
    }

    @Test
    @DisplayName("Should allow setting role name")
    void testSetName() {
        role.setName("USER");
        assertEquals("USER", role.getName());
    }

    @Test
    @DisplayName("Should allow setting role description")
    void testSetDescription() {
        role.setDescription("Standard user role");
        assertEquals("Standard user role", role.getDescription());
    }

    @Test
    @DisplayName("Should add permission to role successfully")
    void testAddPermission() {
        role.addPermission(permission.getId());
        
        assertEquals(1, role.getPermissionIds().size());
        assertTrue(role.getPermissionIds().contains(permission.getId()));
    }

    @Test
    @DisplayName("Should not add null permission")
    void testAddNullPermission() {
        role.addPermission(null);
        
        assertEquals(0, role.getPermissionIds().size());
    }

    @Test
    @DisplayName("Should remove permission from role successfully")
    void testRemovePermission() {
        role.addPermission(permission.getId());
        assertEquals(1, role.getPermissionIds().size());
        
        role.removePermission(permission.getId());
        assertEquals(0, role.getPermissionIds().size());
        assertFalse(role.getPermissionIds().contains(permission.getId()));
    }

    @Test
    @DisplayName("Should not fail when removing null permission")
    void testRemoveNullPermission() {
        role.addPermission(permission.getId());
        assertEquals(1, role.getPermissionIds().size());
        
        role.removePermission(null);
        assertEquals(1, role.getPermissionIds().size());
    }

    @Test
    @DisplayName("Should use builder pattern correctly")
    void testBuilderPattern() {
        String testId = "role-456";
        Role builtRole = Role.builder()
                .id(testId)
                .name("SALES")
                .description("Sales role")
                .build();

        assertEquals(testId, builtRole.getId());
        assertEquals("SALES", builtRole.getName());
        assertEquals("Sales role", builtRole.getDescription());
    }

    @Test
    @DisplayName("Should handle multiple permissions")
    void testMultiplePermissions() {
        String permId1 = "perm-1";
        String permId2 = "perm-2";
        String permId3 = "perm-3";

        role.addPermission(permId1);
        role.addPermission(permId2);
        role.addPermission(permId3);

        assertEquals(3, role.getPermissionIds().size());
        assertTrue(role.getPermissionIds().contains(permId1));
        assertTrue(role.getPermissionIds().contains(permId2));
        assertTrue(role.getPermissionIds().contains(permId3));
    }

    @Test
    @DisplayName("Should allow null description")
    void testNullDescription() {
        role.setDescription(null);
        assertNull(role.getDescription());
    }

    @Test
    @DisplayName("Should remove specific permission from multiple permissions")
    void testRemoveSpecificPermission() {
        String permId1 = "perm-1";
        String permId2 = "perm-2";

        role.addPermission(permId1);
        role.addPermission(permId2);
        assertEquals(2, role.getPermissionIds().size());

        role.removePermission(permId1);
        assertEquals(1, role.getPermissionIds().size());
        assertFalse(role.getPermissionIds().contains(permId1));
        assertTrue(role.getPermissionIds().contains(permId2));
    }
}
