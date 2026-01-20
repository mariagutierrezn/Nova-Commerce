package com.novacommerce.auth_service.application.service;

import com.novacommerce.auth_service.application.port.out.TokenGeneratorPort;
import com.novacommerce.auth_service.application.port.out.UserValidationPort;
import com.novacommerce.auth_service.client.dto.UserValidationRequest;
import com.novacommerce.auth_service.client.dto.UserValidationResponse;
 
import com.novacommerce.auth_service.web.rest.exceptions.InvalidCredentialsException;
import com.novacommerce.auth_service.web.rest.exceptions.InvalidTokenException;
import com.novacommerce.auth_service.web.api.dto.request.LoginRequest;
import com.novacommerce.auth_service.web.api.dto.request.RefreshTokenRequest;
import com.novacommerce.auth_service.web.api.dto.response.LoginResponse;
import com.novacommerce.auth_service.web.api.dto.response.TokenValidationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

    @Mock
    private UserValidationPort userValidationPort;

    @Mock
    private TokenGeneratorPort tokenGeneratorPort;

    @InjectMocks
    private AuthService authService;

    private long jwtExpirationMs = 86400000L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtExpirationMs", jwtExpirationMs);
    }

    @Test
    @DisplayName("Should authenticate user successfully")
    void testAuthenticateSuccess() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        
        UserValidationResponse validationResponse = new UserValidationResponse(
            "testuser",
            "test@email.com",
            true,
            false,
            Set.of("ADMIN", "MANAGER"),
            Set.of("USER_READ", "USER_WRITE"),
            null
        );

        when(userValidationPort.validateCredentials(any(UserValidationRequest.class)))
            .thenReturn(validationResponse);
        when(tokenGeneratorPort.generateToken(any(Authentication.class), any()))
            .thenReturn("access-token");
        when(tokenGeneratorPort.generateRefreshToken(anyString()))
            .thenReturn("refresh-token");

        // When
        LoginResponse response = authService.authenticate(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(jwtExpirationMs / 1000, response.expiresIn());
        assertEquals("testuser", response.username());
        
        verify(userValidationPort, times(1)).validateCredentials(any(UserValidationRequest.class));
        verify(tokenGeneratorPort, times(1)).generateToken(any(Authentication.class), any());
        verify(tokenGeneratorPort, times(1)).generateRefreshToken("testuser");
    }

    @Test
    @DisplayName("Should throw exception when user is disabled")
    void testAuthenticateUserDisabled() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        
        UserValidationResponse validationResponse = new UserValidationResponse(
            "testuser",
            "test@email.com",
            false,
            false,
            Set.of("USER"),
            Set.of(),
            null
        );

        when(userValidationPort.validateCredentials(any(UserValidationRequest.class)))
            .thenReturn(validationResponse);

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> 
            authService.authenticate(loginRequest)
        );
        
        verify(userValidationPort, times(1)).validateCredentials(any(UserValidationRequest.class));
        verify(tokenGeneratorPort, never()).generateToken(any(), any());
        verify(tokenGeneratorPort, never()).generateRefreshToken(anyString());
    }

    @Test
    @DisplayName("Should throw exception when user is locked")
    void testAuthenticateUserLocked() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        
        UserValidationResponse validationResponse = new UserValidationResponse(
            "testuser",
            "test@email.com",
            true,
            true, // locked
            Set.of("USER"),
            Set.of(),
            null
        );

        when(userValidationPort.validateCredentials(any(UserValidationRequest.class)))
            .thenReturn(validationResponse);

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> 
            authService.authenticate(loginRequest)
        );
        
        verify(userValidationPort, times(1)).validateCredentials(any(UserValidationRequest.class));
        verify(tokenGeneratorPort, never()).generateToken(any(), any());
        verify(tokenGeneratorPort, never()).generateRefreshToken(anyString());
    }

    @Test
    @DisplayName("Should throw exception when credentials are invalid")
    void testAuthenticateInvalidCredentials() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");
        
        when(userValidationPort.validateCredentials(any(UserValidationRequest.class)))
            .thenThrow(new InvalidCredentialsException("Credenciales inválidas"));

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> 
            authService.authenticate(loginRequest)
        );
        
        verify(userValidationPort, times(1)).validateCredentials(any(UserValidationRequest.class));
        verify(tokenGeneratorPort, never()).generateToken(any(), any());
        verify(tokenGeneratorPort, never()).generateRefreshToken(anyString());
    }

    @Test
    @DisplayName("Should include ROLE_ prefix for roles")
    void testAuthenticateRolePrefix() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        
        UserValidationResponse validationResponse = new UserValidationResponse(
            "testuser",
            "test@email.com",
            true,
            false,
            Set.of("ADMIN"),
            Set.of("USER_READ"),
            null
        );

        when(userValidationPort.validateCredentials(any(UserValidationRequest.class)))
            .thenReturn(validationResponse);
        when(tokenGeneratorPort.generateToken(any(Authentication.class), any()))
            .thenReturn("access-token");
        when(tokenGeneratorPort.generateRefreshToken(anyString()))
            .thenReturn("refresh-token");

        // When
        authService.authenticate(loginRequest);

        // Then
        verify(tokenGeneratorPort).generateToken(argThat(auth -> {
            String authorities = auth.getAuthorities().toString();
            return authorities.contains("ROLE_ADMIN") && authorities.contains("USER_READ");
        }), any());
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void testRefreshTokenSuccess() {
        // Given
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("valid-refresh-token");
        
        when(tokenGeneratorPort.validateToken("valid-refresh-token")).thenReturn(true);
        when(tokenGeneratorPort.getUsernameFromToken("valid-refresh-token")).thenReturn("testuser");
        when(tokenGeneratorPort.getAuthoritiesFromToken("valid-refresh-token"))
            .thenReturn("ROLE_ADMIN,USER_READ");
        when(tokenGeneratorPort.generateToken(any(Authentication.class), any()))
            .thenReturn("new-access-token");

        // When
        LoginResponse response = authService.refreshToken(refreshTokenRequest);

        // Then
        assertNotNull(response);
        assertEquals("new-access-token", response.accessToken());
        assertEquals("valid-refresh-token", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals("testuser", response.username());
        
        verify(tokenGeneratorPort, times(1)).validateToken("valid-refresh-token");
        verify(tokenGeneratorPort, times(1)).getUsernameFromToken("valid-refresh-token");
        verify(tokenGeneratorPort, times(1)).generateToken(any(Authentication.class), any());
    }

    @Test
    @DisplayName("Should throw exception when refresh token is invalid")
    void testRefreshTokenInvalid() {
        // Given
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("invalid-token");
        
        when(tokenGeneratorPort.validateToken("invalid-token")).thenReturn(false);

        // When & Then
        assertThrows(InvalidTokenException.class, () -> 
            authService.refreshToken(refreshTokenRequest)
        );
        
        verify(tokenGeneratorPort, times(1)).validateToken("invalid-token");
        verify(tokenGeneratorPort, never()).getUsernameFromToken(anyString());
        verify(tokenGeneratorPort, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("Should validate token successfully")
    void testValidateTokenSuccess() {
        // Given
        String token = "valid-token";
        
        when(tokenGeneratorPort.validateToken(token)).thenReturn(true);
        when(tokenGeneratorPort.getUsernameFromToken(token)).thenReturn("testuser");
        when(tokenGeneratorPort.getAuthoritiesFromToken(token)).thenReturn("ROLE_ADMIN,USER_READ");

        // When
        TokenValidationResponse response = authService.validateToken(token);

        // Then
        assertNotNull(response);
        assertTrue(response.valid());
        assertEquals("testuser", response.username());
        assertEquals("ROLE_ADMIN,USER_READ", response.authorities());
        
        verify(tokenGeneratorPort, times(1)).validateToken(token);
        verify(tokenGeneratorPort, times(1)).getUsernameFromToken(token);
        verify(tokenGeneratorPort, times(1)).getAuthoritiesFromToken(token);
    }

    @Test
    @DisplayName("Should return invalid when token validation fails")
    void testValidateTokenInvalid() {
        // Given
        String token = "invalid-token";
        
        when(tokenGeneratorPort.validateToken(token)).thenReturn(false);

        // When
        TokenValidationResponse response = authService.validateToken(token);

        // Then
        assertNotNull(response);
        assertFalse(response.valid());
        assertNull(response.username());
        assertNull(response.authorities());
        
        verify(tokenGeneratorPort, times(1)).validateToken(token);
        verify(tokenGeneratorPort, never()).getUsernameFromToken(anyString());
        verify(tokenGeneratorPort, never()).getAuthoritiesFromToken(anyString());
    }

    @Test
    @DisplayName("Should handle exception during token validation")
    void testValidateTokenException() {
        // Given
        String token = "problematic-token";
        
        when(tokenGeneratorPort.validateToken(token))
            .thenThrow(new RuntimeException("Unexpected error"));

        // When
        TokenValidationResponse response = authService.validateToken(token);

        // Then
        assertNotNull(response);
        assertFalse(response.valid());
        assertNull(response.username());
        assertNull(response.authorities());
    }

    @Test
    @DisplayName("Should handle empty roles and permissions")
    void testAuthenticateWithEmptyRolesAndPermissions() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        
        UserValidationResponse validationResponse = new UserValidationResponse(
            "testuser",
            "test@email.com",
            true,
            false,
            Set.<String>of(),
            Set.<String>of(), null);

        when(userValidationPort.validateCredentials(any(UserValidationRequest.class)))
            .thenReturn(validationResponse);
        when(tokenGeneratorPort.generateToken(any(Authentication.class), any()))
            .thenReturn("access-token");
        when(tokenGeneratorPort.generateRefreshToken(anyString()))
            .thenReturn("refresh-token");

        // When
        LoginResponse response = authService.authenticate(loginRequest);

        // Then
        assertNotNull(response);
        verify(tokenGeneratorPort).generateToken(any(Authentication.class), any());
    }

    @Test
    @DisplayName("Should refresh token with empty authorities")
    void testRefreshTokenWithEmptyAuthorities() {
        // Given
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("valid-refresh-token");
        
        when(tokenGeneratorPort.validateToken("valid-refresh-token")).thenReturn(true);
        when(tokenGeneratorPort.getUsernameFromToken("valid-refresh-token")).thenReturn("testuser");
        when(tokenGeneratorPort.getAuthoritiesFromToken("valid-refresh-token")).thenReturn("");
        when(tokenGeneratorPort.generateToken(any(Authentication.class), any()))
            .thenReturn("new-access-token");

        // When
        LoginResponse response = authService.refreshToken(refreshTokenRequest);

        // Then
        assertNotNull(response);
        assertEquals("new-access-token", response.accessToken());
    }

    @Test
    @DisplayName("Should refresh token with null authorities")
    void testRefreshTokenWithNullAuthorities() {
        // Given
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("valid-refresh-token");
        
        when(tokenGeneratorPort.validateToken("valid-refresh-token")).thenReturn(true);
        when(tokenGeneratorPort.getUsernameFromToken("valid-refresh-token")).thenReturn("testuser");
        when(tokenGeneratorPort.getAuthoritiesFromToken("valid-refresh-token")).thenReturn(null);
        when(tokenGeneratorPort.generateToken(any(Authentication.class), any()))
            .thenReturn("new-access-token");

        // When
        LoginResponse response = authService.refreshToken(refreshTokenRequest);

        // Then
        assertNotNull(response);
        assertEquals("new-access-token", response.accessToken());
    }
}



