package com.novacommerce.order_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Agregado raíz del dominio Order.
 * Contiene toda la lógica de negocio relacionada con órdenes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String id;
    private String customerId;
    private OrderStatus status;
    private Money totalBeforeDiscount;
    private Money discountTotal;
    private Money totalAfterDiscount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Calcula todos los totales basándose en los items.
     */
    public void recalculateTotals() {
        Money total = Money.zero();
        for (OrderItem item : items) {
            total = total.add(item.calculateSubTotal());
        }
        this.totalBeforeDiscount = total;
        
        // Si no hay descuento aplicado, el total después del descuento es igual
        if (this.discountTotal == null || this.discountTotal.isZero()) {
            this.totalAfterDiscount = this.totalBeforeDiscount;
        } else {
            this.totalAfterDiscount = this.totalBeforeDiscount.subtract(this.discountTotal);
        }
    }

    /**
     * Aplica un descuento a la orden.
     */
    public void applyDiscount(Money discount) {
        if (discount == null || discount.isZero()) {
            this.discountTotal = Money.zero();
        } else if (discount.isGreaterThan(totalBeforeDiscount)) {
            throw new IllegalArgumentException("Discount cannot be greater than total");
        } else {
            this.discountTotal = discount;
        }
        recalculateTotals();
    }

    /**
     * Cambia el estado de la orden validando transiciones permitidas.
     */
    public void changeStatus(OrderStatus newStatus) {
        if (this.status == null) {
            this.status = OrderStatus.CREATED;
            return;
        }
        
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", this.status, newStatus)
            );
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Agrega un item a la orden y recalcula totales.
     */
    public void addItem(OrderItem item) {
        item.validate();
        this.items.add(item);
        recalculateTotals();
    }

    /**
     * Valida reglas de negocio antes de crear la orden.
     */
    public void validate() {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        items.forEach(OrderItem::validate);
    }

    /**
     * Marca la orden como creada inicialmente.
     */
    public void markAsCreated() {
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Métodos para conversión Money <-> BigDecimal (persistencia)
    
    public BigDecimal getTotalBeforeDiscountValue() {
        return totalBeforeDiscount != null ? totalBeforeDiscount.getAmount() : BigDecimal.ZERO;
    }

    public void setTotalBeforeDiscountValue(BigDecimal value) {
        this.totalBeforeDiscount = Money.of(value);
    }

    public BigDecimal getDiscountTotalValue() {
        return discountTotal != null ? discountTotal.getAmount() : BigDecimal.ZERO;
    }

    public void setDiscountTotalValue(BigDecimal value) {
        this.discountTotal = Money.of(value);
    }

    public BigDecimal getTotalAfterDiscountValue() {
        return totalAfterDiscount != null ? totalAfterDiscount.getAmount() : BigDecimal.ZERO;
    }

    public void setTotalAfterDiscountValue(BigDecimal value) {
        this.totalAfterDiscount = Money.of(value);
    }
}
