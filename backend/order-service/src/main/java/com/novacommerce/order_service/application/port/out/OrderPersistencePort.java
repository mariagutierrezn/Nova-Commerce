package com.novacommerce.order_service.application.port.out;

import com.novacommerce.order_service.domain.model.Order;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de órdenes.
 */
public interface OrderPersistencePort {
    
    /**
     * Guarda una orden (crea o actualiza).
     */
    Order save(Order order);
    
    /**
     * Busca una orden por ID.
     */
    Optional<Order> findById(String id);
    
    /**
     * Busca todas las órdenes de un cliente.
     */
    List<Order> findByCustomerId(String customerId);
    
    /**
     * Busca todas las órdenes.
     */
    List<Order> findAll();
    
    /**
     * Elimina una orden.
     */
    void deleteById(String id);
}
