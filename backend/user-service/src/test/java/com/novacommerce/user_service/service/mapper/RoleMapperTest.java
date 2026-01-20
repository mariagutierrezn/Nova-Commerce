package com.novacommerce.user_service.service.mapper;

import com.novacommerce.user_service.domain.model.Role;
import com.novacommerce.user_service.web.api.dto.response.RoleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("RoleMapper Tests")
class RoleMapperTest {

    @Autowired
    private RoleMapper roleMapper;

    private Role role;
    private String permissionId;

    @BeforeEach
    void setUp() {
        permissionId = "perm-1";
        Set<String> permissionIds = new HashSet<>();
        permissionIds.add(permissionId);
        role = Role.builder()
            .id("role-1")
            .name("ADMIN")
            .description("Administrator role")
            .permissionIds(permissionIds)
            .build();
    }

    @Test
    @DisplayName("Should map Role to RoleResponse")
    void testRoleToRoleResponse() {
        RoleResponse response = roleMapper.roleToRoleResponse(role);
        assertNotNull(response);
        assertEquals(role.getId(), response.id());
        assertEquals(role.getName(), response.name());
        assertEquals(role.getDescription(), response.description());
        assertNotNull(response.permissionIds());
        assertEquals(1, response.permissionIds().size());
    }

    @Test
    @DisplayName("Should map permissions correctly")
    void testPermissionsMapping() {
        RoleResponse response = roleMapper.roleToRoleResponse(role);
        assertNotNull(response.permissionIds());
        assertEquals(1, response.permissionIds().size());
        assertTrue(response.permissionIds().contains(permissionId));
    }

    @Test
    @DisplayName("Should handle null role")
    void testNullRole() {
        RoleResponse response = roleMapper.roleToRoleResponse(null);
        assertNull(response);
    }

    @Test
    @DisplayName("Should handle role without permissions")
    void testRoleWithoutPermissions() {
        Role roleWithoutPermissions = Role.builder()
            .id("role-2")
            .name("USER")
            .description("User role")
            .permissionIds(new HashSet<>())
            .build();
        RoleResponse response = roleMapper.roleToRoleResponse(roleWithoutPermissions);
        assertNotNull(response);
        assertNotNull(response.permissionIds());
        assertTrue(response.permissionIds().isEmpty());
    }

    @Test
    @DisplayName("Should handle null description")
    void testNullDescription() {
        role.setDescription(null);
        RoleResponse response = roleMapper.roleToRoleResponse(role);

        assertNotNull(response);
        assertNull(response.description());
    }

    @Test
    @DisplayName("Should handle multiple permissions")
    void testMultiplePermissions() {
        role.getPermissionIds().add("perm-2");
        role.getPermissionIds().add("perm-3");
        RoleResponse response = roleMapper.roleToRoleResponse(role);
        assertEquals(3, response.permissionIds().size());
        assertTrue(response.permissionIds().contains(permissionId));
        assertTrue(response.permissionIds().contains("perm-2"));
        assertTrue(response.permissionIds().contains("perm-3"));
    }

    @Test
    @DisplayName("Should map UUID correctly")
    void testUUIDMapping() {
        String roleId = "role-uuid";
        role.setId(roleId);
        RoleResponse response = roleMapper.roleToRoleResponse(role);
        assertEquals(roleId, response.id());
    }

    @Test
    @DisplayName("Should map different role names")
    void testDifferentRoleNames() {
        role.setName("SALES");
        RoleResponse salesResponse = roleMapper.roleToRoleResponse(role);
        assertEquals("SALES", salesResponse.name());
        role.setName("USER");
        RoleResponse userResponse = roleMapper.roleToRoleResponse(role);
        assertEquals("USER", userResponse.name());
    }

    @Test
    @DisplayName("Should map permission details completely")
    void testCompletePermissionMapping() {
        RoleResponse response = roleMapper.roleToRoleResponse(role);
        assertNotNull(response.permissionIds());
    }

    @Test
    @DisplayName("Should handle empty permission set")
    void testEmptyPermissionSet() {
        role.setPermissionIds(new HashSet<>());
        RoleResponse response = roleMapper.roleToRoleResponse(role);
        assertNotNull(response.permissionIds());
        assertEquals(0, response.permissionIds().size());
    }

    @Test
    @DisplayName("Should preserve role name and description")
    void testNameAndDescriptionPreservation() {
        role.setName("CUSTOM_ROLE");
        role.setDescription("Custom role description");

        RoleResponse response = roleMapper.roleToRoleResponse(role);

        assertEquals("CUSTOM_ROLE", response.name());
        assertEquals("Custom role description", response.description());
    }
}
