package com.novacommerce.customer_service.adapter.in.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilterTest")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenValidatorAdapter tokenValidator;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private SecretKey signingKey;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        String secret = Base64.getEncoder().encodeToString("my-super-secret-key-for-testing-purposes".getBytes(StandardCharsets.UTF_8));
        signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    @Test
    @DisplayName("givenValidBearerToken_whenDoFilter_thenAuthenticationSet")
    void givenValidBearerToken_whenDoFilter_thenAuthenticationSet() throws ServletException, IOException {
        // GIVEN
        String token = Jwts.builder()
            .subject("user@example.com")
            .claim("authorities", "ROLE_USER,ROLE_ADMIN")
            .signWith(signingKey)
            .compact();

        Claims claims = Jwts.claims()
            .subject("user@example.com")
            .add("authorities", "ROLE_USER,ROLE_ADMIN")
            .build();

        when(request.getHeader(HttpHeaders.AUTHORIZATION))
            .thenReturn("Bearer " + token);
        when(tokenValidator.validate(token)).thenReturn(claims);
        when(tokenValidator.extractAuthorities(claims)).thenReturn(List.of("ROLE_USER", "ROLE_ADMIN"));

        // WHEN
        filter.doFilterInternal(request, response, filterChain);

        // THEN
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("user@example.com", auth.getPrincipal());
        assertEquals(2, auth.getAuthorities().size());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("givenInvalidBearerToken_whenDoFilter_thenFilterContinues")
    void givenInvalidBearerToken_whenDoFilter_thenFilterContinues() throws ServletException, IOException {
        // GIVEN - Invalid token that will throw exception
        when(request.getHeader(HttpHeaders.AUTHORIZATION))
            .thenReturn("Bearer invalid");
        when(tokenValidator.validate("invalid")).thenThrow(new RuntimeException("Invalid token"));

        // WHEN - Filter should catch exception and continue
        filter.doFilterInternal(request, response, filterChain);

        // THEN - Exception is caught, filter continues without authentication
        verify(tokenValidator).validate("invalid");
        verify(filterChain).doFilter(request, response);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    @DisplayName("givenMissingAuthorizationHeader_whenDoFilter_thenFilterContinues")
    void givenMissingAuthorizationHeader_whenDoFilter_thenFilterContinues() throws ServletException, IOException {
        // GIVEN
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        // WHEN
        filter.doFilterInternal(request, response, filterChain);

        // THEN
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain).doFilter(request, response);
        verify(tokenValidator, never()).validate(anyString());
    }

    @Test
    @DisplayName("givenNonBearerToken_whenDoFilter_thenFilterContinues")
    void givenNonBearerToken_whenDoFilter_thenFilterContinues() throws ServletException, IOException {
        // GIVEN
        when(request.getHeader(HttpHeaders.AUTHORIZATION))
            .thenReturn("Basic sometoken");

        // WHEN
        filter.doFilterInternal(request, response, filterChain);

        // THEN
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain).doFilter(request, response);
        verify(tokenValidator, never()).validate(anyString());
    }

    @Test
    @DisplayName("givenBearerPrefixNoToken_whenDoFilter_thenFilterContinues")
    void givenBearerPrefixNoToken_whenDoFilter_thenFilterContinues() throws ServletException, IOException {
        // GIVEN - Bearer with space and no token means empty token string
        when(request.getHeader(HttpHeaders.AUTHORIZATION))
            .thenReturn("Bearer ");
        // Empty token will throw exception during validation, which is caught

        // WHEN
        filter.doFilterInternal(request, response, filterChain);

        // THEN
        // Filter should continue chain even after exception
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("givenValidTokenWithNoAuthorities_whenDoFilter_thenAuthenticationSetWithEmptyAuthorities")
    void givenValidTokenWithNoAuthorities_whenDoFilter_thenAuthenticationSetWithEmptyAuthorities() throws ServletException, IOException {
        // GIVEN
        String token = "validtoken";
        Claims claims = Jwts.claims()
            .subject("user@example.com")
            .build();

        when(request.getHeader(HttpHeaders.AUTHORIZATION))
            .thenReturn("Bearer " + token);
        when(tokenValidator.validate(token)).thenReturn(claims);
        when(tokenValidator.extractAuthorities(claims)).thenReturn(List.of());

        // WHEN
        filter.doFilterInternal(request, response, filterChain);

        // THEN
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("user@example.com", auth.getPrincipal());
        assertEquals(0, auth.getAuthorities().size());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("givenEmptyAuthorizationHeader_whenDoFilter_thenFilterContinues")
    void givenEmptyAuthorizationHeader_whenDoFilter_thenFilterContinues() throws ServletException, IOException {
        // GIVEN
        when(request.getHeader(HttpHeaders.AUTHORIZATION))
            .thenReturn("");

        // WHEN
        filter.doFilterInternal(request, response, filterChain);

        // THEN
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain).doFilter(request, response);
        verify(tokenValidator, never()).validate(anyString());
    }
}
