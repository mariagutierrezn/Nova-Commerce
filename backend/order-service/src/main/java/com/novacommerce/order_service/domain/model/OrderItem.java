package com.novacommerce.order_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Item individual de una orden.
 * Contiene lógica de negocio para cálculo de subtotales.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private String id;
    private String productId;
    private String productName;
    private Integer quantity;
    private Money unitPrice;
    private String productType;
    private String imageUrl;

    /**
     * Calcula el subtotal del item (cantidad * precio unitario).
     */
    public Money calculateSubTotal() {
        if (quantity == null || quantity <= 0) {
            throw new IllegalStateException("Quantity must be greater than zero");
        }
        if (unitPrice == null) {
            throw new IllegalStateException("Unit price cannot be null");
        }
        return unitPrice.multiply(quantity);
    }

    /**
     * Valida que el item tenga los datos mínimos requeridos.
     */
    public void validate() {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (unitPrice == null || unitPrice.isZero()) {
            throw new IllegalArgumentException("Unit price must be greater than zero");
        }
    }

    /**
     * Convierte Money a BigDecimal para persistencia.
     */
    public BigDecimal getUnitPriceValue() {
        return unitPrice != null ? unitPrice.getAmount() : BigDecimal.ZERO;
    }

    public void setUnitPriceValue(BigDecimal value) {
        this.unitPrice = Money.of(value);
    }
}
