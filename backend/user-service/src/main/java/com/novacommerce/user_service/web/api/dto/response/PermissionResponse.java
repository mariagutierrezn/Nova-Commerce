package com.novacommerce.user_service.web.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

// ...existing code...

/**
 * DTO de respuesta para información de permiso.
 */
public record PermissionResponse(
    @JsonProperty("id")
    String id,

    @JsonProperty("name")
    String name,

    @JsonProperty("description")
    String description
) {}
