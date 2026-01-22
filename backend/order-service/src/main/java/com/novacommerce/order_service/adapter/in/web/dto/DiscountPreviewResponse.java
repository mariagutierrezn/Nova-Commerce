package com.novacommerce.order_service.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para respuesta de preview de descuentos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountPreviewResponse {
    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal total;
    private List<AppliedDiscountDto> discounts;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppliedDiscountDto {
        private String type;
        private String label;
        private String description;
        private BigDecimal percentage;
        private BigDecimal amount;
    }
}
