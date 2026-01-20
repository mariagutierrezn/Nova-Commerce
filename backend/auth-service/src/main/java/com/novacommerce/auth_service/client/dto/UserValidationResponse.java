package com.novacommerce.auth_service.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;

/**
 * DTO para respuesta de validación de usuario desde user-service.
 * Contiene información necesaria para generar el JWT.
 */
public record UserValidationResponse(
    String username,
    String email,
    boolean enabled,
    boolean locked,
    Set<String> roles,
    Set<String> permissions,
    @JsonProperty("customer_id")
    String customerId
) {
}
