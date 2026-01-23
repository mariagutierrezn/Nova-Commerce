package com.novacommerce.notification_service.infrastructure.controller;

import com.novacommerce.notification_service.application.service.ChatService;
import com.novacommerce.notification_service.domain.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar el sistema de chat
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"}, allowCredentials = "true")
public class ChatController {
    
    private final ChatService chatService;
    
    /**
     * Crea una nueva sesión de chat
     */
    @PostMapping("/sessions")
    public ResponseEntity<ChatSessionDTO> createSession(@Valid @RequestBody CreateChatSessionRequest request) {
        log.info("POST /api/chat/sessions - Crear nueva sesión para: {}", request.getCustomerId());
        
        ChatSessionDTO session = chatService.createSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }
    
    /**
     * Obtiene todas las sesiones activas (para asesores)
     */
    @GetMapping("/sessions/active")
    public ResponseEntity<List<ChatSessionDTO>> getActiveSessions() {
        log.info("GET /api/chat/sessions/active - Obtener sesiones activas");
        
        List<ChatSessionDTO> sessions = chatService.getActiveSessions();
        return ResponseEntity.ok(sessions);
    }
    
    /**
     * Obtiene las sesiones de un cliente específico
     */
    @GetMapping("/sessions/customer/{customerId}")
    public ResponseEntity<List<ChatSessionDTO>> getCustomerSessions(@PathVariable String customerId) {
        log.info("GET /api/chat/sessions/customer/{} - Obtener sesiones del cliente", customerId);
        
        List<ChatSessionDTO> sessions = chatService.getCustomerSessions(customerId);
        return ResponseEntity.ok(sessions);
    }
    
    /**
     * Obtiene los mensajes de una sesión específica
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getSessionMessages(@PathVariable String sessionId) {
        log.info("GET /api/chat/sessions/{}/messages - Obtener mensajes", sessionId);
        
        List<ChatMessageDTO> messages = chatService.getSessionMessages(sessionId);
        return ResponseEntity.ok(messages);
    }
    
    /**
     * Envía un mensaje en una sesión (alternativa REST al WebSocket)
     */
    @PostMapping("/messages")
    public ResponseEntity<ChatMessageDTO> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        log.info("POST /api/chat/messages - Enviar mensaje en sesión: {}", request.getSessionId());
        
        ChatMessageDTO message = chatService.sendMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
    
    /**
     * Asigna un asesor a una sesión
     */
    @PutMapping("/sessions/{sessionId}/assign")
    public ResponseEntity<ChatSessionDTO> assignAdvisor(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> payload) {
        
        String advisorId = payload.get("advisorId");
        String advisorName = payload.get("advisorName");
        
        log.info("PUT /api/chat/sessions/{}/assign - Asignar asesor: {}", sessionId, advisorId);
        
        ChatSessionDTO session = chatService.assignAdvisor(sessionId, advisorId, advisorName);
        return ResponseEntity.ok(session);
    }
    
    /**
     * Marca los mensajes de una sesión como leídos
     */
    @PutMapping("/sessions/{sessionId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable String sessionId,
            @RequestParam String userId,
            @RequestParam(defaultValue = "false") boolean isAdvisor) {
        
        log.info("PUT /api/chat/sessions/{}/read - Marcar como leído por: {}", sessionId, userId);
        
        chatService.markMessagesAsRead(sessionId, userId, isAdvisor);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Cierra una sesión de chat
     */
    @PutMapping("/sessions/{sessionId}/close")
    public ResponseEntity<ChatSessionDTO> closeSession(@PathVariable String sessionId) {
        log.info("PUT /api/chat/sessions/{}/close - Cerrar sesión", sessionId);
        
        ChatSessionDTO session = chatService.closeSession(sessionId);
        return ResponseEntity.ok(session);
    }
    
    /**
     * Obtiene estadísticas del chat para el admin
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getChatStats() {
        log.info("GET /api/chat/stats - Obtener estadísticas");
        
        long waitingSessions = chatService.countWaitingSessions();
        
        Map<String, Object> stats = Map.of(
            "waitingSessions", waitingSessions,
            "timestamp", System.currentTimeMillis()
        );
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Health check del servicio de chat
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "chat-service"
        ));
    }
}
