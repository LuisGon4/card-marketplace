package com.spring.cardmarketplace.services;

import com.spring.cardmarketplace.client.JustTcgClient;
import com.spring.cardmarketplace.entities.Condition;
import com.spring.cardmarketplace.entities.Printing;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;

@Service
public class CardPricingService {
    private static final Duration CACHE_TTL = Duration.ofHours(1);
    private static final Duration NOT_FOUND_TTL = Duration.ofDays(1);
    private static final String NOT_FOUND = "NOT_FOUND";

    private final StringRedisTemplate redisTemplate;
    private final JustTcgClient justTcgClient;

    public CardPricingService(StringRedisTemplate redisTemplate, JustTcgClient justTcgClient) {
        this.redisTemplate = redisTemplate;
        this.justTcgClient = justTcgClient;
    }

    public BigDecimal getCachedPrice(String justTcgId, Condition condition, Printing printing){
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
        String key = buildKey(justTcgId, condition, printing);
        BigDecimal price = justTcgClient.fetchPrice(justTcgId, condition, printing);

        // Cache null value
        if(price == null){
            redisTemplate.opsForValue().set(key, NOT_FOUND, NOT_FOUND_TTL);
            return null;
        }

        // Cache price
        redisTemplate.opsForValue().set(key, price.toString(), CACHE_TTL);
        return price;
    }

    private String buildKey(String justTcgId, Condition condition, Printing printing){
        return "card:valuation:%s:%s:%s".formatted(justTcgId, condition.name(), printing.name());
    }
}
