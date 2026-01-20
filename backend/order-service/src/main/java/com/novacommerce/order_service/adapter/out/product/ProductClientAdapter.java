package com.novacommerce.order_service.adapter.out.product;

import com.novacommerce.order_service.adapter.out.product.dto.ProductResponse;
import com.novacommerce.order_service.application.port.out.ProductValidationPort;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Adaptador para validación de productos vía Feign.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductClientAdapter implements ProductValidationPort {

    private final ProductServiceClient productServiceClient;

    @Value("${app.jwt.internal-api-key}")
    private String internalApiKey;

    @Override
    public boolean isProductValid(String productId) {
        try {
            ProductResponse product = productServiceClient.getProductById(productId, internalApiKey);
            return product != null && 
                   product.getId() != null && 
                   "ACTIVE".equalsIgnoreCase(product.getStatus());
        } catch (FeignException.NotFound e) {
            log.warn("Product not found: {}", productId);
            return false;
        } catch (Exception e) {
            log.error("Error validating product: {}", productId, e);
            return false;
        }
    }

    @Override
    public String getProductName(String productId) {
        try {
            ProductResponse product = productServiceClient.getProductById(productId, internalApiKey);
            return product != null ? product.getName() : "Unknown";
        } catch (Exception e) {
            log.error("Error getting product name: {}", productId, e);
            return "Unknown";
        }
    }

    @Override
    public String getProductType(String productId) {
        try {
            ProductResponse product = productServiceClient.getProductById(productId, internalApiKey);
            // El tipo de producto podría venir de la categoría
            return product != null ? product.getCategoryName() : null;
        } catch (Exception e) {
            log.error("Error getting product type: {}", productId, e);
            return null;
        }
    }

    @Override
    public boolean hasStock(String productId, Integer quantity) {
        try {
            ProductResponse product = productServiceClient.getProductById(productId, internalApiKey);
            return product != null && 
                   product.getStock() != null && 
                   product.getStock() >= quantity;
        } catch (Exception e) {
            log.error("Error checking stock for product: {}", productId, e);
            return false;
        }
    }
}
