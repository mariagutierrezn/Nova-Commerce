package com.novacommerce.product_service.adapter.in.web.dto;

import com.novacommerce.product_service.domain.model.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO público para mostrar productos en el home.
 * Incluye información de stock, descuentos y categoría para la UI.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicProductResponse {
    
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private ProductType productType;
    private String imageUrl;
    private String categoryId;
    private Integer stockQuantity;
    private Boolean hasDiscount;
    private Integer discountPercentage;
}
