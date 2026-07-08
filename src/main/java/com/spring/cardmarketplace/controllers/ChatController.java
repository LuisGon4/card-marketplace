package com.spring.cardmarketplace.controllers;

import com.spring.cardmarketplace.dto.request.ChatMessageRequest;
import com.spring.cardmarketplace.dto.response.MessageResponse;
import com.spring.cardmarketplace.services.MessageService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Controller
public class ChatController {
    private static final String TOPIC_DESTINATION = "/topic/conversation";

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatController(MessageService messageService, SimpMessagingTemplate simpMessagingTemplate) {
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/chat/{conversationId}")
    public void sendMessage(@DestinationVariable UUID conversationId,
                            @Payload @Valid ChatMessageRequest request) {
        MessageResponse response = messageService.sendMessage(conversationId, request.content());
        simpMessagingTemplate.convertAndSend(TOPIC_DESTINATION + conversationId, response);
    }
}
