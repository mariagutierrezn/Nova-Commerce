package com.novacommerce.notification_service.domain.dto;

import com.novacommerce.notification_service.domain.model.ChatSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO para transferir información de sesiones de chat
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionDTO {
    
    private String id;
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String advisorId;
    private String advisorName;
    private String status;  // WAITING, ACTIVE, CLOSED
    private Instant createdAt;
    private Instant updatedAt;
    private Instant closedAt;
    private int unreadByCustomer;
    private int unreadByAdvisor;
    private Instant lastActivity;
    private ChatMessageDTO lastMessage;  // Último mensaje de la sesión
    
    /**
     * Convierte una entidad ChatSession a DTO
     */
    public static ChatSessionDTO fromEntity(ChatSession session) {
        return ChatSessionDTO.builder()
                .id(session.getId())
                .customerId(session.getCustomerId())
                .customerName(session.getCustomerName())
                .customerEmail(session.getCustomerEmail())
                .advisorId(session.getAdvisorId())
                .advisorName(session.getAdvisorName())
                .status(session.getStatus() != null ? session.getStatus().name() : null)
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .closedAt(session.getClosedAt())
                .unreadByCustomer(session.getUnreadByCustomer())
                .unreadByAdvisor(session.getUnreadByAdvisor())
                .lastActivity(session.getLastActivity())
                .build();
    }
}
