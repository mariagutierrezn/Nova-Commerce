package com.novacommerce.order_service.application.port.in;

import com.novacommerce.order_service.domain.model.Order;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada (use case) para consultar órdenes.
 */
public interface GetOrderUseCase {
    
    /**
     * Obtiene una orden por ID.
     */
    Optional<Order> getOrderById(String id);
    
    /**
     * Obtiene todas las órdenes de un cliente.
     */
    List<Order> getOrdersByCustomerId(String customerId);
    
    /**
     * Obtiene todas las órdenes.
     */
    List<Order> getAllOrders();
}
