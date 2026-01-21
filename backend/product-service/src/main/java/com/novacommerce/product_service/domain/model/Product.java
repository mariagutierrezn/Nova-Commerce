package com.novacommerce.product_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private ProductType productType;
    private String categoryId;
    private Integer stockQuantity;
    private String status;
    private String imageUrl;
    private Boolean hasDiscount;
    private Integer discountPercentage;
}
