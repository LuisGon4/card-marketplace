package com.spring.cardmarketplace.services;

import com.spring.cardmarketplace.dto.tcgdex.SetData;
import com.spring.cardmarketplace.dto.tcgdex.SetResult;
import com.spring.cardmarketplace.entities.Card;
import com.spring.cardmarketplace.repositories.CardRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Profile("seed")
public class SetSeeder {
    private static final Logger log = LoggerFactory.getLogger(SetSeeder.class);

    private final CardRepository cardRepository;

    public SetSeeder(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Transactional
    public SetResult setSeed(SetData detail){
        // Store any existing cards in hashmap for O(1) lookups
        List<String> ids = detail.cards().stream()
                .map(SetData.CardBrief::id)
                .toList();

        Map<String, Card> existing = cardRepository.findByTcgDexIdIn(ids).stream()
                .collect(Collectors.toMap(Card::getTcgDexId, Function.identity()));

        int upserted = 0, failed = 0;
        List<Card> toSave = new ArrayList<>();

        for(SetData.CardBrief cardBrief : detail.cards()){
            if(cardBrief.id() == null || cardBrief.name() == null){
                log.warn("Skipping malformed card in set {}: {}", detail.id(), cardBrief);
                failed++;
                continue;
            }

            // O(1)
            Card card = existing.get(cardBrief.id());

            // Not already in database
            if(card == null){
                card = new Card();
                card.setTcgDexId(cardBrief.id());
                card.setCustom(false);
            }

            card.setCardName(cardBrief.name());
            card.setSetName(detail.name());
            card.setImageUrl(cardBrief.image());
            toSave.add(card);
            upserted++;
        }

        cardRepository.saveAll(toSave);
        return new SetResult(upserted, failed);
    }
}
