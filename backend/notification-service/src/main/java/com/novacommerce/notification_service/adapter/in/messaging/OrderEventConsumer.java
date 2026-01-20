package com.novacommerce.notification_service.adapter.in.messaging;

import com.novacommerce.notification_service.application.service.NotificationService;
import com.novacommerce.notification_service.domain.model.OrderCreatedEvent;
import com.novacommerce.notification_service.domain.model.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumer de eventos de RabbitMQ.
 * Escucha eventos de órdenes y los procesa.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final NotificationService notificationService;

    /**
     * Consume eventos de orden creada desde la cola.
     */
    @RabbitListener(queues = "${app.rabbitmq.order-created-queue:order.created.queue}")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("📨 Received OrderCreatedEvent: orderId={}, customerId={}", 
                event.getOrderId(), event.getCustomerId());
        
        try {
            notificationService.handleOrderCreated(event);
        } catch (Exception e) {
            log.error("Error processing OrderCreatedEvent: orderId={}", 
                    event.getOrderId(), e);
            // En producción, implementar retry o Dead Letter Queue
            throw e; // Re-lanzar para que RabbitMQ reintente o envíe a DLQ
        }
    }

    /**
     * Consume eventos de orden pagada desde la cola.
     */
    @RabbitListener(queues = "${app.rabbitmq.order-paid-queue:order.paid.queue}")
    public void handleOrderPaid(OrderPaidEvent event) {
        log.info("📨 Received OrderPaidEvent: orderId={}, customerId={}", 
                event.getOrderId(), event.getCustomerId());
        
        try {
            notificationService.handleOrderPaid(event);
        } catch (Exception e) {
            log.error("Error processing OrderPaidEvent: orderId={}", 
                    event.getOrderId(), e);
            // En producción, implementar retry o Dead Letter Queue
            throw e;
        }
    }
}
