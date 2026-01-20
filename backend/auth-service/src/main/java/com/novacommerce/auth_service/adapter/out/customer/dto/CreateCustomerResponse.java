package com.novacommerce.auth_service.adapter.out.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de creación de cliente desde customer-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de creación de cliente")
public class CreateCustomerResponse {

    @Schema(description = "ID del cliente creado", example = "5")
    private String id;

    @Schema(description = "Email del cliente")
    private String email;

    @Schema(description = "Nombre del cliente")
    private String firstName;

    @Schema(description = "Apellido del cliente")
    private String lastName;

    @Schema(description = "Teléfono")
    private String phone;

    @Schema(description = "Estado del cliente")
    private String status;

    @Schema(description = "Nivel de lealtad", example = "BRONZE")
    private String loyaltyLevel;
}
