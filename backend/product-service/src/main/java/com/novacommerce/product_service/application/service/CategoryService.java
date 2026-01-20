package com.novacommerce.product_service.application.service;

import com.novacommerce.product_service.application.port.in.ManageCategoriesUseCase;
import com.novacommerce.product_service.application.port.out.CategoryPersistencePort;
import com.novacommerce.product_service.domain.exception.ResourceNotFoundException;
import com.novacommerce.product_service.domain.model.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService implements ManageCategoriesUseCase {

    private final CategoryPersistencePort categoryPersistencePort;

    @Override
    @Transactional
    public Category createCategory(Category category) {
        log.info("Creating new category: {}", category.getName());
        return categoryPersistencePort.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(String id, Category category) {
        log.info("Updating category with ID: {}", id);
        
        if (!categoryPersistencePort.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with ID: " + id);
        }
        
        category.setId(id);
        return categoryPersistencePort.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(String id) {
        log.info("Deleting category with ID: {}", id);
        
        if (!categoryPersistencePort.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with ID: " + id);
        }
        
        categoryPersistencePort.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(String id) {
        log.info("Fetching category with ID: {}", id);
        
        return categoryPersistencePort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Category> getAllCategories(Pageable pageable) {
        log.info("Fetching all categories - Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return categoryPersistencePort.findAll(pageable);
    }
}
