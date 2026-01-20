package com.novacommerce.product_service.application.port.in;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Puerto de entrada para subir imágenes de productos.
 */
public interface UploadProductImageUseCase {
    
    /**
     * Sube una imagen para un producto existente.
     *
     * @param productId ID del producto
     * @param imageFile archivo de imagen
     * @return URL pública de la imagen
     * @throws IOException si ocurre un error al guardar la imagen
     * @throws IllegalArgumentException si el archivo no es válido
     * @throws com.novacommerce.product_service.domain.exception.ResourceNotFoundException si el producto no existe
     */
    String uploadProductImage(String productId, MultipartFile imageFile) throws IOException;
}
