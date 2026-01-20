package com.novacommerce.product_service.repository.mapper;

import com.novacommerce.product_service.domain.model.Product;
import com.novacommerce.product_service.domain.model.ProductType;
import com.novacommerce.product_service.repository.entity.ProductEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductEntityMapper Tests")
class ProductEntityMapperTest {

    private ProductEntityMapper mapper;
    private ProductEntity productEntity;
    private Product product;

    @BeforeEach
    void setUp() {
        // Utiliza implementación generada por MapStruct sin contexto de Spring
        mapper = Mappers.getMapper(ProductEntityMapper.class);
        
        productEntity = ProductEntity.builder()
                .id("1")
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("999.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId("1")
                .stockQuantity(10)
                .status("ACTIVE")
                .build();

        product = Product.builder()
                .id("1")
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("999.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId("1")
                .stockQuantity(10)
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("Should map ProductEntity to Product")
    void testToDomain() {
        Product mappedProduct = mapper.toDomain(productEntity);

        assertNotNull(mappedProduct);
        assertEquals(productEntity.getId(), mappedProduct.getId());
        assertEquals(productEntity.getName(), mappedProduct.getName());
        assertEquals(productEntity.getDescription(), mappedProduct.getDescription());
        assertEquals(productEntity.getPrice(), mappedProduct.getPrice());
        assertEquals(productEntity.getProductType(), mappedProduct.getProductType());
        assertEquals(productEntity.getCategoryId(), mappedProduct.getCategoryId());
        assertEquals(productEntity.getStockQuantity(), mappedProduct.getStockQuantity());
        assertEquals(productEntity.getStatus(), mappedProduct.getStatus());
    }

    @Test
    @DisplayName("Should map Product to ProductEntity")
    void testToEntity() {
        ProductEntity mappedEntity = mapper.toEntity(product);

        assertNotNull(mappedEntity);
        assertEquals(product.getId(), mappedEntity.getId());
        assertEquals(product.getName(), mappedEntity.getName());
        assertEquals(product.getDescription(), mappedEntity.getDescription());
        assertEquals(product.getPrice(), mappedEntity.getPrice());
        assertEquals(product.getProductType(), mappedEntity.getProductType());
        assertEquals(product.getCategoryId(), mappedEntity.getCategoryId());
        assertEquals(product.getStockQuantity(), mappedEntity.getStockQuantity());
        assertEquals(product.getStatus(), mappedEntity.getStatus());
    }

    @Test
    @DisplayName("Should handle null ProductEntity")
    void testToDomainWithNull() {
        Product result = mapper.toDomain(null);
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null Product")
    void testToEntityWithNull() {
        ProductEntity result = mapper.toEntity(null);
        assertNull(result);
    }

    @Test
    @DisplayName("Should preserve ProductType enum during mapping")
    void testProductTypePreservation() {
        productEntity.setProductType(ProductType.DIGITAL);
        Product mapped = mapper.toDomain(productEntity);
        assertEquals(ProductType.DIGITAL, mapped.getProductType());

        productEntity.setProductType(ProductType.SERVICE);
        mapped = mapper.toDomain(productEntity);
        assertEquals(ProductType.SERVICE, mapped.getProductType());
    }

    @Test
    @DisplayName("Should handle null description")
    void testNullDescription() {
        productEntity.setDescription(null);
        Product mapped = mapper.toDomain(productEntity);
        assertNull(mapped.getDescription());
    }

    @Test
    @DisplayName("Should handle decimal prices correctly")
    void testDecimalPriceMapping() {
        productEntity.setPrice(new BigDecimal("19.99"));
        Product mapped = mapper.toDomain(productEntity);
        assertEquals(new BigDecimal("19.99"), mapped.getPrice());
    }

    @Test
    @DisplayName("Should bidirectional mapping consistency")
    void testBidirectionalMapping() {
        Product domainProduct = mapper.toDomain(productEntity);
        ProductEntity remappedEntity = mapper.toEntity(domainProduct);

        assertEquals(productEntity.getId(), remappedEntity.getId());
        assertEquals(productEntity.getName(), remappedEntity.getName());
        assertEquals(productEntity.getPrice(), remappedEntity.getPrice());
        assertEquals(productEntity.getStockQuantity(), remappedEntity.getStockQuantity());
    }

    @Test
    @DisplayName("Should handle zero stock quantity")
    void testZeroStockQuantity() {
        productEntity.setStockQuantity(0);
        Product mapped = mapper.toDomain(productEntity);
        assertEquals(0, mapped.getStockQuantity());
    }

    @Test
    @DisplayName("Should map all product types")
    void testAllProductTypesMappings() {
        for (ProductType type : ProductType.values()) {
            productEntity.setProductType(type);
            Product mapped = mapper.toDomain(productEntity);
            assertEquals(type, mapped.getProductType());
        }
    }
}
