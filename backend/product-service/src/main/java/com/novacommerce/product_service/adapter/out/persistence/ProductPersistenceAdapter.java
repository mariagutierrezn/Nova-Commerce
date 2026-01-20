package com.novacommerce.product_service.adapter.out.persistence;

import com.novacommerce.product_service.application.port.out.ProductPersistencePort;
import com.novacommerce.product_service.domain.model.Product;
import com.novacommerce.product_service.repository.ProductRepository;
import com.novacommerce.product_service.repository.entity.ProductEntity;
import com.novacommerce.product_service.repository.mapper.ProductEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductPersistencePort {

    private final ProductRepository productRepository;
    private final ProductEntityMapper productEntityMapper;

    @Override
    public Product save(Product product) {
        var entity = productEntityMapper.toEntity(product);
        var savedEntity = productRepository.save(entity);
        return productEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(String id) {
        return productRepository.findById(id)
                .map(productEntityMapper::toDomain);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productEntityMapper::toDomain);
    }

    @Override
    public Page<Product> findByCategoryId(String categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(productEntityMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        productRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return productRepository.existsById(id);
    }

    @Override
    public List<Product> findActiveProductsWithStockRandomOrder(int limit) {
        // MongoDB query: find active products with stock > 0, limit results
        // Note: MongoDB doesn't have native RANDOM() like SQL, so we get all matching and limit
        List<ProductEntity> entities = productRepository.findByStatusAndStockQuantityGreaterThan("ACTIVE", 0);
        // Shuffle for randomness and limit
        java.util.Collections.shuffle(entities);
        return entities.stream()
                .limit(limit)
                .map(productEntityMapper::toDomain)
                .collect(Collectors.toList());
    }
}
