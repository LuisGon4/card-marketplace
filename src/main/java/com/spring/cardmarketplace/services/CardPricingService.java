package com.spring.cardmarketplace.services;

import com.spring.cardmarketplace.client.JustTcgClient;
import com.spring.cardmarketplace.client.TcgDexClient;
import com.spring.cardmarketplace.dto.justtcg.JustTcgPrice;
import com.spring.cardmarketplace.dto.tcgdex.TcgDexCard;
import com.spring.cardmarketplace.entities.Card;
import com.spring.cardmarketplace.entities.Condition;
import com.spring.cardmarketplace.entities.Printing;
import com.spring.cardmarketplace.repositories.CardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.Duration;

@Service
public class CardPricingService {
    private static final Logger log = LoggerFactory.getLogger(CardPricingService.class);

    private static final Duration CACHE_TTL = Duration.ofHours(1);
    private static final Duration NOT_FOUND_TTL = Duration.ofDays(1);
    private static final String NOT_FOUND = "NOT_FOUND";

    private final StringRedisTemplate redisTemplate;
    private final JustTcgClient justTcgClient;
    private final TcgDexClient tcgDexClient;
    private final CardRepository cardRepository;

    public CardPricingService(StringRedisTemplate redisTemplate, JustTcgClient justTcgClient,
                              TcgDexClient tcgDexClient, CardRepository cardRepository) {
        this.redisTemplate = redisTemplate;
        this.justTcgClient = justTcgClient;
        this.tcgDexClient = tcgDexClient;
        this.cardRepository = cardRepository;
    }

    // Returns null when no price is available; never throws on upstream API failures
    public BigDecimal getCachedPrice(Card card, Condition condition, Printing printing){
        try {
            if (card.getJustTcgId() != null) {
                return getCachedPrice(card.getJustTcgId(), condition, printing);
            }
            if (card.getTcgDexId() != null) {
                return resolveAndFetch(card, condition, printing);
            }
            return null;
        } catch (RestClientException e) {
            // Transient (rate limit, auth, 5xx, network): don't cache so the next request retries
            log.warn("Price lookup failed for card {}", card.getId(), e);
            return null;
        }
    }

    private BigDecimal getCachedPrice(String justTcgId, Condition condition, Printing printing){
        // Cache key
        String key = buildKey(justTcgId, condition, printing);
        //Check if we have cached value
        String cached = redisTemplate.opsForValue().get(key);

        if(cached == null){
            return handleCacheMiss(justTcgId, condition, printing);
        }

        if (NOT_FOUND.equals(cached)) {
            return null;
        }

        try{
            return new BigDecimal(cached);
        } catch(NumberFormatException e){
            return handleCacheMiss(justTcgId, condition, printing);
        }
    }

    private BigDecimal handleCacheMiss(String justTcgId, Condition condition, Printing printing) {
        BigDecimal price = justTcgClient.fetchPrice(justTcgId, condition, printing);
        cachePrice(justTcgId, condition, printing, price);
        return price;
    }

    // First lookup for a seeded card: TCGdex card -> TCGPlayer product id -> JustTCG card id + price
    private BigDecimal resolveAndFetch(Card card, Condition condition, Printing printing) {
        String missKey = buildResolveMissKey(card.getTcgDexId(), condition, printing);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(missKey))) {
            return null;
        }

        TcgDexCard tcgDexCard = tcgDexClient.fetchCard(card.getTcgDexId());
        String tcgPlayerId = (tcgDexCard == null) ? null : tcgDexCard.productIdFor(printing);
        if (tcgPlayerId == null) {
            redisTemplate.opsForValue().set(missKey, NOT_FOUND, NOT_FOUND_TTL);
            return null;
        }

        JustTcgPrice result = justTcgClient.fetchByTcgPlayerId(tcgPlayerId, condition, printing);
        if (result == null || result.cardId() == null) {
            // Card may exist in other conditions/printings, so the miss is per condition+printing
            redisTemplate.opsForValue().set(missKey, NOT_FOUND, NOT_FOUND_TTL);
            return null;
        }

        card.setJustTcgId(result.cardId());
        cardRepository.save(card);

        cachePrice(result.cardId(), condition, printing, result.price());
        return result.price();
    }

    private void cachePrice(String justTcgId, Condition condition, Printing printing, BigDecimal price) {
        String key = buildKey(justTcgId, condition, printing);

        // Cache null value
        if(price == null){
            redisTemplate.opsForValue().set(key, NOT_FOUND, NOT_FOUND_TTL);
            return;
        }

        // Cache price
        redisTemplate.opsForValue().set(key, price.toString(), CACHE_TTL);
    }

    private String buildKey(String justTcgId, Condition condition, Printing printing){
        return "card:valuation:%s:%s:%s".formatted(justTcgId, condition.name(), printing.name());
    }

    private String buildResolveMissKey(String tcgDexId, Condition condition, Printing printing){
        return "card:jtid-miss:%s:%s:%s".formatted(tcgDexId, condition.name(), printing.name());
    }
}
