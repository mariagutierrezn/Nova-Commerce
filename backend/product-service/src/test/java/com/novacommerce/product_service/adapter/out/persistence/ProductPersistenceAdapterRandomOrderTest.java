package com.novacommerce.product_service.adapter.out.persistence;

import com.novacommerce.product_service.domain.model.Product;
import com.novacommerce.product_service.domain.model.ProductType;
import com.novacommerce.product_service.repository.ProductRepository;
import com.novacommerce.product_service.repository.entity.ProductEntity;
import com.novacommerce.product_service.repository.mapper.ProductEntityMapper;
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
@DisplayName("ProductPersistenceAdapter - Random Order Tests")
class ProductPersistenceAdapterRandomOrderTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductEntityMapper productEntityMapper;

    @InjectMocks
    private ProductPersistenceAdapter productPersistenceAdapter;

    private ProductEntity entity1;
    private ProductEntity entity2;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        entity1 = ProductEntity.builder()
                .id("1")
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("999.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId("1")
                .stockQuantity(10)
                .status("ACTIVE")
                .build();

        entity2 = ProductEntity.builder()
                .id("2")
                .name("Mouse")
                .description("Wireless mouse")
                .price(new BigDecimal("25.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId("2")
                .stockQuantity(50)
                .status("ACTIVE")
                .build();

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
    @DisplayName("Should find active products with stock in random order")
    void testFindActiveProductsWithStockRandomOrder() {
        List<ProductEntity> entities = Arrays.asList(entity1, entity2);
        when(productRepository.findByStatusAndStockQuantityGreaterThan("ACTIVE", 0))
                .thenReturn(entities);
        when(productEntityMapper.toDomain(entity1)).thenReturn(product1);
        when(productEntityMapper.toDomain(entity2)).thenReturn(product2);

        List<Product> result = productPersistenceAdapter.findActiveProductsWithStockRandomOrder(12);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals("Mouse", result.get(1).getName());
        verify(productRepository, times(1)).findByStatusAndStockQuantityGreaterThan("ACTIVE", 0);
        verify(productEntityMapper, times(2)).toDomain(any(ProductEntity.class));
    }

    @Test
    @DisplayName("Should return empty list when no active products found")
    void testFindActiveProductsWithStockRandomOrderEmpty() {
        when(productRepository.findByStatusAndStockQuantityGreaterThan("ACTIVE", 0))
                .thenReturn(Collections.emptyList());

        List<Product> result = productPersistenceAdapter.findActiveProductsWithStockRandomOrder(12);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findByStatusAndStockQuantityGreaterThan("ACTIVE", 0);
        verify(productEntityMapper, never()).toDomain(any(ProductEntity.class));
    }

    @Test
    @DisplayName("Should use correct status filter")
    void testStatusFilter() {
        when(productRepository.findByStatusAndStockQuantityGreaterThan("ACTIVE", 0))
                .thenReturn(Arrays.asList(entity1));
        when(productEntityMapper.toDomain(entity1)).thenReturn(product1);

        productPersistenceAdapter.findActiveProductsWithStockRandomOrder(12);

        verify(productRepository, times(1)).findByStatusAndStockQuantityGreaterThan("ACTIVE", 0);
    }

    @Test
    @DisplayName("Should use correct stock filter")
    void testStockFilter() {
        when(productRepository.findByStatusAndStockQuantityGreaterThan("ACTIVE", 0))
                .thenReturn(Arrays.asList(entity1));
        when(productEntityMapper.toDomain(entity1)).thenReturn(product1);

        productPersistenceAdapter.findActiveProductsWithStockRandomOrder(12);

        // Verify that minStock is 0 (stockQuantity > 0)
        verify(productRepository, times(1)).findByStatusAndStockQuantityGreaterThan("ACTIVE", 0);
    }

    @Test
    @DisplayName("Should respect limit parameter")
    void testLimitParameter() {
        when(productRepository.findByStatusAndStockQuantityGreaterThan("ACTIVE", 0))
                .thenReturn(Arrays.asList(entity1));
        when(productEntityMapper.toDomain(entity1)).thenReturn(product1);

        List<Product> result = productPersistenceAdapter.findActiveProductsWithStockRandomOrder(5);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findByStatusAndStockQuantityGreaterThan("ACTIVE", 0);
    }

    @Test
    @DisplayName("Should map all entities to domain")
    void testEntityToDomainMapping() {
        List<ProductEntity> entities = Arrays.asList(entity1, entity2);
        when(productRepository.findByStatusAndStockQuantityGreaterThan("ACTIVE", 0))
                .thenReturn(entities);
        when(productEntityMapper.toDomain(entity1)).thenReturn(product1);
        when(productEntityMapper.toDomain(entity2)).thenReturn(product2);

        List<Product> result = productPersistenceAdapter.findActiveProductsWithStockRandomOrder(12);

        assertEquals(2, result.size());
        verify(productEntityMapper, times(1)).toDomain(entity1);
        verify(productEntityMapper, times(1)).toDomain(entity2);
    }

    @Test
    @DisplayName("Should handle large limit")
    void testLargeLimit() {
        when(productRepository.findByStatusAndStockQuantityGreaterThan("ACTIVE", 0))
                .thenReturn(Arrays.asList(entity1, entity2));
        when(productEntityMapper.toDomain(entity1)).thenReturn(product1);
        when(productEntityMapper.toDomain(entity2)).thenReturn(product2);

        List<Product> result = productPersistenceAdapter.findActiveProductsWithStockRandomOrder(100);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findByStatusAndStockQuantityGreaterThan("ACTIVE", 0);
    }

    @Test
    @DisplayName("Should handle limit of 1")
    void testLimitOne() {
        when(productRepository.findByStatusAndStockQuantityGreaterThan("ACTIVE", 0))
                .thenReturn(Arrays.asList(entity1));
        when(productEntityMapper.toDomain(entity1)).thenReturn(product1);

        List<Product> result = productPersistenceAdapter.findActiveProductsWithStockRandomOrder(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        verify(productRepository, times(1)).findByStatusAndStockQuantityGreaterThan("ACTIVE", 0);
    }

    @Test
    @DisplayName("Should return products in order returned by repository")
    void testOrderPreservation() {
        List<ProductEntity> entities = Arrays.asList(entity2, entity1); // Reversed order
        when(productRepository.findByStatusAndStockQuantityGreaterThan("ACTIVE", 0))
                .thenReturn(entities);
        when(productEntityMapper.toDomain(entity2)).thenReturn(product2);
        when(productEntityMapper.toDomain(entity1)).thenReturn(product1);

        List<Product> result = productPersistenceAdapter.findActiveProductsWithStockRandomOrder(12);

        assertEquals(2, result.size());
        assertEquals("Mouse", result.get(0).getName()); // First should be Mouse
        assertEquals("Laptop", result.get(1).getName()); // Second should be Laptop
    }
}
