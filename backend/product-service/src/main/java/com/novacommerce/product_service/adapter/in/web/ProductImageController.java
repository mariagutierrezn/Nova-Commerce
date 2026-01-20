package com.novacommerce.product_service.adapter.in.web;

import com.novacommerce.product_service.application.port.in.UploadProductImageUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para la gestión de imágenes de productos.
 */
@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Images", description = "Product image management endpoints")
public class ProductImageController {

    private final UploadProductImageUseCase uploadProductImageUseCase;

    @PostMapping(value = "/{productId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload product image", 
               description = "Upload an image for a specific product. Only ADMIN role can perform this action.")
    @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Map<String, String>> uploadProductImage(
            @PathVariable String productId,
            @RequestParam("imageFile") MultipartFile imageFile) {
        
        log.info("Received request to upload image for product ID: {}", productId);
        
        try {
            String imageUrl = uploadProductImageUseCase.uploadProductImage(productId, imageFile);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Image uploaded successfully");
            response.put("imageUrl", imageUrl);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid image file: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (IOException e) {
            log.error("Error uploading image for product ID: {}", productId, e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to upload image");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
