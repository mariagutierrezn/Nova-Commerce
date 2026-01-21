package com.novacommerce.order_service.adapter.out.persistence;

import com.novacommerce.order_service.domain.model.DiscountRule;
import com.novacommerce.order_service.repository.entity.DiscountRuleEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper entre entidad y dominio para DiscountRule.
 */
@Component
public class DiscountRuleMapper {
    
    public DiscountRule toDomain(DiscountRuleEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return DiscountRule.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .strategy(entity.getStrategy())
                .value(entity.getValue())
                .minPurchase(entity.getMinPurchase())
                .maxDiscount(entity.getMaxDiscount())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .active(entity.getActive())
                .usageCount(entity.getUsageCount())
                .maxUsage(entity.getMaxUsage())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    public DiscountRuleEntity toEntity(DiscountRule domain) {
        if (domain == null) {
            return null;
        }
        
        return DiscountRuleEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .strategy(domain.getStrategy())
                .value(domain.getValue())
                .minPurchase(domain.getMinPurchase())
                .maxDiscount(domain.getMaxDiscount())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .active(domain.getActive())
                .usageCount(domain.getUsageCount())
                .maxUsage(domain.getMaxUsage())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
