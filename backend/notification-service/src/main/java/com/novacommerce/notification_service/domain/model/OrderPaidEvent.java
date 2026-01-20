package com.novacommerce.notification_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento de orden pagada recibido desde RabbitMQ.
 * Debe coincidir con el evento publicado por order-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaidEvent {
    private String orderId;
    private String customerId;
    private BigDecimal totalAmount;
    private LocalDateTime paidAt;
    private String paymentMethod;
}
