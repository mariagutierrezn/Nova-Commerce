package com.novacommerce.notification_service.application.service;

import com.novacommerce.notification_service.domain.dto.*;
import com.novacommerce.notification_service.domain.model.ChatMessage;
import com.novacommerce.notification_service.domain.model.ChatSession;
import com.novacommerce.notification_service.infrastructure.repository.ChatMessageRepository;
import com.novacommerce.notification_service.infrastructure.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar las sesiones y mensajes de chat
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * Crea una nueva sesión de chat para un cliente
     */
    @Transactional
    public ChatSessionDTO createSession(CreateChatSessionRequest request) {
        log.info("Creando nueva sesión de chat para cliente: {}", request.getCustomerId());
        
        // Verificar si ya existe una sesión activa para este cliente
        var existingSession = sessionRepository.findByCustomerIdAndStatus(
            request.getCustomerId(), 
            ChatSession.ChatStatus.WAITING
        );
        
        if (existingSession.isPresent()) {
            log.info("Ya existe una sesión activa para el cliente: {}", request.getCustomerId());
            return ChatSessionDTO.fromEntity(existingSession.get());
        }
        
        // Crear nueva sesión
        ChatSession session = ChatSession.builder()
                .customerId(request.getCustomerId())
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .status(ChatSession.ChatStatus.WAITING)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .lastActivity(Instant.now())
                .unreadByCustomer(0)
                .unreadByAdvisor(0)
                .build();
        
        session = sessionRepository.save(session);
        
        // Enviar mensaje de bienvenida del bot
        sendBotWelcomeMessage(session.getId(), request.getCustomerName());
        
        // Notificar a los asesores que hay una nueva sesión en espera
        notifyAdvisorsNewSession(session);
        
        return ChatSessionDTO.fromEntity(session);
    }
    
    /**
     * Envía un mensaje de bienvenida automático del bot
     */
    private void sendBotWelcomeMessage(String sessionId, String customerName) {
        ChatMessage welcomeMessage = ChatMessage.builder()
                .sessionId(sessionId)
                .senderId("bot")
                .senderName("Nova Assistant")
                .senderType(ChatMessage.SenderType.BOT)
                .content(String.format("Hola %s, soy Nova Assistant. Te conectaré con un asesor físico en un momento. Por favor espera.", customerName))
                .sentAt(Instant.now())
                .read(false)
                .messageType(ChatMessage.MessageType.TEXT)
                .build();
        
        welcomeMessage = messageRepository.save(welcomeMessage);
        
        // Enviar mensaje por WebSocket
        messagingTemplate.convertAndSend(
            "/topic/chat/" + sessionId,
            ChatMessageDTO.fromEntity(welcomeMessage)
        );
    }
    
    /**
     * Notifica a los asesores que hay una nueva sesión en espera
     */
    private void notifyAdvisorsNewSession(ChatSession session) {
        ChatSessionDTO sessionDTO = ChatSessionDTO.fromEntity(session);
        
        // Enviar notificación a todos los asesores conectados
        messagingTemplate.convertAndSend("/topic/advisor/new-session", sessionDTO);
        
        log.info("Notificación enviada a asesores: nueva sesión {}", session.getId());
    }
    
    /**
     * Envía un mensaje en una sesión de chat
     */
    @Transactional
    public ChatMessageDTO sendMessage(SendMessageRequest request) {
        log.info("Enviando mensaje en sesión: {}", request.getSessionId());
        
        // Verificar que la sesión existe
        ChatSession session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
        
        // Crear el mensaje
        ChatMessage message = ChatMessage.builder()
                .sessionId(request.getSessionId())
                .senderId(request.getSenderId())
                .senderName(request.getSenderName())
                .senderType(ChatMessage.SenderType.valueOf(request.getSenderType()))
                .content(request.getContent())
                .sentAt(Instant.now())
                .read(false)
                .messageType(ChatMessage.MessageType.valueOf(request.getMessageType()))
                .build();
        
        message = messageRepository.save(message);
        
        // Actualizar contador de mensajes no leídos
        updateUnreadCount(session, message.getSenderType());
        
        // Actualizar última actividad de la sesión
        session.setLastActivity(Instant.now());
        session.setUpdatedAt(Instant.now());
        sessionRepository.save(session);
        
        // Enviar mensaje por WebSocket a la sesión específica
        ChatMessageDTO messageDTO = ChatMessageDTO.fromEntity(message);
        messagingTemplate.convertAndSend("/topic/chat/" + request.getSessionId(), messageDTO);
        
        // Si es un mensaje del cliente, notificar al asesor asignado
        if (message.getSenderType() == ChatMessage.SenderType.CUSTOMER && session.getAdvisorId() != null) {
            notifyAdvisorNewMessage(session, messageDTO);
        }
        
        // Si es un mensaje del asesor, notificar al cliente
        if (message.getSenderType() == ChatMessage.SenderType.ADVISOR) {
            notifyCustomerNewMessage(session, messageDTO);
        }
        
        return messageDTO;
    }
    
    /**
     * Actualiza el contador de mensajes no leídos según el tipo de remitente
     */
    private void updateUnreadCount(ChatSession session, ChatMessage.SenderType senderType) {
        if (senderType == ChatMessage.SenderType.CUSTOMER) {
            session.setUnreadByAdvisor(session.getUnreadByAdvisor() + 1);
        } else if (senderType == ChatMessage.SenderType.ADVISOR) {
            session.setUnreadByCustomer(session.getUnreadByCustomer() + 1);
        }
    }
    
    /**
     * Notifica al asesor que tiene un nuevo mensaje
     */
    private void notifyAdvisorNewMessage(ChatSession session, ChatMessageDTO message) {
        messagingTemplate.convertAndSendToUser(
            session.getAdvisorId(),
            "/queue/messages",
            message
        );
    }
    
    /**
     * Notifica al cliente que tiene un nuevo mensaje
     */
    private void notifyCustomerNewMessage(ChatSession session, ChatMessageDTO message) {
        messagingTemplate.convertAndSendToUser(
            session.getCustomerId(),
            "/queue/messages",
            message
        );
    }
    
    /**
     * Asigna un asesor a una sesión de chat
     */
    @Transactional
    public ChatSessionDTO assignAdvisor(String sessionId, String advisorId, String advisorName) {
        log.info("Asignando asesor {} a sesión {}", advisorId, sessionId);
        
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
        
        session.setAdvisorId(advisorId);
        session.setAdvisorName(advisorName);
        session.setStatus(ChatSession.ChatStatus.ACTIVE);
        session.setUpdatedAt(Instant.now());
        
        session = sessionRepository.save(session);
        
        // Enviar mensaje automático de asignación
        sendAdvisorAssignedMessage(session);
        
        return ChatSessionDTO.fromEntity(session);
    }
    
    /**
     * Envía un mensaje automático indicando que se asignó un asesor
     */
    private void sendAdvisorAssignedMessage(ChatSession session) {
        ChatMessage assignedMessage = ChatMessage.builder()
                .sessionId(session.getId())
                .senderId("bot")
                .senderName("Nova Assistant")
                .senderType(ChatMessage.SenderType.BOT)
                .content(String.format("%s se ha unido a la conversación.", session.getAdvisorName()))
                .sentAt(Instant.now())
                .read(false)
                .messageType(ChatMessage.MessageType.TEXT)
                .build();
        
        assignedMessage = messageRepository.save(assignedMessage);
        
        // Enviar por WebSocket
        messagingTemplate.convertAndSend(
            "/topic/chat/" + session.getId(),
            ChatMessageDTO.fromEntity(assignedMessage)
        );
    }
    
    /**
     * Obtiene todas las sesiones activas y en espera para asesores
     */
    public List<ChatSessionDTO> getActiveSessions() {
        List<ChatSession.ChatStatus> activeStatuses = List.of(
            ChatSession.ChatStatus.WAITING,
            ChatSession.ChatStatus.ACTIVE
        );
        
        List<ChatSession> sessions = sessionRepository.findByStatusInOrderByLastActivityDesc(activeStatuses);
        
        return sessions.stream()
                .map(session -> {
                    ChatSessionDTO dto = ChatSessionDTO.fromEntity(session);
                    // Agregar el último mensaje
                    ChatMessage lastMessage = messageRepository.findFirstBySessionIdOrderBySentAtDesc(session.getId());
                    if (lastMessage != null) {
                        dto.setLastMessage(ChatMessageDTO.fromEntity(lastMessage));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene el historial de mensajes de una sesión
     */
    public List<ChatMessageDTO> getSessionMessages(String sessionId) {
        List<ChatMessage> messages = messageRepository.findBySessionIdOrderBySentAtAsc(sessionId);
        return messages.stream()
                .map(ChatMessageDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Marca los mensajes de una sesión como leídos
     */
    @Transactional
    public void markMessagesAsRead(String sessionId, String userId, boolean isAdvisor) {
        log.info("Marcando mensajes como leídos en sesión: {} por usuario: {}", sessionId, userId);
        
        List<ChatMessage> unreadMessages = messageRepository.findBySessionIdAndReadFalseOrderBySentAtAsc(sessionId);
        
        for (ChatMessage message : unreadMessages) {
            // Solo marcar como leídos los mensajes del otro participante
            if ((isAdvisor && message.getSenderType() == ChatMessage.SenderType.CUSTOMER) ||
                (!isAdvisor && message.getSenderType() == ChatMessage.SenderType.ADVISOR)) {
                message.setRead(true);
                message.setReadAt(Instant.now());
                messageRepository.save(message);
            }
        }
        
        // Actualizar contador en la sesión
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
        
        if (isAdvisor) {
            session.setUnreadByAdvisor(0);
        } else {
            session.setUnreadByCustomer(0);
        }
        
        sessionRepository.save(session);
    }
    
    /**
     * Cierra una sesión de chat
     */
    @Transactional
    public ChatSessionDTO closeSession(String sessionId) {
        log.info("Cerrando sesión de chat: {}", sessionId);
        
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
        
        session.setStatus(ChatSession.ChatStatus.CLOSED);
        session.setClosedAt(Instant.now());
        session.setUpdatedAt(Instant.now());
        
        session = sessionRepository.save(session);
        
        // Notificar a ambos participantes que la sesión se cerró
        ChatSessionDTO sessionDTO = ChatSessionDTO.fromEntity(session);
        messagingTemplate.convertAndSend("/topic/chat/" + sessionId + "/closed", sessionDTO);
        
        return sessionDTO;
    }
    
    /**
     * Obtiene las sesiones de un cliente específico
     */
    public List<ChatSessionDTO> getCustomerSessions(String customerId) {
        List<ChatSession> sessions = sessionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        return sessions.stream()
                .map(ChatSessionDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Cuenta las sesiones en espera
     */
    public long countWaitingSessions() {
        return sessionRepository.findByStatusOrderByCreatedAtAsc(ChatSession.ChatStatus.WAITING).size();
    }
}
