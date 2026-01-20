package com.novacommerce.user_service.web.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * DTO para crear un nuevo rol del sistema.
 */
public record CreateRoleRequest(
    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre del rol debe tener entre 2 y 100 caracteres")
    String name,

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    String description,

    @JsonProperty("permission_ids")
    Set<String> permissionIds
) {}
