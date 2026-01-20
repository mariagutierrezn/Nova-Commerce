package com.novacommerce.auth_service.web.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de registro exitoso.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de registro exitoso")
public class RegisterResponse {

    @Schema(description = "ID del usuario creado", example = "7eea2162-ff23-4d9e-b431-643e4dda2d0c")
    private String userId;

    @Schema(description = "ID del cliente creado", example = "5")
    private String customerId;

    @Schema(description = "Email del usuario registrado", example = "leonardo@sofka.com.co")
    private String email;

    @Schema(description = "Nombre completo", example = "Leonardo Pérez")
    private String fullName;

    @Schema(description = "Mensaje de bienvenida")
    private String message;

    @Schema(description = "URL para iniciar sesión", example = "/api/auth/login")
    private String loginUrl;
}
