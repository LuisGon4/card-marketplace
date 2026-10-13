package com.spring.cardmarketplace.dto.justtcg;

import java.math.BigDecimal;

// JustTCG card id + price of the matching variant
public record JustTcgPrice(
        String cardId,
        BigDecimal price
) {}
