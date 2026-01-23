package com.novacommerce.notification_service.infrastructure.controller;

import com.novacommerce.notification_service.domain.dto.NotificationDTO;
import com.novacommerce.notification_service.domain.model.Notification;
import com.novacommerce.notification_service.application.service.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API para gestión de notificaciones
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NotificationController {

    private final AdminNotificationService adminNotificationService;

    /**
     * Obtiene notificaciones con paginación y filtros
     */
    @GetMapping
    public ResponseEntity<Page<Notification>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean read,
            @RequestParam(required = false) String type) {
        
        log.info("GET /api/notifications - page={}, size={}, read={}, type={}", page, size, read, type);
        Page<Notification> notifications = adminNotificationService.getNotifications(
                PageRequest.of(page, size), read, type);
        
        return ResponseEntity.ok(notifications);
    }

    /**
     * Marca una notificación como leída
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id) {
        log.info("PATCH /api/notifications/{}/read", id);
        adminNotificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Marca todas las notificaciones como leídas
     */
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        log.info("PATCH /api/notifications/read-all");
        adminNotificationService.markAllAsRead();
        return ResponseEntity.ok().build();
    }

    /**
     * Elimina una notificación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        log.info("DELETE /api/notifications/{}", id);
        adminNotificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint de prueba para enviar notificación manualmente
     */
    @PostMapping("/test")
    public ResponseEntity<Notification> sendTestNotification(@RequestBody NotificationDTO dto) {
        log.info("POST /api/notifications/test - {}", dto);
        Notification notification = adminNotificationService.createAndSendNotification(dto);
        return ResponseEntity.ok(notification);
    }
}
