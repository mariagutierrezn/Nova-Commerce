package com.novacommerce.user_service.web.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO de respuesta para información de usuario.
 * Expone información pública del usuario sin datos sensibles.
 */
public record UserResponse(
    @JsonProperty("id")
    String id,

    @JsonProperty("username")
    String username,

    @JsonProperty("email")
    String email,

    @JsonProperty("status")
    String status,

    @JsonProperty("enabled")
    Boolean enabled,

    @JsonProperty("locked")
    Boolean locked,

    @JsonProperty("customer_id")
    String customerId,

    @JsonProperty("role_ids")
    Set<String> roleIds,

    @JsonProperty("created_at")
    LocalDateTime createdAt,

    @JsonProperty("updated_at")
    LocalDateTime updatedAt
) {}
