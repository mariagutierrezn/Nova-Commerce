package com.novacommerce.product_service.adapter.out.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
public class JwtTokenValidatorAdapter {

    private final String jwtSecret;

    public JwtTokenValidatorAdapter(@Value("${app.jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = extractClaims(token);
        return claims.getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = extractClaims(token);
        // The token contains "authorities" as a comma-separated string, convert to list
        String authorities = claims.get("authorities", String.class);
        if (authorities == null || authorities.isEmpty()) {
            log.warn("No authorities found in token");
            return List.of();
        }
        return List.of(authorities.split(","));
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            log.debug("Secret is not Base64 encoded, using UTF-8 bytes directly");
            return Keys.hmacShaKeyFor(jwtSecret.getBytes());
        }
    }
}
