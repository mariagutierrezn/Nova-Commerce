package com.novacommerce.notification_service.infrastructure.repository;

import com.novacommerce.notification_service.domain.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestionar los mensajes de chat en MongoDB
 */
@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    
    /**
     * Buscar todos los mensajes de una sesión ordenados por fecha
     */
    List<ChatMessage> findBySessionIdOrderBySentAtAsc(String sessionId);
    
    /**
     * Buscar mensajes no leídos de una sesión
     */
    List<ChatMessage> findBySessionIdAndReadFalseOrderBySentAtAsc(String sessionId);
    
    /**
     * Contar mensajes no leídos de una sesión
     */
    long countBySessionIdAndReadFalse(String sessionId);
    
    /**
     * Obtener el último mensaje de una sesión
     */
    ChatMessage findFirstBySessionIdOrderBySentAtDesc(String sessionId);
}
