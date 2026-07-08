package com.spring.cardmarketplace.controllers;

import com.spring.cardmarketplace.dto.response.ValuationResponse;
import com.spring.cardmarketplace.entities.Card;
import com.spring.cardmarketplace.entities.Condition;
import com.spring.cardmarketplace.entities.Printing;
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
    public ValuationResponse getValuation(@PathVariable UUID id,
                                          @RequestParam Condition condition,
                                          @RequestParam Printing printing){
        return cardService.getValuation(id, condition, printing);
    }
}
