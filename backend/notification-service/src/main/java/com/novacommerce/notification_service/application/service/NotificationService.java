package com.novacommerce.notification_service.application.service;

import com.novacommerce.notification_service.domain.model.OrderCreatedEvent;
import com.novacommerce.notification_service.domain.model.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Servicio de notificaciones.
 * Procesa eventos y envía notificaciones (email, SMS, push, etc.).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    /**
     * Procesa evento de orden creada y envía notificación al cliente.
     */
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Processing OrderCreatedEvent for order: {}", event.getOrderId());
        
        try {
            // Simular envío de email
            sendOrderConfirmationEmail(event);
            
            // En el futuro se pueden agregar:
            // - SMS de confirmación
            // - Push notification
            // - Actualización de analytics
            // - Notificación a administradores
            
            log.info("OrderCreatedEvent processed successfully for order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error processing OrderCreatedEvent for order: {}", event.getOrderId(), e);
            throw new IllegalStateException("Failed to process OrderCreatedEvent for order: " + event.getOrderId(), e);
        }
    }

    /**
     * Procesa evento de orden pagada y envía notificación de pago.
     */
    public void handleOrderPaid(OrderPaidEvent event) {
        log.info("Processing OrderPaidEvent for order: {}", event.getOrderId());
        
        try {
            // Simular envío de email de confirmación de pago
            sendPaymentConfirmationEmail(event);
            
            // En el futuro se pueden agregar:
            // - Generación de factura
            // - Actualización de inventario
            // - Notificación a shipping service
            // - Actualización de puntos de lealtad
            
            log.info("OrderPaidEvent processed successfully for order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error processing OrderPaidEvent for order: {}", event.getOrderId(), e);
            throw new IllegalStateException("Failed to process OrderPaidEvent for order: " + event.getOrderId(), e);
        }
    }

    /**
     * Simula el envío de email de confirmación de orden.
     */
    private void sendOrderConfirmationEmail(OrderCreatedEvent event) {
        log.info("📧 Sending order confirmation email:");
        log.info("   To: Customer {}", event.getCustomerId());
        log.info("   Order ID: {}", event.getOrderId());
        log.info("   Total: ${}", event.getTotalAfterDiscount());
        log.info("   Items: {}", event.getItems().size());
        // En producción: integrar con servicio de email (SendGrid, SES, etc.)
    }

    /**
     * Simula el envío de email de confirmación de pago.
     */
    private void sendPaymentConfirmationEmail(OrderPaidEvent event) {
        log.info("📧 Sending payment confirmation email:");
        log.info("   To: Customer {}", event.getCustomerId());
        log.info("   Order ID: {}", event.getOrderId());
        log.info("   Amount Paid: ${}", event.getTotalAmount());
        log.info("   Payment Method: {}", event.getPaymentMethod());
        // En producción: integrar con servicio de email
    }
}
