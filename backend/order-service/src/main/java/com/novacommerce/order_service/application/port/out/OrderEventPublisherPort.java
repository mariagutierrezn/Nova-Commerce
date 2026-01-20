package com.novacommerce.order_service.application.port.out;

import com.novacommerce.order_service.domain.event.OrderCreatedEvent;
import com.novacommerce.order_service.domain.event.OrderPaidEvent;

/**
 * Puerto de salida para publicar eventos de orden.
 * Implementación asíncrona usando RabbitMQ.
 */
public interface OrderEventPublisherPort {
    
    /**
     * Publica el evento de orden creada.
     * 
     * @param event evento de orden creada
     */
    void publishOrderCreated(OrderCreatedEvent event);
    
    /**
     * Publica el evento de orden pagada.
     * 
     * @param event evento de orden pagada
     */
    void publishOrderPaid(OrderPaidEvent event);
}
