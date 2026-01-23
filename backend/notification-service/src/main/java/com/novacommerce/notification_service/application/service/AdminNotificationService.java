package com.novacommerce.notification_service.application.service;

import com.novacommerce.notification_service.domain.dto.NotificationDTO;
import com.novacommerce.notification_service.domain.model.Notification;
import com.novacommerce.notification_service.infrastructure.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para gestión de notificaciones en tiempo real
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminNotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Crea y envía una notificación por WebSocket a los admins
     */
    @Transactional
    public Notification createAndSendNotification(NotificationDTO dto) {
        log.info("Creating notification: type={}, orderId={}", dto.getType(), dto.getOrderId());
        
        // Crear entidad
        Notification notification = Notification.builder()
                .type(dto.getType())
                .title((String) dto.getData().get("title"))
                .message((String) dto.getData().get("message"))
                .priority((String) dto.getData().getOrDefault("priority", "MEDIUM"))
                .read(false)
                .createdAt(Instant.now())
                .orderId(dto.getOrderId())
                .customerId(dto.getCustomerId())
                .productId(dto.getProductId())
                .metadata(dto.getData())
                .build();
        
        // Guardar en MongoDB
        notification = notificationRepository.save(notification);
        log.info("Notification saved with ID: {}", notification.getId());
        
        // Enviar por WebSocket a admins conectados
        try {
            messagingTemplate.convertAndSend("/topic/admin/notifications", dto);
            log.info("Notification sent via WebSocket to /topic/admin/notifications");
        } catch (Exception e) {
            log.error("Error sending notification via WebSocket", e);
        }
        
        return notification;
    }

    /**
     * Obtiene notificaciones con filtros
     */
    @Transactional(readOnly = true)
    public Page<Notification> getNotifications(Pageable pageable, Boolean read, String type) {
        if (read != null && type != null) {
            return notificationRepository.findByReadAndType(read, type, pageable);
        } else if (read != null) {
            return notificationRepository.findByRead(read, pageable);
        } else if (type != null) {
            return notificationRepository.findByType(type, pageable);
        } else {
            return notificationRepository.findAll(pageable);
        }
    }

    /**
     * Marca una notificación como leída
     */
    @Transactional
    public void markAsRead(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
        
        notification.setRead(true);
        notificationRepository.save(notification);
        log.info("Notification {} marked as read", id);
    }

    /**
     * Marca todas las notificaciones como leídas
     */
    @Transactional
    public void markAllAsRead() {
        Page<Notification> unreadNotifications = notificationRepository.findAllUnread(Pageable.unpaged());
        unreadNotifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
        log.info("All notifications marked as read");
    }

    /**
     * Elimina una notificación
     */
    @Transactional
    public void deleteNotification(String id) {
        notificationRepository.deleteById(id);
        log.info("Notification {} deleted", id);
    }

    /**
     * Crea NotificationDTO a partir de un evento de orden
     */
    public NotificationDTO createOrderNotification(String orderId, String customerId, BigDecimal total) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "Nuevo Pedido");
        data.put("message", String.format("Pedido #%s por $%.2f", orderId, total));
        data.put("priority", "HIGH");
        data.put("timestamp", Instant.now().toString());
        
        return NotificationDTO.builder()
                .type("ORDER_CREATED")
                .orderId(orderId)
                .customerId(customerId)
                .data(data)
                .build();
    }

    /**
     * Crea NotificationDTO para pago recibido
     */
    public NotificationDTO createPaymentNotification(String orderId, BigDecimal amount) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "Pago Recibido");
        data.put("message", String.format("Pago de $%.2f para pedido #%s", amount, orderId));
        data.put("priority", "MEDIUM");
        data.put("timestamp", Instant.now().toString());
        
        return NotificationDTO.builder()
                .type("PAYMENT_RECEIVED")
                .orderId(orderId)
                .data(data)
                .build();
    }
}
