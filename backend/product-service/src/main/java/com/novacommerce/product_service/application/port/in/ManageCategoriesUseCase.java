package com.novacommerce.product_service.application.port.in;

import com.novacommerce.product_service.domain.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManageCategoriesUseCase {
    
    Category createCategory(Category category);
    
    Category updateCategory(String id, Category category);
    
    void deleteCategory(String id);
    
    Category getCategoryById(String id);
    
    Page<Category> getAllCategories(Pageable pageable);
}
