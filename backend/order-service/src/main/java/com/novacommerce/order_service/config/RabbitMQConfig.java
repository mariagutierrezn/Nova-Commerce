package com.novacommerce.order_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el Order Service.
 * Define exchanges, queues y bindings para mensajería asíncrona.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange-name:nova.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.order-created-key:order.created}")
    private String orderCreatedRoutingKey;

    @Value("${app.rabbitmq.order-paid-key:order.paid}")
    private String orderPaidRoutingKey;

    // Queues
    public static final String ORDER_CREATED_QUEUE = "order.created.queue";
    public static final String ORDER_PAID_QUEUE = "order.paid.queue";

    /**
     * Exchange directo para eventos de órdenes.
     */
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(exchange, true, false);
    }

    /**
     * Queue para eventos de orden creada.
     */
    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(ORDER_CREATED_QUEUE, true);
    }

    /**
     * Queue para eventos de orden pagada.
     */
    @Bean
    public Queue orderPaidQueue() {
        return new Queue(ORDER_PAID_QUEUE, true);
    }

    /**
     * Binding entre exchange y queue para orden creada.
     */
    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder
                .bind(orderCreatedQueue())
                .to(orderExchange())
                .with(orderCreatedRoutingKey);
    }

    /**
     * Binding entre exchange y queue para orden pagada.
     */
    @Bean
    public Binding orderPaidBinding() {
        return BindingBuilder
                .bind(orderPaidQueue())
                .to(orderExchange())
                .with(orderPaidRoutingKey);
    }

    /**
     * RabbitTemplate configurado con Jackson2JsonMessageConverter.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
