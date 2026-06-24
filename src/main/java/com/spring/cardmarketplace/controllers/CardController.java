package com.spring.cardmarketplace.controllers;

import com.spring.cardmarketplace.entities.Card;
import com.spring.cardmarketplace.services.CardService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/cards")
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService){
        this.cardService = cardService;
    }

    @GetMapping
    public List<Card> searchCards(@RequestParam String name){
        return cardService.searchByName(name);
    }

    // Placeholder before actual implementation logic
    @GetMapping("/{id}/valuation")
    public Map<String, Object> getValuation(@PathVariable UUID id){
        Map<String, Object> placeholder = new HashMap<>();
        placeholder.put("cardId", id);
        placeholder.put("marketPrice", null);
        placeholder.put("message", "Valuation logic not implemented yet — Phase 3");
        return placeholder;
    }
}
