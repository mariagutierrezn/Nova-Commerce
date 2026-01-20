package com.novacommerce.user_service.adapter.out.persistence;

import com.novacommerce.user_service.domain.model.Role;
import com.novacommerce.user_service.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RolePersistenceAdapter Tests")
class RolePersistenceAdapterTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RolePersistenceAdapter adapter;

    private Role testRole;
    private String roleId;

    @BeforeEach
    void setUp() {
        roleId = "role-123";
        testRole = Role.builder()
            .id(roleId)
            .name("ADMIN")
            .description("Administrador del sistema")
            .permissionIds(Set.of())
            .build();
    }

    @Test
    @DisplayName("Should find all roles")
    void testFindAll() {
        List<Role> roles = List.of(testRole);
        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> result = adapter.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0).getName());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find role by id")
    void testFindById() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(testRole));

        Optional<Role> result = adapter.findById(roleId);

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getName());
        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    @DisplayName("Should return empty when role not found by id")
    void testFindByIdNotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        Optional<Role> result = adapter.findById(roleId);

        assertFalse(result.isPresent());
        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    @DisplayName("Should find role by name")
    void testFindByName() {
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(testRole));

        Optional<Role> result = adapter.findByName("ADMIN");

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getName());
        verify(roleRepository, times(1)).findByName("ADMIN");
    }

    @Test
    @DisplayName("Should return empty when role not found by name")
    void testFindByNameNotFound() {
        when(roleRepository.findByName("NONEXISTENT")).thenReturn(Optional.empty());

        Optional<Role> result = adapter.findByName("NONEXISTENT");

        assertFalse(result.isPresent());
        verify(roleRepository, times(1)).findByName("NONEXISTENT");
    }

    @Test
    @DisplayName("Should find all roles by id set")
    void testFindAllById() {
        Set<String> ids = Set.of(roleId);
        List<Role> roles = List.of(testRole);
        when(roleRepository.findAllById(ids)).thenReturn(roles);

        Set<Role> result = adapter.findAllById(ids);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(testRole));
        verify(roleRepository, times(1)).findAllById(ids);
    }

    @Test
    @DisplayName("Should return empty set when no roles found by ids")
    void testFindAllByIdEmpty() {
        Set<String> ids = Set.of("role-456");
        when(roleRepository.findAllById(ids)).thenReturn(List.of());

        Set<Role> result = adapter.findAllById(ids);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(roleRepository, times(1)).findAllById(ids);
    }

    @Test
    @DisplayName("Should check if role exists by name")
    void testExistsByName() {
        when(roleRepository.existsByName("ADMIN")).thenReturn(true);

        boolean exists = adapter.existsByName("ADMIN");

        assertTrue(exists);
        verify(roleRepository, times(1)).existsByName("ADMIN");
    }

    @Test
    @DisplayName("Should return false when role does not exist by name")
    void testExistsByNameFalse() {
        when(roleRepository.existsByName("NONEXISTENT")).thenReturn(false);

        boolean exists = adapter.existsByName("NONEXISTENT");

        assertFalse(exists);
        verify(roleRepository, times(1)).existsByName("NONEXISTENT");
    }
}
