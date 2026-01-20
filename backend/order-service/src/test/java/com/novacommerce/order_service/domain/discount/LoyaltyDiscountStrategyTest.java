package com.novacommerce.order_service.domain.discount;

import com.novacommerce.order_service.domain.model.DiscountContext;
import com.novacommerce.order_service.domain.model.DiscountResult;
import com.novacommerce.order_service.domain.model.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoyaltyDiscountStrategyTest {

    private final LoyaltyDiscountStrategy strategy = new LoyaltyDiscountStrategy();

    @Test
    @DisplayName("GIVEN GOLD loyalty WHEN apply THEN 15 percent discount")
    void applyGold() {
        DiscountContext ctx = DiscountContext.builder()
                .customerId("1")
                .customerStatus("ACTIVE")
                .customerLoyaltyLevel("GOLD")
                .orderTotal(Money.of(200))
                .build();
        DiscountResult res = strategy.apply(ctx);
        assertTrue(res.hasDiscount());
        assertEquals("30.00", res.getDiscountAmount().toString());
    }

    @Test
    @DisplayName("GIVEN no loyalty WHEN apply THEN no discount")
    void notApplicable() {
        DiscountContext ctx = DiscountContext.builder()
                .customerId("1")
                .customerStatus("ACTIVE")
                .orderTotal(Money.of(200))
                .build();
        DiscountResult res = strategy.apply(ctx);
        assertFalse(res.hasDiscount());
    }
}
