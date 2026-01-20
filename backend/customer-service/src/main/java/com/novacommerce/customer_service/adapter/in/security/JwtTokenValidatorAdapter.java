package com.novacommerce.customer_service.adapter.in.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenValidatorAdapter {

    private final String secret;

    public JwtTokenValidatorAdapter(@Value("${app.jwt.secret}") String secret) {
        this.secret = secret;
    }

    private SecretKey getSigningKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }

    public Claims validate(String token) {
        try {
            return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
        } catch (JwtException ex) {
            throw new JwtException("Token inválido: " + ex.getMessage(), ex);
        }
    }

    public List<String> extractAuthorities(Claims claims) {
        Object authorities = claims.get("authorities");
        if (authorities instanceof String s && !s.isBlank()) {
            return List.of(s.split(","));
        }
        if (authorities instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        if (authorities instanceof Map<?,?> map && map.containsKey("roles")) {
            Object roles = map.get("roles");
            if (roles instanceof List<?> rl) {
                return rl.stream().map(Object::toString).toList();
            }
        }
        return List.of();
    }
}
