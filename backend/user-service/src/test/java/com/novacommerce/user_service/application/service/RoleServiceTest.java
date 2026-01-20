package com.novacommerce.user_service.application.service;

import com.novacommerce.user_service.application.port.out.RolePersistencePort;
import com.novacommerce.user_service.domain.model.Permission;
import com.novacommerce.user_service.domain.model.Role;
import com.novacommerce.user_service.repository.PermissionRepository;
import com.novacommerce.user_service.service.mapper.RoleMapper;
import com.novacommerce.user_service.web.api.dto.request.CreateRoleRequest;
import com.novacommerce.user_service.web.api.dto.response.RoleResponse;
import com.novacommerce.user_service.web.rest.exceptions.DuplicateResourceException;
import com.novacommerce.user_service.web.rest.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleService Tests")
class RoleServiceTest {

    @Mock
    private RolePersistencePort rolePersistencePort;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    private Role role;
    private RoleResponse roleResponse;
    private CreateRoleRequest createRoleRequest;
    private Permission permission;

    @BeforeEach
    void setUp() {
        permission = Permission.builder()
            .id("perm-" + System.nanoTime())
            .name("USER_READ")
            .description("Permission to read users")
            .build();

        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);

        role = Role.builder()
            .id("role-" + System.nanoTime())
            .name("ADMIN")
            .description("Administrator role")
            .permissionIds(permissions.stream().map(Permission::getId).collect(Collectors.toSet()))
            .build();

        roleResponse = new RoleResponse(
            role.getId(),
            role.getName(),
            role.getDescription(),
            new HashSet<>()
        );

        Set<String> permissionIds = new HashSet<>();
        permissionIds.add(permission.getId());

        createRoleRequest = new CreateRoleRequest(
            "ADMIN",
            "Administrator role",
            permissionIds
        );
    }

    @Test
    @DisplayName("Should get all roles successfully")
    void testGetAllRoles() {
        when(rolePersistencePort.findAll()).thenReturn(List.of(role));
        when(roleMapper.roleToRoleResponse(role)).thenReturn(roleResponse);

        List<RoleResponse> result = roleService.getAllRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(rolePersistencePort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get role by ID successfully")
    void testGetRoleById() {
        String roleId = role.getId();

        when(rolePersistencePort.findById(roleId)).thenReturn(Optional.of(role));
        when(roleMapper.roleToRoleResponse(role)).thenReturn(roleResponse);

        RoleResponse result = roleService.getRoleById(roleId);

        assertNotNull(result);
        assertEquals(roleId, result.id());
        verify(rolePersistencePort, times(1)).findById(roleId);
    }

    @Test
    @DisplayName("Should throw exception when role not found by ID")
    void testGetRoleByIdNotFound() {
        String roleId = "role-not-found";

        when(rolePersistencePort.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            roleService.getRoleById(roleId);
        });

        verify(rolePersistencePort, times(1)).findById(roleId);
    }

    @Test
    @DisplayName("Should create role successfully")
    void testCreateRole() {
        when(rolePersistencePort.existsByName(createRoleRequest.name())).thenReturn(false);
        when(permissionRepository.findAllById(anySet())).thenReturn(List.of(permission));
        when(rolePersistencePort.save(any(Role.class))).thenReturn(role);
        when(roleMapper.roleToRoleResponse(role)).thenReturn(roleResponse);

        RoleResponse result = roleService.createRole(createRoleRequest);

        assertNotNull(result);
        assertEquals("ADMIN", result.name());
        verify(rolePersistencePort, times(1)).save(any(Role.class));
    }

    @Test
    @DisplayName("Should throw exception when creating role with duplicate name")
    void testCreateRoleDuplicateName() {
        when(rolePersistencePort.existsByName(createRoleRequest.name())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            roleService.createRole(createRoleRequest);
        });

        verify(rolePersistencePort, never()).save(any(Role.class));
    }

    @Test
    @DisplayName("Should delete role successfully")
    void testDeleteRole() {
        String roleId = role.getId();

        when(rolePersistencePort.findById(roleId)).thenReturn(Optional.of(role));
        doNothing().when(rolePersistencePort).deleteById(roleId);

        roleService.deleteRole(roleId);

        verify(rolePersistencePort, times(1)).deleteById(roleId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent role")
    void testDeleteRoleNotFound() {
        String roleId = "role-not-found";

        when(rolePersistencePort.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            roleService.deleteRole(roleId);
        });

        verify(rolePersistencePort, never()).deleteById(any(String.class));
    }

    @Test
    @DisplayName("Should handle empty permission IDs")
    void testCreateRoleWithEmptyPermissions() {
        CreateRoleRequest requestWithoutPermissions = new CreateRoleRequest(
            "USER",
            "User role",
            new HashSet<>()
        );

        when(rolePersistencePort.existsByName(requestWithoutPermissions.name())).thenReturn(false);
        when(rolePersistencePort.save(any(Role.class))).thenReturn(role);
        when(roleMapper.roleToRoleResponse(any(Role.class))).thenReturn(roleResponse);

        RoleResponse result = roleService.createRole(requestWithoutPermissions);

        assertNotNull(result);
        verify(rolePersistencePort, times(1)).save(any(Role.class));
    }

    @Test
    @DisplayName("Should handle null permission IDs")
    void testCreateRoleWithNullPermissions() {
        CreateRoleRequest requestWithNullPermissions = new CreateRoleRequest(
            "USER",
            "User role",
            null
        );

        when(rolePersistencePort.existsByName(requestWithNullPermissions.name())).thenReturn(false);
        when(rolePersistencePort.save(any(Role.class))).thenReturn(role);
        when(roleMapper.roleToRoleResponse(any(Role.class))).thenReturn(roleResponse);

        RoleResponse result = roleService.createRole(requestWithNullPermissions);

        assertNotNull(result);
        verify(rolePersistencePort, times(1)).save(any(Role.class));
    }

    @Test
    @DisplayName("Should return empty list when no roles exist")
    void testGetAllRolesEmpty() {
        when(rolePersistencePort.findAll()).thenReturn(Collections.emptyList());

        List<RoleResponse> result = roleService.getAllRoles();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(rolePersistencePort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle multiple roles")
    void testGetAllRolesMultiple() {
        Role salesRole = Role.builder()
            .id("role-sales")
            .name("SALES")
            .description("Sales role")
            .permissionIds(new HashSet<>())
            .build();

        RoleResponse salesResponse = new RoleResponse(
                salesRole.getId(),
                salesRole.getName(),
                salesRole.getDescription(),
                new HashSet<>()
        );

        when(rolePersistencePort.findAll()).thenReturn(List.of(role, salesRole));
        when(roleMapper.roleToRoleResponse(role)).thenReturn(roleResponse);
        when(roleMapper.roleToRoleResponse(salesRole)).thenReturn(salesResponse);

        List<RoleResponse> result = roleService.getAllRoles();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(rolePersistencePort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should load permissions correctly")
    void testLoadPermissions() {
        Set<String> permissionIds = new HashSet<>();
        permissionIds.add(permission.getId());

        when(rolePersistencePort.existsByName(createRoleRequest.name())).thenReturn(false);
        when(permissionRepository.findAllById(permissionIds)).thenReturn(List.of(permission));
        when(rolePersistencePort.save(any(Role.class))).thenReturn(role);
        when(roleMapper.roleToRoleResponse(role)).thenReturn(roleResponse);

        RoleResponse result = roleService.createRole(createRoleRequest);

        assertNotNull(result);
        verify(permissionRepository, times(1)).findAllById(permissionIds);
    }
}
