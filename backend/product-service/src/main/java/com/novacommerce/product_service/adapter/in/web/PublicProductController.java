package com.novacommerce.product_service.adapter.in.web;

import com.novacommerce.product_service.adapter.in.web.dto.PublicProductResponse;
import com.novacommerce.product_service.adapter.in.web.mapper.PublicProductDtoMapper;
import com.novacommerce.product_service.application.port.in.GetPublicProductsUseCase;
import com.novacommerce.product_service.domain.model.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST público para productos.
 * No requiere autenticación.
 */
@RestController
@RequestMapping("/api/public/products")
@RequiredArgsConstructor
@Tag(name = "Public Products", description = "Public product endpoints without authentication")
public class PublicProductController {

    private final GetPublicProductsUseCase getPublicProductsUseCase;

    private static final int DEFAULT_HOME_PRODUCTS_LIMIT = 12;

    @GetMapping("/home")
    @Operation(summary = "Get products for home page", 
               description = "Returns a random selection of active products with stock for the home page")
    public ResponseEntity<List<PublicProductResponse>> getHomeProducts() {
        var products = getPublicProductsUseCase.getPublicHomeProducts(DEFAULT_HOME_PRODUCTS_LIMIT);
        var response = products.stream()
                .map(this::toPublicResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Get all public products with optional filters", 
               description = "Returns all active products with stock. Supports filtering by discount, category, and search")
    public ResponseEntity<List<PublicProductResponse>> getAllPublicProducts(
            @RequestParam(required = false) Boolean hasDiscount,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String search) {
        
        var allProducts = getPublicProductsUseCase.getPublicHomeProducts(100); // Get more products for filtering
        
        var filteredProducts = allProducts.stream()
                .filter(product -> {
                    // Filter by discount
                    if (hasDiscount != null && hasDiscount) {
                        if (product.getHasDiscount() == null || !product.getHasDiscount()) {
                            return false;
                        }
                    }
                    
                    // Filter by category
                    if (categoryId != null && !categoryId.isEmpty()) {
                        if (product.getCategoryId() == null || !product.getCategoryId().equals(categoryId)) {
                            return false;
                        }
                    }
                    
                    // Filter by search term (name or description)
                    if (search != null && !search.isEmpty()) {
                        String searchLower = search.toLowerCase();
                        String name = product.getName() != null ? product.getName().toLowerCase() : "";
                        String desc = product.getDescription() != null ? product.getDescription().toLowerCase() : "";
                        if (!name.contains(searchLower) && !desc.contains(searchLower)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .map(this::toPublicResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(filteredProducts);
    }
    
    private PublicProductResponse toPublicResponse(Product product) {
        return PublicProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .productType(product.getProductType())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategoryId())
                .stockQuantity(product.getStockQuantity())
                .hasDiscount(product.getHasDiscount())
                .discountPercentage(product.getDiscountPercentage())
                .build();
    }
}
