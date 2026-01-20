package com.novacommerce.product_service.adapter.in.web.dto;

import com.novacommerce.product_service.domain.model.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductResponse DTO Tests")
class ProductResponseTest {

    private ProductResponse response;

    @BeforeEach
    void setUp() {
        response = ProductResponse.builder()
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
    @DisplayName("Should create ProductResponse with all fields")
    void testProductResponseCreation() {
        assertNotNull(response);
        assertEquals("1", response.getId());
        assertEquals("Laptop", response.getName());
        assertEquals("High-performance laptop", response.getDescription());
        assertEquals(new BigDecimal("999.99"), response.getPrice());
        assertEquals(ProductType.PHYSICAL, response.getProductType());
        assertEquals("1", response.getCategoryId());
        assertEquals(10, response.getStockQuantity());
        assertEquals("ACTIVE", response.getStatus());
    }

    @Test
    @DisplayName("Should allow setting id")
    void testSetId() {
        response.setId("2");
        assertEquals("2", response.getId());
    }

    @Test
    @DisplayName("Should allow setting name")
    void testSetName() {
        response.setName("Desktop");
        assertEquals("Desktop", response.getName());
    }

    @Test
    @DisplayName("Should allow setting price")
    void testSetPrice() {
        response.setPrice(new BigDecimal("1299.99"));
        assertEquals(new BigDecimal("1299.99"), response.getPrice());
    }

    @Test
    @DisplayName("Should allow setting product type")
    void testSetProductType() {
        response.setProductType(ProductType.DIGITAL);
        assertEquals(ProductType.DIGITAL, response.getProductType());
    }

    @Test
    @DisplayName("Should allow setting stock quantity")
    void testSetStockQuantity() {
        response.setStockQuantity(50);
        assertEquals(50, response.getStockQuantity());
    }

    @Test
    @DisplayName("Should use builder pattern")
    void testBuilderPattern() {
        ProductResponse built = ProductResponse.builder()
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
        response.setDescription(null);
        assertNull(response.getDescription());
    }

    @Test
    @DisplayName("Should handle all product types")
    void testAllProductTypes() {
        response.setProductType(ProductType.DIGITAL);
        assertEquals(ProductType.DIGITAL, response.getProductType());

        response.setProductType(ProductType.SERVICE);
        assertEquals(ProductType.SERVICE, response.getProductType());

        response.setProductType(ProductType.SUBSCRIPTION);
        assertEquals(ProductType.SUBSCRIPTION, response.getProductType());
    }

    @Test
    @DisplayName("Should handle decimal prices")
    void testDecimalPrices() {
        response.setPrice(new BigDecimal("0.99"));
        assertEquals(new BigDecimal("0.99"), response.getPrice());

        response.setPrice(new BigDecimal("9999.99"));
        assertEquals(new BigDecimal("9999.99"), response.getPrice());
    }
}
