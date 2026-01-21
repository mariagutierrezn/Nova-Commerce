package com.novacommerce.product_service.adapter.in.web;

import com.novacommerce.product_service.application.port.in.ManageProductsUseCase;
import com.novacommerce.product_service.domain.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints internos para consumo entre microservicios.
 * Protegidos por InternalApiKeyFilter y permitidos en SecurityConfig.
 */
@RestController
@RequestMapping("/internal/products")
public class InternalProductController {

    private final ManageProductsUseCase manageProductsUseCase;

    public InternalProductController(ManageProductsUseCase manageProductsUseCase) {
        this.manageProductsUseCase = manageProductsUseCase;
    }

    static class InternalProductResponse {
        public String id;
        public String name;
        public String description;
        public java.math.BigDecimal price;
        public String categoryName; // opcional, puede ser null
        public Integer stock; // mapeado desde stockQuantity
        public String status;

        static InternalProductResponse from(Product p) {
            InternalProductResponse r = new InternalProductResponse();
            r.id = p.getId();
            r.name = p.getName();
            r.description = p.getDescription();
            r.price = p.getPrice();
            r.categoryName = null; // no disponible directamente; se puede enriquecer si es necesario
            r.stock = p.getStockQuantity();
            r.status = p.getStatus();
            return r;
        }
    }
    
    static class DecrementStockRequest {
        public Integer quantity;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternalProductResponse> getByIdInternal(@PathVariable String id) {
        Product product = manageProductsUseCase.getProductById(id);
        return ResponseEntity.ok(InternalProductResponse.from(product));
    }
    
    @PostMapping("/{id}/decrement-stock")
    public ResponseEntity<InternalProductResponse> decrementStock(
            @PathVariable String id,
            @RequestBody DecrementStockRequest request) {
        Product product = manageProductsUseCase.decrementStock(id, request.quantity);
        return ResponseEntity.ok(InternalProductResponse.from(product));
    }
}
