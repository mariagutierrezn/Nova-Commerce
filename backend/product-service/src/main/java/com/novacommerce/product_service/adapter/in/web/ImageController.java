package com.novacommerce.product_service.adapter.in.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Controlador REST para servir imágenes de productos.
 */
@Slf4j
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    @Value("${app.upload.path:uploads/products}")
    private String uploadPath;

    @GetMapping("/products/{productId}/{filename}")
    public ResponseEntity<Resource> getProductImage(
            @PathVariable String productId,
            @PathVariable String filename) {
        
        try {
            log.info("Requesting image: {}/{}", productId, filename);
            
            // Construir la ruta del archivo
            Path filePath = Paths.get(uploadPath, productId, filename);
            
            // Verificar que el archivo existe
            if (!Files.exists(filePath)) {
                log.warn("Image not found: {}", filePath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            // Determinar el tipo de contenido según la extensión
            String contentType = determineContentType(filename);
            
            // Cargar el recurso
            Resource resource = new UrlResource(filePath.toUri());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
                    
        } catch (MalformedURLException e) {
            log.error("Error loading image: {}/{}", productId, filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String determineContentType(String filename) {
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG_VALUE;
        } else if (filename.endsWith(".png")) {
            return MediaType.IMAGE_PNG_VALUE;
        } else if (filename.endsWith(".webp")) {
            return "image/webp";
        } else if (filename.endsWith(".gif")) {
            return MediaType.IMAGE_GIF_VALUE;
        }
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }
}
