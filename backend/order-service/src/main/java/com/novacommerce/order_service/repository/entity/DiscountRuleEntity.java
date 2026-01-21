package com.novacommerce.order_service.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad MongoDB para reglas de descuento.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "discount_rules")
public class DiscountRuleEntity {
    
    @Id
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
}
