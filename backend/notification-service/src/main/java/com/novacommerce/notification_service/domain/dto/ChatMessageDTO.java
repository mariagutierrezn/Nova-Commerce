package com.novacommerce.notification_service.domain.dto;

import com.novacommerce.notification_service.domain.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO para transferir información de mensajes de chat
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    
    private String id;
    private String sessionId;
    private String senderId;
    private String senderName;
    private String senderType;  // CUSTOMER, ADVISOR, BOT
    private String content;
    private Instant sentAt;
    private boolean read;
    private Instant readAt;
    private String messageType;  // TEXT, IMAGE, FILE
    
    /**
     * Convierte una entidad ChatMessage a DTO
     */
    public static ChatMessageDTO fromEntity(ChatMessage message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .sessionId(message.getSessionId())
                .senderId(message.getSenderId())
                .senderName(message.getSenderName())
                .senderType(message.getSenderType() != null ? message.getSenderType().name() : null)
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .read(message.isRead())
                .readAt(message.getReadAt())
                .messageType(message.getMessageType() != null ? message.getMessageType().name() : "TEXT")
                .build();
    }
}
