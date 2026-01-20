package com.novacommerce.product_service.adapter.in.web;

import com.novacommerce.product_service.adapter.in.web.dto.ProductRequest;
import com.novacommerce.product_service.adapter.in.web.dto.ProductResponse;
import com.novacommerce.product_service.adapter.in.web.mapper.ProductDtoMapper;
import com.novacommerce.product_service.application.port.in.ManageProductsUseCase;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management endpoints")
public class ProductRestController {

    private final ManageProductsUseCase manageProductsUseCase;
    private final ProductDtoMapper productDtoMapper;

    @GetMapping
    @Operation(summary = "Get all products")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        
        var products = manageProductsUseCase.getAllProducts(pageable)
                .map(productDtoMapper::toResponse);
        
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        var product = manageProductsUseCase.getProductById(id);
        return ResponseEntity.ok(productDtoMapper.toResponse(product));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @PathVariable String categoryId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        
        var products = manageProductsUseCase.getProductsByCategoryId(categoryId, pageable)
                .map(productDtoMapper::toResponse);
        
        return ResponseEntity.ok(products);
    }

    @PostMapping
    @Operation(summary = "Create new product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        var product = productDtoMapper.toDomain(request);
        var createdProduct = manageProductsUseCase.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDtoMapper.toResponse(createdProduct));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest request) {
        
        var product = productDtoMapper.toDomain(request);
        var updatedProduct = manageProductsUseCase.updateProduct(id, product);
        return ResponseEntity.ok(productDtoMapper.toResponse(updatedProduct));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        manageProductsUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
