package com.novacommerce.order_service.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para reglas de descuento.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRuleResponse {
    
    private String id;
    private String name;
    private String description;
    private String strategy;
    private BigDecimal value;
    private BigDecimal minPurchase;
    private BigDecimal maxDiscount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean active;
    private Integer usageCount;
    private Integer maxUsage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isValid;
}
