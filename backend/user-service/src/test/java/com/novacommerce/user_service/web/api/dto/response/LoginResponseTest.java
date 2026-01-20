package com.novacommerce.user_service.web.api.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginResponse Tests")
class LoginResponseTest {

    @Test
    @DisplayName("Should create LoginResponse with all fields")
    void testCreateWithAllFields() {
        LoginResponse response = new LoginResponse(
            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ",
            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.refresh_token_example",
            "Bearer",
            3600L
        );

        assertNotNull(response);
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600L, response.expiresIn());
    }

    @Test
    @DisplayName("Should use factory method for bearer token")
    void testBearerFactory() {
        String accessToken = "access_token_123";
        String refreshToken = "refresh_token_456";
        Long expiresIn = 7200L;

        LoginResponse response = LoginResponse.bearer(accessToken, refreshToken, expiresIn);

        assertNotNull(response);
        assertEquals(accessToken, response.accessToken());
        assertEquals(refreshToken, response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(expiresIn, response.expiresIn());
    }

    @Test
    @DisplayName("Should have Bearer token type from factory")
    void testBearerTokenType() {
        LoginResponse response = LoginResponse.bearer("token1", "token2", 3600L);

        assertEquals("Bearer", response.tokenType());
    }

    @Test
    @DisplayName("Should be a record type")
    void testIsRecord() {
        LoginResponse response = new LoginResponse("a", "b", "Bearer", 3600L);

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should have all fields with valid values")
    void testValidValues() {
        LoginResponse response = new LoginResponse(
            "validAccessToken",
            "validRefreshToken",
            "Bearer",
            1800L
        );

        assertEquals("validAccessToken", response.accessToken());
        assertEquals("validRefreshToken", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(1800L, response.expiresIn());
    }

    @Test
    @DisplayName("Should handle large expiration times")
    void testLargeExpirationTime() {
        LoginResponse response = new LoginResponse("token", "refresh", "Bearer", 86400000L);

        assertEquals(86400000L, response.expiresIn());
    }

    @Test
    @DisplayName("Should handle minimal expiration times")
    void testMinimalExpirationTime() {
        LoginResponse response = new LoginResponse("token", "refresh", "Bearer", 1L);

        assertEquals(1L, response.expiresIn());
    }

    @Test
    @DisplayName("Should handle null tokens")
    void testNullTokens() {
        LoginResponse response = new LoginResponse(null, null, "Bearer", 3600L);

        assertNull(response.accessToken());
        assertNull(response.refreshToken());
    }

    @Test
    @DisplayName("Should work with different token types")
    void testDifferentTokenTypes() {
        LoginResponse response1 = new LoginResponse("token", "refresh", "Bearer", 3600L);
        LoginResponse response2 = new LoginResponse("token", "refresh", "Basic", 3600L);

        assertEquals("Bearer", response1.tokenType());
        assertEquals("Basic", response2.tokenType());
    }

    @Test
    @DisplayName("Should be immutable")
    void testImmutability() {
        LoginResponse response = LoginResponse.bearer("token1", "token2", 3600L);

        assertEquals("token1", response.accessToken());
        assertEquals("token2", response.refreshToken());
    }
}
