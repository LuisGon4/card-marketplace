package com.spring.cardmarketplace.services;

import com.spring.cardmarketplace.entities.Card;
import com.spring.cardmarketplace.repositories.CardRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class CardService {
    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public List<Card> searchByName(String partial){
        return cardRepository.findByCardNameContainingIgnoreCase(partial);
    }
}
