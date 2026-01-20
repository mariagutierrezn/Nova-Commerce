package com.novacommerce.order_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order sampleOrder() {
        Order order = Order.builder()
                .customerId("10")
                .build();
        order.addItem(OrderItem.builder().productId("1").quantity(2).unitPrice(Money.of(50)).build());
        order.addItem(OrderItem.builder().productId("2").quantity(1).unitPrice(Money.of(30)).build());
        return order;
    }

    @Test
    @DisplayName("GIVEN order with items WHEN recalculateTotals THEN sums subtotals and sets after discount")
    void recalculateTotals() {
        Order order = sampleOrder();
        order.recalculateTotals();
        assertEquals("130.00", order.getTotalBeforeDiscount().toString());
        assertEquals("130.00", order.getTotalAfterDiscount().toString());
    }

    @Test
    @DisplayName("GIVEN discount WHEN applyDiscount THEN totalAfterDiscount is decreased")
    void applyDiscount() {
        Order order = sampleOrder();
        order.recalculateTotals();
        order.applyDiscount(Money.of(19.5));
        assertEquals("110.50", order.getTotalAfterDiscount().toString());
    }

    @Test
    @DisplayName("GIVEN big discount WHEN greater than total THEN throws")
    void discountGreaterThanTotalThrows() {
        Order order = sampleOrder();
        order.recalculateTotals();
        assertThrows(IllegalArgumentException.class, () -> order.applyDiscount(Money.of(1000)));
    }

    @Test
    @DisplayName("GIVEN new order WHEN changeStatus without current THEN defaults to CREATED")
    void changeStatusDefaultsCreated() {
        Order order = sampleOrder();
        order.changeStatus(OrderStatus.CREATED);
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    @DisplayName("GIVEN status invalid transition WHEN changeStatus THEN throws")
    void invalidStatusTransition() {
        Order order = sampleOrder();
        order.markAsCreated();
        order.changeStatus(OrderStatus.PAID);
        assertThrows(IllegalStateException.class, () -> order.changeStatus(OrderStatus.CREATED));
    }

    @Test
    @DisplayName("GIVEN invalid order WHEN validate THEN throws")
    void validateInvalidOrder() {
        Order order = Order.builder().build();
        assertThrows(IllegalArgumentException.class, order::validate);
    }
}
