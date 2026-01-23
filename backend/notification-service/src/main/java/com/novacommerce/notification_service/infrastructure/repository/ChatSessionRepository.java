package com.novacommerce.notification_service.infrastructure.repository;

import com.novacommerce.notification_service.domain.model.ChatSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar las sesiones de chat en MongoDB
 */
@Repository
public interface ChatSessionRepository extends MongoRepository<ChatSession, String> {
    
    /**
     * Buscar sesión activa de un cliente
     */
    Optional<ChatSession> findByCustomerIdAndStatus(String customerId, ChatSession.ChatStatus status);
    
    /**
     * Buscar todas las sesiones de un cliente
     */
    List<ChatSession> findByCustomerIdOrderByCreatedAtDesc(String customerId);
    
    /**
     * Buscar sesiones asignadas a un asesor
     */
    List<ChatSession> findByAdvisorIdAndStatus(String advisorId, ChatSession.ChatStatus status);
    
    /**
     * Buscar todas las sesiones en espera
     */
    List<ChatSession> findByStatusOrderByCreatedAtAsc(ChatSession.ChatStatus status);
    
    /**
     * Buscar todas las sesiones activas
     */
    List<ChatSession> findByStatusInOrderByLastActivityDesc(List<ChatSession.ChatStatus> statuses);
    
    /**
     * Contar sesiones no leídas por el asesor
     */
    long countByStatusAndUnreadByAdvisorGreaterThan(ChatSession.ChatStatus status, int unreadCount);
}
