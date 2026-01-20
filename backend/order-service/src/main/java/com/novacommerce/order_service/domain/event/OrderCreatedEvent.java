package com.novacommerce.order_service.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Evento de dominio que se publica cuando se crea una orden.
 * Este evento se envía de forma asíncrona via RabbitMQ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private String orderId;
    private String customerId;
    private String status;
    private BigDecimal totalBeforeDiscount;
    private BigDecimal discountTotal;
    private BigDecimal totalAfterDiscount;
    private LocalDateTime createdAt;
    private List<OrderItemEvent> items;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemEvent {
        private String productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subTotal;
    }
}
