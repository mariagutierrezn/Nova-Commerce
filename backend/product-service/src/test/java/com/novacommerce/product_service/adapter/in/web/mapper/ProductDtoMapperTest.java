package com.novacommerce.product_service.adapter.in.web.mapper;

import com.novacommerce.product_service.adapter.in.web.dto.ProductRequest;
import com.novacommerce.product_service.adapter.in.web.dto.ProductResponse;
import com.novacommerce.product_service.domain.model.Product;
import com.novacommerce.product_service.domain.model.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("ProductDtoMapper Tests")
class ProductDtoMapperTest {

    @Autowired
    private ProductDtoMapper mapper;

    private ProductRequest productRequest;
    private Product product;

    @BeforeEach
    void setUp() {
        productRequest = ProductRequest.builder()
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
    @DisplayName("Should map ProductRequest to Product")
    void testToDomain() {
        Product mappedProduct = mapper.toDomain(productRequest);

        assertNotNull(mappedProduct);
        assertEquals(productRequest.getName(), mappedProduct.getName());
        assertEquals(productRequest.getDescription(), mappedProduct.getDescription());
        assertEquals(productRequest.getPrice(), mappedProduct.getPrice());
        assertEquals(productRequest.getProductType(), mappedProduct.getProductType());
        assertEquals(productRequest.getCategoryId(), mappedProduct.getCategoryId());
        assertEquals(productRequest.getStockQuantity(), mappedProduct.getStockQuantity());
        assertEquals(productRequest.getStatus(), mappedProduct.getStatus());
    }

    @Test
    @DisplayName("Should map Product to ProductResponse")
    void testToResponse() {
        ProductResponse response = mapper.toResponse(product);

        assertNotNull(response);
        assertEquals(product.getId(), response.getId());
        assertEquals(product.getName(), response.getName());
        assertEquals(product.getDescription(), response.getDescription());
        assertEquals(product.getPrice(), response.getPrice());
        assertEquals(product.getProductType(), response.getProductType());
        assertEquals(product.getCategoryId(), response.getCategoryId());
        assertEquals(product.getStockQuantity(), response.getStockQuantity());
        assertEquals(product.getStatus(), response.getStatus());
    }

    @Test
    @DisplayName("Should handle null ProductRequest")
    void testToDomainWithNull() {
        Product result = mapper.toDomain(null);
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null Product")
    void testToResponseWithNull() {
        ProductResponse result = mapper.toResponse(null);
        assertNull(result);
    }

    @Test
    @DisplayName("Should preserve ProductType enum")
    void testProductTypePreservation() {
        productRequest.setProductType(ProductType.DIGITAL);
        Product mapped = mapper.toDomain(productRequest);
        assertEquals(ProductType.DIGITAL, mapped.getProductType());
    }

    @Test
    @DisplayName("Should handle null description")
    void testNullDescription() {
        productRequest.setDescription(null);
        Product mapped = mapper.toDomain(productRequest);
        assertNull(mapped.getDescription());
    }

    @Test
    @DisplayName("Should map decimal prices")
    void testDecimalPriceMapping() {
        productRequest.setPrice(new BigDecimal("19.99"));
        Product mapped = mapper.toDomain(productRequest);
        assertEquals(new BigDecimal("19.99"), mapped.getPrice());
    }

    @Test
    @DisplayName("Should map all product types")
    void testAllProductTypes() {
        for (ProductType type : ProductType.values()) {
            productRequest.setProductType(type);
            Product mapped = mapper.toDomain(productRequest);
            assertEquals(type, mapped.getProductType());
        }
    }

    @Test
    @DisplayName("Should handle zero stock quantity")
    void testZeroStockQuantity() {
        productRequest.setStockQuantity(0);
        Product mapped = mapper.toDomain(productRequest);
        assertEquals(0, mapped.getStockQuantity());
    }

    @Test
    @DisplayName("Should map response with all fields")
    void testResponseMapping() {
        ProductResponse response = mapper.toResponse(product);
        assertEquals("Laptop", response.getName());
        assertEquals("1", response.getId());
        assertEquals(new BigDecimal("999.99"), response.getPrice());
        assertEquals(ProductType.PHYSICAL, response.getProductType());
    }
}
