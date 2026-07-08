package com.spring.cardmarketplace.services;

import com.spring.cardmarketplace.dto.response.ValuationResponse;
import com.spring.cardmarketplace.entities.Card;
import com.spring.cardmarketplace.entities.Condition;
import com.spring.cardmarketplace.entities.Printing;
import com.spring.cardmarketplace.exception.CardNotFoundException;
import com.spring.cardmarketplace.repositories.CardRepository;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final CardPricingService cardPricingService;

    public CardService(CardRepository cardRepository, CardPricingService cardPricingService) {
        this.cardRepository = cardRepository;
        this.cardPricingService = cardPricingService;
    }

    public List<Card> searchByName(String partial){
        return cardRepository.findByCardNameContainingIgnoreCase(partial);
    }

    public ValuationResponse getValuation(UUID cardId, Condition condition, Printing printing) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(
                        "Card not found with id: " + cardId
                ));

        BigDecimal price = (card.getJustTcgId() == null) ?
                null :
                cardPricingService.getCachedPrice(card.getJustTcgId(), condition, printing);

        return new ValuationResponse(
                cardId,
                condition,
                printing,
                price
        );
    }
}
