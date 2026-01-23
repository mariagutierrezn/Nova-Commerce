package com.novacommerce.notification_service.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para enviar un mensaje de chat
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    
    @NotBlank(message = "El ID de la sesión es obligatorio")
    private String sessionId;
    
    @NotBlank(message = "El ID del remitente es obligatorio")
    private String senderId;
    
    @NotBlank(message = "El nombre del remitente es obligatorio")
    private String senderName;
    
    @NotBlank(message = "El tipo de remitente es obligatorio")
    private String senderType;  // CUSTOMER, ADVISOR, BOT
    
    @NotBlank(message = "El contenido del mensaje es obligatorio")
    private String content;
    
    private String messageType = "TEXT";  // TEXT, IMAGE, FILE
}
