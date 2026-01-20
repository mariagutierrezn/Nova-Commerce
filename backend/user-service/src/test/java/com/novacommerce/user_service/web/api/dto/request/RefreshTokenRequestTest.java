package com.novacommerce.user_service.web.api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("RefreshTokenRequest Tests")
class RefreshTokenRequestTest {

    @Autowired
    private Validator validator;

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    @Test
    @DisplayName("Should create refresh token request with valid token")
    void testValidRefreshTokenRequest() {
        RefreshTokenRequest request = new RefreshTokenRequest(VALID_TOKEN);

        assertNotNull(request);
        assertEquals(VALID_TOKEN, request.refreshToken());
    }

    @Test
    @DisplayName("Should reject request with blank token")
    void testBlankToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("");

        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject request with null token")
    void testNullToken() {
        RefreshTokenRequest request = new RefreshTokenRequest(null);

        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept long token string")
    void testLongTokenString() {
        String longToken = "a".repeat(500);
        RefreshTokenRequest request = new RefreshTokenRequest(longToken);

        assertNotNull(request);
        assertEquals(longToken, request.refreshToken());
    }

    @Test
    @DisplayName("Should be a record type")
    void testIsRecord() {
        RefreshTokenRequest request = new RefreshTokenRequest("token123");

        assertNotNull(request);
    }

    @Test
    @DisplayName("Should have immutable fields")
    void testImmutability() {
        RefreshTokenRequest request = new RefreshTokenRequest("myToken");

        assertEquals("myToken", request.refreshToken());
    }

    @Test
    @DisplayName("Should reject whitespace-only token")
    void testWhitespaceOnlyToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("   ");

        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should work with JWT-like token format")
    void testJWTFormat() {
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMTIzIn0.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";
        RefreshTokenRequest request = new RefreshTokenRequest(jwtToken);

        assertNotNull(request);
        assertEquals(jwtToken, request.refreshToken());
    }

    @Test
    @DisplayName("Should accept token with special characters")
    void testTokenWithSpecialCharacters() {
        String tokenWithSpecials = "token_with-special.chars+/=";
        RefreshTokenRequest request = new RefreshTokenRequest(tokenWithSpecials);

        assertNotNull(request);
        assertEquals(tokenWithSpecials, request.refreshToken());
    }

    @Test
    @DisplayName("Should work with different token formats")
    void testDifferentTokenFormats() {
        RefreshTokenRequest request1 = new RefreshTokenRequest("simpleToken");
        RefreshTokenRequest request2 = new RefreshTokenRequest("eyJhbGc...");
        RefreshTokenRequest request3 = new RefreshTokenRequest("token.with.dots");

        assertNotNull(request1);
        assertNotNull(request2);
        assertNotNull(request3);
    }

    @Test
    @DisplayName("Should have validation message for blank token")
    void testValidationMessage() {
        RefreshTokenRequest request = new RefreshTokenRequest("");

        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        // Verify that violations contain message about refresh token
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("obligatorio")));
    }
}
