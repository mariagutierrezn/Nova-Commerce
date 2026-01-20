package com.novacommerce.order_service.repository.entity;

import com.novacommerce.order_service.domain.model.OrderStatus;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    
    @Id
    private String id;
    private String customerId;
    private OrderStatus status;
    private BigDecimal totalBeforeDiscount;
    private BigDecimal discountTotal;
    private BigDecimal totalAfterDiscount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Builder.Default
    private List<OrderItemEntity> items = new ArrayList<>();
    
    public void addItem(OrderItemEntity item) {
        items.add(item);
    }
    
    public void setTimestamps() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }
}
