package com.novacommerce.auth_service.application.port.out;

import org.springframework.security.core.Authentication;

/**
 * Puerto de salida para generar tokens JWT
 * Define el contrato que deben implementar los adaptadores de generación de tokens
 */
public interface TokenGeneratorPort {
    
    /**
     * Genera un token de acceso JWT
     * 
     * @param authentication información de autenticación del usuario
     * @param customerId ID del cliente asociado al usuario
     * @return token JWT como String
     */
    String generateToken(Authentication authentication, String customerId);
    
    /**
     * Genera un refresh token
     * 
     * @param username nombre de usuario
     * @return refresh token como String
     */
    String generateRefreshToken(String username);
    
    /**
     * Valida un token JWT
     * 
     * @param token el token a validar
     * @return true si es válido, false si no
     */
    boolean validateToken(String token);
    
    /**
     * Extrae el username de un token
     * 
     * @param token el token JWT
     * @return username del token
     */
    String getUsernameFromToken(String token);
    
    /**
     * Extrae las autoridades (roles y permisos) del token
     * 
     * @param token el token JWT
     * @return autoridades como String separado por comas
     */
    String getAuthoritiesFromToken(String token);
}
