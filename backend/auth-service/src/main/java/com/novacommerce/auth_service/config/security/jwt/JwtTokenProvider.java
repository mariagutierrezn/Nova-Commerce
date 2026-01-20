package com.novacommerce.auth_service.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Proveedor de tokens JWT.
 * Responsable de generar, validar y extraer información de tokens JWT.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${app.jwt.secret:nova-auth-secret-key-for-production-use-environment-variables}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration:604800000}")
    private long refreshTokenExpirationMs;

    /**
     * Genera un JWT a partir de la autenticación.
     *
     * @param authentication la autenticación del usuario
     * @param customerId ID del cliente asociado al usuario
     * @return el token JWT generado
     */
    public String generateToken(Authentication authentication, String customerId) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        String authoritiesString = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = getSigningKey();

        var builder = Jwts.builder()
            .setSubject(username)
            .claim(JwtConstants.AUTHORITIES_CLAIM, authoritiesString)
            .setIssuedAt(now)
            .setExpiration(expiryDate);
            
        if (customerId != null) {
            builder.claim("customerId", customerId);
        }
        
        return builder.signWith(key)
            .compact();
    }

    /**
     * Genera un refresh token.
     *
     * @param username el nombre de usuario
     * @return el refresh token generado
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

        SecretKey key = getSigningKey();

        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key)
            .compact();
    }

    /**
     * Extrae el nombre de usuario de un token JWT.
     *
     * @param token el token JWT
     * @return el nombre de usuario
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    /**
     * Extrae las autoridades de un token JWT.
     *
     * @param token el token JWT
     * @return las autoridades como string
     */
    public String getAuthoritiesFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get(JwtConstants.AUTHORITIES_CLAIM, String.class);
    }

    /**
     * Valida un token JWT.
     *
     * @param token el token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = getSigningKey();
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error(JwtConstants.INVALID_TOKEN_MESSAGE, ex);
        } catch (ExpiredJwtException ex) {
            log.error(JwtConstants.EXPIRED_TOKEN_MESSAGE, ex);
        } catch (UnsupportedJwtException ex) {
            log.error(JwtConstants.UNSUPPORTED_TOKEN_MESSAGE, ex);
        } catch (IllegalArgumentException ex) {
            log.error(JwtConstants.EMPTY_CLAIMS_MESSAGE, ex);
        }
        return false;
    }

    /**
     * Parsea los claims de un token JWT.
     *
     * @param token el token JWT
     * @return los claims del token
     */
    private Claims parseClaims(String token) {
        SecretKey key = getSigningKey();
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * Obtiene la clave de firma.
     * Si la clave JWT es una cadena normal, la convierte en una clave HMAC-SHA de 512 bits.
     * Si la clave es Base64, la decodifica primero.
     *
     * @return la clave de firma
     */
    private SecretKey getSigningKey() {
        try {
            // Intentar decodificar como Base64 (para claves pre-generadas)
            byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);
            log.debug("JWT Secret decodificado correctamente. Bytes: {}, Bits: {}", decodedKey.length, decodedKey.length * 8);
            return Keys.hmacShaKeyFor(decodedKey);
        } catch (IllegalArgumentException ex) {
            log.warn("JWT Secret no es válido en Base64, usando como UTF-8");
            // Si no es Base64 válido, usar como string UTF-8
            // Esto generará una clave a partir de los bytes de la string
            byte[] secretBytes = jwtSecret.getBytes();
            // Para asegurar una clave lo suficientemente fuerte, usar Keys.hmacShaKeyFor
            // que valida el tamaño mínimo
            return Keys.hmacShaKeyFor(secretBytes);
        } catch (Exception ex) {
            log.error("Error al procesar JWT Secret: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error fatal al procesar JWT Secret", ex);
        }
    }

    /**
     * Extrae el token del header Authorization.
     *
     * @param authorizationHeader el header Authorization
     * @return el token o null si no existe
     */
    public String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(JwtConstants.BEARER_PREFIX)) {
            return authorizationHeader.substring(JwtConstants.TOKEN_START_INDEX);
        }
        return null;
    }
}
