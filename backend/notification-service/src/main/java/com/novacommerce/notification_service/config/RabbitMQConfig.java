package com.novacommerce.notification_service.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el Notification Service.
 * Define el message converter para serialización JSON.
 */
@Configuration
public class RabbitMQConfig {

    /**
     * Configura el message converter para serializar/deserializar JSON.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
