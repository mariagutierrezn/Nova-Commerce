package com.novacommerce.order_service.adapter.out.product;

import com.novacommerce.order_service.adapter.out.product.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Cliente Feign para comunicarse con product-service.
 */
@FeignClient(
        name = "product-service",
        url = "${app.services.product-service.url}"
)
public interface ProductServiceClient {

    @GetMapping("/internal/products/{id}")
    ProductResponse getProductById(
            @PathVariable("id") String id,
            @RequestHeader("X-Internal-API-Key") String apiKey
    );
    
    @PostMapping("/internal/products/{id}/decrement-stock")
    ProductResponse decrementStock(
            @PathVariable("id") String id,
            @RequestBody DecrementStockRequest request,
            @RequestHeader("X-Internal-API-Key") String apiKey
    );
    
    class DecrementStockRequest {
        public Integer quantity;
        
        public DecrementStockRequest() {}
        
        public DecrementStockRequest(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
