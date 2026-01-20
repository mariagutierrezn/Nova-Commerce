package com.novacommerce.user_service.web.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

/**
 * DTO de respuesta para información de rol.
 * Incluye el rol y sus permisos asociados.
 */
public record RoleResponse(
    @JsonProperty("id")
    String id,

    @JsonProperty("name")
    String name,

    @JsonProperty("description")
    String description,

    @JsonProperty("permission_ids")
    Set<String> permissionIds
) {}
