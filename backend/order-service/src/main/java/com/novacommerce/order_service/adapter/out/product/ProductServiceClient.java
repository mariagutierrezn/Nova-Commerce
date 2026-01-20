package com.novacommerce.order_service.adapter.out.product;

import com.novacommerce.order_service.adapter.out.product.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

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
}
