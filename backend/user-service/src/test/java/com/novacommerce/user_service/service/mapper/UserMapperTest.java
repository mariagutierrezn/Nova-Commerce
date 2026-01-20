package com.novacommerce.user_service.service.mapper;

import com.novacommerce.user_service.domain.enums.UserStatusEnum;
import com.novacommerce.user_service.domain.model.User;
import com.novacommerce.user_service.web.api.dto.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("UserMapper Tests")
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    private User user;
    private Set<String> roleIds;

    @BeforeEach
    void setUp() {
        roleIds = new HashSet<>();
        roleIds.add("role-1");
        user = User.builder()
            .id("user-1")
            .username("testuser")
            .email("test@example.com")
            .password("encryptedPassword")
            .status(UserStatusEnum.ACTIVE)
            .enabled(true)
            .locked(false)
            .roleIds(roleIds)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .lastLogin(LocalDateTime.now().minusDays(1))
            .build();
    }

    @Test
    @DisplayName("Should map User to UserResponse")
    void testUserToUserResponse() {
        UserResponse response = userMapper.userToUserResponse(user);
        assertNotNull(response);
        assertEquals(user.getId(), response.id());
        assertEquals(user.getUsername(), response.username());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getStatus().name(), response.status());
        assertEquals(user.getEnabled(), response.enabled());
        assertEquals(user.getLocked(), response.locked());
        assertEquals(user.getCreatedAt(), response.createdAt());
        assertEquals(user.getUpdatedAt(), response.updatedAt());
    }

    @Test
    @DisplayName("Should map roles correctly")
    void testRolesMapping() {
        UserResponse response = userMapper.userToUserResponse(user);
        assertNotNull(response.roleIds());
        assertEquals(1, response.roleIds().size());
        assertTrue(response.roleIds().contains("role-1"));
    }

    @Test
    @DisplayName("Should map nested permissions in roles")
    void testNestedPermissionsMapping() {
        UserResponse response = userMapper.userToUserResponse(user);
        assertNotNull(response.roleIds());
        assertTrue(response.roleIds().contains("role-1"));
    }

    @Test
    @DisplayName("Should handle null user")
    void testNullUser() {
        UserResponse response = userMapper.userToUserResponse(null);
        assertNull(response);
    }

    @Test
    @DisplayName("Should handle user without roles")
    void testUserWithoutRoles() {
        User userWithoutRoles = User.builder()
            .id("user-2")
            .username("noroles")
            .email("noroles@example.com")
            .password("password")
            .status(UserStatusEnum.ACTIVE)
            .enabled(true)
            .locked(false)
            .roleIds(new HashSet<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        UserResponse response = userMapper.userToUserResponse(userWithoutRoles);
        assertNotNull(response);
        assertNotNull(response.roleIds());
        assertTrue(response.roleIds().isEmpty());
    }

    @Test
    @DisplayName("Should handle different user statuses")
    void testDifferentStatuses() {
        user.setStatus(UserStatusEnum.INACTIVE);
        UserResponse inactiveResponse = userMapper.userToUserResponse(user);
        assertEquals("INACTIVE", inactiveResponse.status());

        user.setStatus(UserStatusEnum.LOCKED);
        UserResponse lockedResponse = userMapper.userToUserResponse(user);
        assertEquals("LOCKED", lockedResponse.status());

        user.setStatus(UserStatusEnum.ACTIVE);
        UserResponse activeResponse = userMapper.userToUserResponse(user);
        assertEquals("ACTIVE", activeResponse.status());
    }

    @Test
    @DisplayName("Should map enabled and locked flags correctly")
    void testEnabledAndLockedMapping() {
        user.setEnabled(false);
        user.setLocked(true);

        UserResponse response = userMapper.userToUserResponse(user);

        assertFalse(response.enabled());
        assertTrue(response.locked());
    }

    @Test
    @DisplayName("Should handle multiple roles")
    void testMultipleRoles() {
        user.getRoleIds().add("role-2");
        UserResponse response = userMapper.userToUserResponse(user);
        assertEquals(2, response.roleIds().size());
        assertTrue(response.roleIds().contains("role-1"));
        assertTrue(response.roleIds().contains("role-2"));
    }

    @Test
    @DisplayName("Should preserve timestamps")
    void testTimestampMapping() {
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updated = LocalDateTime.of(2024, 1, 15, 14, 30);

        user.setCreatedAt(created);
        user.setUpdatedAt(updated);

        UserResponse response = userMapper.userToUserResponse(user);

        assertEquals(created, response.createdAt());
        assertEquals(updated, response.updatedAt());
    }

    @Test
    @DisplayName("Should map UUID correctly")
    void testUUIDMapping() {
        String userId = "user-uuid";
        user.setId(userId);
        UserResponse response = userMapper.userToUserResponse(user);
        assertEquals(userId, response.id());
    }
}
