package com.novacommerce.product_service.application.service;

import com.novacommerce.product_service.application.port.in.UploadProductImageUseCase;
import com.novacommerce.product_service.application.port.out.ProductImageStoragePort;
import com.novacommerce.product_service.application.port.out.ProductPersistencePort;
import com.novacommerce.product_service.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Servicio para manejar la subida de imágenes de productos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageService implements UploadProductImageUseCase {

    private final ProductPersistencePort productPersistencePort;
    private final ProductImageStoragePort productImageStoragePort;

    @Override
    @Transactional
    public String uploadProductImage(String productId, MultipartFile imageFile) throws IOException {
        log.info("Uploading image for product ID: {}", productId);

        // Validar que el producto exista
        var product = productPersistencePort.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        // Validar que el archivo sea una imagen válida
        if (!productImageStoragePort.isValidImage(imageFile)) {
            throw new IllegalArgumentException("Invalid image file. Only JPG, PNG, and WEBP are allowed");
        }

        // Si el producto ya tiene una imagen, eliminar la anterior
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            try {
                productImageStoragePort.deleteImage(product.getImageUrl());
                log.info("Deleted old image for product ID: {}", productId);
            } catch (IOException e) {
                log.warn("Failed to delete old image for product ID: {}", productId, e);
                // No fallar si no se puede eliminar la imagen anterior
            }
        }

        // Guardar la nueva imagen
        String imageUrl = productImageStoragePort.storeImage(productId, imageFile);
        log.info("Image stored successfully: {}", imageUrl);

        // Actualizar el producto con la nueva URL
        product.setImageUrl(imageUrl);
        productPersistencePort.save(product);

        log.info("Product image updated successfully for product ID: {}", productId);
        return imageUrl;
    }
}
