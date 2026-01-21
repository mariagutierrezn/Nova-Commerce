package com.novacommerce.order_service.application.port.out;

/**
 * Puerto de salida para validación de productos.
 */
public interface ProductValidationPort {
    
    /**
     * Valida que el producto existe y está activo.
     * 
     * @param productId ID del producto
     * @return true si el producto es válido
     */
    boolean isProductValid(String productId);
    
    /**
     * Obtiene el nombre del producto.
     */
    String getProductName(String productId);
    
    /**
     * Obtiene el tipo de producto (para descuentos).
     */
    String getProductType(String productId);
    
    /**
     * Verifica stock disponible.
     */
    boolean hasStock(String productId, Integer quantity);
    
    /**
     * Decrementa el stock de un producto.
     * 
     * @param productId ID del producto
     * @param quantity cantidad a decrementar
     */
    void decrementStock(String productId, Integer quantity);
}
