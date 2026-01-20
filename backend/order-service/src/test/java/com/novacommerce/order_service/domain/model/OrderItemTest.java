package com.novacommerce.order_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    @DisplayName("GIVEN valid item WHEN calculateSubTotal THEN multiplies quantity by unit price")
    void calculateSubTotal() {
        OrderItem item = OrderItem.builder()
                .productId("1")
                .quantity(3)
                .unitPrice(Money.of(12.5))
                .build();
        assertEquals("37.50", item.calculateSubTotal().toString());
    }

    @Test
    @DisplayName("GIVEN invalid quantity WHEN calculateSubTotal THEN throws")
    void invalidQuantitySubtotal() {
        OrderItem item = OrderItem.builder()
                .productId("1")
                .quantity(0)
                .unitPrice(Money.of(10))
                .build();
        assertThrows(IllegalStateException.class, item::calculateSubTotal);
    }

    @Test
    @DisplayName("GIVEN missing fields WHEN validate THEN throws with proper messages")
    void validateFields() {
        OrderItem noProduct = OrderItem.builder().quantity(1).unitPrice(Money.of(1)).build();
        assertThrows(IllegalArgumentException.class, noProduct::validate);

        OrderItem zeroQty = OrderItem.builder().productId("1").quantity(0).unitPrice(Money.of(1)).build();
        assertThrows(IllegalArgumentException.class, zeroQty::validate);

        OrderItem zeroPrice = OrderItem.builder().productId("1").quantity(1).unitPrice(Money.zero()).build();
        assertThrows(IllegalArgumentException.class, zeroPrice::validate);
    }
}
