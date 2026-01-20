package com.novacommerce.auth_service.config.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    // Use a Base64-encoded secret that's at least 512 bits (64 bytes) for HS512
    private String jwtSecret = "VGhpcyBpcyBhIHRlc3Qgc2VjcmV0IGtleSB0aGF0IGlzIGxvbmcgZW5vdWdoIGZvciBIUzUxMiBhbGdvcml0aG0gYW5kIGlzIDY0IGJ5dGVzISEh";
    private long jwtExpirationMs = 86400000L;
    private long refreshTokenExpirationMs = 604800000L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", jwtExpirationMs);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpirationMs", refreshTokenExpirationMs);
    }

    @Test
    @DisplayName("Should generate a valid JWT token")
    void testGenerateToken() {
        // Given
        Authentication authentication = createAuthentication("testuser", "ROLE_ADMIN,ROLE_USER");

        // When
        String token = jwtTokenProvider.generateToken(authentication, null);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Should generate a valid refresh token")
    void testGenerateRefreshToken() {
        // Given
        String username = "testuser";

        // When
        String refreshToken = jwtTokenProvider.generateRefreshToken(username);

        // Then
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        assertTrue(jwtTokenProvider.validateToken(refreshToken));
    }

    @Test
    @DisplayName("Should extract username from valid token")
    void testGetUsernameFromToken() {
        // Given
        String username = "testuser";
        Authentication authentication = createAuthentication(username, "ROLE_ADMIN");
        String token = jwtTokenProvider.generateToken(authentication, null);

        // When
        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);

        // Then
        assertNotNull(extractedUsername);
        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Should extract authorities from valid token")
    void testGetAuthoritiesFromToken() {
        // Given
        Authentication authentication = createAuthentication("testuser", "ROLE_ADMIN,ROLE_USER");
        String token = jwtTokenProvider.generateToken(authentication, null);

        // When
        String authorities = jwtTokenProvider.getAuthoritiesFromToken(token);

        // Then
        assertNotNull(authorities);
        assertTrue(authorities.contains("ROLE_ADMIN"));
        assertTrue(authorities.contains("ROLE_USER"));
    }

    @Test
    @DisplayName("Should validate a valid token")
    void testValidateToken() {
        // Given
        Authentication authentication = createAuthentication("testuser", "ROLE_ADMIN");
        String token = jwtTokenProvider.generateToken(authentication, null);

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should invalidate an invalid token")
    void testValidateInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should invalidate a malformed token")
    void testValidateMalformedToken() {
        // Given
        String malformedToken = "malformed";

        // When
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should invalidate an expired token")
    void testValidateExpiredToken() {
        // Given - Create an expired token
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", -1000L);
        Authentication authentication = createAuthentication("testuser", "ROLE_ADMIN");
        String expiredToken = jwtTokenProvider.generateToken(authentication, null);
        
        // Reset expiration for later tests
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", jwtExpirationMs);

        // When
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should extract token from Bearer header")
    void testExtractTokenFromHeader() {
        // Given
        String authorizationHeader = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";

        // When
        String token = jwtTokenProvider.extractTokenFromHeader(authorizationHeader);

        // Then
        assertNotNull(token);
        assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token", token);
    }

    @Test
    @DisplayName("Should return null when header is null")
    void testExtractTokenFromNullHeader() {
        // When
        String token = jwtTokenProvider.extractTokenFromHeader(null);

        // Then
        assertNull(token);
    }

    @Test
    @DisplayName("Should return null when header doesn't start with Bearer")
    void testExtractTokenFromInvalidHeader() {
        // Given
        String authorizationHeader = "Basic invalid";

        // When
        String token = jwtTokenProvider.extractTokenFromHeader(authorizationHeader);

        // Then
        assertNull(token);
    }

    @Test
    @DisplayName("Should handle empty authorities in token")
    void testGetAuthoritiesFromTokenEmpty() {
        // Given
        Authentication authentication = createAuthentication("testuser", "");
        String token = jwtTokenProvider.generateToken(authentication, null);

        // When
        String authorities = jwtTokenProvider.getAuthoritiesFromToken(token);

        // Then
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty() || authorities.isBlank());
    }

    @Test
    @DisplayName("Should generate different tokens for different times")
    void testGenerateTokenWithDifferentContent() {
        // Given
        Authentication auth1 = createAuthentication("user1", "ROLE_ADMIN");
        Authentication auth2 = createAuthentication("user2", "ROLE_USER");

        // When
        String token1 = jwtTokenProvider.generateToken(auth1, null);
        String token2 = jwtTokenProvider.generateToken(auth2, null);

        // Then
        assertNotEquals(token1, token2);
        assertEquals("user1", jwtTokenProvider.getUsernameFromToken(token1));
        assertEquals("user2", jwtTokenProvider.getUsernameFromToken(token2));
    }

    @Test
    @DisplayName("Should handle Base64 encoded secret")
    void testGenerateTokenWithBase64Secret() {
        // Given - Use a Base64 encoded secret (needs to be at least 512 bits / 64 bytes for HS512)
        String longSecret = "This is a test secret key that is long enough for HS512 algorithm and is 64 bytes!!!";
        String base64Secret = Base64.getEncoder().encodeToString(longSecret.getBytes());
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", base64Secret);
        
        Authentication authentication = createAuthentication("testuser", "ROLE_ADMIN");

        // When
        String token = jwtTokenProvider.generateToken(authentication, null);

        // Then
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Should handle non-Base64 secret")
    void testGenerateTokenWithNonBase64Secret() {
        // Given - Use a non-Base64 secret (needs to be long enough)
        String plainSecret = "this-is-a-long-non-base64-secret-key-that-is-long-enough-for-hs512-algorithm";
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", plainSecret);
        
        Authentication authentication = createAuthentication("testuser", "ROLE_USER");

        // When
        String token = jwtTokenProvider.generateToken(authentication, null);

        // Then
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("testuser", jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Should preserve multiple authorities in token")
    void testMultipleAuthorities() {
        // Given
        String authorities = "ROLE_ADMIN,ROLE_MANAGER,ROLE_USER";
        Authentication authentication = createAuthentication("testuser", authorities);
        String token = jwtTokenProvider.generateToken(authentication, null);

        // When
        String extractedAuthorities = jwtTokenProvider.getAuthoritiesFromToken(token);

        // Then
        assertNotNull(extractedAuthorities);
        assertTrue(extractedAuthorities.contains("ROLE_ADMIN"));
        assertTrue(extractedAuthorities.contains("ROLE_MANAGER"));
        assertTrue(extractedAuthorities.contains("ROLE_USER"));
    }

    /**
     * Helper method to create an Authentication object for testing.
     */
    private Authentication createAuthentication(String username, String authorities) {
        List<SimpleGrantedAuthority> authoritiesList = new ArrayList<>();
        if (authorities != null && !authorities.isEmpty()) {
            String[] authArray = authorities.split(",");
            for (String auth : authArray) {
                authoritiesList.add(new SimpleGrantedAuthority(auth.trim()));
            }
        }

        return new org.springframework.security.core.Authentication() {
            @Override
            public Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
                return authoritiesList;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return username;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
                // Empty implementation for test Authentication mock
            }

            @Override
            public String getName() {
                return username;
            }
        };
    }
}

