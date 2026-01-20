package com.novacommerce.product_service.application.service;

import com.novacommerce.product_service.application.port.out.ProductPersistencePort;
import com.novacommerce.product_service.domain.model.Product;
import com.novacommerce.product_service.domain.model.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PublicProductService Tests")
class PublicProductServiceTest {

    @Mock
    private ProductPersistencePort productPersistencePort;

    @InjectMocks
    private PublicProductService publicProductService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = Product.builder()
                .id("1")
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("999.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId("1")
                .stockQuantity(10)
                .status("ACTIVE")
                .build();

        product2 = Product.builder()
                .id("2")
                .name("Mouse")
                .description("Wireless mouse")
                .price(new BigDecimal("25.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId("2")
                .stockQuantity(50)
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("Should get public home products successfully")
    void testGetPublicHomeProducts() {
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        when(productPersistencePort.findActiveProductsWithStockRandomOrder(12))
                .thenReturn(expectedProducts);

        List<Product> result = publicProductService.getPublicHomeProducts(12);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals("Mouse", result.get(1).getName());
        verify(productPersistencePort, times(1)).findActiveProductsWithStockRandomOrder(12);
    }

    @Test
    @DisplayName("Should return empty list when no products available")
    void testGetPublicHomeProductsEmpty() {
        when(productPersistencePort.findActiveProductsWithStockRandomOrder(12))
                .thenReturn(Collections.emptyList());

        List<Product> result = publicProductService.getPublicHomeProducts(12);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productPersistencePort, times(1)).findActiveProductsWithStockRandomOrder(12);
    }

    @Test
    @DisplayName("Should respect limit parameter")
    void testGetPublicHomeProductsWithDifferentLimit() {
        List<Product> expectedProducts = Arrays.asList(product1);
        when(productPersistencePort.findActiveProductsWithStockRandomOrder(5))
                .thenReturn(expectedProducts);

        List<Product> result = publicProductService.getPublicHomeProducts(5);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productPersistencePort, times(1)).findActiveProductsWithStockRandomOrder(5);
    }

    @Test
    @DisplayName("Should handle large limit")
    void testGetPublicHomeProductsLargeLimit() {
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        when(productPersistencePort.findActiveProductsWithStockRandomOrder(100))
                .thenReturn(expectedProducts);

        List<Product> result = publicProductService.getPublicHomeProducts(100);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productPersistencePort, times(1)).findActiveProductsWithStockRandomOrder(100);
    }

    @Test
    @DisplayName("Should handle limit of 1")
    void testGetPublicHomeProductsLimitOne() {
        when(productPersistencePort.findActiveProductsWithStockRandomOrder(1))
                .thenReturn(Arrays.asList(product1));

        List<Product> result = publicProductService.getPublicHomeProducts(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        verify(productPersistencePort, times(1)).findActiveProductsWithStockRandomOrder(1);
    }

    @Test
    @DisplayName("Should only return active products with stock")
    void testGetPublicHomeProductsActiveWithStock() {
        // Verify that only ACTIVE products with stock > 0 are returned
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        when(productPersistencePort.findActiveProductsWithStockRandomOrder(12))
                .thenReturn(expectedProducts);

        List<Product> result = publicProductService.getPublicHomeProducts(12);

        assertNotNull(result);
        result.forEach(product -> {
            assertEquals("ACTIVE", product.getStatus());
            assertTrue(product.getStockQuantity() > 0);
        });
    }

    @Test
    @DisplayName("Should use transactional read-only")
    void testTransactionalReadOnly() {
        // This test verifies the @Transactional(readOnly = true) annotation
        when(productPersistencePort.findActiveProductsWithStockRandomOrder(12))
                .thenReturn(Arrays.asList(product1));

        List<Product> result = publicProductService.getPublicHomeProducts(12);

        assertNotNull(result);
        verify(productPersistencePort, times(1)).findActiveProductsWithStockRandomOrder(12);
    }
}
