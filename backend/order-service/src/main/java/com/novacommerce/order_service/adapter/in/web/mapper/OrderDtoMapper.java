package com.novacommerce.order_service.adapter.in.web.mapper;

import com.novacommerce.order_service.adapter.in.web.dto.*;
import com.novacommerce.order_service.domain.model.Money;
import com.novacommerce.order_service.domain.model.Order;
import com.novacommerce.order_service.domain.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper entre DTOs de API y modelos de dominio.
 */
@Component
public class OrderDtoMapper {

    public Order toDomain(CreateOrderRequest request) {
        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .build();

        if (request.getItems() != null) {
            request.getItems().forEach(itemRequest -> {
                OrderItem item = OrderItem.builder()
                        .productId(itemRequest.getProductId())
                        .quantity(itemRequest.getQuantity())
                        .unitPrice(Money.of(itemRequest.getUnitPrice()))
                        .build();
                order.addItem(item);
            });
        }

        return order;
    }

    public OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .status(order.getStatus().name())
                .totalBeforeDiscount(order.getTotalBeforeDiscountValue())
                .discountTotal(order.getDiscountTotalValue())
                .totalAfterDiscount(order.getTotalAfterDiscountValue())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems().stream()
                        .map(this::toItemResponse)
                        .collect(Collectors.toList()))
                .discounts(order.getDiscounts() != null ? order.getDiscounts().stream()
                        .map(d -> OrderResponse.DiscountInfo.builder()
                                .type(d.getType())
                                .percentage(d.getPercentage())
                                .amount(d.getAmount() != null ? d.getAmount().getAmount() : null)
                                .build())
                        .collect(Collectors.toList()) : null)
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPriceValue())
                .subTotal(item.calculateSubTotal().getAmount())
                .productType(item.getProductType())
                .imageUrl(item.getImageUrl())
                .build();
    }
}
