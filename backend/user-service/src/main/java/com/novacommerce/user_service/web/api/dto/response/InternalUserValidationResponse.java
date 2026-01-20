package com.novacommerce.user_service.web.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO de respuesta para validación de credenciales de usuario.
 * Se retorna cuando auth-service valida exitosamente las credenciales.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternalUserValidationResponse {

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("locked")
    private Boolean locked;
    
    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("roles")
    private Set<String> roles;

    @JsonProperty("permissions")
    private Set<String> permissions;
}
