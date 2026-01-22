package com.novacommerce.order_service.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuesta de orden.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String id;
    private Long orderNumber;
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String shippingAddress;
    private String paymentMethod;
    private String status;
    private BigDecimal totalBeforeDiscount;
    private BigDecimal discountTotal;
    private BigDecimal totalAfterDiscount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> items;
    private List<DiscountInfo> discounts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiscountInfo {
        private String type;
        private BigDecimal percentage;
        private BigDecimal amount;
    }
}
