package com.novacommerce.order_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo de dominio para reglas de descuento configurables.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRule {
    
    private String id;
    private String name;
    private String description;
    private String strategy;  // LOYALTY, SEASON, PRODUCT_TYPE, PERCENTAGE, etc.
    private BigDecimal value;  // Porcentaje o monto fijo
    private BigDecimal minPurchase;
    private BigDecimal maxDiscount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean active;
    private Integer usageCount;
    private Integer maxUsage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Verifica si la regla está vigente en el momento actual.
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return active != null && active 
                && startDate != null && !now.isBefore(startDate)
                && (endDate == null || !now.isAfter(endDate))
                && (maxUsage == null || usageCount == null || usageCount < maxUsage);
    }
    
    /**
     * Incrementa el contador de uso.
     */
    public void incrementUsage() {
        this.usageCount = (this.usageCount == null) ? 1 : this.usageCount + 1;
    }
}
