package com.spring.cardmarketplace.controllers;

import com.spring.cardmarketplace.dto.request.CreateConversationRequest;
import com.spring.cardmarketplace.dto.response.ConversationResponse;
import com.spring.cardmarketplace.services.ConversationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
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
}
