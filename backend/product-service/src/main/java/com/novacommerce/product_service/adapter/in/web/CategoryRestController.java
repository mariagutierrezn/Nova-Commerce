package com.novacommerce.product_service.adapter.in.web;

import com.novacommerce.product_service.adapter.in.web.dto.CategoryRequest;
import com.novacommerce.product_service.adapter.in.web.dto.CategoryResponse;
import com.novacommerce.product_service.adapter.in.web.mapper.CategoryDtoMapper;
import com.novacommerce.product_service.application.port.in.ManageCategoriesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryRestController {

    private final ManageCategoriesUseCase manageCategoriesUseCase;
    private final CategoryDtoMapper categoryDtoMapper;

    @GetMapping
    @Operation(summary = "Get all categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        
        var categories = manageCategoriesUseCase.getAllCategories(pageable)
                .map(categoryDtoMapper::toResponse);
        
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable String id) {
        var category = manageCategoriesUseCase.getCategoryById(id);
        return ResponseEntity.ok(categoryDtoMapper.toResponse(category));
    }

    @PostMapping
    @Operation(summary = "Create new category")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        var category = categoryDtoMapper.toDomain(request);
        var createdCategory = manageCategoriesUseCase.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryDtoMapper.toResponse(createdCategory));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody CategoryRequest request) {
        
        var category = categoryDtoMapper.toDomain(request);
        var updatedCategory = manageCategoriesUseCase.updateCategory(id, category);
        return ResponseEntity.ok(categoryDtoMapper.toResponse(updatedCategory));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        manageCategoriesUseCase.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
