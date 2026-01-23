package com.novacommerce.notification_service.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear una nueva sesión de chat
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatSessionRequest {
    
    @NotBlank(message = "El ID del cliente es obligatorio")
    private String customerId;
    
    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String customerName;
    
    private String customerEmail;
}
