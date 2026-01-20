package com.novacommerce.order_service.domain.discount;

import com.novacommerce.order_service.domain.model.DiscountContext;
import com.novacommerce.order_service.domain.model.DiscountResult;
import com.novacommerce.order_service.domain.model.Money;
import com.novacommerce.order_service.domain.model.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductTypeDiscountStrategyTest {

    private final ProductTypeDiscountStrategy strategy = new ProductTypeDiscountStrategy();

    @Test
    @DisplayName("GIVEN electronics and clothing items WHEN apply THEN accumulates item discounts")
    void applyItemTypes() {
        OrderItem i1 = OrderItem.builder().productId("1").quantity(1).unitPrice(Money.of(100)).productType("ELECTRONICS").build();
        OrderItem i2 = OrderItem.builder().productId("2").quantity(2).unitPrice(Money.of(50)).productType("CLOTHING").build();
        DiscountContext ctx = DiscountContext.builder()
                .items(List.of(i1, i2))
                .orderTotal(Money.of(200))
                .build();
        DiscountResult res = strategy.apply(ctx);
        assertTrue(res.hasDiscount());
        // 8% of 100 = 8; 12% of 100 = 12; total = 20
        assertEquals("20.00", res.getDiscountAmount().toString());
    }

    @Test
    @DisplayName("GIVEN items with no type WHEN apply THEN no discount")
    void noTypeNoDiscount() {
        OrderItem i1 = OrderItem.builder().productId("1").quantity(1).unitPrice(Money.of(100)).build();
        DiscountContext ctx = DiscountContext.builder()
                .items(List.of(i1))
                .orderTotal(Money.of(100))
                .build();
        DiscountResult res = strategy.apply(ctx);
        assertFalse(res.hasDiscount());
    }
}
