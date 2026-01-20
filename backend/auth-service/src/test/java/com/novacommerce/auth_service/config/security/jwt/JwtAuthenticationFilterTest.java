package com.novacommerce.auth_service.config.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter Tests")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should set authentication in security context with valid token")
    void testDoFilterInternalWithValidToken() throws ServletException, IOException {
        // Given
        String token = "valid-jwt-token";
        String username = "testuser";
        String authorities = "ROLE_ADMIN,ROLE_USER";

        when(request.getHeader(JwtConstants.AUTHORIZATION_HEADER))
            .thenReturn("Bearer " + token);
        when(tokenProvider.extractTokenFromHeader("Bearer " + token))
            .thenReturn(token);
        when(tokenProvider.validateToken(token))
            .thenReturn(true);
        when(tokenProvider.getUsernameFromToken(token))
            .thenReturn(username);
        when(tokenProvider.getAuthoritiesFromToken(token))
            .thenReturn(authorities);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not set authentication with invalid token")
    void testDoFilterInternalWithInvalidToken() throws ServletException, IOException {
        // Given
        String token = "invalid-token";

        when(request.getHeader(JwtConstants.AUTHORIZATION_HEADER))
            .thenReturn("Bearer " + token);
        when(tokenProvider.extractTokenFromHeader("Bearer " + token))
            .thenReturn(token);
        when(tokenProvider.validateToken(token))
            .thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should skip authentication when no Authorization header is present")
    void testDoFilterInternalWithoutAuthorizationHeader() throws ServletException, IOException {
        // Given
        when(request.getHeader(JwtConstants.AUTHORIZATION_HEADER))
            .thenReturn(null);
        when(tokenProvider.extractTokenFromHeader(null))
            .thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle exception gracefully and continue filter chain")
    void testDoFilterInternalWithException() throws ServletException, IOException {
        // Given
        String token = "problematic-token";

        when(request.getHeader(JwtConstants.AUTHORIZATION_HEADER))
            .thenReturn("Bearer " + token);
        when(tokenProvider.extractTokenFromHeader("Bearer " + token))
            .thenReturn(token);
        when(tokenProvider.validateToken(token))
            .thenThrow(new RuntimeException("JWT validation error"));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle token with no authorities")
    void testDoFilterInternalWithNoAuthorities() throws ServletException, IOException {
        // Given
        String token = "token-no-auth";
        String username = "testuser";

        when(request.getHeader(JwtConstants.AUTHORIZATION_HEADER))
            .thenReturn("Bearer " + token);
        when(tokenProvider.extractTokenFromHeader("Bearer " + token))
            .thenReturn(token);
        when(tokenProvider.validateToken(token))
            .thenReturn(true);
        when(tokenProvider.getUsernameFromToken(token))
            .thenReturn(username);
        when(tokenProvider.getAuthoritiesFromToken(token))
            .thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().isEmpty());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle token with empty authorities string")
    void testDoFilterInternalWithEmptyAuthorities() throws ServletException, IOException {
        // Given
        String token = "token-empty-auth";
        String username = "testuser";

        when(request.getHeader(JwtConstants.AUTHORIZATION_HEADER))
            .thenReturn("Bearer " + token);
        when(tokenProvider.extractTokenFromHeader("Bearer " + token))
            .thenReturn(token);
        when(tokenProvider.validateToken(token))
            .thenReturn(true);
        when(tokenProvider.getUsernameFromToken(token))
            .thenReturn(username);
        when(tokenProvider.getAuthoritiesFromToken(token))
            .thenReturn("");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().isEmpty());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should parse multiple authorities correctly")
    void testDoFilterInternalWithMultipleAuthorities() throws ServletException, IOException {
        // Given
        String token = "token-multi-auth";
        String username = "testuser";
        String authorities = "ROLE_ADMIN,ROLE_MANAGER,ROLE_USER";

        when(request.getHeader(JwtConstants.AUTHORIZATION_HEADER))
            .thenReturn("Bearer " + token);
        when(tokenProvider.extractTokenFromHeader("Bearer " + token))
            .thenReturn(token);
        when(tokenProvider.validateToken(token))
            .thenReturn(true);
        when(tokenProvider.getUsernameFromToken(token))
            .thenReturn(username);
        when(tokenProvider.getAuthoritiesFromToken(token))
            .thenReturn(authorities);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(username, auth.getName());
        assertEquals(3, auth.getAuthorities().size());
        assertTrue(auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER")));
        assertTrue(auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle whitespace in authorities")
    void testDoFilterInternalWithWhitespaceInAuthorities() throws ServletException, IOException {
        // Given
        String token = "token-whitespace-auth";
        String username = "testuser";
        String authorities = "ROLE_ADMIN , ROLE_USER , ROLE_MANAGER";

        when(request.getHeader(JwtConstants.AUTHORIZATION_HEADER))
            .thenReturn("Bearer " + token);
        when(tokenProvider.extractTokenFromHeader("Bearer " + token))
            .thenReturn(token);
        when(tokenProvider.validateToken(token))
            .thenReturn(true);
        when(tokenProvider.getUsernameFromToken(token))
            .thenReturn(username);
        when(tokenProvider.getAuthoritiesFromToken(token))
            .thenReturn(authorities);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        // Should trim whitespace properly
        assertEquals(3, auth.getAuthorities().size());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should continue filter chain even if authentication fails")
    void testDoFilterInternalContinuesChainOnError() throws ServletException, IOException {
        // Given
        when(request.getHeader(JwtConstants.AUTHORIZATION_HEADER))
            .thenThrow(new RuntimeException("Request header error"));

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle authentication with special characters in username")
    void testDoFilterInternalWithSpecialCharactersInUsername() throws ServletException, IOException {
        // Given
        String token = "token-special-username";
        String username = "test.user+special@domain";
        String authorities = "ROLE_USER";

        when(request.getHeader(JwtConstants.AUTHORIZATION_HEADER))
            .thenReturn("Bearer " + token);
        when(tokenProvider.extractTokenFromHeader("Bearer " + token))
            .thenReturn(token);
        when(tokenProvider.validateToken(token))
            .thenReturn(true);
        when(tokenProvider.getUsernameFromToken(token))
            .thenReturn(username);
        when(tokenProvider.getAuthoritiesFromToken(token))
            .thenReturn(authorities);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
