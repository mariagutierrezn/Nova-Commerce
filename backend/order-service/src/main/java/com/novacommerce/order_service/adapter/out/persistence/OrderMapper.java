package com.novacommerce.order_service.adapter.out.persistence;

import com.novacommerce.order_service.domain.model.Money;
import com.novacommerce.order_service.domain.model.Order;
import com.novacommerce.order_service.domain.model.OrderItem;
import com.novacommerce.order_service.repository.entity.OrderEntity;
import com.novacommerce.order_service.repository.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper manual entre dominio y entidades MongoDB.
 */
@Component
public class OrderMapper {

    public OrderEntity toEntity(Order order) {
        if (order == null) {
            return null;
        }

        OrderEntity entity = OrderEntity.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .totalBeforeDiscount(order.getTotalBeforeDiscountValue())
                .discountTotal(order.getDiscountTotalValue())
                .totalAfterDiscount(order.getTotalAfterDiscountValue())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();

        // Mapear items
        if (order.getItems() != null) {
            order.getItems().forEach(item -> {
                OrderItemEntity itemEntity = toItemEntity(item);
                entity.addItem(itemEntity);
            });
        }

        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }

        Order order = Order.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .status(entity.getStatus())
                .totalBeforeDiscount(Money.of(entity.getTotalBeforeDiscount()))
                .discountTotal(Money.of(entity.getDiscountTotal()))
                .totalAfterDiscount(Money.of(entity.getTotalAfterDiscount()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

        // Mapear items
        if (entity.getItems() != null) {
            order.setItems(entity.getItems().stream()
                    .map(this::toItemDomain)
                    .collect(Collectors.toList()));
        }

        return order;
    }

    private OrderItemEntity toItemEntity(OrderItem item) {
        return OrderItemEntity.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPriceValue())
                .productType(item.getProductType())
                .build();
    }

    private OrderItem toItemDomain(OrderItemEntity entity) {
        return OrderItem.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .quantity(entity.getQuantity())
                .unitPrice(Money.of(entity.getUnitPrice()))
                .productType(entity.getProductType())
                .build();
    }
}
