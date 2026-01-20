package com.novacommerce.product_service.application.service;

import com.novacommerce.product_service.application.port.out.CategoryPersistencePort;
import com.novacommerce.product_service.domain.exception.ResourceNotFoundException;
import com.novacommerce.product_service.domain.model.Category;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Tests")
class CategoryServiceTest {

    @Mock
    private CategoryPersistencePort categoryPersistencePort;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id("1")
                .name("Electronics")
                .description("Electronic devices")
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("Should create category successfully")
    void testCreateCategory() {
        when(categoryPersistencePort.save(any(Category.class))).thenReturn(category);

        Category created = categoryService.createCategory(category);

        assertNotNull(created);
        assertEquals("Electronics", created.getName());
        verify(categoryPersistencePort, times(1)).save(category);
    }

    @Test
    @DisplayName("Should update category successfully")
    void testUpdateCategory() {
        Category updated = category;
        updated.setName("Computing");

        when(categoryPersistencePort.existsById("1")).thenReturn(true);
        when(categoryPersistencePort.save(any(Category.class))).thenReturn(updated);

        Category result = categoryService.updateCategory("1", updated);

        assertNotNull(result);
        assertEquals("Computing", result.getName());
        verify(categoryPersistencePort, times(1)).existsById("1");
        verify(categoryPersistencePort, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent category")
    void testUpdateCategoryNotFound() {
        when(categoryPersistencePort.existsById("999")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.updateCategory("999", category);
        });

        verify(categoryPersistencePort, times(1)).existsById("999");
    }

    @Test
    @DisplayName("Should delete category successfully")
    void testDeleteCategory() {
        when(categoryPersistencePort.existsById("1")).thenReturn(true);

        categoryService.deleteCategory("1");

        verify(categoryPersistencePort, times(1)).existsById("1");
        verify(categoryPersistencePort, times(1)).deleteById("1");
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent category")
    void testDeleteCategoryNotFound() {
        when(categoryPersistencePort.existsById("999")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.deleteCategory("999");
        });

        verify(categoryPersistencePort, times(1)).existsById("999");
    }

    @Test
    @DisplayName("Should get category by ID successfully")
    void testGetCategoryById() {
        when(categoryPersistencePort.findById("1")).thenReturn(Optional.of(category));

        Category found = categoryService.getCategoryById("1");

        assertNotNull(found);
        assertEquals("Electronics", found.getName());
        verify(categoryPersistencePort, times(1)).findById("1");
    }

    @Test
    @DisplayName("Should throw exception when category not found by ID")
    void testGetCategoryByIdNotFound() {
        when(categoryPersistencePort.findById("999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategoryById("999");
        });

        verify(categoryPersistencePort, times(1)).findById("999");
    }

    @Test
    @DisplayName("Should get all categories with pagination")
    void testGetAllCategories() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Category> categoryPage = new PageImpl<>(List.of(category), pageable, 1);

        when(categoryPersistencePort.findAll(pageable)).thenReturn(categoryPage);

        Page<Category> result = categoryService.getAllCategories(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Electronics", result.getContent().get(0).getName());
        verify(categoryPersistencePort, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should handle empty category list")
    void testGetAllCategoriesEmpty() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Category> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(categoryPersistencePort.findAll(pageable)).thenReturn(emptyPage);

        Page<Category> result = categoryService.getAllCategories(pageable);

        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        verify(categoryPersistencePort, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should handle multiple categories in one page")
    void testGetAllCategoriesMultiple() {
        Pageable pageable = PageRequest.of(0, 20);
        Category category2 = Category.builder()
                .id("2")
                .name("Clothing")
                .description("Clothing and fashion")
                .status("ACTIVE")
                .build();

        Page<Category> categoryPage = new PageImpl<>(List.of(category, category2), pageable, 2);

        when(categoryPersistencePort.findAll(pageable)).thenReturn(categoryPage);

        Page<Category> result = categoryService.getAllCategories(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(categoryPersistencePort, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should handle category with different statuses")
    void testCategoryWithDifferentStatuses() {
        category.setStatus("INACTIVE");
        when(categoryPersistencePort.save(category)).thenReturn(category);

        Category created = categoryService.createCategory(category);

        assertEquals("INACTIVE", created.getStatus());
    }
}
