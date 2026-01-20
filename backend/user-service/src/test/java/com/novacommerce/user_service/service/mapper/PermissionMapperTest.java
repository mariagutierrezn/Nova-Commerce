package com.novacommerce.user_service.service.mapper;

import com.novacommerce.user_service.domain.model.Permission;
import com.novacommerce.user_service.web.api.dto.response.PermissionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("PermissionMapper Tests")
class PermissionMapperTest {

    @Autowired
    private PermissionMapper permissionMapper;

    private Permission permission;

    @BeforeEach
    void setUp() {
        permission = Permission.builder()
            .id("perm-1")
            .name("USER_READ")
            .description("Permission to read user data")
            .build();
    }

    @Test
    @DisplayName("Should map Permission to PermissionResponse")
    void testPermissionToPermissionResponse() {
        PermissionResponse response = permissionMapper.permissionToPermissionResponse(permission);
        assertNotNull(response);
        assertEquals(permission.getId(), response.id());
        assertEquals(permission.getName(), response.name());
        assertEquals(permission.getDescription(), response.description());
    }

    @Test
    @DisplayName("Should handle null permission")
    void testNullPermission() {
        PermissionResponse response = permissionMapper.permissionToPermissionResponse(null);
        assertNull(response);
    }

    @Test
    @DisplayName("Should handle null description")
    void testNullDescription() {
        permission.setDescription(null);
        PermissionResponse response = permissionMapper.permissionToPermissionResponse(permission);

        assertNotNull(response);
        assertNull(response.description());
    }

    @Test
    @DisplayName("Should map UUID correctly")
    void testUUIDMapping() {
        String permId = "perm-uuid";
        permission.setId(permId);
        PermissionResponse response = permissionMapper.permissionToPermissionResponse(permission);
        assertEquals(permId, response.id());
    }

    @Test
    @DisplayName("Should map different permission names")
    void testDifferentPermissionNames() {
        permission.setName("USER_CREATE");
        PermissionResponse createResponse = permissionMapper.permissionToPermissionResponse(permission);
        assertEquals("USER_CREATE", createResponse.name());
        permission.setName("USER_UPDATE");
        PermissionResponse updateResponse = permissionMapper.permissionToPermissionResponse(permission);
        assertEquals("USER_UPDATE", updateResponse.name());
        permission.setName("USER_DELETE");
        PermissionResponse deleteResponse = permissionMapper.permissionToPermissionResponse(permission);
        assertEquals("USER_DELETE", deleteResponse.name());
    }

    @Test
    @DisplayName("Should preserve description")
    void testDescriptionPreservation() {
        String desc = "This is a detailed permission description";
        permission.setDescription(desc);

        PermissionResponse response = permissionMapper.permissionToPermissionResponse(permission);

        assertEquals(desc, response.description());
    }

    @Test
    @DisplayName("Should handle user permissions")
    void testUserPermissions() {
        Permission userRead = Permission.builder()
            .id("perm-user-read")
            .name("USER_READ")
            .description("Read users")
            .build();
        PermissionResponse response = permissionMapper.permissionToPermissionResponse(userRead);
        assertEquals("USER_READ", response.name());
    }

    @Test
    @DisplayName("Should handle role permissions")
    void testRolePermissions() {
        Permission roleCreate = Permission.builder()
            .id("perm-role-create")
            .name("ROLE_CREATE")
            .description("Create roles")
            .build();
        PermissionResponse response = permissionMapper.permissionToPermissionResponse(roleCreate);
        assertEquals("ROLE_CREATE", response.name());
    }

    @Test
    @DisplayName("Should handle auth permissions")
    void testAuthPermissions() {
        Permission authValidate = Permission.builder()
            .id("perm-auth-validate")
            .name("AUTH_VALIDATE")
            .description("Validate tokens")
            .build();
        PermissionResponse response = permissionMapper.permissionToPermissionResponse(authValidate);
        assertEquals("AUTH_VALIDATE", response.name());
    }

    @Test
    @DisplayName("Should map all fields correctly")
    void testCompleteMapping() {
        String id = "perm-complete";
        permission.setId(id);
        permission.setName("COMPLETE_PERMISSION");
        permission.setDescription("Complete description");
        PermissionResponse response = permissionMapper.permissionToPermissionResponse(permission);
        assertEquals(id, response.id());
        assertEquals("COMPLETE_PERMISSION", response.name());
        assertEquals("Complete description", response.description());
    }
}
