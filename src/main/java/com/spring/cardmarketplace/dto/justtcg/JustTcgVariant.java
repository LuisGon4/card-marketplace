package com.spring.cardmarketplace.dto.justtcg;

import java.math.BigDecimal;

// variant level: { "price": [ 64.45 ] }
public record JustTcgVariant(
        BigDecimal price
) {}
