package com.novacommerce.order_service.application.port.in;

import com.novacommerce.order_service.domain.model.Order;
import com.novacommerce.order_service.domain.model.OrderStatus;

/**
 * Puerto de entrada (use case) para actualizar estado de órdenes.
 */
public interface UpdateOrderStatusUseCase {
    
    /**
     * Actualiza el estado de una orden validando transiciones permitidas.
     * 
     * @param orderId ID de la orden
     * @param newStatus nuevo estado
     * @return orden actualizada
     */
    Order updateOrderStatus(String orderId, OrderStatus newStatus);
}
