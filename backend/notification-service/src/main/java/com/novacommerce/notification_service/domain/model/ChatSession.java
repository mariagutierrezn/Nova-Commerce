package com.novacommerce.notification_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Entidad de Sesión de Chat para persistencia en MongoDB
 * Representa una conversación entre un cliente y un asesor
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_sessions")
public class ChatSession {
    
    @Id
    private String id;
    
    /**
     * ID del cliente (usuario) que inicia el chat
     */
    private String customerId;
    
    /**
     * Nombre del cliente
     */
    private String customerName;
    
    /**
     * Email del cliente
     */
    private String customerEmail;
    
    /**
     * ID del asesor asignado (admin)
     */
    private String advisorId;
    
    /**
     * Nombre del asesor asignado
     */
    private String advisorName;
    
    /**
     * Estado de la sesión: WAITING, ACTIVE, CLOSED
     */
    private ChatStatus status;
    
    /**
     * Fecha de creación de la sesión
     */
    private Instant createdAt;
    
    /**
     * Fecha de última actualización
     */
    private Instant updatedAt;
    
    /**
     * Fecha de cierre de la sesión
     */
    private Instant closedAt;
    
    /**
     * Contador de mensajes no leídos por el cliente
     */
    private int unreadByCustomer;
    
    /**
     * Contador de mensajes no leídos por el asesor
     */
    private int unreadByAdvisor;
    
    /**
     * Última actividad registrada
     */
    private Instant lastActivity;
    
    public enum ChatStatus {
        WAITING,    // Esperando asignación de asesor
        ACTIVE,     // Chat activo con asesor
        CLOSED      // Chat cerrado
    }
}
