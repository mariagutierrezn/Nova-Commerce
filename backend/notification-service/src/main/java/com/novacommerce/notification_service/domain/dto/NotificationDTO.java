package com.novacommerce.notification_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * DTO para transferencia de notificaciones
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    
    private String id;
    
    private String type;
    
    private String orderId;
    
    private String customerId;
    
    private String productId;
    
    private Map<String, Object> data;
    
    private boolean read;
    
    private Instant createdAt;
}
