package com.novacommerce.product_service.application.service;

import com.novacommerce.product_service.application.port.out.ProductPersistencePort;
import com.novacommerce.product_service.domain.exception.ResourceNotFoundException;
import com.novacommerce.product_service.domain.model.Product;
import com.novacommerce.product_service.domain.model.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock
    private ProductPersistencePort productPersistencePort;

    @InjectMocks
    private ProductService productService;

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
    @DisplayName("Should create product successfully")
    void testCreateProduct() {
        when(productPersistencePort.save(any(Product.class))).thenReturn(product);

        Product created = productService.createProduct(product);

        assertNotNull(created);
        assertEquals("Laptop", created.getName());
        assertEquals(new BigDecimal("999.99"), created.getPrice());
        verify(productPersistencePort, times(1)).save(product);
    }

    @Test
    @DisplayName("Should update product successfully")
    void testUpdateProduct() {
        Product updatedProduct = product;
        updatedProduct.setName("Gaming Laptop");

        when(productPersistencePort.existsById("1")).thenReturn(true);
        when(productPersistencePort.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct("1", updatedProduct);

        assertNotNull(result);
        assertEquals("Gaming Laptop", result.getName());
        verify(productPersistencePort, times(1)).existsById("1");
        verify(productPersistencePort, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent product")
    void testUpdateProductNotFound() {
        when(productPersistencePort.existsById("999")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProduct("999", product);
        });

        verify(productPersistencePort, times(1)).existsById("999");
    }

    @Test
    @DisplayName("Should delete product successfully")
    void testDeleteProduct() {
        when(productPersistencePort.existsById("1")).thenReturn(true);

        productService.deleteProduct("1");

        verify(productPersistencePort, times(1)).existsById("1");
        verify(productPersistencePort, times(1)).deleteById("1");
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void testDeleteProductNotFound() {
        when(productPersistencePort.existsById("999")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProduct("999");
        });

        verify(productPersistencePort, times(1)).existsById("999");
    }

    @Test
    @DisplayName("Should get product by ID successfully")
    void testGetProductById() {
        when(productPersistencePort.findById("1")).thenReturn(Optional.of(product));

        Product found = productService.getProductById("1");

        assertNotNull(found);
        assertEquals("Laptop", found.getName());
        verify(productPersistencePort, times(1)).findById("1");
    }

    @Test
    @DisplayName("Should throw exception when product not found by ID")
    void testGetProductByIdNotFound() {
        when(productPersistencePort.findById("999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById("999");
        });

        verify(productPersistencePort, times(1)).findById("999");
    }

    @Test
    @DisplayName("Should get all products with pagination")
    void testGetAllProducts() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productPersistencePort.findAll(pageable)).thenReturn(productPage);

        Page<Product> result = productService.getAllProducts(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Laptop", result.getContent().get(0).getName());
        verify(productPersistencePort, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should get products by category ID with pagination")
    void testGetProductsByCategoryId() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productPersistencePort.findByCategoryId("1", pageable)).thenReturn(productPage);

        Page<Product> result = productService.getProductsByCategoryId("1", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productPersistencePort, times(1)).findByCategoryId("1", pageable);
    }

    @Test
    @DisplayName("Should handle empty product list")
    void testGetAllProductsEmpty() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(productPersistencePort.findAll(pageable)).thenReturn(emptyPage);

        Page<Product> result = productService.getAllProducts(pageable);

        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        verify(productPersistencePort, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should handle different product types")
    void testCreateProductDifferentTypes() {
        for (ProductType type : ProductType.values()) {
            Product p = product;
            p.setProductType(type);
            when(productPersistencePort.save(p)).thenReturn(p);

            Product created = productService.createProduct(p);

            assertEquals(type, created.getProductType());
        }
    }
}
