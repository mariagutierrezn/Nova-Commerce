package com.novacommerce.order_service.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento de dominio que se publica cuando una orden es pagada.
 * Este evento se envía de forma asíncrona via RabbitMQ.
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
