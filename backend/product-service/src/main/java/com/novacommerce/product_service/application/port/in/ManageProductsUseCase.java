package com.novacommerce.product_service.application.port.in;

import com.novacommerce.product_service.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManageProductsUseCase {
    
    Product createProduct(Product product);
    
    Product updateProduct(String id, Product product);
    
    void deleteProduct(String id);
    
    Product getProductById(String id);
    
    Page<Product> getAllProducts(Pageable pageable);
    
    Page<Product> getProductsByCategoryId(String categoryId, Pageable pageable);
}
