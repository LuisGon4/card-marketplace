package com.spring.cardmarketplace.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

// All fields a Listing may need
public record ListingResponse(
        UUID id,
        UUID cardId,
        String cardName,
        String condition,
        String printing,
        BigDecimal askingPrice,
        BigDecimal marketPrice,
        boolean priceFlagged,
        String location,
        String description,
        String sellerUsername,
        UUID sellerId
) {}