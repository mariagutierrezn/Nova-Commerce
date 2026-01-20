package com.novacommerce.user_service.adapter.out.persistence;

import com.novacommerce.user_service.domain.model.User;
import com.novacommerce.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserPersistenceAdapter Tests")
class UserPersistenceAdapterTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserPersistenceAdapter adapter;

    private User testUser;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = "user-123";
        testUser = User.builder()
            .id(userId)
            .username("testuser")
            .email("test@example.com")
            .password("encodedPassword")
            .enabled(true)
            .locked(false)
            .roleIds(Set.of())
            .build();
    }

    @Test
    @DisplayName("Should find all users with pagination")
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(testUser), pageable, 1);
        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> result = adapter.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).getUsername());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should return empty page when no users found")
    void testFindAllEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<User> result = adapter.findAll(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should find user by id")
    void testFindById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        Optional<User> result = adapter.findById(userId);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should return empty when user not found by id")
    void testFindByIdNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = adapter.findById(userId);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should find user by username")
    void testFindByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Optional<User> result = adapter.findByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should return empty when user not found by username")
    void testFindByUsernameNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<User> result = adapter.findByUsername("nonexistent");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindByEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = adapter.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void testFindByEmailNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<User> result = adapter.findByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Should find user by username or email")
    void testFindByUsernameOrEmail() {
        when(userRepository.findByUsernameOrEmail("testuser")).thenReturn(Optional.of(testUser));

        Optional<User> result = adapter.findByUsernameOrEmail("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository, times(1)).findByUsernameOrEmail("testuser");
    }

    @Test
    @DisplayName("Should find user by email when searching with email")
    void testFindByUsernameOrEmailWithEmail() {
        when(userRepository.findByUsernameOrEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = adapter.findByUsernameOrEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findByUsernameOrEmail("test@example.com");
    }

    @Test
    @DisplayName("Should check if user exists by username")
    void testExistsByUsername() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        boolean exists = adapter.existsByUsername("testuser");

        assertTrue(exists);
        verify(userRepository, times(1)).existsByUsername("testuser");
    }

    @Test
    @DisplayName("Should return false when user does not exist by username")
    void testExistsByUsernameFalse() {
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        boolean exists = adapter.existsByUsername("nonexistent");

        assertFalse(exists);
        verify(userRepository, times(1)).existsByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void testExistsByEmail() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean exists = adapter.existsByEmail("test@example.com");

        assertTrue(exists);
        verify(userRepository, times(1)).existsByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should return false when user does not exist by email")
    void testExistsByEmailFalse() {
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        boolean exists = adapter.existsByEmail("nonexistent@example.com");

        assertFalse(exists);
        verify(userRepository, times(1)).existsByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Should save user")
    void testSave() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = adapter.save(testUser);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should delete user by id")
    void testDeleteById() {
        doNothing().when(userRepository).deleteById(userId);

        adapter.deleteById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}
