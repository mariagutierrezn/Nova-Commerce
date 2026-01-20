package com.novacommerce.order_service.adapter.out.messaging;

import com.novacommerce.order_service.application.port.out.OrderEventPublisherPort;
import com.novacommerce.order_service.domain.event.OrderCreatedEvent;
import com.novacommerce.order_service.domain.event.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida para publicar eventos de orden en RabbitMQ.
 * Implementa el puerto OrderEventPublisherPort.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisherAdapter implements OrderEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange-name:nova.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.order-created-key:order.created}")
    private String orderCreatedRoutingKey;

    @Value("${app.rabbitmq.order-paid-key:order.paid}")
    private String orderPaidRoutingKey;

    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("Publishing OrderCreatedEvent for order: {}", event.getOrderId());
            rabbitTemplate.convertAndSend(exchange, orderCreatedRoutingKey, event);
            log.info("OrderCreatedEvent published successfully for order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error publishing OrderCreatedEvent for order: {}", event.getOrderId(), e);
            // No lanzamos excepción para no afectar la creación de la orden
            // En producción, considerar Dead Letter Queue o retry mechanism
        }
    }

    @Override
    public void publishOrderPaid(OrderPaidEvent event) {
        try {
            log.info("Publishing OrderPaidEvent for order: {}", event.getOrderId());
            rabbitTemplate.convertAndSend(exchange, orderPaidRoutingKey, event);
            log.info("OrderPaidEvent published successfully for order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error publishing OrderPaidEvent for order: {}", event.getOrderId(), e);
            // No lanzamos excepción para no afectar el pago
        }
    }
}
