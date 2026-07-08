package com.spring.cardmarketplace.controllers;

import com.spring.cardmarketplace.dto.request.CreateConversationRequest;
import com.spring.cardmarketplace.dto.response.ConversationResponse;
import com.spring.cardmarketplace.dto.response.MessageResponse;
import com.spring.cardmarketplace.services.ConversationService;
import com.spring.cardmarketplace.services.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    private final ConversationService conversationService;
    private final MessageService messageService;

    public ConversationController(ConversationService conversationService, MessageService messageService) {
        this.conversationService = conversationService;
        this.messageService = messageService;
    }

    @GetMapping
    public List<ConversationResponse> getConversations(){
        return conversationService.getForUser();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ConversationResponse startConversation(@Valid @RequestBody CreateConversationRequest request){
        return conversationService.startWithRetry(request);
    }

    @GetMapping("/{id}/messages")
    public List<MessageResponse> getMessages(@PathVariable UUID id){
        return messageService.getMessages(id);
    }
}
