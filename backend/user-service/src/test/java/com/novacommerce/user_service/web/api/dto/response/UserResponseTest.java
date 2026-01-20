package com.novacommerce.user_service.web.api.dto.response;

import com.novacommerce.user_service.domain.enums.UserStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
// ...existing code...

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserResponse DTO Tests")
class UserResponseTest {

        private UserResponse userResponse;
        private String userId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

    @BeforeEach
    void setUp() {
        userId = "user-1";
        createdAt = LocalDateTime.now().minusDays(10);
        updatedAt = LocalDateTime.now();
        Set<String> roleIds = new HashSet<>();
        roleIds.add("role-1");
        userResponse = new UserResponse(
                userId,
                "testuser",
                "test@example.com",
                UserStatusEnum.ACTIVE.name(),
                true,
                false,
                null,
                roleIds,
                createdAt,
                updatedAt
        );
    }

    @Test
    @DisplayName("Should create UserResponse with all fields")
    void testUserResponseCreation() {
        assertNotNull(userResponse);
        assertEquals(userId, userResponse.id());
        assertEquals("testuser", userResponse.username());
        assertEquals("test@example.com", userResponse.email());
        assertEquals("ACTIVE", userResponse.status());
        assertTrue(userResponse.enabled());
        assertFalse(userResponse.locked());
        assertNotNull(userResponse.roleIds());
        assertEquals(1, userResponse.roleIds().size());
        assertEquals(createdAt, userResponse.createdAt());
        assertEquals(updatedAt, userResponse.updatedAt());
    }

    @Test
    @DisplayName("Should handle null roles")
    void testNullRoles() {
        UserResponse response = new UserResponse(
                "user-2",
                "testuser",
                "test@example.com",
                "ACTIVE",
                true,
                false,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        assertNull(response.roleIds());
    }

    @Test
    @DisplayName("Should handle empty roles")
    void testEmptyRoles() {
        UserResponse response = new UserResponse(
                "user-3",
                "testuser",
                "test@example.com",
                "ACTIVE",
                true,
                false,
                null,
                new HashSet<>(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        assertNotNull(response.roleIds());
        assertTrue(response.roleIds().isEmpty());
    }

    @Test
    @DisplayName("Should handle multiple roles")
    void testMultipleRoles() {
        Set<String> roleIds = new HashSet<>();
        roleIds.add("role-2");
        roleIds.add("role-3");
        UserResponse response = new UserResponse(
                "user-4",
                "testuser",
                "test@example.com",
                "ACTIVE",
                true,
                false,
                null,
                roleIds,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        assertEquals(2, response.roleIds().size());
    }

    @Test
    @DisplayName("Should handle different statuses")
    void testDifferentStatuses() {
        UserResponse activeUser = new UserResponse(
                "user-5", "user1", "email1@test.com", "ACTIVE", true, false, null, new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );
        assertEquals("ACTIVE", activeUser.status());
        UserResponse inactiveUser = new UserResponse(
                "user-6", "user2", "email2@test.com", "INACTIVE", false, false, null, new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );
        assertEquals("INACTIVE", inactiveUser.status());
        UserResponse lockedUser = new UserResponse(
                "user-7", "user3", "email3@test.com", "LOCKED", true, true, null, new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );
        assertEquals("LOCKED", lockedUser.status());
    }

    @Test
    @DisplayName("Should handle enabled and locked combinations")
    void testEnabledLockedCombinations() {
        UserResponse user1 = new UserResponse(
                "user-8", "user1", "email@test.com", "ACTIVE", true, false, null, new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );
        assertTrue(user1.enabled());
        assertFalse(user1.locked());
        UserResponse user2 = new UserResponse(
                "user-9", "user2", "email@test.com", "INACTIVE", false, false, null, new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );
        assertFalse(user2.enabled());
        assertFalse(user2.locked());
        UserResponse user3 = new UserResponse(
                "user-10", "user3", "email@test.com", "LOCKED", true, true, null, new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );
        assertTrue(user3.enabled());
        assertTrue(user3.locked());
    }

    @Test
    @DisplayName("Should preserve UUID format")
    void testUUIDFormat() {
        String testId = "user-uuid";
        UserResponse response = new UserResponse(
                testId, "testuser", "test@example.com", "ACTIVE", true, false, null, new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );
        assertEquals(testId, response.id());
    }

    @Test
    @DisplayName("Should preserve timestamps")
    void testTimestamps() {
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updated = LocalDateTime.of(2024, 1, 15, 14, 30);

        UserResponse response = new UserResponse(
                "user-11", "testuser", "test@example.com", "ACTIVE", true, false, null, new HashSet<>(), created, updated
        );
        assertEquals(created, response.createdAt());
        assertEquals(updated, response.updatedAt());
    }

    @Test
    @DisplayName("Should handle username and email correctly")
    void testUsernameAndEmail() {
        UserResponse response = new UserResponse(
                "user-12", "admin", "admin@novacommerce.com", "ACTIVE", true, false, null, new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );
        assertEquals("admin", response.username());
        assertEquals("admin@novacommerce.com", response.email());
    }
}
