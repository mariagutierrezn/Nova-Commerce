package com.novacommerce.order_service.domain.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Contexto que contiene toda la información necesaria para aplicar descuentos.
 */
@Value
@Builder
public class DiscountContext {
    String customerId;
    String customerLoyaltyLevel;
    String customerStatus;
    Money orderTotal;
    List<OrderItem> items;
    String currentSeason;

    public boolean hasLoyaltyLevel() {
        return customerLoyaltyLevel != null && !customerLoyaltyLevel.isBlank();
    }

    public boolean isCustomerActive() {
        return "ACTIVE".equalsIgnoreCase(customerStatus);
    }

    public boolean isVIP() {
        return "VIP".equalsIgnoreCase(customerLoyaltyLevel) || 
               "GOLD".equalsIgnoreCase(customerLoyaltyLevel);
    }
}
