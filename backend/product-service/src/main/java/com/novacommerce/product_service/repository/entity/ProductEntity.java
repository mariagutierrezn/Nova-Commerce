package com.novacommerce.product_service.repository.entity;

import com.novacommerce.product_service.domain.model.ProductType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.*;

import java.math.BigDecimal;

@Document(collection = "products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

    @Id
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
