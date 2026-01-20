package com.novacommerce.product_service.adapter.out.persistence;

import com.novacommerce.product_service.application.port.out.CategoryPersistencePort;
import com.novacommerce.product_service.domain.model.Category;
import com.novacommerce.product_service.repository.CategoryRepository;
import com.novacommerce.product_service.repository.mapper.CategoryEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryPersistencePort {

    private final CategoryRepository categoryRepository;
    private final CategoryEntityMapper categoryEntityMapper;

    @Override
    public Category save(Category category) {
        var entity = categoryEntityMapper.toEntity(category);
        var savedEntity = categoryRepository.save(entity);
        return categoryEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Category> findById(String id) {
        return categoryRepository.findById(id)
                .map(categoryEntityMapper::toDomain);
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryEntityMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return categoryRepository.existsById(id);
    }
}
