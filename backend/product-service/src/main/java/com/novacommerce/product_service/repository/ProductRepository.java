package com.novacommerce.product_service.repository;

import com.novacommerce.product_service.repository.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<ProductEntity, String> {
    
    Page<ProductEntity> findByCategoryId(String categoryId, Pageable pageable);
    
    /**
     * Encuentra productos activos con stock disponible.
     *
     * @param status estado del producto (ej. "ACTIVE")
     * @param minStock cantidad mínima de stock
     * @return lista de productos que cumplen los criterios
     */
    List<ProductEntity> findByStatusAndStockQuantityGreaterThan(String status, int minStock);
}
