package com.novacommerce.notification_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

/**
 * Entidad de Notificación para persistencia en MongoDB
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {
    
    @Id
    private String id;
    
    private String type;
    
    private String title;
    
    private String message;
    
    private String priority;
    
    private boolean read;
    
    private Instant createdAt;
    
    private String orderId;
    
    private String customerId;
    
    private String productId;
    
    private Map<String, Object> metadata;
}
