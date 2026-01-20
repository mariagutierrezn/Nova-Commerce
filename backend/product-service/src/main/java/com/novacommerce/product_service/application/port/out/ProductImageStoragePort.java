package com.novacommerce.product_service.application.port.out;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Puerto de salida para almacenamiento de imágenes de productos.
 * Permite abstraer la implementación del almacenamiento (local, S3, etc.)
 */
public interface ProductImageStoragePort {
    
    /**
     * Almacena una imagen de producto y retorna la URL pública.
     *
     * @param productId ID del producto
     * @param imageFile archivo de imagen
     * @return URL pública de la imagen almacenada
     * @throws IOException si ocurre un error al guardar el archivo
     * @throws IllegalArgumentException si el archivo no es válido
     */
    String storeImage(String productId, MultipartFile imageFile) throws IOException;
    
    /**
     * Elimina la imagen de un producto.
     *
     * @param imageUrl URL de la imagen a eliminar
     * @throws IOException si ocurre un error al eliminar el archivo
     */
    void deleteImage(String imageUrl) throws IOException;
    
    /**
     * Valida que el archivo sea una imagen válida.
     *
     * @param imageFile archivo a validar
     * @return true si es una imagen válida
     */
    boolean isValidImage(MultipartFile imageFile);
}
