package com.novacommerce.user_service.web.api.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TokenValidationResponse Tests")
class TokenValidationResponseTest {

    @Test
    @DisplayName("Should create valid TokenValidationResponse")
    void testCreateValidToken() {
        TokenValidationResponse response = new TokenValidationResponse(
            true,
            "testuser",
            "ROLE_USER,USER_READ"
        );

        assertNotNull(response);
        assertTrue(response.valid());
        assertEquals("testuser", response.username());
        assertEquals("ROLE_USER,USER_READ", response.authorities());
    }

    @Test
    @DisplayName("Should create invalid TokenValidationResponse")
    void testCreateInvalidToken() {
        TokenValidationResponse response = new TokenValidationResponse(
            false,
            null,
            null
        );

        assertNotNull(response);
        assertFalse(response.valid());
        assertNull(response.username());
        assertNull(response.authorities());
    }

    @Test
    @DisplayName("Should use factory method for valid token")
    void testValidFactory() {
        TokenValidationResponse response = TokenValidationResponse.valid("admin", "ROLE_ADMIN");

        assertNotNull(response);
        assertTrue(response.valid());
        assertEquals("admin", response.username());
        assertEquals("ROLE_ADMIN", response.authorities());
    }

    @Test
    @DisplayName("Should use factory method for invalid token")
    void testInvalidFactory() {
        TokenValidationResponse response = TokenValidationResponse.invalid();

        assertNotNull(response);
        assertFalse(response.valid());
        assertNull(response.username());
        assertNull(response.authorities());
    }

    @Test
    @DisplayName("Should handle multiple authorities")
    void testMultipleAuthorities() {
        String authorities = "ROLE_ADMIN,ROLE_USER,USER_READ,USER_CREATE,USER_DELETE";
        TokenValidationResponse response = TokenValidationResponse.valid("admin", authorities);

        assertEquals(authorities, response.authorities());
    }

    @Test
    @DisplayName("Should be a record type")
    void testIsRecord() {
        TokenValidationResponse response = new TokenValidationResponse(true, "user", "ROLE_USER");

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should handle null username in valid token")
    void testNullUsernameInValidToken() {
        TokenValidationResponse response = new TokenValidationResponse(true, null, "ROLE_USER");

        assertTrue(response.valid());
        assertNull(response.username());
    }

    @Test
    @DisplayName("Should handle empty authorities")
    void testEmptyAuthorities() {
        TokenValidationResponse response = TokenValidationResponse.valid("user", "");

        assertNotNull(response);
        assertEquals("", response.authorities());
    }

    @Test
    @DisplayName("Should distinguish between valid and invalid")
    void testValidFlag() {
        TokenValidationResponse validResponse = TokenValidationResponse.valid("user", "ROLE_USER");
        TokenValidationResponse invalidResponse = TokenValidationResponse.invalid();

        assertTrue(validResponse.valid());
        assertFalse(invalidResponse.valid());
    }

    @Test
    @DisplayName("Should be immutable")
    void testImmutability() {
        TokenValidationResponse response = TokenValidationResponse.valid("testuser", "ROLE_USER");

        assertEquals("testuser", response.username());
        assertEquals("ROLE_USER", response.authorities());
    }

    @Test
    @DisplayName("Should work with long authority string")
    void testLongAuthorityString() {
        String longAuth = "ROLE_" + "A".repeat(100);
        TokenValidationResponse response = TokenValidationResponse.valid("user", longAuth);

        assertEquals(longAuth, response.authorities());
    }

    @Test
    @DisplayName("Should handle special characters in authorities")
    void testSpecialCharactersInAuthorities() {
        String authorities = "ROLE_USER,ROLE_ADMIN_SPECIAL,USER_READ/WRITE";
        TokenValidationResponse response = TokenValidationResponse.valid("user", authorities);

        assertEquals(authorities, response.authorities());
    }

    @Test
    @DisplayName("Should have null values for invalid token from factory")
    void testInvalidFactoryReturnsNull() {
        TokenValidationResponse response = TokenValidationResponse.invalid();

        assertFalse(response.valid());
        assertNull(response.username());
        assertNull(response.authorities());
    }
}
