package com.novacommerce.gateway.adapter.out.jwt;

import com.novacommerce.gateway.domain.exception.AuthenticationException;
import com.novacommerce.gateway.domain.model.Token;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "app.jwt.secret=dGhpc2lzYXRlc3RzZWNyZXRrZXlmb3JqanN0dGVzdGluZ3B1cnBvc2VzMTIz"
})
class JwtTokenValidatorAdapterTest {

    @Autowired
    private JwtTokenValidatorAdapter tokenValidator;

    private String validToken;
    private String expiredToken;
    private String invalidToken;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode("dGhpc2lzYXRlc3RzZWNyZXRrZXlmb3JqanN0dGVzdGluZ3B1cnBvc2VzMTIz"));
        
        // Create valid token
        validToken = Jwts.builder()
            .subject("testuser")
            .claim("authorities", "ROLE_USER,ROLE_ADMIN")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(secretKey)
            .compact();

        // Create expired token
        expiredToken = Jwts.builder()
            .subject("testuser")
            .claim("authorities", "ROLE_USER")
            .issuedAt(new Date(System.currentTimeMillis() - 7200000))
            .expiration(new Date(System.currentTimeMillis() - 3600000))
            .signWith(secretKey)
            .compact();

        invalidToken = "invalid.token.value";
    }

    @Test
    void testValidateTokenWithValidToken() {
        assertTrue(tokenValidator.validateToken(validToken), "Valid token should return true");
    }

    @Test
    void testValidateTokenWithExpiredToken() {
        assertFalse(tokenValidator.validateToken(expiredToken), "Expired token should return false");
    }

    @Test
    void testValidateTokenWithInvalidToken() {
        assertFalse(tokenValidator.validateToken(invalidToken), "Invalid token should return false");
    }

    @Test
    void testValidateTokenWithEmptyString() {
        assertFalse(tokenValidator.validateToken(""), "Empty token should return false");
    }

    @Test
    void testValidateTokenWithNull() {
        // The implementation should handle null gracefully and return false rather than throw
        boolean result = tokenValidator.validateToken(null);
        assertFalse(result, "Null token should return false");
    }

    @Test
    void testExtractUsernameFromValidToken() {
        String username = tokenValidator.extractUsername(validToken);
        assertEquals("testuser", username, "Should extract correct username");
    }

    @Test
    void testExtractUsernameFromInvalidToken() {
        assertThrows(AuthenticationException.class, () -> tokenValidator.extractUsername(invalidToken),
            "Should throw AuthenticationException for invalid token");
    }

    @Test
    void testExtractAuthoritiesFromValidToken() {
        String authorities = tokenValidator.extractAuthorities(validToken);
        assertEquals("ROLE_USER,ROLE_ADMIN", authorities, "Should extract correct authorities");
    }

    @Test
    void testExtractAuthoritiesFromTokenWithoutAuthorities() {
        String tokenWithoutAuthorities = Jwts.builder()
            .subject("testuser")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(secretKey)
            .compact();

        String authorities = tokenValidator.extractAuthorities(tokenWithoutAuthorities);
        assertEquals("", authorities, "Should return empty string when authorities are not present");
    }

    @Test
    void testExtractAuthoritiesFromInvalidToken() {
        assertThrows(AuthenticationException.class, () -> tokenValidator.extractAuthorities(invalidToken),
            "Should throw AuthenticationException for invalid token");
    }

    @Test
    void testExtractTokenFromValidToken() {
        Token token = tokenValidator.extractToken(validToken);

        assertNotNull(token, "Token should not be null");
        assertEquals("testuser", token.getUsername(), "Username should match");
        assertEquals("ROLE_USER,ROLE_ADMIN", String.join(",", token.getAuthorities()), "Authorities should match");
        assertTrue(token.isValid(), "Token should be valid");
        assertNotNull(token.getExpirationTime(), "Expiration time should not be null");
    }

    @Test
    void testExtractTokenFromInvalidToken() {
        assertThrows(AuthenticationException.class, () -> tokenValidator.extractToken(invalidToken),
            "Should throw AuthenticationException for invalid token");
    }

    @Test
    void testTokenModelEquality() {
        Token token1 = tokenValidator.extractToken(validToken);
        Token token2 = tokenValidator.extractToken(validToken);

        assertEquals(token1.getUsername(), token2.getUsername(), "Usernames should match");
        assertEquals(token1.getAuthorities(), token2.getAuthorities(), "Authorities should match");
    }

    @Test
    void testExtractTokenWithAuthoritiesAsList() {
        String tokenWithListAuthorities = Jwts.builder()
            .subject("testuser")
            .claim("authorities", List.of("ROLE_USER", "ROLE_ADMIN"))
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(secretKey)
            .compact();

        Token token = tokenValidator.extractToken(tokenWithListAuthorities);
        assertEquals(2, token.getAuthorities().size(), "Should have 2 authorities");
        assertTrue(token.getAuthorities().contains("ROLE_USER"), "Should contain ROLE_USER");
        assertTrue(token.getAuthorities().contains("ROLE_ADMIN"), "Should contain ROLE_ADMIN");
    }

    @Test
    void testMalformedToken() {
        assertFalse(tokenValidator.validateToken("malformed.token"), "Malformed token should return false");
    }

    @Test
    void testValidateTokenReturnsBooleanNotException() {
        boolean result = tokenValidator.validateToken(invalidToken);
        assertFalse(result, "validateToken should return false, not throw exception");
    }
}
