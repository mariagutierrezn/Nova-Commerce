package com.novacommerce.product_service.application.service;

import com.novacommerce.product_service.application.port.in.ManageProductsUseCase;
import com.novacommerce.product_service.application.port.out.ProductPersistencePort;
import com.novacommerce.product_service.domain.exception.ResourceNotFoundException;
import com.novacommerce.product_service.domain.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService implements ManageProductsUseCase {

    private final ProductPersistencePort productPersistencePort;

    @Override
    @Transactional
    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product.getName());
        return productPersistencePort.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(String id, Product product) {
        log.info("Updating product with ID: {}", id);
        
        if (!productPersistencePort.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
        
        product.setId(id);
        return productPersistencePort.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(String id) {
        log.info("Deleting product with ID: {}", id);
        
        if (!productPersistencePort.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
        
        productPersistencePort.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(String id) {
        log.info("Fetching product with ID: {}", id);
        
        return productPersistencePort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        log.info("Fetching all products - Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return productPersistencePort.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategoryId(String categoryId, Pageable pageable) {
        log.info("Fetching products by category ID: {} - Page: {}, Size: {}", 
                categoryId, pageable.getPageNumber(), pageable.getPageSize());
        return productPersistencePort.findByCategoryId(categoryId, pageable);
    }

    @Override
    @Transactional
    public Product decrementStock(String productId, Integer quantity) {
        log.info("Decrementing stock for product ID: {} by quantity: {}", productId, quantity);
        
        Product product = productPersistencePort.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        
        Integer currentStock = product.getStockQuantity();
        if (currentStock == null || currentStock < quantity) {
            throw new IllegalStateException(
                    String.format("Insufficient stock for product %s. Available: %d, Requested: %d", 
                            productId, currentStock != null ? currentStock : 0, quantity));
        }
        
        product.setStockQuantity(currentStock - quantity);
        Product updated = productPersistencePort.save(product);
        
        log.info("Stock decremented successfully. Product: {}, New Stock: {}", productId, updated.getStockQuantity());
        return updated;
    }
}
