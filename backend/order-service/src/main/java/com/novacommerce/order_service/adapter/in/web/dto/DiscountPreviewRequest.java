package com.novacommerce.order_service.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para solicitar un preview de descuentos antes de crear la orden
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountPreviewRequest {
    private String customerId;
    private List<OrderItemDto> items;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        private String productId;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}
