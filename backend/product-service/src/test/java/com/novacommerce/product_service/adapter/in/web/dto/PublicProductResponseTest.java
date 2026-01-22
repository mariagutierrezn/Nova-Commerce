package com.novacommerce.product_service.adapter.in.web.dto;

import com.novacommerce.product_service.domain.model.ProductType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PublicProductResponse DTO Tests")
class PublicProductResponseTest {

    @Test
    @DisplayName("Should create PublicProductResponse with all fields")
    void testPublicProductResponseCreation() {
        PublicProductResponse response = PublicProductResponse.builder()
                .id("1")
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("999.99"))
                .productType(ProductType.PHYSICAL)
                .build();

        assertNotNull(response);
        assertEquals("1", response.getId());
        assertEquals("Laptop", response.getName());
        assertEquals("High-performance laptop", response.getDescription());
        assertEquals(new BigDecimal("999.99"), response.getPrice());
        assertEquals(ProductType.PHYSICAL, response.getProductType());
    }

    @Test
    @DisplayName("Should use no-args constructor")
    void testNoArgsConstructor() {
        PublicProductResponse response = new PublicProductResponse();
        assertNotNull(response);
    }

    @Test
    @DisplayName("Should use all-args constructor")
    void testAllArgsConstructor() {
        PublicProductResponse response = new PublicProductResponse(
                "1",
                "Laptop",
                "High-performance laptop",
                new BigDecimal("999.99"),
                ProductType.PHYSICAL,
                "http://localhost:8083/images/products/1/test.jpg",
                "category1",
                10,
                true,
                15
        );

        assertEquals("1", response.getId());
        assertEquals("Laptop", response.getName());
    }

    @Test
    @DisplayName("Should allow setting individual fields")
    void testSetters() {
        PublicProductResponse response = new PublicProductResponse();
        response.setId("2");
        response.setName("Mouse");
        response.setDescription("Wireless mouse");
        response.setPrice(new BigDecimal("25.99"));
        response.setProductType(ProductType.DIGITAL);

        assertEquals("2", response.getId());
        assertEquals("Mouse", response.getName());
        assertEquals("Wireless mouse", response.getDescription());
        assertEquals(new BigDecimal("25.99"), response.getPrice());
        assertEquals(ProductType.DIGITAL, response.getProductType());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        PublicProductResponse response = PublicProductResponse.builder()
                .id(null)
                .name(null)
                .description(null)
                .price(null)
                .productType(null)
                .build();

        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getDescription());
        assertNull(response.getPrice());
        assertNull(response.getProductType());
    }

    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void testEqualsAndHashCode() {
        PublicProductResponse response1 = PublicProductResponse.builder()
                .id("1")
                .name("Laptop")
                .price(new BigDecimal("999.99"))
                .build();

        PublicProductResponse response2 = PublicProductResponse.builder()
                .id("1")
                .name("Laptop")
                .price(new BigDecimal("999.99"))
                .build();

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void testToString() {
        PublicProductResponse response = PublicProductResponse.builder()
                .id("1")
                .name("Laptop")
                .build();

        String toString = response.toString();
        assertTrue(toString.contains("Laptop"));
        assertTrue(toString.contains("1"));
    }
}
