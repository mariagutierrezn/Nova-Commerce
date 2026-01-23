package com.novacommerce.notification_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Entidad de Mensaje de Chat para persistencia en MongoDB
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_messages")
public class ChatMessage {
    
    @Id
    private String id;
    
    /**
     * ID de la sesión de chat a la que pertenece este mensaje
     */
    private String sessionId;
    
    /**
     * ID del remitente (customer o advisor)
     */
    private String senderId;
    
    /**
     * Nombre del remitente
     */
    private String senderName;
    
    /**
     * Tipo de remitente: CUSTOMER, ADVISOR, BOT
     */
    private SenderType senderType;
    
    /**
     * Contenido del mensaje
     */
    private String content;
    
    /**
     * Fecha de envío del mensaje
     */
    private Instant sentAt;
    
    /**
     * Indica si el mensaje ha sido leído
     */
    private boolean read;
    
    /**
     * Fecha en que se leyó el mensaje
     */
    private Instant readAt;
    
    /**
     * Tipo de mensaje: TEXT, IMAGE, FILE
     */
    private MessageType messageType;
    
    public enum SenderType {
        CUSTOMER,   // Mensaje enviado por el cliente
        ADVISOR,    // Mensaje enviado por el asesor
        BOT         // Mensaje automático del bot
    }
    
    public enum MessageType {
        TEXT,       // Mensaje de texto
        IMAGE,      // Imagen
        FILE        // Archivo adjunto
    }
}
