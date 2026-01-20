package com.novacommerce.product_service.adapter.out.storage;

import com.novacommerce.product_service.application.port.out.ProductImageStoragePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Adapter para almacenamiento local de imágenes de productos.
 * Guarda las imágenes en el filesystem local.
 */
@Slf4j
@Component
public class LocalProductImageStorageAdapter implements ProductImageStoragePort {

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Value("${app.upload.path:uploads/products}")
    private String uploadPath;

    @Value("${app.upload.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public String storeImage(String productId, MultipartFile imageFile) throws IOException {
        log.info("Storing image for product ID: {}", productId);

        // Validar el archivo
        if (!isValidImage(imageFile)) {
            throw new IllegalArgumentException("Invalid image file");
        }

        // Crear directorio si no existe
        Path productDirectory = Paths.get(uploadPath, productId);
        if (!Files.exists(productDirectory)) {
            Files.createDirectories(productDirectory);
            log.debug("Created directory: {}", productDirectory);
        }

        // Generar nombre único para el archivo
        String originalFilename = imageFile.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String filename = UUID.randomUUID().toString() + extension;

        // Guardar el archivo
        Path filePath = productDirectory.resolve(filename);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        log.info("Image saved to: {}", filePath);

        // Retornar URL pública
        String imageUrl = String.format("%s/images/products/%s/%s", baseUrl, productId, filename);
        log.info("Image URL: {}", imageUrl);
        return imageUrl;
    }

    @Override
    public void deleteImage(String imageUrl) throws IOException {
        log.info("Deleting image: {}", imageUrl);

        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            // Extraer el path relativo de la URL
            // Ejemplo: http://localhost:8083/images/products/1/uuid.jpg -> uploads/products/1/uuid.jpg
            String relativePath = imageUrl.replace(baseUrl + "/images/products/", "");
            Path filePath = Paths.get(uploadPath, relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Image deleted: {}", filePath);
            } else {
                log.warn("Image file not found: {}", filePath);
            }
        } catch (Exception e) {
            log.error("Error deleting image: {}", imageUrl, e);
            throw new IOException("Failed to delete image: " + imageUrl, e);
        }
    }

    @Override
    public boolean isValidImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            log.warn("Image file is null or empty");
            return false;
        }

        // Validar tipo de contenido
        String contentType = imageFile.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            log.warn("Invalid content type: {}", contentType);
            return false;
        }

        // Validar tamaño
        if (imageFile.getSize() > MAX_FILE_SIZE) {
            log.warn("File size exceeds maximum: {} bytes", imageFile.getSize());
            return false;
        }

        log.debug("Image file is valid: {}", imageFile.getOriginalFilename());
        return true;
    }
}
