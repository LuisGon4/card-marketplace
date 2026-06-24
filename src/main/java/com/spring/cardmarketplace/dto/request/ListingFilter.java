package com.spring.cardmarketplace.dto.request;

import java.math.BigDecimal;

public record ListingFilter(
        String cardName,
        String setName,
        String condition,
        String printing,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {}
