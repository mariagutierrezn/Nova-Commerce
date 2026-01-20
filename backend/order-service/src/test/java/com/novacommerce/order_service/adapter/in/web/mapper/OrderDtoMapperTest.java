package com.novacommerce.order_service.adapter.in.web.mapper;

import com.novacommerce.order_service.adapter.in.web.dto.CreateOrderRequest;
import com.novacommerce.order_service.adapter.in.web.dto.OrderItemRequest;
import com.novacommerce.order_service.adapter.in.web.dto.OrderResponse;
import com.novacommerce.order_service.domain.model.Money;
import com.novacommerce.order_service.domain.model.Order;
import com.novacommerce.order_service.domain.model.OrderItem;
import com.novacommerce.order_service.domain.model.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderDtoMapperTest {

    private final OrderDtoMapper mapper = new OrderDtoMapper();

    @Test
    @DisplayName("GIVEN CreateOrderRequest WHEN toDomain THEN builds Order with items and money")
    void toDomain() {
        CreateOrderRequest req = CreateOrderRequest.builder()
                .customerId("3")
                .items(List.of(
                        OrderItemRequest.builder().productId("1").quantity(2).unitPrice(new BigDecimal("10.00")).build(),
                        OrderItemRequest.builder().productId("2").quantity(1).unitPrice(new BigDecimal("5.50")).build()
                ))
                .build();

        Order order = mapper.toDomain(req);
        assertEquals("3", order.getCustomerId());
        assertEquals(2, order.getItems().size());
        assertEquals("25.50", order.getTotalBeforeDiscount().toString());
    }

    @Test
    @DisplayName("GIVEN Order WHEN toResponse THEN maps fields and calculates subtotals")
    void toResponse() {
        Order o = Order.builder().id("10").customerId("3").status(OrderStatus.CREATED).build();
        o.addItem(OrderItem.builder().id("1").productId("1").productName("A").quantity(2).unitPrice(Money.of(10)).productType("ELECTRONICS").build());
        o.addItem(OrderItem.builder().id("2").productId("2").productName("B").quantity(1).unitPrice(Money.of(5.5)).productType("FOOD").build());
        o.applyDiscount(Money.of(3));

        OrderResponse resp = mapper.toResponse(o);
        assertEquals("10", resp.getId());
        assertEquals("CREATED", resp.getStatus());
        assertEquals(new BigDecimal("25.50"), resp.getTotalBeforeDiscount());
        assertEquals(new BigDecimal("22.50"), resp.getTotalAfterDiscount());
        assertEquals(2, resp.getItems().size());
        assertEquals(new BigDecimal("20.00"), resp.getItems().get(0).getSubTotal());
    }
}
