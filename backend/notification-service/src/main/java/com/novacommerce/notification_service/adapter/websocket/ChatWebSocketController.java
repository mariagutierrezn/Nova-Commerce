package com.novacommerce.notification_service.adapter.websocket;

import com.novacommerce.notification_service.application.service.ChatService;
import com.novacommerce.notification_service.domain.dto.ChatMessageDTO;
import com.novacommerce.notification_service.domain.dto.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Controlador WebSocket para gestionar mensajes de chat en tiempo real
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    
    private final ChatService chatService;
    
    /**
     * Recibe mensajes enviados por los clientes o asesores
     * Ruta: /app/chat.sendMessage
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/chat/{sessionId}")
    public ChatMessageDTO sendMessage(@Payload SendMessageRequest request) {
        log.info("Mensaje recibido vía WebSocket: sessionId={}, senderId={}", 
                request.getSessionId(), request.getSenderId());
        
        return chatService.sendMessage(request);
    }
    
    /**
     * Marca mensajes como leídos
     * Ruta: /app/chat.markAsRead/{sessionId}
     */
    @MessageMapping("/chat.markAsRead/{sessionId}")
    public void markAsRead(@DestinationVariable String sessionId, @Payload String userId) {
        log.info("Marcando mensajes como leídos: sessionId={}, userId={}", sessionId, userId);
        
        // Determinar si es asesor basado en el prefijo del userId
        boolean isAdvisor = userId.startsWith("admin_") || userId.startsWith("advisor_");
        
        chatService.markMessagesAsRead(sessionId, userId, isAdvisor);
    }
    
    /**
     * Se ejecuta cuando un cliente se suscribe al historial de una sesión
     * Ruta: /app/chat.history/{sessionId}
     */
    @SubscribeMapping("/chat.history/{sessionId}")
    public List<ChatMessageDTO> getHistory(@DestinationVariable String sessionId) {
        log.info("Cliente solicitando historial de sesión: {}", sessionId);
        return chatService.getSessionMessages(sessionId);
    }
    
    /**
     * Asigna un asesor a una sesión
     * Ruta: /app/chat.assign
     */
    @MessageMapping("/chat.assign")
    public void assignAdvisor(@Payload AssignAdvisorRequest request) {
        log.info("Asignando asesor: sessionId={}, advisorId={}", 
                request.getSessionId(), request.getAdvisorId());
        
        chatService.assignAdvisor(
            request.getSessionId(),
            request.getAdvisorId(),
            request.getAdvisorName()
        );
    }
    
    /**
     * Cierra una sesión de chat
     * Ruta: /app/chat.close/{sessionId}
     */
    @MessageMapping("/chat.close/{sessionId}")
    public void closeSession(@DestinationVariable String sessionId) {
        log.info("Cerrando sesión: {}", sessionId);
        chatService.closeSession(sessionId);
    }
    
    /**
     * DTO interno para asignar asesor
     */
    public static class AssignAdvisorRequest {
        private String sessionId;
        private String advisorId;
        private String advisorName;
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public String getAdvisorId() { return advisorId; }
        public void setAdvisorId(String advisorId) { this.advisorId = advisorId; }
        
        public String getAdvisorName() { return advisorName; }
        public void setAdvisorName(String advisorName) { this.advisorName = advisorName; }
    }
}
