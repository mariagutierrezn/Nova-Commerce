package com.novacommerce.auth_service.adapter.out.jwt;

import com.novacommerce.auth_service.application.port.out.TokenGeneratorPort;
import com.novacommerce.auth_service.config.security.jwt.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
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
 * Adaptador de salida para generación y validación de tokens JWT
 * Implementa TokenGeneratorPort usando la librería JJWT
 */
@Component
@Slf4j
public class JwtTokenAdapter implements TokenGeneratorPort {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration:604800000}")
    private long refreshTokenExpirationMs;

    @Override
    public String generateToken(Authentication authentication, String customerId) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        String authoritiesString = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = getSigningKey();

        log.debug("Generating token for user: {} with authorities: {} and customerId: {}", username, authoritiesString, customerId);
        
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

    @Override
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

        SecretKey key = getSigningKey();

        log.debug("Generating refresh token for user: {}", username);
        
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key)
            .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            SecretKey key = getSigningKey();
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            log.debug("Token validated successfully");
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

    @Override
    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    @Override
    public String getAuthoritiesFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get(JwtConstants.AUTHORITIES_CLAIM, String.class);
    }

    /**
     * Parsea los claims de un token JWT
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
     * Obtiene la clave de firma decodificando el secreto
     */
    private SecretKey getSigningKey() {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);
            log.debug("JWT Secret decoded as Base64. Bytes: {}, Bits: {}", decodedKey.length, decodedKey.length * 8);
            return Keys.hmacShaKeyFor(decodedKey);
        } catch (IllegalArgumentException ex) {
            log.warn("JWT Secret is not valid Base64, using as UTF-8");
            byte[] secretBytes = jwtSecret.getBytes();
            return Keys.hmacShaKeyFor(secretBytes);
        }
    }
}
