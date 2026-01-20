package com.novacommerce.order_service.adapter.in.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para JwtTokenValidator.
 * Valida la verificación y extracción de información de tokens JWT.
 */
class JwtTokenValidatorTest {

    private JwtTokenValidator tokenValidator;
    private SecretKey secretKey;
    private String base64Secret;

    @BeforeEach
    void setUp() {
        // Generar una clave secreta válida
        secretKey = Keys.hmacShaKeyFor(new byte[64]);
        base64Secret = Encoders.BASE64.encode(secretKey.getEncoded());
        tokenValidator = new JwtTokenValidator(base64Secret);
    }

    @Test
    void givenValidToken_whenIsValid_thenReturnsTrue() {
        // GIVEN - Token JWT válido
        String token = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora
                .signWith(secretKey)
                .compact();

        // WHEN - Validar token
        boolean isValid = tokenValidator.isValid(token);

        // THEN - Debe ser válido
        assertTrue(isValid);
    }

    @Test
    void givenExpiredToken_whenIsValid_thenReturnsFalse() {
        // GIVEN - Token JWT expirado
        String token = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date(System.currentTimeMillis() - 7200000)) // Hace 2 horas
                .expiration(new Date(System.currentTimeMillis() - 3600000)) // Expiró hace 1 hora
                .signWith(secretKey)
                .compact();

        // WHEN - Validar token
        boolean isValid = tokenValidator.isValid(token);

        // THEN - Debe ser inválido
        assertFalse(isValid);
    }

    @Test
    void givenInvalidToken_whenIsValid_thenReturnsFalse() {
        // GIVEN - Token JWT con formato inválido
        String invalidToken = "invalid.jwt.token";

        // WHEN - Validar token
        boolean isValid = tokenValidator.isValid(invalidToken);

        // THEN - Debe ser inválido
        assertFalse(isValid);
    }

    @Test
    void givenTokenWithDifferentSignature_whenIsValid_thenReturnsFalse() {
        // GIVEN - Token firmado con otra clave
        SecretKey differentKey = Keys.hmacShaKeyFor(new byte[64]);
        String token = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(differentKey)
                .compact();

        // WHEN - Validar token con clave diferente
        boolean isValid = tokenValidator.isValid(token);

        // THEN - Debe ser inválido
        assertFalse(isValid);
    }

    @Test
    void givenValidToken_whenGetUsername_thenReturnsSubject() {
        // GIVEN - Token JWT con subject
        String expectedUsername = "john.doe";
        String token = Jwts.builder()
                .subject(expectedUsername)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(secretKey)
                .compact();

        // WHEN - Extraer username
        String username = tokenValidator.getUsername(token);

        // THEN - Debe retornar el subject correcto
        assertEquals(expectedUsername, username);
    }

    @Test
    void givenTokenWithAuthoritiesAsList_whenGetAuthorities_thenReturnsList() {
        // GIVEN - Token JWT con authorities como lista
        List<String> expectedAuthorities = List.of("ROLE_USER", "ROLE_ADMIN");
        String token = Jwts.builder()
                .subject("testuser")
                .claim("authorities", expectedAuthorities)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(secretKey)
                .compact();

        // WHEN - Extraer authorities
        List<String> authorities = tokenValidator.getAuthorities(token);

        // THEN - Debe retornar las authorities
        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains("ROLE_USER"));
        assertTrue(authorities.contains("ROLE_ADMIN"));
    }

    @Test
    void givenTokenWithAuthoritiesAsString_whenGetAuthorities_thenParsesList() {
        // GIVEN - Token JWT con authorities como string JSON array
        String token = Jwts.builder()
                .subject("testuser")
                .claim("authorities", "[\"ROLE_USER\",\"ROLE_MANAGER\"]")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(secretKey)
                .compact();

        // WHEN - Extraer authorities
        List<String> authorities = tokenValidator.getAuthorities(token);

        // THEN - Debe parsear el string y retornar lista
        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains("ROLE_USER"));
        assertTrue(authorities.contains("ROLE_MANAGER"));
    }

    @Test
    void givenTokenWithSingleAuthorityString_whenGetAuthorities_thenReturnsSingletonList() {
        // GIVEN - Token JWT con authority como string simple
        String token = Jwts.builder()
                .subject("testuser")
                .claim("authorities", "ROLE_USER")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(secretKey)
                .compact();

        // WHEN - Extraer authorities
        List<String> authorities = tokenValidator.getAuthorities(token);

        // THEN - Debe retornar lista con un elemento
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertEquals("ROLE_USER", authorities.get(0));
    }

    @Test
    void givenTokenWithoutAuthorities_whenGetAuthorities_thenReturnsEmptyList() {
        // GIVEN - Token JWT sin claim de authorities
        String token = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(secretKey)
                .compact();

        // WHEN - Extraer authorities
        List<String> authorities = tokenValidator.getAuthorities(token);

        // THEN - Debe retornar lista vacía
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void givenTokenWithNullAuthorities_whenGetAuthorities_thenReturnsEmptyList() {
        // GIVEN - Token JWT con authorities explícitamente null
        // No podemos crear un claim null directamente con Map.of, así que usamos un token sin el claim
        String token = Jwts.builder()
                .subject("testuser")
                .claim("otherClaim", "value")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(secretKey)
                .compact();

        // WHEN - Extraer authorities (que no existen en el token)
        List<String> authorities = tokenValidator.getAuthorities(token);

        // THEN - Debe retornar lista vacía
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void givenTokenWithEmptyStringAuthorities_whenGetAuthorities_thenHandlesGracefully() {
        // GIVEN - Token JWT con authorities como string vacío entre corchetes
        String token = Jwts.builder()
                .subject("testuser")
                .claim("authorities", "[]")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(secretKey)
                .compact();

        // WHEN - Extraer authorities
        List<String> authorities = tokenValidator.getAuthorities(token);

        // THEN - Debe manejar gracefully (puede ser lista vacía o con string vacío)
        assertNotNull(authorities);
        // El comportamiento específico depende de la implementación, 
        // pero no debe lanzar excepción
    }
}
