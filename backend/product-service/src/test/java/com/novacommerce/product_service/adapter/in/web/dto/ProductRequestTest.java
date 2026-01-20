package com.novacommerce.product_service.adapter.in.web.dto;

import com.novacommerce.product_service.domain.model.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductRequest DTO Tests")
class ProductRequestTest {

    private ProductRequest request;

    @BeforeEach
    void setUp() {
        request = ProductRequest.builder()
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
    @DisplayName("Should create ProductRequest with all fields")
    void testProductRequestCreation() {
        assertNotNull(request);
        assertEquals("Laptop", request.getName());
        assertEquals("High-performance laptop", request.getDescription());
        assertEquals(new BigDecimal("999.99"), request.getPrice());
        assertEquals(ProductType.PHYSICAL, request.getProductType());
        assertEquals("1", request.getCategoryId());
        assertEquals(10, request.getStockQuantity());
        assertEquals("ACTIVE", request.getStatus());
    }

    @Test
    @DisplayName("Should allow setting name")
    void testSetName() {
        request.setName("Desktop");
        assertEquals("Desktop", request.getName());
    }

    @Test
    @DisplayName("Should allow setting description")
    void testSetDescription() {
        request.setDescription("Gaming desktop");
        assertEquals("Gaming desktop", request.getDescription());
    }

    @Test
    @DisplayName("Should allow setting price")
    void testSetPrice() {
        request.setPrice(new BigDecimal("1299.99"));
        assertEquals(new BigDecimal("1299.99"), request.getPrice());
    }

    @Test
    @DisplayName("Should allow setting product type")
    void testSetProductType() {
        request.setProductType(ProductType.DIGITAL);
        assertEquals(ProductType.DIGITAL, request.getProductType());
    }

    @Test
    @DisplayName("Should allow setting category ID")
    void testSetCategoryId() {
        request.setCategoryId("2");
        assertEquals("2", request.getCategoryId());
    }

    @Test
    @DisplayName("Should allow setting stock quantity")
    void testSetStockQuantity() {
        request.setStockQuantity(50);
        assertEquals(50, request.getStockQuantity());
    }

    @Test
    @DisplayName("Should allow setting status")
    void testSetStatus() {
        request.setStatus("INACTIVE");
        assertEquals("INACTIVE", request.getStatus());
    }

    @Test
    @DisplayName("Should handle null description")
    void testNullDescription() {
        request.setDescription(null);
        assertNull(request.getDescription());
    }

    @Test
    @DisplayName("Should use builder pattern")
    void testBuilderPattern() {
        ProductRequest built = ProductRequest.builder()
                .name("Mouse")
                .price(new BigDecimal("25.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId("3")
                .stockQuantity(100)
                .status("ACTIVE")
                .build();

        assertEquals("Mouse", built.getName());
        assertEquals(new BigDecimal("25.99"), built.getPrice());
    }

    @Test
    @DisplayName("Should handle zero stock")
    void testZeroStock() {
        request.setStockQuantity(0);
        assertEquals(0, request.getStockQuantity());
    }

    @Test
    @DisplayName("Should handle different product types")
    void testAllProductTypes() {
        request.setProductType(ProductType.PHYSICAL);
        assertEquals(ProductType.PHYSICAL, request.getProductType());

        request.setProductType(ProductType.DIGITAL);
        assertEquals(ProductType.DIGITAL, request.getProductType());

        request.setProductType(ProductType.SERVICE);
        assertEquals(ProductType.SERVICE, request.getProductType());

        request.setProductType(ProductType.SUBSCRIPTION);
        assertEquals(ProductType.SUBSCRIPTION, request.getProductType());
    }
}
