package com.novacommerce.order_service.application.port.out;

/**
 * Puerto de salida para validación de clientes.
 */
public interface CustomerValidationPort {
    
    /**
     * Valida que el cliente existe y está activo.
     * 
     * @param customerId ID del cliente
     * @return true si el cliente es válido
     */
    boolean isCustomerValid(String customerId);
    
    /**
     * Obtiene el estado del cliente.
     */
    String getCustomerStatus(String customerId);
    
    /**
     * Obtiene el nivel de fidelidad del cliente.
     */
    String getCustomerLoyaltyLevel(String customerId);
}
