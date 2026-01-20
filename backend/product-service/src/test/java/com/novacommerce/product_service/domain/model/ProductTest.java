package com.novacommerce.product_service.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Domain Model Tests")
class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
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
    @DisplayName("Should create product with all required fields")
    void testProductCreation() {
        assertNotNull(product);
        assertEquals("1", product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals("High-performance laptop", product.getDescription());
        assertEquals(new BigDecimal("999.99"), product.getPrice());
        assertEquals(ProductType.PHYSICAL, product.getProductType());
        assertEquals("1", product.getCategoryId());
        assertEquals(10, product.getStockQuantity());
        assertEquals("ACTIVE", product.getStatus());
    }

    @Test
    @DisplayName("Should allow setting product name")
    void testSetName() {
        product.setName("Desktop");
        assertEquals("Desktop", product.getName());
    }

    @Test
    @DisplayName("Should allow setting product price")
    void testSetPrice() {
        BigDecimal newPrice = new BigDecimal("1299.99");
        product.setPrice(newPrice);
        assertEquals(newPrice, product.getPrice());
    }

    @Test
    @DisplayName("Should allow setting product type")
    void testSetProductType() {
        product.setProductType(ProductType.DIGITAL);
        assertEquals(ProductType.DIGITAL, product.getProductType());
    }

    @Test
    @DisplayName("Should allow setting stock quantity")
    void testSetStockQuantity() {
        product.setStockQuantity(50);
        assertEquals(50, product.getStockQuantity());
    }

    @Test
    @DisplayName("Should allow setting product status")
    void testSetStatus() {
        product.setStatus("INACTIVE");
        assertEquals("INACTIVE", product.getStatus());
    }

    @Test
    @DisplayName("Should allow setting category ID")
    void testSetCategoryId() {
        product.setCategoryId("5");
        assertEquals("5", product.getCategoryId());
    }

    @Test
    @DisplayName("Should handle zero price")
    void testZeroPrice() {
        product.setPrice(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, product.getPrice());
    }

    @Test
    @DisplayName("Should handle zero stock quantity")
    void testZeroStockQuantity() {
        product.setStockQuantity(0);
        assertEquals(0, product.getStockQuantity());
    }

    @Test
    @DisplayName("Should handle null description")
    void testNullDescription() {
        product.setDescription(null);
        assertNull(product.getDescription());
    }

    @Test
    @DisplayName("Should use builder pattern correctly")
    void testBuilderPattern() {
        Product builtProduct = Product.builder()
                .id("2")
                .name("Keyboard")
                .description("Mechanical keyboard")
                .price(new BigDecimal("150.00"))
                .productType(ProductType.PHYSICAL)
                .categoryId("2")
                .stockQuantity(25)
                .status("ACTIVE")
                .build();

        assertEquals("2", builtProduct.getId());
        assertEquals("Keyboard", builtProduct.getName());
        assertEquals(ProductType.PHYSICAL, builtProduct.getProductType());
    }
}
