package com.novacommerce.notification_service.infrastructure.repository;

import com.novacommerce.notification_service.domain.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para Notificaciones
 */
@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    Page<Notification> findByRead(boolean read, Pageable pageable);
    
    Page<Notification> findByType(String type, Pageable pageable);
    
    Page<Notification> findByReadAndType(boolean read, String type, Pageable pageable);
    
    @Query("{ 'read': false }")
    Page<Notification> findAllUnread(Pageable pageable);
}
