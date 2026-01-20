package com.novacommerce.user_service.application.service;

import com.novacommerce.user_service.application.port.out.PasswordEncoderPort;
import com.novacommerce.user_service.application.port.out.RolePersistencePort;
import com.novacommerce.user_service.application.port.out.UserPersistencePort;
import com.novacommerce.user_service.domain.enums.UserStatusEnum;
import com.novacommerce.user_service.domain.model.Role;
import com.novacommerce.user_service.domain.model.User;
import com.novacommerce.user_service.service.mapper.UserMapper;
import com.novacommerce.user_service.web.api.dto.request.CreateUserRequest;
import com.novacommerce.user_service.web.api.dto.response.UserResponse;
import com.novacommerce.user_service.web.rest.exceptions.DuplicateResourceException;
import com.novacommerce.user_service.web.rest.exceptions.ResourceNotFoundException;
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

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @Mock
    private RolePersistencePort rolePersistencePort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse userResponse;
    private CreateUserRequest createUserRequest;
    private Role role;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .id("role-123")
                .name("ADMIN")
                .description("Administrator role")
                .permissionIds(new HashSet<>())
                .build();

        Set<String> roleIds = new HashSet<>();
        roleIds.add(role.getId());

        user = User.builder()
                .id("user-123")
                .username("testuser")
                .email("test@example.com")
                .password("encryptedPassword")
                .status(UserStatusEnum.ACTIVE)
                .enabled(true)
                .locked(false)
                .roleIds(roleIds)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userResponse = new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getStatus().name(),
                user.getEnabled(),
                user.getLocked(),
                null,
                new HashSet<>(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );

        Set<String> roleIdsForRequest = new HashSet<>();
        roleIdsForRequest.add(role.getId());

        createUserRequest = new CreateUserRequest(
                "testuser",
                "test@example.com",
                "password123",
                roleIdsForRequest,
                null
        );
    }

    @Test
    @DisplayName("Should get all users successfully")
    void testGetAllUsers() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userPersistencePort.findAll(pageable)).thenReturn(userPage);
        when(userMapper.userToUserResponse(user)).thenReturn(userResponse);

        Page<UserResponse> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userPersistencePort, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void testGetUserById() {
        String userId = user.getId();

        when(userPersistencePort.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.userToUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.id());
        verify(userPersistencePort, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void testGetUserByIdNotFound() {
        String userId = "user-456";

        when(userPersistencePort.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(userId);
        });

        verify(userPersistencePort, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUser() {
        when(userPersistencePort.existsByUsername(createUserRequest.username())).thenReturn(false);
        when(userPersistencePort.existsByEmail(createUserRequest.email())).thenReturn(false);
        when(passwordEncoderPort.encode(createUserRequest.password())).thenReturn("encryptedPassword");
        when(rolePersistencePort.findAllById(anySet())).thenReturn(Set.of(role));
        when(userPersistencePort.save(any(User.class))).thenReturn(user);
        when(userMapper.userToUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.createUser(createUserRequest);

        assertNotNull(result);
        assertEquals("testuser", result.username());
        verify(userPersistencePort, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with duplicate username")
    void testCreateUserDuplicateUsername() {
        when(userPersistencePort.existsByUsername(createUserRequest.username())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            userService.createUser(createUserRequest);
        });

        verify(userPersistencePort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with duplicate email")
    void testCreateUserDuplicateEmail() {
        when(userPersistencePort.existsByUsername(createUserRequest.username())).thenReturn(false);
        when(userPersistencePort.existsByEmail(createUserRequest.email())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            userService.createUser(createUserRequest);
        });

        verify(userPersistencePort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser() {
        String userId = user.getId();
        CreateUserRequest updateRequest = new CreateUserRequest(
                "updateduser",
                "updated@example.com",
                "newpassword",
                new HashSet<>(Set.of(role.getId())),
                null
        );

        when(userPersistencePort.findById(userId)).thenReturn(Optional.of(user));
        when(userPersistencePort.existsByUsername(updateRequest.username())).thenReturn(false);
        when(userPersistencePort.existsByEmail(updateRequest.email())).thenReturn(false);
        when(passwordEncoderPort.encode(updateRequest.password())).thenReturn("newEncryptedPassword");
        when(rolePersistencePort.findAllById(anySet())).thenReturn(Set.of(role));
        when(userPersistencePort.save(any(User.class))).thenReturn(user);
        when(userMapper.userToUserResponse(any(User.class))).thenReturn(userResponse);

        UserResponse result = userService.updateUser(userId, updateRequest);

        assertNotNull(result);
        verify(userPersistencePort, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void testUpdateUserNotFound() {
        String userId = "user-789";

        when(userPersistencePort.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(userId, createUserRequest);
        });

        verify(userPersistencePort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser() {
        String userId = user.getId();

        when(userPersistencePort.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userPersistencePort).deleteById(userId);

        userService.deleteUser(userId);

        verify(userPersistencePort, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void testDeleteUserNotFound() {
        String userId = "user-999";

        when(userPersistencePort.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });

        verify(userPersistencePort, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Should encode password when creating user")
    void testPasswordEncoding() {
        when(userPersistencePort.existsByUsername(anyString())).thenReturn(false);
        when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode("password123")).thenReturn("encryptedPassword123");
        when(rolePersistencePort.findAllById(anySet())).thenReturn(Set.of(role));
        when(userPersistencePort.save(any(User.class))).thenReturn(user);
        when(userMapper.userToUserResponse(any(User.class))).thenReturn(userResponse);

        userService.createUser(createUserRequest);

        verify(passwordEncoderPort, times(1)).encode("password123");
    }

    @Test
    @DisplayName("Should handle empty role IDs")
    void testCreateUserWithEmptyRoles() {
        CreateUserRequest requestWithoutRoles = new CreateUserRequest(
                "testuser",
                "test@example.com",
                "password123",
                new HashSet<>(),
                null
        );

        Role userRole = Role.builder()
                .id("role-789")
                .name("USER")
                .description("User role")
                .permissionIds(new HashSet<>())
                .build();

        when(userPersistencePort.existsByUsername(anyString())).thenReturn(false);
        when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode(anyString())).thenReturn("encryptedPassword");
        when(rolePersistencePort.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userPersistencePort.save(any(User.class))).thenReturn(user);
        when(userMapper.userToUserResponse(any(User.class))).thenReturn(userResponse);

        UserResponse result = userService.createUser(requestWithoutRoles);

        assertNotNull(result);
        verify(userPersistencePort, times(1)).save(any(User.class));
    }
}
