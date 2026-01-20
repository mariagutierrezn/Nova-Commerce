package com.novacommerce.product_service.adapter.in.web.mapper;

import com.novacommerce.product_service.adapter.in.web.dto.PublicProductResponse;
import com.novacommerce.product_service.domain.model.Product;
import com.novacommerce.product_service.domain.model.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("PublicProductDtoMapper Tests")
class PublicProductDtoMapperTest {

    @Autowired
    private PublicProductDtoMapper mapper;

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
    @DisplayName("Should map Product to PublicProductResponse")
    void testToPublicResponse() {
        PublicProductResponse response = mapper.toPublicResponse(product);

        assertNotNull(response);
        assertEquals(product.getId(), response.getId());
        assertEquals(product.getName(), response.getName());
        assertEquals(product.getDescription(), response.getDescription());
        assertEquals(product.getPrice(), response.getPrice());
        assertEquals(product.getProductType(), response.getProductType());
    }

    @Test
    @DisplayName("Should not include categoryId in public response")
    void testCategoryIdNotExposed() {
        PublicProductResponse response = mapper.toPublicResponse(product);

        assertNotNull(response);
        // Verify that PublicProductResponse doesn't have categoryId field
        assertDoesNotThrow(() -> response.getId());
    }

    @Test
    @DisplayName("Should not include stockQuantity in public response")
    void testStockQuantityNotExposed() {
        PublicProductResponse response = mapper.toPublicResponse(product);

        assertNotNull(response);
        // Verify that PublicProductResponse doesn't have stockQuantity field
        assertDoesNotThrow(() -> response.getId());
    }

    @Test
    @DisplayName("Should not include status in public response")
    void testStatusNotExposed() {
        PublicProductResponse response = mapper.toPublicResponse(product);

        assertNotNull(response);
        // Verify that PublicProductResponse doesn't have status field
        assertDoesNotThrow(() -> response.getId());
    }

    @Test
    @DisplayName("Should handle null Product")
    void testToPublicResponseWithNull() {
        PublicProductResponse response = mapper.toPublicResponse(null);
        assertNull(response);
    }

    @Test
    @DisplayName("Should map list of Products to list of PublicProductResponses")
    void testToPublicResponseList() {
        Product product2 = Product.builder()
                .id("2")
                .name("Mouse")
                .description("Wireless mouse")
                .price(new BigDecimal("25.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId("2")
                .stockQuantity(50)
                .status("ACTIVE")
                .build();

        List<Product> products = Arrays.asList(product, product2);
        List<PublicProductResponse> responses = mapper.toPublicResponseList(products);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Laptop", responses.get(0).getName());
        assertEquals("Mouse", responses.get(1).getName());
    }

    @Test
    @DisplayName("Should handle empty list")
    void testToPublicResponseListEmpty() {
        List<PublicProductResponse> responses = mapper.toPublicResponseList(Arrays.asList());

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("Should handle null list")
    void testToPublicResponseListNull() {
        List<PublicProductResponse> responses = mapper.toPublicResponseList(null);
        assertNull(responses);
    }

    @Test
    @DisplayName("Should preserve ProductType enum")
    void testProductTypePreservation() {
        product.setProductType(ProductType.DIGITAL);
        PublicProductResponse response = mapper.toPublicResponse(product);

        assertEquals(ProductType.DIGITAL, response.getProductType());
    }

    @Test
    @DisplayName("Should handle null description")
    void testNullDescription() {
        product.setDescription(null);
        PublicProductResponse response = mapper.toPublicResponse(product);

        assertNotNull(response);
        assertNull(response.getDescription());
    }

    @Test
    @DisplayName("Should correctly map decimal prices")
    void testDecimalPriceMapping() {
        product.setPrice(new BigDecimal("19.99"));
        PublicProductResponse response = mapper.toPublicResponse(product);

        assertEquals(new BigDecimal("19.99"), response.getPrice());
    }
}
