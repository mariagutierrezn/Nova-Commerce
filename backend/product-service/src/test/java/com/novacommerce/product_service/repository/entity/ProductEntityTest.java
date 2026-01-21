package com.novacommerce.product_service.repository.entity;

import com.novacommerce.product_service.domain.model.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductEntity JPA Entity Tests")
class ProductEntityTest {

    private ProductEntity productEntity;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    @DisplayName("Should create product entity with all fields")
    void testProductEntityCreation() {
        assertNotNull(productEntity);
        assertEquals("1", productEntity.getId());
        assertEquals("Laptop", productEntity.getName());
        assertEquals(ProductType.PHYSICAL, productEntity.getProductType());
    }

    @Test
    @DisplayName("Should allow setting all product fields")
    void testSetAllFields() {
        productEntity.setName("Desktop");
        productEntity.setPrice(new BigDecimal("1299.99"));
        productEntity.setStockQuantity(50);
        productEntity.setStatus("INACTIVE");

        assertEquals("Desktop", productEntity.getName());
        assertEquals(new BigDecimal("1299.99"), productEntity.getPrice());
        assertEquals(50, productEntity.getStockQuantity());
        assertEquals("INACTIVE", productEntity.getStatus());
    }

    @Test
    @DisplayName("Should handle decimal prices correctly")
    void testDecimalPrices() {
        productEntity.setPrice(new BigDecimal("19.99"));
        assertEquals(new BigDecimal("19.99"), productEntity.getPrice());
    }

    @Test
    @DisplayName("Should handle large decimal prices")
    void testLargePrices() {
        productEntity.setPrice(new BigDecimal("9999999.99"));
        assertEquals(new BigDecimal("9999999.99"), productEntity.getPrice());
    }

    @Test
    @DisplayName("Should handle different product types")
    void testDifferentProductTypes() {
        productEntity.setProductType(ProductType.DIGITAL);
        assertEquals(ProductType.DIGITAL, productEntity.getProductType());

        productEntity.setProductType(ProductType.SERVICE);
        assertEquals(ProductType.SERVICE, productEntity.getProductType());

        productEntity.setProductType(ProductType.SUBSCRIPTION);
        assertEquals(ProductType.SUBSCRIPTION, productEntity.getProductType());
    }

    @Test
    @DisplayName("Should use builder pattern")
    void testBuilderPattern() {
        ProductEntity built = ProductEntity.builder()
                .id("2")
                .name("Mouse")
                .price(new BigDecimal("25.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId("2")
                .stockQuantity(100)
                .status("ACTIVE")
                .build();

        assertEquals("2", built.getId());
        assertEquals("Mouse", built.getName());
    }

    @Test
    @DisplayName("Should handle null description")
    void testNullDescription() {
        productEntity.setDescription(null);
        assertNull(productEntity.getDescription());
    }

    @Test
    @DisplayName("Should create product entity with no-args constructor")
    void testNoArgsConstructor() {
        ProductEntity entity = new ProductEntity();
        assertNotNull(entity);
    }

    @Test
    @DisplayName("Should support all-args constructor")
    void testAllArgsConstructor() {
        ProductEntity entity = new ProductEntity(
                "5", "Keyboard", "Mechanical", new BigDecimal("150.00"),
                ProductType.PHYSICAL, "3", 25, "ACTIVE", "http://localhost:8083/images/products/5/test.jpg",
                false, 0
        );
        assertEquals("5", entity.getId());
        assertEquals("Keyboard", entity.getName());
    }

    @Test
    @DisplayName("Should handle zero stock correctly")
    void testZeroStock() {
        productEntity.setStockQuantity(0);
        assertEquals(0, productEntity.getStockQuantity());
    }

    @Test
    @DisplayName("Should handle high stock quantities")
    void testHighStockQuantity() {
        productEntity.setStockQuantity(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, productEntity.getStockQuantity());
    }
}
